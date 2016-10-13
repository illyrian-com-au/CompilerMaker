package au.com.illyrian.domainparser;

import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserBase;

public class TestTokenParser extends ParserBase implements ParseClass, ParseMembers
{
    StringBuffer buf = new StringBuffer();
    
    public TestTokenParser()
    {
        setLexer(createLexer());
    }

    protected Latin1Lexer createLexer()
    {
        return new Latin1Lexer();
    }
    
    /* (non-Javadoc)
     * @see au.com.illyrian.classparser.Parser#parse()
     */
    public void perentheses() throws ParserException
    {
        expect(Lexer.OPEN_P, "{", "'{' expected");
        while (getToken() != Lexer.END)
        {
            if (match(Lexer.CLOSE_P, "}"))
                return;
            if (match(Lexer.OPEN_P, "{"))
                perentheses();
            buf.append(getLexer().getTokenValue());
            buf.append(' ');
            nextToken();
        }
        throw error(getInput(), "Unexpected end of input");
    }
    
    /* (non-Javadoc)
     * @see au.com.illyrian.classparser.Parser#parse()
     */
    public Object parseClass(CompilerContext context) throws ParserException
    {
        setCompilerContext(context);
        nextToken();
        perentheses();
        return buf.toString();
    }
    
    public Object parseMembers(CompilerContext context) throws ParserException
    {
        setCompilerContext(context);
        nextToken();
        perentheses();
        return buf.toString();
    }
}
