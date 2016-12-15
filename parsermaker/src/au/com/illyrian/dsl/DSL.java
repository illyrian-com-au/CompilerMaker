package au.com.illyrian.dsl;

import au.com.illyrian.dsl.ast.DslActionAstFactory;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserBase;

/**
 * Compiler generator for Domain Specific Languages (DLS)
 *  
 * @author dstrong
 */
public class DSL extends ParserBase implements ParseClass
{
    DslAction dslAction = null;
    
    public DSL()
    {
        setLexer(createLexer());
    }
    
    protected Latin1Lexer createLexer()
    {
        return new DSLLexer();
    }
    
    protected DslAction createDslAction()
    {
        return new DslActionAstFactory();
    }
   
    public DslAction getDslAction()
    {
        if (dslAction == null)
            dslAction = createDslAction();
        return dslAction;
    }

    public void setDslAction(DslAction dslAction)
    {
        this.dslAction = dslAction;
    }

    /**
     *  class_body  ::= '{' many_rules '}'
     *  many_rules  ::= parse_rule { parse_rule }
     *  parse_rule  ::= target '::=' rule_expr [ '=' rule_action ] ';'
     *  target      ::= IDENTIFIER { ':' IDENTIFIER }
     *  rule_expr   ::= rule_fact { ',' rule_fact }
     *  rule_fact   ::= rule_term | '[' rule_expr ']' | '{' rule_expr '}' | '(' rule_alt ')' 
     *  rule_alt    ::= rule_term { '|' rule_elem }
     *  alt_string  ::= STRING { '|' STRING }
     *  alt_ident   ::= IDENTIFIER { '|' IDENTIFIER }
     *  rule_term   ::= STRING | IDENTIFIER | DELIMITER
     *  rule_action ::= '{' { sub_action } '}' 
     *  sub_action  ::= { qualifier '(' java_expr ')' } 
     *  java_expr   ::= 
     */
    public Object parseClass(CompilerContext context)
    {
        setCompilerContext(context);
        getDslAction();
        nextToken();
        
        return class_body();
    }
    
    public Object class_body()
    {
        expect(TokenType.DELIMITER, "{", "'{' expected.");
        Object result = many_rules();
        if (!match(TokenType.DELIMITER, "}"))
            throw error("'}' expected.");
        return result;
    }

    public Object many_rules()
    {
        Object ruleList = dslAction.newDslLanguage(null);
        while (true)
        {
            Object rule = parse_rule();
            ruleList = dslAction.addDslRule(ruleList, rule);
            if (match(TokenType.DELIMITER, "}"))
                break;
            else if (match(TokenType.END, null))
                break;
        }
        return ruleList;
    }

    /*  parse_rule  ::= rule_target '::=' rule_expr [ '=' rule_action ] */
    public Object parse_rule()
    {
        Object target = rule_target();
        expect(TokenType.OPERATOR, "::=", "'::=' expected.");
        Object expr = rule_expr();
        if (accept(TokenType.OPERATOR, "="))
            rule_action();
        expect(TokenType.DELIMITER, ";", null);
        return dslAction.newDslRule(target, expr);
    }
    
    /*  rule_target  ::= IDENTIFIER [ ':' IDENTIFIER ] */
    public Object rule_target()
    {
        String name = expect(TokenType.IDENTIFIER, null, "Name of rule expected.");
        String type = null;
        if (accept(TokenType.OPERATOR, ":"))
        {
            type = expect(TokenType.IDENTIFIER, null, "Return type expected.");
        }
        return dslAction.newDslTarget(name, type);
    }

    /*  rule_expr  ::= rule_alt { ',' rule_alt } ';' */
    public Object rule_expr() 
    {
        Object expr = rule_alt();
        while (true)
        {
            if (accept(TokenType.DELIMITER, ","))
            {
                Object item = rule_alt();
                expr = dslAction.addDslSequence(expr, item);
            }
            else if (match(TokenType.END, null))
                break;
            else if (match(TokenType.DELIMITER, ";"))
                break;
            else if (match(TokenType.DELIMITER, ")"))
                break;
            else if (match(TokenType.DELIMITER, "]"))
                break;
            else
                throw error(", or | expected.");
        }
        return expr;
    }

    /*  rule_alt    ::= rule_elem { '|' rule_elem } */
    public Object rule_alt() 
    {
        Object expr = rule_elem();
        while (true)
        {
            if (accept(TokenType.OPERATOR, "|"))
            {
                Object item = rule_elem();
                expr = dslAction.addDslAlternative(expr, item);
            }
            else if (match(TokenType.END, null))
                break;
            else
                break;
        }
        return expr;
    }

    /*  rule_elem   ::= IDENTIFIER | STRING | '[' rule_expr ']' | '{' rule_expr '}' | '(' rule_expr ')' */ 
    public Object rule_elem() 
    {
        Object expr = null;
        if (match(TokenType.IDENTIFIER, null))
        {
            String name = getLexer().getTokenValue();
            nextToken();
            expr = dslAction.newDslIdentifier(name);
        }
        else if (match(TokenType.STRING, "\""))
        {
            String name = getLexer().getTokenString();
            nextToken();
            expr = dslAction.newDslString(name);
        }
        else if (match(TokenType.STRING, "\'"))
        {
            String name = getLexer().getTokenString();
            nextToken();
            expr = dslAction.newDslDelimiter(name);
        }
        else if (accept(TokenType.DELIMITER, "["))
        {
            expr = rule_expr();
            expect(TokenType.DELIMITER, "]", null);
            expr = dslAction.newDslOptional(expr);
        }
        else if (accept(TokenType.DELIMITER, "{"))
        {
            expr = rule_expr();
            expect(TokenType.DELIMITER, "}", null);
        }
        else if (accept(TokenType.DELIMITER, "("))
        {
            expr = rule_expr();
            expect(TokenType.DELIMITER, ")", null);
        }
        else if (match(TokenType.END))
            ;
        else
            throw error("Terminal, non-terminal or rule construct expected");
        return expr;
    }
    
    public void rule_action ()  
    {
        expect(TokenType.DELIMITER, "{", "'{' expected.");
        expect(TokenType.DELIMITER, "}", "'}' expected.");
    }
}
