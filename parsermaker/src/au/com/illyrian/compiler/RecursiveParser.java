package au.com.illyrian.compiler;

import au.com.illyrian.compiler.ast.AstParser;
import au.com.illyrian.compiler.ast.AstParserFactory;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.PrecidenceParser;

public class RecursiveParser extends PrecidenceParser<AstParser>
{
    AstParserFactory factory;

    public RecursiveParser()
    {
        factory = new AstParserFactory();
    }

    protected Lexer createLexer()
    {
        return new ParserLexer();
    }

    /*
     * class_body ::= '{' rules_plus '}' 
     * rules_plus ::= parse_rule rules_plus 
     *            | parse_rule 
     * parse_rule ::= target '::=' alt_rule action_opt ';'
     * target     ::= name 
     * alt_rule   ::= seq_rule '|' alt_rule 
     *            | seq_rule 
     * seq_rule   ::= rule_token seq_rule 
     *            | EMPTY 
     * rule_token ::= name 
     *            |   name '(' param_opt ')' 
     *            |   string 
     *            |   reserved 
     * action_opt ::= '{' expr '}' 
     *            | EMPTY 
     * param_opt  ::= name
     * name       ::= IDENTIFIER
     * string     ::= STRING
     * reserved   ::= RESERVED
     * expr       ::= name
     */
    public AstParser parseClass() throws ParserException
    {
        nextToken();

        return class_body();
    }

    /* class_body ::= '{' rules_plus '}' { $2 } ; */
    public AstParser class_body() throws ParserException
    {
        AstParser result = null;
        expect(Lexer.OPEN_P, "{");
        result = rules_plus();
        expect(Lexer.CLOSE_P, "}");
        return result;
    }

    /* 
     * rules_plus ::= parse_rule lookahead(RPAR) <EMPTY>
     *            |   parse_rule rules_plus { factory.List($1, $2) }
     *            ;
     */
    public AstParser rules_plus() throws ParserException
    {
        AstParser $1 = parse_rule();
        if (match(Lexer.CLOSE_P, "}")) {
            return $1;
        } else {
            AstParser $2 = rules_plus();
            return factory.List($1, $2);
        }
    }

    /* parse_rule ::= rule_target '::=' rule_alt ';' { factory.Rule($1, $3) } ; */
    public AstParser parse_rule() throws ParserException
    {
        AstParser $1 = rule_target();
        expect(Lexer.OPERATOR, "::=");
        AstParser $3 = rule_alt();
        expect(Lexer.DELIMITER, ";", "'|', '=' or ';' expected");
        return factory.Rule($1, $3);
    }

    /*
     * rule_target ::= name | error("Name of rule expected") ;
     */
    public AstParser rule_target() throws ParserException
    {
        AstParser $1 = null;
        if (match(Lexer.IDENTIFIER, null)) {
            $1 = name();
        } else {
            throw error("Name of rule expected.");
        }
        // String type = null;
        // if (accept(Lexer.OPERATOR, ":"))
        // {
        // type = expect(Lexer.IDENTIFIER, null, "Return type expected.");
        // }
        return $1;
    }

    /*
     * rule_alt ::= rule_seq '|' rule_alt | rule_seq ;
     */
    public AstParser rule_alt() throws ParserException
    {
        AstParser $1 = rule_seq();
        if (accept(Lexer.OPERATOR, "|")) {
            AstParser $3 = rule_alt();
            return factory.Alt($1, $3);
        } else {
            return $1;
        }
    }

    /*
     * seq_rule ::= rule_action | lookahead('|' | ';') EMPTY | rule_token seq_rule ;
     */
    public AstParser rule_seq() throws ParserException
    {
        AstParser $$;
        if (match(Lexer.OPEN_P, "{")) {
            $$ = rule_action();
        } else if (match(Lexer.DELIMITER, ";") || match(Lexer.OPERATOR, "|")){
            $$ = factory.Empty();
        } else /*if (first$rule_token()) */ {
            AstParser $1 = rule_token();
            AstParser $3 = rule_seq();
            $$ = factory.Seq($1, $3);
        }
        return $$;
    }

    /*
     * rule_action ::= '{' ... '}'
     */
    public AstParser rule_action() throws ParserException
    {
        // FIXME create rule action
        expect(Lexer.OPEN_P, "{");
        while (!match(Lexer.CLOSE_P, "}"))
            nextToken();
        expect(Lexer.CLOSE_P, "}");
        return factory.Empty();
    }

    boolean first$rule_token() {
        return (match(Lexer.IDENTIFIER) || match(Lexer.STRING) || match(Lexer.RESERVED));
    }
    
    /*
     * rule_token ::= name '(' actual_opt ')' = { factory.Call($1, $3) } 
     *            |   name
     *            |   STRING   = { factory.String(getLexer()) } 
     *            |   RESERVED = { factory.Reserved(getLexer()) } ;
     */
    public AstParser rule_token() throws ParserException
    {
        AstParser $$;
        if (match(Lexer.IDENTIFIER, null)) {
            AstParser $1 = name();
            if (accept(Lexer.OPEN_P, "(")) {
                AstParser $3 = actual_opt();
                expect(Lexer.CLOSE_P, ")");
                $$ = factory.Call($1, $3);
            } else {
                $$ = $1;
            }
        } else if (match(Lexer.STRING, null)) {
            $$ = factory.String(getLexer());
        } else if (match(Lexer.RESERVED, null)) {
            $$ = factory.Reserved(getLexer());
        } else {
            throw new IllegalStateException("Rule rule_token has no option for input: " + getLexer());
        }
        return $$;
    }

    /*
     * actual_opt ::= lookahead(RPAR) EMPTY | param_mult ;
     */
    public AstParser actual_opt() throws ParserException
    {
        if (match(Lexer.CLOSE_P, ")")) {
            return null;
        } else {
            return param_mult();
        }
    }

    /*
     * param_mult ::= actual COMMA param_mult { factory.Comma($1, $3) } 
     *            |   actual 
     *            ;
     */
    public AstParser param_mult() throws ParserException
    {
        AstParser $1 = actual();
        if (accept(Lexer.DELIMITER, ",")) {
            AstParser $3 = param_mult();
            return factory.Comma($1,  $3);
        } else {
            return $1;
        }
    }

    /*
     * actual ::= INTEGER { factory.Integer(getLexer()) }
     *        |   DECIMAL { factory.Decimal(getLexer()) }
     *        |   rule_token ;
     */
    public AstParser actual() throws ParserException
    {
        AstParser $$; 
        if (match(Lexer.INTEGER, null)) {
            $$ = factory.Integer(getLexer());
        } else if (match(Lexer.DECIMAL, null)) {
            $$ = factory.Decimal(getLexer());
        } else {
            $$ = rule_token();
        }
        return $$;
    }

    /*
     * name ::= IDENTIFIER { factory.Name(getLexer()) }
     *      |   error("NameExpected")
     *      ;
     */
    public AstParser name() throws ParserException
    {
        AstParser $$;
        if (match(Lexer.IDENTIFIER, null)) {
            $$ = factory.Name(getLexer());
        } else {
            throw error("NameExpected");
        }
        return $$;
    }

    public ParserException error(String message)
    {
        return getCompileUnit().error(getInput(), message);
    }

}