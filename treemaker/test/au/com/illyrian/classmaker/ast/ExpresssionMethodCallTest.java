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
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.jesub.ast.AstStructureVisitor;

public class ExpresssionMethodCallTest extends TestCase
{
    ExpressionIfc buf = new ClassMakerText();
    AstExpressionFactory build = new AstExpressionFactory();
    AstStructureVisitor visitor = new AstStructureVisitor(buf);

    public void testLocalCall()
    {
    	AstExpression ast = build.Call(build.Name("f"), null);
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "f(null) ", ast.toString());
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"f\", Push())]", buf.toString());
    }

    public void testDotsCall()
    {
    	AstExpression ast = build.Dot(build.Dot(build.Dot(build.Name("x"), build.Name("y")), build.Name("z")), 
    			build.Call(build.Name("f"), null));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(Get(Get(Get(\"x\"), \"y\"), \"z\"), \"f\", Push())]", buf.toString());
    }

    public void testStaticCall()
    {
    	AstExpression ast = build.Dot(build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("Object")), 
    			build.Call(build.Name("f"), null));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "[Call(\"java.lang.Object\", \"f\", Push())]", buf.toString());
    }

    public void testLocalCallParams()
    {
    	AstExpression ast = build.Call(build.Name("f"), 
    			build.Push(build.Push(build.Push(build.Literal(1)), build.Literal(2)), build.Literal(3)));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "f(1, 2, 3) ", ast.toString());
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"f\", Push(Literal(1)).Push(Literal(2)).Push(Literal(3)))]", buf.toString());
    }

    public void testOtherCallParams()
    {
    	AstExpression ast = build.Dot(build.Name("x"), build.Call(build.Name("f"), build.Push(build.Literal(1))));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "x.f(1) ", ast.toString());
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(Get(\"x\"), \"f\", Push(Literal(1)))]", buf.toString());
    }

    public void testStaticCallParams()
    {
    	AstExpression ast = build.Dot(build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("Object")), 
    			build.Call(build.Name("f"), build.Push(build.Literal(1))));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "java.lang.Object.f(1) ", ast.toString());
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(\"java.lang.Object\", \"f\", Push(Literal(1)))]", buf.toString());
    }

    public void testLocalCallExpressions()
    {
    	AstExpression ast = build.Call(build.Name("f"), 
    			build.Push(build.Push(build.Push(build.Mult(build.Literal(2), build.Literal(3))), build.Literal("Hello")), build.Name("a")));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "f((2 * 3), \"Hello\", a) ", ast.toString());
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"f\", Push(Mult(Literal(2), Literal(3))).Push(Literal(\"Hello\")).Push(Get(\"a\")))]", buf.toString());
    }

}
