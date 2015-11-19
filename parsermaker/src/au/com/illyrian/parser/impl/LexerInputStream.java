package au.com.illyrian.parser.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import au.com.illyrian.parser.Input;


public class LexerInputStream extends LexerInputString implements Input
{
    final LineNumberReader reader;
    final String sourceFilename;

    public LexerInputStream(File file) throws IOException
    {
        reader = createReader(file);
        sourceFilename = file.getPath();
        nextLine();
    }

    public LexerInputStream(Reader reader, String sourceFilename) throws IOException
    {
        this.reader = new LineNumberReader(reader);
        this.sourceFilename = sourceFilename;
        nextLine();
    }
    
    private static LineNumberReader createReader(File file) throws IOException
    {
        FileReader fileReader = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(fileReader);
        return reader;
    }
    
    public void nextLine()
    {
        try {
            setLine(reader.readLine());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    /**
     * Get the character at the start of the current token.
     *
     * @return a character or NULL if past the end of input.
     */
    public char startChar()
    {
        char ch = super.startChar();
        if (ch == Input.EOLN)
        {
            nextLine();
        }
        return super.startChar();
    }
    
    public char nextChar()
    {
        if (getChar() == Input.EOLN)
        {
            nextLine();
        } else {
            incrementFinish();
        }
        char ch = getChar();
        return ch;
    }

    protected char eoln() {
        return Input.EOLN;
    }

    /**
     * The source file.
     * 
     * @return the source file or null if the source file is not provided
     */
    public String getSourceFilename()
    {
        return sourceFilename;
    }

    /**
     * The line number in the current source file.
     * 
     * @return the line number
     */
    public int getLineNumber()
    {
        return reader.getLineNumber();
    }
    
    public void close() throws IOException
    {
        reader.close();
    }
    
    public String toString()
    {
        return getSourceFilename() + ':' + getLineNumber() + " " + super.toString();
    }
}
