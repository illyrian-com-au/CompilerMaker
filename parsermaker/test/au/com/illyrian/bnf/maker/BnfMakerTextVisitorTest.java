package au.com.illyrian.bnf.maker;

import java.util.Map;

import au.com.illyrian.bnf.BnfParser;
import au.com.illyrian.bnf.ast.BnfFirstSet;
import au.com.illyrian.bnf.ast.BnfFirstVisitor;
import au.com.illyrian.bnf.ast.BnfTree;
import au.com.illyrian.bnf.ast.BnfTreeFactory;
import au.com.illyrian.bnf.ast.BnfTreeMethodCall;
import au.com.illyrian.bnf.ast.BnfTreeName;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.ast.BnfTreeRule;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.ClassMakerText;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

public class BnfMakerTextVisitorTest extends BnfMakerTextBase
{
    private static final String OBJ = "java.lang.Object";
    
    ClassMakerIfc maker = new ClassMakerText();
    BnfMakerVisitor visitor = new BnfMakerVisitor(maker);
    BnfTreeFactory ast = new BnfTreeFactory(null);

    public void testTerminalName() {
        BnfTreeName tree = ast.BnfName("IDENTIFIER");
        visitor.resolveType(tree);
        String expected = "[" + expect("IDENTIFIER") + "]";
        assertEquals(expected, maker.toString());
    }

    public void testMethodParams0() {
        BnfTreeMethodCall tree = ast.MethodCall(ast.BnfName("expression"), null);
        visitor.resolveType(tree);
        String expected = "[" + call("expression") + "]";
        assertEquals(expected, maker.toString());
    }

    public void testMethodParams1() {
        BnfTreeMethodCall tree = ast.MethodCall(ast.BnfName("error"), ast.Literal("; expected"));
        visitor.resolveType(tree);
        String expected = "[Call(This(), \"error\", Push(Literal(\"; expected\")))]";
        assertEquals(expected, maker.toString());
    }

    public void testMethodParams2() {
        BnfTreeMethodCall tree = ast.MethodCall(ast.BnfName("error"), 
                ast.Comma(ast.Literal("Hello"), ast.Literal("World")));
        assertEquals("error(\"Hello\", \"World\")", tree.toString());
        visitor.resolveType(tree);
        String expected = "[Call(This(), \"error\", Push(Literal(\"Hello\")).Push(Literal(\"World\")))]";
        assertEquals(expected, maker.toString());
    }

    public void testResolveMethod() {
        BnfTreeMethodCall tree = ast.MethodCall(ast.BnfName("error"), ast.Literal("; expected"));
        visitor.resolveSequence(tree, 1);
        String expected = error("; expected");
        assertEquals(expected, maker.toString());
    }

    public void testSimpleToken() {
        BnfTree tree = ast.Seq(ast.BnfReserved("RETURN"), ast.Empty());
        assertEquals("RETURN .", tree.toString());
        tree.resolveDeclaration(visitor);
        String expected = set("$1", "java.lang.String", expect("RETURN"));
        assertEquals(expected, maker.toString());
    }

    public void testAlternative1() {
        BnfTree ret = ast.Seq(ast.BnfReserved("RETURN"), ast.Empty());
        BnfTree tree = ast.Alt(ret, ast.Empty());
        assertEquals("( RETURN . | . )", tree.toString());
        tree.resolveDeclaration(visitor);
        
        String expect = ifMatchThen( "RETURN", set("$1", expect("RETURN")));
        assertEquals(expect, maker.toString());
    }

    public void testAlternative2() {
        BnfTree ret = ast.Seq(ast.BnfReserved("RETURN"), ast.Empty());
        BnfTree brk = ast.Seq(ast.BnfReserved("BREAK"), ast.Empty());
        BnfTree tree = ast.Alt(ret, brk);
        assertEquals("( RETURN . | BREAK . )", tree.toString());
        tree.resolveDeclaration(visitor);
        
        String expect = 
                ifMatchThenElse( "RETURN", 
                        set("$1", expect("RETURN")), 
                        set("$1", expect("BREAK")));
        assertEquals(expect, maker.toString());
    }

    public void testReturnOptional1() {
        BnfTree num = ast.Seq(ast.BnfReserved("INTEGER"), ast.Empty());
        BnfTree alt = ast.Alt(num, ast.Empty());
        BnfTree tree = ast.Seq(ast.BnfReserved("RETURN"), alt);
        assertEquals("RETURN ( INTEGER . | . )", tree.toString());
        tree.resolveDeclaration(visitor);
        
        String expect 
        = set("$1", expect("RETURN")) 
        + ifMatchThen( "INTEGER", set("$2", expect("INTEGER")));
        assertEquals(expect, maker.toString());
    }

    //               alt
    //             /     \
    //         RETURN    alt
    //                 /     \
    //            BREAK      CONTINUE
    public void testAlternative3Right() {
        BnfTree ret  = ast.BnfReserved("RETURN");
        BnfTree brk  = ast.BnfReserved("BREAK");
        BnfTree cont = ast.BnfReserved("CONTINUE");
        BnfTree tree1 = ast.Alt(brk, cont);
        BnfTree tree2 = ast.Alt(ret, tree1);
        assertEquals("( RETURN | BREAK | CONTINUE )", tree2.toString());
        tree2.resolveDeclaration(visitor);
        
        String expect = 
                ifMatchThenElse( "RETURN", set("$1", expect("RETURN")),
                        ifMatchThenElse( "BREAK", set("$1", expect("BREAK")),
                                set("$1", expect("CONTINUE"))));
        assertEquals(expect, maker.toString());
    }

    //               alt
    //             /     \
    //         alt     CONTINUE
    //       /     \
    //   RETURN   BREAK 
    public void testAlternative3Left() {
        BnfTree ret  = ast.BnfReserved("RETURN");
        BnfTree brk  = ast.BnfReserved("BREAK");
        BnfTree cont = ast.BnfReserved("CONTINUE");
        BnfTree tree1 = ast.Alt(ret, brk);
        BnfTree tree2 = ast.Alt(tree1, cont);
        assertEquals("( RETURN | BREAK | CONTINUE )", tree2.toString());
        tree2.resolveDeclaration(visitor);
        
        // Should get the same result as previous test
        String expect = 
                ifMatchThenElse( "RETURN", set("$1", expect("RETURN")),
                        ifMatchThenElse( "BREAK", 
                                set("$1", expect("BREAK")), 
                                        set("$1", expect("CONTINUE"))));
        assertEquals(expect, maker.toString());
    }
    
    //                   alt
    //                 /     \
    //               alt     /* EMPTY */
    //             /     \
    //         alt     CONTINUE
    //       /     \
    //   RETURN   BREAK 
    public void testAlternativeOpt3() {
        BnfTree ret  = ast.BnfReserved("RETURN");
        BnfTree brk  = ast.BnfReserved("BREAK");
        BnfTree cont = ast.BnfReserved("CONTINUE");
        BnfTree tree1 = ast.Alt(ret, brk);
        BnfTree tree2 = ast.Alt(tree1, cont);
        BnfTree tree3 = ast.Alt(tree2, ast.Empty());
        assertEquals("( RETURN | BREAK | CONTINUE | . )", tree3.toString());
        tree3.resolveDeclaration(visitor);
        
        // Tokens should be optional, i.e. no error if nothing matches.
        String expect = 
                ifMatchThenElse( "RETURN", 
                        set("$1", expect("RETURN")),
                        ifMatchThenElse( "BREAK", 
                                set("$1", expect("BREAK")),
                                ifMatchThen( "CONTINUE", 
                                        set("$1", expect("CONTINUE")))));
        assertEquals(expect, maker.toString());
    }
    
    //                   alt
    //                 /     \
    //               alt     error("return, break or continue expected")
    //             /     \
    //         alt     CONTINUE
    //       /     \
    //   RETURN   BREAK 
    public void testAlternativeError3() {
        BnfTree ret  = ast.BnfReserved("RETURN");
        BnfTree brk  = ast.BnfReserved("BREAK");
        BnfTree cont = ast.BnfReserved("CONTINUE");
        BnfTree err  = ast.MethodCall(ast.BnfName("error"), 
                ast.Literal("return, break or continue expected"));
        BnfTree tree1 = ast.Alt(ret, brk);
        BnfTree tree2 = ast.Alt(tree1, cont);
        BnfTree tree3 = ast.Alt(tree2, err);
        assertEquals("( RETURN | BREAK | CONTINUE | error(\"return, break or continue expected\") )", tree3.toString());
        tree3.resolveDeclaration(visitor);
        
        // Error if no token is matched.
        String expect = 
                ifMatchThenElse( "RETURN", set("$1", expect("RETURN")),
                        ifMatchThenElse( "BREAK", set("$1", expect("BREAK")),
                                ifMatchThenElse( "CONTINUE", set("$1", expect("CONTINUE")),
                                        error("return, break or continue expected"))));
        assertEquals(expect, maker.toString());
    }
    
    public void testVariableAssign() {
        BnfTree result = ast.Seq(ast.Nonterminal("expression"), ast.Empty());
        BnfTree tree = ast.Seq(ast.BnfReserved("RETURN"), result);
        assertEquals("RETURN expression() .", tree.toString());
        tree.resolveDeclaration(visitor);
        
        String expect = 
                set("$1", expect("RETURN")) 
                + set("$2", OBJ, call("expression"));
        assertEquals(expect, maker.toString());
    }
    
    public void testVariableAssign2() {
        BnfTree for_9 = ast.Seq(ast.Nonterminal("statement"), ast.Empty());
        BnfTree for_8 = ast.Seq(ast.BnfReserved("RPAR"), for_9);
        BnfTree for_7 = ast.Seq(ast.Nonterminal("expression"), for_8);
        BnfTree for_6 = ast.Seq(ast.BnfReserved("SEMI"), for_7);
        BnfTree for_5 = ast.Seq(ast.Nonterminal("expression"), for_6);
        BnfTree for_4 = ast.Seq(ast.BnfReserved("SEMI"), for_5);
        BnfTree for_3 = ast.Seq(ast.Nonterminal("expression"), for_4);
        BnfTree for_2 = ast.Seq(ast.BnfReserved("LPAR"), for_3);
        BnfTree for_1 = ast.Seq(ast.BnfReserved("FOR"), for_2);
        assertEquals("FOR LPAR expression() SEMI expression() SEMI expression() RPAR statement() .", 
                for_1.toString());
        for_1.resolveDeclaration(visitor);
        
        String expect 
                = set("$1", expect("FOR")) 
                + set("$2", expect("LPAR")) 
                + set("$3", OBJ, call("expression"))
                + set("$4", expect("SEMI"))
                + set("$5", OBJ, call("expression"))
                + set("$6", expect("SEMI"))
                + set("$7", OBJ, call("expression"))
                + set("$8", expect("RPAR")) 
                + set("$9", OBJ, call("statement"));
        assertEquals(expect, maker.toString());
    }
    
    //    qualified_name ::=
    //            name DOT qualified_name
    //        |   name  ;
    public void testVariableAssign3() {
        BnfTree seq3 = ast.Seq(ast.Nonterminal("qualified_name"), ast.Empty());
        BnfTree seq2 = ast.Seq(ast.BnfReserved("DOT"), seq3);
        BnfTree alt2 = ast.Alt(seq2, ast.Empty());
        BnfTree seq1 = ast.Seq(ast.Nonterminal("name"), alt2);
        BnfTree rule = ast.Rule(ast.BnfName("qualified_name"), seq1);
        assertEquals("qualified_name ::= name() ( DOT qualified_name() . | . ) ;", rule.toString());
        rule.resolveDeclaration(visitor);
        
        String expect 
        = beginMethod("qualified_name", "java.lang.Object")
        + set("$1", OBJ, call("name"))
        + ifMatchThen("DOT", 
                set("$2", expect("DOT")) 
                + set("$3", OBJ, call("qualified_name"))
                )
        + endMethod();
        assertEquals(expect, maker.toString());
    }

    public void testLookaheadAlt1() {
        BnfTreeName ruleName = ast.BnfName("jump");
        BnfTreeName ruleBody = ast.BnfName("RETURN");
        BnfTreeRule rule = ast.Rule(ruleName, ruleBody);

        BnfFirstVisitor firstVisitor = new BnfFirstVisitor();
        rule.resolveFirst(firstVisitor, null);
        BnfFirstSet firstSet = rule.getFirstSet();
        assertEquals("First set", "first(jump)=[RETURN]", firstSet.toString());
        
        rule.resolveLookahead(visitor, 0);
        
        String expect = "[" + match("RETURN") + "]"; 
        assertEquals(expect, maker.toString());
    }
    
    public void testLookaheadAlt2() {
        BnfTreeName ruleBreak = ast.BnfName("BREAK");
        BnfTreeName ruleCont = ast.BnfName("CONTINUE");
        BnfTreeRule rule = ast.Rule(null, null);
        BnfFirstSet firstSet = new BnfFirstSet("jump");
        firstSet.add("BREAK", ruleBreak);
        firstSet.add("CONTINUE", ruleCont);
        rule.setFirstSet(firstSet);
        assertEquals("First set", "first(jump)=[BREAK, CONTINUE]", firstSet.toString());
        
        rule.resolveLookahead(visitor, 0);
        
        String expect = "[" + logic(orElse(match("BREAK")), match("CONTINUE")) + "]"; 
        assertEquals(expect, maker.toString());
    }
    
    public void testLookaheadAlt3() {
        BnfTreeName ruleBreak = ast.BnfName("BREAK");
        BnfTreeName ruleCont = ast.BnfName("CONTINUE");
        BnfTreeName ruleRet = ast.BnfName("RETURN");
        BnfTreeRule rule = ast.Rule(null, null);
        BnfFirstSet firstSet = new BnfFirstSet("jump");
        firstSet.add("BREAK", ruleBreak);
        firstSet.add("CONTINUE", ruleCont);
        firstSet.add("RETURN", ruleRet);
        rule.setFirstSet(firstSet);
        assertEquals("First set", "first(jump)=[BREAK, CONTINUE, RETURN]", firstSet.toString());
        
        rule.resolveLookahead(visitor, 1);
        
        String expect = "[" + logic(orElse(orElse(
                match("BREAK")), 
                match("CONTINUE")), 
                match("RETURN")) + "]"; 
        assertEquals(expect, maker.toString());
    }
    
    public void testLookaheadAlt4() {
        BnfTreeName ruleBreak = ast.BnfName("BREAK");
        BnfTreeName ruleCont = ast.BnfName("CONTINUE");
        BnfTreeName ruleRet = ast.BnfName("RETURN");
        BnfTreeName ruleStop = ast.BnfName("STOP");
        BnfTreeRule rule = ast.Rule(null, null);
        BnfFirstSet firstSet = new BnfFirstSet("jump");
        firstSet.add("BREAK", ruleBreak);
        firstSet.add("CONTINUE", ruleCont);
        firstSet.add("RETURN", ruleRet);
        firstSet.add("STOP", ruleStop);
        rule.setFirstSet(firstSet);
        assertEquals("First set", "first(jump)=[BREAK, CONTINUE, RETURN, STOP]", firstSet.toString());
        
        rule.resolveLookahead(visitor, 1);
        
        String expect = "[" + logic(orElse(orElse(orElse(
                match("BREAK")), 
                match("CONTINUE")), 
                match("RETURN")), 
                match("STOP")) + "]"; 
        assertEquals(expect, maker.toString());
    }
    
    BnfTreeParser parseCompassTree() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= north");
        out.println("               | east");
        out.println("               | south");
        out.println("               | west");
        out.println("               | error(\"WrongDirection\")");
        out.println("         ;");
        out.println("    north ::= NORTH EAST | NORTH WEST | NORTH ;");
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
        
        return tree;
    }
    
    public void testGenMethodSouth() throws Exception 
    {
        BnfTreeParser tree = parseCompassTree();
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule eastRule = lookup.get("south");
        eastRule.resolveDeclaration(visitor);
        String expect 
        = beginMethod("south", "java.lang.Object")
        + set("$1", expect("SOUTH"))
        + ifMatchThenElse("EAST",
                set("$2", expect("EAST")),
                ifMatchThen("WEST",
                        set("$2", expect("WEST"))))
        + endMethod();
        assertEquals(expect, maker.toString());
    }
    
    public void testGenMethodNorth() throws Exception 
    {
        BnfTreeParser tree = parseCompassTree();
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule eastRule = lookup.get("north");
        eastRule.resolveDeclaration(visitor);
        String expect 
        = beginMethod("north", OBJ)
        + set("$1", expect("NORTH"))
        + ifMatchThenElse("EAST",
                set("$2", expect("EAST")),
                ifMatchThen("WEST",
                        set("$2", expect("WEST"))))
        + endMethod();
        assertEquals(expect, maker.toString());
    }
    
    public void testGenMethodEast() throws Exception 
    {
        BnfTreeParser tree = parseCompassTree();
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule eastRule = lookup.get("east");
        eastRule.resolveDeclaration(visitor);
        String expect 
            = beginMethod("east", OBJ)
            + set("$1", expect("EAST"))
            + endMethod();
        assertEquals(expect, maker.toString());
    }
    
    public void testGenMethodDirection() throws Exception 
    {
        BnfTreeParser tree = parseCompassTree();
        
        Map<String, BnfTreeRule> lookup = tree.getRuleSet();
        visitor.setRuleSet(lookup);

        BnfTreeRule eastRule = lookup.get("direction");
        eastRule.resolveDeclaration(visitor);
        String expect 
        = beginMethod("direction", "java.lang.Object")
        + ifMatchThenElse("NORTH", 
            set("$1", OBJ, call("north")),
            ifMatchThenElse("EAST",
               set("$1", OBJ, call("east")),
            ifMatchThenElse("SOUTH",
               set("$1", OBJ, call("south")),
            ifMatchThenElse("WEST",
               set("$1", OBJ, call("west")),
               error("WrongDirection")
        ))))
        + endMethod();
        assertEquals(expect, maker.toString());
    }
    
    public void testGenMethodDirectionLookahead() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= northsouth");
        out.println("               | eastwest");
        out.println("               | error(\"WrongDirection\");");
        out.println("    northsouth ::= north");
        out.println("               | south;");
        out.println("    eastwest ::= east");
        out.println("               | west;");
        out.println("    north ::= NORTH;");
        out.println("    east  ::= EAST;");
        out.println("    south ::= SOUTH;");
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
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("direction");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("direction", OBJ)
        + ifThenElse(logic(orElse(match("NORTH")), match("SOUTH")), 
            set("$1", OBJ, call("northsouth")),
            ifThenElse(logic(orElse(match("EAST")), match("WEST")),
               set("$1", OBJ, call("eastwest")),
               error("WrongDirection")
          ))
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }

    public void testGenMethodNorthEastWestLookahead() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= LOOKAHEAD(NORTH EAST) northeast");
        out.println("               | LOOKAHEAD(NORTH WEST) northwest");
        out.println("               | north");
        out.println("               | error(\"WrongDirection\");");
        out.println("    north ::= NORTH;");
        out.println("    northeast  ::= NORTH EAST;");
        out.println("    northwest  ::= NORTH WEST;");
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
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("direction");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("direction", "java.lang.Object")
        + ifThenElse(logic(andThen(match("NORTH")),match("EAST", 1)), 
            set("$1", OBJ, call("northeast")),
            ifThenElse(logic(andThen(match("NORTH")),match("WEST", 1)),
               set("$1", OBJ, call("northwest")),
               ifThenElse(match("NORTH"),
                       set("$1", OBJ, call("north")),
                       error("WrongDirection")
          )))
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }

    public void testGenMethodEverythingLookahead() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    direction ::= LOOKAHEAD(NORTH EAST) northeast");
        out.println("               | north");
        out.println("               | /*EMPTY*/");
        out.println("               | RECOVER(SOUTH);");
        out.println("    north      ::= NORTH;");
        out.println("    northeast  ::= NORTH EAST;");
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
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("direction");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("direction", OBJ)
        + ifThenElse(logic(andThen(match("NORTH")),match("EAST", 1)), 
            set("$1", OBJ, call("northeast")),
               ifThen(match("NORTH"),
                       set("$1", OBJ, call("north"))
          ))
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }

    public void testGenLabelLookahead() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    label       ::= LOOKAHEAD(IDENTIFIER COLON) label_name COLON");
        out.println("                  | /* EMPTY */;");
        out.println("    label_name  ::= IDENTIFIER;");
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
        visitor.setRuleSet(lookup);

        BnfTreeRule dirRule = lookup.get("label");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("label", "java.lang.Object")
        + ifThen(logic(andThen(match("IDENTIFIER")),match("COLON", 1)), 
            set("$1", OBJ, call("label_name"))
            + set("$2", expect("COLON"))
          )
        + endMethod();
        assertEquals(expectDirection, maker.toString());
    }

    public void testParseLookaheadIndirectAction() throws Exception 
    {
        StringReadWriter out = new StringReadWriter();
        out.println("{");
        out.println("    package ::= name");
        out.println("              | STAR ;");
        out.println("    name   ::= LOOKAHEAD(IDENTIFIER) ;");
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

        BnfTreeRule dirRule = lookup.get("package");
        dirRule.resolveDeclaration(visitor);
        String expectDirection 
        = beginMethod("package", OBJ)
        + ifThenElse(match("IDENTIFIER"),
            set("$1",OBJ, call("name")) + assign$0$1(),
            set("$1", expect("STAR")) + assign$0$1()
            ) + endMethod();
        assertEquals(expectDirection, maker.toString());
    }
/*
    // String functions
    String match(String token) {
        return "Call(This(), \"match\", Push(Get(This(), \"" + token + "\")))";
    }
    String match(String token, int ahead) {
        return "Call(This(), \"match\", Push(Get(This(), \"" + token + "\")).Push(Literal(" + ahead + ")))";
    }
    String expect(String token) {
        return "Call(This(), \"expect\", Push(Get(This(), \"" + token + "\")))";
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
        return "  If(" + cond + ");\n  Begin();\n" + thenCode + "  End();\n  EndIf();\n";
    }
    String ifMatchThenElse(String token, String thenCode, String elseCode) {
        return ifThenElse(match(token), thenCode, elseCode);
    }
    String ifThenElse(String cond, String thenCode, String elseCode) {
        return "  If(" + cond + ");\n  Begin();\n"  + thenCode 
                + "  End();\n  Else();\n  Begin();\n" + elseCode 
                + "  End();\n  EndIf();\n";
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
   */ 
}
