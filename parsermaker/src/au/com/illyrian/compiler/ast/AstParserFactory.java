package au.com.illyrian.compiler.ast;

public class AstParserFactory
{
    public AstParserFactory () {
        
    }

    public AstParserList List(AstParser left, AstParser right) {
        return new AstParserList(left, right);
    }
    
    public AstParserRule Rule(AstParser target, AstParser body) {
        return new AstParserRule(target, body);
    }

    public AstParserSequence Seq(AstParser left, AstParser right) {
        return new AstParserSequence(left, right);
    }
    
    public AstParserAlternative Alt(AstParser left, AstParser right) {
        return new AstParserAlternative(left, right);
    }
    
    public AstParserName Name(String name) {
        return new AstParserName(name);
    }

    public AstParserCall Call(AstParser name, AstParser actuals) {
        return new AstParserCall(name, actuals);
    }

    public AstParserString String(String text) {
        return new AstParserString(text);
    }

    public AstParserReserved Reserved(String name)
    {
        return new AstParserReserved(name);
    }

}
