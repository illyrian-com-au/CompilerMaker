package au.com.illyrian.parser.impl;

import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.Operator;
import au.com.illyrian.parser.ParserException;

public class JavaOperatorPrecedenceParser extends PrecidenceParser
{
    public JavaOperatorPrecedenceParser()
    {
        super();
    }
    
    protected Object ledExpression(Object leftOperand, OperatorImpl operator, int minPrecedence) throws ParserException
    {
        if (operator.mode == Operator.SHORTCUT)
        {
            leftOperand = getPrecidenceActions().preProcess(leftOperand, operator);
            Object rightOperand = expression(operator.precedence);
            leftOperand = getPrecidenceActions().infixAction(operator, leftOperand, rightOperand);
        }
        else if (operator.mode == Operator.BINARY)
        {
          int nextPrecidence = operator.leftAssociative ? 
                 operator.precedence + 1 : operator.precedence;
          Object rightOperand = expression(nextPrecidence);
          leftOperand = getPrecidenceActions().infixAction(operator, leftOperand, rightOperand);
        }
        else if (operator.mode == Operator.POSTFIX)
        {
            leftOperand = getPrecidenceActions().postfixAction(operator, leftOperand);
        }
        else if (operator.mode == Operator.BRACKET)
        {
            Object rightOperand = expression(0);
            expect(Lexer.CLOSE_P, operator.endName, null);
            leftOperand = getPrecidenceActions().bracketAction(operator, leftOperand, rightOperand);
        }
        else if (operator.mode == Operator.PARAMS)
        {
            Object params = actualParameters(leftOperand);
            expect(Lexer.CLOSE_P, operator.endName, null);
            leftOperand = getPrecidenceActions().callAction(leftOperand, params);
        }
        else
          throw new IllegalStateException("Unknown operator arity: " + operator.mode);
        return leftOperand;
    }

    protected Object unaryExpression(int minPrecidence) throws ParserException
    {
        Object result = null;
        OperatorImpl nudOperator = null; 
        if ((nudOperator = getNudOperator()) != null)
        {
            nextToken();
            result = nudExpression(nudOperator, minPrecidence);
        }
        else if (getToken() == Lexer.IDENTIFIER)
        {
            // Get the identifier value.
            String identifier = getLexer().getTokenValue();
            // Apply an action to the identifier
            result = getPrecidenceActions().identifierAction(identifier);
            nextToken();
            // FIXME - check for actual parameters.
//            if (accept(Lexer.OPEN_P, "("))
//            {
////                Object value = expression();
//                Object value = null;
//                expect(Lexer.CLOSE_P, ")", "\')\' expected.");
//                result = getPrecidenceActions().callAction(identifier, value);
//            }
        }
        else if (getToken() == Lexer.INTEGER)
        {
            // Apply an action to the literal
            result = getPrecidenceActions().literalAction(getLexer());
            nextToken();
        }
        else if (accept(Lexer.OPEN_P, "("))
        {
            Object value = expression(0);
            expect(Lexer.CLOSE_P, ")", "\')\' expected.");
            result = getPrecidenceActions().parenthesesAction(value);
        }
        else
        {
            throw error(getInput(), "Expression expected.");
        }
    
        return result;
    }

    protected Object nudExpression(OperatorImpl nudOperator, int minPrecedence) throws ParserException
    {
        Object result = null;
        if (nudOperator.mode == Operator.PREFIX )
        {
            result = expression(nudOperator.precedence);
            result = getPrecidenceActions().prefixAction(nudOperator, result);
        }
        else if (nudOperator.mode == Operator.BRACKET)
        {
            Object firstOperand = expression(0);
            expect(Lexer.CLOSE_P, nudOperator.endName, null);
//            if ("(".equals(nudOperator.name) && isNudExpression())
            if (isNudExpression())
            {
                Object secondOperand = expression(nudOperator.precedence);
                result = getPrecidenceActions().castAction(firstOperand, secondOperand);
            }
            else
                result = getPrecidenceActions().parenthesesAction(firstOperand);
        }
        else
            throw new IllegalStateException("Unknown operator arity: " + nudOperator.mode);
        return result;
    }
    
    boolean isNudExpression() throws ParserException
    {
        int token = getToken();
        switch (token)
        {
        case Lexer.IDENTIFIER:
        case Lexer.INTEGER:
            return true;
        default:
            return (getNudOperator() != null);
        }
    }

    public Object actualParameters(Object leftOperand) throws ParserException
    {
        Object callStack = getPrecidenceActions().beginParameters(leftOperand);
        while (!match(Lexer.CLOSE_P, ")"))
        {
            Object param = expression(0);
            callStack = getPrecidenceActions().addParameter(callStack, param);
            if (!accept(Lexer.DELIMITER, ",") && !match(Lexer.CLOSE_P, ")"))
                throw new ParserException("Actual parameter list expected: " + toString());
        }
        return callStack;
    }
}
