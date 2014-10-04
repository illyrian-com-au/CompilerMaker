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

public class BranchingTest extends ClassFileWriterTestCase
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

    public void testGoto() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/BranchingTest$Eval");
        defaultConstructor();
        // declare int id;
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        int jump = cfw.acquireLabel();
        cfw.add(ByteCode.GOTO, jump);
        // set id to 2
        cfw.addPush(2);
        cfw.addLoadThis();
        cfw.add(ByteCode.SWAP);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");

        cfw.markLabel(jump);

        cfw.add(ByteCode.ICONST_4);
        cfw.add(ByteCode.IRETURN);
        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        int id =getIntField(myClass, exec, "id");
        assertEquals("Wrong initial value for myObj.id", 0, id);

        id = exec.eval();
        assertEquals("Wrong value for exec.eval()", 4, id);

        id = getIntField(myClass, exec, "id");
        assertEquals("Wrong value for myObj.id", 0, id);
    }

    public void testSubroutine() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/BranchingTest$Eval");
        defaultConstructor();
        // declare int id;
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("eval", "()I", (short) (ClassFileWriter.ACC_PUBLIC));

        int jump = cfw.acquireLabel();
        cfw.add(ByteCode.JSR, jump);
        cfw.add(ByteCode.JSR, jump);

        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "I");
        cfw.add(ByteCode.IRETURN);

        cfw.markLabel(jump);
        cfw.add(ByteCode.ASTORE_1); // Store return address in Local 1

        // increment id
        cfw.addLoadThis();
        cfw.add(ByteCode.GETFIELD, cfw.getClassName(), "id", "I");
        cfw.add(ByteCode.ICONST_1);
        cfw.add(ByteCode.IADD);

        cfw.addLoadThis();
        cfw.add(ByteCode.SWAP);
        cfw.add(ByteCode.PUTFIELD, cfw.getClassName(), "id", "I");
        cfw.add(ByteCode.RET, 1);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Eval exec = (Eval)myClass.newInstance();

        int id =getIntField(myClass, exec, "id");
        assertEquals("Wrong initial value for myObj.id", 0, id);

        id = exec.eval();
        assertEquals("Wrong value for exec.eval()", 2, id);

        id = getIntField(myClass, exec, "id");
        assertEquals("Wrong value for myObj.id", 2, id);
    }

    public void testIfBranch() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/BranchingTest$Unary");
        defaultConstructor();
        // declare int id;
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addILoad(1);  // 1st Argument
        cfw.addIStore(2);  // 2nd Local

        int endIf = cfw.acquireLabel();
        cfw.addILoad(1);  // 1st Argument
        cfw.add(ByteCode.IFEQ, endIf);

        // set local to 2
        cfw.addPush(2);
        cfw.addIStore(2);  // 2nd Local

        cfw.markLabel(endIf);

        cfw.addLoadThis();
        cfw.addILoad(2);  // 2nd Local
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(-1));
    }

    public void testIfNonNullBranch() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/BranchingTest$UnaryObject");
        defaultConstructor();
        cfw.startMethod("unary", "(Ljava/lang/Object;)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $2 = 1
        cfw.add(ByteCode.ICONST_1);  // 1st Argument
        cfw.addIStore(2);  // 2nd Local

        // IF ($1 != null) GOTO endIf
        int endIf = cfw.acquireLabel();
        cfw.addALoad(1);  // 1st Argument
        cfw.add(ByteCode.IFNONNULL, endIf);

        // set $2 = 2
        cfw.add(ByteCode.ICONST_2);
        cfw.addIStore(2);  // 2nd Local

        // END IF
        cfw.markLabel(endIf);

        cfw.addLoadThis();
        cfw.addILoad(2);  // 2nd Local
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryObject exec = (UnaryObject)myClass.newInstance();
        String test = "Hello";

        assertEquals("Wrong value for exec.eval()", 2, exec.unary(null));
        assertEquals("Wrong value for exec.eval()", 1, exec.unary(test));
    }

    public void testIfNullBranch() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/BranchingTest$UnaryObject");
        defaultConstructor();
        cfw.startMethod("unary", "(Ljava/lang/Object;)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $2 = 1
        cfw.add(ByteCode.ICONST_1);  // 1st Argument
        cfw.addIStore(2);  // 2nd Local

        int endIf = cfw.acquireLabel();
        cfw.addALoad(1);  // 1st Argument
        cfw.add(ByteCode.IFNULL, endIf);

        // set local to 2
        cfw.add(ByteCode.ICONST_2);
        cfw.addIStore(2);  // 2nd Local

        cfw.markLabel(endIf);

        cfw.addLoadThis();
        cfw.addILoad(2);  // 2nd Local
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        UnaryObject exec = (UnaryObject)myClass.newInstance();
        String test = "Hello";

        assertEquals("Wrong value for exec.eval()", 1, exec.unary(null));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(test));
    }

    public void testIfElseBranch() throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/BranchingTest$Unary");
        defaultConstructor();
        // declare int id;
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        cfw.addILoad(1);  // 1st Argument
        cfw.addIStore(2);  // 2nd Local

        int endIf = cfw.acquireLabel();
        int endElse = cfw.acquireLabel();

        // IF $1 == 0 GOTO endIf
        cfw.addILoad(1);  // 1st Argument
        cfw.add(ByteCode.IFEQ, endIf);

        // SET $2 = 2
        cfw.addPush(2);
        cfw.addIStore(2);  // 2nd Local

        // ELSE
        cfw.add(ByteCode.GOTO, endElse);
        cfw.markLabel(endIf);

        // SET $2 = 3
        cfw.addPush(3);
        cfw.addIStore(2);  // 2nd Local

        // ENDIF
        cfw.markLabel(endElse);

        // RETURN $2
        cfw.addLoadThis();
        cfw.addILoad(2);  // 2nd Local
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 3, exec.unary(0));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(-1));
    }

    public Unary methodWhileBranch(int condition, int step) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/BranchingTest$Unary");
        defaultConstructor();
        // declare int id;
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("unary", "(I)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $2 = 0
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(2);  // 2nd Local

        int beginWhile = cfw.acquireLabel();
        int condWhile = cfw.acquireLabel();
        int endWhile = cfw.acquireLabel();

        // BEGIN While
        cfw.add(ByteCode.GOTO, condWhile);
        cfw.markLabel(beginWhile);

        cfw.add(ByteCode.IINC, 2, 1);  // Count number of loops

        // DEC $1
        cfw.add(ByteCode.IINC, 1, step);  // Decrement 1st argument

        // WHILE ($1 != 0) GOTO beginWhile
        cfw.markLabel(condWhile);
        cfw.addILoad(1);
        cfw.add(condition, beginWhile);

        // END while
        cfw.markLabel(endWhile); // BREAK to here.

        // RETURN $2
        cfw.addILoad(2);  // 2nd Local
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 3);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Unary)myClass.newInstance();
    }

    public void testWhileNEBranch() throws Exception
    {
        Unary exec = methodWhileBranch(ByteCode.IFNE, -1);

        assertEquals("Wrong value for exec.eval()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(2));
    }

    public void testWhileEQBranch() throws Exception
    {
        Unary exec = methodWhileBranch(ByteCode.IFEQ, -1);

        assertEquals("Wrong value for exec.eval()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.eval()", 0, exec.unary(2));
    }

    public void testWhileLTBranch() throws Exception
    {
        Unary exec = methodWhileBranch(ByteCode.IFLT, 1);

        assertEquals("Wrong value for exec.eval()", 0, exec.unary(2));
        assertEquals("Wrong value for exec.eval()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(-2));
    }

    public void testWhileLEBranch() throws Exception
    {
        Unary exec = methodWhileBranch(ByteCode.IFLE, 1);

        assertEquals("Wrong value for exec.eval()", 0, exec.unary(2));
        assertEquals("Wrong value for exec.eval()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.eval()", 3, exec.unary(-2));
    }

    public void testWhileGTBranch() throws Exception
    {
        Unary exec = methodWhileBranch(ByteCode.IFGT, -1);

        assertEquals("Wrong value for exec.eval()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.eval()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.eval()", 0, exec.unary(-2));
    }

    public void testWhileGEBranch() throws Exception
    {
        Unary exec = methodWhileBranch(ByteCode.IFGE, -1);

        assertEquals("Wrong value for exec.eval()", 3, exec.unary(2));
        assertEquals("Wrong value for exec.eval()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.eval()", 0, exec.unary(-2));
    }

    public Binary methodForBranch(int condition, int step) throws Exception
    {
        startClass("MyClass", "org/mozilla/classfile/BranchingTest$Binary");
        defaultConstructor();
        // declare int id;
        cfw.addField("id", "I", ClassFileWriter.ACC_PUBLIC);
        cfw.startMethod("binary", "(II)I", (short) (ClassFileWriter.ACC_PUBLIC));

        // SET $3 = 0
        cfw.add(ByteCode.ICONST_0);
        cfw.addIStore(3);

        int beginFor = cfw.acquireLabel();
        int condFor = cfw.acquireLabel();
        int endFor = cfw.acquireLabel();

        // FOR ($4 = $1; $4 < $2; $4++)
        cfw.addILoad(1);  // 1st Argument
        cfw.addIStore(4);  // 2nd Local
        cfw.add(ByteCode.GOTO, condFor);

        cfw.markLabel(beginFor); // Top of loop

        // $3++
        cfw.add(ByteCode.IINC, 3, 1);  // Count number of loops

        // $4 = $4 + step
        cfw.add(ByteCode.IINC, 4, step);  // Increment loop variable

        // WHILE ($4 < $2) GOTO beginFor
        cfw.markLabel(condFor);
        cfw.addILoad(4);
        cfw.addILoad(2);
        cfw.add(condition, beginFor);

        // END FOR
        cfw.markLabel(endFor);

        // RETURN $3
        cfw.addILoad(3);  // Count of loops
        cfw.add(ByteCode.IRETURN);

        cfw.stopMethod((short) 5);

        Class myClass = defineClass("MyClass", cfw.toByteArray());
        return (Binary)myClass.newInstance();
    }

    public void testForLTBranch() throws Exception
    {
        Binary exec = methodForBranch(ByteCode.IF_ICMPLT, 1);

        assertEquals("Wrong value for exec.eval()", 3, exec.binary(0, 3));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(1, 6));
        assertEquals("Wrong value for exec.eval()", 3, exec.binary(-3, 0));
        assertEquals("Wrong value for exec.eval()", 0, exec.binary(3, 0));
    }

    public void testForGTBranch() throws Exception
    {
        Binary exec = methodForBranch(ByteCode.IF_ICMPGT, -1);

        assertEquals("Wrong value for exec.eval()", 3, exec.binary(3, 0));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(6, 1));
        assertEquals("Wrong value for exec.eval()", 3, exec.binary(0, -3));
        assertEquals("Wrong value for exec.eval()", 0, exec.binary(0, 3));
    }

    public void testForLEBranch() throws Exception
    {
        Binary exec = methodForBranch(ByteCode.IF_ICMPLE, 1);

        assertEquals("Wrong value for exec.eval()", 4, exec.binary(0, 3));
        assertEquals("Wrong value for exec.eval()", 6, exec.binary(1, 6));
        assertEquals("Wrong value for exec.eval()", 1, exec.binary(0, 0));
        assertEquals("Wrong value for exec.eval()", 0, exec.binary(3, 0));
    }

    public void testForGEBranch() throws Exception
    {
        Binary exec = methodForBranch(ByteCode.IF_ICMPGE, -1);

        assertEquals("Wrong value for exec.eval()", 4, exec.binary(3, 0));
        assertEquals("Wrong value for exec.eval()", 6, exec.binary(6, 1));
        assertEquals("Wrong value for exec.eval()", 1, exec.binary(0, 0));
        assertEquals("Wrong value for exec.eval()", 0, exec.binary(0, 3));
    }

    public void testForEQBranch() throws Exception
    {
        Binary exec = methodForBranch(ByteCode.IF_ICMPEQ, 1);

        assertEquals("Wrong value for exec.eval()", 0, exec.binary(0, 3));
        assertEquals("Wrong value for exec.eval()", 1, exec.binary(3, 3));
        assertEquals("Wrong value for exec.eval()", 1, exec.binary(0, 0));
        assertEquals("Wrong value for exec.eval()", 0, exec.binary(0, -3));
    }

    public void testForNEBranch() throws Exception
    {
        Binary exec = methodForBranch(ByteCode.IF_ICMPNE, 1);

        assertEquals("Wrong value for exec.eval()", 3, exec.binary(0, 3));
        assertEquals("Wrong value for exec.eval()", 0, exec.binary(3, 3));
        assertEquals("Wrong value for exec.eval()", 0, exec.binary(0, 0));
        assertEquals("Wrong value for exec.eval()", 6, exec.binary(-3, 3));
    }
}
