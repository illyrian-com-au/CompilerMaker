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

public class AstExpressionFactory 
{
    private final SourceLine sourceLine;
    
    public AstExpressionFactory()
    {
        sourceLine = null;
    }

    public AstExpressionFactory(SourceLine sourceLine)
    {
        this.sourceLine = sourceLine;
    }
    
    public SourceLine getSourceLine() 
    {
        return sourceLine;
    }

    public TerminalName Name(String name)
    {
        return new TerminalName(name, sourceLine);
    }
    
    public TerminalString Literal(String string)
    {
        return new TerminalString(string, sourceLine);
    }
    
    public TerminalNumber Literal(long value)
    {
        return new TerminalNumber(value, sourceLine);
    }
    
    public TerminalNumber Literal(int value)
    {
        return new TerminalNumber(value, sourceLine);
    }
    
    public TerminalNumber Literal(short value)
    {
        return new TerminalNumber(value, sourceLine);
    }
    
    public TerminalNumber Literal(byte value)
    {
        return new TerminalNumber(value, sourceLine);
    }
    
    public TerminalNumber Literal(char ch)
    {
        return new TerminalNumber(ch, sourceLine);
    }
    
    public TerminalDecimal Literal(double value)
    {
        return new TerminalDecimal(value, sourceLine);
    }
    
    public TerminalDecimal Literal(float value)
    {
        return new TerminalDecimal(value, sourceLine);
    }
    
    public TerminalBoolean Literal(boolean value)
    {
        return new TerminalBoolean(value, sourceLine);
    }
    
    public DotOperator Dot(AstExpression left, AstExpression right)
    {
        return new DotOperator(left, right, sourceLine);
    }
    
    public AssignmentOperator Assign(AstExpression left, AstExpression right)
    {
        return new AssignmentOperator(left, right, sourceLine);
    }
    
    public BinaryOperator Add(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.ADD, left, right, sourceLine);
    }
    
    public BinaryOperator Subt(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.SUBT, left, right, sourceLine);
    }
    
    public BinaryOperator Mult(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.MULT, left, right, sourceLine);
    }
    
    public BinaryOperator Div(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.DIV, left, right, sourceLine);
    }

    public BinaryOperator Rem(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.REM, left, right, sourceLine);
    }

    public BinaryOperator SHL(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.SHL, left, right, sourceLine);
    }

    public BinaryOperator SHR(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.SHR, left, right, sourceLine);
    }

    public BinaryOperator USHR(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.USHR, left, right, sourceLine);
    }

    public BinaryOperator And(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.AND, left, right, sourceLine);
    }

    public BinaryOperator Or(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.OR, left, right, sourceLine);
    }

    public BinaryOperator Xor(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.XOR, left, right, sourceLine);
    }

    public UnaryOperator Neg(AstExpression expr)
    {
        return new UnaryOperator(UnaryOperator.NEG, expr, sourceLine);
    }
    
    public BinaryOperator GT(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.GT, left, right, sourceLine);
    }

    public BinaryOperator LT(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.LT, left, right, sourceLine);
    }

    public BinaryOperator GE(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.GE, left, right, sourceLine);
    }

    public BinaryOperator LE(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.LE, left, right, sourceLine);
    }

    public BinaryOperator EQ(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.EQ, left, right, sourceLine);
    }

    public BinaryOperator NE(AstExpression left, AstExpression right)
    {
        return new BinaryOperator(BinaryOperator.NE, left, right, sourceLine);
    }

    public UnaryOperator Not(AstExpression expr)
    {
        return new UnaryOperator(UnaryOperator.NOT, expr, sourceLine);
    }
    
    public UnaryOperator Inv(AstExpression expr)
    {
        return new UnaryOperator(UnaryOperator.INV, expr, sourceLine);
    }
    
    public IncrementOperator Inc(AstExpression expr)
    {
        return new IncrementOperator(expr, sourceLine);
    }
    
    public DecrementOperator Dec(AstExpression expr)
    {
        return new DecrementOperator(expr, sourceLine);
    }

    public PostIncrementOperator PostInc(AstExpression expr)
    {
        return new PostIncrementOperator(expr, sourceLine);
    }

    public PostDecrementOperator PostDec(AstExpression expr)
    {
        return new PostDecrementOperator(expr, sourceLine);
    }
    
    public AndThenOperator AndThen(AstExpression left, AstExpression right)
    {
        return new AndThenOperator(left, right, sourceLine);
    }

    public OrElseOperator OrElse(AstExpression left, AstExpression right)
    {
        return new OrElseOperator(left, right, sourceLine);
    }

    public CastOperator Cast(AstExpression left, AstExpression right)
    {
        return new CastOperator(left, right, sourceLine);
    }

    public ArrayIndex ArrayIndex(AstExpression left, AstExpression right)
    {
        return new ArrayIndex(left, right, sourceLine);
    }

    public AstExpression ArrayOf(AstExpression type) 
    {
        return new ArrayOf(type, sourceLine);
    }

    public AstExpression ArrayOf(AstExpression type, AstExpression dimension) 
    {
        return new ArrayOf(type, dimension, sourceLine);
    }

    public InstanceOfOperator InstanceOf(AstExpression left, AstExpression right)
    {
        return new InstanceOfOperator(left, right, sourceLine);
    }

    public CommaOperator Comma(AstExpression left, AstExpression right)
    {
        return new CommaOperator(left, right, sourceLine);
    }
    
    public MethodCall Call(AstExpression left, AstExpression right)
    {
        return new MethodCall(left, right, sourceLine);
    }

    public NewOperator New(AstExpression constructor)
    {
        return new NewOperator(constructor, sourceLine);
    }

    public NewArrayOperator NewArray(AstExpression left, AstExpression right)
    {
        return new NewArrayOperator(left, right, sourceLine);
    }

    public AstExpressionLink Link(AstExpression left, AstExpression right)
    {
        return new AstExpressionLink(left, right);
    }
   
    public AstExpression Reserved(String name) {
        AstStatementReserved stmt = AstStatementReserved.lookup(name);
        return stmt;
    }

    public String toString()
    {
        return "AstExpresssionFactory()";
    }
}
