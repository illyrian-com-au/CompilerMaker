package au.com.illyrian.dsl.pc;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;

public class PCParserTest extends TestCase
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

    public void testPCSequence() throws Exception
    {
        out.println("{");
        out.println("  Abilities: {Str:10 Int:17 Wis:7 Dex:9 Con:12 Cha:15}");
        out.println("  Name  : Haggar the 'orrable");
        out.println("  Race  : dwarf");
        out.println("  Class : fighter");
        out.println("  Sex   : male");
        out.println("  Alignment : lawful");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        PC parser = new PC();
        compile.visit(parser);
        parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
    }
/*
    public void testPCAlternative() throws Exception
    {
        out.println("{");
        out.println("   test ::= the | quick| brown|fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        PC parser = new PC();
        compile.visit(parser);
        parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
    }

    public void testPCTerminals() throws Exception
    {
        out.println("{");
        out.println("   test ::= the , \"quick\" | 'brown', fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        PC parser = new PC();
        compile.visit(parser);
        parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
    }
*/
//    public void testDomainParserPackage() throws Exception
//    {
//        out.println("package au.com.illyrian.dsl.pc;");
//        out.println("PC::{");
//        out.println("   a * b + c;");
//        out.println("}::PC");
//        Input input = new LexerInputStream(getReader(), null);
//        ModuleAction action = new TestModuleAction();
//
//        CompileModule compile = new CompileModule();
//        compile.setInput(input);
//        compile.getModuleParser().setModuleAction(action);
//        Object output = compile.parseModule();
//        //assertEquals("Output text", expected, output.toString());
//    }

}
