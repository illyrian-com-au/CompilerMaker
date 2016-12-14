package au.com.illyrian.parser.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import au.com.illyrian.domainparser.ModuleParser;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;

public class ModuleContext implements CompilerContext
{

    private Input input = null;
    private InvokeParser invokerParser = null;
    private ModuleParser moduleParser = null;
    private Properties map = new Properties();

    public ModuleContext()
    {
        super();
    }

    public Input getInput()
    {
        if (input == null)
            throw new NullPointerException("Input is null.");
        return input;
    }

    public void setInput(Input input)
    {
        this.input = input;
    }

    public void setInput(File file) throws IOException
    {
        Input input = new LexerInputStream(file);

        this.input = input;
    }

    public void setInputReader(Reader reader, String filename) throws IOException
    {
        setInput(new LexerInputStream(reader, filename));
    }

    public void setInputFile(File sourceFile) throws IOException
    {
        Reader reader = new FileReader(sourceFile);
        setInputReader(reader, sourceFile.getPath());
    }

    public void setInputString(String string, String sourceName) throws IOException
    {
        Reader reader = new StringReader(string);
        setInputReader(reader, sourceName);
    }

    public InvokeParser getInvokeParser()
    {
        if (invokerParser == null)
            invokerParser = new InvokeParserImpl(this);
        return invokerParser;
    }

    public void setInvokeParser(InvokeParser invoker)
    {
        this.invokerParser = invoker;
    }

    public String getFullyQualifiedClassName(String simpleClassName)
    {
        return getAlias(simpleClassName);
    }
    
    public void addFullyQualifiedClassName(String fullyQualifiedClassname)
    {
        int offset = fullyQualifiedClassname.lastIndexOf('.');
        String simpleClassName = (offset == -1) ? fullyQualifiedClassname : fullyQualifiedClassname.substring(offset+1);
        this.setAlias(simpleClassName, fullyQualifiedClassname);
    }
    
    /**
     * Creates a mapping from a simple class name to a fully qualified class name.
     * @param simpleName the simple class name
     * @param className the fully qualified class name
     */
    public void setAlias(String simpleName, String className)
    {
        map.put(simpleName, className);
    }
    
    /**
     * Resolves a simple class name to a fully qualified class name.
     * @param simpleName the simple class name
     * @return the fully qualified class name if available; otherwise the simple class name
     */
    public String getAlias(String simpleName)
    {
        String className = map.getProperty(simpleName);
        if (className == null)
            className = simpleName;
        return className;
    }

    public ModuleParser getModuleParser()
    {
        if (moduleParser == null) {
            // FIXME should create ClassParser
            moduleParser = new ModuleParser();
        }
        return moduleParser;
    }

    public void setModuleParser(ModuleParser moduleParser)
    {
        this.moduleParser = moduleParser;
        moduleParser.setCompilerContext(this);
    }

    public ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    public Object parseModule() throws ParserException
    {
        ModuleParser parser = getModuleParser();
        visitParser(parser);
        return parser.parseModule(this);
    }

    public void visitParser(Object object)
    {
        if (object instanceof ParserBase)
        {
            visit((ParserBase)object);
        }
    }

    public void visit(ParserBase parser)
    {
        parser.setInput(getInput());
    }

    public ParserException error(Input input, String message)
    {
        ParserException ex =  new ParserException(message);
        ex.setParserStatus(input);
        return ex;
    }

}