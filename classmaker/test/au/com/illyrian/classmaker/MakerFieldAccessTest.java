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

import java.io.File;

import org.mozilla.classfile.ByteCode;

import au.com.illyrian.classmaker.test.TestFieldAccess;
import au.com.illyrian.classmaker.test.TestableImpl;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;

public class MakerFieldAccessTest extends ClassMakerTestCase implements ByteCode
{
    ClassMakerFactory factory;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
    }


    // Generate default constructor
    public void defaultConstructor(ClassMaker maker)
    {
        maker.Method("<init>", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    private void failSetObjectAccess(ClassMaker maker, ClassType classType, String fieldName, String message)
    {
        try {
            maker.Set(classType, fieldName, PrimitiveType.INT_TYPE);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failGetObjectAccess(ClassMaker maker, ClassType classType, String fieldName, String message)
    {
        try {
            maker.Get(classType, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failIncObjectAccess(ClassMaker maker, ClassType classType, String fieldName, String message)
    {
        try {
            maker.Inc(classType, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failDecObjectAccess(ClassMaker maker, ClassType classType, String fieldName, String message)
    {
        try {
            maker.Dec(classType, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failPostIncObjectAccess(ClassMaker maker, ClassType classType, String fieldName, String message)
    {
        try {
            maker.PostInc(classType, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failPostDecObjectAccess(ClassMaker maker, ClassType classType, String fieldName, String message)
    {
        try {
            maker.PostDec(classType, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    static class TestFieldAccessMaker extends ClassMakerBase
    {
        public TestFieldAccessMaker(ClassMakerFactory factory, String className)
        {
            this(factory, className, ACC_PUBLIC);
        }

        public TestFieldAccessMaker(ClassMakerFactory factory, String className, int modifiers)
        {
            super(factory);
            setFullyQualifiedClassName(className);
            setClassModifiers(modifiers);
        }

        public void code()
        {
            Implements(Accessable.class);
            Implements(StaticAccessable.class);

            Declare("publicInt", int.class, ACC_PUBLIC);
            Declare("protectedInt", int.class, ACC_PROTECTED);
            Declare("packageInt", int.class, 0);
            Declare("privateInt", int.class, ACC_PRIVATE);

            Method("getPublicInt", int.class, ACC_PUBLIC);
            Begin();
            Return(Get(This(), "publicInt"));
            End();

            Method("getProtectedInt", int.class, ACC_PUBLIC);
            Begin();
            Return(Get(This(), "protectedInt"));
            End();

            Method("getPackageInt", int.class, ACC_PUBLIC);
            Begin();
            Return(Get(This(), "packageInt"));
            End();

            Method("getPrivateInt", int.class, ACC_PUBLIC);
            Begin();
            Return(Get(This(), "privateInt"));
            End();

            // Static fields
            Declare("publicStatic", int.class, ACC_PUBLIC | ACC_STATIC);
            Declare("protectedStatic", int.class, ACC_PROTECTED | ACC_STATIC);
            Declare("packageStatic", int.class,  ACC_STATIC);
            Declare("privateStatic", int.class, ACC_PRIVATE | ACC_STATIC);

            Method("getPublicStatic", int.class, ACC_PUBLIC);
            Begin();
            Return(Get(getFullyQualifiedClassName(), "publicStatic"));
            End();

            Method("getProtectedStatic", int.class, ACC_PUBLIC);
            Begin();
            Return(Get(getFullyQualifiedClassName(), "protectedStatic"));
            End();

            Method("getPackageStatic", int.class, ACC_PUBLIC);
            Begin();
            Return(Get(getFullyQualifiedClassName(), "packageStatic"));
            End();

            Method("getPrivateStatic", int.class, ACC_PUBLIC);
            Begin();
            Return(Get(getFullyQualifiedClassName(), "privateStatic"));
            End();

            Method("clear", void.class, ACC_PUBLIC);
            Begin();
                Set(getFullyQualifiedClassName(), "publicStatic", Literal(0));
                Set(getFullyQualifiedClassName(), "protectedStatic", Literal(0));
                Set(getFullyQualifiedClassName(), "packageStatic", Literal(0));
                Set(getFullyQualifiedClassName(), "privateStatic", Literal(0));
                Return();
            End();
        }
    }

    public interface Accessable
    {
        public int getPublicInt();
        public int getProtectedInt();
        public int getPackageInt();
        public int getPrivateInt();
    }

    public interface StaticAccessable
    {
        public int getPublicStatic();
        public int getProtectedStatic();
        public int getPackageStatic();
        public int getPrivateStatic();
        public void clear();
    }

    public interface Testable
    {
        public void exec(TestFieldAccess test);
    }

    public void testFieldAccessUnrelated() throws Exception
    {
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        ClassType classType = maker.classToClassType(TestFieldAccess.class);
        maker.Implements(Testable.class);
        maker.Method("exec", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Declare("test", TestFieldAccess.class, 0);
        maker.Begin();
        {
            // Instance fields
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Literal(2)));
            maker.Eval(maker.Get(maker.Get("test"), "publicInt"));
            failSetObjectAccess(maker, classType, "protectedInt",
                            "Access Denied: field " + classType.getName() + "." + "protectedInt is not visible");
            failGetObjectAccess(maker, classType, "protectedInt",
                            "Access Denied: field " + classType.getName() + "." + "protectedInt is not visible");
            failSetObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failGetObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failSetObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failGetObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "publicInt"));
            failIncObjectAccess(maker, classType, "protectedInt",
                            "Access Denied: field " + classType.getName() + "." + "protectedInt is not visible");
            failDecObjectAccess(maker, classType, "protectedInt",
                            "Access Denied: field " + classType.getName() + "." + "protectedInt is not visible");
            failPostIncObjectAccess(maker, classType, "protectedInt",
                            "Access Denied: field " + classType.getName() + "." + "protectedInt is not visible");
            failPostDecObjectAccess(maker, classType, "protectedInt",
                            "Access Denied: field " + classType.getName() + "." + "protectedInt is not visible");
            failIncObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failDecObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failPostIncObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failPostDecObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failIncObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failDecObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failPostIncObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failPostDecObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Testable exec = (Testable)myClass.newInstance();

        TestFieldAccess values = new TestFieldAccess();
        exec.exec(values);
        assertEquals("publicInt", 2, values.getPublicInt());
        assertEquals("protectedInt", 0, values.getProtectedInt());
        assertEquals("packageInt", 0, values.getPackageInt());
        assertEquals("privateInt", 0, values.getPrivateInt());
    }

    public void testFieldAccessDerived() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        ClassMaker baseMaker = new TestFieldAccessMaker(factory, "other.TestFieldAccess");
        Class otherClass = baseMaker.defineClass();

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        ClassType classType = maker.getClassType();
        maker.Implements(Runnable.class);

        maker.Declare("testDerived", classType, ACC_PUBLIC);

        defaultConstructor(maker);

        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("i", int.class, 0);
            // Access fields in same object
            maker.Eval(maker.Set(maker.This(), "publicInt", maker.Literal(12)));
            maker.Eval(maker.Set("i", maker.Get(maker.This(), "publicInt")));
            maker.Eval(maker.Set(maker.This(), "protectedInt", maker.Literal(13)));
            maker.Eval(maker.Set("i", maker.Get(maker.This(), "protectedInt")));
            failSetObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field test.MyClass.packageInt is not visible");
            failGetObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field test.MyClass.packageInt is not visible");
            failSetObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field test.MyClass.privateInt is not visible");
            failGetObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field test.MyClass.privateInt is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(maker.This(), "publicInt"));
            maker.Eval(maker.Dec(maker.This(), "publicInt"));
            maker.Eval(maker.PostInc(maker.This(), "publicInt"));
            maker.Eval(maker.PostDec(maker.This(), "publicInt"));
            maker.Eval(maker.Inc(maker.This(), "protectedInt"));
            maker.Eval(maker.Dec(maker.This(), "protectedInt"));
            maker.Eval(maker.PostInc(maker.This(), "protectedInt"));
            maker.Eval(maker.PostDec(maker.This(), "protectedInt"));
            failIncObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failDecObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failPostIncObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failPostDecObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failIncObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failDecObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failPostIncObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failPostDecObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");

            // Access fields in other object
            maker.Declare("test", classType, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testDerived")));
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Literal(2)));
            maker.Eval(maker.Get(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Set(maker.Get("test"), "protectedInt", maker.Literal(3)));
            maker.Eval(maker.Get(maker.Get("test"), "protectedInt"));
            failSetObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failGetObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failSetObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failGetObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Inc(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "protectedInt"));
            failIncObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failDecObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failPostIncObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failPostDecObjectAccess(maker, classType, "packageInt",
                            "Access Denied: field " + classType.getName() + "." + "packageInt is not visible");
            failIncObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failDecObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failPostIncObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            failPostDecObjectAccess(maker, classType, "privateInt",
                            "Access Denied: field " + classType.getName() + "." + "privateInt is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        File classesDir = new File("build/classes");
        maker.saveClass(classesDir);
        Runnable exec = (Runnable)myClass.newInstance();
        Accessable same = (Accessable)exec;


        Accessable other = (Accessable)myClass.newInstance();
        setField(myClass, exec, "testDerived", other);
        exec.run();
        assertEquals("publicInt", 12, same.getPublicInt());
        assertEquals("protectedInt", 13, same.getProtectedInt());
        assertEquals("packageInt", 0, same.getPackageInt());
        assertEquals("privateInt", 0, same.getPrivateInt());
        assertEquals("publicInt", 2, other.getPublicInt());
        assertEquals("protectedInt", 3, other.getProtectedInt());
        assertEquals("packageInt", 0, other.getPackageInt());
        assertEquals("privateInt", 0, other.getPrivateInt());
    }

    public void testFieldAccessProtectedRestriction() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        ClassMaker baseMaker = new TestFieldAccessMaker(factory, "other.TestFieldAccess");
        Class otherClass = baseMaker.defineClass();
        ClassType otherType = baseMaker.classToClassType(otherClass);

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        maker.Declare("testBase", otherClass, ACC_PUBLIC);

        defaultConstructor(maker);

        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("i", int.class, 0);
            maker.Declare("test", otherType, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testBase")));
            // Access fields in other object
            maker.Eval(maker.Set(maker.Get(maker.This(), "testBase"), "publicInt", maker.Literal(2)));
            maker.Eval(maker.Get(maker.Get(maker.This(), "testBase"), "publicInt"));
            failSetObjectAccess(maker, otherType, "protectedInt",
                            "Access Denied: field " + otherClass.getName() + "." + "protectedInt is protected so the type of the class being accessed must be the same or a subclass of the current class");
            failGetObjectAccess(maker, otherType, "protectedInt",
                            "Access Denied: field " + otherClass.getName() + "." + "protectedInt is protected so the type of the class being accessed must be the same or a subclass of the current class");
            failSetObjectAccess(maker, otherType, "packageInt",
                            "Access Denied: field " + otherClass.getName() + "." + "packageInt is not visible");
            failGetObjectAccess(maker, otherType, "packageInt",
                            "Access Denied: field " + otherClass.getName() + "." + "packageInt is not visible");
            failSetObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherClass.getName() + "." + "privateInt is not visible");
            failGetObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherClass.getName() + "." + "privateInt is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "publicInt"));
            failIncObjectAccess(maker, otherType, "protectedInt",
                            "Access Denied: field " + otherClass.getName() + "." + "protectedInt is protected so the type of the class being accessed must be the same or a subclass of the current class");
            failDecObjectAccess(maker, otherType, "protectedInt",
                            "Access Denied: field " + otherClass.getName() + "." + "protectedInt is protected so the type of the class being accessed must be the same or a subclass of the current class");
            failPostIncObjectAccess(maker, otherType, "protectedInt",
                            "Access Denied: field " + otherClass.getName() + "." + "protectedInt is protected so the type of the class being accessed must be the same or a subclass of the current class");
            failPostDecObjectAccess(maker, otherType, "protectedInt",
                            "Access Denied: field " + otherClass.getName() + "." + "protectedInt is protected so the type of the class being accessed must be the same or a subclass of the current class");
            failIncObjectAccess(maker, otherType, "packageInt",
                            "Access Denied: field " + otherType.getName() + "." + "packageInt is not visible");
            failDecObjectAccess(maker, otherType, "packageInt",
                            "Access Denied: field " + otherType.getName() + "." + "packageInt is not visible");
            failPostIncObjectAccess(maker, otherType, "packageInt",
                            "Access Denied: field " + otherType.getName() + "." + "packageInt is not visible");
            failPostDecObjectAccess(maker, otherType, "packageInt",
                            "Access Denied: field " + otherType.getName() + "." + "packageInt is not visible");
            failIncObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherType.getName() + "." + "privateInt is not visible");
            failDecObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherType.getName() + "." + "privateInt is not visible");
            failPostIncObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherType.getName() + "." + "privateInt is not visible");
            failPostDecObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherType.getName() + "." + "privateInt is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        File classesDir = new File("build/classes");
        maker.saveClass(classesDir);
        Runnable exec = (Runnable)myClass.newInstance();

        Accessable other = (Accessable)myClass.newInstance();
        setField(myClass, exec, "testBase", other);
        exec.run();
        assertEquals("publicInt", 2, other.getPublicInt());
        assertEquals("protectedInt", 0, other.getProtectedInt());
        assertEquals("packageInt", 0, other.getPackageInt());
        assertEquals("privateInt", 0, other.getPrivateInt());
    }

    public void testJavaFieldAccessSamePackage() throws Exception
    {
        Class testableImpl = TestableImpl.class;
        Testable exec = (Testable)testableImpl.newInstance();
        TestFieldAccess values = new TestFieldAccess();
        exec.exec(values);
        assertEquals("publicInt", 2, values.getPublicInt());
        assertEquals("protectedInt", 3, values.getProtectedInt());
        assertEquals("packageInt", 4, values.getPackageInt());
        assertEquals("privateInt", 0, values.getPrivateInt());
        assertEquals("Packages", TestFieldAccess.class.getPackage(), testableImpl.getPackage());
    }

    public static class DerivedImpl extends TestFieldAccess implements Runnable
    {
        public TestFieldAccess testBase;
        public DerivedImpl     testDerived;
        public void run()
        {
            @SuppressWarnings("unused")
            int i;
            publicInt =12;
            i = publicInt;
            protectedInt = 13;
            i = protectedInt;
//            packageInt = 14;
//            i = packageInt;
//            privateInt = 15;
            testBase.publicInt = 2;
            i = testBase.publicInt;
//            test.protectedInt = 3;
//            i = test.protectedInt;
//            test.packageInt = 4;
//            i = test.packageInt;
//            test.privateInt = 5;
            testDerived.publicInt = 22;
            i = testDerived.publicInt;
            testDerived.protectedInt = 23;
            i = testDerived.protectedInt;
            i++;
        }
    }

    public void testJavaFieldAccessDerived() throws Exception
    {
        Class derivedImpl = DerivedImpl.class;
        DerivedImpl self = (DerivedImpl)derivedImpl.newInstance();
        TestFieldAccess base = new TestFieldAccess();
        DerivedImpl derived = new DerivedImpl();
        self.testBase = base;
        self.testDerived = derived;
        self.run();
        assertEquals("publicInt", 12, self.getPublicInt());
        assertEquals("protectedInt", 13, self.getProtectedInt());
        assertEquals("packageInt", 0, self.getPackageInt());
        assertEquals("privateInt", 0, self.getPrivateInt());
        assertEquals("publicInt", 2, base.getPublicInt());
        assertEquals("protectedInt", 0, base.getProtectedInt());
        assertEquals("packageInt", 0, base.getPackageInt());
        assertEquals("privateInt", 0, base.getPrivateInt());
        assertEquals("publicInt", 22, derived.getPublicInt());
        assertEquals("protectedInt", 23, derived.getProtectedInt());
        assertEquals("packageInt", 0, derived.getPackageInt());
        assertEquals("privateInt", 0, derived.getPrivateInt());
    }

    public void testFieldAccessSamePackage() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        ClassMaker baseMaker = new TestFieldAccessMaker(factory, "test.TestFieldAccess");
        Class otherClass = baseMaker.defineClass();
        ClassType otherType = baseMaker.classToClassType(otherClass);

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        maker.Declare("testOther", otherClass, ACC_PUBLIC);

        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            maker.Declare("test", otherType, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testOther")));
            // Access fields in other object
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Literal(2)));
            maker.Eval(maker.Get(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Set(maker.Get("test"), "protectedInt", maker.Literal(3)));
            maker.Eval(maker.Get(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.Set(maker.Get("test"), "packageInt", maker.Literal(4)));
            maker.Eval(maker.Get(maker.Get("test"), "packageInt"));
            failSetObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherClass.getName() + "." + "privateInt is not visible");
            failGetObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherClass.getName() + "." + "privateInt is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Inc(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.Inc(maker.Get("test"), "packageInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "packageInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "packageInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "packageInt"));
            failIncObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherType.getName() + "." + "privateInt is not visible");
            failDecObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherType.getName() + "." + "privateInt is not visible");
            failPostIncObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherType.getName() + "." + "privateInt is not visible");
            failPostDecObjectAccess(maker, otherType, "privateInt",
                            "Access Denied: field " + otherType.getName() + "." + "privateInt is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        Accessable other = (Accessable)otherClass.newInstance();
        setField(myClass, exec, "testOther", other);
        exec.run();
        assertEquals("publicInt", 2, other.getPublicInt());
        assertEquals("protectedInt", 3, other.getProtectedInt());
        assertEquals("packageInt", 4, other.getPackageInt());
        assertEquals("privateInt", 0, other.getPrivateInt());
    }

    public void testFieldAccessSameClass() throws Exception
    {
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        ClassType classType = maker.getClassType();
        maker.Implements(Runnable.class);
        maker.Implements(Accessable.class);

        maker.Declare("other", maker.getClassType(), ACC_PUBLIC);
        maker.Declare("publicInt", int.class, ACC_PUBLIC);
        maker.Declare("protectedInt", int.class, ACC_PROTECTED);
        maker.Declare("packageInt", int.class, ClassMaker.ACC_PACKAGE);
        maker.Declare("privateInt", int.class, ACC_PRIVATE);

        maker.Method("getPublicInt", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Get(maker.This(), "publicInt"));
        maker.End();

        maker.Method("getProtectedInt", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Get(maker.This(), "protectedInt"));
        maker.End();

        maker.Method("getPackageInt", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Get(maker.This(), "packageInt"));
        maker.End();

        maker.Method("getPrivateInt", int.class, ACC_PUBLIC);
        maker.Begin();
        maker.Return(maker.Get(maker.This(), "privateInt"));
        maker.End();

        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Instance fields
            maker.Eval(maker.Set(maker.This(), "publicInt", maker.Literal(12)));
            maker.Eval(maker.Get(maker.This(), "publicInt"));
            maker.Eval(maker.Set(maker.This(), "protectedInt", maker.Literal(13)));
            maker.Eval(maker.Get(maker.This(), "protectedInt"));
            maker.Eval(maker.Set(maker.This(), "packageInt", maker.Literal(14)));
            maker.Eval(maker.Get(maker.This(), "packageInt"));
            maker.Eval(maker.Set(maker.This(), "privateInt", maker.Literal(15)));
            maker.Eval(maker.Get(maker.This(), "privateInt"));
            // Increment & Decrement
            maker.Eval(maker.Inc(maker.This(), "publicInt"));
            maker.Eval(maker.Dec(maker.This(), "publicInt"));
            maker.Eval(maker.PostInc(maker.This(), "publicInt"));
            maker.Eval(maker.PostDec(maker.This(), "publicInt"));
            maker.Eval(maker.Inc(maker.This(), "protectedInt"));
            maker.Eval(maker.Dec(maker.This(), "protectedInt"));
            maker.Eval(maker.PostInc(maker.This(), "protectedInt"));
            maker.Eval(maker.PostDec(maker.This(), "protectedInt"));
            maker.Eval(maker.Inc(maker.This(), "packageInt"));
            maker.Eval(maker.Dec(maker.This(), "packageInt"));
            maker.Eval(maker.PostInc(maker.This(), "packageInt"));
            maker.Eval(maker.PostDec(maker.This(), "packageInt"));
            maker.Eval(maker.Inc(maker.This(), "privateInt"));
            maker.Eval(maker.Dec(maker.This(), "privateInt"));
            maker.Eval(maker.PostInc(maker.This(), "privateInt"));
            maker.Eval(maker.PostDec(maker.This(), "privateInt"));
            // Other fields
            maker.Declare("test", classType, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "other")));
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Literal(2)));
            maker.Eval(maker.Get(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Set(maker.Get("test"), "protectedInt", maker.Literal(3)));
            maker.Eval(maker.Get(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.Set(maker.Get("test"), "packageInt", maker.Literal(4)));
            maker.Eval(maker.Get(maker.Get("test"), "packageInt"));
            maker.Eval(maker.Set(maker.Get("test"), "privateInt", maker.Literal(5)));
            maker.Eval(maker.Get(maker.Get("test"), "privateInt"));
            // Increment & Decrement
            maker.Eval(maker.Inc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "publicInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "publicInt"));
            maker.Eval(maker.Inc(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "protectedInt"));
            maker.Eval(maker.Inc(maker.Get("test"), "packageInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "packageInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "packageInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "packageInt"));
            maker.Eval(maker.Inc(maker.Get("test"), "privateInt"));
            maker.Eval(maker.Dec(maker.Get("test"), "privateInt"));
            maker.Eval(maker.PostInc(maker.Get("test"), "privateInt"));
            maker.Eval(maker.PostDec(maker.Get("test"), "privateInt"));
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();
        Accessable self = (Accessable)exec;

        Accessable other = (Accessable)myClass.newInstance();
        setField(myClass, exec, "other", other);
        exec.run();
        assertEquals("other.publicInt", 2, other.getPublicInt());
        assertEquals("other.protectedInt", 3, other.getProtectedInt());
        assertEquals("other.packageInt", 4, other.getPackageInt());
        assertEquals("other.privateInt", 5, other.getPrivateInt());
        assertEquals("this.publicInt", 12, self.getPublicInt());
        assertEquals("this.protectedInt", 13, self.getProtectedInt());
        assertEquals("this.packageInt", 14, self.getPackageInt());
        assertEquals("this.privateInt", 15, self.getPrivateInt());
    }

    private void failSetStaticAccess(ClassMaker maker, String className, String fieldName, String message)
    {
        try {
            maker.Set(className, fieldName, PrimitiveType.INT_TYPE);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failAssignStaticAccess(ClassMaker maker, String className, String fieldName, String message)
    {
        try {
            maker.Assign(className, fieldName, maker.Literal(1));
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failGetStaticAccess(ClassMaker maker, String className, String fieldName, String message)
    {
        try {
            maker.Get(className, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failIncStaticAccess(ClassMaker maker, String className, String fieldName, String message)
    {
        try {
            maker.Inc(className, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failDecStaticAccess(ClassMaker maker, String className, String fieldName, String message)
    {
        try {
            maker.Dec(className, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failPostIncStaticAccess(ClassMaker maker, String className, String fieldName, String message)
    {
        try {
            maker.PostInc(className, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private void failPostDecStaticAccess(ClassMaker maker, String className, String fieldName, String message)
    {
        try {
            maker.PostDec(className, fieldName);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    public void testStaticFieldAccessUnrelated() throws Exception
    {
        String className = TestFieldAccess.class.getName();
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Implements(Runnable.class);
        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Static fields
            maker.Eval(maker.Set(className, "publicStatic", maker.Literal(2)));
            maker.Eval(maker.Get(className, "publicStatic"));
            failSetStaticAccess(maker, className, "protectedStatic",
                            "Access Denied: field " + className + "." + "protectedStatic is not visible");
            failGetStaticAccess(maker,  className, "protectedStatic",
                            "Access Denied: field " + className + "." + "protectedStatic is not visible");
            failSetStaticAccess(maker,  className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failGetStaticAccess(maker,  className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failSetStaticAccess(maker,  className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failGetStaticAccess(maker,  className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(className, "publicStatic"));
            maker.Eval(maker.Dec(className, "publicStatic"));
            maker.Eval(maker.PostInc(className, "publicStatic"));
            maker.Eval(maker.PostDec(className, "publicStatic"));
            failIncStaticAccess(maker, className, "protectedStatic",
                            "Access Denied: field " + className + "." + "protectedStatic is not visible");
            failDecStaticAccess(maker, className, "protectedStatic",
                            "Access Denied: field " + className + "." + "protectedStatic is not visible");
            failPostIncStaticAccess(maker, className, "protectedStatic",
                            "Access Denied: field " + className + "." + "protectedStatic is not visible");
            failPostDecStaticAccess(maker, className, "protectedStatic",
                            "Access Denied: field " + className + "." + "protectedStatic is not visible");
            failIncStaticAccess(maker, className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failDecStaticAccess(maker, className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failPostIncStaticAccess(maker, className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failPostDecStaticAccess(maker, className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failIncStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failDecStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failPostIncStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failPostDecStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        TestFieldAccess values = new TestFieldAccess();
        values.clear();
        exec.run();
        assertEquals("publicInt", 2, values.getPublicStatic());
        assertEquals("protectedInt", 0, values.getProtectedStatic());
        assertEquals("packageInt", 0, values.getPackageStatic());
        assertEquals("privateInt", 0, values.getPrivateStatic());
    }

    public void testStaticFieldAccessDerived() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String baseName = "other.TestFieldAccess";
        ClassMaker baseMaker = new TestFieldAccessMaker(factory, baseName);
        Class otherClass = baseMaker.defineClass();

        String className = "test.MyClass";
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        maker.Declare("test", otherClass, ACC_PUBLIC);

        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Static fields
            maker.Eval(maker.Set(className, "publicStatic", maker.Literal(2)));
            maker.Eval(maker.Get(className, "publicStatic"));
            maker.Eval(maker.Set(className, "protectedStatic", maker.Literal(3)));
            maker.Eval(maker.Get(className, "protectedStatic"));
            failSetStaticAccess(maker,  className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failGetStaticAccess(maker,  className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failSetStaticAccess(maker,  className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failGetStaticAccess(maker,  className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(className, "publicStatic"));
            maker.Eval(maker.Dec(className, "publicStatic"));
            maker.Eval(maker.PostInc(className, "publicStatic"));
            maker.Eval(maker.PostDec(className, "publicStatic"));
          maker.Eval(maker.Inc(className, "protectedStatic"));
          maker.Eval(maker.Dec(className, "protectedStatic"));
          maker.Eval(maker.PostInc(className, "protectedStatic"));
          maker.Eval(maker.PostDec(className, "protectedStatic"));
//          maker.Eval(maker.Inc(className, "packageStatic"));
//          maker.Eval(maker.Dec(className, "packageStatic"));
//          maker.Eval(maker.PostInc(className, "packageStatic"));
//          maker.Eval(maker.PostDec(className, "packageStatic"));
//          failIncStaticAccess(maker, className, "protectedStatic",
//                          "Access Denied: field " + className + "." + "protectedStatic is not visible");
//          failDecStaticAccess(maker, className, "protectedStatic",
//                          "Access Denied: field " + className + "." + "protectedStatic is not visible");
//          failPostIncStaticAccess(maker, className, "protectedStatic",
//                          "Access Denied: field " + className + "." + "protectedStatic is not visible");
//          failPostDecStaticAccess(maker, className, "protectedStatic",
//                          "Access Denied: field " + className + "." + "protectedStatic is not visible");
          failIncStaticAccess(maker, className, "packageStatic",
                          "Access Denied: field " + className + "." + "packageStatic is not visible");
          failDecStaticAccess(maker, className, "packageStatic",
                          "Access Denied: field " + className + "." + "packageStatic is not visible");
          failPostIncStaticAccess(maker, className, "packageStatic",
                          "Access Denied: field " + className + "." + "packageStatic is not visible");
          failPostDecStaticAccess(maker, className, "packageStatic",
                          "Access Denied: field " + className + "." + "packageStatic is not visible");
            failIncStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failDecStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failPostIncStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failPostDecStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        StaticAccessable values = (StaticAccessable)myClass.newInstance();
        values.clear();
        exec.run();
        assertEquals("publicStatic", 2, values.getPublicStatic());
        assertEquals("protectedStatic", 3, values.getProtectedStatic());
        assertEquals("packageStatic", 0, values.getPackageStatic());
        assertEquals("privateStatic", 0, values.getPrivateStatic());
    }


    public void testStaticFieldAccessBase() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String baseName = "other.TestFieldAccess";
        ClassMaker baseMaker = new TestFieldAccessMaker(factory, baseName);
        Class otherClass = baseMaker.defineClass();

        String className = "test.MyClass";
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        maker.Declare("test", otherClass, ACC_PUBLIC);

        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Static fields of base class
            maker.Eval(maker.Set(baseName, "publicStatic", maker.Literal(2)));
            maker.Eval(maker.Get(baseName, "publicStatic"));
            maker.Eval(maker.Set(baseName, "protectedStatic", maker.Literal(3)));
            maker.Eval(maker.Get(baseName, "protectedStatic"));
            failSetStaticAccess(maker,  baseName, "packageStatic",
                            "Access Denied: field " + baseName + "." + "packageStatic is not visible");
            failGetStaticAccess(maker,  baseName, "packageStatic",
                            "Access Denied: field " + baseName + "." + "packageStatic is not visible");
            failAssignStaticAccess(maker,  baseName, "packageStatic",
                    "Access Denied: field " + baseName + "." + "packageStatic is not visible");
            failSetStaticAccess(maker,  baseName, "privateStatic",
                            "Access Denied: field " + baseName + "." + "privateStatic is not visible");
            failGetStaticAccess(maker,  baseName, "privateStatic",
                            "Access Denied: field " + baseName + "." + "privateStatic is not visible");
            failAssignStaticAccess(maker,  baseName, "privateStatic",
                    "Access Denied: field " + baseName + "." + "privateStatic is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(className, "publicStatic"));
            maker.Eval(maker.Dec(className, "publicStatic"));
            maker.Eval(maker.PostInc(className, "publicStatic"));
            maker.Eval(maker.PostDec(className, "publicStatic"));
            maker.Eval(maker.Inc(className, "protectedStatic"));
            maker.Eval(maker.Dec(className, "protectedStatic"));
            maker.Eval(maker.PostInc(className, "protectedStatic"));
            maker.Eval(maker.PostDec(className, "protectedStatic"));
            failIncStaticAccess(maker, className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failDecStaticAccess(maker, className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failPostIncStaticAccess(maker, className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failPostDecStaticAccess(maker, className, "packageStatic",
                            "Access Denied: field " + className + "." + "packageStatic is not visible");
            failIncStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failDecStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failPostIncStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            failPostDecStaticAccess(maker, className, "privateStatic",
                            "Access Denied: field " + className + "." + "privateStatic is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        StaticAccessable values = (StaticAccessable)otherClass.newInstance();
        values.clear();
        exec.run();
        assertEquals("publicStatic", 2, values.getPublicStatic());
        assertEquals("protectedStatic", 3, values.getProtectedStatic());
        assertEquals("packageStatic", 0, values.getPackageStatic());
        assertEquals("privateStatic", 0, values.getPrivateStatic());
    }

    public void testStaticFieldSamePackage() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String otherName = "test.TestFieldAccess";
        ClassMaker baseMaker = new TestFieldAccessMaker(factory, otherName);
        Class otherClass = baseMaker.defineClass();

        String className = "test.MyClass";
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Implements(Runnable.class);

        maker.Declare("test", otherClass, ACC_PUBLIC);

        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Static fields
            maker.Eval(maker.Set(otherName, "publicStatic", maker.Literal(2)));
            maker.Eval(maker.Get(otherName, "publicStatic"));
            maker.Eval(maker.Set(otherName, "protectedStatic", maker.Literal(3)));
            maker.Eval(maker.Get(otherName, "protectedStatic"));
            maker.Eval(maker.Set(otherName, "packageStatic", maker.Literal(4)));
            maker.Eval(maker.Get(otherName, "packageStatic"));
            failSetStaticAccess(maker,  otherName, "privateStatic",
                            "Access Denied: field " + otherName + "." + "privateStatic is not visible");
            failGetStaticAccess(maker,  otherName, "privateStatic",
                            "Access Denied: field " + otherName + "." + "privateStatic is not visible");
            // Increment & Decrement
            maker.Eval(maker.Inc(otherName, "publicStatic"));
            maker.Eval(maker.Dec(otherName, "publicStatic"));
            maker.Eval(maker.PostInc(otherName, "publicStatic"));
            maker.Eval(maker.PostDec(otherName, "publicStatic"));
            maker.Eval(maker.Inc(otherName, "protectedStatic"));
            maker.Eval(maker.Dec(otherName, "protectedStatic"));
            maker.Eval(maker.PostInc(otherName, "protectedStatic"));
            maker.Eval(maker.PostDec(otherName, "protectedStatic"));
            maker.Eval(maker.Inc(otherName, "packageStatic"));
            maker.Eval(maker.Dec(otherName, "packageStatic"));
            maker.Eval(maker.PostInc(otherName, "packageStatic"));
            maker.Eval(maker.PostDec(otherName, "packageStatic"));
            failIncStaticAccess(maker, otherName, "privateStatic",
                            "Access Denied: field " + otherName + "." + "privateStatic is not visible");
            failDecStaticAccess(maker, otherName, "privateStatic",
                            "Access Denied: field " + otherName + "." + "privateStatic is not visible");
            failPostIncStaticAccess(maker, otherName, "privateStatic",
                            "Access Denied: field " + otherName + "." + "privateStatic is not visible");
            failPostDecStaticAccess(maker, otherName, "privateStatic",
                            "Access Denied: field " + otherName + "." + "privateStatic is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        StaticAccessable values = (StaticAccessable)otherClass.newInstance();
        values.clear();
        exec.run();
        assertEquals("publicStatic", 2, values.getPublicStatic());
        assertEquals("protectedStatic", 3, values.getProtectedStatic());
        assertEquals("packageStatic", 4, values.getPackageStatic());
        assertEquals("privateStatic", 0, values.getPrivateStatic());
    }

    public static class RunnableFieldAccessMaker extends TestFieldAccessMaker
    {
        public RunnableFieldAccessMaker(ClassMakerFactory factory, String className)
        {
            super(factory, className);
        }

        public void code()
        {
            Implements(Runnable.class);
            super.code();

            Method("run", void.class, ACC_PUBLIC);
            Begin();
                Set(getFullyQualifiedClassName(), "publicStatic", Literal(31));
                Set(getFullyQualifiedClassName(), "protectedStatic", Literal(32));
                Set(getFullyQualifiedClassName(), "packageStatic", Literal(33));
                Set(getFullyQualifiedClassName(), "privateStatic", Literal(34));
                Return();
            End();
        }
    }

    public void testStaticFieldSameClass() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String className = "test.RunnableFieldAccess";
        ClassMaker maker = new RunnableFieldAccessMaker(factory, className);
        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();
        StaticAccessable values = (StaticAccessable)exec;
        values.clear();
        exec.run();
        assertEquals("publicStatic", 31, values.getPublicStatic());
        assertEquals("protectedStatic", 32, values.getProtectedStatic());
        assertEquals("packageStatic", 33, values.getPackageStatic());
        assertEquals("privateStatic", 34, values.getPrivateStatic());
    }

    public void testFieldAccessProtectedClass() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String className = "other.TestMethodAccess";
        ClassMaker baseMaker = new TestFieldAccessMaker(factory, className, 0);
        Class otherClass = baseMaker.defineClass();
        ClassType classType = baseMaker.classToClassType(otherClass);

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        maker.Declare("testBase", otherClass, ACC_PUBLIC);

        maker.Method("run", PrimitiveType.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access fields in other object
            maker.Declare("test", otherClass, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testBase")));
            failGetObjectAccess(maker, classType, "dummyInt",
                "Cannot find member field \'dummyInt\' in class " + className);
            failGetObjectAccess(maker, classType, "publicInt",
                       "Access Denied: class other.TestMethodAccess is not visible");
            failGetObjectAccess(maker, classType, "protectedInt",
                       "Access Denied: class other.TestMethodAccess is not visible");
            failGetObjectAccess(maker, classType, "packageInt",
                       "Access Denied: class other.TestMethodAccess is not visible");
            failGetObjectAccess(maker, classType, "privateInt",
                       "Access Denied: class other.TestMethodAccess is not visible");
            // Test access to static method.
            failGetStaticAccess(maker, className, "dummyStatic",
                "Cannot find member field \'dummyStatic\' in class " + className);
            failGetStaticAccess(maker, className, "publicStatic",
                "Access Denied: class other.TestMethodAccess is not visible");
            failGetStaticAccess(maker, className, "protectedStatic",
                "Access Denied: class other.TestMethodAccess is not visible");
            failGetStaticAccess(maker, className, "packageStatic",
                "Access Denied: class other.TestMethodAccess is not visible");
            failGetStaticAccess(maker, className, "privateStatic",
                "Access Denied: class other.TestMethodAccess is not visible");
            failGetStaticAccess(maker, className, "publicInt",
                "Access Denied: class other.TestMethodAccess is not visible");
            // Increment & Decrement
            failIncStaticAccess(maker, className, "publicStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failDecStaticAccess(maker, className, "publicStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failPostIncStaticAccess(maker, className, "publicStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failPostDecStaticAccess(maker, className, "publicStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failIncStaticAccess(maker, className, "protectedStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failDecStaticAccess(maker, className, "protectedStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failPostIncStaticAccess(maker, className, "protectedStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failPostDecStaticAccess(maker, className, "protectedStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failIncStaticAccess(maker, className, "packageStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failDecStaticAccess(maker, className, "packageStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failPostIncStaticAccess(maker, className, "packageStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failPostDecStaticAccess(maker, className, "packageStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failIncStaticAccess(maker, className, "privateStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failDecStaticAccess(maker, className, "privateStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failPostIncStaticAccess(maker, className, "privateStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            failPostDecStaticAccess(maker, className, "privateStatic",
            "Access Denied: class other.TestMethodAccess is not visible");
            maker.Return();
        }
        maker.End();
     }

    public interface Unary
    {
    	int unary(int a);
    }
    
    public void testDeclarationExceptions() throws Exception
    {
    	ClassMakerFactory factory = new ClassMakerFactory();
    	ClassMaker maker = factory.createClassMaker();
    	maker.setFullyQualifiedClassName("MyClass");
    	
        maker.Declare("test", PrimitiveType.INT_TYPE, 0);
        try {
        	maker.Declare("test", char.class, 0);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Duplicate member field declaration: test", ex.getMessage());
        }

    	maker.Method("unary", void.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", PrimitiveType.INT_TYPE, 0);
        maker.Begin();
        try {
        	maker.Declare("a", int.class, 0);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Duplicate local variable or parameter declaration: a", ex.getMessage());
        }
        maker.Declare("b", PrimitiveType.INT_TYPE, 0);
        try {
        	maker.Declare("b", char.class, 0);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Duplicate local variable or parameter declaration: b", ex.getMessage());
        }
    }
}
