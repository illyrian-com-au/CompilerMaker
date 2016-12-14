package au.com.illyrian.bnf.ast;

import java.util.Map;
import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.ParserException;

public class BnfTreeParser extends BnfTreeBase <Type>
{
    private final BnfTree rules;
    private Map<String, BnfTreeRule> ruleSet;
    
    public BnfTreeParser(BnfTree rules) {
        this.rules = rules;
    }
    
    public Map<String, BnfTreeRule> getRuleSet()
    {
        return ruleSet;
    }

    public void setRuleSet(Map<String, BnfTreeRule> ruleSet)
    {
        this.ruleSet = ruleSet;
    }

    public BnfTree getRules()
    {
        return rules;
    }

    public BnfTreeRule [] toRuleArray() {
        return getRules().toRuleArray();
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) 
    {
        return visitor.resolveMerge(this);
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor) throws ParserException 
    {
        return visitor.resolveDeclaration(this);
    }

    public String toString() {
        return rules.toString();
    }
}
