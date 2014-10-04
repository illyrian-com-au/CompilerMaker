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


public class ClassFileWriterTest extends ClassFileWriterTestCase
{

    public void testConstructorHelloWorld() throws Exception
    {
        // Generate Class
        ClassFileWriter cfw = new ClassFileWriter("MyClass", "java/lang/Object", "MyClass.java");
        cfw.addInterface("java/lang/Runnable");

        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);

        // Generate constructor
        {
            cfw.startMethod("<init>", "()V", ClassFileWriter.ACC_PUBLIC);
            cfw.addLoadThis();
            cfw.addInvoke(ByteCode.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");

            // System.out.println("Hello World");
            cfw.add(ByteCode.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            cfw.addLoadConstant("MyClass constructor");
            cfw.addInvoke(ByteCode.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");

            // set id to 1
            cfw.addLoadThis();
            cfw.addPush(1);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

            cfw.add(ByteCode.RETURN);
            // 1 parameter = this
            cfw.stopMethod((short) 1);
        }

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Object myObj = myClass.newInstance();

        int id = getIntField(myClass, myObj, "id");
        assertEquals("myObj.id should be 1", 1, id);

    }

    public void testRunnableHelloWorld() throws Exception
    {
        // Generate Class
        ClassFileWriter cfw = new ClassFileWriter("MyClass", "java/lang/Object", "MyClass.java");
        cfw.addInterface("java/lang/Runnable");

        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);

        // Generate default constructor
        {
            cfw.startMethod("<init>", "()V", ClassFileWriter.ACC_PUBLIC);
            cfw.addLoadThis();
            cfw.addInvoke(ByteCode.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");

            cfw.add(ByteCode.RETURN);
            // 1 parameter = this
            cfw.stopMethod((short) 1);
        }
        // Generate public void run()
        {
            cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

            // System.out.println("Hello World");
            cfw.add(ByteCode.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            cfw.addLoadConstant("Runnable.run()");
            cfw.addInvoke(ByteCode.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");

            // set id to 2
            cfw.addLoadThis();
            cfw.addPush(2);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

            cfw.add(ByteCode.RETURN);
            // 1 = String[] args
            cfw.stopMethod((short) 1);
        }


        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Object myObj = myClass.newInstance();

        int id =getIntField(myClass, myObj, "id");
        assertEquals("myObj.id should be 0", 0, id);

        Runnable exec = (Runnable)myObj;
        exec.run();

        id = getIntField(myClass, myObj, "id");
        assertEquals("myObj.id should be 2", 2, id);
    }

    public void testStaticHelloWorld() throws Exception
    {
        // Generate Class
        ClassFileWriter cfw = new ClassFileWriter("MyClass", "java/lang/Object", "MyClass.java");
        cfw.addField("sid", "I", (short)(ClassFileWriter.ACC_STATIC
                                       | ClassFileWriter.ACC_PUBLIC));

        // Generate public static void main(String [] args)
        {
            cfw.startMethod("main", "([Ljava/lang/String;)V",
                    (short) (ClassFileWriter.ACC_PUBLIC | ClassFileWriter.ACC_STATIC));

            // System.out.println("Hello World");
            // getstatic java/lang/System/out Ljava/io/PrintStream;
            cfw.add(ByteCode.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            // ldc "Hello World"
            cfw.addLoadConstant("MyClass.main(String [] args)");
            // invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
            cfw.addInvoke(ByteCode.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");

            // set sid to 3
            cfw.addPush(3);
            //cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");
            cfw.add(ByteCode.PUTSTATIC,  cfw.getClassName(), "sid", "I");

            cfw.add(ByteCode.RETURN);
            // 1 = String[] args
            cfw.stopMethod((short) 1);
        }
        Class myClass = defineClass("MyClass", cfw.toByteArray());

        int sid = getIntField(myClass, null, "sid");
        assertEquals("myObj.sid should be 0", 0, sid);

        invokeMain(myClass, null);

        sid = getIntField(myClass, null, "sid");
        assertEquals("myObj.id should be 3", 3, sid);
    }
}
