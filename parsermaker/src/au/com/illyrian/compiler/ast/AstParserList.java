package au.com.illyrian.compiler.ast;

public class AstParserList extends AstParserBinary
{
    
    public AstParserList(AstParser left, AstParser right)
    {
        super(left, right);
    }

    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }

}
