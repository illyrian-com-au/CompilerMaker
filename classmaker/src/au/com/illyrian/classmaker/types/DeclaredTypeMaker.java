package au.com.illyrian.classmaker.types;

import au.com.illyrian.classmaker.ClassMaker;

public class DeclaredTypeMaker extends DeclaredType
{
    final ClassMaker maker;
    
    public DeclaredTypeMaker(ClassMaker maker)
    {
        super(null);
        this.maker = maker;
    }
    
    public ClassMaker getMaker()
    {
        return maker;
    }
  
    public String getName()
    {
        return maker.getFullyQualifiedClassName();
    }
    
    public Type getType()
    {
        return maker.getClassType();
    }
    
    public ClassType getClassType()
    {
        return maker.getClassType();
    }

    public ArrayType getArrayType()
    {
        return null;
    }

    public String getSignature()
    {
        return maker.getClassSignature();
    }
    
    public short getSlotSize()
    {
        return (short)(ClassMaker.DOUBLE_TYPE.equals(getType()) || ClassMaker.LONG_TYPE.equals(getType()) ? 2 : 1);
    }

    public Class defineClass()
    {
        return maker.defineClass();
    }

    public String toString()
    {
        return "DeclaredTypeMaker(" + getName() + ")";
    }
}
