package au.com.illyrian.compiler;

import au.com.illyrian.parser.impl.Latin1Lexer;

public class ParserLexer extends Latin1Lexer
{
    public boolean isQuote(char ch)
    {
        return ch == '\"' | ch == '\'';
    }

}
