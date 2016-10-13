/*
 * Created on 8/05/2008 by strongd
 *
 * Copyright (c) 2006 Department of Infrastructure (DOI)
 * State Government of Victoria, Australia
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of DOI.
 */

package au.com.illyrian.expressionparser;

import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.opp.OperatorPrecidenceAction;

/**
 *
 * An interface for performing actions on expressions recognised by the parser.
 *
 * @author strongd
 */
public interface ExpressionAction <T> extends OperatorPrecidenceAction <T>
{
    public Object declareFunctionName(String identifier) throws ParserException;

    public Object addParameter(Object params, String identifier) throws ParserException;

    public Object declareFunction(Object name, Object params, Object body) throws ParserException;

    public Object addFunction(Object function, Object functionList) throws ParserException;

    public Object declareClassname(String identifier) throws ParserException;

    public Object declareExtends(String extendClass) throws ParserException;

    public Object declareClass(Object classname, Object extendClass, Object functionList) throws ParserException;

    public void beginMethod() throws ParserException;

    public Object endMethod(Object type) throws ParserException;

    public Object getModule() throws ParserException;
}
