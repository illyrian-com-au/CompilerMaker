package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfTreeReserved extends BnfTreeName
{
    public BnfTreeReserved(String name)
    {
        super(name);
    }

    public boolean matches(BnfTree other)
    {
        if (other instanceof BnfTreeReserved)
        {
            BnfTreeReserved otherReserved = (BnfTreeReserved)other;
            return getName().matches(otherReserved.getName());
        }
        return false;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return getName();
    }
}
