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

package au.com.illyrian.classmaker.ast;

import java.util.Vector;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ExpressionIfc;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.Type;

public class AstExpressionVisitor implements SourceLine
{
    ExpressionIfc maker = null;
    
    private static final ClassMakerException [] ERRORS_PROTO = new ClassMakerException[0];
    private Vector <ClassMakerException> errorList = new Vector <ClassMakerException>();
    protected SourceLine sourceLine = null;
    
    public AstExpressionVisitor()
    {
    }
    
    public AstExpressionVisitor(ExpressionIfc classMaker)
    {
        setMaker(classMaker);
    }
    
    public ExpressionIfc getMaker()
    {
        return maker;
    }

    public void setMaker(ExpressionIfc maker)
    {
        this.maker = maker;
    }

    public String getFilename()
    {
        return (sourceLine != null) ? sourceLine.getFilename() : null;
    }

    public int getLineNumber()
    {
        return (sourceLine != null) ? sourceLine.getLineNumber() : 0;
    }
    
    public boolean hasErrors()
    {
        return !errorList.isEmpty();
    }

    public ClassMakerException [] getErrors()
    {
        ClassMakerException [] list = errorList.toArray(ERRORS_PROTO);
        return list;
    }
    
    public void addError(ClassMakerException ex)
    {
        errorList.add(ex);
    }
    
    // Type resolveType(...)
    public Type resolveType(AstExpression tree)
    {
        throw new IllegalStateException("No special case for ExpressionTree type: " 
                + tree.getClass().getSimpleName());
    }
    
    public Type resolveType(TerminalName term)
    {
        sourceLine = term;
        if ("this".equals(term.getName()))
            return maker.This();
        else if ("null".equals(term.getName()))
            return maker.Null();
        else if ("super".equals(term.getName()))
            return maker.Super();
        else
        {
            MakerField field = resolveMakerField(term);
            // FIXME - Get should do this and handle the this pointer.
            if (field == null || field.isLocal()) 
                return maker.Get(term.getName());
            else 
                return maker.Get(field.getClassType(), term.getName());
        }
    }
    
    public Type resolveType(TerminalBoolean term)
    {
        sourceLine = term;
        return maker.Literal(term.booleanValue());
    }
    
    public Type resolveType(TerminalNumber term)
    {
        sourceLine = term;
        switch (term.getSize())
        {
        case TerminalNumber.LONG:
            return maker.Literal(term.longValue());
        case TerminalNumber.INT:
            return maker.Literal(term.intValue());
        case TerminalNumber.SHORT:
            return maker.Literal(term.shortValue());
        case TerminalNumber.BYTE:
            return maker.Literal(term.byteValue());
        case TerminalNumber.CHAR:
            return maker.Literal(term.charValue());
        default:
            throw new IllegalStateException("Don't know what to do with " + term);
        }
    }
    
    public Type resolveType(TerminalDecimal term)
    {
        sourceLine = term;
        switch (term.getSize())
        {
        case TerminalDecimal.DOUBLE:
            return maker.Literal(term.doubleValue());
        case TerminalDecimal.FLOAT:
            return maker.Literal(term.floatValue());
        default:
            throw new IllegalStateException("Don't know what to do with " + term);
        }
    }
    
    public Type resolveType(TerminalString term)
    {
        sourceLine = term;
        return maker.Literal(term.stringValue());
    }
    
    public Type resolveType(BinaryOperator tree)
    {
        Type leftType = tree.getLeftOperand().resolveType(this);
        Type rightType = tree.getRightOperand().resolveType(this);
        sourceLine = tree;
        switch (tree.getOperatorType())
        {
        case BinaryOperator.MULT:
            return maker.Mult(leftType, rightType);
        case BinaryOperator.DIV:
            return maker.Div(leftType, rightType);
        case BinaryOperator.REM:
            return maker.Rem(leftType, rightType);
        case BinaryOperator.ADD:
            return maker.Add(leftType, rightType);
        case BinaryOperator.SUBT:
            return maker.Subt(leftType, rightType);
        case BinaryOperator.SHL:
            return maker.SHL(leftType, rightType);
        case BinaryOperator.SHR:
            return maker.SHR(leftType, rightType);
        case BinaryOperator.USHR:
            return maker.USHR(leftType, rightType);
        case BinaryOperator.LT:
            return maker.LT(leftType, rightType);
        case BinaryOperator.GT:
            return maker.GT(leftType, rightType);
        case BinaryOperator.LE:
            return maker.LE(leftType, rightType);
        case BinaryOperator.GE:
            return maker.GE(leftType, rightType);
        case BinaryOperator.EQ:
            return maker.EQ(leftType, rightType);
        case BinaryOperator.NE:
            return maker.NE(leftType, rightType);
        case BinaryOperator.AND:
            return maker.And(leftType, rightType);
        case BinaryOperator.XOR:
            return maker.Xor(leftType, rightType);
        case BinaryOperator.OR :
            return maker.Or(leftType, rightType);
        default:
            throw new IllegalStateException("No special case for BinaryOperator type: " 
                    + tree.getOperatorType());
        }
    }
    
    public Type resolveType(UnaryOperator tree)
    {
        Type type = tree.getOperand().resolveType(this);
        sourceLine = tree;
        switch (tree.getOperatorType())
        {
        case UnaryOperator.NEG:
            return maker.Neg(type);
        case UnaryOperator.INV:
            return maker.Inv(type);
        case UnaryOperator.NOT:
            return maker.Not(type);
        default:
            throw new IllegalStateException("No special case for UnaryOperator type: " 
                    + tree.getOperatorType());
        }
    }
    
    public Type resolveType(InstanceOfOperator tree)
    {
        Type type = tree.getLeftOperand().resolveType(this);
        String typeName = tree.getRightOperand().resolvePath(this);
        sourceLine = tree;
        return maker.InstanceOf(type, typeName);
    }
    
    public Type resolveType(CastOperator tree)
    {
        String typeName = tree.getLeftOperand().resolvePath(this);
        Type type = tree.getRightOperand().resolveType(this);
        sourceLine = tree;
        return maker.Cast(type, typeName);
    }
    
    public Type resolveType(ArrayIndex tree)
    {
    	Type reference = tree.arrayOperand.resolveType(this);
    	Type index = tree.indexOperand.resolveType(this);
        sourceLine = tree;
        return maker.GetAt(reference, index);
    }
    
    public Type resolveType(NewOperator tree)
    {
        DeclaredType declared = tree.getTypeName().resolveDeclaredType(this);
        CallStack stack = null;
        if (tree.getParams() != null)
            stack = tree.getParams().resolveCallStack(this);
        sourceLine = tree;
        return maker.New(declared).Init(stack);
    }
    
    public Type resolveType(NewArrayOperator tree)
    {
        DeclaredType declared = tree.getTypeName().resolveDeclaredType(this);
        Type indexType = tree.getDimensions().resolveType(this);
        sourceLine = tree;
        return maker.NewArray(declared, indexType);
    }
    
    public Type resolveType(AssignmentOperator tree)
    {
        if (tree.leftOperand instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.leftOperand;
            Type arrayType = array.arrayOperand.resolveType(this);
            Type indexType = array.indexOperand.resolveType(this);
            Type valueType = tree.rightOperand.resolveType(this);
            sourceLine = tree;
            return maker.AssignAt(arrayType, indexType, valueType);
        } else {
            MakerField field = tree.leftOperand.resolveMakerField(this);
            Type type = tree.rightOperand.resolveType(this);
            sourceLine = tree;
            if (field == null)
                throw new IllegalStateException("MakerField is null: " + tree.leftOperand);
            if (field.isLocal())
                return maker.Assign(field.getName(), type);
            else if (field.isStatic())
                return maker.Assign(field.getClassType().getName(), field.getName(), type);
            else
                return maker.Assign(field.getClassType(), field.getName(), type);
        }
    }
    
    public Type resolveType(DotOperator tree)
    {
        Type reference = tree.leftOperand.resolveTypeOrNull(this);
        if (reference != null)
        {
            String name = tree.rightOperand.resolvePath(this);
            if (name != null) {
                sourceLine = tree;
                if (ClassMaker.isArray(reference) && "length".equals(name))
                    return maker.Length(reference);
                else
                    return maker.Get(reference, name);
            } else {
                CallStack callStack = tree.rightOperand.resolveCallStack(this);
                sourceLine = tree;
                return maker.Call(reference, callStack.getMethodName(), callStack);
            }
        } else {
            String path = tree.leftOperand.resolvePath(this);
            String name = tree.rightOperand.resolvePath(this);
            if (name != null) {
                sourceLine = tree;
                return maker.Get(path, name);
            } else {
                CallStack callStack = tree.rightOperand.resolveCallStack(this);
                sourceLine = tree;
                return maker.Call(path, callStack.getMethodName(), callStack);
            }
        }
    }
    
    public Type resolveType(IncrementOperator tree)
    {
    	if (tree.operand instanceof ArrayIndex)
    	{
    	    ArrayIndex array = (ArrayIndex)tree.operand;
    	    Type arrayType = array.arrayOperand.resolveType(this);
    	    Type indexType = array.indexOperand.resolveType(this);
            sourceLine = tree;
    	    return maker.IncAt(arrayType, indexType);
    	}
    	else
    	{
            MakerField field = tree.operand.resolveMakerField(this);
            sourceLine = tree;
            if (field.getClassType() == null)
                return maker.Inc(field.getName());
            else if (field.isStatic())
                return maker.Inc(field.getClassType().getName(), field.getName());
            else
                return maker.Inc(field.getClassType(), field.getName());
	}
    }

    public Type resolveType(DecrementOperator tree)
    {
        if (tree.operand instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.operand;
            Type arrayType = array.arrayOperand.resolveType(this);
            Type indexType = array.indexOperand.resolveType(this);
            sourceLine = tree;
            return maker.DecAt(arrayType, indexType);
        } else {
            MakerField field = tree.operand.resolveMakerField(this);
            sourceLine = tree;
            if (field.getClassType() == null)
                return maker.Dec(field.getName());
            else if (field.isStatic())
                return maker.Dec(field.getClassType().getName(), field.getName());
            else
                return maker.Dec(field.getClassType(), field.getName());
        }
    }
    
    public Type resolveType(PostIncrementOperator tree)
    {
        if (tree.operand instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.operand;
            Type arrayType = array.arrayOperand.resolveType(this);
            Type indexType = array.indexOperand.resolveType(this);
            sourceLine = tree;
            return maker.PostIncAt(arrayType, indexType);
        } else {
            MakerField field = tree.operand.resolveMakerField(this);
            sourceLine = tree;
            if (field.getClassType() == null)
                return maker.PostInc(field.getName());
            else if (field.isStatic())
                return maker.PostInc(field.getClassType().getName(), field.getName());
            else
                return maker.PostInc(field.getClassType(), field.getName());
        }
    }
    
    public Type resolveType(PostDecrementOperator tree)
    {
        if (tree.operand instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.operand;
            Type arrayType = array.arrayOperand.resolveType(this);
            Type indexType = array.indexOperand.resolveType(this);
            sourceLine = tree;
            return maker.PostDecAt(arrayType, indexType);
        } else {
            MakerField field = tree.operand.resolveMakerField(this);
            sourceLine = tree;
            if (field.getClassType() == null)
                return maker.PostDec(field.getName());
            else if (field.isStatic())
                return maker.PostDec(field.getClassType().getName(), field.getName());
            else
                return maker.PostDec(field.getClassType(), field.getName());
        }
    }
    
    public Type resolveType(AndThenOperator tree)
    {
        ClassMaker.AndOrExpression expr = tree.leftOperand.resolveAndThen(this);
        Type cond = tree.rightOperand.resolveType(this);
        sourceLine = tree;
        return maker.Logic(expr, cond);
    }
    
    public Type resolveType(OrElseOperator tree)
    {
        ClassMaker.AndOrExpression expr = tree.leftOperand.resolveOrElse(this);
        Type cond = tree.rightOperand.resolveType(this);
        sourceLine = tree;
        return maker.Logic(expr, cond);
    }
    
    public Type resolveType(MethodCall call)
    {
        Type reference = maker.This();
        CallStack callStack = resolveCallStack(call);
        sourceLine = call;
        return maker.Call(reference, callStack.getMethodName(), callStack);
    }

    public Type resolveType(AstStatementReserved term)
    {
        sourceLine = term;
        if ("this".equals(term.reservedWord))
            return maker.This();
        else if ("super".equals(term.reservedWord))
            return maker.Super();
        else if ("null".equals(term.reservedWord))
            return maker.Null();
        else
            throw new IllegalStateException("Unknown reserved word: " + term.reservedWord);
    }
    
    // resolveTypeOrNull(...)
    public Type resolveTypeOrNull(TerminalName term)
    {
        sourceLine = term;
        if ("this".equals(term.getName()))
            return maker.This();
        else if ("null".equals(term.getName()))
            return maker.Null();
        else if ("super".equals(term.getName()))
            return maker.Super();
        else {
            MakerField field = resolveMakerField(term);
            if (field == null)
                return null;
            else if (field.isLocal())
                return maker.Get(term.getName());
            else
                return maker.Get(field.getClassType(), term.getName());
        }
    }
    
    public Type resolveTypeOrNull(DotOperator tree)
    {
        Type reference = tree.leftOperand.resolveTypeOrNull(this);
        if (reference != null)
        {
            String name = tree.rightOperand.resolvePath(this);
            if (name != null)
            {
                sourceLine = tree;
                return maker.Get(reference, name);
            } else {
                CallStack callStack = tree.rightOperand.resolveCallStack(this);
                sourceLine = tree;
                return maker.Call(reference, callStack.getMethodName(), callStack);
            }
        } else {
            String path = tree.leftOperand.resolvePath(this);
            DeclaredType declared = maker.findDeclaredType(path);
            if (declared != null)
            {
                String name = tree.rightOperand.resolvePath(this);
                sourceLine = tree;
                return maker.Get(declared.getName(), name);
            }
        }
        return null;
    }
/*    
    public Type resolveType(DotOperator tree)
    {
        Type reference = tree.leftOperand.resolveTypeOrNull(this);
        if (reference != null)
        {
            String name = tree.rightOperand.resolvePath(this);
            if (name != null) {
                sourceLine = tree;
                if (ClassMaker.isArray(reference) && "length".equals(name))
                    return maker.Length(reference);
                else
                    return maker.Get(reference, name);
            } else {
                CallStack callStack = tree.rightOperand.resolveCallStack(this);
                sourceLine = tree;
                return maker.Call(reference, callStack.getMethodName(), callStack);
            }
        } else {
            String path = tree.leftOperand.resolvePath(this);
            String name = tree.rightOperand.resolvePath(this);
            if (name != null) {
                sourceLine = tree;
                return maker.Get(path, name);
            } else {
                CallStack callStack = tree.rightOperand.resolveCallStack(this);
                sourceLine = tree;
                return maker.Call(path, callStack.getMethodName(), callStack);
            }
        }
    }
*/
    // resolveMakerField(...)
    public MakerField resolveMakerField(TerminalName term)
    {
        MakerField field = maker.findField(term.getName());
        if (field != null)
        {
            if (!field.isLocal() && !field.isStatic())
            {
                sourceLine = term;
                maker.This();
            }
        }
        return field;
    }
    
    public MakerField resolveMakerField(DotOperator tree)
    {
        Type type = tree.leftOperand.resolveTypeOrNull(this);
        if (type != null)
        {
            String name = tree.rightOperand.resolvePath(this);
            sourceLine = tree;
            return maker.Find(type, name);
        } else {
            String path = tree.leftOperand.resolvePath(this);
            String name = tree.rightOperand.resolvePath(this);
            sourceLine = tree;
            return maker.Find(path, name);
        }
    }
    
    // resolvePath(...)
    public String resolvePath(TerminalName term)
    {
        sourceLine = term;
        return term.getName();
    }
    
    public String resolvePath(DotOperator term)
    {
    	String left = term.leftOperand.resolvePath(this);
    	String right = term.rightOperand.resolvePath(this);
        return left + "." + right;
    }
    
    public DeclaredType resolveDeclaredType(TerminalName term)
    {
        sourceLine = term;
        return maker.findDeclaredType(term.getName());
    }
    
    public DeclaredType resolveDeclaredType(DotOperator term)
    {
    	String path = resolvePath(term);
        sourceLine = term;
        return maker.findDeclaredType(path);
    }

    public DeclaredType resolveDeclaredType(ArrayOf arrayOf) 
    {
        DeclaredType type = arrayOf.getType().resolveDeclaredType(this);
        sourceLine = arrayOf;
        return maker.ArrayOf(type);
    }

	// resolveCallStack(...)
    public CallStack resolveCallStack(MethodCall call)
    {
        String methodName = call.getMethodName().resolvePath(this);
        CallStack actualParameters = null;
        sourceLine = call;
        if (call.getParams() == null)
            actualParameters = maker.Push();
        else
            actualParameters = call.getParams().resolveCallStack(this);
        actualParameters.setMethodName(methodName);
        return actualParameters;
    }
    
    //             tree
    //            /    \
    //        tree    3rd param
    //       /    \
    //   tree    2nd param
    //       \
    //      1st param
    public CallStack resolveCallStack(CommaOperator tree)
    {
        if (tree.leftExpression == null)
        {
            Type reference = tree.rightExpression.resolveType(this);
            sourceLine = tree;
            return maker.Push(reference);
        } else {
            CallStack actualParameters = tree.leftExpression.resolveCallStack(this);
            Type reference = tree.rightExpression.resolveType(this);
            sourceLine = tree;
            return actualParameters.Push(reference);
        }
    }
    
    public CallStack resolveCallStack(AstExpression expr)
    {
        Type reference = expr.resolveType(this);
        sourceLine = expr;
        return maker.Push(reference);
    }
    
    public AndOrExpression resolveAndThen(AndThenOperator tree)
    {
        if (tree.leftOperand == null) {
            Type cond = tree.rightOperand.resolveType(this);
            sourceLine = tree;
            return maker.AndThen(cond);
        } else {
            AndOrExpression expr = null;
            if (tree.leftOperand instanceof AndThenOperator)
                expr = tree.leftOperand.resolveAndThen(this);
            else {
                Type cond = tree.leftOperand.resolveType(this);
                sourceLine = tree;
                expr = maker.AndThen(cond);
            }
            Type cond = tree.rightOperand.resolveType(this);
            sourceLine = tree;
            return maker.AndThen(expr, cond);
        }
    }
    
    public AndOrExpression resolveOrElse(OrElseOperator tree)
    {
        if (tree.leftOperand == null) {
            Type cond = tree.rightOperand.resolveType(this);
            sourceLine = tree;
            return maker.OrElse(cond);
        } else {
            AndOrExpression expr = null;
            if (tree.leftOperand instanceof OrElseOperator)
                expr = tree.leftOperand.resolveOrElse(this);
            else {
                Type cond = tree.leftOperand.resolveType(this);
                sourceLine = tree;
                expr = maker.OrElse(cond);
            }
            Type cond = tree.rightOperand.resolveType(this);
            sourceLine = tree;
            return maker.OrElse(expr, cond);
        }
    }
    
    public AndOrExpression resolveOrElse(AstExpression tree)
    {
        Type cond = tree.resolveType(this);    	
        sourceLine = tree;
        return maker.OrElse(cond);
    }
    
    public AndOrExpression resolveAndThen(AstExpression tree)
    {
        Type cond = tree.resolveType(this);    	
        sourceLine = tree;
        return maker.AndThen(cond);
    }
    
    public String toString()
    {
        return getClass().getCanonicalName();
    }
}
