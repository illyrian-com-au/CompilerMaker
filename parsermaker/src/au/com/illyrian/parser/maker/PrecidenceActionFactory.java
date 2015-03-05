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

package au.com.illyrian.parser.maker;

import au.com.illyrian.classmaker.ast.ActualParameter;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.jesub.ast.AstStructureFactory;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.Operator;
import au.com.illyrian.parser.impl.ParserConstants;
import au.com.illyrian.parser.impl.PrecidenceAction;

public class PrecidenceActionFactory implements PrecidenceAction<AstExpression>, ParserConstants
{
	AstStructureFactory factory = new AstStructureFactory();
    
    public PrecidenceActionFactory()
    {
    	super();
    }

    public AstStructureFactory factory()
    {
        return factory;
    }

    public AstExpression identifierAction(String name) throws ParserException
    {
        return factory.Name(name);
    }

    public AstExpression callAction(AstExpression name, AstExpression callStack) throws ParserException
    {
    	// FIXME
    	TerminalName name1 = (TerminalName)name;
    	ActualParameter params = (ActualParameter)callStack;
        return factory.Call(name1, params);
    }

    public AstExpression beginParameters(AstExpression name) throws ParserException
    {
        return null;
    }

    public AstExpression addParameter(AstExpression callStack, AstExpression param) throws ParserException
    {
    	// FIXME
    	ActualParameter left = (ActualParameter)callStack;
    	AstExpression right = (AstExpression)param;
        return factory.Push(left, right);
    }

    public AstExpression literalAction(Lexer lexer) throws ParserException
    {
    	AstExpression result = null;
        switch (lexer.getToken())
        {
        case Lexer.INTEGER:
        	result = factory.Literal(lexer.getTokenInteger());
            break;
        case Lexer.DECIMAL:
        	result = factory.Literal(lexer.getTokenFloat());
            break;
        case Lexer.CHARACTER:
        	result = factory.Literal(lexer.getTokenInteger());
            break;
        case Lexer.STRING:
        	result = factory.Literal(lexer.getTokenValue());
            break;
        default:
            throw new ParserException("Cannot handle: " + lexer);
        }
        return result;
    }

    public AstExpression parenthesesAction(AstExpression expr) throws ParserException
    {
        return expr;
    }

    public AstExpression infixAction(Operator operator, AstExpression leftOperand, AstExpression rightOperand)
            throws ParserException
    {
    	AstExpression result = null;
        switch (operator.getIndex())
        {
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
            // FIXME add other binary operators
        default:
            throw new IllegalStateException("Don't know how to process binary operator: " + operator);
        }
        return result;
    }

    public AstExpression prefixAction(Operator operator, AstExpression operand) throws ParserException
    {
    	AstExpression result = null;
        switch (operator.getIndex())
        {
        case NEG:
        	result = factory.Neg(operand);
            break;
        case NOT:
        	result = factory.Not(operand);
            break;
        case INV:
        	result = factory.Inv(operand);
            break;
        case INC:
        	result = factory.Inc(operand);
            break;
        case DEC:
        	result = factory.Dec(operand);
            break;
        default:
            throw new IllegalStateException("Don't know how to process prefix operator: " + operator);
        }
        return result;
    }

    public AstExpression postfixAction(Operator operator, AstExpression operand) throws ParserException
    {
    	AstExpression result = null;
        switch (operator.getIndex())
        {
        case POSTINC:
        	result =factory.PostInc(operand);
            break;
        case POSTDEC:
        	result =factory.PostDec(operand);
            break;
        default:
            throw new IllegalStateException("Don't know how to process postfix operator: " + operator);
        }
        return result;
    }

    public AstExpression bracketAction(Operator operator, AstExpression leftOperand, AstExpression rightOperand) throws ParserException
    {
        return factory.ArrayIndex(leftOperand, rightOperand);
    }

    public AstExpression castAction(AstExpression type, AstExpression value) throws ParserException
    {
        return factory.Cast(type, value);
    }

}
