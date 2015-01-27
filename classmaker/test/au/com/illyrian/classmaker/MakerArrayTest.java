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

import java.lang.reflect.Field;

import org.mozilla.classfile.ByteCode;
import org.mozilla.classfile.ClassFilePrinter;

import au.com.illyrian.classmaker.types.Type;

public class MakerArrayTest extends ClassMakerTestCase implements ByteCode
{
    ClassMakerFactory factory;
    ClassMaker maker;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("MyClass", Object.class, "MyClass.java");
        defaultConstructor(maker);
        //maker.getClassFileWriter().setDebugCodeOutput(System.out);
    }


    public interface UnaryInt
    {
        int unary(int a);
    }

    public interface UnaryByte
    {
        byte unary(int a);
    }

    public interface UnaryLong
    {
        long unary(int a);
    }

    public interface UnaryFloat
    {
        float unary(int a);
    }

    public interface UnaryDouble
    {
        double unary(int a);
    }

    // Generate default constructor
    public void defaultConstructor(ClassMaker maker)
    {
        maker.Method("<init>", void.class, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public Object getField(Class myClass, Object myObj, String name)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.get(myObj);
    }

    public void setField(Class myClass, Object myObj, String name, Object value)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.set(myObj, value);
    }

    public void testIntGetAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(int.class), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        	// return this.values[x];
            maker.Return(maker.GetAt(maker.Get(maker.This(), "values"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        int [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testLongSetAt() throws Exception
    {
        maker.Implements(Runnable.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("x", ClassMaker.LONG_TYPE, 0);
            // int x = this.values[0];
            maker.Set("x", maker.GetAt(maker.Get(maker.This(), "values"), maker.Literal(0)));
            // this.values[0] = ++x;
            maker.SetAt(maker.Get(maker.This(), "values"), maker.Literal(0), maker.Inc("x"));

            maker.Return();
        }
        maker.End();


        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        long [] arr = {1L, 2L};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1L, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2L, arr[0]);
    }

    public void testFloatGetAt() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("values", maker.ArrayOf(float.class), ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Return(maker.GetAt(maker.Get(maker.This(), "values"), maker.Get("x")));
        maker.End();

        // RETURN this.values[$1]

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        float [] arr = {1.0f, 2.0f};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1.0f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2.0f, exec.unary(1));
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testFloatSetAt() throws Exception
    {
        maker.Implements(Runnable.class);
        maker.Declare("values", maker.ArrayOf(float.class), ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("x", float.class, 0);
            // int x = this.values[0];
            maker.Set("x", maker.GetAt(maker.Get(maker.This(), "values"), maker.Literal(0)));
            // this.values[0] = ++x;
            maker.SetAt(maker.Get(maker.This(), "values"), maker.Literal(0), maker.Inc("x"));

            maker.Return();
        }
        maker.End();


        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        float [] arr = {1.2f, 2.1f};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1.2f, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2.2f, arr[0]);
    }

    public void testDoubleGetAt() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("values", maker.ArrayOf(double.class), ACC_PUBLIC);

        maker.Method("unary", double.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Return(maker.GetAt(maker.Get(maker.This(), "values"), maker.Get("x")));
        maker.End();

        // RETURN this.values[$1]

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        double [] arr = {1.2d, 2.2d};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1.2d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2.2d, exec.unary(1));
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testDoubleSetAt() throws Exception
    {
        maker.Implements(Runnable.class);
        maker.Declare("values", maker.ArrayOf(double.class), ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("x", double.class, 0);
            // int x = this.values[0];
            maker.Set("x", maker.GetAt(maker.Get(maker.This(), "values"), maker.Literal(0)));
            // this.values[0] = ++x;
            maker.SetAt(maker.Get(maker.This(), "values"), maker.Literal(0), maker.Inc("x"));

            maker.Return();
        }
        maker.End();


        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        double [] arr = {1.2d, 2.1d};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1.2d, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2.2d, arr[0]);
    }

    public void testIntIncAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(int.class), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(int.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.IncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        int [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, arr[1]);
        assertEquals("Wrong value for exec.unary()", 4, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testIntDecAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(int.class), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(int.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.DecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        int [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, arr[0]);
        assertEquals("Wrong value for exec.unary()", -1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1, arr[0]);
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1, arr[1]);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testIntPostIncAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(int.class), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(int.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostIncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        int [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, arr[1]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testIntPostDecAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(int.class), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(int.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostDecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        int [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, arr[0]);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1, arr[1]);
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testByteGetAt() throws Exception
    {
        //maker.getClassFileWriter().setDebugCodeOutput(System.out);

        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
                // return this.values[x];
            maker.Return(maker.GetAt(maker.Get(maker.This(), "values"), maker.Get("x")));
        maker.End();

//        byte[] code = maker.getClassFileWriter().getCodeAttribute();
//        ClassFilePrinter printer = new ClassFilePrinter(System.out);
//        printer.byteCode(code);

        Class myClass = maker.defineClass();

        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        byte [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testByteSetAt() throws Exception
    {
        maker.Implements(Runnable.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("x", ClassMaker.BYTE_TYPE, 0);
            // int x = this.values[0];
            maker.Set("x", maker.GetAt(maker.Get(maker.This(), "values"), maker.Literal(0)));
            // this.values[0] = ++x;
            maker.SetAt(maker.Get(maker.This(), "values"), maker.Literal(0), maker.Inc("x"));

            maker.Return();
        }
        maker.End();


        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        byte [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2, arr[0]);
    }

    public void testByteIncAt() throws Exception
    {
        maker.Implements(UnaryByte.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);

        maker.Method("unary", ClassMaker.BYTE_TYPE, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.IncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryByte exec = (UnaryByte)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        byte [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, arr[1]);
        assertEquals("Wrong value for exec.unary()", 4, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4, arr[1]);
        // Test how java wraps bytes
        arr[1] = Byte.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, ++arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Byte.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testByteDecAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.DecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        byte [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, arr[0]);
        assertEquals("Wrong value for exec.unary()", -1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1, arr[0]);
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1, arr[1]);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0, arr[1]);
        // Test how java wraps bytes
        arr[1] = Byte.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, --arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Byte.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testBytePostIncAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostIncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        byte [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, arr[1]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4, arr[1]);
        // Test how java wraps bytes
        arr[1] = Byte.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]++);
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Byte.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testBytePostDecAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.BYTE_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostDecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        byte [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, arr[0]);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1, arr[1]);
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0, arr[1]);
        // Test how java wraps bytes
        arr[1] = Byte.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]--);
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Byte.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Byte.MIN_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Byte.MAX_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testShortGetAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
                // return this.values[x];
            maker.Return(maker.GetAt(maker.Get(maker.This(), "values"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();

//        byte[] code = maker.getClassFileWriter().getCodeAttribute();
//        ClassFilePrinter printer = new ClassFilePrinter(System.out);
//        printer.byteCode(code);

        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        short [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testShortSetAt() throws Exception
    {
        maker.Implements(Runnable.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("x", ClassMaker.SHORT_TYPE, 0);
            // int x = this.values[0];
            maker.Set("x", maker.GetAt(maker.Get(maker.This(), "values"), maker.Literal(0)));
            // this.values[0] = ++x;
            maker.SetAt(maker.Get(maker.This(), "values"), maker.Literal(0), maker.Inc("x"));

            maker.Return();
        }
        maker.End();


        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        short [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2, arr[0]);
    }

    public void testShortIncAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.IncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        short [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, arr[1]);
        assertEquals("Wrong value for exec.unary()", 4, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4, arr[1]);
        // Test how java wraps bytes
        arr[1] = Short.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, ++arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Short.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testShortDecAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.DecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        short [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, arr[0]);
        assertEquals("Wrong value for exec.unary()", -1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1, arr[0]);
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1, arr[1]);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0, arr[1]);
        // Test how java wraps bytes
        arr[1] = Short.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, --arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Short.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testShortPostIncAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostIncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        short [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, arr[1]);
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4, arr[1]);
        // Test how java wraps bytes
        arr[1] = Short.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]++);
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Short.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testShortPostDecAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.SHORT_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostDecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        short [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, arr[0]);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1, arr[1]);
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0, arr[1]);
        // Test how java wraps bytes
        arr[1] = Short.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]--);
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Short.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Short.MIN_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Short.MAX_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testCharGetAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
                // return this.values[x];
            maker.Return(maker.GetAt(maker.Get(maker.This(), "values"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();

//        byte[] code = maker.getClassFileWriter().getCodeAttribute();
//        ClassFilePrinter printer = new ClassFilePrinter(System.out);
//        printer.byteCode(code);

        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        char [] arr = {'a', 'y'};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 'a', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'y', exec.unary(1));
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testCharSetAt() throws Exception
    {
        maker.Implements(Runnable.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("x", ClassMaker.CHAR_TYPE, 0);
            // int x = this.values[0];
            maker.Set("x", maker.GetAt(maker.Get(maker.This(), "values"), maker.Literal(0)));
            // this.values[0] = ++x;
            maker.SetAt(maker.Get(maker.This(), "values"), maker.Literal(0), maker.Inc("x"));

            maker.Return();
        }
        maker.End();


        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        char [] arr = {'a', 'y'};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 'a', arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 'b', arr[0]);
    }

    public void testCharIncAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.IncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        char [] arr = {'a', 'y'};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 'b', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'b', arr[0]);
        assertEquals("Wrong value for exec.unary()", 'c', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'c', arr[0]);
        assertEquals("Wrong value for exec.unary()", 'z', exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 'z', arr[1]);
        // Test how java wraps bytes
        arr[1] = Character.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)++arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Character.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testCharDecAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.DecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        char [] arr = {'c', 'z'};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 'b', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'b', arr[0]);
        assertEquals("Wrong value for exec.unary()", 'a', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'a', arr[0]);
        assertEquals("Wrong value for exec.unary()", 'y', exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 'y', arr[1]);
        assertEquals("Wrong value for exec.unary()", 'x', exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 'x', arr[1]);
        // Test how java wraps bytes
        arr[1] = Character.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)--arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Character.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testCharPostIncAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostIncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        char [] arr = {'a', 'x'};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 'a', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'b', arr[0]);
        assertEquals("Wrong value for exec.unary()", 'b', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'c', arr[0]);
        assertEquals("Wrong value for exec.unary()", 'x', exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 'y', arr[1]);
        assertEquals("Wrong value for exec.unary()", 'y', exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 'z', arr[1]);
        // Test how java wraps bytes
        arr[1] = Character.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]++);
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Character.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testCharPostDecAt() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.CHAR_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostDecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        char [] arr = {'c', 'z'};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 'c', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'b', arr[0]);
        assertEquals("Wrong value for exec.unary()", 'b', exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 'a', arr[0]);
        assertEquals("Wrong value for exec.unary()", 'z', exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 'y', arr[1]);
        assertEquals("Wrong value for exec.unary()", 'y', exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 'x', arr[1]);
        // Test how java wraps bytes
        arr[1] = Character.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]--);
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Character.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, (int)arr[1]);
        assertEquals("Wrong value for exec.unary()", (int)Character.MIN_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", (int)Character.MAX_VALUE, (int)arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testLongIncAt() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);

        maker.Method("unary", ClassMaker.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.IncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        long [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 2L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2L, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3L, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3L, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3L, arr[1]);
        assertEquals("Wrong value for exec.unary()", 4L, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4L, arr[1]);
        // Test how java wraps bytes
        arr[1] = Long.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, ++arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Long.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testLongDecAt() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);

        maker.Method("unary", ClassMaker.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.DecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        long [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 0L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0L, arr[0]);
        assertEquals("Wrong value for exec.unary()", -1L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1L, arr[0]);
        assertEquals("Wrong value for exec.unary()", 1L, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1L, arr[1]);
        assertEquals("Wrong value for exec.unary()", 0L, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0L, arr[1]);
        // Test how java wraps bytes
        arr[1] = Long.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, --arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Long.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testLongPostIncAt() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);

        maker.Method("unary", ClassMaker.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostIncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        long [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2L, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3L, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2L, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3L, arr[1]);
        assertEquals("Wrong value for exec.unary()", 3L, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4L, arr[1]);
        // Test how java wraps bytes
        arr[1] = Long.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]++);
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Long.MAX_VALUE;
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testLongPostDecAt() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("values", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);

        maker.Method("unary", ClassMaker.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(ClassMaker.LONG_TYPE), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostDecAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        long [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0L, arr[0]);
        assertEquals("Wrong value for exec.unary()", 0L, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1L, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2L, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1L, arr[1]);
        assertEquals("Wrong value for exec.unary()", 1L, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0L, arr[1]);
        // Test how java wraps bytes
        arr[1] = Long.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]--);
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]);
        // Test how classmaker wraps bytes.
        arr[1] = Long.MIN_VALUE;
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, arr[1]);
        assertEquals("Wrong value for exec.unary()", Long.MIN_VALUE, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", Long.MAX_VALUE, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testFloatIncAt() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("values", maker.ArrayOf(float.class), ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(float.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.IncAt(maker.Get("a"), maker.Get("x")));
        maker.End();


        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        float [] arr = {1.0f, 2.0f};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 2f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2f, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3f, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3f, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3f, arr[1]);
        assertEquals("Wrong value for exec.unary()", 4f, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4f, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testFloatDecAt() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("values", maker.ArrayOf(float.class), ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(float.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return --a[x];
            maker.Return(maker.DecAt(maker.Get("a"), maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        float [] arr = {1.0f, 2.0f};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 0f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0f, arr[0]);
        assertEquals("Wrong value for exec.unary()", -1f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1f, arr[0]);
        assertEquals("Wrong value for exec.unary()", 1f, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1f, arr[1]);
        assertEquals("Wrong value for exec.unary()", 0f, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0f, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testFloatPostIncAt() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("values", maker.ArrayOf(float.class), ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(float.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return a[x]++;
            maker.Return(maker.PostIncAt(maker.Get("a"), maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        float [] arr = {1.0f, 2.0f};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2f, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3f, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2f, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3f, arr[1]);
        assertEquals("Wrong value for exec.unary()", 3f, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4f, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testFloatPostDecAt() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("values", maker.ArrayOf(float.class), ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(float.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostDecAt(maker.Get("a"), maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        float [] arr = {1.0f, 2.0f};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0f, arr[0]);
        assertEquals("Wrong value for exec.unary()", 0f, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1f, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2f, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1f, arr[1]);
        assertEquals("Wrong value for exec.unary()", 1f, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0f, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }
    public void testDoubleIncAt() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("values", maker.ArrayOf(double.class), ACC_PUBLIC);

        maker.Method("unary", double.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(double.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.IncAt(maker.Get("a"), maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        double [] arr = {1.0d, 2.0d};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 2d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2d, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3d, arr[0]);
        assertEquals("Wrong value for exec.unary()", 3d, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3d, arr[1]);
        assertEquals("Wrong value for exec.unary()", 4d, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4d, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testDoubleDecAt() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("values", maker.ArrayOf(double.class), ACC_PUBLIC);

        maker.Method("unary", double.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(double.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.DecAt(maker.Get("a"), maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        double [] arr = {1.0d, 2.0d};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 0d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0d, arr[0]);
        assertEquals("Wrong value for exec.unary()", -1d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1d, arr[0]);
        assertEquals("Wrong value for exec.unary()", 1d, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1d, arr[1]);
        assertEquals("Wrong value for exec.unary()", 0d, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0d, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testDoublePostIncAt() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("values", maker.ArrayOf(double.class), ACC_PUBLIC);

        maker.Method("unary", double.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(double.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return ++a[x];
            maker.Return(maker.PostIncAt(maker.Get("a"), maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        double [] arr = {1.0d, 2.0d};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2d, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 3d, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2d, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3d, arr[1]);
        assertEquals("Wrong value for exec.unary()", 3d, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 4d, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testDoublePostDecAt() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("values", maker.ArrayOf(double.class), ACC_PUBLIC);

        maker.Method("unary", double.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
            maker.Declare("a", maker.ArrayOf(double.class), ACC_PUBLIC);
            maker.Set("a", maker.Get(maker.This(), "values"));
            // return a[x]--;
            maker.Return(maker.PostDecAt(maker.Get("a"), maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        assertNull("Wrong value for exec.unary()", getField(exec.getClass(), exec, "values"));
        double [] arr = {1.0d, 2.0d};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value for exec.unary()", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.unary()", 1d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0d, arr[0]);
        assertEquals("Wrong value for exec.unary()", 0d, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -1d, arr[0]);
        assertEquals("Wrong value for exec.unary()", 2d, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 1d, arr[1]);
        assertEquals("Wrong value for exec.unary()", 1d, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0d, arr[1]);
        try {
            exec.unary(2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            assertEquals("Wrong exception", "2", ex.getMessage());
        }
    }

    public void testGetAtException() throws Exception
    {
        maker.Method("test", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Declare("x", int.class, 0);
        maker.Declare("b", maker.ArrayOf(byte.class), 0);
        try {
            maker.GetAt(maker.Literal("b"), maker.Literal(5));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Expected an array but was type java.lang.String", ex.getMessage());
        }
        try {
            maker.GetAt(maker.Get("b"), maker.Literal(5.0));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Array index must be must be type int, short, byte or char, not double", ex.getMessage());
        }
        try {
            maker.NewArray(maker.Literal("Hello"), maker.Literal((byte)5));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("java.lang.String is not a type of array", ex.getMessage());
        }
    }

    public void testSetAtException() throws Exception
    {
        maker.Method("test", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Declare("x", int.class, 0);
        maker.Declare("b", maker.ArrayOf(byte.class), 0);
        try {
            maker.SetAt(maker.Get("b"), maker.Get("x"), maker.Literal((short)1));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("An array of type byte cannot be assigned short", ex.getMessage());
        }
        try {
            maker.SetAt(maker.Get("b"), maker.Get("x"), maker.Literal('a'));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("An array of type byte cannot be assigned char", ex.getMessage());
        }
        try {
            maker.SetAt(maker.Literal("b"), maker.Literal(5), maker.Literal('a'));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Expected an array but was type java.lang.String", ex.getMessage());
        }
        try {
            maker.SetAt(maker.Get("b"), maker.Literal(5.0), maker.Literal('a'));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Array index must be must be type int, short, byte or char, not double", ex.getMessage());
        }
        try {
            maker.SetAt(maker.Get("b"), maker.Literal(5), maker.Null());
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("An array of type byte cannot be assigned null", ex.getMessage());
        }
        try {
            maker.Length(maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Expected an array but was type int", ex.getMessage());
        }
        try {
            maker.NewArray(maker.Get("x"),
                            maker.Push(maker.Literal(3))
                                 .Push(maker.Literal(2)));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("int is not a type of array", ex.getMessage());
        }
        try {
            maker.NewArray(maker.ArrayOf(int.class),
                            maker.Push(maker.Literal(2.0))
                                 .Push(maker.Literal(2)));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Type of array dimension 0 must be type int, short, byte or char; not double", ex.getMessage());
        }
        try {
            maker.NewArray(maker.ArrayOf(int.class), maker.Literal(2.0));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Array size must be type int, short, byte or char; not double", ex.getMessage());
        }
    }

    public void testIncAtException() throws Exception
    {
        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", ClassMaker.LONG_TYPE, 0);
        maker.Begin();
        try {
            maker.IncAt(maker.Get("x"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("long is not a type of array", ex.getMessage());
        }
        try {
            maker.DecAt(maker.Get("x"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("long is not a type of array", ex.getMessage());
        }
        try {
            maker.PostIncAt(maker.Get("x"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("long is not a type of array", ex.getMessage());
        }
        try {
            maker.PostDecAt(maker.Get("x"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("long is not a type of array", ex.getMessage());
        }
        try {
            maker.IncAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("No local variable called \'a\'", ex.getMessage());
        }
        try {
            maker.DecAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("No local variable called \'a\'", ex.getMessage());
        }
        try {
            maker.PostIncAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("No local variable called \'a\'", ex.getMessage());
        }
        try {
            maker.PostDecAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("No local variable called \'a\'", ex.getMessage());
        }


        maker.Declare("a", maker.ArrayOf(int.class), ACC_PUBLIC);
        try {
            maker.IncAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Array index must be must be type int, short, byte or char, not long", ex.getMessage());
        }
        try {
            maker.DecAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Array index must be must be type int, short, byte or char, not long", ex.getMessage());
        }
        try {
            maker.PostIncAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Array index must be must be type int, short, byte or char, not long", ex.getMessage());
        }
        try {
            maker.PostDecAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Array index must be must be type int, short, byte or char, not long", ex.getMessage());
        }
        maker.Return(ClassMaker.INT_TYPE);
        maker.End();

        // MemberField
        maker.Declare("b", maker.ArrayOf(ClassMaker.BOOLEAN_TYPE), ACC_PUBLIC);

        maker.Method("other", int.class, ACC_PUBLIC);
        maker.Declare("x", int.class, 0);
        maker.Begin();
        maker.Declare("a", maker.ArrayOf(ClassMaker.BOOLEAN_TYPE), ACC_PUBLIC);
        try {
            maker.IncAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment array element of type boolean", ex.getMessage());
        }
        try {
            maker.IncAt(maker.Get(maker.This(), "b"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment array element of type boolean", ex.getMessage());
        }
        try {
            maker.DecAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot decrement array element of type boolean", ex.getMessage());
        }
        try {
            maker.PostIncAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment array element of type boolean", ex.getMessage());
        }
        try {
            maker.PostDecAt(maker.Get("a"), maker.Get("x"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot decrement array element of type boolean", ex.getMessage());
        }
    }

    public interface IntArray {
        int [] create();
    }

    public void createArrayClass(Class elementClass)
    {
        // Generate Class
        maker.Implements(Runnable.class);

        maker.Declare("values", maker.ArrayOf(elementClass), ACC_PUBLIC);

        // Generate public void run()
        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            // int[] x;
            maker.Declare("x", maker.ArrayOf(elementClass), 0);
            // x = new int[5];
            maker.Set("x" , maker.NewArray(maker.ArrayOf(elementClass), maker.Literal((byte)5)));
            // this.values = x;
            maker.Set(maker.This(), "values", maker.Get("x"));
            // x[0] = 2;
            maker.SetAt(maker.Get("x"), maker.Literal(0), maker.Literal(2));
            // x[1] = 5;
            maker.SetAt(maker.Get("x"), maker.Literal(1), maker.Literal(5));
            // return;
            maker.Return();
        }
        maker.End();
    }

    public void testNewIntArray() throws Exception
    {
        createArrayClass(int.class);
        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        int [] arr = (int [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 2, arr[0]);
        assertEquals("Wrong value for exec.values[1]", 5, arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public void testNewByteArray() throws Exception
    {
        createArrayClass(byte.class);

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        byte [] arr = (byte [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 2, arr[0]);
        assertEquals("Wrong value for exec.values[1]", 5, arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public void testNewShortArray() throws Exception
    {
        createArrayClass(short.class);

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        short [] arr = (short [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 2, arr[0]);
        assertEquals("Wrong value for exec.values[1]", 5, arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public void testNewCharArray() throws Exception
    {
        // Generate Class
        maker.Implements(Runnable.class);

        maker.Declare("values", maker.ArrayOf(char.class), ACC_PUBLIC);

        // Generate public void run()
        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            // int[] x;
            maker.Declare("x", maker.ArrayOf(char.class), 0);
            // x = new int[5];
            maker.Set("x" , maker.NewArray(maker.ArrayOf(char.class), maker.Literal((byte)5)));
            // this.values = x;
            maker.Set(maker.This(), "values", maker.Get("x"));
            // x[0] = 'a';
            maker.SetAt(maker.Get("x"), maker.Literal(0), maker.Literal('a'));
            // x[1] = 'Z';
            maker.SetAt(maker.Get("x"), maker.Literal(1), maker.Literal('Z'));
            // return;
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        char [] arr = (char [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 'a', arr[0]);
        assertEquals("Wrong value for exec.values[1]", 'Z', arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public void testNewBooleanArray() throws Exception
    {
        // Generate Class
        maker.Implements(Runnable.class);

        maker.Declare("values", maker.ArrayOf(boolean.class), ACC_PUBLIC);

        // Generate public void run()
        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            // int[] x;
            maker.Declare("x", maker.ArrayOf(boolean.class), 0);
            // x = new int[5];
            maker.Set("x" , maker.NewArray(maker.ArrayOf(boolean.class), maker.Literal((byte)5)));
            // this.values = x;
            maker.Set(maker.This(), "values", maker.Get("x"));
            // x[0] = 'a';
            maker.SetAt(maker.Get("x"), maker.Literal(0), maker.Literal(true));
            // x[1] = 'Z';
            maker.SetAt(maker.Get("x"), maker.Literal(1), maker.Literal(false));
            // return;
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        boolean [] arr = (boolean [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", true, arr[0]);
        assertEquals("Wrong value for exec.values[1]", false, arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public void testNewLongArray() throws Exception
    {
        createArrayClass(long.class);

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        long [] arr = (long [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 2L, arr[0]);
        assertEquals("Wrong value for exec.values[1]", 5L, arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public void testNewFloatArray() throws Exception
    {
        createArrayClass(float.class);

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        float [] arr = (float [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 2.0F, arr[0]);
        assertEquals("Wrong value for exec.values[1]", 5.0F, arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public void testNewDoubleArray() throws Exception
    {
        createArrayClass(double.class);

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        double [] arr = (double [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 2.0D, arr[0]);
        assertEquals("Wrong value for exec.values[1]", 5.0D, arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public void testNewSetObjectArray() throws Exception
    {
        // Generate Class
        maker.Implements(Runnable.class);

        maker.Declare("values", maker.ArrayOf(String.class), ACC_PUBLIC);

        // Generate public void run()
        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        {
            // int[] x;
            maker.Declare("x", maker.ArrayOf(String.class), 0);
            // x = new int[5];
            maker.Set("x" , maker.NewArray(maker.ArrayOf(String.class), maker.Literal((byte)5)));
            // this.values = x;
            maker.Set(maker.This(), "values", maker.Get("x"));
            // x[0] = "Hello";
            maker.SetAt(maker.Get("x"), maker.Literal(0), maker.Literal("Hello"));
            // x[1] = "World";
            maker.SetAt(maker.Get("x"), maker.Literal(1), maker.Literal("World"));
            // x[2] = null;
            maker.SetAt(maker.Get("x"), maker.Literal(2), maker.Null());
            // return;
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        exec.run();
        String [] arr = (String [])getField(exec.getClass(), exec, "values");
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", "Hello", arr[0]);
        assertEquals("Wrong value for exec.values[1]", "World", arr[1]);
        assertEquals("Wrong value for exec.values[2]", null, arr[2]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public interface ObjectArray {
        Integer [] create();
    }

    public static final String INTEGER_CLASS = "java.lang.Integer";

    public void testNewObjectArray() throws Exception
    {
        // Generate Class
        maker.Implements(ObjectArray.class);
        maker.Import(Integer.class);

        maker.Declare("values", maker.ArrayOf("Integer"), ACC_PUBLIC);

        // Generate public Integer setIntegerAt(int index, int value)
        maker.Method("setIntegerAt", "Integer", ACC_PRIVATE);
        maker.Declare("index", int.class, 0);
        maker.Declare("value", int.class, 0);
        maker.Begin();
        {
            maker.Declare("x", "Integer", 0);
            maker.Set("x", maker.New("Integer").Init(maker.Push(maker.Get("value"))));
            maker.SetAt(maker.Get(maker.This(), "values"), maker.Get("index"), maker.Get("x"));
            maker.Return(maker.Get("x"));
        }
        maker.End();

        // Generate public Integer [] create()
        maker.Method("create", maker.ArrayOf("Integer"), ACC_PUBLIC);
        maker.Begin();
        {
            // $1 = new Object[2];
            maker.Declare("x", maker.ArrayOf("Integer"), 0);
            maker.Set("x" , maker.NewArray(maker.ArrayOf("Integer"), maker.Literal((byte)2)));
            maker.Set(maker.This(), "values", maker.Get("x"));

            maker.Call(maker.This(), "setIntegerAt", maker.Push(maker.Literal(0)).Push(maker.Literal(2)));
            maker.Call(maker.This(), "setIntegerAt", maker.Push(maker.Literal(1)).Push(maker.Literal(5)));
            maker.Return(maker.Get("x"));
        }
        maker.End();

        Class myClass = maker.defineClass();
        ObjectArray exec = (ObjectArray)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        Integer [] arr = (Integer [])exec.create();
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong array length", 2, arr.length);
        assertEquals("Wrong value for exec.values[0]", new Integer(2), arr[0]);
        assertEquals("Wrong value for exec.values[1]", new Integer(5), arr[1]);
    }

    public interface Eval
    {
        int eval();
    }

    public void testArrayLength() throws Exception
    {
        maker.Implements(Eval.class);
        maker.Declare("values", maker.ArrayOf(int.class), ACC_PUBLIC);

        // Generate public Integer [] create()
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Return(maker.Length(maker.Get(maker.This(), "values")));
        }
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval) myClass.newInstance();

        int [] arr2 = {1, 2};
        setField(myClass, exec, "values", arr2);
        assertEquals("Wrong Array length", 2, exec.eval());

        int [] arr5 = {1, 2, 3, 4, 5};
        setField(myClass, exec, "values", arr5);
        assertEquals("Wrong Array length", 5, exec.eval());

        int [] arr0 = {};
        setField(myClass, exec, "values", arr0);
        assertEquals("Wrong Array length", 0, exec.eval());
    }

    public interface MultiArray {
        int [] [] [] create();
    }

    public static final String MINT_ARRAY = "[[[I";

    public void testNewMultiIntArray() throws Exception
    {
        // Generate Class
        maker.Implements(MultiArray.class);

        Type mint_array = maker.ArrayOf(maker.ArrayOf(maker.ArrayOf(int.class)));
        maker.Declare("values", mint_array, ACC_PUBLIC);

        // Generate public int [][][] create()
        maker.Method("create", mint_array, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("x", mint_array, 0);
            // x = new int[3,2,2];
            maker.Set("x", maker.NewArray(mint_array,
                    maker.Push(maker.Literal(3))
                         .Push(maker.Literal(2))
                         .Push(maker.Literal(2))));
            // SET this.values = x
            maker.Set(maker.This(), "values", maker.Get("x"));
            // x[0][0][0] = 3
            maker.SetAt(maker.GetAt(maker.GetAt(maker.Get("x"), maker.Literal(0)) ,maker.Literal(0)),maker.Literal(0), maker.Literal(3));
            // x[2][1][1] = 5
            maker.SetAt(maker.GetAt(maker.GetAt(maker.Get("x"), maker.Literal(2)) ,maker.Literal(1)),maker.Literal(1), maker.Literal(5));
            // RETURN x
            maker.Return(maker.Get("x"));
      }
      maker.End();

        Class myClass = maker.defineClass();
        MultiArray exec = (MultiArray)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        int [][][] arr = (int [][][])exec.create();
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong array length", 3, arr.length);
        assertEquals("Wrong array length", 2, arr[0].length);
        assertEquals("Wrong array length", 2, arr[0][0].length);
        assertEquals("Wrong value for arr[0][0][0]", 3, arr[0][0][0]);
        assertEquals("Wrong value for arr[2][1][1]", 5, arr[2][1][1]);
    }
}
