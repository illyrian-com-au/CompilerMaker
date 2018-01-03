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

import java.lang.reflect.Method;

import org.mozilla.classfile.ByteCode;

import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;

public class MakerInvokeTest extends ClassMakerTestCase implements ByteCode
{
    ClassMakerFactory factory;
    ClassMaker maker;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
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

    public static class RunnableClass implements Runnable
    {
        public int id;
        public void run()
        {
            id = 5;
        }

        public int getId()
        {
            return id;
        }

        public void setId(int value)
        {
            id = value;
        }

        public static int add(int a, int b)
        {
            return a + b;
        }
    }

    public static class StaticClass
    {
        public static int id;

        public static int getId()
        {
            return id;
        }

        public static void setId(int value)
        {
            id = value;
        }
    }

    private void assertObjectMethods(Method[] methods) {
        assertContains("public java.lang.String java.lang.Object.toString()", methods);
        assertContains("public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException", methods);
        assertContains("public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException", methods); 
        assertContains("public final void java.lang.Object.wait() throws java.lang.InterruptedException", methods);
        assertContains("public boolean java.lang.Object.equals(java.lang.Object)", methods);
        assertContains("public java.lang.String java.lang.Object.toString()", methods);
        assertContains("public native int java.lang.Object.hashCode()", methods);
        assertContains("public final native java.lang.Class java.lang.Object.getClass()", methods); 
        assertContains("public final native void java.lang.Object.notify()", methods);
        assertContains("public final native void java.lang.Object.notifyAll()", methods);
    }
    
    public void testJavaClassMethods() {
        Class object = Object.class;
        assertFalse("isInterface", object.isInterface());
        Method [] methodsO = object.getMethods(); 
        assertEquals("No of methods", 9, methodsO.length);
        assertObjectMethods(methodsO);

        Method [] declaredO = object.getDeclaredMethods(); 
        assertEquals("No of methods", 12, declaredO.length);
        assertContains("public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException", declaredO);
        assertContains("protected native java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException", declaredO);
        assertContains("private static native void java.lang.Object.registerNatives()", declaredO);
        assertObjectMethods(declaredO);

        Class runnable = RunnableClass.class;
        assertFalse("isInterface", runnable.isInterface());
        Method [] methodsR = runnable.getMethods(); 
        assertEquals("No of methods", 13, methodsR.length);
        assertContains("public void au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass.run()", methodsR);
        assertContains("public static int au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass.add(int,int)", methodsR); 
        assertContains("public int au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass.getId()", methodsR);
        assertContains("public void au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass.setId(int)", methodsR);
        assertObjectMethods(methodsR);

        Method [] declaredR = runnable.getDeclaredMethods(); 
        assertEquals("No of methods", 4, declaredR.length);
        assertContains("public void au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass.run()", declaredR);
        assertContains("public static int au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass.add(int,int)", declaredR); 
        assertContains("public int au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass.getId()", declaredR);
        assertContains("public void au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass.setId(int)", declaredR);

        Class stat = StaticClass.class;
        assertFalse("isInterface", stat.isInterface());
        Method [] methodsS = stat.getMethods(); 
        assertEquals("No of methods", 11, methodsS.length);
        assertContains("public static int au.com.illyrian.classmaker.MakerInvokeTest$StaticClass.getId()", methodsS);
        assertContains("public static void au.com.illyrian.classmaker.MakerInvokeTest$StaticClass.setId(int)", methodsS);
        assertObjectMethods(methodsS);

        Method [] declaredS = stat.getDeclaredMethods(); 
        assertEquals("No of methods", 2, declaredS.length);
        assertContains("public static int au.com.illyrian.classmaker.MakerInvokeTest$StaticClass.getId()", declaredS);
        assertContains("public static void au.com.illyrian.classmaker.MakerInvokeTest$StaticClass.setId(int)", declaredS);
    }
    
    private void assertObjectMakerMethods(MakerMethod[] methods) {
        assertContains("public java.lang.String toString()", methods);
        assertContains("public final native void wait(long)", methods); 
        assertContains("public final void wait()", methods);
        assertContains("public final void wait(long, int)", methods);
        assertContains("public boolean equals(java.lang.Object)", methods);
        assertContains("public java.lang.String toString()", methods);
        assertContains("public native int hashCode()", methods);
        assertContains("public final native java.lang.Class getClass()", methods); 
        assertContains("public final native void notify()", methods);
        assertContains("public final native void notifyAll()", methods);
    }
    
    public void testClassTypeMethods()
    {
        ClassType execTypeO = maker.classToClassType(Object.class);
        assertFalse("isInterface", execTypeO.isInterface());
        //MakerMethod [] methodsO =  execTypeO.getAllMethods();
        MakerMethod [] methodsO = maker.getAllClassMethods(execTypeO);
        // FIXME - should not include clone() and wait(int, long) which are protected
        assertEquals("No of methods", /*9*/ 12, methodsO.length);
        assertObjectMakerMethods(methodsO);

        MakerMethod [] declaredO = execTypeO.getDeclaredMethods();
        assertEquals("No of methods", 12, declaredO.length);
        assertContains("protected void finalize()", declaredO);
        assertContains("protected native java.lang.Object clone()", declaredO);
        assertContains("private static native void registerNatives()", declaredO);
        assertObjectMakerMethods(declaredO);
    }

    // Generate public void run()
    public void testRunMethod() throws Exception
    {
        maker.Implements(Runnable.class);
        maker.Declare("id", int.class, ACC_PUBLIC);

        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
          maker.Set(maker.This(), "id", maker.Literal(2));
          maker.Return();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec =  (Runnable)myClass.newInstance();

        assertEquals("Wrong initial value for myObj.id", 0, getIntField(myClass, exec, "id"));
        exec.run();
        assertEquals("Wrong value for myObj.id", 2, getIntField(myClass, exec, "id"));
   }

    public void testJavaRunMethod() throws Exception
    {
        Runnable exec = new RunnableClass();

        int id =getIntField(exec.getClass(), exec, "id");
        assertEquals("Wrong initial value for myObj.id", 0, id);
        exec.run();
        id = getIntField(exec.getClass(), exec, "id");
        assertEquals("Wrong value for myObj.id", 5, id);
    }

    public void testCallRunnable() throws Exception
    {
        maker.Implements(Eval.class);

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
          maker.Declare("obj", RunnableClass.class, 0);
          maker.Set("obj", maker.New(RunnableClass.class).Init(null));
          maker.Call(maker.Get("obj"), "run", maker.Push());
          maker.Return(maker.Get(maker.Get("obj"), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 5, exec.eval());
    }

    public void testCallStaticMethodFromRunnable() throws Exception
    {
        maker.Implements(Eval.class);

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
          maker.Declare("obj", RunnableClass.class, 0);
          maker.Set("obj", maker.New(RunnableClass.class).Init(null));
          maker.Return(maker.Call(maker.Get("obj"), "add", maker.Push(maker.Literal(1)).Push(maker.Literal(2))));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 3, exec.eval());
    }

    public void testCallPrivate() throws Exception
    {
        maker.Implements(Eval.class);

        maker.Method("add", int.class, ACC_PRIVATE);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Begin();
          maker.Return(maker.Add(maker.Get("a"), maker.Get("b")));
        maker.End();

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
          maker.Return(maker.Call(maker.This(), "add", maker.Push(maker.Literal(1)).Push(maker.Literal(2))));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 3, exec.eval());
    }

    public void testCallSetGet() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/MakerInvokeTest$Eval");
        maker.Implements("Eval");
        maker.Import(RunnableClass.class);

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
          maker.Declare("obj", RunnableClass.class, 0);
          // (obj = new RunnableClass).<init>();
          maker.Set("obj", maker.New(RunnableClass.class).Init(null));
          // obj.setId(5);
          maker.Call(maker.Get("obj"), "setId", maker.Push(maker.Literal(5)));
          // return obj.id;
          maker.Return(maker.Get(maker.Get("obj"), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 5, exec.eval());
    }

    public void testCallStatic() throws Exception
    {
        String StaticClassStr = "au/com/illyrian/classmaker/MakerInvokeTest$StaticClass";
        maker.Import(StaticClassStr);
        maker.Implements(Eval.class);

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
          // obj.setId(4);
          maker.Call(StaticClass.class, "setId", maker.Push(maker.Literal(4)));
          // return obj.id;
          maker.Return(maker.Get(StaticClassStr, "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 4, exec.eval());
    }

    public void testPackageDeclared() throws Exception
    {
        maker.setPackageName("au.com.illyrian.classmaker");
        maker.setSimpleClassName("Test");
        Type declared1 = maker.findType("CallStack");
        assertNotNull("Cannot find CallStack", declared1);
        assertEquals("Wrong class name", "au.com.illyrian.classmaker.CallStack", declared1.getName());
        Type declared2 = maker.findType("ClassMakerIfc");
        assertNotNull("Cannot find ClassMakerIfc", declared2);
        assertEquals("Wrong class name", "au.com.illyrian.classmaker.ClassMakerIfc", declared2.getName());
    }

    public void testImport() throws Exception
    {
        try {
            maker.Import("test.dummy");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("No class type called \'test.dummy\'", ex.getMessage());
        }
        maker.Import(java.util.Date.class);
        assertNotNull(maker.stringToClassType("java.util.Date"));
        assertNotNull(maker.stringToClassType("Date"));
        maker.Import(java.util.Date.class);
        assertNotNull(maker.stringToClassType("java.util.Date"));
        assertNotNull(maker.stringToClassType("Date"));
        maker.Import("java.util.Date");
        assertNotNull(maker.stringToClassType("java.util.Date"));
        assertNotNull(maker.stringToClassType("Date"));
        maker.Import(java.sql.Date.class);
        assertNotNull(maker.stringToClassType("java.sql.Date"));
        assertNotNull(maker.stringToClassType("java.util.Date"));
        maker.Import("java.sql.Date");
        try {
            maker.findType("Date");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("'Date' is ambiguous; must use the fully qualified class name", ex.getMessage());
        }
        try {
            maker.findImportedType("Date");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("'Date' is ambiguous; must use the fully qualified class name", ex.getMessage());
        }
    }

    public void testCallException() throws Exception
    {
        String RunnableClassStr = "au.com.illyrian.classmaker.MakerInvokeTest$RunnableClass";
        maker.Import(RunnableClassStr);
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
        try {
            maker.Call(RunnableClass.class, "setId", maker.Push(maker.Literal(4)));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Static call to non static method: public void setId(int) in class " + RunnableClassStr, ex.getMessage());
        }
        try {
            maker.Call(maker.Literal(1), "setId", null);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Type byte is not a class", ex.getMessage());
        }
    }

    public static final String BINARY_CLASS = "au/com/illyrian/classmaker/BinaryClass";
    public static final String BINARY_IFACE = "au/com/illyrian/classmaker/MakerInvokeTest$Binary";

    public Class binaryOperatorClass() throws Exception
    {
        String className = BINARY_CLASS;
        ClassMaker submaker = factory.createClassMaker("au/com/illyrian/classmaker", "BinaryClass", className + ".java");
        submaker.setClassModifiers(ClassMakerConstants.ACC_PUBLIC);
        submaker.Implements(Binary.class);

        submaker.Method("binary", int.class, ACC_PUBLIC);
        submaker.Declare("a", int.class, 0);
        submaker.Declare("b", int.class, 0);
        submaker.Begin();
          submaker.Return(submaker.Mult(submaker.Get("a"), submaker.Get("b")));
        submaker.End();

        return submaker.defineClass();
    }

    public void testBinaryOperatorClass() throws Exception
    {
        Class myClass = binaryOperatorClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.binary()", 10, exec.binary(2, 5));
    }

    public void testCallBinaryOperatorMethod() throws Exception
    {
        maker.Implements(Unary.class);

        // Create the class that gets called.
        Class binClass = binaryOperatorClass();

        maker.Declare("val", int.class, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("a", int.class, 0);
        maker.Begin();
            maker.Declare("obj", binClass, 0);
            // obj = new RunnableClass();
            maker.Set("obj", maker.New(binClass).Init(null));
            // obj.setId(5);
            maker.Return(maker.Call(maker.Get("obj"), "binary",
                    maker.Push(maker.Get("a"))
                         .Push(maker.Get(maker.This(), "val"))));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(3));
        setIntField(exec.getClass(), exec, "val", 2);
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", -4, exec.unary(-2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
    }

    // Same as previous test except calling the Binary interface.
    public void testCallBinaryOperatorInterface() throws Exception
    {
        maker.Implements(Unary.class);

        // Create the class that gets called.
        Class binClass = binaryOperatorClass();

        maker.Declare("val", int.class, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("unary", int.class, ACC_PUBLIC);
        maker.Declare("a", int.class, 0);
        maker.Begin();
            maker.Declare("obj", Binary.class, 0);
            // obj = new RunnableClass();
            maker.Set("obj", maker.Cast(maker.New(binClass).Init(null), Binary.class));
            // obj.binary(a, this.val);
            maker.Return(maker.Call(maker.Get("obj"), "binary",
                            maker.Push(maker.Get("a"))
                                 .Push(maker.Get(maker.This(), "val"))));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(3));
        setIntField(exec.getClass(), exec, "val", 2);
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", -4, exec.unary(-2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
    }

    public void testReturnVoidException() throws Exception
    {
        try {
            maker.Return();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
                assertEquals("Return while not in a method", ex.getMessage());
        }
        maker.Method("run", void.class, ACC_PUBLIC);
        maker.Begin();
            maker.Declare("a", int.class, 0);
        	maker.Set("a", maker.Literal(8));
        try {
            maker.Return(ClassMakerFactory.INT_TYPE.getValue());
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
                assertEquals("Method run returns void so must not return a value", ex.getMessage());
        }
    }

    public void testReturnValueException() throws Exception
    {
        try {
            maker.Return(ClassMakerFactory.INT_TYPE.getValue());
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
                assertEquals("Return while not in a method", ex.getMessage());
        }
        maker.Method("run", int.class, ACC_PUBLIC);
        maker.Begin();
            maker.Declare("a", int.class, 0);
                maker.Set("a", maker.Literal(8));
        try {
            maker.End();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
                assertEquals("A call to Return or Throw must precede End()", ex.getMessage());
        }
        try {
            maker.Return(ClassMakerFactory.VOID_TYPE.getValue());
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
                assertEquals("Cannot return type void", ex.getMessage());
        }
        try {
            maker.Return(maker.Literal(500L));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
                assertEquals("Method run returns type int so cannot return a value of type long", ex.getMessage());
        }
        try {
            maker.Return(maker.Literal("Hello"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
                assertEquals("Method run returns type int so cannot return a value of type java.lang.String", ex.getMessage());
        }
        try {
            maker.Return();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
                assertEquals("Method run must return type int", ex.getMessage());
        }
    }

    public void testForwardDeclaration() throws Exception
    {
        maker.Implements(Eval.class);

        maker.Method("add", int.class, ACC_PRIVATE);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Forward();

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
          maker.Return(maker.Call(maker.This(), "add", maker.Push(maker.Literal(1)).Push(maker.Literal(2))));
        maker.End();

        maker.Method("add", int.class, ACC_PRIVATE);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Begin();
          maker.Return(maker.Add(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 3, exec.eval());
    }

    public void testImplements()
    {
        try {
            maker.Implements(int.class);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("int is not a class", ex.getMessage());
        }
        try {
            maker.Implements("int");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("No class type called \'int\'", ex.getMessage());
        }
        try {
            maker.Implements("does.not.Exist");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("No class type called \'does.not.Exist\'", ex.getMessage());
        }
    }

    public void testAddClassType()
    {
        try {
            factory.addClassType(int.class);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("int is not a class", ex.getMessage());
        }
    }

    public void code(ClassMaker maker) throws Exception
    {
        maker.Implements(Eval.class);

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
          maker.Set(maker.This(), "value", maker.Call(maker.This(), "add", maker.Push(maker.Literal(1)).Push(maker.Literal(2))));
          maker.Return(maker.Get(maker.This(), "value"));
        maker.End();

        maker.Method("add", int.class, ACC_PRIVATE);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Begin();
          maker.Return(maker.Add(maker.Get("a"), maker.Get("b")));
        maker.End();

        maker.Declare("value", int.class, ACC_PUBLIC);
    }

    public void testTwoPassGeneration() throws Exception
    {
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");

        factory.setPass(ClassMakerConstants.FIRST_PASS);
        assertNull("Should return null: maker.getGen()", maker.getGen());

        code(maker);
        maker.EndClass();

        assertEquals("Number of interfaces", 1, maker.getDeclaredInterfaces().length);
        assertEquals("Number of fields", 1, maker.getDeclaredFields().length);
        assertEquals("Number of methods", 2, maker.getDeclaredMethods().length);
        assertEquals("Number of constructors", 1, maker.getDeclaredConstructors().length);
        assertEquals("Number of methods", 14, maker.getAllClassMethods(maker.getClassType()).length);

        factory.setPass(ClassMakerConstants.SECOND_PASS);
        assertNotNull("Should not be null : maker.getGen()", maker.getGen());
        code(maker);
        maker.EndClass();

        assertEquals("Number of interfaces", 1, maker.getDeclaredInterfaces().length);
        assertEquals("Number of fields", 1, maker.getDeclaredFields().length);
        assertEquals("Number of methods", 2, maker.getDeclaredMethods().length);
        assertEquals("Number of constructors", 1, maker.getDeclaredConstructors().length);
        assertEquals("Number of methods", 14, maker.getAllClassMethods(maker.getClassType()).length);

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 3, exec.eval());
    }
    
    public static interface InterfaceA extends Runnable
    {
    	public void runA();
    }
    
    public static interface ExecutableAA extends InterfaceA
    {
    	public void execA();
    }
    
    public static interface ExecutableAB extends Runnable
    {
    	public void execB();
    }
    
    public void testFindInterfaceMethods()
    {
        ClassType execTypeR = maker.classToClassType(Runnable.class);
        assertTrue("isInterface", execTypeR.isInterface());
        //MakerMethod [] methodsR =  execTypeR.getAllMethods();
        MakerMethod [] methodsR =  maker.getAllInterfaceMethods(execTypeR);
        assertEquals("No of methods", 1, methodsR.length);
        assertContains("public abstract void run()", methodsR);

        ClassType execTypeA = maker.classToClassType(ExecutableAA.class);
        assertTrue("isInterface", execTypeA.isInterface());
        MakerMethod [] methodsA = maker.getAllInterfaceMethods(execTypeA);
        assertEquals("No of methods", 3, methodsA.length);
        assertContains("public abstract void run()", methodsA);
        assertContains("public abstract void runA()", methodsA);
        assertContains("public abstract void execA()", methodsA);
        
        ClassType execTypeAB = maker.classToClassType(ExecutableAB.class);
        assertTrue("isInterface", execTypeAB.isInterface());
        MakerMethod [] methodsAB = maker.getAllInterfaceMethods(execTypeAB); 
        assertEquals("No of methods", 2, methodsAB.length);
        assertContains("public abstract void run()", methodsAB);
        assertContains("public abstract void execB()", methodsAB);
        
        ClassType execTypeAA = maker.classToClassType(ExecutableAA.class);
        assertTrue("isInterface", execTypeAA.isInterface());
        MakerMethod [] methodsAA = maker.getAllInterfaceMethods(execTypeAA); 
        assertEquals("No of methods", 3, methodsAA.length);
        assertContains("public abstract void run()", methodsAA);
        assertContains("public abstract void runA()", methodsAA);
        assertContains("public abstract void execA()", methodsAA);
    }
    
    public void testJavaInterfaceMethods() {
        Class runnable = Runnable.class;
        assertTrue("isInterface", runnable.isInterface());
        Method [] methodsR = runnable.getMethods(); 
        assertEquals("No of methods", 1, methodsR.length);
        assertContains("public abstract void java.lang.Runnable.run()", methodsR);

        Class execJavaA = InterfaceA.class;
        assertTrue("isInterface", execJavaA.isInterface());
        Method [] methodsA = execJavaA.getMethods(); 
        assertEquals("No of methods", 2, methodsA.length);
        assertContains("public abstract void java.lang.Runnable.run()", methodsA);
        assertContains("public abstract void au.com.illyrian.classmaker.MakerInvokeTest$InterfaceA.runA()", methodsA);


        Class execJavaAB = ExecutableAB.class;
        assertTrue("isInterface", execJavaAB.isInterface());
        Method [] methodsAB = execJavaAB.getMethods(); 
        assertEquals("No of methods", 2, methodsAB.length);
        assertContains("public abstract void java.lang.Runnable.run()", methodsAB);
        assertContains("public abstract void au.com.illyrian.classmaker.MakerInvokeTest$ExecutableAB.execB()", methodsAB);

        Class execJavaAA = ExecutableAA.class;
        assertTrue("isInterface", execJavaAA.isInterface());
        Method [] methodsAA = execJavaAA.getMethods(); 
        assertEquals("No of methods", 3, methodsAA.length);
        assertContains("public abstract void java.lang.Runnable.run()", methodsAA);
        assertContains("public abstract void au.com.illyrian.classmaker.MakerInvokeTest$InterfaceA.runA()", methodsAA);
        assertContains("public abstract void au.com.illyrian.classmaker.MakerInvokeTest$ExecutableAA.execA()", methodsAA);
    }
    
    public void assertContains(String find, Object [] list)
    {
    	for (Object item : list)
    	{
    	    if (find.equals(item.toString()))
    	        return;
    	}
    	fail("Could not find: " + find);
    }
    
    static public class Other {
        public int getA() {return 1;}
    }

    public void testOtherGetZ() throws Exception
    {
        maker.Implements(Eval.class);
        maker.Declare("other", Other.class, ACC_PUBLIC);

        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Begin();
          maker.Return(maker.Call(maker.Get(maker.This(), "other"), "getA", maker.Push()));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();
        Other other = new Other();
        setField(myClass, exec, "other", other);

        assertEquals("Wrong value for exec.eval()", 1, exec.eval());
    }

    public void testMthodExceptions() throws Exception
    {
        maker.Method("getValue", int.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
        {
            maker.Return(maker.Literal(1));
        }
        try {
            maker.Method("getNext", int.class, ClassMakerConstants.ACC_PUBLIC);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Missing End() for previous method: public int getValue()", ex.getMessage());
        }
        maker.End();

        maker.Method("getNext", int.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
        {
            maker.Return(maker.Literal(2));
        }
        try {
            maker.EndClass();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Missing End() for previous method: public int getNext()", ex.getMessage());
        }
        maker.End();
        maker.EndClass();

        Class myClass = maker.defineClass();
        myClass.newInstance();
    }

}
