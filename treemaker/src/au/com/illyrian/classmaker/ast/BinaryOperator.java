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
import au.com.illyrian.classmaker.types.Value;

public class BinaryOperator extends AstExpressionBase
{
    public static final int MULT = 21;
    public static final int DIV = 22;
    public static final int REM = 23;
    public static final int POW = 24; // Not a java operator
    public static final int ADD = 31;
    public static final int SUBT = 32;
    public static final int SHL = 41;
    public static final int SHR = 42;
    public static final int USHR = 43;
    public static final int LT = 51;
    public static final int GT = 52;
    public static final int LE = 53;
    public static final int GE = 54;
    public static final int EQ = 61;
    public static final int NE = 62;
    public static final int AND = 71;
    public static final int XOR = 72;
    public static final int OR  = 73;

    private final int            operatorType;
    private final AstExpression leftOperand;
    private final AstExpression rightOperand;
    
    public BinaryOperator(int operator, AstExpression left, AstExpression right)
    {
        operatorType = operator;
        leftOperand = left;
        rightOperand = right;
    }
    
    public BinaryOperator(int operator, AstExpression left, AstExpression right, SourceLine sourceLine)
    {
        super(sourceLine);
        operatorType = operator;
        leftOperand = left;
        rightOperand = right;
    }
    
    public int getOperatorType()
    {
        return operatorType;
    }
    
    public AstExpression getLeftOperand()
    {
        return leftOperand;
    }
    
    public AstExpression getRightOperand()
    {
        return rightOperand;
    }
    
    public Value resolveValue(AstExpressionVisitor visitor)
    {
        return visitor.resolveValue(this);
    }
    
    public String toString()
    {
        switch (getOperatorType())
        {
        case MULT:
            return "(" + leftOperand + " * " + rightOperand + ")";
        case DIV:
            return "(" + leftOperand + " / " + rightOperand + ")";
        case REM:
            return "(" + leftOperand + " % " + rightOperand + ")";
        case ADD:
            return "(" + leftOperand + " + " + rightOperand + ")";
        case SUBT:
            return "(" + leftOperand + " - " + rightOperand + ")";
        case SHL:
            return "(" + leftOperand + " << " + rightOperand + ")";
        case SHR:
            return "(" + leftOperand + " >> " + rightOperand + ")";
        case USHR:
            return "(" + leftOperand + " >>> " + rightOperand + ")";
        case LT:
            return "(" + leftOperand + " < " + rightOperand + ")";
        case GT:
            return "(" + leftOperand + " > " + rightOperand + ")";
        case LE:
            return "(" + leftOperand + " <= " + rightOperand + ")";
        case GE:
            return "(" + leftOperand + " >= " + rightOperand + ")";
        case EQ:
            return "(" + leftOperand + " == " + rightOperand + ")";
        case NE:
            return "(" + leftOperand + " != " + rightOperand + ")";
        case AND:
            return "(" + leftOperand + " & " + rightOperand + ")";
        case XOR:
            return "(" + leftOperand + " ^ " + rightOperand + ")";
        case OR :
            return "(" + leftOperand + " | " + rightOperand + ")";
        default:
            return "(" + leftOperand + " #" + getOperatorType() + "# " + rightOperand + ")";
        }
    }
}
