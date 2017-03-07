package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;

public interface BnfTree <T> extends AstExpression
{
    // BnfMakerVisitor methods
    public Type resolveDeclaration(BnfMakerVisitor visitor);
    
    public Type resolveLookahead(BnfMakerVisitor visitor, int howFar);

    public Type resolveSequence(BnfMakerVisitor visitor, int variable);

    public Type resolveType(BnfMakerVisitor visitor);

    // BnfMergeVisitor methods
    public BnfTree resolveMerge(BnfMergeVisitor visitor);
    
    public BnfTree resolveAlternatives(BnfMergeVisitor visitor);

    public boolean matches(BnfTree other);

    // BnfFirstVisitor methods
    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet);
    
    public boolean isEmpty();
    
    public boolean isMacro();
    
    // General methods
    public BnfTreeAlternative toAlternative();
    
    public BnfTreeSequence toSequence();
    
    public BnfTreeRule [] toRuleArray();
    
    public BnfTree [] toAltArray();
    
    public BnfTree [] toSeqArray();
    
    public BnfTree getHead();
    
    public BnfTree getTail();
    
    public String getName();
    
    public String toRuleString();
}
