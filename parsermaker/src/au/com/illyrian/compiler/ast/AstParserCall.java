package au.com.illyrian.compiler.ast;

public class AstParserCall extends AstParserBase
{
    private final AstParser name;
    private final AstParser actuals;
    
    public AstParserCall(AstParser name, AstParser actuals)
    {
        this.name = name;
        this.actuals = actuals;
    }

    public AstParser getName()
    {
        return name;
    }

    public AstParser getActuals()
    {
        return actuals;
    }

    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }
}
