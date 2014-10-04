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


public class SwitchTest extends ClassFileWriterTestCase
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

    public void testTableSwitchMethod() throws Exception
    {
        String CallClassStr = "CallBinaryOperatorClass";

        startClass(CallClassStr, "org/mozilla/classfile/SwitchTest$Unary");
        defaultConstructor();

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $2 = 0
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);

        cfw.addILoad(1);
        int startSwitch = cfw.addTableSwitch(0, 2);
        int endlabel = cfw.acquireLabel();

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

//        byte[] code = cfw.getCodeAttribute();
//        ClassFilePrinter printer = new ClassFilePrinter(System.out);
//        printer.byteCode(code);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(30));
    }

    public void testTableSwitchLabel() throws Exception
    {
        String CallClassStr = "CallBinaryOperatorClass";

        startClass(CallClassStr, "org/mozilla/classfile/SwitchTest$Unary");
        defaultConstructor();

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $2 = 0
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);

        int endSwitch = cfw.acquireLabel();
        int beginSwitch = cfw.acquireLabel();
        cfw.add(ByteCode.GOTO, beginSwitch);

        int case0 = cfw.acquireLabel();
        cfw.markLabel(case0);
        cfw.add(ByteCode.ICONST_1);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endSwitch);

        int case1 = cfw.acquireLabel();
        cfw.markLabel(case1);
        cfw.add(ByteCode.ICONST_2);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endSwitch);

        int case2 = cfw.acquireLabel();
        cfw.markLabel(case2);
        cfw.add(ByteCode.ICONST_3);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endSwitch);

        int caseDefault = cfw.acquireLabel();
        cfw.markLabel(caseDefault);
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endSwitch);

        cfw.markLabel(beginSwitch);
        cfw.addILoad(1);
        int startSwitch = cfw.addTableSwitch(1, 3);
        cfw.addTableSwitchCaseLabel(startSwitch, 0, case0);
        cfw.addTableSwitchCaseLabel(startSwitch, 1, case1);
        cfw.addTableSwitchCaseLabel(startSwitch, 2, case2);
        cfw.addTableSwitchDefaultLabel(startSwitch, caseDefault);

        cfw.markLabel(endSwitch);

        cfw.addILoad(2);
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

//        byte[] code = cfw.getCodeAttribute();
//        byteCode(code, 0);
//        ClassFilePrinter printer = new ClassFilePrinter(System.out);
//        printer.byteCode(code);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(4));
    }

    public void testLookupSwitchMethod() throws Exception
    {
        String CallClassStr = "CallBinaryOperatorClass";

        startClass(CallClassStr, "org/mozilla/classfile/SwitchTest$Unary");
        defaultConstructor();

        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $2 = 0
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);

        int endSwitch = cfw.acquireLabel();
        int beginSwitch = cfw.acquireLabel();
        cfw.add(ByteCode.GOTO, beginSwitch);

        int case0 = cfw.acquireLabel();
        cfw.markLabel(case0);
        cfw.add(ByteCode.ICONST_1);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endSwitch);

        int case1 = cfw.acquireLabel();
        cfw.markLabel(case1);
        cfw.add(ByteCode.ICONST_2);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endSwitch);

        int case2 = cfw.acquireLabel();
        cfw.markLabel(case2);
        cfw.add(ByteCode.ICONST_3);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endSwitch);

        int caseDefault = cfw.acquireLabel();
        cfw.markLabel(caseDefault);
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);
        cfw.add(ByteCode.GOTO, endSwitch);

        cfw.markLabel(beginSwitch);
        cfw.addILoad(1);
        int startSwitch = cfw.addLookupSwitch(3);
        cfw.addLookupSwitchCaseLabel(startSwitch, 0, -1, case0);
        cfw.addLookupSwitchCaseLabel(startSwitch, 1, 1, case1);
        cfw.addLookupSwitchCaseLabel(startSwitch, 2, 3, case2);
        cfw.addLookupSwitchDefaultLabel(startSwitch, caseDefault);

        cfw.markLabel(endSwitch);
        cfw.add(ByteCode.NOP);

        cfw.addILoad(2);
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        byte[] code = cfw.getCodeAttribute();
//        byteCode(code, 0);
        ClassFilePrinter printer = new ClassFilePrinter(System.out);
        printer.byteCode(code);

        Class myClass = defineClass(CallClassStr, cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(-2));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(-1));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(4));
    }

}
