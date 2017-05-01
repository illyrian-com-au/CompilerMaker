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

import au.com.illyrian.classmaker.types.ClassType;

public class MakerMethodAccessTest extends ClassMakerTestCase implements ByteCode
{

    // Generate default constructor
    public void defaultConstructor(ClassMaker maker)
    {
        maker.Method("<init>", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    private void failGetMethodAccess(ClassMaker maker, ClassType classType, String methodName, String message)
    {
        try {
            maker.Call(classType, methodName, null);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    static class TestMethodAccessMaker extends ClassMakerBase
    {
        public TestMethodAccessMaker(ClassMakerFactory factory, String className)
        {
            this(factory, className, ACC_PUBLIC);
        }
        public TestMethodAccessMaker(ClassMakerFactory factory, String className, int modifiers)
        {
            super(factory);
            setFullyQualifiedClassName(className);
            setClassModifiers(modifiers);
        }

        public void code()
        {
            Declare("publicInt", int.class, ACC_PUBLIC);
            Declare("protectedInt", int.class, ACC_PUBLIC);
            Declare("packageInt", int.class, ACC_PUBLIC);
            Declare("privateInt", int.class, ACC_PUBLIC);

            Method("getPublicInt", int.class, ACC_PUBLIC);
            Begin();
                Return(Literal(12));
            End();

            Method("getProtectedInt", int.class, ACC_PROTECTED);
            Begin();
            Return(Literal(13));
            End();

            Method("getPackageInt", int.class, 0);
            Begin();
            Return(Literal(14));
            End();

            Method("getPrivateInt", int.class, ACC_PRIVATE);
            Begin();
            Return(Literal(15));
            End();

            Method("getPublicStatic", int.class, ACC_PUBLIC | ACC_STATIC);
            Begin();
                Return(Literal(21));
            End();

            Method("getProtectedStatic", int.class, ACC_PROTECTED | ACC_STATIC);
            Begin();
                Return(Literal(22));
            End();

            Method("getPackageStatic", int.class, ACC_STATIC);
            Begin();
                Return(Literal(23));
            End();

            Method("getPrivateStatic", int.class, ACC_PRIVATE | ACC_STATIC);
            Begin();
                Return(Literal(24));
            End();

        }
    }

    public void testMethodAccessUnrelated() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        ClassMaker baseMaker = new TestMethodAccessMaker(factory, "other.TestMethodAccess");
        Class otherClass = baseMaker.defineClass();
        ClassType classType = baseMaker.classToClassType(otherClass);

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Implements(Runnable.class);

        maker.Declare("testBase", otherClass, ACC_PUBLIC);

        defaultConstructor(maker);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access fields in other object
            maker.Declare("test", otherClass, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testBase")));
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Call(maker.Get("test"), "getPublicInt", null)));
            failGetMethodAccess(maker, classType, "getProtectedInt",
                       "Access Denied: method protected int getProtectedInt() in class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, classType, "getPackageInt",
                       "Access Denied: method int getPackageInt() in class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, classType, "getPrivateInt",
                       "Access Denied: method private int getPrivateInt() in class other.TestMethodAccess is not visible");
            // Test access to static method.
            maker.Eval(maker.Call(maker.Get("test"), "getPublicStatic", null));
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

//        File classesDir = new File("build/classes");
//        maker.saveClass(classesDir);
        Runnable exec = (Runnable)myClass.newInstance();

        Object other = otherClass.newInstance();
        setField(myClass, exec, "testBase", other);
        exec.run();
        assertEquals("publicInt", 12, getIntField(otherClass, other, "publicInt"));
        assertEquals("protectedInt", 0, getIntField(otherClass, other, "protectedInt"));
        assertEquals("packageInt", 0, getIntField(otherClass, other, "packageInt"));
        assertEquals("privateInt", 0, getIntField(otherClass, other, "privateInt"));
    }

    public void testMethodAccessProtectedClass() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String className = "other.TestMethodAccess";
        ClassMaker baseMaker = new TestMethodAccessMaker(factory, className, 0);
        Class otherClass = baseMaker.defineClass();
        ClassType classType = baseMaker.classToClassType(otherClass);

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Implements(Runnable.class);

        maker.Declare("testBase", otherClass, ACC_PUBLIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access fields in other object
            maker.Declare("test", otherClass, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testBase")));
            failGetMethodAccess(maker, classType, "getPublicInt",
                       "Access Denied: class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, classType, "getProtectedInt",
                       "Access Denied: class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, classType, "getPackageInt",
                       "Access Denied: class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, classType, "getPrivateInt",
                       "Access Denied: class other.TestMethodAccess is not visible");
            // Test access to static method.
            failGetMethodAccess(maker, className, "getPublicStatic",
                "Access Denied: class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, className, "getProtectedStatic",
                "Access Denied: class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, className, "getPackageStatic",
                "Access Denied: class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, className, "getPrivateStatic",
                "Access Denied: class other.TestMethodAccess is not visible");
//            failGetMethodAccess(maker, className, "getPublicInt",
//                "Access Denied: class other.TestMethodAccess is not visible");
            maker.Return();
        }
        maker.End();

     }

    public void testMethodAccessDerived() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        ClassMaker baseMaker = new TestMethodAccessMaker(factory, "other.TestMethodAccess");
        Class otherClass = baseMaker.defineClass();
        ClassType classType = baseMaker.classToClassType(otherClass);

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        maker.Declare("testDerived", maker.getClassType(), ACC_PUBLIC);

        defaultConstructor(maker);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access fields in same object
            maker.Eval(maker.Set(maker.This(), "publicInt", maker.Call(maker.This(), "getPublicInt", null)));
            maker.Eval(maker.Set(maker.This(), "protectedInt", maker.Call(maker.This(), "getProtectedInt", null)));
            failGetMethodAccess(maker, maker.getClassType(), "getPackageInt",
                            "Access Denied: method int getPackageInt() in class test.MyClass is not visible");
            failGetMethodAccess(maker, maker.getClassType(), "getPrivateInt",
                            "Access Denied: method private int getPrivateInt() in class test.MyClass is not visible");
            // Access fields in other object
            maker.Declare("test", maker.getClassType(), ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testDerived")));
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Call(maker.Get("test"), "getPublicInt", null)));
            maker.Eval(maker.Set(maker.Get("test"), "protectedInt", maker.Call(maker.Get("test"), "getProtectedInt", null)));
            failGetMethodAccess(maker, classType, "getPackageInt",
                       "Access Denied: method int getPackageInt() in class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, classType, "getPrivateInt",
                       "Access Denied: method private int getPrivateInt() in class other.TestMethodAccess is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        File classesDir = new File("build/classes");
        maker.saveClass(classesDir);
        Runnable exec = (Runnable)myClass.newInstance();

        Object other = myClass.newInstance();
        setField(myClass, exec, "testDerived", other);
        exec.run();
        assertEquals("publicInt", 12, getIntField(myClass, exec, "publicInt"));
        assertEquals("protectedInt", 13, getIntField(myClass, exec, "protectedInt"));
        assertEquals("packageInt", 0, getIntField(myClass, exec, "packageInt"));
        assertEquals("privateInt", 0, getIntField(myClass, exec, "privateInt"));
        assertEquals("publicInt", 12, getIntField(myClass, other, "publicInt"));
        assertEquals("protectedInt", 13, getIntField(myClass, other, "protectedInt"));
        assertEquals("packageInt", 0, getIntField(myClass, other, "packageInt"));
        assertEquals("privateInt", 0, getIntField(myClass, other, "privateInt"));
    }

    public void testFieldAccessProtectedRestriction() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        ClassMaker baseMaker = new TestMethodAccessMaker(factory, "other.TestMethodAccess");
        Class otherClass = baseMaker.defineClass();
        ClassType classType = baseMaker.classToClassType(otherClass);

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        maker.Declare("testBase", otherClass, ACC_PUBLIC);

        defaultConstructor(maker);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access fields in other object
            maker.Declare("test", otherClass, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testBase")));
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Call(maker.Get("test"), "getPublicInt", null)));
            //maker.Eval(maker.Set(maker.Get("test"), "protectedInt", maker.Call(maker.Get("test"), "getProtectedInt", null)));
            failGetMethodAccess(maker, classType, "getProtectedInt",
                       "Access Denied: method protected int getProtectedInt() in class other.TestMethodAccess is protected so the type of the class being accessed must be the same or a subclass of the current class");
            failGetMethodAccess(maker, classType, "getPackageInt",
                       "Access Denied: method int getPackageInt() in class other.TestMethodAccess is not visible");
            failGetMethodAccess(maker, classType, "getPrivateInt",
                       "Access Denied: method private int getPrivateInt() in class other.TestMethodAccess is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        File classesDir = new File("build/classes");
        maker.saveClass(classesDir);
        Runnable exec = (Runnable)myClass.newInstance();

        Object other = myClass.newInstance();
        setField(myClass, exec, "testBase", other);
        exec.run();
        assertEquals("publicInt", 12, getIntField(myClass, other, "publicInt"));
        assertEquals("protectedInt", 0, getIntField(myClass, other, "protectedInt"));
        assertEquals("packageInt", 0, getIntField(myClass, other, "packageInt"));
        assertEquals("privateInt", 0, getIntField(myClass, other, "privateInt"));
    }

    public void testFieldAccessSamePackage() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        ClassMaker baseMaker = new TestMethodAccessMaker(factory, "test.TestMethodAccess");
        Class otherClass = baseMaker.defineClass();
        ClassType classType = baseMaker.classToClassType(otherClass);

        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        maker.Declare("testBase", otherClass, ACC_PUBLIC);

        defaultConstructor(maker);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access fields in other object
            maker.Declare("test", otherClass, ACC_PUBLIC);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "testBase")));
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Call(maker.Get("test"), "getPublicInt", null)));
            maker.Eval(maker.Set(maker.Get("test"), "protectedInt", maker.Call(maker.Get("test"), "getProtectedInt", null)));
            maker.Eval(maker.Set(maker.Get("test"), "packageInt", maker.Call(maker.Get("test"), "getPackageInt", null)));
            failGetMethodAccess(maker, classType, "getPrivateInt",
                       "Access Denied: method private int getPrivateInt() in class test.TestMethodAccess is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        File classesDir = new File("build/classes");
        maker.saveClass(classesDir);
        Runnable exec = (Runnable)myClass.newInstance();

        Object other = myClass.newInstance();
        setField(myClass, exec, "testBase", other);
        exec.run();
        assertEquals("publicInt", 12, getIntField(myClass, other, "publicInt"));
        assertEquals("protectedInt", 13, getIntField(myClass, other, "protectedInt"));
        assertEquals("packageInt", 14, getIntField(myClass, other, "packageInt"));
        assertEquals("privateInt", 0, getIntField(myClass, other, "privateInt"));
    }

    public void testFieldAccessSameClass() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Implements(Runnable.class);

        maker.Declare("test", maker.getClassType(), ACC_PUBLIC);
        maker.Declare("publicInt", int.class, ACC_PUBLIC);
        maker.Declare("protectedInt", int.class, ACC_PUBLIC);
        maker.Declare("packageInt", int.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("privateInt", int.class, ACC_PUBLIC);

        maker.Method("getPublicInt", int.class, ACC_PUBLIC);
        maker.Begin();
            maker.Return(maker.Literal(12));
        maker.End();

        maker.Method("getProtectedInt", int.class, ACC_PROTECTED);
        maker.Begin();
            maker.Return(maker.Literal(13));
        maker.End();

        maker.Method("getPackageInt", int.class, 0);
        maker.Begin();
            maker.Return(maker.Literal(14));
        maker.End();

        maker.Method("getPrivateInt", int.class, ACC_PRIVATE);
        maker.Begin();
            maker.Return(maker.Literal(15));
        maker.End();


        defaultConstructor(maker);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access fields in self
            maker.Eval(maker.Set(maker.This(), "publicInt", maker.Call(maker.This(), "getPublicInt", null)));
            maker.Eval(maker.Set(maker.This(), "protectedInt", maker.Call(maker.This(), "getProtectedInt", null)));
            maker.Eval(maker.Set(maker.This(), "packageInt", maker.Call(maker.This(), "getPackageInt", null)));
            maker.Eval(maker.Set(maker.This(), "privateInt", maker.Call(maker.This(), "getPrivateInt", null)));
            // Access fields in other object
            maker.Declare("test", maker.getClassType(), 0);
            maker.Eval(maker.Set("test", maker.Get(maker.This(), "test")));
            maker.Eval(maker.Set(maker.Get("test"), "publicInt", maker.Call(maker.Get("test"), "getPublicInt", null)));
            maker.Eval(maker.Set(maker.Get("test"), "protectedInt", maker.Call(maker.Get("test"), "getProtectedInt", null)));
            maker.Eval(maker.Set(maker.Get("test"), "packageInt", maker.Call(maker.Get("test"), "getPackageInt", null)));
            maker.Eval(maker.Set(maker.Get("test"), "privateInt", maker.Call(maker.Get("test"), "getPrivateInt", null)));
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();
        Object self = (Object)exec;

        Object other = (Object)myClass.newInstance();
        setField(myClass, exec, "test", other);
        exec.run();
        assertEquals("publicInt", 12, getIntField(myClass, self, "publicInt"));
        assertEquals("protectedInt", 13, getIntField(myClass, self, "protectedInt"));
        assertEquals("packageInt", 14, getIntField(myClass, self, "packageInt"));
        assertEquals("privateInt", 15, getIntField(myClass, self, "privateInt"));
        assertEquals("publicInt", 12, getIntField(myClass, other, "publicInt"));
        assertEquals("protectedInt", 13, getIntField(myClass, other, "protectedInt"));
        assertEquals("packageInt", 14, getIntField(myClass, other, "packageInt"));
        assertEquals("privateInt", 15, getIntField(myClass, other, "privateInt"));
    }

    private void failGetMethodAccess(ClassMaker maker, String className, String methodName, String message)
    {
        try {
            maker.Call(className, methodName, null);
            fail("Expected ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    public void testStaticFieldAccessUnrelated() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String otherName = "other.TestMethodAccess";
        ClassMaker baseMaker = new TestMethodAccessMaker(factory, otherName);
        Class otherClass = baseMaker.defineClass();

        String className = "test.MyClass";
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Implements(Runnable.class);

        defaultConstructor(maker);

        // Static fields
        maker.Declare("publicStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("protectedStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("packageStatic", int.class,  ACC_PUBLIC | ACC_STATIC);
        maker.Declare("privateStatic", int.class, ACC_PUBLIC | ACC_STATIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access methods in other object
            maker.Eval(maker.Set(className, "publicStatic", maker.Call(otherName, "getPublicStatic", null)));
            failGetMethodAccess(maker, otherName, "getProtectedStatic",
                       "Access Denied: method protected static int getProtectedStatic() in class " + otherName + " is not visible");
            failGetMethodAccess(maker, otherName, "getPackageStatic",
                            "Access Denied: method static int getPackageStatic() in class " + otherName + " is not visible");
            failGetMethodAccess(maker, otherName, "getPrivateStatic",
                            "Access Denied: method private static int getPrivateStatic() in class " + otherName + " is not visible");
            failGetMethodAccess(maker, otherName, "getPublicInt",
                            "Static call to non static method: public int getPublicInt() in class " + otherName);
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("publicStatic", 21, getIntField(myClass, null, "publicStatic"));
        assertEquals("protectedStatic", 0, getIntField(myClass, null, "protectedStatic"));
        assertEquals("packageStatic", 0, getIntField(myClass, null, "packageStatic"));
        assertEquals("privateStatic", 0, getIntField(myClass, null, "privateStatic"));
    }

    public void testStaticFieldAccessDerived() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String otherName = "other.TestMethodAccess";
        ClassMaker baseMaker = new TestMethodAccessMaker(factory, otherName);
        Class otherClass = baseMaker.defineClass();

        String className = "test.MyClass";
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Extends(otherClass);
        maker.Implements(Runnable.class);

        defaultConstructor(maker);

        // Static fields
        maker.Declare("publicStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("protectedStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("packageStatic", int.class,  ACC_PUBLIC | ACC_STATIC);
        maker.Declare("privateStatic", int.class, ACC_PUBLIC | ACC_STATIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access methods in other object
            maker.Eval(maker.Set(className, "publicStatic", maker.Call(otherName, "getPublicStatic", null)));
            maker.Eval(maker.Set(className, "protectedStatic", maker.Call(otherName, "getProtectedStatic", null)));
            failGetMethodAccess(maker, otherName, "getPackageStatic",
                            "Access Denied: method static int getPackageStatic() in class " + otherName + " is not visible");
            failGetMethodAccess(maker, otherName, "getPrivateStatic",
                            "Access Denied: method private static int getPrivateStatic() in class " + otherName + " is not visible");
            failGetMethodAccess(maker, otherName, "getPublicInt",
                            "Static call to non static method: public int getPublicInt() in class " + otherName);
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("publicStatic", 21, getIntField(myClass, null, "publicStatic"));
        assertEquals("protectedStatic", 22, getIntField(myClass, null, "protectedStatic"));
        assertEquals("packageStatic", 0, getIntField(myClass, null, "packageStatic"));
        assertEquals("privateStatic", 0, getIntField(myClass, null, "privateStatic"));
    }

    public void testStaticFieldSamePackage() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String otherName = "test.TestMethodAccess";
        ClassMaker baseMaker = new TestMethodAccessMaker(factory, otherName);
        Class otherClass = baseMaker.defineClass();

        String className = "test.MyClass";
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Implements(Runnable.class);

        defaultConstructor(maker);

        // Static fields
        maker.Declare("publicStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("protectedStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("packageStatic", int.class,  ACC_PUBLIC | ACC_STATIC);
        maker.Declare("privateStatic", int.class, ACC_PUBLIC | ACC_STATIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access methods in other object
            maker.Eval(maker.Set(className, "publicStatic", maker.Call(otherName, "getPublicStatic", null)));
            maker.Eval(maker.Set(className, "protectedStatic", maker.Call(otherName, "getProtectedStatic", null)));
            maker.Eval(maker.Set(className, "packageStatic", maker.Call(otherName, "getPackageStatic", null)));
            failGetMethodAccess(maker, otherName, "getPrivateStatic",
                            "Access Denied: method private static int getPrivateStatic() in class " + otherName + " is not visible");
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        // Classloaders must be the same for access to package or protected fields.
        assertEquals("ClassLoaders", otherClass.getClassLoader(), myClass.getClassLoader());

        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("publicStatic", 21, getIntField(myClass, null, "publicStatic"));
        assertEquals("protectedStatic", 22, getIntField(myClass, null, "protectedStatic"));
        assertEquals("packageStatic", 23, getIntField(myClass, null, "packageStatic"));
        assertEquals("privateStatic", 0, getIntField(myClass, null, "privateStatic"));
    }

    public void testStaticFieldSameClass() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        String className = "test.MyClass";
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);
        maker.Implements(Runnable.class);

        defaultConstructor(maker);

        // Static fields
        maker.Declare("publicStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("protectedStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Declare("packageStatic", int.class,  ACC_PUBLIC | ACC_STATIC);
        maker.Declare("privateStatic", int.class, ACC_PUBLIC | ACC_STATIC);

        maker.Method("getPublicStatic", int.class, ACC_PUBLIC | ACC_STATIC);
        maker.Begin();
            maker.Return(maker.Literal(21));
        maker.End();

        maker.Method("getProtectedStatic", int.class, ACC_PROTECTED | ACC_STATIC);
        maker.Begin();
            maker.Return(maker.Literal(22));
        maker.End();

        maker.Method("getPackageStatic", int.class, ACC_STATIC);
        maker.Begin();
            maker.Return(maker.Literal(23));
        maker.End();

        maker.Method("getPrivateStatic", int.class, ACC_PRIVATE | ACC_STATIC);
        maker.Begin();
            maker.Return(maker.Literal(24));
        maker.End();

        maker.Method("run", ClassMaker.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
        {
            // Access methods in other object
            maker.Eval(maker.Set(className, "publicStatic", maker.Call(className, "getPublicStatic", null)));
            maker.Eval(maker.Set(className, "protectedStatic", maker.Call(className, "getProtectedStatic", null)));
            maker.Eval(maker.Set(className, "packageStatic", maker.Call(className, "getPackageStatic", null)));
            maker.Eval(maker.Set(className, "privateStatic", maker.Call(className, "getPrivateStatic", null)));
            maker.Return();
        }
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("publicStatic", 21, getIntField(myClass, null, "publicStatic"));
        assertEquals("protectedStatic", 22, getIntField(myClass, null, "protectedStatic"));
        assertEquals("packageStatic", 23, getIntField(myClass, null, "packageStatic"));
        assertEquals("privateStatic", 24, getIntField(myClass, null, "privateStatic"));
    }
}
