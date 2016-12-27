package au.com.illyrian.bnf.ast;

import au.com.illyrian.classmaker.ast.AstExpression;


public class BnfTreeAction extends BnfTreeBase
{
    AstExpression expr;
    
    public BnfTreeAction(AstExpression expr) {
        this.expr = expr;
    }
    
    public String toRuleString() {
        return "{}";
    }
    
    public String toString() {
        return "{ " + expr.toString() + " }";
    }
}
