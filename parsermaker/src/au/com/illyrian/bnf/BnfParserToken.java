package au.com.illyrian.bnf;

import au.com.illyrian.parser.Token;
import au.com.illyrian.parser.TokenType;

public class BnfParserToken implements Token
{
    private final TokenType tokenType;
    private final String tokenValue;
    
    public BnfParserToken(TokenType type)
    {
        this(type, null);
    }
    
    public BnfParserToken(TokenType type, String value)
    {
        this.tokenType = type;
        this.tokenValue = value;
    }

    @Override
    public TokenType getTokenType()
    {
        return tokenType;
    }

    @Override
    public String getTokenValue()
    {
        return tokenValue;
    }

    public String toString() {
        return "BnfToken(" + tokenType + ", " + tokenValue + ")";
    }
}
