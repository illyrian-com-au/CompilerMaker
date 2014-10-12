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

import java.util.EmptyStackException;
import java.util.Stack;

import au.com.illyrian.classmaker.ExpressionIfc;
import au.com.illyrian.classmaker.types.Type;

public class AstExpressionFactoryOld
{
    private final ExpressionIfc maker;
    Stack<AstExpression>  stack = new Stack<AstExpression>();
    AstExpressionVisitor visitor;
    
    // FIXME - make private then remove.
    public AstExpressionFactoryOld(ExpressionIfc classMaker)
    {
        maker = classMaker;
        visitor = new AstExpressionVisitor(classMaker);
    }

    // State access methods
    public ExpressionIfc getExpressionIfc()
    {
        return maker;
    }

    public AstExpressionVisitor getVisitor()
    {
        return visitor;
    }

    public AstExpression peek()
    {
        return stack.peek();
    }
    
    protected void push(AstExpression element)
    {
        stack.push(element);
    }
    
    public AstExpression pop()
    {
        try {
            return stack.pop();
        } catch (EmptyStackException ex) {
            throw new IllegalStateException(getClass().getSimpleName() + " - Stack is empty");
        }
    }

    public void name(String name)
    {
        push(new TerminalName(name));
    }
    
    public void literal(String string)
    {
        push(new TerminalString(string));
    }
    
    public void literal(long value)
    {
        push(new TerminalNumber(value));
    }
    
    public void literal(int value)
    {
        push(new TerminalNumber(value));
    }
    
    public void literal(short value)
    {
        push(new TerminalNumber(value));
    }
    
    public void literal(byte value)
    {
        push(new TerminalNumber(value));
    }
    
    public void literal(char ch)
    {
        push(new TerminalNumber(ch));
    }
    
    public void literal(double value)
    {
        push(new TerminalDecimal(value));
    }
    
    public void literal(float value)
    {
        push(new TerminalDecimal(value));
    }
    
    public void dot()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new DotOperator(left, right));
    }
    
    public void assign()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new AssignmentOperator(left, right));
    }
    
    public void add()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.ADD, left, right));
    }
    
    public void subt()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.SUBT, left, right));
    }
    
    public void mult()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.MULT, left, right));
    }
    
    public void div()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.DIV, left, right));
    }

    public void rem()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.REM, left, right));
    }

    public void shl()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.SHL, left, right));
    }

    public void shr()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.SHR, left, right));
    }

    public void ushr()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.USHR, left, right));
    }

    public void and()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.AND, left, right));
    }

    public void or()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.OR, left, right));
    }

    public void xor()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.XOR, left, right));
    }

    public void neg()
    {
        push(new UnaryOperator(UnaryOperator.NEG, pop()));
    }
    
    public void gt()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.GT, left, right));
    }

    public void lt()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.LT, left, right));
    }

    public void ge()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.GE, left, right));
    }

    public void le()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.LE, left, right));
    }

    public void eq()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.EQ, left, right));
    }

    public void ne()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new BinaryOperator(BinaryOperator.NE, left, right));
    }

    public void not()
    {
        push(new UnaryOperator(UnaryOperator.NOT, pop()));
    }
    
    public void inv()
    {
        push(new UnaryOperator(UnaryOperator.INV, pop()));
    }
    
    public void inc()
    {
        push(new IncrementOperator(pop()));
    }
    
    public void dec()
    {
        push(new DecrementOperator(pop()));
    }

    public void postinc()
    {
        push(new PostIncrementOperator(pop()));
    }

    public void postdec()
    {
        push(new PostDecrementOperator(pop()));
    }
    
    public void cast()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new CastOperator(left, right));
    }

    public void checkinstance()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new InstanceOfOperator(left, right));
    }

    public void emptyParams()
    {
        push(null);
    }
    
    public void param()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new ActualParameter(left, right));
    }
    
    public void call()
    {
        AstExpression right = pop();
        AstExpression left = pop();
        push(new MethodCall((TerminalName)left, (ActualParameter)right));
    }
    
    public Type resolve()
    {
        if (stack.size() != 1)
            throw new IllegalStateException(getClass().getSimpleName() + " - Stack has more elements: " + this);
        return pop().resolveType(visitor);
    }
    
    public String toString()
    {
        return stack.toString();
    }
}
