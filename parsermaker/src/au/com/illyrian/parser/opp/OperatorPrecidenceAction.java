/*
 * Created on 8/05/2008 by strongd
 *
 * Copyright (c) 2006 Department of Infrastructure (DOI)
 * State Government of Victoria, Australia
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of DOI.
 */

package au.com.illyrian.parser.opp;


/**
 *
 * An interface for performing actions on expressions recognized by the parser.
 *
 * @author strongd
 */
public interface OperatorPrecidenceAction<Expr> extends LexerTokenAction<Expr>
{
    /**
     * Perform an action to process a binary operator.
     *
     * @param operator - information about the operator
     * @param leftOperand - the expression on the left of the operand
     * @param rightOperand - the expression on the right of the operand
     * @return an object representing the operation
     */
    public Expr binaryAction(int operator, Expr leftOperand, Expr rightOperand);

    /**
     * Perform an action to process a unary operator.
     *
     * @param operator - information about the operator
     * @param operand - the expression on the right of the operand
     * @return an object representing the operation
     */
    public Expr unaryAction(int operator, Expr operand);
}
