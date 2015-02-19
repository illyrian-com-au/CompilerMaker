package au.com.illyrian.parser.impl;

import java.util.HashMap;
import java.util.Map;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;

public class PrecidenceParser<Expr> extends ParserBase
{
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

    public Operator addInfixOperator(String name, int index, int precedence, int arity, boolean leftAssociative)
    {
        return addLedOperator(name, null, index, precedence, arity, leftAssociative);
    }
    
    public Operator addPrefixOperator(String name, int index, int precedence, int arity)
    {
        return addNudOperator(name, null, index, precedence, arity, true);
    }
    
    public Operator addPrefixOperator(String leftName, String rightName, int index, int precedence, int arity)
    {
        return addNudOperator(leftName, rightName, index, precedence, arity, true);
    }
    
    public Operator addPostfixOperator(String name, int index, int precedence, int arity)
    {
        return addLedOperator(name, null, index, precedence, arity, true);
    }
    
    public Operator addPostfixOperator(String leftName, String rightName, int index, int precedence, int arity)
    {
        return addLedOperator(leftName, rightName, index, precedence, arity, true);
    }
    
    protected Operator addNudOperator(String leftName, String rightName, int index, int precedence, int arity, boolean leftAssociative)
    {
        Operator op = new Operator(leftName, rightName, index, precedence, arity, leftAssociative);
        nudOperators.put(leftName, op);
        return op;
    }
    
    protected Operator addLedOperator(String leftName, String rightName, int index, int precedence, int arity, boolean leftAssociative)
    {
        Operator op = new Operator(leftName, rightName, index, precedence, arity, leftAssociative);
        ledOperators.put(leftName, op);
        return op;
    }
    
    protected Operator getNudOperator() throws ParserException
    {
    	Operator operator = null;
        int token = getToken();
        // Operators may include reserved words and brackets.
        if (token == Lexer.OPERATOR || token == Lexer.RESERVED || token == Lexer.OPEN_P)
        {
            String lookup = getLexer().getTokenValue();
            operator = nudOperators.get(lookup);
            // Throw an exception if this is a pure operator that we know nothing about.
            if (operator == null && token == Lexer.OPERATOR && ledOperators.get(lookup) == null)
                throw new ParserException("Operator not implemented: " + getLexer().getTokenValue());
        }
        return operator;
    }

    protected Operator getLedOperator() throws ParserException
    {
        Operator operator = null;
        int token = getToken();
        // Operators may include reserved words and brackets.
        if (token == Lexer.OPERATOR || token == Lexer.RESERVED || token == Lexer.OPEN_P)
        {
            String lookup = getLexer().getTokenValue();
            operator =  ledOperators.get(lookup);
            // Throw an exception if this is a pure operator that we know nothing about.
            if (operator == null && token == Lexer.OPERATOR && nudOperators.get(lookup) == null)
                throw new ParserException("Operator not implemented: " + getLexer().getTokenValue());
        }
        return operator;
    }

    public Expr expression() throws ParserException
    {
        Expr result = expression(0);
        return result;
    }

    protected Expr expression(int minPrecedence) throws ParserException
    {
        Expr leftOperand = unaryExpression(minPrecedence);
        Operator operator = null;
        while ((operator = getLedOperator()) != null && operator.precedence >= minPrecedence)
        {
            nextToken();
            leftOperand = ledExpression(leftOperand, operator, minPrecedence);
        }
        return leftOperand;
    }
    
    protected Expr ledExpression(Expr leftOperand, Operator operator, int minPrecedence) throws ParserException
    {
        if (operator.mode == Operator.BINARY)
        {
            int nextPrecedence = operator.leftAssociative ? 
                   operator.precedence + 1 : operator.precedence;
            Expr rightOperand = expression(nextPrecedence);
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
                Expr subExpression = expression(0);
                expect(Lexer.CLOSE_P, operator.endName, null);
                leftOperand = getPrecidenceActions().bracketAction(operator, leftOperand, subExpression);
            }
        }
        else
          throw new IllegalStateException("Unknown operator arity: " + operator.mode);
        return leftOperand;
    }

    
    protected Expr unaryExpression(int minPrecidence) throws ParserException
    {
        Expr result = null;
        Operator nudOperator = null; 
        if ((nudOperator = getNudOperator()) != null)
        {
            nextToken();
            result = nudExpression(nudOperator, minPrecidence);
        }
        else if (getToken() == Lexer.IDENTIFIER)
        {
            String identifier = getLexer().getTokenValue();
            result = getPrecidenceActions().identifierAction(identifier);
            nextToken();
        }
        else if (getToken() == Lexer.INTEGER)
        {
            result = getPrecidenceActions().literalAction(getLexer());
            nextToken();
        }
        else if (accept(Lexer.OPEN_P, "("))
        {
            Expr subExpression = expression(0);
            expect(Lexer.CLOSE_P, ")", "\')\' expected.");
            result = getPrecidenceActions().parenthesesAction(subExpression);
        }
        else
        {
            throw error(getInput(), "Expression expected.");
        }
    
        return result;
    }
    
    protected Expr nudExpression(Operator nudOperator, int minPrecedence) throws ParserException
    {
        Expr result = null;
        if (nudOperator.mode == Operator.PREFIX )
        {
            Expr subExpression = expression(nudOperator.getPrecedence());
            result = getPrecidenceActions().prefixAction(nudOperator, subExpression);
        }
        else if (nudOperator.mode == Operator.BRACKET)
        {
            Expr subExpression = expression(0);
            expect(Lexer.CLOSE_P, nudOperator.endName, null);
            result = getPrecidenceActions().parenthesesAction(subExpression);
        }
        else
            throw new IllegalStateException("Unknown operator arity: " + nudOperator.mode);
        return result;
    }

}