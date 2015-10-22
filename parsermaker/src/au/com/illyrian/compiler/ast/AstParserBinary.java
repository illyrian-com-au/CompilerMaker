package au.com.illyrian.compiler.ast;

public abstract class AstParserBinary extends AstParserBase
{
    private final AstParser left;
    private final AstParser right;
    
    public AstParserBinary(AstParser left, AstParser right)
    {
        this.left = left;
        this.right = right;
    }

    public AstParser getLeft()
    {
        return left;
    }

    public AstParser getRight()
    {
        return right;
    }
    
    
}
