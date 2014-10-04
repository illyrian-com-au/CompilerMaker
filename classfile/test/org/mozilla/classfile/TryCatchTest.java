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

import java.io.FileNotFoundException;

public class TryCatchTest extends ClassFileWriterTestCase
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

    public interface UnaryChecked
    {
        int unary(int a) throws FileNotFoundException;
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

    public static final String ILLEGAL_ARGUMENT_EXCEPTION = "java/lang/IllegalArgumentException";

    public void testThrowUncheckedException() throws Exception
    {
        String CallClassStr = "MyClass";

        startClass(CallClassStr, "org/mozilla/classfile/TryCatchTest$Unary");
        defaultConstructor();

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        {
            // $1 = new IllegalArgumentException("Too big string");
            cfw.add(ByteCode.NEW, ILLEGAL_ARGUMENT_EXCEPTION);
            cfw.add(ByteCode.DUP);
            cfw.addPush("Too big string");
            cfw.addInvoke(ByteCode.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, "<init>", "(Ljava/lang/String;)V");
            cfw.addAStore(1);

            // throw $1;
            cfw.addALoad(1);
            cfw.add(ByteCode.ATHROW);

            // return 2;
            cfw.addPush(2);
            cfw.add(ByteCode.IRETURN);
        }

        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        try {
            exec.unary(0);
            fail("Expected IllegalArgumentException().");
        } catch (IllegalArgumentException ex) {};
    }

    public void testThrowCheckedException() throws Exception
    {
        String CallClassStr = "MyClass";

        startClass(CallClassStr, "org/mozilla/classfile/TryCatchTest$UnaryChecked");
        defaultConstructor();

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        {
            // $1 = new FileNotFoundException("File not there");
            cfw.add(ByteCode.NEW, FILE_NOT_FOUND_EXCEPTION);
            cfw.add(ByteCode.DUP);
            cfw.addPush("File not there");
            cfw.addInvoke(ByteCode.INVOKESPECIAL, FILE_NOT_FOUND_EXCEPTION, "<init>", "(Ljava/lang/String;)V");
            cfw.addAStore(1);

            // throw $1;
            cfw.addALoad(1);
            cfw.add(ByteCode.ATHROW);

            // return 2;
            cfw.addPush(2);
            cfw.add(ByteCode.IRETURN);
        }

        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        try {
            exec.unary(0);
            fail("Expected FileNotFoundException().");
        } catch (FileNotFoundException ex) {};
    }

    public static class ThrowClass implements UnaryChecked
    {
        public int unary(int a) throws FileNotFoundException
        {
            switch (a)
            {
            case 1 :
                throw new IllegalArgumentException();
            case 2 :
                throw new FileNotFoundException();
            case 3 :
                throw new IllegalStateException();
            }
            return 0;
        }
    }

    public void testTableSwitchMethod() throws Exception
    {
        String CallClassStr = "CallBinaryOperatorClass";

        startClass(CallClassStr, "org/mozilla/classfile/TryCatchTest$Unary");
        defaultConstructor();

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $2 = 0
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);

        cfw.addILoad(1);
        int startSwitch = cfw.addTableSwitch(0, 2);
        int endlabel = cfw.acquireLabel();

        //cfw.markTableSwitchCase(startSwitch, 0, (short)6);
        cfw.markTableSwitchCase(startSwitch, 0);
        cfw.add(ByteCode.ICONST_1);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endlabel);

        cfw.markTableSwitchCase(startSwitch, 1);
        cfw.add(ByteCode.ICONST_2);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endlabel);

        cfw.markTableSwitchCase(startSwitch, 2);
        cfw.add(ByteCode.ICONST_3);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endlabel);

        cfw.markTableSwitchDefault(startSwitch);
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);

        cfw.markLabel(endlabel);

        cfw.addILoad(2);
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(30));
    }

    public void testThrowException() throws Exception
    {
        String CallClassStr = "MyClass";

        startClass(CallClassStr, "org/mozilla/classfile/TryCatchTest$UnaryChecked");
        defaultConstructor();

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $2 = 0
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);

        cfw.addILoad(1);
        int startSwitch = cfw.addTableSwitch(0, 2);
        int endlabel = cfw.acquireLabel();

        //CASE 1 : throw new IllegalArgumentException();
        cfw.markTableSwitchCase(startSwitch, 0);
        {
            // $1 = new IllegalArgumentException("Too big string");
            cfw.add(ByteCode.NEW, ILLEGAL_ARGUMENT_EXCEPTION);
            cfw.add(ByteCode.DUP);
            cfw.addPush("Too big string");
            cfw.addInvoke(ByteCode.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, "<init>", "(Ljava/lang/String;)V");
            cfw.addAStore(3);

            // Throw $3;
            cfw.addALoad(3);
            cfw.add(ByteCode.ATHROW);
        }
        // Break;
        cfw.add(ByteCode.GOTO, endlabel);

        //CASE 2 :  throw new FileNotFoundException();
        cfw.markTableSwitchCase(startSwitch, 1);
        {
            // $1 = new IllegalArgumentException("Too big string");
            cfw.add(ByteCode.NEW, FILE_NOT_FOUND_EXCEPTION);
            cfw.add(ByteCode.DUP);
            cfw.addPush("Its not there!");
            cfw.addInvoke(ByteCode.INVOKESPECIAL, FILE_NOT_FOUND_EXCEPTION, "<init>", "(Ljava/lang/String;)V");
            cfw.addAStore(3);

            // Throw $3;
            cfw.addALoad(3);
            cfw.add(ByteCode.ATHROW);
        }
        // Break;
        cfw.add(ByteCode.GOTO, endlabel);

        //CASE 3 :  throw new IllegalStateException();
        cfw.markTableSwitchCase(startSwitch, 2);
        {
            // $1 = new IllegalArgumentException("Too big string");
            cfw.add(ByteCode.NEW, ILLEGAL_STATE_EXCEPTION);
            cfw.add(ByteCode.DUP);
            cfw.addPush("Bad stuff");
            cfw.addInvoke(ByteCode.INVOKESPECIAL, ILLEGAL_STATE_EXCEPTION, "<init>", "(Ljava/lang/String;)V");
            cfw.addAStore(3);

            // Throw $3;
            cfw.addALoad(3);
            cfw.add(ByteCode.ATHROW);
        }
        // Break;
        cfw.add(ByteCode.GOTO, endlabel);

        // DEFAULT :
        cfw.markTableSwitchDefault(startSwitch);
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);

        cfw.markLabel(endlabel);

        cfw.addILoad(2);
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 4);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(10));
        try {
            exec.unary(0);
            fail("Expected IllegalArgumentException().");
        } catch (IllegalArgumentException ex) {};
        try {
            exec.unary(1);
            fail("Expected FileNotFoundException().");
        } catch (FileNotFoundException ex) {};
        try {
            exec.unary(2);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {};
    }

    public static final String UNARY_CHECKED = "org/mozilla/classfile/TryCatchTest$UnaryChecked";
    public static final String UNARY_CHECKED_S = "Lorg/mozilla/classfile/TryCatchTest$UnaryChecked;";

    public void testCatchNoExceptions() throws Exception
    {
        String CallClassStr = "MyClass";

        startClass(CallClassStr, "org/mozilla/classfile/TryCatchTest$UnaryChecked");
        defaultConstructor();
        cfw.addField("func", UNARY_CHECKED_S, ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        {
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "func", UNARY_CHECKED_S);
            cfw.addILoad(1);
            cfw.addInvoke(ByteCode.INVOKEINTERFACE, UNARY_CHECKED, "unary", "(I)I");

            // return $2;
            // cfw.addILoad(2);
            cfw.add(ByteCode.IRETURN);
        }

        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        try {
            exec.unary(1);
            fail("Expected IllegalArgumentException().");
        } catch (IllegalArgumentException ex) {};
        try {
            exec.unary(2);
            fail("Expected FileNotFoundException().");
        } catch (FileNotFoundException ex) {};
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {};
    }

    public static final String FILE_NOT_FOUND_EXCEPTION = "java/io/FileNotFoundException";
    public static final String ILLEGAL_ARGUEMENT_EXCEPTION = "java/lang/IllegalArgumentException";
    public static final String ILLEGAL_STATE_EXCEPTION = "java/lang/IllegalStateException";

    public void testCatchExceptions() throws Exception
    {
        String CallClassStr = "MyClass";

        startClass(CallClassStr, "org/mozilla/classfile/TryCatchTest$UnaryChecked");
        defaultConstructor();
        cfw.addField("func", UNARY_CHECKED_S, ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        {
            int startTryBlock = cfw.acquireLabel();
            int endTryBlock = cfw.acquireLabel();
            int endCatchBlock = cfw.acquireLabel();

            // Try {
            cfw.markLabel(startTryBlock);

            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "func", UNARY_CHECKED_S);
            cfw.addILoad(1);
            cfw.addInvoke(ByteCode.INVOKEINTERFACE, UNARY_CHECKED, "unary", "(I)I");
            cfw.addIStore(1);

            cfw.markLabel(endTryBlock);
            cfw.add(ByteCode.GOTO, endCatchBlock);

            //catch (FileNotFoundException ex)
            int catchBlock1 = cfw.acquireLabel();
            cfw.addExceptionHandler(startTryBlock, endTryBlock, catchBlock1, FILE_NOT_FOUND_EXCEPTION);
            cfw.markLabel(catchBlock1);
            cfw.adjustStackTop(1); // exception pointer pushed onto stack.
            cfw.add(ByteCode.POP);
            cfw.addLoadConstant(10000);
            cfw.addIStore(1);
            cfw.add(ByteCode.GOTO, endCatchBlock);

            //catch (IllegalArgumentException ex)
            int catchBlock2 = cfw.acquireLabel();
            cfw.addExceptionHandler(startTryBlock, endTryBlock, catchBlock2, ILLEGAL_ARGUEMENT_EXCEPTION);
            cfw.markLabel(catchBlock2);
            cfw.adjustStackTop(1); // exception pointer pushed onto stack.
            cfw.add(ByteCode.POP);
            cfw.addLoadConstant(20000);
            cfw.addIStore(1);
            cfw.add(ByteCode.GOTO, endCatchBlock);

            //catch (*)
            int catchBlock3 = cfw.acquireLabel();
            cfw.addExceptionHandler(startTryBlock, endTryBlock, catchBlock3, null);
            cfw.markLabel(catchBlock3);
            cfw.adjustStackTop(1); // exception pointer pushed onto stack.
            cfw.addAStore(2);
            // Do stuff ...
            cfw.addLoadConstant(30000);
            cfw.addIStore(1);
            // Rethrow exception.
            cfw.addALoad(2);
            cfw.add(ByteCode.ATHROW);

            // Finaly block
            int finalyBlock = cfw.acquireLabel();
            cfw.add(ByteCode.JSR, finalyBlock);
            cfw.markLabel(finalyBlock);
            cfw.addAStore(1); // Store return address in Local 1

            cfw.add(ByteCode.RET, 1);

            // End Try Catch Finaly
            cfw.markLabel(endCatchBlock);

            cfw.addILoad(1);
            cfw.add(ByteCode.IRETURN);
        }

        cfw.stopMethod((short) 3);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 20000, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 10000, exec.unary(2));
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {};
    }

    public void testCatchFinaly() throws Exception
    {
        String CallClassStr = "MyClass";

        startClass(CallClassStr, "org/mozilla/classfile/TryCatchTest$UnaryChecked");
        defaultConstructor();
        cfw.addField("func", UNARY_CHECKED_S, ClassFileWriter.ACC_PUBLIC);
        cfw.addField("val", "I", ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        {
            int startTryBlock = cfw.acquireLabel();
            int endTryBlock = cfw.acquireLabel();
            int endCatchBlock = cfw.acquireLabel();
            int finalyBlock = cfw.acquireLabel();

            // Try {
            cfw.markLabel(startTryBlock);

            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "func", UNARY_CHECKED_S);
            cfw.addILoad(1);
            cfw.addInvoke(ByteCode.INVOKEINTERFACE, UNARY_CHECKED, "unary", "(I)I");
            cfw.addIStore(1);

            cfw.markLabel(endTryBlock);
            cfw.add(ByteCode.GOTO, endCatchBlock);

            // } catch (FileNotFoundException ex) {
            int catchBlock1 = cfw.acquireLabel();
            cfw.addExceptionHandler(startTryBlock, endTryBlock, catchBlock1, FILE_NOT_FOUND_EXCEPTION);
            cfw.markLabel(catchBlock1);
            cfw.adjustStackTop(1); // exception pointer pushed onto stack.
            cfw.add(ByteCode.POP);
            cfw.addLoadConstant(10000);
            cfw.addIStore(1);
            cfw.add(ByteCode.GOTO, endCatchBlock);

            // } catch (IllegalArgumentException ex) {
            int catchBlock2 = cfw.acquireLabel();
            cfw.addExceptionHandler(startTryBlock, endTryBlock, catchBlock2, ILLEGAL_ARGUEMENT_EXCEPTION);
            cfw.markLabel(catchBlock2);
            cfw.adjustStackTop(1); // exception pointer pushed onto stack.
            cfw.add(ByteCode.POP);
            cfw.addLoadConstant(20000);
            cfw.addIStore(1);
            cfw.add(ByteCode.GOTO, endCatchBlock);

            // } catch (IllegalStateException ex) {
            int catchBlock3 = cfw.acquireLabel();
            cfw.addExceptionHandler(startTryBlock, endTryBlock, catchBlock3, ILLEGAL_STATE_EXCEPTION);
            cfw.markLabel(catchBlock3);
            cfw.adjustStackTop(1); // exception pointer pushed onto stack.
            cfw.addAStore(2);
            cfw.addLoadConstant(20000);
            cfw.addIStore(1);
            // Rethrow exception.
            cfw.addALoad(2);
            cfw.add(ByteCode.ATHROW);
            cfw.add(ByteCode.GOTO, endCatchBlock);

            // } finaly {
            int catchBlockAll = cfw.acquireLabel();
            cfw.addExceptionHandler(startTryBlock, catchBlockAll, catchBlockAll, null);
            cfw.markLabel(catchBlockAll);
            cfw.adjustStackTop(1); // exception pointer pushed onto stack.
            cfw.addAStore(2);

            cfw.add(ByteCode.JSR, finalyBlock);

            // Rethrow exception.
            cfw.addALoad(2);
            cfw.add(ByteCode.ATHROW);

            // Finaly subroutine
            cfw.markLabel(finalyBlock);
            cfw.addAStore(3); // Store return address

            // this.val = this.val + 1;
            cfw.addLoadThis();
            cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "val", "I");
            cfw.add(ByteCode.ICONST_1);
            cfw.add(ByteCode.IADD);
            cfw.addLoadThis();
            cfw.add(ByteCode.SWAP);
            cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "val", "I");

            cfw.add(ByteCode.RET, 3);

            // } EndTry
            cfw.markLabel(endCatchBlock);
            cfw.add(ByteCode.JSR, finalyBlock);

            cfw.addILoad(1);
            cfw.add(ByteCode.IRETURN);
        }
        cfw.stopMethod((short) 4);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        int count = 100;
        setIntField(exec.getClass(), exec, "val", count);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        assertEquals("Wrong value for exec.unary()", 20000, exec.unary(1));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        assertEquals("Wrong value for exec.unary()", 10000, exec.unary(2));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {};
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
    }

}
