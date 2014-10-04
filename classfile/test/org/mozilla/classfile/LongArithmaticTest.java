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


public class LongArithmaticTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface Run
    {
        void run();
    }

    public interface Eval
    {
        long eval();
    }

    public interface Unary
    {
        long unary(long a);
    }

    public interface UnaryInt
    {
        int unary(long a);
    }

    public interface Binary
    {
        long binary(long x, long y);
    }

    public interface BinaryLongInt
    {
        long binary(long x, int y);
    }

    public void startClass(String className, String iface) throws Exception
    {
        // Generate Class
        cfw = new ClassFileWriter(className, "java/lang/Object", className + ".java");
        //cfw.addInterface("java/lang/Runnable");
        cfw.addInterface(iface);
    }

    // Generate default constructor
    public void defaultConstructor() throws Exception
    {
        cfw.startMethod("<init>", "()V", ClassFileWriter.ACC_PUBLIC);
        cfw.addLoadThis();
        cfw.addInvoke(ByteCode.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");

        cfw.add(ByteCode.RETURN);
        // 1 parameter = this
        cfw.stopMethod((short) 1);
    }

    // Generate public long eval()
    public void runMethod() throws Exception
    {
        cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 2
        cfw.addLoadThis();
        cfw.addPush(2);
        cfw.add(ByteCode.I2L);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "J");

        cfw.add(ByteCode.RETURN);
        // 1 = String[] args
        cfw.stopMethod((short) 1);
    }

    // Generate public long eval()
    public void evalMethod() throws Exception
    {
        cfw.startMethod("eval", "()J", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 4
        cfw.addLoadThis();
        cfw.addPush(4);
        cfw.add(ByteCode.I2L);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "J");

        // Return 5
        cfw.addPush(5);
        cfw.add(ByteCode.I2L);
        cfw.add(ByteCode.LRETURN);
        // 1 = String[] args
        cfw.stopMethod((short) 1);
    }

    // public long unary(long a)
    public void incrementMethod() throws Exception
    {
        cfw.startMethod("unary", "(J)J", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addLoadThis();
        cfw.addLLoad(1);  // 1st Argument

        cfw.add(ByteCode.LCONST_1);
        cfw.add(ByteCode.LADD);

        cfw.add(ByteCode.LRETURN);
        // 1 = String[] args
        cfw.stopMethod((short) 2);
    }

    public void testRun() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Run");
        defaultConstructor();

        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);
        //cfw.addField("b", "J", ClassFileWriter.ACC_PUBLIC);

        runMethod();

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Object myObj = myClass.newInstance();

        long id =getLongField(myClass, myObj, "id");
        assertEquals("myObj.id should be 0", 0, id);

        Run exec = (Run)myObj;
        exec.run();

        id = getLongField(myClass, myObj, "id");
        assertEquals("myObj.id should be 2", 2, id);

    }

    public void testEval() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Eval");
        defaultConstructor();

        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);
        //cfw.addField("b", "J", ClassFileWriter.ACC_PUBLIC);

        evalMethod();

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Object myObj = myClass.newInstance();

        long id =getLongField(myClass, myObj, "id");
        assertEquals("myObj.id should be 0", 0, id);

        Eval exec = (Eval)myObj;
        id = exec.eval();
        assertEquals("exec.eval() should be 5", 5, id);

        id = getLongField(myClass, myObj, "id");
        assertEquals("myObj.id should be 4", 4, id);

    }

    public void testIncrement() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Unary");
        defaultConstructor();

        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("unary", "(J)J", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addLoadThis();
        cfw.addLLoad(1);  // 1st Argument

        cfw.add(ByteCode.LCONST_1);
        cfw.add(ByteCode.LADD);

        cfw.add(ByteCode.LRETURN);
        cfw.stopMethod((short) 4);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Object myObj = myClass.newInstance();

        long id =getLongField(myClass, myObj, "id");
        assertEquals("myObj.id should be 0", 0, id);

        Unary exec = (Unary)myObj;

        id = exec.unary(6);
        assertEquals("Increment failed", 7, id);
    }

    public void testDecrement() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Unary");
        defaultConstructor();

        cfw.startMethod("unary", "(J)J", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addLoadThis();
        cfw.addLLoad(1);  // 1st Argument

        cfw.add(ByteCode.LCONST_1);
        cfw.add(ByteCode.LNEG);
        cfw.add(ByteCode.LADD);

        cfw.add(ByteCode.LRETURN);
        // 1 = String[] args
        cfw.stopMethod((short) 4);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Object myObj = myClass.newInstance();

        Unary exec = (Unary)myObj;

        long id = exec.unary(6);
        assertEquals("Decrement failed", 5, id);
    }

    // public long unary(long a)
    public Unary unaryMethod(int opCode) throws Exception
    {
        return unaryMethod(opCode, ByteCode.NOP);
    }

    public Unary unaryMethod(int opCode1, int opCode2) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(J)J", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLoadThis();
        cfw.addLLoad(1);
        cfw.add(opCode1);
        if (opCode2 != ByteCode.NOP)
        {
            cfw.add(opCode2);
        }
        cfw.add(ByteCode.LRETURN);
        cfw.stopMethod((short) 4);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Unary) myClass.newInstance();
    }

    public void testNegate() throws Exception
    {
        Unary exec = unaryMethod(ByteCode.LNEG);
        assertEquals("Long Negate failed", -6L, exec.unary(6L));
    }

    public void testLongToByte() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$UnaryInt");
        defaultConstructor();
        cfw.startMethod("unary", "(J)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLoadThis();
        cfw.addLLoad(1);
        cfw.add(ByteCode.L2I);
        cfw.add(ByteCode.I2B);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 4);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryInt exec = (UnaryInt) myClass.newInstance();
        assertEquals("Convert Long to Byte failed", 64, exec.unary(64L));
        assertEquals("Convert Long to Byte failed", -64, exec.unary(-64L));
        assertEquals("Convert Long to Byte failed", 122, exec.unary(-134L));
        assertEquals("Convert Long to Byte failed", -122, exec.unary(134L));
    }

    public void testLongToChar() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$UnaryInt");
        defaultConstructor();
        cfw.startMethod("unary", "(J)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLoadThis();
        cfw.addLLoad(1);
        cfw.add(ByteCode.L2I);
        cfw.add(ByteCode.I2C);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 4);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryInt exec = (UnaryInt) myClass.newInstance();
        assertEquals("Convert Long to Char failed", 64, exec.unary(64L));
        assertEquals("Convert Long to Char failed", 65472, exec.unary(-64L));
        assertEquals("Convert Long to Char failed", 65402, exec.unary(-134L));
        assertEquals("Convert Long to Char failed", 134, exec.unary(134L));
        assertEquals("Convert Long to Char failed", 64321, exec.unary(64321L));
        assertEquals("Convert Long to Char failed", 1215, exec.unary(-64321L));
    }

    public void testLongToShort() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$UnaryInt");
        defaultConstructor();
        cfw.startMethod("unary", "(J)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLoadThis();
        cfw.addLLoad(1);
        cfw.add(ByteCode.L2I);
        cfw.add(ByteCode.I2S);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 4);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryInt exec = (UnaryInt) myClass.newInstance();
        assertEquals("Convert Long to Short failed", 64, exec.unary(64L));
        assertEquals("Convert Long to Short failed", -64, exec.unary(-64L));
        assertEquals("Convert Long to Short failed", -134, exec.unary(-134L));
        assertEquals("Convert Long to Short failed", 134, exec.unary(134L));
        assertEquals("Convert Long to Short failed", -1215, exec.unary(64321L));
        assertEquals("Convert Long to Short failed", 1215, exec.unary(-64321L));
    }

    public void testLongToInt() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$UnaryInt");
        defaultConstructor();
        cfw.startMethod("unary", "(J)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLoadThis();
        cfw.addLLoad(1);
        cfw.add(ByteCode.L2I);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 4);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryInt exec = (UnaryInt) myClass.newInstance();
        assertEquals("Convert Long to Integer failed", 64, exec.unary(64L));
        assertEquals("Convert Long to Integer failed", -64, exec.unary(-64L));
        assertEquals("Convert Long to Integer failed", -134, exec.unary(-134L));
        assertEquals("Convert Long to Integer failed", 134, exec.unary(134L));
        assertEquals("Convert Long to Integer failed", 64321, exec.unary(64321L));
        assertEquals("Convert Long to Integer failed", -64321, exec.unary(-64321L));
    }

    public void testFloatToLong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()J", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "F");

        cfw.add(ByteCode.F2L);
        cfw.add(ByteCode.LRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setFloatField(myClass, exec, "id", 64.9f);
        assertEquals("Convert Integer to Byte failed", 64, exec.eval());
        setFloatField(myClass, exec, "id", -64.9f);
        assertEquals("Convert Integer to Byte failed", -64, exec.eval());
    }

    public void testIntegerToLong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()J", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "I");

        cfw.add(ByteCode.I2L);
        cfw.add(ByteCode.LRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setIntField(myClass, exec, "id", 64);
        assertEquals("Convert Integer to Long failed", 64, exec.eval());
        setIntField(myClass, exec, "id", -64);
        assertEquals("Convert Integer to Long failed", -64, exec.eval());
    }

    public void testDoubleToLong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()J", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "D");

        cfw.add(ByteCode.D2L);
        cfw.add(ByteCode.LRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setDoubleField(myClass, exec, "id", 64.9d);
        assertEquals("Convert Integer to Byte failed", 64, exec.eval());
        setDoubleField(myClass, exec, "id", -64.9d);
        assertEquals("Convert Integer to Byte failed", -64, exec.eval());
    }

    public void testIncByTwo() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Unary");
        defaultConstructor();

        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);

        cfw.startMethod("unary", "(J)J", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addLoadThis();
        cfw.addLLoad(1);  // 1st Argument

        cfw.add(ByteCode.ICONST_2);
        cfw.add(ByteCode.I2L);
        cfw.add(ByteCode.LADD);

        cfw.add(ByteCode.LRETURN);
        // 1 = String[] args
        cfw.stopMethod((short) 4);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Object myObj = myClass.newInstance();

        long id =getLongField(myClass, myObj, "id");
        assertEquals("myObj.id should be 0", 0, id);

        Unary exec = (Unary)myObj;

        id = exec.unary(6);
        assertEquals("Add Two failed", 8, id);
    }

    public Binary binaryOperator(int opCode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Binary");
        defaultConstructor();

        cfw.startMethod("binary", "(JJ)J", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addLoadThis();
        cfw.addLLoad(1);  // 1st Argument
        cfw.addLLoad(3);  // 2nd Argument

        cfw.add(opCode);

        cfw.add(ByteCode.LRETURN);
        // 1 = String[] args
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Binary)myClass.newInstance();
    }

    public void testAddOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.LADD);

        assertEquals("2 + 3 failed", 5L, exec.binary(2L, 3L));
        assertEquals("2 + -3 failed", -1L, exec.binary(2L, -3L));
        assertEquals("-2 + 3 failed", 1L, exec.binary(-2L, 3L));
        assertEquals("-2 + -3 failed", -5L, exec.binary(-2L, -3L));

        assertEquals("33m + 33m failed", 66000000, exec.binary(33000000, 33000000));
        assertEquals("MAX_LONG + 1 failed", Long.MIN_VALUE, exec.binary(Long.MAX_VALUE, 1));
    }

    public void testSubtractOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.LSUB);

        assertEquals("2 - 3 failed", -1, exec.binary(2, 3));
        assertEquals("2 - -3 failed", 5, exec.binary(2, -3));
        assertEquals("-2 - 3 failed", -5, exec.binary(-2, 3));
        assertEquals("-2 - -3 failed", 1, exec.binary(-2, -3));

        assertEquals("33m - 33m failed", 0, exec.binary(33000000, 33000000));
        assertEquals("MIN_LONG - 1 failed", Long.MAX_VALUE, exec.binary(Long.MIN_VALUE, 1));
    }

    public void testMultiplyOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.LMUL);

        assertEquals("2 * 3 failed", 6, exec.binary(2, 3));
        assertEquals("2 * -3 failed", -6, exec.binary(2, -3));
        assertEquals("-2 * 3 failed", -6, exec.binary(-2, 3));
        assertEquals("-2 * -3 failed", 6, exec.binary(-2, -3));
    }

    public void testDivideOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.LDIV);

        assertEquals("7 / 3 failed",   2, exec.binary(7, 3));
        assertEquals("7 / -3 failed", -2, exec.binary(7, -3));
        assertEquals("-7 / 3 failed", -2, exec.binary(-7, 3));
        assertEquals("-7 / -3 failed", 2, exec.binary(-7, -3));
    }

    public void testRemainderOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.LREM);

        assertEquals("7 % 3 failed",   1, exec.binary(7, 3));
        assertEquals("7 % -3 failed",  1, exec.binary(7, -3));
        assertEquals("-7 % 3 failed", -1, exec.binary(-7, 3));
        assertEquals("-7 % -3 failed",-1, exec.binary(-7, -3));
    }

    public void testExclusiveOrOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.LXOR);

        assertEquals("5(0101) xor 3(0011) = 6(0110) failed",   6, exec.binary(5, 3));
    }

    public void testBitwiseAndOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.LAND);

        assertEquals("13(1101) and 11(1011) = 9(1001) failed",   9, exec.binary(13, 11));
    }

    public void testBitwiseOrOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.LOR);

        assertEquals("5(0101) or 3(0011) = 7(0111) failed",   7, exec.binary(5, 3));
    }

    public BinaryLongInt shiftOperator(int opCode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$BinaryLongInt");
        defaultConstructor();

        cfw.startMethod("binary", "(JI)J", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addLoadThis();
        cfw.addLLoad(1);  // 1st Argument
        cfw.addILoad(3);  // 2nd Argument

        cfw.add(opCode);

        cfw.add(ByteCode.LRETURN);
        // 1 = String[] args
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (BinaryLongInt)myClass.newInstance();
    }

    public void testShiftLeftOperator() throws Exception
    {
        BinaryLongInt exec = shiftOperator(ByteCode.LSHL);

        assertEquals("3(0011) << 2) = 12(1100) failed",   12L, exec.binary(3L, 2));
    }

    public void testShiftRightOperator() throws Exception
    {
        BinaryLongInt exec = shiftOperator(ByteCode.LSHR);

        assertEquals("12(1100) >> 1 = 6(0110) failed",   6L, exec.binary(12L, 1));
        assertEquals("-12 >> 1 =  failed",              -6L, exec.binary(-12L, 1));
    }

    public void testUnsignedShiftRightOperator() throws Exception
    {
        BinaryLongInt exec = shiftOperator(ByteCode.LUSHR);

        assertEquals("12(1100) u>> 1 = 6(0110) failed",   6L, exec.binary(12L, 1));
        assertEquals("-12 u>> 1 = 2147483642 failed", 2147483647L, exec.binary(-12L, 33));
    }

    public void testDivRemExpression() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/LongArithmaticTest$Binary");
        defaultConstructor();

        cfw.startMethod("binary", "(JJ)J", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addLoadThis();

        // infix:   (x / y) * y + (x % y) = x
        // postfix: x y / y * x y % +
        cfw.addLLoad(1);  // 1st Argument
        cfw.addLLoad(3);  // 2nd Argument
        cfw.add(ByteCode.LDIV);
        cfw.addLLoad(3);  // 2nd Argument
        cfw.add(ByteCode.LMUL);
        cfw.addLLoad(1);  // 1st Argument
        cfw.addLLoad(3);  // 2nd Argument
        cfw.add(ByteCode.LREM);
        cfw.add(ByteCode.LADD);

        cfw.add(ByteCode.LRETURN);
        // 1 = String[] args
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("(x / y) * y + (x % y) = x failed", 7, exec.binary(7, 3));
        assertEquals("(x / y) * y + (x % y) = x failed", -7, exec.binary(-7, 3));
        assertEquals("(x / y) * y + (x % y) = x failed", 7, exec.binary(7, -3));
        assertEquals("(x / y) * y + (x % y) = x failed", -7, exec.binary(-7, -3));
    }

}
