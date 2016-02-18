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

import au.com.illyrian.parser.Input;

public class LexerInputString implements Input
{
    /** The line of input. */
    private String line;

    /** The start of the current token */
    private int start = 0;

    /** Just past the end of the current token. */
    private int finish = 0;

    /**
     * Constructor for String Tokeniser.
     *
     */
    public LexerInputString()
    {
    }

    /**
     * Constructor for Strong Tokeniser.
     *
     * @param input
     *            the string to be tokenised.
     */
    public LexerInputString(String input)
    {
        setLine(input);
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.LexerInput#getInput()
     */
    public String getLine()
    {
        return line;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.LexerInput#setInput(java.lang.String)
     */
    public void setLine(String input)
    {
        this.line = input;
        start = 0;
        finish = 0;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.LexerInput#getFinish()
     */
    public int getTokenFinish()
    {
        return finish;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.LexerInput#getStart()
     */
    public int getTokenStart()
    {
        return start;
    }

    /**
     * Get the character at the start of the current token.
     *
     * @return a character or NULL if past the end of input.
     */
    public char startChar()
    {
        start = finish;
        return getChar();
    }

    /**
     * Step over the current character.
     */
    int spanCharacter(int state)
    {
        char ch = startChar(); // Mark start of token
        incrementFinish(); // Step over character
        return state;
    }

    /**
     * Get the character at the end of the current token.
     *
     * @return a character or NULL if past the end of input.
     */
    public char nextChar()
    {
        incrementFinish();
        return getChar();
    }

    protected void incrementFinish()
    {
        if (finish < line.length()) {
            ++finish;
        }
    }

    public char getChar()
    {
        if (line == null)
            return Input.NULL;
        else if (finish < line.length())
            return line.charAt(finish);
        else
            return eoln();
    }
    
    protected char eoln() {
        return Input.NULL;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.LexerInput#getTokenString()
     */
    public String getTokenString()
    {
        // Don't read beyond the end of the string.
        return (line != null) ? line.substring(start, finish) : null;
    }

    /**
     * The source file.
     * 
     * @return null as the source file is not provided
     */
    public File getSourceFile()
    {
        return null;
    }

    /**
     * The line number in the current source file.
     * 
     * @return zero (0) as there is no source file.
     */
    public int getLineNumber()
    {
        return 0;
    }

    /**
     * The source file.
     * 
     * @return the source file or null if the source file is not provided
     */
    public String getSourceFilename()
    {
        return (getSourceFile() == null ? "" : getSourceFile().getPath());
    }

    public String peek(int lookahead) {
        if (line== null)
            return null;
        int offset = finish + lookahead;
        if (offset >= line.length())
            return line.substring(finish);
        else
            return line.substring(finish, offset);
    }
    
    public String toString()
    {
        if (line == null)
            return "EOF";
        StringBuffer buf = new StringBuffer();
        buf.append(line.substring(0, start));
        buf.append('$');
        buf.append(line.substring(start, finish));
        buf.append('$');
        buf.append(line.substring(finish, line.length()));
        return buf.toString();
    }
}
