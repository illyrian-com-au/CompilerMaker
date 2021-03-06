/*
 * Created on 7/05/2008 by strongd
 *
 * Copyright (c) 2006 Department of Infrastructure (DOI)
 * State Government of Victoria, Australia
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of DOI.
 */

package au.com.illyrian.expressionparser;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerLocation;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParseExpression;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.expr.AstExpressionPrecidenceAction;
import au.com.illyrian.parser.expr.AstExpressionPrecidenceParser;

/**
 *
 * This Parser parses simple expressions comprising Java style Identifiers that are combined using AND, OR and
 * parentheses ().
 * <p>
 * <code>
 * eg. c1 AND c2 OR (c3 AND c4).
 * </code><br>
 * The AND operator has the usual precedence over OR so that ... a AND b OR c is interpreted as ... a AND (b OR c)
 * <p>
 * The responsibilities of this class are to parse the input string and report error messages as Exceptions. <br>
 * It calls the ExpressionAction interface to perform semantic actions with the recognised tokens.
 *
 * @author strongd
 */
public class ExpressionParser extends AstExpressionPrecidenceParser
    implements ParseClass, ParseMembers, ParseExpression, ClassMakerLocation
{
    private ClassMaker maker = null;

    /** The actions to be applied to the recognised input tokens. */
    private ExpressionActionFactory    expressionAction;
    
    /**
     * Public constructor for the search query parser. When no actions are provided the parser only performs validation.
     */
    public ExpressionParser()
    {
        super();
        populateReservedWords();
        //populateOperators();
    }
    
    protected void populateReservedWords()
    {
        addReservedWord("package", "package");
        addReservedWord("import", "import");
        addReservedWord("class", "class");
        addReservedWord("extends", "extends");
        addReservedWord("new", "new");
        addReservedWord("instanceof", "instanceof");
    }
    
//    protected void populateOperators()
//    {
//        addLedOperator("**", ParserConstants.POW, 16, Operator.BINARYRIGHT);
//        addLedOperator("--", ParserConstants.POSTDEC, 15, Operator.POSTFIX);
//        addLedOperator("++", ParserConstants.POSTINC, 15, Operator.POSTFIX);
//        addNudOperator("-", ParserConstants.NEG, 14, Operator.PREFIX);
//        addNudOperator("--", ParserConstants.DEC, 14, Operator.PREFIX);
//        addNudOperator("++", ParserConstants.INC, 14, Operator.PREFIX);
//        addNudOperator("+", ParserConstants.NOP, 14, Operator.PREFIX);
//        addNudOperator("~", ParserConstants.INV, 14, Operator.PREFIX);
//        addNudOperator("!", ParserConstants.NOT, 14, Operator.PREFIX);
//        addNudOperator("new", ParserConstants.NEW, 13, Operator.PREFIX);
//        addLedOperator("*", ParserConstants.MULT, 12, Operator.BINARY);
//        addLedOperator("/", ParserConstants.DIV, 12, Operator.BINARY);
//        addLedOperator("%", ParserConstants.REM, 12, Operator.BINARY);
//        addLedOperator("+", ParserConstants.ADD, 11, Operator.BINARY);
//        addLedOperator("-", ParserConstants.SUBT, 11, Operator.BINARY);
//        addLedOperator("<<", ParserConstants.SHL, 10, Operator.BINARY);
//        addLedOperator(">>", ParserConstants.SHR, 10, Operator.BINARY);
//        addLedOperator(">>>", ParserConstants.USHR, 10, Operator.BINARY);
//        addLedOperator("<", ParserConstants.LT, 9, Operator.BINARY);
//        addLedOperator(">", ParserConstants.GT, 9, Operator.BINARY);
//        addLedOperator("<=", ParserConstants.LE, 9, Operator.BINARY);
//        addLedOperator(">=", ParserConstants.GE, 9, Operator.BINARY);
//        addLedOperator("==", ParserConstants.EQ, 8, Operator.BINARY);
//        addLedOperator("!=", ParserConstants.NE, 8, Operator.BINARY);
//        addLedOperator("instanceof", ParserConstants.INSTANCEOF, 8, Operator.BINARY);
//        addLedOperator("&", ParserConstants.AND, 7, Operator.BINARY);
//        addLedOperator("^", ParserConstants.XOR, 6, Operator.BINARY);
//        addLedOperator("|", ParserConstants.OR, 5, Operator.BINARY);
//        addLedOperator("=", ParserConstants.ASSIGN, 1, Operator.BINARYRIGHT);
//    }

//    public void addOperator(String name, Object value)
//    {
//        getLexer().getOperators().put(name, value);
//    }

    public void addReservedWord(String name, Object value)
    {
        getLexer().getReservedWords().put(name, value);
    }

//    protected Object getOperator(String op)
//    {
//        return getLexer().getOperators().get(op);
//    }
    
    /**
     * Public constructor for the search query parser.
     *
     * @param actions -
     *            the actions to be applied to the recognised input tokens.
     */
    public void setExpressionAction(ExpressionActionFactory actions)
    {
        this.expressionAction = actions;
        AstExpressionFactory factory = new AstExpressionFactory();
        AstExpressionPrecidenceAction precidenceActions = new AstExpressionPrecidenceAction(factory);
        setPrecidenceActions(precidenceActions);
    }
    
    public ExpressionActionFactory getExpressionAction()
    {
        if (expressionAction == null)
            setExpressionAction(defaultExpressionAction());
        return expressionAction;
    }

    public ExpressionActionFactory defaultExpressionAction()
    {
    	ExpressionActionFactory action = new ExpressionActionFactory();
    	action.setClassMaker(getClassMaker());
        return action;
    }

    public void setClassMaker(ClassMaker classMaker) 
    {
        maker = classMaker;
    }

    public ClassMaker getClassMaker() 
    {
        return maker;
    }

    public Object parseClass(CompilerContext context)
    {
        setCompilerContext(context);
        // Read the first token from input.
        nextToken();

        beginFragment();
        // Parse top level expression.
        Object result = dec_class();

        endFragment();

        return result;
    }

    protected void beginFragment()
    {
        expect(TokenType.DELIMITER, "{", "'{' expected at start of code fragment.");
    }

    protected void endFragment()
    {
        if (match(TokenType.DELIMITER, "}"))
        	return;
        
        TokenType token = getTokenType();
        // Ensure all tokens have been processed.
        if (token == TokenType.ERROR)
        {
            throw exception(getLexer().getErrorMessage());
        }
        else if (token == TokenType.DELIMITER)
        {
            throw exception("Unbalanced perentheses - too many \')\'.");
        }
        else if (token == TokenType.IDENTIFIER)
        {
            throw exception("Operator expected.");
        }
        else
        {
            throw exception("Operator or '}' expected.");
        }
    }
    
    /** dec_class ::= dec_classname dec_extends dec_functions */
    public Object dec_class()
    {
        Object classname = dec_classname();
        Object extendClass = dec_extends();
        Object functionList = dec_functions();
        getExpressionAction().declareClass(classname, extendClass, functionList);
        return getExpressionAction().getModule();
    }

    /** dec_classname ::= 'class' IDENTIFIER */
    Object dec_classname()
    {
        Object result = null;
        expect(TokenType.RESERVED, "class", null);
        if (match(TokenType.IDENTIFIER, null))
        {
            String classname = getLexer().getTokenValue();
            nextToken();
            result = getExpressionAction().declareClassname(classname);
        }
        else
        {
            throw exception("Expected the name of the class.");
        }
        return result;
    }

    /** dec_extends ::= [ 'extends' IDENTIFIER ] */
    Object dec_extends()
    {
        String extendClass = null;
        if (accept(TokenType.RESERVED, "extends"))
        {
            if (match(TokenType.IDENTIFIER, null))
            {
                extendClass = getLexer().getTokenValue();
                nextToken();
                return getExpressionAction().declareExtends(extendClass);
            }
            else
            {
                throw exception("Expected the name of the extended class.");
            }
        }
        return null;
    }

    /** dec_functions ::= '{' more_functions '}'  */
    Object dec_functions()
    {
        Object functionList = null;
        expect(TokenType.DELIMITER, "{", null);
        functionList = more_functions();
        
        expect(TokenType.DELIMITER, "}", null);
        return functionList;
    }

    /** more_functions ::= { dec_function } */
    Object more_functions()
    {
        Object functionList = null;
        if (match(TokenType.IDENTIFIER, null))
        {
            Object function = dec_function();
            functionList = more_functions();
            functionList = getExpressionAction().addFunction(function, functionList);
        }
        return functionList;
    }

    /**
     * Parse a function declaration.
     * <p>
     * <code>
     *     dec_function  ::= IDENTIFIER '(' parameters ')' mult_expr ';'
     *     parameters ::= IDENTIFIER { ',' IDENTIFIER }
     * </code>
     *
     * @return the result of parsing the input and applying actions from ParserConstants.
     * @throws Exception -
     *             if an error occurs.
     */
    public Object parseMembers(CompilerContext context)
    {
        setCompilerContext(context);
        // Read the first token from input.
        nextToken();

        beginFragment();
        // Parse top level expression.
        Object result = dec_function();
        // Ensure all tokens have been processed.
        endFragment();

        return result;
    }

    /** dec_function ::= IDENTIFIER '(' parameters ')' assign mult_expr ';' */
    Object dec_function()
    {
        Object result = null;
        if (match(TokenType.IDENTIFIER, null))
        {
            String identifier = getLexer().getTokenValue();
            Object functionName = getExpressionAction().declareFunctionName(identifier);
            nextToken();
            expect(TokenType.DELIMITER, "(", null);
            Object params = parameters();
            expect(TokenType.DELIMITER, ")", null);
            Object ass = assign_op();
            Object exprType = expression();
            expect(TokenType.DELIMITER, ";", null);
            getExpressionAction().endMethod(exprType);
            result = getExpressionAction().declareFunction(functionName, params, exprType);
        }
        return result;
    }
    
    Object assign_op()
    {
        expect(TokenType.OPERATOR, "=", null);
        getExpressionAction().beginMethod();
    	return null;
    }

    /** parameters ::= [ IDENTIFIER { ',' IDENTIFIER } ] */
    Object parameters()
    {
        Object params = null;
        if (match(TokenType.IDENTIFIER, null))
        {
            String identifier = getLexer().getTokenValue();
            nextToken();
            params = getExpressionAction().addParameter(params, identifier);

            while (accept(TokenType.DELIMITER, ","))
            {
                if (match(TokenType.IDENTIFIER, null))
                {
                    identifier = getLexer().getTokenValue();
                    nextToken();
                    params = getExpressionAction().addParameter(params, identifier);
                }
                else
                {
                    throw exception("Name of a variable expected.");
                }
            }
        }
        return params;
    }

    /**
     * Parse the given compound query.
     * <p>
     * This is a recursive descent parser. <br>
     * The parser rules are expressed in Extended Bacus Nour Form (EBNF). <br>
     * <code>
     *     mult_expr ::= plus_expr [ '*' mult_expr ]
     *                 | plus_expr [ '/' mult_expr ]
     *     plus_expr ::= operand [ '+' plus_expr ]
     *                 | operand [ '-' plus_expr ]
     *     operand   ::= IDENTIFIER
     *                 | INTEGER
     *                 | OPEN_P mult_expr CLOSE_P
     * </code>
     * The rules favour tail end recursion (unlike table driven parsers). <br>
     * Each rule is then implemented in a similarly named method. <br>
     * Converting the rule into an implementation is quite straight forward, <br>
     * as is error detection and reporting.
     *
     * @return the result of parsing the input and applying actions from ParserConstants.
     * @throws Exception -
     *             if an error occurs.
     */
    public Object parseExpression(CompilerContext context)
    {
        setCompilerContext(context);
        context.visitParser(this);
        
        // Read the first token from input.
        nextToken();

        beginFragment();
        // Parse top level expression.
        Object result = expression();
        // Ensure all tokens have been processed.
        endFragment();

        return result;
    }
}
