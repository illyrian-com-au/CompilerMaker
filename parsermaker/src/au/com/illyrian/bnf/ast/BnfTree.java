package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.ParserException;

public interface BnfTree extends AstExpression
{
    public BnfTree resolveMerge(BnfMergeVisitor visitor);
    
    public BnfTree resolveAlternatives(BnfMergeVisitor visitor);

    public boolean matches(BnfTree other);
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException;
    
    public BnfTreeRule [] toRuleArray();
    
    public BnfTree [] toAltArray();
    
    public BnfTree [] toSeqArray();
    
    public BnfTree getHead();
    
    public BnfTree getTail();
    
    public String getName();
    
    public String toRuleString();
}
