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

package au.com.illyrian.jesub.ast;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerLocation;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.ast.TerminalNumber;
import au.com.illyrian.classmaker.types.Type;

public class AstStructureFactoryMaker extends AstExpressionFactory implements ClassMakerLocation
{
    protected ClassMaker  maker = null;
    protected AstExpressionVisitor visitor = null; 

    int modifiers = 0;
    
    public AstStructureFactoryMaker(ClassMaker maker)
    {
    	setClassMaker(maker);
    }
    
    public void setClassMaker(ClassMaker classMaker) 
    {
    	maker = classMaker;
    	visitor = new AstExpressionVisitor(maker);
	}

    public ClassMaker getClassMaker()
    {
        if (maker == null)
            throw new NullPointerException("classMaker is null.");
        return maker;
    }

    public void Package(AstExpression packageName)
    {
    	String path = packageName.resolvePath(visitor);
        maker.setPackageName(path);
    }
    
    public void Import(AstExpression className)
    {
    	String path = className.resolvePath(visitor);
        maker.Import(path);
    }
    
    public void Modifier(String modifierName)
    {
    	modifiers = maker.addModifier(modifiers, modifierName);
    }
    
    public int popModifiers()
    {
        int temp = modifiers;
        modifiers = 0;
        return temp;
    }
    
    public void ClassName(TerminalName className)
    {
    	ClassName(popModifiers(), className);
    }
    
    public void ClassName(int modifiers, TerminalName className)
    {
    	maker.setClassModifiers(modifiers);
    	maker.setSimpleClassName(className.getName());
    }
    
    public void Extends(AstExpression className)
    {
    	String path = className.resolvePath(visitor);
        maker.Extends(path);
    }
    
    public void Implements(AstExpression className)
    {
    	String path = className.resolvePath(visitor);
        maker.Implements(path);
    }
    
    public void Declare(AstExpression type, TerminalName name)
    {
        Declare(popModifiers(), type, name);
    }
    
    public void Declare(int modifiers, AstExpression type, TerminalName name)
    {
    	String typeName = type.resolvePath(visitor);
    	String varName = name.getName();
    	maker.Declare(varName, typeName, modifiers);
    }
    
    public void Method(AstExpression type, TerminalName name)
    {
        Method(popModifiers(), type, name);
    }
    
    public void Method(int modifiers, AstExpression type, TerminalName name)
    {
    	String typeName = type.resolvePath(visitor);
    	String varName = name.getName();
    	maker.Method(varName, typeName, modifiers);
    }
    
    public void Begin()
    {
    	maker.Begin();
    }
    
    public void End()
    {
    	maker.End();
    }
    
    public void Return(AstExpression expr)
    {
    	Type type = expr.resolveType(visitor);
    	maker.Return(type);
    }
    
    public void Return()
    {
    	maker.Return();
    }
    
    public void Eval(AstExpression expr)
    {
    	Type type = expr.resolveType(visitor);
    	maker.Eval(type);
    }
    
    public void If(AstExpression condition)
    {
    	Type type = condition.resolveType(visitor);
    	maker.If(type);
    }
    
    public void Else()
    {
    	maker.Else();
    }
    
    public void EndIf()
    {
    	maker.EndIf();
    }
    
    public void While(AstExpression condition)
    {
    	maker.Loop();
    	Type type = condition.resolveType(visitor);
    	maker.While(type);
    }
    
    public void EndWhile()
    {
    	maker.EndLoop();
    }

    public void Break()
    {
    	maker.Break();
    }

    public void Continue()
    {
    	maker.Continue();
    }

	public void Switch(AstExpression expr) 
	{
    	Type type = expr.resolveType(visitor);
		maker.Switch(type);
	}
    
    public void Case(TerminalNumber value)
    {
    	maker.Case(value.intValue());
    }

    public void Default()
    {
    	maker.Default();
    }

    public void EndSwitch()
    {
    	maker.EndSwitch();
    }

    public void Try()
    {
    	maker.Try();
    }

    public void Catch(AstExpression type, TerminalName name)
    {
    	String typeString = type.resolvePath(visitor);
    	String nameString = name.getName();
    	maker.Catch(typeString, nameString);
    }

    public void Finally()
    {
    	maker.Finally();
    }

    public void EndTry()
    {
    	maker.EndTry();
    }

}
