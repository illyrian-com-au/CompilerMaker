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


public class LoadStoreTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface EvalInt
    {
        int eval();
    }

    public interface EvalLong
    {
        long eval();
    }

    public interface EvalFloat
    {
        float eval();
    }

    public interface EvalDouble
    {
        double eval();
    }

    public interface UnaryLong
    {
        long unary(long a);
    }

    public void startClass(String className, String iface) throws Exception
    {
        // Generate Class
        cfw = new ClassFileWriter(className, "java/lang/Object", className + ".java");
        cfw.addInterface(iface);
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

    public void testILoadStore() throws Exception
    {
        // Generate Class
        startClass("MyClass", "java/lang/Runnable");
        defaultConstructor();

        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "I");

            // SET id = id + 1
            cfw.add(ByteCode.ICONST_1);
            cfw.add(ByteCode.IADD);

            // PUT myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.SWAP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

            cfw.add(ByteCode.RETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Runnable exec = (Runnable)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0, getIntField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 1, getIntField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 2, getIntField(myClass, exec, "id"));
        setIntField(myClass, exec, "id", Integer.MAX_VALUE);
        assertEquals("Wrong value for myObj.id", Integer.MAX_VALUE, getIntField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", Integer.MIN_VALUE, getIntField(myClass, exec, "id"));
    }

    public void testIPreIncrement() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$EvalInt");
        defaultConstructor();

        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

            // Duplicate this -> this1, this2
            cfw.addLoadThis();
            cfw.add(ByteCode.DUP);
            // GET this2.id -> id
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "I");

            // SET id = id + 1
            cfw.add(ByteCode.ICONST_1);
            cfw.add(ByteCode.IADD);

            // Duplicate this1, this2, id -> this1, id1, this2, id2
            cfw.add(ByteCode.DUP_X1);

            // PUT this2.id = id2
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

            // Return id1
            cfw.add(ByteCode.IRETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        EvalInt exec = (EvalInt)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1, exec.eval());
        assertEquals("Wrong value for myObj.id", 1, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 2, exec.eval());
        assertEquals("Wrong value for myObj.id", 2, getIntField(myClass, exec, "id"));
        setIntField(myClass, exec, "id", Integer.MAX_VALUE);
        assertEquals("Wrong value for myObj.id", Integer.MAX_VALUE, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", Integer.MIN_VALUE, exec.eval());
        assertEquals("Wrong value for myObj.id", Integer.MIN_VALUE, getIntField(myClass, exec, "id"));
    }

    public void testIPostIncrement() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$EvalInt");
        defaultConstructor();

        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.id -> id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "I");

            // Duplicate id -> id1, id2
            cfw.add(ByteCode.DUP);

            // SET id2 = id2 + 1
            cfw.add(ByteCode.ICONST_1);
            cfw.add(ByteCode.IADD);

            // PUT id2 -> myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.SWAP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

            // Return id1
            cfw.add(ByteCode.IRETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        EvalInt exec = (EvalInt)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 0, exec.eval());
        assertEquals("Wrong value for myObj.id", 1, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1, exec.eval());
        assertEquals("Wrong value for myObj.id", 2, getIntField(myClass, exec, "id"));
        setIntField(myClass, exec, "id", Integer.MAX_VALUE);
        assertEquals("Wrong value for myObj.id", Integer.MAX_VALUE, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", Integer.MAX_VALUE, exec.eval());
        assertEquals("Wrong value for myObj.id", Integer.MIN_VALUE, getIntField(myClass, exec, "id"));
    }

    public void testFLoadStore() throws Exception
    {
        // Generate Class
        startClass("MyClass", "java/lang/Runnable");
        defaultConstructor();

        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "F");

            // SET id = id + 1
            cfw.add(ByteCode.FCONST_1);
            cfw.add(ByteCode.FADD);

            // PUT myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.SWAP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "F");

            cfw.add(ByteCode.RETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Runnable exec = (Runnable)myClass.newInstance();
        // NOTE: Decimal literals are doubles by default so be explicit with floats.
        assertEquals("Wrong value for myObj.id", 0.0f, getFloatField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 1.0f, getFloatField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 2.0f, getFloatField(myClass, exec, "id"));
        setFloatField(myClass, exec, "id", Float.NEGATIVE_INFINITY);
        assertEquals("Wrong value for myObj.id", Float.NEGATIVE_INFINITY, getFloatField(myClass, exec, "id"));
        setFloatField(myClass, exec, "id", Float.POSITIVE_INFINITY);
        assertEquals("Wrong value for myObj.id", Float.POSITIVE_INFINITY, getFloatField(myClass, exec, "id"));
        setFloatField(myClass, exec, "id", Float.NaN);
        assertEquals("Wrong value for myObj.id", Float.NaN, getFloatField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", Float.NaN, getFloatField(myClass, exec, "id"));
    }

    public void testFPreIncrement() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$EvalFloat");
        defaultConstructor();

        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("eval", "()F", (short) (ClassFileWriter.ACC_PUBLIC));

            // Duplicate this -> this1, this2
            cfw.addLoadThis();
            cfw.add(ByteCode.DUP);
            // GET this2.id -> id
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "F");

            // SET id = id + 1.0
            cfw.add(ByteCode.FCONST_1);
            cfw.add(ByteCode.FADD);

            // Duplicate this1, this2, id -> this1, id1, this2, id2
            cfw.add(ByteCode.DUP_X1);

            // PUT this2.id = id2
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "F");

            // Return id1
            cfw.add(ByteCode.FRETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        EvalFloat exec = (EvalFloat)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1.0f, exec.eval());
        assertEquals("Wrong value for myObj.id", 1.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 2.0f, exec.eval());
        assertEquals("Wrong value for myObj.id", 2.0f, getFloatField(myClass, exec, "id"));
    }

    public void testFPostIncrement() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$EvalFloat");
        defaultConstructor();

        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("eval", "()F", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.id -> id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "F");

            // Duplicate id -> id1, id2
            cfw.add(ByteCode.DUP);

            // SET id2 = id2 + 1
            cfw.add(ByteCode.FCONST_1);
            cfw.add(ByteCode.FADD);

            // PUT id2 -> myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.SWAP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "F");

            // Return id1
            cfw.add(ByteCode.FRETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        EvalFloat exec = (EvalFloat)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 0.0f, exec.eval());
        assertEquals("Wrong value for myObj.id", 1.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1.0f, exec.eval());
        assertEquals("Wrong value for myObj.id", 2.0f, getFloatField(myClass, exec, "id"));
    }

    public void testLLoadStore() throws Exception
    {
        // Generate Class
        startClass("MyClass", "java/lang/Runnable");
        defaultConstructor();

        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "J");

            // SET id = id + 1
            cfw.add(ByteCode.LCONST_1);
            cfw.add(ByteCode.LADD);

            // PUT myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.DUP_X2); // swap reference to below long.
            cfw.add(ByteCode.POP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "J");

            cfw.add(ByteCode.RETURN);
            cfw.stopMethod((short) 2);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Runnable exec = (Runnable)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0, getLongField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 1, getLongField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 2, getLongField(myClass, exec, "id"));
        setLongField(myClass, exec, "id", Long.MAX_VALUE);
        assertEquals("Wrong value for myObj.id", Long.MAX_VALUE, getLongField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", Long.MIN_VALUE, getLongField(myClass, exec, "id"));
    }

    public void testLongPreIncrement() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$EvalLong");
        defaultConstructor();

        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("eval", "()J", (short) (ClassFileWriter.ACC_PUBLIC));

            // Duplicate this -> this1, this2
            cfw.addLoadThis();
            cfw.add(ByteCode.DUP);
            // GET this2.id -> id
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "J");

            // SET id = id + 1
            cfw.add(ByteCode.LCONST_1);
            cfw.add(ByteCode.LADD);

            // Duplicate this1, this2, id -> this1, id1, this2, id2
            cfw.add(ByteCode.DUP2_X1);

            // PUT this2.id = id2
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "J");

            // Return id1
            cfw.add(ByteCode.LRETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        EvalLong exec = (EvalLong)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0, getLongField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1, exec.eval());
        assertEquals("Wrong value for myObj.id", 1, getLongField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 2, exec.eval());
        assertEquals("Wrong value for myObj.id", 2, getLongField(myClass, exec, "id"));
        setLongField(myClass, exec, "id", Long.MAX_VALUE);
        assertEquals("Wrong value for myObj.id", Long.MAX_VALUE, getLongField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", Long.MIN_VALUE, exec.eval());
        assertEquals("Wrong value for myObj.id", Long.MIN_VALUE, getLongField(myClass, exec, "id"));
    }

    public void testLongPostIncrement() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$EvalLong");
        defaultConstructor();

        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("eval", "()J", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.id -> id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "J");

            // Duplicate id -> id1, id2
            cfw.add(ByteCode.DUP2);

            // SET id2 = id2 + 1
            cfw.add(ByteCode.LCONST_1);
            cfw.add(ByteCode.LADD);

            // PUT id2 -> myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.DUP_X2); // swap reference to below long.
            cfw.add(ByteCode.POP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "J");

            // Return id1
            cfw.add(ByteCode.LRETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        EvalLong exec = (EvalLong)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0L, getLongField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 0L, exec.eval());
        assertEquals("Wrong value for myObj.id", 1L, getLongField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1L, exec.eval());
        assertEquals("Wrong value for myObj.id", 2L, getLongField(myClass, exec, "id"));
        setLongField(myClass, exec, "id", Long.MAX_VALUE);
        assertEquals("Wrong value for myObj.id", Long.MAX_VALUE, getLongField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", Long.MAX_VALUE, exec.eval());
        assertEquals("Wrong value for myObj.id", Long.MIN_VALUE, getLongField(myClass, exec, "id"));
    }

    public void testDLoadStore() throws Exception
    {
        // Generate Class
        startClass("MyClass", "java/lang/Runnable");
        defaultConstructor();

        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "D");

            // SET id = id + 1
            cfw.add(ByteCode.DCONST_1);
            cfw.add(ByteCode.DADD);

            // PUT myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.DUP_X2); // swap reference to below long.
            cfw.add(ByteCode.POP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "D");

            cfw.add(ByteCode.RETURN);
            cfw.stopMethod((short) 2);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Runnable exec = (Runnable)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0d, getDoubleField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 1.0d, getDoubleField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 2.0d, getDoubleField(myClass, exec, "id"));
        setDoubleField(myClass, exec, "id", Double.NEGATIVE_INFINITY);
        assertEquals("Wrong value for myObj.id", Double.NEGATIVE_INFINITY, getDoubleField(myClass, exec, "id"));
        setDoubleField(myClass, exec, "id", Double.POSITIVE_INFINITY);
        assertEquals("Wrong value for myObj.id", Double.POSITIVE_INFINITY, getDoubleField(myClass, exec, "id"));
        setDoubleField(myClass, exec, "id", Double.NaN);
        assertEquals("Wrong value for myObj.id", Double.NaN, getDoubleField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", Double.NaN, getDoubleField(myClass, exec, "id"));
    }

    public void testDoublePreIncrement() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$EvalDouble");
        defaultConstructor();

        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("eval", "()D", (short) (ClassFileWriter.ACC_PUBLIC));

            // Duplicate this -> this1, this2
            cfw.addLoadThis();
            cfw.add(ByteCode.DUP);
            // GET this2.id -> id
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "D");

            // SET id = id + 1
            cfw.add(ByteCode.DCONST_1);
            cfw.add(ByteCode.DADD);

            // Duplicate this1, this2, id -> this1, id1, this2, id2
            cfw.add(ByteCode.DUP2_X1);

            // PUT this2.id = id2
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "D");

            // Return id1
            cfw.add(ByteCode.DRETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        EvalDouble exec = (EvalDouble)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0d, getDoubleField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1.0d, exec.eval());
        assertEquals("Wrong value for myObj.id", 1.0d, getDoubleField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 2.0d, exec.eval());
        assertEquals("Wrong value for myObj.id", 2.0d, getDoubleField(myClass, exec, "id"));
    }

    public void testDoublePostIncrement() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$EvalDouble");
        defaultConstructor();

        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("eval", "()D", (short) (ClassFileWriter.ACC_PUBLIC));

            // GET myclass.id -> id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "D");

            // Duplicate id -> id1, id2
            cfw.add(ByteCode.DUP2);

            // SET id2 = id2 + 1
            cfw.add(ByteCode.DCONST_1);
            cfw.add(ByteCode.DADD);

            // PUT id2 -> myclass.id
            cfw.addLoadThis();
            cfw.add(ByteCode.DUP_X2); // swap reference to below long.
            cfw.add(ByteCode.POP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "D");

            // Return id1
            cfw.add(ByteCode.DRETURN);
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        EvalDouble exec = (EvalDouble)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0d, getDoubleField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 0.0d, exec.eval());
        assertEquals("Wrong value for myObj.id", 1.0d, getDoubleField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1.0d, exec.eval());
        assertEquals("Wrong value for myObj.id", 2.0d, getDoubleField(myClass, exec, "id"));
    }

    public void testSwapLong() throws Exception
    {
        // Generate Class
        startClass("MyClass", "org/mozilla/classfile/LoadStoreTest$UnaryLong");
        defaultConstructor();

        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);

        // Generate public void run()
        {
            cfw.startMethod("unary", "(J)J", (short) (ClassFileWriter.ACC_PUBLIC));

            // Load $1 -> $1
            cfw.addLLoad(1);

            // GET myclass.id -> $1, id
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "J");

            // $1, id -> id, $1, id
            cfw.add(ByteCode.DUP2_X2); // swap reference to below long.
            cfw.add(ByteCode.POP2);
            cfw.addLoadThis();

            // id, $1, this -> id, this, $1
            cfw.add(ByteCode.DUP_X2); // swap reference to below long.
            cfw.add(ByteCode.POP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "J");

            // Return id
            cfw.add(ByteCode.LRETURN);
            cfw.stopMethod((short) 3);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0L, getLongField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 0L, exec.unary(3L));
        assertEquals("Wrong value for myObj.id", 3L, getLongField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 3L, exec.unary(-5L));
        assertEquals("Wrong value for myObj.id", -5L, getLongField(myClass, exec, "id"));
    }
}
