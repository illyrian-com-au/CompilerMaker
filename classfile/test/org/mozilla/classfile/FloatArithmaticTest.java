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

public class FloatArithmaticTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface Run
    {
        void run();
    }

    public interface Eval
    {
        float eval();
    }

    public interface Unary
    {
        float unary(float a);
    }

    public interface Binary
    {
        float binary(float x, float y);
    }

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

    // Generate public float eval()
    public void runMethod() throws Exception
    {
        cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 2
        cfw.addLoadThis();
        cfw.add(ByteCode.FCONST_2);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "F");

        cfw.add(ByteCode.RETURN);
        cfw.stopMethod((short) 3);
    }

    // Generate public float eval()
    public void evalMethod() throws Exception
    {
        cfw.startMethod("eval", "()F", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 2.0
        cfw.addLoadThis();
        cfw.add(ByteCode.FCONST_2);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "F");

        // Return 5.0
        cfw.addLoadConstant(5.0f);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 1);
    }

    // public float unary(float a)
    public void incrementMethod() throws Exception
    {
        cfw.startMethod("unary", "(F)F", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addILoad(1);  // 1st Argument
        cfw.add(ByteCode.FCONST_1);
        cfw.add(ByteCode.FADD);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 2);
    }

    public void testRun() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Run");
        defaultConstructor();
        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);
        runMethod();
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Run exec = (Run) myClass.newInstance();

        assertEquals("myObj.id should be 0", 0.0f, getFloatField(myClass, exec, "id"));
        exec.run();
        assertEquals("myObj.id should be 2", 2.0f, getFloatField(myClass, exec, "id"));
    }

    public void testEval() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);
        evalMethod();

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Wrong value for exec.eval()", 5.0f, exec.eval());
        assertEquals("Wrong value for myObj.id", 2.0f, getFloatField(myClass, exec, "id"));

    }

    public void testIncrement() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Unary");
        defaultConstructor();
        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(F)F", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1);  // 1st Argument
        cfw.add(ByteCode.FCONST_1);
        cfw.add(ByteCode.FADD);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Wrong value for exec.eval()", 7.0f, exec.unary(6));
    }

    public void testDecrement() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(F)F", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1);  // 1st Argument
        cfw.add(ByteCode.FCONST_1);
        cfw.add(ByteCode.FNEG);
        cfw.add(ByteCode.FADD);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Decrement failed", 5.0f, exec.unary(6.0f));
    }

    // public float unary(float a)
    public Unary unaryMethod(int opCode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(F)F", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1);
        cfw.add(opCode);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 3);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Unary) myClass.newInstance();
    }

    public void testNegate() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(F)F", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1);
        cfw.add(ByteCode.FNEG);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 3);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary) myClass.newInstance();
        assertEquals("Negate failed", -6.0f, exec.unary(6.0f));
    }

    public void testIntToFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()F", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "I");

        cfw.add(ByteCode.I2F);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setIntField(myClass, exec, "id", 64);
        assertEquals("Convert Integer to Float failed", 64.0f, exec.eval());
        setIntField(myClass, exec, "id", -64);
        assertEquals("Convert Integer to Float failed", -64.0f, exec.eval());
    }

    public void testLongToFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()F", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "J");

        cfw.add(ByteCode.L2F);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setLongField(myClass, exec, "id", 64);
        assertEquals("Convert Long to Float failed", 64.0f, exec.eval());
        setLongField(myClass, exec, "id", -64);
        assertEquals("Convert Long to Float failed", -64.0f, exec.eval());
    }

    public void testDoubleToFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()F", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "D");

        cfw.add(ByteCode.D2F);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setDoubleField(myClass, exec, "id", 64.0d);
        assertEquals("Convert Integer to Float failed", 64.0f, exec.eval());
        setDoubleField(myClass, exec, "id", -64.0d);
        assertEquals("Convert Integer to Float failed", -64.0f, exec.eval());
    }

    public void testAddTwo() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Unary");
        defaultConstructor();
        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(F)F", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1);  // 1st Argument
        cfw.add(ByteCode.FCONST_2);
        cfw.add(ByteCode.FADD);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("myObj.id should be 0", 0.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Add Two failed", 8.0f, exec.unary(6));
    }

    public Binary binaryOperator(int opCode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/FloatArithmaticTest$Binary");
        defaultConstructor();
        cfw.startMethod("binary", "(FF)F", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1);  // 1st Argument
        cfw.addFLoad(2);  // 2nd Argument
        cfw.add(opCode);
        cfw.add(ByteCode.FRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Binary)myClass.newInstance();
    }

    public void testAddOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.FADD);

        assertEquals("2.25 + 3.5 failed", 5.75f, exec.binary(2.25f, 3.5f));
        assertEquals("2.25 + -3.5 failed", -1.25f, exec.binary(2.25f, -3.5f));
        assertEquals("-2.25 + 3.5 failed", 1.25f, exec.binary(-2.25f, 3.5f));
        assertEquals("-2.25 + -3.5 failed", -5.75f, exec.binary(-2.25f, -3.5f));
    }

    public void testSubtractOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.FSUB);

        assertEquals("2.25 - 3 failed", -1.25f, exec.binary(2.25f, 3.5f));
        assertEquals("2.25 - -3 failed", 5.75f, exec.binary(2.25f, -3.5f));
        assertEquals("-2.25 - 3 failed", -5.75f, exec.binary(-2.25f, 3.5f));
        assertEquals("-2.25 - -3 failed", 1.25f, exec.binary(-2.25f, -3.5f));
    }

    public void testMultiplyOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.FMUL);

        assertEquals("2 * 3 failed", 6.0f, exec.binary(2.0f, 3.0f));
        assertEquals("2 * -3 failed", -6.0f, exec.binary(2.0f, -3.0f));
        assertEquals("-2 * 3 failed", -6.0f, exec.binary(-2.0f, 3.0f));
        assertEquals("-2 * -3 failed", 6.0f, exec.binary(-2.0f, -3.0f));
    }

    public void testDivideOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.FDIV);

        assertEquals("6.0/ 3.0 failed",   2.0f, exec.binary(6.0f, 3.0f));
        assertEquals("6.0 / -3.0 failed", -2.0f, exec.binary(6.0f, -3.0f));
        assertEquals("-6.0 / 3.0 failed", -2.0f, exec.binary(-6.0f, 3.0f));
        assertEquals("-6.0 / -3.0 failed", 2.0f, exec.binary(-6.0f, -3.0f));
    }

    public void testRemainderOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.FREM);

        assertEquals("7.0 % 3.0 failed",   1.0f, exec.binary(7.0f, 3.0f));
        assertEquals("7.0 % -3.0 failed",  1.0f, exec.binary(7.0f, -3.0f));
        assertEquals("-7.0 % 3.0 failed", -1.0f, exec.binary(-7.0f, 3.0f));
        assertEquals("-7.0 % -3.0 failed",-1.0f, exec.binary(-7.0f, -3.0f));
    }

}
