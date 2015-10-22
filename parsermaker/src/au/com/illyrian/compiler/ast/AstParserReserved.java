package au.com.illyrian.compiler.ast;

public class AstParserReserved extends AstParserName
{
    public AstParserReserved(String name)
    {
        super(name);
    }

    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }
}
