package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;

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
        
    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }
    
    public Type resolveSequence(BnfMakerVisitor visitor, int variable) 
    {
        return visitor.resolveSequence(this, variable);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }

   public String toString() {
        return getName();
    }
}
