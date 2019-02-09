package au.com.illyrian.classmaker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Stack;

import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;
import au.com.illyrian.classmaker.util.MakerUtil;

public class ClassMakerText extends PrintWriter implements ClassMakerIfc {
    ClassMakerFactory factory = new ClassMakerFactory();
    Stack<String> stack = new Stack<String>();
    HashMap<String, Type>typeMap = new HashMap<String, Type>();

    public ClassMakerText() {
        this(new StringWriter());
    }

    public ClassMakerText(Writer out) {
        super(out);
        typeMap.put("match()", ClassMakerFactory.BOOLEAN_TYPE);
        typeMap.put("expect()", ClassMakerFactory.STRING_TYPE);
    }
    
    public ClassMakerIfc createClassMaker() {
        return new ClassMakerText();
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
    public void Extends(Class javaClass) throws ClassMakerException {
        println("Extends(\"" + javaClass.getName() + "\");");
    }

    @Override
    public void Extends(ClassType classType) throws ClassMakerException {
        println("Extends(\"" + classType.getName() + "\");");
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
    public void Implements(ClassType classType) throws ClassMakerException {
        println("Implements(\"" + classType.getName() + "\");");
    }

    @Override
    public void Implements(Class javaClass) throws ClassMakerException {
        println("Implements(\"" + javaClass.getName() + "\");");
    }

    @Override
    public Value Call(String className, String methodName,
            CallStack actualParameters) throws ClassMakerException {
        String parameters = processCallStack(actualParameters.size());
        stack.push("Call(\"" + className + "\", \"" + methodName + "\", "
                + parameters + ")");
        Type returnType = typeMap.get(methodName + "()");
        return (returnType == null) ? ClassMakerFactory.OBJECT_TYPE.getValue() : returnType.getValue();
    }

    @Override
    public Value Call(Value reference, String methodName,
            CallStack actualParameters) throws ClassMakerException {
        String parameters = processCallStack(actualParameters.size());
        String refType = stack.pop();
        stack.push("Call(" + refType + ", \"" + methodName + "\", "
                + parameters + ")");
        Type returnType = typeMap.get(methodName + "()");
        return (returnType == null) ? ClassMakerFactory.OBJECT_TYPE.getValue() : returnType.getValue();
    }

    private String processCallStack(int count) {
        String result;
        if (count > 1) {
            String value = stack.pop();
            result = processCallStack(count - 1) + ".Push(" + value + ")";
        } else if (count == 1) {
            result = "Push(" + stack.pop() + ")";
        } else {
            result = "Push()";
        }
        return result;
    }

    @Override
    public Value This() throws ClassMakerException {
        stack.push("This()");
        return ClassMakerFactory.OBJECT_TYPE.getValue();
    }

    @Override
    public Value Super() throws ClassMakerException {
        stack.push("Super()");
        return ClassMakerFactory.OBJECT_TYPE.getValue();
    }

    @Override
    public Value Null() throws ClassMakerException {
        stack.push("Null()");
        return ClassMakerFactory.NULL_TYPE.getValue();
    }

    @Override
    public Value Literal(double value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return ClassMakerFactory.DOUBLE_TYPE.getValue();
    }

    @Override
    public Value Literal(float value) throws ClassMakerException {
        stack.push("Literal(" + value + "f)");
        return ClassMakerFactory.FLOAT_TYPE.getValue();
    }

    @Override
    public Value Literal(long value) throws ClassMakerException {
        stack.push("Literal(" + value + "l)");
        return ClassMakerFactory.LONG_TYPE.getValue();
    }

    @Override
    public Value Literal(int value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value Literal(char value) throws ClassMakerException {
        stack.push("Literal(\'" + (char) value + "\')");
        return ClassMakerFactory.CHAR_TYPE.getValue();
    }

    @Override
    public Value Literal(byte value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return ClassMakerFactory.BYTE_TYPE.getValue();
    }

    @Override
    public Value Literal(short value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return ClassMakerFactory.SHORT_TYPE.getValue();
    }

    @Override
    public Value Literal(boolean value) throws ClassMakerException {
        stack.push("Literal(" + value + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    @Override
    public Value Literal(String value) throws ClassMakerException {
        stack.push("Literal(\"" + value + "\")");
        return ClassMakerFactory.STRING_TYPE.getValue();
    }

    @Override
    public Value Assign(String name, Value type) throws ClassMakerException {
        String val = stack.pop();
        stack.push("Assign(\"" + name + "\", " + val + ")");
        return type;
    }

    @Override
    public Value Assign(Value type, String fieldName, Value value)
            throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Assign(" + operand1 + ", \"" + fieldName + "\", "
                + operand2 + ")");
        return value;
    }

    @Override
    public Value Assign(String className, String fieldName, Value value)
            throws ClassMakerException {
        String val = stack.pop();
        stack.push("Assign(\"" + className + "\", \"" + fieldName + "\", "
                + val + ")");
        return value;
    }

    @Override
    public Value Set(String name, Value value) throws ClassMakerException {
        String val = stack.pop();
        stack.push("Set(\"" + name + "\", " + val + ")");
        return value;
    }

    @Override
    public Value Set(Value reference, String fieldName, Value value)
            throws ClassMakerException {
        String val = stack.pop();
        String ref = stack.pop();
        stack.push("Set(" + ref + ", \"" + fieldName + "\", " + val + ")");
        return null;
    }

    @Override
    public Value Set(String className, String fieldName, Value value)
            throws ClassMakerException {
        String val = stack.pop();
        stack.push("Set(\"" + className + "\", \"" + fieldName + "\", " + val
                + ")");
        return null;
    }

    private Value get(String fieldName) {
        if ("x".equals(fieldName) || "y".equals(fieldName)
                || "z".equals(fieldName))
            return ClassMakerFactory.OBJECT_TYPE.getValue();
        else if ("this".equals(fieldName) || "super".equals(fieldName))
            return ClassMakerFactory.OBJECT_TYPE.getValue();
        else if ("class".equals(fieldName))
            return ClassMakerFactory.CLASS_TYPE.getValue();
        else
            return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value Get(Value reference, String fieldName)
            throws ClassMakerException {
        String ref = stack.pop();
        stack.push("Get(" + ref + ", \"" + fieldName + "\")");
        return get(fieldName);
    }

    @Override
    public Value Get(String className, String fieldName)
            throws ClassMakerException {
        Type declared = findType(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName = declared.getName();
        stack.push("Get(\"" + qualifiedName + "\", \"" + fieldName + "\")");
        return get(fieldName);
    }

    @Override
    public Value Get(String name) throws ClassMakerException {
        stack.push("Get(\"" + name + "\")");
        return get(name);
    }

    public MakerField Find(Value reference, String name)
            throws ClassMakerException {
        Type valueType = findTypeOfValue(name);
        if (valueType != null && reference.toClass() != null)
            return new MakerField(reference.toClass(), name, valueType, 0);
        return null;
    }

    public MakerField Find(String className, String name)
            throws ClassMakerException {
        Type classType = findType(className);
        if (classType == null)
            throw createException("Unknown path: " + className);
        Type valueType = findTypeOfValue(name);
        if (valueType != null)
            return new MakerField(classType.toClass(), name, valueType,
                    ClassMakerConstants.ACC_STATIC);
        return null;
    }

    public MakerField Find(String name) throws ClassMakerException {
        Type type = findTypeOfValue(name);
        if (type != null) {
            MakerField field = new MakerField(name, type, 0);
            // ScopeLevel > 0 means variable is declared in a method.
            field.setScopeLevel(1);
            return field;
        }
        return null;
    }

    public void addType(String name, Class classType) {
        Type type = new ClassType(classType);
        typeMap.put(name, type);
        String qname = classType.getName();
        if (!name.equals(qname)) {
            typeMap.put(qname, type);
        }
    }

    public Type findTypeOfValue(String name) throws ClassMakerException {
        if ("x".equals(name) || "y".equals(name) || "z".equals(name))
            return ClassMakerFactory.OBJECT_TYPE;
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
            return ClassMakerFactory.INT_TYPE;
    }
    
    public Type findType(String typeName) throws ClassMakerException {
        if (typeName.endsWith("Object"))
            return ClassMakerFactory.OBJECT_TYPE;
        else if (typeName.endsWith("String"))
            return ClassMakerFactory.STRING_TYPE;
        else if (typeName.endsWith("StringBuffer"))
            return ClassMakerFactory.STRING_BUFFER_TYPE;
        else if (typeName.endsWith("StringBuilder"))
            return ClassMakerFactory.STRING_BUFFER_TYPE;
        else if (typeName.equals("int"))
            return ClassMakerFactory.INT_TYPE;
        else {
            // FIXME Remove DeclaredType from MakerField constructor 
            // then return null and fix Find(...).
            //return null; 
            return findTypeOfValue(typeName);
        }
    }
    
     @Override
    public MakerField findField(String name) throws ClassMakerException {
        return Find(name);
    }

    @Override
    public int addModifier(int modifiers, String modifierName) {
        int mod = MakerUtil.fromModifierString(modifierName);
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
    public void Method(String methodName, Class returnType, int methodModifiers)
            throws ClassMakerException {
        Method(methodName, returnType.getSimpleName(), methodModifiers);
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
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_PUBLIC, "ACC_PUBLIC");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_PROTECTED, "ACC_PROTECTED");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_PRIVATE, "ACC_PRIVATE");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_STATIC, "ACC_STATIC");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_FINAL, "ACC_FINAL");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_SYNCHRONIZED, "ACC_SYNCHRONIZED");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_VOLATILE, "ACC_VOLATILE");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_TRANSIENT, "ACC_TRANSIENT");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_NATIVE, "ACC_NATIVE");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_ABSTRACT, "ACC_ABSTRACT");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_STRICTFP, "ACC_STRICTFP");
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
    public Value Cast(Value source, Type target) throws ClassMakerException {
        return Cast(source, target.getName());
    }
    
    @Override
    public Value Cast(Value source, String target) throws ClassMakerException {
        String ref = stack.pop();
        Type result = findType(target);
        stack.push("Cast(" + ref + ", \"" + target + "\")");
        return result.getValue();
    }

    @Override
    public Value Cast(Value source, Class target) throws ClassMakerException {
        String ref = stack.pop();
        Type result = findType(target.getCanonicalName());
        stack.push("Cast(" + ref + ", " + target.getCanonicalName() + ")");
        return result.getValue();
    }

    @Override
    public Value InstanceOf(Value reference, Type target) {
        return InstanceOf(reference, target.getName());
    }

    @Override
    public Value InstanceOf(Value reference, String target) {
        String ref = stack.pop();
        stack.push("InstanceOf(" + ref + ", \"" + target + "\")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    private Type promote(Value value1, Value value2) {
        Type type1 = value1.getType();
        Type type2 = value2.getType();
        if (ClassMakerFactory.STRING_TYPE.equals(type1)
                || ClassMakerFactory.STRING_TYPE.equals(type2))
            return ClassMakerFactory.STRING_TYPE;
        else if (ClassMakerFactory.DOUBLE_TYPE.equals(type1)
                || ClassMakerFactory.DOUBLE_TYPE.equals(type2))
            return ClassMakerFactory.DOUBLE_TYPE;
        else if (ClassMakerFactory.FLOAT_TYPE.equals(type1)
                || ClassMakerFactory.FLOAT_TYPE.equals(type2))
            return ClassMakerFactory.FLOAT_TYPE;
        else if (ClassMakerFactory.LONG_TYPE.equals(type1)
                || ClassMakerFactory.LONG_TYPE.equals(type2))
            return ClassMakerFactory.LONG_TYPE;
        return ClassMakerFactory.INT_TYPE;
    }

    @Override
    public Value Add(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Add(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2).getValue();
    }

    @Override
    public Value Subt(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Subt(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2).getValue();
    }

    @Override
    public Value Mult(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Mult(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2).getValue();
    }

    @Override
    public Value Div(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Div(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2).getValue();
    }

    @Override
    public Value Rem(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Rem(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2).getValue();
    }

    @Override
    public Value Neg(Value value) {
        String operand1 = stack.pop();
        stack.push("Neg(" + operand1 + ")");
        return value;
    }

    @Override
    public Value Xor(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Xor(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2).getValue();
    }

    @Override
    public Value And(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("And(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2).getValue();
    }

    @Override
    public Value Or(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Or(" + operand1 + ", " + operand2 + ")");
        return promote(value1, value2).getValue();
    }

    @Override
    public Value Inv(Value value) {
        String operand1 = stack.pop();
        stack.push("Inv(" + operand1 + ")");
        return value;
    }

    @Override
    public Value SHL(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("SHL(" + operand1 + ", " + operand2 + ")");
        return value1;
    }

    @Override
    public Value SHR(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("SHR(" + operand1 + ", " + operand2 + ")");
        return value1;
    }

    @Override
    public Value USHR(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("USHR(" + operand1 + ", " + operand2 + ")");
        return value1;
    }

    @Override
    public Value GT(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("GT(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    @Override
    public Value GE(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("GE(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    @Override
    public Value LE(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("LE(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    @Override
    public Value LT(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("LT(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    @Override
    public Value EQ(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("EQ(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    @Override
    public Value NE(Value value1, Value value2) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("NE(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    @Override
    public Value Not(Value value) {
        String operand1 = stack.pop();
        stack.push("Not(" + operand1 + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }
    
    public class InitialiserImpl implements Initialiser
    {
        final ClassType classType;

        public InitialiserImpl(ClassType classType)
        {
            this.classType = classType;
        }
        
       @Override
        public Value Init(CallStack actualParameters)
        {
            ClassMakerText.this.Init(classType.getValue(), actualParameters);
            return classType.getValue();
        }
    }

    @Override
    public Initialiser New(Class javaClass) throws ClassMakerException {
        return New(javaClass.getName());
    }

    @Override
    public Initialiser New(String className) throws ClassMakerException {
        Type type = findType(className);
        return New(type);
    }

    @Override
    public Initialiser New(Type classType) throws ClassMakerException {
        stack.push("New(" + classType.getName() + ")");
        return new InitialiserImpl(classType.toClass());
    }

    @Override
    public void Init(Value classType, CallStack actualParameters)
            throws ClassMakerException {
        String operand1 = stack.pop();
        String operand2 = (actualParameters == null) ? null : stack.pop();
        stack.push(operand1 + ".Init(" + operand2 + ")");
    }

    @Override
    public Value NewArray(Type arrayType, Value size) {
        if (arrayType.toArray() == null)
            throw new IllegalArgumentException("Value is not an ArrayType: " + arrayType);
        String operand2 = stack.pop();
        String operand1 = arrayType.getName();
        stack.push("NewArray(" + operand1 + ", " + operand2 + ")");
        return arrayType.getValue();
    }

    @Override
    public ArrayType ArrayOf(Class javaClass) {
        return ArrayOf(javaClass.getName());
    }

    @Override
    public ArrayType ArrayOf(String typeName) {
        Type declared = this.findType(typeName);
        return ArrayOf(declared);
    }

    public ArrayType ArrayOf(Type type) {
        String typeName = type.getName();
        String name = typeName + "[]";
        String signature = "[" + type.getSignature();
        ArrayType element = new ArrayType(name, signature, type, null);
        return element;
    }

    @Override
    public Value GetAt(Value reference, Value index) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("GetAt(" + operand1 + ", " + operand2 + ")");
        return index;
    }

    @Override
    public Value AssignAt(Value array, Value index, Value value) {
        String operand3 = stack.pop();
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("AssignAt(" + operand1 + ", " + operand2 + ", " + operand3
                + ")");
        return index;
    }

    @Override
    public Value SetAt(Value arrayRef, Value index, Value value) {
        String operand3 = stack.pop();
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("SetAt(" + operand1 + ", " + operand2 + ", " + operand3
                + ")");
        return index;
    }

    @Override
    public Value Length(Value arrayRef) {
        String ref = stack.pop();
        stack.push("Length(" + ref + ")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value Inc(String name) throws ClassMakerException {
        stack.push("Inc(\"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value Inc(Value reference, String name) throws ClassMakerException {
        String ref = stack.pop();
        stack.push("Inc(" + ref + ", \"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value Inc(String className, String name) throws ClassMakerException {
        stack.push("Inc(\"" + className + "\", \"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value Dec(String name) throws ClassMakerException {
        stack.push("Dec(\"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value Dec(Value reference, String name) throws ClassMakerException {
        String ref = stack.pop();
        stack.push("Dec(" + ref + ", \"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value Dec(String className, String name) throws ClassMakerException {
        stack.push("Dec(\"" + className + "\", \"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value PostInc(String name) throws ClassMakerException {
        stack.push("PostInc(\"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value PostInc(Value reference, String name) throws ClassMakerException {
        String ref = stack.pop();
        stack.push("PostInc(" + ref + ", \"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value PostInc(String className, String name)
            throws ClassMakerException {
        stack.push("PostInc(\"" + className + "\", \"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value PostDec(String name) throws ClassMakerException {
        stack.push("PostDec(\"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value PostDec(Value reference, String name) throws ClassMakerException {
        String ref = stack.pop();
        stack.push("PostDec(" + ref + ", \"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value PostDec(String className, String name)
            throws ClassMakerException {
        stack.push("PostDec(\"" + className + "\", \"" + name + "\")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value IncAt(Value array, Value index) throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("IncAt(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value DecAt(Value array, Value index) throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("DecAt(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value PostIncAt(Value array, Value index) throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("PostIncAt(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    @Override
    public Value PostDecAt(Value array, Value index) throws ClassMakerException {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("PostDecAt(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.INT_TYPE.getValue();
    }
    
    public class CallStackText implements CallStack
    {
        String name;
        int size = 0;
        
        @Override
        public CallStack Push(Value reference)
        {
            size++;
            return this;
        }

        @Override
        public Type[] toArray()
        {
            return null;
        }

        @Override
        public String getMethodName()
        {
            return name;
        }

        @Override
        public void setMethodName(String methodName)
        {
            name = methodName;
        }

        @Override
        public int size()
        {
            return size;
        }
    }

    @Override
    public CallStack Push(Value reference) {
        CallStack callStack = new CallStackText();
        callStack.Push(reference);
        return callStack;
    }

    @Override
    public CallStack Push() {
        return new CallStackText();
    }

    @Override
    public AndOrExpression AndThen(AndOrExpression andOr, Value cond) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("AndThen(" + operand1 + ", " + operand2 + ")");
        return null;
    }

    @Override
    public AndOrExpression AndThen(Value cond) {
        String operand1 = stack.pop();
        stack.push("AndThen(" + operand1 + ")");
        return null;
    }

    @Override
    public AndOrExpression OrElse(AndOrExpression andOr, Value cond) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("OrElse(" + operand1 + ", " + operand2 + ")");
        return null;
    }

    @Override
    public AndOrExpression OrElse(Value cond) {
        String operand1 = stack.pop();
        stack.push("OrElse(" + operand1 + ")");
        return null;
    }

    @Override
    public Value Logic(AndOrExpression andOr, Value cond) {
        String operand2 = stack.pop();
        String operand1 = stack.pop();
        stack.push("Logic(" + operand1 + ", " + operand2 + ")");
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    @Override
    public void Eval(Value value) {
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
    public void Return(Value type) throws ClassMakerException {
        String operand1 = stack.pop();
        println("  Return(" + operand1 + ");");
    }

    @Override
    public Labelled If(Value condition) throws ClassMakerException {
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
    public void While(Value condition) throws ClassMakerException {
        String operand1 = stack.pop();
        println("  While(" + operand1 + ");");
    }

    @Override
    public ForWhile For(Value declare) throws ClassMakerException {
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
    public Labelled Switch(Value type) {
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
    public ClassMakerException createException(String key, String ... values) {
        return new ClassMakerException(null, key);
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
        return ClassMakerConstants.ONE_PASS;
    }

    @Override
    public void EndClass() throws ClassMakerException
    {
    }

}
