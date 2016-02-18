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

/**
 *
 * This tokeniser splits an input string into tokens. <br>
 * It returns these tokens:
 * <ul>
 * <li>ERROR - Invalid or unrecognised character</li>
 * <li>END - End of input</li>
 * <li>IDENTIFIER - A java style identifier</li>
 * <li>DELIMITER - "`~!@#%^&*|\:;'<,.>?/+=-</li>
 * <li>OPEN_DELIM - ({[</li>
 * <li>CLOSE_DELIM - )}]</li>
 * <li>OPERATOR - Sequence of one or more delimiters</li>
 * <li>STRING - " any "</li>
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
    private int token = 0;

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

    public int getToken()
    {
        return token;
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

    /*
     * (non-Javadoc)
     * 
     * @see au.com.illyrian.parser.Lexer#getTokenString()
     */
    public String getTokenValue()
    {
        return input.getTokenString();
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
        if (token == INTEGER) {
            return Integer.valueOf(input.getTokenString());
        } else {
            throw new NumberFormatException("Token is not an Integer");
        }
    }

    public Float getTokenFloat()
    {
        if (token == DECIMAL) {
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
        if (token == OPERATOR) {
            return getOperators().get(input.getTokenString());
        } else {
            throw new IllegalStateException("Token is not an Operator");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.illyrian.parser.Lexer#getStart()
     */
    public int getStart()
    {
        return input.getTokenStart();
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.illyrian.parser.Lexer#getFinish()
     */
    public int getFinish()
    {
        return input.getTokenFinish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.illyrian.parser.Lexer#nextToken()
     */
    public int nextToken()
    {
        if (input == null)
            throw new NullPointerException("Input is null.");

        do {

            // Move over any whitespace before the token.
            spanWhiteSpace();

            // Now examine the start character.
            char ch = input.startChar();
            if (ch == Input.NULL) {
                token = END;
            } else if (isIdentifierStartChar(ch)) {
                token = spanIdentifier();
            } else if (isDigitChar(ch)) {
                token = spanNumber();
            } else if (peekLineComment()) {
                token = spanLineComment();
            } else  if (peekMultiComment()) {
                token = spanMultiComment();
            } else if (isDelimiter(ch)) {
                token = spanCharacter(DELIMITER);
            } else if (isOpenP(ch)) {
                token = spanCharacter(OPEN_P);
            } else if (isCloseP(ch)) {
                token = spanCharacter(CLOSE_P);
            } else if (isQuote(ch)) {
                token = spanStringLiteral();
            } else if (isCharacterQuote(ch)) {
                token = spanCharLiteral();
            } else if (isOperator(ch)) {
                token = spanOperator();
            } else {
                this.token = error("Unrecognised input character: \\x0" + Integer.toOctalString(ch));
            }
        } while (token == COMMENT);
        return this.token;
    }

    /**
     * Step over the current character.
     */
    int spanCharacter(int state)
    {
        char ch = input.startChar(); // Mark start of token
        ch = input.nextChar();
        return state;
    }

    /**
     * Step over the current string.
     */
    int spanCharLiteral()
    {
        char ch = input.startChar(); // Mark start of token
        if (isCharacterQuote(ch)) {
            tokenDelimiter = ch;
            ch = input.nextChar();
            if (!isCharacterQuote(ch)) {
                tokenString = String.valueOf(ch); // Step over character
                ch = input.nextChar();
                if (isCharacterQuote(ch) && tokenDelimiter == ch) {
                    ch = input.nextChar();
                    return CHARACTER;
                }
                return error("Missing quote at end of character: " + tokenDelimiter);
            }
            return error("Missing character within quotes.");
        }
        return error("Missing quote at start of character.");
    }

    /**
     * Step over the current character.
     */
    int spanStringLiteral()
    {
        StringBuffer buf = new StringBuffer();
        char ch = input.startChar(); // Mark start of token
        if (isQuote(ch)) {
            tokenDelimiter = ch;
            ch = input.nextChar();
            while (!isQuote(ch)) {
                if (ch == END)
                    break;
                buf.append(ch);
                ch = input.nextChar();
            }
            if (ch == tokenDelimiter) {
                ch = input.nextChar();
                tokenString = buf.toString();
                return STRING;
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
        int ch = input.startChar();
        if (isWhitespace((char) ch) || isStartComment((char) ch)) {
            while (isWhitespace((char) ch) || isStartComment((char) ch)) {
                if (isStartComment((char) ch)) {
                    spanLineComment();
                    ch = input.nextChar();
                } else {
                    ch = input.nextChar();
                }
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
    public int spanIdentifier()
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
                    return OPERATOR;
                }
                return RESERVED;
            }
        }
        return IDENTIFIER;
    }

    /**
     * Span a sequence of digits in the input string. Determine whether the
     * number is valid. The text for the token will be available through
     * getTokenValue().
     *
     * @return the code for the operator or delimiter.
     */
    public int spanNumber()
    {
        char ch = input.startChar();
        while (isDigitChar(ch)) {
            ch = input.nextChar();
        }
        if (ch != '.')
            return INTEGER;
        ch = input.nextChar();
        while (isDigitChar(ch)) {
            ch = input.nextChar();
        }
        return DECIMAL;
    }

    /**
     * Span a sequence of operators in the input string. Determine whether the
     * operator is valid. The text for the token will be available through
     * getTokenString().
     *
     * @return the code for the operator or delimiter.
     */
    public int spanOperator()
    {
        char ch = input.startChar();
        while (isOperator(ch)) {
            if (peekLineComment() || peekMultiComment()) {
                break;
            }
            ch = input.nextChar();
        }
        return OPERATOR;
    }

    /**
     * Span a sequence of operators in the input string. Determine whether the
     * operator is valid. The text for the token will be available through
     * getTokenString().
     *
     * @return the code for the operator or delimiter.
     */
    public int spanString()
    {
        char ch = input.startChar();
        if (isQuote(ch)) {
            ch = input.nextChar();

            while (!isQuote(ch)) {
                ch = input.nextChar();
            }
        }
        return STRING;
    }

    boolean isStartLineComment()
    {
        if (input.getTokenFinish() - input.getTokenStart() == 2) {
            if ("//".equals(input.getTokenString())) {
                return true;
            }
        }
        return false;
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
    public int spanLineComment()
    {
        // Move the start pointer past the end of line.
        char ch = input.startChar();
        ch = input.nextChar();
        ch = input.nextChar();
        while (ch != Input.NULL && ch != EOL) {
            ch = input.nextChar();
        }
        commentString = input.getTokenString();
        return COMMENT;
    }

    boolean peekMultiComment()
    {
        return ("/*".equals(input.peek(2)));
    }

    boolean isStartMultiComment()
    {
        if (input.getTokenFinish() - input.getTokenStart() == 2) {
            if ("/*".equals(input.getTokenString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Span a comment. The text for the token will be available through
     * getCommentString().
     *
     * @return the code for the operator or delimiter.
     */
    public int spanMultiComment()
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
        return COMMENT;
    }

    /**
     * Span a comment. The text for the token will be available through
     * getTokenString().
     *
     * @return the code for the operator or delimiter.
     */
    public String spanToEndOfLine()
    {
        char ch = input.getChar();
        while (ch != Input.NULL && ch != EOL) {
            ch = input.nextChar();
        }
        return this.getTokenValue();
    }

    int error(String message)
    {
        errorMessage = message;
        return ERROR;
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
        return ch == ',' || ch == ';';
    }

    public boolean isOpenP(char ch)
    {
        return ch == '(' || ch == '{' || ch == '[';
    }

    public boolean isStartComment(char ch)
    {
        return ch == '#';
    }

    public boolean isCloseP(char ch)
    {
        return ch == ')' || ch == '}' || ch == ']';
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

    public String toErrorString(int token, String value)
    {
        switch (token) {
        case END:
            return "End of input expected";
        case OPEN_P:
        case CLOSE_P:
        case DELIMITER:
        case QUOTE:
        case OPERATOR:
            return "'" + value + "' expected";
        case RESERVED:
            return value + " expected";
        case IDENTIFIER:
            if (value == null)
                return "Identifier expected";
            else
                return value + " expected";
        case INTEGER:
            return "Integer expected";
        case DECIMAL:
            return "Decimal expected";
        }
        return value + " expected";
    }

    public String toString()
    {
        switch (token) {
        case END:
            return "END";
        case IDENTIFIER:
            return "IDENTIFIER=" + getTokenValue();
        case OPEN_P:
            return "OPEN_P='" + getTokenValue() + "'";
        case CLOSE_P:
            return "CLOSE_P='" + getTokenValue() + "'";
        case DELIMITER:
            return "DELIMITER='" + getTokenValue() + "'";
        case RESERVED:
            return "RESERVED=" + getTokenValue();
        case QUOTE:
            return "QUOTE='" + getTokenValue() + "'";
        case OPERATOR:
            return "OPERATOR='" + getTokenValue() + "'";
        case INTEGER:
            return "INTEGER=" + getTokenValue();
        case DECIMAL:
            return "DECIMAL=" + getTokenValue();
        case STRING:
            return "STRING=" + getTokenValue();
        }
        return "Unknown token @ " + input + "\n" + input.getTokenStart() + ": "
                + input.getLine().substring(input.getTokenStart());
    }
}
