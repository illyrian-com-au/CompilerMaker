package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfTreeNonterminal extends BnfTreeName
{
    public BnfTreeNonterminal(String name)
    {
        super(name);
    }

    public boolean matches(BnfTree other)
    {
        if (other instanceof BnfTreeNonterminal)
        {
            BnfTreeNonterminal otherReserved = (BnfTreeNonterminal)other;
            return getName().matches(otherReserved.getName());
        }
        return false;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return "<" + getName() + ">";
    }
}
