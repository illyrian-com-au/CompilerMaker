package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;

public class BnfTreeLookahead extends BnfTreeBase<Type>
{
    private final BnfTree pattern;

    public BnfTreeLookahead(BnfTree<Type> pattern)
    {
        this.pattern = pattern;
    }

    public String getName()
    {
        return toString();
    }

    public BnfTree<Type> getPattern()
    {
        return pattern;
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) 
    {
        return visitor.resolveMerge(this);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }

    public boolean isMacro()
    {
        return true;
    }

    public BnfTreeLookahead replace(BnfTree pattern) {
        if (pattern != this.pattern) {
            return new BnfTreeLookahead(pattern);
        }
        return this;
    }

    public String toString()
    {
        return "LOOKAHEAD(" + pattern + ")";
    }
}
