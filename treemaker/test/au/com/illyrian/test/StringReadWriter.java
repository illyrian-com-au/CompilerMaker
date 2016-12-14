package au.com.illyrian.test;

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

    public String toString() {
        return out.toString();
    }
}
