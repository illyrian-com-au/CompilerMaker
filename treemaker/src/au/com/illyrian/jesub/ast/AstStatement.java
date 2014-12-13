package au.com.illyrian.jesub.ast;

public interface AstStatement {
	
	public void resolveStatement(AstStructureVisitor visitor);
    
}
