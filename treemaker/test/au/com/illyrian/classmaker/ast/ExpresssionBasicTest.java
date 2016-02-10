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

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ExpressionIfc;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.types.Type;

public class ExpresssionBasicTest extends TestCase
{
    ExpressionIfc buf = new ClassMakerText();
    AstExpressionVisitor visitor = new AstExpressionVisitor(buf);
    AstExpressionFactory ast = new AstExpressionFactory();
   
    public void testLiteralByte()
    {
        TerminalNumber term = ast.Literal((byte)2);
        assertEquals("Wrong toString()", "2b", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(byte)", type.toString());
        assertEquals("Wrong output", "[Literal(2)]", buf.toString());
    }

    public void testLiteralShort()
    {
        TerminalNumber term = ast.Literal((short)2);
        assertEquals("Wrong toString()", "2s", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(short)", type.toString());
        assertEquals("Wrong output", "[Literal(2)]", buf.toString());
    }

    public void testLiteralInt()
    {
        TerminalNumber term = ast.Literal(2);
        assertEquals("Wrong toString()", "2", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Literal(2)]", buf.toString());
    }

    public void testLiteralLong()
    {
        TerminalNumber term = ast.Literal(2L);
        assertEquals("Wrong toString()", "2l", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(long)", type.toString());
        assertEquals("Wrong output", "[Literal(2l)]", buf.toString());
    }

    public void testLiteralFloat()
    {
        TerminalDecimal term = new TerminalDecimal(3.141f);
        assertEquals("Wrong toString()", "3.141f", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(float)", type.toString());
        assertEquals("Wrong output", "[Literal(3.141f)]", buf.toString());

        TerminalDecimal term2 = new TerminalDecimal(new Float(3.141));
        assertEquals("Wrong toString()", "3.141f", term2.toString());
    }

    public void testLiteralDouble()
    {
        TerminalDecimal term = new TerminalDecimal(3.141d);
        assertEquals("Wrong toString()", "3.141", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(double)", type.toString());
        assertEquals("Wrong output", "[Literal(3.141)]", buf.toString());

        TerminalDecimal term2 = new TerminalDecimal(new Double(3.141));
        assertEquals("Wrong toString()", "3.141", term2.toString());
}

    public void testLiteralString()
    {
        TerminalString term = new TerminalString("Hello");
        assertEquals("Wrong toString()", "\"Hello\"", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.String)", type.toString());
        assertEquals("Wrong output", "[Literal(\"Hello\")]", buf.toString());
    }

    public void testLiteralChar()
    {
        TerminalNumber term = ast.Literal('A');
        assertEquals("Wrong toString()", "'A'", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(char)", type.toString());
        assertEquals("Wrong output", "[Literal('A')]", buf.toString());
    }

    public void testLiteralCharToString()
    {
        TerminalNumber term2 = ast.Literal(new Character('\n'));
        assertEquals("Wrong toString()", "'\\012'", term2.toString());

        TerminalNumber term3 = ast.Literal(new Character('\002'));
        assertEquals("Wrong toString()", "'\\002'", term3.toString());

        TerminalNumber term4 = ast.Literal(new Character('\000'));
        assertEquals("Wrong toString()", "'\\000'", term4.toString());

        TerminalNumber term5 = ast.Literal(new Character('~'));
        assertEquals("Wrong toString()", "'~'", term5.toString());

        TerminalNumber term6 = ast.Literal(new Character((char)255));
        assertEquals("Wrong toString()", "'\\377'", term6.toString());

        TerminalNumber term7 = ast.Literal(new Character(' '));
        assertEquals("Wrong toString()", "' '", term7.toString());

        TerminalNumber term8 = ast.Literal(new Character(Character.MAX_VALUE));
        assertEquals("Wrong toString()", "'\\177777'", term8.toString());
}

    public void testLiteralTrue()
    {
        TerminalBoolean term = new TerminalBoolean(true);
        assertEquals("Wrong toString()", "true", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(boolean)", type.toString());
        assertEquals("Wrong output", "[Literal(true)]", buf.toString());

        TerminalBoolean term2 = new TerminalBoolean(Boolean.TRUE);
        assertEquals("Wrong toString()", "true", term2.toString());
}

    public void testLiteralFalse()
    {
        TerminalBoolean term = new TerminalBoolean(false);
        assertEquals("Wrong toString()", "false", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(boolean)", type.toString());
        assertEquals("Wrong output", "[Literal(false)]", buf.toString());

        TerminalBoolean term2 = new TerminalBoolean(Boolean.FALSE);
        assertEquals("Wrong toString()", "false", term2.toString());
    }

    public void testLocalGet()
    {
        TerminalName term = new TerminalName("Foo");
        assertEquals("Wrong toString()", "Foo", term.toString());
        Type type = term.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Get(\"Foo\")]", buf.toString());
    }
    
    public void testFieldGet()
    {
    	AstExpression expr = ast.Dot(ast.Name("x"), ast.Name("a"));
        assertEquals("Wrong toString()", "x.a", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Get(Get(\"x\"), \"a\")]", buf.toString());
    }

    public void testGetStatic()
    {
    	AstExpression expr = ast.Dot(ast.Name("Object"), ast.Name("a"));
        assertEquals("Wrong toString()", "Object.a", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Get(\"java.lang.Object\", \"a\")]", buf.toString());
    }

    public void testIntClassGet()
    {
    	AstExpression expr = ast.Dot(ast.Name("int"), ast.Name("class"));
        assertEquals("Wrong toString()", "int.class", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Class)", type.toString());
        assertEquals("Wrong output", "[Get(\"int\", \"class\")]", buf.toString());
    }

    public void testStringClassGet()
    {
    	AstExpression expr = ast.Dot(ast.Dot(ast.Dot(ast.Name("java"), ast.Name("lang")), ast.Name("Object")), ast.Name("class"));
        assertEquals("Wrong toString()", "java.lang.Object.class", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Class)", type.toString());
        assertEquals("Wrong output", "[Get(\"java.lang.Object\", \"class\")]", buf.toString());
    }

    public void testGetDotStatic()
    {
    	AstExpression expr = ast.Dot(ast.Dot(ast.Dot(ast.Name("java"), ast.Name("lang")), ast.Name("Object")), ast.Name("a"));
        assertEquals("Wrong toString()", "java.lang.Object.a", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Get(\"java.lang.Object\", \"a\")]", buf.toString());
    }

    // this.a
    // super.a
    // int.class
    // java.lang.String.class
    public void testThisFieldGet()
    {
    	AstExpression expr = ast.Dot(ast.Name("this"), ast.Name("a"));
        assertEquals("Wrong toString()", "this.a", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Get(This(), \"a\")]", buf.toString());
    }

    public void testSuperFieldGet()
    {
    	AstExpression expr = ast.Dot(ast.Name("super"), ast.Name("a"));
        assertEquals("Wrong toString()", "super.a", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Get(Super(), \"a\")]", buf.toString());
    }

    public void testLocalAssign()
    {
    	AstExpression expr = ast.Assign(ast.Name("Foo"), ast.Literal(2));
        assertEquals("Wrong toString()", "(Foo = 2)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Assign(\"Foo\", Literal(2))]", buf.toString());
    }

    public void testFieldAssign()
    {
    	AstExpression expr = ast.Assign(ast.Dot(ast.Name("x"), ast.Name("a")), ast.Literal(2));
        assertEquals("Wrong toString()", "(x.a = 2)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Assign(Get(\"x\"), \"a\", Literal(2))]", buf.toString());
    }

    public void testStaticAssign()
    {
    	AstExpression expr = ast.Assign(ast.Dot(ast.Name("Object"), ast.Name("a")), ast.Literal(2));
        assertEquals("Wrong toString()", "(Object.a = 2)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Assign(\"java.lang.Object\", \"a\", Literal(2))]", buf.toString());
    }

    public void testLocalInc()
    {
        IncrementOperator assign = ast.Inc(ast.Name("Foo"));
        assertEquals("Wrong toString()", "++(Foo)", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Inc(\"Foo\")]", buf.toString());
    }

    public void testFieldInc()
    {
        IncrementOperator assign = ast.Inc(ast.Dot(ast.Name("x"), ast.Name("Foo")));
        assertEquals("Wrong toString()", "++(x.Foo)", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Inc(Get(\"x\"), \"Foo\")]", buf.toString());
    }

    public void testStaticInc()
    {
        IncrementOperator assign = ast.Inc(ast.Dot(ast.Name("Object"), ast.Name("Foo")));
        assertEquals("Wrong toString()", "++(Object.Foo)", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Inc(\"java.lang.Object\", \"Foo\")]", buf.toString());
    }

    public void testLocalDec()
    {
        DecrementOperator assign = ast.Dec(ast.Name("Foo"));
        assertEquals("Wrong toString()", "--(Foo)", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Dec(\"Foo\")]", buf.toString());
    }
    
    public void testFieldDec()
    {
        DecrementOperator assign = ast.Dec(ast.Dot(ast.Name("x"), ast.Name("Foo")));
        assertEquals("Wrong toString()", "--(x.Foo)", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Dec(Get(\"x\"), \"Foo\")]", buf.toString());
    }

    public void testStaticDec()
    {
        DecrementOperator assign = ast.Dec(ast.Dot(ast.Name("Object"), ast.Name("Foo")));
        assertEquals("Wrong toString()", "--(Object.Foo)", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Dec(\"java.lang.Object\", \"Foo\")]", buf.toString());
    }

    public void testLocalPostInc()
    {
        AstExpression assign = ast.PostInc(ast.Name("Foo"));
        assertEquals("Wrong toString()", "(Foo)++", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[PostInc(\"Foo\")]", buf.toString());
    }

    public void testFieldPostInc()
    {
        AstExpression assign = ast.PostInc(ast.Dot(ast.Name("x"), ast.Name("Foo")));
        assertEquals("Wrong toString()", "(x.Foo)++", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[PostInc(Get(\"x\"), \"Foo\")]", buf.toString());
    }

    public void testStaticPostInc()
    {
        AstExpression assign = ast.PostInc(ast.Dot(ast.Name("Object"), ast.Name("Foo")));
        assertEquals("Wrong toString()", "(Object.Foo)++", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[PostInc(\"java.lang.Object\", \"Foo\")]", buf.toString());
    }

    public void testLocalPostDec()
    {
        AstExpression assign = ast.PostDec(ast.Name("Foo"));
        assertEquals("Wrong toString()", "(Foo)--", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[PostDec(\"Foo\")]", buf.toString());
    }
    
    public void testFieldPostDec()
    {
        AstExpression assign = ast.PostDec(ast.Dot(ast.Name("x"), ast.Name("Foo")));
        assertEquals("Wrong toString()", "(x.Foo)--", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[PostDec(Get(\"x\"), \"Foo\")]", buf.toString());
    }

    public void testStaticPostDec()
    {
        AstExpression assign = ast.PostDec(ast.Dot(ast.Name("Object"), ast.Name("Foo")));
        assertEquals("Wrong toString()", "(Object.Foo)--", assign.toString());
        Type type = assign.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[PostDec(\"java.lang.Object\", \"Foo\")]", buf.toString());
    }

    public void testLocalCall()
    {
        AstExpression call = ast.Call(ast.Name("Foo"), null);
        assertEquals("Wrong toString()", "Foo()", call.toString());
        Type type = call.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"Foo\", Push())]", buf.toString());
    }

    public void testLocalCallParam1()
    {
        AstExpression call = ast.Call(ast.Name("Foo"), ast.Name("x"));
        assertEquals("Wrong toString()", "Foo(x)", call.toString());
        Type type = call.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"Foo\", Push(Get(\"x\")))]", buf.toString());
    }

    public void testLocalCallParam2()
    {
        AstExpression call = ast.Call(ast.Name("Foo"), ast.Comma(ast.Name("x"), ast.Literal("Hi")));
        assertEquals("Wrong toString()", "Foo(x, \"Hi\")", call.toString());
        Type type = call.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"Foo\", Push(Get(\"x\")).Push(Literal(\"Hi\")))]", buf.toString());
    }
    
    public void testCallOtherMethod()
    {
        AstExpression expr = ast.Dot(ast.Name("x"), ast.Call(ast.Name("Foo"), null));
        assertEquals("Wrong toString()", "x.Foo()", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(Get(\"x\"), \"Foo\", Push())]", buf.toString());
    }

    public void testCallStatic()
    {
        AstExpression expr = ast.Dot(ast.Name("Object"), ast.Call(ast.Name("Foo"), null));
        assertEquals("Wrong toString()", "Object.Foo()", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(\"Object\", \"Foo\", Push())]", buf.toString());
    }

    public void testBasicNeg()
    {
        AstExpression expr = ast.Neg(ast.Literal(5));
        assertEquals("Wrong toString()", "-(5)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Neg(Literal(5))]", buf.toString());
    }

    public void testInstanceOf()
    {
        AstExpression expr = ast.InstanceOf(ast.Name("Foo"), ast.Name("Object"));
        assertEquals("Wrong toString()", "instanceof(Foo,Object)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(boolean)", type.toString());
        assertEquals("Wrong output", "[InstanceOf(Get(\"Foo\"), \"Object\")]", buf.toString());
    }

    public void testCast()
    {
        AstExpression expr = ast.Cast(ast.Name("Object"), ast.Name("Foo"));
        assertEquals("Wrong toString()", "cast(Object, Foo)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Cast(Get(\"Foo\"), \"Object\")]", buf.toString());
    }

    public static class DummyOperator extends AstExpressionBase
    {
    }

    public void testAstExpressionBase()
    {
        AstExpression term = new DummyOperator();
        
        try {
            term.resolveMakerField(visitor);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // success
        }
        try {
            term.resolveCallStack(visitor);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // success
        }
        
        try {
            visitor.resolveType(term);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            assertEquals("Wrong IllegalStateException message", 
                    "No special case for ExpressionTree type: DummyOperator", ex.getMessage());
        }
        
        assertEquals("Wrong toString", "AstExpressionVisitor", visitor.toString());
    }
    
    public void testAstExpessionSourceLine()
    {
        SourceLine line = new SourceLine() {
            public String getFilename()
            {
                return "MyClass.java";
            }

            public int getLineNumber()
            {
                return 1;
            }
        };
        AstExpressionBase term = new TerminalName("Foo");
        term.setSourceLine(line);
        assertEquals("Wrong filename", "MyClass.java", term.getFilename());
        assertEquals("Wrong linenumber", 1, term.getLineNumber());
    }
}
