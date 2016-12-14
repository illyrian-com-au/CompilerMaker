package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.ParserException;

public class BnfTreeName extends BnfTreeBase <Type>
{
    private final String name;
    
    public BnfTreeName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public boolean matches(BnfTree other)
    {
        if (other instanceof BnfTreeName)
        {
            BnfTreeName otherName = (BnfTreeName)other;
            return name.equals(otherName.name);
        }
        return false;
    }
    
    public boolean isVoidType()
    {
        return false;
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
        return name;
    }
}
