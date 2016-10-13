/*
 * Created on 8/05/2008 by strongd
 *
 * Copyright (c) 2006 Department of Infrastructure (DOI)
 * State Government of Victoria, Australia
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of DOI.
 */

package au.com.illyrian.domainparser;


/**
 *
 * An interface for performing actions on expressions recognised by the parser.
 *
 * @author strongd
 */
public interface ModuleAction
{
    public Object Package(String packageName);
    
    public Object Import(String fullyQualifiedClassname);

    public String getParserName(String simpleName);

    public String Dot(String className, String simpleName);
    
    public Object getModule();

    public Object handleModule(Object result);
}
