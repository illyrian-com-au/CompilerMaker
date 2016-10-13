package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfTreeAlternative extends BnfTreeBinary
{
    public static final BnfTree [] NULL_ARRAY = new BnfTree [] {null};
    BnfTree [] altArray;
    
    public BnfTreeAlternative(BnfTree left, BnfTree right)
    {
        super(left, right);
        BnfTree [] leftArr = (left == null) ? NULL_ARRAY : left.toAltArray();
        BnfTree [] rightArr = (right == null) ? NULL_ARRAY : right.toAltArray();
        altArray = concat(leftArr, rightArr);
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }

    public BnfTreeAlternative replace(BnfTree left, BnfTree right) {
        if (left == this.getHead() && right == this.getTail())
            return this;
        return new BnfTreeAlternative(left, right);
    }

    public BnfTree [] toAltArray() {
        return altArray;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toRuleString() {
        StringBuffer buf = new StringBuffer();
        buf.append("( ").append(altArray[0].toRuleString());
        for (int i=1; i<altArray.length; i++) {
            buf.append(" | ").append(altArray[i].toRuleString());
        }
        buf.append(" )");
        return buf.toString();
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
