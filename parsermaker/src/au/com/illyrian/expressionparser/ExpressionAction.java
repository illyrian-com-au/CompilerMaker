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

import au.com.illyrian.parser.impl.PrecidenceAction;

/**
 *
 * An interface for performing actions on expressions recognised by the parser.
 *
 * @author strongd
 */
public interface ExpressionAction <T> extends PrecidenceAction <T>
{
	public Object declareFunctionName(String identifier);

    public Object addParameter(Object params, String identifier);

    public Object declareFunction(Object name, Object params, Object body);

    public Object addFunction(Object function, Object functionList);

    public Object declareClassname(String identifier);

    public Object declareExtends(String extendClass);

    public Object declareClass(Object classname, Object extendClass, Object functionList);

	public void beginMethod();

	public Object endMethod(Object type);
	
	public Object getModule();
}
