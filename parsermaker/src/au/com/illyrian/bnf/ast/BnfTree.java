package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;

public interface BnfTree <T> extends AstExpression
{
    public T resolveDeclaration(BnfMakerVisitor visitor);
    
    public T resolveLookahead(BnfMakerVisitor visitor, int howFar);

    public Type resolveType(BnfMakerVisitor visitor);

    public BnfTree resolveMerge(BnfMergeVisitor visitor);
    
    public BnfTree resolveAlternatives(BnfMergeVisitor visitor);

    public boolean matches(BnfTree other);

    public boolean isEmpty();
    
    public boolean isVoidType();
    
    public boolean isMacro();
    
    public BnfTreeAlternative toAlternative();
    
    public BnfTreeSequence toSequence();
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet);
    
    public BnfTreeRule [] toRuleArray();
    
    public BnfTree [] toAltArray();
    
    public BnfTree [] toSeqArray();
    
    public BnfTree getHead();
    
    public BnfTree getTail();
    
    public String getName();
    
    public String toRuleString();
}
