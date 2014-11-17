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

import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

public class MakerBooleanTest extends ClassMakerTestCase implements ByteCode
{
    ClassMaker maker;
    ClassMakerFactory factory;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test/MyClass", Object.class, "MyClass.java");
    }

    // Generate default constructor
    public void defaultConstructor() throws Exception
    {
        maker.Method("<init>", void.class, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public interface BooleanXY
    {
        boolean eval(boolean x, boolean y);
    }

    public interface BooleanABCD
    {
        boolean eval(boolean a, boolean b, boolean c, boolean d);
    }

    public void testEQ() throws Exception
    {
        maker.Implements(BooleanXY.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("x", boolean.class, 0);
        maker.Declare("y", boolean.class, 0);
        maker.Begin();
          maker.Return(maker.EQ(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanXY exec = (BooleanXY)myClass.newInstance();

        assertEquals("Wrong value", true, exec.eval(true, true));
        assertEquals("Wrong value", false, exec.eval(false, true));
        assertEquals("Wrong value", false, exec.eval(true, false));
        assertEquals("Wrong value", true, exec.eval(false, false));
    }

    public void testNE() throws Exception
    {
        maker.Implements(BooleanXY.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("x", boolean.class, 0);
        maker.Declare("y", boolean.class, 0);
        maker.Begin();
          maker.Return(maker.NE(maker.Get("x"), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanXY exec = (BooleanXY)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(true, true));
        assertEquals("Wrong value", true, exec.eval(false, true));
        assertEquals("Wrong value", true, exec.eval(true, false));
        assertEquals("Wrong value", false, exec.eval(false, false));
    }

    public void testAndThen() throws Exception
    {
        maker.Implements(BooleanXY.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("x", boolean.class, 0);
        maker.Declare("y", boolean.class, 0);
        maker.Begin();
          maker.Return(maker.Logic(maker.AndThen(maker.Get("x")), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanXY exec = (BooleanXY)myClass.newInstance();

        assertEquals("Wrong value", true, exec.eval(true, true));
        assertEquals("Wrong value", false, exec.eval(false, true));
        assertEquals("Wrong value", false, exec.eval(true, false));
        assertEquals("Wrong value", false, exec.eval(false, false));
    }

    public void testAndThen2() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	Type a = maker.Get("a");
        	AndOrExpression aExpr = maker.AndThen(a);
        	Type b = maker.Get("b");
        	AndOrExpression bExpr = maker.AndThen(aExpr, b);
        	Type c = maker.Get("c");
        	Type result = maker.Logic(bExpr, c);
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", false, exec.eval(true, true, false, false));
        assertEquals("Wrong value", false, exec.eval(false, false, true, false));
        assertEquals("Wrong value", false, exec.eval(true, false, true, false));
        assertEquals("Wrong value", false, exec.eval(false, true, true, false));
        assertEquals("Wrong value", true, exec.eval(true, true, true, false));
    }

    public void testAndThen3() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.AndThen(maker.Get("a"));
        	AndOrExpression bExpr = maker.AndThen(aExpr, maker.Get("b"));
        	AndOrExpression cExpr = maker.AndThen(bExpr, maker.Get("c"));
        	Type result = maker.Logic(cExpr, maker.Get("d"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", false, exec.eval(true, true, false, false));
        assertEquals("Wrong value", false, exec.eval(false, false, true, false));
        assertEquals("Wrong value", false, exec.eval(true, false, true, false));
        assertEquals("Wrong value", false, exec.eval(false, true, true, false));
        assertEquals("Wrong value", false, exec.eval(true, true, true, false));
        assertEquals("Wrong value", false, exec.eval(false, false, false, true));
        assertEquals("Wrong value", false, exec.eval(true, false, false, true));
        assertEquals("Wrong value", false, exec.eval(false, true, false, true));
        assertEquals("Wrong value", false, exec.eval(true, true, false, true));
        assertEquals("Wrong value", false, exec.eval(false, false, true, true));
        assertEquals("Wrong value", false, exec.eval(true, false, true, true));
        assertEquals("Wrong value", false, exec.eval(false, true, true, true));
        assertEquals("Wrong value", true, exec.eval(true, true, true, true));
    }

    public void testOrElse() throws Exception
    {
        maker.Implements(BooleanXY.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("x", boolean.class, 0);
        maker.Declare("y", boolean.class, 0);
        maker.Begin();
          maker.Return(maker.Logic(maker.OrElse(maker.Get("x")), maker.Get("y")));
        maker.End();

        Class myClass = maker.defineClass();
        BooleanXY exec = (BooleanXY)myClass.newInstance();

        assertEquals("Wrong value", true, exec.eval(true, true));
        assertEquals("Wrong value", true, exec.eval(false, true));
        assertEquals("Wrong value", true, exec.eval(true, false));
        assertEquals("Wrong value", false, exec.eval(false, false));
    }

    public void testOrElse2() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.OrElse(maker.Get("a"));
        	AndOrExpression bExpr = maker.OrElse(aExpr, maker.Get("b"));
        	Type result = maker.Logic(bExpr, maker.Get("c"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", true, exec.eval(true, false, false, false));
        assertEquals("Wrong value", true, exec.eval(false, true, false, false));
        assertEquals("Wrong value", true, exec.eval(true, true, false, false));
        assertEquals("Wrong value", true, exec.eval(false, false, true, false));
        assertEquals("Wrong value", true, exec.eval(true, false, true, false));
        assertEquals("Wrong value", true, exec.eval(false, true, true, false));
        assertEquals("Wrong value", true, exec.eval(true, true, true, false));
    }

    public void testOrElse3() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.OrElse(maker.Get("a"));
        	AndOrExpression bExpr = maker.OrElse(aExpr, maker.Get("b"));
        	AndOrExpression cExpr = maker.OrElse(bExpr, maker.Get("c"));
        	Type result = maker.Logic(cExpr, maker.Get("d"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", true, exec.eval(true, false, false, false));
        assertEquals("Wrong value", true, exec.eval(false, true, false, false));
        assertEquals("Wrong value", true, exec.eval(true, true, false, false));
        assertEquals("Wrong value", true, exec.eval(false, false, true, false));
        assertEquals("Wrong value", true, exec.eval(true, false, true, false));
        assertEquals("Wrong value", true, exec.eval(false, true, true, false));
        assertEquals("Wrong value", true, exec.eval(true, true, true, false));
        assertEquals("Wrong value", true, exec.eval(false, false, false, true));
        assertEquals("Wrong value", true, exec.eval(true, false, false, true));
        assertEquals("Wrong value", true, exec.eval(false, true, false, true));
        assertEquals("Wrong value", true, exec.eval(true, true, false, true));
        assertEquals("Wrong value", true, exec.eval(false, false, true, true));
        assertEquals("Wrong value", true, exec.eval(true, false, true, true));
        assertEquals("Wrong value", true, exec.eval(false, true, true, true));
        assertEquals("Wrong value", true, exec.eval(true, true, true, true));
    }
    
    public void testAndThenOrElse() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	Type a = maker.Get("a");
        	AndOrExpression aExpr = maker.AndThen(a);
        	Type b = maker.Get("b");
        	AndOrExpression bExpr = maker.OrElse(aExpr, b);
        	Type c = maker.Get("c");
        	Type result = maker.Logic(bExpr, c);
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", true, exec.eval(true, true, false, false));
        assertEquals("Wrong value", true, exec.eval(false, false, true, false));
        assertEquals("Wrong value", true, exec.eval(true, false, true, false));
        assertEquals("Wrong value", true, exec.eval(false, true, true, false));
        assertEquals("Wrong value", true, exec.eval(true, true, true, false));
    }

    public void testOrElseAndThen() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	Type a = maker.Get("a");
        	AndOrExpression aExpr = maker.OrElse(a);
        	Type b = maker.Get("b");
        	AndOrExpression bExpr = maker.AndThen(aExpr, b);
        	Type c = maker.Get("c");
        	Type result = maker.Logic(bExpr, c);
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", false, exec.eval(true, true, false, false));
        assertEquals("Wrong value", false, exec.eval(false, false, true, false));
        assertEquals("Wrong value", true, exec.eval(true, false, true, false));
        assertEquals("Wrong value", true, exec.eval(false, true, true, false));
        assertEquals("Wrong value", true, exec.eval(true, true, true, false));
    }

    public void testOrElseAndThenAndThen() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.OrElse(maker.Get("a"));
        	AndOrExpression bExpr = maker.AndThen(aExpr, maker.Get("b"));
        	AndOrExpression cExpr = maker.AndThen(bExpr, maker.Get("c"));
        	Type result = maker.Logic(cExpr, maker.Get("d"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", false, exec.eval(true, true, false, false));
        assertEquals("Wrong value", false, exec.eval(false, false, true, false));
        assertEquals("Wrong value", false, exec.eval(true, false, true, false));
        assertEquals("Wrong value", false, exec.eval(false, true, true, false));
        assertEquals("Wrong value", false, exec.eval(true, true, true, false));
        assertEquals("Wrong value", false, exec.eval(false, false, false, true));
        assertEquals("Wrong value", false, exec.eval(true, false, false, true));
        assertEquals("Wrong value", false, exec.eval(false, true, false, true));
        assertEquals("Wrong value", false, exec.eval(true, true, false, true));
        assertEquals("Wrong value", false, exec.eval(false, false, true, true));
        assertEquals("Wrong value", true, exec.eval(true, false, true, true));
        assertEquals("Wrong value", true, exec.eval(false, true, true, true));
        assertEquals("Wrong value", true, exec.eval(true, true, true, true));
    }

    public void testOrElseOrElseAndThen() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.OrElse(maker.Get("a"));
        	AndOrExpression bExpr = maker.OrElse(aExpr, maker.Get("b"));
        	AndOrExpression cExpr = maker.AndThen(bExpr, maker.Get("c"));
        	Type result = maker.Logic(cExpr, maker.Get("d"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", false, exec.eval(true, true, false, false));
        assertEquals("Wrong value", false, exec.eval(false, false, true, false));
        assertEquals("Wrong value", false, exec.eval(true, false, true, false));
        assertEquals("Wrong value", false, exec.eval(false, true, true, false));
        assertEquals("Wrong value", false, exec.eval(true, true, true, false));
        assertEquals("Wrong value", false, exec.eval(false, false, false, true));
        assertEquals("Wrong value", true, exec.eval(true, false, false, true));
        assertEquals("Wrong value", true, exec.eval(false, true, false, true));
        assertEquals("Wrong value", true, exec.eval(true, true, false, true));
        assertEquals("Wrong value", true, exec.eval(false, false, true, true));
        assertEquals("Wrong value", true, exec.eval(true, false, true, true));
        assertEquals("Wrong value", true, exec.eval(false, true, true, true));
        assertEquals("Wrong value", true, exec.eval(true, true, true, true));
    }

    public void testOrElseAndThenOrElse() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.OrElse(maker.Get("a"));
        	AndOrExpression bExpr = maker.AndThen(aExpr, maker.Get("b"));
        	AndOrExpression cExpr = maker.OrElse(bExpr, maker.Get("c"));
        	Type result = maker.Logic(cExpr, maker.Get("d"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", false, exec.eval(true, true, false, false));
        assertEquals("Wrong value", false, exec.eval(false, false, true, false));
        assertEquals("Wrong value", true, exec.eval(true, false, true, false));
        assertEquals("Wrong value", true, exec.eval(false, true, true, false));
        assertEquals("Wrong value", true, exec.eval(true, true, true, false));
        assertEquals("Wrong value", true, exec.eval(false, false, false, true));
        assertEquals("Wrong value", true, exec.eval(true, false, false, true));
        assertEquals("Wrong value", true, exec.eval(false, true, false, true));
        assertEquals("Wrong value", true, exec.eval(true, true, false, true));
        assertEquals("Wrong value", true, exec.eval(false, false, true, true));
        assertEquals("Wrong value", true, exec.eval(true, false, true, true));
        assertEquals("Wrong value", true, exec.eval(false, true, true, true));
        assertEquals("Wrong value", true, exec.eval(true, true, true, true));
    }

    public void testAndThenOrElseOrElse() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.AndThen(maker.Get("a"));
        	AndOrExpression bExpr = maker.OrElse(aExpr, maker.Get("b"));
        	AndOrExpression cExpr = maker.OrElse(bExpr, maker.Get("c"));
        	Type result = maker.Logic(cExpr, maker.Get("d"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", true, exec.eval(true, true, false, false));
        assertEquals("Wrong value", true, exec.eval(false, false, true, false));
        assertEquals("Wrong value", true, exec.eval(true, false, true, false));
        assertEquals("Wrong value", true, exec.eval(false, true, true, false));
        assertEquals("Wrong value", true, exec.eval(true, true, true, false));
        assertEquals("Wrong value", true, exec.eval(false, false, false, true));
        assertEquals("Wrong value", true, exec.eval(true, false, false, true));
        assertEquals("Wrong value", true, exec.eval(false, true, false, true));
        assertEquals("Wrong value", true, exec.eval(true, true, false, true));
        assertEquals("Wrong value", true, exec.eval(false, false, true, true));
        assertEquals("Wrong value", true, exec.eval(true, false, true, true));
        assertEquals("Wrong value", true, exec.eval(false, true, true, true));
        assertEquals("Wrong value", true, exec.eval(true, true, true, true));
    }

    public void testAndThenAndThenOrElse() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.AndThen(maker.Get("a"));
        	AndOrExpression bExpr = maker.AndThen(aExpr, maker.Get("b"));
        	AndOrExpression cExpr = maker.OrElse(bExpr, maker.Get("c"));
        	Type result = maker.Logic(cExpr, maker.Get("d"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", false, exec.eval(true, true, false, false));
        assertEquals("Wrong value", false, exec.eval(false, false, true, false));
        assertEquals("Wrong value", false, exec.eval(true, false, true, false));
        assertEquals("Wrong value", false, exec.eval(false, true, true, false));
        assertEquals("Wrong value", true, exec.eval(true, true, true, false));
        assertEquals("Wrong value", true, exec.eval(false, false, false, true));
        assertEquals("Wrong value", true, exec.eval(true, false, false, true));
        assertEquals("Wrong value", true, exec.eval(false, true, false, true));
        assertEquals("Wrong value", true, exec.eval(true, true, false, true));
        assertEquals("Wrong value", true, exec.eval(false, false, true, true));
        assertEquals("Wrong value", true, exec.eval(true, false, true, true));
        assertEquals("Wrong value", true, exec.eval(false, true, true, true));
        assertEquals("Wrong value", true, exec.eval(true, true, true, true));
    }

    public void testAndThenOrElseAndThen() throws Exception
    {
        maker.Implements(BooleanABCD.class);
        defaultConstructor();

        maker.Method("eval", boolean.class, ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        {
        	AndOrExpression aExpr = maker.AndThen(maker.Get("a"));
        	AndOrExpression bExpr = maker.OrElse(aExpr, maker.Get("b"));
        	AndOrExpression cExpr = maker.AndThen(bExpr, maker.Get("c"));
        	Type result = maker.Logic(cExpr, maker.Get("d"));
        	maker.Return(result);
        }
        maker.End();

        Class myClass = maker.defineClass();
        BooleanABCD exec = (BooleanABCD)myClass.newInstance();

        assertEquals("Wrong value", false, exec.eval(false, false, false, false));
        assertEquals("Wrong value", false, exec.eval(true, false, false, false));
        assertEquals("Wrong value", false, exec.eval(false, true, false, false));
        assertEquals("Wrong value", false, exec.eval(true, true, false, false));
        assertEquals("Wrong value", false, exec.eval(false, false, true, false));
        assertEquals("Wrong value", false, exec.eval(true, false, true, false));
        assertEquals("Wrong value", false, exec.eval(false, true, true, false));
        assertEquals("Wrong value", false, exec.eval(true, true, true, false));
        assertEquals("Wrong value", false, exec.eval(false, false, false, true));
        assertEquals("Wrong value", false, exec.eval(true, false, false, true));
        assertEquals("Wrong value", false, exec.eval(false, true, false, true));
        assertEquals("Wrong value", true, exec.eval(true, true, false, true));
        assertEquals("Wrong value", true, exec.eval(false, false, true, true));
        assertEquals("Wrong value", true, exec.eval(true, false, true, true));
        assertEquals("Wrong value", true, exec.eval(false, true, true, true));
        assertEquals("Wrong value", true, exec.eval(true, true, true, true));
    }
    
}
