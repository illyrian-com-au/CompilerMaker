package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.CompileUnit;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParseExpression;
import au.com.illyrian.parser.ParseMember;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.ParseStatement;
import au.com.illyrian.parser.ParserException;

public class InvokeParserImpl<T> extends ParserBase implements InvokeParser<T>
{
    private CompileUnit compileUnit = null;
    
    public InvokeParserImpl()
    {
        super();
    }
    
    public InvokeParserImpl(CompileUnit compileUnit)
    {
        super();
        setCompileUnit(compileUnit);
    }
    
    public CompileUnit getCompileUnit()
    {
        if (compileUnit == null)
            throw new NullPointerException("compileUnit is null");
        return compileUnit;
    }

    public void setCompileUnit(CompileUnit compileUnit)
    {
        this.compileUnit = compileUnit;
    }

    protected ClassLoader getClassLoader()
    {
        return getCompileUnit().getClassLoader();
    }
    
    protected Object loadParser(String parseName, Input input) throws ParserException
    {
        Object objectInstance = null;
        try {
            Class parserClass = getClassLoader().loadClass(parseName);
            objectInstance = parserClass.newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw error(input, "Could not load parser: " + parseName);
        } catch (IllegalAccessException iae) {
            throw error(input, "Could not access parser: " + parseName);
        } catch (InstantiationException iae) {
            throw error(input, "Could not instantiate parser: " + parseName);
        }            
        return objectInstance;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseModule(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseModule(String parseName, Input input) throws ParserException
    {
       ParseModule parserInstance = null;
       try {
           Object parser = loadParser(parseName, input);
           parserInstance = (ParseModule)parser;
       } catch (ClassCastException cce) {
           throw error(input, "Class does not implement the ParseModule interface: " + parseName);
       } catch (ParserException ex) {
           throw ex;
       } catch (Exception ex) {
           ParserException pex =  new ParserException("Error invoking parser: " + parseName, ex);
           pex.setParserStatus(input);
           throw pex;
       }
       T result = (T)parserInstance.parseModule();
       return result;
   }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseClass(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseClass(String parseName, Input input) throws ParserException
    {
    	ParseClass parserInstance = null;
        try {
           Object parser = loadParser(parseName, input);
           parserInstance = (ParseClass)parser;
        } catch (ClassCastException cce) {
            throw error(input, "Class does not implement the ParseClass interface: " + parseName);
        } catch (ParserException ex) {
           throw ex;
        } catch (Exception ex) {
           ParserException pex =  new ParserException("Error invoking parser: " + parseName, ex);
           pex.setParserStatus(input);
           throw pex;
        }
        getCompileUnit().visitParser(parserInstance);
        T result = (T)parserInstance.parseClass();
        return result;
   }
    
    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseMember(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseMember(String parseName, Input input) throws ParserException
    {
        ParseMember parserInstance = null;
        try {
            Object parser = loadParser(parseName, input);
            parserInstance = (ParseMember)parser;
        } catch (ClassCastException cce) {
            throw error(input, "Class does not implement the ParseMember interface: " + parseName);
        } catch (ParserException ex) {
            throw ex;
        } catch (Exception ex) {
            ParserException pex =  new ParserException("Error invoking parser: " + parseName, ex);
            pex.setParserStatus(input);
            throw pex;
        }
        getCompileUnit().visitParser(parserInstance);
        T result = (T)parserInstance.parseMember();
        return result;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseStatement(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseStatement(String parseName, Input input) throws ParserException
    {
        ParseStatement parserInstance = null;
       try {
           Object parser = loadParser(parseName, input);
           parserInstance = (ParseStatement)parser;
       } catch (ClassCastException cce) {
           throw error(input, "Class does not implement the ParseStatement interface: " + parseName);
       } catch (ParserException ex) {
           throw ex;
       } catch (Exception ex) {
           ParserException pex =  new ParserException("Error invoking parser: " + parseName, ex);
           pex.setParserStatus(input);
           throw pex;
       }
       getCompileUnit().visitParser(parserInstance);
       T result = (T)parserInstance.parseStatement();
       return result;
   }
    
    /* (non-Javadoc)
     * @see au.com.illyrian.parser.impl.InvokeParser#invokeParseExpression(java.lang.String, au.com.illyrian.parser.Input)
     */
    public T invokeParseExpression(String parseName, Input input) throws ParserException
    {
        ParseExpression parserInstance = null;
       try {
           Object parser = loadParser(parseName, input);
           parserInstance = (ParseExpression)parser;
       } catch (ClassCastException cce) {
           throw error(input, "Class does not implement the ParseExpression interface: " + parseName);
       } catch (ParserException ex) {
           throw ex;
       } catch (Exception ex) {
           ParserException pex =  new ParserException("Error invoking parser: " + parseName, ex);
           pex.setParserStatus(input);
           throw pex;
       }
       getCompileUnit().visitParser(parserInstance);
       T result = (T)parserInstance.parseExpression();
       return result;
   }
}