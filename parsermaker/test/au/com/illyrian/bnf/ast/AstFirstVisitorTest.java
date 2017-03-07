package au.com.illyrian.bnf.ast;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import junit.framework.TestCase;
import au.com.illyrian.bnf.BnfParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.ModuleContext;

public class AstFirstVisitorTest extends TestCase
{
    BnfFirstVisitor visitor = new BnfFirstVisitor();
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
    
    BnfTree parse(StringReader reader) throws Exception {
        Input input = new LexerInputStream(reader, null);
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        
        BnfParser parser = new BnfParser();
        //BnfTree tree = parser.parseMembers(context);
        parser.setCompilerContext(context);
        parser.nextToken();
        BnfTreeParser tree = parser.class_body();
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        return tree;
    }

    public void testFirstSeq() throws Exception {
        out.println("{");
        out.println("   rhyme ::= the fox ;");
        out.println("}");
        BnfTree tree = parse(getReader());
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        BnfFirstSet rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertEquals("first(rhyme)=[the]", rhymeSet.toString());
    }

    public void testEmpty() throws Exception {
        out.println("{");
        out.println("   rhyme ::= /*EMPTY*/ ;");
        out.println("}");
        StringReader reader = getReader();
        BnfTree tree = parse(reader);
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertTrue("hasEmpty should be false", hasEmpty);
        BnfFirstSet rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertEquals("first(rhyme)=[<EMPTY>]", rhymeSet.toString());
    }

    public void testAlt() throws Exception {
        out.println("{");
        out.println("   rhyme ::= fox | dog | cat ;");
        out.println("}");
        BnfTree tree = parse(getReader());
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        BnfFirstSet rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertEquals("first(rhyme)=[cat, dog, fox]", rhymeSet.toString());
    }

    public void testMultipleRules() throws Exception {
        out.println("{");
        out.println("   rhyme ::= fox | dog | cat ;");
        out.println("   fox   ::= foxy;");
        out.println("   dog   ::= dogy;");
        out.println("}");
        BnfTree tree = parse(getReader());
        
        BnfTreeRule [] rules = tree.toRuleArray();
        assertNotNull("tree.toRuleArray() should not be null", rules);
        assertEquals("rules.length", 3, rules.length);
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        BnfFirstSet rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertEquals("first(rhyme)=[cat, dogy, foxy]", rhymeSet.toString());
    }

    public void testEmptySeqenceRules() throws Exception {
        out.println("{");
        out.println("   rhyme  ::= speed colour fox ;");
        out.println("   speed  ::= quick | /*EMPTY*/ ;");
        out.println("   colour ::= brown | /*EMPTY*/ ;");
        out.println("}");
        BnfTree tree = parse(getReader());
        
        BnfTreeRule [] rules = tree.toRuleArray();
        assertNotNull("tree.toRuleArray() should not be null", rules);
        assertEquals("rules.length", 3, rules.length);
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        
        BnfFirstSet rhymeSet = visitor.getSet("rhyme");
        assertNotNull("set(rhyme) should not be null", rhymeSet);
        assertTrue("should contain quick: " + rhymeSet, rhymeSet.contains("quick"));
        assertTrue("should contain brown: " + rhymeSet, rhymeSet.contains("brown"));
        assertTrue("should contain fox: " + rhymeSet, rhymeSet.contains("fox"));
        assertFalse("should not contain <EMPTY>: "   + rhymeSet, rhymeSet.contains(BnfFirstVisitor.EMPTY));
        assertEquals("first(rhyme)=[brown, fox, quick]", rhymeSet.toString());

        BnfFirstSet speedSet = visitor.getSet("speed");
        assertNotNull("set(speed) should not be null", speedSet);
        assertTrue("should contain quick: " + speedSet, speedSet.contains("quick"));
        assertFalse("should not contain brown: " + speedSet, speedSet.contains("brown"));
        assertFalse("should not contain fox: " + speedSet, speedSet.contains("fox"));
        assertTrue("should contain <EMPTY>: " + speedSet, speedSet.contains(BnfFirstVisitor.EMPTY));
        assertEquals("first(speed)=[<EMPTY>, quick]", speedSet.toString());

        BnfFirstSet colourSet = visitor.getSet("colour");
        assertNotNull("set(colour) should not be null", colourSet);
        assertFalse("should not contain quick: " + colourSet, colourSet.contains("quick"));
        assertTrue("should contain brown: " + colourSet, colourSet.contains("brown"));
        assertFalse("should not contain fox: " + colourSet, colourSet.contains("fox"));
        assertTrue("should contain <EMPTY>: " + colourSet, colourSet.contains(BnfFirstVisitor.EMPTY));
        assertEquals("first(colour)=[<EMPTY>, brown]", colourSet.toString());
    }

    public void testMemberSyntax() throws Exception {
        out.println("{");
        out.println("   name        ::= IDENTIFIER ;");
        out.println("   method_type ::= INTEGER | VOID ;");
        out.println("   modifier_mult ::= PUBLIC  modifier_mult | PRIVATE  modifier_mult | /*EMPTY*/ ;");
        out.println("   member ::= modifier_mult method_type name SEMI ;");
        out.println("}");
        BnfTree tree = parse(getReader());
        
        BnfTreeRule [] rules = tree.toRuleArray();
        assertNotNull("tree.toRuleArray() should not be null", rules);
        assertEquals("rules.length", 4, rules.length);
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        
        BnfFirstSet set;
        set= visitor.getSet("name");
        assertNotNull("first(name) should not be null", set);
        assertEquals("first(name)=[IDENTIFIER]", set.toString());
        
        set= visitor.getSet("method_type");
        assertNotNull("first(method_type) should not be null", set);
        assertEquals("first(method_type)=[INTEGER, VOID]", set.toString());
        
        set= visitor.getSet("modifier_mult");
        assertNotNull("first(modifier_mult) should not be null", set);
        assertEquals("first(modifier_mult)=[<EMPTY>, PRIVATE, PUBLIC]", set.toString());
        
        set= visitor.getSet("member");
        assertNotNull("first(member) should not be null", set);
        assertEquals("first(member)=[INTEGER, PRIVATE, PUBLIC, VOID]", set.toString());
    }

    public void testLookaheadRules() throws Exception {
        out.println("{");
        out.println("   label_opt ::= LOOKAHEAD(IDENTIFIER COLON) name COLON ");
        out.println("             | /*EMPTY*/ ;");
        out.println("   name ::= IDENTIFIER ; ");
        out.println("}");
        BnfTree tree = parse(getReader());
        
        BnfTreeRule [] rules = tree.toRuleArray();
        assertNotNull("tree.toRuleArray() should not be null", rules);
        assertEquals("rules.length", 2, rules.length);
        
        boolean hasEmpty = tree.resolveFirst(visitor, null);
        assertFalse("hasEmpty should be false", hasEmpty);
        BnfFirstSet firstSet = visitor.getSet("label_opt");
        assertNotNull("set(label_opt) should not be null", firstSet);
        assertEquals("first(label_opt)=[<EMPTY>, LOOKAHEAD(IDENTIFIER COLON)]", firstSet.toString());
    }

    // Exception handling 
    
    public void testLeftRecursive() throws Exception {
        out.println("{");
        out.println("   dogs   ::= dogs dog;");
        out.println("   dog    ::= dog;");
        out.println("}");
        BnfTree tree = parse(getReader());
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
        out.println("   dog    ::= dogs | /*EMPTY*/ ;");
        out.println("}");
        BnfTree tree = parse(getReader());
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
        out.println("   fox   ::= ID;");
        out.println("   dog   ::= ID;");
        out.println("}");
        BnfTree tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (Exception ex) {
            String expected = "Ambiguous grammer on terminal: ID";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }

    public void testAmbiguousRule2() throws Exception {
        out.println("{");
        out.println("   rhyme ::= fox | dog ;");
        out.println("   dog   ::= fox;");
        out.println("}");
        BnfTree tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (Exception ex) {
            String expected = "Ambiguous grammer on terminal: fox";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }

    public void testAmbiguousRule3() throws Exception {
        out.println("{");
        out.println("   rhyme ::= dog | fox ;");
        out.println("   dog   ::= fox;");
        out.println("}");
        BnfTree tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (Exception ex) {
            String expected = "Ambiguous grammer on terminal: fox";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }

    public void testEmptyAmbiguousRule() throws Exception {
        out.println("{");
        out.println("   rhyme ::= foxy fox ;");
        out.println("   foxy   ::= fox | /*EMPTY*/ ;");
        out.println("}");
        BnfTree tree = parse(getReader());
        assertNotNull("tree should not be null", tree);
        
        try {
            tree.resolveFirst(visitor, null);
            fail("Should throw exception");
        } catch (Exception ex) {
            String expected = "Ambiguous grammer on terminal: fox";
            assertEquals("Wrong ParserException", expected, ex.getMessage());
        }
    }
}
