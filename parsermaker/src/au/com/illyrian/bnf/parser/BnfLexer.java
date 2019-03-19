package au.com.illyrian.bnf.parser;

import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.impl.Latin1Lexer;

public class BnfLexer extends Latin1Lexer
{
    public BnfLexer()
    {
    }

    public BnfLexer(Input inp)
    {
        super(inp);
    }

    public boolean isQuote(char ch)
    {
        return ch == '\"' | ch == '\'';
    }
}
