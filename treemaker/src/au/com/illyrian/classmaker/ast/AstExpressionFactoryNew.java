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

public class AstExpressionFactoryNew
{
    public AstExpressionFactoryNew()
    {
    }

    public TerminalName Name(String name)
    {
        return new TerminalName(name);
    }
    
    public TerminalString Literal(String string)
    {
        return new TerminalString(string);
    }
    
    public TerminalNumber Literal(long value)
    {
        return new TerminalNumber(value);
    }
    
    public TerminalNumber Literal(int value)
    {
        return new TerminalNumber(value);
    }
    
    public TerminalNumber Literal(short value)
    {
        return new TerminalNumber(value);
    }
    
    public TerminalNumber Literal(byte value)
    {
        return new TerminalNumber(value);
    }
    
    public TerminalNumber Literal(char ch)
    {
        return new TerminalNumber(ch);
    }
    
    public TerminalDecimal Literal(double value)
    {
        return new TerminalDecimal(value);
    }
    
    public TerminalDecimal Literal(float value)
    {
        return new TerminalDecimal(value);
    }
    
    public DotOperator Dot(AstExpression left, AstExpression right)
    {
        return new DotOperator(left, right);
    }
    
    public AssignmentOperator Assign(AstExpression left, AstExpression right)
    {
        return new AssignmentOperator(left, right);
    }
    
    public BinaryOperator Add(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.ADD, left, right);
    }
    
    public BinaryOperator Subt(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.SUBT, left, right);
    }
    
    public BinaryOperator Mult(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.MULT, left, right);
    }
    
    public BinaryOperator Div(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.DIV, left, right);
    }

    public BinaryOperator Rem(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.REM, left, right);
    }

    public BinaryOperator SHL(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.SHL, left, right);
    }

    public BinaryOperator SHR(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.SHR, left, right);
    }

    public BinaryOperator USHR(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.USHR, left, right);
    }

    public BinaryOperator And(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.AND, left, right);
    }

    public BinaryOperator Or(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.OR, left, right);
    }

    public BinaryOperator Xor(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.XOR, left, right);
    }

    public UnaryOperator Neg(AstExpression expr)
    {
        return new UnaryOperator(UnaryOperator.NEG, expr);
    }
    
    public BinaryOperator GT(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.GT, left, right);
    }

    public BinaryOperator LT(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.LT, left, right);
    }

    public BinaryOperator GE(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.GE, left, right);
    }

    public BinaryOperator LE(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.LE, left, right);
    }

    public BinaryOperator EQ(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.EQ, left, right);
    }

    public BinaryOperator NE(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.NE, left, right);
    }

    public UnaryOperator Not(AstExpression expr)
    {
        return new UnaryOperator(UnaryOperator.NOT, expr);
    }
    
    public UnaryOperator Inv(AstExpression expr)
    {
        return new UnaryOperator(UnaryOperator.INV, expr);
    }
    
    public IncrementOperator Inc(AstExpression expr)
    {
        return new IncrementOperator(expr);
    }
    
    public DecrementOperator Dec(AstExpression expr)
    {
        return new DecrementOperator(expr);
    }

    public PostIncrementOperator PostInc(AstExpression expr)
    {
        return new PostIncrementOperator(expr);
    }

    public PostDecrementOperator PostDec(AstExpression expr)
    {
        return new PostDecrementOperator(expr);
    }
    
    public AndThenOperator AndThen(AstExpression left, AstExpression right)
    {
        return new AndThenOperator(left, right);
    }

    public OrElseOperator OrElse(AstExpression left, AstExpression right)
    {
        return new OrElseOperator(left, right);
    }

    public CastOperator Cast(AstExpression left, AstExpression right)
    {
        return new CastOperator(left, right);
    }

    public InstanceOfOperator InstanceOf(AstExpression left, AstExpression right)
    {
        return new InstanceOfOperator(left, right);
    }

    public ActualParameter Push(ActualParameter left, AstExpression right)
    {
        return new ActualParameter(left, right);
    }
    
    public ActualParameter Push(AstExpression expr)
    {
        return new ActualParameter(null, expr);
    }
    
    public MethodCall Call(TerminalName left, ActualParameter right)
    {
        return new MethodCall(left, right);
    }
    
    public String toString()
    {
        return "AstExpresssionFactory()";
    }
}
