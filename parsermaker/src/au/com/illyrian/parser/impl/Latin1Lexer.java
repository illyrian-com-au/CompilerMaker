/*
 * Created on 7/05/2008 by strongd
 *
 * Copyright (c) 2006 Department of Infrastructure (DOI)
 * State Government of Victoria, Australia
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of DOI.
 */

package au.com.illyrian.parser.impl;

import java.io.File;
import java.util.Properties;

import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.TokenType;

/**
 *
 * This tokeniser splits an input string into tokens. <br>
 * It returns these tokens:
 * <ul>
 * <li>ERROR - Invalid or unrecognised character</li>
 * <li>END - End of input</li>
 * <li>IDENTIFIER - A java style identifier</li>
 * <li>DELIMITER - ;,({[)}]</li>
 * <li>OPERATOR - "`~!@#%^&*|\:;'<,.>?/+=-</li>
 * <li>STRING - " any characters "</li>
 * <li>CHARACTER - ' a single char '</li>
 * <li>RESERVED - An Identifier with special meaning</li>
 * </ul>
 * <p>
 * Identifiers start with an alphabetic character and may include any number of
 * alphanumeric characters. <br>
 * Identifiers are case sensitive while reserved words (AND and OR) are
 * <b>not</b> case sensitive.
 *
 * @author strongd
 */
public class Latin1Lexer implements Lexer
{
    /** The last read token. Modified by nextToken() */
    private TokenType tokenType = TokenType.END;

    private Input input = null;

    /** Whitespace before the current token */
    private String whitespace;

    /** The characters in a string or char excluding the quotes. */
    private String tokenString;

    /** The quote character that surrounded the string. */
    private char tokenDelimiter;

    /** The text of a comment. */
    private String commentString = null;

    private String errorMessage;

    /**
     * End of line character. (Used internally).
     */
    protected static final char EOL = '\n';

    /**
     * A map from reserved word to an object.
     */
    private Properties reservedWords = null;

    /**
     * A map from operator to an object.
     */
    private Properties operators = null;

    /**
     * Constructor for Search Query Tokeniser.
     *
     * @param input
     *            the string to be tokenised.
     */
    public Latin1Lexer()
    {
    }

    /**
     * Constructor for Search Query Tokeniser.
     *
     * @param input
     *            the string to be tokenised.
     */
    public Latin1Lexer(Input input)
    {
        setInput(input);
    }

    public void setInput(Input input)
    {
        this.input = input;
    }

    /*
     * @see au.com.illyrian.parser.Lexer#getLexerInput()
     */
    public Input getInput()
    {
        return input;
    }

    public String getCommentString()
    {
        return commentString;
    }

    public void setReservedWords(Properties reservedWords)
    {
        this.reservedWords = reservedWords;
    }

    public Properties getReservedWords()
    {
        if (reservedWords == null)
            reservedWords = new Properties();
        return reservedWords;
    }

    public void setOperators(Properties operators)
    {
        this.operators = operators;
    }

    public Properties getOperators()
    {
        if (operators == null)
            operators = new Properties();
        return operators;
    }

    /**
     * Get the whitespace before the current token.
     *
     * @return the whitespace before the current input token.
     */
    public String getWhitespace()
    {
        return whitespace;
    }

    public Lexer clone()
    {
        return null;
    }

    public TokenType getTokenType()
    {
        return tokenType;
    }

    /*
     */
    public String getTokenValue()
    {
        return input.getTokenString();
    }
    
    protected void setTokenString(String value) {
        tokenString = value;
    }

    public String getTokenString()
    {
        return tokenString;
    }

    public char getTokenDelimiter()
    {
        return tokenDelimiter;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public Integer getTokenInteger()
    {
        if (tokenType == TokenType.NUMBER) {
            return Integer.valueOf(input.getTokenString());
        } else {
            throw new NumberFormatException("Token is not an Integer");
        }
    }

    public Float getTokenFloat()
    {
        if (tokenType == TokenType.DECIMAL) {
            return Float.valueOf(input.getTokenString());
        } else {
            throw new NumberFormatException("Token is not a decimal number");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.illyrian.parser.Lexer#getTokenString()
     */
    public Object getTokenOperator()
    {
        if (tokenType == TokenType.OPERATOR) {
            return getOperators().get(input.getTokenString());
        } else {
            throw new IllegalStateException("Token is not an Operator");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.illyrian.parser.Lexer#nextToken()
     */
    public TokenType nextToken()
    {
        if (input == null)
            throw new NullPointerException("Input is null.");

        do {

            // Move over any whitespace before the token.
            spanWhiteSpace();
            // Read the next token
            tokenType = spanNextToken();

        } while (tokenType == TokenType.COMMENT);
        return tokenType;
    }

    /**
     * Read the next token.
     */
    protected TokenType spanNextToken()
    {
        TokenType token;
        // Now examine the start character.
        char ch = input.startChar();
        if (ch == Input.NULL) {
            token = TokenType.END;
        } else if (isIdentifierStartChar(ch)) {
            token = spanIdentifier();
        } else if (isDigitChar(ch)) {
            token = spanNumber();
        } else if (peekLineComment()) {
            token = spanLineComment();
        } else  if (peekMultiComment()) {
            token = spanMultiComment();
        } else if (isDelimiter(ch)) {
            token = spanDelimiter();
        } else if (isQuote(ch)) {
            token = spanStringLiteral();
        } else if (isCharacterQuote(ch)) {
            token = spanCharLiteral();
        } else if (isOperator(ch)) {
            token = spanOperator();
        } else {
            token = error("Unrecognised input character: " + LexerInputString.encode(ch));
        }
        return token;
    }

    /**
     * Step over the current delimiter character.
     * Spans a single character, e.g. <code>;</code> or <code>(</code>.
     * The delimiter character is available from <code>getDelimiter()</code> 
     * and <code>getTokenValue()</code>.
     * @returns Lexer.DELIMITER
     */
    TokenType spanDelimiter()
    {
        input.startChar(); // Mark start of token
        tokenDelimiter = input.nextChar();
        return TokenType.DELIMITER;
    }

    /**
     * Step over the current quoted character token.
     * Recognises a token of the form 'a'. 
     * The character without quotes is available from <code>getString()</code>.
     * The quote delimiter is available from <code>getDelimiter()</code>.
     * The entire token including quotes is available from <code>getTokenValue()</code>.
     * @returns Lexer.CHARACTER
     */
    TokenType spanCharLiteral()
    {
        char ch = input.startChar(); // Mark start of token
        if (isCharacterQuote(ch)) {
            tokenDelimiter = ch;
            ch = input.nextChar();
            if (!isCharacterQuote(ch)) {
                tokenString = String.valueOf(ch); // Store the character
                ch = input.nextChar();
                if (isCharacterQuote(ch) && tokenDelimiter == ch) {
                    ch = input.nextChar();
                    return TokenType.CHARACTER;
                }
                return error("Missing quote at end of character: " + tokenDelimiter);
            }
            return error("Missing character within quotes.");
        }
        return error("Missing quote at start of character.");
    }

    /**
     * Step over the current quoted string.
     * Recognises a token of the form "Hello world". 
     * The string without quotes is available from <code>getString()</code>.
     * The quote delimiter is available from <code>getDelimiter()</code>.
     * The entire token including quotes is available from <code>getTokenValue()</code>.
     * @returns Lexer.CHARACTER
     */
    TokenType spanStringLiteral()
    {
        StringBuffer buf = new StringBuffer();
        char ch = input.startChar(); // Mark start of token
        if (isQuote(ch)) {
            tokenDelimiter = ch;
            ch = input.nextChar();
            while (!isQuote(ch)) {
                if (ch == Input.NULL)
                    break;
                buf.append(ch);
                ch = input.nextChar();
            }
            if (ch == tokenDelimiter) {
                ch = input.nextChar();
                tokenString = buf.toString();
                return TokenType.STRING;
            }
            return error("Missing quote at end of String: " + tokenDelimiter);
        }
        return error("Missing quote at start of character.");
    }

    /**
     * Skip over the whitespace at the start of the current token.
     */
    protected void spanWhiteSpace()
    {
        char ch = input.startChar();
        if (isWhitespace(ch)) {
            while (isWhitespace(ch)) {
                ch = input.nextChar();
            }
            whitespace = getTokenValue();
        } else {
            whitespace = "";
        }
    }

    /**
     * Span the current identifier in the input string. Determine whether the
     * identifier is a reserved word. The text for the token will be available
     * through getTokenString().
     *
     * @return the code for the identifier or reserved word.
     */
    public TokenType spanIdentifier()
    {
        char ch = input.startChar();
        {
            // Move the finish pointer just past the end of the identifier.
            while (isIdentifierChar(ch)) {
                ch = input.nextChar();
            }
        }
        // Examine the identifier to determine if it is a reserved word.
        if (reservedWords != null) {
            String identifier = getTokenValue();
            if (getReservedWords().getProperty(identifier) != null) {
                if (getOperators().getProperty(identifier) != null) {
                    return TokenType.OPERATOR;
                }
                return TokenType.RESERVED;
            }
        }
        return TokenType.IDENTIFIER;
    }

    /**
     * Span a sequence of digits in the input string. Determine whether the
     * number is valid. The text for the token will be available through
     * getTokenValue().
     *
     * @return the code for the operator or delimiter.
     */
    public TokenType spanNumber()
    {
        char ch = input.startChar();
        while (isDigitChar(ch)) {
            ch = input.nextChar();
        }
        if (ch != '.') {
            return TokenType.NUMBER;
        }
        ch = input.nextChar();
        while (isDigitChar(ch)) {
            ch = input.nextChar();
        }
        return TokenType.DECIMAL;
    }

    /**
     * Span a sequence of operators in the input string. Determine whether the
     * operator is valid. The text for the token will be available through
     * getTokenString().
     *
     * @return the code for the operator or delimiter.
     */
    public TokenType spanOperator()
    {
        char ch = input.startChar();
        while (isOperator(ch)) {
            // Handle 1 -// \n 3; and 1 */**/ 3;
            if (peekLineComment() || peekMultiComment()) {
                break;
            }
            ch = input.nextChar();
        }
        return TokenType.OPERATOR;
    }

    boolean peekLineComment()
    {
        return ("//".equals(input.peek(2)));
    }

    /**
     * Span a comment. The text for the token will be available through
     * getTokenString().
     *
     * @return the code for the operator or delimiter.
     */
    public TokenType spanLineComment()
    {
        // Move the start pointer past the end of line.
        char ch = input.startChar();
        ch = input.nextChar();
        ch = input.nextChar();
        while (ch != Input.NULL && ch != EOL) {
            ch = input.nextChar();
        }
        commentString = input.getTokenString();
        return TokenType.COMMENT;
    }

    boolean peekMultiComment()
    {
        return ("/*".equals(input.peek(2)));
    }

    /**
     * Span a comment. The text for the token will be available through
     * getCommentString().
     *
     * @return the code for the operator or delimiter.
     */
    public TokenType spanMultiComment()
    {
        commentString = "";
        char prev = Input.NULL;
        char ch = input.nextChar();
        while (ch != Input.NULL) {
            if (prev == '*' && ch == '/') {
                input.nextChar();
                break;
            } else if (ch == Input.EOLN) {
                commentString += input.getTokenString();
            }
            prev = ch;
            ch = input.nextChar();
        }
        commentString += input.getTokenString();
        return TokenType.COMMENT;
    }

    protected TokenType error(String message)
    {
        errorMessage = message;
        return TokenType.ERROR;
    }

    // #### character tests ####

    /**
     * Determine whether the character is a white space character.
     *
     * @param ch
     *            the character to be tested.
     * @return true if the character is a digit.
     */
    public boolean isWhitespace(char ch)
    {
        return Character.isWhitespace(ch);
    }

    /**
     * Determine whether the character is a digit.
     *
     * @param ch
     *            - the character to be tested.
     * @return true if the character is a digit.
     */
    public boolean isDigitChar(char ch)
    {
        return ('0' <= ch && ch <= '9');
    }

    /**
     * Determine whether the character is suitable in an identifier.
     *
     * @param ch
     *            - the character to be tested.
     * @return true if the character is a letter or digit.
     */
    public boolean isIdentifierChar(char ch)
    {
        return isIdentifierStartChar(ch) || isDigitChar(ch);
    }

    /**
     * Determine whether the character is suitable as the first character of an
     * identifier.
     *
     * @param ch
     *            - the character to be tested.
     * @return true if the character is a letter.
     */
    public boolean isIdentifierStartChar(char ch)
    {
        return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '$' || ch == '_';
    }

    public boolean isDelimiter(char ch)
    {
        switch (ch) {
        case ',' :
        case ';' :
        case '(' :
        case '{' :
        case '[' :
        case ')' :
        case '}' :
        case ']' :
            return true;
        default:
            return false;
        }
    }

    public boolean isQuote(char ch)
    {
        return ch == '\"';
    }

    public boolean isCharacterQuote(char ch)
    {
        return ch == '\'';
    }

    public boolean isOperator(char ch)
    {
        switch (ch) {
        case '+':
        case '-':
        case '*':
        case '/':
        case '=':
        case '%':
        case '&':
        case '|':
        case '<':
        case '>':
        case '.':
        case '?':
        case '!':
        case '~':
        case '^':
        case ':':
            return true;
        default:
            return false;
        }
    }
    
    /**
     * The source file.
     * 
     * @return the source file or null if the source file is not provided
     */
    public File getSourceFile()
    {
        return input.getSourceFile();
    }

    /**
     * The source file.
     * 
     * @return the source file or null if the source file is not provided
     */
    public String getSourceFilename()
    {
        return input.getSourceFilename();
    }

    /**
     * The line number
     * 
     * @return the line number or 0 if not reading from a source file
     */
    public int getLineNumber()
    {
        return input.getLineNumber();
    }

    public String toString()
    {
        switch (tokenType) {
        case END:
            return "END";
        case IDENTIFIER:
        case DELIMITER:
        case RESERVED:
        case OPERATOR:
        case NUMBER:
        case DECIMAL:
        case CHARACTER:
        case STRING:
            return tokenType + "= " + getTokenValue();
        case COMMENT:
            return "COMMENT= " + getCommentString();
        case ERROR:
            return "ERROR= " + getErrorMessage();
        }
        return "Unknown token index : " + tokenType;
    }
}
