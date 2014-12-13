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

import au.com.illyrian.classmaker.ast.AssignmentOperator;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.ast.AstExpressionLink;
import au.com.illyrian.classmaker.ast.BinaryOperator;
import au.com.illyrian.classmaker.ast.DecrementOperator;
import au.com.illyrian.classmaker.ast.DotOperator;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.ast.TerminalNumber;

public class AstStructureFactory extends AstExpressionFactory
{
    public AstStructureFactory()
    {
    }
    
    AstDeclareModule Module(AstExpression packageName, AstExpressionLink importsList, AstDeclareClass declaredClass)
    {
    	return new AstDeclareModule(packageName, importsList, declaredClass);
    }
    
    public AstExpressionLink Link(AstExpression left, AstExpression right)
    {
        return new AstExpressionLink(left, right);
    }
    
    public AstStructureLink Link(AstStructure left, AstStructure right)
    {
        return new AstStructureLink(left, right);
    }
    
    public AstModifiers Modifier(TerminalName modifier, AstModifiers next)
    {
        return new AstModifiers(modifier, next);
    }
    
    public AstDeclareClass DeclareClass(AstModifiers modifiers, TerminalName name, AstExpression baseClass, 
    		AstExpressionLink implementsList, AstStructureLink membersList)
    {
    	AstDeclareClass declareClass = new AstDeclareClass(modifiers, name, baseClass, implementsList, membersList);
        return declareClass;
    }
    
    public AstDeclareVariable DeclareVariable(AstModifiers modifiers, AstExpression type, TerminalName name)
    {
        return new AstDeclareVariable(modifiers, type, name);
    }
    
    public AstDeclareMethod Method(AstModifiers modifiers, AstExpression type, TerminalName name, 
    		AstStructureLink params, AstStructureLink code)
    {
    	AstDeclareMethod declareMethod = new AstDeclareMethod(modifiers, type, name, params, code);
    	return declareMethod;
    }
    
    public AstStatementReturn Return(AstExpression value)
    {
        AstStatementReturn stmt = new AstStatementReturn(value);
        return stmt;
    }
    
    public AstStatementEval Eval(AstExpression value)
    {
        AstStatementEval stmt = new AstStatementEval(value);
        return stmt;
    }
    
    public AstStatementIf If(AstExpression condition, AstStructureLink thenStatement, AstStructureLink elseStatement)
    {
    	AstStatementIf stmt = new AstStatementIf(condition, thenStatement, elseStatement);
    	return stmt;
    }
    
    public AstStatementWhile While(AstExpression condition, AstStructureLink bodyStatement)
    {
    	AstStatementWhile stmt = new AstStatementWhile(condition, bodyStatement);
    	return stmt;
    }
    
//    public AssignmentOperator Assign(AstExpression left, AstExpression right)
//    {
//        return new AssignmentOperator(left, right);
//    }
//    
//    public DotOperator Dot(AstExpression left, TerminalName right)
//    {
//        return new DotOperator(left, right);
//    }
//    
//    public BinaryOperator Mult(AstExpression left, AstExpression right)
//    {
//        return new BinaryOperator(BinaryOperator.MULT, left, right);
//    }
//    
//    public BinaryOperator Div(AstExpression left, AstExpression right)
//    {
//        return new BinaryOperator(BinaryOperator.DIV, left, right);
//    }
//    
//    public BinaryOperator NE(AstExpression left, AstExpression right)
//    {
//        return new BinaryOperator(BinaryOperator.NE, left, right);
//    }
//    
//    public BinaryOperator GT(AstExpression left, AstExpression right)
//    {
//        return new BinaryOperator(BinaryOperator.GT, left, right);
//    }
//    
//    public DecrementOperator Dec(AstExpression expr)
//    {
//        return new DecrementOperator(expr);
//    }
//    
//    public TerminalNumber Literal(int value)
//    {
//        return new TerminalNumber(value);
//    }
//
//    public TerminalName Name(String name)
//    {
//        return new TerminalName(name);
//    }
}
