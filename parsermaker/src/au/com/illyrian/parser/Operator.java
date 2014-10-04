package au.com.illyrian.parser;

public interface Operator
{
    public static final int PREFIX = 1;
    public static final int POSTFIX = 2;
    public static final int BINARY = 4;
    public static final int TERNARY = 8;
    public static final int BRACKET = 16;
    public static final int PARAMS = 32;
    public static final int SHORTCUT = 64;

    public String getName();
    
    public String getEndName();

    public int getIndex();

    public int getPrecedence();

    public int getMode();

    public boolean isLeftAssociative();

}