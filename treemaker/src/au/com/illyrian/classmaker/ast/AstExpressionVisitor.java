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

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;
import au.com.illyrian.jesub.ast.AstClass;
import au.com.illyrian.jesub.ast.AstImport;
import au.com.illyrian.jesub.ast.AstModifiers;
import au.com.illyrian.jesub.ast.AstPackage;
import au.com.illyrian.jesub.ast.AstStructure;

public class AstExpressionVisitor implements SourceLine {
    ClassMakerIfc maker = null;

    private String filename;
    private int lineNumber;

    public AstExpressionVisitor() {
    }

    public AstExpressionVisitor(ClassMakerIfc classMaker) {
        setMaker(classMaker);
    }

    public ClassMakerIfc getMaker() {
        return maker;
    }

    public void setMaker(ClassMakerIfc maker) {
        this.maker = maker;
        maker.setSourceLine(this);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    public void setFilename(String sourceFilename) {
        this.filename = sourceFilename;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setSource(AstExpression tree) {
        setLineNumber(tree.getLineNumber());
    }

    // Value resolveValue(...)
    public Value resolveValue(AstExpression tree) {
        throw new IllegalStateException("No special case for ExpressionTree type: " + tree.getClass().getSimpleName());
    }

    public Value resolveValue(TerminalName term) {
        setSource(term);
        if ("this".equals(term.getName()))
            return maker.This();
        else if ("null".equals(term.getName()))
            return maker.Null();
        else if ("super".equals(term.getName()))
            return maker.Super();
        else {
            MakerField field = resolveMakerField(term);
            // FIXME - Get should do this and handle the this pointer.
            if (field == null || field.isLocal())
                return maker.Get(term.getName());
            else
                return maker.Get(field.getClassType().getValue(), term.getName());
        }
    }

    public Value resolveValue(TerminalBoolean term) {
        setSource(term);
        return maker.Literal(term.booleanValue());
    }

    public Value resolveValue(TerminalNumber term) {
        setSource(term);
        switch (term.getSize()) {
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

    public Value resolveValue(TerminalDecimal term) {
        setSource(term);
        switch (term.getSize()) {
        case TerminalDecimal.DOUBLE:
            return maker.Literal(term.doubleValue());
        case TerminalDecimal.FLOAT:
            return maker.Literal(term.floatValue());
        default:
            throw new IllegalStateException("Don't know what to do with " + term);
        }
    }

    public Value resolveValue(TerminalString term) {
        setSource(term);
        return maker.Literal(term.stringValue());
    }

    public Value resolveValue(BinaryOperator tree) {
        Value leftType = tree.getLeftOperand().resolveValue(this);
        Value rightType = tree.getRightOperand().resolveValue(this);
        setSource(tree);
        switch (tree.getOperatorType()) {
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
        case BinaryOperator.OR:
            return maker.Or(leftType, rightType);
        default:
            throw new IllegalStateException("No special case for BinaryOperator type: " + tree.getOperatorType());
        }
    }

    public Value resolveValue(UnaryOperator tree) {
        Value type = tree.getOperand().resolveValue(this);
        setSource(tree);
        switch (tree.getOperatorType()) {
        case UnaryOperator.NEG:
            return maker.Neg(type);
        case UnaryOperator.INV:
            return maker.Inv(type);
        case UnaryOperator.NOT:
            return maker.Not(type);
        default:
            throw new IllegalStateException("No special case for UnaryOperator type: " + tree.getOperatorType());
        }
    }

    public Value resolveValue(InstanceOfOperator tree) {
        Value type = tree.getLeftOperand().resolveValue(this);
        String typeName = tree.getRightOperand().resolvePath(this);
        setSource(tree);
        return maker.InstanceOf(type, typeName);
    }

    public Value resolveValue(CastOperator tree) {
        String typeName = tree.getLeftOperand().resolvePath(this);
        Value type = tree.getRightOperand().resolveValue(this);
        setSource(tree);
        return maker.Cast(type, typeName);
    }

    public Value resolveValue(ArrayIndex tree) {
        Value reference = tree.getArrayOperand().resolveValue(this);
        Value index = tree.getIndexOperand().resolveValue(this);
        setSource(tree);
        return maker.GetAt(reference, index);
    }

    public Value resolveValue(NewOperator tree) {
        return tree.getConstructor().resolveNew(this);
    }

    public Value resolveNew(MethodCall tree) {
        Type type = tree.getName().resolveType(this);
        CallStack stack = null;
        if (tree.getParams() != null)
            stack = tree.getParams().resolveCallStack(this);
        setSource(tree);
        return maker.New(type).Init(stack);
    }

    public Value resolveNew(ArrayIndex tree) {
        Type type = tree.getArrayOperand().resolveType(this);
        ArrayType arrayType = maker.ArrayOf(type);
        Value size = resolveArraySize(tree);
        return maker.NewArray(arrayType, size);
    }

    public Value resolveArraySize(ArrayIndex index) {
        if (index.getIndexOperand() == null) {
            return index.getArrayOperand().resolveArraySize(this);
        } else if (index.getIndexOperand() == null) {
            // FIXME - Should throw ParseException
            throw new IllegalStateException("Array dimension not provided");
        } else {
            return index.getIndexOperand().resolveValue(this);
        }
    }

    public Value resolveValue(NewArrayOperator tree) {
        Type type = tree.getTypeName().resolveType(this);
        Value indexType = tree.getDimensions().resolveValue(this);
        setSource(tree);
        return maker.NewArray(type, indexType);
    }

    public Value resolveValue(AssignmentOperator tree) {
        if (tree.getLeftOperand() instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.getLeftOperand();
            Value arrayType = array.getArrayOperand().resolveValue(this);
            Value indexType = array.getIndexOperand().resolveValue(this);
            Value valueType = tree.getRightOperand().resolveValue(this);
            setSource(tree);
            return maker.AssignAt(arrayType, indexType, valueType);
        } else {
            MakerField field = tree.getLeftOperand().resolveMakerField(this);
            Value type = tree.getRightOperand().resolveValue(this);
            setSource(tree);
            if (field == null)
                throw new IllegalStateException("MakerField is null: " + tree.getLeftOperand());
            if (field.isLocal())
                return maker.Assign(field.getName(), type);
            else if (field.isStatic())
                return maker.Assign(field.getClassType().getName(), field.getName(), type);
            else
                return maker.Assign(field.getClassType().getValue(), field.getName(), type);
        }
    }

    public Value resolveValue(DotOperator tree) {
        Value reference = tree.getLeftOperand().resolveValueOrNull(this);
        if (reference != null) {
            String name = tree.getRightOperand().resolvePath(this);
            if (name != null) {
                setSource(tree);
                if (reference.toArray() != null && "length".equals(name))
                    return maker.Length(reference);
                else
                    return maker.Get(reference, name);
            } else if (tree.getRightOperand().toMethodCall() != null) {
                MethodCall call = tree.getRightOperand().toMethodCall();
                CallStack callStack = resolveMethodCall(call);
                setSource(tree);
                return maker.Call(reference, callStack.getMethodName(), callStack);
            }
        } else {
            String path = tree.getLeftOperand().resolvePath(this);
            String name = tree.getRightOperand().resolvePath(this);
            if (name != null) {
                setSource(tree);
                return maker.Get(path, name);
            } else if (tree.getRightOperand().toMethodCall() != null) {
                MethodCall call = tree.getRightOperand().toMethodCall();
                CallStack callStack = resolveMethodCall(call);
                setSource(tree);
                return maker.Call(path, callStack.getMethodName(), callStack);
            }
        }
        throw new IllegalStateException("Cannot resolveValue(" + tree + ")");
    }

    public Value resolveValue(MethodCall call) {
        Value reference = maker.This();
        CallStack callStack = resolveMethodCall(call);
        setSource(call);
        return maker.Call(reference, callStack.getMethodName(), callStack);
    }

    public Value resolveValue(IncrementOperator tree) {
        if (tree.getOperand() instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.getOperand();
            Value arrayType = array.getArrayOperand().resolveValue(this);
            Value indexType = array.getIndexOperand().resolveValue(this);
            setSource(tree);
            return maker.IncAt(arrayType, indexType);
        } else {
            MakerField field = tree.getOperand().resolveMakerField(this);
            setSource(tree);
            if (field.isLocal())
                return maker.Inc(field.getName());
            else if (field.isStatic())
                return maker.Inc(field.getClassType().getName(), field.getName());
            else
                return maker.Inc(field.getClassType().getValue(), field.getName());
        }
    }

    public Value resolveValue(DecrementOperator tree) {
        if (tree.getOperand() instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.getOperand();
            Value arrayType = array.getArrayOperand().resolveValue(this);
            Value indexType = array.getIndexOperand().resolveValue(this);
            setSource(tree);
            return maker.DecAt(arrayType, indexType);
        } else {
            MakerField field = tree.getOperand().resolveMakerField(this);
            setSource(tree);
            if (field.isLocal())
                return maker.Dec(field.getName());
            else if (field.isStatic())
                return maker.Dec(field.getClassType().getName(), field.getName());
            else
                return maker.Dec(field.getClassType().getValue(), field.getName());
        }
    }

    public Value resolveValue(PostIncrementOperator tree) {
        if (tree.getOperand() instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.getOperand();
            Value arrayType = array.getArrayOperand().resolveValue(this);
            Value indexType = array.getIndexOperand().resolveValue(this);
            setSource(tree);
            return maker.PostIncAt(arrayType, indexType);
        } else {
            MakerField field = tree.getOperand().resolveMakerField(this);
            setSource(tree);
            if (field.isLocal())
                return maker.PostInc(field.getName());
            else if (field.isStatic())
                return maker.PostInc(field.getClassType().getName(), field.getName());
            else
                return maker.PostInc(field.getClassType().getValue(), field.getName());
        }
    }

    public Value resolveValue(PostDecrementOperator tree) {
        if (tree.getOperand() instanceof ArrayIndex) {
            ArrayIndex array = (ArrayIndex) tree.getOperand();
            Value arrayType = array.getArrayOperand().resolveValue(this);
            Value indexType = array.getIndexOperand().resolveValue(this);
            setSource(tree);
            return maker.PostDecAt(arrayType, indexType);
        } else {
            MakerField field = tree.getOperand().resolveMakerField(this);
            setSource(tree);
            if (field.isLocal())
                return maker.PostDec(field.getName());
            else if (field.isStatic())
                return maker.PostDec(field.getClassType().getName(), field.getName());
            else
                return maker.PostDec(field.getClassType().getValue(), field.getName());
        }
    }

    public Value resolveValue(AndThenOperator tree) {
        ClassMaker.AndOrExpression expr = tree.getLeftOperand().resolveAndThen(this);
        Value cond = tree.getRightOperand().resolveValue(this);
        setSource(tree);
        return maker.Logic(expr, cond);
    }

    public Value resolveValue(OrElseOperator tree) {
        ClassMaker.AndOrExpression expr = tree.getLeftOperand().resolveOrElse(this);
        Value cond = tree.getRightOperand().resolveValue(this);
        setSource(tree);
        return maker.Logic(expr, cond);
    }

    public Value resolveValue(AstStatementReserved term) {
        // setLineNumber(term);
        if ("this".equals(term.getReservedWord()))
            return maker.This();
        else if ("super".equals(term.getReservedWord()))
            return maker.Super();
        else if ("null".equals(term.getReservedWord()))
            return maker.Null();
        else
            throw new IllegalStateException("Unknown reserved word: " + term.getReservedWord());
    }

    // resolveValueOrNull(...)
    public Value resolveValueOrNull(TerminalName term) {
        setSource(term);
        // FIXME this looks dodgy ...
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
                return maker.Get(field.getClassType().getValue(), term.getName());
        }
    }

    public Value resolveValueOrNull(DotOperator tree) {
        Value reference = tree.getLeftOperand().resolveValueOrNull(this);
        if (reference != null) {
            String name = tree.getRightOperand().resolvePath(this);
            if (name != null) {
                setSource(tree);
                return maker.Get(reference, name);
            } else if (tree.getRightOperand().toMethodCall() != null) {
                MethodCall call = tree.getRightOperand().toMethodCall();
                CallStack callStack = resolveMethodCall(call);
                setSource(tree);
                return maker.Call(reference, callStack.getMethodName(), callStack);
            }
        } else {
            String path = tree.getLeftOperand().resolvePath(this);
            Type declared = maker.findType(path);
            if (declared != null) {
                String name = tree.getRightOperand().resolvePath(this);
                setSource(tree);
                return maker.Get(declared.getName(), name);
            }
        }
        return null;
    }

    // resolveMakerField(...)
    public MakerField resolveMakerField(TerminalName term) {
        MakerField field = maker.findField(term.getName());
        if (field != null) {
            if (!field.isLocal() && !field.isStatic()) {
                setSource(term);
                maker.This();
            }
        }
        return field;
    }

    public MakerField resolveMakerField(DotOperator tree) {
        Value type = tree.getLeftOperand().resolveValueOrNull(this);
        if (type != null) {
            String name = tree.getRightOperand().resolvePath(this);
            setSource(tree);
            return maker.Find(type, name);
        } else {
            String path = tree.getLeftOperand().resolvePath(this);
            String name = tree.getRightOperand().resolvePath(this);
            setSource(tree);
            return maker.Find(path, name);
        }
    }

    // resolvePath(...)
    public String resolvePath(TerminalName term) {
        setSource(term);
        return term.getName();
    }

    public String resolvePath(DotOperator term) {
        String left = term.getLeftOperand().resolvePath(this);
        String right = term.getRightOperand().resolvePath(this);
        return left + "." + right;
    }

    public Type resolveType(TerminalName term) {
        setSource(term);
        return maker.findType(term.getName());
    }

    public Type resolveType(DotOperator term) {
        String path = resolvePath(term);
        setSource(term);
        return maker.findType(path);
    }

    public Type resolveType(ArrayOf arrayOf) {
        Type type = arrayOf.getType().resolveType(this);
        setSource(arrayOf);
        return maker.ArrayOf(type);
    }

    public Type resolveType(ArrayIndex arrayOf) {
        Type type = arrayOf.getArrayOperand().resolveType(this);
        setSource(arrayOf);
        return maker.ArrayOf(type);
    }

    public CallStack resolveMethodCall(MethodCall call) {
        String methodName = call.getName().resolvePath(this);
        CallStack actualParameters = null;
        setSource(call);
        if (call.getParams() == null)
            actualParameters = maker.Push();
        else
            actualParameters = call.getParams().resolveCallStack(this);
        actualParameters.setMethodName(methodName);
        return actualParameters;
    }

    // resolveCallStack(...)

    //             tree
    //            /    \
    //        tree    3rd param
    //       /    \
    //   tree    2nd param
    //       \
    //      1st param
    public CallStack resolveCallStack(CommaOperator tree) {
        if (tree.getLeftExpression() == null) {
            Value reference = tree.getRightExpression().resolveValue(this);
            setSource(tree);
            return maker.Push(reference);
        } else {
            CallStack actualParameters = tree.getLeftExpression().resolveCallStack(this);
            Value reference = tree.getRightExpression().resolveValue(this);
            setSource(tree);
            return actualParameters.Push(reference);
        }
    }

    public CallStack resolveCallStack(AstExpression expr) {
        Value reference = expr.resolveValue(this);
        setSource(expr);
        return maker.Push(reference);
    }

    public AndOrExpression resolveAndThen(AndThenOperator tree) {
        if (tree.getLeftOperand() == null) {
            Value cond = tree.getRightOperand().resolveValue(this);
            setSource(tree);
            return maker.AndThen(cond);
        } else {
            AndOrExpression expr = null;
            if (tree.getLeftOperand() instanceof AndThenOperator)
                expr = tree.getLeftOperand().resolveAndThen(this);
            else {
                Value cond = tree.getLeftOperand().resolveValue(this);
                setSource(tree);
                expr = maker.AndThen(cond);
            }
            Value cond = tree.getRightOperand().resolveValue(this);
            setSource(tree);
            return maker.AndThen(expr, cond);
        }
    }

    public AndOrExpression resolveOrElse(OrElseOperator tree) {
        if (tree.getLeftOperand() == null) {
            Value cond = tree.getRightOperand().resolveValue(this);
            setSource(tree);
            return maker.OrElse(cond);
        } else {
            AndOrExpression expr = null;
            if (tree.getLeftOperand() instanceof OrElseOperator)
                expr = tree.getLeftOperand().resolveOrElse(this);
            else {
                Value cond = tree.getLeftOperand().resolveValue(this);
                setSource(tree);
                expr = maker.OrElse(cond);
            }
            Value cond = tree.getRightOperand().resolveValue(this);
            setSource(tree);
            return maker.OrElse(expr, cond);
        }
    }

    public AndOrExpression resolveOrElse(AstExpression tree) {
        Value cond = tree.resolveValue(this);
        setSource(tree);
        return maker.OrElse(cond);
    }

    public AndOrExpression resolveAndThen(AstExpression tree) {
        Value cond = tree.resolveValue(this);
        setSource(tree);
        return maker.AndThen(cond);
    }

    public String toString() {
        String source = "";
        if (getFilename() != null) {
            source = "(" + getFilename() + ":" + getLineNumber() + ")";
        }
        return getClass().getSimpleName() + source;
    }
}
