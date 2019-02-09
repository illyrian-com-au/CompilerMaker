package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.Token;
import au.com.illyrian.parser.TokenType;

public class ParserBase
{
    private Lexer lexer;
    private CompilerContext compilerContext = null;

    public ParserBase()
    {
        super();
    }

    public Input getInput()
    {
        return getLexer().getInput();
    }
    
    public void setInput(Input input)
    {
        getLexer().setInput(input);
    }

    public CompilerContext getCompilerContext()
    {
        if (compilerContext == null)
            throw new NullPointerException("CompilerContext is null.");
        return compilerContext;
    }

    public void setCompilerContext(CompilerContext context)
    {
        this.compilerContext = context;
        context.visitParser(this);
    }

    public Lexer getLexer()
    {
        if (lexer == null)
            lexer = createLexer();
        return lexer;
    }

    protected Lexer createLexer()
    {
        return new Latin1Lexer();
    }

    public void setLexer(Lexer lexer)
    {
        this.lexer = lexer;
    }
    
    /**
     * The source file.
     * 
     * @return the source file or null if the source file is not provided
     */
    public String getFilename()
    {
        return lexer.getFilename();
    }

    /**
     * The line number.
     * The line number of the start of the current token.
     * 
     * @return the line number or 0 if not reading from a source file
     */
    public int getLineNumber()
    {
        return lexer.getLineNumber();
    }
    
    public void addReserved(String word) {
        getLexer().getReservedWords().put(word, word);
    }
    
    public TokenType getTokenType()
    {
        return getLexer().getTokenType();
    }

    /** Advance to the next token. */
    public TokenType nextToken()
    {
        TokenType token = getLexer().nextToken();
        if (token == TokenType.ERROR)
        {
            throw exception(getLexer().getErrorMessage() );
        }
        return token;
    }
    
    public boolean match(Token [] tokens) {
        for (Token token : tokens) {
            if (match(token)) {
                return true;
            }
        }
        return false;
    }

    public boolean match(Token token)
    {
        return match(token.getTokenType(), token.getTokenValue());
    }

    public boolean match(TokenType s)
    {
        TokenType token = getTokenType();
        return (token == s);
    }

    public boolean match(TokenType s, String value)
    {
        TokenType tokenType = getTokenType();
        String tokenValue;
        if (s == TokenType.STRING || s == TokenType.CHARACTER) {
            tokenValue = Character.toString(getLexer().getTokenDelimiter());
        } else {
            tokenValue = getLexer().getTokenValue();
        }
        return (tokenType == s && (value == null || value.equals(tokenValue)));
    }
    
    public boolean accept(Token token)
    {
        return accept(token.getTokenType(), token.getTokenValue());
    }
    
    public boolean accept(TokenType s, String value)
    {
        if (match(s, value)) 
        {
            nextToken();
            return true;
        }
        return false;
    }

    public String expect(Token token)
    {
        return expect(token.getTokenType(), token.getTokenValue(), null);
    }

    public String expect(TokenType expected, String value)
    {
        return expect(expected, value, null);
    }

    public String expect(TokenType expected, String value, String message)
    {
        if (match(expected, value)) 
        {
            String text = getLexer().getTokenValue(); 
            nextToken();
            return text;
        } else {
            if (message == null) {
                message = toErrorString(expected, value);
            }
            throw exception(message);
        }
    }

    public String toErrorString(TokenType token, String value)
    {
        switch (token) {
        case END:
            return "End of input expected";
        case DELIMITER:
        case OPERATOR:
            return value + " expected";
        case RESERVED:
            return value + " expected";
        case IDENTIFIER:
            if (value == null)
                return "Identifier expected";
            else
                return value + " expected";
        case NUMBER:
            return "Integer expected";
        case DECIMAL:
            return "Decimal expected";
        case STRING:
            return "String literal expected";
        case CHARACTER:
            return "Char literal expected";
        case COMMENT:
            return "Comment Expected";
        case ERROR:
            return "Lexer error";
        }
        return value + " expected";
    }

    /**
        * Throw an appropriate Exception.
        *
        * @param message -
        *            the error message to include in the Exception.
        * @throws Exception -
        *             with details of the error.
        */
    public ParserException exception(String message)
    {
        ParserException ex =  new ParserException(message);
        ex.setParserStatus(getInput());
        return ex;
    }
    
    public void error(String message) {
        throw exception(message);
    }
    
    public String toString()
    {
        if (getInput() == null) {
            return "$$ - no input";
        }
        return getInput().toString();
   }
}