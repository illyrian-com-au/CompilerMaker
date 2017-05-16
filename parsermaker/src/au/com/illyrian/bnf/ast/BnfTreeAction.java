package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;


public class BnfTreeAction extends BnfTreeBase
{
    private final AstExpression expr;
    
    public BnfTreeAction(AstExpression expr, int lineNumber) {
        super(lineNumber);
        this.expr = expr;
    }
    
    public AstExpression getExpression()
    {
        return expr;
    }

    @Override
    public Type resolveSequence(BnfMakerVisitor visitor, int variable) {
        return visitor.resolveSequence(this, variable);
    }

    @Override
    public Value resolveType(BnfMakerVisitor visitor)
    {
        return visitor.resolveType(this);
    }
    
    public String toRuleString() {
        return "{}";
    }
    
    public String toString() {
        return "{ " + expr.toString() + " }";
    }
}
