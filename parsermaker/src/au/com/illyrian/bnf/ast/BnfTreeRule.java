package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class BnfTreeRule extends BnfTreeBase <Type>
{
    private final BnfTree target;
    private final BnfTree body;
    private BnfFirstSet firstSet;
    
    public BnfTreeRule(BnfTree target, BnfTree body) {
        this.target = target;
        this.body = body;
    }

    public BnfFirstSet getFirstSet()
    {
        return firstSet;
    }

    public void setFirstSet(BnfFirstSet firstSet)
    {
        this.firstSet = firstSet;
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) 
    {
        return visitor.resolveMerge(this);
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }
    
    public BnfTreeRule [] toRuleArray() {
        return new BnfTreeRule [] { this };
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor)
    {
        return visitor.resolveDeclaration(this);
    }

    public Value resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }

    public BnfTree getTarget()
    {
        return target;
    }

    public BnfTree getBody()
    {
        return body;
    }
    
    public String getName() {
        return getTarget().getName();
    }
    
    public int getLineNumber() {
        return target.getLineNumber();
    }
    
    public BnfTreeRule replace(BnfTree target, BnfTree body) {
        if (target == this.target && body == this.body)
            return this;
        return new BnfTreeRule(target, body);
    }

    public String toRuleString() {
        return target + " ::= " + body.toRuleString() + " ;";
    }

    public String toString() {
        return target + " ::= " + body + " ;";
    }
}
