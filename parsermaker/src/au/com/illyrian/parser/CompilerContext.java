package au.com.illyrian.parser;

import au.com.illyrian.parser.impl.InvokeParser;

public interface CompilerContext extends VisitParser
{
    public Input getInput();
    
    public String getFullyQualifiedClassName(String simpleName);
    
    public void addFullyQualifiedClassName(String className);
    
    public InvokeParser getInvokeParser();

    public ClassLoader getClassLoader();

    public ParserException error(Input input, String message);
}
