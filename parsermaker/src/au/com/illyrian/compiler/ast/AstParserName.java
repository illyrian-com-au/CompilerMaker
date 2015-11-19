package au.com.illyrian.compiler.ast;

public class AstParserName extends AstParserBase
{
    private final String name;
    
    public AstParserName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }
    
    public boolean matches(AstParser other)
    {
        if (other instanceof AstParserName)
        {
            AstParserName otherName = (AstParserName)other;
            return name.equals(otherName.name);
        }
        return false;
    }
    
    public String toString() {
        return name;
    }
}
