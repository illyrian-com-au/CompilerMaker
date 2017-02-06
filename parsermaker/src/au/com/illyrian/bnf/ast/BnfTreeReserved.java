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
    
    public boolean isVoidType()
    {
        return false;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }
    
    public Type resolveDeclaration(BnfMakerVisitor visitor) 
    {
        return visitor.resolveDeclaration(this);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }

   public String toString() {
        return getName();
    }
}
