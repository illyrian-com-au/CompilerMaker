package au.com.illyrian.domainparser;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionLink;
import au.com.illyrian.classmaker.ast.DotOperator;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.jesub.ast.AstDeclareClass;
import au.com.illyrian.jesub.ast.AstDeclareModule;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.InvokeParser;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserBase;
import au.com.illyrian.parser.maker.ClassActionMaker;

/**
 *
 * @author Donald Strong
 */
public class AstModuleParser extends ParserBase implements ParseModule<AstStructure>
{
    /** The actions to be applied to the recognised input tokens. */
    private ClassAction classAction = null;

    /**
     * Public constructor for the search query parser. When no actions are
     * provided the parser only performs validation.
     */
    public AstModuleParser()
    {
        setLexer(createLexer());
        addReserved("package");
        addReserved("import");
    }

    public ClassAction getModuleAction()
    {
        if (classAction == null)
            classAction = createClassAction();
        return classAction;
    }

    public void setClassAction(ClassAction actions)
    {
        this.classAction = actions;
    }

    protected ClassAction createClassAction()
    {
        ClassAction action = new ClassActionMaker();
        getCompilerContext().visitParser(action);
        return action;
    }

    protected Latin1Lexer createLexer()
    {
        return new Latin1Lexer();
    }

    /*
         dec_module:AstDeclareModule   ::= dec_package dec_imports dec_class ;
         dec_package:AstExpression     ::= 'package' class_name ';'
                                       | EMPTY ;
         dec_imports:AstExpression     ::= 'import' class_name ';' dec_imports
                                       | EMPTY ;
         class_name:AstExpression      ::= IDENTIFIER more_classname
                                       | '.' IDENTIFIER
                                       | '.' error("More of the class name expected) 
                                       | error("Class name expected") ;
         dec_class:AstDeclareClass     ::= class_name '::' code '::' class_name ';' ;
         code:AstDeclareClass          ::= <code for the invoked parser> 
     */

    /**
     * Parse the given Domain Specific Language.
     * <p>
     * This is a recursive descent parser. <br>
     * The parser rules are expressed in Extended Bacus Nour Form (EBNF). <br>
     * <code>
     *     dec_module   ::= dec_package dec_imports dec_class
     *     dec_package  ::= [ 'package' classname ';' ]
     *     dec_imports  ::= { 'import' classname ';' }
     *     classname    ::= IDENTIFIER { '.' IDENTIFIER } 
     *     dec_class    ::= IDENTIFIER '::' code '::' IDENTIFIER ';'
     *     code         ::= <code for the invoked parser> 
     * </code>
     *
     * @param input
     *            - shared input for the parser
     * @return the result of parsing the input and applying actions from
     *         ExpressionAction.
     * @throws ParserException
     *             - if an error occurs.
     */
    public AstStructure parseModule(CompilerContext context) throws ParserException
    {
        setCompilerContext(context);
        // Read the first token from input.
        nextToken();

        // Parse top level expression.
        AstStructure module = dec_module();
        // Ensure all tokens have been processed.
        endModule();

        return module;
    }

    protected void endModule() throws ParserException
    {
        TokenType token = getTokenType();
        // Ensure all tokens have been processed.
        if (token == TokenType.ERROR) {
            throw error(getLexer().getErrorMessage());
        } else if (token == TokenType.DELIMITER) {
            throw error("Unbalanced perentheses - too many \')\'.");
        } else if (token != TokenType.END) {
            throw error("End of input expected");
        }
    }

    /**
     * dec_module:AstStructure ::= dec_package more_imports dec_class { return
     * new AstDeclareModule($1, $2, $3); };
     */
    public AstStructure dec_module() throws ParserException
    {
        AstExpression packageExpr = dec_package();
        AstExpression importsExpr = more_imports();
        AstDeclareClass classExpr = dec_class();
        return new AstDeclareModule(packageExpr, importsExpr, classExpr);
    }

    /**
     * packageStatement ::= 'package' class_name ';' { return $2; }
     * | EMPTY ;
     */
    public AstExpression dec_package() throws ParserException
    {
        if (accept(TokenType.RESERVED, "package")) {
            AstExpression packageName = class_name();
            expect(TokenType.DELIMITER, ";", "';' expected at the end of the package name");

            return packageName;
        }
        return null;
    }

    /**
     * more_imports:AstExpression ::= declare_import more_imports
     * { return ($2==null) ? S1 : new AstExpressionLink($1, $2); }
     * | EMPTY
     */
    public AstExpression more_imports() throws ParserException
    {
        if (match(TokenType.RESERVED, "import")) {
            AstExpression className = declare_import();
            AstExpression more = more_imports();
            return (more == null) ? className : new AstExpressionLink(className, more);
        }
        return null;
    }

    /**
     * declare_import:AstExpression ::= 'import' class_name ';' { return $2; } ;
     */
    public AstExpression declare_import() throws ParserException
    {
        expect(TokenType.RESERVED, "import");
        AstExpression className = class_name();
        semi();
        return className;
    }

    /**
     * simple_name:AstExpression ::= IDENTIFIER { return new
     * TerminalName(value); }
     * | error("Class name expected") ;
     */
    public AstExpression simple_name() throws ParserException
    {
        AstExpression result = null;
        if (getTokenType() == TokenType.IDENTIFIER) {
            String simpleName = getLexer().getTokenValue();
            nextToken();
            result = new TerminalName(simpleName);
        } else
            throw error("Class name expected.");
        return result;
    }

    /**
     * class_name:AstExpression :== simple_name '.' class_name { return new
     * DotOperator($1, $2); }
     * | simple_name ;
     */
    public AstExpression class_name() throws ParserException
    {
        AstExpression result = simple_name();
        if (accept(TokenType.OPERATOR, ".")) {
            AstExpression moreClassName = class_name();

            result = new DotOperator(result, moreClassName);
        }
        return result;
    }

    public String semi() throws ParserException
    {
        return expect(TokenType.DELIMITER, ";", "';' expected at end of statement");
    }

    /**
     * parser ::= class_name '::' code($1) '::' class_name ';' {
     * verifyParserName($1, $5); return $3; }
     * | error("Parser class name expected") ;
     */
    public AstDeclareClass dec_class() throws ParserException
    {
        AstDeclareClass decClass = null;
        if (getTokenType() == TokenType.IDENTIFIER) {
            AstExpression parseName = class_name();
            if (match(TokenType.OPERATOR, "::")) {
                decClass = code(parseName.toString());
                nextToken();
                if (accept(TokenType.OPERATOR, "::")) {
                    AstExpression className = class_name();
                    verifyParserName(parseName, className);
                    if (!className.equals(parseName))
                        throw error("'" + parseName + "' expected at end of parser space");
                } else
                    throw error(":: expected");
            } else
                throw error(":: expected");
        } else
            throw error("Parser class name expected");
        return decClass;
    }

    public AstDeclareClass code(String parseName) throws ParserException
    {
        AstDeclareClass decClass = null;
        String qualifiedName = getModuleAction().getParserName(parseName);
        InvokeParser parser = getCompilerContext().getInvokeParser();
        Input input = getInput();
        Object result = parser.invokeParseClass(qualifiedName);
        if (result instanceof AstDeclareClass)
            decClass = (AstDeclareClass) result;
        return decClass;
    }

    public void verifyParserName(AstExpression name1, AstExpression name2) throws ParserException
    {
        String firstName = name1.toString();
        String secondName = name2.toString();
        if (!firstName.equals(secondName))
            throw error("'" + firstName + "' expected at end of parser space");
    }
}
