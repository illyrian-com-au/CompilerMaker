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

import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Value;

public class MakerIntegerTest extends ClassMakerTestCase implements ByteCode
{
    ClassMaker maker;
    ClassMakerFactory factory;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
    }

    // Generate default constructor
    public void defaultConstructor() throws Exception
    {
        maker.Method("<init>", void.class, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }


    public interface Run
    {
        void run();
    }

    public interface Eval
    {
        int eval();
    }

    public interface Unary
    {
        int unary(int a);
    }

    public interface ByteUnary
    {
        byte unary(byte a);
    }

    public interface ShortUnary
    {
        short unary(short a);
    }

    public interface CharUnary
    {
        char unary(char a);
    }

    public interface LongUnary
    {
        long unary(long a);
    }

    public interface Binary
    {
        int binary(int x, int y);
    }

    public interface ByteBinary
    {
        byte binary(byte x, byte y);
    }

    public interface ShortBinary
    {
        short binary(short x, short y);
    }

    public interface CharBinary
    {
        char binary(char x, char y);
    }

    public interface LongBinary
    {
        long binary(long x, long y);
    }

    public void testRun() throws Exception
    {
        maker.Implements(Run.class);
        defaultConstructor();
        maker.Declare("id", int.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        // set this.id to 2
        maker.Set(maker.This(), "id", maker.Literal(2));
        maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Run exec = (Run) myClass.newInstance();

        assertEquals("myObj.id should be 0", 0, getIntField(myClass, exec, "id"));
        exec.run();
        assertEquals("myObj.id should be 2", 2, getIntField(myClass, exec, "id"));
    }

    public void testEval() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Declare("id", int.class, ACC_PUBLIC);
        //maker.startMethod("eval", null, "I", (short) (ACC_PUBLIC));
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();

        // set id to 4
        maker.Set(maker.This(), "id", maker.Literal(4));

        // Return 5
        maker.Return(maker.Literal(5));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        int id =getIntField(myClass, exec, "id");
        assertEquals("myObj.id should be 0", 0, id);

        id = exec.eval();
        assertEquals("exec.eval() should be 5", 5, id);

        id = getIntField(myClass, exec, "id");
        assertEquals("myObj.id should be 4", 4, id);

    }

    // Generate public int eval()
    public Eval constClass(int value) throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Literal(value));
        maker.End();

        Class myClass = maker.defineClass();
        return (Eval)myClass.newInstance();
    }

    public void testConstM1() throws Exception
    {
        Eval exec = constClass(-1);
        assertEquals("Wrong const value", -1, exec.eval());
    }

    public void printBytes(byte[] array)
    {
        for (int i=0; i<array.length; i++)
        {
            int ch = toInt(array[i]);
           System.out.print(ch + " ");
           if (i%20 == 0) System.out.println();
        }
    }

    public int toInt(byte b)
    {
        return (b < 0) ? (b + 256) : b;
    }

    public void testConst0() throws Exception
    {
        Eval exec = constClass(0);
        assertEquals("Wrong const value", 0, exec.eval());
    }

    public void testConstMaxInt() throws Exception
    {
        Eval exec = constClass(Integer.MAX_VALUE);
        assertEquals("Wrong const value", Integer.MAX_VALUE, exec.eval());
    }

    public void testConstMinInt() throws Exception
    {
        Eval exec = constClass(Integer.MIN_VALUE);
        assertEquals("Wrong const value", Integer.MIN_VALUE, exec.eval());
    }

    public void testConst1() throws Exception
    {
        Eval exec = constClass(1);
        assertEquals("Wrong const value", 1, exec.eval());
    }

    public void testConst2() throws Exception
    {
        Eval exec = constClass(2);
        assertEquals("Wrong const value", 2, exec.eval());
    }

    public void testConst3() throws Exception
    {
        Eval exec = constClass(3);
        assertEquals("Wrong const value", 3, exec.eval());
    }

    public void testConst4() throws Exception
    {
        Eval exec = constClass(4);
        assertEquals("Wrong const value", 4, exec.eval());
    }

    public void testConst5() throws Exception
    {
        Eval exec = constClass(5);
        assertEquals("Wrong const value", 5, exec.eval());
    }

    public void testSetInt() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Declare("value", int.class, ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
           maker.Declare("a", int.class, 0);
           maker.Set(maker.This(), "value", maker.Assign("a", maker.Get("x")));
           maker.Return(maker.Get("a"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Set failed", 6, exec.unary(6));
        assertEquals("Set values failed", 6, getIntField(myClass, exec, "value"));
    }

    public void testSetLong() throws Exception
    {
        maker.Implements(LongUnary.class);
        defaultConstructor();

        maker.Declare("value", long.class, ACC_PUBLIC);

        maker.Method("unary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Begin();
          maker.Declare("a", long.class, 0);
          maker.Set(maker.This(), "value", maker.Assign("a", maker.Get("x")));
          maker.Return(maker.Get("a"));
        maker.End();

        Class myClass = maker.defineClass();
        LongUnary exec = (LongUnary)myClass.newInstance();

        assertEquals("Set failed", 6L, exec.unary(6L));
        assertEquals("Set values failed", 6L, getLongField(myClass, exec, "value"));
    }

    public void testSetClassInt() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Declare("value", int.class, ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
           maker.Return(maker.Assign(maker.This(), "value", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Set failed", 6, exec.unary(6));
        assertEquals("Set values failed", 6, getIntField(myClass, exec, "value"));
    }

    public void testSetClassLong() throws Exception
    {
        maker.Implements(LongUnary.class);
        defaultConstructor();

        maker.Declare("value", long.class, ACC_PUBLIC);

        maker.Method("unary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Begin();
           maker.Return(maker.Assign(maker.This(), "value", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        LongUnary exec = (LongUnary)myClass.newInstance();

        assertEquals("Set failed", 6L, exec.unary(6L));
        assertEquals("Set values failed", 6L, getLongField(myClass, exec, "value"));
    }

    public void testSetStaticInt() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Declare("value", int.class, ACC_STATIC | ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
           maker.Return(maker.Assign(maker.getFullyQualifiedClassName(), "value", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Set failed", 6, exec.unary(6));
        assertEquals("Set values failed", 6, getIntField(myClass, exec, "value"));
    }

    public void testSetStaticLong() throws Exception
    {
        maker.Implements(LongUnary.class);
        defaultConstructor();

        maker.Declare("value", long.class, ACC_STATIC | ACC_PUBLIC);

        maker.Method("unary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Begin();
           maker.Return(maker.Assign(maker.getFullyQualifiedClassName(), "value", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        LongUnary exec = (LongUnary)myClass.newInstance();

        assertEquals("Set failed", 6L, exec.unary(6L));
        assertEquals("Set values failed", 6L, getLongField(myClass, exec, "value"));
    }

    public void testIncrement() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.Return(maker.Inc("x"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        int id = exec.unary(6);
        assertEquals("Increment failed", 7, id);
    }

    public void testIncrementWide() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.getGen().incrementLocal(maker.lookupLocal(0), 500); // Increment 1st local by wide increment
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        int id = exec.unary(6);
        assertEquals("Increment failed", 506, id);
    }

    public void testDec() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.Return(maker.Dec("x"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        int id = exec.unary(6);
        assertEquals("Decrement failed", 5, id);
    }

    public void testIntNegate() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.Return(maker.Neg(maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();
        assertEquals("Negate failed", -6, exec.unary(6));
    }

    public void testByteNegate() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Declare("x", byte.class, ACC_PUBLIC);

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Neg(maker.Get(maker.This(), "x")));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();
        setByteField(myClass, exec, "x", 6);
        assertEquals("Negate failed", -6, exec.eval());
    }

    public void testShortNegate() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Declare("x", short.class, ACC_PUBLIC);

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Neg(maker.Get(maker.This(), "x")));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();
        setShortField(myClass, exec, "x", 6);
        assertEquals("Negate failed", -6, exec.eval());
    }

    public void testLongNegate() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", long.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        maker.Set(maker.This(), "x", maker.Neg(maker.Get(maker.This(), "x")));
        maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();
        setLongField(myClass, exec, "x", 6);
        exec.run();
        assertEquals("Negate failed", -6, getLongField(myClass, exec, "x"));
    }

    public void testIntToByte() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get("x"), byte.class));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Convert Integer to Byte failed", 64, exec.unary(64));
        assertEquals("Convert Integer to Byte failed", -64, exec.unary(-64));
        assertEquals("Convert Integer to Byte failed", 122, exec.unary(-134));
        assertEquals("Convert Integer to Byte failed", -122, exec.unary(134));
    }

    public void testToPrimitiveException() throws Exception
    {

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", Runnable.class, 0);
        maker.Begin();
        try {
            maker.Cast(maker.Get("x"), byte.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot cast from type java.lang.Runnable to type byte", ex.getMessage());
        }
        try {
            maker.Cast(maker.Get("x"), char.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot cast from type java.lang.Runnable to type char", ex.getMessage());
        }
        try {
            maker.Cast(maker.Get("x"), short.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot cast from type java.lang.Runnable to type short", ex.getMessage());
        }
        try {
            maker.Cast(maker.Get("x"), int.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot cast from type java.lang.Runnable to type int", ex.getMessage());
        }
        try {
            maker.Cast(maker.Get("x"), long.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot cast from type java.lang.Runnable to type long", ex.getMessage());
        }
        try {
            maker.Cast(maker.Get("x"), float.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot cast from type java.lang.Runnable to type float", ex.getMessage());
        }
        try {
            maker.Cast(maker.Get("x"), double.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot cast from type java.lang.Runnable to type double", ex.getMessage());
        }

        maker.Declare("b", boolean.class, 0);
        try {
            maker.Neg(maker.Get("b"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot Negate type boolean", ex.getMessage());
        }
    }

    public void testIntToChar() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get("x"), char.class));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Convert Integer to Char failed", 64, exec.unary(64));
        assertEquals("Convert Integer to Char failed", 65472, exec.unary(-64));
        assertEquals("Convert Integer to Char failed", 65402, exec.unary(-134));
        assertEquals("Convert Integer to Char failed", 134, exec.unary(134));
        assertEquals("Convert Integer to Char failed", 64321, exec.unary(64321));
        assertEquals("Convert Integer to Char failed", 1215, exec.unary(-64321));
    }

    public void testIntToShort() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get("x"), short.class));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Convert Integer to Short failed", 64, exec.unary(64));
        assertEquals("Convert Integer to Short failed", -64, exec.unary(-64));
        assertEquals("Convert Integer to Short failed", -134, exec.unary(-134));
        assertEquals("Convert Integer to Short failed", 134, exec.unary(134));
        assertEquals("Convert Integer to Short failed", -1215, exec.unary(64321));
        assertEquals("Convert Integer to Short failed", 1215, exec.unary(-64321));
    }

    public void testLongToInt() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Declare("id", long.class, ACC_PUBLIC);
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get(maker.This(), "id"), int.class));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval) myClass.newInstance();

        setLongField(myClass, exec, "id", 64L);
        assertEquals("Convert Integer to Byte failed", 64, exec.eval());
        setLongField(myClass, exec, "id", -64L);
        assertEquals("Convert Integer to Byte failed", -64, exec.eval());
    }

    public void testDoubleToInt() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();

        maker.Declare("id", double.class, ACC_PUBLIC);
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get(maker.This(), "id"), int.class));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval) myClass.newInstance();

        setDoubleField(myClass, exec, "id", 64.9d);
        assertEquals("Convert Integer to Byte failed", 64, exec.eval());
        setDoubleField(myClass, exec, "id", -64.9d);
        assertEquals("Convert Integer to Byte failed", -64, exec.eval());
    }

    public void testFloatToInt() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();

        maker.Declare("id", float.class, ACC_PUBLIC);
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get(maker.This(), "id"), int.class));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval) myClass.newInstance();

        setFloatField(myClass, exec, "id", 64.9f);
        assertEquals("Convert Integer to Byte failed", 64, exec.eval());
        setFloatField(myClass, exec, "id", -64.9f);
        assertEquals("Convert Integer to Byte failed", -64, exec.eval());
    }

    public void testAddTwo() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Declare("id", float.class, ACC_PUBLIC);
        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.Return(maker.Add(maker.Get("x"), maker.Literal(2)));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        int id = exec.unary(6);
        assertEquals("Add Two failed", 8, id);
    }

    public void testAddBytes() throws Exception
    {
        maker.Implements(ByteUnary.class);
        defaultConstructor();

        maker.Method("unary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Begin();
        maker.Return(maker.Cast(maker.Add(maker.Get("x"), maker.Literal((byte)64)), byte.class));
        maker.End();

        Class myClass = maker.defineClass();
        ByteUnary exec = (ByteUnary)myClass.newInstance();

        assertEquals("Add byte failed", 70, exec.unary((byte)6));
        assertEquals("Add byte failed", -122, exec.unary((byte)70));
        assertEquals("Add byte failed", -64, exec.unary((byte)-128));
        assertEquals("Add byte failed", 76, exec.unary((byte)-500));
        assertEquals("Add byte failed", 52, exec.unary((byte)500));
    }

    public void testSubByte64() throws Exception
    {
        maker.Implements(ByteUnary.class);
        defaultConstructor();

        maker.Method("unary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Begin();
        maker.Return(maker.Cast(maker.Add(maker.Get("x"), maker.Literal((byte)-64)), byte.class));
        maker.End();

        Class myClass = maker.defineClass();
        ByteUnary exec = (ByteUnary)myClass.newInstance();

        assertEquals("Add byte failed", -70, exec.unary((byte)-6));
        assertEquals("Add byte failed", 122, exec.unary((byte)-70));
        assertEquals("Add byte failed", 63, exec.unary((byte)127));
        assertEquals("Add byte failed", -76, exec.unary((byte)500));
        assertEquals("Add byte failed", -52, exec.unary((byte)-500));
    }

    public void testAddShort() throws Exception
    {
        // Short: -32768 .. 32767
        maker.Implements(ShortUnary.class);
        defaultConstructor();

        maker.Method("unary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Begin();
        maker.Return(maker.Cast(maker.Add(maker.Get("x"), maker.Literal((short)10000)), short.class));
        maker.End();

        Class myClass = maker.defineClass();
        ShortUnary exec = (ShortUnary)myClass.newInstance();

        assertEquals("Add byte failed", 16000, exec.unary((short)6000));
        assertEquals("Add byte failed", Short.MIN_VALUE, exec.unary((short)22768));
        assertEquals("Add byte failed", -22768, exec.unary(Short.MIN_VALUE));
        assertEquals("Add byte failed", -31248, exec.unary((short)-500000));
        assertEquals("Add byte failed", -14288, exec.unary((short)500000));
    }

    public void testSubShort() throws Exception
    {
        // Short: -32768 .. 32767
        maker.Implements(ShortUnary.class);
        defaultConstructor();

        maker.Method("unary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Begin();
        maker.Return(maker.Cast(maker.Add(maker.Get("x"), maker.Literal((short)-10000)), short.class));
        maker.End();

        Class myClass = maker.defineClass();
        ShortUnary exec = (ShortUnary)myClass.newInstance();

        assertEquals("Add byte failed", -16000, exec.unary((short)-6000));
        assertEquals("Add byte failed", Short.MAX_VALUE, exec.unary((short)-22769));
        assertEquals("Add byte failed", 22767, exec.unary(Short.MAX_VALUE));
        assertEquals("Add byte failed", 31248, exec.unary((short)500000));
        assertEquals("Add byte failed", 14288, exec.unary((short)-500000));
    }

    // public int unary(int a)
    public void binaryBegin() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
    }

    // public int unary(int a)
    public Binary binaryEnd() throws Exception
    {
        maker.End();

        Class myClass = maker.defineClass();
        return (Binary)myClass.newInstance();
    }

    //# Add
    public void testIntAddOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.Add(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("2 + 3 failed", 5, exec.binary(2, 3));
        assertEquals("2 + -3 failed", -1, exec.binary(2, -3));
        assertEquals("-2 + 3 failed", 1, exec.binary(-2, 3));
        assertEquals("-2 + -3 failed", -5, exec.binary(-2, -3));
        assertEquals("33m + 33m failed", 66000000, exec.binary(33000000, 33000000));
        assertEquals("MAX_INT + 1 failed", Integer.MIN_VALUE, exec.binary(Integer.MAX_VALUE, 1));
    }

    public void testLongAddOperator() throws Exception
    {
        maker.Implements(LongBinary.class);
        defaultConstructor();

        maker.Method("binary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Declare("y", long.class, 0);
        maker.Begin();
        maker.Return(maker.Add(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        LongBinary exec = (LongBinary)myClass.newInstance();

        assertEquals("2 + 3 failed", 5L, exec.binary(2L, 3L));
        assertEquals("2 + -3 failed", -1L, exec.binary(2L, -3L));
        assertEquals("-2 + 3 failed", 1L, exec.binary(-2L, 3L));
        assertEquals("-2 + -3 failed", -5L, exec.binary(-2L, -3L));
        assertEquals("33 + 33 failed", 66000000000000L, exec.binary(33000000000000L, 33000000000000L));
        assertEquals("MAX_LONG + 1 failed", Long.MIN_VALUE, exec.binary(Long.MAX_VALUE, 1));
    }

    public void testByteAddOperator() throws Exception
    {
        maker.Implements(ByteBinary.class);
        defaultConstructor();

        maker.Method("binary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Declare("y", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveAdd(op1.toPrimitive(), op2.toPrimitive()).getValue());
       }
        maker.End();

        Class myClass = maker.defineClass();
        ByteBinary exec = (ByteBinary)myClass.newInstance();

        assertEquals("2 + 3 failed", (byte)5, exec.binary((byte)2, (byte)3));
        assertEquals("2 + -3 failed", (byte)-1, exec.binary((byte)2, (byte)-3));
        assertEquals("-2 + 3 failed", (byte)1, exec.binary((byte)-2, (byte)3));
        assertEquals("-2 + -3 failed", (byte)-5, exec.binary((byte)-2, (byte)-3));
        assertEquals("MAX_INT + 1 failed", Byte.MIN_VALUE, exec.binary(Byte.MAX_VALUE, (byte)1));
    }

    public void testShortAddOperator() throws Exception
    {
        maker.Implements(ShortBinary.class);
        defaultConstructor();

        maker.Method("binary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Declare("y", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveAdd(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ShortBinary exec = (ShortBinary)myClass.newInstance();

        assertEquals("2 + 3 failed", (short)5, exec.binary((short)2, (short)3));
        assertEquals("2 + -3 failed", (short)-1, exec.binary((short)2, (short)-3));
        assertEquals("-2 + 3 failed", (short)1, exec.binary((short)-2, (short)3));
        assertEquals("-2 + -3 failed", (short)-5, exec.binary((short)-2, (short)-3));
        assertEquals("33000 + 33000 failed", (short)66000, exec.binary((short)33000, (short)33000));
        assertEquals("MAX_INT + 1 failed", Short.MIN_VALUE, exec.binary(Short.MAX_VALUE, (short)1));
    }

    public void testCharAddOperator() throws Exception
    {
        maker.Implements(CharBinary.class);
        defaultConstructor();

        maker.Method("binary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Declare("y", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveAdd(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        CharBinary exec = (CharBinary)myClass.newInstance();

        assertEquals("7 + 3 failed",   (char)10, exec.binary((char)7, (char)3));
        assertEquals("21 + 3 failed",  (char)24, exec.binary((char)21, (char)3));
        assertEquals("MAX_CHAR + 1 failed", Character.MIN_VALUE, exec.binary(Character.MAX_VALUE, (char)1));
    }

    //# Subt
    public void testIntSubtractOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
        maker.Return(maker.Subt(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("2 - 3 failed", -1, exec.binary(2, 3));
        assertEquals("2 - -3 failed", 5, exec.binary(2, -3));
        assertEquals("-2 - 3 failed", -5, exec.binary(-2, 3));
        assertEquals("-2 - -3 failed", 1, exec.binary(-2, -3));
        assertEquals("33m - 33m failed", 0, exec.binary(33000000, 33000000));
        assertEquals("MIN_INT - 1 failed", Integer.MAX_VALUE, exec.binary(Integer.MIN_VALUE, 1));
    }

    public void testLongSubtractOperator() throws Exception
    {
        maker.Implements(LongBinary.class);
        defaultConstructor();

        maker.Method("binary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Declare("y", long.class, 0);
        maker.Begin();
        maker.Return(maker.Subt(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        LongBinary exec = (LongBinary)myClass.newInstance();

        assertEquals("2 - 3 failed", -1L, exec.binary(2L, 3L));
        assertEquals("2 - -3 failed", 5L, exec.binary(2L, -3L));
        assertEquals("-2 - 3 failed", -5L, exec.binary(-2L, 3L));
        assertEquals("-2 - -3 failed", 1L, exec.binary(-2L, -3L));
        assertEquals("33m - 33m failed", 0L, exec.binary(33000000L, 33000000L));
        assertEquals("MIN_LONG - 1 failed", Long.MAX_VALUE, exec.binary(Long.MIN_VALUE, 1));
    }

    public void testByteSubtractOperator() throws Exception
    {
        maker.Implements(ByteBinary.class);
        defaultConstructor();

        maker.Method("binary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Declare("y", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveSubt(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ByteBinary exec = (ByteBinary)myClass.newInstance();

        assertEquals("2 - 3 failed", (byte)-1, exec.binary((byte)2, (byte)3));
        assertEquals("2 - -3 failed", (byte)5, exec.binary((byte)2, (byte)-3));
        assertEquals("-2 - 3 failed", (byte)-5, exec.binary((byte)-2, (byte)3));
        assertEquals("-2 - -3 failed", (byte)1, exec.binary((byte)-2, (byte)-3));
        assertEquals("MIN_INT - 1 failed", Byte.MAX_VALUE, exec.binary(Byte.MIN_VALUE, (byte)1));
    }

    public void testShortSubtractOperator() throws Exception
    {
        maker.Implements(ShortBinary.class);
        defaultConstructor();

        maker.Method("binary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Declare("y", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveSubt(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ShortBinary exec = (ShortBinary)myClass.newInstance();

        assertEquals("2 - 3 failed", (short)-1, exec.binary((short)2, (short)3));
        assertEquals("2 - -3 failed", (short)5, exec.binary((short)2, (short)-3));
        assertEquals("-2 - 3 failed", (short)-5, exec.binary((short)-2, (short)3));
        assertEquals("-2 - -3 failed", (short)1, exec.binary((short)-2, (short)-3));
        assertEquals("33m - 33m failed", (short)0, exec.binary((short)33000000, (short)33000000));
        assertEquals("MIN_INT - 1 failed", Short.MAX_VALUE, exec.binary(Short.MIN_VALUE, (short)1));
    }

    public void testCharSubtractOperator() throws Exception
    {
        maker.Implements(CharBinary.class);
        defaultConstructor();

        maker.Method("binary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Declare("y", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveSubt(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        CharBinary exec = (CharBinary)myClass.newInstance();

        assertEquals("7 - 3 failed",   (char)4, exec.binary((char)7, (char)3));
        assertEquals("21 - 3 failed",  (char)18, exec.binary((char)21, (char)3));
        assertEquals("MIN_CHAR - 1 failed", Character.MAX_VALUE, exec.binary(Character.MIN_VALUE, (char)1));
    }

    //# Mult
    public void testIntMultiplyOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.Mult(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("2 * 3 failed", 6, exec.binary(2, 3));
        assertEquals("2 * -3 failed", -6, exec.binary(2, -3));
        assertEquals("-2 * 3 failed", -6, exec.binary(-2, 3));
        assertEquals("-2 * -3 failed", 6, exec.binary(-2, -3));
    }

    public void testLongMultiplyOperator() throws Exception
    {
        maker.Implements(LongBinary.class);
        defaultConstructor();

        maker.Method("binary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Declare("y", long.class, 0);
        maker.Begin();
        maker.Return(maker.Mult(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        LongBinary exec = (LongBinary)myClass.newInstance();

        assertEquals("2 * 3 failed", 6L, exec.binary(2L, 3L));
        assertEquals("2 * -3 failed", -6L, exec.binary(2L, -3L));
        assertEquals("-2 * 3 failed", -6L, exec.binary(-2L, 3L));
        assertEquals("-2 * -3 failed", 6L, exec.binary(-2L, -3L));
    }

    public void testByteMultiplyOperator() throws Exception
    {
        maker.Implements(ByteBinary.class);
        defaultConstructor();

        maker.Method("binary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Declare("y", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveMult(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ByteBinary exec = (ByteBinary)myClass.newInstance();

        assertEquals("2 * 3 failed", (byte)6, exec.binary((byte)2, (byte)3));
        assertEquals("2 * -3 failed", (byte)-6, exec.binary((byte)2, (byte)-3));
        assertEquals("-2 * 3 failed", (byte)-6, exec.binary((byte)-2, (byte)3));
        assertEquals("-2 * -3 failed", (byte)6, exec.binary((byte)-2, (byte)-3));
    }

    public void testShortMultiplyOperator() throws Exception
    {
        maker.Implements(ShortBinary.class);
        defaultConstructor();

        maker.Method("binary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Declare("y", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveMult(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ShortBinary exec = (ShortBinary)myClass.newInstance();

        assertEquals("2 * 3 failed",  (short)6, exec.binary((short)2, (short)3));
        assertEquals("2 * -3 failed", (short)-6, exec.binary((short)2, (short)-3));
        assertEquals("-2 * 3 failed", (short)-6, exec.binary((short)-2, (short)3));
        assertEquals("-2 * -3 failed", (short)6, exec.binary((short)-2, (short)-3));
    }

    public void testCharMultiplyOperator() throws Exception
    {
        maker.Implements(CharBinary.class);
        defaultConstructor();

        maker.Method("binary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Declare("y", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveMult(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        CharBinary exec = (CharBinary)myClass.newInstance();

        assertEquals("7 * 3 failed",   (char)21, exec.binary((char)7, (char)3));
        assertEquals("21 * 3 failed",  (char)63, exec.binary((char)21, (char)3));
    }

    //# Div
    public void testIntDivideOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.Div(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("7 / 3 failed",   2, exec.binary(7, 3));
        assertEquals("7 / -3 failed", -2, exec.binary(7, -3));
        assertEquals("-7 / 3 failed", -2, exec.binary(-7, 3));
        assertEquals("-7 / -3 failed", 2, exec.binary(-7, -3));
    }

    public void testLongDivideOperator() throws Exception
    {
        maker.Implements(LongBinary.class);
        defaultConstructor();

        maker.Method("binary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Declare("y", long.class, 0);
        maker.Begin();
        maker.Return(maker.Div(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        LongBinary exec = (LongBinary)myClass.newInstance();

        assertEquals("7 / 3 failed",   2L, exec.binary(7L, 3L));
        assertEquals("7 / -3 failed", -2L, exec.binary(7L, -3L));
        assertEquals("-7 / 3 failed", -2L, exec.binary(-7L, 3L));
        assertEquals("-7 / -3 failed", 2L, exec.binary(-7L, -3L));
    }

    public void testByteDivideOperator() throws Exception
    {
        maker.Implements(ByteBinary.class);
        defaultConstructor();

        maker.Method("binary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Declare("y", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveDiv(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ByteBinary exec = (ByteBinary)myClass.newInstance();

        assertEquals("7 / 3 failed",   (byte)2, exec.binary((byte)7, (byte)3));
        assertEquals("7 / -3 failed", (byte)-2, exec.binary((byte)7, (byte)-3));
        assertEquals("-7 / 3 failed", (byte)-2, exec.binary((byte)-7, (byte)3));
        assertEquals("-7 / -3 failed", (byte)2, exec.binary((byte)-7, (byte)-3));
    }

    public void testShortDivideOperator() throws Exception
    {
        maker.Implements(ShortBinary.class);
        defaultConstructor();

        maker.Method("binary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Declare("y", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveDiv(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ShortBinary exec = (ShortBinary)myClass.newInstance();

        assertEquals("7 / 3 failed",   (short)2, exec.binary((short)7, (short)3));
        assertEquals("7 / -3 failed", (short)-2, exec.binary((short)7, (short)-3));
        assertEquals("-7 / 3 failed", (short)-2, exec.binary((short)-7, (short)3));
        assertEquals("-7 / -3 failed", (short)2, exec.binary((short)-7, (short)-3));
    }

    public void testCharDivideOperator() throws Exception
    {
        maker.Implements(CharBinary.class);
        defaultConstructor();

        maker.Method("binary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Declare("y", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveDiv(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        CharBinary exec = (CharBinary)myClass.newInstance();

        assertEquals("7 % 3 failed",   (char)2, exec.binary((char)7, (char)3));
        assertEquals("21 % 3 failed",  (char)7, exec.binary((char)21, (char)3));
    }

    //# Rem
    public void testRemainderOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.Rem(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("7 % 3 failed",   1, exec.binary(7, 3));
        assertEquals("7 % -3 failed",  1, exec.binary(7, -3));
        assertEquals("-7 % 3 failed", -1, exec.binary(-7, 3));
        assertEquals("-7 % -3 failed",-1, exec.binary(-7, -3));
    }

    public void testLongRemainderOperator() throws Exception
    {
        maker.Implements(LongBinary.class);
        defaultConstructor();

        maker.Method("binary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Declare("y", long.class, 0);
        maker.Begin();
            maker.Return(maker.Rem(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        LongBinary exec = (LongBinary)myClass.newInstance();

        assertEquals("7 % 3 failed",   1L, exec.binary(7L, 3L));
        assertEquals("7 % -3 failed",  1L, exec.binary(7L, -3L));
        assertEquals("-7 % 3 failed", -1L, exec.binary(-7L, 3L));
        assertEquals("-7 % -3 failed",-1L, exec.binary(-7L, -3L));
    }

    public void testByteRemainderOperator() throws Exception
    {
        maker.Implements(ByteBinary.class);
        defaultConstructor();

        maker.Method("binary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Declare("y", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveRem(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ByteBinary exec = (ByteBinary)myClass.newInstance();

        assertEquals("7 % 3 failed",   (byte)1, exec.binary((byte)7, (byte)3));
        assertEquals("7 % -3 failed",  (byte)1, exec.binary((byte)7, (byte)-3));
        assertEquals("-7 % 3 failed", (byte)-1, exec.binary((byte)-7, (byte)3));
        assertEquals("-7 % -3 failed",(byte)-1, exec.binary((byte)-7, (byte)-3));
    }

    public void testShortRemainderOperator() throws Exception
    {
        maker.Implements(ShortBinary.class);
        defaultConstructor();

        maker.Method("binary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Declare("y", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveRem(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ShortBinary exec = (ShortBinary)myClass.newInstance();

        assertEquals("7 % 3 failed",   (short)1, exec.binary((short)7, (short)3));
        assertEquals("7 % -3 failed",  (short)1, exec.binary((short)7, (short)-3));
        assertEquals("-7 % 3 failed", (short)-1, exec.binary((short)-7, (short)3));
        assertEquals("-7 % -3 failed",(short)-1, exec.binary((short)-7, (short)-3));
    }

    public void testCharRemainderOperator() throws Exception
    {
        maker.Implements(CharBinary.class);
        defaultConstructor();

        maker.Method("binary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Declare("y", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveRem(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        CharBinary exec = (CharBinary)myClass.newInstance();

        assertEquals("7 % 3 failed",   (char)1, exec.binary((char)7, (char)3));
        assertEquals("21 % 3 failed",  (char)0, exec.binary((char)21, (char)3));
    }

    public void testMathException() throws Exception
    {
        maker.Method("other", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
        maker.Begin();
        maker.Declare("x", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("o", ClassMakerFactory.OBJECT_TYPE, 0);
        //# Add
        try {
            maker.Add(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot add type boolean to type int", ex.getMessage());
        }
        try {
            maker.Add(maker.Get("a"), maker.Get("a"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot add type boolean to type boolean", ex.getMessage());
        }
        try {
            maker.Add(maker.Get("o"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot add type java.lang.Object to type int", ex.getMessage());
        }
        //# Subt
        try {
            maker.Subt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot subtract type int from type boolean", ex.getMessage());
        }
        try {
            maker.Subt(maker.Get("a"), maker.Get("a"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot subtract type boolean from type boolean", ex.getMessage());
        }
        try {
            maker.Subt(maker.Literal("a"), maker.Get("a"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot subtract type boolean from type java.lang.String", ex.getMessage());
        }
        //# Mult
        try {
            maker.Mult(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot multiply type boolean by type int", ex.getMessage());
        }
        try {
            maker.Mult(maker.Get("a"), maker.Get("a"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot multiply type boolean by type boolean", ex.getMessage());
        }
        try {
            maker.Mult(maker.Literal("a"), maker.Get("a"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot multiply type java.lang.String by type boolean", ex.getMessage());
        }
        //# Div
        try {
            maker.Div(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot divide type boolean by type int", ex.getMessage());
        }
        try {
            maker.Div(maker.Get("a"), maker.Get("a"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot divide type boolean by type boolean", ex.getMessage());
        }
        try {
            maker.Div(maker.Literal("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot divide type java.lang.String by type int", ex.getMessage());
        }
        //# Rem
        try {
            maker.Rem(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot remainder type boolean by type int", ex.getMessage());
        }
        try {
            maker.Rem(maker.Get("a"), maker.Get("a"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot remainder type boolean by type boolean", ex.getMessage());
        }
        try {
            maker.Rem(maker.Literal("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot remainder type java.lang.String by type int", ex.getMessage());
        }
    }

    // Assignment
    public void testAssignment() throws Exception
    {
        maker.Implements(Run.class);
        defaultConstructor();
        maker.Declare("b", byte.class, ACC_PUBLIC);
        maker.Declare("s", short.class, ACC_PUBLIC);
        maker.Declare("c", char.class, ACC_PUBLIC);
        maker.Declare("i", int.class, ACC_PUBLIC);
        maker.Declare("l", long.class, ACC_PUBLIC);
        maker.Declare("f", float.class, ACC_PUBLIC);
        maker.Declare("d", double.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            // Byte assignment
            maker.Set(maker.This(), "b", maker.Literal(30));
            // Short assignment
            maker.Set(maker.This(), "s", maker.Literal(30));
            maker.Set(maker.This(), "s", maker.Literal(3000));
            // Integer assignment
            maker.Set(maker.This(), "i", maker.Literal(30));
            maker.Set(maker.This(), "i", maker.Literal(3000));
            maker.Set(maker.This(), "i", maker.Literal(300000));
            maker.Set(maker.This(), "i", maker.Literal('Z'));
            // Char assignment
            maker.Set(maker.This(), "c", maker.Literal('Z'));
            // Long assignment
            maker.Set(maker.This(), "l", maker.Literal(30));
            maker.Set(maker.This(), "l", maker.Literal(3000));
            maker.Set(maker.This(), "l", maker.Literal(300000));
            maker.Set(maker.This(), "l", maker.Literal(300000000000L));
            maker.Set(maker.This(), "l", maker.Literal('Z'));
            // Float assignment
            maker.Set(maker.This(), "f", maker.Literal(30));
            maker.Set(maker.This(), "f", maker.Literal(3000));
            maker.Set(maker.This(), "f", maker.Literal(300000));
            maker.Set(maker.This(), "f", maker.Literal(300000000000L));
            maker.Set(maker.This(), "f", maker.Literal('Z'));
            maker.Set(maker.This(), "f", maker.Literal(12345.6789F));
            // Double assignment
            maker.Set(maker.This(), "d", maker.Literal(30));
            maker.Set(maker.This(), "d", maker.Literal(3000));
            maker.Set(maker.This(), "d", maker.Literal(300000));
            maker.Set(maker.This(), "d", maker.Literal(300000000000L));
            maker.Set(maker.This(), "d", maker.Literal('Z'));
            maker.Set(maker.This(), "d", maker.Literal(12345.6789F));
            maker.Set(maker.This(), "d", maker.Literal(12345.6789D));

            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        myClass.newInstance();

    }

    // Casting conversion
    public void testCasting() throws Exception
    {
        maker.Implements(Run.class);
        defaultConstructor();
        maker.Declare("b1", byte.class, ACC_PUBLIC);
        maker.Declare("b2", byte.class, ACC_PUBLIC);
        maker.Declare("b3", byte.class, ACC_PUBLIC);
        maker.Declare("b4", byte.class, ACC_PUBLIC);
        maker.Declare("b5", byte.class, ACC_PUBLIC);
        maker.Declare("b6", byte.class, ACC_PUBLIC);
        maker.Declare("b7", byte.class, ACC_PUBLIC);
        maker.Declare("s1", short.class, ACC_PUBLIC);
        maker.Declare("s2", short.class, ACC_PUBLIC);
        maker.Declare("s3", short.class, ACC_PUBLIC);
        maker.Declare("s4", short.class, ACC_PUBLIC);
        maker.Declare("s5", short.class, ACC_PUBLIC);
        maker.Declare("s6", short.class, ACC_PUBLIC);
        maker.Declare("s7", short.class, ACC_PUBLIC);
        maker.Declare("c1", char.class, ACC_PUBLIC);
        maker.Declare("c2", char.class, ACC_PUBLIC);
        maker.Declare("c3", char.class, ACC_PUBLIC);
        maker.Declare("c4", char.class, ACC_PUBLIC);
        maker.Declare("c5", char.class, ACC_PUBLIC);
        maker.Declare("c6", char.class, ACC_PUBLIC);
        maker.Declare("c7", char.class, ACC_PUBLIC);
        maker.Declare("i1", int.class, ACC_PUBLIC);
        maker.Declare("i2", int.class, ACC_PUBLIC);
        maker.Declare("i3", int.class, ACC_PUBLIC);
        maker.Declare("i4", int.class, ACC_PUBLIC);
        maker.Declare("i5", int.class, ACC_PUBLIC);
        maker.Declare("i6", int.class, ACC_PUBLIC);
        maker.Declare("i7", int.class, ACC_PUBLIC);
        maker.Declare("l1", long.class, ACC_PUBLIC);
        maker.Declare("l2", long.class, ACC_PUBLIC);
        maker.Declare("l3", long.class, ACC_PUBLIC);
        maker.Declare("l4", long.class, ACC_PUBLIC);
        maker.Declare("l5", long.class, ACC_PUBLIC);
        maker.Declare("l6", long.class, ACC_PUBLIC);
        maker.Declare("l7", long.class, ACC_PUBLIC);
        maker.Declare("f1", float.class, ACC_PUBLIC);
        maker.Declare("f2", float.class, ACC_PUBLIC);
        maker.Declare("f3", float.class, ACC_PUBLIC);
        maker.Declare("f4", float.class, ACC_PUBLIC);
        maker.Declare("f5", float.class, ACC_PUBLIC);
        maker.Declare("f6", float.class, ACC_PUBLIC);
        maker.Declare("f7", float.class, ACC_PUBLIC);
        maker.Declare("d1", double.class, ACC_PUBLIC);
        maker.Declare("d2", double.class, ACC_PUBLIC);
        maker.Declare("d3", double.class, ACC_PUBLIC);
        maker.Declare("d4", double.class, ACC_PUBLIC);
        maker.Declare("d5", double.class, ACC_PUBLIC);
        maker.Declare("d6", double.class, ACC_PUBLIC);
        maker.Declare("d7", double.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            // Byte assignment
            maker.Set(maker.This(), "b1", maker.Cast(maker.Literal(30), byte.class));
            maker.Set(maker.This(), "b2", maker.Cast(maker.Literal(3000), byte.class));
            maker.Set(maker.This(), "b3", maker.Cast(maker.Literal(300000), byte.class));
            maker.Set(maker.This(), "b4", maker.Cast(maker.Literal(300000000000L), byte.class));
            maker.Set(maker.This(), "b5", maker.Cast(maker.Literal('Z'), byte.class));
            maker.Set(maker.This(), "b6", maker.Cast(maker.Literal(12345.6789F), byte.class));
            maker.Set(maker.This(), "b7", maker.Cast(maker.Literal(12345.6789D), byte.class));
            // Short assignment
            maker.Set(maker.This(), "s1", maker.Cast(maker.Literal(30), short.class));
            maker.Set(maker.This(), "s2", maker.Cast(maker.Literal(3000), short.class));
            maker.Set(maker.This(), "s3", maker.Cast(maker.Literal(300000), short.class));
            maker.Set(maker.This(), "s4", maker.Cast(maker.Literal(300000000000L), short.class));
            maker.Set(maker.This(), "s5", maker.Cast(maker.Literal('Z'), short.class));
            maker.Set(maker.This(), "s6", maker.Cast(maker.Literal(12345.6789F), short.class));
            maker.Set(maker.This(), "s7", maker.Cast(maker.Literal(12345.6789D), short.class));
            // Integer assignment
            maker.Set(maker.This(), "i1", maker.Cast(maker.Literal(30), int.class));
            maker.Set(maker.This(), "i2", maker.Cast(maker.Literal(3000), int.class));
            maker.Set(maker.This(), "i3", maker.Cast(maker.Literal(300000), int.class));
            maker.Set(maker.This(), "i4", maker.Cast(maker.Literal(300000000000L), int.class));
            maker.Set(maker.This(), "i5", maker.Cast(maker.Literal('Z'), int.class));
            maker.Set(maker.This(), "i6", maker.Cast(maker.Literal(12345.6789F), int.class));
            maker.Set(maker.This(), "i7", maker.Cast(maker.Literal(12345.6789D), int.class));
            // Char assignment
            maker.Set(maker.This(), "c1", maker.Cast(maker.Literal(30), char.class));
            maker.Set(maker.This(), "c2", maker.Cast(maker.Literal(3000), char.class));
            maker.Set(maker.This(), "c3", maker.Cast(maker.Literal(300000), char.class));
            maker.Set(maker.This(), "c4", maker.Cast(maker.Literal(300000000000L), char.class));
            maker.Set(maker.This(), "c5", maker.Cast(maker.Literal('Z'), char.class));
            maker.Set(maker.This(), "c6", maker.Cast(maker.Literal(12345.6789F), char.class));
            maker.Set(maker.This(), "c7", maker.Cast(maker.Literal(12345.6789D), char.class));
            // Long assignment
            maker.Set(maker.This(), "l1", maker.Cast(maker.Literal(30), long.class));
            maker.Set(maker.This(), "l2", maker.Cast(maker.Literal(3000), long.class));
            maker.Set(maker.This(), "l3", maker.Cast(maker.Literal(300000), long.class));
            maker.Set(maker.This(), "l4", maker.Cast(maker.Literal(300000000000L), long.class));
            maker.Set(maker.This(), "l5", maker.Cast(maker.Literal('Z'), long.class));
            maker.Set(maker.This(), "l6", maker.Cast(maker.Literal(12345.6789F), long.class));
            maker.Set(maker.This(), "l7", maker.Cast(maker.Literal(12345.6789D), long.class));
            // Float assignment
            maker.Set(maker.This(), "f1", maker.Cast(maker.Literal(30), float.class));
            maker.Set(maker.This(), "f2", maker.Cast(maker.Literal(3000), float.class));
            maker.Set(maker.This(), "f3", maker.Cast(maker.Literal(300000), float.class));
            maker.Set(maker.This(), "f4", maker.Cast(maker.Literal(300000000000L), float.class));
            maker.Set(maker.This(), "f5", maker.Cast(maker.Literal('Z'), float.class));
            maker.Set(maker.This(), "f6", maker.Cast(maker.Literal(12345.6789F), float.class));
            maker.Set(maker.This(), "f7", maker.Cast(maker.Literal(12345.6789D), float.class));
            // Double assignment
            maker.Set(maker.This(), "d1", maker.Cast(maker.Literal(30), double.class));
            maker.Set(maker.This(), "d2", maker.Cast(maker.Literal(3000), double.class));
            maker.Set(maker.This(), "d3", maker.Cast(maker.Literal(300000), double.class));
            maker.Set(maker.This(), "d4", maker.Cast(maker.Literal(300000000000L), double.class));
            maker.Set(maker.This(), "d5", maker.Cast(maker.Literal('Z'), double.class));
            maker.Set(maker.This(), "d6", maker.Cast(maker.Literal(12345.6789F), double.class));
            maker.Set(maker.This(), "d7", maker.Cast(maker.Literal(12345.6789D), double.class));

            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Run exec = (Run)myClass.newInstance();
        exec.run();

        assertEquals((byte)30,            getIntField(myClass, exec, "b1"));
        assertEquals((byte)3000,          getIntField(myClass, exec, "b2"));
        assertEquals((byte)300000,        getIntField(myClass, exec, "b3"));
        assertEquals((byte)300000000000L, getIntField(myClass, exec, "b4"));
        assertEquals((byte)'Z',           getIntField(myClass, exec, "b5"));
        assertEquals((byte)12345.6789F,   getIntField(myClass, exec, "b6"));
        assertEquals((byte)12345.6789D,   getIntField(myClass, exec, "b7"));
        assertEquals((short)30,            getIntField(myClass, exec, "s1"));
        assertEquals((short)3000,          getIntField(myClass, exec, "s2"));
        assertEquals((short)300000,        getIntField(myClass, exec, "s3"));
        assertEquals((short)300000000000L, getIntField(myClass, exec, "s4"));
        assertEquals((short)'Z',           getIntField(myClass, exec, "s5"));
        assertEquals((short)12345.6789F,   getIntField(myClass, exec, "s6"));
        assertEquals((short)12345.6789D,   getIntField(myClass, exec, "s7"));
        assertEquals((int)30,            getIntField(myClass, exec, "i1"));
        assertEquals((int)3000,          getIntField(myClass, exec, "i2"));
        assertEquals((int)300000,        getIntField(myClass, exec, "i3"));
        assertEquals((int)300000000000L, getIntField(myClass, exec, "i4"));
        assertEquals((int)'Z',           getIntField(myClass, exec, "i5"));
        assertEquals((int)12345.6789F,   getIntField(myClass, exec, "i6"));
        assertEquals((int)12345.6789D,   getIntField(myClass, exec, "i7"));
        assertEquals((char)30,            getIntField(myClass, exec, "c1"));
        assertEquals((char)3000,          getIntField(myClass, exec, "c2"));
        assertEquals((char)300000,        getIntField(myClass, exec, "c3"));
        assertEquals((char)300000000000L, getIntField(myClass, exec, "c4"));
        assertEquals((char)'Z',           getIntField(myClass, exec, "c5"));
        assertEquals((char)12345.6789F,   getIntField(myClass, exec, "c6"));
        assertEquals((char)12345.6789D,   getIntField(myClass, exec, "c7"));
        assertEquals((long)30,            getLongField(myClass, exec, "l1"));
        assertEquals((long)3000,          getLongField(myClass, exec, "l2"));
        assertEquals((long)300000,        getLongField(myClass, exec, "l3"));
        assertEquals((long)300000000000L, getLongField(myClass, exec, "l4"));
        assertEquals((long)'Z',           getLongField(myClass, exec, "l5"));
        assertEquals((long)12345.6789F,   getLongField(myClass, exec, "l6"));
        assertEquals((long)12345.6789D,   getLongField(myClass, exec, "l7"));
        assertEquals((float)30,            getFloatField(myClass, exec, "f1"));
        assertEquals((float)3000,          getFloatField(myClass, exec, "f2"));
        assertEquals((float)300000,        getFloatField(myClass, exec, "f3"));
        assertEquals((float)300000000000L, getFloatField(myClass, exec, "f4"));
        assertEquals((float)'Z',           getFloatField(myClass, exec, "f5"));
        assertEquals((float)12345.6789F,   getFloatField(myClass, exec, "f6"));
        assertEquals((float)12345.6789D,   getFloatField(myClass, exec, "f7"));
        assertEquals((double)30,            getDoubleField(myClass, exec, "d1"));
        assertEquals((double)3000,          getDoubleField(myClass, exec, "d2"));
        assertEquals((double)300000,        getDoubleField(myClass, exec, "d3"));
        assertEquals((double)300000000000L, getDoubleField(myClass, exec, "d4"));
        assertEquals((double)'Z',           getDoubleField(myClass, exec, "d5"));
        assertEquals((double)12345.6789F,   getDoubleField(myClass, exec, "d6"));
        assertEquals((double)12345.6789D,   getDoubleField(myClass, exec, "d7"));
    }

    //# Bitwise And
    public void testBitwiseAndOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.And(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("13(1101) and 11(1011) = 9(1001) failed",   9, exec.binary(13, 11));
        assertEquals("0xCF7FAF3F or 0xFCF7FAF3) = 0xCC77AA33) failed",
                        0xCC77AA33, exec.binary(0xCF7FAF3F, 0xFCF7FAF3));
    }

    public void testByteBitwiseAndOperator() throws Exception
    {
        maker.Implements(ByteBinary.class);
        defaultConstructor();

        maker.Method("binary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Declare("y", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveAnd(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ByteBinary exec = (ByteBinary)myClass.newInstance();

        assertEquals("5(0101) and 3(0011) = 1(0001) failed",   (byte)1, exec.binary((byte)5, (byte)3));
        assertEquals("0xCF or 0xFC) = 0xCC) failed",   (byte)0xCC, exec.binary((byte)0xCF, (byte)0xFC));
    }

    public void testShortBitwiseAndOperator() throws Exception
    {
        maker.Implements(ShortBinary.class);
        defaultConstructor();

        maker.Method("binary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Declare("y", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveAnd(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ShortBinary exec = (ShortBinary)myClass.newInstance();

        assertEquals("5(0101) and 3(0011) = 1(0001) failed",   (short)1, exec.binary((short)5, (short)3));
        assertEquals("0xCF7F or 0xFCF7) = 0xCC77) failed",
                        (short)0xCC77, exec.binary((short)0xCF7F, (short)0xFCF7));
    }

    public void testCharBitwiseAndOperator() throws Exception
    {
        maker.Implements(CharBinary.class);
        defaultConstructor();

        maker.Method("binary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Declare("y", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveAnd(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        CharBinary exec = (CharBinary)myClass.newInstance();

        assertEquals("5(0101) and 3(0011) = 1(0001) failed",   (char)1, exec.binary((char)5, (char)3));
        assertEquals("0xCF7F or 0xFCF7) = 0xCC77) failed",
                        (char)0xCC77, exec.binary((char)0xCF7F, (char)0xFCF7));
    }

    public void testLongBitwiseAndOperator() throws Exception
    {
        maker.Implements(LongBinary.class);
        defaultConstructor();

        maker.Method("binary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Declare("y", long.class, 0);
        maker.Begin();
            maker.Return(maker.And(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        LongBinary exec = (LongBinary)myClass.newInstance();

        assertEquals("5(0101) and 3(0011) = 1(0001) failed",   (char)1, exec.binary((char)5, (char)3));
        assertEquals("long bitwise And failed",
                        0xCC77AA33CC77AA33L, exec.binary(0xCF7FAF3FCF7FAF3FL, 0xFCF7FAF3FCF7FAF3L));
    }

    //# Bitwise Xor
    public void testBitwiseXorOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.Xor(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("5(0101) xor 3(0011) = 6(0110) failed",   6, exec.binary(5, 3));
        assertEquals("0xFF0F or 0xF0FF) = 0x0FF0) failed",
                        0x0FF0, exec.binary(0xFF0F, 0xF0FF));
        assertEquals("0xFF0FFF0F or 0xF0FFF0FF) = 0x0FF00FF0) failed",
                        0x0FF00FF0, exec.binary(0xFF0FFF0F, 0xF0FFF0FF));
    }

    public void testByteBitwiseXorOperator() throws Exception
    {
        maker.Implements(ByteBinary.class);
        defaultConstructor();

        maker.Method("binary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Declare("y", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveXor(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ByteBinary exec = (ByteBinary)myClass.newInstance();

        assertEquals("5(0101) or 3(0011) = 6(0110) failed", (byte)6, exec.binary((byte)5, (byte)3));
        assertEquals("0xFF0F or 0xF0FF) = 0x0FF0) failed",
                        (byte)0xF0, exec.binary((byte)0x0F, (byte)0xFF));
    }

    public void testShortBitwiseXorOperator() throws Exception
    {
        maker.Implements(ShortBinary.class);
        defaultConstructor();

        maker.Method("binary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Declare("y", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveXor(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ShortBinary exec = (ShortBinary)myClass.newInstance();

        assertEquals("5(0101) or 3(0011) = 6(0110) failed",   (short)6, exec.binary((short)5, (short)3));
        assertEquals("0xFF0F or 0xF0FF) = 0x0FF0) failed",
                        (short)0x0FF0, exec.binary((short)0xFF0F, (short)0xF0FF));
    }

    public void testCharBitwiseXorOperator() throws Exception
    {
        maker.Implements(CharBinary.class);
        defaultConstructor();

        maker.Method("binary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Declare("y", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveXor(op1.toPrimitive(), op2.toPrimitive()).getValue());
       }
        maker.End();

        Class myClass = maker.defineClass();
        CharBinary exec = (CharBinary)myClass.newInstance();

        assertEquals("5(0101) or 3(0011) = 6(0110) failed",   (char)6, exec.binary((char)5, (char)3));
        assertEquals("0xFF0F or 0xF0FF) = 0x0FF0) failed",
                        (char)0x0FF0, exec.binary((char)0xFF0F, (char)0xF0FF));
    }

    public void testLongBitwiseXorOperator() throws Exception
    {
        maker.Implements(LongBinary.class);
        defaultConstructor();

        maker.Method("binary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Declare("y", long.class, 0);
        maker.Begin();
            maker.Return(maker.Xor(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        LongBinary exec = (LongBinary)myClass.newInstance();

        assertEquals("5(0101) xor 3(0011) = 6(0110) failed",   6L, exec.binary(5L, 3L));
        assertEquals("0xFF0F or 0xF0FF) = 0x0FF0) failed",
                        0x0FF0L, exec.binary(0xFF0FL, 0xF0FFL));
        assertEquals("0xFF0FFF0F or 0xF0FFF0FF) = 0x0FF00FF0) failed",
                        0x0FF00FF0L, exec.binary(0xFF0FFF0FL, 0xF0FFF0FFL));
        assertEquals("0xFF0FFF0FFF0FFF0F or 0xF0FFF0FFF0FFF0FF) = 0x0FF00FF00FF00FF0) failed",
                        0x0FF00FF00FF00FF0L, exec.binary(0xFF0FFF0FFF0FFF0FL, 0xF0FFF0FFF0FFF0FFL));
    }

    //# Bitwise Or
    public void testBitwiseOrOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.Or(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("5(0101) or 3(0011) = 7(0111) failed",   7, exec.binary(5, 3));
        assertEquals("0x0F0F0F0F or 0xF0F0F0F0) = 0xFFFFFFFF) failed",
                        0xFFFFFFFF, exec.binary(0x0F0F0F0F, 0xF0F0F0F0));
    }

    public void testByteBitwiseOrOperator() throws Exception
    {
        maker.Implements(ByteBinary.class);
        defaultConstructor();

        maker.Method("binary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Declare("y", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveOr(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ByteBinary exec = (ByteBinary)myClass.newInstance();

        assertEquals("5(0101) or 3(0011) = 7(0111) failed",   (byte)7, exec.binary((byte)5, (byte)3));
        assertEquals("0x0F0F or 0xF0F0) = 0xFFFF) failed",   (byte)0xFF, exec.binary((byte)0x0F0F, (byte)0xF0F0));
    }

    public void testShortBitwiseOrOperator() throws Exception
    {
        maker.Implements(ShortBinary.class);
        defaultConstructor();

        maker.Method("binary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Declare("y", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveOr(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ShortBinary exec = (ShortBinary)myClass.newInstance();

        assertEquals("5(0101) or 3(0011) = 7(0111) failed",   (short)7, exec.binary((short)5, (short)3));
        assertEquals("0x0F0F0F0F or 0xF0F0F0F0) = 0xFFFFFFFF) failed",
                        (short)0xFFFFFFFF, exec.binary((short)0x0F0F0F0F, (short)0xF0F0F0F0));
    }

    public void testCharBitwiseOrOperator() throws Exception
    {
        maker.Implements(CharBinary.class);
        defaultConstructor();

        maker.Method("binary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Declare("y", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            Value op2 = maker.Get("y");
            maker.Return(maker.primitiveOr(op1.toPrimitive(), op2.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        CharBinary exec = (CharBinary)myClass.newInstance();

        assertEquals("5(0101) or 3(0011) = 7(0111) failed",   (char)7, exec.binary((char)5, (char)3));
        assertEquals("0x0F0F0F0F or 0xF0F0F0F0) = 0xFFFFFFFF) failed",
                        (char)0xFFFFFFFF, exec.binary((char)0x0F0F0F0F, (char)0xF0F0F0F0));
    }

    public void testLongBitwiseOrOperator() throws Exception
    {
        maker.Implements(LongBinary.class);
        defaultConstructor();

        maker.Method("binary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Declare("y", long.class, 0);
        maker.Begin();
            maker.Return(maker.Or(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        LongBinary exec = (LongBinary)myClass.newInstance();

        assertEquals("5(0101) or 3(0011) = 7(0111) failed",   7L, exec.binary(5, 3));
        assertEquals("0x0F0F or 0xF0F0) = 0xFFFF) failed",   0xFFFF, exec.binary(0x0F0F, 0xF0F0));
    }

    public void testByteBitwisePrimitiveInvOperator() throws Exception
    {
        maker.Implements(ByteUnary.class);
        defaultConstructor();

        maker.Method("unary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            maker.Return(maker.primitiveInv(op1.toPrimitive()).getValue());
        }
        maker.End();

        Class myClass = maker.defineClass();
        ByteUnary exec = (ByteUnary)myClass.newInstance();

        assertEquals("-6(1010) = invert 5(0101) failed",   (byte)-6, exec.unary((byte)5));
        assertEquals("0xF0 = invert 0x0F failed",   (byte)0xF0, exec.unary((byte)0x0F));
    }

    public void testShortBitwisePrimitiveInvOperator() throws Exception
    {
        maker.Implements(ShortUnary.class);
        defaultConstructor();

        maker.Method("unary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            maker.Return(maker.primitiveInv(op1.toPrimitive()).getValue());
       }
        maker.End();

        Class myClass = maker.defineClass();
        ShortUnary exec = (ShortUnary)myClass.newInstance();

        assertEquals("~5 = invert 5 failed",   (short)~5, exec.unary((short)5));
        assertEquals("0xF0F0 = invert 0x0F0F failed",   (short)0xF0F0, exec.unary((short)0x0F0F));
    }

    public void testCharBitwisePrimitiveInvOperator() throws Exception
    {
        maker.Implements(CharUnary.class);
        defaultConstructor();

        maker.Method("unary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            maker.Return(maker.primitiveInv(op1.toPrimitive()).getValue());
       }
        maker.End();

        Class myClass = maker.defineClass();
        CharUnary exec = (CharUnary)myClass.newInstance();

        assertEquals("~ 'A' = invert('A') failed",   (char)~'A', exec.unary((char)'A'));
        assertEquals("0xF0F0 = invert 0x0F0F failed",   (char)0xF0F0, exec.unary((char)0x0F0F));
    }

    public void testIntBitwisePrimitiveInvOperator() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            maker.Return(maker.primitiveInv(op1.toPrimitive()).getValue());
       }
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("~5 = invert 5 failed",   ~5, exec.unary(5));
        assertEquals("0xF0F0F0F0 = invert 0x0F0F0F0F failed",   0xF0F0F0F0, exec.unary(0x0F0F0F0F));
    }

    public void testLongBitwisePrimitiveInvOperator() throws Exception
    {
        maker.Implements(LongUnary.class);
        defaultConstructor();

        maker.Method("unary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Begin();
        {
            Value op1 = maker.Get("x");
            maker.Return(maker.primitiveInv(op1.toPrimitive()).getValue());
       }
        maker.End();

        Class myClass = maker.defineClass();
        LongUnary exec = (LongUnary)myClass.newInstance();

        assertEquals("~5 = invert 5 failed",   (long)~5, exec.unary((long)5));
        assertEquals("0xF0F0F0F0F0F0F0F0 = invert 0x0F0F0F0F0F0F0F0F failed",
                      0xF0F0F0F0F0F0F0F0L, exec.unary(0x0F0F0F0F0F0F0F0FL));
    }

    public void testByteBitwiseInvOperator() throws Exception
    {
        maker.Implements(ByteUnary.class);
        defaultConstructor();

        maker.Method("unary", byte.class, ACC_PUBLIC);
        maker.Declare("x", byte.class, 0);
        maker.Begin();
            maker.Return(maker.Cast(maker.Inv(maker.Get("x")), byte.class));
        maker.End();

        Class myClass = maker.defineClass();
        ByteUnary exec = (ByteUnary)myClass.newInstance();

        assertEquals("-6(1010) = invert 5(0101) failed",   -6, exec.unary((byte)5));
        assertEquals("0xF0 = invert 0x0F failed",   (byte)0xF0, exec.unary((byte)0x0F));
    }

    public void testShortBitwiseInvOperator() throws Exception
    {
        maker.Implements(ShortUnary.class);
        defaultConstructor();

        maker.Method("unary", short.class, ACC_PUBLIC);
        maker.Declare("x", short.class, 0);
        maker.Begin();
            maker.Return(maker.Cast(maker.Inv(maker.Get("x")), short.class));
        maker.End();

        Class myClass = maker.defineClass();
        ShortUnary exec = (ShortUnary)myClass.newInstance();

        assertEquals("~5 = invert 5 failed",   (short)~5, exec.unary((short)5));
        assertEquals("0xF0F0 = invert 0x0F0F failed",   (short)0xF0F0, exec.unary((short)0x0F0F));
    }

    public void testCharBitwiseInvOperator() throws Exception
    {
        maker.Implements(CharUnary.class);
        defaultConstructor();

        maker.Method("unary", char.class, ACC_PUBLIC);
        maker.Declare("x", char.class, 0);
        maker.Begin();
            maker.Return(maker.Cast(maker.Inv(maker.Get("x")), char.class));
        maker.End();

        Class myClass = maker.defineClass();
        CharUnary exec = (CharUnary)myClass.newInstance();

        assertEquals("~ 'A' = invert('A') failed",   (char)~'A', exec.unary((char)'A'));
        assertEquals("0xF0F0 = invert 0x0F0F failed",   (char)0xF0F0, exec.unary((char)0x0F0F));
    }


    public void testIntBitwiseInvOperator() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Return(maker.Inv(maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("~5 = invert 5 failed",   ~5, exec.unary(5));
        assertEquals("0xF0F0F0F0 = invert 0x0F0F0F0F failed",   0xF0F0F0F0, exec.unary(0x0F0F0F0F));
    }

    public void testLongBitwiseInvOperator() throws Exception
    {
        maker.Implements(LongUnary.class);
        defaultConstructor();

        maker.Method("unary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Begin();
            maker.Return(maker.Inv(maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        LongUnary exec = (LongUnary)myClass.newInstance();

        assertEquals("~5 = invert 5 failed",   (long)~5, exec.unary((long)5));
        assertEquals("0xF0F0F0F0F0F0F0F0 = invert 0x0F0F0F0F0F0F0F0F failed",
                      0xF0F0F0F0F0F0F0F0L, exec.unary(0x0F0F0F0F0F0F0F0FL));
    }

    public void testBitwiseException() throws Exception
    {
        // MemberField
        maker.Method("eval", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
        maker.Begin();
        maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("i", ClassMakerFactory.INT_TYPE, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("l", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("o", ClassMakerFactory.OBJECT_TYPE, ACC_PUBLIC);
        try {
            maker.Return(maker.Or(maker.Get("b"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot Or type boolean with type boolean", ex.getMessage());
        }
        try {
            maker.Return(maker.Or(maker.Literal("xxxx"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot Or type java.lang.String with type int", ex.getMessage());
        }
        try {
            maker.Return(maker.And(maker.Get("b"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot And type boolean with type boolean", ex.getMessage());
        }
        try {
            maker.Return(maker.And(maker.Literal("xxxx"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot And type java.lang.String with type int", ex.getMessage());
        }
        try {
            maker.Return(maker.Xor(maker.Get("b"), maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot Xor type boolean with type boolean", ex.getMessage());
        }
        try {
            maker.Return(maker.Xor(maker.Literal("xxxx"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot Xor type java.lang.String with type int", ex.getMessage());
        }
        try {
            maker.Return(maker.Inv(maker.Get("b")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot Invert type boolean", ex.getMessage());
        }
    }

    //# Shift Left
    public void testShiftLeftOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.SHL(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("3(0011) << 2) = 12(1100) failed",   12, exec.binary(3, 2));
    }

    public void testLongShiftLeftOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", long.class, ACC_PUBLIC);
        maker.Declare("y", int.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.SHL(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setLongField(myClass, exec, "x", 6);
        setIntField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("6(0110) << 1 = 12(1100) failed",   12, getLongField(myClass, exec, "x"));
        setLongField(myClass, exec, "x", 64);
        setIntField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("64 << 4 = 1024 failed", 1024, getLongField(myClass, exec, "x"));
    }

    public void testByteShiftLeftOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", byte.class, ACC_PUBLIC);
        maker.Declare("y", byte.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.Cast(maker.SHL(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")), byte.class));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setByteField(myClass, exec, "x", 6);
        setByteField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("6(0110) << 1 = 12(1100) failed",   12, getByteField(myClass, exec, "x"));
        setByteField(myClass, exec, "x", 31);
        setByteField(myClass, exec, "y", 2);
        exec.run();
        assertEquals("31(00001111) << 2 = 124(00111100) failed", 124, getByteField(myClass, exec, "x"));
    }

    public void testShortShiftLeftOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", short.class, ACC_PUBLIC);
        maker.Declare("y", short.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.Cast(maker.SHL(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")), short.class));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setShortField(myClass, exec, "x", 6);
        setShortField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("6(0110) << 1 = 12(1100) failed",   12, getShortField(myClass, exec, "x"));
        setShortField(myClass, exec, "x", 64);
        setShortField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("64 << 4 = 1024 failed", 1024, getShortField(myClass, exec, "x"));
    }

    public void testBytePrimitiveShiftLeftOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", byte.class, ACC_PUBLIC);
        maker.Declare("y", byte.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
                    maker.primitiveShiftLeft(maker.Get(maker.This(), "x").toPrimitive(),
                            maker.Get(maker.This(), "y").toPrimitive()).getValue());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setByteField(myClass, exec, "x", 6);
        setByteField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("6(0110) << 1 = 12(1100) failed",   12, getByteField(myClass, exec, "x"));
        setByteField(myClass, exec, "x", 31);
        setByteField(myClass, exec, "y", 2);
        exec.run();
        assertEquals("31(00001111) << 2 = 124(00111100) failed", 124, getByteField(myClass, exec, "x"));
    }

    public void testShortPrimitiveShiftLeftOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", short.class, ACC_PUBLIC);
        maker.Declare("y", short.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
            maker.primitiveShiftLeft(maker.Get(maker.This(), "x").toPrimitive(),
                      maker.Get(maker.This(), "y").toPrimitive()).getValue());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setShortField(myClass, exec, "x", 6);
        setShortField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("6(0110) << 1 = 12(1100) failed",   12, getShortField(myClass, exec, "x"));
        setShortField(myClass, exec, "x", 64);
        setShortField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("64 << 4 = 1024 failed", 1024, getShortField(myClass, exec, "x"));
    }

    public void testCharPrimitiveShiftLeftOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", char.class, ACC_PUBLIC);
        maker.Declare("y", char.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
            maker.primitiveShiftLeft(maker.Get(maker.This(), "x").toPrimitive(),
                      maker.Get(maker.This(), "y").toPrimitive()).getValue());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setCharField(myClass, exec, "x", (char)6);
        setCharField(myClass, exec, "y", (char)1);
        exec.run();
        assertEquals("6(0110) << 1 = 12(1100) failed",   12, getCharField(myClass, exec, "x"));
        setCharField(myClass, exec, "x", (char)64);
        setCharField(myClass, exec, "y", (char)4);
        exec.run();
        assertEquals("64 << 4 = 1024 failed", 1024, getCharField(myClass, exec, "x"));
    }

    public void testShiftRightOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.SHR(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("12(1100) >> 1 = 6(0110) failed",   6, exec.binary(12, 1));
        assertEquals("-12 >> 1 =  failed",              -6, exec.binary(-12, 1));
    }

    public void testLongShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", long.class, ACC_PUBLIC);
        maker.Declare("y", int.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.SHR(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setLongField(myClass, exec, "x", 12);
        setIntField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getLongField(myClass, exec, "x"));
        setLongField(myClass, exec, "x", 1024);
        setIntField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("1024 >>> 4 = 64 failed", 64, getLongField(myClass, exec, "x"));
    }

    public void testByteShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", byte.class, ACC_PUBLIC);
        maker.Declare("y", byte.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.Cast(maker.SHR(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")), byte.class));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setByteField(myClass, exec, "x", 12);
        setByteField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getByteField(myClass, exec, "x"));
        setByteField(myClass, exec, "x", 127);
        setByteField(myClass, exec, "y", 2);
        exec.run();
        assertEquals("127(00100000) >>> 2 = 31(00001111) failed", 31, getByteField(myClass, exec, "x"));
    }

    public void testShortShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", short.class, ACC_PUBLIC);
        maker.Declare("y", short.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.Cast(maker.SHR(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")), short.class));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setShortField(myClass, exec, "x", 12);
        setShortField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getShortField(myClass, exec, "x"));
        setShortField(myClass, exec, "x", 1024);
        setShortField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("1024 >>> 4 = 64 failed", 64, getShortField(myClass, exec, "x"));
    }

    public void testBytePrimitiveShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", byte.class, ACC_PUBLIC);
        maker.Declare("y", byte.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
            maker.primitiveShiftRight(maker.Get(maker.This(), "x").toPrimitive(),
                    maker.Get(maker.This(), "y").toPrimitive()).getValue());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setByteField(myClass, exec, "x", 12);
        setByteField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getByteField(myClass, exec, "x"));
        setByteField(myClass, exec, "x", 127);
        setByteField(myClass, exec, "y", 2);
        exec.run();
        assertEquals("127(00100000) >>> 2 = 31(00001111) failed", 31, getByteField(myClass, exec, "x"));
    }

    public void testShortPrimitiveShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", short.class, ACC_PUBLIC);
        maker.Declare("y", short.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
            maker.primitiveShiftRight(maker.Get(maker.This(), "x").toPrimitive(),
                      maker.Get(maker.This(), "y").toPrimitive()).getValue());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setShortField(myClass, exec, "x", 12);
        setShortField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getShortField(myClass, exec, "x"));
        setShortField(myClass, exec, "x", 1024);
        setShortField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("1024 >>> 4 = 64 failed", 64, getShortField(myClass, exec, "x"));
    }

    public void testCharPrimitiveShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", char.class, ACC_PUBLIC);
        maker.Declare("y", char.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
                    maker.primitiveShiftRight(maker.Get(maker.This(), "x").toPrimitive(),
                            maker.Get(maker.This(), "y").toPrimitive()).getValue());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setCharField(myClass, exec, "x", (char)12);
        setCharField(myClass, exec, "y", (char)1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getCharField(myClass, exec, "x"));
        setCharField(myClass, exec, "x", (char)1024);
        setCharField(myClass, exec, "y", (char)4);
        exec.run();
        assertEquals("1024 >>> 4 = 64 failed", 64, getCharField(myClass, exec, "x"));
    }

    public void testUnsignedShiftRightOperator() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Declare("y", int.class, 0);
        maker.Begin();
            maker.Return(maker.USHR(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("12(1100) u>> 1 = 6(0110) failed",   6, exec.binary(12, 1));
        assertEquals("-12 u>> 1 = 2147483642 failed", 2147483642, exec.binary(-12, 1));
    }

    public void testLongUnsignedShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", long.class, ACC_PUBLIC);
        maker.Declare("y", int.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.USHR(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setLongField(myClass, exec, "x", 12);
        setIntField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getLongField(myClass, exec, "x"));
        setLongField(myClass, exec, "x", 1024);
        setIntField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("1024 >>> 4 = 64 failed", 64, getLongField(myClass, exec, "x"));
    }

    public void testByteUnsignedShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", byte.class, ACC_PUBLIC);
        maker.Declare("y", byte.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.Cast(maker.USHR(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")), byte.class));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setByteField(myClass, exec, "x", 12);
        setByteField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getByteField(myClass, exec, "x"));
        setByteField(myClass, exec, "x", 127);
        setByteField(myClass, exec, "y", 2);
        exec.run();
        assertEquals("127(00100000) >>> 2 = 31(00001111) failed", 31, getByteField(myClass, exec, "x"));
    }

    public void testShortUnsignedShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", short.class, ACC_PUBLIC);
        maker.Declare("y", short.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x", maker.Cast(maker.USHR(maker.Get(maker.This(), "x"),
                                                    maker.Get(maker.This(), "y")), short.class));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setShortField(myClass, exec, "x", 12);
        setShortField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getShortField(myClass, exec, "x"));
        setShortField(myClass, exec, "x", 1024);
        setShortField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("1024 >>> 4 = 64 failed", 64, getShortField(myClass, exec, "x"));
    }

    public void testBytePrimitiveUnsignedShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", byte.class, ACC_PUBLIC);
        maker.Declare("y", byte.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
            maker.primitiveUnsignedShiftRight(maker.Get(maker.This(), "x").toPrimitive(),
                       maker.Get(maker.This(), "y").toPrimitive()).getValue());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setByteField(myClass, exec, "x", 12);
        setByteField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getByteField(myClass, exec, "x"));
        setByteField(myClass, exec, "x", 127);
        setByteField(myClass, exec, "y", 2);
        exec.run();
        assertEquals("127(00100000) >>> 2 = 31(00001111) failed", 31, getByteField(myClass, exec, "x"));
    }

    public void testShortPrimitiveUnsignedShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", short.class, ACC_PUBLIC);
        maker.Declare("y", short.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
                    maker.primitiveUnsignedShiftRight(maker.Get(maker.This(), "x").toPrimitive(),
                            maker.Get(maker.This(), "y").toPrimitive()).getValue());
           maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setShortField(myClass, exec, "x", 12);
        setShortField(myClass, exec, "y", 1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getShortField(myClass, exec, "x"));
        setShortField(myClass, exec, "x", 1024);
        setShortField(myClass, exec, "y", 4);
        exec.run();
        assertEquals("1024 >>> 4 = 64 failed", 64, getShortField(myClass, exec, "x"));
    }

    public void testCharPrimitiveUnsignedShiftRightOperator() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", char.class, ACC_PUBLIC);
        maker.Declare("y", char.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "x",
                    maker.primitiveUnsignedShiftRight(maker.Get(maker.This(), "x").toPrimitive(),
                            maker.Get(maker.This(), "y").toPrimitive()).getValue());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setCharField(myClass, exec, "x", (char)12);
        setCharField(myClass, exec, "y", (char)1);
        exec.run();
        assertEquals("12(1100) >>> 1 = 6(0110) failed",   6, getCharField(myClass, exec, "x"));
        setCharField(myClass, exec, "x", (char)1024);
        setCharField(myClass, exec, "y", (char)4);
        exec.run();
        assertEquals("1024 >>> 4 = 64 failed", 64, getCharField(myClass, exec, "x"));
    }

    public void testShiftException() throws Exception
    {
        // MemberField
        maker.Method("eval", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
        maker.Begin();
        maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("i", ClassMakerFactory.INT_TYPE, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("l", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("o", ClassMakerFactory.OBJECT_TYPE, ACC_PUBLIC);
        try {
            maker.Return(maker.USHR(maker.Get("b"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot unsigned shift right type: boolean", ex.getMessage());
        }
        try {
            maker.Return(maker.USHR(maker.Get("i"), maker.Get("l")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Shift operand must be type int, short, byte or char; not long", ex.getMessage());
        }
        try {
            maker.Return(maker.USHR(maker.Literal("b"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot unsigned shift right type: java.lang.String", ex.getMessage());
        }
        try {
            maker.Return(maker.SHR(maker.Get("b"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot shift right type: boolean", ex.getMessage());
        }
        try {
            maker.Return(maker.SHR(maker.Get("i"), maker.Get("l")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Shift operand must be type int, short, byte or char; not long", ex.getMessage());
        }
        try {
            maker.Return(maker.SHR(maker.Literal("b"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot shift right type: java.lang.String", ex.getMessage());
        }
        try {
            maker.Return(maker.SHL(maker.Get("b"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot shift left type: boolean", ex.getMessage());
        }
        try {
            maker.Return(maker.SHL(maker.Get("i"), maker.Get("l")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Shift operand must be type int, short, byte or char; not long", ex.getMessage());
        }
        try {
            maker.Return(maker.SHL(maker.Literal("b"), maker.Get("i")));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot shift left type: java.lang.String", ex.getMessage());
        }
    }

    public void testDivRemExpression() throws Exception
    {
        binaryBegin();

        // infix:   (x / y) * y + (x % y) = x
        // prefix: + * / x y y % x y
        maker.Return(maker.Add(maker.Mult(maker.Div(maker.Get("x"), maker.Get("y")),
                                          maker.Get("y")),
                               maker.Rem(maker.Get("x"), maker.Get("y"))));

        Binary exec = binaryEnd();

        assertEquals("(x / y) * y + (x % y) = x failed", 7, exec.binary(7, 3));
        assertEquals("(x / y) * y + (x % y) = x failed", -7, exec.binary(-7, 3));
        assertEquals("(x / y) * y + (x % y) = x failed", 7, exec.binary(7, -3));
        assertEquals("(x / y) * y + (x % y) = x failed", -7, exec.binary(-7, -3));
    }

    //# Increment/decrement operators
    public void testIncByte() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("b", byte.class, 0);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
          maker.Declare("a", byte.class, 0);
          maker.Set("a", maker.Cast(maker.Get("x"), byte.class));
          maker.Return(maker.Inc("a"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(-1));
        assertEquals("Wrong value for exec.unary()", 7, exec.unary(6));
        assertEquals("Wrong value for exec.unary()", -128, exec.unary(127));
        assertEquals("Wrong value for exec.unary()", -127, exec.unary(128));
    }

    public void testIncShort() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("b", short.class, 0);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
          maker.Declare("a", short.class, 0);
          maker.Set("a", maker.Cast(maker.Get("x"), short.class));
          maker.Return(maker.Inc("a"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(-1));
        assertEquals("Wrong value for exec.unary()", 7, exec.unary(6));
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, exec.unary(Short.MAX_VALUE));
    }

    public void testIncChar() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("b", char.class, 0);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
          maker.Declare("a", char.class, 0);
          maker.Set("a", maker.Cast(maker.Get("x"), char.class));
          maker.Return(maker.Inc("a"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 'b', exec.unary('a'));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 7, exec.unary(6));
        assertEquals("Wrong value for exec.unary()", Character.MIN_VALUE, exec.unary(Character.MAX_VALUE));
    }

    public void testIncInt() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("b", int.class, 0);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
          maker.Declare("a", int.class, 0);
          maker.Set("a", maker.Get("x"));
          maker.Return(maker.Inc("a"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(-1));
        assertEquals("Wrong value for exec.unary()", 7, exec.unary(6));
        assertEquals("Wrong value for exec.unary()", Integer.MIN_VALUE, exec.unary(Integer.MAX_VALUE));
    }

    public void testIncLong() throws Exception
    {
        maker.Implements(LongUnary.class);
        defaultConstructor();
        maker.Declare("b", long.class, 0);

        maker.Method("unary", long.class, ACC_PUBLIC);
        maker.Declare("x", long.class, 0);
        maker.Begin();
          maker.Declare("a", long.class, 0);
          maker.Set("a", maker.Get("x"));
          maker.Return(maker.Inc("a"));
        maker.End();

        Class myClass = maker.defineClass();
        LongUnary exec = (LongUnary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 1L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0L, exec.unary(-1));
        assertEquals("Wrong value for exec.unary()", 7L, exec.unary(6));
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, exec.unary(Long.MAX_VALUE));
    }

    public void testIncException() throws Exception
    {
        // MemberField
        maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("c", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("other", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        try {
            maker.Inc("a");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment variable 'a' of type boolean", ex.getMessage());
        }
        try {
            maker.Inc(maker.This(), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment field 'b' of type boolean", ex.getMessage());
        }
        try {
            maker.Inc(maker.getFullyQualifiedClassName(), "c");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment field 'c' of type boolean", ex.getMessage());
        }
        try {
            maker.Inc(maker.Get("x"), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Expected a class but was type int", ex.getMessage());
        }
        try {
            maker.Inc(maker.getFullyQualifiedClassName(), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Class variable 'test.MyClass.b' is not static", ex.getMessage());
        }

    }

    public void testDecException() throws Exception
    {
        // MemberField
        maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("c", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("other", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        try {
            maker.Dec("a");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot decrement variable 'a' of type boolean", ex.getMessage());
        }
        try {
            maker.Dec(maker.This(), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot decrement field 'b' of type boolean", ex.getMessage());
        }
        try {
            maker.Dec(maker.getFullyQualifiedClassName(), "c");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot decrement field 'c' of type boolean", ex.getMessage());
        }
        try {
            maker.Dec(maker.Get("x"), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Expected a class but was type int", ex.getMessage());
        }
        try {
            maker.Dec(maker.getFullyQualifiedClassName(), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Class variable 'test.MyClass.b' is not static", ex.getMessage());
        }

    }

    public void testPostIncException() throws Exception
    {
        // MemberField
        maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("c", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("other", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        try {
            maker.PostInc("a");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment variable 'a' of type boolean", ex.getMessage());
        }
        try {
            maker.PostInc(maker.This(), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment field 'b' of type boolean", ex.getMessage());
        }
        try {
            maker.PostInc(maker.getFullyQualifiedClassName(), "c");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment field 'c' of type boolean", ex.getMessage());
        }
        try {
            maker.PostInc(maker.Get("x"), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Expected a class but was type int", ex.getMessage());
        }
        try {
            maker.PostInc(maker.getFullyQualifiedClassName(), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Class variable 'test.MyClass.b' is not static", ex.getMessage());
        }
    }

    public void testPostDecException() throws Exception
    {
        // MemberField
        maker.Declare("b", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        maker.Declare("c", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("other", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
        maker.Declare("a", ClassMakerFactory.BOOLEAN_TYPE, ACC_PUBLIC);
        try {
            maker.PostDec("a");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot decrement variable 'a' of type boolean", ex.getMessage());
        }
        try {
            maker.PostDec(maker.This(), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot decrement field 'b' of type boolean", ex.getMessage());
        }
        try {
            maker.PostDec(maker.getFullyQualifiedClassName(), "c");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot decrement field 'c' of type boolean", ex.getMessage());
        }
        try {
            maker.PostDec(maker.Get("x"), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Expected a class but was type int", ex.getMessage());
        }
        try {
            maker.PostDec(maker.getFullyQualifiedClassName(), "b");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Class variable 'test.MyClass.b' is not static", ex.getMessage());
        }
    }

}
