package au.com.illyrian.parser;

import java.util.Properties;

public interface Lexer
{

    /**
     * Error on input token.
     */
    public static final int ERROR = -1;
    /**
     * End of input token.
     */
    public static final int END = 0;
    /**
     * Identifier token. The name of the identifier is available through getTokenString().
     */
    public static final int IDENTIFIER = 1;
    /**
     * The Open Parentheses token.
     */
    public static final int OPEN_P = 2;
    /**
     * The Close Parentheses token.
     */
    public static final int CLOSE_P = 3;
    /**
     * The DELIMITER token.
     */
    public static final int DELIMITER = 4;
    /**
     * The Reserved word token.
     */
    public static final int RESERVED = 5;
    /**
     * The Reserved word token.
     */
    public static final int QUOTE = 6;
    /**
     * The Operator token.
     */
    public static final int OPERATOR = 7;
    /**
     * The Integer literal token.
     */
    public static final int INTEGER = 8;
    /**
     * The Decimal literal token.
     */
    public static final int DECIMAL = 9;
    /**
     * The Character literal token.
     */
    public static final int CHARACTER = 10;
    /**
     * The String literal token.
     */
    public static final int STRING = 11;

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

    public String toErrorString(int token, String value);
    
    public String getErrorMessage();
    
    public Properties getReservedWords();

    public Properties getOperators();

    /**
     * Advance the Lexer to the next token in the input.
     * 
     * @return a number representing the next token
     */
    public int nextToken();
    
    public String spanToEndOfLine();
    
    public int getToken();

    public Input getLexerInput();
    
    public void setLexerInput(Input input);
}