package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.Operator;

public class OperatorImpl implements Operator
{
    public final String name;
    public final int index;
    public final int precedence;
    public final int mode;
    public final boolean leftAssociative;
    public final String endName;

    public OperatorImpl(String name, int index, int precedence, int arity, boolean leftAssociative)
    {
        this.name = name;
        this.endName = null;
        this.index = index;
        this.precedence = precedence;
        this.mode = arity;
        this.leftAssociative = leftAssociative;
    }
    
    public OperatorImpl(String name, String endName, int index, int precedence, int arity, boolean leftAssociative)
    {
        this.name = name;
        this.endName = endName;
        this.index = index;
        this.precedence = precedence;
        this.mode = arity;
        this.leftAssociative = leftAssociative;
    }
    /* (non-Javadoc)
     * @see au.com.illyrian.expressionparser.OperatorIface#getName()
     */
    public String getName()
    {
        return name;
    }

    public String getEndName()
    {
        return endName;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.expressionparser.OperatorIface#getIndex()
     */
    public int getIndex()
    {
        return index;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.expressionparser.OperatorIface#getPrecedence()
     */
    public int getPrecedence()
    {
        return precedence;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.expressionparser.OperatorIface#getArity()
     */
    public int getMode()
    {
        return mode;
    }

    /* (non-Javadoc)
     * @see au.com.illyrian.expressionparser.OperatorIface#isLeftAssociative()
     */
    public boolean isLeftAssociative()
    {
        return leftAssociative;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("Operator(\"").append(name).append("\", ");
        if (endName != null)
            buf.append(endName).append(", ");
        buf.append(precedence).append(", ");
        switch (mode)
        {
        case Operator.PREFIX:
            buf.append("prefix, ");
            break;
        case Operator.POSTFIX:
            buf.append("postfix, ");
            break;
        case Operator.BINARY:
            buf.append("binary, ");
            break;
        case Operator.TERNARY:
            buf.append("ternary, ");
            break;
        case Operator.BRACKET:
            buf.append("bracket, ");
            break;
//        case Operator.IDENTCHAIN:
//            buf.append("identchain, ");
//            break;
        case Operator.SHORTCUT:
            buf.append("shortcut, ");
            break;
        default:
            buf.append("<error!>, ");
        }
        buf.append(leftAssociative ? "leftAssociative" : "rightAssociative");
        buf.append(")");
        return buf.toString();
    }
}
