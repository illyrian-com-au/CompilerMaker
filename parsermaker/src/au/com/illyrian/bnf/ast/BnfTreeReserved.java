package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class BnfTreeReserved extends BnfTreeName
{
    public BnfTreeReserved(String name, int sourceLine) {
        super(name, sourceLine);
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

    public Value resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }

   public String toString() {
        return getName();
    }
}
