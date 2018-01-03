package au.com.illyrian.classmaker.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public interface TypeVisitor {
    void visit(Class<?> clazz);
    void visit(ParameterizedType parameterized);
    void visit(TypeVariable variable);
    void visit(GenericArrayType genericArray);
    void visit(WildcardType wildcard);
}