package au.com.illyrian.jesub.ast;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;

public interface AstClassAction {

	public void Package(AstExpression packageName);

	public void Import(AstExpression className);

	public int Modifier(String modifierName);

	public int Modifier(int modifiers, String modifierName);

	public void ClassName(int modifiers, TerminalName className);

	public void Extends(AstExpression className);

	public void Implements(AstExpression className);

}