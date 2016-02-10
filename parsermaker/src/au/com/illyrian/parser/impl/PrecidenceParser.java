package au.com.illyrian.parser.impl;

import java.util.HashMap;
import java.util.Map;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;

public class PrecidenceParser<Expr> extends ParserBase
{
    public static final int DEFAULT_PRECIDENCE = 0;

    /**
     * A map from operator to an object.
     */
    protected final Map<String, Operator> nudOperators;
    protected final Map<String, Operator> ledOperators;
    protected PrecidenceAction<Expr> precidenceActions = null;

    public PrecidenceParser()
    {
        super();
        nudOperators = new HashMap<String, Operator>();
        ledOperators = new HashMap<String, Operator>();
    }

    public PrecidenceAction<Expr> getPrecidenceActions()
    {
        if (precidenceActions == null)
            throw new IllegalStateException("PrecidenceAction not provided.");
        return precidenceActions;
    }

    public void setPrecidenceActions(PrecidenceAction<Expr> actions)
    {
        precidenceActions = actions;
    }

    public Operator addInfixOperator(String name, int index, int precedence, int arity)
    {
        Operator op = new Operator(name, index, precedence, arity);
        return addLedOperator(op);
    }

    public Operator addPrefixOperator(String name, int index, int precedence, int arity)
    {
        Operator op = new Operator(name, index, precedence, arity);
        return addNudOperator(op);
    }

    public Operator addPrefixOperator(String leftName, String rightName, int index, int precedence, int arity)
    {
        Operator op = new Operator(leftName, rightName, index, precedence, arity);
        return addNudOperator(op);
    }

    public Operator addPostfixOperator(String name, int index, int precedence, int arity)
    {
        Operator op = new Operator(name, index, precedence, arity);
        return addLedOperator(op);
    }

    public Operator addPostfixOperator(String leftName, String rightName, int index, int precedence, int arity)
    {
        Operator op = new Operator(leftName, rightName, index, precedence, arity);
        return addLedOperator(op);
    }

    protected Operator addNudOperator(Operator operator)
    {
        nudOperators.put(operator.getName(), operator);
        return operator;
    }

    protected Operator addLedOperator(Operator operator)
    {
        ledOperators.put(operator.getName(), operator);
        return operator;
    }

    protected Operator getNudOperator() throws ParserException
    {
        Operator operator = null;
        int token = getToken();
        // Operators may include reserved words and brackets.
        if (token == Lexer.OPERATOR || token == Lexer.DELIMITER || token == Lexer.RESERVED || token == Lexer.OPEN_P) {
            String lookup = getLexer().getTokenValue();
            operator = nudOperators.get(lookup);
            if (operator == null) {
                checkOperatorImplemented(token, lookup);
            }
        }
        return operator;
    }

    protected Operator getLedOperator() throws ParserException
    {
        Operator operator = null;
        int token = getToken();
        // Operators may include reserved words and brackets.
        if (token == Lexer.OPERATOR || token == Lexer.DELIMITER || token == Lexer.RESERVED || token == Lexer.OPEN_P) {
            String lookup = getLexer().getTokenValue();
            operator = ledOperators.get(lookup);
            if (operator == null) {
                checkOperatorImplemented(token, lookup);
            }
        }
        return operator;
    }

    protected void checkOperatorImplemented(int tokenValue, String operatorName) throws ParserException
    {
        // Throw an exception if this is a pure operator that we know nothing about.
        if (tokenValue == Lexer.OPERATOR && nudOperators.get(operatorName) == null
                && ledOperators.get(operatorName) == null)
            throw new ParserException("Operator not implemented: " + getLexer().getTokenValue());
    }

    public Expr expression() throws ParserException
    {
        Expr result = expression(DEFAULT_PRECIDENCE);
        return result;
    }

    protected Expr expression(int minPrecedence) throws ParserException
    {
        Expr leftOperand = unaryExpression(minPrecedence);
        Operator operator = null;
        while ((operator = getLedOperator()) != null && operator.precedence >= minPrecedence) {
            nextToken();
            leftOperand = ledExpression(leftOperand, operator, minPrecedence);
        }
        return leftOperand;
    }

    protected Expr ledExpression(Expr leftOperand, Operator operator, int minPrecedence) throws ParserException
    {
        switch (operator.mode) {
        case Operator.BINARY:
        {
            Expr rightOperand = expression(operator.precedence + 1);
            leftOperand = getPrecidenceActions().infixAction(operator, leftOperand, rightOperand);
            break;
        }
        case Operator.BINARYRIGHT:
        {
            Expr rightOperand = expression(operator.precedence);
            leftOperand = getPrecidenceActions().infixAction(operator, leftOperand, rightOperand);
            break;
        }
        case Operator.POSTFIX:
        {
            leftOperand = getPrecidenceActions().postfixAction(operator, leftOperand);
            break;
        }
        case Operator.BRACKET:
        {
            Expr subExpression = expression();
            expect(Lexer.CLOSE_P, operator.endName, null);
            leftOperand = getPrecidenceActions().bracketAction(operator, leftOperand, subExpression);
            break;
        }
        case Operator.PARAMS:
        {
            if (accept(Lexer.CLOSE_P, operator.endName))
                leftOperand = getPrecidenceActions().callAction(leftOperand, null);
            else {
                Expr subExpression = expression();
                expect(Lexer.CLOSE_P, operator.endName, null);
                leftOperand = getPrecidenceActions().callAction(leftOperand, subExpression);
            }
            break;
        }
        default:
            throw new IllegalStateException("Unknown operator mode: " + operator.mode);
        }
        return leftOperand;
    }

    protected Expr unaryExpression(int minPrecidence) throws ParserException
    {
        Expr result = null;
        Operator nudOperator = null;
        if ((nudOperator = getNudOperator()) != null) {
            nextToken();
            result = nudExpression(nudOperator, minPrecidence);
        } else
            switch (getToken()) {
            case Lexer.IDENTIFIER:
            case Lexer.RESERVED:
            {
                String identifier = getLexer().getTokenValue();
                result = getPrecidenceActions().identifierAction(identifier);
                nextToken();
                break;
            }
            case Lexer.CHARACTER:
            case Lexer.INTEGER:
            case Lexer.DECIMAL:
            case Lexer.STRING:
            {
                result = getPrecidenceActions().literalAction(getLexer());
                nextToken();
                break;
            }
            case Lexer.OPEN_P:
            {
                if (accept(Lexer.OPEN_P, "(")) {
                    Expr subExpression = expression();
                    expect(Lexer.CLOSE_P, ")", "\')\' expected.");
                    result = getPrecidenceActions().parenthesesAction(subExpression);
                } else {
                    throw error(getInput(), "Unexpected Perenthesis");
                }
                break;
            }
            default:
            {
                throw error(getInput(), "Expression expected.");
            }
            }
        return result;
    }

    protected Expr nudExpression(Operator nudOperator, int minPrecedence) throws ParserException
    {
        Expr result = null;
        if (nudOperator.mode == Operator.PREFIX) {
            Expr subExpression = expression(nudOperator.getPrecedence());
            result = getPrecidenceActions().prefixAction(nudOperator, subExpression);
        } else if (nudOperator.mode == Operator.BRACKET) {
            Expr firstOperand = expression();
            expect(Lexer.CLOSE_P, nudOperator.endName, null);
            result = getPrecidenceActions().parenthesesAction(firstOperand);
            // If an expression immediately follows a parenthesized expression
            // then the previous expression must have been a cast.
            // Eg. (java.lang.Serializable)b (long)(int)(short)d
            if (isNudExpression()) {
                Expr secondOperand = expression(nudOperator.precedence);
                result = getPrecidenceActions().castAction(firstOperand, secondOperand);
            } else
                result = getPrecidenceActions().parenthesesAction(firstOperand);
        } else
            throw new IllegalStateException("Unknown operator mode: " + nudOperator.mode);
        return result;
    }

    protected boolean isNudExpression() throws ParserException
    {
        int token = getToken();
        switch (token) {
        case Lexer.IDENTIFIER:
        case Lexer.INTEGER:
        case Lexer.DECIMAL:
        case Lexer.STRING:
        case Lexer.CHARACTER:
        case Lexer.RESERVED:
            return true;
        default:
            return (getNudOperator() != null);
        }
    }

}