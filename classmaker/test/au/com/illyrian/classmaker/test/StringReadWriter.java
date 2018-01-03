package au.com.illyrian.classmaker.test;

import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class StringReadWriter extends PrintWriter
{
    public StringReadWriter() {
        super(new StringWriter());
    }
    
    StringReadWriter(Writer writer) {
        super(writer);
    }
    
    public StringReader getReader()
    {
        return new StringReader(toString());
    }
    
    public LineNumberReader getLineReader()
    {
        return new LineNumberReader(getReader());
    }
    
    public void close() {
        // Do nothing otherwise the string gets reset.
    }

    public String toString() {
        return out.toString();
    }
}
