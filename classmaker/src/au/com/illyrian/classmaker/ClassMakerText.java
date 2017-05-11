package au.com.illyrian.classmaker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Stack;

import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.ClassMaker.Initialiser;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

public class ClassMakerText extends PrintWriter implements ClassMakerIfc {
    ClassMakerFactory factory = new ClassMakerFactory();
    Stack<String> stack = new Stack<String>();
    HashMap<String, Type>typeMap = new HashMap<String, Type>();

    public ClassMakerText() {
        this(new StringWriter());
    }

    public ClassMakerText(Writer out) {
        super(out);
        typeMap.put("match()", PrimitiveType.BOOLEAN_TYPE);
        typeMap.put("expect()", ClassType.STRING_TYPE);
    }
    
    @Override
    public void setSourceLine(SourceLine source) {
    }

    @Override
    public void setPackageName(String packageName) throws ClassMakerException {
        println("setPackageName(\"" + packageName + "\");");
    }

    @Override
    public void Import(String className) throws ClassMakerException {
        println("Import(\"" + className + "\");");
    }

    @Override
    public void setSimpleClassName(String className) throws ClassMakerException {
        println("setSimpleClassName(\"" + className + "\");");
    }

    @Override
    public void setClassModifiers(int modifiers) {
        println("setClassModifiers(" + modifiers + ");");
    }

    @Override
    public void Extends(String className) throws ClassMakerException {
        println("Extends(\"" + className + "\");");
    }

    @Override
    public void Implements(String className) throws ClassMakerException {
        println("Implements(\"" + className + "\");");
    }

    @Override
    public Type Call(String className, String methodName,
            CallStack actualParameters) throws ClassMakerException {
        String parameters = processCallStack(actualParameters.size());
        stack.push("Call(\"" + className + "\", \"" + methodName + "\", "
                + parameters + ")");
        Type returnType = typeMap.get(methodName + "()");
        return (returnType == null) ? ClassType.OBJECT_TYPE : returnType;
    }

    @Override
    public Type Call(Type reference, String methodName,
            CallStack actualParameters) throws ClassMakerException {
        String parameters = processCallStack(actualParameters.size());
        String refType = stack.pop();
        stack.push("Call(" + refType + ", \"" + methodName + "\", "
                + parameters + ")");
        Type returnType = typeMap.get(methodName + "()");
        return (returnType == null) ? ClassType.OBJECT_TYPE : returnType;
    }

    private String processCallStack(int count) {
        String result;
        if (count > 1) {
            String value = stack.pop();
            result = processCallStack(count - 1) + ".Push(" + value + ")";
        } else
            result = "Push(" + stack.pop() + ")";
        return result;
    }

    @Override
    public ClassType This() throws ClassMakerException {
        stack.push("This()");
        return ClassType.OBJECT_TYPE;
    }

    @Override
    public ClassType Super() throws ClassMakerException {
        stack.push("Super()");
        return ClassType.OBJECT_TYPE;
    }

    @Override
    public ClassType Null() throws ClassMakerException {
        stack.push("Null()");
        return ClassType.NULL_TYPE;
    }

    @Override
    public PrimitiveType Literal(double value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return PrimitiveType.DOUBLE_TYPE;
    }

    @Override
    public PrimitiveType Literal(float value) throws ClassMakerException {
        stack.push("Literal(" + value + "f)");
        return PrimitiveType.FLOAT_TYPE;
    }

    @Override
    public PrimitiveType Literal(long value) throws ClassMakerException {
        stack.push("Literal(" + value + "l)");
        return PrimitiveType.LONG_TYPE;
    }

    @Override
    public PrimitiveType Literal(int value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public PrimitiveType Literal(char value) throws ClassMakerException {
        stack.push("Literal(\'" + (char) value + "\')");
        return PrimitiveType.CHAR_TYPE;
    }

    @Override
    public PrimitiveType Literal(byte value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return PrimitiveType.BYTE_TYPE;
    }

    @Override
    public PrimitiveType Literal(short value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return PrimitiveType.SHORT_TYPE;
    }

    @Override
    public PrimitiveType Literal(boolean value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    @Override
    public ClassType Literal(String value) throws ClassMakerException {
        stack.push("Literal(\"" + value + "\")");
        return ClassType.STRING_TYPE;
    }

    @Override
    public Type Assign(String name, Type type) throws ClassMakerException {
        String val = stack.pop();
        stack.push("Assign(\"" + name + "\", " + val + ")");
        return type;
    }

    @Override
    public Type Assign(Type type, String fieldName, Type value)
            throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Assign(" + operand1 + ", \"" + fieldName + "\", "
                + operand2 + ")");
        return value;
    }

    @Override
    public Type Assign(String className, String fieldName, Type value)
            throws ClassMakerException {
        String val = stack.pop();
        stack.push("Assign(\"" + className + "\", \"" + fieldName + "\", "
                + val + ")");
        return value;
    }

    @Override
    public Type Set(String name, Type value) throws ClassMakerException {
        String val = stack.pop();
        stack.push("Set(\"" + name + "\", " + val + ")");
        return value;
    }

    @Override
    public Type Set(Type reference, String fieldName, Type value)
            throws ClassMakerException {
        String ref = stack.pop();
        String val = stack.pop();
        stack.push("Set(" + ref + ", \"" + fieldName + "\", " + val + ")");
        return null;
    }

    @Override
    public Type Set(String className, String fieldName, Type value)
            throws ClassMakerException {
        String val = stack.pop();
        stack.push("Set(\"" + className + "\", \"" + fieldName + "\", " + val
                + ")");
        return null;
    }

    private Type get(String fieldName) {
        if ("x".equals(fieldName) || "y".equals(fieldName)
                || "z".equals(fieldName))
            return ClassType.OBJECT_TYPE;
        else if ("this".equals(fieldName) || "super".equals(fieldName))
            return ClassType.OBJECT_TYPE;
        else if ("class".equals(fieldName))
            return ClassType.CLASS_TYPE;
        else
            return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type Get(Type reference, String fieldName)
            throws ClassMakerException {
        String ref = stack.pop();
        stack.push("Get(" + ref + ", \"" + fieldName + "\")");
        return get(fieldName);
    }

    @Override
    public Type Get(String className, String fieldName)
            throws ClassMakerException {
        DeclaredType declared = findDeclaredType(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName = declared.getName();
        stack.push("Get(\"" + qualifiedName + "\", \"" + fieldName + "\")");
        return get(fieldName);
    }

    @Override
    public Type Get(String name) throws ClassMakerException {
        stack.push("Get(\"" + name + "\")");
        return get(name);
    }

    public MakerField Find(Type reference, String name)
            throws ClassMakerException {
        DeclaredType declared = findDeclaredType(name);
        if (declared != null && reference.toClass() != null)
            return new MakerField(reference.toClass(), name, declared, 0);
        return null;
    }

    public MakerField Find(String className, String name)
            throws ClassMakerException {
        Type type = findType(name);
        DeclaredType declared = findDeclaredType(className);
        if (declared == null)
            throw createException("Unknown path: " + className);
        if (type != null)
            return new MakerField(declared.getClassType(), name, declared,
                    ClassMaker.ACC_STATIC);
        return null;
    }

    public MakerField Find(String name) throws ClassMakerException {
        Type type = findType(name);
        if (type != null) {
            DeclaredType declared = new DeclaredType(type);
            MakerField field = new MakerField(name, declared, 0);
            // ScopeLevel > 0 means variable is declared in a method.
            field.setScopeLevel(1);
            return field;
        }
        return null;
    }

    Type findType(String name) throws ClassMakerException {
        if ("x".equals(name) || "y".equals(name) || "z".equals(name))
            return ClassType.OBJECT_TYPE;
        else if ("java".equals(name) || "lang".equals(name)
                || "test".equals(name))
            return null;
        else if ("int".equals(name))
            return null;
        else if (name.indexOf('.') > -1)
            return null; // This is a path
        else if (name.endsWith("Object"))
            return null;
        else if (typeMap.containsKey(name)) 
            return typeMap.get(name);
        else
            return PrimitiveType.INT_TYPE;
    }
    
    public void addType(String name, Class classType) {
        Type type = new ClassType(classType);
        typeMap.put(name, type);
        String qname = classType.getName();
        if (!name.equals(qname)) {
            typeMap.put(qname, type);
        }
    }

    public DeclaredType stringToDeclaredClass(String typeName) {
        DeclaredType declared = findDeclaredType(typeName);
        if (declared == null) {
            throw new IllegalArgumentException("Unknown type: " + typeName);
        }
        return declared;
    }

    public DeclaredType findDeclaredType(String typeName)
            throws ClassMakerException {
        if (typeName.endsWith("Object"))
            return new DeclaredType(ClassType.OBJECT_TYPE);
        else if (typeName.endsWith("String"))
            return new DeclaredType(ClassType.STRING_TYPE);
        else if (typeName.endsWith("StringBuffer"))
            return new DeclaredType(ClassType.STRING_BUFFER_TYPE);
        else if (typeName.equals("int"))
            return new DeclaredType(PrimitiveType.INT_TYPE);
        else {
            Type type = findType(typeName);
            if (type != null)
                return new DeclaredType(type);
        }
        return null;
    }

    public DeclaredType classToDeclaredType(Class javaClass) throws ClassMakerException
    {
        return factory.classToDeclaredType(javaClass); 
    }
    
    @Override
    public MakerField findField(String name) throws ClassMakerException {
        return Find(name);
    }

    @Override
    public int addModifier(int modifiers, String modifierName) {
        int mod = ClassMaker.fromModifierString(modifierName);
        stack.push("addModifier(" + modifiers + ", \"" + modifierName + "\")");
        return modifiers | mod;
    }

    @Override
    public void Method(String methodName, Type returnType, int methodModifiers)
            throws ClassMakerException {
        typeMap.put(methodName + "()", returnType);
        String modifiers = toModifierString(methodModifiers);
        println("Method(\"" + methodName + "\", \"" + returnType.getName() + "\", "
                + modifiers + ")");
    }

    @Override
    public void Method(String methodName, String returnType, int methodModifiers)
            throws ClassMakerException {
        Type type = findType(returnType);
        Method(methodName, type, methodModifiers);
    }

    @Override
    public Labelled Begin() throws ClassMakerException {
        println("  Begin();");
        return null;
    }

    @Override
    public void End() throws ClassMakerException {
        println("  End();");
    }

    @Override
    public void Declare(String name, Class javaClass, int modifiers)
            throws ClassMakerException {
        String modStr = toModifierString(modifiers);
        String returnType = javaClass.getName();
        println("  Declare(\"" + name + "\", " + returnType + ", " + modStr + ");");
    }

    @Override
    public void Declare(String name, String typeName, int modifiers)
            throws ClassMakerException {
        String modStr = toModifierString(modifiers);
        println("  Declare(\"" + name + "\", \"" + typeName + "\", " + modStr
                + ");");
    }

    @Override
    public void Declare(String name, Type type, int modifiers)
            throws ClassMakerException {
        String modStr = toModifierString(modifiers);
        String returnType = type.getName();
        println("  Declare(\"" + name + "\", \"" + returnType + "\", " + modStr + ");");
    }
    
    String toModifierString(int modifiers) {
        if (modifiers == 0) {
            return "0";
        }
        StringBuffer buf = new StringBuffer();
        appendModifier(buf, modifiers, ClassMaker.ACC_PUBLIC, "ACC_PUBLIC");
        appendModifier(buf, modifiers, ClassMaker.ACC_PROTECTED, "ACC_PROTECTED");
        appendModifier(buf, modifiers, ClassMaker.ACC_PRIVATE, "ACC_PRIVATE");
        appendModifier(buf, modifiers, ClassMaker.ACC_STATIC, "ACC_STATIC");
        appendModifier(buf, modifiers, ClassMaker.ACC_FINAL, "ACC_FINAL");
        appendModifier(buf, modifiers, ClassMaker.ACC_SYNCHRONIZED, "ACC_SYNCHRONIZED");
        appendModifier(buf, modifiers, ClassMaker.ACC_VOLATILE, "ACC_VOLATILE");
        appendModifier(buf, modifiers, ClassMaker.ACC_TRANSIENT, "ACC_TRANSIENT");
        appendModifier(buf, modifiers, ClassMaker.ACC_NATIVE, "ACC_NATIVE");
        appendModifier(buf, modifiers, ClassMaker.ACC_ABSTRACT, "ACC_ABSTRACT");
        appendModifier(buf, modifiers, ClassMaker.ACC_STRICTFP, "ACC_STRICTFP");
        return buf.toString();

    }

    /* Appends a modifier to a StringBuffer. */
    private static void appendModifier(StringBuffer buf, int modifiers, int expected, String name)
    {
        if ((modifiers & expected) != 0)
        {
            if (buf.length() > 0)
                buf.append(" | ");
            buf.append(name);
        }
    }
    
    @Override
    public Type Cast(Type source, String target) throws ClassMakerException {
        String ref = stack.pop();
        DeclaredType result = findDeclaredType(target);
        stack.push("Cast(" + ref + ", \"" + target + "\")");
        return result.getType();
    }

    @Override
    public Type Cast(Type source, Class target) throws ClassMakerException {
        String ref = stack.pop();
        DeclaredType result = this.findDeclaredType(target.getCanonicalName());
        stack.push("Cast(" + ref + ", " + target.getCanonicalName() + ")");
        return result.getType();
    }

    @Override
    public Type InstanceOf(Type reference, String target) {
        String ref = stack.pop();
        DeclaredType result = findDeclaredType(target);
        stack.push("InstanceOf(" + ref + ", \"" + target + "\")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    private Type promote(Type value1, Type value2) {
        if (ClassType.STRING_TYPE.equals(value1)
                || ClassType.STRING_TYPE.equals(value2))
            return ClassType.STRING_TYPE;
        else if (PrimitiveType.DOUBLE_TYPE.equals(value1)
                || PrimitiveType.DOUBLE_TYPE.equals(value2))
            return PrimitiveType.DOUBLE_TYPE;
        else if (PrimitiveType.FLOAT_TYPE.equals(value1)
                || PrimitiveType.FLOAT_TYPE.equals(value2))
            return PrimitiveType.FLOAT_TYPE;
        else if (PrimitiveType.LONG_TYPE.equals(value1)
                || PrimitiveType.LONG_TYPE.equals(value2))
            return PrimitiveType.LONG_TYPE;
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type Add(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Add(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2);
    }

    @Override
    public Type Subt(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Subt(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2);
    }

    @Override
    public Type Mult(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Mult(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2);
    }

    @Override
    public Type Div(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Div(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2);
    }

    @Override
    public Type Rem(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Rem(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2);
    }

    @Override
    public Type Neg(Type value) {
        String operand1 = stack.pop();
        stack.push("Neg(" + operand1 + ")");
        return value;
    }

    @Override
    public Type Xor(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Xor(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2);
    }

    @Override
    public Type And(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("And(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2);
    }

    @Override
    public Type Or(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Or(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2);
    }

    @Override
    public Type Inv(Type value) {
        String operand1 = stack.pop();
        stack.push("Inv(" + operand1 + ")");
        return value;
    }

    @Override
    public Type SHL(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("SHL(" + operand1 + ", " + operand2 + ")");
        return value1;
    }

    @Override
    public Type SHR(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("SHR(" + operand1 + ", " + operand2 + ")");
        return value1;
    }

    @Override
    public Type USHR(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("USHR(" + operand1 + ", " + operand2 + ")");
        return value1;
    }

    @Override
    public Type GT(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("GT(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    @Override
    public Type GE(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("GE(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    @Override
    public Type LE(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("LE(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    @Override
    public Type LT(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("LT(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    @Override
    public Type EQ(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("EQ(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    @Override
    public Type NE(Type value1, Type value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("NE(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    @Override
    public Type Not(Type value) {
        String operand1 = stack.pop();
        stack.push("Not(" + operand1 + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }
    
    public class InitialiserImpl implements ClassMaker.Initialiser
    {
        final ClassType classType;

        public InitialiserImpl(ClassType classType)
        {
            this.classType = classType;
        }
        
       @Override
        public ClassType Init(CallStack actualParameters)
        {
            ClassMakerText.this.Init(classType, actualParameters);
            return classType;
        }
    }

    @Override
    public Initialiser New(Class javaClass) throws ClassMakerException {
        return New(javaClass.getName());
    }

    @Override
    public Initialiser New(String className) throws ClassMakerException {
        DeclaredType declared = findDeclaredType(className);
        return New(declared);
    }

    @Override
    public Initialiser New(DeclaredType declared) throws ClassMakerException {
        stack.push("New(" + declared.getName() + ")");
        return new InitialiserImpl(declared.getClassType());
    }

    @Override
    public void Init(ClassType classType, CallStack actualParameters)
            throws ClassMakerException {
        String operand1 = stack.pop();
        String operand2 = (actualParameters == null) ? null : stack.pop();
        stack.push(operand1 + ".Init(" + operand2 + ")");
    }

    @Override
    public ArrayType NewArray(DeclaredType arrayType, Type size) {
        if (arrayType.getArrayType() == null)
            throw new IllegalArgumentException("Type is not an ArrayType: " + arrayType);
        String operand2 = stack.pop();
        String operand1 = arrayType.getName();
        stack.push("NewArray(" + operand1 + ", " + operand2 + ")");
        return arrayType.getArrayType();
    }

    @Override
    public ArrayType NewArray(DeclaredType type, CallStack dimensions) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("NewArray(" + operand1 + ", " + operand2 + ")");
        return type.getArrayType();
    }

    @Override
    public DeclaredType ArrayOf(Class javaClass) {
        return ArrayOf(javaClass.getName());
    }

    @Override
    public DeclaredType ArrayOf(String typeName) {
        DeclaredType declared = this.findDeclaredType(typeName);
        return ArrayOf(declared);
    }

    @Override
    public DeclaredType ArrayOf(DeclaredType type) {
        return new DeclaredType(ArrayOf(type.getType()));
    }

    @Override
    public ArrayType ArrayOf(Type type) {
        String typeName = type.getName();
        String name = typeName + "[]";
        String signature = "[" + type.getSignature();
        ArrayType element = new ArrayType(name, signature, type);
        return element;
    }

    @Override
    public Type GetAt(Type reference, Type index) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("GetAt(" + operand1 + ", " + operand2 + ")");
        return index;
    }

    @Override
    public Type AssignAt(Type array, Type index, Type value) {
        String operand3 = stack.pop();
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("AssignAt(" + operand1 + ", " + operand2 + ", " + operand3
                + ")");
        return index;
    }

    @Override
    public Type SetAt(Type arrayRef, Type index, Type value) {
        String operand3 = stack.pop();
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("SetAt(" + operand1 + ", " + operand2 + ", " + operand3
                + ")");
        return index;
    }

    @Override
    public PrimitiveType Length(Type arrayRef) {
        String ref = stack.pop();
        stack.push("Length(" + ref + ")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type Inc(String name) throws ClassMakerException {
        stack.push("Inc(\"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type Inc(Type reference, String name) throws ClassMakerException {
        String ref = stack.pop();
        stack.push("Inc(" + ref + ", \"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type Inc(String className, String name) throws ClassMakerException {
        stack.push("Inc(\"" + className + "\", \"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type Dec(String name) throws ClassMakerException {
        stack.push("Dec(\"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type Dec(Type reference, String name) throws ClassMakerException {
        String ref = stack.pop();
        stack.push("Dec(" + ref + ", \"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type Dec(String className, String name) throws ClassMakerException {
        stack.push("Dec(\"" + className + "\", \"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type PostInc(String name) throws ClassMakerException {
        stack.push("PostInc(\"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type PostInc(Type reference, String name) throws ClassMakerException {
        String ref = stack.pop();
        stack.push("PostInc(" + ref + ", \"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type PostInc(String className, String name)
            throws ClassMakerException {
        stack.push("PostInc(\"" + className + "\", \"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type PostDec(String name) throws ClassMakerException {
        stack.push("PostDec(\"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type PostDec(Type reference, String name) throws ClassMakerException {
        String ref = stack.pop();
        stack.push("PostDec(" + ref + ", \"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type PostDec(String className, String name)
            throws ClassMakerException {
        stack.push("PostDec(\"" + className + "\", \"" + name + "\")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type IncAt(Type array, Type index) throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("IncAt(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type DecAt(Type array, Type index) throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("DecAt(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type PostIncAt(Type array, Type index) throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("PostIncAt(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public Type PostDecAt(Type array, Type index) throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("PostDecAt(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.INT_TYPE;
    }

    @Override
    public CallStack Push(Type reference) {
        String operand1 = stack.pop();
        stack.push(operand1);
        CallStack callStack = new CallStackMaker(null);
        callStack.Push(reference);
        return callStack;
    }

    @Override
    public CallStack Push() {
        stack.push("");
        return new CallStackMaker(null);
    }

    @Override
    public AndOrExpression AndThen(AndOrExpression andOr, Type cond) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("AndThen(" + operand1 + ", " + operand2 + ")");
        return null;
    }

    @Override
    public AndOrExpression AndThen(Type cond) {
        String operand1 = stack.pop();
        stack.push("AndThen(" + operand1 + ")");
        return null;
    }

    @Override
    public AndOrExpression OrElse(AndOrExpression andOr, Type cond) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("OrElse(" + operand1 + ", " + operand2 + ")");
        return null;
    }

    @Override
    public AndOrExpression OrElse(Type cond) {
        String operand1 = stack.pop();
        stack.push("OrElse(" + operand1 + ")");
        return null;
    }

    @Override
    public Type Logic(AndOrExpression andOr, Type cond) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Logic(" + operand1 + ", " + operand2 + ")");
        return PrimitiveType.BOOLEAN_TYPE;
    }

    @Override
    public void Eval(Type value) {
        String operand1 = stack.pop();
        println("  Eval(" + operand1 + ");");
    }

    @Override
    public void Forward() throws ClassMakerException {
        println("Forward();");
    }

    @Override
    public void Return() throws ClassMakerException {
        println("  Return();");
    }

    @Override
    public void Return(Type type) throws ClassMakerException {
        String operand1 = stack.pop();
        println("  Return(" + operand1 + ");");
    }

    @Override
    public Labelled If(Type condition) throws ClassMakerException {
        String operand1 = stack.pop();
        println("  If(" + operand1 + ");");
        return null;
    }

    @Override
    public void Else() throws ClassMakerException {
        println("  Else();");
    }

    @Override
    public void EndIf() throws ClassMakerException {
        println("  EndIf();");
    }

    @Override
    public Labelled Loop() throws ClassMakerException {
        println("  Loop();");
        return null;
    }

    @Override
    public void EndLoop() throws ClassMakerException {
        println("  EndLoop();");
    }

    @Override
    public void While(Type condition) throws ClassMakerException {
        String operand1 = stack.pop();
        println("  While(" + operand1 + ");");
    }

    @Override
    public ForWhile For(Type declare) throws ClassMakerException {
        String operand1 = stack.pop();
        println("  For(" + operand1 + ");");
        return null;
    }

    @Override
    public void EndFor() throws ClassMakerException {
        println("  EndFor();");
    }

    @Override
    public void Break() throws ClassMakerException {
        println("  Break();");
    }

    @Override
    public void Break(String label) throws ClassMakerException {
        println("  Break(\"" + label + "\");");
    }

    @Override
    public void Continue() throws ClassMakerException {
        println("  Continue();");
    }

    @Override
    public void Continue(String label) throws ClassMakerException {
        println("  Continue(\"" + label + "\");");
    }

    @Override
    public Labelled Switch(Type type) {
        println("  Switch(" + type + ");");
        return null;
    }

    @Override
    public void Case(int key) {
        println("  Case(" + key + ");");
    }

    @Override
    public void Default() {
        println("  Default();");
    }

    @Override
    public void EndSwitch() {
        println("  EndSwitch();");
    }

    @Override
    public Labelled Try() {
        println("  Try();");
        return null;
    }

    @Override
    public void Catch(String exceptionName, String name) {
        println("  Catch(\"" + exceptionName + "\", \"" + name + "\");");
    }

    @Override
    public void Catch(Class javaClass, String name) throws ClassMakerException {
        println("  Catch(" + javaClass.getCanonicalName() + ", \"" + name
                + "\");");
    }

    @Override
    public void Finally() throws ClassMakerException {
        println("  Finally();");
    }

    @Override
    public void EndTry() throws ClassMakerException {
        println("  EndTry();");
    }

    @Override
    public ClassMakerException createException(String msg) {
        return new ClassMakerException(null, msg);
    }

    public String toString() {
        String result = out.toString();
        if (result.length() > 0)
            return result;
        else
            return stack.toString();
    }

    @Override
    public int getPass()
    {
        return ClassMaker.ONE_PASS;
    }

    @Override
    public void EndClass() throws ClassMakerException
    {
    }
}
