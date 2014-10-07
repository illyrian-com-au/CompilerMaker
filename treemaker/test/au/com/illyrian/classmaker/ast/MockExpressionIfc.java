// Copyright (c) 2014, Donald Strong.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.

package au.com.illyrian.classmaker.ast;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.ClassMaker.ForWhile;
import au.com.illyrian.classmaker.ClassMaker.Initialiser;
import au.com.illyrian.classmaker.ClassMaker.Labelled;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ExpressionIfc;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

public class MockExpressionIfc implements ExpressionIfc, SourceLine
{
    StringBuffer buf = new StringBuffer();
    
    public MockExpressionIfc()
    {
        addPrimitives();
        addStandardClasses();
    }
    
    /** Adds all the standard PrimitiveTypes */
    protected void addPrimitives()
    {
        ClassMaker.VOID_TYPE    = addPrimitiveType(PrimitiveType.VOID_INDEX, "void", "V", void.class);
        ClassMaker.BYTE_TYPE    = addPrimitiveType(PrimitiveType.BYTE_INDEX, "byte", "B", byte.class);
        ClassMaker.CHAR_TYPE    = addPrimitiveType(PrimitiveType.CHAR_INDEX, "char", "C", char.class);
        ClassMaker.DOUBLE_TYPE  = addPrimitiveType(PrimitiveType.DOUBLE_INDEX, "double", "D", double.class);
        ClassMaker.FLOAT_TYPE   = addPrimitiveType(PrimitiveType.FLOAT_INDEX, "float", "F", float.class);
        ClassMaker.INT_TYPE     = addPrimitiveType(PrimitiveType.INT_INDEX, "int", "I", int.class);
        ClassMaker.LONG_TYPE    = addPrimitiveType(PrimitiveType.LONG_INDEX, "long", "J", long.class);
        ClassMaker.SHORT_TYPE   = addPrimitiveType(PrimitiveType.SHORT_INDEX, "short", "S", short.class);
        ClassMaker.BOOLEAN_TYPE = addPrimitiveType(PrimitiveType.BOOLEAN_INDEX, "boolean", "Z", boolean.class);
    }

    /** Adds important Types representing standard java classes */
    protected void addStandardClasses()
    {
        // Prime the first objects in classTypeToString
        ClassMaker.NULL_TYPE = addClassType("null", (ClassType) null);
        ClassMaker.OBJECT_TYPE = addClassType(Object.class);
        ClassMaker.STRING_TYPE = addClassType(String.class);
        // An automatically created StringBuffer resulting from concatenating
        // a String with any value or object.
        ClassMaker.AUTO_STRING_TYPE = addClassType(StringBuffer.class);
        // This hides AUTO_STRING_TYPE from normal use.
        ClassMaker.STRING_BUFFER_TYPE = addClassType(StringBuffer.class);
        ClassMaker.CLONEABLE_TYPE = addClassType(Cloneable.class);
        ClassMaker.THROWABLE_TYPE = addClassType(Throwable.class);
        ClassMaker.CLASS_TYPE = addClassType(Class.class);
    }

    /**
     * Adds a primitive type to the type map.
     * @param index an index for the primitive used in case statements
     * @param name the name of the primitive type
     * @param signature the JVM signature of the primitive type
     * @param javaClass the java class for the primitive type
     * @return a PrimitiveType that has been added to the Type map
     */
    protected PrimitiveType addPrimitiveType(int index, String name, String signature, Class javaClass)
    {
        PrimitiveType prim = new PrimitiveType(index, name, signature, javaClass);
        return prim;
    }

    /**
     * Adds a ClassType to the type map.
     * <br/>
     * A ClassType wrapper is created around the java class.
     * Information about the java class, such as implemented fields and methods,
     * will be lazy loaded into the ClassType as required.
     * @param javaClass the java class to be added
     * @return a ClassType that has been added to the Type map
     */
    protected ClassType addClassType(Class javaClass)
    {
        if (javaClass.isPrimitive()) // Should not get here
            throw new IllegalArgumentException(javaClass.getName() + " is not a class");
        String name = ClassMaker.classToName(javaClass);
        String signature = "L" + ClassMaker.toSlashName(name) + ";";
        ClassType type = new ClassType(name, signature, null);
        type.setJavaClass(javaClass);
        type.setModifiers(javaClass.getModifiers());
        return type;
    }

    /**
     * Adds the ClassType being generated to the type map.
     * <br/>
     * The ClassType being generated is added with minimal information so
     * that recursive declarations can be resolved. The details about
     * fields and methods that the class implements are added later.
     * @param className the fully qualified class name
     * @param extendsClass the ClassType that the given class extends
     * @return a ClassType that has been added to the Type map
     */
    protected ClassType addClassType(String className, ClassType extendsClass)
    {
        String name = className;
        String signature = "L" + ClassMaker.toSlashName(className) + ";";
        ClassType type = new ClassType(name, signature, extendsClass);
        return type;
    }

    public String toString()
    {
        return buf.toString();
    }

    public Initialiser New(Class javaClass) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Initialiser New(String className) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Initialiser New(DeclaredType declared) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void Init(ClassType classType, CallStack actualParameters) throws ClassMakerException
    {
        // TODO Auto-generated method stub

    }
    
    public void setPackageName(String packageName) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        
    }

    public void Import(String className) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        
    }

    public void setSimpleClassName(String className) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        
    }

    public void setClassModifiers(int modifiers)
    {
        // TODO Auto-generated method stub
        
    }

    public void Extends(String className) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        
    }

    public void Implements(String className) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        
    }

    public static class MyCallStack extends CallStack
    {
        public MyCallStack()
        {
            super((ClassMaker)null);
        }
        
        public CallStack Push(Type param) throws ClassMakerException
        {
            getStack().add(param.toType());
            return this;
        }

    }

    public CallStack Push(Type type)
    {
        CallStack callStack = new MyCallStack();
        //callStack.Push(reference);
        // Hack to avoid using classmaker instance
        callStack.Push(type);
        return callStack;
    }
    
    public CallStack Push()
    {
        return new CallStack(null);
    }
    
    public Type Call(Class javaClass, String methodName, CallStack actualParameters) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    private void callMethod(String prefix, String methodName, CallStack actualParameters)
    {
        buf.append("call(").append(prefix).append(",").append(methodName).append("(");
        int offset = 8-actualParameters.size();
        buf.append("$$$$$$$$".substring(offset));
        buf.append(")) ");
    }

    public Type Call(String className, String methodName, CallStack actualParameters) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
        callMethod("\"" + qualifiedName + "\"", methodName, actualParameters);
        return ClassMaker.INT_TYPE;
    }

    public Type Call(Type reference, String methodName, CallStack actualParameters) throws ClassMakerException
    {
        //buf.append("call($,").append(methodName).append("()) ");
        callMethod("$", methodName, actualParameters);
        return ClassMaker.INT_TYPE;
    }

//    public Type Call(CallStack actualParameters) throws ClassMakerException
//    {
//        if (actualParameters.getDeclaredType() != null)
//            return Call(actualParameters.getDeclaredType().getName(), actualParameters.getMethodName(), actualParameters);
//        else 
//            return Call(actualParameters.getReference(), actualParameters.getMethodName(), actualParameters);
//    }

    public ClassType This() throws ClassMakerException
    {
        buf.append("load(this) ");
        return ClassMaker.OBJECT_TYPE;
    }

    public ClassType Super() throws ClassMakerException
    {
        buf.append("load(super) ");
        return ClassMaker.OBJECT_TYPE;
    }

    public ClassType Null() throws ClassMakerException
    {
        buf.append("load(null) ");
        return ClassMaker.NULL_TYPE;
    }

    public int addModifier(int modifiers, String modifierName)
    {
        buf.append(" ").append(modifierName);
        return 0;
    }

    public void Method(String methodName, String returnType, int methodModifiers) throws ClassMakerException
    {
        buf.append(" Method(").append(methodName)
            .append(",").append(returnType)
            .append(", ").append(methodModifiers);
    }

    public Labelled Begin() throws ClassMakerException
    {
        buf.append(" Begin()");
        return null;
    }

    public void End() throws ClassMakerException
    {
        buf.append(" End()");
    }

    public void Forward() throws ClassMakerException
    {
        buf.append(" Forward()");
    }

    public void Return() throws ClassMakerException
    {
        buf.append(" Return()");
    }

    public void Return(Type type) throws ClassMakerException
    {
        buf.append(" Return($)");
    }

    public PrimitiveType Literal(double value) throws ClassMakerException
    {
        buf.append(value).append("d ");
        return ClassMaker.DOUBLE_TYPE;
    }

    public PrimitiveType Literal(float value) throws ClassMakerException
    {
        buf.append(value).append("f ");
        return ClassMaker.FLOAT_TYPE;
    }

    public PrimitiveType Literal(long value) throws ClassMakerException
    {
        buf.append(value).append("l ");
        return ClassMaker.LONG_TYPE;
    }

    public PrimitiveType Literal(int value) throws ClassMakerException
    {
        buf.append(value).append(" ");
        return ClassMaker.INT_TYPE;
    }

    public PrimitiveType Literal(char value) throws ClassMakerException
    {
        buf.append("'").append(value).append("' ");
        return ClassMaker.CHAR_TYPE;
    }

    public PrimitiveType Literal(byte value) throws ClassMakerException
    {
        buf.append(value).append("b ");
        return ClassMaker.BYTE_TYPE;
    }

    public PrimitiveType Literal(short value) throws ClassMakerException
    {
        buf.append(value).append("s ");
        return ClassMaker.SHORT_TYPE;
    }

    public PrimitiveType Literal(boolean value) throws ClassMakerException
    {
        buf.append(value?"true ":"false ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    public ClassType Literal(String value) throws ClassMakerException
    {
        buf.append("\"").append(value).append("\" ");
        return ClassMaker.STRING_TYPE;
    }

    public Type Assign(String name, Type type) throws ClassMakerException
    {
        buf.append("assign(").append(name).append(",$) ");
        return type;
    }

    public Type Assign(Type reference, String fieldName, Type value) throws ClassMakerException
    {
        buf.append("assign($,").append(fieldName).append(",$) ");
        return value;
    }

    public Type Assign(String className, String fieldName, Type value) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
        buf.append("assign(\"").append(qualifiedName).append("\",").append(fieldName).append(",$) ");
        return value;
    }

//    public Type Assign(Type lvalue, Type value) throws ClassMakerException
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }

    public Type Set(Type lvalue, Type value) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type Set(String name, Type value) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type Set(Type reference, String fieldName, Type value) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type Set(String className, String fieldName, Type value) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
        // TODO Auto-generated method stub
        return null;
    }

    Type get(String fieldName)
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
    
    public Type Get(Type reference, String fieldName) throws ClassMakerException
    {
        buf.append("load($,").append(fieldName).append(") ");
        return get(fieldName);
    }

    public Type Get(String className, String fieldName) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
        buf.append("load(\"").append(qualifiedName).append("\",").append(fieldName).append(") ");
        return get(fieldName);
    }

    public Type Get(String name) throws ClassMakerException
    {
        buf.append("load(").append(name).append(") ");
        Type type = findType(name);
        if (type != null)
            return type;
        throw createException("Unknown variable path: " + name);
    }

    public MakerField findField(String name) throws ClassMakerException
    {
        return Find(name);
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

    public void Declare(String name, Class javaClass, int modifiers) throws ClassMakerException
    {
        // TODO Auto-generated method stub

    }

    public void Declare(String name, String typeName, int modifiers) throws ClassMakerException
    {
        // TODO Auto-generated method stub

    }

    public void Declare(String name, Type type, int modifiers) throws ClassMakerException
    {
        // TODO Auto-generated method stub

    }

    public Type Cast(Type reference, String target) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(target);
        buf.append("cast($,\"").append(declared.getName()).append("\") ");
        return declared.getType();
    }

    public Type Cast(Type source, Class target) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type Cast(Type source, DeclaredType target) throws ClassMakerException
    {
        buf.append("cast($,\"").append(target.getName()).append("\") ");
        return target.getType();
    }
    
    public Type InstanceOf(Type reference, String target)
    {
        DeclaredType declared = FindDeclared(target);
        buf.append("instanceof($,\"").append(declared.getName()).append("\") ");
        return declared.getType();
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

    public Type Add(Type value1, Type value2)
    {
        buf.append("+$$ ");
        return promote(value1, value2);
    }

    public Type Subt(Type value1, Type value2)
    {
        buf.append("-$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type Mult(Type value1, Type value2)
    {
        buf.append("*$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type Div(Type value1, Type value2)
    {
        buf.append("/$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type Rem(Type value1, Type value2)
    {
        buf.append("%$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type Neg(Type value)
    {
        buf.append("-$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type Xor(Type value1, Type value2)
    {
        buf.append("~$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type And(Type value1, Type value2)
    {
        buf.append("&$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type Or(Type value1, Type value2)
    {
        buf.append("|$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type Inv(Type value)
    {
        buf.append("^$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type SHL(Type value1, Type value2)
    {
        buf.append("<<$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type SHR(Type value1, Type value2)
    {
        buf.append(">>$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type USHR(Type value1, Type value2)
    {
        buf.append(">>>$$ ");
        return ClassMaker.INT_TYPE;
    }

    public Type GT(Type value1, Type value2)
    {
        buf.append(">$$ ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    public Type GE(Type value1, Type value2)
    {
        buf.append(">=$$ ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    public Type LE(Type value1, Type value2)
    {
        buf.append("<=$$ ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    public Type LT(Type value1, Type value2)
    {
        buf.append("<$$ ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    public Type EQ(Type value1, Type value2)
    {
        buf.append("==$$ ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    public Type NE(Type value1, Type value2)
    {
        buf.append("!=$$ ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    public Type Not(Type value)
    {
        buf.append("!$ ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    public ArrayType NewArray(Type arrayType, Type size)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayType NewArray(Type array, CallStack dimensions)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type ArrayOf(Class javaClass)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type ArrayOf(String typeName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayType ArrayOf(Type type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type GetAt(Type reference, Type index)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type AssignAt(Type array, Type index, Type value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type SetAt(Type arrayRef, Type index, Type value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PrimitiveType Length(Type arrayRef)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void Eval(Type value)
    {
        if (!value.getName().equals("void"))
            buf.append("pop($)");
    }

    public Type Inc(String name) throws ClassMakerException
    {
        buf.append("inc(").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type Inc(Type reference, String name) throws ClassMakerException
    {
        buf.append("inc($,").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type Inc(String className, String name) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
        buf.append("inc(\"").append(qualifiedName).append("\",").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type Dec(String name) throws ClassMakerException
    {
        buf.append("dec(").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type Dec(Type reference, String name) throws ClassMakerException
    {
        buf.append("dec($,").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type Dec(String className, String name) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
        buf.append("dec(\"").append(qualifiedName).append("\",").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type PostInc(String name) throws ClassMakerException
    {
        buf.append("postinc(").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type PostInc(Type reference, String name) throws ClassMakerException
    {
        buf.append("postinc($,").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type PostInc(String className, String name) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
        buf.append("postinc(\"").append(qualifiedName).append("\",").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type PostDec(String name) throws ClassMakerException
    {
        buf.append("postdec(").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type PostDec(Type reference, String name) throws ClassMakerException
    {
        buf.append("postdec($,").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type PostDec(String className, String name) throws ClassMakerException
    {
        DeclaredType declared = FindDeclared(className);
        if (declared == null)
            throw createException("Unknown Class path: " + className);
        String qualifiedName  = declared.getName();
        buf.append("postdec(\"").append(qualifiedName).append("\",").append(name).append(") ");
        return ClassMaker.INT_TYPE;
    }

    public Type IncAt(Type array, Type index) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type DecAt(Type array, Type index) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type PostIncAt(Type array, Type index) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Type PostDecAt(Type array, Type index) throws ClassMakerException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public AndOrExpression AndThen(AndOrExpression andOr, Type cond)
    {
        buf.append("&& ");
        return andOr;
    }

    public AndOrExpression AndThen(Type cond)
    {
        buf.append("{&& ");
        return new AndOrExpression();
    }

    public AndOrExpression OrElse(AndOrExpression andOr, Type cond)
    {
        buf.append("|| ");
        return andOr;
    }

    public AndOrExpression OrElse(Type cond)
    {
        buf.append("{|| ");
        return new AndOrExpression();
    }

    public Type Logic(AndOrExpression andOr, Type cond)
    {
        buf.append("} ");
        return ClassMaker.BOOLEAN_TYPE;
    }

    /**
     * Begins an <code>If</code> statement.
     * @return an interface to set a Label
     */
    public Labelled If(Type condition) throws ClassMakerException
    {
        buf.append("If(" +condition + ") {");
        return null;
    }

    /**
     * Begins an <code>Else</code> clause of an <code>If</code> statement.
     */
    public void Else() throws ClassMakerException
    {
        buf.append("} else {");
    }

    /**
     * Ends an <ocde>If</code> Statement.
     */
    public void EndIf() throws ClassMakerException
    {
        buf.append("}");
    }


    public Labelled Loop() throws ClassMakerException
    {
        buf.append("loop()\n");
        return null;
    }

    public void EndLoop() throws ClassMakerException
    {
        buf.append("endloop()\n");
    }

    public void While(Type condition) throws ClassMakerException
    {
        buf.append("while($)\n");
    }

    public ForWhile For(Type declare) throws ClassMakerException
    {
        buf.append("for($)\n");
        return null;
    }

    public void EndFor() throws ClassMakerException
    {
        buf.append("endfor()\n");
    }

    public void Break() throws ClassMakerException
    {
        buf.append("break();\n");
    }

    public void Break(String label) throws ClassMakerException
    {
        buf.append("break("+ label + ")\n");
    }

    public void Continue() throws ClassMakerException
    {
        buf.append("continue()\n");
    }

    public void Continue(String label) throws ClassMakerException
    {
        buf.append("continue("+label+")\n");
    }

    // SourceLine interface implementation
    /** The name of the source file. */
    public String getFilename()
    {
        return "MyClass.java";
    }

    /** The current line number in the source file */
    public int getLineNumber()
    {
        return 1;
    }

    public ClassMakerException createException(String msg)
    {
        throw new ClassMakerException(this, msg);
    }
}
