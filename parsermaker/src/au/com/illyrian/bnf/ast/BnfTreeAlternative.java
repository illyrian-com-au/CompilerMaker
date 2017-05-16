package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class BnfTreeAlternative extends BnfTreeBinary <Type>
{
    public static final BnfTree [] NULL_ARRAY = new BnfTree [] {null};
    BnfTree [] altArray;
    
    public BnfTreeAlternative(BnfTree left, BnfTree right) {
        super(left, right);
        altArray = concat(left.toAltArray(), right.toAltArray());
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }

    public Type resolveSequence(BnfMakerVisitor visitor, int variable) {
        return visitor.resolveSequence(this, variable);
    }

    public BnfTreeAlternative replace(BnfTree left, BnfTree right) {
        if (left == this.getHead() && right == this.getTail())
            return this;
        return new BnfTreeAlternative(left, right);
    }

    public BnfTreeAlternative toAlternative() {
        return this;
    }
    
    public BnfTree [] toAltArray() {
        return altArray;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor)
    {
        return visitor.resolveDeclaration(this);
    }

    public Value resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
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
