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

import junit.framework.TestCase;

public class ExampleMethodsTest extends TestCase
{
    public interface Accessable
    {
        void exec();
        void setName(String name);
        String getName();
    }
    
    public class AccessMaker extends ClassMakerBase
    {
        public void code()
        {
            Implements(Accessable.class);
            // BEGIN - Declaring Methods example
            Declare("name", String.class, ACC_PUBLIC);

            Method("exec", void.class, ACC_PUBLIC);
            Begin();
                Eval(Set(This(), "name", Literal("Hello World")));
            End();
            // END - Declaring Methods example

            // BEGIN - Formal Parameters example
            Method("setName", void.class, ACC_PUBLIC);
            Declare("name", String.class, 0);
            Begin();
                Eval(Set(This(), "name", Get("name")));
            End();
            // END - Formal Parameters example

            // BEGIN - Returning Values example
            Method("getName", String.class, ACC_PUBLIC);
            Begin();
                Return(Get(This(), "name"));
            End();
            // END - Returning Values example
        }
    }

    public void testMethodDeclarations() throws Exception
    {
        ClassMaker maker = new AccessMaker();
        Class testClass = maker.defineClass();
        Accessable exec = (Accessable)testClass.newInstance();
        
        String value = (String)testClass.getField("name").get(exec);
        assertNull("name", value);
        
        exec.exec();
        value = (String)testClass.getField("name").get(exec);
        assertEquals("name", "Hello World", value);
        
        exec.setName("Wally");
        value = (String)testClass.getField("name").get(exec);
        assertEquals("name", "Wally", value);
        
        testClass.getField("name").set(exec, "Bruce");
        value = exec.getName();
        assertEquals("name", "Bruce", value);
    }
    
	static Executable instance = null;

	public static Executable getInstance()
	{
		return ExampleMethodsTest.instance;
	}
	
	public static void setInstance(Executable invoke)
	{
		ExampleMethodsTest.instance = invoke;
	}

	public class InvocationJava implements Executable
    {
    	public Executable obj;
    	int x;
    	int y;
    	
    	public void run()
    	{
    		x = 2;
    	}
    	
    	public int add(int a, int b)
    	{
    		return a + b;
    	}
    	
    	public void exec()
    	{
            run();
            obj.run();
            x = add(1, 2);
            y = obj.add(x, 3);
            obj = ExampleMethodsTest.getInstance();
            ExampleMethodsTest.setInstance(this);
    	}
    }

	public void testJavaMethodInvocation() throws Exception
    {
	    ExampleMethodsTest.instance = null;
    	InvocationJava obj1 = new InvocationJava();
    	InvocationJava obj2 = new InvocationJava();
    	obj1.obj = obj2;
        obj1.exec();
        assertEquals("obj1.x", 3, obj1.x);
        assertEquals("obj2.x", 2, obj2.x);
        assertEquals("obj1.y", 6, obj1.y);
        assertEquals("obj2.y", 0, obj2.y);
        assertNull("obj1.obj", obj1.obj);
        assertEquals("ExampleMethodsTest.instance", obj1, ExampleMethodsTest.getInstance());
    }
    
	public interface Executable extends Runnable
	{
		public int add(int a, int b);
		public void exec();
	}
	
	public class InvocationMaker extends ClassMakerBase
    {
        public void code()
        {
            Implements(Executable.class);
            Declare("obj", Executable.class, ACC_PUBLIC);
            Declare("x", int.class, ACC_PUBLIC);
            Declare("y", int.class, ACC_PUBLIC);

            Method("run", void.class, ACC_PUBLIC);
            Begin();
                Eval(Set(This(), "x", Literal(2)));
            End();

            Method("add", int.class, ACC_PUBLIC);
            Declare("a", int.class, 0);
            Declare("b", int.class, 0);
            Begin();
                Return(Add(Get("a"), Get("b")));
            End();
        	
            Method("exec", void.class, ACC_PUBLIC);
            Begin();
                Eval(Call(This(), "run", null));
                Eval(Call(Get(This(), "obj"), "run", null));
                Eval(Set(This(), "x", Call(This(), "add", Push(Literal(1)).Push(Literal(2)))));
                Eval(Set(This(), "y", Call(Get(This(), "obj"), "add", Push(Get(This(), "x")).Push(Literal(3)))));
                Eval(Set(This(), "obj", Call(ExampleMethodsTest.class, "getInstance", null)));
                Eval(Call(ExampleMethodsTest.class, "setInstance", Push(This())));
            End();
        }
    }

	public void testMethodInvocation() throws Exception
    {
        ExampleMethodsTest.instance = null;
		ClassMakerBase.setSharedFactory(null);
		ExampleMethodsTest.setInstance(null);
		InvocationMaker maker = new InvocationMaker();
		Class invocationClass = maker.defineClass(); 
    	Executable obj1 = (Executable)invocationClass.newInstance();
    	Executable obj2 = (Executable)invocationClass.newInstance();
    	invocationClass.getField("obj").set(obj1, obj2);
        obj1.exec();
        assertEquals("obj1.x", 3, invocationClass.getField("x").getInt(obj1));
        assertEquals("obj2.x", 2, invocationClass.getField("x").getInt(obj2));
        assertEquals("obj1.y", 6, invocationClass.getField("y").getInt(obj1));
        assertEquals("obj2.y", 0, invocationClass.getField("y").getInt(obj2));
        assertNull("obj1.obj", invocationClass.getField("obj").get(obj1));
        assertEquals("ExampleMethodsTest.instance", obj1, ExampleMethodsTest.getInstance());
    }
    
    public interface Binary
    {
        long binary(long a, long b);
    }
    
    public class MultiplyMaker extends ClassMakerBase
    {
        public void code()
        {
            Implements(Binary.class);

            // BEGIN - Declaring Methods example 1
            Method("binary", long.class, ACC_PUBLIC);
            Declare("x", long.class, 0);
            Declare("y", long.class, 0);
            Begin();
                Return(Mult(Get("x"), Get("y")));
            End();
            // BEGIN - Declaring Methods example 1
        }
    }

    public void testBinaryMultiply() throws Exception
    {
		ClassMakerBase.setSharedFactory(null);
        ClassMaker maker = new MultiplyMaker();
        Class multiplyClass = maker.defineClass();
        Binary exec = (Binary)multiplyClass.newInstance();
        
        assertEquals("Multiply", 6, exec.binary(2, 3));
        assertEquals("Multiply", -4, exec.binary(-2, 2));
        assertEquals("Multiply", 1, exec.binary(-1, -1));
    }
    
    public interface Linkable
    {
        Linkable find(String target);
        Linkable push(Linkable top, String value);
    }
    
    public static class LinkMaker extends ClassMakerBase
    {
        public static final String linkName = "au.com.illyrian.classmaker.ExampleMethodsTest$Link";
        public void code()
        {
            Implements(Linkable.class);

            // BEGIN - Default Constructor example
            Method("<init>", void.class, ACC_PUBLIC);
            Begin();
                Init(Super(), null);
                Return();
            End();
            // END - Default Constructor example

            // BEGIN - Recursive data structures
            Declare("name", String.class, 0);
            Declare("next", getClassType(), 0);

            Method("find", getClassType(), ACC_PUBLIC);
                Declare("target", String.class, 0);
            Begin();
                If(Call(Get("target"), "equals", Push(Get(This(), "name"))));
                    Return(This());
                Else();
                    Return(Call(Get(This(), "next"), "find", Push(Get("target"))));
                EndIf();
            End();
            // END - Recursive data structures
            
            Method("find", Linkable.class, ACC_PUBLIC);
            Declare("target", String.class, 0);
            Begin();
                Return(Cast(Call(This(), "find", Push(Get("target"))), Linkable.class));
            End();
            
            Method("push", Linkable.class, ACC_PUBLIC);
            Declare("top", Linkable.class, 0);
            Declare("value", String.class, 0);
            Begin();
                Eval(Set(This(), "next", Cast(Get("top"), linkName)));
                Eval(Set(This(), "name", Get("value")));
                Return(This());
            End();            
        }
    }
    
    public void testLink() throws Exception
    {
        ClassMakerBase.setSharedFactory(null);
        ClassMaker maker = new LinkMaker();
        Class linkClass = maker.defineClass();
        
        Linkable top = null;
        Linkable alpha = (Linkable)linkClass.newInstance();
        top = alpha.push(top, "alpha");
        
        Linkable beta = (Linkable)linkClass.newInstance();
        top = beta.push(top, "beta");
        
        assertEquals("Find alpha", alpha, top.find("alpha"));
        assertEquals("Find beta", beta, top.find("beta"));
    }

    public interface Evaluate
    {
        int eval();
    }
    
    public void testForwardDeclaration() throws Exception
    {
		ClassMakerBase.setSharedFactory(null);
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "Unary", null);
        maker.Implements(Evaluate.class);

        // BEGIN - Forward Declarations
        maker.Method("add", int.class, ClassMaker.ACC_PRIVATE);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Forward();

        maker.Method("eval", int.class, ClassMaker.ACC_PUBLIC);
        maker.Begin();
          maker.Return(maker.Call(maker.This(), "add", maker.Push(maker.Literal(1)).Push(maker.Literal(2))));
        maker.End();

        maker.Method("add", int.class, ClassMaker.ACC_PRIVATE);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Begin();
          maker.Return(maker.Add(maker.Get("a"), maker.Get("b")));
        maker.End();
        // END - Forward Declarations
        
        Class testClass = maker.defineClass();
        Evaluate exec = (Evaluate)testClass.newInstance();
        assertEquals("Forward declare Add", 3, exec.eval());
    }
}
