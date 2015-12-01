package au.com.illyrian.compiler.ast;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.compiler.RecursiveParser;
import au.com.illyrian.compiler.ast.AstMergeVisitor;
import au.com.illyrian.compiler.ast.AstParser;
import au.com.illyrian.compiler.ast.AstParserFactory;
import au.com.illyrian.compiler.ast.AstParserName;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;

public class AstMergeVisitorTest extends TestCase
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

    public void testGroupCommon() throws Exception
    {
        AstParserFactory fac = new AstParserFactory();
        AstParser alt4 = fac.Alt(new AstParserName("dog"), new AstParserName("fox"));
        AstParser alt3 = fac.Alt(new AstParserName("fox"), alt4);
        AstParser alt2 = fac.Alt(new AstParserName("dog"), alt3);
        AstParser alt1 = fac.Alt(new AstParserName("fox"), alt2);
        assertNotNull("Should not be null:", alt2);
        assertEquals("AST", "( fox | dog | fox | dog | fox )", alt1.toString());
        
        AstMergeVisitor merge = new AstMergeVisitor();
        AstParser [] group = merge.groupCommonHeads(alt1.toAltArray());
        
        int i = 0;
        assertEquals("fox", group[i++].toString());
        assertEquals("fox", group[i++].toString());
        assertEquals("fox", group[i++].toString());
        assertEquals("dog", group[i++].toString());
        assertEquals("dog", group[i++].toString());
    }

    public void testMergeStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the fox | the dog ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= the ( fox . | dog . ) ;";
        assertEquals("AST", expect, newtree.toString());
    }


    public void testMergeTwoSeqentialStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the brown fox | the brown dog ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= the brown ( fox . | dog . ) ;";
        assertEquals("AST", expect, newtree.toString());
    }

    public void testMergeThreeAlternativeStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the fox | the dog | the squirrel ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= the ( fox . | dog . | squirrel . ) ;";
        assertEquals("AST", expect, newtree.toString());
    }

   
    public void testMergeTwoOneStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= quick fox | quick dog | nutty squirrel ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= ( quick ( fox . | dog . ) | nutty squirrel . ) ;";
        assertEquals("AST", expect, newtree.toString());
    }

    public void testMergeOneTwoStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= nutty squirrel | quick fox | quick dog ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= ( nutty squirrel . | quick ( fox . | dog . ) ) ;";
        assertEquals("AST", expect, newtree.toString());
    }

    public void testMergeTreeOneTwoStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the nutty squirrel | the quick fox | the quick dog ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= the ( nutty squirrel . | quick ( fox . | dog . ) ) ;";
        assertEquals("AST", expect, newtree.toString());
    }

    public void testMergeManyStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the quick brown fox");
        out.println("         |   the quick brown dog");
        out.println("         |   the quick red squirrel");
        out.println("         |   the slow moving snail");
        out.println("         ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= the ( quick ( brown ( fox . | dog . ) | red squirrel . ) | slow moving snail . ) ;";
        assertEquals("AST", expect, newtree.toString());
    }

    public void testMergeManyStates2() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the slow moving snail");
        out.println("         |   the quick red squirrel");
        out.println("         |   the quick brown dog");
        out.println("         |   the quick brown fox");
        out.println("         ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= the ( slow moving snail . | quick ( red squirrel . | brown ( dog . | fox . ) ) ) ;";
        assertEquals("AST", expect, newtree.toString());
    }

    public void testMergeShortStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= squirrel | squirrel eating ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= squirrel ( . | eating . ) ;";
        assertEquals("AST", expect, newtree.toString());
    }
    
    public void testMergeOptionalStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= squirrel eating | ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= ( squirrel eating . | . ) ;";
        assertEquals("AST", expect, newtree.toString());
    }
    
    public void testMergeManyStates3() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the slow snail sleeping");
        out.println("         |   the squirrel climbing");
        out.println("         |   the slow snail");
        out.println("         |   the slow snail moving");
        out.println("         |   the squirrel");
        out.println("         ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);
        assertNotNull("Should not be null:", newtree);
        String expect = "rhyme ::= the ( slow snail ( sleeping . | . | moving . ) | squirrel ( climbing . | . ) ) ;";
        assertEquals("AST", expect, newtree.toString());
    }
    
}
