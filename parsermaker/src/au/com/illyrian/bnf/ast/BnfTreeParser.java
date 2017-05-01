package au.com.illyrian.bnf.ast;

import java.util.HashMap;
import java.util.Map;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;

public class BnfTreeParser extends BnfTreeBase <Type>
{
    private final BnfTree rules;
    private final Map<String, BnfTreeRule> ruleSet;
    
    public BnfTreeParser(BnfTree rules, int sourceLine) {
        super(sourceLine);
        this.rules = rules;
        ruleSet = createRuleSet(rules.toRuleArray());
    }
    
    public Map<String, BnfTreeRule> getRuleSet()
    {
        return ruleSet;
    }

    Map<String, BnfTreeRule> createRuleSet(BnfTreeRule [] ruleList)
    {
        Map<String, BnfTreeRule> set = new HashMap<String, BnfTreeRule>();
        for (BnfTreeRule rule : ruleList) {
            set.put(rule.getName(), rule);
        }
        return set;
    }

    public BnfTree getRules()
    {
        return rules;
    }

    public BnfTreeRule [] toRuleArray() {
        return getRules().toRuleArray();
    }

    public BnfTreeParser replace(BnfTree rules) {
        if (rules == this.rules)
            return this;
        return new BnfTreeParser(rules, getLineNumber());
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public BnfTreeParser resolveMerge(BnfMergeVisitor visitor) 
    {
        return visitor.resolveMerge(this);
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor) 
    {
        return visitor.resolveDeclaration(this);
    }

    public String toString() {
        return rules.toString();
    }
}
