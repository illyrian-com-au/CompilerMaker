package au.com.illyrian.bnf.maker;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

import au.com.illyrian.bnf.BnfParser;
import au.com.illyrian.bnf.ast.BnfTreeFactory;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.ast.BnfTreeRule;
import au.com.illyrian.classmaker.ClassMakerText;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

public class BnfMakerTextActionTest extends BnfMakerTextBase
{
    private static final String TEST_DIR = "test/";
    private static final String OBJ = "java.lang.Object";
    private static final String STR = "java.lang.String";
    private static final String TYPE = "au.com.illyrian.classmaker.types.Type";
    private static final String STRUCT = "au.com.illyrian.jesub.ast.AstStructure";
    
    ClassMakerText maker = new ClassMakerText();
    BnfMakerVisitor visitor = new BnfMakerVisitor(maker);
    BnfTreeFactory ast = new BnfTreeFactory(null);
    
    public static class Direction {};

    public void testParseSimpleAction() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= NORTH { factory.north() }");
        out.println("               | /* EMPTY */");
        out.println("         ;");
        out.println("}");
        out.close();
    
        ModuleContext compile = new ModuleContext();
        compile.setInputReader(out.getReader(), null);
        
        BnfParser parser = new BnfParser();
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Should not be null:", tree);
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);
 
        BnfTreeRule dirRule = lookup.get("direction");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("direction", OBJ)
        + ifThen(match("NORTH"), 
            set("$1", expect("NORTH"))
            + assign$0(call("Get(\"factory\")", "north", "Push()"))
          )
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }
    
    public void testParseEmptyAction() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= NORTH { factory.direction($1) }");
        out.println("               | { factory.other() }");
        out.println("         ;");
        out.println("}");
        out.close();
    
        ModuleContext compile = new ModuleContext();
        compile.setInputReader(out.getReader(), null);
        
        BnfParser parser = new BnfParser();
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Nothing returned from parser", tree);
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("direction");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("direction", OBJ)
        + ifThenElse(match("NORTH"), 
            set("$1", expect("NORTH"))
            + assign$0(call("Get(\"factory\")", "direction", "Push(Get(\"$1\"))")),
            assign$0(call("Get(\"factory\")", "other", "Push()"))
          )
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }

    public void testParseDefaultAction() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= north ;");
        out.println("    north     ::= NORTH ;");
        out.println("}");
        out.close();
    
        ModuleContext compile = new ModuleContext();
        compile.setInputReader(out.getReader(), null);
        
        BnfParser parser = new BnfParser();
        visitor.setActionRequired(true);
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Nothing returned from parser", tree);
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("direction");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("direction", OBJ)
            + set("$1", OBJ, call("This()", "north", "Push()"))
            + assign$0$1()
        + endMethod();
        assertEquals(expectDirection, maker.toString());

        maker = new ClassMakerText();
        visitor.setMaker(maker);
        dirRule = lookup.get("north");
        dirRule.resolveDeclaration(visitor);
        String expectNorth
        = beginMethod("north", OBJ)
            + set("$1", call("This()", "expect", "Push(Get(This(), \"NORTH\"))"))
            + assign$0$1()
        + endMethod();
        assertEquals(expectNorth, maker.toString());
    }

    public void testParseNorthActions() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= NORTH EAST { factory.add($1, $2) }");
        out.println("                | NORTH");
        out.println("                | EAST");
        out.println("         ;");
        out.println("}");
        out.close();
    
        ModuleContext compile = new ModuleContext();
        compile.setInputReader(out.getReader(), null);
        
        BnfParser parser = new BnfParser();
        visitor.setActionRequired(true);
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Nothing returned from parser", tree);
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("direction");
        dirRule.resolveDeclaration(visitor);
        String northeast = set("$2", expect("EAST"))
                + assign$0(call("Get(\"factory\")", "add", "Push(Get(\"$1\")).Push(Get(\"$2\"))"));
        String north = assign$0("Get(\"$1\")");
        String east = set("$1", expect("EAST")) + assign$0$1();
        String expectDirection 
        = beginMethod("direction", OBJ)
        + ifThenElse(match("NORTH"), 
            (set("$1", expect("NORTH"))
             + ifThenElse(match("EAST"), northeast, north)),
             east
          )
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }

    public void testParseActionsError() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= NORTH EAST { factory.add($1, $2) }");
        out.println("                | NORTH");
        out.println("                | error(\"Expected to go north\")");
        out.println("         ;");
        out.println("}");
        out.close();
    
        ModuleContext compile = new ModuleContext();
        compile.setInputReader(out.getReader(), null);
        
        BnfParser parser = new BnfParser();
        visitor.setActionRequired(true);
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Nothing returned from parser", tree);
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("direction");
        dirRule.resolveDeclaration(visitor);
        String northeast = set("$2", expect("EAST"))
                + assign("$0", call("Get(\"factory\")", "add", "Push(Get(\"$1\")).Push(Get(\"$2\"))"));
        String expectDirection 
        = beginMethod("direction", OBJ)
        + ifThenElse(match("NORTH"), 
            (set("$1", expect("NORTH"))
             + ifThenElse(match("EAST"), northeast, assign$0$1())),
             error("Expected to go north")
          )
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }

    public void testParseIdentifierAction() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    name   ::= LOOKAHEAD(IDENTIFIER) { factory.identifier(getLexer()) }");
        out.println("             | error(\"Identifier expected\") ;");
        out.println("}");
        out.close();
    
        ModuleContext compile = new ModuleContext();
        compile.setInputReader(out.getReader(), null);
        
        BnfParser parser = new BnfParser();
        visitor.setActionRequired(true);
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Nothing returned from parser", tree);
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("name");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("name", OBJ)
        + ifThenElse(match("IDENTIFIER"),
            assign$0(call("Get(\"factory\")", "identifier",
                    "Push(" + call("This()", "getLexer", "Push()") + ")")),
            error("Identifier expected")
            ) + endMethod();
        assertEquals(expectDirection, maker.toString());
    }

    public void testParsePackage() throws Exception 
    {
        File file = new File(TEST_DIR, "test/PackageParser.bnf");
        assertTrue("Cannot find " + file.getAbsolutePath(), file.exists());

        FileReader reader = new FileReader(file);
        ModuleContext compile = new ModuleContext();
        compile.setInputReader(reader, null);
        
        ClassMakerText textMaker = new ClassMakerText();
        textMaker.addType("AstStructure", AstStructure.class);
        BnfMakerVisitor visitor = new BnfMakerVisitor(textMaker);
        BnfParser parser = new BnfParser();
        visitor.setActionRequired(true);
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Nothing returned from parser", tree);
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("package");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("package", STRUCT)
        + ifThen(match("PACKAGE"),
            set("$1", expect("PACKAGE"))
            + ifThenElse(match("IDENTIFIER"),
                 set("$2",OBJ, call("qualified_name"))
                 + set("$3", expect("SEMI"))
                 + assign$0(call("Get(\"ast\")","Package", "Push(Get(\"$2\"))")),
                 error("Package name expected"))
            ) 
        + endMethod();
        assertEquals(expectDirection, textMaker.toString());
        
        
    }
    
    public void testAssignSpecificType() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    dir_mult:Type ::= NORTH dir_mult { factory.seq($1, $2) }");
        out.println("                |  ;");
        out.println("}");
        out.close();
    
        maker.addType("Type", Type.class);
        ModuleContext compile = new ModuleContext();
        compile.setInputReader(out.getReader(), null);
        
        BnfParser parser = new BnfParser();
        visitor.setActionRequired(true);
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        assertNotNull("Nothing returned from parser", tree);
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("dir_mult");
        assertNotNull("Rule dir_mult does not exist", dirRule);
        dirRule.resolveDeclaration(visitor);
        String northeast = set("$2", expect("EAST"))
                + assign("$0", call("Get(\"factory\")", "add", "Push(Get(\"$1\")).Push(Get(\"$2\"))"));
        String expectDirection 
        = beginMethod("dir_mult", TYPE)
        + ifThen(match("NORTH"), 
            set("$1", expect("NORTH"))
             + set("$2", TYPE, call("dir_mult", "Push()"))
             + assign$0(call("Get(\"factory\")", "seq", "Push(Get(\"$1\")).Push(Get(\"$2\"))"))
          )
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }
}
