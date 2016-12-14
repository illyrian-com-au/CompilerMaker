package au.com.illyrian.bnf;

import au.com.illyrian.parser.Token;
import au.com.illyrian.parser.TokenType;

public enum BnfToken implements Token
{
    LPAR   (TokenType.DELIMITER, "("),
    RPAR   (TokenType.DELIMITER, ")"),
    BEGIN  (TokenType.DELIMITER, "{"),
    END    (TokenType.DELIMITER, "}"),
    ASSIGN (TokenType.OPERATOR,  "::="),
    ALT    (TokenType.OPERATOR,  "|"),
    COLON  (TokenType.OPERATOR,  ":"),
    COMMA  (TokenType.DELIMITER, ","),
    SEMI   (TokenType.DELIMITER, ";"),
    
    IDENTIFIER (TokenType.IDENTIFIER),
    RESERVED   (TokenType.RESERVED),
    STRING     (TokenType.STRING);
    
    private final TokenType tokenType; 
    private final String tokenValue;
    
    BnfToken(TokenType type, String value) {
        this.tokenType = type;
        this.tokenValue = value;
    }
    
    BnfToken(TokenType type) {
        this.tokenType = type;
        this.tokenValue = null;
    }
    
    public TokenType getTokenType()
    {
        return tokenType;
    }
    public String getTokenValue()
    {
        return tokenValue;
    }

    public String toString() {
        return "BnfToken(" + tokenType + ", " + tokenValue + ")";
    }
}
