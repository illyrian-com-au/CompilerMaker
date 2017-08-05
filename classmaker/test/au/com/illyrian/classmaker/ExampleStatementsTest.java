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

public class ExampleStatementsTest extends TestCase
{
    public interface StatementIface
    {
        int example1(int a);
        int example2(int a);
        int example3(int n);
        int example4(int n);
        int example5(int n);
    }

    int example1(int a)
    {
        // BEGIN - If statement example 1a
        if (a<0) {
            a = 0;
        }
        // END - If statement example 1a
        return a;
    }
    
    int example2(int a)
    {
        // BEGIN - If statement example 2a
        if (a<0) {
            a = 0;
        } else {
            a = 1;
        }
        // END - If statement example 2a
        return a;
    }
    
    int example3(int n)
    {
    	int x;
        // BEGIN - If statement example 3a
        x = 1;
        while (n>0)
        {
            x = x * n;
            --n;
        }
        // END - If statement example 3a
        return x;
    }
    
    int example4(int n)
    {
    	int x;
        // BEGIN - break/continue statement example 4a
        // Sum all values up to n.
        x = 0;
        while (true) {
            if (n<=0) {
                break;
            }
            x = x + n;
            --n;
        }
        // END - If statement example 4a
        return x;
    }
    
    int example5(int n)
    {
    	int x, y;
        // BEGIN - If statement example 5a
        // Sum all values up to n, except multiples of 2
        x = 0;
        while (true) {
            y = n--;
            if (y<=0) {
                break;
            }
            if ((y % 2) == 0) {
               continue;
            }
            x = x + y;
        }
        // END - If statement example 5a
        return x;
    }
    
    public class StatementExampleMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(StatementIface.class);
            
            Method("example1", int.class, ACC_PUBLIC);
            Declare("a", int.class, 0);
            Begin();
                // BEGIN - If statement example 1b
                If(LT(Get("a"), Literal(0)));
                    Set("a", Literal(0));
                EndIf();
                // END - If statement example 1b
                Return(Get("a"));
            End();

            Method("example2", int.class, ACC_PUBLIC);
            Declare("a", int.class, 0);
            Begin();
                // BEGIN - If statement example 2b
                If(LT(Get("a"), Literal(0)));
                    Set("a", Literal(0));
                Else();
                    Set("a", Literal(1));
                EndIf();
                // END - If statement example 2b
                Return(Get("a"));
            End();

            Method("example3", int.class, ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
                Declare("x", int.class, 0);
                // BEGIN - While statement example 3b
	            Eval(Set("x", Literal(1)));
	            Loop();
	                While(GT(Get("n"), Literal(0)));
	                Eval(Set("x", Mult(Get("x"), Get("n"))));
	                Eval(Dec("n"));
	            EndLoop();
                // END - While statement example 3b
                Return(Get("x"));
            End();

            Method("example4", int.class, ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
            {
            	Declare("x", int.class, 0);
                // BEGIN - Break statement example 4b
	            Eval(Set("x", Literal(0)));
	            Loop();
	                If(LE(Get("n"), Literal(0)));
	                    Break();
	                EndIf();
	                Eval(Set("x", Add(Get("x"), Get("n"))));
	                Eval(Dec("n"));
	            EndLoop();
                // END - Break statement example 4b
                Return(Get("x"));
            }
            End();

            Method("example5", int.class, ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
            	Declare("x", int.class, 0);
            	Declare("y", int.class, 0);
                // BEGIN - If statement example 5b
	            Eval(Set("x", Literal(0)));
	            Loop();
	                Eval(Set("y", PostDec("n")));
	                If(LE(Get("y"), Literal(0)));
	                    Break();
	                EndIf();
	                If(EQ(Rem(Get("y"), Literal(2)), Literal(0)));
	                    Continue();
	                EndIf();
	                Eval(Set("x", Add(Get("x"), Get("y"))));
	            EndLoop();
                // END - If statement example 5b
                Return(Get("x"));
            End();

        }
    }

    public void testStatements() throws Exception
    {
        ClassMaker maker = new StatementExampleMaker();
        Class testClass = maker.defineClass();
        StatementIface exec = (StatementIface)testClass.newInstance();
        
        assertEquals("example1", example1(1), exec.example1(1));
        assertEquals("example1", example1(0), exec.example1(0));
        assertEquals("example1", example1(-1), exec.example1(-1));
        
        assertEquals("example2", example2(1), exec.example2(1));
        assertEquals("example2", example2(0), exec.example2(0));
        assertEquals("example2", example2(-1), exec.example2(-1));
        
        assertEquals("example3", example3(1), exec.example3(1));
        assertEquals("example3", example3(0), exec.example3(0));
        assertEquals("example3", example3(-1), exec.example3(-1));
        
        assertEquals("example4", 1, example4(1));
        assertEquals("example4", 3, example4(2));
        assertEquals("example4", 6, example4(3));
        assertEquals("example4", 10, example4(4));

        assertEquals("example4", example4(1), exec.example4(1));
        assertEquals("example4", example4(2), exec.example4(2));
        assertEquals("example4", example4(3), exec.example4(3));
        assertEquals("example4", example4(4), exec.example4(4));

        assertEquals("example5", 1, example5(1));
        assertEquals("example5", 1, example5(2));
        assertEquals("example5", 4, example5(3));
        assertEquals("example5", 4, example5(4));
        assertEquals("example5", 9, example5(5));
        assertEquals("example5", 9, example5(6));

        assertEquals("example5", example5(1), exec.example5(1));
        assertEquals("example5", example5(2), exec.example5(2));
        assertEquals("example5", example5(3), exec.example5(3));
        assertEquals("example5", example5(4), exec.example5(4));
        assertEquals("example5", example5(5), exec.example5(5));
        assertEquals("example5", example5(6), exec.example5(6));
    }

    public char find(char[][] phrase, int n)
    {
    	int i = 0;
		char result = 0;
    	found: while (i < phrase.length)
    	{
    		int j = 0;
    		char[] word = phrase[i];
    		while (j < word.length)
    		{
    			if (n<=0)
    			{
    				result = word[j];
    				break found;
    			}
        		n--;
    			j++;
    		}
    		i++;
    	}
		return result;
    }

    char [] [] testPhrase = {
    		"The".toCharArray(),
    		"quick".toCharArray(),
    		"brown".toCharArray(),
    		"fox".toCharArray() };
    
    public void testBreakStatement()
    {
    	assertEquals("Break", 'T', find(testPhrase, 0));
    	assertEquals("Break", 'e', find(testPhrase, 2));
    	assertEquals("Break", 'q', find(testPhrase, 3));
    	assertEquals("Break", 'k', find(testPhrase, 7));
    	assertEquals("Break", 'f', find(testPhrase, 13));
    	assertEquals("Break", 'x', find(testPhrase, 15));
    	assertEquals("Break", 0, find(testPhrase, 16));
    }
    
    public interface BreakContinueIface {
    	int breakContinue(int i);
    }
    
    public class BreakContinueExampleMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(BreakContinueIface.class);
            
            Method("breakContinue", int.class, ACC_PUBLIC);
            Declare("i", int.class, 0);
            Begin();
            	Declare("n", int.class, 0);
            	Set("n", Literal(0));
            	Declare("j", int.class, 0);
                // BEGIN - Statements example 6b
            	Begin().setLabel("outer");
            	    For(Set("j", Literal(1))).While(LE(Get("j"), Literal(3))).Step(Inc("j")).setLabel("loop");
        	    		Switch(Get("i"));
        	    		Case(1);
        	    			Break();
        	    		Case(2);
        	    			Continue();
        	    		Case(3);
        	    			Break("loop");
        	    		Case(4);
        	    			Continue("loop");
        	    		Case(5);
        	    			Break("outer");
        	    		EndSwitch();
        	    		Eval(Inc("n"));
        	    	EndFor();
            	End();
                // END - Statements example 6b
            	Return(Add(Get("n"), Get("j")));
            End();
        }
    }
    
    public int breakContinue(int i)
    {
    	int n = 0;
    	int j;
        // BEGIN - Statements example 6a
    	outer: {
	    	loop:  for (j=1; j<=3; j++) 
	    	{
	    		switch (i) {
	    		case 1:
	    			break;
	    		case 2:
	    			continue;
	    		case 3:
	    			break loop;
	    		case 4:
	    			continue loop;
	    		case 5:
	    			break outer;
	    		}
	    		n++;
	    	}
    	}
        // END - Statements example 6a
    	return n + j;
    }
    
    public void testBreakContinueLoop() throws Exception
    {
    	assertEquals("Break/Continue", 7, breakContinue(0));
    	assertEquals("Break/Continue", 7, breakContinue(1));
    	assertEquals("Break/Continue", 4, breakContinue(2));
    	assertEquals("Break/Continue", 1, breakContinue(3));
    	assertEquals("Break/Continue", 4, breakContinue(4));
    	assertEquals("Break/Continue", 1, breakContinue(5));
    	assertEquals("Break/Continue", 7, breakContinue(6));

    	ClassMaker maker = new BreakContinueExampleMaker();
        Class testClass = maker.defineClass();
        BreakContinueIface exec = (BreakContinueIface)testClass.newInstance();

        assertEquals("Break/Continue", 7, exec.breakContinue(0));
    	assertEquals("Break/Continue", 7, exec.breakContinue(1));
    	assertEquals("Break/Continue", 4, exec.breakContinue(2));
    	assertEquals("Break/Continue", 1, exec.breakContinue(3));
    	assertEquals("Break/Continue", 4, exec.breakContinue(4));
    	assertEquals("Break/Continue", 1, exec.breakContinue(5));
    	assertEquals("Break/Continue", 7, exec.breakContinue(6));
        
    }

    public interface BeginEndIface {
    	int block(int i);
    }
    
    public class BeginEndExampleMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(BeginEndIface.class);
            
            Method("block", int.class, ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
                // BEGIN - Statements example 0b
            	If(GT(Get("n"), Literal(0)));
            	    Begin();
                    	Declare("a", int.class, 0);
                	    Set("a", Inc("n"));
            	    End();
            	Else();
            	    Begin();
                	    Declare("a", char.class, 0);
                	    Set("a", Literal('0'));
                	    Set("n", Subt(Get("a"), Get("n")));
                	End();
            	EndIf();
                // END - Statements example 0b
            	Return(Get("n"));
            End();
        }
    }
    
    public int block(int n)
    {
        // BEGIN - Statements example 0a
    	if (n > 0)
    	{
    		int a;
    		a = n++;
    	}
    	else
    	{
    		char a;
    		a = '0';
    		n = a - n;
    	}
        // END - Statements example 0a
    	return n;
    }
    
    public void testBeginEnd() throws Exception
    {
    	assertEquals("block", '9', block(-9));
    	assertEquals("block", '2', block(-2));
    	assertEquals("block", '1', block(-1));
    	assertEquals("block", '0', block(0));
    	assertEquals("block", 2, block(1));
    	assertEquals("block", 3, block(2));
    	assertEquals("block", 100, block(99));

    	ClassMaker maker = new BeginEndExampleMaker();
        Class testClass = maker.defineClass();
        BeginEndIface exec = (BeginEndIface)testClass.newInstance();

    	assertEquals("block", '9', exec.block(-9));
    	assertEquals("block", '2', exec.block(-2));
    	assertEquals("block", '1', exec.block(-1));
    	assertEquals("block", '0', exec.block(0));
    	assertEquals("block",   2, exec.block(1));
    	assertEquals("block",   3, exec.block(2));
    	assertEquals("block", 100, exec.block(99));
    }
}
