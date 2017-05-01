package au.com.illyrian.parser;

import java.util.Properties;

import au.com.illyrian.classmaker.ast.LineNumber;

public interface Lexer extends LineNumber
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

    public TokenType nextToken();
    
    public TokenType getTokenType();

    public Input getInput();
    
    public void setInput(Input input);

    public String getFilename();

    public int getLineNumber();
}