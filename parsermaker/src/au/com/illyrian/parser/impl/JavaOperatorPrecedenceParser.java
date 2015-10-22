package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;

public class JavaOperatorPrecedenceParser<Expr> extends PrecidenceParser<Expr>
{
    public JavaOperatorPrecedenceParser()
    {
        super();
    }

    protected Expr ledExpression(Expr leftOperand, Operator operator, int minPrecedence) throws ParserException
    {
        Expr result = null;
        if (operator.mode == Operator.BINARY) {
            Expr rightOperand = expression(operator.precedence + 1);
            result = getPrecidenceActions().infixAction(operator, leftOperand, rightOperand);
        } else if (operator.mode == Operator.BINARYRIGHT) {
            Expr rightOperand = expression(operator.precedence);
            result = getPrecidenceActions().infixAction(operator, leftOperand, rightOperand);
        } else if (operator.mode == Operator.POSTFIX) {
            result = getPrecidenceActions().postfixAction(operator, leftOperand);
        } else if (operator.mode == Operator.BRACKET) {
            Expr rightOperand = expression(0);
            expect(Lexer.CLOSE_P, operator.endName, null);
            result = getPrecidenceActions().bracketAction(operator, leftOperand, rightOperand);
        } else if (operator.mode == Operator.PARAMS) {
            Expr params = actualParameters(leftOperand);
            expect(Lexer.CLOSE_P, operator.endName, null);
            result = getPrecidenceActions().callAction(leftOperand, params);
        } else
            throw new IllegalStateException("Unknown operator arity: " + operator.mode);
        return result;
    }

    protected Expr unaryExpression(int minPrecidence) throws ParserException
    {
        Expr result = null;
        Operator nudOperator = null;
        if ((nudOperator = getNudOperator()) != null) {
            nextToken();
            result = nudExpression(nudOperator, minPrecidence);
        } else if (getToken() == Lexer.IDENTIFIER) {
            // Get the identifier value.
            String identifier = getLexer().getTokenValue();
            // Apply an action to the identifier
            result = getPrecidenceActions().identifierAction(identifier);
            nextToken();
        } else if (getToken() == Lexer.INTEGER) {
            // Apply an action to the literal
            result = getPrecidenceActions().literalAction(getLexer());
            nextToken();
        } else if (accept(Lexer.OPEN_P, "(")) {
            Expr subordinate = expression(0);
            expect(Lexer.CLOSE_P, ")", "\')\' expected.");
            result = getPrecidenceActions().parenthesesAction(subordinate);
        } else {
            throw error(getInput(), "Expression expected.");
        }

        return result;
    }

    protected Expr nudExpression(Operator nudOperator, int minPrecedence) throws ParserException
    {
        Expr result = null;
        if (nudOperator.mode == Operator.PREFIX) {
            result = expression(nudOperator.precedence);
            result = getPrecidenceActions().prefixAction(nudOperator, result);
        } else if (nudOperator.mode == Operator.BRACKET) {
            Expr firstOperand = expression(0);
            expect(Lexer.CLOSE_P, nudOperator.endName, null);
            // If an expression immediately follows a parenthesized expression
            // then the previous expression must have been a cast.
            // Eg. (java.lang.Serializable)b (long)(int)(short)d
            if (isNudExpression()) {
                Expr secondOperand = expression(nudOperator.precedence);
                result = getPrecidenceActions().castAction(firstOperand, secondOperand);
            } else
                result = getPrecidenceActions().parenthesesAction(firstOperand);
        } else
            throw new IllegalStateException("Unknown operator arity: " + nudOperator.mode);
        return result;
    }

    boolean isNudExpression() throws ParserException
    {
        int token = getToken();
        switch (token) {
        case Lexer.IDENTIFIER:
        case Lexer.INTEGER:
        case Lexer.DECIMAL:
        case Lexer.STRING:
        case Lexer.CHARACTER:
            return true;
        default:
            return (getNudOperator() != null);
        }
    }

    public Expr actualParameters(Expr leftOperand) throws ParserException
    {
        // return expression(-1);
        Expr callStack = getPrecidenceActions().beginParameters(leftOperand);
        while (!match(Lexer.CLOSE_P, ")")) {
            Expr param = expression(0);
            callStack = getPrecidenceActions().addParameter(callStack, param);
            if (!accept(Lexer.DELIMITER, ",") && !match(Lexer.CLOSE_P, ")"))
                throw new ParserException("Actual parameter list expected: " + toString());
        }
        return callStack;
    }
}
