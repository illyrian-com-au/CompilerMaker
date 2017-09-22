package au.com.illyrian.classmaker;

class SourceLineImpl implements SourceLine {
    /** The name of the source file relative to the source path */
    private String sourceFilename = null;
    int currentLineNumber = 0;

    public void setFilename(String filename) {
        if (sourceFilename != null) {
            throw new IllegalStateException("Filename cannot be set twice (old)" + sourceFilename + " (new)"
                    + filename);
        }
        sourceFilename = filename;
    }

    /**
     * Current source file name.
     */
    public String getFilename() {
        return sourceFilename;
    }

    /**
     * Current Line Number.
     */
    public int getLineNumber() {
        return currentLineNumber;
    }

    /**
     * Remembers the current line number. <br/>
     * Called whenever the line number changes in the input file.
     * The line number may be used by <code>markLineNumber</code> to
     * generate a
     * line number entry in the generated class file.
     *
     * @param lineNumber
     *            current line number
     */
    public void setLineNumber(int lineNumber) {
        currentLineNumber = lineNumber;
    }
}