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
import au.com.illyrian.classmaker.types.Type;

public class ExpresssionMethodCallTest extends TestCase
{
    MockExpressionIfc buf = new MockExpressionIfc();
    AstExpressionFactory ast = new AstExpressionFactory(buf);

    public void testLocalCall()
    {
        ast.name("f");
        ast.emptyParams();
        ast.call();
        assertEquals("Wrong toString()", "[f(null) ]", ast.toString());
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "load(this) call($,f()) ", buf.toString());
    }

    public void testDotsCall()
    {
        ast.name("x");
        ast.name("y");
        ast.dot();
        ast.name("z");
        ast.dot();
        ast.name("f");
        ast.emptyParams();
        ast.call();
        ast.dot();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "load(x) load($,y) load($,z) call($,f()) ", buf.toString());
    }

    public void testStaticCall()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Object");
        ast.dot();
        ast.name("f");
        ast.emptyParams();
        ast.call();
        ast.dot();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "call(\"java.lang.Object\",f()) ", buf.toString());
    }

    public void testLocalCallParams()
    {
        ast.name("f");
        ast.emptyParams();
        ast.literal(1);
        ast.param();
        ast.literal(2);
        ast.param();
        ast.literal(3);
        ast.param();
        ast.call();
        assertEquals("Wrong toString()", "[f(1, 2, 3) ]", ast.toString());
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "load(this) 1 2 3 call($,f($$$)) ", buf.toString());
    }

    public void testOtherCallParams()
    {
        ast.name("x");
        ast.name("f");
        ast.emptyParams();
        ast.literal(1);
        ast.param();
        ast.call();
        ast.dot();
        assertEquals("Wrong toString()", "[x.f(1) ]", ast.toString());
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "load(x) 1 call($,f($)) ", buf.toString());
    }

    public void testStaticCallParams()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Object");
        ast.dot();
        ast.name("f");
        ast.emptyParams();
        ast.literal(1);
        ast.param();
        ast.call();
        ast.dot();
        assertEquals("Wrong toString()", "[java.lang.Object.f(1) ]", ast.toString());
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "1 call(\"java.lang.Object\",f($)) ", buf.toString());
    }

    public void testLocalCallExpressions()
    {
        ast.name("f");
        ast.emptyParams();
        ast.literal(2);
        ast.literal(3);
        ast.mult();
        ast.param();
        ast.literal("Hello");
        ast.param();
        ast.name("a");
        ast.param();
        ast.call();
        assertEquals("Wrong toString()", "[f((2 * 3), \"Hello\", a) ]", ast.toString());
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "load(this) 2 3 *$$ \"Hello\" load(a) call($,f($$$)) ", buf.toString());
    }

}
