package au.com.illyrian.bnf;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.bnf.ast.BnfTree;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.ModuleContext;

public class BnfParserActionTest extends TestCase
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

    public void testSimpleExpression() throws Exception
    {
        out.println("{");
        out.println("   $2");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        compile.visit(parser);
        parser.nextToken();
        BnfTree tree = parser.rule_action();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        String expect = "{ $2 }";
        assertNotNull("Should not be null:", tree);
        assertEquals("AST", expect, tree.toString());
    }

    public void testMethodCall() throws Exception
    {
        out.println("{");
        out.println("   ast.Declare($2, $4, $6)");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        compile.visit(parser);
        parser.nextToken();
        BnfTree tree = parser.rule_action();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        String expect = "{ ast.Declare($2, $4, $6) }";
        assertNotNull("Should not be null:", tree);
        assertEquals("AST", expect, tree.toString());
    }

    public void testSimpleNew() throws Exception
    {
        out.println("{");
        out.println("   new AstDeclareVariable($2, $4, $6)");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        compile.visit(parser);
        parser.nextToken();
        BnfTree tree = parser.rule_action();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        String expect = "{ new AstDeclareVariable($2, $4, $6) }";
        assertNotNull("Should not be null:", tree);
        assertEquals("AST", expect, tree.toString());
    }


}
