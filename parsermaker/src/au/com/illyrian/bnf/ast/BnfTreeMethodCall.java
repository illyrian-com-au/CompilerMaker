package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class BnfTreeMethodCall extends BnfTreeBase <Type>
{
    private final BnfTree name;
    private final AstExpression actuals;
    
    public BnfTreeMethodCall(BnfTree name, AstExpression actuals) {
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

    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public Type resolveSequence(BnfMakerVisitor visitor, int variable) 
    {
        return visitor.resolveSequence(this, variable);
    }

    public Value resolveType(BnfMakerVisitor visitor)
    {
        return visitor.resolveType(this);
    }
    
    public Value resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }
    
    public int getLineNumber() {
        return name.getLineNumber();
    }
    
    public String toString() {
        return getName() + "(" + (actuals == null ? "" : actuals) + ")";
    }
}
