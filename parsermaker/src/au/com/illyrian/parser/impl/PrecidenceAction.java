/*
 * Created on 8/05/2008 by strongd
 *
 * Copyright (c) 2006 Department of Infrastructure (DOI)
 * State Government of Victoria, Australia
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of DOI.
 */

package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;

/**
 *
 * An interface for performing actions on expressions recognised by the parser.
 *
 * @author strongd
 */
public interface PrecidenceAction<Expr>
{
//    /* Unary operators */
//    public static final int DOT = 9;
//    public static final int NOP = 10;
//    public static final int INV = 11;
//    public static final int NEG = 12;
//    public static final int NOT = 13;
//    public static final int INC = 14;
//    public static final int DEC = 15;
//    public static final int POSTINC = 16;
//    public static final int POSTDEC = 17;
//    public static final int NEW = 18;
//    public static final int CAST = 19;
//    
//    /* Binary operators */
//    public static final int POW = 1;
//    public static final int MULT = 21;
//    public static final int DIV = 22;
//    public static final int REM = 23;
//    public static final int ADD = 31;
//    public static final int SUBT = 32;
//    public static final int SHL = 41;
//    public static final int SHR = 42;
//    public static final int USHR = 43;
//    public static final int LT = 51;
//    public static final int GT = 52;
//    public static final int LE = 53;
//    public static final int GE = 54;
//    public static final int INSTANCEOF = 55;
//    public static final int EQ = 61;
//    public static final int NE = 62;
//    public static final int AND = 71;
//    public static final int XOR = 72;
//    public static final int OR  = 73;
//    public static final int ANDTHEN = 74;
//    public static final int ORELSE = 75;
//    public static final int ASSIGN = 91;
//
//    /* Other operators */
//    public static final int CALL = 101;
//    public static final int INDEX = 101;
    
    /**
     * Perform an action to process an Identifier.
     *
     * @param name -
     *            the text of the identifier.
     * @return an expression structure containing the identifier.
     * @throws ParserException -
     *             if an error occurs.
     */
    public Expr identifierAction(String name) throws ParserException;

    /**
     * Perform an action to process an Integer.
     *
     * @param lexer -  the lexer containing the literal value.
     * @return an object representing the literal.
     * @throws ParserException - if an error occurs.
     */
    public Expr literalAction(Lexer lexer) throws ParserException;

    /**
     * Perform an action to process parentheses.
     *
     * @param expr -
     *            the expression contained in the parentheses.
     * @return an expression structure implying the use of parentheses.
     * @throws ParserException -
     *             if an error occurs.
     */
    public Expr parenthesesAction(Expr expr) throws ParserException;

    /**
     * Perform an action to process an infix operator.
     *
     * @param operator - information about the operator
     * @param leftOperand - the expression on the left of the operand
     * @param rightOperand - the expression on the right of the operand
     * @return an object representing the operation
     * @throws ParserException - if an error occurs
     */
    public Expr infixAction(Operator operator, Expr leftOperand, Expr rightOperand) throws ParserException;

    /**
     * Perform an action to process an infix operator.
     *
     * @param operator - information about the operator
     * @param 0perand - the expression on the right of the operand
     * @return an object representing the operation
     * @throws ParserException - if an error occurs
     */
    public Expr prefixAction(Operator operator, Expr operand) throws ParserException;

    /**
     * Perform an action to process an infix operator.
     *
     * @param operator - information about the operator
     * @param 0perand - the expression on the left of the operand
     * @return an object representing the operation
     * @throws ParserException - if an error occurs
     */
    public Expr postfixAction(Operator operator, Expr operand) throws ParserException;

    public Expr bracketAction(Operator operator, Expr leftOperand, Expr rightOperand) throws ParserException;

    public Expr castAction(Expr type, Expr value) throws ParserException;

    public Expr callAction(Expr name, Expr callStack) throws ParserException;

    public Expr beginParameters(Expr name) throws ParserException;

    public Expr addParameter(Expr callStack, Expr param) throws ParserException;
}
