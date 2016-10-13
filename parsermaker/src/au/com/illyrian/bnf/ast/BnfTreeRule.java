package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfTreeRule extends BnfTreeBase
{
    private final BnfTree target;
    private final BnfTree body;
    
    public BnfTreeRule(BnfTree target, BnfTree body)
    {
        this.target = target;
        this.body = body;
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) 
    {
        return visitor.resolveMerge(this);
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }
    
    public BnfTreeRule [] toRuleArray() {
        return new BnfTreeRule [] { this };
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
