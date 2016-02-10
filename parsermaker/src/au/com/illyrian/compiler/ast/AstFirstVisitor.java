package au.com.illyrian.compiler.ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstFirstVisitor
{
    public static final String EMPTY = "<EMPTY>";
    
    Map<String, FirstSet> firstSets;
    Map<String, AstParserRule>ruleSet;
    
    public AstFirstVisitor() {
        firstSets = createMap();
        ruleSet = new HashMap<String, AstParserRule>();
    }
    
    Map<String, FirstSet> createMap() {
        return new HashMap<String, FirstSet>();
    }
    
    FirstSet createSet(String name) {
        return new FirstSet(name);
    }

    public FirstSet getSet(String name) {
        return firstSets.get(name);
    }
    
    public void addRuleSet(AstParserRule rule)
    {
        String name = rule.getTarget().toString(); // FIXME - getName() ???
        ruleSet.put(name, rule);
    }

    public boolean resolveFirst(AstParserList list, Set<String> firstSet) throws ParserException
    {
        AstParserRule [] rules = list.toRuleArray();
        for (AstParserRule rule : rules) {
            addRuleSet(rule);
        }
        for (AstParserRule rule : rules) {
            resolveFirst(rule, null);
        }
        return false;
    }

    public boolean resolveFirst(AstParserRule rule, Set<String> firstSet) throws ParserException
    {
        boolean hasEmpty = false;
        String name = rule.getTarget().toString();
        FirstSet set = getSet(name);
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

    public boolean resolveFirst(AstParserAlternative alt, Set<String> firstSet) throws ParserException {
        boolean hasEmpty = false;
        AstParser []alternatives = alt.toAltArray();
        for (AstParser clause : alternatives) {
            // hasEmpty is true if any of the alternatives includes EMPTY.
            hasEmpty |= clause.resolveFirst(this, firstSet);
        }
        return hasEmpty;
    }

    public boolean resolveFirst(AstParserSequence seq, Set<String> firstSet) throws ParserException
    {
        boolean hasEmpty = seq.getHead().resolveFirst(this, firstSet);
        if (hasEmpty) {
            hasEmpty = seq.getTail().resolveFirst(this, firstSet);
        }
        return hasEmpty;
    }

    public boolean resolveFirst(AstParserName name, Set<String> firstSet) throws ParserException
    {
        boolean hasEmpty = false;
        AstParserRule rule = ruleSet.get(name.getName());
        if (rule != null) {
            hasEmpty = rule.resolveFirst(this, firstSet);
        } else {
            add(firstSet, name.getName());
        }
        return hasEmpty;
    }

    public boolean resolveFirst(AstParserEmpty empty, Set<String> firstSet)
    {
        return true;
    }

    public boolean resolveFirst(AstParserCall call, Set<String> firstSet) throws ParserException
    {
        // Do not add method calls to the first set as these are for special processing.
        if ("lookahead".equals(call.getName()) || "precedence".equals(call.getName())) {
            add(firstSet, call.toString());
        }
        return false;
    }

    public boolean resolveFirst(AstParserReserved reserved, Set<String> firstSet) throws ParserException
    {
        add(firstSet, reserved.getName());
        return  false;
    }

    public boolean resolveFirst(AstParserString string, Set<String> firstSet) throws ParserException
    {
        add(firstSet, string.toString());
        return false;
    }
    
}
