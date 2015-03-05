package au.com.illyrian.domainparser;

import java.io.PrintWriter;
import java.io.StringReader;

import junit.framework.TestCase;
import au.com.illyrian.jesub.PrettyPrintWriter;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;

public class ClassParserTest  extends TestCase
{
    StringReader reader;
    PrintWriter  out;
    
    ClassAction action = new TestClassAction();

    public void setUp()
    {
        out = new PrettyPrintWriter();
    }
    
    public StringReader getReader()
    {
        return new StringReader(out.toString());
    }

	public void testClassParser() throws Exception
	{
	    out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("class test");
	    out.println("TestTokenParser::{");
	    out.println("   a * b + c;");
	    out.println("}::TestTokenParser");
	    String expected = "(import au.com.illyrian.domainparser.TestTokenParser) "
	                    + "(class test) ";
	    Input input = new LexerInputStream(getReader(), null);
	    ClassParser parser = new ClassParser();
	    parser.setClassAction(action);
	    
	    CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertNotNull("parser output is null", output);
        assertEquals("Output text", expected, output.toString());
	}

    public void testClassModifierPublic() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("public class test");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::TestTokenParser");
        String expected = "(import au.com.illyrian.domainparser.TestTokenParser) "
                        + "(modifiers public ) (class test) ";
        Input input = new LexerInputStream(getReader(), null);
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertNotNull("parser output is null", output);
        assertEquals("Output text", expected, output.toString());
    }

    public void testClassModifierMany() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("public protected abstract final strictfp class test");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::TestTokenParser");
        String expected = "(import au.com.illyrian.domainparser.TestTokenParser) "
                        + "(modifiers public protected abstract final strictfp ) (class test) ";
        Input input = new LexerInputStream(getReader(), null);
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertNotNull("parser output is null", output);
        assertEquals("Output text", expected, output.toString());
    }

    public void testClassExtends() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("class Test extends BaseClass");
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        String expected = "(package au.com.illyrian.domainparser) "
                        + "(class Test) (extends BaseClass) ";
        Input input = new LexerInputStream(getReader(), null);
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertNotNull("parser output is null", output);
        assertEquals("Output text", expected, output.toString());
    }

    public void testClassImplements() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("class Test implements BaseIface");
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        String expected = "(package au.com.illyrian.domainparser) "
                        + "(class Test) (implements BaseIface) ";
        Input input = new LexerInputStream(getReader(), null);
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertNotNull("parser output is null", output);
        assertEquals("Output text", expected, output.toString());
    }

    public void testClassImplementsMany() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("class Test implements BaseIface, AltIface, OptIface");
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        String expected = "(package au.com.illyrian.domainparser) "
                        + "(class Test) (implements BaseIface) (implements AltIface) (implements OptIface) ";
        Input input = new LexerInputStream(getReader(), null);
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertNotNull("parser output is null", output);
        assertEquals("Output text", expected, output.toString());
    }

    public void testClassExtendsImplements() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("class Test extends BaseClass implements BaseIface");
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        String expected = "(package au.com.illyrian.domainparser) "
                        + "(class Test) (extends BaseClass) (implements BaseIface) ";
        Input input = new LexerInputStream(getReader(), null);
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertNotNull("parser output is null", output);
        assertEquals("Output text", expected, output.toString());
    }

    public void testNoClassCalledException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("TestTokenParser::{");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);

        try
        {
            compile.parseModule();
            fail("ParserException expected.");
        }
        catch (ParserException pex)
        {
            assertEquals("ParserException ", "import or class expected.", pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 2, pex.getLineNumber());
        }
    }

    public void testDoesNotImplementParseMemberException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestModuleAction;");
        out.println("class Test");
        out.println("TestModuleAction::{");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);

        try
        {
            compile.parseModule();
            fail("ParserException expected.");
        }
        catch (ParserException pex)
        {
            String msg = "Class does not implement the ParseMember interface: "
                    + "au.com.illyrian.domainparser.TestModuleAction";
            assertEquals("ParserException ", msg, pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 3, pex.getLineNumber());
        }
    }

    public void testUnexpectedEndOfInputException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("class Test");
        out.println("TestTokenParser::{");
        
        Input input = new LexerInputStream(getReader(), "Test.dat");
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        try {
            compile.parseModule();
            fail("ParserException expected.");
        } catch (ParserException pex) {
            assertEquals("ParserException ", "Unexpected end of input", pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 3, pex.getLineNumber());
            String expected = "Test.dat;3\nUnexpected end of input";
            assertEquals("ParserException", expected, pex.toString());
        }
    }

    public void testEndOfParserSpaceException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("class Test");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::DummyParser");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        try {
            compile.parseModule();
            fail("ParserException expected.");
        } catch (ParserException pex) {
            assertEquals("ParserException ", "'TestTokenParser' expected at end of parser space", pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 5, pex.getLineNumber());
            String expected = "Test.dat;5\n'TestTokenParser' expected at end of parser space";
            assertEquals("ParserException", expected, pex.toString());
        }
    }

    public void testExpectEndOfInputException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("class Test");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::TestTokenParser");
        out.println("   a * b + c;");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        ClassParser parser = new ClassParser();
        parser.setClassAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        try {
            compile.parseModule();
            fail("ParserException expected.");
        } catch (ParserException pex) {
            assertEquals("ParserException ", "End of input expected", pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 6, pex.getLineNumber());
        }
    }
}
