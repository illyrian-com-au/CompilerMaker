package au.com.illyrian.parser.impl;

public interface ParserConstants {
    /* Unary operators */
    public static final int DOT = 9;
    public static final int NOP = 10;
    public static final int INV = 11;
    public static final int NEG = 12;
    public static final int NOT = 13;
    public static final int INC = 14;
    public static final int DEC = 15;
    public static final int POSTINC = 16;
    public static final int POSTDEC = 17;
    public static final int NEW = 18;
    public static final int CAST = 19;
    
    /* Binary operators */
    public static final int POW = 1;
    public static final int MULT = 21;
    public static final int DIV = 22;
    public static final int REM = 23;
    public static final int ADD = 31;
    public static final int SUBT = 32;
    public static final int SHL = 41;
    public static final int SHR = 42;
    public static final int USHR = 43;
    public static final int LT = 51;
    public static final int GT = 52;
    public static final int LE = 53;
    public static final int GE = 54;
    public static final int INSTANCEOF = 55;
    public static final int EQ = 61;
    public static final int NE = 62;
    public static final int AND = 71;
    public static final int XOR = 72;
    public static final int OR  = 73;
    public static final int ANDTHEN = 74;
    public static final int ORELSE = 75;
    public static final int ASSIGN = 91;

    /* Other operators */
    public static final int CALL = 101;
    public static final int INDEX = 101;

}
