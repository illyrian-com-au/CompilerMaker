package au.com.illyrian.compiler.ast;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import au.com.illyrian.compiler.RecursiveParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;
import junit.framework.TestCase;

public class AstFirstVisitorTest extends TestCase
{
    AstFirstVisitor visitor = new AstFirstVisitor();
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
    
    AstParser parse(StringReader reader) throws Exception {
        Input input = new LexerInputStream(reader, null);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        return tree;
    }

    public void testFirstSeq() throws Exception {
        out.println("{");
        out.println("   rhyme ::= the fox ;");
        out.println("}");
        AstParser tree = parse(getReader());
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        Set<String>rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertEquals("rhymeSet", "[the]", rhymeSet.toString());
    }

    public void testEmpty() throws Exception {
        out.println("{");
        out.println("   rhyme ::= /*EMPTY*/ ;");
        out.println("}");
        AstParser tree = parse(getReader());
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertTrue("hasEmpty should be false", hasEmpty);
        Set<String>rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertEquals("rhymeSet", "[<EMPTY>]", rhymeSet.toString());
    }

    public void testAlt() throws Exception {
        out.println("{");
        out.println("   rhyme ::= fox | dog | \"cat\" ;");
        out.println("}");
        AstParser tree = parse(getReader());
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        Set<String>rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertEquals("rhymeSet", "[fox, dog, \"cat\"]", rhymeSet.toString());
    }

    public void testMultipleRules() throws Exception {
        out.println("{");
        out.println("   rhyme ::= fox | dog | \"cat\" ;");
        out.println("   fox   ::= \"fox\";");
        out.println("   dog   ::= \"dog\";");
        out.println("}");
        AstParser tree = parse(getReader());
        
        AstParserRule [] rules = tree.toRuleArray();
        assertNotNull("tree.toRuleArray() should not be null", rules);
        assertEquals("rules.length", 3, rules.length);
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        Set<String>rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertEquals("rhymeSet", "[\"fox\", \"dog\", \"cat\"]", rhymeSet.toString());
    }

    public void testEmptySeqenceRules() throws Exception {
        out.println("{");
        out.println("   rhyme  ::= speed colour \"fox\" ;");
        out.println("   speed  ::= \"quick\" | /*EMPTY*/ ;");
        out.println("   colour ::= \"brown\" | /*EMPTY*/ ;");
        out.println("}");
        AstParser tree = parse(getReader());
        
        AstParserRule [] rules = tree.toRuleArray();
        assertNotNull("tree.toRuleArray() should not be null", rules);
        assertEquals("rules.length", 3, rules.length);
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        
        Set<String>rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertTrue("should contain quick: " + rhymeSet, rhymeSet.contains("\"quick\""));
        assertTrue("should contain brown: " + rhymeSet, rhymeSet.contains("\"brown\""));
        assertTrue("should contain fox: " + rhymeSet, rhymeSet.contains("\"fox\""));
        assertFalse("should not contain <EMPTY>: "   + rhymeSet, rhymeSet.contains(visitor.EMPTY));

        Set<String>speedSet = visitor.getSet("speed");
        assertNotNull("set(speed) should not be null", speedSet);
        assertTrue("should contain quick: " + speedSet, speedSet.contains("\"quick\""));
        assertFalse("should not contain brown: " + speedSet, speedSet.contains("\"brown\""));
        assertFalse("should not contain fox: " + speedSet, speedSet.contains("\"fox\""));
        assertTrue("should contain <EMPTY>: " + speedSet, speedSet.contains(visitor.EMPTY));

        Set<String>colourSet = visitor.getSet("colour");
        assertNotNull("set(colour) should not be null", colourSet);
        assertFalse("should not contain quick: " + colourSet, colourSet.contains("\"quick\""));
        assertTrue("should contain brown: " + colourSet, colourSet.contains("\"brown\""));
        assertFalse("should not contain fox: " + colourSet, colourSet.contains("\"fox\""));
        assertTrue("should contain <EMPTY>: " + colourSet, colourSet.contains(visitor.EMPTY));
    }

    public void testLeftRecursive() throws Exception {
        out.println("{");
        out.println("   dogs   ::= dogs dog;");
        out.println("   dog    ::= \"dog\";");
        out.println("}");
        AstParser tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (ParserException ex) {
            String expected = "Grammer is left recursive on non-terminal: dogs";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }

    public void testEmptyLeftRecursive() throws Exception {
        out.println("{");
        out.println("   dogs   ::= dog dogs;");
        out.println("   dog    ::= \"dog\" | /*EMPTY*/ ;");
        out.println("}");
        AstParser tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (ParserException ex) {
            String expected = "Grammer is left recursive on non-terminal: dogs";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }

    public void testAmbiguousRule() throws Exception {
        out.println("{");
        out.println("   rhyme ::= fox | dog ;");
        out.println("   fox   ::= \"fox\";");
        out.println("   dog   ::= \"fox\";");
        out.println("}");
        AstParser tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (Exception ex) {
            String expected = "Ambiguous grammer on terminal: \"fox\"";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }

    public void testAmbiguousRule2() throws Exception {
        out.println("{");
        out.println("   rhyme ::= \"fox\" | dog ;");
        out.println("   dog   ::= \"fox\";");
        out.println("}");
        AstParser tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (Exception ex) {
            String expected = "Ambiguous grammer on terminal: \"fox\"";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }

    public void testAmbiguousRule3() throws Exception {
        out.println("{");
        out.println("   rhyme ::= dog | \"fox\" ;");
        out.println("   dog   ::= \"fox\";");
        out.println("}");
        AstParser tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (Exception ex) {
            String expected = "Ambiguous grammer on terminal: \"fox\"";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }

    public void testEmptyAmbiguousRule() throws Exception {
        out.println("{");
        out.println("   rhyme ::= foxy \"fox\" ;");
        out.println("   foxy   ::= \"fox\" | /*EMPTY*/ ;");
        out.println("}");
        AstParser tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (Exception ex) {
            String expected = "Ambiguous grammer on terminal: \"fox\"";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }
}
