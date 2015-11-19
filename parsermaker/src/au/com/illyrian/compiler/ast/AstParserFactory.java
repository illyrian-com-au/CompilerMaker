package au.com.illyrian.compiler.ast;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;

public class AstParserFactory
{
    public AstParserFactory () {
        
    }

    public AstParserList List(AstParser left, AstParser right) {
        return new AstParserList(left, right);
    }
    
    public AstParserRule Rule(AstParser target, AstParser body, AstParser action) {
        return new AstParserRule(target, body, action);
    }

    public AstParser Seq(AstParser left, AstParser right) {
        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        } else {
            return new AstParserSequence(left, right);
        }
    }
    
    public AstParserAlternative Alt(AstParser left, AstParser right) {
        return new AstParserAlternative(left, right);
    }
    
    public AstParserCall Call(AstParser name, AstParser actuals) {
        return new AstParserCall(name, actuals);
    }

    public AstParserName Name(Lexer lexer) throws ParserException
    {
        // FIXME - check IDENTIFIER
        AstParserName result = new AstParserName(lexer.getTokenValue());
        lexer.nextToken();
        return result;
    }
    
    public AstParserString String(Lexer lexer) {
        // FIXME - check STRING
        AstParserString result = new AstParserString(lexer.getTokenString());
        lexer.nextToken();
        return result;
    }

    public AstParserReserved Reserved(Lexer lexer)
    {
        // FIXME - check RESERVED
        AstParserReserved result = new AstParserReserved(lexer.getTokenValue());
        lexer.nextToken();
        return result;
    }

    public AstParser Empty()
    {
        return new AstParserEmpty();
    }

}
