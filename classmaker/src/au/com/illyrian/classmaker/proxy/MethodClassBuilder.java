package au.com.illyrian.classmaker.proxy;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class MethodClassBuilder {
    public static final String MEMBER_REF = "reference";
    public static final String MEMBER_NAME = "method";
    public static final String MEMBER_PREFIX = "param";
    public static final String MEMBER_RESULT = "result";
    public static final String METHOD_REF = "reference";
    public static final String METHOD_PREFIX = "$";
    public static final String METHOD_RESULT = "result";
    public static final int ABSTRACT_MASK = ~ClassMakerConstants.ACC_ABSTRACT;

    protected final ClassMakerIfc maker;
    ClassType iface;
    MakerMethod method;
    String [] memberNames = null;
    
    static public interface Apply<T> {
        void apply(T ref);
    }

    public MethodClassBuilder(ClassMakerIfc maker) {
        this.maker = maker;
    }
    
    public ClassMakerIfc getClassMaker() {
        return maker;
    }
    
    public ClassType getInterface() {
        if (iface == null) {
            throw new NullPointerException("interface has not been set");
        }
        return iface;
    }

    public MethodClassBuilder withInterface(ClassType iface, MakerMethod method) {
        this.iface = iface;
        this.method = method;
        return this;
    }

    public MethodClassBuilder withInterface(ClassType iface, String methodName) {
        MakerMethod[] methods = iface.getMethods(methodName);
        if (methods.length == 1) {
            withInterface(iface, methods[0]);
        } else {
            throw new IllegalArgumentException("More than one method called: " + methodName);
        }
        return this;
    }

    public MethodClassBuilder withInterface(Class iface, String methodName) {
        if (iface.isInterface()) {
            String typeName = iface.getName();
            Type type = maker.findType(typeName);
            if (Type.isClass(type)) {
                return withInterface(type.toClass(), methodName);
            }
        }
        throw new IllegalArgumentException("Must be an interface: " + iface.getName());
    }

    public MethodClassBuilder withInterface(ClassType iface) {
        this.iface = iface;
        return this;
    }

    public MakerMethod getMethod() {
        if (iface == null) {
            throw new NullPointerException("method has not been set");
        }
        return method;
    }

    public MethodClassBuilder withMethod(MakerMethod method) {
        this.method = method;
        return this;
    }
    
    public String [] createFieldNames(String prefix, int len) {
        String [] names = new String[len];
        for (int i=0; i<len; i++) {
            names[i] = prefix + i;
        }
        return names;
    }
    
    public String [] getMemberNames() {
        return memberNames;
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
        String className = getInterface().getSimpleName() + "$" + camelCase(getMethod().getName());
        return className;
    }
    
    public void beginClass() {
        maker.setSimpleClassName(getSimpleName());
        maker.setPackageName(getInterface().getPackageName());
        maker.Implements(getInterface());
        maker.Implements(Apply.class);
    }
    
    public void declareFields(MakerMethod method, String [] names) {
        maker.Declare("methodName", String.class, 0);
        if (!isVoid(method)) {
            maker.Declare(MEMBER_RESULT, method.getReturnType(), ClassMakerConstants.ACC_PRIVATE);
        }
        // Declare fields for each of the formal parameters.
        int i = 0;
        for (Type type : method.getFormalTypes()) {
            maker.Declare(names[i++], type, ClassMakerConstants.ACC_PRIVATE);
        }
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
        // Declare a local variable for the return value.
        if (!isVoid(method)) {
            maker.Declare(METHOD_RESULT, method.getReturnType(), 0);
        }
        // Set the name of the method.
        maker.Set(maker.This(), "methodName", maker.Literal(method.getName()));
    }
    
    public void stashParameters(String [] members, String [] params) {
        int len = params.length;
        for (int i=0; i<len; i++) {
            maker.Set(maker.This(), members[i], maker.Get(params[i]));
        }
    }
    
    public void methodResult(Value result) {
        if (!ClassMakerFactory.VOID_TYPE.equals(result.getType())) {
            maker.Eval(maker.Assign(METHOD_RESULT, result));
        }
    }
    
    public void methodEnd(MakerMethod method) {
        if (ClassMakerFactory.VOID_TYPE.equals(method.getReturnType())) {
            maker.Return();
        } else {
            maker.Return(maker.Get(METHOD_RESULT));
        }
        maker.End();
     }
    
    public void createMethod(MakerMethod method, String [] memberNames) {
        String [] paramNames = createFieldNames(METHOD_PREFIX, method.getFormalTypes().length);
        methodBegin(method, paramNames);
        stashParameters(memberNames, paramNames);
        methodEnd(method);
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
    
    Value callMethod(String methodName, String [] fieldNames) {
        Value ref = maker.Get(METHOD_REF);
        CallStack stack = maker.Push();
        for (String name : fieldNames) {
            stack.Push(maker.Get(maker.This(), name));
        }
        return maker.Call(ref, methodName, stack);
    }
    
    // Java equivalent: public void apply(T reference) { reference.method(...); }
    void createApply(ClassType refType, MakerMethod method, String [] fieldNames) {
        maker.Method("apply", ClassMakerFactory.VOID_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare(METHOD_REF, refType, 0);
        maker.Begin();
        if (!isVoid(method)) {
            maker.Eval(maker.Set(maker.This(), MEMBER_RESULT, callMethod(method.getName(), fieldNames)));
        } else {
            maker.Eval(callMethod(method.getName(), fieldNames));
        }
        maker.End();
    }
    
    // Java equivalent: public void apply(Object reference) { apply((T)reference); }
    void createApplyBridge(ClassType refType, MakerMethod method, String [] fieldNames) {
        maker.Method("apply", ClassMakerFactory.VOID_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare(METHOD_REF, Object.class, 0);
        maker.Begin();
        {
            maker.Eval(maker.Call(maker.This(), "apply", maker.Push(maker.Cast(maker.Get(METHOD_REF), refType))));
        }
        maker.End();
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
    
    void createToString(String methodName, String [] fieldNames, Type [] fieldTypes, Type resultType) {
        maker.Method("toString", String.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
        StringMakerBuilder builder = new StringMakerBuilder(maker);
        builder.append(methodName).append("(");
        for (int i=0; i<fieldNames.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            createQuote(builder, fieldNames[i], fieldTypes[i]);
            builder.append(maker.Get(maker.This(), fieldNames[i]));
            createQuote(builder, fieldNames[i], fieldTypes[i]);
        }
        builder.append(")");
        maker.Return(builder.build());
        maker.End();
    }

    public void build() {
        memberNames = createFieldNames(MEMBER_PREFIX, method.getFormalTypes().length);
        beginClass();
        declareFields(method, memberNames);
        createMethod(method, memberNames);
        createToString(method.getName(), memberNames, method.getFormalTypes(), method.getReturnType());
        createProperty(MEMBER_REF, getInterface());
        createApply(getInterface(), method, memberNames);
        createApplyBridge(getInterface(), method, memberNames);
    }
}
