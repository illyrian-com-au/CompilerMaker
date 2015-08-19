package au.com.illyrian.classmaker.types;

public class DeclaredTypeForward extends DeclaredType
{
    final String name;
    
    public DeclaredTypeForward(String name)
    {
        super(null);
        this.name = name;
    }
  
    public String getName()
    {
        return name;
    }
    
    public void setType(Type type)
    {
        this.type = type;
    }
    
    public String toString()
    {
        return "DeclaredTypeForward(" + getName() + ")";
    }
}
