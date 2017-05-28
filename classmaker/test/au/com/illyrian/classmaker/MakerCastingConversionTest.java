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

public class MakerCastingConversionTest extends ClassMakerTestCase implements ByteCode
{
    ClassMakerFactory factory;
    ClassMaker maker;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        defaultConstructor(maker);
    }

    // Generate default constructor
    public void defaultConstructor(ClassMaker maker) throws Exception
    {
        maker.Method("<init>", void.class, ByteCode.ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

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

    public interface ObjectEval
    {
        public Object eval(Object a);
    }

    public void testObjectObjectCast() throws Exception
    {
        maker.Implements(ObjectEval.class);

        maker.Declare("val", String.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", Object.class, ACC_PUBLIC);
        maker.Declare("a", Object.class, 0);
        maker.Begin();
            // Test: public Type Cast(Type source, Class target)
            maker.Set(maker.This(), "val", maker.Cast(maker.Get("a"), String.class));
            // Test: public Type Cast(Type source, String target)
            maker.Set(maker.This(), "val", maker.Cast(maker.Get("a"), "java.lang.String"));
            // Test: public Type Cast(Type source, Type target)
            //maker.Set(maker.This(), "val", maker.Cast(maker.Get("a"), ClassMaker.STRING_TYPE));
            maker.Return(maker.Get(maker.This(), "val"));
        maker.End();

        Class myClass = maker.defineClass();
        ObjectEval exec = (ObjectEval)myClass.newInstance();

        String result = (String)this.getField(exec.getClass(), exec, "val");
        assertNull("Should be null", result);
        exec.eval("Hello World");
        result = (String)this.getField(exec.getClass(), exec, "val");
        assertEquals("Wrong value", "Hello World", result);
    }

    public class DoRun implements Runnable
    {
        public void run()
        {
        }
    }

    public void testObjectInterfaceCast() throws Exception
    {
        maker.Implements(ObjectEval.class);

        maker.Declare("runner", Runnable.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", Object.class, ACC_PUBLIC);
        maker.Declare("a", Object.class, 0);
        maker.Begin();
            // Test: public Type Cast(Type source, Class target)
            maker.Set(maker.This(), "runner", maker.Cast(maker.Get("a"), Runnable.class));
            // Test: public Type Cast(Type source, String target)
            maker.Set(maker.This(), "runner", maker.Cast(maker.Get("a"), "java.lang.Runnable"));
            maker.Return(maker.Get(maker.This(), "runner"));
        maker.End();

        Class myClass = maker.defineClass();
        ObjectEval exec = (ObjectEval)myClass.newInstance();

        Runnable result = (Runnable)this.getField(exec.getClass(), exec, "runner");
        assertNull("Should be null", result);
        exec.eval(new DoRun());
        result = (Runnable)this.getField(exec.getClass(), exec, "runner");
        assertNotNull("Wrong value", result);
    }

    public void testInterfaceObjectCast() throws Exception
    {
        maker.Implements(Runnable.class);

        maker.Declare("source", Runnable.class, ClassFileWriter.ACC_PUBLIC);
        maker.Declare("target", DoRun.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "target", maker.Cast(maker.Get(maker.This(), "source"), DoRun.class));
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        setField(myClass, exec, "source", new DoRun());
        exec.run();
        assertNotNull("Wrong value", getField(myClass, exec, "target"));
    }

    public void testThisObjectCast() throws Exception
    {
        maker.Implements(Runnable.class);

        maker.Declare("target", maker.getClassType(), ClassFileWriter.ACC_PUBLIC);
        maker.Declare("runner", Runnable.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "target", maker.Cast(maker.This(), maker.getClassType()));
            maker.Set(maker.This(), "target", maker.This());
            maker.Set(maker.This(), "runner", maker.Cast(maker.This(), Runnable.class));
            maker.Set(maker.This(), "runner", maker.This());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertNotNull("Wrong value", getField(myClass, exec, "target"));
    }

    public void testNullCast() throws Exception
    {
        maker.Implements(Runnable.class);

        maker.Declare("target", maker.getClassType(), ClassFileWriter.ACC_PUBLIC);
        maker.Declare("runner", Runnable.class, ClassFileWriter.ACC_PUBLIC);
        maker.Declare("message", String.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Set(maker.This(), "target", maker.Cast(maker.Null(), maker.getClassType()));
            maker.Set(maker.This(), "target", maker.Null());
            maker.Set(maker.This(), "runner", maker.Cast(maker.Null(), Runnable.class));
            maker.Set(maker.This(), "runner", maker.Null());
            maker.Set(maker.This(), "message", maker.Cast(maker.Null(), String.class));
            maker.Set(maker.This(), "message", maker.Null());
            maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertNull("Should be null", getField(myClass, exec, "target"));
        assertNull("Should be null", getField(myClass, exec, "runner"));
        assertNull("Should be null", getField(myClass, exec, "message"));
    }

    public void testCastException() throws Exception
    {
        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
        try {
            maker.Cast(maker.This(), Runnable.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot cast from type test.MyClass to type java.lang.Runnable", ex.getMessage());
        }
        try {
            maker.Cast(maker.This(), String.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot cast from type test.MyClass to type java.lang.String", ex.getMessage());
        }
        try {
            maker.Cast(maker.This(), "does/not/Exist");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("No type called \'does/not/Exist\'", ex.getMessage());
        }
    }

    public static interface BooleanEval
    {
        public boolean eval();
    }
    
    public void testThisInstanceOfObject() throws Exception
    {
        maker.Implements(BooleanEval.class);

        maker.Declare("target", boolean.class, ClassFileWriter.ACC_PUBLIC);
        maker.Declare("runner", Runnable.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Begin();
            maker.Return(maker.InstanceOf(maker.This(), "java.lang.Object"));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanEval exec = (BooleanEval)myClass.newInstance();

        boolean result = exec.eval();
        assertTrue("this instanceof java.lang.Object", result);
    }

    public void testThisInstanceOfRunnable() throws Exception
    {
        maker.Implements(BooleanEval.class);

        maker.Declare("target", boolean.class, ClassFileWriter.ACC_PUBLIC);
        maker.Declare("runner", Runnable.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Begin();
            maker.Return(maker.InstanceOf(maker.This(), "java.lang.Runnable"));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanEval exec = (BooleanEval)myClass.newInstance();

        boolean result = exec.eval();
        assertFalse("this instanceof java.lang.Runnable", result);
    }

    public void testThisInstanceOfBooleanEval() throws Exception
    {
        maker.Implements(BooleanEval.class);

        maker.Declare("target", boolean.class, ClassFileWriter.ACC_PUBLIC);
        maker.Declare("runner", Runnable.class, ClassFileWriter.ACC_PUBLIC);

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Begin();
            maker.Return(maker.InstanceOf(maker.This(), BooleanEval.class));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanEval exec = (BooleanEval)myClass.newInstance();

        boolean result = exec.eval();
        assertTrue("this instanceof java.lang.Object", result);
    }
}
