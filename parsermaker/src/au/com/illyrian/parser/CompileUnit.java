package au.com.illyrian.parser;

import au.com.illyrian.parser.impl.InvokeParser;

public interface CompileUnit extends VisitParser
{
    public Input getInput();
    
    public String getFullyQualifiedClassName(String simpleClassName);
    
    public InvokeParser getInvokeParser();

    public ClassLoader getClassLoader();

    public ParserException error(Input input, String message);
}
