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
     * Perform an action to process a literal.
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
     * @param operand - the expression on the right of the operand
     * @return an object representing the operation
     * @throws ParserException - if an error occurs
     */
    public Expr prefixAction(Operator operator, Expr operand) throws ParserException;

    /**
     * Perform an action to process an infix operator.
     *
     * @param operator - information about the operator
     * @param operand - the expression on the left of the operand
     * @return an object representing the operation
     * @throws ParserException - if an error occurs
     */
    public Expr postfixAction(Operator operator, Expr operand) throws ParserException;

    public Expr bracketAction(Operator operator, Expr leftOperand, Expr rightOperand) throws ParserException;

    public Expr castAction(Expr type, Expr value) throws ParserException;

    public Expr callAction(Expr name, Expr callStack) throws ParserException;
}
