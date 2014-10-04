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

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

public abstract class ClassMakerTestCase extends TestCase
{
    public void setUp() throws Exception
    {
       // Logger.getGlobal().setLevel(Level.FINE);
    }
    
    public static char getCharField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getChar(myObj);
    }

    public static void setCharField(Class myClass, Object myObj, String name, char value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setChar(myObj, value);
    }

    public static int getIntField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        //Field sidField = myClass.getDeclaredField(name);
        Field sidField = myClass.getField(name);
        return sidField.getInt(myObj);
    }

    public static void setIntField(Class myClass, Object myObj, String name, int value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setInt(myObj, value);
    }

    public static byte getByteField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getByte(myObj);
    }

    public static void setByteField(Class myClass, Object myObj, String name, int value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setByte(myObj, (byte)value);
    }

    public static short getShortField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getShort(myObj);
    }

    public static void setShortField(Class myClass, Object myObj, String name, int value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setShort(myObj, (short)value);
    }

    public static boolean getBooleanField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getBoolean(myObj);
    }

    public static void setBooleanField(Class myClass, Object myObj, String name, boolean value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setBoolean(myObj, value);
    }

    public static float getFloatField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getFloat(myObj);
    }

    public static void setFloatField(Class myClass, Object myObj, String name, float value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setFloat(myObj, value);
    }

    public static long getLongField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getLong(myObj);
    }

    public static void setLongField(Class myClass, Object myObj, String name, long value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setLong(myObj, value);
    }

    public static double getDoubleField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.getDouble(myObj);
    }

    public static void setDoubleField(Class myClass, Object myObj, String name, double value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.setDouble(myObj, value);
    }

    public Object getField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        return sidField.get(myObj);
    }

    public void setField(Class myClass, Object myObj, String name, Object value) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getField(name);
        sidField.set(myObj, value);
    }

}
