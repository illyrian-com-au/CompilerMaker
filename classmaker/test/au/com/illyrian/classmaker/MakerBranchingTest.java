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

public class MakerBranchingTest extends ClassMakerTestCase implements SourceLine
{
    ClassMakerFactory factory;
    ClassMaker maker;
    protected int lineNo = 0;
    
    public int getLineNumber() {
    	return lineNo;
    }
    
    public String getFilename() {
    	return getClass().getName().replace('.', '/') + ".java";
    }

    /** Sets the line number in the generated class to match the line number in this file. */
    public void nl(int line) {
        lineNo = line;
    }

    public void nl() {
        ++lineNo;
    }

    public void setUp()
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker();
        maker.setSourceLine(this);
        maker.setSimpleClassName("MyClass");
    }

    // Generate default constructor
    public void defaultConstructor() throws Exception
    {
        maker.Method("<init>", ClassMakerFactory.VOID_TYPE, ClassMakerConstants.ACC_PUBLIC);
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

    public class IfBranchMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("a", int.class, 0);
            Begin();
              If(LT(Get("a"), Literal(0)));
                Set("a", Literal(0));
              EndIf();
              Return(Get("a"));
            End();
        }
    }

    public class IfElseBranchMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("a", int.class, 0);
            Begin();
              If(LT(Get("a"), Literal(0)));
                Set("a", Literal(0));
              Else();
                Set("a", Literal(1));
              EndIf();
              Return(Get("a"));
            End();
        }
    }

    
    public void testIfBranch() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // nl(xxx) allows the debugger to step through this code.
nl(135);maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
nl();   maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
nl();   maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
nl();   maker.Begin();
nl();     maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
nl();     maker.If(maker.NE(maker.Get("a"), maker.Get("b")));
nl();       maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
nl();     maker.EndIf();
nl();     maker.Return(maker.Get(maker.This(), "id"));
nl();   maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(0, 1));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(5, 5));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(-1, -1));
    }

    public void testIfTrueBranch() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // nl(xxx) allows the debugger to step through this code.
nl(161);maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
nl();   maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
nl();   maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
nl();   maker.Begin();
nl();     maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
nl();     maker.If(maker.Literal(true));
nl();       maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
nl();     maker.EndIf();
nl();     maker.Return(maker.Get(maker.This(), "id"));
nl();   maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(0, 0));
    }

    public void testIfFalseBranch() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // nl(xxx) allows the debugger to step through this code.
nl(185);  maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
nl();     maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
nl();     maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
nl();     maker.Begin();
nl();       maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
nl();       maker.If(maker.Literal(false));
nl();         maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
nl();       maker.EndIf();
nl();       maker.Return(maker.Get(maker.This(), "id"));
nl();     maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 5, exec.binary(0, 0));
    }

    public void testIfElseBranch() throws Exception {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker();
        maker.setSourceLine(this);
        maker.setSimpleClassName("MyClass");

        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // nl(xxx) allows the debugger to step through this code.
nl(213);  maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
nl();     maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
nl();     maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
nl();     maker.Begin();
nl();       maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
nl();       maker.If(maker.EQ(maker.Get("a"), maker.Get("b")));
nl();         maker.Eval(maker.Set(maker.This(), "id", maker.Literal(3)));
nl();       maker.Else();
nl();         maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
nl();       maker.EndIf();
nl();       maker.Return(maker.Get(maker.This(), "id"));
nl();     maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(0, 1)); // Set breakpoint here
        assertEquals("Wrong value for exec.eval()", 3, exec.binary(5, 5));
        assertEquals("Wrong value for exec.eval()", 3, exec.binary(-1, -1));
    }

    public void testAndThen() throws Exception {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker();
        maker.setSourceLine(this);
        maker.setSimpleClassName("MyClass");

        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // nl(xxx) allows the debugger to step through this code.
nl(245);  maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
nl();     maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
nl();     maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
nl();     maker.Begin();
nl();       maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
nl();       maker.If(maker.Logic(maker.AndThen(maker.LT(maker.Literal(1), maker.Get("a"))),
                  maker.LT(maker.Get("a"), maker.Literal(3))));
nl(252);      maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
nl();       maker.EndIf();
nl();       maker.Return(maker.Get(maker.This(), "id"));
nl();     maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.binary()", 5, exec.binary(0, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(1, 5));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(2, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(3, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(4, 5));
    }

    public void testOrElse() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // nl(xxx) allows the debugger to step through this code.
nl(274);     maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
nl();        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
nl();        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
nl();        maker.Begin();
nl();          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
nl();          maker.If(maker.Logic(maker.OrElse(maker.GT(maker.Literal(1), maker.Get("a"))),
                        maker.GT(maker.Get("a"), maker.Literal(3))));
nl(281);         maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
nl();          maker.EndIf();
nl();          maker.Return(maker.Get(maker.This(), "id"));
nl();        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.binary()", 2, exec.binary(0, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(1, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(2, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(3, 5));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(4, 5));
    }

    public void testOrElseException() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
        try {
          maker.If(maker.Literal(1));
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "If condition must be type boolean, not byte", ex.getMessage());
        }
        try {
            maker.If(maker.Logic(maker.OrElse(maker.Literal(1)),
                    maker.Literal(true)));
        } catch (ClassMakerException ex) {
              assertEquals("Wrong message", "Condition for OrElse must be type boolean, not byte", ex.getMessage());
        }
        try {
            maker.If(maker.Logic(maker.AndThen(maker.Literal(1)),
                    maker.Literal(true)));
        } catch (ClassMakerException ex) {
              assertEquals("Wrong message", "Condition for AndThen must be type boolean, not byte", ex.getMessage());
        }
        try {
            maker.If(maker.Logic(maker.OrElse(maker.Literal(false)),
                    maker.Literal(1)));
        } catch (ClassMakerException ex) {
              assertEquals("Wrong message", "Condition for Logic must be type boolean, not byte", ex.getMessage());
        }
        try {
            maker.If(maker.Logic(maker.AndThen(maker.Literal(false)),
                    maker.Literal(1)));
        } catch (ClassMakerException ex) {
              assertEquals("Wrong message", "Condition for Logic must be type boolean, not byte", ex.getMessage());
        }
        try {
              maker.EndIf();
        } catch (ClassMakerException ex) {
              assertEquals("Wrong message", "EndIf without a matching If", ex.getMessage());
        }
    }


    public void testAndThenOrElse() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
          // infix:  (a>1 && a<3) || (b=0)
          // prefix: || && > a 1 < a 3 = b 0
          maker.If(maker.Logic(
                         maker.OrElse(
                               maker.AndThen(maker.GT(maker.Get("a"), maker.Literal(1))),
                               maker.LT(maker.Get("a"), maker.Literal(3))
                         ), maker.EQ(maker.Get("b"), maker.Literal(0))
                   )
          );
            maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
          maker.EndIf();
          maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.binary()", 5, exec.binary(0, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(1, 5));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(2, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(3, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(4, 5));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(1, 0));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(2, 0));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(3, 0));
    }

    public void testAndThenOrElseAndThen() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
          // infix:  (a>1 && a<3) || (b>0 && b<2)
          // prefix: || && > a 1 < a 3 && > b 0 < b 2
          maker.If(maker.Logic(maker.OrElse(maker.AndThen(
                               maker.GT(maker.Get("a"), maker.Literal(1))),
                               maker.LT(maker.Get("a"), maker.Literal(3))),
                   maker.Logic(maker.AndThen(
                               maker.GT(maker.Get("b"), maker.Literal(0))),
                               maker.LT(maker.Get("b"), maker.Literal(2))))
                  );
            maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
          maker.EndIf();
          maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.binary()", 5, exec.binary(1, 0));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(1, 1));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(1, 2));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(2, 0));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(2, 1));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(2, 2));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(3, 0));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(3, 1));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(3, 2));
    }

    public void testOrElseAndThenOrElse() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
          // infix:  (a<2 || a>2) && (b<1 || b>1)
          // prefix: (|| (&& (> a 2) (< a 2)) (&& (> b 1) (< b 1)))
          maker.If(maker.Logic(maker.AndThen(maker.OrElse(
                               maker.LT(maker.Get("a"), maker.Literal(2))),
                               maker.GT(maker.Get("a"), maker.Literal(2))),
                   maker.Logic(maker.OrElse(
                               maker.LT(maker.Get("b"), maker.Literal(1))),
                               maker.GT(maker.Get("b"), maker.Literal(1))))
                  );
            maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
          maker.EndIf();
          maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.binary()", 2, exec.binary(1, 0));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(1, 1));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(1, 2));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(2, 0));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(2, 1));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(2, 2));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(3, 0));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(3, 1));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(3, 2));
    }

    public void testOrElseAndThen() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
          // infix:  (a<1 || a>3) && (b>2)
          // prefix: || && < a 1 > a 3 > b 2
          maker.If(maker.Logic(maker.AndThen(maker.OrElse(
                               maker.LT(maker.Get("a"), maker.Literal(1))),
                               maker.GT(maker.Get("a"), maker.Literal(3))),
                               maker.GT(maker.Get("b"), maker.Literal(2)))
                  );
            maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
          maker.EndIf();
          maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.binary()", 2, exec.binary(0, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(1, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(2, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(3, 5));
        assertEquals("Wrong value for exec.binary()", 2, exec.binary(4, 5));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(0, 0));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(2, 0));
        assertEquals("Wrong value for exec.binary()", 5, exec.binary(4, 0));
    }

    public void testIfReturn() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          maker.If(maker.LE(maker.Get("a"), maker.Get("b")));
            maker.Return(maker.Literal(2));
          maker.EndIf();
          maker.Return(maker.Literal(5));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(0, 1));
        assertEquals("Wrong value for exec.eval()", 2, exec.binary(5, 5));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(5, -1));
    }

    public void testIfException() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();

        maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
            try {
                maker.Else();
                fail("Else without If");
            } catch (ClassMakerException ex) {
                assertEquals("Else without a matching If", ex.getMessage());
            }
            try {
                maker.EndIf();
                fail("EndIf without If");
            } catch (ClassMakerException ex) {
                assertEquals("EndIf without a matching If", ex.getMessage());
            }
            try {
                maker.If(ClassMakerFactory.INT_TYPE.getValue());
                fail("If(INT)");
            } catch (ClassMakerException ex) {
                assertEquals("If condition must be type boolean, not int", ex.getMessage());
            }
          maker.If(maker.Literal(true));
            maker.Return(maker.Literal(2));
          maker.Else();
              try {
                  maker.Else();
                  fail("Else twice");
              } catch (ClassMakerException ex) {
                  assertEquals("Else called twice", ex.getMessage());
              }
            maker.Return(maker.Literal(3));
          maker.EndIf();
          maker.Return(maker.Literal(5));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(0, 1));
        assertEquals("Wrong value for exec.eval()", 2, exec.binary(5, 5));
    }

    public void testIfElseReturn() throws Exception
    {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          maker.Set(maker.This(), "id", maker.Literal(5));
          maker.If(maker.GT(maker.Get("a"), maker.Get("b")));
            maker.Return(maker.Literal(2));
          maker.Else();
            maker.Return(maker.Literal(3));
          maker.EndIf();
          maker.Return(maker.Literal(0));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(1, 0));
        assertEquals("Wrong value for exec.eval()", 3, exec.binary(5, 5));
        assertEquals("Wrong value for exec.eval()", 3, exec.binary(-1, 0));
        assertEquals("Wrong value for exec.eval()", 5, getIntField(myClass, exec, "id"));
    }

    public void testIfNonNullBranch() throws Exception
    {
        maker.Implements(UnaryObject.class);
        defaultConstructor();

        maker.Method("unary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.OBJECT_TYPE, 0);
        maker.Begin();
          maker.If(maker.NE(maker.Get("a"), maker.Null()));
            maker.Return(maker.Literal(2));
          maker.EndIf();
          maker.Return(maker.Literal(5));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryObject exec = (UnaryObject)myClass.newInstance();

        String test = "Hello";
        assertEquals("Wrong value for exec.unary()", 5, exec.unary(null));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(test));
    }

    public void testIfObjectEqNullBranch() throws Exception
    {
        maker.Implements(UnaryObject.class);
        defaultConstructor();

        maker.Method("unary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.OBJECT_TYPE, 0);
        maker.Begin();
          maker.If(maker.EQ(maker.Get("a"), maker.Null()));
            maker.Return(maker.Literal(1));
          maker.EndIf();
          maker.Return(maker.Literal(2));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryObject exec = (UnaryObject)myClass.newInstance();
        String test = "Hello";

        assertEquals("Wrong value for exec.eval()", 1, exec.unary(null));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(test));
    }

    public void testIfNullEqObjectBranch() throws Exception
    {
        maker.Implements(UnaryObject.class);
        defaultConstructor();

        maker.Method("unary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.OBJECT_TYPE, 0);
        maker.Begin();
          maker.If(maker.EQ(maker.Null(), maker.Get("a")));
            maker.Return(maker.Literal(1));
          maker.EndIf();
          maker.Return(maker.Literal(2));
        maker.End();

        Class myClass = maker.defineClass();
        UnaryObject exec = (UnaryObject)myClass.newInstance();
        String test = "Hello";

        assertEquals("Wrong value for exec.eval()", 1, exec.unary(null));
        assertEquals("Wrong value for exec.eval()", 2, exec.unary(test));
    }

    public void testBreakNEBranch() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("unary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(0)));
          maker.Loop();
            maker.If(maker.LE(maker.Get("a"), maker.Literal(0)));
              maker.Break();
            maker.EndIf();
            maker.Eval(maker.Set(maker.This(), "id", maker.Add(maker.Get(maker.This(), "id"), maker.Get("a"))));
            maker.Eval(maker.Dec("a"));
          maker.EndLoop();
          maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 10, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 28, exec.unary(7));
    }

    public void testContinueWhileBranch() throws Exception
    {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("unary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(0)));
          maker.Loop();
            // while (a-- > 0)
            maker.While(maker.GT(maker.PostDec("a"), maker.Literal(0)));
            // if ((a % 2) > 0) continue;
            maker.If(maker.GT(maker.Rem(maker.Get("a"), maker.Literal(2)), maker.Literal(0)));
              maker.Continue();
            maker.EndIf();
            // this.id = this.id + a;
            maker.Eval(maker.Set(maker.This(), "id", maker.Add(maker.Get(maker.This(), "id"), maker.Get("a"))));
          maker.EndLoop();
          maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(6));
        assertEquals("Wrong value for exec.unary()", 12, exec.unary(7));
        assertEquals("Wrong value for exec.unary()", 12, exec.unary(8));
    }

    public class LoopBreakMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              Eval(Set("x", Literal(1)));
              Loop();
                If(LE(Get("n"), Literal(0)));
                  Break();
                EndIf();
                Eval(Set("x", Mult(Get("x"), Get("n"))));
                Eval(Dec("n"));
              EndLoop();
              Return(Get("x"));
            End();
        }
    }

    public class Factorial
        implements Unary
    {
        public int unary(int n)
        {
            int x;
            x= 1;
            while (n>0)
            {
                x = x * n;
                n--;
            }
            return x;
        }
    }

    // FIXME - also  implement as calls to ClassMaker.
    public class FactorialMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              Eval(Set("x", Literal(1)));
              Loop();
                While(GT(Get("n"), Literal(0)));
                Eval(Set("x", Mult(Get("x"), Get("n"))));
                Eval(Dec("n"));
              EndLoop();
              Return(Get("x"));
            End();
        }
    }

    public class LabelMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              Eval(Set("x", Literal(1)));
              Loop().setLabel("Here");
                Loop();
                  If(LE(Get("n"), Literal(0)));
                      Break("Here");
                  EndIf();
                  Eval(Set("x", Mult(Get("x"), Get("n"))));
                  Eval(Dec("n"));
                EndLoop();
              EndLoop();
              Return(Get("x"));
            End();
        }
    }

    public class ForWhileStepMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              For(Set("x", Literal(1))).While(GT(Get("n"), Literal(0))).Step(Dec("n"));
                Eval(Set("x", Mult(Get("x"), Get("n"))));
              EndFor();
              Return(Get("x"));
            End();
        }
    }

    public class ForWhileMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              For(Set("x", Literal(1))).While(GT(Get("n"), Literal(0)));
                Eval(Set("x", Mult(Get("x"), Get("n"))));
                Eval(Dec("n"));
              EndFor();
              Return(Get("x"));
            End();
        }
    }


    public class ForMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              For(Set("x", Literal(1)));
                If(LE(Get("n"), Literal(0)));
                  Break();
                EndIf();
                Eval(Set("x", Mult(Get("x"), Get("n"))));
                Eval(Dec("n"));
              EndFor();
              Return(Get("x"));
            End();
        }
    }

    public void testForWhileStepMaker() throws Exception
    {
        ClassMaker maker = new ForWhileStepMaker();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        factorialTest(exec);
    }

    public void testForWhileMaker() throws Exception
    {
        ClassMaker maker = new ForWhileMaker();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        factorialTest(exec);
    }

    public void testForMaker() throws Exception
    {
        ClassMaker maker = new ForMaker();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        factorialTest(exec);
    }

    public void testLoopBreakMaker() throws Exception
    {
        ClassMaker maker = new LoopBreakMaker();
        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();
        factorialTest(exec);
    }

    public void testFactorialMaker() throws Exception
    {
        ClassMaker maker = new FactorialMaker();
        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();
        factorialTest(exec);
    }

    public void testLabelMaker() throws Exception
    {
        ClassMaker maker = new LabelMaker();
        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();
        factorialTest(exec);
    }

    public void testFactorialJava() throws Exception
    {
        Unary exec = (Unary)new Factorial();
        factorialTest(exec);
    }

    public void testMakerForFactorial() throws Exception
    {
    	ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker();

        maker.setClassModifiers(ClassMakerConstants.ACC_PUBLIC);
        maker.setSimpleClassName("Factorial");
        maker.Implements(Unary.class);

        maker.Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("n", int.class, 0);
        maker.Begin();
	        maker.Declare("x", int.class, 0);
	        maker.Eval(maker.Set("x", maker.Literal(1)));
	        maker.Loop();
		        maker.While(maker.GT(maker.Get("n"), maker.Literal(0)));
		        maker.Eval(maker.Set("x", maker.Mult(maker.Get("x"), maker.Get("n"))));
		        maker.Eval(maker.Dec("n"));
	        maker.EndLoop();
	        maker.Return(maker.Get("x"));
        maker.End();
        
        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();
        factorialTest(exec);
    }

    public void factorialTest(Unary exec) throws Exception
    {
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 24, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 120, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 720, exec.unary(6));
        assertEquals("Wrong value for exec.unary()", 5040, exec.unary(7));
        assertEquals("Wrong value for exec.unary()", 40320, exec.unary(8));
    }

    public class ForWhileStepContinueMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              For(Set("x", Literal(0))).While(GT(Get("n"), Literal(0))).Step(Dec("n"));
                // if (x%2 != 0) continue;
                If(NE(Rem(Get("n"), Literal(2)), Literal(0)));
                  Continue();
                EndIf();
                Eval(Set("x", Add(Get("x"), Get("n"))));
              EndFor();
              Return(Get("x"));
            End();
        }
    }

    public void testForWhileStepContinueMaker() throws Exception
    {
        ClassMaker maker = new ForWhileStepContinueMaker();
        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 12, exec.unary(6));
    }

    public class ForWhileContinueMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              For(Set("x", Literal(0))).While(GT(Get("n"), Literal(0)));
                // if (x%2 != 0){--n; continue;}
                If(NE(Rem(Get("n"), Literal(2)), Literal(0)));
                  Eval(Dec("n"));
                  Continue();
                EndIf();
                Eval(Set("x", Add(Get("x"), Get("n"))));
                Eval(Dec("n"));
              EndFor();
              Return(Get("x"));
            End();
        }
    }

    public void testForWhileContinueMaker() throws Exception
    {
        ClassMaker maker = new ForWhileContinueMaker();
        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 12, exec.unary(6));
    }

    public class ForContinueMaker extends ClassMakerCode
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              For(Set("x", Literal(0)));
                If(LE(Get("n"), Literal(0)));
                  Break();
                EndIf();
                // if (n%2 != 0){--n; continue;}
                If(NE(Rem(Get("n"), Literal(2)), Literal(0)));
                  Eval(Dec("n"));
                  Continue();
                EndIf();
                Eval(Set("x", Add(Get("x"), Get("n"))));
                Eval(Dec("n"));
              EndFor();
              Return(Get("x"));
            End();
        }
    }

    public void testForContinueMaker() throws Exception {
        ClassMaker maker = new ForContinueMaker();
        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 12, exec.unary(6));
    }

    public void testForLoopBreakMaker() throws Exception {
    	maker.setDebugCodeOutput(System.out);
    	
        maker.Implements(Unary.class);

        maker.Method("unary", int.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("n", int.class, 0);
        maker.Begin();
        {
        	maker.Declare("x", int.class, 0);
        	maker.Set("x", maker.Literal(0));
        	maker.For(null).While(null).Step(null);
        	{
        		maker.If(maker.LE(maker.Get("n"), maker.Literal(0)));
        		{
        			maker.Break();
        		}
        		maker.EndIf();
        		// if (n%2 != 0){--n; continue;}
        		maker.If(maker.NE(maker.Rem(maker.Get("n"), maker.Literal(2)), maker.Literal(0)));
        		{
        			maker.Eval(maker.Dec("n"));
        			maker.Continue();
        		}
        		maker.EndIf();
        		maker.Eval(maker.Set("x", maker.Add(maker.Get("x"), maker.Get("n"))));
        		maker.Eval(maker.Dec("n"));
        	}
        	maker.EndFor();
        	maker.Return(maker.Get("x"));
        }
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 12, exec.unary(6));
    }

    public void testWhileNEBranch() throws Exception {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // nl(xxx) allows the debugger to step through this code.
nl(1040);    maker.Method("unary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
nl();        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
nl();        maker.Begin();
nl();          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(0)));
nl();          maker.Loop();
nl();            maker.While(maker.GT(maker.Get("a"), maker.Literal(0)));
nl();            maker.Eval(maker.Set(maker.This(), "id", maker.Add(maker.Get(maker.This(), "id"), maker.Get("a"))));
nl();            maker.Eval(maker.Dec("a"));
nl();          maker.EndLoop();
nl();          maker.Return(maker.Get(maker.This(), "id"));
nl();        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 10, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 28, exec.unary(7));
    }

    public void testWhileExceptions() throws Exception {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("unary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          try {
              maker.Break();
              fail("Break without Loop");
          } catch (ClassMakerException ex) {
              assertEquals("Break while not in a Loop or Switch statement", ex.getMessage());
          }
          try {
              maker.Continue();
              fail("Continue without Loop");
          } catch (ClassMakerException ex) {
              assertEquals("Continue while not in a Loop", ex.getMessage());
          }
          try {
              maker.While(ClassMakerFactory.BOOLEAN_TYPE.getValue());
              fail("While without Loop");
          } catch (ClassMakerException ex) {
              assertEquals("While must be within a Loop", ex.getMessage());
          }
          try {
              maker.EndLoop();
              fail("EndLoop without Loop");
          } catch (ClassMakerException ex) {
              assertEquals("EndLoop without a matching Loop", ex.getMessage());
          }

          maker.Loop();
          try {
              maker.While(ClassMakerFactory.INT_TYPE.getValue());
              fail("While(INT)");
          } catch (ClassMakerException ex) {
              assertEquals("While condition must be type boolean, not int", ex.getMessage());
          }
          try {
              maker.EndLoop();
              fail("EndLoop but no Break");
          } catch (ClassMakerException ex) {
              assertEquals("Loop does not contain a Break", ex.getMessage());
          }

          maker.Break();
          maker.EndLoop();
          try {
              maker.Break();
              fail("Break without Loop");
          } catch (ClassMakerException ex) {
              assertEquals("Break while not in a Loop or Switch statement", ex.getMessage());
          }
          try {
              maker.Continue();
              fail("Continue without Loop");
          } catch (ClassMakerException ex) {
              assertEquals("Continue while not in a Loop", ex.getMessage());
          }
          try {
              maker.While(ClassMakerFactory.BOOLEAN_TYPE.getValue());
              fail("While without Loop");
          } catch (ClassMakerException ex) {
              assertEquals("While must be within a Loop", ex.getMessage());
          }
          try {
              maker.EndLoop();
              fail("EndLoop without Loop");
          } catch (ClassMakerException ex) {
              assertEquals("EndLoop without a matching Loop", ex.getMessage());
          }

          maker.Return(maker.Literal(1));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        // Prove code works even after exceptions.
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
    }

    public void testForExceptions() throws Exception {
        maker.Implements(Unary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        maker.Method("unary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
        maker.Begin();
          try {
              maker.EndFor();
              fail("EndFor without For");
          } catch (ClassMakerException ex) {
              assertEquals("EndFor without a matching For", ex.getMessage());
          }

          maker.For(ClassMakerFactory.VOID_TYPE.getValue());
          try {
              maker.While(ClassMakerFactory.INT_TYPE.getValue());
              fail("While(INT)");
          } catch (ClassMakerException ex) {
              assertEquals("While condition must be type boolean, not int", ex.getMessage());
          }
          try {
              maker.EndFor();
              fail("EndFor but no Break");
          } catch (ClassMakerException ex) {
              assertEquals("For does not contain a Break", ex.getMessage());
          }
    }

    public void testDebugScope() throws Exception {
        maker.Implements(Binary.class);
        defaultConstructor();
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // nl(xxx) allows the debugger to step through this code.
nl(1283);  maker.Method("binary", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);
nl();     maker.Declare("a", ClassMakerFactory.INT_TYPE, 0);
nl();     maker.Declare("b", ClassMakerFactory.INT_TYPE, 0);
nl();     maker.Begin();
nl();       maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
nl();       maker.Declare("c", ClassMakerFactory.INT_TYPE, 0);
nl();       maker.Set("c", maker.Add(maker.Get("a"), maker.Get("b")));
nl();       maker.If(maker.EQ(maker.Get("a"), maker.Get("b")));
nl();       maker.Begin();
nl();         maker.Declare("z", ClassMakerFactory.INT_TYPE, 0);
nl();         maker.Set("z", maker.Literal(1024));
nl();         maker.Declare("d", ClassMakerFactory.INT_TYPE, 0);
nl();         maker.Set("d", maker.Assign("c", maker.Get("a")));
nl();         maker.Eval(maker.Set(maker.This(), "id", maker.Literal(3)));
nl();       maker.End();
nl();       maker.Else();
nl();       maker.Begin();
nl();         maker.Declare("z", ClassMakerFactory.INT_TYPE, 0);
nl();         maker.Set("z", maker.Assign("c", maker.Get("b")));
nl();         maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
nl();       maker.End();
nl();       maker.EndIf();
nl();       maker.Declare("f", ClassMakerFactory.INT_TYPE, 0);
nl();       maker.Set("f", maker.Add(maker.Get("a"), maker.Get("b")));
nl();       maker.Return(maker.Get("f"));
nl();     maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 1, exec.binary(0, 1));
        assertEquals("id", 2, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for exec.eval()", 10, exec.binary(5, 5));
        assertEquals("id", 3, getIntField(myClass, exec, "id"));
        assertEquals("Wrong value for exec.eval()", -2, exec.binary(-1, -1));
        assertEquals("id", 3, getIntField(myClass, exec, "id"));
    }

}
