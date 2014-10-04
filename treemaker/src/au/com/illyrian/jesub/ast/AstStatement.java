package au.com.illyrian.jesub.ast;

public interface AstStatement {
	
	public AstStructureList toList();
	
	public AstStatementIf toIf();
	
	public void resolveStatement(AstStructureVisitor visitor);
    
}
