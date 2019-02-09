package au.com.illyrian.classmaker.proxy;

import java.util.HashMap;
import java.util.Map;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class ActorApplyMethodBuilder {
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
    ClassType [] implmentInterfaces = null;
    
    public ActorApplyMethodBuilder(ClassMakerIfc maker) {
        this.maker = maker;
    }
    
    public ClassMakerIfc getClassMaker() {
        return maker;
    }
    
    public MakerMethod getMethod() {
        if (iface == null) {
            throw new NullPointerException("method has not been set");
        }
        return method;
    }

    public ActorApplyMethodBuilder withMethod(MakerMethod method) {
        this.method = method;
        return this;
    }

    public ClassType getInterface() {
        if (iface == null) {
            throw new NullPointerException("interface has not been set");
        }
        return iface;
    }


    public ActorApplyMethodBuilder withInterface(Class iface) {
        if (iface.isInterface()) {
            String typeName = iface.getName();
            Type type = maker.findType(typeName);
            if (Type.isClass(type)) {
                return withInterface(type.toClass());
            }
        }
        throw new IllegalArgumentException("Must be an interface: " + iface.getName());
    }
    
    public ActorApplyMethodBuilder withInterface(ClassType iface) {
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
    
    static String camelCase(String source) {
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
    
    static String toKey(MakerMethod method) {
        return method.getName() + method.getSignature();
    }

    public static Map<String, ActorApplyMethodBuilder> createMethodClasses(ClassMakerIfc maker, ClassType iface) {
        Map<String, ActorApplyMethodBuilder> classMap = new HashMap<String, ActorApplyMethodBuilder>();
        for (MakerMethod method: iface.getDeclaredMethods()) {
            ClassMakerIfc newMaker = maker.createClassMaker();
            ActorApplyMethodBuilder builder = createMethodClass(newMaker, iface, method);
            classMap.put(toKey(method), builder);
        }
        return classMap;
    }

    public static ActorApplyMethodBuilder createMethodClass(ClassMakerIfc newMaker, ClassType iface, MakerMethod method) {
        ActorApplyMethodBuilder builder = new ActorApplyMethodBuilder(newMaker);
        builder.withInterface(iface);
        builder.withMethod(method);
        builder.build();
        return builder;
    }

    public void beginClass() {
        maker.setSimpleClassName(getSimpleName());
        maker.setPackageName(getClass().getPackage().getName());
        maker.Implements(ActorApply.class);
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
        maker.Method(method.getName(), method.getReturnType(), ClassMakerConstants.ACC_PUBLIC);
        // Declare the formal parameters.
        int i = 0;
        for (Type type : method.getFormalTypes()) {
            maker.Declare(names[i++], type, 0);
        }
        maker.Begin();
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
    
    public void methodEnd() {
        maker.Return();
        maker.End();
     }
    
    public void createMethod(MakerMethod method, String [] memberNames) {
        String [] paramNames = createFieldNames(METHOD_PREFIX, method.getFormalTypes().length);
        methodBegin(method, paramNames);
        stashParameters(memberNames, paramNames);
        methodEnd();
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
        String [] memberNames = createFieldNames(MEMBER_PREFIX, method.getFormalTypes().length);
        beginClass();
        declareFields(method, memberNames);
        createMethod(method, memberNames);
        createToString(method.getName(), memberNames, method.getFormalTypes(), method.getReturnType());
        createApply(getInterface(), method, memberNames);
        createApplyBridge(getInterface(), method, memberNames);
    }
}
