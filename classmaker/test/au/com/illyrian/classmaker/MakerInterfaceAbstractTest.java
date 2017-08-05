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

public class MakerInterfaceAbstractTest extends ClassMakerTestCase implements SourceLine
{
    public void setUp() throws Exception
    {
        super.setUp();
        ClassMakerCode.setSharedFactory(null);
    }

    public static class BaseClass
    {
        public int id;

        public int getId() {return Integer.MIN_VALUE;}

        public void setId(int value) {}
    }

    public static class DerivedClassMaker extends ClassMakerCode
    {
        public void code()
        {
            Extends(BaseClass.class);

            Method("getId", int.class, ACC_PUBLIC);
            Begin();
            {
                Return(Get(This(), "id"));
            }
            End();

            Method("setId", void.class, ACC_PUBLIC);
            Declare("value", int.class, 0);
            Begin();
            {
                Eval(Set(This(), "id", Get("value")));
                Return();
            }
            End();
        }
    }

    public void testDerivedClass() throws Exception
    {
        ClassMaker maker = new DerivedClassMaker();
        Class myClass = maker.defineClass();
        BaseClass exec =  (BaseClass)myClass.newInstance();

        assertEquals("Wrong initial value for myObj.id", 0, getIntField(myClass, exec, "id"));
        assertEquals("Wrong initial value for myObj.getId()", 0, exec.getId());
        exec.setId(2);
        assertEquals("Wrong initial value for myObj.id", 2, getIntField(myClass, exec, "id"));
        assertEquals("Wrong initial value for myObj.getId()", 2, exec.getId());
   }

    public static abstract class AbstractClass implements AccessId
    {
        public int id;

        public abstract int getId();
        public abstract void setId(int value);
    }

    public static class FullClassMaker extends ClassMakerCode
    {
        public void code()
        {
            Declare("id", int.class, ACC_PUBLIC);

            Method("getId", int.class, ACC_PUBLIC);
            Begin();
            {
                Return(Get(This(), "id"));
            }
            End();

            Method("setId", void.class, ACC_PUBLIC);
            Declare("value", int.class, 0);
            Begin();
            {
                Eval(Set(This(), "id", Get("value")));
                Return();
            }
            End();

            Method("exec", int.class, ACC_PUBLIC);
            Declare("value", int.class, 0);
            Begin();
            {
                Return(Inc("value"));
            }
            End();
            
            Method("toString", String.class, ACC_PUBLIC);
            Begin();
            {
            	Return(Literal("class FullClass"));
            }
            End();
        }
    }

    public void testAbstractClass() throws Exception
    {
        ClassMaker maker = new FullClassMaker();
        maker.Extends(BaseClass.class);
        Class myClass = maker.defineClass();
        BaseClass exec =  (BaseClass)myClass.newInstance();

        assertEquals("Wrong initial value for myObj.id", 0, getIntField(myClass, exec, "id"));
        assertEquals("Wrong initial value for myObj.getId()", 0, exec.getId());
        exec.setId(2);
        assertEquals("Wrong final value for myObj.id", 2, getIntField(myClass, exec, "id"));
        assertEquals("Wrong final value for myObj.getId()", 2, exec.getId());
   }

    public interface AccessId
    {
        public int getId();
        public void setId(int value);
    }

    public static class BaseGenClassMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(AccessId.class);
            Declare("id", int.class, ACC_PUBLIC);

            Method("getId", int.class, ACC_PUBLIC);
            Begin();
            {
                Return(Literal(Integer.MAX_VALUE));
            }
            End();

            Method("setId", void.class, ACC_PUBLIC);
            Declare("value", int.class, 0);
            Begin();
              Return();
            End();
        }
    }

    public static class DerivedGenClassMaker extends ClassMakerCode
    {
        Class base;
        public DerivedGenClassMaker(Class baseClass)
        {
            super();
            base = baseClass;
        }

        public void code()
        {
            Extends(base);
            Declare("id", int.class, 0);

            Method("getId", int.class, ACC_PUBLIC);
            Begin();
            {
                Return(Get(This(), "id"));
            }
            End();

            Method("setId", void.class, ACC_PUBLIC);
            Declare("value", int.class, 0);
            Begin();
            {
                Eval(Set(This(), "id", Get("value")));
                Return();
            }
            End();
        }
    }

    public void testDerivedGenClass() throws Exception
    {
        ClassMaker baseMaker = new BaseGenClassMaker();
        Class baseClass = baseMaker.defineClass();
        ClassMaker derivedMaker = new DerivedGenClassMaker(baseClass);
        Class derivedClass = derivedMaker.defineClass();
        AccessId exec =  (AccessId)derivedClass.newInstance();

        assertEquals("Wrong initial value for myObj.id", 0, getIntField(derivedClass, exec, "id"));
        assertEquals("Wrong initial value for myObj.getId()", 0, exec.getId());
        exec.setId(2);
        assertEquals("Wrong initial value for myObj.id", 0, getIntField(derivedClass, exec, "id"));
        assertEquals("Wrong initial value for myObj.getId()", 2, exec.getId());
    }

    public static class AbstractClassMaker extends ClassMakerCode
    {
        public int getModifiers()
        {
            return ACC_PUBLIC | ACC_ABSTRACT;
        }
        public void code()
        {
            Implements(AccessId.class);
            Declare("id", int.class, ACC_PUBLIC);

            // BEGIN - Abstract method example
            Method("getId", int.class, ACC_PUBLIC | ACC_ABSTRACT);
            Forward();

            Method("setId", void.class, ACC_PUBLIC | ACC_ABSTRACT);
            Declare("value", int.class, 0);
            Forward();
            // END - Abstract method example
        }
    }

    public static class DerivedAbstractClassMaker extends ClassMakerCode
    {
        Class base;
        public DerivedAbstractClassMaker(Class baseClass)
        {
            super();
            base = baseClass;
        }

        public void code()
        {
            Extends(base);

            Method("getId", int.class, ACC_PUBLIC);
            Begin();
            {
                Return(Get(This(), "id"));
            }
            End();

            Method("setId", void.class, ACC_PUBLIC);
            Declare("value", int.class, 0);
            Begin();
            {
                Eval(Set(This(), "id", Get("value")));
                Return();
            }
            End();
        }
    }

    public static final String ABSTRACT_CLASS_NAME = "au.com.illyrian.classmaker.MakerInterfaceAbstractTest$AbstractClass";
    public void testDerivedAbstractClass() throws Exception
    {
        ClassMaker baseMaker = new AbstractClassMaker();
        Class baseClass = baseMaker.defineClass();
        ClassMaker derivedMaker = new DerivedAbstractClassMaker(baseClass);
        Class derivedClass = derivedMaker.defineClass();
        AccessId exec =  (AccessId)derivedClass.newInstance();

        assertEquals("Wrong initial value for myObj.id", 0, getIntField(derivedClass, exec, "id"));
        assertEquals("Wrong initial value for myObj.getId()", 0, exec.getId());
        exec.setId(2);
        assertEquals("Wrong final value for myObj.id", 2, getIntField(derivedClass, exec, "id"));
        assertEquals("Wrong final value for myObj.getId()", 2, exec.getId());
        assertEquals("ClassName ", ABSTRACT_CLASS_NAME, baseClass.getName());
   }

    public static class IncompleteAbstractClassMaker extends ClassMakerCode
    {
        public void code()
        {
        }
    }

    public void testAbstractExceptions() throws Exception
    {
        ClassMaker baseMaker = new AbstractClassMaker();
        Class baseClass = baseMaker.defineClass();
        ClassMaker derivedMaker = new IncompleteAbstractClassMaker();
        derivedMaker.Extends(baseClass);
        try {
            derivedMaker.defineClass();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex)
        {
            assertEquals("ClassMakerException", "Abstract methods in a concrete class: public abstract int getId()\npublic abstract void setId(int)", ex.getMessage());
        }
    }

    public void testInterfaceExceptions() throws Exception
    {
        // Create the interface class
        ClassMaker ifaceMaker = new InterfaceClassMaker();
        Class ifaceClass = ifaceMaker.defineClass();
        ClassMaker derivedMaker = new IncompleteAbstractClassMaker();
        derivedMaker.Implements(ifaceClass);
        try {
            derivedMaker.defineClass();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex)
        {
            assertEquals("ClassMakerException", "Class does not implement all methods in interface: public abstract int getId()\npublic abstract void setId(int)", ex.getMessage());
        }
    }

    public static class InterfaceClassMaker extends ClassMakerCode
    {
        public void code()
        {
            setIsInterface();
            Method("getId", int.class, ACC_PUBLIC | ACC_ABSTRACT);
            Forward();

            Method("setId", void.class, ACC_PUBLIC | ACC_ABSTRACT);
            Declare("value", int.class, 0);
            Forward();
        }
    }

    public interface Execute {
        int exec(int x);
    }

    public static class ExecutorClassMaker extends ClassMakerCode
    {
        public ExecutorClassMaker(Class interfaceClass)
        {
            this.ifaceClass = interfaceClass;
        }
        private Class ifaceClass;
        public void code()
        {
            Implements(Execute.class);
            Declare("access", ifaceClass, ACC_PUBLIC);

            Method("exec", int.class, ACC_PUBLIC);
            Declare("value", int.class, 0);
            Begin();
            {
                Declare("temp", int.class, 0);
                Eval(Set("temp", Call(Get(This(), "access"), "getId", null)));
                Eval(Call(Get(This(), "access"), "setId", Push(Get("value"))));
                Return(Get("temp"));
            }
            End();
        }
    }

    public void testImplementsInterfaceClass() throws Exception
    {
        // Create the interface class
        ClassMaker ifaceMaker = new InterfaceClassMaker();
        Class ifaceClass = ifaceMaker.defineClass();
        ifaceMaker.saveClass(new File("build/classes"));
        // Create the class that implements the interface
        ClassMaker fullMaker = new FullClassMaker();
        fullMaker.Implements(ifaceClass);
        Class fullClass = fullMaker.defineClass();
        Object object =  fullClass.newInstance();
        // Create the class that calls the interface
        ClassMaker execMaker = new ExecutorClassMaker(ifaceClass);
        Class execClass = execMaker.defineClass();
        Execute exec =  (Execute)execClass.newInstance();

        // Set the interface value.
        setField(execClass, exec, "access", object);
        setIntField(fullClass, object, "id", 5);
        assertEquals("Wrong initial value for id", 5, getIntField(fullClass, object, "id"));
        int prev = exec.exec(2);
        assertEquals("Wrong final value for myObj.id", 2, getIntField(fullClass, object, "id"));
        assertEquals("wrong return value", 5, prev);
   }


    public static class ExtendsExecutorClassMaker extends ClassMakerCode
    {
        public ExtendsExecutorClassMaker(Class interfaceClass)
        {
            this.ifaceClass = interfaceClass;
        }
        private Class ifaceClass;
        public void code()
        {
            Implements(Execute.class);
            Declare("executor", ifaceClass, ACC_PUBLIC);
            Declare("result", int.class, ACC_PUBLIC);
            Declare("descriptor", String.class, ACC_PUBLIC);

            Method("exec", int.class, ACC_PUBLIC);
            Declare("value", int.class, 0);
            Begin();
            {
                Set(This(), "result", Call(Get(This(), "executor"), "exec", Push(Get(This(), "result"))));
                Declare("temp", int.class, 0);
                Eval(Set("temp", Call(Get(This(), "executor"), "getId", null)));
                Eval(Call(Get(This(), "executor"), "setId", Push(Get("value"))));
                // Test calling a concrete Object Method on an interface.
                Eval(Set(This(), "descriptor", Call(Get(This(), "executor"), "toString", null)));
                Return(Get("temp"));
            }
            End();
        }
    }

    public static class ExtendsInterfaceClassMaker extends ClassMakerCode
    {
        private Class ifaceClass;
        public ExtendsInterfaceClassMaker(Class interfaceClass)
        {
            this.ifaceClass = interfaceClass;
        }
        public void code()
        {
            setIsInterface();
            Implements(ifaceClass);

            Method("exec", int.class, ACC_PUBLIC | ACC_ABSTRACT);
            Declare("value", int.class, 0);
            Forward();
        }
    }

    public void testExtendsInterfaceClass() throws Exception
    {
        // Create the interface class
        ClassMaker ifaceMaker = new InterfaceClassMaker();
        Class ifaceClass = ifaceMaker.defineClass();
        // Create the interface class
        ClassMaker exfaceMaker = new ExtendsInterfaceClassMaker(ifaceClass);
        Class exfaceClass = exfaceMaker.defineClass();
        exfaceMaker.saveClass(new File("build/classes"));
        // Create the class that implements the interface
        ClassMaker fullMaker = new FullClassMaker();
        fullMaker.Implements(exfaceClass);
        Class fullClass = fullMaker.defineClass();
        Object object =  fullClass.newInstance();
        // Create the class that calls the interface
        ClassMaker execMaker = new ExtendsExecutorClassMaker(exfaceClass);
        Class execClass = execMaker.defineClass();
        Execute exec =  (Execute)execClass.newInstance();

        // Set the interface value.
        setField(execClass, exec, "executor", object);
        setIntField(fullClass, object, "id", 5);
        assertEquals("Wrong initial value for id", 5, getIntField(fullClass, object, "id"));
        int prev = exec.exec(2);
        assertEquals("Wrong final value for myObj.id", 2, getIntField(fullClass, object, "id"));
        assertEquals("wrong return value", 5, prev);
        assertEquals("Access concrete class on interface", "class FullClass", getField(execClass, exec, "descriptor"));
   }

    public void testAbstractMethodExceptions()
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        maker.Method("test", void.class, ClassMakerConstants.ACC_PUBLIC | ClassMakerConstants.ACC_ABSTRACT);
        try {
            maker.Begin();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "Abstract method cannot have a body. Use Forward() instead of Begin()", ex.getMessage());
        }
        try {
            maker.End();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "Abstract method cannot have a body. Use Forward() instead of End()", ex.getMessage());
        }
    }

    public void testExtendsImplementsExceptions()
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        try {
            maker.Extends(Runnable.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "Cannot extend an interface: java.lang.Runnable", ex.getMessage());
        }
        try {
            maker.Extends("java.lang.Runnable");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                    "Cannot extend an interface: java.lang.Runnable", ex.getMessage());
        }
        try {
            maker.Implements(String.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                    "Class is not an interface: java.lang.String", ex.getMessage());
        }
        try {
            maker.Implements("java.lang.String");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                    "Class is not an interface: java.lang.String", ex.getMessage());
        }
    }

    public void testClassExceptions()
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        maker.getClassFileWriter();
        try {
            maker.Extends(Object.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "To late to Extend the class. Call method Extend earlier.", ex.getMessage());
        }
        try {
            maker.setFullyQualifiedClassName("MyClass");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "To late to name the fully qualified class. Call method setFullQualifiedClassName earlier.", ex.getMessage());
        }
        try {
            maker.setPackageName("test");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "To late to name the package. Call method setPackageName earlier.", ex.getMessage());
        }
        try {
            maker.setSimpleClassName("MyClass");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "To late to name the class. Call method setSimpleClassName earlier.", ex.getMessage());
        }
        try {
            maker.setSourceFilename("MyClass.java");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "To late to set the source file name. Call method setSourceFilename earlier.", ex.getMessage());
        }
        try {
            maker.setClassType(null);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "To late to set the class type. Call method setClassType earlier.", ex.getMessage());
        }

        ClassMaker maker2 = factory.createClassMaker();
        maker2.setSourceLine(this);
        try {
            maker2.setSourceFilename("Dummy.java");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("ClassMakerException",
                "Cannot set source filename because it is provided by the SourceLine interface", ex.getMessage());
        }
        assertEquals("Source File Name:", getFilename(), maker2.getSourceLine().getFilename());
        assertEquals("Source Line Number:", getLineNumber(), maker2.getSourceLine().getLineNumber());
    }
    
    /** The name of the source file. */
    public String getFilename()
    {
    	return "HelloWorld.java";
    }

    /** The current line number in the source file */
    public int getLineNumber()
    {
    	return 123456; 
    }
}
