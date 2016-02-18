package au.com.illyrian.parser;

import java.io.File;

public interface Input
{
    /**
     * End of input character. (Used internally).
     */
    public static final char NULL       = '\0';

    /**
     * End of line character.
     */
    public static final char EOLN       = '\n';

    /**
     * Get the input string.
     *
     * @return the input string currently being parsed.
     */
    String getLine();

    /**
     * Get the finish of the current token.
     *
     * @return the offset to the finish of the current input token.
     */
    int getTokenFinish();

    /**
     * Get the start of the current token.
     *
     * @return the offset to the start of the current input token.
     */
    int getTokenStart();

    /**
     * Get the String for the current token
     * 
     * @return the String for the current token
     */
    String getTokenString();

    /**
     * Get the character at the start of the current token.
     *
     * @return a character or NULL if past the end of input.
     */
    char startChar();
    
    /**
     * Get the character at the end of the current token.
     *
     * @return a character or NULL if past the end of input.
     */
    char nextChar();

    /**
     * Get the current character without advancing.
     *
     * @return a character or NULL if past the end of input.
     */
    public char getChar();

    /**
     * The source file.
     * 
     * @return the source file or null if the source file is not provided
     */
    public File getSourceFile();

    /**
     * The source file.
     * 
     * @return the source file or null if the source file is not provided
     */
    public String getSourceFilename();
    
    /**
     * The line number
     * @return the line number or 0 if not reading from a source file
     */
    public int getLineNumber();
    
    public String peek(int lookahead);
    
}