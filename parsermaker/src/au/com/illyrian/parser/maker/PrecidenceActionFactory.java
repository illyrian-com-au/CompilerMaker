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
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.expressionparser.ExpressionAction;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.Operator;
import au.com.illyrian.parser.impl.Operator;
import au.com.illyrian.parser.impl.PrecidenceAction;

public class PrecidenceActionFactory implements PrecidenceAction
{
    AstExpressionFactory build = new AstExpressionFactory();
    
    public PrecidenceActionFactory()
    {
    	super();
    }

    public AstExpressionFactory ast()
    {
        return build;
    }

    public AstExpression identifierAction(String name) throws ParserException
    {
        return build.Name(name);
    }

    public Object callAction(Object name, Object callStack) throws ParserException
    {
    	TerminalName name1 = (TerminalName)name;
    	ActualParameter params = (ActualParameter)callStack;
        return build.Call(name1, params);
    }

    public Object beginParameters(Object name) throws ParserException
    {
        return null;
    }

    public Object addParameter(Object callStack, Object param) throws ParserException
    {
    	ActualParameter left = (ActualParameter)callStack;
    	AstExpression right = (AstExpression)param;
        return build.Push(left, right);
    }

    public AstExpression literalAction(Lexer lexer) throws ParserException
    {
    	AstExpression result = null;
        switch (lexer.getToken())
        {
        case Lexer.INTEGER:
        	result = build.Literal(lexer.getTokenInteger());
            break;
        case Lexer.DECIMAL:
        	result = build.Literal(lexer.getTokenFloat());
            break;
        case Lexer.CHARACTER:
        	result = build.Literal(lexer.getTokenInteger());
            break;
        case Lexer.STRING:
        	result = build.Literal(lexer.getTokenValue());
            break;
        default:
            throw new ParserException("Cannot handle: " + lexer);
        }
        return result;
    }

    public Object parenthesesAction(Object expr) throws ParserException
    {
        return expr;
    }

    public AstExpression infixAction(Operator operator, Object leftOperand, Object rightOperand)
            throws ParserException
    {
    	AstExpression result = null;
    	AstExpression left = (AstExpression)leftOperand;
    	AstExpression right = (AstExpression)rightOperand;
        switch (operator.getIndex())
        {
        case ExpressionAction.ADD:
            result = build.Add(left, right);
            break;
        case ExpressionAction.SUBT:
            result = build.Subt(left, right);
            break;
        case ExpressionAction.MULT:
        	result = build.Mult(left, right);
            break;
        case ExpressionAction.DIV:
        	result = build.Div(left, right);
            break;
        case ExpressionAction.REM:
        	result = build.Rem(left, right);
            break;
        case ExpressionAction.DOT:
        	result = build.Dot(left, right);
            break;
        case ExpressionAction.ASSIGN:
        	result = build.Assign(left, right);
            break;
        case ExpressionAction.EQ:
        	result = build.EQ(left, right);
            break;
        case ExpressionAction.NE:
        	result = build.NE(left, right);
            break;
        case ExpressionAction.LE:
        	result = build.LE(left, right);
            break;
        case ExpressionAction.GE:
        	result = build.GE(left, right);
            break;
        case ExpressionAction.GT:
        	result = build.GT(left, right);
            break;
        case ExpressionAction.LT:
        	result = build.LT(left, right);
            break;
        case ExpressionAction.ANDTHEN:
        	result = build.AndThen(left, right);
            break;
        case ExpressionAction.ORELSE:
        	result = build.OrElse(left, right);
            break;
        case ExpressionAction.XOR:
        	result = build.Xor(left, right);
            break;
        case ExpressionAction.SHL:
        	result = build.SHL(left, right);
            break;
        case ExpressionAction.SHR:
        	result = build.SHR(left, right);
            break;
        case ExpressionAction.USHR:
        	result = build.USHR(left, right);
            break;
        case ExpressionAction.AND:
        	result = build.And(left, right);
            break;
        case ExpressionAction.OR:
        	result = build.Or(left, right);
            break;
            // FIXME add other binary operators
        default:
            throw new IllegalStateException("Don't know how to process binary operator: " + operator);
        }
        return result;
    }

    public AstExpression prefixAction(Operator operator, Object operand) throws ParserException
    {
    	AstExpression result = null;
    	AstExpression expr = (AstExpression)operand;
        switch (operator.getIndex())
        {
        case ExpressionAction.NEG:
        	result = build.Neg(expr);
            break;
        case ExpressionAction.NOT:
        	result = build.Not(expr);
            break;
        case ExpressionAction.INV:
        	result = build.Inv(expr);
            break;
        case ExpressionAction.INC:
        	result = build.Inc(expr);
            break;
        case ExpressionAction.DEC:
        	result = build.Dec(expr);
            break;
        default:
            throw new IllegalStateException("Don't know how to process prefix operator: " + operator);
        }
        return result;
    }

    public AstExpression postfixAction(Operator operator, Object operand) throws ParserException
    {
    	AstExpression result = null;
    	AstExpression expr = (AstExpression)operand;
        switch (operator.getIndex())
        {
        case ExpressionAction.POSTINC:
        	result =build.PostInc(expr);
            break;
        case ExpressionAction.POSTDEC:
        	result =build.PostDec(expr);
            break;
        default:
            throw new IllegalStateException("Don't know how to process postfix operator: " + operator);
        }
        return result;
    }

    public AstExpression bracketAction(Operator operator, Object leftOperand, Object rightOperand) throws ParserException
    {
    	AstExpression leftExpr = (AstExpression)leftOperand;
    	AstExpression rightExpr = (AstExpression)rightOperand;
        return build.ArrayIndex(leftExpr, rightExpr);
    }

    public AstExpression castAction(Object type, Object value) throws ParserException
    {
    	AstExpression typeExpr = (AstExpression)type;
    	AstExpression valueExpr = (AstExpression)value;
        return build.Cast(typeExpr, valueExpr);
    }

    /**
     * @deprecated
     */
	public Object postProcess(Object result) throws ParserException {
    	AstExpression expr = (AstExpression)result;
		return expr;
	}

    /**
     * @deprecated
     */
	public Object preProcess(Object operand, Operator nextOperator)
			throws ParserException {
		return operand;
	}

    
}
