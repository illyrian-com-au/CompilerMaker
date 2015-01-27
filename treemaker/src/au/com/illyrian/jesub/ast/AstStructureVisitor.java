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

import au.com.illyrian.classmaker.ClassMaker.ForStep;
import au.com.illyrian.classmaker.ClassMaker.ForWhile;
import au.com.illyrian.classmaker.ClassMaker.Labelled;
import au.com.illyrian.classmaker.ExpressionIfc;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionLink;
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
    
    public void resolveDeclaration(AstDeclareModule unit)
    {
        String packageName = unit.getPackageName().resolvePath(this);
        maker.setPackageName(packageName);
        resolveImport(unit.getImportsList());
        if (unit.getDeclareClass() != null)
        	unit.getDeclareClass().resolveDeclaration(this);
    }

    public void resolveDeclaration(AstDeclareClass unit)
    {
        int modifiers = resolveModifiers(unit.getModifiers());
        maker.setClassModifiers(modifiers);
        String className = unit.getClassName().resolvePath(this);
        maker.setSimpleClassName(className);
        resolveExtends(unit.getExtends());
        if (unit.getImplementsList() != null)
        	unit.getImplementsList().resolveImplements(this);
        if (unit.getMembers() != null)
        	unit.getMembers().resolveDeclaration(this);
    }

    public void resolveExtends(ResolvePath className)
    {
        if (className != null)
        {
            String baseClass = className.resolvePath(this);
            maker.Extends(baseClass);
        }
    }
    
    public void resolveImport(AstExpressionLink link)
    {
    	if (link != null)
    	{
    		if (link.left != null)	
    			link.left.resolveImport(this);
    		if (link.right != null)	
    			link.right.resolveImport(this);
    	}
    }
    
    public void resolveImport(AstExpression className)
    {
    	if (className != null)
    	{
    		String name = className.resolvePath(this);
    		maker.Import(name);
    	}
    }

    public void resolveImplements(AstExpressionLink link)
    {
    	if (link != null)
    	{
    		if (link.left != null)	
    			link.left.resolveImplements(this);
    		if (link.right != null)	
    			link.right.resolveImplements(this);
    	}
    }
    
    public void resolveImplements(AstExpression className)
    {
    	if (className != null)
    	{
	        String name = className.resolvePath(this);
	        maker.Implements(name);
    	}
    }
    
    public int resolveModifiers(AstModifiers modifiers)
    {
        int modifierBits = 0; 
        if (modifiers != null)
        {
            String modifierName = modifiers.modifier.getName();
            modifierBits = resolveModifiers(modifiers.next);
            modifierBits = maker.addModifier(modifierBits, modifierName);
        }
        return modifierBits;
    }

    public void resolveDeclaration(AstStructureList list)
    {
        for (AstStructure item : list.toArray())
        {
            item.resolveDeclaration(this);
        }
    }

    public void resolveDeclaration(AstStructureLink link)
    {
    	if (link != null)
    	{
	    	if (link.left != null)
	    		link.left.resolveDeclaration(this);
	    	if (link.right != null)
	    		link.right.resolveDeclaration(this);
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
        if (method.parameters != null)
        	method.parameters.resolveDeclaration(this);
        if (method.methodBody != null)
        {
            maker.Begin();
            method.methodBody.resolveStatement(this);
            maker.End();
        }
        else
            maker.Forward();
    }
    
    public void resolveStatement(AstStructureLink link)
    {
    	if (link != null)
    	{
	    	if (link.left != null)
	    		link.left.resolveStatement(this);
	    	if (link.right != null)
	    		link.right.resolveStatement(this);
    	}
    }
    
    public void resolveStatement(AstStructureList list)
    {
        for (AstStructure item : list.toArray())
        {
            item.resolveStatement(this);
        }
    }
    
    public void resolveStatement(AstStatementReserved statement)
    {
    	if (statement == AstStatementReserved.THIS)
    		maker.This();
    	else if (statement == AstStatementReserved.SUPER)
    		maker.Super();
    	else if (statement == AstStatementReserved.NULL)
    		maker.Null();
    	else
    		throw new IllegalStateException("Unknown reserved word: " + statement);
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
        String label = (statement.getLabel() == null) ? null : statement.getLabel().getName(); 
        Type type = statement.condition.resolveType(this);
        maker.If(type).setLabel(label);;
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
        String label = (statement.getLabel() == null) ? null : statement.getLabel().getName(); 
        maker.Loop().setLabel(label);
        Type cond = statement.condition.resolveType(this);
        maker.While(cond);
        statement.getCode().resolveStatement(this);
        maker.EndLoop();
        
    }

    public void resolveStatement(AstStatementFor statement)
    {
    	Type init = (statement.getInitialise() == null) ? null : statement.getInitialise().resolveType(this);
        ForWhile step1 = maker.For(init);
        Type cond = (statement.getCondition() == null) ? null : statement.getCondition().resolveType(this);
        ForStep step2 = step1.While(cond);
    	Type dec = (statement.getIncrement() == null) ? null : statement.getIncrement().resolveType(this);
        Labelled step3 = step2.Step(dec);
        String label = (statement.getLabel() == null) ? null : statement.getLabel().getName(); 
        step3.setLabel(label);

        statement.getCode().resolveStatement(this);
        maker.EndLoop();
        
    }

	public void resolveStatement(AstStatementBreak statement) 
	{
		if (statement.label == null)
			maker.Break();
		else
			maker.Break(statement.label.getName());
	}

	public void resolveStatement(AstStatementContinue statement) 
	{
		if (statement.label == null)
			maker.Continue();
		else
			maker.Continue(statement.label.getName());
	}

	public void resolveStatement(AstStatementSwitch statement) 
	{
        String label = (statement.getLabel() == null) ? null : statement.getLabel().getName(); 
        Type cond = statement.expression.resolveType(this);
		maker.Switch(cond).setLabel(label);
		statement.getCode().resolveStatement(this);
		maker.EndSwitch();
	}

	public void resolveStatement(AstStatementCase statement) 
	{
		maker.Case(statement.value.intValue());
	}

	public void resolveStatement(AstStatementDefault statement) 
	{
		maker.Default();
	}

	public void resolveStatement(AstStatementCompound statement) 
	{
        String label = (statement.getLabel() == null) ? null : statement.getLabel().getName(); 
		maker.Begin().setLabel(label);
		statement.code.resolveStatement(this);
		maker.End();
	}

	public void resolveStatement(AstStatementTry statement) 
	{
        String label = (statement.getLabel() == null) ? null : statement.getLabel().getName(); 
		maker.Try().setLabel(label);
		statement.getCode().resolveStatement(this);
		if (statement.catchClause != null)
			statement.catchClause.resolveStatement(this);
		if (statement.finallyClause != null)
			statement.finallyClause.resolveStatement(this);
		maker.EndTry();
		
	}

	public void resolveStatement(AstStatementCatch catchClause) 
	{
		AstDeclareVariable exception = catchClause.getException();
		String exceptionName = exception.type.resolvePath(this);
		String name = exception.name.getName();
		maker.Catch(exceptionName, name);

		catchClause.getCode().resolveStatement(this);
	}

	public void resolveStatement(AstStatementFinally finallyClause) {
		maker.Finally();
		finallyClause.getCode().resolveStatement(this);
	}

}
