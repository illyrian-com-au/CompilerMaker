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

package org.mozilla.classfile;



public class ArrayLoadStoreTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface Unary
    {
        int unary(int a);
    }

    public void startClass(String className, String iface) throws Exception
    {
        // Generate Class
        cfw = new ClassFileWriter(className, "java/lang/Object", className + ".java");
        if (iface != null)
        {
            cfw.addInterface(iface);
        }
    }

    // Generate default constructor
    public void defaultConstructor() throws Exception
    {
        cfw.startMethod("<init>", "()V", ClassFileWriter.ACC_PUBLIC);
        cfw.addLoadThis();
        cfw.addInvoke(ByteCode.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        cfw.add(ByteCode.RETURN);
        cfw.stopMethod((short) 1);
    }

    public void testGetIntAt() throws Exception
    {
        String CallClassStr = "CallBinaryOperatorClass";

        startClass(CallClassStr, "org/mozilla/classfile/ArrayLoadStoreTest$Unary");
        defaultConstructor();

        cfw.addField("values", "[I", ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // RETURN this.values[$1]
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "values", "[I");
        cfw.addILoad(1);
        cfw.add(ByteCode.IALOAD);
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

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

    public void testIALoadStore() throws Exception
    {
        // Generate Class
        startClass("MyClass", "java/lang/Runnable");
        defaultConstructor();

        cfw.addField("values", "[I", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.values[0]
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "values", "[I");
            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.IALOAD);

            // SET top = top + 1
            cfw.add(ByteCode.ICONST_1);
            cfw.add(ByteCode.IADD);

            // PUT myclass.values[0] = top
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "values", "[I");
            cfw.add(ByteCode.SWAP);
            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.SWAP);
            cfw.add(ByteCode.IASTORE);

            cfw.add(ByteCode.RETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        int [] arr = {1, 2};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2, arr[0]);
    }

    public void testLALoadStore() throws Exception
    {
        // Generate Class
        startClass("MyClass", "java/lang/Runnable");
        defaultConstructor();

        cfw.addField("values", "[J", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // SET $1 = myclass.values
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "values", "[J");
//            cfw.add(ByteCode.DUP);
            cfw.addAStore(1);

            // GET top = $1[0]
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.LALOAD);

            // SET $2 = top + 1
            cfw.add(ByteCode.LCONST_1);
            cfw.add(ByteCode.LADD);
            cfw.addLStore(2);

            // PUT $1[0] = top
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_0);
            cfw.addLLoad(2);
            cfw.add(ByteCode.LASTORE);

            cfw.add(ByteCode.RETURN);
            cfw.stopMethod((short) 4);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        long [] arr = {1L, 2L};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1L, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2L, arr[0]);
    }

    public void testFALoadStore() throws Exception
    {
        // Generate Class
        startClass("MyClass", "java/lang/Runnable");
        defaultConstructor();

        cfw.addField("values", "[F", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // SET $1 = myclass.values
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "values", "[F");
//            cfw.add(ByteCode.DUP);
            cfw.addAStore(1);

            // GET top = $1[0]
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.FALOAD);

            // SET $2 = top + 1.0
            cfw.add(ByteCode.FCONST_1);
            cfw.add(ByteCode.FADD);
            cfw.addFStore(2);

            // PUT $1[0] = top
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_0);
            cfw.addFLoad(2);
            cfw.add(ByteCode.FASTORE);

            cfw.add(ByteCode.RETURN);
            cfw.stopMethod((short) 4);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        float [] arr = {1.2f, 2.1f};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1.2f, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2.2f, arr[0]);
    }

    public void testDALoadStore() throws Exception
    {
        // Generate Class
        startClass("MyClass", "java/lang/Runnable");
        defaultConstructor();

        cfw.addField("values", "[D", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // SET $1 = myclass.values
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "values", "[D");
//            cfw.add(ByteCode.DUP);
            cfw.addAStore(1);

            // GET top = $1[0]
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.DALOAD);

            // SET $2 = top + 1.0
            cfw.add(ByteCode.DCONST_1);
            cfw.add(ByteCode.DADD);
            cfw.addDStore(2);

            // PUT $1[0] = top
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_0);
            cfw.addDLoad(2);
            cfw.add(ByteCode.DASTORE);

            cfw.add(ByteCode.RETURN);
            cfw.stopMethod((short) 4);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Runnable exec = (Runnable)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        double [] arr = {1.2D, 2.1D};
        setField(exec.getClass(), exec, "values", arr);
        assertEquals("Wrong value", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 1.2D, arr[0]);
        exec.run();
        assertEquals("Wrong value for exec.values[0]", 2.2D, arr[0]);
    }

    public interface IntArray {
        int [] create();
    }

    public void testNewIntArray() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/ArrayLoadStoreTest$IntArray");
        defaultConstructor();

        cfw.addField("values", "[I", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("create", "()[I", (short) (ClassFileWriter.ACC_PUBLIC));

            // $1 = new int[5];
            //cfw.add(ByteCode.ICONST_5);  // This doesn't work. Creates Array of size 8.
            cfw.add(ByteCode.BIPUSH, 5);
            cfw.add(ByteCode.NEWARRAY, ByteCode.T_INT);
            cfw.addAStore(1);

            // SET $1 = myclass.values
            cfw.addLoadThis();
            cfw.addALoad(1);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "values", "[I");
//            cfw.add(ByteCode.DUP);


            // PUT $1[0] = 1
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.ICONST_2);
            cfw.add(ByteCode.IASTORE);

            // PUT $1[1] = 5
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_1);
            cfw.add(ByteCode.ICONST_5);
            cfw.add(ByteCode.IASTORE);

            // RETURN $1
            cfw.addALoad(1);
            cfw.add(ByteCode.ARETURN);
            cfw.stopMethod((short) 4);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        IntArray exec = (IntArray)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        int [] arr = (int [])exec.create();
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", 2, arr[0]);
        assertEquals("Wrong value for exec.values[1]", 5, arr[1]);
        assertEquals("Wrong array length", 5, arr.length);
    }

    public interface ObjectArray {
        Integer [] create();
    }

    public static final String INTEGER_CLASS = "java/lang/Integer";
    public static final String INTEGER_ARRAY = "[Ljava/lang/Integer;";

    public void testNewObjectArray() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/ArrayLoadStoreTest$ObjectArray");
        defaultConstructor();

        cfw.addField("values", INTEGER_ARRAY, ClassFileWriter.ACC_PUBLIC);

        // Generate public Integer [] create()
        {
            cfw.startMethod("create", "()"+INTEGER_ARRAY, (short) (ClassFileWriter.ACC_PUBLIC));

            // $1 = new Object[2];
            cfw.add(ByteCode.BIPUSH, 2);
            cfw.add(ByteCode.ANEWARRAY, INTEGER_CLASS);
            cfw.addAStore(1);

            // SET this.values = $1
            cfw.addLoadThis();
            cfw.addALoad(1);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "values", INTEGER_ARRAY);

            int setIntegerAt = cfw.acquireLabel();

            // GOSUB setIntegerAt(0, 2)
            cfw.add(ByteCode.ICONST_2);
            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.JSR, setIntegerAt);

            // GOSUB setIntegerAt(1, 5)
            cfw.add(ByteCode.ICONST_5);
            cfw.add(ByteCode.ICONST_1);
            cfw.add(ByteCode.JSR, setIntegerAt);

            // RETURN $1
            cfw.addALoad(1);
            cfw.add(ByteCode.ARETURN);

            // SUBROUTINE setIntegerAt(from, index, value)
            cfw.markLabel(setIntegerAt);
            cfw.addAStore(3); // Store return address in Local 3
            cfw.addIStore(4); // Store index in Local 4
            cfw.addIStore(5); // Store value in Local 5

            // $2 = new Integer
            cfw.add(ByteCode.NEW, INTEGER_CLASS);
            cfw.addAStore(2);

            // $2.<init>($5)
            cfw.addALoad(2);
            cfw.addILoad(5);
            cfw.addInvoke(ByteCode.INVOKESPECIAL, INTEGER_CLASS, "<init>", "(I)V");

            // PUT $1[$4] = $2
            cfw.addALoad(1);
            cfw.addILoad(4);
            cfw.addALoad(2);
            cfw.add(ByteCode.AASTORE);

            cfw.add(ByteCode.RET, 3);

            cfw.stopMethod((short) 6);
        }

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        ObjectArray exec = (ObjectArray)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        Integer [] arr = (Integer [])exec.create();
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong value for exec.values[0]", new Integer(2), arr[0]);
        assertEquals("Wrong value for exec.values[1]", new Integer(5), arr[1]);
        assertEquals("Wrong array length", 2, arr.length);
    }

    public interface Eval
    {
        int eval();
    }

    public void testArrayLength() throws Exception
    {
        {
            startClass("MyClass", "org/mozilla/classfile/ArrayLoadStoreTest$Eval");
            defaultConstructor();
            cfw.addField("values", "[I", ClassFileWriter.ACC_PUBLIC);

            cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

            // RETURN this.values[$1]
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "values", "[I");
            cfw.add(ByteCode.ARRAYLENGTH);
            cfw.add(ByteCode.IRETURN);

            cfw.stopMethod((short) 2);
        }
        Class myClass = defineClass("MyClass", cfw.toByteArray());
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
        startClass("MyClass", "org/mozilla/classfile/ArrayLoadStoreTest$MultiArray");
        defaultConstructor();

        cfw.addField("values", MINT_ARRAY, ClassFileWriter.ACC_PUBLIC);

        // Generate public int [][][] create()
        {
            cfw.startMethod("create", "()"+MINT_ARRAY, (short) (ClassFileWriter.ACC_PUBLIC));

            // $1 = new int[3,2,2];
            cfw.add(ByteCode.BIPUSH, 3);
            cfw.add(ByteCode.BIPUSH, 2);
            cfw.add(ByteCode.BIPUSH, 2);
            cfw.add(ByteCode.MULTIANEWARRAY, MINT_ARRAY, 3);
            cfw.addAStore(1);

            // SET this.values = $1
            cfw.addLoadThis();
            cfw.addALoad(1);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "values", MINT_ARRAY);

            // $1[0,0,0] = 3
            cfw.addALoad(1);
            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.AALOAD);

            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.AALOAD);

            cfw.add(ByteCode.ICONST_0);
            cfw.add(ByteCode.ICONST_3);
            cfw.add(ByteCode.IASTORE);

            // RETURN $1
            cfw.addALoad(1);
            cfw.add(ByteCode.ARETURN);

            cfw.stopMethod((short) 6);
        }

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        MultiArray exec = (MultiArray)myClass.newInstance();

        assertNull("Wrong value", getField(exec.getClass(), exec, "values"));
        int [][][] arr = (int [][][])exec.create();
        assertEquals("Wrong value for exec.values", arr, getField(exec.getClass(), exec, "values"));
        assertEquals("Wrong array length", 3, arr.length);
        assertEquals("Wrong array length", 2, arr[0].length);
        assertEquals("Wrong array length", 2, arr[0][0].length);
        assertEquals("Wrong value for arr[0][0][0]", 3, arr[0][0][0]);
        assertEquals("Wrong value for arr[2][1][1]", 0, arr[2][1][1]);
    }

}
