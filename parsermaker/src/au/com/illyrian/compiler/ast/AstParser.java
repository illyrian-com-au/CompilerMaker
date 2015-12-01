package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public interface AstParser
{
    public AstParser resolveMerge(AstMergeVisitor visitor);
    
    public AstParser resolveAlternatives(AstMergeVisitor visitor);

    public boolean matches(AstParser other);
    
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException;
    
    public AstParserRule [] toRuleArray();
    
    public AstParser [] toAltArray();
    
    public AstParser [] toSeqArray();
    
    public AstParser getHead();
    
    public AstParser getTail();

}
