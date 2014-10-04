package au.com.illyrian.domainparser;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;

public class ModuleParserTest  extends TestCase
{
    StringReader reader;
    StringWriter writer;
    PrintWriter  out;
    
    ModuleAction action = new TestModuleAction();

    public void setUp()
    {
        writer = new StringWriter() ;
        out = new PrintWriter(writer);
    }
    
    public StringReader getReader()
    {
        return new StringReader(writer.toString());
    }

    public void testTokenParser() throws Exception
    {
        out.println("{");
        out.println("   a * b + c;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        
        TestTokenParser parser = new TestTokenParser();
        parser.setInput(input);
        parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
    }

    public void testNestedTokenParser() throws Exception
    {
        out.println("{");
        out.println("   able");
        out.println("   {");
        out.println("       baker");
        out.println("   }");
        out.println("   charlie");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        
        TestTokenParser parser = new TestTokenParser();
        parser.setInput(input);
        parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
    }

	public void testDomainParser() throws Exception
	{
	    out.println("import au.com.illyrian.domainparser.TestTokenParser;");
	    out.println("TestTokenParser::{");
	    out.println("   a * b + c;");
	    out.println("}::TestTokenParser");
	    String expected = "(import au.com.illyrian.domainparser.TestTokenParser TestTokenParser) null";
	    Input input = new LexerInputStream(getReader(), null);

	    CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);
        Object output = compile.parseModule();
        assertEquals("Output text", expected, output.toString());
	}

    public void testFullyQualifiedDomainParser() throws Exception
    {
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        String expected = "null";
        Input input = new LexerInputStream(getReader(), null);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);
        Object output = compile.parseModule();
        assertEquals("Output text", expected, output.toString());
    }

    public void testDomainParserPackage() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        Input input = new LexerInputStream(getReader(), null);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);
        Object output = compile.parseModule();
        //assertEquals("Output text", expected, output.toString());
    }
    
    public void testNoClassCalledException() throws Exception
    {
        out.println("TestTokenParser::{");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);

        try {
            compile.parseModule();
            fail("ParserExceptoion expected.");
        } catch (ParserException pex) {
            assertEquals("ParserException ", "Could not load parser: TestTokenParser", pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 1, pex.getLineNumber());
        }
	}

    public void testUnexpectedEndOfInputException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("TestTokenParser::{");
        
        Input input = new LexerInputStream(getReader(), "Test.dat");
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);
        try {
            compile.parseModule();
            fail("ParserException expected.");
        } catch (ParserException pex) {
            assertEquals("ParserException ", "Unexpected end of input", pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 2, pex.getLineNumber());
            String expected = "Test.dat;2\nUnexpected end of input";
            assertEquals("ParserException", expected, pex.toString());
        }
    }

    public void testDoesNotImplementParseMemberException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestModuleAction;");
        out.println("TestModuleAction::{");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);
        try
        {
            compile.parseModule();
            fail("ParserException expected.");
        }
        catch (ParserException pex)
        {
            String msg = "Class does not implement the ParseClass interface: "
                    + "au.com.illyrian.domainparser.TestModuleAction";
            assertEquals("ParserException ", msg, pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 2, pex.getLineNumber());
        }
    }

    public void testEndOfParserSpaceException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::DummyParser");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);
        try {
            compile.parseModule();
            fail("ParserException expected.");
        } catch (ParserException pex) {
            assertEquals("ParserException ", "'TestTokenParser' expected at end of parser space", pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 4, pex.getLineNumber());
            String expected = "Test.dat;4\n'TestTokenParser' expected at end of parser space";
            assertEquals("ParserException", expected, pex.toString());
        }
    }

    public void testExpectEndOfInputException() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::TestTokenParser");
        out.println("   a * b + c;");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);
        try {
            compile.parseModule();
            fail("ParserException expected.");
        } catch (ParserException pex) {
            assertEquals("ParserException ", "End of input expected", pex.getMessage());
            assertEquals("ParserException ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException ", 5, pex.getLineNumber());
            String expected = "Test.dat;5:3\nEnd of input expected";
            assertEquals("ParserException", expected, pex.toString());
        }
    }
}
