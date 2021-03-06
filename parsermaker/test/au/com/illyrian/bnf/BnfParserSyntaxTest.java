package au.com.illyrian.bnf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;
import au.com.illyrian.bnf.ast.BnfFirstSet;
import au.com.illyrian.bnf.ast.BnfTree;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.ast.BnfTreeRule;
import au.com.illyrian.bnf.parser.BnfParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.ModuleContext;

public class BnfParserSyntaxTest extends TestCase
{
    public static final String JESUB_SYNTAX_BNF = "test/au/com/illyrian/bnf/Jesub_syntax.bnf";
    
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
        assertEquals("Wrong line count", 249, count);
    }

    public void testRawJesubSyntax() throws Exception {
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        Input input = new LexerInputStream(reader, JESUB_SYNTAX_BNF);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        // Avoid parseMembers(ModuleContext) as it includes merging of common states.
        parser.setCompilerContext(compile);
        parser.nextToken();
        BnfTreeParser tree = parser.class_body();
        
        // Verify that all input has been parsed.
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        
        int i = 0;
        BnfTreeRule [] rules = tree.toRuleArray();
        
        assertEquals("Wrong rule", "goal ::= compilation_unit . ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "name:TerminalName ::= IDENTIFIER {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "qualified_name:AstExpression ::= ( name DOT qualified_name {} | name . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "modifier_mult:AstModifiers ::= ( PUBLIC modifier_mult {} | PROTECTED modifier_mult {} | PRIVATE modifier_mult {} | ABSTRACT modifier_mult {} | FINAL modifier_mult {} | STRICTFP modifier_mult {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "compilation_unit ::= package_opt import_mult class_declaration_plus {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "package_opt:AstPackage ::= ( PACKAGE qualified_name SEMI {} | PACKAGE error(\"IncompletePackageName\") . | . | RECOVER(( IMPORT | class_declaration )) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "import_mult ::= ( IMPORT import_path_plus import_mult {} | IMPORT import_path_plus {} | . | RECOVER(( IMPORT | class_declaration )) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "import_path_plus:AstExpression ::= ( name DOT import_path_plus {} | name SEMI {} | MULT SEMI {} | error(\"IncompleteImportPath\") . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "class_declaration_plus ::= ( class_declaration class_declaration_plus {} | class_declaration . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "class_declaration ::= ( modifier_mult CLASS name extends_opt implements_opt class_body {} | modifier_mult INTERFACE name extends_opt class_body {} | error(\"ExpectedClassOrInterface\") . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "extends_opt:AstExpression ::= ( EXTENDS qualified_name {} | EXTENDS error(\"ExpectedExtendedClassName\") . | . | RECOVER(( IMPLEMENTS | BEGIN )) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "implements_opt:AstExpression ::= ( IMPLEMENTS implements_plus {} | IMPLEMENTS error(\"ExpectedImplementedInterfaceName\") . | . | RECOVER(BEGIN) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "implements_plus:AstExpression ::= ( qualified_name COMMA implements_plus {} | qualified_name . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "class_body ::= BEGIN member_mult END {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "member_mult ::= ( member member_mult {} | . | RECOVER(member) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "member ::= ( modifier_mult method_type name LPAR formal_mult RPAR method_body {} | modifier_mult method_type name SEMI {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "method_type:AstExpression ::= ( type . | VOID {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "type:AstExpression ::= ( class_type . | class_type LBRAC RBRAC {} | primitive_type . | primitive_type LBRAC RBRAC {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "primitive_type:AstExpression ::= ( BOOLEAN {} | BYTE {} | CHAR {} | SHORT {} | INT {} | LONG {} | FLOAT {} | DOUBLE {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "class_type:AstExpression ::= qualified_name . ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "formal_mult ::= ( formal_param formal_mult {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "formal_param ::= modifier_mult type name {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "method_body ::= ( compound_statement . | SEMI {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "compound_statement ::= BEGIN statement_mult END {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "statement_mult ::= ( LOOKAHEAD(END) . | statement statement_mult {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "statement ::= ( compound_statement . | if_else_statement . | try_statement . | break_statement . | continue_statement . | return_statement . | labeled_statement . | SEMI {} | declare_statement . | expression_statement . | RECOVER(statement) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "labeled_statement ::= ( while_statement . | for_statement . ) ;", rules[i++].toRuleString());
//        assertEquals("Wrong rule", "declare_label_opt ::= ( LOOKAHEAD(IDENTIFIER COLON) name COLON {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "if_else_statement ::= IF LPAR expr RPAR statement else_opt {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "else_opt ::= ( ELSE statement {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "while_statement ::= WHILE LPAR expr RPAR statement {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "for_statement ::= FOR LPAR expression_opt SEMI expression_opt SEMI expression_opt RPAR statement {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "try_statement ::= TRY compound_statement catch_plus finally_opt {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "catch_plus ::= ( catch_clause catch_plus {} | catch_clause . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "catch_clause ::= CATCH LPAR class_type name RPAR compound_statement {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "finally_opt ::= ( FINALLY compound_statement {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "break_statement ::= BREAK label_opt SEMI {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "continue_statement ::= CONTINUE label_opt SEMI {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "label_opt:TerminalName ::= ( IDENTIFIER {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "return_statement ::= ( RETURN SEMI {} | RETURN expr SEMI {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "declare_statement ::= modifier_mult type name SEMI {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "expression_statement ::= expr SEMI {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "expression_opt:AstExpression ::= ( expr . | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "expr:AstExpression ::= expression() . ;", rules[i++].toRuleString());
        assertEquals("Wrong number of rule", rules.length, i);
    }
    
    public void testMergedJesubSyntax() throws Exception {
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        Input input = new LexerInputStream(reader, JESUB_SYNTAX_BNF);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTree tree = parser.parseMembers(compile);
        
        int i = 0;
        BnfTreeRule [] rules = tree.toRuleArray();
        
        assertEquals("Wrong rule", "goal ::= compilation_unit() . ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "name:TerminalName ::= IDENTIFIER {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "qualified_name:AstExpression ::= name() ( DOT qualified_name() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "modifier_mult:AstModifiers ::= ( PUBLIC modifier_mult() {} | PROTECTED modifier_mult() {} | PRIVATE modifier_mult() {} | ABSTRACT modifier_mult() {} | FINAL modifier_mult() {} | STRICTFP modifier_mult() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "compilation_unit ::= package_opt() import_mult() class_declaration_plus() {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "package_opt:AstPackage ::= ( PACKAGE ( qualified_name() SEMI {} | error(\"IncompletePackageName\") . ) | . | RECOVER(( IMPORT | class_declaration )) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "import_mult ::= ( IMPORT import_path_plus() ( import_mult() {} | {} ) | . | RECOVER(( IMPORT | class_declaration )) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "import_path_plus:AstExpression ::= ( name() ( DOT import_path_plus() {} | SEMI {} ) | MULT SEMI {} | error(\"IncompleteImportPath\") . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "class_declaration_plus ::= class_declaration() ( class_declaration_plus() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "class_declaration ::= ( modifier_mult() ( CLASS name() extends_opt() implements_opt() class_body() {} | INTERFACE name() extends_opt() class_body() {} ) | error(\"ExpectedClassOrInterface\") . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "extends_opt:AstExpression ::= ( EXTENDS ( qualified_name() {} | error(\"ExpectedExtendedClassName\") . ) | . | RECOVER(( IMPLEMENTS | BEGIN )) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "implements_opt:AstExpression ::= ( IMPLEMENTS ( implements_plus() {} | error(\"ExpectedImplementedInterfaceName\") . ) | . | RECOVER(BEGIN) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "implements_plus:AstExpression ::= qualified_name() ( COMMA implements_plus() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "class_body ::= BEGIN member_mult() END {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "member_mult ::= ( member() member_mult() {} | . | RECOVER(member) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "member ::= modifier_mult() method_type() name() ( LPAR formal_mult() RPAR method_body() {} | SEMI {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "method_type:AstExpression ::= ( type() . | VOID {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "type:AstExpression ::= ( class_type() ( LBRAC RBRAC {} | . ) | primitive_type() ( LBRAC RBRAC {} | . ) ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "primitive_type:AstExpression ::= ( BOOLEAN {} | BYTE {} | CHAR {} | SHORT {} | INT {} | LONG {} | FLOAT {} | DOUBLE {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "class_type:AstExpression ::= qualified_name() . ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "formal_mult ::= ( formal_param() formal_mult() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "formal_param ::= modifier_mult() type() name() {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "method_body ::= ( compound_statement() . | SEMI {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "compound_statement ::= BEGIN statement_mult() END {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "statement_mult ::= ( statement() statement_mult() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "statement ::= ( compound_statement() . | if_else_statement() . | try_statement() . | break_statement() . | continue_statement() . | return_statement() . | labeled_statement() . | SEMI {} | declare_statement() . | expression_statement() . | RECOVER(statement) . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "labeled_statement ::= ( while_statement() . | for_statement() . ) ;", rules[i++].toRuleString());
        //assertEquals("Wrong rule", "declare_label_opt ::= ( LOOKAHEAD(IDENTIFIER COLON) name() COLON {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "if_else_statement ::= IF LPAR expr() RPAR statement() else_opt() {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "else_opt ::= ( ELSE statement() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "while_statement ::= WHILE LPAR expr() RPAR statement() {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "for_statement ::= FOR LPAR expression_opt() SEMI expression_opt() SEMI expression_opt() RPAR statement() {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "try_statement ::= TRY compound_statement() catch_plus() finally_opt() {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "catch_plus ::= catch_clause() ( catch_plus() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "catch_clause ::= CATCH LPAR class_type() name() RPAR compound_statement() {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "finally_opt ::= ( FINALLY compound_statement() {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "break_statement ::= BREAK label_opt() SEMI {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "continue_statement ::= CONTINUE label_opt() SEMI {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "label_opt:TerminalName ::= ( IDENTIFIER {} | . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "return_statement ::= RETURN ( SEMI {} | expr() SEMI {} ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "declare_statement ::= modifier_mult() type() name() SEMI {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "expression_statement ::= expr() SEMI {} ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "expression_opt:AstExpression ::= ( LOOKAHEAD(( SEMI | RPAR )) . | expr() . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "actual_opt:AstExpression ::= ( LOOKAHEAD(RPAR) . | expr() . ) ;", rules[i++].toRuleString());
        assertEquals("Wrong rule", "expr:AstExpression ::= expression() . ;", rules[i++].toRuleString());
        assertEquals("Wrong number of rule", rules.length, i);
    }
    
    private String getSet(BnfTreeRule rule) {
        BnfFirstSet set = rule.getFirstSet();
        return set.toString();
    }

    public void testFirstJesubSyntax() throws Exception {
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        Input input = new LexerInputStream(reader, JESUB_SYNTAX_BNF);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        
        BnfParser parser = new BnfParser();
        BnfTreeParser tree = parser.parseMembers(compile);
        assertEquals("token", TokenType.END, parser.getLexer().nextToken());
        
        int i = 0;
        BnfTreeRule [] rules = tree.toRuleArray();
        
        assertEquals("first(goal)=[ABSTRACT, CLASS, FINAL, IMPORT, INTERFACE, PACKAGE, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(rules[i++]));
        assertEquals("first(name)=[IDENTIFIER]", getSet(rules[i++]));
        assertEquals("first(qualified_name)=[IDENTIFIER]", getSet(rules[i++]));
        assertEquals("first(modifier_mult)=[<EMPTY>, ABSTRACT, FINAL, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(rules[i++]));
        assertEquals("first(compilation_unit)=[ABSTRACT, CLASS, FINAL, IMPORT, INTERFACE, PACKAGE, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(rules[i++]));
        assertEquals("first(package_opt)=[<EMPTY>, PACKAGE]", getSet(rules[i++]));
        assertEquals("first(import_mult)=[<EMPTY>, IMPORT]", getSet(rules[i++]));
        assertEquals("first(import_path_plus)=[IDENTIFIER, MULT]", getSet(rules[i++]));
        assertEquals("first(class_declaration_plus)=[ABSTRACT, CLASS, FINAL, INTERFACE, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(rules[i++]));
        assertEquals("first(class_declaration)=[ABSTRACT, CLASS, FINAL, INTERFACE, PRIVATE, PROTECTED, PUBLIC, STRICTFP]", getSet(rules[i++]));
        assertEquals("first(extends_opt)=[<EMPTY>, EXTENDS]", getSet(rules[i++]));
        assertEquals("first(implements_opt)=[<EMPTY>, IMPLEMENTS]", getSet(rules[i++]));
        assertEquals("first(implements_plus)=[IDENTIFIER]", getSet(rules[i++]));
        assertEquals("first(class_body)=[BEGIN]", getSet(rules[i++]));
        assertEquals("first(member_mult)=[<EMPTY>, ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP, VOID]", getSet(rules[i++]));
        assertEquals("first(member)=[ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP, VOID]", getSet(rules[i++]));
        assertEquals("first(method_type)=[BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, IDENTIFIER, INT, LONG, SHORT, VOID]", getSet(rules[i++]));
        assertEquals("first(type)=[BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, IDENTIFIER, INT, LONG, SHORT]", getSet(rules[i++]));
        assertEquals("first(primitive_type)=[BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, INT, LONG, SHORT]", getSet(rules[i++]));
        assertEquals("first(class_type)=[IDENTIFIER]", getSet(rules[i++]));
        assertEquals("first(formal_mult)=[<EMPTY>, ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP]", getSet(rules[i++]));
        assertEquals("first(formal_param)=[ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP]", getSet(rules[i++]));
        assertEquals("first(method_body)=[BEGIN, SEMI]", getSet(rules[i++]));
        assertEquals("first(compound_statement)=[BEGIN]", getSet(rules[i++]));
        assertEquals("first(statement_mult)=[ABSTRACT, BEGIN, BOOLEAN, BREAK, BYTE, CHAR, CONTINUE, DOUBLE, FINAL, FLOAT, FOR, IDENTIFIER, IF, INT, LONG, LOOKAHEAD(END), PRIVATE, PROTECTED, PUBLIC, RETURN, SEMI, SHORT, STRICTFP, TRY, WHILE]", getSet(rules[i++]));
        assertEquals("first(statement)=[ABSTRACT, BEGIN, BOOLEAN, BREAK, BYTE, CHAR, CONTINUE, DOUBLE, FINAL, FLOAT, FOR, IDENTIFIER, IF, INT, LONG, PRIVATE, PROTECTED, PUBLIC, RETURN, SEMI, SHORT, STRICTFP, TRY, WHILE]", getSet(rules[i++]));
        assertEquals("first(labeled_statement)=[FOR, WHILE]", getSet(rules[i++]));
        //assertEquals("first(declare_label_opt)=[<EMPTY>, LOOKAHEAD(IDENTIFIER COLON)]", getSet(rules[i++]));
        assertEquals("first(if_else_statement)=[IF]", getSet(rules[i++]));
        assertEquals("first(else_opt)=[<EMPTY>, ELSE]", getSet(rules[i++]));
        assertEquals("first(while_statement)=[WHILE]", getSet(rules[i++]));
        assertEquals("first(for_statement)=[FOR]", getSet(rules[i++]));
        assertEquals("first(try_statement)=[TRY]", getSet(rules[i++]));
        assertEquals("first(catch_plus)=[CATCH]", getSet(rules[i++]));
        assertEquals("first(catch_clause)=[CATCH]", getSet(rules[i++]));
        assertEquals("first(finally_opt)=[<EMPTY>, FINALLY]", getSet(rules[i++]));
        assertEquals("first(break_statement)=[BREAK]", getSet(rules[i++]));
        assertEquals("first(continue_statement)=[CONTINUE]", getSet(rules[i++]));
        assertEquals("first(label_opt)=[<EMPTY>, IDENTIFIER]", getSet(rules[i++]));
        assertEquals("first(return_statement)=[RETURN]", getSet(rules[i++]));
        assertEquals("first(declare_statement)=[ABSTRACT, BOOLEAN, BYTE, CHAR, DOUBLE, FINAL, FLOAT, IDENTIFIER, INT, LONG, PRIVATE, PROTECTED, PUBLIC, SHORT, STRICTFP]", getSet(rules[i++]));
        assertEquals("first(expression_statement)=[]", getSet(rules[i++]));
        assertEquals("first(expression_opt)=[LOOKAHEAD(( SEMI | RPAR ))]", getSet(rules[i++]));
        //assertEquals("first(expression)=[]", getSet(rules[i++]));
        assertEquals("first(actual_opt)=[LOOKAHEAD(RPAR)]", getSet(rules[i++]));
        assertEquals("first(expr)=[]", getSet(rules[i++]));
        assertEquals("Wrong number of rules", rules.length, i);
    }
}

