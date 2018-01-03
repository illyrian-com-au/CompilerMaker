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

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.GenericType;
import au.com.illyrian.classmaker.types.ParameterType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;
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
        assertEquals("getPackageName", object.getPackage().getName(), type.getPackageName());
        assertEquals("getModifiers", object.getModifiers(), type.getModifiers());
        assertEquals("Field count", object.getDeclaredFields().length, type.getDeclaredFields().length);
        assertEquals("Constructor count", object.getConstructors().length, type.getConstructors().length);
        assertEquals("Method count", object.getDeclaredMethods().length, type.getDeclaredMethods().length);
        assertEquals("Interface count", object.getInterfaces().length, type.getInterfaces().length);
        assertNull("Supertype", object.getSuperclass());
        assertNull("Supertype", type.getExtendsType());
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

    public void testPrimitiveTypeInt() {
        Type intType = factory.classToType(int.class);
        assertEquals("int", intType.getName());
        PrimitiveType intPrimitive = intType.toPrimitive();
        assertNotNull("toPrimitive() should not be null", intPrimitive);
    }

    public void testClassTypeObject() {
        Type objectType = factory.classToType(java.lang.Object.class);
        assertEquals("java.lang.Object", objectType.getName());
        ClassType objectClassType = objectType.toClass();
        assertNotNull("toClass() should not be null", objectClassType);
        assertEquals(1, objectClassType.getConstructors().length);
        assertNull("getExtendsType() should be null", objectClassType.getExtendsType());
        assertFalse("Object should not be an interface", ClassType.isInterface(objectClassType));
        assertEquals("Should equal canonical OBJECT_TYPE", objectType, ClassMakerFactory.OBJECT_TYPE);
    }

    public void testGenericTypeList() {
        Type type = factory.classToType(List.class);
        assertEquals("java.util.List", type.getName());
        ClassType classType = type.toClass();
        assertNotNull("toClass() should not be null", classType);
        assertEquals(0, classType.getConstructors().length);
        assertEquals(25, classType.getDeclaredMethods().length);
        assertNull("getExtendsType() should be null", classType.getExtendsType());
        assertTrue("Object must be an interface", ClassType.isInterface(classType));
        GenericType genericType = type.toGeneric();
        assertEquals("java.util.List<E>", genericType.getGenericName());
        assertNotNull("toGeneric() should not be null", genericType);
        assertEquals(1, genericType.getParameterTypes().length);
        ParameterType paraType = genericType.getParameterTypes()[0];
        assertEquals("E", paraType.getName());
        assertEquals("<E>", paraType.toString());
    }

    public void testGenericInterfaceMap() {
        Class javaClass = Map.class;
        assertEquals(0, javaClass.getInterfaces().length);
        assertNull("Map.class.getSuperclass()", javaClass.getSuperclass());

        Type type = factory.classToType(javaClass);
        assertEquals("java.util.Map", type.getName());
        ClassType classType = type.toClass();
        assertNotNull("toClass() should not be null", classType);
        assertEquals(0, classType.getConstructors().length);
        assertEquals(14, classType.getDeclaredMethods().length);
        assertEquals("GenericType(java.util.Map<K, V>)", classType.toString());
        assertNull("getExtendsType() should be null", classType.getExtendsType());
        assertNull("Map.class.getSuperclass()", javaClass.getSuperclass());
        assertTrue("Object must be an interface", ClassType.isInterface(classType));
        GenericType genericType = type.toGeneric();
        assertEquals("java.util.Map<K, V>", genericType.getGenericName());
        assertNotNull("toGeneric() should not be null", genericType);
        assertEquals(2, genericType.getParameterTypes().length);
        ParameterType para1 = genericType.getParameterTypes()[0];
        assertEquals("K", para1.getName());
        assertEquals(type, para1.getParentClassType());
        assertEquals("<K>", para1.toString());
        assertEquals(type, para1.getParentClassType());
        assertEquals(ClassMakerFactory.OBJECT_TYPE, para1.getBoundType());
        ParameterType para2 = genericType.getParameterTypes()[1];
        assertEquals("V", para2.getName());
        assertEquals("<V>", para2.toString());
        assertEquals(type, para2.getParentClassType());
        assertEquals(ClassMakerFactory.OBJECT_TYPE, para2.getBoundType());
    }
    
    public void testGenericClassHashMap() {
        Class javaClass = HashMap.class;
        assertEquals(3, javaClass.getInterfaces().length);
        assertEquals(AbstractMap.class, javaClass.getSuperclass());

        Type type = factory.classToType(javaClass);
        Type abstractMapType = factory.classToType(AbstractMap.class);
        assertEquals("java.util.HashMap", type.getName());
        ClassType classType = type.toClass();
        assertNotNull("toClass() should not be null", classType);
        assertEquals(javaClass.getConstructors().length, classType.getConstructors().length);
        assertEquals(javaClass.getDeclaredMethods().length, classType.getDeclaredMethods().length);
        assertEquals("GenericType(java.util.HashMap<K, V>)", classType.toString());
        assertEquals(abstractMapType, classType.getExtendsType());
        assertFalse("Object must not be an interface", ClassType.isInterface(classType));
        GenericType genericType = type.toGeneric();
        assertEquals("java.util.HashMap<K, V>", genericType.getGenericName());
        assertNotNull("toGeneric() should not be null", genericType);
        assertEquals(2, genericType.getParameterTypes().length);
        ParameterType para1 = genericType.getParameterTypes()[0];
        assertEquals("K", para1.getName());
        assertEquals(type, para1.getParentClassType());
        assertEquals("<K>", para1.toString());
        assertEquals(type, para1.getParentClassType());
        assertEquals(ClassMakerFactory.OBJECT_TYPE, para1.getBoundType());
        assertEquals(null, para1.getActualType());
        ParameterType para2 = genericType.getParameterTypes()[1];
        assertEquals("V", para2.getName());
        assertEquals("<V>", para2.toString());
        assertEquals(type, para2.getParentClassType());
        assertEquals(ClassMakerFactory.OBJECT_TYPE, para2.getBoundType());
        assertEquals(null, para2.getActualType());
        
        GenericType hashMapType = factory.classToType(HashMap.class).toGeneric();
        assertEquals("Same type is not equal", type, hashMapType);
    }

    public static class StringMap extends HashMap<String, String> {};
    
    public void testConcreteHashMap() {
        Class javaClass = StringMap.class;
        assertEquals(0, javaClass.getInterfaces().length);
        assertEquals(HashMap.class, javaClass.getSuperclass());
        java.lang.reflect.Type superType = javaClass.getGenericSuperclass();
        assertEquals("java.util.HashMap<java.lang.String, java.lang.String>", superType.toString());
        assertTrue(superType instanceof java.lang.reflect.ParameterizedType);

        Type type = factory.classToType(javaClass);
        assertEquals("au.com.illyrian.classmaker.MakerClassTypeTest$StringMap", type.getName());
        ClassType classType = type.toClass();
        assertNotNull("toClass() should not be null", classType);
        assertEquals(javaClass.getConstructors().length, classType.getConstructors().length);
        assertEquals(javaClass.getDeclaredMethods().length, classType.getDeclaredMethods().length);
        assertEquals("ClassType(au.com.illyrian.classmaker.MakerClassTypeTest$StringMap)", classType.toString());
        assertFalse("Object must not be an interface", ClassType.isInterface(classType));
        assertNull("toGeneric() should be null", type.toGeneric());

        ClassType extendsClassType = classType.getExtendsType();
        GenericType genericType = extendsClassType.toGeneric();
        assertNotNull("super class should be Generic", genericType);
        assertEquals("java.util.HashMap<java.lang.String, java.lang.String>", genericType.getGenericName());
        assertEquals(2, genericType.getParameterTypes().length);
        ParameterType para1 = genericType.getParameterTypes()[0];
        assertEquals("K", para1.getName());
        assertEquals("<K=java.lang.String>", para1.toString());
        assertEquals(genericType, para1.getParentClassType());
        assertEquals(ClassMakerFactory.OBJECT_TYPE, para1.getBoundType());
        assertEquals(ClassMakerFactory.STRING_TYPE, para1.getActualType());
        ParameterType para2 = genericType.getParameterTypes()[1];
        assertEquals("V", para2.getName());
        assertEquals("<V=java.lang.String>", para2.toString());
        assertEquals(genericType, para2.getParentClassType());
        assertEquals(ClassMakerFactory.OBJECT_TYPE, para2.getBoundType());
        assertEquals(ClassMakerFactory.STRING_TYPE, para2.getActualType());
        
        Type hashMapType = factory.classToType(HashMap.class);
        assertEquals("GenericType(java.util.HashMap<K, V>)", hashMapType.toString());
        assertEquals(extendsClassType + " should not be equal to" + hashMapType, extendsClassType, hashMapType);
    }
}
