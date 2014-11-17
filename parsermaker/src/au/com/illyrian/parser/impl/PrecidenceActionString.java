package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.Operator;
import au.com.illyrian.parser.ParserException;

public class PrecidenceActionString implements PrecidenceAction<String>
{
    public String literalAction(Lexer lexer) throws ParserException
    {
        return lexer.getTokenValue();
    }

    public String identifierAction(String name) throws ParserException
    {
        return name;
    }

    public String beginParameters(String name) throws ParserException
    {
        return name + "(";
    }

    public String addParameter(String callStack, String param) throws ParserException
    {
        return callStack + " " + param;
    }

    public String callAction(String name, String callStack) throws ParserException
    {
        return callStack + ")";
    }

    public String literalAction(Integer value) throws ParserException
    {
        return value.toString();
    }

    public String literalAction(String value) throws ParserException
    {
        return value;
    }

    public String parenthesesAction(String expr) throws ParserException
    {
        return "(" + expr + ")";
    }

    public String infixAction(Operator operator, String leftOperand, String rightOperand) throws ParserException
    {
        if (operator.getMode() == Operator.SHORTCUT)
            return logicAction(operator, leftOperand, rightOperand);
        return leftOperand + " " + rightOperand + " " + operator.getName();
    }

    public String prefixAction(Operator operator, String operand) throws ParserException
    {
        return operand + " " + operator.getName() + "$";
    }

    public String postfixAction(Operator operator, String operand) throws ParserException
    {
        return operand + " $" + operator.getName();
    }

    public String bracketAction(Operator operator, String leftOperand, String rightOperand) throws ParserException
    {
        return leftOperand + " " + operator.getName() + rightOperand + operator.getEndName();
    }

    public String assignAction(Operator operator, String leftOperand, String name, String rightOperand) throws ParserException
    {
        return infixAction(operator, leftOperand, rightOperand);
    }

    public String assignAction(Operator operator, String name, String rightOperand) throws ParserException
    {
        return prefixAction(operator, rightOperand);
    }

    public String preProcess(String operand, Operator nextOperator) throws ParserException
    {
        if (nextOperator.getMode() == Operator.SHORTCUT)
            return shortcutAction(nextOperator, operand);
        
        return operand;
    }

    public String postProcess(String result) throws ParserException
    {
        return result;
    }

    public String castAction(String type, String value) throws ParserException
    {
        return value + " cast(" + type + ")";
    }

    public String shortcutAction(Operator operator, String leftOperand) throws ParserException
    {
        return leftOperand + " " + operator.getName() + "{";
    }

    public String logicAction(Operator operator, String leftOperand, String rightOperand) throws ParserException
    {
        return leftOperand + "" + rightOperand + "}";
    }

}
