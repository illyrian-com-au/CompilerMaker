package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.ClassMaker.Initialiser;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

public class ExpressionDelegate //implements ExpressionIfc
{
    private final ClassMaker classMaker;
    
    public ExpressionDelegate(ClassMaker maker)
    {
        classMaker = maker;
    }

    public ClassMaker getClassMaker()
    {
        return classMaker;
    }

    public Initialiser New(Class javaClass) throws ClassMakerException
    {
        return classMaker.New(javaClass);
    }

    public Initialiser New(String className) throws ClassMakerException
    {
        return classMaker.New(className);
    }

    public Initialiser New(DeclaredType declared) throws ClassMakerException
    {
        return classMaker.New(declared);
    }

    public void Init(ClassType classType, CallStack actualParameters) throws ClassMakerException
    {
        classMaker.Init(classType, actualParameters);
    }

    public Type Call(Class javaClass, String methodName, CallStack actualParameters) throws ClassMakerException
    {
        return classMaker.Call(javaClass, methodName, actualParameters);
    }

    public Type Call(String className, String methodName, CallStack actualParameters) throws ClassMakerException
    {
        return classMaker.Call(className, methodName, actualParameters);
    }

    public Type Call(Type reference, String methodName, CallStack actualParameters) throws ClassMakerException
    {
        return classMaker.Call(reference, actualParameters);
    }

    public Type Call(Type reference, CallStack actualParameters) throws ClassMakerException
    {
        return classMaker.Call(reference, actualParameters);
    }

    public ClassType This() throws ClassMakerException
    {
        return classMaker.This();
    }

    public ClassType Super() throws ClassMakerException
    {
        return classMaker.Super();
    }

    public ClassType Null() throws ClassMakerException
    {
        return classMaker.Null();
    }

    public PrimitiveType Literal(double value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public PrimitiveType Literal(float value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public PrimitiveType Literal(long value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public PrimitiveType Literal(int value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public PrimitiveType Literal(char value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public PrimitiveType Literal(byte value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public PrimitiveType Literal(short value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public PrimitiveType Literal(boolean value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public ClassType Literal(String value) throws ClassMakerException
    {
        return classMaker.Literal(value);
    }

    public Type Assign(String name, Type type) throws ClassMakerException
    {
        return classMaker.Assign(name, type);
    }

    public Type Assign(Type type, String fieldName, Type value) throws ClassMakerException
    {
        return classMaker.Assign(type, fieldName, value);
    }

    public Type Assign(String className, String fieldName, Type value) throws ClassMakerException
    {
        return classMaker.Assign(className, fieldName, value);
    }

//    public Type Assign(MakerField lvalue, Type value) throws ClassMakerException
//    {
//        return classMaker.Assign(lvalue, value);
//    }
//
//    public Type Set(MakerField lvalue, Type value) throws ClassMakerException
//    {
//        return classMaker.Set(lvalue, value);
//    }

    public Type Set(String name, Type value) throws ClassMakerException
    {
        return classMaker.Set(name, value);
    }

    public Type Set(Type reference, String fieldName, Type value) throws ClassMakerException
    {
        return classMaker.Set(reference, fieldName, value);
    }

    public Type Set(String className, String fieldName, Type value) throws ClassMakerException
    {
        return classMaker.Set(className, fieldName, value);
    }

    public Type Get(Type reference, String fieldName) throws ClassMakerException
    {
        return classMaker.Get(reference, fieldName);
    }

    public Type Get(String className, String fieldName) throws ClassMakerException
    {
        return classMaker.Get(className, fieldName);
    }

    public Type Get(String name) throws ClassMakerException
    {
        return classMaker.Get(name);
    }

    public MakerField Find(Type reference, String name) throws ClassMakerException
    {
        return classMaker.Find(reference, name);
    }

    public MakerField Find(String className, String fieldName) throws ClassMakerException
    {
        return classMaker.Find(className, fieldName);
    }

    public MakerField Find(String name) throws ClassMakerException
    {
        return classMaker.Find(name);
    }

    public void Declare(String name, Class javaClass, int modifiers) throws ClassMakerException
    {
        classMaker.Declare(name, javaClass, modifiers);
    }

    public void Declare(String name, String typeName, int modifiers) throws ClassMakerException
    {
        classMaker.Declare(name, typeName, modifiers);
    }

    public void Declare(String name, Type type, int modifiers) throws ClassMakerException
    {
        classMaker.Declare(name, type, modifiers);
    }

    public Type Cast(Type source, String target) throws ClassMakerException
    {
        return classMaker.Cast(source, target);
    }

    public Type Cast(Type source, Class target) throws ClassMakerException
    {
        return classMaker.Cast(source, target);
    }

    public Type Cast(Type source, DeclaredType target) throws ClassMakerException
    {
        return classMaker.Cast(source, target);
    }

    public Type Add(Type value1, Type value2)
    {
        return classMaker.Add(value1, value2);
    }

    public Type Subt(Type value1, Type value2)
    {
        return classMaker.Subt(value1, value2);
    }

    public Type Mult(Type value1, Type value2)
    {
        return classMaker.Mult(value1, value2);
    }

    public Type Div(Type value1, Type value2)
    {
        return classMaker.Div(value1, value2);
    }

    public Type Neg(Type value)
    {
        return classMaker.Neg(value);
    }

    public Type Xor(Type value1, Type value2)
    {
        return classMaker.Xor(value1, value2);
    }

    public Type And(Type value1, Type value2)
    {
        return classMaker.And(value1, value2);
    }

    public Type Or(Type value1, Type value2)
    {
        return classMaker.Or(value1, value2);
    }

    public Type Inv(Type value)
    {
        return classMaker.Inv(value);
    }

    public Type SHL(Type value1, Type value2)
    {
        return classMaker.SHL(value1, value2);
    }

    public Type SHR(Type value1, Type value2)
    {
        return classMaker.SHR(value1, value2);
    }

    public Type USHR(Type value1, Type value2)
    {
        return classMaker.USHR(value1, value2);
    }

    public Type GT(Type value1, Type value2)
    {
        return classMaker.GT(value1, value2);
    }

    public Type GE(Type value1, Type value2)
    {
        return classMaker.GE(value1, value2);
    }

    public Type LE(Type value1, Type value2)
    {
        return classMaker.LE(value1, value2);
    }

    public Type LT(Type value1, Type value2)
    {
        return classMaker.LT(value1, value2);
    }

    public Type NE(Type value1, Type value2)
    {
        return classMaker.NE(value1, value2);
    }

    public Type Not(Type value)
    {
        return classMaker.Not(value);
    }

    public ArrayType NewArray(Type arrayType, Type size)
    {
        return classMaker.NewArray(arrayType, size);
    }

    public ArrayType NewArray(Type array, CallStack dimensions)
    {
        return classMaker.NewArray(array, dimensions);
    }

    public Type ArrayOf(Class javaClass)
    {
        return classMaker.ArrayOf(javaClass);
    }

    public Type ArrayOf(String typeName)
    {
        return classMaker.ArrayOf(typeName);
    }

    public ArrayType ArrayOf(Type type)
    {
        return classMaker.ArrayOf(type);
    }

    public Type GetAt(Type reference, Type index)
    {
        return classMaker.GetAt(reference, index);
    }

    public Type AssignAt(Type array, Type index, Type value)
    {
        return classMaker.AssignAt(array, index, value);
    }

    public Type SetAt(Type arrayRef, Type index, Type value)
    {
        return classMaker.SetAt(arrayRef, index, value);
    }

    public PrimitiveType Length(Type arrayRef)
    {
        return classMaker.Length(arrayRef);
    }

    public void Eval(Type value)
    {
        classMaker.Eval(value);
    }

    public Type Inc(String name) throws ClassMakerException
    {
        return classMaker.Inc(name);
    }

    public Type Inc(Type reference, String name) throws ClassMakerException
    {
        return classMaker.Inc(reference, name);
    }

    public Type Inc(String className, String name) throws ClassMakerException
    {
        return classMaker.Inc(className, name);
    }

    public Type Inc(MakerField lvalue) throws ClassMakerException
    {
        return classMaker.Inc(lvalue);
    }

    public Type Dec(String name) throws ClassMakerException
    {
        return classMaker.Dec(name);
    }

    public Type Dec(Type reference, String name) throws ClassMakerException
    {
        return classMaker.Dec(reference, name);
    }

    public Type Dec(String className, String name) throws ClassMakerException
    {
        return classMaker.Dec(className, name);
    }

    public Type Dec(MakerField lvalue) throws ClassMakerException
    {
        return classMaker.Dec(lvalue);
    }

    public Type PostInc(String name) throws ClassMakerException
    {
        return classMaker.PostInc(name);
    }

    public Type PostInc(Type reference, String name) throws ClassMakerException
    {
        return classMaker.PostInc(reference, name);
    }

    public Type PostInc(String className, String name) throws ClassMakerException
    {
        return classMaker.PostInc(className, name);
    }

    public Type PostInc(MakerField lvalue) throws ClassMakerException
    {
        return classMaker.PostInc(lvalue);
    }

    public Type PostDec(String name) throws ClassMakerException
    {
        return classMaker.PostDec(name);
    }

    public Type PostDec(Type reference, String name) throws ClassMakerException
    {
        return classMaker.PostDec(reference, name);
    }

    public Type PostDec(String className, String name) throws ClassMakerException
    {
        return classMaker.PostDec(className, name);
    }

    public Type PostDec(MakerField lvalue) throws ClassMakerException
    {
        return classMaker.PostDec(lvalue);
    }

    public Type IncAt(Type array, Type index) throws ClassMakerException
    {
        return classMaker.IncAt(array, index);
    }

    public Type DecAt(Type array, Type index) throws ClassMakerException
    {
        return classMaker.DecAt(array, index);
    }

    public Type PostIncAt(Type array, Type index) throws ClassMakerException
    {
        return classMaker.PostDecAt(array, index);
    }

    public Type PostDecAt(Type array, Type index) throws ClassMakerException
    {
        return classMaker.PostDecAt(array, index);
    }

    public AndOrExpression AndThen(AndOrExpression andOr, Type cond)
    {
        return classMaker.AndThen(andOr, cond);
    }

    public AndOrExpression AndThen(Type cond)
    {
        return classMaker.AndThen(cond);
    }

    public AndOrExpression OrElse(AndOrExpression andOr, Type cond)
    {
        return classMaker.OrElse(andOr, cond);
    }

    public AndOrExpression OrElse(Type cond)
    {
        return classMaker.OrElse(cond);
    }
}
