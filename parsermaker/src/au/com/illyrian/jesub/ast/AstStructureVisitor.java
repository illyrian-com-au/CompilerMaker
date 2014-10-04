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

import java.util.Vector;

import au.com.illyrian.classmaker.ExpressionIfc;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.ast.ResolvePath;
import au.com.illyrian.classmaker.types.Type;

public class AstStructureVisitor extends AstExpressionVisitor
{
    ExpressionIfc maker;
    
    public AstStructureVisitor(ExpressionIfc classMaker)
    {
        super(classMaker);
        maker = classMaker;
    }
    
    public String resolvePath(AstClassPath className)
    {
        return className.expression.resolvePath(this);
    }
    
    public void resolveDeclaration(AstDeclareModule unit)
    {
        String packageName = unit.packageName.resolvePath(this);
        maker.setPackageName(packageName);
        resolveImport(unit.importsList);
        unit.declareClass.resolveDeclaration(this);
    }

    public void resolveDeclaration(AstDeclareClass unit)
    {
        int modifiers = resolveModifiers(unit.modifiers);
        maker.setClassModifiers(modifiers);
        String className = unit.className.resolvePath(this);
        maker.setSimpleClassName(className);
        resolveExtends(unit.baseClass);
        resolveImplements(unit.getImplementsList());
        resolveDeclaration(unit.membersList);
    }

    public void resolveExtends(ResolvePath className)
    {
        if (className != null)
        {
            String baseClass = className.resolvePath(this);
            maker.Extends(baseClass);
        }
    }
    
    public void resolveImport(Vector<ResolvePath> list)
    {
        for (ResolvePath item : list)
        {
            resolveImport(item);
        }
    }
    
    public void resolveImport(ResolvePath className)
    {
        String name = className.resolvePath(this);
        maker.Import(name);
    }

    public void resolveImplements(AstStructureList list)
    {
        for (AstStructure item : list.getList())
        {
            item.resolveImplements(this);
        }
    }
    
    public void resolveImplements(AstClassPath className)
    {
        String name = className.resolvePath(this);
        maker.Implements(name);
    }

    public int resolveModifiers(AstModifiers modifiers)
    {
        int modifierBits = 0; 
        if (modifiers != null)
        {
            String modifierName = modifiers.modifier;
            modifierBits = resolveModifiers(modifiers.next);
            modifierBits = maker.addModifier(modifierBits, modifierName);
        }
        return modifierBits;
    }

    public void resolveDeclaration(AstStructureList list)
    {
        for (AstStructure item : list.getList())
        {
            item.resolveDeclaration(this);
        }
    }

    public void resolveDeclaration(AstDeclareVariable member)
    {
        int modifiers = resolveModifiers(member.modifiers);
        String type   = member.type.resolvePath(this);
        String name   = member.name.resolvePath(this);
        maker.Declare(name, type, modifiers);
    }
    
    public void resolveDeclaration(AstDeclareMethod method)
    {
        int modifiers = method.modifiers.resolveModifiers(this);
        String type   = method.type.resolvePath(this);
        String name   = method.name.resolvePath(this);
        maker.Method(name, type, modifiers);
        resolveDeclaration(method.parameters);
        if (method.methodBody != null)
        {
            maker.Begin();
            resolveStatement(method.methodBody);
            maker.End();
        }
        else
            maker.Forward();
    }
    
//    public void resolveStatement(AstDeclareVariable declare)
//    {
//        resolveDeclaration(declare);
//    }
    
    public void resolveStatement(AstStructureList list)
    {
        for (AstStructure item : list.getList())
        {
            item.resolveStatement(this);
        }
    }
    
    public void resolveStatement(AstStatementEval statement)
    {
        Type type = statement.expression.resolveType(this);
        maker.Eval(type);
    }

    public void resolveStatement(AstStatementReturn statement)
    {
        if (statement.expression != null)
        {
            Type type = statement.expression.resolveType(this);
            maker.Return(type);
        }
        else
        {
            maker.Return();
        }
    }

    public void resolveStatement(AstStatementIf statement)
    {
        Type type = statement.condition.resolveType(this);
        maker.If(type);
        statement.thenCode.resolveStatement(this);
        if (statement.elseCode != null)
        {
            maker.Else();
            statement.elseCode.resolveStatement(this);
        }
        maker.EndIf();
    }

    public void resolveStatement(AstStatementWhile statement)
    {
        maker.Loop();
        Type cond = statement.condition.resolveType(this);
        maker.While(cond);
        statement.loopCode.resolveStatement(this);
        maker.EndLoop();
        
    }

}
