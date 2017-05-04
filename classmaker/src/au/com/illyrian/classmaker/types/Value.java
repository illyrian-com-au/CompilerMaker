package au.com.illyrian.classmaker.types;

public class Value
{
    private final Type type;
    
    public Value(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }
    
    public boolean isPrimitive() {
        return type.toPrimitive() != null;
    }
    
    public PrimitiveType toPrimitive() {
        return type.toPrimitive();
    }
    
    public boolean isClass() {
        return type.toClass() != null;
    }
    
    public ClassType toClass() {
        return type.toClass();
    }
    
    public boolean isArray() {
        return type.toArray() != null;
    }
    
    public ArrayType toArray() {
        return type.toArray();
    }    
}
