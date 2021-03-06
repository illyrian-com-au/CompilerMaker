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
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

public class MakerMethodConversionTest extends ClassMakerTestCase
{
    MyClassMakerFactory factory;
    MethodInvocationConversion converter;

    public void setUp() throws Exception
    {
        factory = new MyClassMakerFactory();
        converter = factory.getMethodInvocationConversion();
    }

    // Generate default constructor
    public void defaultConstructor(ClassMaker maker) throws Exception
    {
        maker.Method("<init>", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public void isConvertable(boolean expected, Type actual, Type formal)
    {
        if (expected)
        {
                String msg = "Type " + actual.getName() + " should be assignable to Type " + formal.getName();
                assertTrue(msg, converter.isConvertable(actual, formal));
        }
        else
        {
                String msg = "Type " + actual.getName() + " should NOT be assignable to Type " + formal.getName();
                assertFalse(msg, converter.isConvertable(actual, formal));
        }
    }

    public void testIsConvertablePrimitive()
    {
        // byte
        isConvertable(true,  ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.BYTE_TYPE);
        isConvertable(false, ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.CHAR_TYPE);
        isConvertable(false, ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.DOUBLE_TYPE);
        isConvertable(false, ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.FLOAT_TYPE);
        isConvertable(true,  ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.INT_TYPE);
        isConvertable(false, ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.LONG_TYPE);
        isConvertable(true,  ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.SHORT_TYPE);
        isConvertable(false, ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.BOOLEAN_TYPE);

        // char
        isConvertable(false, ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.BYTE_TYPE);
        isConvertable(true,  ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.CHAR_TYPE);
        isConvertable(false, ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.DOUBLE_TYPE);
        isConvertable(false, ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.FLOAT_TYPE);
        isConvertable(true,  ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.INT_TYPE);
        isConvertable(false, ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.LONG_TYPE);
        isConvertable(false, ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.SHORT_TYPE);
        isConvertable(false, ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.BOOLEAN_TYPE);

        // double
        isConvertable(false, ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.BYTE_TYPE);
        isConvertable(false, ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.CHAR_TYPE);
        isConvertable(true,  ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.DOUBLE_TYPE);
        isConvertable(false, ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.FLOAT_TYPE);
        isConvertable(false, ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.INT_TYPE);
        isConvertable(false, ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.LONG_TYPE);
        isConvertable(false, ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.SHORT_TYPE);
        isConvertable(false, ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.BOOLEAN_TYPE);

        // float
        isConvertable(false, ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.BYTE_TYPE);
        isConvertable(false, ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.CHAR_TYPE);
        isConvertable(false, ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.DOUBLE_TYPE);
        isConvertable(true,  ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.FLOAT_TYPE);
        isConvertable(false, ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.INT_TYPE);
        isConvertable(false, ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.LONG_TYPE);
        isConvertable(false, ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.SHORT_TYPE);
        isConvertable(false, ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.BOOLEAN_TYPE);

        // int
        isConvertable(false, ClassMakerFactory.INT_TYPE, ClassMakerFactory.BYTE_TYPE);
        isConvertable(false, ClassMakerFactory.INT_TYPE, ClassMakerFactory.CHAR_TYPE);
        isConvertable(false, ClassMakerFactory.INT_TYPE, ClassMakerFactory.DOUBLE_TYPE);
        isConvertable(false, ClassMakerFactory.INT_TYPE, ClassMakerFactory.FLOAT_TYPE);
        isConvertable(true,  ClassMakerFactory.INT_TYPE, ClassMakerFactory.INT_TYPE);
        isConvertable(false, ClassMakerFactory.INT_TYPE, ClassMakerFactory.LONG_TYPE);
        isConvertable(false, ClassMakerFactory.INT_TYPE, ClassMakerFactory.SHORT_TYPE);
        isConvertable(false, ClassMakerFactory.INT_TYPE, ClassMakerFactory.BOOLEAN_TYPE);

        // long
        isConvertable(false, ClassMakerFactory.LONG_TYPE, ClassMakerFactory.BYTE_TYPE);
        isConvertable(false, ClassMakerFactory.LONG_TYPE, ClassMakerFactory.CHAR_TYPE);
        isConvertable(false, ClassMakerFactory.LONG_TYPE, ClassMakerFactory.DOUBLE_TYPE);
        isConvertable(false, ClassMakerFactory.LONG_TYPE, ClassMakerFactory.FLOAT_TYPE);
        isConvertable(false, ClassMakerFactory.LONG_TYPE, ClassMakerFactory.INT_TYPE);
        isConvertable(true,  ClassMakerFactory.LONG_TYPE, ClassMakerFactory.LONG_TYPE);
        isConvertable(false, ClassMakerFactory.LONG_TYPE, ClassMakerFactory.SHORT_TYPE);
        isConvertable(false, ClassMakerFactory.LONG_TYPE, ClassMakerFactory.BOOLEAN_TYPE);

        // short
        isConvertable(false, ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.BYTE_TYPE);
        isConvertable(false, ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.CHAR_TYPE);
        isConvertable(false, ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.DOUBLE_TYPE);
        isConvertable(false, ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.FLOAT_TYPE);
        isConvertable(true,  ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.INT_TYPE);
        isConvertable(false, ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.LONG_TYPE);
        isConvertable(true,  ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.SHORT_TYPE);
        isConvertable(false, ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.BOOLEAN_TYPE);

        // boolean
        isConvertable(false, ClassMakerFactory.BOOLEAN_TYPE, ClassMakerFactory.BYTE_TYPE);
        isConvertable(false, ClassMakerFactory.BOOLEAN_TYPE, ClassMakerFactory.CHAR_TYPE);
        isConvertable(false, ClassMakerFactory.BOOLEAN_TYPE, ClassMakerFactory.DOUBLE_TYPE);
        isConvertable(false, ClassMakerFactory.BOOLEAN_TYPE, ClassMakerFactory.FLOAT_TYPE);
        isConvertable(false, ClassMakerFactory.BOOLEAN_TYPE, ClassMakerFactory.INT_TYPE);
        isConvertable(false, ClassMakerFactory.BOOLEAN_TYPE, ClassMakerFactory.LONG_TYPE);
        isConvertable(false, ClassMakerFactory.BOOLEAN_TYPE, ClassMakerFactory.SHORT_TYPE);
        isConvertable(true,  ClassMakerFactory.BOOLEAN_TYPE, ClassMakerFactory.BOOLEAN_TYPE);

    }

    public void testIsAssignableClass()
    {
        isConvertable(true,  ClassMakerFactory.OBJECT_TYPE, ClassMakerFactory.OBJECT_TYPE);
        isConvertable(true,  ClassMakerFactory.STRING_TYPE, ClassMakerFactory.STRING_TYPE);
        isConvertable(false, ClassMakerFactory.OBJECT_TYPE, ClassMakerFactory.STRING_TYPE);
        isConvertable(true,  ClassMakerFactory.STRING_TYPE, ClassMakerFactory.OBJECT_TYPE);
    }

    public void testIsAssignableArray()
    {
        isConvertable(true,  factory.typeToArray(ClassMakerFactory.OBJECT_TYPE), factory.typeToArray(ClassMakerFactory.OBJECT_TYPE));
        isConvertable(true,  factory.typeToArray(ClassMakerFactory.STRING_TYPE), factory.typeToArray(ClassMakerFactory.STRING_TYPE));
        isConvertable(false, factory.typeToArray(ClassMakerFactory.OBJECT_TYPE), factory.typeToArray(ClassMakerFactory.STRING_TYPE));
        isConvertable(true,  factory.typeToArray(ClassMakerFactory.STRING_TYPE), factory.typeToArray(ClassMakerFactory.OBJECT_TYPE));
    }

    public void testPrimitiveConversion()
    {
        assertTrue("byte -> byte", converter.isConvertable(ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.BYTE_TYPE));
        assertTrue("byte -> short", converter.isConvertable(ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.SHORT_TYPE));
        assertTrue("byte -> int", converter.isConvertable(ClassMakerFactory.BYTE_TYPE, ClassMakerFactory.INT_TYPE));
        assertTrue("short -> short", converter.isConvertable(ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.SHORT_TYPE));
        assertTrue("short -> int", converter.isConvertable(ClassMakerFactory.SHORT_TYPE, ClassMakerFactory.INT_TYPE));
        assertTrue("int -> int", converter.isConvertable(ClassMakerFactory.INT_TYPE, ClassMakerFactory.INT_TYPE));
        assertTrue("char -> char", converter.isConvertable(ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.CHAR_TYPE));
        assertTrue("char -> int", converter.isConvertable(ClassMakerFactory.CHAR_TYPE, ClassMakerFactory.INT_TYPE));
        assertTrue("long -> long", converter.isConvertable(ClassMakerFactory.LONG_TYPE, ClassMakerFactory.LONG_TYPE));
        assertTrue("float -> float", converter.isConvertable(ClassMakerFactory.FLOAT_TYPE, ClassMakerFactory.FLOAT_TYPE));
        assertTrue("double -> double", converter.isConvertable(ClassMakerFactory.DOUBLE_TYPE, ClassMakerFactory.DOUBLE_TYPE));
    }

    final ClassType DESSERT = new ClassType("test.Dessert", ClassMakerFactory.OBJECT_TYPE);;
    final ClassType CAKE = new ClassType("test.Cake", DESSERT);
    final ClassType SCONE = new ClassType("test.Scone", DESSERT);
    final ClassType CHOCOLATE_CAKE = new ClassType("test.ChocolateCake", CAKE);
    final ClassType BUTTERED_SCONE = new ClassType("test.ButteredScone", SCONE);
    ArrayType OBJECT_ARRAY;
    ArrayType DESSERT_ARRAY;
    ArrayType CAKE_ARRAY;
    ArrayType SCONE_ARRAY;
    ArrayType CHOCOLATE_CAKE_ARRAY;
    ArrayType BUTTERED_SCONE_ARRAY;

    class MyClassMakerFactory extends ClassMakerFactory
    {
        MyClassMakerFactory()
        {
            super();
            addLocalClasses();
        }

        protected void addLocalClasses()
        {
            addType(DESSERT); 
            addType(CAKE);
            addType(SCONE);
            addType(CHOCOLATE_CAKE);
            addType(BUTTERED_SCONE);

            OBJECT_ARRAY  = addArrayOfType(ClassMakerFactory.OBJECT_TYPE);
            DESSERT_ARRAY = addArrayOfType(DESSERT);
            CAKE_ARRAY    = addArrayOfType(CAKE);
            SCONE_ARRAY   = addArrayOfType(SCONE);
            CHOCOLATE_CAKE_ARRAY = addArrayOfType(CHOCOLATE_CAKE);
            BUTTERED_SCONE_ARRAY = addArrayOfType(BUTTERED_SCONE);
        }
    }


    public void testReferenceConversion()
    {
        assertTrue("Dessert -> Object", converter.isConvertable(DESSERT, ClassMakerFactory.OBJECT_TYPE));
        assertTrue("Cake -> Object", converter.isConvertable(CAKE, ClassMakerFactory.OBJECT_TYPE));
        assertTrue("Scone -> Object", converter.isConvertable(SCONE, ClassMakerFactory.OBJECT_TYPE));
        assertTrue("ButteredScone -> Scone", converter.isConvertable(BUTTERED_SCONE, SCONE));
        assertTrue("ChocolateCake -> Cake", converter.isConvertable(CHOCOLATE_CAKE, CAKE));
        assertTrue("null -> Object", converter.isConvertable(ClassMakerFactory.NULL_TYPE, ClassMakerFactory.OBJECT_TYPE));
        assertTrue("null -> Cake", converter.isConvertable(ClassMakerFactory.NULL_TYPE, CAKE));
        assertTrue("null -> ButteredScone", converter.isConvertable(ClassMakerFactory.NULL_TYPE, BUTTERED_SCONE));
        // Not convertable
        assertFalse("ButteredScone -> Cake", converter.isConvertable(BUTTERED_SCONE, CAKE));
        assertFalse("Scone -> ChocolateCake", converter.isConvertable(SCONE, CHOCOLATE_CAKE));
    }

    public void testArrayReferenceConversion()
    {
        assertTrue("Dessert[] -> Object", converter.isConvertable(DESSERT_ARRAY, ClassMakerFactory.OBJECT_TYPE));
        assertTrue("Cake[] -> Object[]", converter.isConvertable(CAKE_ARRAY, OBJECT_ARRAY));
        assertTrue("Scone[] -> Object", converter.isConvertable(SCONE_ARRAY, ClassMakerFactory.OBJECT_TYPE));
        assertTrue("ButteredScone[] -> Scone[]", converter.isConvertable(BUTTERED_SCONE_ARRAY, SCONE_ARRAY));
        assertTrue("ChocolateCake[] -> Cake[]", converter.isConvertable(CHOCOLATE_CAKE_ARRAY, CAKE_ARRAY));
        assertTrue("null -> Object[]", converter.isConvertable(ClassMakerFactory.NULL_TYPE, OBJECT_ARRAY));
        assertTrue("null -> Cake[]", converter.isConvertable(ClassMakerFactory.NULL_TYPE, CAKE_ARRAY));
        assertTrue("null -> Scone[]", converter.isConvertable(ClassMakerFactory.NULL_TYPE, SCONE_ARRAY));
        assertTrue("null -> ButteredScone[]", converter.isConvertable(ClassMakerFactory.NULL_TYPE, BUTTERED_SCONE_ARRAY));
        // Not convertable
        assertFalse("ButteredScone -> Cake", converter.isConvertable(BUTTERED_SCONE_ARRAY, CAKE_ARRAY));
        assertFalse("Scone -> ChocolateCake", converter.isConvertable(SCONE_ARRAY, CHOCOLATE_CAKE_ARRAY));
    }

}
