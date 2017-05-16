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
    
    public String getName() {
        return type.getName();
    }
    
    public PrimitiveType toPrimitive() {
        return type.toPrimitive();
    }
    
    public ClassType toClass() {
        return type.toClass();
    }
    
    public ArrayType toArray() {
        return type.toArray();
    }
    
    public String toString() {
        return type.toString();
    }
}
