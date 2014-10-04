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

package au.com.illyrian.classmaker;

import org.mozilla.classfile.ByteCode;
import org.mozilla.classfile.ClassFileWriter;

public class MakerFloatTest extends ClassMakerTestCase implements ByteCode
{
    ClassFileWriter cfw;
    ClassMaker maker;
    ClassMakerFactory factory;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("MyClass", Object.class, "MyClass.java");
        cfw = maker.getClassFileWriter();
    }

    // Generate default constructor
    public void defaultConstructor() throws Exception
    {
        maker.Method("<init>", void.class, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
          maker.Return();
        maker.End();
    }


    public interface Run
    {
        void run();
    }

    public interface Eval
    {
        float eval();
    }

    public interface FloatUnary
    {
        float unary(float a);
    }

    public interface DoubleUnary
    {
        double unary(double a);
    }

    public interface FloatBinary
    {
        float binary(float x, float y);
    }

    public interface DoubleBinary
    {
        double binary(double x, double y);
    }

    public void testRun() throws Exception
    {
        maker.Implements(Run.class);
        defaultConstructor();
        maker.Declare("id", float.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Literal(2.0f));
        maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Run exec = (Run) myClass.newInstance();


        assertEquals("myObj.id should be 0", 0.0f, getFloatField(myClass, exec, "id"));
        exec.run();
        assertEquals("myObj.id should be 2", 2.0f, getFloatField(myClass, exec, "id"));
    }

    public void testEval() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Declare("id", float.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", float.class, ACC_PUBLIC);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Literal(2.0f));
        maker.Return(maker.Literal(5.0f));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Wrong value for exec.eval()", 5.0f, exec.eval());
        assertEquals("Wrong value for myObj.id", 2.0f, getFloatField(myClass, exec, "id"));

    }

    public void testSetClassFloat() throws Exception
    {
        maker.Implements(FloatUnary.class);
        defaultConstructor();

        maker.Declare("value", float.class, ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("x", float.class, 0);
        maker.Begin();
           maker.Return(maker.Assign(maker.This(), "value", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        FloatUnary exec = (FloatUnary)myClass.newInstance();

        assertEquals("Set failed", 6.0f, exec.unary(6.0f));
        assertEquals("Set values failed", 6.0f, getFloatField(myClass, exec, "value"));
    }

    public void testSetClassDouble() throws Exception
    {
        maker.Implements(DoubleUnary.class);
        defaultConstructor();

        maker.Declare("value", double.class, ACC_PUBLIC);

        maker.Method("unary", double.class, ACC_PUBLIC);
        maker.Declare("x", double.class, 0);
        maker.Begin();
           maker.Return(maker.Assign(maker.This(), "value", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        DoubleUnary exec = (DoubleUnary)myClass.newInstance();

        assertEquals("Set failed", 6.0, exec.unary(6.0));
        assertEquals("Set values failed", 6.0, getDoubleField(myClass, exec, "value"));
    }

    public void testSetStaticFloat() throws Exception
    {
        maker.Implements(FloatUnary.class);
        defaultConstructor();

        maker.Declare("value", float.class, ACC_STATIC | ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("x", float.class, 0);
        maker.Begin();
           maker.Return(maker.Assign(maker.getFullyQualifiedClassName(), "value", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        FloatUnary exec = (FloatUnary)myClass.newInstance();

        assertEquals("Set failed", 6.0f, exec.unary(6.0f));
        assertEquals("Set values failed", 6.0f, getFloatField(myClass, exec, "value"));
    }

    public void testSetStaticDouble() throws Exception
    {
        maker.Implements(DoubleUnary.class);
        defaultConstructor();

        maker.Declare("value", double.class, ACC_STATIC | ACC_PUBLIC);

        maker.Method("unary", double.class, ACC_PUBLIC);
        maker.Declare("x", double.class, 0);
        maker.Begin();
           maker.Return(maker.Assign(maker.getFullyQualifiedClassName(), "value", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        DoubleUnary exec = (DoubleUnary)myClass.newInstance();

        assertEquals("Set failed", 6.0, exec.unary(6.0));
        assertEquals("Set values failed", 6.0, getDoubleField(myClass, exec, "value"));
    }

    public void testFloatNegate() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", float.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        maker.Set(maker.This(), "x", maker.Neg(maker.Get(maker.This(), "x")));
        maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();
        setFloatField(myClass, exec, "x", 6.0f);
        exec.run();
        assertEquals("Negate failed", -6.0f, getFloatField(myClass, exec, "x"));
    }

    public void testDoubleNegate() throws Exception
    {
        maker.Implements(Runnable.class);
        defaultConstructor();
        maker.Declare("x", double.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        maker.Set(maker.This(), "x", maker.Neg(maker.Get(maker.This(), "x")));
        maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();
        setDoubleField(myClass, exec, "x", 6.0);
        exec.run();
        assertEquals("Negate failed", -6.0, getDoubleField(myClass, exec, "x"));
    }

    public void testIncrement() throws Exception
    {
        maker.Implements(FloatUnary.class);
        defaultConstructor();
        maker.Declare("id", float.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Literal(2.0f));
        maker.Return(maker.Add(maker.Get("a"), maker.Literal(1.0f)));
        maker.End();

        Class myClass = maker.defineClass();
        FloatUnary exec = (FloatUnary)myClass.newInstance();

        assertEquals("Wrong value for myObj.id", 0.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Wrong value for exec.eval()", 7.0f, exec.unary(6));
    }

    public void testDecrement() throws Exception
    {
        maker.Implements(FloatUnary.class);
        defaultConstructor();
        maker.Declare("id", float.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Literal(2.0f));
        maker.Return(maker.Add(maker.Get("a"), maker.Neg(maker.Literal(1.0f))));
        maker.End();

        Class myClass = maker.defineClass();
        FloatUnary exec = (FloatUnary)myClass.newInstance();

        assertEquals("Decrement failed", 5.0f, exec.unary(6.0f));
    }

    public void testNegate() throws Exception
    {
        maker.Implements(FloatUnary.class);
        defaultConstructor();
        maker.Declare("id", float.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Literal(2.0f));
        maker.Return(maker.Neg(maker.Get("a")));
        maker.End();

        Class myClass = maker.defineClass();
        FloatUnary exec = (FloatUnary)myClass.newInstance();

        assertEquals("Negate failed", -6.0f, exec.unary(6.0f));
    }

    public void testIntToFloat() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Declare("id", int.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", float.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get(maker.This(), "id"), float.class));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        setIntField(myClass, exec, "id", 64);
        assertEquals("Convert Integer to Float failed", 64.0f, exec.eval());
        setIntField(myClass, exec, "id", -64);
        assertEquals("Convert Integer to Float failed", -64.0f, exec.eval());
    }

    public void testLongToFloat() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Declare("id", long.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", float.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get(maker.This(), "id"), float.class));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        setLongField(myClass, exec, "id", 64);
        assertEquals("Convert Long to Float failed", 64.0f, exec.eval());
        setLongField(myClass, exec, "id", -64);
        assertEquals("Convert Long to Float failed", -64.0f, exec.eval());
    }

    public void testDoubleToFloat() throws Exception
    {
        maker.Implements(Eval.class);
        defaultConstructor();
        maker.Declare("id", double.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", float.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Cast(maker.Get(maker.This(), "id"), float.class));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        setDoubleField(myClass, exec, "id", 64.0d);
        assertEquals("Convert Integer to Float failed", 64.0f, exec.eval());
        setDoubleField(myClass, exec, "id", -64.0d);
        assertEquals("Convert Integer to Float failed", -64.0f, exec.eval());
    }

    public void testAddTwo() throws Exception
    {
        maker.Implements(FloatUnary.class);
        defaultConstructor();
        maker.Declare("id", float.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Literal(2.0f));
        maker.Return(maker.Add(maker.Get("a"), maker.Literal(2.0f)));
        maker.End();

        Class myClass = maker.defineClass();
        FloatUnary exec = (FloatUnary)myClass.newInstance();

        assertEquals("myObj.id should be 0", 0.0f, getFloatField(myClass, exec, "id"));
        assertEquals("Add Two failed", 8.0f, exec.unary(6));
    }

    public void testFloatAddOperator() throws Exception
    {
        maker.Implements(FloatBinary.class);
        defaultConstructor();

        maker.Method("binary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Declare("b", float.class, 0);
        maker.Begin();
            maker.Return(maker.Add(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        FloatBinary exec = (FloatBinary)myClass.newInstance();

        assertEquals("2.25 + 3.5 failed", 5.75f, exec.binary(2.25f, 3.5f));
        assertEquals("2.25 + -3.5 failed", -1.25f, exec.binary(2.25f, -3.5f));
        assertEquals("-2.25 + 3.5 failed", 1.25f, exec.binary(-2.25f, 3.5f));
        assertEquals("-2.25 + -3.5 failed", -5.75f, exec.binary(-2.25f, -3.5f));
    }

    public void testDoubleAddOperator() throws Exception
    {
        maker.Implements(DoubleBinary.class);
        defaultConstructor();

        maker.Method("binary", double.class, ACC_PUBLIC);
        maker.Declare("a", double.class, 0);
        maker.Declare("b", double.class, 0);
        maker.Begin();
            maker.Return(maker.Add(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        DoubleBinary exec = (DoubleBinary)myClass.newInstance();

        assertEquals("2.25 + 3.5 failed", 5.75, exec.binary(2.25, 3.5));
        assertEquals("2.25 + -3.5 failed", -1.25, exec.binary(2.25, -3.5));
        assertEquals("-2.25 + 3.5 failed", 1.25, exec.binary(-2.25, 3.5));
        assertEquals("-2.25 + -3.5 failed", -5.75, exec.binary(-2.25, -3.5));
    }

    public void testFloatSubtractOperator() throws Exception
    {
        maker.Implements(FloatBinary.class);
        defaultConstructor();

        maker.Method("binary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Declare("b", float.class, 0);
        maker.Begin();
            maker.Return(maker.Subt(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        FloatBinary exec = (FloatBinary)myClass.newInstance();

        assertEquals("2.25 - 3 failed", -1.25f, exec.binary(2.25f, 3.5f));
        assertEquals("2.25 - -3 failed", 5.75f, exec.binary(2.25f, -3.5f));
        assertEquals("-2.25 - 3 failed", -5.75f, exec.binary(-2.25f, 3.5f));
        assertEquals("-2.25 - -3 failed", 1.25f, exec.binary(-2.25f, -3.5f));
    }

    public void testDoubleSubtractOperator() throws Exception
    {
        maker.Implements(DoubleBinary.class);
        defaultConstructor();

        maker.Method("binary", double.class, ACC_PUBLIC);
        maker.Declare("a", double.class, 0);
        maker.Declare("b", double.class, 0);
        maker.Begin();
            maker.Return(maker.Subt(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        DoubleBinary exec = (DoubleBinary)myClass.newInstance();

        assertEquals("2.25 - 3 failed", -1.25, exec.binary(2.25, 3.5));
        assertEquals("2.25 - -3 failed", 5.75, exec.binary(2.25, -3.5));
        assertEquals("-2.25 - 3 failed", -5.75, exec.binary(-2.25, 3.5));
        assertEquals("-2.25 - -3 failed", 1.25, exec.binary(-2.25, -3.5));
    }

    public void testFloatMultiplyOperator() throws Exception
    {
        maker.Implements(FloatBinary.class);
        defaultConstructor();

        maker.Method("binary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Declare("b", float.class, 0);
        maker.Begin();
            maker.Return(maker.Mult(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        FloatBinary exec = (FloatBinary)myClass.newInstance();

        assertEquals("2 * 3 failed", 6.0f, exec.binary(2.0f, 3.0f));
        assertEquals("2 * -3 failed", -6.0f, exec.binary(2.0f, -3.0f));
        assertEquals("-2 * 3 failed", -6.0f, exec.binary(-2.0f, 3.0f));
        assertEquals("-2 * -3 failed", 6.0f, exec.binary(-2.0f, -3.0f));
    }

    public void testDoubleMultiplyOperator() throws Exception
    {
        maker.Implements(DoubleBinary.class);
        defaultConstructor();

        maker.Method("binary", double.class, ACC_PUBLIC);
        maker.Declare("a", double.class, 0);
        maker.Declare("b", double.class, 0);
        maker.Begin();
            maker.Return(maker.Mult(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        DoubleBinary exec = (DoubleBinary)myClass.newInstance();

        assertEquals("2 * 3 failed", 6.0, exec.binary(2.0, 3.0));
        assertEquals("2 * -3 failed", -6.0, exec.binary(2.0, -3.0));
        assertEquals("-2 * 3 failed", -6.0, exec.binary(-2.0, 3.0));
        assertEquals("-2 * -3 failed", 6.0, exec.binary(-2.0, -3.0));
    }

    public void testFloatDivideOperator() throws Exception
    {
        maker.Implements(FloatBinary.class);
        defaultConstructor();

        maker.Method("binary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Declare("b", float.class, 0);
        maker.Begin();
            maker.Return(maker.Div(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        FloatBinary exec = (FloatBinary)myClass.newInstance();

        assertEquals("6.0/ 3.0 failed",   2.0f, exec.binary(6.0f, 3.0f));
        assertEquals("6.0 / -3.0 failed", -2.0f, exec.binary(6.0f, -3.0f));
        assertEquals("-6.0 / 3.0 failed", -2.0f, exec.binary(-6.0f, 3.0f));
        assertEquals("-6.0 / -3.0 failed", 2.0f, exec.binary(-6.0f, -3.0f));
    }

    public void testDoubleDivideOperator() throws Exception
    {
        maker.Implements(DoubleBinary.class);
        defaultConstructor();

        maker.Method("binary", double.class, ACC_PUBLIC);
        maker.Declare("a", double.class, 0);
        maker.Declare("b", double.class, 0);
        maker.Begin();
            maker.Return(maker.Div(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        DoubleBinary exec = (DoubleBinary)myClass.newInstance();

        assertEquals("6.0/ 3.0 failed",   2.0, exec.binary(6.0, 3.0));
        assertEquals("6.0 / -3.0 failed", -2.0, exec.binary(6.0, -3.0));
        assertEquals("-6.0 / 3.0 failed", -2.0, exec.binary(-6.0, 3.0));
        assertEquals("-6.0 / -3.0 failed", 2.0, exec.binary(-6.0, -3.0));
    }

    public void testFloatRemainderOperator() throws Exception
    {
        maker.Implements(FloatBinary.class);
        defaultConstructor();

        maker.Method("binary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Declare("b", float.class, 0);
        maker.Begin();
        maker.Return(maker.Rem(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        FloatBinary exec = (FloatBinary)myClass.newInstance();

        assertEquals("7.0 % 3.0 failed",   1.0f, exec.binary(7.0f, 3.0f));
        assertEquals("7.0 % -3.0 failed",  1.0f, exec.binary(7.0f, -3.0f));
        assertEquals("-7.0 % 3.0 failed", -1.0f, exec.binary(-7.0f, 3.0f));
        assertEquals("-7.0 % -3.0 failed",-1.0f, exec.binary(-7.0f, -3.0f));
    }

    public void testDoubleRemainderOperator() throws Exception
    {
        maker.Implements(DoubleBinary.class);
        defaultConstructor();

        maker.Method("binary", double.class, ACC_PUBLIC);
        maker.Declare("a", double.class, 0);
        maker.Declare("b", double.class, 0);
        maker.Begin();
        maker.Return(maker.Rem(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        DoubleBinary exec = (DoubleBinary)myClass.newInstance();

        assertEquals("7.0 % 3.0 failed",   1.0, exec.binary(7.0, 3.0));
        assertEquals("7.0 % -3.0 failed",  1.0, exec.binary(7.0, -3.0));
        assertEquals("-7.0 % 3.0 failed", -1.0, exec.binary(-7.0, 3.0));
        assertEquals("-7.0 % -3.0 failed",-1.0, exec.binary(-7.0, -3.0));
    }

    public void testIncReturnFloat() throws Exception
    {
        maker.Implements(FloatUnary.class);
        defaultConstructor();
        maker.Declare("id", float.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("unary", float.class, ACC_PUBLIC);
        maker.Declare("a", float.class, 0);
        maker.Begin();
            maker.Eval(maker.Inc("a"));
            maker.Return(maker.Get("a"));
        maker.End();

        Class myClass = maker.defineClass();
        FloatUnary exec = (FloatUnary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 7.0f, exec.unary(6.0f));
    }

    public void testIncReturnDouble() throws Exception
    {
        maker.Implements(DoubleUnary.class);
        defaultConstructor();
        maker.Declare("id", double.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("unary", double.class, ACC_PUBLIC);
        maker.Declare("a", double.class, 0);
        maker.Begin();
            maker.Eval(maker.Inc("a"));
            maker.Return(maker.Get("a"));
        maker.End();

        Class myClass = maker.defineClass();
        DoubleUnary exec = (DoubleUnary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 7.0, exec.unary(6.0));
    }

}
