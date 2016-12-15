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

package au.com.illyrian.parser.expr;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.ParserConstants;
import au.com.illyrian.parser.opp.OperatorPrecidenceAction;

public class AstExpressionPrecidenceAction 
   implements OperatorPrecidenceAction<AstExpression>, ParserConstants
{
    final AstExpressionFactory factory;

    public AstExpressionPrecidenceAction(AstExpressionFactory factory)
    {
        super();
        this.factory = factory;
    }

    public AstExpressionFactory factory()
    {
        return factory;
    }

    public AstExpression tokenAction(Lexer lexer)
    {
        AstExpression result = null;
        switch (lexer.getTokenType()) {
        case NUMBER:
            result = factory.Literal(lexer.getTokenInteger());
            break;
        case DECIMAL:
            result = factory.Literal(lexer.getTokenFloat());
            break;
        case CHARACTER:
            result = factory.Literal(lexer.getTokenInteger());
            break;
        case STRING:
            result = factory.Literal(lexer.getTokenString());
            break;
        case IDENTIFIER:
            result = factory.Name(lexer.getTokenValue());
            break;
        case RESERVED:
            result = factory.Reserved(lexer.getTokenValue());
            break;
        default:
            throw new ParserException("Cannot handle: " + lexer);
        }
        return result;
    }

    public AstExpression binaryAction(int operator, AstExpression leftOperand, AstExpression rightOperand)
           
    {
        AstExpression result = null;
        switch (operator) {
        case ADD:
            result = factory.Add(leftOperand, rightOperand);
            break;
        case SUBT:
            result = factory.Subt(leftOperand, rightOperand);
            break;
        case MULT:
            result = factory.Mult(leftOperand, rightOperand);
            break;
        case DIV:
            result = factory.Div(leftOperand, rightOperand);
            break;
        case REM:
            result = factory.Rem(leftOperand, rightOperand);
            break;
        case DOT:
            result = factory.Dot(leftOperand, rightOperand);
            break;
        case ASSIGN:
            result = factory.Assign(leftOperand, rightOperand);
            break;
        case EQ:
            result = factory.EQ(leftOperand, rightOperand);
            break;
        case NE:
            result = factory.NE(leftOperand, rightOperand);
            break;
        case LE:
            result = factory.LE(leftOperand, rightOperand);
            break;
        case GE:
            result = factory.GE(leftOperand, rightOperand);
            break;
        case GT:
            result = factory.GT(leftOperand, rightOperand);
            break;
        case LT:
            result = factory.LT(leftOperand, rightOperand);
            break;
        case ANDTHEN:
            result = factory.AndThen(leftOperand, rightOperand);
            break;
        case ORELSE:
            result = factory.OrElse(leftOperand, rightOperand);
            break;
        case XOR:
            result = factory.Xor(leftOperand, rightOperand);
            break;
        case SHL:
            result = factory.SHL(leftOperand, rightOperand);
            break;
        case SHR:
            result = factory.SHR(leftOperand, rightOperand);
            break;
        case USHR:
            result = factory.USHR(leftOperand, rightOperand);
            break;
        case AND:
            result = factory.And(leftOperand, rightOperand);
            break;
        case OR:
            result = factory.Or(leftOperand, rightOperand);
            break;
        case INSTANCEOF:
            result = factory.InstanceOf(leftOperand, rightOperand);
            break;
        case INDEX:
            result = factory.ArrayIndex(leftOperand, rightOperand);
            break;
        case CAST:
            result = factory.Cast(leftOperand, rightOperand);
            break;
        case CALL:
            result = factory.Call(leftOperand, rightOperand);
            break;
        case COMMA:
            result = factory.Comma(leftOperand, rightOperand);
            break;
        case ARRAYOF:
            result = factory.ArrayOf(leftOperand, rightOperand);
            break;
        // FIXME add other binary operators
        default:
            throw new IllegalStateException("Don't know how to process binary operator: " + operator);
        }
        return result;
    }

    public AstExpression unaryAction(int operator, AstExpression operand)
    {
        AstExpression result = null;
        switch (operator) {
        case NEG:
            result = factory.Neg(operand);
            break;
        case NOT:
            result = factory.Not(operand);
            break;
        case INV:
            result = factory.Inv(operand);
            break;
        case NEW:
            result = factory.New(operand);
            break;
        case INC:
            result = factory.Inc(operand);
            break;
        case DEC:
            result = factory.Dec(operand);
            break;
        case POSTINC:
            result = factory.PostInc(operand);
            break;
        case POSTDEC:
            result = factory.PostDec(operand);
            break;
        case ARRAYOF:
            result = factory.ArrayOf(operand);
            break;
        case NOP:
            result = operand;
            break;
        default:
            throw new IllegalStateException("Don't know how to process unary operator: " + operator);
        }
        return result;
    }
    
    public AstExpression call(AstExpression name, AstExpression parameters) {
        return factory.Call(name,  parameters);
    }
    
    public AstExpression index(AstExpression name, AstExpression parameters) {
        return factory.ArrayIndex(name,  parameters);
    }
}
