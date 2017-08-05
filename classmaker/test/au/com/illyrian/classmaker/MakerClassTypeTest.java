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

import au.com.illyrian.classmaker.types.ClassType;
import junit.framework.TestCase;

public class MakerClassTypeTest extends TestCase
{
    ClassMakerFactory factory;
    ClassMakerConstants maker;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
    }
    
    String toString(Object [] list) {
        if (list == null) {
            return "null";
        }
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        for (int i=0; i<list.length; i++) {
            if (i>0) {
                buf.append(", ");
            }
            Object obj = list[i];
            buf.append(obj==null ? null : obj.toString());
        }
        buf.append("]");
        return buf.toString();
    }
    
    void assertEquals(String message, Object[] expected, Object[] actual) {
        String expectedString = toString(expected);
        String actualString = toString(actual);
        if (!expectedString.equals(actualString)) {
            fail(format(message, expectedString, actualString));
        }
    }

    public void testObject() throws Exception
    {
        ClassType type = ClassMakerFactory.OBJECT_TYPE;
        Class object = Object.class;
        //Object obj = new Integer(3);
        assertEquals("getName", object.getName(), type.getName());
        assertEquals("getModifiers", object.getModifiers(), type.getModifiers());
//        assertEquals("getFields", object.getFields(), type.getFields()); // All fields
//        assertEquals("getInterfaces", object.getInterfaces(), type.getInterfaces());
//        assertEquals("getMethods", object.getMethods(), type.getMethods());
//        assertEquals("getPackage", object.getPackage().getName(), type.getPackageName());
//        assertEquals("getConstructors()", object.getConstructors(), type.getConstructors()); // Public constructors
        //assertEquals(object.getCanonicalName(), type.getCanonicalName());
        //assertEquals(object.getComponentType(), type.getArrayElement()); // Component type of an array or null
        //assertEquals("getDeclaredConstructors()", object.getDeclaredConstructors(), type.getDeclaredConstructors()); // All constructors
        //assertEquals("getDeclaredField(name)", object.getDeclaredField("foo"), type.getDeclaredField());
        //assertEquals("getDeclaredFields()", object.getDeclaredFields(), type.getDeclaredFields()); // Fields in this class
        //assertEquals("getDeclaredMethods()", object.getDeclaredMethods(), type.getDeclaredMethods()); // Methods in this class
        //assertEquals("getDeclaredMethod()", object.getDeclaredMethod("foo", int.class), type.getName());
        //assertEquals("getField", object.getField("foo"), type.getField("foo")); // Field in this class or super class
        //assertEquals("getMethod", object.getMethod("foo", int.class), type.getMethod());
        assertEquals("getSimpleName", object.getSimpleName(), type.getSimpleName());
        //assertEquals("getSuperclass", object.getSuperclass(), type.getSuperclass());
        //assertEquals("isArray", object.isArray(), type.isArray());
        //assertEquals("isAssignableFrom", object.isAssignableFrom(int.class), type.getName());
        //assertEquals("isInstance", object.isInstance(obj), type.isInstance());
        //assertEquals("isInterface", object.isInterface(), type.isInterface());
        //assertEquals("isPrimitive", object.isPrimitive(), type.isPrimitive());
        //assertEquals("newInstance", object.newInstance(), type.newInstance());
//        assertEquals("toString", object.toString(), type.toString());
    }

}
