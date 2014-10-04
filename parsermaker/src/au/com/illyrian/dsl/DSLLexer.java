package au.com.illyrian.dsl;

import au.com.illyrian.parser.impl.Latin1Lexer;

public class DSLLexer extends Latin1Lexer
{
    public boolean isQuote(char ch)
    {
        return ch == '\"' | ch == '\'';
    }

}
