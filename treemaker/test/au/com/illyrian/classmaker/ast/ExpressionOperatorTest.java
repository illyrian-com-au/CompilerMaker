// Copyright (c) 2010, Donald Strong.
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
import junit.framework.TestCase;

public class ExpressionOperatorTest extends TestCase
{
    MockExpressionIfc buf = new MockExpressionIfc();
    AstExpressionVisitor visitor = new AstExpressionVisitor(buf);
    AstExpressionFactory ast = new AstExpressionFactory();

    public void testBasicAdd()
    {
        AstExpression expr = ast.Add(ast.Literal(1), ast.Literal(2));
        assertEquals("Wrong toString()", "(1 + 2)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "1 2 +$$ ", buf.toString());
    }

    public void testManyAdds()
    {
    	AstExpression expr = ast.Add(ast.Add(ast.Add(ast.Literal(1), ast.Literal(2)), ast.Literal(3)), ast.Literal(4));
        assertEquals("Wrong toString()", "(((1 + 2) + 3) + 4)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "1 2 +$$ 3 +$$ 4 +$$ ", buf.toString());
    }

    public void testManyTypes()
    {
    	AstExpression expr = ast.Add(ast.Add(ast.Add(ast.Literal((byte)1), 
    				ast.Literal((short)2)), ast.Literal((long)3)), ast.Literal('A'));
        assertEquals("Wrong toString()", "(((1b + 2s) + 3l) + 'A')", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(long)", type.toString());
        assertEquals("Wrong output", "1b 2s +$$ 3l +$$ 'A' +$$ ", buf.toString());
    }

    public void testAssignDecimals()
    {
    	AstExpression expr = ast.Assign(ast.Name("x"), ast.Add(ast.Literal(3.141f), ast.Literal(9.87654321)));
        assertEquals("Wrong toString()", "x = (3.141f + 9.87654321)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(double)", type.toString());
        assertEquals("Wrong output", "3.141f 9.87654321d +$$ assign(x,$) ", buf.toString());
    }

    public void testString()
    {
    	AstExpression expr = ast.Add(ast.Literal("Hello "), ast.Literal("World "));
        assertEquals("Wrong toString()", "(\"Hello \" + \"World \")", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.String)", type.toString());
        assertEquals("Wrong output", "\"Hello \" \"World \" +$$ ", buf.toString());
    }

    public void testArithmeticOps1()
    {
    	AstExpression expr =  
    			ast.Rem(ast.Div(ast.Neg(ast.Mult(
    					ast.Subt(ast.Add(ast.Literal(2), ast.Literal(3)),  ast.Literal(3)),  
    					ast.Literal(4))), ast.Literal(2)), ast.Literal(3));
        assertEquals("Wrong toString()", "((-(((2 + 3) - 3) * 4) / 2) % 3)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "2 3 +$$ 3 -$$ 4 *$$ -$ 2 /$$ 3 %$$ ", buf.toString());
    }

    public void testBitshiftOps1()
    {
    	AstExpression expr = ast.USHR(ast.Inv(ast.SHR(ast.SHL(ast.Literal(299), ast.Literal(3)),  ast.Literal(2))), ast.Literal(4));
        assertEquals("Wrong toString()", "(~((299 << 3) >> 2) >>> 4)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "299 3 <<$$ 2 >>$$ ^$ 4 >>>$$ ", buf.toString());
    }

    public void testBitwiseOps1()
    {
    	AstExpression expr = ast.Xor(ast.Or(ast.Literal(2), ast.Literal(3)), ast.And(ast.Literal(2), ast.Literal(4)));
        assertEquals("Wrong toString()", "((2 | 3) ^ (2 & 4))", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "2 3 |$$ 2 4 &$$ ~$$ ", buf.toString());
    }

    public void testRelationOps1()
    {
    	AstExpression expr =  ast.Not(ast.EQ(ast.GT(ast.Literal(2), ast.Literal(3)), ast.LT(ast.Literal(2), ast.Literal(4))));
        assertEquals("Wrong toString()", "!((2 > 3) == (2 < 4))", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(boolean)", type.toString());
        assertEquals("Wrong output", "2 3 >$$ 2 4 <$$ ==$$ !$ ", buf.toString());
    }

    public void testRelationOps2()
    {
    	AstExpression expr = ast.NE(ast.GE(ast.Literal(2), ast.Literal(3)),ast.LE(ast.Literal(2), ast.Literal(4)));
        assertEquals("Wrong toString()", "((2 >= 3) != (2 <= 4))", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(boolean)", type.toString());
        assertEquals("Wrong output", "2 3 >=$$ 2 4 <=$$ !=$$ ", buf.toString());
    }

    public void testNegativeOps()
    {
    	AstExpression expr = ast.Neg(ast.Subt(ast.Neg(ast.Literal(2)), 
    			ast.Neg(ast.Div(ast.Neg(ast.Literal(3)), 
    			ast.Neg(ast.Mult(ast.Neg(ast.Literal(2)), ast.Neg(ast.Literal(3))))))));
        assertEquals("Wrong toString()", "-(-2 - -(-3 / -(-2 * -3)))", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "2 -$ 3 -$ 2 -$ 3 -$ *$$ -$ /$$ -$ -$$ -$ ", buf.toString());
    }

}
