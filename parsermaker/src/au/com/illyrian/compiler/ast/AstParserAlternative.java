package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserAlternative extends AstParserBinary
{
    public static final AstParser [] NULL_ARRAY = new AstParser [] {null};
    AstParser [] altArray;
    
    public AstParserAlternative(AstParser left, AstParser right)
    {
        super(left, right);
        AstParser [] leftArr = (left == null) ? NULL_ARRAY : left.toAltArray();
        AstParser [] rightArr = (right == null) ? NULL_ARRAY : right.toAltArray();
        altArray = concat(leftArr, rightArr);
    }

    public AstParser resolveMerge(AstMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }

    public AstParserAlternative replace(AstParser head, AstParser tail) {
        if (head == this.getHead() && tail == this.getTail())
            return this;
        return new AstParserAlternative(head, tail);
    }

    public AstParser [] toAltArray() {
        return altArray;
    }
    
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("( ").append(altArray[0]);
        for (int i=1; i<altArray.length; i++) {
            buf.append(" | ").append(altArray[i]);
        }
        buf.append(" )");
        return buf.toString();
    }
}
