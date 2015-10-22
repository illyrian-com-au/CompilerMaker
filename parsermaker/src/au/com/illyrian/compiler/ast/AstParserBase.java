package au.com.illyrian.compiler.ast;

public abstract class AstParserBase implements AstParser
{
     public abstract void resolveRule(AstParserVisitor visitor);

}
