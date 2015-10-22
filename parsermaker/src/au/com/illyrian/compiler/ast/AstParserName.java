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
}
