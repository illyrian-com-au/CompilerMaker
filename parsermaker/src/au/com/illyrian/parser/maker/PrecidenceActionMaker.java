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

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerLocation;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.expressionparser.ExpressionAction;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.Operator;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.PrecidenceAction;

public class PrecidenceActionMaker implements PrecidenceAction, ClassMakerLocation
{
    private ClassMaker  maker = null;
    AstExpressionFactory ast = null;

    public void setClassMaker(ClassMaker classMaker) 
    {
        maker = classMaker;
    }

    public ClassMaker getClassMaker()
    {
        if (maker == null)
            throw new NullPointerException("classMaker is null.");
        return maker;
    }
    
    AstExpressionFactory ast()
    {
        if (ast == null)
            ast = new AstExpressionFactory(getClassMaker());
        return ast;
    }

    public Object literalAction(Integer value) throws ParserException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object identifierAction(String name) throws ParserException
    {
        ast().name(name);
        return null;
    }

    public Object callAction(Object callStack) throws ParserException
    {
        ast().call();
        return null;
    }

    public Object beginParameters(Object name) throws ParserException
    {
        ast().emptyParams();
        return null;
    }

    public Object addParameter(Object callStack, Object param) throws ParserException
    {
        ast().param();
        return null;
    }

    public Object literalAction(Lexer lexer) throws ParserException
    {
        switch (lexer.getToken())
        {
        case Lexer.INTEGER:
            ast().literal(lexer.getTokenInteger());
            break;
        case Lexer.DECIMAL:
            ast().literal(lexer.getTokenFloat());
            break;
        case Lexer.CHARACTER:
            ast().literal(lexer.getTokenInteger());
            break;
        case Lexer.STRING:
            ast().literal(lexer.getTokenValue());
            break;
        default:
            throw new ParserException("Cannot handle: " + lexer);
        }
        return null;
    }

    public Object parenthesesAction(Object expr) throws ParserException
    {
        return expr;
    }

    public Object infixAction(Operator operator, Object leftOperand, Object rightOperand)
            throws ParserException
    {
        switch (operator.getIndex())
        {
        case ExpressionAction.ADD:
            ast().add();
            break;
        case ExpressionAction.SUBT:
            ast().subt();
            break;
        case ExpressionAction.MULT:
            ast().mult();
            break;
        case ExpressionAction.DIV:
            ast().div();
            break;
        case ExpressionAction.REM:
            ast().rem();
            break;
        case ExpressionAction.DOT:
            ast().dot();
            break;
        case ExpressionAction.ASSIGN:
            ast().assign();
            break;
        default:
            throw new IllegalStateException("Don't know how to process binary operator: " + operator);
        }
        return null;
    }

    public Object prefixAction(Operator operator, Object operand) throws ParserException
    {
        switch (operator.getIndex())
        {
        case ExpressionAction.NEG:
            ast().neg();
            break;
        case ExpressionAction.NOT:
            ast().not();
            break;
        case ExpressionAction.INV:
            ast().inv();
            break;
        case ExpressionAction.INC:
            ast().inc();
            break;
        case ExpressionAction.DEC:
            ast().dec();
            break;
        default:
            throw new IllegalStateException("Don't know how to process prefix operator: " + operator);
        }
        return null;
    }

    public Object postfixAction(Operator operator, Object operand) throws ParserException
    {
        switch (operator.getIndex())
        {
        case ExpressionAction.POSTINC:
            ast().postinc();
            break;
        case ExpressionAction.POSTDEC:
            ast().postdec();
            break;
        default:
            throw new IllegalStateException("Don't know how to process postfix operator: " + operator);
        }
        return null;
    }

    public Object bracketAction(Operator operator, Object leftOperand, Object rightOperand) throws ParserException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object assignAction(Operator operator, Object leftOperand, String name, Object rightOperand)
            throws ParserException
    {
        return null; // FIXME remove assignAction
    }

    public Object assignAction(Operator operator, String name, Object rightOperand) throws ParserException
    {
        // TODO Auto-generated method stub
        return null; // FIXME remove assignAction
    }

    public Object preProcess(Object operand, Operator nextOperator) throws ParserException
    {
        return operand; // FIXME - remove preprocess
    }

    public Object postProcess(Object result) throws ParserException
    {
        return ast().resolve();
    }

    public Object castAction(Object type, Object value) throws ParserException
    {
        ast().cast();
        return null;
    }

    
}
