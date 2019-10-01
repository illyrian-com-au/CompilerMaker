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

import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.ForStep;
import au.com.illyrian.classmaker.ForWhile;
import au.com.illyrian.classmaker.Labelled;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionLink;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.ast.AstStatementReserved;
import au.com.illyrian.classmaker.ast.ResolvePath;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class AstStructureVisitor extends AstExpressionVisitor
{
    public AstStructureVisitor()
    {
    }

    public AstStructureVisitor(ClassMakerIfc classMaker)
    {
        super(classMaker);
    }

    public void setSource(AstStructure tree) {
        setLineNumber(tree.getLineNumber());
    }

    public void handleError(ClassMakerException ex) {
        throw ex;
    }

    public void resolveDeclaration(AstModule unit)
    {
        if (unit.getPackage() != null) {
            unit.getPackage().resolveDeclaration(this);
        }
        if (unit.getImportsList() != null) {
            unit.getImportsList().resolveDeclaration(this);
        }
        if (unit.getClassList() != null)
            unit.getClassList().resolveDeclaration(this);
    }

    public void resolveDeclaration(AstPackage pack)
    {
        try {
            String packageName = pack.getExpression().resolvePath(this);
            getMaker().setPackageName(packageName);
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveDeclaration(AstImport unit)
    {
        try {
            String name = unit.getExpression().resolvePath(this);
            getMaker().Import(name);
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveDeclaration(AstClass unit)
    {
        try {
            int modifiers = resolveModifiers(unit.getModifiers());
            getMaker().setClassModifiers(modifiers);
            String className = unit.getClassName().resolvePath(this);
            getMaker().setSimpleClassName(className);
            resolveExtends(unit.getExtends());
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
        if (unit.getImplementsList() != null)
            unit.getImplementsList().resolveImplements(this);
        if (unit.getMembers() != null)
            unit.getMembers().resolveDeclaration(this);
    }

    public void resolveDeclaration(AstInterface unit)
    {
        try {
            int modifiers = resolveModifiers(unit.getModifiers());
            getMaker().setClassModifiers(modifiers);
            String className = unit.getClassName().resolvePath(this);
            getMaker().setSimpleClassName(className);
            resolveExtends(unit.getExtends());
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
        if (unit.getMembers() != null)
            unit.getMembers().resolveDeclaration(this);
    }

    public void resolveStatement(AstPackage declare)
    {
        String packageName = declare.getExpression().resolvePath(this);
        getMaker().setPackageName(packageName);
    }

    public void resolveStatement(AstImport declare)
    {
        String importName = declare.getExpression().resolvePath(this);
        getMaker().Import(importName);
    }

    public void resolveExtends(ResolvePath className)
    {
        try {
            if (className != null) {
                String baseClass = className.resolvePath(this);
                getMaker().Extends(baseClass);
            }
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveImport(AstExpressionLink link)
    {
        link.left.resolveImport(this);
        link.right.resolveImport(this);
    }

    public void resolveImport(AstExpression className)
    {
        try {
            if (className != null) {
                String name = className.resolvePath(this);
                getMaker().Import(name);
            }
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveImplements(AstExpressionLink link)
    {
        link.left.resolveImplements(this);
        link.right.resolveImplements(this);
    }

    public void resolveImplements(AstExpression className)
    {
        try {
            if (className != null) {
                String name = className.resolvePath(this);
                getMaker().Implements(name);
            }
        } catch (ClassMakerException ex) {
            handleError(ex);
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
            handleError(ex);
        }
        return modifierBits;
    }

    public void resolveDeclaration(AstStructureLink link)
    {
        link.left.resolveDeclaration(this);
        link.right.resolveDeclaration(this);
    }

    public void resolveDeclaration(AstDeclareVariable member)
    {
        setSource(member);
        try {
            int modifiers = resolveModifiers(member.getModifiers());
            Type type = member.getType().resolveType(this);
            if (type == null)
                throw new IllegalArgumentException("Cannot find declared type:" + member.getType());
            String name = member.getName().resolvePath(this);
            getMaker().Declare(name, type.getName(), modifiers);
        } catch (ClassMakerException ex) {
            handleError(ex);
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
                if (getMaker().getPass() != ClassMakerConstants.FIRST_PASS)
                    method.getMethodBody().resolveStatement(this);
                getMaker().End();
            } else
                getMaker().Forward();
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStructureLink link)
    {
        link.left.resolveStatement(this);
        link.right.resolveStatement(this);
    }

    public void resolveStatement(AstStatementReserved statement)
    {
        setSource(statement);
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
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementEval statement)
    {
        try {
            Value type = statement.getExpression().resolveValue(this);
            getMaker().Eval(type);
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementReturn statement)
    {
        setSource(statement);
        try {
            if (statement.getExpression() != null) {
                Value type = statement.getExpression().resolveValue(this);
                getMaker().Return(type);
            } else {
                getMaker().Return();
            }
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementIf statement)
    {
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            Value type = statement.getCondition().resolveValue(this);
            getMaker().If(type).setLabel(label);
            statement.getThenCode().resolveStatement(this);
            if (statement.getElseCode() != null) {
                getMaker().Else();
                statement.getElseCode().resolveStatement(this);
            }
            getMaker().EndIf();
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementWhile statement)
    {
        setSource(statement);
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            getMaker().Loop().setLabel(label);
            Value cond = statement.getCondition().resolveValue(this);
            getMaker().While(cond);
            statement.getCode().resolveStatement(this);
            getMaker().EndLoop();
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementFor statement)
    {
        setSource(statement);
        try {
            Value init = (statement.getInitialise() == null) ? null : statement.getInitialise().resolveValue(this);
            ForWhile step1 = getMaker().For(init);
            Value cond = (statement.getCondition() == null) ? null : statement.getCondition().resolveValue(this);
            ForStep step2 = step1.While(cond);
            Value dec = (statement.getIncrement() == null) ? null : statement.getIncrement().resolveValue(this);
            Labelled step3 = step2.Step(dec);
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            step3.setLabel(label);

            statement.getCode().resolveStatement(this);
            getMaker().EndLoop();
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementBreak statement)
    {
        setSource(statement);
        try {
            if (statement.getLabel() == null)
                getMaker().Break();
            else
                getMaker().Break(statement.getLabel().getName());
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementContinue statement)
    {
        setSource(statement);
        try {
            if (statement.getLabel() == null)
                getMaker().Continue();
            else
                getMaker().Continue(statement.getLabel().getName());
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementSwitch statement)
    {
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            Value cond = statement.getExpression().resolveValue(this);
            getMaker().Switch(cond).setLabel(label);
            statement.getCode().resolveStatement(this);
            getMaker().EndSwitch();
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementCase statement)
    {
        setSource(statement);
        try {
            getMaker().Case(statement.getValue().intValue());
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementDefault statement)
    {
        setSource(statement);
        try {
            getMaker().Default();
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementCompound statement)
    {
        setSource(statement);
        try {
            String label = (statement.getLabel() == null) ? null : statement.getLabel().getName();
            getMaker().Begin().setLabel(label);
            if (statement.getCode() != null) {
                statement.getCode().resolveStatement(this);
            }
            getMaker().End();
        } catch (ClassMakerException ex) {
            handleError(ex);
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
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementCatch catchClause)
    {
        setSource(catchClause);
        try {
            AstDeclareVariable exception = catchClause.getException();
            String exceptionName = exception.getType().resolvePath(this);
            String name = exception.getName().getName();
            getMaker().Catch(exceptionName, name);

            catchClause.getCode().resolveStatement(this);
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public void resolveStatement(AstStatementFinally finallyClause)
    {
        setSource(finallyClause);
        try {
            getMaker().Finally();
            finallyClause.getCode().resolveStatement(this);
        } catch (ClassMakerException ex) {
            handleError(ex);
        }
    }

    public String toString() {
        return getClass().getSimpleName() + '(' + getFilename() + ';' + getLineNumber() + ')';
    }
}
