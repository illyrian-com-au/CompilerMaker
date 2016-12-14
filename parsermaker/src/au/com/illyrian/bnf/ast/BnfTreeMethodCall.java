package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.ParserException;

public class BnfTreeMethodCall extends BnfTreeBase <Type>
{
    private final AstExpression name;
    private final AstExpression actuals;
    
    public BnfTreeMethodCall(AstExpression name, AstExpression actuals)
    {
        this.name = name;
        this.actuals = actuals;
    }

    public String getName()
    {
        return name.toString();
    }

    public AstExpression getActuals()
    {
        return actuals;
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveDeclaration(this);
    }
    
    public String toString() {
        return getName() + "(" + (actuals == null ? "" : actuals) + ")";
    }
}
