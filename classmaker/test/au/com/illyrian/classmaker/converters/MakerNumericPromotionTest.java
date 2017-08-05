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

import org.mozilla.classfile.ByteCode;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerCode;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.types.PrimitiveType;

public class MakerNumericPromotionTest extends ClassMakerTestCase implements ByteCode
{
    // Generate default constructor
    public void defaultConstructor(ClassMaker maker)
    {
        maker.Method("<init>", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public void testByteLeftPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("byteField", ClassMakerFactory.BYTE_TYPE, ACC_PUBLIC);
                Declare("shortField", ClassMakerFactory.SHORT_TYPE, ACC_PUBLIC);
                Declare("intField", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
                Declare("longField", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "byteField", Cast(Add(Literal((byte)50), Literal((byte)100)), byte.class));
                    Set(This(), "shortField", Cast(Add(Literal((byte)50), Literal((short)100)), short.class));
                    Set(This(), "intField", Add(Literal((byte)50), Literal(100)));
                    Set(This(), "longField", Add(Literal((byte)50), Literal(100L)));
                    Set(This(), "floatField", Add(Literal((byte)50), Literal(100.0f)));
                    Set(This(), "doubleField", Add(Literal((byte)50), Literal(100.0d)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", -106, getIntField(myClass, exec, "byteField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 150L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testByteRightPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("byteField", ClassMakerFactory.BYTE_TYPE, ACC_PUBLIC);
                Declare("shortField", ClassMakerFactory.SHORT_TYPE, ACC_PUBLIC);
                Declare("intField", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
                Declare("longField", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "byteField", Cast(Add(Literal((byte)50), Literal((byte)100)), byte.class));
                    Set(This(), "shortField", Cast(Add(Literal((short)50), Literal((byte)100)), short.class));
                    Set(This(), "intField", Add(Literal(100), Literal((byte)50)));
                    Set(This(), "longField", Add(Literal(100L), Literal((byte)50)));
                    Set(This(), "floatField", Add(Literal(100.0f), Literal((byte)50)));
                    Set(This(), "doubleField", Add(Literal(100.0d), Literal((byte)50)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", -106, getIntField(myClass, exec, "byteField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 150L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testShortLeftPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("byteField", ClassMakerFactory.BYTE_TYPE, ACC_PUBLIC);
                Declare("shortField", ClassMakerFactory.SHORT_TYPE, ACC_PUBLIC);
                Declare("intField", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
                Declare("longField", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "byteField", Cast(Add(Literal((short)50), Literal((byte)100)), byte.class));
                    Set(This(), "shortField", Cast(Add(Literal((short)50), Literal((short)100)), short.class));
                    Set(This(), "intField", Add(Literal((short)50), Literal(100)));
                    Set(This(), "longField", Add(Literal((short)50), Literal(100L)));
                    Set(This(), "floatField", Add(Literal((short)50), Literal(100.0f)));
                    Set(This(), "doubleField", Add(Literal((short)50), Literal(100.0d)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", -106, getIntField(myClass, exec, "byteField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 150L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testShortRightPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("byteField", ClassMakerFactory.BYTE_TYPE, ACC_PUBLIC);
                Declare("shortField", ClassMakerFactory.SHORT_TYPE, ACC_PUBLIC);
                Declare("intField", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
                Declare("longField", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "byteField", Cast(Add(Literal((byte)50), Literal((short)100)), byte.class));
                    Set(This(), "shortField", Cast(Add(Literal((short)50), Literal((short)100)), short.class));
                    Set(This(), "intField", Add(Literal(100), Literal((short)50)));
                    Set(This(), "longField", Add(Literal(100L), Literal((short)50)));
                    Set(This(), "floatField", Add(Literal(100.0f), Literal((short)50)));
                    Set(This(), "doubleField", Add(Literal(100.0d), Literal((short)50)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", -106, getIntField(myClass, exec, "byteField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 150L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testIntLeftPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("byteField", ClassMakerFactory.BYTE_TYPE, ACC_PUBLIC);
                Declare("shortField", ClassMakerFactory.SHORT_TYPE, ACC_PUBLIC);
                Declare("intField", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
                Declare("longField", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "byteField", Cast(Add(Literal(50), Literal((byte)100)), byte.class));
                    Set(This(), "shortField", Cast(Add(Literal(50), Literal((short)100)), short.class));
                    Set(This(), "intField", Add(Literal(50), Literal(100)));
                    Set(This(), "longField", Add(Literal(50), Literal(100L)));
                    Set(This(), "floatField", Add(Literal(50), Literal(100.0f)));
                    Set(This(), "doubleField", Add(Literal(50), Literal(100.0d)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", -106, getIntField(myClass, exec, "byteField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 150L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testIntRightPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("byteField", ClassMakerFactory.BYTE_TYPE, ACC_PUBLIC);
                Declare("shortField", ClassMakerFactory.SHORT_TYPE, ACC_PUBLIC);
                Declare("intField", ClassMakerFactory.INT_TYPE, ACC_PUBLIC);
                Declare("longField", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "byteField", Cast(Add(Literal((byte)50), Literal(100)), byte.class));
                    Set(This(), "shortField", Cast(Add(Literal((short)50), Literal(100)), short.class));
                    Set(This(), "intField", Add(Literal(100), Literal(50)));
                    Set(This(), "longField", Add(Literal(100L), Literal(50)));
                    Set(This(), "floatField", Add(Literal(100.0f), Literal(50)));
                    Set(This(), "doubleField", Add(Literal(100.0d), Literal(50)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", -106, getIntField(myClass, exec, "byteField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 150, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 150L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testLongLeftPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("longField", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "longField", Add(Literal((long)50), Literal(100L)));
                    Set(This(), "floatField", Add(Literal((long)50), Literal(100.0f)));
                    Set(This(), "doubleField", Add(Literal((long)50), Literal(100.0d)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 150L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testLongRightPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("longField", ClassMakerFactory.LONG_TYPE, ACC_PUBLIC);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "longField", Add(Literal(100L), Literal((long)50)));
                    Set(This(), "floatField", Add(Literal(100.0f), Literal((long)50)));
                    Set(This(), "doubleField", Add(Literal(100.0d), Literal((long)50)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 150L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testFloatLeftPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "floatField", Add(Literal((float)50.0), Literal(100.0f)));
                    Set(This(), "doubleField", Add(Literal((float)50.0), Literal(100.0d)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testFloatRightPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("floatField", ClassMakerFactory.FLOAT_TYPE, ACC_PUBLIC);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "floatField", Add(Literal(100.0f), Literal((float)50.0)));
                    Set(This(), "doubleField", Add(Literal(100.0d), Literal((float)50.0)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 150.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testDoubleRightPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "doubleField", Add(Literal(100.0d), Literal((double)50.0)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testDoubleleftPromotion() throws Exception
    {
        ClassMaker classMaker = new ClassMakerCode()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("doubleField", ClassMakerFactory.DOUBLE_TYPE, ACC_PUBLIC);

                Method("run", ClassMakerFactory.VOID_TYPE, ACC_PUBLIC);
                Begin();
                    Set(This(), "doubleField", Add(Literal((double)50.0), Literal(100.0d)));
                    Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable)myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 150.0d, getDoubleField(myClass, exec, "doubleField"));
    }
}
