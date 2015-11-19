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

    public AstParser resolveAlternatives(AstMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }

    public AstParser getLeft()
    {
        return left;
    }

    public AstParser getRight()
    {
        return right;
    }
    
    public AstParser getHead() {
        return left;
    }
    
    public AstParser getTail() {
        return right;
    }

    public AstParserBinary replace(AstParser head, AstParser tail) {
        return this;
    }

    public String toString() {
        return left + " " + right;
    }
}
