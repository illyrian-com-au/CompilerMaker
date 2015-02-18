package au.com.illyrian.dsl.ast;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.dsl.DSL;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;

public class DslParseAstVisitorTest extends TestCase
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

    public void testDSLSequence() throws Exception
    {
        out.println("{");
        out.println("   expr ::= \"the\", \"brown\", fox;");
        out.println("   fox  ::= \"fox\";");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        DSL parser = new DSL();
        compile.visit(parser);
        DslActionAstFactory action = new DslActionAstFactory();
        parser.setDslAction(action);
        DslLanguage result = (DslLanguage)parser.parseClass();
        DslAstVisitor visitor = new DslAstVisitor();
        result.sequence(visitor);
        String output = visitor.toString();
//        assertEquals("", output);
    }

    public void testDSLAltIdentifiers() throws Exception
    {
        out.println("{");
        out.println("   alts ::= \"the\" | \"quick\" | \"brown\" | \"fox\";");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        DSL parser = new DSL();
        compile.visit(parser);
        DslActionAstFactory action = new DslActionAstFactory();
        parser.setDslAction(action);
        DslLanguage result = (DslLanguage)parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        DslAstVisitor visitor = new DslAstVisitor();
        result.sequence(visitor);
        String output = visitor.toString();
//        assertEquals("", output);
    }

    public void testDSLAltExpressions() throws Exception
    {
        out.println("{");
        out.println("   alts ::= the | quick | brown | fox;");
        out.println("   the ::= \"the\";");
        out.println("   quick ::= \"quick\";");
        out.println("   brown ::= \"brown\";");
        out.println("   fox ::= \"fox\";");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        DSL parser = new DSL();
        compile.visit(parser);
        DslActionAstFactory action = new DslActionAstFactory();
        parser.setDslAction(action);
        DslLanguage result = (DslLanguage)parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        DslAstVisitor visitor = new DslAstVisitor();
        result.sequence(visitor);
        String output = visitor.toString();
//        assertEquals("", output);
    }

    public void testDSLOptional() throws Exception
    {
        out.println("{");
        out.println("   expr ::= \"the\", [ \"brown\" ], [ fox ];");
        out.println("   fox  ::= \"fox\";");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        DSL parser = new DSL();
        compile.visit(parser);
        DslActionAstFactory action = new DslActionAstFactory();
        parser.setDslAction(action);
        DslLanguage result = (DslLanguage)parser.parseClass();
        DslAstVisitor visitor = new DslAstVisitor();
        result.sequence(visitor);
        String output = visitor.toString();
//        assertEquals("", output);
    }


    public void testDSLSeqAlt() throws Exception
    {
        out.println("{");
        out.println("   test ::= the , quick | brown, fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        DSL parser = new DSL();
        compile.visit(parser);
        DslActionAstFactory action = new DslActionAstFactory();
        parser.setDslAction(action);
        Object result = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        String output = "" + result;
        assertEquals("test ::= the, (quick | brown), fox\n", output);
    }

    public void testDSLTerminals() throws Exception
    {
        out.println("{");
        out.println("   test ::= the , \"quick\", 'brown', fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        DSL parser = new DSL();
        compile.visit(parser);
        DslActionAstFactory action = new DslActionAstFactory();
        parser.setDslAction(action);
        Object result = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        String output = "" + result;
        assertEquals("test ::= the, \"quick\", \'brown\', fox\n", output);
    }

    /*
    public void testDomainParserPackage() throws Exception
    {
        out.println("import au.com.illyrian.dsl.DSL;");
        out.println("DSL::{");
        out.println("class_body  ::= '{', abilities, char_attrs, '}' ;");
        out.println("char_attr   ::= name_attr, race_attr, class_attr, sex_attr, align_attr, abilities;");
        out.println("name_attr   ::= \"Name\", ':', IDENTIFIER;");
        out.println("race_attr   ::= \"Race\", ':', (\"human\" | \"elf\" | \"dwarf\" | \"halfling\");");
        out.println("class_attr  ::= \"Class\", ':', (\"fighter\" | \"magic-user\" | \"cleric\" | \"anti-cleric\" | \"thief\");");
        //out.println("| error("Expected Class : one of ...")");
        out.println("sex_attr    ::= \"Sex\", ':', (\"male\" | \"female\");");
        out.println("align_attr  ::= \"Alignment\", ':', (\"lawful\" | \"neutral\" | \"chaotic\");");
        out.println("abilities   ::= \"Abilities\", ':', '{', many_abilities, '}';");
        out.println("many_abilities ::= str_attr, int_attr, wis_attr, dex_attr, con_attr, cha_attr;");
        out.println("str_attr    ::= \"Strength\", ':', ability_score;");
        out.println("int_attr    ::= \"Intelligence\", ':', ability_score;");
        out.println("wis_attr    ::= \"Wisdom\", ':', ability_score;");
        out.println("dex_attr    ::= \"Dexterity\", ':', ability_score;");
        out.println("con_attr    ::= \"Constitution\", ':', ability_score;");
        out.println("cha_attr    ::= \"Charisma\", ':', ability_score;");
        out.println("ability_score ::= INTEGER;");
        out.println("}::DSL");
        Input input = new LexerInputStream(getReader(), null);
        ModuleAction action = new TestModuleAction();

        CompileModule compile = new CompileModule();
        compile.setInput(input);
        compile.getModuleParser().setModuleAction(action);
        Object output = compile.parseModule();
        String expected = "";
        //assertEquals("Output text", expected, output.toString());
    }
*/
}
