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

package org.mozilla.classfile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public abstract class ClassFileWriterTestCase extends TestCase
{
   /**
    * Load generated classes.
    * @TODO - remove this and use the mozilla DefiningClassLoader
    *
    * @author Norris Boyd
    */
   public static class DefiningClassLoader extends ClassLoader
   {
       public DefiningClassLoader() {
           this.parentLoader = getClass().getClassLoader();
       }

       public DefiningClassLoader(ClassLoader parentLoader) {
           this.parentLoader = parentLoader;
       }

       public Class<?> defineClass(String name, byte[] data) {
           // Use our own protection domain for the generated classes.
           // TODO: we might want to use a separate protection domain for classes
           // compiled from scripts, based on where the script was loaded from.
           return super.defineClass(name, data, 0, data.length,
                   SimpleClassLoader.getProtectionDomain(getClass()));
       }

       public void linkClass(Class<?> cl) {
           resolveClass(cl);
       }

       @Override
       public Class<?> loadClass(String name, boolean resolve)
           throws ClassNotFoundException
       {
           Class<?> cl = findLoadedClass(name);
           if (cl == null) {
               if (parentLoader != null) {
                   cl = parentLoader.loadClass(name);
               } else {
                   cl = findSystemClass(name);
               }
           }
           if (resolve) {
               resolveClass(cl);
           }
           return cl;
       }

       protected Class<?> findClass(String className)
           throws ClassNotFoundException
       {
           return super.findClass(className);
       }

       private final ClassLoader parentLoader;
   }

    DefiningClassLoader loader = null;

    public ClassFileWriterTestCase()
    {
        super();
    }

    protected DefiningClassLoader getLoader()
    {
        if (loader == null)
        {
            // The generated classes in this case refer only to Rhino classes
            // which must be accessible through this class loader
            ClassLoader parent = getClass().getClassLoader();
            loader = new DefiningClassLoader(parent);
        }
        return loader;
    }

    protected Class defineClass(String className, byte[] classBytes)
    {
        String classDotName = className.replace('/', '.');
        loader = getLoader();
        Exception e;
        try {
            Class<?> cl = loader.defineClass(classDotName, classBytes);
            loader.linkClass(cl);
            return cl;
        } catch (SecurityException x) {
            e = x;
        } catch (IllegalArgumentException x) {
            e = x;
        }
        throw new RuntimeException("Malformed optimizer package " + e);
    }

    public void invokeMain(Class myClass, Object myObj) throws Exception
    {
        Class [] parameterTypes = {String[].class};
        Method main = myClass.getMethod("main", parameterTypes);
        assertNotNull(main);

        Object [] args = {new String [0]};
        main.invoke(myObj, args);
    }

    public static char getCharField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getChar(myObj);
    }

    public static int getIntField(Class myClass, Object myObj, String name)
    throws IllegalAccessException, NoSuchFieldException
{
    Field sidField = myClass.getField(name);
    return sidField.getInt(myObj);
}

    public static void setIntField(Class myClass, Object myObj, String name, int value)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setInt(myObj, value);
    }

    public static float getFloatField(Class myClass, Object myObj, String name)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getFloat(myObj);
    }

    public static void setFloatField(Class myClass, Object myObj, String name, float value)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setFloat(myObj, value);
    }

    public static long getLongField(Class myClass, Object myObj, String name)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getLong(myObj);
    }

    public static void setLongField(Class myClass, Object myObj, String name, long value)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setLong(myObj, value);
    }

    public static double getDoubleField(Class myClass, Object myObj, String name)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getDouble(myObj);
    }

    public static void setDoubleField(Class myClass, Object myObj, String name, double value)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setDouble(myObj, value);
    }

    public Object getField(Class myClass, Object myObj, String name)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.get(myObj);
    }

    public void setField(Class myClass, Object myObj, String name, Object value)
        throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.set(myObj, value);
    }

}