package au.com.illyrian.bnf.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfFirstVisitor
{
    public static final String EMPTY = "<EMPTY>";
    
    Map<String, BnfFirstSet> firstSets;
    Map<String, BnfTreeRule> ruleSet;
    
    public BnfFirstVisitor() {
        firstSets = createMap();
        ruleSet = new HashMap<String, BnfTreeRule>();
    }
    
    Map<String, BnfFirstSet> createMap() {
        return new HashMap<String, BnfFirstSet>();
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

    public boolean resolveFirst(BnfTreeParser bnfTreeParser, Set<String> firstSet) throws ParserException
    {
        return bnfTreeParser.getRules().resolveFirst(this, firstSet);
    }
    
    public boolean resolveFirst(BnfTreeList list, Set<String> firstSet) throws ParserException
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

    public boolean resolveFirst(BnfTreeRule rule, Set<String> firstSet) throws ParserException
    {
        boolean hasEmpty = false;
        String name = rule.getTarget().toString();
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
                    set.add(EMPTY);
                }
                firstSets.put(name, set);
                rule.setFirstSet(set);
            }
        } else {
            hasEmpty = set.contains(EMPTY);
        }
        if (firstSet != null) {
            mergeSets(firstSet, set);
        }
        return hasEmpty;
    }
    
    void mergeSets(Set<String> firstSet, Set<String> set) throws ParserException {
        for (String name: set) {
            add(firstSet, name);
        }
    }

    void add(Set<String> firstSet, String name) throws ParserException {
        if (firstSet.contains(name)) {
            throw new ParserException("Ambiguous grammer on terminal: " + name);
        } else if (name != EMPTY) {
            firstSet.add(name);
        }
    }

    public boolean resolveFirst(BnfTreeAlternative alt, Set<String> firstSet) throws ParserException {
        boolean hasEmpty = false;
        BnfTree []alternatives = alt.toAltArray();
        for (BnfTree<?> clause : alternatives) {
            // hasEmpty is true if any of the alternatives includes EMPTY.
            hasEmpty |= clause.resolveFirst(this, firstSet);
        }
        return hasEmpty;
    }

    public boolean resolveFirst(BnfTreeSequence seq, Set<String> firstSet) throws ParserException
    {
        boolean hasEmpty = seq.getHead().resolveFirst(this, firstSet);
        if (hasEmpty) {
            hasEmpty = seq.getTail().resolveFirst(this, firstSet);
        }
        return hasEmpty;
    }

    public boolean resolveFirst(BnfTreeName name, Set<String> firstSet) throws ParserException
    {
        boolean hasEmpty = false;
        BnfTreeRule rule = ruleSet.get(name.getName());
        if (rule != null) {
            hasEmpty = rule.resolveFirst(this, firstSet);
        } else {
            add(firstSet, name.getName());
        }
        return hasEmpty;
    }

    public boolean resolveFirst(BnfTreeEmpty empty, Set<String> firstSet)
    {
        return true;
    }

    public boolean resolveFirst(BnfTreeMethodCall call, Set<String> firstSet) throws ParserException
    {
        // Do not add method calls to the first set as these are for special processing.
        return false;
    }

    public boolean resolveFirst(BnfTreeReserved reserved, Set<String> firstSet) throws ParserException
    {
        add(firstSet, reserved.getName());
        return  false;
    }

    public boolean resolveFirst(BnfTreeString string, Set<String> firstSet) throws ParserException
    {
        add(firstSet, string.toString());
        return false;
    }

    public boolean resolveFirst(BnfTreeMacroCall macro, Set<String> firstSet) throws ParserException
    {
        // Only add the LOOKAHEAD macro to the first set.
        if ("LOOKAHEAD".equals(macro.getName())) {
            add(firstSet, macro.toString());
        }
        return false;
    }
}
