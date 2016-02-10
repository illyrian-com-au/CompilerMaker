package au.com.illyrian.compiler;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.compiler.ast.AstMergeVisitor;
import au.com.illyrian.compiler.ast.AstParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;

public class RecursiveParserTest extends TestCase
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

    public void testSimpleSequence() throws Exception
    {
        out.println("{");
        out.println("   test ::= the \"quick\" brown fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        parser.addReserved("the");
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        String expect = "test ::= <the> \"quick\" brown fox . ;";
        assertNotNull("Should not be null:", tree);
        assertEquals("AST", expect, tree.toString());
    }

    public void testAlternatives1() throws Exception
    {
        out.println("{");
        out.println("   test ::= the| \"quick\" brown |fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        parser.addReserved("the");
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        assertNotNull("Should not be null:", tree);
        String expect = "test ::= ( <the> . | \"quick\" brown . | fox . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testAlternatives2() throws Exception
    {
        out.println("{");
        out.println("   test ::= the \"quick\" | fox(brown);");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        parser.addReserved("the");
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        assertNotNull("Should not be null:", tree);
        String expect = "test ::= ( <the> \"quick\" . | fox(brown) . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testPackage() throws Exception
    {
        out.println("{");
        out.println("   package_opt ::= PACKAGE qualified_name SEMI ");
        out.println("       |   PACKAGE qualified_name error(\"IncompletePackageName\") ");
        out.println("       |   empty ");
        out.println("       |   recover(class_declaration) ;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        parser.addReserved("PACKAGE");
        parser.addReserved("EMPTY");
        parser.addReserved("SEMI");
        parser.addReserved("IDENTIFIER");
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser merged = tree.resolveMerge(merger);

        String expect = "package_opt ::= ( <PACKAGE> qualified_name ( <SEMI> . "
                + "| error(\"IncompletePackageName\") . ) "
                + "| empty . | recover(class_declaration) . ) ;";
        assertEquals("AST", expect, merged.toString());
    }

    public void testImport() throws Exception
    {
        out.println("{");
        out.println("   import_mult    ::= IMPORT import_path import_mult");
        out.println("                  |   /*EMPTY*/ ;");
        out.println("   import_path    ::= name DOT import_path");
        out.println("                  |   name SEMI");
        out.println("                  |   MULT SEMI");
        out.println("                  |   error(\"IncpmpleteImportPath\") ;");
        out.println("   name           ::= IDENTIFIER ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        parser.addReserved("IMPORT");
        parser.addReserved("EMPTY");
        parser.addReserved("DOT");
        parser.addReserved("SEMI");
        parser.addReserved("MULT");
        parser.addReserved("IDENTIFIER");
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);

        writer = new StringWriter() ;
        out = new PrintWriter(writer);
        out.println("import_mult ::= ( <IMPORT> import_path import_mult . | . ) ;");
        out.println("import_path ::= ( name ( <DOT> import_path . | <SEMI> . ) | <MULT> <SEMI> . | error(\"IncpmpleteImportPath\") . ) ;");
        out.print("name ::= <IDENTIFIER> . ;");
        assertEquals("AST", writer.toString(), newtree.toString());
    }

    
    public void testMemberMult() throws Exception
    {
        out.println("{");
        out.println("   member_mult ::= member member_mult");
        out.println("                 | /*EMPTY*/");
        out.println("                 | recover(member) ; ");
        out.println("   member ::= modifier_mult method_type IDENTIFIER LPAR formal_mult RPAR method_body");
        out.println("            | modifier_mult method_type IDENTIFIER SEMI ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        parser.addReserved("LPAR");
        parser.addReserved("RPAR");
        parser.addReserved("SEMI");
        parser.addReserved("IDENTIFIER");
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        AstMergeVisitor merger = new AstMergeVisitor();
        AstParser newtree = tree.resolveMerge(merger);

        writer = new StringWriter() ;
        out = new PrintWriter(writer);
        out.println("member_mult ::= ( member member_mult . | . | recover(member) . ) ;");
        out.print("member ::= modifier_mult method_type <IDENTIFIER> ( <LPAR> formal_mult <RPAR> method_body . | <SEMI> . ) ;");
        assertEquals("AST", writer.toString(), newtree.toString());
    }

    
    public void testFunctions() throws Exception
    {
        out.println("{");
        out.println("   function_opt ::= error(\"Got it wrong\")");
        out.println("                 | lookahead(IDENTIFIER, COLON) ");
        out.println("                 | lookahead() ");
        out.println("                 | recover(member) ");
        out.println("                 | expression(0) ");
        out.println("                 | function(3.141) ");
        out.println("                 | /*EMPTY*/ ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        parser.addReserved("COLON");
        parser.addReserved("IDENTIFIER");
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);

        String expect = "function_opt ::= ( error(\"Got it wrong\") . | lookahead(<IDENTIFIER>, <COLON>) . "
                + "| lookahead() . | recover(member) . | expression(0) . | function(3.141) . | . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

}
