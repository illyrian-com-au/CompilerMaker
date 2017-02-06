package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
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

    public Type resolveType(BnfMakerVisitor visitor)
    {
        return visitor.resolveType(this);
    }

    public String toString() {
        return getName() + "()";
    }
}
