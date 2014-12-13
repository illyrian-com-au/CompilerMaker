package au.com.illyrian.jesub.ast;

public class AstStatementBase extends AstStructureBase implements AstStatement 
{
    public void resolveStatement(AstStructureVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolve Statement: " + getClass().getSimpleName());
    }

}
