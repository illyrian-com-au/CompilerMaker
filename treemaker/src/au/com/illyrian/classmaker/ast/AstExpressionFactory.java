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

import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.jesub.ast.AstStructureLink;

public class AstExpressionFactory 
{
    private final LineNumber source;
    
    public AstExpressionFactory()
    {
        source = null;
    }

    public AstExpressionFactory(LineNumber source)
    {
        this.source = source;
    }
    
    public int getLineNumber() {
        return source == null ? 0 : source.getLineNumber();
    }

    public TerminalName Name(String name)
    {
        TerminalName expr = new TerminalName(name);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalString Literal(String string)
    {
        TerminalString expr =  new TerminalString(string);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalNumber Literal(long value)
    {
        TerminalNumber expr =  new TerminalNumber(value);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalNumber Literal(int value)
    {
        TerminalNumber expr =  new TerminalNumber(value);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalNumber Literal(short value)
    {
        TerminalNumber expr =  new TerminalNumber(value);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalNumber Literal(byte value)
    {
        TerminalNumber expr =  new TerminalNumber(value);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalNumber Literal(char ch)
    {
        TerminalNumber expr =  new TerminalNumber(ch);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalDecimal Literal(double value)
    {
        TerminalDecimal expr =  new TerminalDecimal(value);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalDecimal Literal(float value)
    {
        TerminalDecimal expr =  new TerminalDecimal(value);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public TerminalBoolean Literal(boolean value)
    {
        TerminalBoolean expr =  new TerminalBoolean(value);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public DotOperator Dot(AstExpression left, AstExpression right)
    {
        DotOperator expr =  new DotOperator(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public AssignmentOperator Assign(AstExpression left, AstExpression right)
    {
        AssignmentOperator expr =  new AssignmentOperator(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public BinaryOperator Add(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.ADD, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public BinaryOperator Subt(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.SUBT, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public BinaryOperator Mult(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.MULT, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public BinaryOperator Div(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.DIV, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator Rem(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.REM, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator SHL(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.SHL, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator SHR(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.SHR, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator USHR(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.USHR, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator And(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.AND, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator Or(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.OR, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator Xor(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.XOR, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public UnaryOperator Neg(AstExpression expr)
    {
        UnaryOperator operator = new UnaryOperator(UnaryOperator.NEG, expr);
        operator.setLineNumber(getLineNumber());
        return operator;
    }
    
    public BinaryOperator GT(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.GT, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator LT(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.LT, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator GE(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.GE, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator LE(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.LE, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator EQ(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.EQ, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public BinaryOperator NE(AstExpression left, AstExpression right)
    {
        BinaryOperator expr = new BinaryOperator(BinaryOperator.NE, left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public UnaryOperator Not(AstExpression expr)
    {
        UnaryOperator operator = new UnaryOperator(UnaryOperator.NOT, expr);
        operator.setLineNumber(getLineNumber());
        return operator;
    }
    
    public UnaryOperator Inv(AstExpression expr)
    {
        UnaryOperator operator = new UnaryOperator(UnaryOperator.INV, expr);
        operator.setLineNumber(getLineNumber());
        return operator;
    }
    
    public IncrementOperator Inc(AstExpression expr)
    {
        IncrementOperator operator = new IncrementOperator(expr);
        operator.setLineNumber(getLineNumber());
        return operator;
    }
    
    public DecrementOperator Dec(AstExpression expr)
    {
        DecrementOperator operator = new DecrementOperator(expr);
        operator.setLineNumber(getLineNumber());
        return operator;
    }

    public PostIncrementOperator PostInc(AstExpression expr)
    {
        PostIncrementOperator operator = new PostIncrementOperator(expr);
        operator.setLineNumber(getLineNumber());
        return operator;
    }

    public PostDecrementOperator PostDec(AstExpression expr)
    {
        PostDecrementOperator operator = new PostDecrementOperator(expr);
        operator.setLineNumber(getLineNumber());
        return operator;
    }
    
    public AndThenOperator AndThen(AstExpression left, AstExpression right)
    {
        AndThenOperator expr = new AndThenOperator(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public OrElseOperator OrElse(AstExpression left, AstExpression right)
    {
        OrElseOperator expr = new OrElseOperator(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public CastOperator Cast(AstExpression left, AstExpression right)
    {
        CastOperator expr = new CastOperator(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public ArrayIndex ArrayIndex(AstExpression left, AstExpression right)
    {
        ArrayIndex expr = new ArrayIndex(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public AstExpression ArrayOf(AstExpression type) 
    {
        ArrayOf expr = new ArrayOf(type);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public AstExpression ArrayOf(AstExpression type, AstExpression dimension) 
    {
        ArrayOf expr = new ArrayOf(type, dimension);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public InstanceOfOperator InstanceOf(AstExpression left, AstExpression right)
    {
        InstanceOfOperator expr = new InstanceOfOperator(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public CommaOperator Comma(AstExpression left, AstExpression right)
    {
        CommaOperator expr = new CommaOperator(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }
    
    public MethodCall Call(AstExpression left, AstExpression right)
    {
        MethodCall expr = new MethodCall(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public NewOperator New(AstExpression constructor)
    {
        NewOperator expr = new NewOperator(constructor);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public NewArrayOperator NewArray(AstExpression left, AstExpression right)
    {
        NewArrayOperator expr = new NewArrayOperator(left, right);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public AstExpression Link(AstExpression left, AstExpression right)
    {
        if (right == null) {
            return left;
        } else if (left == null) {
            return right;
        } else {
            AstExpressionLink expr = new AstExpressionLink(left, right);
            expr.setLineNumber(getLineNumber());
            return expr;
        }
    }
   
    public AstExpression Reserved(String name) {
        AstStatementReserved expr = AstStatementReserved.lookup(name);
        expr.setLineNumber(getLineNumber());
        return expr;
    }

    public String toString()
    {
        return "AstExpresssionFactory()";
    }
}
