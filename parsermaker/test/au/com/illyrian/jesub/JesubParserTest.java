package au.com.illyrian.jesub;

import java.io.PrintWriter;
import java.io.StringReader;

import junit.framework.TestCase;
import au.com.illyrian.domainparser.ModuleParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;

public class JesubParserTest  extends TestCase
{
    StringReader reader;
    PrintWriter  out;
    
    JesubAction action = new JesubActionString();

    public void setUp()
    {
        out = new PrettyPrintWriter();
    }
    
    public StringReader getReader()
    {
        return new StringReader(out.toString());
    }

	public void testJesubParser() throws Exception
	{
	    out.println("import au.com.illyrian.jesub.JesubParser;");
	    out.println("JesubParser::{");
	    out.println("   class test {}");
	    out.println("}::JesubParser");
	    PrettyPrintWriter expected = new PrettyPrintWriter();
	    expected.println("Import(\"au.com.illyrian.jesub.JesubParser\");");
	    expected.println("setSimpleClassName(\"test\");");
	    Input input = new LexerInputStream(getReader(), null);
	    ModuleParser parser = new ModuleParser();
	    parser.setModuleAction(action);
	    
	    CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertEquals("Output text", expected.toString(), output.toString());
	    expected.close();
	}

	public void testClassModifierPublic() throws Exception
    {
        out.println("import au.com.illyrian.jesub.JesubParser;");
        out.println("JesubParser::{");
        out.println("   public class test {}");
        out.println("}::JesubParser");
        PrettyPrintWriter expected = new PrettyPrintWriter();
        expected.println("Import(\"au.com.illyrian.jesub.JesubParser\");");
        expected.println("setModifiers(\"public \");");
        expected.println("setSimpleClassName(\"test\");");
        Input input = new LexerInputStream(getReader(), null);
        ModuleParser parser = new ModuleParser();
        parser.setModuleAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertEquals("Output text", expected.toString(), output.toString());
	    expected.close();
    }

    public void testClassModifierMany() throws Exception
    {
        out.println("import au.com.illyrian.jesub.JesubParser;");
        out.println("JesubParser::{");
        out.println("    public protected abstract final strictfp class test {}");
        out.println("}::JesubParser");
        PrettyPrintWriter expected = new PrettyPrintWriter();
        expected.println("Import(\"au.com.illyrian.jesub.JesubParser\");");
        expected.println("setModifiers(\"public protected abstract final strictfp \");");
        expected.println("setSimpleClassName(\"test\");");
        Input input = new LexerInputStream(getReader(), null);
        ModuleParser parser = new ModuleParser();
        parser.setModuleAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertEquals("Output text", expected.toString(), output.toString());
	    expected.close();
    }

    public void testClassExtends() throws Exception
    {
        out.println("import au.com.illyrian.jesub.JesubParser;");
        out.println("JesubParser::{");
        out.println("    public class test extends BaseClass{}");
        out.println("}::JesubParser");
        PrettyPrintWriter expected = new PrettyPrintWriter();
        expected.println("Import(\"au.com.illyrian.jesub.JesubParser\");");
        expected.println("setModifiers(\"public \");");
        expected.println("setSimpleClassName(\"test\");");
        expected.println("Extends(\"BaseClass\");");
        Input input = new LexerInputStream(getReader(), null);
        ModuleParser parser = new ModuleParser();
        parser.setModuleAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertEquals("Output text", expected.toString(), output.toString());
	    expected.close();
    }

    public void testClassImplements() throws Exception
    {
        out.println("import au.com.illyrian.jesub.JesubParser;");
        out.println("JesubParser::{");
        out.println("    public class test implements Runnable {}");
        out.println("}::JesubParser");
        PrettyPrintWriter expected = new PrettyPrintWriter();
        expected.println("Import(\"au.com.illyrian.jesub.JesubParser\");");
        expected.println("setModifiers(\"public \");");
        expected.println("setSimpleClassName(\"test\");");
        expected.println("Implements(\"Runnable\");");
        Input input = new LexerInputStream(getReader(), null);
        ModuleParser parser = new ModuleParser();
        parser.setModuleAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertEquals("Output text", expected.toString(), output.toString());
	    expected.close();
    }

    public void testClassImplementsMany() throws Exception
    {
        out.println("import au.com.illyrian.jesub.JesubParser;");
        out.println("JesubParser::{");
        out.println("    public class test implements Runnable, Assignable, Compatable {}");
        out.println("}::JesubParser");
        PrettyPrintWriter expected = new PrettyPrintWriter();
        expected.println("Import(\"au.com.illyrian.jesub.JesubParser\");");
        expected.println("setModifiers(\"public \");");
        expected.println("setSimpleClassName(\"test\");");
        expected.println("Implements(\"Runnable\");");
        expected.println("Implements(\"Assignable\");");
        expected.println("Implements(\"Compatable\");");
        Input input = new LexerInputStream(getReader(), null);
        ModuleParser parser = new ModuleParser();
        parser.setModuleAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertEquals("Output text", expected.toString(), output.toString());
	    expected.close();
    }

    public void testClassExtendsImplements() throws Exception
    {
        out.println("import au.com.illyrian.jesub.JesubParser;");
        out.println("JesubParser::{");
        out.println("    public class test extends BaseClass implements Runnable, Assignable, Compatable {}");
        out.println("}::JesubParser");
        PrettyPrintWriter expected = new PrettyPrintWriter();
        expected.println("Import(\"au.com.illyrian.jesub.JesubParser\");");
        expected.println("setModifiers(\"public \");");
        expected.println("setSimpleClassName(\"test\");");
        expected.println("Extends(\"BaseClass\");");
        expected.println("Implements(\"Runnable\");");
        expected.println("Implements(\"Assignable\");");
        expected.println("Implements(\"Compatable\");");
        Input input = new LexerInputStream(getReader(), null);
        ModuleParser parser = new ModuleParser();
        parser.setModuleAction(action);
        
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.setModuleParser(parser);
        Object output = compile.parseModule();
        assertEquals("Output text", expected.toString(), output.toString());
	    expected.close();
    }

//    public void testClassImplementsRunnable() throws Exception
//    {
//        out.println("import au.com.illyrian.jesub.JesubParser;");
//        out.println("JesubParser::{");
//        out.println("    public class test implements Runnable {");
//        out.println("        public abstract void run();");
//        out.println("    }");
//        out.println("}::JesubParser");
//        PrettyPrintWriter expected = new PrettyPrintWriter();
//        expected.println("Import(\"au.com.illyrian.jesub.JesubParser\");");
//        expected.println("setModifiers(\"public \");");
//        expected.println("setSimpleClassName(\"test\");");
//        expected.println("Implements(\"Runnable\");");
//        expected.println("Method(\"run\", ACC_PUBLIC | ACC_ABSTRACT);");
//        expected.println("Begin();");
//        expected.println("End();");
//        Input input = new LexerInputStream(getReader(), null);
//        ModuleParser parser = new ModuleParser();
//        parser.setModuleAction(action);
//        
//        CompileModule compile = new CompileModule();
//        compile.setInput(input);
//        compile.setModuleParser(parser);
//        Object output = compile.parseModule();
//        assertEquals("Output text", expected.toString(), output.toString());
//    }
    
    public void testAccessModifierOrClassException() throws Exception
    {
        out.println("import au.com.illyrian.jesub.JesubParser;");
        out.println("JesubParser::{");
        out.println("    int a;");
        Input input = new LexerInputStream(getReader(), "Test.dat");
        ModuleParser parser = new ModuleParser();
        parser.setModuleAction(action);
        
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
            assertEquals("ParserException message: ", "Access modifier or class expected.", pex.getMessage());
            assertEquals("ParserException filename: ", "Test.dat", pex.getSourceFilename());
            assertEquals("ParserException line number: ", 3, pex.getLineNumber());
        }
    }

}
