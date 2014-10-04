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

public class ConditionalTest extends ClassFileWriterTestCase
{
    ClassFileWriter cfw;

    public interface Eval
    {
        boolean eval();
    }

    public interface Unary
    {
        int unary(int a);
    }

    public interface UnaryLong
    {
        int unary(long a);
    }

    public interface BooleanLong
    {
        boolean test(long a, long b);
    }

    public interface UnaryFloat
    {
        int unary(float a);
    }

    public interface BooleanFloat
    {
        boolean test(float a, float b);
    }

    public interface UnaryDouble
    {
        int unary(double a);
    }

    public interface Binary
    {
        int binary(int x, int y);
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

    public Eval booleanMethod(int value) throws Exception
    {
        cfw.startMethod("eval", "()Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addPush(value);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 1);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Eval) myClass.newInstance();
}

    public void evalFalse()
    {
        cfw.startMethod("eval", "()Z", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addPush(false);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 0);
    }

    public void testTrue() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$Eval");
        defaultConstructor();
        Eval exec = booleanMethod(1);
        assertEquals(true, exec.eval());
    }

    public void testFalse() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$Eval");
        defaultConstructor();
        Eval exec = booleanMethod(0);
        assertEquals(false, exec.eval());
    }

    public void testCompareLong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$UnaryLong");
        defaultConstructor();
        cfw.startMethod("unary", "(J)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLLoad(1); // 1st Argument
        cfw.add(ByteCode.LCONST_0);
        cfw.add(ByteCode.LCMP);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryLong exec =  (UnaryLong) myClass.newInstance();
        assertEquals(0, exec.unary(0));
        assertEquals(1, exec.unary(10));
        assertEquals(-1, exec.unary(-10));
    }

    public void testGTLong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanLong");
        defaultConstructor();
        cfw.startMethod("test", "(JJ)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLLoad(1); // 1st Argument
        cfw.addLLoad(3); // 2nd Argument
        cfw.add(ByteCode.LCMP);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFGT, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
    }

    public void testGELong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanLong");
        defaultConstructor();
        cfw.startMethod("test", "(JJ)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLLoad(1); // 1st Argument
        cfw.addLLoad(3); // 2nd Argument
        cfw.add(ByteCode.LCMP);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFGE, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanLong ge =  (BooleanLong) myClass.newInstance();
        assertTrue(ge.test(10,5));
        assertTrue(ge.test(5,5));
        assertFalse(ge.test(5,10));
    }

    public void testEQLong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanLong");
        defaultConstructor();
        cfw.startMethod("test", "(JJ)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLLoad(1); // 1st Argument
        cfw.addLLoad(3); // 2nd Argument
        cfw.add(ByteCode.LCMP);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFEQ, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertFalse(cmp.test(10,5));
        assertTrue(cmp.test(5,5));
        assertFalse(cmp.test(5,10));
    }

    public void testNELong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanLong");
        defaultConstructor();
        cfw.startMethod("test", "(JJ)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLLoad(1); // 1st Argument
        cfw.addLLoad(3); // 2nd Argument
        cfw.add(ByteCode.LCMP);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFNE, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanLong cmp =  (BooleanLong) myClass.newInstance();
        assertTrue(cmp.test(10,5));
        assertFalse(cmp.test(5,5));
        assertTrue(cmp.test(5,10));
    }

    public void testLELong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanLong");
        defaultConstructor();
        cfw.startMethod("test", "(JJ)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLLoad(1); // 1st Argument
        cfw.addLLoad(3); // 2nd Argument
        cfw.add(ByteCode.LCMP);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFLE, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanLong le =  (BooleanLong) myClass.newInstance();
        assertFalse(le.test(10,5));
        assertTrue(le.test(5,5));
        assertTrue(le.test(5,10));
    }

    public void testLTLong() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanLong");
        defaultConstructor();
        cfw.startMethod("test", "(JJ)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addLLoad(1); // 1st Argument
        cfw.addLLoad(3); // 2nd Argument
        cfw.add(ByteCode.LCMP);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFLT, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanLong lt =  (BooleanLong) myClass.newInstance();
        assertFalse(lt.test(10,5));
        assertFalse(lt.test(5,5));
        assertTrue(lt.test(5,10));
    }

    public void testCompareFloatL() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$UnaryFloat");
        defaultConstructor();
        cfw.startMethod("unary", "(F)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1); // 1st Argument
        cfw.add(ByteCode.FCONST_0);
        cfw.add(ByteCode.FCMPL);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryFloat exec =  (UnaryFloat) myClass.newInstance();
        assertEquals(0, exec.unary(0.0f));
        assertEquals(1, exec.unary(10.0f));
        assertEquals(-1, exec.unary(-10.0f));
        assertEquals(-1, exec.unary(Float.NaN));
    }

    public void testCompareFloatG() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$UnaryFloat");
        defaultConstructor();
        cfw.startMethod("unary", "(F)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1); // 1st Argument
        cfw.add(ByteCode.FCONST_0);
        cfw.add(ByteCode.FCMPG);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryFloat exec =  (UnaryFloat) myClass.newInstance();
        assertEquals(0, exec.unary(0.0f));
        assertEquals(1, exec.unary(10.0f));
        assertEquals(-1, exec.unary(-10.0f));
        assertEquals(1, exec.unary(Float.NaN));
    }

    public void testGTFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanFloat");
        defaultConstructor();
        cfw.startMethod("test", "(FF)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1); // 1st Argument
        cfw.addFLoad(2); // 2nd Argument
        cfw.add(ByteCode.FCMPL);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFGT, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertTrue(cmp.test(10.0f,5.0f));
        assertFalse(cmp.test(5.0f,5.0f));
        assertFalse(cmp.test(5.0f,10.0f));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testGEFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanFloat");
        defaultConstructor();
        cfw.startMethod("test", "(FF)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1); // 1st Argument
        cfw.addFLoad(2); // 2nd Argument
        cfw.add(ByteCode.FCMPL);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFGE, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertTrue(cmp.test(10.0f,5.0f));
        assertTrue(cmp.test(5.0f,5.0f));
        assertFalse(cmp.test(5.0f,10.0f));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testEQFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanFloat");
        defaultConstructor();
        cfw.startMethod("test", "(FF)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1); // 1st Argument
        cfw.addFLoad(2); // 2nd Argument
        cfw.add(ByteCode.FCMPG);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFEQ, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertFalse(cmp.test(10.0f,5.0f));
        assertTrue(cmp.test(5.0f,5.0f));
        assertFalse(cmp.test(5.0f,10.0f));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testNEFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanFloat");
        defaultConstructor();
        cfw.startMethod("test", "(FF)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1); // 1st Argument
        cfw.addFLoad(2); // 2nd Argument
        cfw.add(ByteCode.FCMPL);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFNE, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertTrue(cmp.test(10.0f,5.0f));
        assertFalse(cmp.test(5.0f,5.0f));
        assertTrue(cmp.test(5.0f,10.0f));
        assertTrue(cmp.test(Float.NaN,10.0f));
        assertTrue(cmp.test(5.0f,Float.NaN));
        assertTrue(cmp.test(Float.NaN,Float.NaN));
    }

    public void testLEFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanFloat");
        defaultConstructor();
        cfw.startMethod("test", "(FF)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1); // 1st Argument
        cfw.addFLoad(2); // 2nd Argument
        cfw.add(ByteCode.FCMPG);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFLE, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertFalse(cmp.test(10.0f,5.0f));
        assertTrue(cmp.test(5.0f,5.0f));
        assertTrue(cmp.test(5.0f,10.0f));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testLTFloat() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$BooleanFloat");
        defaultConstructor();
        cfw.startMethod("test", "(FF)Z", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addFLoad(1); // 1st Argument
        cfw.addFLoad(2); // 2nd Argument
        cfw.add(ByteCode.FCMPG);

        int jumpTrue  = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();
        cfw.add(ByteCode.IFLT, jumpTrue);
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);

        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        BooleanFloat cmp =  (BooleanFloat) myClass.newInstance();
        assertFalse(cmp.test(10.0f,5.0f));
        assertFalse(cmp.test(5.0f,5.0f));
        assertTrue(cmp.test(5.0f,10.0f));
        assertFalse(cmp.test(Float.NaN,10.0f));
        assertFalse(cmp.test(5.0f,Float.NaN));
        assertFalse(cmp.test(Float.NaN,Float.NaN));
    }

    public void testCompareDoubleL() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$UnaryDouble");
        defaultConstructor();
        cfw.startMethod("unary", "(D)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addDLoad(1); // 1st Argument
        cfw.add(ByteCode.DCONST_0);
        cfw.add(ByteCode.DCMPL);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryDouble exec =  (UnaryDouble) myClass.newInstance();
        assertEquals(0, exec.unary(0.0d));
        assertEquals(1, exec.unary(10.0d));
        assertEquals(-1, exec.unary(-10.0d));
        assertEquals(-1, exec.unary(Double.NaN));
    }

    public void testCompareDoubleG() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/ConditionalTest$UnaryDouble");
        defaultConstructor();
        cfw.startMethod("unary", "(D)I", (short) (ClassFileWriter.ACC_PUBLIC));
        cfw.addDLoad(1); // 1st Argument
        cfw.add(ByteCode.DCONST_0);
        cfw.add(ByteCode.DCMPG);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryDouble exec =  (UnaryDouble) myClass.newInstance();
        assertEquals(0, exec.unary(0.0d));
        assertEquals(1, exec.unary(10.0d));
        assertEquals(-1, exec.unary(-10.0d));
        assertEquals(1, exec.unary(Double.NaN));
    }
}
