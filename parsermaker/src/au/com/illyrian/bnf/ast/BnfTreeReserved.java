package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;
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
    
    public boolean isVoidType()
    {
        return true;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }
    
    public Type resolveDeclaration(BnfMakerVisitor visitor) throws ParserException 
    {
        return visitor.resolveDeclaration(this);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveLookahead(this);
    }

   public String toString() {
        return getName();
    }
}
