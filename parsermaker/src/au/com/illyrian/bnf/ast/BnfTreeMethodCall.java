package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfTreeMethodCall extends BnfTreeBase
{
    private final BnfTree name;
    private final BnfTree actuals;
    
    public BnfTreeMethodCall(BnfTree name, BnfTree actuals)
    {
        this.name = name;
        this.actuals = actuals;
    }

    public String getName()
    {
        return name.getName();
    }

    public BnfTree getActuals()
    {
        return actuals;
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return getName() + "(" + (actuals == null ? "" : actuals) + ")";
    }
}
