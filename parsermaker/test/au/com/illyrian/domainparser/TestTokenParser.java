package au.com.illyrian.domainparser;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParseMember;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserBase;

public class TestTokenParser extends ParserBase implements ParseClass, ParseMember
{
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
            nextToken();
        }
        throw error(getInput(), "Unexpected end of input");
    }
    
//    /* (non-Javadoc)
//     * @see au.com.illyrian.classparser.Parser#parse()
//     */
//    public Object parseClass(Input input, Object action) throws ParserException
//    {
//        setInput(input);
//        nextToken();
//        perentheses();
//        return null;
//    }
//
    /* (non-Javadoc)
     * @see au.com.illyrian.classparser.Parser#parse()
     */
    public Object parseClass() throws ParserException
    {
//        setInput(input);
        nextToken();
        perentheses();
        return null;
    }
    
    public Object parseMember() throws ParserException
    {
        nextToken();
        perentheses();
        return null;
    }

}
