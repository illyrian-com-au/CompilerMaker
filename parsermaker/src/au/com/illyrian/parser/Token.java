package au.com.illyrian.parser;

public interface Token
{
    TokenType getTokenType();
    
    String getTokenValue();
}
