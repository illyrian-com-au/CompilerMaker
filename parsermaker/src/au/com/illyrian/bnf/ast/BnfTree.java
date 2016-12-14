package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.ParserException;

public interface BnfTree <T> extends AstExpression
{
    // FIXME - remove checked exceptions
    public T resolveDeclaration(BnfMakerVisitor visitor) throws ParserException;
    
    public T resolveLookahead(BnfMakerVisitor visitor) throws ParserException;

    public BnfTree resolveMerge(BnfMergeVisitor visitor);
    
    public BnfTree resolveAlternatives(BnfMergeVisitor visitor);

    public boolean matches(BnfTree other);

    public boolean isEmpty();
    
    public boolean isVoidType();
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException;
    
    public BnfTreeRule [] toRuleArray();
    
    public BnfTree [] toAltArray();
    
    public BnfTree [] toSeqArray();
    
    public BnfTree getHead();
    
    public BnfTree getTail();
    
    public String getName();
    
    public String toRuleString();
}
