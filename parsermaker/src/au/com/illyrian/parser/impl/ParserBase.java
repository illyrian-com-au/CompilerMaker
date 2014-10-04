package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.CompileUnit;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;

public class ParserBase
{
    private Lexer lexer;
    private CompileUnit compileUnit = null;

    public ParserBase()
    {
        super();
    }

    public Input getInput()
    {
        return getLexer().getLexerInput();
    }
    
    public void setInput(Input input)
    {
        getLexer().setLexerInput(input);
    }

    public CompileUnit getCompileUnit()
    {
        if (compileUnit == null)
            throw new NullPointerException("compileUnit is null.");
        return compileUnit;
    }

    public void setCompileUnit(CompileUnit compileModule)
    {
        this.compileUnit = compileModule;
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
    
    public int getToken()
    {
        return getLexer().getToken();
    }

    /** Advance to the next token. */
    public int nextToken() throws ParserException
    {
        int token = getLexer().nextToken();
        if (token == Lexer.ERROR)
        {
            throw error(getInput(), "Invalid character: " + getLexer().getTokenValue());
        }
        return token;
    }

    public boolean match(int s, String value)
    {
        int token = getLexer().getToken();
        String tokenValue;
        if (s == Lexer.STRING)
            tokenValue = Character.toString(getLexer().getTokenDelimiter());
        else
            tokenValue = getLexer().getTokenValue();
        return (token == s && (value == null || value.equals(tokenValue)));
    }
    
    public boolean accept(int s, String value) throws ParserException
    {
        if (match(s, value)) 
        {
            nextToken();
            return true;
        }
        return false;
    }

    public String expect(int expected, String value) throws ParserException
    {
        return expect(expected, value, null);
    }

    public String expect(int expected, String value, String message) throws ParserException
    {
        if (match(expected, value)) 
        {
            String text = getLexer().getTokenValue(); 
            nextToken();
            return text;
        }
        if (message == null)
            message = getLexer().toErrorString(expected, value);
        throw error(getInput(), message);
    }

    /**
        * Throw an appropriate Exception.
        *
        * @param message -
        *            the error message to include in the Exception.
        * @throws Exception -
        *             with details of the error.
        */
    public ParserException error(Input input, String message)
    {
        return getCompileUnit().error(input, message);
    }
    
    public String toString()
    {
        String line = getInput().getLine();
        if (line == null)
            return "$$";
        StringBuffer buf = new StringBuffer();
        int start = getInput().getTokenStart();
        int finish = getInput().getTokenFinish();
        buf.append(line.substring(0, start));
        buf.append('$');
        buf.append(line.substring(start, finish));
        buf.append('$');
        buf.append(line.substring(finish, line.length()));
        return buf.toString();
    }
}