package au.com.illyrian.bnf.ast;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import junit.framework.TestCase;
import au.com.illyrian.bnf.parser.BnfParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

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
        BnfTreeFactory fac = new BnfTreeFactory(null);
        BnfTree alt4 = fac.Alt(fac.BnfName("dog"), fac.BnfName("fox"));
        BnfTree alt3 = fac.Alt(fac.BnfName("fox"), alt4);
        BnfTree alt2 = fac.Alt(fac.BnfName("dog"), alt3);
        BnfTree alt1 = fac.Alt(fac.BnfName("fox"), alt2);
        assertNotNull("Should not be null:", alt2);
        assertEquals("AST", "( fox | dog | fox | dog | fox )", alt1.toString());
        
        BnfMergeVisitor merge = new BnfMergeVisitor();
        BnfTree [] group = merge.groupCommonHeads(alt1.toAltArray());
        
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
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= the ( fox . | dog . ) ;";
        assertEquals("AST", expect, tree.toString());
    }


    public void testMergeTwoSeqentialStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the brown fox | the brown dog ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= the brown ( fox . | dog . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testMergeThreeAlternativeStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the fox | the dog | the squirrel ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= the ( fox . | dog . | squirrel . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

   
    public void testMergeTwoOneStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= quick fox | quick dog | nutty squirrel ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= ( quick ( fox . | dog . ) | nutty squirrel . ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testMergeOneTwoStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= nutty squirrel | quick fox | quick dog ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= ( nutty squirrel . | quick ( fox . | dog . ) ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testMergeTreeOneTwoStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= the nutty squirrel | the quick fox | the quick dog ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= the ( nutty squirrel . | quick ( fox . | dog . ) ) ;";
        assertEquals("AST", expect, tree.toString());
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
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= the ( quick ( brown ( fox . | dog . ) | red squirrel . ) | slow moving snail . ) ;";
        assertEquals("AST", expect, tree.toString());
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
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= the ( slow moving snail . | quick ( red squirrel . | brown ( dog . | fox . ) ) ) ;";
        assertEquals("AST", expect, tree.toString());
    }

    public void testMergeShortStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= squirrel | squirrel eating ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        // FIXME short option must appear last.
        String expect = "rhyme ::= squirrel ( eating . | . ) ;";
        assertEquals("AST", expect, tree.toString());
    }
    
    public void testMergeOptionalStates() throws Exception
    {
        out.println("{");
        out.println("   rhyme ::= squirrel eating | ;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= ( squirrel eating . | . ) ;";
        assertEquals("AST", expect, tree.toString());
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
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "rhyme ::= the ( slow snail ( sleeping . | moving . | . ) | squirrel ( climbing . | . ) ) ;";
        assertEquals("AST", expect, tree.toString());
    }
    
    public void testMergeImportStates() throws Exception
    {
        out.println("{");
        out.println("   import_path    ::= name DOT import_path");
        out.println("                  |   name SEMI");
        out.println("                  |   MULT SEMI");
        out.println("                  |   error(\"IncompleteImportPath\")");
        out.println("         ;");
        out.println("    name ::= IDENTIFIER;");
        out.println("}");

        Input input = new LexerInputStream(getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        String expect = "import_path ::= ( name() ( DOT import_path() . | SEMI . ) | MULT SEMI . | error(\"IncompleteImportPath\") . ) ;\n"
                      + "name ::= IDENTIFIER . ;";
        assertEquals("AST", expect, tree.toString());
    }
    
    public void testRuleLookup() throws Exception
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("   direction ::= north");
        out.println("               | east");
        out.println("               | south");
        out.println("               | west");
        out.println("               | error(\"WrongDirection\")");
        out.println("         ;");
        out.println("    north ::= NORTH | NORTH EAST | NORTH WEST;");
        out.println("    east  ::= EAST;");
        out.println("    south ::= SOUTH | SOUTH EAST | SOUTH WEST;");
        out.println("    west  ::= WEST;");
        out.println("}");
        out.close();

        Input input = new LexerInputStream(out.getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);

        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        assertNotNull("Cannot find first set for north", lookup.get("north"));
        BnfTreeRule north = lookup.get("north");
        assertEquals("north ::= NORTH ( EAST . | WEST . | . ) ;",  north.toString());
        assertNotNull("Cannot find first set for north", north.getFirstSet());
        assertEquals("first(north)=[NORTH]",  north.getFirstSet().toString());

        assertNotNull("Cannot find first set for east", lookup.get("east"));
        BnfTreeRule east = lookup.get("east");
        assertEquals("east ::= EAST . ;",  east.toString());
        assertNotNull("Cannot find first set for east",  east.getFirstSet());
        assertEquals("first(east)=[EAST]",  east.getFirstSet().toString());
        
        assertNotNull("Cannot find first set for south", lookup.get("south"));
        BnfTreeRule south = lookup.get("south");
        assertEquals("south ::= SOUTH ( EAST . | WEST . | . ) ;",  south.toString());
        assertNotNull("Cannot find first set for south", south.getFirstSet());
        assertEquals("first(south)=[SOUTH]",  south.getFirstSet().toString());
        
        assertNotNull("Cannot find first set for west", lookup.get("west"));
        BnfTreeRule west = lookup.get("west");
        assertEquals("west ::= WEST . ;",  west.toString());
        assertNotNull("Cannot find first set for west",  west.getFirstSet());
        assertEquals("first(west)=[WEST]",  west.getFirstSet().toString());
        
        assertNotNull("Cannot find first set for direction", lookup.get("direction"));
        BnfTreeRule direction = lookup.get("direction");
        String expected = "direction ::= ( north() . | east() . | south() . | west() . "
                + "| error(\"WrongDirection\") . ) ;";
        assertEquals(expected, direction.toString());
        assertNotNull("Cannot find first set for direction", direction.getFirstSet());
        assertEquals("first(direction)=[EAST, NORTH, SOUTH, WEST]",  direction.getFirstSet().toString());

    }
    
}
