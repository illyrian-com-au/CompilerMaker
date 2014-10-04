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

import au.com.illyrian.classmaker.ClassMaker.Labelled;

public class MakerSwitchTest extends ClassMakerTestCase
{
    protected ClassMaker maker;
    ClassMakerFactory factory;

    protected int lineNo = 0;

    public void nl(int line)
    {
        lineNo = line;
        maker.setLineNumber(line);
    }

    public void nl()
    {
        maker.setLineNumber(++lineNo);
    }

    public interface Unary
    {
        int unary(int a);
    }

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("MyClass", Object.class, "au/com/illyrian/classmaker/MakerSwitchTest.java");
        defaultConstructor();
    }

    // Generate default constructor
    public void defaultConstructor()
    {
nl(65);      maker.Method("<init>", ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC);
nl();        maker.Begin();
nl();        maker.Init(maker.Super(), null);
nl();        maker.Return();
nl();        maker.End();
    }

    public void testTableSwitchMethod() throws Exception
    {
nl(74);   maker.Implements(Unary.class);
nl();     maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();     maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();     maker.Begin();
nl();     {
nl();         maker.Declare("y", ClassMaker.INT_TYPE, 0);
nl();         maker.Set("y", maker.Literal(0));
nl();         maker.Switch(maker.Get("x"));
nl();         {
nl();             maker.Case(0);
nl();                 maker.Set("y", maker.Literal(1));
nl();                 maker.Break();
nl();             maker.Case(1);
nl();                 maker.Set("y", maker.Literal(2));
nl();                 maker.Break();
nl();             maker.Case(2);
nl();                 maker.Set("y", maker.Literal(3));
nl();                 maker.Break();
nl();             maker.Default();
nl();                 maker.Set("y", maker.Literal(0));
nl();                 maker.Break();
nl();        }
nl();        maker.EndSwitch();
nl();        maker.Return(maker.Get("y"));
nl();   }
nl();   maker.End();

//        byte[] code = cfw.getCodeAttribute();
//        ClassFilePrinter printer = new ClassFilePrinter(System.out);
//        printer.byteCode(code);

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(30));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(3));
    }

    public void testLookupSwitchMethod() throws Exception
    {
nl(117);   maker.Implements(Unary.class);
nl();     maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();     maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();     maker.Begin();
nl();     {
nl();         maker.Declare("y", ClassMaker.INT_TYPE, 0);
nl();         maker.Set("y", maker.Literal(0));
nl();         maker.Switch(maker.Get("x"));
nl();         {
nl();             maker.Case(0);
nl();                 maker.Set("y", maker.Literal(1));
nl();                 maker.Break();
nl();             maker.Case(4);
nl();                 maker.Set("y", maker.Literal(3));
nl();                 maker.Break();
nl();             maker.Case(2);
nl();                 maker.Set("y", maker.Literal(2));
nl();                 maker.Break();
nl();             maker.Case(6);
nl();                 maker.Set("y", maker.Literal(4));
nl();                 maker.Break();
nl();             maker.Default();
nl();                 maker.Set("y", maker.Literal(0));
nl();                 maker.Break();
nl();        }
nl();        maker.EndSwitch();
nl();        maker.Return(maker.Get("y"));
nl();   }
nl();   maker.End();

//        byte[] code = cfw.getCodeAttribute();
//        ClassFilePrinter printer = new ClassFilePrinter(System.out);
//        printer.byteCode(code);

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(-1));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 4, exec.unary(6));
    }

    public void testOuterBreak() throws Exception
    {
        maker.Implements(Runnable.class);
        
        maker.Method("run", void.class, ClassMaker.ACC_PUBLIC);
        maker.Begin();
	        maker.Begin().setLabel("outer");
		        maker.Break("outer");
	        maker.End();
        maker.End();

        Class myClass = maker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
    }

    public interface BreakContinueIface {
    	int breakContinue(int i);
    }
    
//    public class BreakContinueExampleMaker extends ClassMakerBase
//    {
//    	public String getSourceFilename()
//    	{
//    		return "au/com/illyrian/classmaker/MakerSwitchTest.java";
//    	}
//    	
//        public void code()
//        {
//        	Implements(BreakContinueIface.class);
//            
//            Method("breakContinue", int.class, ACC_PUBLIC);
//            Declare("i", int.class, 0);
//nl(198);    Begin();
//nl();       	Declare("n", int.class, 0);
//nl();       	Set("n", Literal(0));
//nl();       	Declare("j", int.class, 0);
//nl();           // BEGIN - If statement example 6b
//nl();       	Begin().setLabel("outer");
//nl();       	    Try().setLabel("trying");
//nl();           	    For(Set("j", Literal(1))).While(LE(Get("j"), Literal(3))).Step(Inc("j")).setLabel("loop");
//nl();           	        If(NE(Get("i"), Literal(0))).setLabel("branch");
//nl();	        	    		Switch(Get("i")).setLabel("switch");
//nl();	        	    		Case(1);
//nl();	        	    			Break();
//nl();	        	    		Case(2);
//nl();	        	    			Continue();
//nl();	        	    		Case(3);
//nl();	        	    			Break("loop");
//nl();	        	    		Case(4);
//nl();	        	    			Continue("loop");
//nl();	          	    		Case(5);
//nl();	        	    			Break("switch");
//nl();	        	    		Case(6);
//nl();	        	    			Break("trying");
//nl();	        	    		Case(7);
//nl();	        	    			Break("outer");
//nl();	        	    	//	Case(8);
//nl();	        	    	//	    Break("branch");
//nl();	        	    		EndSwitch();
//nl();	        	    		Eval(Inc("n"));
//nl();	        	    	Else();
//nl();  		        	    	Eval(Set("n", Literal(100)));
//nl();	        	    	EndIf();
//nl();       	    	EndFor();
//nl();       	    	Eval(Set("n", Add(Get("n"), Get("i"))));
//nl();   	    	Finally();
//nl();   	    		Eval(Inc("n"));
//nl();   	    	EndTry();
//nl();       	End();
//nl();           // END - If statement example 6b
//nl();       	Return(Get("n"));
//nl();       End();
//        }
//    }
    
    public void testBreakContinueLoop() throws Exception
    {
    	maker.Implements(BreakContinueIface.class);
        
    	maker.Method("breakContinue", int.class, ClassMaker.ACC_PUBLIC);
    	maker.Declare("i", int.class, 0);
nl(247);    maker.Begin();
nl();       	maker.Declare("n", int.class, 0);
nl();       	maker.Set("n", maker.Literal(0));
nl();       	maker.Declare("j", int.class, 0);
nl();           // BEGIN - If statement example 6b
nl();       	maker.Begin().setLabel("outer");
nl();       	    maker.Try().setLabel("trying");
nl();           	    maker.For(maker.Set("j", maker.Literal(1))).While(maker.LE(maker.Get("j"), maker.Literal(3))).Step(maker.Inc("j")).setLabel("loop");
nl();           	        maker.If(maker.NE(maker.Get("i"), maker.Literal(0))).setLabel("branch");
nl();	        	    		maker.Switch(maker.Get("i")).setLabel("switch");
nl();	        	    		maker.Case(1);
nl();	        	    			maker.Break();
nl();	        	    		maker.Case(2);
nl();	        	    			maker.Continue();
nl();	        	    		maker.Case(3);
nl();	        	    			maker.Break("loop");
nl();	        	    		maker.Case(4);
nl();	        	    			maker.Continue("loop");
nl();	          	    		maker.Case(5);
nl();	        	    			maker.Break("switch");
nl();	        	    		maker.Case(6);
nl();	        	    			maker.Break("trying");
nl();	        	    		maker.Case(7);
nl();	        	    			maker.Break("outer");
nl();	        	    		maker.Case(8);
nl();	        	    		    maker.Break("branch");
nl();	        	    		maker.EndSwitch();
nl();	        	    		maker.Eval(maker.Inc("n"));
nl();	        	    	maker.Else();
nl();  		        	    	maker.Eval(maker.Set("n", maker.Literal(100)));
nl();	        	    	maker.EndIf();
nl();       	    	maker.EndFor();
nl();       	    	maker.Eval(maker.Set("n", maker.Add(maker.Get("n"), maker.Get("i"))));
nl();   	    	maker.Finally();
nl();   	    		maker.Eval(maker.Inc("n"));
nl();   	    	maker.EndTry();
nl();       	maker.End();
nl();       	maker.Return(maker.Get("n"));
nl();       maker.End();

        Class testClass = maker.defineClass();
        BreakContinueIface exec = (BreakContinueIface)testClass.newInstance();

        assertEquals("Else", 101, exec.breakContinue(0));
    	assertEquals("Break", 5, exec.breakContinue(1));
    	assertEquals("Continue", 3, exec.breakContinue(2));
    	assertEquals("Break loop", 4, exec.breakContinue(3));
    	assertEquals("Continue loop", 5, exec.breakContinue(4));
    	assertEquals("Break switch", 9, exec.breakContinue(5));
    	assertEquals("Break trying", 1, exec.breakContinue(6));
    	assertEquals("Break outer", 0, exec.breakContinue(7));        
    	assertEquals("Break branch", 9, exec.breakContinue(8)); // **
    	assertEquals("Default", 14, exec.breakContinue(10));        
    }

    public void testSwitchExceptions() throws Exception
    {
        maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("x", ClassMaker.INT_TYPE, 0);
        Labelled labelled = maker.Begin();
        try {
            labelled.setLabel("test");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot set a label on the Begin at the start of a method", ex.getMessage());
        }
        try {
            maker.Case(1);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Case while not in a Switch statement", ex.getMessage());
        }
        try {
            maker.Default();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Default while not in a Switch statement", ex.getMessage());
        }
        try {
            maker.Break();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Break while not in a Loop or Switch statement", ex.getMessage());
        }
        try {
            maker.Break("test");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Could not find target label for Break statement: test", ex.getMessage());
        }
        try {
            maker.EndSwitch();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "EndSwitch while not in a Switch statement", ex.getMessage());
        }

        maker.Declare("l", ClassMaker.LONG_TYPE, 0);
        try {
            maker.Switch(maker.Get("l"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Switch type must be int, char, short or byte but was long", ex.getMessage());
        }

        maker.Switch(maker.Get("x"));
        try {
            maker.EndSwitch();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "No Case clause in Switch statement", ex.getMessage());
        }

        maker.Case(1);
        try {
            maker.Case(1);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Duplicate Case key in Switch statement: 1", ex.getMessage());
        }

        maker.Default();
        try {
            maker.Default();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "More than one Default in Switch statement", ex.getMessage());
        }
    }
}

