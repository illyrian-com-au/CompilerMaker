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

package au.com.illyrian.classmaker;

import org.mozilla.classfile.ByteCode;
import org.mozilla.classfile.SimpleClassLoader;

public class MakerConditionalTest extends ClassMakerTestCase implements ByteCode
{
    ClassMaker maker;
    SimpleClassLoader loader = new SimpleClassLoader();
    ClassMakerFactory factory;

    public interface Eval
    {
        boolean eval();
    }

    public interface Unary
    {
        int unary(int a);
    }

    public interface BooleanInt
    {
        boolean test(int a, int b);
    }

    public interface UnaryLong
    {
        int unary(long a);
    }

    public interface BooleanLong
    {
        boolean test(long a, long b);
    }

    public interface UnaryFloat
    {
        int unary(float a);
    }

    public interface BooleanFloat
    {
        boolean test(float a, float b);
    }

    public interface UnaryDouble
    {
        int unary(double a);
    }

    public interface BooleanDouble
    {
        boolean test(double a, double b);
    }

    public interface Binary
    {
        int binary(int x, int y);
    }

    public void setUp()
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
    }

    // Generate default constructor
    public void defaultConstructor() throws Exception
    {
        maker.Method("<init>", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public void testNotTrue() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/MakerConditionalTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/MakerConditionalTest$Eval");
        defaultConstructor();
        maker.Method("eval", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Not(maker.Literal(true)));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval) myClass.newInstance();
        assertFalse("Not(true) failed", exec.eval());
    }

    public void testNotFalse() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/MakerConditionalTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/MakerConditionalTest$Eval");
        defaultConstructor();
        maker.Method("eval", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Not(maker.Literal(false)));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval) myClass.newInstance();
        assertTrue("Not(false) failed", exec.eval());
    }

    public void testTrue() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/MakerConditionalTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/MakerConditionalTest$Eval");
        defaultConstructor();
        maker.Method("eval", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Literal(true));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval) myClass.newInstance();
        assertTrue("Should return true", exec.eval());
    }

    public void testFalse() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/MakerConditionalTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/MakerConditionalTest$Eval");
        defaultConstructor();
        maker.Method("eval", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Literal(false));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval) myClass.newInstance();
        assertFalse("Should return false", exec.eval());
    }

    public void testGTInt() throws Exception
    {
        maker.Implements(BooleanInt.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.GT(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanInt cmp =  (BooleanInt) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
    }

    public void testGEInt() throws Exception
    {
        maker.Implements(BooleanInt.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.GE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanInt cmp =  (BooleanInt) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
    }

    public void testLEInt() throws Exception
    {
        maker.Implements(BooleanInt.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.LE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanInt cmp =  (BooleanInt) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
    }

    public void testLTInt() throws Exception
    {
        maker.Implements(BooleanInt.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.LT(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanInt cmp =  (BooleanInt) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
    }

    public void testEQInt() throws Exception
    {
        maker.Implements(BooleanInt.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.EQ(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanInt cmp =  (BooleanInt) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
    }

    public void testNEInt() throws Exception
    {
        maker.Implements(BooleanInt.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.NE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanInt cmp =  (BooleanInt) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
    }

    public void testGTLong() throws Exception
    {
        maker.Implements(BooleanLong.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.LONG_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.GT(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
    }

    public void testGELong() throws Exception
    {
        maker.Implements(BooleanLong.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.LONG_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.GE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
    }

    public void testEQLong() throws Exception
    {
        maker.Implements(BooleanLong.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.LONG_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.EQ(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
    }

    public void testNELong() throws Exception
    {
        maker.Implements(BooleanLong.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.LONG_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.NE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
    }

    public void testLELong() throws Exception
    {
        maker.Implements(BooleanLong.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.LONG_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.LE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
    }

    public void testLTLong() throws Exception
    {
        maker.Implements(BooleanLong.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.LONG_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.LT(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
    }

    public void testGTFloat() throws Exception
    {
        maker.Implements(BooleanFloat.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.GT(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testGEFloat() throws Exception
    {
        maker.Implements(BooleanFloat.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.GE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testEQFloat() throws Exception
    {
        maker.Implements(BooleanFloat.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.EQ(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testNEFloat() throws Exception
    {
        maker.Implements(BooleanFloat.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.NE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
        assertTrue(cmp.test(Float.NaN,10.0f));
        assertTrue(cmp.test(5.0f,Float.NaN));
        assertTrue(cmp.test(Float.NaN,Float.NaN));
    }

    public void testLEFloat() throws Exception
    {
        maker.Implements(BooleanFloat.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.LE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testLTFloat() throws Exception
    {
        maker.Implements(BooleanFloat.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.LT(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testGTDouble() throws Exception
    {
        maker.Implements(BooleanDouble.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.GT(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanDouble cmp =  (BooleanDouble) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
        assertFalse(cmp.test(Double.NaN,10.0));
        assertFalse(cmp.test(5.0,Double.NaN));
        assertFalse(cmp.test(Double.NaN,Double.NaN));
    }

    public void testGEDouble() throws Exception
    {
        maker.Implements(BooleanDouble.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.GE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanDouble cmp =  (BooleanDouble) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
        assertFalse(cmp.test(Double.NaN,10.0));
        assertFalse(cmp.test(5.0,Double.NaN));
        assertFalse(cmp.test(Double.NaN,Double.NaN));
    }

    public void testEQDouble() throws Exception
    {
        maker.Implements(BooleanDouble.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.EQ(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanDouble cmp =  (BooleanDouble) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
        assertFalse(cmp.test(Double.NaN,10.0));
        assertFalse(cmp.test(5.0,Double.NaN));
        assertFalse(cmp.test(Double.NaN,Double.NaN));
    }

    public void testNEDouble() throws Exception
    {
        maker.Implements(BooleanDouble.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.NE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanDouble cmp =  (BooleanDouble) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
        assertTrue(cmp.test(Double.NaN,10.0));
        assertTrue(cmp.test(5.0,Double.NaN));
        assertTrue(cmp.test(Double.NaN,Double.NaN));
    }

    public void testLEDouble() throws Exception
    {
        maker.Implements(BooleanDouble.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.LE(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanDouble cmp =  (BooleanDouble) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
        assertFalse(cmp.test(Double.NaN,10.0));
        assertFalse(cmp.test(5.0,Double.NaN));
        assertFalse(cmp.test(Double.NaN,Double.NaN));
    }

    public void testLTDouble() throws Exception
    {
        maker.Implements(BooleanDouble.class);
        defaultConstructor();
        maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.LT(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanDouble cmp =  (BooleanDouble) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
        assertFalse(cmp.test(Double.NaN,10.0));
        assertFalse(cmp.test(5.0,Double.NaN));
        assertFalse(cmp.test(Double.NaN,Double.NaN));
    }

    public void testConditionException() throws Exception
    {
        // MemberField
        maker.Method("eval", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
        maker.Begin();
        try {
            maker.Not(maker.Literal(1));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot Not type byte", ex.getMessage());
        }
    }

    public void testLTBooleanException() throws Exception
    {
        try {
            maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
            maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Begin();
            maker.Return(maker.LT(maker.Get("a"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot LT type boolean with boolean", ex.getMessage());
        }
    }

    public void testLEBooleanException() throws Exception
    {
        try {
            maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
            maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Begin();
            maker.Return(maker.LE(maker.Get("a"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot LE type boolean with boolean", ex.getMessage());
        }
    }

    public void testGTBooleanException() throws Exception
    {
        try {
            maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
            maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Begin();
            maker.Return(maker.GT(maker.Get("a"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot GT type boolean with boolean", ex.getMessage());
        }
    }

    public void testGEBooleanException() throws Exception
    {
        try {
            maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
            maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Begin();
            maker.Return(maker.GE(maker.Get("a"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot GE type boolean with boolean", ex.getMessage());
        }
    }

    public void testEQException() throws Exception
    {
        try {
            maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
            maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
            maker.Begin();
            maker.Return(maker.EQ(maker.Get("a"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot EQ type boolean with int", ex.getMessage());
        }
    }

    public void testNEException() throws Exception
    {
        try {
            maker.Method("test", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
            maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, 0);
            maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
            maker.Begin();
            maker.Return(maker.NE(maker.Get("a"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot NE type boolean with int", ex.getMessage());
        }
    }
}
