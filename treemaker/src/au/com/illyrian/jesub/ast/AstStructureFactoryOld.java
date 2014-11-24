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

import java.util.Stack;

import au.com.illyrian.classmaker.ast.AssignmentOperator;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.BinaryOperator;
import au.com.illyrian.classmaker.ast.DecrementOperator;
import au.com.illyrian.classmaker.ast.DotOperator;
import au.com.illyrian.classmaker.ast.ResolvePath;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.ast.TerminalNumber;

public class AstStructureFactoryOld
{
    AstDeclareModule declareModule = new AstDeclareModule();

    // Work in progress
    AstDeclareClass declareClass = null;
    AstDeclareMethod declareMethod = null;

    // Temporary structures
    private AstModifiers modifierList = null;
    private Stack<AstStructure> statementStack = new Stack<AstStructure>();

    public AstStructureFactoryOld()
    {
    }
    
    public AstDeclareModule getModule()
    {
        return declareModule;
    }
    
    public void Package(ResolvePath packageName)
    {
        declareModule.setPackageName(packageName);
    }
    
    public void Import(ResolvePath className)
    {
        declareModule.addImportsList(className);
    }
    
    public void Modifier(String modifier)
    {
        modifierList = new AstModifiers(modifier, modifierList);
    }
    
    public AstModifiers popModifiers()
    {
        AstModifiers temp = modifierList;
        modifierList = null;
        return temp;
    }
    
    public AstDeclareClass ClassName(TerminalName name)
    {
        return ClassName(popModifiers(), name);
    }
    
    public AstDeclareClass ClassName(AstModifiers modifiers, TerminalName name)
    {
        assert(declareClass == null);
        declareClass = new AstDeclareClass(modifiers, name);
        declareModule.setDeclareClass(declareClass);
        return declareClass;
    }
    
    public void Extends(ResolvePath classPath)
    {
        assert(declareClass != null);
        declareClass.setExtends(classPath);
    }
    
    public void Implements(ResolvePath className)
    {
        assert(declareClass != null);
        declareClass.addImplements(className);
    }
    
    public void Declare(ResolvePath type, TerminalName name)
    {
        Declare(popModifiers(), type, name);
    }
    
    public void Declare(AstModifiers modifiers, ResolvePath type, TerminalName name)
    {
        assert(declareMethod != null);
        AstDeclareVariable variable = new AstDeclareVariable(modifiers, type, name);
        if (declareMethod == null)
            declareClass.addMember(variable);
        else if (declareMethod.getMethodBody() == null)
            declareMethod.addParameter(variable);
        else // Local variable
            declareMethod.addStatement(variable);
    }
    
    public void Method(ResolvePath type, TerminalName name)
    {
        Method(popModifiers(), type, name);
    }
    
    public void Method(AstModifiers modifiers, ResolvePath type, TerminalName name)
    {
        assert(declareMethod == null);
        declareMethod = new AstDeclareMethod(modifiers, type, name);
    }
    
    public void Begin()
    {
        assert(declareMethod != null);
    	AstStructureList code = new AstStructureList();
    	statementStack.push(code);
        declareMethod.setMethodBody(code);
    }
    
    public void End()
    {
        assert(declareMethod != null);
        assert(declareClass != null);
        declareClass.addMember(declareMethod);
        declareMethod = null;
    }
    
    public void Reserved(String reserved)
    {
    	AstStatementReserved stmt = AstStatementReserved.lookup(reserved);
    	addStatement(stmt);
    }

    public void Return(AstExpression value)
    {
        AstStatementReturn stmt = new AstStatementReturn(value);
        addStatement(stmt);
    }
    
    public void Eval(AstExpression value)
    {
        AstStatementEval stmt = new AstStatementEval(value);
        addStatement(stmt);
    }
    
    private AstStructureList castList(AstStructure stmt)
    {
    	if (stmt instanceof AstStructureList)
    		return (AstStructureList)stmt;
    	throw new IllegalStateException("Expected class AstStructureList on stack but was " + stmt.getClass().getSimpleName());
    }
    
    private void addStatement(AstStructure stmt)
    {
    	AstStructureList code = castList(statementStack.peek());
    	code.add(stmt);
    }
    
    private AstStatementIf castIf(AstStructure stmt)
    {
    	if (stmt instanceof AstStatementIf)
    		return (AstStatementIf)stmt;
    	throw new IllegalStateException("Expected class AstStatementIf on stack but was " + stmt.getClass().getSimpleName());
    }
    
    public void If(AstExpression condition)
    {
    	AstStatementIf stmt = new AstStatementIf();
    	stmt.setCondition(condition);
    	statementStack.push(stmt);
    	AstStructureList thenCode = new AstStructureList();
    	statementStack.push(thenCode);
    }
    
    public void Else()
    {
    	AstStructureList thenCode = castList(statementStack.pop());
    	AstStatementIf ifStmt = castIf(statementStack.peek());
    	ifStmt.setThenCode(thenCode);
    	AstStructureList elseCode = new AstStructureList();
    	statementStack.push(elseCode);
    }
    
    public void EndIf()
    {
    	AstStructureList code = castList(statementStack.pop());
    	AstStatementIf ifStmt = castIf(statementStack.peek());
    	if (ifStmt.getThenCode() == null)
    		ifStmt.setThenCode(code);
    	else
    		ifStmt.setElseCode(code);
    	statementStack.pop();
        declareMethod.addStatement(ifStmt);
    }
    
    private AstStatementWhile castWhile(AstStructure stmt)
    {
    	if (stmt instanceof AstStatementWhile)
    		return (AstStatementWhile)stmt;
    	throw new IllegalStateException("Expected class AstStatementWhile on stack but was " + stmt.getClass().getSimpleName());
    }
    
    public void While(AstExpression condition)
    {
    	AstStatementWhile stmt = new AstStatementWhile();
    	stmt.setCondition(condition);
    	statementStack.push(stmt);
    	AstStructureList whileCode = new AstStructureList();
    	statementStack.push(whileCode);    	
    }
    
    public void EndWhile()
    {
    	AstStructureList code = castList(statementStack.pop());
    	AstStatementWhile whileStmt = castWhile(statementStack.pop());
    	whileStmt.setLoopCode(code);
    	// FIXME - add to code block
        declareMethod.addStatement(whileStmt);
    }
    
    public AssignmentOperator Assign(AstExpression left, AstExpression right)
    {
        return new AssignmentOperator(left, right);
    }
    
    public DotOperator Dot(AstExpression left, TerminalName right)
    {
        return new DotOperator(left, right);
    }
    
    public BinaryOperator Mult(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.MULT, left, right);
    }
    
    public BinaryOperator Div(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.DIV, left, right);
    }
    
    public BinaryOperator NE(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.NE, left, right);
    }
    
    public BinaryOperator GT(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.GT, left, right);
    }
    
    public DecrementOperator Dec(AstExpression expr)
    {
        return new DecrementOperator(expr);
    }
    
    public TerminalNumber Literal(int value)
    {
        return new TerminalNumber(value);
    }

    public TerminalName Name(String name)
    {
        return new TerminalName(name);
    }
}
