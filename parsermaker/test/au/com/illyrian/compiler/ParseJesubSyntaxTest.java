package au.com.illyrian.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import au.com.illyrian.compiler.ast.AstFirstVisitor;
import au.com.illyrian.compiler.ast.AstMergeVisitor;
import au.com.illyrian.compiler.ast.AstParser;
import au.com.illyrian.compiler.ast.AstParserRule;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;
import junit.framework.TestCase;

public class ParseJesubSyntaxTest extends TestCase
{
    public static final String JESUB_SYNTAX_BNF = "test/au/com/illyrian/compiler/Jesub_syntax.bnf";
    
    private BufferedReader createReader(String filename) throws Exception {
        File file = new File(JESUB_SYNTAX_BNF);
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        return reader;
    }
    
    public void testFileExists() throws Exception {
        File file = new File(JESUB_SYNTAX_BNF);
        assertTrue("Could not find " + JESUB_SYNTAX_BNF, file.exists());
    }
    
    public void testFileLineCount() throws Exception {
        int count = 0;
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        while (reader.readLine()!= null) {
            count++;
        }
        assertEquals("Wrong line count", 236, count);
    }

    public void testRawJesubSyntax() throws Exception {
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        Input input = new LexerInputStream(reader, JESUB_SYNTAX_BNF);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        int i = 0;
        AstParserRule [] rules = tree.toRuleArray();
        
        assertEquals("Wrong rule", "goal ::= compilation_unit . ;", rules[i++].toString());
        assertEquals("Wrong rule", "name ::= IDENTIFIER . ;", rules[i++].toString());
        assertEquals("Wrong rule", "qualified_name ::= ( name DOT qualified_name . | name . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "modifier_mult ::= ( PUBLIC modifier_mult . | PROTECTED modifier_mult . | PRIVATE modifier_mult . | ABSTRACT modifier_mult . | FINAL modifier_mult . | STRICTFP modifier_mult . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "compilation_unit ::= package_opt import_mult class_declaration_plus . ;", rules[i++].toString());
        assertEquals("Wrong rule", "package_opt ::= ( PACKAGE qualified_name SEMI . | PACKAGE qualified_name error(\"IncompletePackageName\") . | . | recover(IMPORT, class_declaration) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "import_mult ::= ( IMPORT import_path_plus import_mult . | . | recover(IMPORT, class_declaration) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "import_path_plus ::= ( name DOT import_path_plus . | name SEMI . | MULT SEMI . | error(\"IncompleteImportPath\") . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "class_declaration_plus ::= ( class_declaration class_declaration_plus . | class_declaration . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "class_declaration ::= ( modifier_mult CLASS simple_name extends_opt implements_opt class_body . | modifier_mult INTERFACE simple_name extends_opt class_body . | error(\"ExpectedClassOrInterface\") . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "extends_opt ::= ( EXTENDS qualified_name . | . | recover(IMPLEMENTS, BEGIN) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "implements_opt ::= ( IMPLEMENTS implements_plus . | . | recover(BEGIN) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "implements_plus ::= ( qualified_name COMMA implements_plus . | qualified_name . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "class_body ::= BEGIN member_mult END . ;", rules[i++].toString());
        assertEquals("Wrong rule", "member_mult ::= ( member member_mult . | . | recover(member) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "member ::= ( modifier_mult method_type name LPAR formal_mult RPAR method_body . | modifier_mult method_type name SEMI . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "method_type ::= ( type . | VOID . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "type ::= ( class_type . | class_type LBRAC RBRAC . | primitive_type . | primitive_type LBRAC RBRAC . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "primitive_type ::= ( BOOLEAN . | BYTE . | CHAR . | SHORT . | INT . | LONG . | FLOAT . | DOUBLE . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "class_type ::= qualified_name . ;", rules[i++].toString());
        assertEquals("Wrong rule", "formal_mult ::= modifier_mult type name . ;", rules[i++].toString());
        assertEquals("Wrong rule", "method_body ::= ( compound_statement . | SEMI . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "compound_statement ::= BEGIN statement_mult END . ;", rules[i++].toString());
        assertEquals("Wrong rule", "statement_mult ::= ( statement statement_mult . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "statement ::= ( compound_statement . | if_else_statement . | while_statement . | for_statement . | try_statement . | break_statement . | continue_statement . | return_statement . | SEMI . | declare_statement . | expression_statement . | recover(statement) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "declare_label_opt ::= ( lookahead(IDENTIFIER, COLON) name COLON . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "if_else_statement ::= \"if\" LPAR expression RPAR statement else_opt . ;", rules[i++].toString());
        assertEquals("Wrong rule", "else_opt ::= ( \"else\" statement . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "while_statement ::= \"while\" LPAR expression RPAR statement . ;", rules[i++].toString());
        assertEquals("Wrong rule", "for_statement ::= \"for\" LPAR expression_opt SEMI expression_opt SEMI expression_opt RPAR statement . ;", rules[i++].toString());
        assertEquals("Wrong rule", "try_statement ::= \"try\" compound_statement catch_plus . ;", rules[i++].toString());
        assertEquals("Wrong rule", "catch_plus ::= ( catch_clause catch_plus . | catch_clause . | finally_clause . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "catch_clause ::= \"catch\" LPAR class_type name RPAR compound_statement . ;", rules[i++].toString());
        assertEquals("Wrong rule", "finally_clause ::= \"finally\" compound_statement . ;", rules[i++].toString());
        assertEquals("Wrong rule", "break_statement ::= \"break\" label_opt SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "continue_statement ::= \"continue\" label_opt SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "label_opt ::= ( IDENTIFIER . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "return_statement ::= \"return\" expression_opt SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "declare_statement ::= modifier_mult type name SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "expression_statement ::= expression SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "expression_opt ::= ( expression . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "expression ::= precedence(1) . ;", rules[i++].toString());
        assertEquals("Wrong rule", "actual_opt ::= ( precedence(0) . | . ) ;", rules[i++].toString());
        assertEquals("Wrong number of rule", rules.length, i);
    }
    
    public void testMergedJesubSyntax() throws Exception {
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        Input input = new LexerInputStream(reader, JESUB_SYNTAX_BNF);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        AstMergeVisitor merge = new AstMergeVisitor();
        AstParser merged = tree.resolveMerge(merge);

        int i = 0;
        AstParserRule [] rules = merged.toRuleArray();
        
        assertEquals("Wrong rule", "goal ::= compilation_unit . ;", rules[i++].toString());
        assertEquals("Wrong rule", "name ::= IDENTIFIER . ;", rules[i++].toString());
        assertEquals("Wrong rule", "qualified_name ::= name ( DOT qualified_name . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "modifier_mult ::= ( PUBLIC modifier_mult . | PROTECTED modifier_mult . | PRIVATE modifier_mult . | ABSTRACT modifier_mult . | FINAL modifier_mult . | STRICTFP modifier_mult . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "compilation_unit ::= package_opt import_mult class_declaration_plus . ;", rules[i++].toString());
        assertEquals("Wrong rule", "package_opt ::= ( PACKAGE qualified_name ( SEMI . | error(\"IncompletePackageName\") . ) | . | recover(IMPORT, class_declaration) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "import_mult ::= ( IMPORT import_path_plus import_mult . | . | recover(IMPORT, class_declaration) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "import_path_plus ::= ( name ( DOT import_path_plus . | SEMI . ) | MULT SEMI . | error(\"IncompleteImportPath\") . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "class_declaration_plus ::= class_declaration ( class_declaration_plus . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "class_declaration ::= ( modifier_mult ( CLASS simple_name extends_opt implements_opt class_body . | INTERFACE simple_name extends_opt class_body . ) | error(\"ExpectedClassOrInterface\") . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "extends_opt ::= ( EXTENDS qualified_name . | . | recover(IMPLEMENTS, BEGIN) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "implements_opt ::= ( IMPLEMENTS implements_plus . | . | recover(BEGIN) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "implements_plus ::= qualified_name ( COMMA implements_plus . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "class_body ::= BEGIN member_mult END . ;", rules[i++].toString());
        assertEquals("Wrong rule", "member_mult ::= ( member member_mult . | . | recover(member) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "member ::= modifier_mult method_type name ( LPAR formal_mult RPAR method_body . | SEMI . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "method_type ::= ( type . | VOID . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "type ::= ( class_type ( . | LBRAC RBRAC . ) | primitive_type ( . | LBRAC RBRAC . ) ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "primitive_type ::= ( BOOLEAN . | BYTE . | CHAR . | SHORT . | INT . | LONG . | FLOAT . | DOUBLE . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "class_type ::= qualified_name . ;", rules[i++].toString());
        assertEquals("Wrong rule", "formal_mult ::= modifier_mult type name . ;", rules[i++].toString());
        assertEquals("Wrong rule", "method_body ::= ( compound_statement . | SEMI . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "compound_statement ::= BEGIN statement_mult END . ;", rules[i++].toString());
        assertEquals("Wrong rule", "statement_mult ::= ( statement statement_mult . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "statement ::= ( compound_statement . | if_else_statement . | while_statement . | for_statement . | try_statement . | break_statement . | continue_statement . | return_statement . | SEMI . | declare_statement . | expression_statement . | recover(statement) . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "declare_label_opt ::= ( lookahead(IDENTIFIER, COLON) name COLON . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "if_else_statement ::= \"if\" LPAR expression RPAR statement else_opt . ;", rules[i++].toString());
        assertEquals("Wrong rule", "else_opt ::= ( \"else\" statement . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "while_statement ::= \"while\" LPAR expression RPAR statement . ;", rules[i++].toString());
        assertEquals("Wrong rule", "for_statement ::= \"for\" LPAR expression_opt SEMI expression_opt SEMI expression_opt RPAR statement . ;", rules[i++].toString());
        assertEquals("Wrong rule", "try_statement ::= \"try\" compound_statement catch_plus . ;", rules[i++].toString());
        assertEquals("Wrong rule", "catch_plus ::= ( catch_clause ( catch_plus . | . ) | finally_clause . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "catch_clause ::= \"catch\" LPAR class_type name RPAR compound_statement . ;", rules[i++].toString());
        assertEquals("Wrong rule", "finally_clause ::= \"finally\" compound_statement . ;", rules[i++].toString());
        assertEquals("Wrong rule", "break_statement ::= \"break\" label_opt SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "continue_statement ::= \"continue\" label_opt SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "label_opt ::= ( IDENTIFIER . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "return_statement ::= \"return\" expression_opt SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "declare_statement ::= modifier_mult type name SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "expression_statement ::= expression SEMI . ;", rules[i++].toString());
        assertEquals("Wrong rule", "expression_opt ::= ( expression . | . ) ;", rules[i++].toString());
        assertEquals("Wrong rule", "expression ::= precedence(1) . ;", rules[i++].toString());
        assertEquals("Wrong rule", "actual_opt ::= ( precedence(0) . | . ) ;", rules[i++].toString());
        assertEquals("Wrong number of rule", rules.length, i);
    }
    
    private String getRuleName(AstParserRule rule) {
        return rule.getTarget().toString();
    }
    
    private String getSet(AstFirstVisitor first, AstParserRule rule) {
        String name = getRuleName(rule);
        Set<String> set = first.getSet(name);
        return (set == null ? "first(" + name + ")= null" : set.toString());
    }

    public void testFirstJesubSyntax() throws Exception {
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        Input input = new LexerInputStream(reader, JESUB_SYNTAX_BNF);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
        
        AstMergeVisitor merge = new AstMergeVisitor();
        AstParser merged = tree.resolveMerge(merge);

        AstFirstVisitor first = new AstFirstVisitor();
        merged.resolveFirst(first, null);

        assertNotNull("Cannot find first set for goal", first.getSet("goal"));
        int i = 0;
        AstParserRule [] rules = merged.toRuleArray();
        
        assertEquals("first(goal)=[ABSTRACT, CLASS, FINAL, IMPORT, INTERFACE, PACKAGE, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(first, rules[i++]));
        assertEquals("first(name)=[IDENTIFIER]", getSet(first, rules[i++]));
        assertEquals("first(qualified_name)=[IDENTIFIER]", getSet(first, rules[i++]));
        assertEquals("first(modifier_mult)=[<EMPTY>, ABSTRACT, FINAL, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(first, rules[i++]));
        assertEquals("first(compilation_unit)=[ABSTRACT, CLASS, FINAL, IMPORT, INTERFACE, PACKAGE, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(first, rules[i++]));
        assertEquals("first(package_opt)=[<EMPTY>, PACKAGE]", getSet(first, rules[i++]));
        assertEquals("first(import_mult)=[<EMPTY>, IMPORT]", getSet(first, rules[i++]));
        assertEquals("first(import_path_plus)=[IDENTIFIER, MULT]", getSet(first, rules[i++]));
        assertEquals("first(class_declaration_plus)=[ABSTRACT, CLASS, FINAL, INTERFACE, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(first, rules[i++]));
        assertEquals("first(class_declaration)=[ABSTRACT, CLASS, FINAL, INTERFACE, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(first, rules[i++]));
        assertEquals("first(extends_opt)=[<EMPTY>, EXTENDS]", getSet(first, rules[i++]));
        assertEquals("first(implements_opt)=[<EMPTY>, IMPLEMENTS]", getSet(first, rules[i++]));
        assertEquals("first(implements_plus)=[IDENTIFIER]", getSet(first, rules[i++]));
        assertEquals("first(class_body)=[BEGIN]", getSet(first, rules[i++]));
        assertEquals("first(member_mult)=[<EMPTY>, ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP, VOID]", getSet(first, rules[i++]));
        assertEquals("first(member)=[ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP, VOID]", getSet(first, rules[i++]));
        assertEquals("first(method_type)=[BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, IDENTIFIER, INT, LONG, SHORT, VOID]", getSet(first, rules[i++]));
        assertEquals("first(type)=[BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, IDENTIFIER, INT, LONG, SHORT]", getSet(first, rules[i++]));
        assertEquals("first(primitive_type)=[BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, INT, LONG, SHORT]", getSet(first, rules[i++]));
        assertEquals("first(class_type)=[IDENTIFIER]", getSet(first, rules[i++]));
        assertEquals("first(formal_mult)=[ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP]", getSet(first, rules[i++]));
        assertEquals("first(method_body)=[BEGIN, SEMI]", getSet(first, rules[i++]));
        assertEquals("first(compound_statement)=[BEGIN]", getSet(first, rules[i++]));
        assertEquals("first(statement_mult)=[\"break\", \"continue\", \"for\", \"if\", \"return\", \"try\", \"while\", <EMPTY>, ABSTRACT, BEGIN, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SEMI, SHORT, STRICTFP, precedence(1)]", getSet(first, rules[i++]));
        assertEquals("first(statement)=[\"break\", \"continue\", \"for\", \"if\", \"return\", \"try\", \"while\", ABSTRACT, BEGIN, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SEMI, SHORT, STRICTFP, precedence(1)]", getSet(first, rules[i++]));
        assertEquals("first(declare_label_opt)=[<EMPTY>, lookahead(IDENTIFIER, COLON)]", getSet(first, rules[i++]));
        assertEquals("first(if_else_statement)=[\"if\"]", getSet(first, rules[i++]));
        assertEquals("first(else_opt)=[\"else\", <EMPTY>]", getSet(first, rules[i++]));
        assertEquals("first(while_statement)=[\"while\"]", getSet(first, rules[i++]));
        assertEquals("first(for_statement)=[\"for\"]", getSet(first, rules[i++]));
        assertEquals("first(try_statement)=[\"try\"]", getSet(first, rules[i++]));
        assertEquals("first(catch_plus)=[\"catch\", \"finally\"]", getSet(first, rules[i++]));
        assertEquals("first(catch_clause)=[\"catch\"]", getSet(first, rules[i++]));
        assertEquals("first(finally_clause)=[\"finally\"]", getSet(first, rules[i++]));
        assertEquals("first(break_statement)=[\"break\"]", getSet(first, rules[i++]));
        assertEquals("first(continue_statement)=[\"continue\"]", getSet(first, rules[i++]));
        assertEquals("first(label_opt)=[<EMPTY>, IDENTIFIER]", getSet(first, rules[i++]));
        assertEquals("first(return_statement)=[\"return\"]", getSet(first, rules[i++]));
        assertEquals("first(declare_statement)=[ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP]", getSet(first, rules[i++]));
        assertEquals("first(expression_statement)=[precedence(1)]", getSet(first, rules[i++]));
        assertEquals("first(expression_opt)=[<EMPTY>, precedence(1)]", getSet(first, rules[i++]));
        assertEquals("first(expression)=[precedence(1)]", getSet(first, rules[i++]));
        assertEquals("first(actual_opt)=[<EMPTY>, precedence(0)]", getSet(first, rules[i++]));
        assertEquals("Wrong number of rule", rules.length, i);
    }
}

