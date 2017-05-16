package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class BnfTreeName extends BnfTreeBase <Type>
{
    private final String name;
    
    public BnfTreeName(String name, int sourceLine) {
        super(sourceLine);
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
    
    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor)
    {
        return visitor.resolveDeclaration(this);
    }

    public Value resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }

    public Type resolveSequence(BnfMakerVisitor visitor, int variable) 
    {
        return visitor.resolveSequence(this, variable);
    }

    public Value resolveType(BnfMakerVisitor visitor)
    {
        return visitor.resolveType(this);
    }

    public String toString() {
        return name;
    }
}
