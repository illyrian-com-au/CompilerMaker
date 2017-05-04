package au.com.illyrian.classmaker.types;

import au.com.illyrian.classmaker.ClassMaker;


public class DeclaredType
{
    protected Type type;
    
    public DeclaredType(Type type)
    {
        this.type = type;
    }
    
    public Type getType()
    {
        if (type == null)
            throw new NullPointerException("Type has not been set.");
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

    public String getSignature()
    {
        return (type != null) ? type.getSignature() : "null";
    }
    
    public short getSlotSize()
    {
        return (short)(PrimitiveType.DOUBLE_TYPE.equals(getType()) || PrimitiveType.LONG_TYPE.equals(getType()) ? 2 : 1);
    }
    
    public Class defineClass()
    {
        return null;
    }

    public String toString()
    {
        return "DeclaredType(" + getName() + ")";
    }
}
