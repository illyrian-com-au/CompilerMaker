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

package au.com.illyrian.classmaker.converters;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;

public class MakerStringTest extends ClassMakerTestCase
{
    ClassMaker maker;
    ClassMakerFactory factory;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        defaultConstructor(maker);
    }

    // Generate default constructor
    public void defaultConstructor(ClassMaker maker) throws Exception
    {
        maker.Method("<init>", ClassMakerFactory.VOID_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public interface Eval
    {
        String eval();
    }

    public interface Unary
    {
        String unary(Object object);
    }

    public void testStringLiteral() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Return(maker.Literal("Hello World"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Hello World", exec.eval());
    }

    public void testStringConversionImpl() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        StringConversion convert = factory.getStringConversion();
        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          ClassType stack = convert.newStringBuffer(maker);
          stack = convert.append(maker, stack, maker.Literal("Hello World").getType());
          stack = convert.toString(maker,stack);
          maker.Set("str", stack.getValue());
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Hello World", exec.eval());
    }

    public void testAddStrings() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal("Hello"), maker.Literal(" World")));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Hello World", exec.eval());
    }

    public void testStringInt() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal("Weight = "),
        		                     maker.Literal(3)));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Weight = 3", exec.eval());
    }

    public void testStringIntInt() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          // str = "Weight = " + 3 + -3567;
          maker.Set("str", maker.Add(maker.Add(maker.Literal("Weight = "),
        		                     maker.Literal(3)), maker.Literal(-3567)));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        String msg = "Wrong value for \"Weight = \" + 3 + -3567";
        assertEquals(msg, "Weight = 3-3567", exec.eval());
        // Check the java equivalent.
        assertEquals(msg, "Weight = 3-3567", "Weight = " + 3 + -3567);
    }

    public void testIntString() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal(5), maker.Literal(" grams")));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "5 grams", exec.eval());
    }

    public void testAddIntString() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          // str = 3 + -10 + " grams";
          maker.Set("str", maker.Add(maker.Add(maker.Literal(3), maker.Literal(-10)),
        		                     maker.Literal(" grams")));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        String msg = "Wrong value for 3 + -10 + \" grams\"";
        assertEquals(msg, "-7 grams", exec.eval());
        // Check the java equivalent.
        assertEquals(msg, "-7 grams", 3 + -10 + " grams");
    }

    public void testIntIntString() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          // str = 3 + (-3567 + " grams");
          maker.Set("str", maker.Add(maker.Literal(3),
        		  maker.Add(maker.Literal(-3567), maker.Literal(" grams"))));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        String msg = "Wrong value for 3 + (-3567 + \" grams\"";
        assertEquals(msg, "3-3567 grams", exec.eval());
        // Check the java equivalent.
        assertEquals(msg, "3-3567 grams", 3 + (-3567 + " grams"));
    }

    public void testStringLong() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal("Stars = "), maker.Literal(9876543210L)));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Stars = 9876543210", exec.eval());
    }

    public void testLongString() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal(5000000000L), maker.Literal(" stars")));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "5000000000 stars", exec.eval());
    }

    public void testStringFloat() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal("Money = "), maker.Literal(105.45)));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Money = 105.45", exec.eval());
    }

    public void testFloatString() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal(324.65), maker.Literal(" dollars")));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "324.65 dollars", exec.eval());
    }

    public void testStringDouble() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal("Pi = "), maker.Literal(Math.PI)));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Pi = " + Math.PI, exec.eval());
    }

    public void testDoubleString() throws Exception
    {
        maker.Import("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");
        maker.Implements("au/com/illyrian/classmaker/converters/MakerStringTest$Eval");

        maker.Method("eval", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal(Math.PI), maker.Literal(" radians")));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", Math.PI + " radians", exec.eval());
    }

    public void testStringObject() throws Exception
    {
        maker.Implements(Unary.class);

        maker.Method("unary", ClassMakerFactory.STRING_TYPE, ClassMakerConstants.ACC_PUBLIC);
          maker.Declare("obj", ClassMakerFactory.OBJECT_TYPE, 0);
        maker.Begin();
          maker.Declare("str", ClassMakerFactory.STRING_TYPE, 0);
          maker.Set("str", maker.Add(maker.Literal("Object = "), maker.Get("obj")));
          maker.Return(maker.Get("str"));
        maker.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        Integer obj = new Integer(351);
        assertEquals("Wrong value for exec.eval()", "Object = 351", exec.unary(obj));
        assertEquals("Wrong value for exec.eval()", "Object = Hello World", exec.unary("Hello World"));
        assertEquals("Wrong value for exec.eval()", "Object = " + this.toString(), exec.unary(this));
    }

    public void testAddToStringBuffer() throws Exception
    {
        maker.Implements(Eval.class);

        maker.Method("eval", String.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Declare("buf", StringBuffer.class, 0);
          maker.Set("buf", maker.New(StringBuffer.class).Init(maker.Push()));
          maker.Call(maker.Get("buf"), "append", maker.Push(maker.Literal("Hello")));
          maker.Call(maker.Get("buf"), "append", maker.Push(maker.Literal(" World")));
          maker.Return(maker.Call(maker.Get("buf"), "toString", maker.Push()));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Hello World", exec.eval());
    }

    public void testAutoStringMethodInvocation() throws Exception
    {
        maker.Implements(Eval.class);

        maker.Method("test", String.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("value", String.class, 0);
        maker.Begin();
          maker.Return(maker.Get("value"));
        maker.End();

        maker.Method("eval", String.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
          maker.Return(maker.Call(maker.This(), "test", 
                       maker.Push(maker.Add(maker.Literal("Hello"), maker.Literal(" World")))));
        maker.End();

        Class myClass = maker.defineClass();
        Eval exec = (Eval)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", "Hello World", exec.eval());
    }
}
