package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;

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
    
    public Type resolveSequence(BnfMakerVisitor visitor, int variable) 
    {
        return visitor.resolveSequence(this, variable);
    }

    public Type resolveType(BnfMakerVisitor visitor)
    {
        return visitor.resolveType(this);
    }

    public String toString() {
        return getName() + "()";
    }
}
