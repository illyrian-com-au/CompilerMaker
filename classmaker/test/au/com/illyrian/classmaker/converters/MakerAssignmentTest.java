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
import au.com.illyrian.classmaker.ClassMakerBase;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;

public class MakerAssignmentTest extends ClassMakerTestCase
{
    ClassMakerFactory factory;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
    }

    public void testSetNumber() throws Exception
    {
        ClassMaker classMaker = new ClassMakerBase()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("intField", INT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("longField", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatField", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleField", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("shortField", SHORT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("byteField", BYTE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("charField", CHAR_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("intLocal", INT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("longLocal", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatLocal", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleLocal", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("intStatic", INT_TYPE, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
                Declare("longStatic", LONG_TYPE, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
                Declare("floatStatic", FLOAT_TYPE, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
                Declare("doubleStatic", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
                Declare("intArray", ArrayOf(INT_TYPE), ClassMaker.ACC_PUBLIC);
                Declare("longArray", ArrayOf(LONG_TYPE), ClassMaker.ACC_PUBLIC);
                Declare("floatArray", ArrayOf(FLOAT_TYPE), ClassMaker.ACC_PUBLIC);
                Declare("doubleArray", ArrayOf(DOUBLE_TYPE), ClassMaker.ACC_PUBLIC);

                Method("run", VOID_TYPE, ClassMaker.ACC_PUBLIC);
                Begin();
                Set(This(), "intField", Literal(8));
                Set(This(), "longField", Literal(8));
                Set(This(), "floatField", Literal(8));
                Set(This(), "doubleField", Literal(8));
                // The following must be literal constants.
                Set(This(), "shortField", Literal(8));
                Set(This(), "byteField", Literal(8));
                Set(This(), "charField", Literal('z'));
                // local variables
                Declare("intValue", INT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("longValue", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatValue", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleValue", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);
                Set("intValue", Literal(8));
                Set("longValue", Literal(8));
                Set("floatValue", Literal(8));
                Set("doubleValue", Literal(8));
                // Copy the converted local variables to public fields.
                Set(This(), "intLocal", Get("intValue"));
                Set(This(), "longLocal", Get("longValue"));
                Set(This(), "floatLocal", Get("floatValue"));
                Set(This(), "doubleLocal", Get("doubleValue"));
                // Static variables
                Set(getFullyQualifiedClassName(), "intStatic", Literal(8));
                Set(getFullyQualifiedClassName(), "longStatic", Literal(8));
                Set(getFullyQualifiedClassName(), "floatStatic", Literal(8));
                Set(getFullyQualifiedClassName(), "doubleStatic", Literal(8));
                // Array variables
                Set(This(), "intArray", NewArray(ArrayOf(INT_TYPE), Literal(1)));
                SetAt(Get(This(), "intArray"), Literal(0), Literal(8));
                Set(This(), "longArray", NewArray(ArrayOf(LONG_TYPE), Literal(1)));
                SetAt(Get(This(), "longArray"), Literal(0), Literal(8));
                Set(This(), "floatArray", NewArray(ArrayOf(FLOAT_TYPE), Literal(1)));
                SetAt(Get(This(), "floatArray"), Literal(0), Literal(8));
                Set(This(), "doubleArray", NewArray(ArrayOf(DOUBLE_TYPE), Literal(1)));
                SetAt(Get(This(), "doubleArray"), Literal(0), Literal(8));
                Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable) myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 8L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 8.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 8.0d, getDoubleField(myClass, exec, "doubleField"));
        // The following must be constants
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "byteField"));
        assertEquals("Wrong value", 'z', getCharField(myClass, exec, "charField"));
        // local variables
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "intLocal"));
        assertEquals("Wrong value", 8L, getLongField(myClass, exec, "longLocal"));
        assertEquals("Wrong value", 8.0f, getFloatField(myClass, exec, "floatLocal"));
        assertEquals("Wrong value", 8.0d, getDoubleField(myClass, exec, "doubleLocal"));
        // static variables
        assertEquals("Wrong value", 8, getIntField(myClass, null, "intStatic"));
        assertEquals("Wrong value", 8L, getLongField(myClass, null, "longStatic"));
        assertEquals("Wrong value", 8.0f, getFloatField(myClass, null, "floatStatic"));
        assertEquals("Wrong value", 8.0d, getDoubleField(myClass, null, "doubleStatic"));
        // Array variables
        int[] intArray = (int[]) this.getField(myClass, exec, "intArray");
        assertEquals("Wrong value", 8, intArray[0]);
        long[] longArray = (long[]) this.getField(myClass, exec, "longArray");
        assertEquals("Wrong value", 8L, longArray[0]);
        float[] floatArray = (float[]) this.getField(myClass, exec, "floatArray");
        assertEquals("Wrong value", 8.0f, floatArray[0]);
        double[] doubleArray = (double[]) this.getField(myClass, exec, "doubleArray");
        assertEquals("Wrong value", 8.0d, doubleArray[0]);
    }

    public void testAssignNumber() throws Exception
    {
        ClassMaker classMaker = new ClassMakerBase()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("intField", INT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("longField", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatField", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleField", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("shortField", SHORT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("byteField", BYTE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("charField", CHAR_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("intLocal", INT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("longLocal", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatLocal", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleLocal", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("intStatic", INT_TYPE, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
                Declare("longStatic", LONG_TYPE, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
                Declare("floatStatic", FLOAT_TYPE, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
                Declare("doubleStatic", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
                Declare("intArray", ArrayOf(INT_TYPE), ClassMaker.ACC_PUBLIC);
                Declare("longArray", ArrayOf(LONG_TYPE), ClassMaker.ACC_PUBLIC);
                Declare("floatArray", ArrayOf(FLOAT_TYPE), ClassMaker.ACC_PUBLIC);
                Declare("doubleArray", ArrayOf(DOUBLE_TYPE), ClassMaker.ACC_PUBLIC);

                Method("run", VOID_TYPE, ClassMaker.ACC_PUBLIC);
                Begin();
                Assign(This(), "intField", Literal(8));
                Assign(This(), "longField", Literal(8));
                Assign(This(), "floatField", Literal(8));
                Assign(This(), "doubleField", Literal(8));
                // The following must be literal constants.
                Assign(This(), "shortField", Literal(8));
                Assign(This(), "byteField", Literal(8));
                Assign(This(), "charField", Literal('z'));
                // local variables
                Declare("intValue", INT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("longValue", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatValue", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleValue", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);
                Assign("intValue", Literal(8));
                Assign("longValue", Literal(8));
                Assign("floatValue", Literal(8));
                Assign("doubleValue", Literal(8));
                // Copy the converted local variables to public fields.
                Assign(This(), "intLocal", Get("intValue"));
                Assign(This(), "longLocal", Get("longValue"));
                Assign(This(), "floatLocal", Get("floatValue"));
                Assign(This(), "doubleLocal", Get("doubleValue"));
                // Static variables
                Assign(getFullyQualifiedClassName(), "intStatic", Literal(8));
                Assign(getFullyQualifiedClassName(), "longStatic", Literal(8));
                Assign(getFullyQualifiedClassName(), "floatStatic", Literal(8));
                Assign(getFullyQualifiedClassName(), "doubleStatic", Literal(8));
                // Array variables
                Assign(This(), "intArray", NewArray(ArrayOf(INT_TYPE), Literal(1)));
                AssignAt(Get(This(), "intArray"), Literal(0), Literal(8));
                Assign(This(), "longArray", NewArray(ArrayOf(LONG_TYPE), Literal(1)));
                AssignAt(Get(This(), "longArray"), Literal(0), Literal(8));
                Assign(This(), "floatArray", NewArray(ArrayOf(FLOAT_TYPE), Literal(1)));
                AssignAt(Get(This(), "floatArray"), Literal(0), Literal(8));
                Assign(This(), "doubleArray", NewArray(ArrayOf(DOUBLE_TYPE), Literal(1)));
                AssignAt(Get(This(), "doubleArray"), Literal(0), Literal(8));
                Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable) myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 8L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 8.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 8.0d, getDoubleField(myClass, exec, "doubleField"));
        // The following must be constants
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "byteField"));
        assertEquals("Wrong value", 'z', getCharField(myClass, exec, "charField"));
        // local variables
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "intLocal"));
        assertEquals("Wrong value", 8L, getLongField(myClass, exec, "longLocal"));
        assertEquals("Wrong value", 8.0f, getFloatField(myClass, exec, "floatLocal"));
        assertEquals("Wrong value", 8.0d, getDoubleField(myClass, exec, "doubleLocal"));
        // static variables
        assertEquals("Wrong value", 8, getIntField(myClass, null, "intStatic"));
        assertEquals("Wrong value", 8L, getLongField(myClass, null, "longStatic"));
        assertEquals("Wrong value", 8.0f, getFloatField(myClass, null, "floatStatic"));
        assertEquals("Wrong value", 8.0d, getDoubleField(myClass, null, "doubleStatic"));
        // Array variables
        int[] intArray = (int[]) this.getField(myClass, exec, "intArray");
        assertEquals("Wrong value", 8, intArray[0]);
        long[] longArray = (long[]) this.getField(myClass, exec, "longArray");
        assertEquals("Wrong value", 8L, longArray[0]);
        float[] floatArray = (float[]) this.getField(myClass, exec, "floatArray");
        assertEquals("Wrong value", 8.0f, floatArray[0]);
        double[] doubleArray = (double[]) this.getField(myClass, exec, "doubleArray");
        assertEquals("Wrong value", 8.0d, doubleArray[0]);
    }

    public void testIntAssignmentExceptions()
    {
        ClassMaker maker = factory.createClassMaker("test", "MyClass", null);

        maker.Method("binary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Begin();
        maker.Declare("a", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        try {
            maker.Set("a", maker.Literal((long) 8));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot assign type long to local variable \'a\' of type int", ex.getMessage());
        }
        try {
            maker.Set("a", maker.Literal((float) 8.0));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot assign type float to local variable \'a\' of type int", ex.getMessage());
        }
        try {
            maker.Set("a", maker.Literal((double) 8.0));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", "Cannot assign type double to local variable \'a\' of type int", ex.getMessage());
        }
    }

    public void testByteAssignment() throws Exception
    {
        ClassMaker classMaker = new ClassMakerBase()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("intField", INT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("longField", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatField", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleField", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("shortField", SHORT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("byteField", BYTE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("charField", CHAR_TYPE, ClassMaker.ACC_PUBLIC);

                Method("run", VOID_TYPE, ClassMaker.ACC_PUBLIC);
                Begin();
                Set(This(), "intField", Literal((byte) 8));
                Set(This(), "longField", Literal((byte) 8));
                Set(This(), "floatField", Literal((byte) 8));
                Set(This(), "doubleField", Literal((byte) 8));
                Set(This(), "shortField", Literal((byte) 8));
                Set(This(), "byteField", Literal((byte) 8));
                Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable) myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 8L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 8.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 8.0d, getDoubleField(myClass, exec, "doubleField"));
        // The following must be constants
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "shortField"));
        assertEquals("Wrong value", 8, getIntField(myClass, exec, "byteField"));
    }

    public void testShortAssignment() throws Exception
    {
        ClassMaker classMaker = new ClassMakerBase()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("intField", INT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("longField", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatField", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleField", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("shortField", SHORT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("byteField", BYTE_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("charField", CHAR_TYPE, ClassMaker.ACC_PUBLIC);

                Method("run", VOID_TYPE, ClassMaker.ACC_PUBLIC);
                Begin();
                Set(This(), "intField", Literal((short) 308));
                Set(This(), "longField", Literal((short) 308));
                Set(This(), "floatField", Literal((short) 308));
                Set(This(), "doubleField", Literal((short) 308));
                Set(This(), "shortField", Literal((short) 308));
                Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable) myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 308, getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 308L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 308.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 308.0d, getDoubleField(myClass, exec, "doubleField"));
        assertEquals("Wrong value", 308, getIntField(myClass, exec, "shortField"));
    }

    public void testShortException() throws Exception
    {
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        maker.Declare("byteField", ClassMaker.BYTE_TYPE, ClassMaker.ACC_PUBLIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Begin();
        try
        {
            maker.Set(maker.This(), "byteField", maker.Literal((short) 308));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.byteField of type byte cannot be assigned type short", ex.getMessage());
        }
    }

    public void testCharAssignment() throws Exception
    {
        ClassMaker classMaker = new ClassMakerBase()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("charField", char.class, ClassMaker.ACC_PUBLIC);
                Declare("intField", int.class, ClassMaker.ACC_PUBLIC);
                Declare("longField", long.class, ClassMaker.ACC_PUBLIC);
                Declare("floatField", float.class, ClassMaker.ACC_PUBLIC);
                Declare("doubleField", double.class, ClassMaker.ACC_PUBLIC);

                Method("run", VOID_TYPE, ClassMaker.ACC_PUBLIC);
                Begin();
                Set(This(), "charField", Literal('z'));
                Set(This(), "intField", Literal('z'));
                Set(This(), "longField", Literal('z'));
                Set(This(), "floatField", Literal('z'));
                Set(This(), "doubleField", Literal('z'));
                Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable) myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 'z', getCharField(myClass, exec, "charField"));
        assertEquals("Wrong value", 'z', getIntField(myClass, exec, "intField"));
        assertEquals("Wrong value", 'z', getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", (float)'z', getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", (double)'z', getDoubleField(myClass, exec, "doubleField"));
    }

    public void testCharException() throws Exception
    {
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        maker.Declare("byteField", ClassMaker.BYTE_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("shortField", ClassMaker.SHORT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("charField", ClassMaker.CHAR_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("intField", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("longField", ClassMaker.LONG_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("floatField", ClassMaker.FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("doubleField", ClassMaker.DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Begin();
        try
        {
            maker.Set(maker.This(), "byteField", maker.Literal('z'));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.byteField of type byte cannot be assigned type char", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "shortField", maker.Literal('z'));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.shortField of type short cannot be assigned type char", ex.getMessage());
        }
    }

    public void testLongAssignment() throws Exception
    {
        ClassMaker classMaker = new ClassMakerBase()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("longField", LONG_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("floatField", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleField", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);

                Method("run", VOID_TYPE, ClassMaker.ACC_PUBLIC);
                Begin();
                Set(This(), "longField", Literal(987654321L));
                Set(This(), "floatField", Literal(987654321L));
                Set(This(), "doubleField", Literal(987654321L));
                Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable) myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 987654321L, getLongField(myClass, exec, "longField"));
        assertEquals("Wrong value", 987654321.0f, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", 987654321.0d, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testLongException() throws Exception
    {
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        maker.Declare("byteField", ClassMaker.BYTE_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("intField", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("shortField", ClassMaker.SHORT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("charField", ClassMaker.CHAR_TYPE, ClassMaker.ACC_PUBLIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Begin();
        try
        {
            maker.Set(maker.This(), "byteField", maker.Literal(987654321L));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.byteField of type byte cannot be assigned type long", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "shortField", maker.Literal(987654321L));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.shortField of type short cannot be assigned type long", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "charField", maker.Literal(987654321L));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.charField of type char cannot be assigned type long", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "intField", maker.Literal(987654321L));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.intField of type int cannot be assigned type long", ex.getMessage());
        }
    }

    public void testFloatAssignment() throws Exception
    {
        ClassMaker classMaker = new ClassMakerBase()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("floatField", FLOAT_TYPE, ClassMaker.ACC_PUBLIC);
                Declare("doubleField", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);

                Method("run", VOID_TYPE, ClassMaker.ACC_PUBLIC);
                Begin();
                Set(This(), "floatField", Literal(98765.43210000F));
                Set(This(), "doubleField", Literal(98765.43210000F));
                Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable) myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 98765.4321F, getFloatField(myClass, exec, "floatField"));
        assertEquals("Wrong value", (double) 98765.4321F, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testFloatException() throws Exception
    {
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        maker.Declare("byteField", ClassMaker.BYTE_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("intField", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("shortField", ClassMaker.SHORT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("charField", ClassMaker.CHAR_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("longField", ClassMaker.LONG_TYPE, ClassMaker.ACC_PUBLIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Begin();
        try
        {
            maker.Set(maker.This(), "byteField", maker.Literal(98765.4321F));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.byteField of type byte cannot be assigned type float", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "shortField", maker.Literal(98765.4321F));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.shortField of type short cannot be assigned type float", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "charField", maker.Literal(98765.4321F));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.charField of type char cannot be assigned type float", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "intField", maker.Literal(98765.4321F));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.intField of type int cannot be assigned type float", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "longField", maker.Literal(98765.4321F));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.longField of type long cannot be assigned type float", ex.getMessage());
        }
    }

    public void testDoubleAssignment() throws Exception
    {
        ClassMaker classMaker = new ClassMakerBase()
        {
            public void code()
            {
                Implements(Runnable.class);
                Declare("doubleField", DOUBLE_TYPE, ClassMaker.ACC_PUBLIC);

                Method("run", VOID_TYPE, ClassMaker.ACC_PUBLIC);
                Begin();
                Set(This(), "doubleField", Literal(98765.43210123D));
                Return();
                End();
            }
        };

        Class myClass = classMaker.defineClass();
        Runnable exec = (Runnable) myClass.newInstance();

        exec.run();
        assertEquals("Wrong value", 98765.43210123D, getDoubleField(myClass, exec, "doubleField"));
    }

    public void testDoubleException() throws Exception
    {
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        maker.Declare("byteField", ClassMaker.BYTE_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("intField", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("shortField", ClassMaker.SHORT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("charField", ClassMaker.CHAR_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("longField", ClassMaker.LONG_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("floatField", ClassMaker.FLOAT_TYPE, ClassMaker.ACC_PUBLIC);

        maker.Method("run", ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Begin();
        try
        {
            maker.Set(maker.This(), "byteField", maker.Literal(98765.43210123D));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.byteField of type byte cannot be assigned type double", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "shortField", maker.Literal(98765.43210123D));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.shortField of type short cannot be assigned type double", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "charField", maker.Literal(98765.43210123D));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.charField of type char cannot be assigned type double", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "intField", maker.Literal(98765.43210123D));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.intField of type int cannot be assigned type double", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "longField", maker.Literal(98765.43210123D));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.longField of type long cannot be assigned type double", ex.getMessage());
        }
        try
        {
            maker.Set(maker.This(), "floatField", maker.Literal(98765.43210123D));
            fail("Should throw ClassMakerException.");
        } catch (ClassMakerException ex)
        {
            assertEquals("Field test.MyClass.floatField of type float cannot be assigned type double", ex.getMessage());
        }
    }
}
