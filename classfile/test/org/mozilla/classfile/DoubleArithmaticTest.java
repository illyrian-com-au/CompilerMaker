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

public class DoubleArithmaticTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface Run
    {
        void run();
    }

    public interface Eval
    {
        double eval();
    }

    public interface Unary
    {
        double unary(double a);
    }

    public interface Binary
    {
        double binary(double x, double y);
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

    // Generate public void run()
    public void runMethod() throws Exception
    {
        cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 2
        cfw.addLoadThis();
        cfw.addLoadConstant(2.0d);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "D");

        cfw.add(ByteCode.RETURN);
        cfw.stopMethod((short) 3);
    }

    // Generate public double eval()
    public void evalMethod() throws Exception
    {
        cfw.startMethod("eval", "()D", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 2.0
        cfw.addLoadThis();
        cfw.addLoadConstant(2d);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "D");

        // Return 5.0
        cfw.addLoadConstant(5.0d);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 1);
    }

    // public double unary(double a)
    public void incrementMethod() throws Exception
    {
        cfw.startMethod("unary", "(D)D", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addDLoad(1);  // 1st Argument
        cfw.add(ByteCode.DCONST_1);
        cfw.add(ByteCode.DADD);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 2);
    }

    public void testRun() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Run");
        defaultConstructor();
        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);
        runMethod();
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Run exec = (Run) myClass.newInstance();

        assertEquals("myObj.id should be 0", 0.0d, getDoubleField(myClass, exec, "id"));
        exec.run();
        assertEquals("myObj.id should be 2", 2.0d, getDoubleField(myClass, exec, "id"));
    }

    public void testEval() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);
        evalMethod();

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0d, getDoubleField(myClass, exec, "id"));
        assertEquals("Wrong value for exec.eval()", 5.0d, exec.eval());
        assertEquals("Wrong value for myObj.id", 2.0d, getDoubleField(myClass, exec, "id"));

    }

    public void testIncrement() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Unary");
        defaultConstructor();
        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(D)D", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addDLoad(1);  // 1st Argument
        cfw.add(ByteCode.DCONST_1);
        cfw.add(ByteCode.DADD);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0d, getDoubleField(myClass, exec, "id"));
        assertEquals("Wrong value for exec.eval()", 7.0d, exec.unary(6));
    }

    public void testDecrement() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(D)D", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addDLoad(1);  // 1st Argument
        cfw.add(ByteCode.DCONST_1);
        cfw.add(ByteCode.DNEG);
        cfw.add(ByteCode.DADD);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Decrement failed", 5.0d, exec.unary(6.0d));
    }

    // public double unary(double a)
    public Unary unaryMethod(int opCode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(D)D", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addDLoad(1);
        cfw.add(opCode);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 3);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Unary) myClass.newInstance();
    }

    public void testNegate() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(D)D", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addDLoad(1);
        cfw.add(ByteCode.DNEG);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 3);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary) myClass.newInstance();
        assertEquals("Negate failed", -6.0d, exec.unary(6.0d));
    }

    public void testIntToFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()D", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "I");

        cfw.add(ByteCode.I2D);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setIntField(myClass, exec, "id", 64);
        assertEquals("Convert Integer to Float failed", 64.0d, exec.eval());
        setIntField(myClass, exec, "id", -64);
        assertEquals("Convert Integer to Float failed", -64.0d, exec.eval());
    }

    public void testLongToFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()D", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "J");

        cfw.add(ByteCode.L2D);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setLongField(myClass, exec, "id", 64L);
        assertEquals("Convert Long to Float failed", 64.0d, exec.eval());
        setLongField(myClass, exec, "id", -64L);
        assertEquals("Convert Long to Float failed", -64.0d, exec.eval());
    }

    public void testFloatToDouble() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()D", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "F");

        cfw.add(ByteCode.F2D);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setFloatField(myClass, exec, "id", 64.0f);
        assertEquals("Convert Float to Double failed", 64.0d, exec.eval());
        setFloatField(myClass, exec, "id", -64.0f);
        assertEquals("Convert Float to Double failed", -64.0d, exec.eval());
    }

    public void testAddTwo() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Unary");
        defaultConstructor();
        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(D)D", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addDLoad(1);  // 1st Argument
        cfw.addLoadConstant(2d);
        cfw.add(ByteCode.DADD);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("myObj.id should be 0", 0.0d, getDoubleField(myClass, exec, "id"));
        assertEquals("Add Two failed", 8.0d, exec.unary(6));
    }

    public Binary binaryOperator(int opCode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/DoubleArithmaticTest$Binary");
        defaultConstructor();
        cfw.startMethod("binary", "(DD)D", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addDLoad(1);  // 1st Argument
        cfw.addDLoad(3);  // 2nd Argument
        cfw.add(opCode);
        cfw.add(ByteCode.DRETURN);
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Binary)myClass.newInstance();
    }

    public void testAddOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.DADD);

        assertEquals("2.25 + 3.5 failed", 5.75d, exec.binary(2.25d, 3.5d));
        assertEquals("2.25 + -3.5 failed", -1.25d, exec.binary(2.25d, -3.5d));
        assertEquals("-2.25 + 3.5 failed", 1.25d, exec.binary(-2.25d, 3.5d));
        assertEquals("-2.25 + -3.5 failed", -5.75d, exec.binary(-2.25d, -3.5d));
    }

    public void testSubtractOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.DSUB);

        assertEquals("2.25 - 3 failed", -1.25d, exec.binary(2.25d, 3.5d));
        assertEquals("2.25 - -3 failed", 5.75d, exec.binary(2.25d, -3.5d));
        assertEquals("-2.25 - 3 failed", -5.75d, exec.binary(-2.25d, 3.5d));
        assertEquals("-2.25 - -3 failed", 1.25d, exec.binary(-2.25d, -3.5d));
    }

    public void testMultiplyOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.DMUL);

        assertEquals("2 * 3 failed", 6.0d, exec.binary(2.0d, 3.0d));
        assertEquals("2 * -3 failed", -6.0d, exec.binary(2.0d, -3.0d));
        assertEquals("-2 * 3 failed", -6.0d, exec.binary(-2.0d, 3.0d));
        assertEquals("-2 * -3 failed", 6.0d, exec.binary(-2.0d, -3.0d));
    }

    public void testDivideOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.DDIV);

        assertEquals("6.0/ 3.0 failed",   2.0d, exec.binary(6.0d, 3.0d));
        assertEquals("6.0 / -3.0 failed", -2.0d, exec.binary(6.0d, -3.0d));
        assertEquals("-6.0 / 3.0 failed", -2.0d, exec.binary(-6.0d, 3.0d));
        assertEquals("-6.0 / -3.0 failed", 2.0d, exec.binary(-6.0d, -3.0d));
    }

    public void testRemainderOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.DREM);

        assertEquals("7.0 % 3.0 failed",   1.0d, exec.binary(7.0d, 3.0d));
        assertEquals("7.0 % -3.0 failed",  1.0d, exec.binary(7.0d, -3.0d));
        assertEquals("-7.0 % 3.0 failed", -1.0d, exec.binary(-7.0d, 3.0d));
        assertEquals("-7.0 % -3.0 failed",-1.0d, exec.binary(-7.0d, -3.0d));
    }

}
