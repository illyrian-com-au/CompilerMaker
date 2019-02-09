package au.com.illyrian.classmaker.proxy;

import java.util.HashMap;
import java.util.Map;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.MakerClassType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class ActorInterfaceBuilder {
    public static final String MEMBER_REF = "reference";
    public static final String LOCAL_REF = "localRef";
    public static final String METHOD_PREFIX = "$";
    public static final String METHOD_RESULT = "result";
    public static final int ABSTRACT_MASK = ~ClassMakerConstants.ACC_ABSTRACT;

    protected final ClassMaker maker;
    ClassType iface;
    Map<String, ClassType> classMap = null;
    
    static public interface Apply<T> {
        void apply(T ref);
    }

    public ActorInterfaceBuilder(ClassMaker maker) {
        this.maker = maker;
    }
    
    public ClassMaker getClassMaker() {
        return maker;
    }
    
    public ClassType getInterface() {
        if (iface == null) {
            throw new NullPointerException("interface has not been set");
        }
        return iface;
    }

    public ActorInterfaceBuilder withInterface(Class iface) {
        if (iface.isInterface()) {
            String typeName = iface.getName();
            Type type = maker.findType(typeName);
            if (Type.isClass(type)) {
                return withInterface(type.toClass());
            }
        }
        throw new IllegalArgumentException("Must be an interface: " + iface.getName());
    }

    public ActorInterfaceBuilder withInterface(ClassType iface) {
        this.iface = iface;
        return this;
    }

    public String [] createFieldNames(String prefix, int len) {
        String [] names = new String[len];
        for (int i=0; i<len; i++) {
            names[i] = prefix + i;
        }
        return names;
    }
    
    boolean isVoid(MakerMethod method) {
        return ClassMakerFactory.VOID_TYPE.equals(method.getReturnType());
    }
    
    public String camelCase(String source) {
        String target = source;
        if (!Character.isUpperCase(source.charAt(0))) {
            target = Character.toUpperCase(source.charAt(0)) + source.substring(1);
        }
        return target;
    }
    
    public String getSimpleName() {
        String className = getInterface().getSimpleName() + "Actor";
        return className;
    }
    
    private Map<String, ClassType> createMethodClasses(ClassType iface) {
        Map<String, ClassType> classMap = new HashMap<String, ClassType>();
        for (MakerMethod method: iface.getDeclaredMethods()) {
            ClassType classType = createMethodClass(iface, method);
            classMap.put(toKey(method), classType);
        }
        return classMap;
    }

    ClassType createMethodClass(ClassType iface, MakerMethod method) {
        ClassMaker newMaker = maker.createClassMaker();
        ActorApplyMethodBuilder builder = new ActorApplyMethodBuilder(newMaker);
        builder.withInterface(iface);
        builder.withMethod(method);
        builder.build();
        newMaker.EndClass(); // Create default constructor
        return newMaker.getClassType();
    }

    public void beginClass() {
        maker.setSimpleClassName(getSimpleName());
        maker.setPackageName(getClass().getPackage().getName());
        maker.Extends(Object.class);
        maker.Implements(getInterface());
    }
    
    public MakerField [] createNamedFields(MakerMethod method, String prefix) {
        int len = method.getFormalTypes().length;
        MakerField [] fields = new MakerField[len];
        int i = 0;
        for (Type type : method.getFormalTypes()) {
            MakerField field = new MakerField(prefix + i, type, 0);
            fields[i++] = field;
        }
        return fields;
    }
    
    public void methodBegin(MakerMethod method, String [] names) {
        int modifiers = method.getModifiers() & ABSTRACT_MASK;
        maker.Method(method.getName(), method.getReturnType(), modifiers);
        // Declare the formal parameters.
        int i = 0;
        for (Type type : method.getFormalTypes()) {
            maker.Declare(names[i++], type, 0);
        }
        maker.Begin();
        if (!isVoid(method)) {
            throw new IllegalArgumentException("Actor interface methods must return void");
        }
    }
    
    public CallStack pushParams(String [] params) {
        CallStack stack = maker.Push();
        for (String fieldName : params) {
            stack.Push(maker.Get(fieldName));
        }
        return stack;
    }

    public void methodEnd(MakerMethod method) {
        maker.Return();
        maker.End();
     }
    
    public void createMethod(MakerMethod method, String refName) {
        String [] paramNames = createFieldNames(METHOD_PREFIX, method.getFormalTypes().length);
        methodBegin(method, paramNames);
        Value reference = newMethodClass(method, paramNames);
        stashReference(refName, reference);
        methodEnd(method);
    }

    public void createMethods(ClassType iface, String refName) {
        for (MakerMethod method: iface.getDeclaredMethods()) {
            createMethod(method, refName);
        }
    }

    private void stashReference(String fieldName, Value reference) {
        //maker.Eval(reference);
        maker.Eval(maker.Set(maker.This(), fieldName, reference));
        //maker.Call(maker.This(), "setRef", maker.Push(reference));
    }
    
    String toKey(MakerMethod method) {
        return method.getName() + method.getSignature();
    }

    Value newMethodClass(MakerMethod method, String[] paramNames) {
        ClassType newClass = classMap.get(toKey(method));
        maker.Declare(LOCAL_REF, newClass, 0);
        Value reference = maker.New(newClass).Init(null);
        maker.Eval(maker.Set(LOCAL_REF,  reference));
        CallStack stack = pushParams(paramNames);
        maker.Call(maker.Get(LOCAL_REF), method.getName(), stack);
        return maker.Get(LOCAL_REF);
    }

    void createGetter(String fieldName, Type fieldType) {
        String methodName = "get" + camelCase(fieldName);
        maker.Method(methodName, fieldType, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Get(maker.This(), fieldName));
        maker.End();
    }
    
    void createSetter(String fieldName, Type fieldType) {
        String methodName = "set" + camelCase(fieldName);
        maker.Method(methodName, ClassMakerFactory.VOID_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("value", fieldType, 0);
        maker.Begin();
        maker.Eval(maker.Set(maker.This(), fieldName, maker.Get("value")));
        maker.End();
    }
    
    void createProperty(String fieldName, Type refType) {
        maker.Declare(fieldName, refType, ClassMakerConstants.ACC_PRIVATE);
        createGetter(fieldName, refType);
        createSetter(fieldName, refType);
    }
    
    Value callMethod(String reference, String methodName, String [] fieldNames) {
        Value ref = maker.Get(maker.This(), reference);
        CallStack stack = maker.Push();
        for (String name : fieldNames) {
            stack.Push(maker.Get(maker.This(), name));
        }
        return maker.Call(ref, methodName, stack);
    }
    
    void createQuote(StringMakerBuilder builder, String fieldName, Type fieldType) {
        if (fieldType.equals(ClassMakerFactory.STRING_TYPE)) {
            maker.If(maker.NE(maker.Get(maker.This(), fieldName), maker.Null()));
            {
                builder.append(maker.Literal('\"'));
            }
            maker.EndIf();
        }
    }
    
    void createToString(StringMakerBuilder builder, MakerMethod method) {
        String methodName = method.getName();
        Type [] fieldTypes = method.getFormalTypes();
        Type resultType = method.getReturnType();
        builder.append("  ").append(resultType.getName()).append(" ").append(methodName).append("(");
        for (int i=0; i<fieldTypes.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(maker.Literal(fieldTypes[i].getName()));
        }
        builder.append(")");
    }
    
    void createToString(ClassType iface) {
        maker.Method("toString", String.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
        StringMakerBuilder builder = new StringMakerBuilder(maker);
        builder.append("class ").append(getSimpleName()).append(" {\n");
        for (MakerMethod method: iface.getDeclaredMethods()) {
            createToString(builder, method);
        }
        builder.append("}");
        maker.Return(builder.build());
        maker.End();
    }

    public void build() {
        classMap = createMethodClasses(iface);
        beginClass();
//        ClassType newClass = classMap.get("run()V");
        createProperty("ref", ClassMakerFactory.OBJECT_TYPE);
        createMethods(iface, "ref");
        createToString(iface);
    }
}
