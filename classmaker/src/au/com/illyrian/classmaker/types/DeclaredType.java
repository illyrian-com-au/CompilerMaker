package au.com.illyrian.classmaker.types;


public class DeclaredType
{
    private final Type type;
    
    public DeclaredType(Type type)
    {
        this.type = type;
    }
    
    /**
     * Convert this <code>Type</code> to a <code>DeclaredType</code>.
     * @return a <code>DeclaredType</code> if appropriate; otherwise null
     */
    public DeclaredType toTypeDec()
    {
        return this;
    }

    public Type getType()
    {
        return type;
    }

    public ClassType getClassType()
    {
        return type.toClass();
    }

    public ArrayType getArrayType()
    {
        return type.toArray();
    }

    public String getName()
    {
        return getType().getName();
    }

    public String toString()
    {
        return "DeclaredType(" + getType().getName() + ")";
    }
}
