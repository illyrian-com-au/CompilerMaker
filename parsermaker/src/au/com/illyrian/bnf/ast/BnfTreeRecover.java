package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;

public class BnfTreeRecover extends BnfTreeBase<Type>
{
    private final BnfTree pattern;

    public BnfTreeRecover(BnfTree pattern)
    {
        this.pattern = pattern;
    }

    public String getName()
    {
        return "LOOKAHEAD";
    }

    public BnfTree getPattern()
    {
        return pattern;
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }

    public boolean isMacro()
    {
        return true;
    }

    public String toString()
    {
        return "RECOVER(" + pattern + ")";
    }
}