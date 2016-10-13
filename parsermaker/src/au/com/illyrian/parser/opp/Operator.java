package au.com.illyrian.parser.opp;


public class Operator
{
    public static final int PREFIX = 1;
    public static final int POSTFIX = 2;
    public static final int BINARY = 4;
    public static final int BINARYRIGHT = 8;
    public static final int TERNARY = 16;
    public static final int BRACKET = 32;
    public static final int PARAMS = 64;

    public final String name;
    public final int index;
    public final int precidence;
    public final int mode;
    public final String endName;

    public Operator(String name, int index, int precidence, int mode)
    {
        this.name = name;
        this.endName = null;
        this.index = index;
        this.precidence = precidence;
        this.mode = mode;
    }
    
    public Operator(String name, String endName, int index, int precidence, int mode)
    {
        this.name = name;
        this.endName = endName;
        this.index = index;
        this.precidence = precidence;
        this.mode = mode;
    }

    public String getName()
    {
        return name;
    }

    public String getEndName()
    {
        return endName;
    }

    public int getIndex()
    {
        return index;
    }

    public int getPrecidence()
    {
        return precidence;
    }

    public int getMode()
    {
        return mode;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("Operator(\"").append(name).append("\", ");
        if (endName != null)
            buf.append(endName).append(", ");
        buf.append(index).append(", ");
        buf.append(precidence).append(", ");
        switch (mode)
        {
        case Operator.PREFIX:
            buf.append("prefix");
            break;
        case Operator.POSTFIX:
            buf.append("postfix");
            break;
        case Operator.BINARY:
            buf.append("binary");
            break;
        case Operator.BINARYRIGHT:
            buf.append("binary_right");
            break;
        case Operator.TERNARY:
            buf.append("ternary");
            break;
        case Operator.BRACKET:
            buf.append("bracket");
            break;
        default:
            buf.append("<error!>");
        }
        buf.append(")");
        return buf.toString();
    }
}
