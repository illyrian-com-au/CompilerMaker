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
import au.com.illyrian.classmaker.ClassMaker.ForStep;
import au.com.illyrian.classmaker.ClassMaker.ForWhile;
import au.com.illyrian.classmaker.ClassMaker.Labelled;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionLink;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.ast.AstStatementReserved;
import au.com.illyrian.classmaker.ast.ResolvePath;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.Type;

public class AstStructureVisitor extends AstExpressionVisitor
{
    public AstStructureVisitor()
    {
    }

    public AstStructureVisitor(ClassMakerIfc classMaker)
    {
        super(classMaker);
    }

    public void resolveDeclaration(AstDeclareModule unit)
    {
        try {
            if (unit.getPackageName() != null) {
                String packageName = unit.getPackageName().resolvePath(this);
                getMaker().setPackageName(packageName);
            }
        } catch (ClassMakerException ex) {
            addError(ex);
        }
        if (unit.getImportsList() != null)
            unit.getImportsList().resolveImport(this);
        if (unit.getClassList() != null)
            unit.getClassList().resolveDeclaration(this);
    }

    public void resolveDeclaration(AstDeclareClass unit)
    {
        try {
            int modifiers = resolveModifiers(unit.getModifiers());
            getMaker().setClassModifiers(modifiers);
            String className = unit.getClassName().resolvePath(this);
            getMaker().setSimpleClassName(className);
            resolveExtends(unit.getExtends());
        } catch (ClassMakerException ex) {
            addError(ex);
        }
        if (unit.getImplementsList() != null)
            unit.getImplementsList().resolveImplements(this);
        if (unit.getMembers() != null)
            unit.getMembers().resolveDeclaration(this);
    }

    public void resolveExtends(ResolvePath className)
    {
        try {
            if (className != null) {
                String baseClass = className.resolvePath(this);
                getMaker().Extends(baseClass);
            }
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveImport(AstExpressionLink link)
    {
        if (link != null) {
            if (link.left != null)
                link.left.resolveImport(this);
            if (link.right != null)
                link.right.resolveImport(this);
        }
    }

    public void resolveImport(AstExpression className)
    {
        try {
            if (className != null) {
                String name = className.resolvePath(this);
                getMaker().Import(name);
            }
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveImplements(AstExpressionLink link)
    {
        if (link != null) {
            if (link.left != null)
                link.left.resolveImplements(this);
            if (link.right != null)
                link.right.resolveImplements(this);
        }
    }

    public void resolveImplements(AstExpression className)
    {
        try {
            if (className != null) {
                String name = className.resolvePath(this);
                getMaker().Implements(name);
            }
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public int resolveModifiers(AstModifiers modifiers)
    {
        int modifierBits = 0;
        try {
            if (modifiers != null) {
                String modifierName = modifiers.modifier.getName();
                modifierBits = resolveModifiers(modifiers.next);
                modifierBits = getMaker().addModifier(modifierBits, modifierName);
            }
        } catch (ClassMakerException ex) {
            addError(ex);
        }
        return modifierBits;
    }

    public void resolveDeclaration(AstStructureLink link)
    {
        if (link != null) {
            if (link.left != null)
                link.left.resolveDeclaration(this);
            if (link.right != null)
                link.right.resolveDeclaration(this);
        }
    }

    public void resolveDeclaration(AstDeclareVariable member)
    {
        sourceLine = member;
        try {
            int modifiers = resolveModifiers(member.getModifiers());
            DeclaredType type = member.getType().resolveDeclaredType(this);
            if (type == null)
                throw new IllegalArgumentException("Cannot find declared type:" + member.getType());
            String name = member.getName().resolvePath(this);
            getMaker().Declare(name, type.getName(), modifiers);
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveDeclaration(AstDeclareMethod method)
    {
        try {
            int modifiers = method.getModifiers().resolveModifiers(this);
            String type = method.getType().resolvePath(this);
            String name = method.getName().resolvePath(this);
            getMaker().Method(name, type, modifiers);
            if (method.getParameters() != null)
                method.getParameters().resolveDeclaration(this);
            if (method.getMethodBody() != null) {
                getMaker().Begin();
                // Do not process the body of the method if this is the first of
                // two passes.
                if (getMaker().getPass() != ClassMaker.FIRST_PASS)
                    method.getMethodBody().resolveStatement(this);
                getMaker().End();
            } else
                getMaker().Forward();
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStructureLink link)
    {
        if (link != null) {
            if (link.left != null)
                link.left.resolveStatement(this);
            if (link.right != null)
                link.right.resolveStatement(this);
        }
    }

    public void resolveStatement(AstStatementReserved statement)
    {
        sourceLine = statement;
        try {
            if (statement == AstStatementReserved.THIS)
                getMaker().This();
            else if (statement == AstStatementReserved.SUPER)
                getMaker().Super();
            else if (statement == AstStatementReserved.NULL)
                getMaker().Null();
            else
                throw new IllegalStateException("Unknown reserved word: " + statement);
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementEval statement)
    {
        try {
            Type type = statement.getExpression().resolveType(this);
            getMaker().Eval(type);
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementReturn statement)
    {
        sourceLine = statement;
        try {
            if (statement.getExpression() != null) {
                Type type = statement.getExpression().resolveType(this);
                getMaker().Return(type);
            } else {
                getMaker().Return();
            }
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementIf statement)
    {
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            Type type = statement.getCondition().resolveType(this);
            getMaker().If(type).setLabel(label);
            statement.getThenCode().resolveStatement(this);
            if (statement.getElseCode() != null) {
                getMaker().Else();
                statement.getElseCode().resolveStatement(this);
            }
            getMaker().EndIf();
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementWhile statement)
    {
        sourceLine = statement;
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            getMaker().Loop().setLabel(label);
            Type cond = statement.getCondition().resolveType(this);
            getMaker().While(cond);
            statement.getCode().resolveStatement(this);
            getMaker().EndLoop();
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementFor statement)
    {
        sourceLine = statement;
        try {
            Type init = (statement.getInitialise() == null) ? null : statement.getInitialise().resolveType(this);
            ForWhile step1 = getMaker().For(init);
            Type cond = (statement.getCondition() == null) ? null : statement.getCondition().resolveType(this);
            ForStep step2 = step1.While(cond);
            Type dec = (statement.getIncrement() == null) ? null : statement.getIncrement().resolveType(this);
            Labelled step3 = step2.Step(dec);
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            step3.setLabel(label);

            statement.getCode().resolveStatement(this);
            getMaker().EndLoop();
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementBreak statement)
    {
        sourceLine = statement;
        try {
            if (statement.getLabel() == null)
                getMaker().Break();
            else
                getMaker().Break(statement.getLabel().getName());
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementContinue statement)
    {
        sourceLine = statement;
        try {
            if (statement.getLabel() == null)
                getMaker().Continue();
            else
                getMaker().Continue(statement.getLabel().getName());
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementSwitch statement)
    {
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            Type cond = statement.getExpression().resolveType(this);
            getMaker().Switch(cond).setLabel(label);
            statement.getCode().resolveStatement(this);
            getMaker().EndSwitch();
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementCase statement)
    {
        sourceLine = statement;
        try {
            getMaker().Case(statement.getValue().intValue());
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementDefault statement)
    {
        sourceLine = statement;
        try {
            getMaker().Default();
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementCompound statement)
    {
        sourceLine = statement;
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            getMaker().Begin().setLabel(label);
            statement.getCode().resolveStatement(this);
            getMaker().End();
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementTry statement)
    {
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            getMaker().Try().setLabel(label);
            statement.getCode().resolveStatement(this);
            if (statement.getCatchClause() != null)
                statement.getCatchClause().resolveStatement(this);
            if (statement.getFinallyClause() != null)
                statement.getFinallyClause().resolveStatement(this);
            getMaker().EndTry();
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementCatch catchClause)
    {
        sourceLine = catchClause;
        try {
            AstDeclareVariable exception = catchClause.getException();
            String exceptionName = exception.getType().resolvePath(this);
            String name = exception.getName().getName();
            getMaker().Catch(exceptionName, name);

            catchClause.getCode().resolveStatement(this);
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

    public void resolveStatement(AstStatementFinally finallyClause)
    {
        sourceLine = finallyClause;
        try {
            getMaker().Finally();
            finallyClause.getCode().resolveStatement(this);
        } catch (ClassMakerException ex) {
            addError(ex);
        }
    }

}
