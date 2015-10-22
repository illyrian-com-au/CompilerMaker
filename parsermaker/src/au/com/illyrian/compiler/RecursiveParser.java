package au.com.illyrian.compiler;

import au.com.illyrian.compiler.ast.AstParser;
import au.com.illyrian.compiler.ast.AstParserFactory;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserBase;

public class RecursiveParser extends ParserBase
{
    AstParserFactory factory;
    Lexer lexer;
    
    public RecursiveParser()
    {
        setLexer(createLexer());
        factory = new AstParserFactory();
    }
    
    protected Latin1Lexer createLexer()
    {
        return new Latin1Lexer();
    }
    
    public Lexer getLexer()
    {
        return lexer;
    }
    
    /**
     *  class_body  ::= '{' rules_plus '}'
     *  rules_plus  ::= parse_rule rules_plus | parse_rule
     *  parse_rule  ::= target '::=' rule_expr action_opt
     *  action_opt  ::= '{' expr '}' | EMPTY
     *  target      ::= name
     *  alt_rule    ::= seq_rule '|' alt_rule | seq_rule
     *  seq_rule    ::= rule_token seq_rule | EMPTY 
     *  rule_token  ::= name | name '(' param_opt ')' | string | reserved
     *  param_opt   ::= name
     *  expr        ::=  
     */
    public AstParser parseClass() throws ParserException
    {
        nextToken();
        
        return class_body();
    }
    
    public AstParser class_body() throws ParserException
    {
        AstParser result = null;
        expect(Lexer.OPEN_P, "{");
        result = rules_plus();
        expect(Lexer.CLOSE_P, "}");
        return result;
    }

    public AstParser rules_plus() throws ParserException
    {
        AstParser $$ = null;
        AstParser $1 = parse_rule();
        if (!match(Lexer.CLOSE_P, "}"))
        {
            AstParser $2 = rules_plus();
            $$ = factory.Seq($1, $2);
        } else {
            $$ = $1;
        }
        return $$;
    }

    /*  parse_rule  ::= rule_target '::=' rule_alt action_opt ';' */
    public AstParser parse_rule() throws ParserException
    {
        AstParser $1 = rule_target();
        expect(Lexer.OPERATOR, "::=");
        AstParser $3 = rule_alt();
        AstParser $4 = action_opt();
        expect(Lexer.DELIMITER, ";");
        return factory.Rule($1, $3); // FIXME - add $4
    }
    
    /* action_opt ::= '=' rule_action
     *            |   EMPTY
     *            ;
     */
    public AstParser action_opt() throws ParserException
    {
        if (accept(Lexer.OPERATOR, "=")) {
            return rule_action();
        } else {
            return null;
        }
    }
    
    public AstParser rule_action() throws ParserException
    {
        return null;
    }
    
    /*  rule_target  ::= IDENTIFIER 
     *               | error("Name of rule expected") 
     *               ;
     */
    public AstParser rule_target() throws ParserException
    {
        AstParser $1 = null;
        if (match(Lexer.IDENTIFIER, null))
        {
            $1 = name();
        } else {
            error("Name of rule expected.");
        }
//        String type = null;
//        if (accept(Lexer.OPERATOR, ":"))
//        {
//            type = expect(Lexer.IDENTIFIER, null, "Return type expected.");
//        }
        return $1;
    }
    
    /*  rule_alt  ::= rule_seq '|' rule_alt
     *            |   rule_seq
     *            ;
      */
    public AstParser rule_alt() throws ParserException
    {
        AstParser $1 = rule_seq();
        if (match(Lexer.OPERATOR, "|"))
        {
            AstParser $3 = rule_alt();
            return factory.Alt($1,  $3);
        } else {
            return $1;
        }
    }
    
    /*  rule_seq  ::= rule_token ',' rule_seq
     *            |   rule_token
     *            ; 
     */
    public AstParser rule_seq() throws ParserException
    {
        AstParser $1 = rule_token();
        if (match(Lexer.OPERATOR, ","))
        {
            AstParser $3 = rule_seq();
            return factory.Seq($1,  $3);
        } else {
            return $1;
        }
    }

    /*  rule_token  ::= name '(' actual_opt ')' 
     *              | name 
     *              | string 
     *              | reserved 
     *              | EMPTY
     *              ;
     */
    public AstParser rule_token() throws ParserException
    {
        if (match(Lexer.IDENTIFIER, null))
        {
            AstParser $1 = name();
            if (accept(Lexer.OPEN_P, "("))
            {
                AstParser $3;
                if (match(Lexer.CLOSE_P, ")")) {
                    $3 = null;
                } else {
                    $3 = actual_opt();
                }
                expect(Lexer.CLOSE_P, ")");
                return factory.Call($1, $3);
            }
            return $1;
        } else if (match(Lexer.STRING, null)) {
            return string();
        } else if (match(Lexer.RESERVED, null)) {
            return string();
        } else {
            return null;
        }
    }

    public AstParser actual_opt()
    {
        return factory.Name(getLexer().getTokenValue());
    }
    
    public AstParser name()
    {
        return factory.Name(getLexer().getTokenValue());
    }
    
    public AstParser string()
    {
        return factory.String(getLexer().getTokenString());
    }
    
    public void error(String message) throws ParserException
    {
        throw new ParserException(message);
    }

}
