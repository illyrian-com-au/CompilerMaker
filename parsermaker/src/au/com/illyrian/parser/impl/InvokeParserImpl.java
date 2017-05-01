package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParseExpression;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.ParseStatement;
import au.com.illyrian.parser.ParserException;

public class InvokeParserImpl<T> extends ParserBase implements InvokeParser<T>
{
    public InvokeParserImpl()
    {
        super();
    }

    public InvokeParserImpl(CompilerContext compilerContext)
    {
        super();
        setCompilerContext(compilerContext);
    }

    protected ClassLoader getClassLoader()
    {
        return getCompilerContext().getClassLoader();
    }

    public Input getInput() {
        return getCompilerContext().getInput();
    }
    
    protected Object loadParser(String parseName)
    {
        Object objectInstance = null;
        try {
            Class parserClass = getClassLoader().loadClass(parseName);
            objectInstance = parserClass.newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw exception("Could not load parser: " + parseName);
        } catch (IllegalAccessException iae) {
            throw exception("Could not access parser: " + parseName);
        } catch (InstantiationException iae) {
            throw exception("Could not instantiate parser: " + parseName);
        }
        return objectInstance;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseModule(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseModule(String parseName)
    {
        ParseModule<T> parserInstance = null;
        try {
            Object parser = loadParser(parseName);
            parserInstance = (ParseModule<T>) parser;
        } catch (ClassCastException cce) {
            throw exception("Class " + parseName + " does not implement the ParseModule interface.");
        } catch (ParserException ex) {
            throw ex;
        } catch (Exception ex) {
            ParserException pex = new ParserException("Error invoking parser: " + parseName, ex);
            pex.setParserStatus(getInput());
            throw pex;
        }
        return parserInstance.parseModule(getCompilerContext());
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseClass(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseClass(String parseName)
    {
        ParseClass<T> parserInstance = null;
        try {
            Object parser = loadParser(parseName);
            parserInstance = (ParseClass<T>) parser;
        } catch (ClassCastException cce) {
            throw exception("Class " + parseName + " does not implement the ParseClass interface.");
        } catch (ParserException ex) {
            throw ex;
        } catch (Exception ex) {
            ParserException pex = new ParserException("Error invoking parser: " + parseName, ex);
            pex.setParserStatus(getInput());
            throw pex;
        }
        return parserInstance.parseClass(getCompilerContext());
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseMember(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseMember(String parseName)
    {
        ParseMembers<T> parserInstance = null;
        try {
            Object parser = loadParser(parseName);
            parserInstance = (ParseMembers<T>) parser;
        } catch (ClassCastException cce) {
            throw exception("Class " + parseName + " does not implement the ParseMember interface.");
        } catch (ParserException ex) {
            throw ex;
        } catch (Exception ex) {
            ParserException pex = new ParserException("Error invoking parser: " + parseName, ex);
            pex.setParserStatus(getInput());
            throw pex;
        }
        return parserInstance.parseMembers(getCompilerContext());
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseStatement(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseStatement(String parseName)
    {
        ParseStatement<T> parserInstance = null;
        try {
            Object parser = loadParser(parseName);
            parserInstance = (ParseStatement<T>) parser;
        } catch (ClassCastException cce) {
            throw exception("Class " + parseName + " does not implement the ParseStatement interface.");
        } catch (ParserException ex) {
            throw ex;
        } catch (Exception ex) {
            ParserException pex = new ParserException("Error invoking parser: " + parseName, ex);
            pex.setParserStatus(getInput());
            throw pex;
        }
        return parserInstance.parseStatement(getCompilerContext());
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseExpression(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseExpression(String parseName)
    {
        ParseExpression<T> parserInstance = null;
        try {
            Object parser = loadParser(parseName);
            parserInstance = (ParseExpression) parser;
        } catch (ClassCastException cce) {
            throw exception("Class " + parseName + " does not implement the ParseExpression interface.");
        } catch (ParserException ex) {
            throw ex;
        } catch (Exception ex) {
            ParserException pex = new ParserException("Error invoking parser: " + parseName, ex);
            pex.setParserStatus(getInput());
            throw pex;
        }
        return parserInstance.parseExpression(getCompilerContext());
    }
}