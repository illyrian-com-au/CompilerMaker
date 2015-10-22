package au.com.illyrian.compiler.ast;

public class AstParserSequence extends AstParserBinary
{
    public AstParserSequence(AstParser left, AstParser right)
    {
        super(left, right);
    }

    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }
}
