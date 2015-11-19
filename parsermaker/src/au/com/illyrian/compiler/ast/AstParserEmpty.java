package au.com.illyrian.compiler.ast;

public class AstParserEmpty extends AstParserBase
{
    public AstParserEmpty() {
    }
    
    @Override
    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }
    
    public String toString() {
        return ".";
    }
}
