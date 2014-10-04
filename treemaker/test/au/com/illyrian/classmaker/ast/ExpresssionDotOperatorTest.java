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

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.types.Type;
import junit.framework.TestCase;

public class ExpresssionDotOperatorTest extends TestCase
{
    MockExpressionIfc buf = new MockExpressionIfc();
    AstExpressionFactory ast = new AstExpressionFactory(buf);

    public void testFieldGet_xyza()
    {
        ast.name("x");
        ast.name("y");
        ast.dot();
        ast.name("z");
        ast.dot();
        ast.name("a");
        ast.dot();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "load(x) load($,y) load($,z) load($,a) ", buf.toString());
    }

    public void testStaticGet_Xyza()
    {
        ast.name("Object");
        ast.name("y");
        ast.dot();
        ast.name("z");
        ast.dot();
        ast.name("a");
        ast.dot();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "load(\"java.lang.Object\",y) load($,z) load($,a) ", buf.toString());
        assertTrue("Stack does not contain a Type", type == ClassMaker.INT_TYPE);
    }

    public void testStaticGet_JavaLangObject_a()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Object");
        ast.dot();
        ast.name("a");
        ast.dot();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "load(\"java.lang.Object\",a) ", buf.toString());
    }

    public void testDotInc()
    {
        ast.name("x");
        ast.name("y");
        ast.dot();
        ast.name("z");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.inc();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "load(x) load($,y) load($,z) inc($,a) ", buf.toString());
    }

    public void testStaticInc()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Object");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.inc();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "inc(\"java.lang.Object\",a) ", buf.toString());
    }

    public void testDotDec()
    {
        ast.name("x");
        ast.name("y");
        ast.dot();
        ast.name("z");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.dec();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "load(x) load($,y) load($,z) dec($,a) ", buf.toString());
    }

    public void testStaticDec()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Object");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.dec();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "dec(\"java.lang.Object\",a) ", buf.toString());
    }

    public void testDotPostInc()
    {
        ast.name("x");
        ast.name("y");
        ast.dot();
        ast.name("z");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.postinc();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "load(x) load($,y) load($,z) postinc($,a) ", buf.toString());
    }

    public void testStaticPostInc()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Object");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.postinc();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "postinc(\"java.lang.Object\",a) ", buf.toString());
    }

    public void testDotPostDec()
    {
        ast.name("x");
        ast.name("y");
        ast.dot();
        ast.name("z");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.postdec();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "load(x) load($,y) load($,z) postdec($,a) ", buf.toString());
    }

    public void testStaticPostDec()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Object");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.postdec();
        Type type = ast.resolve();
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong stack contents", "postdec(\"java.lang.Object\",a) ", buf.toString());
    }

    public void testUnknownPathDotException()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Unknown");
        ast.dot();
        ast.name("a");
        ast.dot();
        try {
            ast.resolve();
            fail("Should throw ClassMakerException: " + ast);
        } catch (ClassMakerException e)
        {
            // Success
        }
    }

    public void testUnknownPathIncException()
    {
        ast.name("java");
        ast.name("lang");
        ast.dot();
        ast.name("Unknown");
        ast.dot();
        ast.name("a");
        ast.dot();
        ast.inc();
        try {
            ast.resolve();
            fail("Should throw ClassMakerException: " + ast);
        } catch (ClassMakerException e)
        {
            // Success
        }
    }

}
