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

public class InvokeTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface Eval
    {
        int eval();
    }

    public interface Unary
    {
        int unary(int a);
    }

    public interface UnaryObject
    {
        int unary(Object a);
    }

    public interface Binary
    {
        int binary(int x, int y);
    }

    public static class RunnableClass implements Runnable
    {
        public int id;
        public void run()
        {
            id = 5;
        }

        public int getId()
        {
            return id;
        }

        public void setId(int value)
        {
            id = value;
        }
    }

    public static class StaticClass
    {
        public static int id;

        public static int getId()
        {
            return id;
        }

        public static void setId(int value)
        {
            id = value;
        }
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

    public static final String RUNNABLE_CLASS = "RunnableClass";

    // Generate public void run()
    public void runMethod() throws Exception
    {
        startClass(RUNNABLE_CLASS, "java/lang/Runnable");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 2
        cfw.addLoadThis();
        cfw.addPush(2);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

        cfw.add(ByteCode.RETURN);
        cfw.stopMethod((short) 1);
    }

    public void testRunMethod() throws Exception
    {
        runMethod();

        Class class1 = defineClass(RUNNABLE_CLASS, cfw.toByteArray());
        Runnable exec =  (Runnable)class1.newInstance();

        assertEquals("Wrong initial value for myObj.id", 0, getIntField(class1, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 2, getIntField(class1, exec, "id"));
   }

    public void testJavaRunMethod() throws Exception
    {
        Runnable exec = new RunnableClass();

        int id =getIntField(exec.getClass(), exec, "id");
        assertEquals("Wrong initial value for myObj.id", 0, id);
        exec.run();
        id = getIntField(exec.getClass(), exec, "id");
        assertEquals("Wrong value for myObj.id", 5, id);
    }

    public void testCallRunnable() throws Exception
    {
        String RunnableClassStr = "org/mozilla/classfile/InvokeTest$RunnableClass";
        String CallClassStr = "CallAddClass";

        startClass(CallClassStr, "org/mozilla/classfile/InvokeTest$Eval");
        defaultConstructor();

        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        // $1 = new RunnableClass();
        cfw.add(ByteCode.NEW, RunnableClassStr);
        cfw.add(ByteCode.DUP);
        cfw.addInvoke(ByteCode.INVOKESPECIAL, RunnableClassStr, "<init>", "()V");
        cfw.addAStore(1);

        // Call $1.run();
        cfw.addALoad(1);
        cfw.addInvoke(ByteCode.INVOKEINTERFACE, "java/lang/Runnable", "run", "()V");

        // Return $1.id
        cfw.addALoad(1);
        cfw.add(ByteCode.GETFIELD, RunnableClassStr, "id", "I");
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        //assertEquals("Wrong value for myObj.id", 0, getIntField(myClass, exec, "id"));

        assertEquals("Wrong value for exec.eval()", 5, exec.eval());

        //assertEquals("Wrong value for myObj.id", 0, getIntField(myClass, exec, "id"));
    }

    public void testCallSetGet() throws Exception
    {
        String RunnableClassStr = "org/mozilla/classfile/InvokeTest$RunnableClass";
        String CallClassStr = "CallAddClass";

        startClass(CallClassStr, "org/mozilla/classfile/InvokeTest$Eval");
        defaultConstructor();

        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        // $1 = new RunnableClass();
        cfw.add(ByteCode.NEW, RunnableClassStr);
        cfw.add(ByteCode.DUP);
        cfw.addInvoke(ByteCode.INVOKESPECIAL, RunnableClassStr, "<init>", "()V");
        cfw.addAStore(1);

        // Call $1.setId(5);
        cfw.addALoad(1);
        cfw.add(ByteCode.ICONST_5);
        cfw.addInvoke(ByteCode.INVOKEVIRTUAL, RunnableClassStr, "setId", "(I)V");

        // Return $1.getId();
        cfw.addALoad(1);
        cfw.addInvoke(ByteCode.INVOKEVIRTUAL, RunnableClassStr, "getId", "()I");
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        //assertEquals("Wrong value for myObj.id", 0, getIntField(myClass, exec, "id"));

        assertEquals("Wrong value for exec.eval()", 5, exec.eval());

        //assertEquals("Wrong value for myObj.id", 0, getIntField(myClass, exec, "id"));
    }

    public void testCallStatic() throws Exception
    {
        String StaticClassStr = "org/mozilla/classfile/InvokeTest$StaticClass";

        startClass("CallClass", "org/mozilla/classfile/InvokeTest$Eval");
        defaultConstructor();

        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        // Call StaticClass.setId(4);
        cfw.add(ByteCode.ICONST_4);
        cfw.addInvoke(ByteCode.INVOKESTATIC, StaticClassStr, "setId", "(I)V");

        // Return StaticClass.getId();
        cfw.add(ByteCode.GETSTATIC, StaticClassStr, "id", "I");
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 1);

        Class myClass = defineClass("CallClass", cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 4, exec.eval());
    }

    public static final String BINARY_CLASS = "org/mozilla/classfile/BinaryClass";

    public Class binaryOperatorClass(int opCode) throws Exception
    {
        startClass(BINARY_CLASS, "org/mozilla/classfile/InvokeTest$Binary");
        defaultConstructor();
        cfw.startMethod("binary", "(II)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addILoad(1);  // 1st Argument
        cfw.addILoad(2);  // 2nd Argument
        cfw.add(opCode);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        return defineClass(BINARY_CLASS, cfw.toByteArray());
    }

    public void testBinaryOperator() throws Exception
    {
        Class myClass = binaryOperatorClass(ByteCode.IADD);
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.binary()", 7, exec.binary(2, 5));
    }

    public void testCallBinaryOperatorMethod() throws Exception
    {
        binaryOperatorClass(ByteCode.IMUL);

        String CallClassStr = "CallBinaryOperatorClass";

        startClass(CallClassStr, "org/mozilla/classfile/InvokeTest$Unary");
        defaultConstructor();
        cfw.addField("val", "I", ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // $2 = new BinaryClass();
        cfw.add(ByteCode.NEW, BINARY_CLASS);
        cfw.add(ByteCode.DUP);
        cfw.addInvoke(ByteCode.INVOKESPECIAL, BINARY_CLASS, "<init>", "()V");
        cfw.addAStore(2);

        // return $2.binary($1, this.val);
        cfw.addALoad(2);
        cfw.addILoad(1);
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "val", "I");
        cfw.addInvoke(ByteCode.INVOKEVIRTUAL, BINARY_CLASS, "binary", "(II)I");
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(3));
        setIntField(exec.getClass(), exec, "val", 2);
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", -4, exec.unary(-2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
    }

    // Same as previous test except calling the Binary interface.
    public void testCallBinaryOperatorInterface() throws Exception
    {
        binaryOperatorClass(ByteCode.IMUL);

        String CallClassStr = "CallBinaryOperatorClass";

        startClass(CallClassStr, "org/mozilla/classfile/InvokeTest$Unary");
        defaultConstructor();
        cfw.addField("val", "I", ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // $2 = new BinaryClass();
        cfw.add(ByteCode.NEW, BINARY_CLASS);
        cfw.add(ByteCode.DUP);
        cfw.addInvoke(ByteCode.INVOKESPECIAL, BINARY_CLASS, "<init>", "()V");
        cfw.addAStore(2);

        // return $2.binary($1, this.val);
        cfw.addALoad(2);
        cfw.addILoad(1);
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "val", "I");
        cfw.addInvoke(ByteCode.INVOKEINTERFACE, "org/mozilla/classfile/InvokeTest$Binary", "binary", "(II)I");
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(3));
        setIntField(exec.getClass(), exec, "val", 2);
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", -4, exec.unary(-2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
    }
}
