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

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;

/**
 *
 * An interface for generating expressions from the Lexer.
 *
 * @author strongd
 */
public interface LexerTokenAction<Expr>
{
    /**
     * Perform an action to process a token.
     *
     * @param lexer -  the lexer containing the token.
     * @return an object representing the token.
     * @throws ParserException - if an error occurs.
     */
    public Expr tokenAction(Lexer lexer) throws ParserException;

}
