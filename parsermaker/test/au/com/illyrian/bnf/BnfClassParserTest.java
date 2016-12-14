package au.com.illyrian.bnf;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.domainparser.ClassAction;
import au.com.illyrian.domainparser.ClassParser;
import au.com.illyrian.domainparser.TestClassAction;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.ModuleContext;

public class BnfClassParserTest extends TestCase
{
    StringReader reader;
    StringWriter writer;
    PrintWriter  out;
    
    public void setUp()
    {
        writer = new StringWriter() ;
        out = new PrintWriter(writer);
    }
    
    public StringReader getReader()
    {
        return new StringReader(writer.toString());
    }

    public void testSimpleClassParser() throws Exception
    {
        out.println("import au.com.illyrian.bnf.BnfParser;");
        out.println("public class BnfParseTest");
        out.println("BnfParser::{");
        out.println("   test ::= the quick brown fox;");
        out.println("}::BnfParser");
        Input input = new LexerInputStream(getReader(), null);
        // FIXME - use AstClassAction
        //ClassAction action = new AstClassAction();
        ClassAction action = new TestClassAction();

        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        ClassParser parser = new ClassParser();
        compile.setModuleParser(parser);
        parser.setAction(action);
        Object output = compile.parseModule();
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        
        String expect = "import au.com.illyrian.bnf.BnfParser;\n"
                + "public class BnfParseTest {\n"
                + "test ::= the quick brown fox . ;\n"
                + "}";
        assertNotNull("Should not be null:", output);
        assertEquals("AST", expect, output.toString());
    }
}
