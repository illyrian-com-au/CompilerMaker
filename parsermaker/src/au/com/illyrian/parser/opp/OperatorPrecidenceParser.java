package au.com.illyrian.parser.opp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.ParserBase;
import au.com.illyrian.parser.impl.ParserConstants;

public class OperatorPrecidenceParser<Expr> extends ParserBase
{
    public static final int DEFAULT_PRECIDENCE = 0;

    /**
     * A map from operator to an object.
     */
    protected final Map<String, Operator> nudOperators;
    protected final Map<String, Operator> ledOperators;
    protected final Set<String> reservedOperands;
    protected final Set<String> reservedOperators;
    
    protected OperatorPrecidenceAction<Expr> precidenceActions = null;
    protected int defaultPrecidence = DEFAULT_PRECIDENCE;

    public OperatorPrecidenceParser()
    {
        super();
        nudOperators = new HashMap<String, Operator>();
        ledOperators = new HashMap<String, Operator>();
        reservedOperands = new HashSet<String>();
        reservedOperators = new HashSet<String>();
    }

    public OperatorPrecidenceAction<Expr> getPrecidenceActions()
    {
        if (precidenceActions == null)
            throw new IllegalStateException("PrecidenceAction not provided.");
        return precidenceActions;
    }

    public void setPrecidenceActions(OperatorPrecidenceAction<Expr> actions)
    {
        precidenceActions = actions;
    }

    /**
     * Add a binary or postfix Led operator.
     * @param name the character string that the lexer should match
     * @param index a unique number representing the operator
     * @param precedence the binding power of the operator
     * @param arity BINARY, BINARYRIGHT or POSTFIX
     * @return the new operator that has already been added to the parser
     */
    public Operator addLedOperator(String name, int index, int precedence, int arity)
    {
        Operator op = new Operator(name, index, precedence, arity);
        return addLedOperator(op);
    }

    public Operator addLedOperator(String leftName, String rightName, int index, int precedence, int arity)
    {
        Operator op = new Operator(leftName, rightName, index, precedence, arity);
        return addLedOperator(op);
    }

    public Operator addNudOperator(String name, int index, int precedence, int arity)
    {
        Operator op = new Operator(name, index, precedence, arity);
        return addNudOperator(op);
    }

    public Operator addNudOperator(String leftName, String rightName, int index, int precedence, int arity)
    {
        Operator op = new Operator(leftName, rightName, index, precedence, arity);
        return addNudOperator(op);
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
                && ledOperators.get(operatorName) == null) {
            throw new ParserException("Operator not implemented: " + getLexer().getTokenValue());
        }
    }
    
    public void addReservedOperand(String operand)
    {
        addReserved(operand);
        reservedOperands.add(operand);
    }

    public void addReservedOperator(String operand)
    {
        addReserved(operand);
        reservedOperators.add(operand);
    }

    public int getDefaultPrecidence()
    {
        return defaultPrecidence;
    }

    public void setDefaultPrecidence(int defaultPrecidence)
    {
        this.defaultPrecidence = defaultPrecidence;
    }

    public Expr expression() throws ParserException
    {
        int minPrecidence = getDefaultPrecidence();
        Expr result = expression(minPrecidence);
        return result;
    }

    protected Expr expression(int minPrecidence) throws ParserException
    {
        Expr leftOperand = unaryExpression(minPrecidence);
        Operator operator = null;
        while ((operator = getLedOperator()) != null && operator.precidence >= minPrecidence) {
            nextToken();
            leftOperand = ledExpression(leftOperand, operator, minPrecidence);
        }
        return leftOperand;
    }

    protected Expr ledExpression(Expr leftOperand, Operator operator, int minPrecidence) throws ParserException
    {
        switch (operator.mode) {
        case Operator.BINARY:
        {
            Expr rightOperand = expression(operator.precidence + 1);
            leftOperand = getPrecidenceActions().binaryAction(operator.getIndex(), leftOperand, rightOperand);
            break;
        }
        case Operator.BINARYRIGHT:
        {
            Expr rightOperand = expression(operator.precidence);
            leftOperand = getPrecidenceActions().binaryAction(operator.getIndex(), leftOperand, rightOperand);
            break;
        }
        case Operator.POSTFIX:
        {
            leftOperand = getPrecidenceActions().unaryAction(operator.getIndex(), leftOperand);
            break;
        }
        case Operator.BRACKET:
        {
            Expr subExpression = expression();
            expect(Lexer.CLOSE_P, operator.endName, null);
            leftOperand = getPrecidenceActions().binaryAction(operator.getIndex(), leftOperand, subExpression);
            break;
        }
        case Operator.PARAMS:
        {
            if (accept(Lexer.CLOSE_P, operator.endName))
                leftOperand = getPrecidenceActions().binaryAction(operator.getIndex(), leftOperand, null);
            else {
                Expr subExpression = expression();
                expect(Lexer.CLOSE_P, operator.endName, null);
                leftOperand = getPrecidenceActions().binaryAction(operator.getIndex(), leftOperand, subExpression);
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
        // Check for a prefix operator
        Operator nudOperator = getNudOperator();
        if (match(Lexer.OPEN_P, "(")) {
            result = parentheses(nudOperator);
        } else if (nudOperator != null) {
            nextToken();
            result = nudExpression(nudOperator, minPrecidence);
        } else {
            result = operandExpression();
        }
        return result;
    }
    
    protected Expr parentheses(Operator nudOperator) throws ParserException {
        Expr result = null;
        if (accept(Lexer.OPEN_P, "(")) {
            result = expression();
            expect(Lexer.CLOSE_P, ")", "\')\' expected");
        } else {
            throw error(getInput(), "Unexpected Perenthesis");
        }
        return result;
    }
    
    /**
     * Parse a Nud operator as part of an expression.
     * Nud operators include prefix operators such as: <code>+ - ! ~ ++ -- new</code><br/>
     * @param nudOperator the Nud operator that has just been recognized
     * @param minPrecedence the minimum precedence of the operator
     * @return an object of type Expr that represents the parsed expression
     * @throws ParserException if the expression has an invalid syntax
     */
    protected Expr nudExpression(Operator nudOperator, int minPrecedence) throws ParserException
    {
        Expr result = null;
        if (nudOperator.mode == Operator.PREFIX) {
            Expr subExpression = expression(nudOperator.getPrecidence());
            result = getPrecidenceActions().unaryAction(nudOperator.getIndex(), subExpression);
        } else
            throw new IllegalStateException("Unknown operator mode: " + nudOperator.mode);
        return result;
    }

    protected Expr operandExpression() throws ParserException
    {
        Expr result = null;
        switch (getToken()) {
        case Lexer.IDENTIFIER:
            result = nameOperand();
            break;
        case Lexer.RESERVED:
            result = reservedOperand();
            break;
        case Lexer.CHARACTER:
        case Lexer.INTEGER:
        case Lexer.DECIMAL:
        case Lexer.STRING:
            result = literalOperand();
            break;
        default:
            throw error(getInput(), "Expression expected");
        }
        return result;
    }
    
    protected Expr literalOperand() throws ParserException {
        Expr result = getPrecidenceActions().tokenAction(getLexer());
        nextToken();
        return result;
    }

    protected Expr nameOperand() throws ParserException {
        Expr result = getPrecidenceActions().tokenAction(getLexer());
        nextToken();
        return result;
    }

    protected Expr reservedOperand() throws ParserException {
        Expr result = getPrecidenceActions().tokenAction(getLexer());
        nextToken();
        return result;
    }
}