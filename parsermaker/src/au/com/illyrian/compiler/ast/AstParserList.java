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

    public AstParserList replace(AstParser left, AstParser right) {
        if (left == this.getLeft() && right == this.getRight())
            return this;
        return new AstParserList(left, right);
    }

    public String toString() {
        return getLeft() + "\n" + getRight();
    }
}
