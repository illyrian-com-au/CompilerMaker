package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.Operator;
import au.com.illyrian.parser.ParserException;

public class PrecidenceActionString implements PrecidenceAction
{
    public Object literalAction(Lexer lexer) throws ParserException
    {
        return lexer.getTokenValue();
    }

    public Object identifierAction(String name) throws ParserException
    {
        return name;
    }

    public Object beginParameters(Object name) throws ParserException
    {
        return name + "(";
    }

    public Object addParameter(Object callStack, Object param) throws ParserException
    {
        return callStack + " " + param;
    }

    public Object callAction(Object callStack) throws ParserException
    {
        return callStack + ")";
    }

    public Object literalAction(Integer value) throws ParserException
    {
        return value.toString();
    }

    public Object literalAction(String value) throws ParserException
    {
        return value;
    }

    public Object parenthesesAction(Object expr) throws ParserException
    {
        return "(" + expr + ")";
    }

    public Object infixAction(Operator operator, Object leftOperand, Object rightOperand) throws ParserException
    {
        if (operator.getMode() == Operator.SHORTCUT)
            return logicAction(operator, leftOperand, rightOperand);
        return leftOperand + " " + rightOperand + " " + operator.getName();
    }

    public Object prefixAction(Operator operator, Object operand) throws ParserException
    {
        return operand + " " + operator.getName() + "$";
    }

    public Object postfixAction(Operator operator, Object operand) throws ParserException
    {
        return operand + " $" + operator.getName();
    }

    public Object bracketAction(Operator operator, Object leftOperand, Object rightOperand) throws ParserException
    {
        return leftOperand + " " + operator.getName() + rightOperand + operator.getEndName();
    }

    public Object assignAction(Operator operator, Object leftOperand, String name, Object rightOperand) throws ParserException
    {
        return infixAction(operator, leftOperand, rightOperand);
    }

    public Object assignAction(Operator operator, String name, Object rightOperand) throws ParserException
    {
        return prefixAction(operator, rightOperand);
    }

    public Object preProcess(Object operand, Operator nextOperator) throws ParserException
    {
        if (nextOperator.getMode() == Operator.SHORTCUT)
            return shortcutAction(nextOperator, operand);
        
        return operand;
    }

    public Object postProcess(Object result) throws ParserException
    {
        return result;
    }

    public Object castAction(Object type, Object value) throws ParserException
    {
        return value + " cast(" + type + ")";
    }

    public Object shortcutAction(Operator operator, Object leftOperand) throws ParserException
    {
        return leftOperand + " " + operator.getName() + "{";
    }

    public Object logicAction(Operator operator, Object leftOperand, Object rightOperand) throws ParserException
    {
        return leftOperand + "" + rightOperand + "}";
    }

}
