package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.ParserException;

public class BnfTreeEmpty extends BnfTreeBase <Type>
{
    public BnfTreeEmpty() {
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor) throws ParserException 
    {
        return visitor.resolveDeclaration(this);
    }

    public boolean isEmpty() {
        return true;
    }
    
    public String toString() {
        return ".";
    }
}
