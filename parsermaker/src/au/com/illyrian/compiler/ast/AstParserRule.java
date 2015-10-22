package au.com.illyrian.compiler.ast;

public class AstParserRule extends AstParserBase
{
    private final AstParser target;
    private final AstParser body;
    
    public AstParserRule(AstParser target, AstParser body)
    {
        this.target = target;
        this.body = body;
    }

    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }
    
    
}
