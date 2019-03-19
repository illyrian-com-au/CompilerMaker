package au.com.illyrian.bnf;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.bnf.ast.BnfMergeVisitor;
import au.com.illyrian.bnf.ast.BnfTree;
import au.com.illyrian.bnf.parser.BnfParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.ModuleContext;

public class BnfParserTest extends TestCase
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
        out.println("   test ::= the quick brown fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        parser.addReserved("the");
        ////compile.visit(parser);
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        
        String expect = "test ::= the quick brown fox . ;";
        assertNotNull("Should not be null:", tree);
        assertEquals("AST", expect, tree.toString());
    }

    public void testAlternatives1() throws Exception
    {
        out.println("{");
        out.println("   test ::= the| quick brown |fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        parser.addReserved("the");
        //compile.visit(parser);
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        
        assertNotNull("Should not be null:", tree);
        String expect = "test ::= ( the . | quick brown . | fox . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testAlternatives2() throws Exception
    {
        out.println("{");
        out.println("   test ::= the quick | RECOVER(brown);");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        parser.addReserved("the");
        //compile.visit(parser);
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        
        assertNotNull("Should not be null:", tree);
        String expect = "test ::= ( the quick . | RECOVER(brown) . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testSeqAlt() throws Exception
    {
        out.println("{");
        out.println("   test ::= the quick | brown fox;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        parser.addReserved("the");
        //compile.visit(parser);
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        
        assertNotNull("Should not be null:", tree);
        String expect = "test ::= ( the quick . | brown fox . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testPackage() throws Exception
    {
        out.println("{");
        out.println("   package_opt ::= PACKAGE qualified_name SEMI ");
        out.println("       |   PACKAGE qualified_name error(\"IncompletePackageName\") ");
        out.println("       |   EMPTY ");
        out.println("       |   RECOVER(class_declaration) ;");
        out.println("}");
        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        parser.addReserved("PACKAGE");
        parser.addReserved("EMPTY");
        parser.addReserved("SEMI");
        parser.addReserved("IDENTIFIER");
        //compile.visit(parser);
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        BnfMergeVisitor merger = new BnfMergeVisitor();
        BnfTree merged = tree.resolveMerge(merger);

        String expect = "package_opt ::= ( PACKAGE qualified_name ( SEMI . "
                + "| error(\"IncompletePackageName\") . ) "
                + "| EMPTY . | RECOVER(class_declaration) . ) ;";
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
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        parser.addReserved("IMPORT");
        parser.addReserved("EMPTY");
        parser.addReserved("DOT");
        parser.addReserved("SEMI");
        parser.addReserved("MULT");
        parser.addReserved("IDENTIFIER");
        //compile.visit(parser);
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        BnfMergeVisitor merger = new BnfMergeVisitor();
        BnfTree newtree = tree.resolveMerge(merger);

        writer = new StringWriter() ;
        out = new PrintWriter(writer);
        out.println("import_mult ::= ( IMPORT import_path() import_mult() . | . ) ;");
        out.println("import_path ::= ( name() ( DOT import_path() . | SEMI . ) | MULT SEMI . | error(\"IncpmpleteImportPath\") . ) ;");
        out.print("name ::= IDENTIFIER . ;");
        assertEquals("AST", writer.toString(), newtree.toString());
    }

    
    public void testMemberMult() throws Exception
    {
        out.println("{");
        out.println("   member_mult ::= member member_mult");
        out.println("                 | /*EMPTY*/");
        out.println("                 | RECOVER(member) ; ");
        out.println("   member ::= modifier_mult method_type IDENTIFIER LPAR formal_mult RPAR method_body");
        out.println("            | modifier_mult method_type IDENTIFIER SEMI ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        parser.addReserved("LPAR");
        parser.addReserved("RPAR");
        parser.addReserved("SEMI");
        parser.addReserved("IDENTIFIER");
        //compile.visit(parser);
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        BnfMergeVisitor merger = new BnfMergeVisitor();
        BnfTree newtree = tree.resolveMerge(merger);

        writer = new StringWriter() ;
        out = new PrintWriter(writer);
        out.println("member_mult ::= ( member() member_mult() . | . | RECOVER(member) . ) ;");
        out.print("member ::= modifier_mult method_type IDENTIFIER ( LPAR formal_mult RPAR method_body . | SEMI . ) ;");
        assertEquals("AST", writer.toString(), newtree.toString());
    }

    
    public void testFunctions() throws Exception
    {
        out.println("{");
        out.println("   function_opt ::= error(\"Got it wrong\")");
        out.println("                 | LOOKAHEAD(IDENTIFIER COLON) ");
        out.println("                 | LOOKAHEAD(SEMI) ");
        out.println("                 | RECOVER(member) ");
        out.println("                 | expression(0) ");
        out.println("                 | /*EMPTY*/ ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);

        String expect = "function_opt ::= ( error(\"Got it wrong\") . | LOOKAHEAD(IDENTIFIER COLON) . "
                + "| LOOKAHEAD(SEMI) . | RECOVER(member) . | expression(0) . | . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

}
