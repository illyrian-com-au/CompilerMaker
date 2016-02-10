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
    
    public AstParserRule Rule(AstParser target, AstParser body) {
        return new AstParserRule(target, body);
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
        if (lexer.getToken() == Lexer.IDENTIFIER) {
            AstParserName result = new AstParserName(lexer.getTokenValue());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("Identifier expected.");
        }
    }
    
    public AstParserString String(Lexer lexer) {
        if (lexer.getToken() == Lexer.STRING) {
            AstParserString result = new AstParserString(lexer.getTokenString());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("String expected.");
        }
    }

    public AstParser Integer(Lexer lexer)
    {
        if (lexer.getToken() == Lexer.INTEGER) {
            AstParserInteger result = new AstParserInteger(lexer.getTokenInteger());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("Integer expected.");
        }
    }

    public AstParser Decimal(Lexer lexer)
    {
        if (lexer.getToken() == Lexer.DECIMAL) {
            AstParserDecimal result = new AstParserDecimal(lexer.getTokenFloat());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("Integer expected.");
        }
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

    public AstParser Comma(AstParser left, AstParser right)
    {
        return new AstParserComma(left, right);
    }

}
