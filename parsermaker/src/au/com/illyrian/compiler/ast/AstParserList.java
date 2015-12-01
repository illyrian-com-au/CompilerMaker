package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserList extends AstParserBinary
{
    
    public AstParserList(AstParser left, AstParser right)
    {
        super(left, right);
    }

    public AstParserList replace(AstParser left, AstParser right) {
        if (left == this.getLeft() && right == this.getRight())
            return this;
        return new AstParserList(left, right);
    }

    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public AstParserRule [] toRuleArray() {
        AstParserRule [] head = (AstParserRule [])getHead().toRuleArray();
        AstParserRule [] tail = (AstParserRule [])getTail().toRuleArray();
        return (AstParserRule [])concat(head, tail);
    }
    
    protected AstParserRule [] concat(AstParserRule [] left, AstParserRule [] right)
    {
        AstParserRule [] list = new AstParserRule [left.length + right.length];
        System.arraycopy(left, 0, list, 0, left.length);
        System.arraycopy(right, 0, list, left.length, right.length);
        return list;
    }



    public String toString() {
        return getLeft() + "\n" + getRight();
    }
}
