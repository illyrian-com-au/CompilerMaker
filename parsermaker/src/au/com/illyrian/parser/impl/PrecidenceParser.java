package au.com.illyrian.parser.impl;

import java.util.Properties;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.Operator;
import au.com.illyrian.parser.ParserException;

public class PrecidenceParser extends ParserBase
{
    /**
     * A map from operator to an object.
     */
    protected final Properties nudOperators;
    protected final Properties ledOperators;
    protected PrecidenceAction precidenceActions = null;

    public PrecidenceParser()
    {
        super();
        nudOperators = new Properties();
        ledOperators = new Properties();
    }

    public PrecidenceAction getPrecidenceActions()
    {
        if (precidenceActions == null)
            precidenceActions = defaultPrecidenceActions();
        return precidenceActions;
    }

    public void setPrecidenceActions(PrecidenceAction actions)
    {
        precidenceActions = actions;
    }

    public PrecidenceAction defaultPrecidenceActions()
    {
        PrecidenceActionString action = new PrecidenceActionString();
        getCompileUnit().visitParser(action); // FIXME - change to visit()
        return action;
    }


    public Operator addInfixOperator(String name, int index, int precedence, int arity, boolean leftAssociative)
    {
        OperatorImpl op = new OperatorImpl(name, index, precedence, arity, leftAssociative);
        ledOperators.put(name, op);
        return op;
    }
    
    public Operator addPrefixOperator(String name, int index, int precedence, int arity)
    {
        OperatorImpl op = new OperatorImpl(name, index, precedence, arity, true);
        nudOperators.put(name, op);
        return op;
    }
    
    public Operator addPostfixOperator(String name, int index, int precedence, int arity)
    {
        OperatorImpl op = new OperatorImpl(name, index, precedence, arity, true);
        ledOperators.put(name, op);
        return op;
    }
    
    protected OperatorImpl addNudOperator(String name, int index, int precedence, int arity, boolean leftAssociative)
    {
        return addNudOperator(name, null, index, precedence, arity, leftAssociative);
    }
    
    protected OperatorImpl addNudOperator(String leftName, String rightName, int index, int precedence, int arity, boolean leftAssociative)
    {
        OperatorImpl op = new OperatorImpl(leftName, rightName, index, precedence, arity, leftAssociative);
        nudOperators.put(leftName, op);
        return op;
    }
    
    protected OperatorImpl addLedOperator(String name, int index, int precedence, int arity, boolean leftAssociative)
    {
        return addLedOperator(name, null, index, precedence, arity, leftAssociative);
    }
    
    protected OperatorImpl addLedOperator(String leftName, String rightName, int index, int precedence, int arity, boolean leftAssociative)
    {
        OperatorImpl op = new OperatorImpl(leftName, rightName, index, precedence, arity, leftAssociative);
        ledOperators.put(leftName, op);
        return op;
    }
    
    protected OperatorImpl getNudOperator() throws ParserException
    {
        Object operator = null;
        int token = getToken();
        // Operators may include reserved words and brackets.
        if (token == Lexer.OPERATOR || token == Lexer.RESERVED || token == Lexer.OPEN_P)
        {
            Object lookup = getLexer().getTokenValue();
            operator =  nudOperators.get(lookup);
            // Throw an exception if this is a pure operator that we know nothing about.
            if (operator == null && token == Lexer.OPERATOR && ledOperators.get(lookup) == null)
                throw new ParserException("Operator not implemented: " + getLexer().getTokenValue());
        }
        return (OperatorImpl)operator;
    }

    protected OperatorImpl getLedOperator() throws ParserException
    {
        Object operator = null;
        int token = getToken();
        // Operators may include reserved words and brackets.
        if (token == Lexer.OPERATOR || token == Lexer.RESERVED || token == Lexer.OPEN_P)
        {
            Object lookup = getLexer().getTokenValue();
            operator =  ledOperators.get(lookup);
            // Throw an exception if this is a pure operator that we know nothing about.
            if (operator == null && token == Lexer.OPERATOR && nudOperators.get(lookup) == null)
                throw new ParserException("Operator not implemented: " + getLexer().getTokenValue());
        }
        return (OperatorImpl)operator;
    }

    public Object expression() throws ParserException
    {
        Object result = expression(0);
        result = getPrecidenceActions().postProcess(result);
        return result;
    }

    protected Object expression(int minPrecedence) throws ParserException
    {
        Object leftOperand = unaryExpression(minPrecedence);
        OperatorImpl operator = null;
        while ((operator = getLedOperator()) != null && operator.precedence >= minPrecedence)
        {
            nextToken();
            leftOperand = ledExpression(leftOperand, operator, minPrecedence);
        }
        return leftOperand;
    }
    
    protected Object ledExpression(Object leftOperand, OperatorImpl operator, int minPrecedence) throws ParserException
    {
        if (operator.mode == Operator.BINARY)
        {
            int nextPrecedence = operator.leftAssociative ? 
                   operator.precedence + 1 : operator.precedence;
            Object rightOperand = expression(nextPrecedence);
            leftOperand = getPrecidenceActions().infixAction(operator, leftOperand, rightOperand);
        }
        else if (operator.mode == Operator.POSTFIX)
        {
            leftOperand = getPrecidenceActions().postfixAction(operator, leftOperand);
        }
        else if (operator.mode == Operator.BRACKET)
        {
            if (accept(Lexer.CLOSE_P, operator.endName))
                leftOperand = getPrecidenceActions().bracketAction(operator, leftOperand, null);
            else
            {
                Object value = expression(0);
                expect(Lexer.CLOSE_P, operator.endName, null);
                leftOperand = getPrecidenceActions().bracketAction(operator, leftOperand, value);
            }
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
            result = expression(nudOperator.getPrecedence());
            result = getPrecidenceActions().prefixAction(nudOperator, result);
        }
        else if (nudOperator.mode == Operator.BRACKET)
        {
            Object firstOperand = expression(0);
            expect(Lexer.CLOSE_P, nudOperator.endName, null);
            result = getPrecidenceActions().parenthesesAction(firstOperand);
        }
        else
            throw new IllegalStateException("Unknown operator arity: " + nudOperator.mode);
        return result;
    }

}