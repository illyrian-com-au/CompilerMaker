// Copyright (c) 2014, Donald Strong.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.

package au.com.illyrian.expressionparser;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerLocation;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.types.Type;

/**
 * 
 * @author donald
 */
public class ExpressionActionFactory 
    implements ClassMakerLocation
{
    private ClassMaker  maker = null;

    public ExpressionActionFactory()
    {
        
    }
    
    public void setClassMaker(ClassMaker classMaker) 
    {
        maker = classMaker;
    }

    public ClassMaker getClassMaker()
    {
        if (maker == null)
            throw new NullPointerException("classMaker is null.");
        return maker;
    }

    public Object declareFunctionName(String identifier)
    {
        getClassMaker().Method(identifier, ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        return null;
    }

    public Object addParameter(Object params, String identifier)
    {
        getClassMaker().Declare(identifier, int.class, 0);
        return null;
    }

    public Object declareFunction(Object name, Object params, Object body)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object addFunction(Object function, Object functionList)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object declareClassname(String identifier)
    {
        getClassMaker().setSimpleClassName(identifier);
        return null;
    }

    public Object declareExtends(String extendClass)
    {
        getClassMaker().Extends(extendClass);
        return null;
    }

    public Object declareClass(Object classname, Object extendClass, Object functionList)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void beginMethod()
    {
        getClassMaker().Begin();
    }

    public Object endMethod(Object result)
    {
    	AstExpression expr = (AstExpression)result;
    	AstExpressionVisitor visitor = new AstExpressionVisitor(maker);
        Type type = expr.resolveType(visitor);
        
        getClassMaker().Return(type);
        getClassMaker().End();
        return null;
    }

    public Object getModule()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
