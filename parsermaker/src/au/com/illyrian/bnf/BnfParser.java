package au.com.illyrian.bnf;

import au.com.illyrian.bnf.ast.BnfTree;
import au.com.illyrian.bnf.ast.BnfTreeExpression;
import au.com.illyrian.bnf.ast.BnfTreeFactory;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.expr.AstExpressionPrecidenceParser;

public class BnfParser extends AstExpressionPrecidenceParser implements ParseMembers
{
    BnfTreeFactory factory;

    public BnfParser()
    {
        factory = new BnfTreeFactory();
        this.setAstExpressionFactory(factory);
        addReservedMacros();
    }
    
    void addReservedMacros() {
        addReserved("LOOKAHEAD");
        addReserved("RECOVER");
        addReserved("EMPTY");
    }

    protected Lexer createLexer()
    {
        return new BnfLexer();
    }

    /*
     * class_body ::= '{' rules_plus '}' 
     * rules_plus ::= parse_rule rules_plus 
     *            | parse_rule 
     * parse_rule ::= target '::=' alt_rule action_opt ';'
     * target     ::= name() 
     * type_opt   ::= ':' type | EMPTY 
     * alt_rule   ::= seq_rule '|' alt_rule 
     *            | seq_rule 
     * seq_rule   ::= rule_token seq_rule 
     *            | EMPTY 
     * rule_token ::= name 
     *            |   name '(' param_opt ')' 
     *            |   string 
     *            |   reserved 
     * action_opt ::= '{' expression '}' 
     *            | EMPTY 
     * param_opt  ::= name
     * name       ::= IDENTIFIER
     */
    public BnfTree parseMembers(CompilerContext context) throws ParserException
    {
        setCompilerContext(context);

        nextToken();
        return class_body();
    }

    /* class_body ::= BEGIN rules_plus END { $2 } ; */
    public BnfTree class_body() throws ParserException
    {
        expect(BnfToken.BEGIN);
        BnfTree $2 = rules_plus();
        if (!match(BnfToken.END)) {
            throw error("} expected");
        }
        //return $2;
        return factory.Parser($2);
    }

    /* 
     * rules_plus ::= parse_rule lookahead(END)
     *            |   parse_rule rules_plus { factory.List($1, $2) }
     *            ;
     */
    public BnfTree rules_plus() throws ParserException
    {
        BnfTree $1 = parse_rule();
        if (match(BnfToken.END)) {
            return $1;
        } else {
            BnfTree $2 = rules_plus();
            return factory.List($1, $2);
        }
    }

    /* parse_rule ::= rule_target ASSIGN rule_alt SEMI { factory.Rule($1, $3) } ; */
    public BnfTree parse_rule() throws ParserException
    {
        BnfTree $1 = rule_target();
        expect(BnfToken.ASSIGN);
        BnfTree $3 = rule_alt();
        expect(BnfToken.SEMI);
        return factory.Rule($1, $3);
    }

    /*
     * rule_target ::= name | name COLON name | error("Name of rule expected") ;
     */
    public BnfTree rule_target() throws ParserException
    {
        BnfTree $$ = null;
        if (match(BnfToken.IDENTIFIER)) {
            BnfTree $1 = name();
            if (accept(BnfToken.COLON))
            {
                BnfTree $3 = name();
                $$ = factory.Target($1, $3);
            } else {
                $$ = $1;
            }
        } else {
            throw error("Name of rule expected.");
        }
        return $$;
    }

    /*
     * type_opt ::= COLON name | EMPTY ;
     */
    public BnfTree type_opt() throws ParserException
    {
        BnfTree $1 = null;
        if (accept(BnfToken.COLON))
        {
            $1 = name();
        }
        return $1;
    }

    /*
     * rule_alt ::= rule_seq ALT rule_alt | rule_seq ;
     */
    public BnfTree rule_alt() throws ParserException
    {
        BnfTree $1 = rule_seq();
        if (accept(BnfToken.ALT)) {
            BnfTree $3 = rule_alt();
            return factory.Alt($1, $3);
        } else {
            return $1;
        }
    }

    /*
     * seq_rule ::= rule_action | lookahead(ALT | SEMI) EMPTY | rule_token seq_rule ;
     */
    public BnfTree rule_seq() throws ParserException
    {
        BnfTree $$;
        if (match(BnfToken.BEGIN)) {
            $$ = rule_action();
        } else if (match(BnfToken.SEMI) || match(BnfToken.ALT)){
            $$ = factory.Empty();
        } else {
            BnfTree $1 = rule_token();
            BnfTree $2 = rule_seq();
            $$ = factory.Seq($1, $2);
        }
        return $$;
    }

    /*
     * rule_action ::= BEGIN expression END ;
     */
    public BnfTree rule_action() throws ParserException
    {
        expect(BnfToken.BEGIN);
        AstExpression expr = expression();
        expect(BnfToken.END);
        return new BnfTreeExpression(expr);
    }

    /*
     * rule_token ::= lookahead(IDENTIFIER) name_method
     *            |   lookahead(RESERVED) macro 
     *            |   error("Terminal, Non-terminal or Macro expected");
     */
    public BnfTree rule_token() throws ParserException
    {
        BnfTree $$;
        if (match(BnfToken.IDENTIFIER)) {
            $$ = name_method();
        } else if (match(BnfToken.RESERVED)) {
            $$ = macro();
        } else {
            throw error("Terminal, Non-terminal or Macro expected");
        }
        return $$;
    }

    /*
     * actual_opt ::= lookahead(RPAR) EMPTY | param_mult ;
     */
    public AstExpression actual_opt() throws ParserException
    {
        if (match(BnfToken.RPAR)) {
            return factory.Empty();
        } else {
            return param_mult();
        }
    }

    /*
     * param_mult ::= actual COMMA param_mult { factory.Comma($1, $3) } 
     *            |   actual 
     *            ;
     */
    public AstExpression param_mult() throws ParserException
    {
        AstExpression $$; 
        AstExpression $1 = actual();
        if (accept(BnfToken.COMMA)) {
            AstExpression $3 = param_mult();
            $$ = factory.Comma($1,  $3);
        } else {
            $$ = $1;
        }
        return $$;
    }

    /*
     * actual ::= NUMBER  { factory.Integer(getLexer()) }
     *        |   DECIMAL { factory.Decimal(getLexer()) }
     *        |   STRING  { factory.String(getLexer()) }
     *        |   error("Function parameter expected") ;
     */
    public AstExpression actual() throws ParserException
    {
        AstExpression $$; 
        if (match(TokenType.NUMBER)) {
            $$ = factory.Literal(getLexer().getTokenInteger());
        } else if (match(TokenType.DECIMAL)) {
            $$ = factory.Literal(getLexer().getTokenFloat());
        } else if (match(TokenType.STRING)) {
            $$ = factory.Literal(getLexer().getTokenString());
        } else {
            throw error("Function parameter expected");
        }
        nextToken();
        return $$;
    }

    /*
     * name_method ::=   name LPAR actual_opt RPAR = { factory.MethodCall($1, $3) } 
     *               |   name ; 
     */
    public BnfTree name_method() throws ParserException
    {
        BnfTree $$;
        BnfTree $1 = name();
        if (accept(BnfToken.LPAR)) {
            AstExpression $3 = actual_opt();
            expect(BnfToken.RPAR);
            $$ = factory.MethodCall($1, $3);
        } else {
            $$ = $1;
        }
        return $$;
    }

    /*
     * name ::= IDENTIFIER { factory.Name(getLexer()) }
     *      |   error("NameExpected")
     *      ;
     */
    public BnfTree name() throws ParserException
    {
        BnfTree $$;
        if (match(BnfToken.IDENTIFIER)) {
            $$ = factory.BnfName(getLexer());
        } else {
            throw error("NameExpected");
        }
        return $$;
    }

    /*
     * macro    ::=   macro_name LPAR macro_param RPAR = { factory.MacroCall($1, $3) } 
     *            |   macro_name ; 
     */
    public BnfTree macro() throws ParserException
    {
        BnfTree $$;
        BnfTree $1 = macro_name();
        if (accept(BnfToken.LPAR)) {
            BnfTree $3 = macro_param();
            expect(BnfToken.RPAR);
            $$ = factory.MacroCall($1, $3);
        } else {
            $$ = $1;
        }
        return $$;
    }

    /*
     * macro_name ::= RESERVED = { factory.Reserved(getLexer()) } ;
     */
    public BnfTree macro_name() throws ParserException
    {
        BnfTree $$;
        if (match(BnfToken.RESERVED)) {
            $$ = factory.Reserved(getLexer());
        } else {
            throw error("MacroNameExpected");
        }
        return $$;
    }

    /*
     * macro_param ::=   STRING 
     *               |   macro_alt;
     */
    public BnfTree macro_param() throws ParserException
    {
        BnfTree $$;
        if (match(BnfToken.STRING)) {
            $$ = factory.BnfString(getLexer());
        } else {
            $$ = macro_alt();
        }
        return $$;
    }

    /*
     * macro_alt   ::=   macro_seq 
     *               |   macro_seq ALT macro_alt;
     */
    public BnfTree macro_alt() throws ParserException
    {
        BnfTree $$;
        BnfTree $1 = macro_seq();
        if (accept(BnfToken.ALT)) {
            BnfTree $3 = macro_alt();
            $$ = factory.Alt($1,  $3);
        } else {
            $$ = $1;
        }
        return $$;
    }

    /*
     * macro_seq   ::=   name 
     *               |   name macro_seq;
     */
    public BnfTree macro_seq() throws ParserException
    {
        BnfTree $$;
        BnfTree $1 = name();
        if (match(BnfToken.IDENTIFIER)) {
            BnfTree $3 = macro_seq();
            $$ = factory.Seq($1,  $3);
        } else {
            $$ = $1;
        }
        return $$;
    }

    public ParserException error(String message)
    {
        return getCompilerContext().error(getInput(), message);
    }

}
