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
    public void setAlias(String simpleName, String className);
    
    public String getAlias(String simpleName);

    public String addClassName(String className, String simpleName);
    
    public Object declareImport(String fullyQualifiedClassname, String simpleClassname);

    public Object declarePackage(String packageName);
    
    public void handleModule(Object module);
    
    public Object getModule();
}
