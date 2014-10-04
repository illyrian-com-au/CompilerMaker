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
public interface ClassAction extends ModuleAction
{
    public Object addModifier(String modifier);
    
    public Object setClassModifiers(Object modifiers);
    
    public String setClassName(String simpleName);
    
    public Object declareExtends(String className);

    public Object declareImplements(String className);
}
