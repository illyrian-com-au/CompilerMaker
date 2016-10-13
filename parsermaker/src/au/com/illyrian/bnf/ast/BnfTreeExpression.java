package au.com.illyrian.bnf.ast;

import au.com.illyrian.classmaker.ast.AstExpression;


public class BnfTreeExpression extends BnfTreeBase
{
    AstExpression expr;
    
    public BnfTreeExpression(AstExpression expr) {
        this.expr = expr;
    }
    
    public String toRuleString() {
        return "{}";
    }
    
    public String toString() {
        return "{ " + expr.toString() + " }";
    }
}
