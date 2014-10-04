package au.com.illyrian.dsl.pc;

import au.com.illyrian.parser.impl.Latin1Lexer;

public class PCLexer extends Latin1Lexer
{
    public boolean isQuote(char ch)
    {
        return ch == '\"' | ch == '\'';
    }

}
