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

import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.PrimitiveType;

public class MakerLoadStoreTest extends ClassMakerTestCase implements ByteCode
{
    ClassMaker maker;
    ClassMakerFactory factory;

    public interface EvalInt
    {
        int eval();
    }

    public interface EvalLong
    {
        long eval();
    }

    public interface EvalFloat
    {
        float eval();
    }

    public interface EvalDouble
    {
        double eval();
    }

    public interface UnaryInt
    {
        int unary(int a);
    }

    public interface UnaryLong
    {
        long unary(long a);
    }

    public interface UnaryFloat
    {
        float unary(float a);
    }

    public interface UnaryDouble
    {
        double unary(double a);
    }

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        defaultConstructor();
    }

    // Generate default constructor
    public void defaultConstructor()
    {
        maker.Method("<init>", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public void testIntGetLocal() throws Exception
    {
        maker.Implements(UnaryInt.class);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertEquals("Local Get", 3, exec.unary(3));
        assertEquals("Local Get", 6, exec.unary(6));
    }

    public void testIntSetLocal() throws Exception
    {
        maker.Implements(UnaryInt.class);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Declare("a", PrimitiveType.INT_TYPE, 0);
        maker.Set("a", maker.Get("x"));
        maker.Return(maker.Get("a"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertEquals("Local Get", 3, exec.unary(3));
        assertEquals("Local Get", 6, exec.unary(6));
    }

    public void testIntAssignLocal() throws Exception
    {
        maker.Implements(UnaryInt.class);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Declare("a", PrimitiveType.INT_TYPE, 0);
        maker.Return(maker.Assign("a", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        assertEquals("Local Get", 3, exec.unary(3));
        assertEquals("Local Get", 6, exec.unary(6));
    }

    public void testIntGetField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, exec, "id", 3);
        assertEquals("Local Get", 3, exec.unary(0));
        setIntField(myClass, exec, "id", 6);
        assertEquals("Local Get", 6, exec.unary(0));
    }

    public void testIntGetSetThisField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Literal(0));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, exec, "id", -1);
        assertEquals("Local Get", 0, exec.unary(3));
        int id = getIntField(myClass, exec, "id");
        assertEquals("Set Field", 3, id);

        setIntField(myClass, exec, "id", -1);
        assertEquals("Local Get", 0, exec.unary(6));
        id = getIntField(myClass, exec, "id");
        assertEquals("Set Field", 6, id);
    }

    public void testIntFindAssignThisField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Assign(maker.This(), "id", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, exec, "id", -1);
        assertEquals("Local Get", 3, exec.unary(3));
        int id = getIntField(myClass, exec, "id");
        assertEquals("Set Field", 3, id);

        setIntField(myClass, exec, "id", -1);
        assertEquals("Local Get", 6, exec.unary(6));
        id = getIntField(myClass, exec, "id");
        assertEquals("Set Field", 6, id);
    }

    public void testIntFindAssignOtherField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("other", maker.getClassType(), ACC_PUBLIC);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Assign(maker.Get(maker.This(), "other"), "id", maker.Get("x")));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();
        Object   other = myClass.newInstance();

        setIntField(myClass, exec, "id", -1);
        setIntField(myClass, other, "id", -1);
        setField(myClass, exec, "other", other);

        assertEquals("Local Get", 3, exec.unary(3));
        int id = getIntField(myClass, exec, "id");
        assertEquals("exec.id", -1, id);
        id = getIntField(myClass, other, "id");
        assertEquals("other.id", 3, id);

        assertEquals("Local Get", 6, exec.unary(6));
        id = getIntField(myClass, exec, "id");
        assertEquals("exec.id", -1, id);
        id = getIntField(myClass, other, "id");
        assertEquals("other.id", 6, id);
    }

    public void testIntSetField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Literal(0));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, exec, "id", -1);
        assertEquals("Local Get", 0, exec.unary(3));
        int id = getIntField(myClass, exec, "id");
        assertEquals("Set Field", 3, id);

        setIntField(myClass, exec, "id", -1);
        assertEquals("Local Get", 0, exec.unary(6));
        id = getIntField(myClass, exec, "id");
        assertEquals("Set Field", 6, id);
    }

    /* Integer */
    public void testIntSetGetStatic() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.getFullyQualifiedClassName(), "id", maker.Get("x"));
        maker.Return(maker.Get(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, null, "id", 3);
        int id = getIntField(myClass, null, "id");
        assertEquals("id not updated", 3, id);
        int x = exec.unary(6);
        assertEquals("Increment failed", 6, x);
        id = getIntField(myClass, null, "id");
        assertEquals("id not updated", 6, id);
    }

    public void testIntSetGetStaticField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("n", PrimitiveType.INT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        // MyClass.n = MyClass.n + 10;
        maker.Set(maker.getFullyQualifiedClassName(), "n", maker.Add(maker.Get(maker.getFullyQualifiedClassName(), "n"), maker.Literal(10)));
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, exec, "id", 3);
        setIntField(myClass, null, "n", 100);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 3, id);
        int x = exec.unary(6);
        assertEquals("Increment failed", 6, x);
        id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 6, id);
        assertEquals("id not updated", 110, getIntField(myClass, null, "n"));
    }

    public void testIntInc() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Inc("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        int x = exec.unary(6);
        assertEquals("Increment failed", 7, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 7, id);
    }

    public void testIntDec() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Dec("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        int x = exec.unary(6);
        assertEquals("Decrement failed", 5, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 5, id);
    }

    public void testIntPostInc() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.PostInc("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        int x = exec.unary(6);
        assertEquals("Post Increment failed", 7, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id should not be updated", 6, id);
    }

    public void testIntPostDec() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.PostDec("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        int x = exec.unary(6);
        assertEquals("Post Decrement failed", 5, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id should not be updated", 6, id);
    }

    public void testIntIncField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Inc(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        int x = exec.unary(6);
        assertEquals("Increment failed", 7, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 7, id);

        assertEquals("Wrong value for myObj.eval()", 1, exec.unary(0));
        assertEquals("Wrong value for myObj.id", 1, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 2, exec.unary(1));
        assertEquals("Wrong value for myObj.id", 2, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 3, exec.unary(2));
        assertEquals("Wrong value for myObj.id", 3, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", Integer.MIN_VALUE, exec.unary(Integer.MAX_VALUE));
        assertEquals("Wrong value for myObj.id", Integer.MIN_VALUE, getIntField(myClass, exec, "id"));
    }

    public void testIntDecField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Dec(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        int x = exec.unary(6);
        assertEquals("Decrement failed", 5, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 5, id);
    }

    public void testIntIncPostField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.PostInc(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        int x = exec.unary(6);
        assertEquals("Increment failed", 6, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 7, id);

        assertEquals("Wrong value for myObj.eval()", 0, exec.unary(0));
        assertEquals("Wrong value for myObj.id", 1, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 1, exec.unary(1));
        assertEquals("Wrong value for myObj.id", 2, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", 2, exec.unary(2));
        assertEquals("Wrong value for myObj.id", 3, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for myObj.eval()", Integer.MAX_VALUE, exec.unary(Integer.MAX_VALUE));
        assertEquals("Wrong value for myObj.id", Integer.MIN_VALUE, getIntField(myClass, exec, "id"));
    }

    public void testIntPostDecField() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.PostDec(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        int x = exec.unary(6);
        assertEquals("Decrement failed", 6, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 5, id);
    }

    public void testIntIncStatic() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Inc(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, null, "id", 6);
        int x = exec.unary(0);
        assertEquals("Increment failed", 7, x);
        int id = getIntField(myClass, null, "id");
        assertEquals("id not updated", 7, id);
    }

    public void testIntDecStatic() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Dec(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, null, "id", 6);
        int x = exec.unary(0);
        assertEquals("Decrement failed", 5, x);
        int id = getIntField(myClass, null, "id");
        assertEquals("id not updated", 5, id);
    }

    public void testIntIncPostStatic() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.PostInc(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, null, "id", 6);
        int x = exec.unary(0);
        assertEquals("Increment failed", 6, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 7, id);
    }

    public void testIntPostDecStatic() throws Exception
    {
        maker.Implements(UnaryInt.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.INT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.PostDec(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryInt exec = (UnaryInt)myClass.newInstance();

        setIntField(myClass, null, "id", 6);
        int x = exec.unary(0);
        assertEquals("Decrement failed", 6, x);
        int id = getIntField(myClass, exec, "id");
        assertEquals("id not updated", 5, id);
    }

    public void testExceptions()
    {
        maker.Declare("id", String.class, ACC_PUBLIC);
        maker.Declare("n", boolean.class, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", String.class, 0);
        maker.Begin();
        try {
            maker.Inc(maker.This(), "id");
            fail("Cannot Inc type");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment field 'id' of type java.lang.String", ex.getMessage());
        }
        try {
            maker.Inc("x");
            fail("Cannot Inc type");
        } catch (ClassMakerException ex) {
            assertEquals("Cannot increment variable 'x' of type java.lang.String", ex.getMessage());
        }
        try {
            maker.Get(PrimitiveType.INT_TYPE.getValue(), "n");
            fail("Expected class");
        } catch (ClassMakerException ex) {
            assertEquals("Expected a class but was type int", ex.getMessage());
        }
        try {
            maker.Set(PrimitiveType.INT_TYPE.getValue(), "id", PrimitiveType.INT_TYPE.getValue());
            fail("Expected class");
        } catch (ClassMakerException ex) {
            assertEquals("Expected a class but was type int", ex.getMessage());
        }
        try {
            maker.Set(maker.getFullyQualifiedClassName(), "n", PrimitiveType.INT_TYPE.getValue());
            fail("Static not asiignable");
        } catch (ClassMakerException ex) {
            assertEquals("Static field \'n\' of type boolean cannot be assigned type int", ex.getMessage());
        }
        try {
            maker.Get(maker.getFullyQualifiedClassName(), "id");
            fail("Not static");
        } catch (ClassMakerException ex) {
            assertEquals("Class variable 'test.MyClass.id' is not static", ex.getMessage());
        }
        try {
            maker.Set(maker.getFullyQualifiedClassName(), "id", PrimitiveType.INT_TYPE.getValue());
            fail("Not static");
        } catch (ClassMakerException ex) {
            assertEquals("Class variable 'test.MyClass.id' is not static", ex.getMessage());
        }
        try {
            maker.Set(maker.getFullyQualifiedClassName(), "n", PrimitiveType.INT_TYPE.getValue());
            fail("Static not asiignable");
        } catch (ClassMakerException ex) {
            assertEquals("Static field 'n' of type boolean cannot be assigned type int", ex.getMessage());
        }

    }

    /* Float */

    public void testFloatSetGetField() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        setFloatField(myClass, exec, "id", 3.0f);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 3.0f, id);
        float x = exec.unary(6);
        assertEquals("Increment failed", 6.0f, x);
        id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 6.0f, id);
    }

    public void testFloatInc() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Inc("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        float x = exec.unary(6.0f);
        assertEquals("Increment failed", 7.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 7.0f, id);
    }

    public void testFloatDec() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Dec("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        float x = exec.unary(6.0f);
        assertEquals("Decrement failed", 5.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 5.0f, id);
    }

    public void testFloatPostInc() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.PostInc("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        float x = exec.unary(6.0f);
        assertEquals("Post Increment failed", 7.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id should not be updated", 6.0f, id);
    }

    public void testFloatPostDec() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.PostDec("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        float x = exec.unary(6.0f);
        assertEquals("Post Decrement failed", 5.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id should not be updated", 6.0f, id);
    }

    public void testFloatIncField() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Inc(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        float x = exec.unary(6.0f);
        assertEquals("Increment failed", 7.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 7.0f, id);
    }

    public void testFloatDecField() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Dec(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        float x = exec.unary(6.0f);
        assertEquals("Decrement failed", 5.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 5.0f, id);
    }

    public void testFloatIncPostField() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.PostInc(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        float x = exec.unary(6.0f);
        assertEquals("Increment failed", 6.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 7.0f, id);
    }

    public void testFloatPostDecField() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.PostDec(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        float x = exec.unary(6.0f);
        assertEquals("Decrement failed", 6.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 5.0f, id);
    }


    public void testFloatSetGetStatic() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Set(maker.getFullyQualifiedClassName(), "id", maker.Get("x"));
        maker.Return(maker.Get(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        setFloatField(myClass, null, "id", 3.0f);
        float id = getFloatField(myClass, null, "id");
        assertEquals("id not updated", 3.0f, id);
        float x = exec.unary(6.0f);
        assertEquals("Increment failed", 6.0f, x);
        id = getFloatField(myClass, null, "id");
        assertEquals("id not updated", 6.0f, id);
    }

    public void testFloatIncStatic() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Inc(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        setFloatField(myClass, null, "id", 6.0f);
        float x = exec.unary(0.0f);
        assertEquals("Increment failed", 7.0f, x);
        float id = getFloatField(myClass, null, "id");
        assertEquals("id not updated", 7.0f, id);
    }

    public void testFloatDecStatic() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Dec(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        setFloatField(myClass, null, "id", 6.0f);
        float x = exec.unary(0.0f);
        assertEquals("Decrement failed", 5.0f, x);
        float id = getFloatField(myClass, null, "id");
        assertEquals("id not updated", 5.0f, id);
    }

    public void testFloatIncPostStatic() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.PostInc(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        setFloatField(myClass, null, "id", 6.0f);
        float x = exec.unary(0.0f);
        assertEquals("Increment failed", 6.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 7.0f, id);
    }

    public void testFloatPostDecStatic() throws Exception
    {
        maker.Implements(UnaryFloat.class);
        maker.Declare("id", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.FLOAT_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.FLOAT_TYPE, 0);
        maker.Begin();
        maker.Return(maker.PostDec(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryFloat exec = (UnaryFloat)myClass.newInstance();

        setFloatField(myClass, null, "id", 6.0f);
        float x = exec.unary(0.0f);
        assertEquals("Decrement failed", 6.0f, x);
        float id = getFloatField(myClass, exec, "id");
        assertEquals("id not updated", 5f, id);
    }

    /* Long */

    public void testLongSetGetField() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        setLongField(myClass, exec, "id", 3l);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 3l, id);
        long x = exec.unary(6);
        assertEquals("Increment failed", 6l, x);
        id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 6l, id);
    }

    public void testLongInc() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Inc("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        long x = exec.unary(6l);
        assertEquals("Increment failed", 7l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 7l, id);
    }

    public void testLongDec() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Dec("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        long x = exec.unary(6l);
        assertEquals("Decrement failed", 5l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 5l, id);
    }

    public void testLongPostInc() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.PostInc("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        long x = exec.unary(6l);
        assertEquals("Post Increment failed", 7l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id should not be updated", 6l, id);
    }

    public void testLongPostDec() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.PostDec("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        long x = exec.unary(6l);
        assertEquals("Post Decrement failed", 5l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id should not be updated", 6l, id);
    }

    public void testLongIncField() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Inc(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        long x = exec.unary(6l);
        assertEquals("Increment failed", 7l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 7l, id);
    }

    public void testLongDecField() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Dec(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        long x = exec.unary(6l);
        assertEquals("Decrement failed", 5l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 5l, id);
    }

    public void testLongIncPostField() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.PostInc(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        long x = exec.unary(6l);
        assertEquals("Increment failed", 6l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 7l, id);
    }

    public void testLongPostDecField() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.PostDec(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        long x = exec.unary(6l);
        assertEquals("Decrement failed", 6l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 5l, id);
    }


    public void testLongSetGetStatic() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Set(maker.getFullyQualifiedClassName(), "id", maker.Get("x"));
        maker.Return(maker.Get(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        setLongField(myClass, null, "id", 3l);
        long id = getLongField(myClass, null, "id");
        assertEquals("id not updated", 3l, id);
        long x = exec.unary(6l);
        assertEquals("Increment failed", 6l, x);
        id = getLongField(myClass, null, "id");
        assertEquals("id not updated", 6l, id);
    }

    public void testLongIncStatic() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Inc(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        setLongField(myClass, null, "id", 6l);
        long x = exec.unary(0l);
        assertEquals("Increment failed", 7l, x);
        long id = getLongField(myClass, null, "id");
        assertEquals("id not updated", 7l, id);
    }

    public void testLongDecStatic() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Dec(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        setLongField(myClass, null, "id", 6l);
        long x = exec.unary(0l);
        assertEquals("Decrement failed", 5l, x);
        long id = getLongField(myClass, null, "id");
        assertEquals("id not updated", 5l, id);
    }

    public void testLongIncPostStatic() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.PostInc(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        setLongField(myClass, null, "id", 6l);
        long x = exec.unary(0l);
        assertEquals("Increment failed", 6l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 7l, id);
    }

    public void testLongPostDecStatic() throws Exception
    {
        maker.Implements(UnaryLong.class);
        maker.Declare("id", PrimitiveType.LONG_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.LONG_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.LONG_TYPE, 0);
        maker.Begin();
        maker.Return(maker.PostDec(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryLong exec = (UnaryLong)myClass.newInstance();

        setLongField(myClass, null, "id", 6l);
        long x = exec.unary(0l);
        assertEquals("Decrement failed", 6l, x);
        long id = getLongField(myClass, exec, "id");
        assertEquals("id not updated", 5l, id);
    }

    /* Double */

    public void testDoubleSetGetField() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        setDoubleField(myClass, exec, "id", 3.0d);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 3.0d, id);
        double x = exec.unary(6);
        assertEquals("Increment failed", 6.0d, x);
        id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 6.0d, id);
    }

    public void testDoubleInc() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Inc("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        double x = exec.unary(6.0d);
        assertEquals("Increment failed", 7.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 7.0d, id);
    }

    public void testDoubleDec() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Dec("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        double x = exec.unary(6.0d);
        assertEquals("Decrement failed", 5.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 5.0d, id);
    }

    public void testDoublePostInc() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.PostInc("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        double x = exec.unary(6.0d);
        assertEquals("Post Increment failed", 7.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id should not be updated", 6.0d, id);
    }

    public void testDoublePostDec() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.PostDec("x"));
        maker.Return(maker.Get("x"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        double x = exec.unary(6.0d);
        assertEquals("Post Decrement failed", 5.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id should not be updated", 6.0d, id);
    }

    public void testDoubleIncField() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Inc(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        double x = exec.unary(6.0d);
        assertEquals("Increment failed", 7.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 7.0d, id);
    }

    public void testDoubleDecField() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.Dec(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        double x = exec.unary(6.0d);
        assertEquals("Decrement failed", 5.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 5.0d, id);
    }

    public void testDoubleIncPostField() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.PostInc(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        double x = exec.unary(6.0d);
        assertEquals("Increment failed", 6.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 7.0d, id);
    }

    public void testDoublePostDecField() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.This(), "id", maker.Get("x"));
        maker.Return(maker.PostDec(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        double x = exec.unary(6.0d);
        assertEquals("Decrement failed", 6.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 5.0d, id);
    }


    public void testDoubleSetGetStatic() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Set(maker.getFullyQualifiedClassName(), "id", maker.Get("x"));
        maker.Return(maker.Get(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        setDoubleField(myClass, null, "id", 3.0d);
        double id = getDoubleField(myClass, null, "id");
        assertEquals("id not updated", 3.0d, id);
        double x = exec.unary(6.0d);
        assertEquals("Increment failed", 6.0d, x);
        id = getDoubleField(myClass, null, "id");
        assertEquals("id not updated", 6.0d, id);
    }

    public void testDoubleIncStatic() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Inc(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        setDoubleField(myClass, null, "id", 6.0d);
        double x = exec.unary(0.0d);
        assertEquals("Increment failed", 7.0d, x);
        double id = getDoubleField(myClass, null, "id");
        assertEquals("id not updated", 7.0d, id);
    }

    public void testDoubleDecStatic() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.Dec(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        setDoubleField(myClass, null, "id", 6.0d);
        double x = exec.unary(0.0d);
        assertEquals("Decrement failed", 5.0d, x);
        double id = getDoubleField(myClass, null, "id");
        assertEquals("id not updated", 5.0d, id);
    }

    public void testDoubleIncPostStatic() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.PostInc(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        setDoubleField(myClass, null, "id", 6.0d);
        double x = exec.unary(0.0d);
        assertEquals("Increment failed", 6.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 7.0d, id);
    }

    public void testDoublePostDecStatic() throws Exception
    {
        maker.Implements(UnaryDouble.class);
        maker.Declare("id", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC | ACC_STATIC);

        maker.Method("unary", PrimitiveType.DOUBLE_TYPE, ACC_PUBLIC);
        maker.Declare("x", PrimitiveType.DOUBLE_TYPE, 0);
        maker.Begin();
        maker.Return(maker.PostDec(maker.getFullyQualifiedClassName(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)myClass.newInstance();

        setDoubleField(myClass, null, "id", 6.0d);
        double x = exec.unary(0.0d);
        assertEquals("Decrement failed", 6.0d, x);
        double id = getDoubleField(myClass, exec, "id");
        assertEquals("id not updated", 5.0d, id);
    }

    /* End Double */

    public class Store
    {
        public int i = 1;
    }

    public void testException() throws Exception
    {

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("x", Runnable.class, 0);
        maker.Begin();
        try {
            maker.Get("a");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "No local variable called 'a'", ex.getMessage());
        }
        try {
            maker.Set("a", PrimitiveType.INT_TYPE.getValue());
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "No local variable called 'a'", ex.getMessage());
        }
        try {
            maker.Get(maker.This(), "a");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot find member field 'a' in class test.MyClass", ex.getMessage());
        }
        try {
            maker.Set(maker.This(), "a", PrimitiveType.INT_TYPE.getValue());
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot find member field 'a' in class test.MyClass", ex.getMessage());
        }

        DeclaredType declaredVoid = maker.getDeclaredType(PrimitiveType.VOID_TYPE);
        try {
            MakerField field = new MakerField("a", declaredVoid, 0);
            field.setSlot(1);
            maker.loadLocal(field);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Don't know how to load type: void", ex.getMessage());
        }
        try {
            MakerField field = new MakerField("a", declaredVoid, 0);
            field.setSlot(1);
            maker.storeLocal(field, PrimitiveType.VOID_TYPE);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Don't know how to store type: void", ex.getMessage());
        }
        try {
            maker.Declare("store", void.class, 0);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot declare a variable of type void", ex.getMessage());
        }
        try {
            maker.Declare("store", ClassType.NULL_TYPE, 0);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot declare a variable of type null", ex.getMessage());
        }

        // FIXME - classnames should have dot seaparators
        maker.Declare("store", Store.class, 0);
        maker.Declare("runnable", Runnable.class, 0);
        String storeName = Store.class.getName();
        try {
            maker.Get(storeName, "a");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot find member field 'a' in class " + storeName, ex.getMessage());
        }
        try {
            maker.Set(storeName, "a", PrimitiveType.INT_TYPE.getValue());
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot find member field 'a' in class " + storeName, ex.getMessage());
        }
        try {
            maker.Set("store", maker.Get("runnable"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot assign type java.lang.Runnable to local variable \'store\' of type " + storeName, ex.getMessage());
        }
        try {
            maker.Set("runnable", PrimitiveType.INT_TYPE.getValue());
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot assign type int to local variable \'runnable\' of type java.lang.Runnable", ex.getMessage());
        }
    }
}
