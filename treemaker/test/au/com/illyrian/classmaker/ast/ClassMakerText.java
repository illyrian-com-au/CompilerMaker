package au.com.illyrian.classmaker.ast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;

import au.com.illyrian.classmaker.CallStackMaker;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.ClassMaker.ForWhile;
import au.com.illyrian.classmaker.ClassMaker.Labelled;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ExpressionIfc;
import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

public class ClassMakerText extends PrintWriter implements ExpressionIfc 
{
	ClassMakerFactory factory = new ClassMakerFactory();
	Stack<String> stack = new Stack<String>();
	
    public ClassMakerText()
    {
        this(new StringWriter());
    }
    
    public ClassMakerText(Writer out)
    {
        super(out);
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
		println("setClassModifiers(" + modifiers + ");"); // FIXME
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
		stack.push("Call(\"" + className + "\", \"" + methodName + "\", " + parameters + ")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type Call(Type reference, String methodName,
			CallStack actualParameters) throws ClassMakerException {
		String parameters = processCallStack(actualParameters.size());
		String refType  = stack.pop();
		stack.push("Call(" + refType + ", \"" + methodName + "\", " + parameters + ")");
		return ClassMaker.INT_TYPE;
	}
	
	private String processCallStack(int count)
	{
		String result;
		if (count > 1) 
		{
			String value = stack.pop();
			result =  processCallStack(count-1) + ".Push(" + value + ")";
		}
		else
			result = "Push(" + stack.pop() + ")";
		return result;
	}

	@Override
	public ClassType This() throws ClassMakerException {
		stack.push("This()");
        return ClassMaker.OBJECT_TYPE;
	}

	@Override
	public ClassType Super() throws ClassMakerException {
		stack.push("Super()");
        return ClassMaker.OBJECT_TYPE;
	}

	@Override
	public ClassType Null() throws ClassMakerException {
		stack.push("Null()");
        return ClassMaker.NULL_TYPE;
	}

	@Override
	public PrimitiveType Literal(double value) throws ClassMakerException {
		stack.push("Literal(" + value + ")");
        return ClassMaker.DOUBLE_TYPE;
	}

	@Override
	public PrimitiveType Literal(float value) throws ClassMakerException {
		stack.push("Literal(" + value + "f)");
        return ClassMaker.FLOAT_TYPE;
	}

	@Override
	public PrimitiveType Literal(long value) throws ClassMakerException {
		stack.push("Literal(" + value + "l)");
        return ClassMaker.LONG_TYPE;
	}

	@Override
	public PrimitiveType Literal(int value) throws ClassMakerException {
		stack.push("Literal(" + value + ")");
        return ClassMaker.INT_TYPE;
	}

	@Override
	public PrimitiveType Literal(char value) throws ClassMakerException {
		stack.push("Literal(\'" + (char)value + "\')");
        return ClassMaker.CHAR_TYPE;
	}

	@Override
	public PrimitiveType Literal(byte value) throws ClassMakerException {
		stack.push("Literal(" + value + ")");
        return ClassMaker.BYTE_TYPE;
	}

	@Override
	public PrimitiveType Literal(short value) throws ClassMakerException {
		stack.push("Literal(" + value + ")");
        return ClassMaker.SHORT_TYPE;
	}

	@Override
	public PrimitiveType Literal(boolean value) throws ClassMakerException {
		stack.push("Literal(" + value + ")");
        return ClassMaker.BOOLEAN_TYPE;
	}

	@Override
	public ClassType Literal(String value) throws ClassMakerException {
		stack.push("Literal(\"" + value + "\")");
        return ClassMaker.STRING_TYPE;
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
		stack.push("Assign(" + operand1 + ", \"" + fieldName + "\", " + operand2 + ")");
		return value;
	}

	@Override
	public Type Assign(String className, String fieldName, Type value)
			throws ClassMakerException {
		String val = stack.pop();
		stack.push("Assign(\"" + className + "\", \"" + fieldName + "\", " + val + ")");
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
		stack.push("Set(\"" + className + "\", \"" + fieldName + "\", " + val + ")");
		return null;
	}

    private Type get(String fieldName)
    {
        if ("x".equals(fieldName) || "y".equals(fieldName) || "z".equals(fieldName))
            return ClassMaker.OBJECT_TYPE;
        else if ("this".equals(fieldName) || "super".equals(fieldName))
            return ClassMaker.OBJECT_TYPE;
        else if ("class".equals(fieldName))
            return ClassMaker.CLASS_TYPE;
        else
            return ClassMaker.INT_TYPE;
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
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
		stack.push("Get(\"" + qualifiedName + "\", \"" + fieldName + "\")");
		return get(fieldName);
	}

	@Override
	public Type Get(String name) throws ClassMakerException {
		stack.push("Get(\"" + name + "\")");
		return get(name);
	}

    public MakerField Find(Type reference, String name) throws ClassMakerException
    {
        Type type = findType(name);
        if (type != null && reference.toClass() != null)
            return new MakerField(reference.toClass(), name, type, 0);
        return null;
    }

    public MakerField Find(String className, String name) throws ClassMakerException
    {
        Type type = findType(name);
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown path: " + className);
        if (type != null)
            return new MakerField(declared.getClassType(), name, type, ClassMaker.ACC_STATIC);
        return null;
    }

    public MakerField Find(String name) throws ClassMakerException
    {
        Type type = findType(name);
        if (type != null)
        {
            MakerField field = new MakerField(name, type, 0);
            // ScopeLevel > 0 means variable is declared in a method.
            field.setScopeLevel(1); 
            return field;
        }
        return null;
    }

    Type findType(String name) throws ClassMakerException
    {
        if ("x".equals(name) || "y".equals(name) || "z".equals(name))
            return ClassMaker.OBJECT_TYPE;
        else if ("java".equals(name) || "lang".equals(name) || "test".equals(name))
            return null;
        else if ("int".equals(name))
            return null;
        else if (name.indexOf('.') > -1)
            return null;  // This is a path
        else if (name.endsWith("Object"))
            return null;
        else
            return ClassMaker.INT_TYPE;
    }

    public DeclaredType FindDeclared(String typeName) throws ClassMakerException
    {
        if (typeName.endsWith("Object"))
            return new DeclaredType(ClassMaker.OBJECT_TYPE);
        else if (typeName.endsWith("String"))
            return new DeclaredType(ClassMaker.STRING_TYPE);
        else if (typeName.equals("int"))
            return new DeclaredType(ClassMaker.INT_TYPE);
        else 
            return null;
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
	public void Method(String methodName, String returnType, int methodModifiers)
			throws ClassMakerException {
		String modifiers = ClassMaker.toModifierString(methodModifiers);
		println("Method(\"" + methodName + "\", \"" + returnType + "\", " + modifiers + ")");
	}

	@Override
	public Labelled Begin() throws ClassMakerException {
		println("Begin()");
		return null;
	}

	@Override
	public void End() throws ClassMakerException {
		println("End()");
	}

	@Override
	public void Declare(String name, Class javaClass, int modifiers)
			throws ClassMakerException {
		String modStr = ClassMaker.toModifierString(modifiers);
		String returnType = javaClass.getName();
		println("Declare(\"" + name + "\", " + returnType + ", " + modStr + ")");
	}

	@Override
	public void Declare(String name, String typeName, int modifiers)
			throws ClassMakerException {
		String modStr = ClassMaker.toModifierString(modifiers);
		println("Declare(\"" + name + "\", \"" + typeName + "\", " + modStr + ")");
	}

	@Override
	public void Declare(String name, Type type, int modifiers)
			throws ClassMakerException {
		String modStr = ClassMaker.toModifierString(modifiers);
		String returnType = type.getName();
		println("Method(\"" + name + "\", " + returnType + ", " + modStr + ")");
	}

	@Override
	public Type Cast(Type source, String target) throws ClassMakerException {
		String ref = stack.pop();
		DeclaredType result = FindDeclared(target);
		stack.push("Cast(" + ref + ", \"" + target + "\")");
		return result.getType();
	}

	@Override
	public Type Cast(Type source, Class target) throws ClassMakerException {
		String ref = stack.pop();
		DeclaredType result = this.FindDeclared(target.getCanonicalName());
		stack.push("Cast(" + ref + ", " + target.getCanonicalName() + ")");
		return result.getType();
	}

	@Override
	public Type InstanceOf(Type reference, String target) {
		String ref = stack.pop();
		DeclaredType result = FindDeclared(target);
		stack.push("InstanceOf(" + ref + ", \"" + target + "\")");
		return ClassMaker.BOOLEAN_TYPE;
	}

    private Type promote(Type value1, Type value2)
    {
        if (ClassMaker.STRING_TYPE.equals(value1) || ClassMaker.STRING_TYPE.equals(value2))
            return ClassMaker.STRING_TYPE;
        else if (ClassMaker.DOUBLE_TYPE.equals(value1) || ClassMaker.DOUBLE_TYPE.equals(value2))
            return ClassMaker.DOUBLE_TYPE;
        else if (ClassMaker.FLOAT_TYPE.equals(value1) || ClassMaker.FLOAT_TYPE.equals(value2))
            return ClassMaker.FLOAT_TYPE;
        else if (ClassMaker.LONG_TYPE.equals(value1) || ClassMaker.LONG_TYPE.equals(value2))
            return ClassMaker.LONG_TYPE;
        return ClassMaker.INT_TYPE;
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
		return ClassMaker.BOOLEAN_TYPE;
	}

	@Override
	public Type GE(Type value1, Type value2) {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("GE(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.BOOLEAN_TYPE;
	}

	@Override
	public Type LE(Type value1, Type value2) {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("LE(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.BOOLEAN_TYPE;
	}

	@Override
	public Type LT(Type value1, Type value2) {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("LT(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.BOOLEAN_TYPE;
	}

	@Override
	public Type EQ(Type value1, Type value2) {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("EQ(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.BOOLEAN_TYPE;
	}

	@Override
	public Type NE(Type value1, Type value2) {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("NE(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.BOOLEAN_TYPE;
	}

	@Override
	public Type Not(Type value) {
		String operand1 = stack.pop();
		stack.push("Not(" + operand1 + ")");
		return ClassMaker.BOOLEAN_TYPE;
	}

	@Override
	public ArrayType NewArray(Type arrayType, Type size) {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("NewArray(" + operand1 + ", " + operand2 + ")");
		return arrayType.toArray();
	}

	@Override
	public ArrayType NewArray(Type array, CallStack dimensions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type ArrayOf(Class javaClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type ArrayOf(String typeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayType ArrayOf(Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type GetAt(Type reference, Type index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type AssignAt(Type array, Type index, Type value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type SetAt(Type arrayRef, Type index, Type value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrimitiveType Length(Type arrayRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type Inc(String name) throws ClassMakerException {
		stack.push("Inc(\"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type Inc(Type reference, String name) throws ClassMakerException {
		String ref = stack.pop();
		stack.push("Inc(" + ref + ", \"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type Inc(String className, String name) throws ClassMakerException {
		stack.push("Inc(\"" + className + "\", \"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type Dec(String name) throws ClassMakerException {
		stack.push("Dec(\"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type Dec(Type reference, String name) throws ClassMakerException {
		String ref = stack.pop();
		stack.push("Dec(" + ref + ", \"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type Dec(String className, String name) throws ClassMakerException {
		stack.push("Dec(\"" + className + "\", \"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type PostInc(String name) throws ClassMakerException {
		stack.push("PostInc(\"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type PostInc(Type reference, String name) throws ClassMakerException {
		String ref = stack.pop();
		stack.push("PostInc(" + ref + ", \"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type PostInc(String className, String name)
			throws ClassMakerException {
		stack.push("PostInc(\"" + className + "\", \"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type PostDec(String name) throws ClassMakerException {
		stack.push("PostDec(\"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type PostDec(Type reference, String name) throws ClassMakerException {
		String ref = stack.pop();
		stack.push("PostDec(" + ref + ", \"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type PostDec(String className, String name)
			throws ClassMakerException {
		stack.push("PostDec(\"" + className + "\", \"" + name + "\")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type IncAt(Type array, Type index) throws ClassMakerException {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("IncAt(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type DecAt(Type array, Type index) throws ClassMakerException {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("DecAt(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type PostIncAt(Type array, Type index) throws ClassMakerException {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("PostIncAt(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.INT_TYPE;
	}

	@Override
	public Type PostDecAt(Type array, Type index) throws ClassMakerException {
		String operand2 = stack.pop();
		String operand1 = stack.pop();
		stack.push("PostDecAt(" + operand1 + ", " + operand2 + ")");
		return ClassMaker.INT_TYPE;
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
		return ClassMaker.BOOLEAN_TYPE;
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
		println("  Catch(\"" + exceptionName + "\", \""  + name  + "\");");
	}

	@Override
	public void Catch(Class javaClass, String name) throws ClassMakerException {
		println("  Catch(" + javaClass.getCanonicalName() + ", \""  + name  + "\");");
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

    public String toString()
    {
    	String result = out.toString();
    	if (result.length() > 0)
    		return result;
    	else
    		return stack.toString();
    }


}
