package au.com.illyrian.bnf.ast;

import java.util.HashMap;
import java.util.Map;

import au.com.illyrian.parser.ParserException;

public class BnfFirstVisitor
{
    public static final String EMPTY = "<EMPTY>";
    
    Map<String, BnfFirstSet> firstSets;
    Map<String, BnfTreeRule> ruleSet;
    
    public BnfFirstVisitor() {
        firstSets = new HashMap<String, BnfFirstSet>();
        ruleSet = new HashMap<String, BnfTreeRule>();
    }
    
    public Map<String, BnfFirstSet> getFirstSets()
    {
        return firstSets;
    }

    public Map<String, BnfTreeRule> getRuleSet()
    {
        return ruleSet;
    }

    BnfFirstSet createSet(String name) {
        return new BnfFirstSet(name);
    }

    public BnfFirstSet getSet(String name) {
        return firstSets.get(name);
    }
    
    public void addRuleSet(BnfTreeRule rule)
    {
        String name = rule.getTarget().getName();
        ruleSet.put(name, rule);
    }

    public boolean resolveFirst(BnfTreeParser bnfTreeParser, BnfFirstSet firstSet)
    {
        return bnfTreeParser.getRules().resolveFirst(this, firstSet);
    }
    
    public boolean resolveFirst(BnfTreeList list, BnfFirstSet firstSet)
    {
        BnfTreeRule [] rules = list.toRuleArray();
        for (BnfTreeRule rule : rules) {
            addRuleSet(rule);
        }
        for (BnfTreeRule rule : rules) {
            resolveFirst(rule, null);
        }
        return false;
    }

    public boolean resolveFirst(BnfTreeRule rule, BnfFirstSet firstSet) // BnfFirtSet
    {
        boolean hasEmpty = false;
        String name = rule.getTarget().getName();
        BnfFirstSet set = getSet(name);
        if (set == null) {
            if (firstSets.containsKey(name)) {
                // A name without a Set indicates that we are already processing it further up the stack.
                throw new ParserException("Grammer is left recursive on non-terminal: " + name);
            } else {
                // Add name without a Set to detect left recursion. If we see it again there is a problem.
                firstSets.put(name, null);
                set = createSet(name);
                hasEmpty = rule.getBody().resolveFirst(this, set);
                if (hasEmpty) {
                    set.add(EMPTY, null);
                }
                firstSets.put(name, set);
                rule.setFirstSet(set);
            }
        } else {
            hasEmpty = set.contains(EMPTY);
        }
        if (firstSet != null) {
            firstSet.merge(set);
        }
        return hasEmpty;
    }
    
    public boolean resolveFirst(BnfTreeAlternative alt, BnfFirstSet firstSet) {
        boolean hasEmpty = false;
        BnfTree []alternatives = alt.toAltArray();
        for (BnfTree<?> clause : alternatives) {
            // hasEmpty is true if any of the alternatives includes EMPTY.
            hasEmpty |= clause.resolveFirst(this, firstSet);
        }
        return hasEmpty;
    }

    public boolean resolveFirst(BnfTreeSequence seq, BnfFirstSet firstSet)
    {
        boolean hasEmpty = seq.getHead().resolveFirst(this, firstSet);
        if (hasEmpty) {
            hasEmpty = seq.getTail().resolveFirst(this, firstSet);
        }
        return hasEmpty;
    }

    public boolean resolveFirst(BnfTreeName name, BnfFirstSet firstSet)
    {
        boolean hasEmpty = false;
        BnfTreeRule rule = ruleSet.get(name.getName());
        if (rule != null) {
            hasEmpty = rule.resolveFirst(this, firstSet);
        } else {
            firstSet.addOnce(name.getName(), name);
        }
        return hasEmpty;
    }

    public boolean resolveFirst(BnfTreeEmpty empty, BnfFirstSet firstSet)
    {
        return true;
    }

    public boolean resolveFirst(BnfTreeMethodCall call, BnfFirstSet firstSet)
    {
        // Do not add method calls to the first set as these are for special processing.
        return false;
    }

    public boolean resolveFirst(BnfTreeReserved reserved, BnfFirstSet firstSet)
    {
        firstSet.addOnce(reserved.getName(), reserved);
        return  false;
    }

    public boolean resolveFirst(BnfTreeLookahead macro, BnfFirstSet firstSet)
    {
        firstSet.addOnce(macro.toString(), macro);
        return false;
    }

    public boolean resolveFirst(BnfTreeRecover bnfTreeRecover, BnfFirstSet firstSet)
    {
        return false;
    }
}
