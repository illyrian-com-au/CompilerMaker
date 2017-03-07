package au.com.illyrian.bnf.maker;

import java.util.Map;

import junit.framework.TestCase;
import au.com.illyrian.bnf.BnfParser;
import au.com.illyrian.bnf.ast.BnfTreeFactory;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.ast.BnfTreeRule;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.ClassMakerText;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

public class BnfMakerTextActionTest extends TestCase
{
    private static final String OBJ = "java.lang.Object";
    private static final String STR = "java.lang.String";
    
    ClassMakerIfc maker = new ClassMakerText();
    BnfMakerVisitor visitor = new BnfMakerVisitor(maker);
    BnfTreeFactory ast = new BnfTreeFactory();

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
            + set("$1", call("This()", "expect", "Push(Get(\"NORTH\"))"))
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

    // String functions
    String match(String token) {
        return "Call(This(), \"match\", Push(Get(\"" + token + "\")))";
    }
    String match(String token, int ahead) {
        return "Call(This(), \"match\", Push(Get(\"" + token + "\")).Push(Literal(" + ahead + ")))";
    }
    String expect(String token) {
        return "Call(This(), \"expect\", Push(Get(\"" + token + "\")))";
    }
    String error(String message) {
        return "  Eval(Call(This(), \"error\", Push(Literal(\"" + message + "\"))));\n";
    }
    String assign(String name, String value) {
        return "  Eval(Assign(\"" + name + "\", " + value + "));\n";
    }
    String assign$0(String value) {
        return assign("$0", value);
    }
    String assign$0$1() {
        return assign$0("Get(\"$1\")");
    }
    String set(String name, String value) {
        return set(name, "java.lang.String", value);
    }
    String set(String name, String type, String value) {
        return declare(name, type) + assign(name, value);
    }
    String call(String name) {
        return call(name, "Push()");
    }
    String call(String name, String parameters) {
        return call("This()", name, parameters);
    }
    String call(String reference, String name, String parameters) {
        return "Call(" + reference + ", \"" + name + "\", " + parameters + ")";
    }
    String declare(String name, String type) {
        return "  Declare(\"" + name + "\", \"" + type + "\", 0);\n";
    }
    String ifMatchThen(String token, String thenCode) {
        return ifThen(match(token), thenCode);
    }
    String ifThen(String cond, String thenCode) {
        return "  If(" + cond + ");\n" + thenCode + "  EndIf();\n";
    }
    String ifMatchThenElse(String token, String thenCode, String elseCode) {
        return ifThenElse(match(token), thenCode, elseCode);
    }
    String ifThenElse(String cond, String thenCode, String elseCode) {
        return "  If(" + cond + ");\n" + thenCode + "  Else();\n" + elseCode + "  EndIf();\n";
    }
    String orElse(String cond) {
        return "OrElse(" + cond + ")";
    }
    String orElse(String prev, String cond) {
        return "OrElse(" + prev + ", " + cond + ")";
    }
    String andThen(String cond) {
        return "AndThen(" + cond + ")";
    }
    String andThen(String prev, String cond) {
        return "AndThen(" + prev + ", " + cond + ")";
    }
    String logic(String prev, String cond) {
        return "Logic(" + prev + ", " + cond + ")";
    }
    String beginMethod(String methodName, String returnType) {
        return "Method(\"" + methodName + "\", \"" + returnType + "\", ACC_PUBLIC)\n"
                + "Begin()\n"
                + "  Declare(\"$0\", \"" + returnType + "\", 0);\n";
    }
    String endMethod() {
        return "  Return(Get(\"$0\"));\n"
                + "End()\n";        
    }
    
}
