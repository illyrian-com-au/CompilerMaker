package au.com.illyrian.classmaker.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeVisitorDirector {
    private final TypeVisitor typeVisitor;
    
    public TypeVisitorDirector(TypeVisitor typeVisitor) {
        this.typeVisitor = typeVisitor;
    }
    
    public TypeVisitor getTypeVisitor() {
        return typeVisitor;
    }

    void visit(Type type) {
        if (type instanceof Class) {
            typeVisitor.visit((Class)type);
        } else if (type instanceof ParameterizedType) {
            typeVisitor.visit((ParameterizedType)type);
        } else if (type instanceof TypeVariable) {
            typeVisitor.visit((TypeVariable)type);
        } else if (type instanceof GenericArrayType) {
            typeVisitor.visit((GenericArrayType)type);
        } else if (type instanceof WildcardType) {
            typeVisitor.visit((WildcardType)type);
        }
    }
}