package au.com.illyrian.classmaker.types;


public class DeclaredType
{
    protected Type type;
    
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
        return (type != null) ? type.toClass() : null;
    }

    public ArrayType getArrayType()
    {
        return (type != null) ? type.toArray() : null;
    }

    public String getName()
    {
        return (type != null) ? type.getName() : "null";
    }

    public String toString()
    {
        return "DeclaredType(" + getName() + ")";
    }
}
