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

import java.io.FileNotFoundException;

public class MakerTryCatchTest extends ClassMakerTestCase
{
    protected ClassMaker maker;
    protected ClassMakerFactory factory;
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

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("MyClass", Object.class, "au/com/illyrian/classmaker/MakerTryCatchTest.java");
        defaultConstructor();
    }

    // Generate default constructor
    public void defaultConstructor()
    {   nl(38);
        maker.Method("<init>", ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC); nl();
        maker.Begin();                      nl();
          maker.Init(maker.Super(), null);  nl();
          maker.Return();                   nl();
        maker.End();                        nl();
    }

    public interface Eval
    {
        int eval();
    }

    public interface Unary
    {
        int unary(int a);
    }

    public interface UnaryChecked
    {
        int unary(int a) throws FileNotFoundException;
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
    }

    public static final String ILLEGAL_ARGUMENT_EXCEPTION = "java/lang/IllegalArgumentException";

    public void testThrowUncheckedException() throws Exception
    {
nl(93);
nl();   maker.Implements(Unary.class);
nl();   maker.Import(ILLEGAL_ARGUMENT_EXCEPTION);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       // IllegalArgumentException ex;
nl();       maker.Declare("ex", ILLEGAL_ARGUMENT_EXCEPTION, 0);
nl();       // ex = new IllegalArgumentException("Too big string");
nl();       maker.Eval(maker.Set("ex", maker.New(ILLEGAL_ARGUMENT_EXCEPTION).Init(maker.Push(maker.Literal("Too big string.")))));
nl();       // throw ex;
nl();       maker.Throw(maker.Get("ex"));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        try {
            exec.unary(0);
            fail("Expected IllegalArgumentException().");
        } catch (IllegalArgumentException ex) {};
    }

    public void testThrowCheckedException() throws Exception
    {
nl(120);
nl();   maker.Implements(UnaryChecked.class);
nl();   maker.Import(FILE_NOT_FOUND_EXCEPTION);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       // IllegalArgumentException ex;
nl();       maker.Declare("ex", FILE_NOT_FOUND_EXCEPTION, 0);
nl();       // ex = new IllegalArgumentException("File not there.");
nl();       maker.Set("ex", maker.New(FILE_NOT_FOUND_EXCEPTION).Init(maker.Push(maker.Literal("File not there."))));
nl();       // throw ex;
nl();       maker.Throw(maker.Get("ex"));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        try {
            exec.unary(0);
            fail("Expected FileNotFoundException().");
        } catch (FileNotFoundException ex) {};
    }

    public static class ThrowClass implements UnaryChecked
    {
        public int unary(int a) throws FileNotFoundException
        {
            switch (a)
            {
            case 1 :
                throw new IllegalArgumentException();
            case 2 :
                throw new FileNotFoundException();
            case 3 :
                throw new IllegalStateException();
            }
            return 0;
        }
    }

    public void testThrowException() throws Exception
    {
        maker.Implements(UnaryChecked.class);

        maker.Method("unary", int.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", int.class, 0);
        maker.Begin();
        {
            // SET b = 0
            maker.Declare("b", int.class, 0);
            maker.Set("b", maker.Literal(0));
            maker.Declare("ex", Exception.class, 0);
    
            maker.Switch(maker.Get("a"));
            {
                //CASE 1 : throw new IllegalArgumentException();
                maker.Case(0);
                maker.Eval(maker.Set("ex", maker.New(IllegalArgumentException.class).Init(maker.Push(maker.Literal("Too big string")))));
                maker.Throw(maker.Get("ex"));
                maker.Break();
                
                maker.Case(1);
                maker.Eval(maker.Set("ex", maker.New(FileNotFoundException.class).Init(maker.Push(maker.Literal("Its not there!")))));
                maker.Throw(maker.Get("ex"));
                maker.Break();
    
                maker.Case(2);
                maker.Eval(maker.Set("ex", maker.New(IllegalStateException.class).Init(maker.Push(maker.Literal("Bad stuff")))));
                maker.Throw(maker.Get("ex"));
                maker.Break();
                
                maker.Default();
                maker.Set("b", maker.Literal(2));
                maker.Break();
            }
            maker.EndSwitch();
            maker.Return(maker.Get("b"));
        }
        maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 2, exec.unary(10));
        try {
            exec.unary(0);
            fail("Expected IllegalArgumentException().");
        } catch (IllegalArgumentException ex) {};
        try {
            exec.unary(1);
            fail("Expected FileNotFoundException().");
        } catch (FileNotFoundException ex) {};
        try {
            exec.unary(2);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {};
    }

    public static final String UNARY_CHECKED = "au/com/illyrian/classmaker/MakerTryCatchTest$UnaryChecked";
    public static final String UNARY_CHECKED_S = "Lau/com/illyrian/classmaker/MakerTryCatchTest$UnaryChecked;";

    public void testCatchNoExceptions() throws Exception
    {
nl(262);
nl();   maker.Implements(UnaryChecked.class);
nl();   maker.Import(UNARY_CHECKED);
nl();   maker.Declare("func", UNARY_CHECKED, ClassMaker.ACC_PUBLIC);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       // return this.func.unary(x);
nl();       maker.Return(maker.Call(maker.Get(maker.This(), "func"), "unary", maker.Push(maker.Get("x"))));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        try {
            exec.unary(1);
            fail("Expected IllegalArgumentException().");
        } catch (IllegalArgumentException ex) {};
        try {
            exec.unary(2);
            fail("Expected FileNotFoundException().");
        } catch (FileNotFoundException ex) {};
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {};
    }

    public static final String FILE_NOT_FOUND_EXCEPTION = "java/io/FileNotFoundException";
    public static final String ILLEGAL_ARGUEMENT_EXCEPTION = "java/lang/IllegalArgumentException";
    public static final String ILLEGAL_STATE_EXCEPTION = "java/lang/IllegalStateException";

    public void testCatchOneException() throws Exception
    {
nl(302);
nl();   maker.Implements(UnaryChecked.class);
nl();   maker.Import(UNARY_CHECKED);
nl();   maker.Import(FILE_NOT_FOUND_EXCEPTION);
nl();   maker.Declare("func", UNARY_CHECKED, ClassMaker.ACC_PUBLIC);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       maker.Try();
nl();       {
nl();           maker.Eval(maker.Set("x", (maker.Call(maker.Get(maker.This(), "func"), "unary", maker.Push(maker.Get("x"))))));
nl();       }
nl();       maker.Catch(FileNotFoundException.class, "ex1");
nl();       {
nl();           maker.Eval(maker.Set("x", maker.Literal(10000)));
nl();        }
nl();       maker.EndTry();
nl();       maker.Return(maker.Get("x"));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("FileNotFoundException was not caught.", 10000, exec.unary(2));
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {};
    }

    public void testCatchExceptions() throws Exception
    {
nl(340);
nl();   maker.Implements(UnaryChecked.class);
nl();   maker.Import(UNARY_CHECKED);
nl();   maker.Import(FILE_NOT_FOUND_EXCEPTION);
nl();   maker.Import(ILLEGAL_ARGUMENT_EXCEPTION);
nl();   maker.Declare("func", UNARY_CHECKED, ClassMaker.ACC_PUBLIC);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       maker.Try();
nl();       {
nl();           maker.Eval(maker.Set("x", (maker.Call(maker.Get(maker.This(), "func"), "unary", maker.Push(maker.Get("x"))))));
nl();       }
nl();       maker.Catch(FileNotFoundException.class, "ex1");
nl();       {
nl();           maker.Eval(maker.Set("x", maker.Literal(10000)));
nl();        }
nl();       maker.Catch(IllegalArgumentException.class, "ex2");
nl();       {
nl();           maker.Eval(maker.Set("x", maker.Literal(20000)));
nl();        }
nl();       maker.EndTry();
nl();       maker.Return(maker.Get("x"));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 20000, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 10000, exec.unary(2));
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {};
    }

    public void testFinally() throws Exception
    {
nl(384);
nl();   maker.Implements(UnaryChecked.class);
nl();   maker.Import(UNARY_CHECKED);
nl();   maker.Declare("func", UNARY_CHECKED, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("val", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       maker.Try();
nl();       {
nl();           maker.Eval(maker.Set("x", (maker.Call(maker.Get(maker.This(), "func"), "unary", maker.Push(maker.Get("x"))))));
nl();       }
nl();       maker.Finally();
nl();       {
nl();           maker.Inc(maker.This(), "val");
nl();       }
nl();       maker.EndTry();
nl();       maker.Return(maker.Get("x"));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        int count = 100;
        setIntField(exec.getClass(), exec, "val", count);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex)
        {
            assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        };
    }

    public void testCatchFinally() throws Exception
    {
nl(427);
nl();   maker.Implements(UnaryChecked.class);
nl();   maker.Import(UNARY_CHECKED);
nl();   maker.Import(FILE_NOT_FOUND_EXCEPTION);
nl();   maker.Declare("func", UNARY_CHECKED, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("val", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       maker.Try();
nl();       {
nl();           maker.Eval(maker.Set("x", (maker.Call(maker.Get(maker.This(), "func"), "unary", maker.Push(maker.Get("x"))))));
nl();       }
nl();       maker.Catch(FILE_NOT_FOUND_EXCEPTION, "ex1");
nl();       {
nl();           maker.Eval(maker.Set("x", maker.Literal(10000)));
nl();        }
nl();       maker.Finally();
nl();       {
nl();           maker.Eval(maker.Inc(maker.This(), "val"));
nl();       }
nl();       maker.EndTry();
nl();       maker.Return(maker.Get("x"));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        int count = 100;
        setIntField(exec.getClass(), exec, "val", count);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        try {
            exec.unary(1);
            fail("Expected IllegalArgumentException().");
        } catch (IllegalArgumentException ex)
        {
            assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        };
        assertEquals("FileNotFoundException was not caught.", 10000, exec.unary(2));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex)
        {
            assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        };
    }

    public void testCatch2Finally() throws Exception
    {
nl(484);
nl();   maker.Implements(UnaryChecked.class);
nl();   maker.Import(UNARY_CHECKED);
nl();   maker.Import(FILE_NOT_FOUND_EXCEPTION);
nl();   maker.Import(ILLEGAL_ARGUMENT_EXCEPTION);
nl();   maker.Declare("func", UNARY_CHECKED, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("val", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       maker.Try();
nl();       {
nl();           maker.Eval(maker.Set("x", (maker.Call(maker.Get(maker.This(), "func"), "unary", maker.Push(maker.Get("x"))))));
nl();       }
nl();       maker.Catch(FILE_NOT_FOUND_EXCEPTION, "ex1");
nl();       {
nl();           maker.Eval(maker.Set("x", maker.Literal(10000)));
nl();        }
nl();       maker.Catch(IllegalArgumentException.class, "ex2");
nl();       {
nl();           maker.Eval(maker.Set("x", maker.Literal(20000)));
nl();        }
nl();       maker.Finally();
nl();       {
nl();           maker.Inc(maker.This(), "val");
nl();       }
nl();       maker.EndTry();
nl();       maker.Return(maker.Get("x"));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        int count = 100;
        setIntField(exec.getClass(), exec, "val", count);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        assertEquals("IllegalArgumentException was not caught.", 20000, exec.unary(1));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        assertEquals("FileNotFoundException was not caught.", 10000, exec.unary(2));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex)
        {
            assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        };
    }

    public void testCatchThrowFinally() throws Exception
    {
nl(541);
nl();   maker.Implements(UnaryChecked.class);
nl();   maker.Import(UNARY_CHECKED);
nl();   maker.Import(FILE_NOT_FOUND_EXCEPTION);
nl();   maker.Import(ILLEGAL_ARGUMENT_EXCEPTION);
nl();   maker.Declare("func", UNARY_CHECKED, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("val", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Method("unary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
nl();   maker.Declare("x", ClassMaker.INT_TYPE, 0);
nl();   maker.Begin();
nl();   {
nl();       maker.Try();
nl();       {
nl();           maker.Eval(maker.Set("x", (maker.Call(maker.Get(maker.This(), "func"), "unary", maker.Push(maker.Get("x"))))));
nl();       }
nl();       maker.Catch(FILE_NOT_FOUND_EXCEPTION, "ex1");
nl();       {
nl();           maker.Eval(maker.Set("x", maker.Literal(10000)));
nl();        }
nl();       maker.Catch(ILLEGAL_ARGUMENT_EXCEPTION, "ex2");
nl();       {
nl();           maker.Eval(maker.Set(maker.This(), "val", maker.Literal(1000)));
nl();           maker.Throw(maker.Get("ex2"));
nl();        }
nl();       maker.Finally();
nl();       {
nl();           maker.Inc(maker.This(), "val");
nl();       }
nl();       maker.EndTry();
nl();       maker.Return(maker.Get("x"));
nl();   }
nl();   maker.End();

        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        ThrowClass test = new ThrowClass();
        setField(exec.getClass(), exec, "func", test);

        int count = 100;
        setIntField(exec.getClass(), exec, "val", count);
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        try {
            exec.unary(1);
            fail("Expected IllegalArgumentException().");
        } catch (IllegalArgumentException ex) {
            // exec.val was set to 1000 by the catch block
            count = 1000;
            // then incremented by the finaly block
            assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        };
        assertEquals("FileNotFoundException was not caught.", 10000, exec.unary(2));
        assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        try {
            exec.unary(3);
            fail("Expected IllegalStateException().");
        } catch (IllegalStateException ex) {
            assertEquals(++count, getIntField(exec.getClass(), exec, "val"));
        };
    }

    public class TryCatchFinallyMaker extends ClassMakerBase
    {
        public void code()
        {
            Implements(UnaryChecked.class);
            Import(FileNotFoundException.class);
            Method("unary", int.class, ClassMaker.ACC_PUBLIC);
            Declare("x", int.class, 0);
            Begin();
            {
                Try();
                {
                    If(LT(Get("x"), Literal(0)));
                        Throw(New(FileNotFoundException.class).Init(Push()));
                    EndIf();
                }
                Catch(FileNotFoundException.class, "ex1");
                {
                    Eval(Set("x", Literal(10000)));
                }
                Finally();
                {
                    Eval(Inc("x"));
                }
                EndTry();
                Return(Get("x"));
            }
            End();
        }
    }

    public void testTryCatchFinally() throws Exception
    {
        ClassMaker maker = new TryCatchFinallyMaker();
        Class myClass = maker.defineClass();
        UnaryChecked exec = (UnaryChecked)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 10001, exec.unary(-1));

    }

    public void testTryException() throws Exception
    {

        maker.Method("unary", int.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("x", Runnable.class, 0);
        maker.Begin();
        try {
            maker.Catch(ILLEGAL_ARGUMENT_EXCEPTION, "ex");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Catch without Try block", ex.getMessage());
        }
        try {
            maker.Catch(ClassMakerException.class, "ex");
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Catch without Try block", ex.getMessage());
        }
        try {
            maker.Finally();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Finally without Try block", ex.getMessage());
        }
        try {
            maker.EndTry();
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "EndTry without Try block", ex.getMessage());
        }
        try {
            maker.Throw(ClassMaker.INT_TYPE);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot throw type int", ex.getMessage());
        }
        try {
            maker.Throw(ClassMaker.STRING_TYPE);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Class java.lang.String cannot be thrown", ex.getMessage());
        }
    }
}
