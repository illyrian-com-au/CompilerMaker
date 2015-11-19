package au.com.illyrian.compiler.ast;

public interface AstParser
{
    public void resolveRule(AstParserVisitor visitor);

    public AstParser resolveMerge(AstMergeVisitor visitor);
    
    public AstParser resolveAlternatives(AstMergeVisitor visitor);

    public boolean matches(AstParser other);
    
    public AstParser [] toAltArray();
    
    public AstParser [] toSeqArray();
    
    public AstParser getHead();
    
    public AstParser getTail();

}
