package au.com.illyrian.parser;

import java.util.Properties;

public interface Lexer
{

    /**
     * Get the text for the current token.
     *
     * @return the text for the current input token.
     */
    public String getTokenValue();

    public Integer getTokenInteger();
    
    public Float getTokenFloat();

    public Object getTokenOperator();

    public String getTokenString();
    
    public char getTokenDelimiter();

    public String getErrorMessage();
    
    public Properties getReservedWords();

    public Properties getOperators();

    /**
     * Advance the Lexer to the next token in the input.
     * 
     * @return a number representing the next token
     */
    public TokenType nextToken();
    
    public TokenType getTokenType();

    public Input getInput();
    
    public void setInput(Input input);
}