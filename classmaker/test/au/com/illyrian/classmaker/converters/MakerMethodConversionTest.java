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
        maker.Method("<init>", ClassMaker.VOID_TYPE, ByteCode.ACC_PUBLIC);
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
        isConvertable(true,  ClassMaker.BYTE_TYPE, ClassMaker.BYTE_TYPE);
        isConvertable(false, ClassMaker.BYTE_TYPE, ClassMaker.CHAR_TYPE);
        isConvertable(false, ClassMaker.BYTE_TYPE, ClassMaker.DOUBLE_TYPE);
        isConvertable(false, ClassMaker.BYTE_TYPE, ClassMaker.FLOAT_TYPE);
        isConvertable(true,  ClassMaker.BYTE_TYPE, ClassMaker.INT_TYPE);
        isConvertable(false, ClassMaker.BYTE_TYPE, ClassMaker.LONG_TYPE);
        isConvertable(true,  ClassMaker.BYTE_TYPE, ClassMaker.SHORT_TYPE);
        isConvertable(false, ClassMaker.BYTE_TYPE, ClassMaker.BOOLEAN_TYPE);

        // char
        isConvertable(false, ClassMaker.CHAR_TYPE, ClassMaker.BYTE_TYPE);
        isConvertable(true,  ClassMaker.CHAR_TYPE, ClassMaker.CHAR_TYPE);
        isConvertable(false, ClassMaker.CHAR_TYPE, ClassMaker.DOUBLE_TYPE);
        isConvertable(false, ClassMaker.CHAR_TYPE, ClassMaker.FLOAT_TYPE);
        isConvertable(true,  ClassMaker.CHAR_TYPE, ClassMaker.INT_TYPE);
        isConvertable(false, ClassMaker.CHAR_TYPE, ClassMaker.LONG_TYPE);
        isConvertable(false, ClassMaker.CHAR_TYPE, ClassMaker.SHORT_TYPE);
        isConvertable(false, ClassMaker.CHAR_TYPE, ClassMaker.BOOLEAN_TYPE);

        // double
        isConvertable(false, ClassMaker.DOUBLE_TYPE, ClassMaker.BYTE_TYPE);
        isConvertable(false, ClassMaker.DOUBLE_TYPE, ClassMaker.CHAR_TYPE);
        isConvertable(true,  ClassMaker.DOUBLE_TYPE, ClassMaker.DOUBLE_TYPE);
        isConvertable(false, ClassMaker.DOUBLE_TYPE, ClassMaker.FLOAT_TYPE);
        isConvertable(false, ClassMaker.DOUBLE_TYPE, ClassMaker.INT_TYPE);
        isConvertable(false, ClassMaker.DOUBLE_TYPE, ClassMaker.LONG_TYPE);
        isConvertable(false, ClassMaker.DOUBLE_TYPE, ClassMaker.SHORT_TYPE);
        isConvertable(false, ClassMaker.DOUBLE_TYPE, ClassMaker.BOOLEAN_TYPE);

        // float
        isConvertable(false, ClassMaker.FLOAT_TYPE, ClassMaker.BYTE_TYPE);
        isConvertable(false, ClassMaker.FLOAT_TYPE, ClassMaker.CHAR_TYPE);
        isConvertable(false, ClassMaker.FLOAT_TYPE, ClassMaker.DOUBLE_TYPE);
        isConvertable(true,  ClassMaker.FLOAT_TYPE, ClassMaker.FLOAT_TYPE);
        isConvertable(false, ClassMaker.FLOAT_TYPE, ClassMaker.INT_TYPE);
        isConvertable(false, ClassMaker.FLOAT_TYPE, ClassMaker.LONG_TYPE);
        isConvertable(false, ClassMaker.FLOAT_TYPE, ClassMaker.SHORT_TYPE);
        isConvertable(false, ClassMaker.FLOAT_TYPE, ClassMaker.BOOLEAN_TYPE);

        // int
        isConvertable(false, ClassMaker.INT_TYPE, ClassMaker.BYTE_TYPE);
        isConvertable(false, ClassMaker.INT_TYPE, ClassMaker.CHAR_TYPE);
        isConvertable(false, ClassMaker.INT_TYPE, ClassMaker.DOUBLE_TYPE);
        isConvertable(false, ClassMaker.INT_TYPE, ClassMaker.FLOAT_TYPE);
        isConvertable(true,  ClassMaker.INT_TYPE, ClassMaker.INT_TYPE);
        isConvertable(false, ClassMaker.INT_TYPE, ClassMaker.LONG_TYPE);
        isConvertable(false, ClassMaker.INT_TYPE, ClassMaker.SHORT_TYPE);
        isConvertable(false, ClassMaker.INT_TYPE, ClassMaker.BOOLEAN_TYPE);

        // long
        isConvertable(false, ClassMaker.LONG_TYPE, ClassMaker.BYTE_TYPE);
        isConvertable(false, ClassMaker.LONG_TYPE, ClassMaker.CHAR_TYPE);
        isConvertable(false, ClassMaker.LONG_TYPE, ClassMaker.DOUBLE_TYPE);
        isConvertable(false, ClassMaker.LONG_TYPE, ClassMaker.FLOAT_TYPE);
        isConvertable(false, ClassMaker.LONG_TYPE, ClassMaker.INT_TYPE);
        isConvertable(true,  ClassMaker.LONG_TYPE, ClassMaker.LONG_TYPE);
        isConvertable(false, ClassMaker.LONG_TYPE, ClassMaker.SHORT_TYPE);
        isConvertable(false, ClassMaker.LONG_TYPE, ClassMaker.BOOLEAN_TYPE);

        // short
        isConvertable(false, ClassMaker.SHORT_TYPE, ClassMaker.BYTE_TYPE);
        isConvertable(false, ClassMaker.SHORT_TYPE, ClassMaker.CHAR_TYPE);
        isConvertable(false, ClassMaker.SHORT_TYPE, ClassMaker.DOUBLE_TYPE);
        isConvertable(false, ClassMaker.SHORT_TYPE, ClassMaker.FLOAT_TYPE);
        isConvertable(true,  ClassMaker.SHORT_TYPE, ClassMaker.INT_TYPE);
        isConvertable(false, ClassMaker.SHORT_TYPE, ClassMaker.LONG_TYPE);
        isConvertable(true,  ClassMaker.SHORT_TYPE, ClassMaker.SHORT_TYPE);
        isConvertable(false, ClassMaker.SHORT_TYPE, ClassMaker.BOOLEAN_TYPE);

        // boolean
        isConvertable(false, ClassMaker.BOOLEAN_TYPE, ClassMaker.BYTE_TYPE);
        isConvertable(false, ClassMaker.BOOLEAN_TYPE, ClassMaker.CHAR_TYPE);
        isConvertable(false, ClassMaker.BOOLEAN_TYPE, ClassMaker.DOUBLE_TYPE);
        isConvertable(false, ClassMaker.BOOLEAN_TYPE, ClassMaker.FLOAT_TYPE);
        isConvertable(false, ClassMaker.BOOLEAN_TYPE, ClassMaker.INT_TYPE);
        isConvertable(false, ClassMaker.BOOLEAN_TYPE, ClassMaker.LONG_TYPE);
        isConvertable(false, ClassMaker.BOOLEAN_TYPE, ClassMaker.SHORT_TYPE);
        isConvertable(true,  ClassMaker.BOOLEAN_TYPE, ClassMaker.BOOLEAN_TYPE);

    }

    public void testIsAssignableClass()
    {
        isConvertable(true,  ClassMaker.OBJECT_TYPE, ClassMaker.OBJECT_TYPE);
        isConvertable(true,  ClassMaker.STRING_TYPE, ClassMaker.STRING_TYPE);
        isConvertable(false, ClassMaker.OBJECT_TYPE, ClassMaker.STRING_TYPE);
        isConvertable(true,  ClassMaker.STRING_TYPE, ClassMaker.OBJECT_TYPE);
    }

    public void testIsAssignableArray()
    {
        isConvertable(true,  factory.typeToArray(ClassMaker.OBJECT_TYPE), factory.typeToArray(ClassMaker.OBJECT_TYPE));
        isConvertable(true,  factory.typeToArray(ClassMaker.STRING_TYPE), factory.typeToArray(ClassMaker.STRING_TYPE));
        isConvertable(false, factory.typeToArray(ClassMaker.OBJECT_TYPE), factory.typeToArray(ClassMaker.STRING_TYPE));
        isConvertable(true,  factory.typeToArray(ClassMaker.STRING_TYPE), factory.typeToArray(ClassMaker.OBJECT_TYPE));
    }

    public void testPrimitiveConversion()
    {
        assertTrue("byte -> byte", converter.isConvertable(ClassMaker.BYTE_TYPE, ClassMaker.BYTE_TYPE));
        assertTrue("byte -> short", converter.isConvertable(ClassMaker.BYTE_TYPE, ClassMaker.SHORT_TYPE));
        assertTrue("byte -> int", converter.isConvertable(ClassMaker.BYTE_TYPE, ClassMaker.INT_TYPE));
        assertTrue("short -> short", converter.isConvertable(ClassMaker.SHORT_TYPE, ClassMaker.SHORT_TYPE));
        assertTrue("short -> int", converter.isConvertable(ClassMaker.SHORT_TYPE, ClassMaker.INT_TYPE));
        assertTrue("int -> int", converter.isConvertable(ClassMaker.INT_TYPE, ClassMaker.INT_TYPE));
        assertTrue("char -> char", converter.isConvertable(ClassMaker.CHAR_TYPE, ClassMaker.CHAR_TYPE));
        assertTrue("char -> int", converter.isConvertable(ClassMaker.CHAR_TYPE, ClassMaker.INT_TYPE));
        assertTrue("long -> long", converter.isConvertable(ClassMaker.LONG_TYPE, ClassMaker.LONG_TYPE));
        assertTrue("float -> float", converter.isConvertable(ClassMaker.FLOAT_TYPE, ClassMaker.FLOAT_TYPE));
        assertTrue("double -> double", converter.isConvertable(ClassMaker.DOUBLE_TYPE, ClassMaker.DOUBLE_TYPE));
    }

    ClassType DESSERT;
    ClassType CAKE;
    ClassType SCONE;
    ClassType CHOCOLATE_CAKE;
    ClassType BUTTERED_SCONE;
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
            DESSERT = addClassType("test/Dessert", ClassMaker.OBJECT_TYPE);
            CAKE    = addClassType("test/Cake", DESSERT);
            SCONE   = addClassType("test/Scone", DESSERT);
            CHOCOLATE_CAKE = addClassType("test/ChocolateCake", CAKE);
            BUTTERED_SCONE = addClassType("test/ButteredScone", SCONE);

            OBJECT_ARRAY  = addArrayOfType(ClassMaker.OBJECT_TYPE);
            DESSERT_ARRAY = addArrayOfType(DESSERT);
            CAKE_ARRAY    = addArrayOfType(CAKE);
            SCONE_ARRAY   = addArrayOfType(SCONE);
            CHOCOLATE_CAKE_ARRAY = addArrayOfType(CHOCOLATE_CAKE);
            BUTTERED_SCONE_ARRAY = addArrayOfType(BUTTERED_SCONE);
        }
    }


    public void testReferenceConversion()
    {
        assertTrue("Dessert -> Object", converter.isConvertable(DESSERT, ClassMaker.OBJECT_TYPE));
        assertTrue("Cake -> Object", converter.isConvertable(CAKE, ClassMaker.OBJECT_TYPE));
        assertTrue("Scone -> Object", converter.isConvertable(SCONE, ClassMaker.OBJECT_TYPE));
        assertTrue("ButteredScone -> Scone", converter.isConvertable(BUTTERED_SCONE, SCONE));
        assertTrue("ChocolateCake -> Cake", converter.isConvertable(CHOCOLATE_CAKE, CAKE));
        assertTrue("null -> Object", converter.isConvertable(ClassMaker.NULL_TYPE, ClassMaker.OBJECT_TYPE));
        assertTrue("null -> Cake", converter.isConvertable(ClassMaker.NULL_TYPE, CAKE));
        assertTrue("null -> ButteredScone", converter.isConvertable(ClassMaker.NULL_TYPE, BUTTERED_SCONE));
        // Not convertable
        assertFalse("ButteredScone -> Cake", converter.isConvertable(BUTTERED_SCONE, CAKE));
        assertFalse("Scone -> ChocolateCake", converter.isConvertable(SCONE, CHOCOLATE_CAKE));
    }

    public void testArrayReferenceConversion()
    {
        assertTrue("Dessert[] -> Object", converter.isConvertable(DESSERT_ARRAY, ClassMaker.OBJECT_TYPE));
        assertTrue("Cake[] -> Object[]", converter.isConvertable(CAKE_ARRAY, OBJECT_ARRAY));
        assertTrue("Scone[] -> Object", converter.isConvertable(SCONE_ARRAY, ClassMaker.OBJECT_TYPE));
        assertTrue("ButteredScone[] -> Scone[]", converter.isConvertable(BUTTERED_SCONE_ARRAY, SCONE_ARRAY));
        assertTrue("ChocolateCake[] -> Cake[]", converter.isConvertable(CHOCOLATE_CAKE_ARRAY, CAKE_ARRAY));
        assertTrue("null -> Object[]", converter.isConvertable(ClassMaker.NULL_TYPE, OBJECT_ARRAY));
        assertTrue("null -> Cake[]", converter.isConvertable(ClassMaker.NULL_TYPE, CAKE_ARRAY));
        assertTrue("null -> Scone[]", converter.isConvertable(ClassMaker.NULL_TYPE, SCONE_ARRAY));
        assertTrue("null -> ButteredScone[]", converter.isConvertable(ClassMaker.NULL_TYPE, BUTTERED_SCONE_ARRAY));
        // Not convertable
        assertFalse("ButteredScone -> Cake", converter.isConvertable(BUTTERED_SCONE_ARRAY, CAKE_ARRAY));
        assertFalse("Scone -> ChocolateCake", converter.isConvertable(SCONE_ARRAY, CHOCOLATE_CAKE_ARRAY));
    }

}
