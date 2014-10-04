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

import au.com.illyrian.classmaker.types.Type;

public class UnaryOperator extends AstExpressionBase
{
//    public static final int DOT = 9;
//    public static final int NOP = 10;
    public static final int INV = 11;
    public static final int NEG = 12;
    public static final int NOT = 13;
//    public static final int INC = 14;
//    public static final int DEC = 15;
//    public static final int POSTINC = 16;
//    public static final int POSTDEC = 17;
//    public static final int NEW = 18;
//    public static final int CAST = 19;

    private final int            operatorType;
    private final AstExpression operand;
    
    public UnaryOperator(int operator, AstExpression operand)
    {
        operatorType = operator;
        this.operand = operand;
    }
    
    public int getOperatorType()
    {
        return operatorType;
    }
    
    public AstExpression getOperand()
    {
        return operand;
    }

    public Type resolveType(AstExpressionVisitor visitor)
    {
        return visitor.resolveType(this);
    }

    public String toString()
    {
        switch (getOperatorType())
        {
        case NEG:
            return "-" + operand;
        case INV:
            return "~" + operand;
        case NOT:
            return "!" + operand;
        default:
            return operand + " unknown unary operator: " + getOperatorType();
        }
    }
}
