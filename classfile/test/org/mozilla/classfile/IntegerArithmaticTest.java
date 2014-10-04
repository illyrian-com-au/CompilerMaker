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

public class IntegerArithmaticTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface Run
    {
        void run();
    }

    public interface Eval
    {
        int eval();
    }

    public interface Unary
    {
        int unary(int a);
    }

    public interface Binary
    {
        int binary(int x, int y);
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

    // Generate public int eval()
    public void runMethod() throws Exception
    {
        cfw.startMethod("run", "()V", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 2
        cfw.addLoadThis();
        cfw.addPush(2);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

        cfw.add(ByteCode.RETURN);
        cfw.stopMethod((short) 1);
    }

    // Generate public int eval()
    public void evalMethod() throws Exception
    {
        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        // set id to 4
        cfw.addLoadThis();
        cfw.addPush(4);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

        // Return 5
        cfw.addPush(5);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 1);
    }

    // public int unary(int a)
    public void incrementMethod() throws Exception
    {
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addILoad(1);  // 1st Argument
        cfw.add(ByteCode.ICONST_1);
        cfw.add(ByteCode.IADD);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 2);
    }

    public void testRun() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Run");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        runMethod();
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Run exec = (Run) myClass.newInstance();

        assertEquals("myObj.id should be 0", 0, getIntField(myClass, exec, "id"));
        exec.run();
        assertEquals("myObj.id should be 2", 2, getIntField(myClass, exec, "id"));
    }

    public void testEval() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        evalMethod();

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        int id =getIntField(myClass, exec, "id");
        assertEquals("myObj.id should be 0", 0, id);

        id = exec.eval();
        assertEquals("exec.eval() should be 5", 5, id);

        id = getIntField(myClass, exec, "id");
        assertEquals("myObj.id should be 4", 4, id);

    }

    // Generate public int eval()
    public Eval constClass(int opcode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Eval");
        defaultConstructor();
        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.add(opcode);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 1);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Eval)myClass.newInstance();
    }

    public void testConstM1() throws Exception
    {
        Eval exec = constClass(ByteCode.ICONST_M1);
        assertEquals("Wrong const value", -1, exec.eval());
    }

    public void testConst0() throws Exception
    {
        Eval exec = constClass(ByteCode.ICONST_0);
        assertEquals("Wrong const value", 0, exec.eval());
    }

    public void testConst1() throws Exception
    {
        Eval exec = constClass(ByteCode.ICONST_1);
        assertEquals("Wrong const value", 1, exec.eval());
    }

    public void testConst2() throws Exception
    {
        Eval exec = constClass(ByteCode.ICONST_2);
        assertEquals("Wrong const value", 2, exec.eval());
    }

    public void testConst3() throws Exception
    {
        Eval exec = constClass(ByteCode.ICONST_3);
        assertEquals("Wrong const value", 3, exec.eval());
    }

    public void testConst4() throws Exception
    {
        Eval exec = constClass(ByteCode.ICONST_4);
        assertEquals("Wrong const value", 4, exec.eval());
    }

    public void testConst5() throws Exception
    {
        Eval exec = constClass(ByteCode.ICONST_5);
        assertEquals("Wrong const value", 5, exec.eval());
    }

    public void testIncrement() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Unary");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.add(ByteCode.IINC, 1, 1); // Increment 1st local by 1
        cfw.addILoad(1);  // 1st Argument
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        int id =getIntField(myClass, exec, "id");
        assertEquals("myObj.id should be 0", 0, id);
        id = exec.unary(6);
        assertEquals("Increment failed", 7, id);
    }

    public void testIncrementWide() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Unary");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.add(ByteCode.IINC, 1, 500); // Increment 1st local by 500, requires wide instruction
        cfw.addILoad(1);  // 1st Argument
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        int id =getIntField(myClass, exec, "id");
        assertEquals("myObj.id should be 0", 0, id);
        id = exec.unary(6);
        assertEquals("Increment failed", 506, id);
    }

    public void testDecrement() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addILoad(1);  // 1st Argument
        cfw.add(ByteCode.ICONST_M1);
        cfw.add(ByteCode.IADD);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        int id = exec.unary(6);
        assertEquals("Decrement failed", 5, id);
    }

    // public int unary(int a)
    public Unary unaryMethod(int opCode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLoadThis();
        cfw.addILoad(1);
        cfw.add(opCode);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 2);
        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Unary) myClass.newInstance();
    }

    public void testNegate() throws Exception
    {
        Unary exec = unaryMethod(ByteCode.INEG);
        assertEquals("Negate failed", -6, exec.unary(6));
    }

    public void testIntToByte() throws Exception
    {
        Unary exec = unaryMethod(ByteCode.I2B);
        assertEquals("Convert Integer to Byte failed", 64, exec.unary(64));
        assertEquals("Convert Integer to Byte failed", -64, exec.unary(-64));
        assertEquals("Convert Integer to Byte failed", 122, exec.unary(-134));
        assertEquals("Convert Integer to Byte failed", -122, exec.unary(134));
    }

    public void testIntToChar() throws Exception
    {
        Unary exec = unaryMethod(ByteCode.I2C);
        assertEquals("Convert Integer to Char failed", 64, exec.unary(64));
        assertEquals("Convert Integer to Char failed", 65472, exec.unary(-64));
        assertEquals("Convert Integer to Char failed", 65402, exec.unary(-134));
        assertEquals("Convert Integer to Char failed", 134, exec.unary(134));
        assertEquals("Convert Integer to Char failed", 64321, exec.unary(64321));
        assertEquals("Convert Integer to Char failed", 1215, exec.unary(-64321));
    }

    public void testIntToShort() throws Exception
    {
        Unary exec = unaryMethod(ByteCode.I2S);
        assertEquals("Convert Integer to Short failed", 64, exec.unary(64));
        assertEquals("Convert Integer to Short failed", -64, exec.unary(-64));
        assertEquals("Convert Integer to Short failed", -134, exec.unary(-134));
        assertEquals("Convert Integer to Short failed", 134, exec.unary(134));
        assertEquals("Convert Integer to Short failed", -1215, exec.unary(64321));
        assertEquals("Convert Integer to Short failed", 1215, exec.unary(-64321));
    }

    public void testLongToInt() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "J", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "J");

        cfw.add(ByteCode.L2I);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setLongField(myClass, exec, "id", 64L);
        assertEquals("Convert Integer to Byte failed", 64, exec.eval());
        setLongField(myClass, exec, "id", -64L);
        assertEquals("Convert Integer to Byte failed", -64, exec.eval());
    }

    public void testDoubleToInt() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "D", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "D");

        cfw.add(ByteCode.D2I);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 4);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setDoubleField(myClass, exec, "id", 64.9d);
        assertEquals("Convert Integer to Byte failed", 64, exec.eval());
        setDoubleField(myClass, exec, "id", -64.9d);
        assertEquals("Convert Integer to Byte failed", -64, exec.eval());
    }

    public void testFloatToInt() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Eval");
        defaultConstructor();
        cfw.addField("id", "F", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        // GET myclass.id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "F");

        cfw.add(ByteCode.F2I);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval) myClass.newInstance();

        setFloatField(myClass, exec, "id", 64.9f);
        assertEquals("Convert Integer to Byte failed", 64, exec.eval());
        setFloatField(myClass, exec, "id", -64.9f);
        assertEquals("Convert Integer to Byte failed", -64, exec.eval());
    }

    public void testAddTwo() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Unary");
        defaultConstructor();
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addILoad(1);  // 1st Argument
        cfw.add(ByteCode.ICONST_2);
        cfw.add(ByteCode.IADD);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        int id =getIntField(myClass, exec, "id");
        assertEquals("myObj.id should be 0", 0, id);
        id = exec.unary(6);
        assertEquals("Add Two failed", 8, id);
    }

    // public int unary(int a)
    public Unary bipushMethod(int opCode, int operand) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addILoad(1);                    // 1st Argument
        cfw.add(ByteCode.BIPUSH, operand);  // constant byte operand
        cfw.add(opCode);                    // operation
        cfw.add(ByteCode.I2B);              // convert to byte
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Unary) myClass.newInstance();
    }

    public void testAddByte64() throws Exception
    {
        Unary exec = bipushMethod(ByteCode.IADD, 64);
        assertEquals("Add byte failed", 70, exec.unary(6));
        assertEquals("Add byte failed", -122, exec.unary(70));
        assertEquals("Add byte failed", -64, exec.unary(-128));
        assertEquals("Add byte failed", 76, exec.unary(-500));
        assertEquals("Add byte failed", 52, exec.unary(500));
    }

    public void testSubByte64() throws Exception
    {
        Unary exec = bipushMethod(ByteCode.IADD, -64);
        assertEquals("Add byte failed", -70, exec.unary(-6));
        assertEquals("Add byte failed", 122, exec.unary(-70));
        assertEquals("Add byte failed", 64, exec.unary(128));
        assertEquals("Add byte failed", -76, exec.unary(500));
        assertEquals("Add byte failed", -52, exec.unary(-500));
    }

    // public int unary(int a)
    public Unary sipushMethod(int opCode, int operand) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Unary");
        defaultConstructor();
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addILoad(1);                    // 1st Argument
        cfw.add(ByteCode.SIPUSH, operand);  // constant short operand
        cfw.add(opCode);                    // operation
        cfw.add(ByteCode.I2S);              // convert to short
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 2);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Unary) myClass.newInstance();
    }

    public void testAddShort() throws Exception
    {
        // Short: -32768 .. 32767
        Unary exec = sipushMethod(ByteCode.IADD, 10000);
        assertEquals("Add byte failed", 16000, exec.unary(6000));
        assertEquals("Add byte failed", Short.MIN_VALUE, exec.unary(22768));
        assertEquals("Add byte failed", -22768, exec.unary(Short.MIN_VALUE));
        assertEquals("Add byte failed", -31248, exec.unary(-500000));
        assertEquals("Add byte failed", -14288, exec.unary(500000));
    }

    public void testSubShort() throws Exception
    {
        // Short: -32768 .. 32767
        Unary exec = sipushMethod(ByteCode.IADD, -10000);
        assertEquals("Add byte failed", -16000, exec.unary(-6000));
        assertEquals("Add byte failed", Short.MAX_VALUE, exec.unary(-22769));
        assertEquals("Add byte failed", 22767, exec.unary(Short.MAX_VALUE));
        assertEquals("Add byte failed", 31248, exec.unary(500000));
        assertEquals("Add byte failed", 14288, exec.unary(-500000));
    }

    public Binary binaryOperator(int opCode) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Binary");
        defaultConstructor();
        cfw.startMethod("binary", "(II)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addILoad(1);  // 1st Argument
        cfw.addILoad(2);  // 2nd Argument
        cfw.add(opCode);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Binary)myClass.newInstance();
    }

    public void testAddOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.IADD);

        assertEquals("2 + 3 failed", 5, exec.binary(2, 3));
        assertEquals("2 + -3 failed", -1, exec.binary(2, -3));
        assertEquals("-2 + 3 failed", 1, exec.binary(-2, 3));
        assertEquals("-2 + -3 failed", -5, exec.binary(-2, -3));
        assertEquals("33m + 33m failed", 66000000, exec.binary(33000000, 33000000));
        assertEquals("MAX_INT + 1 failed", Integer.MIN_VALUE, exec.binary(Integer.MAX_VALUE, 1));
    }

    public void testSubtractOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.ISUB);

        assertEquals("2 - 3 failed", -1, exec.binary(2, 3));
        assertEquals("2 - -3 failed", 5, exec.binary(2, -3));
        assertEquals("-2 - 3 failed", -5, exec.binary(-2, 3));
        assertEquals("-2 - -3 failed", 1, exec.binary(-2, -3));
        assertEquals("33m - 33m failed", 0, exec.binary(33000000, 33000000));
        assertEquals("MIN_INT - 1 failed", Integer.MAX_VALUE, exec.binary(Integer.MIN_VALUE, 1));
    }

    public void testMultiplyOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.IMUL);

        assertEquals("2 * 3 failed", 6, exec.binary(2, 3));
        assertEquals("2 * -3 failed", -6, exec.binary(2, -3));
        assertEquals("-2 * 3 failed", -6, exec.binary(-2, 3));
        assertEquals("-2 * -3 failed", 6, exec.binary(-2, -3));
    }

    public void testDivideOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.IDIV);

        assertEquals("7 / 3 failed",   2, exec.binary(7, 3));
        assertEquals("7 / -3 failed", -2, exec.binary(7, -3));
        assertEquals("-7 / 3 failed", -2, exec.binary(-7, 3));
        assertEquals("-7 / -3 failed", 2, exec.binary(-7, -3));
    }

    public void testRemainderOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.IREM);

        assertEquals("7 % 3 failed",   1, exec.binary(7, 3));
        assertEquals("7 % -3 failed",  1, exec.binary(7, -3));
        assertEquals("-7 % 3 failed", -1, exec.binary(-7, 3));
        assertEquals("-7 % -3 failed",-1, exec.binary(-7, -3));
    }

    public void testExclusiveOrOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.IXOR);

        assertEquals("5(0101) xor 3(0011) = 6(0110) failed",   6, exec.binary(5, 3));
    }

    public void testBitwiseAndOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.IAND);

        assertEquals("13(1101) and 11(1011) = 9(1001) failed",   9, exec.binary(13, 11));
    }

    public void testBitwiseOrOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.IOR);

        assertEquals("5(0101) or 3(0011) = 7(0111) failed",   7, exec.binary(5, 3));
    }

    public void testShiftLeftOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.ISHL);

        assertEquals("3(0011) << 2) = 12(1100) failed",   12, exec.binary(3, 2));
    }

    public void testShiftRightOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.ISHR);

        assertEquals("12(1100) >> 1 = 6(0110) failed",   6, exec.binary(12, 1));
        assertEquals("-12 >> 1 =  failed",              -6, exec.binary(-12, 1));
    }

    public void testUnsignedShiftRightOperator() throws Exception
    {
        Binary exec = binaryOperator(ByteCode.IUSHR);

        assertEquals("12(1100) u>> 1 = 6(0110) failed",   6, exec.binary(12, 1));
        assertEquals("-12 u>> 1 = 2147483642 failed", 2147483642, exec.binary(-12, 1));
    }

    public void testDivRemExpression() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/IntegerArithmaticTest$Binary");
        defaultConstructor();
        cfw.startMethod("binary", "(II)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLoadThis();

        // infix:   (x / y) * y + (x % y) = x
        // postfix: x y / y * x y % +
        cfw.addILoad(1);  // 1st Argument
        cfw.addILoad(2);  // 2nd Argument
        cfw.add(ByteCode.IDIV);
        cfw.addILoad(2);  // 2nd Argument
        cfw.add(ByteCode.IMUL);
        cfw.addILoad(1);  // 1st Argument
        cfw.addILoad(2);  // 2nd Argument
        cfw.add(ByteCode.IREM);
        cfw.add(ByteCode.IADD);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("(x / y) * y + (x % y) = x failed", 7, exec.binary(7, 3));
        assertEquals("(x / y) * y + (x % y) = x failed", -7, exec.binary(-7, 3));
        assertEquals("(x / y) * y + (x % y) = x failed", 7, exec.binary(7, -3));
        assertEquals("(x / y) * y + (x % y) = x failed", -7, exec.binary(-7, -3));
    }

}
