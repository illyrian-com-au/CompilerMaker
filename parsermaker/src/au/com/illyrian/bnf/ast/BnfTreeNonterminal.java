package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.types.Type;
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

    public Type resolveDeclaration(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveDeclaration(this);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveLookahead(this);
    }

    public Type resolveType(AstExpressionVisitor visitor)
    {
        BnfMakerVisitor mVisitor = (BnfMakerVisitor)visitor;
        return mVisitor.resolveType(this);
    }

    public String toString() {
        return getName() + "()";
    }
}
