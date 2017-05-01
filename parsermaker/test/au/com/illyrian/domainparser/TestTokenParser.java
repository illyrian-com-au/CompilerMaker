package au.com.illyrian.domainparser;

import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.TokenType;
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
    public void perentheses()
    {
        expect(TokenType.DELIMITER, "{", "'{' expected");
        while (getTokenType() != TokenType.END)
        {
            if (match(TokenType.DELIMITER, "}"))
                return;
            if (match(TokenType.DELIMITER, "{"))
                perentheses();
            buf.append(getLexer().getTokenValue());
            buf.append(' ');
            nextToken();
        }
        throw exception("Unexpected end of input");
    }
    
    /* (non-Javadoc)
     * @see au.com.illyrian.classparser.Parser#parse()
     */
    public Object parseClass(CompilerContext context)
    {
        setCompilerContext(context);
        nextToken();
        perentheses();
        return buf.toString();
    }
    
    public Object parseMembers(CompilerContext context)
    {
        setCompilerContext(context);
        nextToken();
        perentheses();
        return buf.toString();
    }
}
