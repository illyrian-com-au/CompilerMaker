package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.ParserException;

public class BnfTreeRule extends BnfTreeBase <Type>
{
    private final BnfTree target;
    private final BnfTree body;
    private BnfFirstSet firstSet;
    
    public BnfTreeRule(BnfTree target, BnfTree body)
    {
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

    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }
    
    public BnfTreeRule [] toRuleArray() {
        return new BnfTreeRule [] { this };
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveDeclaration(this);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveLookahead(this);
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
