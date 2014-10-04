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

public class SynchronisationTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface Eval
    {
        int eval();
    }

    private static class Inc2 implements Eval
    {
        public static int id;
        int count;
        public int eval()
        {
            count = 100;
            int result = 0;
            {
                id++;
                while (count > 0) {count--;}
                id++;
                result = id;
            }
            return result;
        }
    }

//    private static class SyncInc2 implements Eval
//    {
//        public static int id;
//        int count;
//        public int eval()
//        {
//            count = 1000;
//            int result = 0;
//            synchronized(this.getClass())
//            {
//                id++;
//                while (count > 0) {count--;}
//                id++;
//                result = id;
//            }
//            return result;
//        }
//    }

    private static class Execute implements Runnable
    {
        public static boolean wait = true;

        public Eval eval;
        public Execute(Eval evaluator)
        {
            eval = evaluator;
        }

        public void run()
        {
            // Spinning wait.
            while (wait == true) { /* Do nothing */ }

            for (int i=0; i<1000; i++)
            {
                int val = eval.eval();

                if ((val % 2) != 0)
                {
                    try {
                        ClassFileWriterTestCase.setIntField(Inc2.class, null, "id", -1);
                    } catch (Exception ex) {};
                    break;
                }
            }
        }
    }

    /**
     * This test is the equivalent of testByteCodeInc2 written in Java.
     * @throws Exception
     */
    public void testJavaInc2() throws Exception
    {
        Inc2.id = 0;
        Eval exec = new Inc2();

        assertEquals("myObj.id should be 0", 0, getIntField(Inc2.class, exec, "id"));
        assertEquals("Wrong incremented value", 2, exec.eval());
        assertEquals("Wrong incremented value", 4, exec.eval());
        assertEquals("Wrong incremented value", 6, exec.eval());
    }

    /**
     * This test is the equivalent of testByteCodeInc2 written in Java.
     * @throws Exception
     */
    public void testJavaNonSynchronousInc2() throws Exception
    {
        setIntField(Inc2.class, null, "id", 0);
        Eval increment1 = new Inc2();
        Thread thread1 = new Thread(new Execute(increment1));
        Eval increment2 = new Inc2();
        Thread thread2 = new Thread(new Execute(increment2));

        assertEquals("myObj.id should be 0", 0, getIntField(Inc2.class, null, "id"));
        thread1.start();
        thread2.start();
        Execute.wait = false;
        thread1.join(5000);
        thread2.join(5000);
        // TODO fix me
        //assertEquals("Wrong incremented value", -1, getIntField(Inc2.class, null, "id"));
    }

    /**
     * This test is the equivalent of testByteCodeInc2 written in Java.
     * @throws Exception
     */
//    public void testJavaSynchronousInc2() throws Exception
//    {
//        Eval increment1 = new SyncInc2();
//        Class clazz = increment1.getClass();
//        Thread thread1 = new Thread(new Execute(increment1));
//        Eval increment2 = new SyncInc2();
//        Thread thread2 = new Thread(new Execute(increment2));
//        setIntField(clazz, increment1, "id", 0);
//
//        assertEquals("myObj.id should be 0", 0, getIntField(clazz, null, "id"));
//        thread1.start();
//        thread2.start();
//        Execute.wait = false;
//        thread1.join(5000);
//        thread2.join(5000);
//        assertEquals("Wrong incremented value", 4000, getIntField(clazz, null, "id"));
//    }

    public void startClass(String className, String iface) throws Exception
    {
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

    // Generate public int eval()
    public void evalMethod(boolean sync) throws Exception
    {
        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addField("id", "I", (short)(ClassFileWriter.ACC_PUBLIC | ClassFileWriter.ACC_STATIC));

        // $2 = this.getClass();
        cfw.addLoadThis();
        cfw.addInvoke(ByteCode.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
        cfw.addAStore(2);

        if (sync)
        {
            cfw.addALoad(2);
            cfw.add(ByteCode.MONITORENTER);
        }

        // $1 = this.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETSTATIC, cfw.getClassName(), "id", "I");
        cfw.addIStore(1);

        // Inc $1
        cfw.add(ByteCode.IINC, 1, 1);

        // this.id = $1
        cfw.addLoadThis();
        cfw.addILoad(1);
        cfw.add(ByteCode.PUTSTATIC, cfw.getClassName(), "id", "I");

        // $1 = this.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETSTATIC, cfw.getClassName(), "id", "I");
        cfw.addIStore(1);

        // Inc $1
        cfw.add(ByteCode.IINC, 1, 1);

        // this.id = $1
        cfw.addLoadThis();
        cfw.addILoad(1);
        cfw.add(ByteCode.PUTSTATIC, cfw.getClassName(), "id", "I");

        if (sync)
        {
            cfw.addALoad(2);
            cfw.add(ByteCode.MONITOREXIT);
        }

       // Return $1
        cfw.addILoad(1);
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);
    }

    public void testByteCodeInc2() throws Exception
    {
        startClass("ByteCodeInc2", "org/mozilla/classfile/SynchronisationTest$Eval");
        defaultConstructor();
        evalMethod(false);

        Class myClass = defineClass("ByteCodeInc2", cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("myObj.id should be 0", 0, getIntField(myClass, null, "id"));
        assertEquals("Wrong incremented value", 2, exec.eval());
        assertEquals("Wrong incremented value", 4, exec.eval());
        assertEquals("Wrong incremented value", 6, exec.eval());
    }

    /**
     * This test is the equivalent of testByteCodeInc2 written in Java.
     * @throws Exception
     */
    public void testByteCodeNonSynchronousInc2() throws Exception
    {
        startClass("Inc2Non", "org/mozilla/classfile/SynchronisationTest$Eval");
        defaultConstructor();
        evalMethod(false);
        Class myClass = defineClass("Inc2Non", cfw.toByteArray());

        setIntField(myClass, null, "id", 0);
        Eval increment1 = (Eval)myClass.newInstance();
        Thread thread1 = new Thread(new Execute(increment1));
        Eval increment2 = (Eval)myClass.newInstance();
        Thread thread2 = new Thread(new Execute(increment2));

        assertEquals("myObj.id should be 0", 0, getIntField(myClass, null, "id"));
        thread1.start();
        thread2.start();
        Execute.wait = false;
        thread1.join(5000);
        thread2.join(5000);
        // TODO - fix me
        //assertEquals("Wrong incremented value", -1, getIntField(myClass, null, "id"));
    }

    /**
     * This test is the equivalent of testByteCodeInc2 written in Java.
     * @throws Exception
     */
//    public void testByteCodeSynchronousInc2() throws Exception
//    {
//        startClass("Inc2Sync", "org/mozilla/classfile/SynchronisationTest$Eval");
//        defaultConstructor();
//        evalMethod(true);
//        Class myClass = defineClass("Inc2Sync", cfw.toByteArray());
//        setIntField(myClass, null, "id", 0);
//
//        Eval increment1 = (Eval)myClass.newInstance();
//        Thread thread1 = new Thread(new Execute(increment1));
//        Eval increment2 = (Eval)myClass.newInstance();
//        Thread thread2 = new Thread(new Execute(increment2));
//
//        assertEquals("myObj.id should be 0", 0, getIntField(myClass, null, "id"));
//        thread1.start();
//        thread2.start();
//        Execute.wait = false;
//        thread1.join(5000);
//        thread2.join(5000);
//        assertEquals("Wrong incremented value", 4000, getIntField(myClass, null, "id"));
//    }


}
