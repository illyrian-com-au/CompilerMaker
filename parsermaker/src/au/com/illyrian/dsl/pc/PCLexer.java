package au.com.illyrian.dsl.pc;

import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.impl.Latin1Lexer;

public class PCLexer extends Latin1Lexer
{
    public boolean isQuote(char ch)
    {
        return ch == '\"' | ch == '\'';
    }

    /**
     * Span a comment. The text for the token will be available through
     * getTokenString().
     *
     * @return the code for the operator or delimiter.
     */
    public String spanToEndOfLine()
    {
        char ch = getInput().getChar();
        while (ch != Input.NULL && ch != EOL) {
            ch = getInput().nextChar();
        }
        return getTokenValue();
    }


}
