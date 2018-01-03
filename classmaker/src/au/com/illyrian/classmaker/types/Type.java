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

package au.com.illyrian.classmaker.types;

import au.com.illyrian.classmaker.ClassMakerFactory;

/**
 * Base class for all ClassMaker types.
 *
 * @author dstrong
 */
public class Type
{
    /** The name of the type */
    final String name;

    /** The JVM signature for the type */
    final String signature;

    /** The java Class which this type is based upon */
    Class javaClass = null;
    
    private final Value value;

    /**
     * Base constructor for all ClassMaker types.
     * @param name name of the type
     * @param signature JVM signature for the type
     */
    public Type(String name, String signature)
    {
        this.name = name;
        this.signature = signature;
        
        this.value = new Value(this);
    }

    public Value getValue() {
        return value;
    }
    
    /**
     * Determines whether the <code>Type</code> is an interface.
     * 
     * @param type
     *            the type to be tested
     * @return true if <code>Type</code> implements a class
     */
    public static boolean isInterface(Type type) {
        if (type != null && type.toClass() != null)
            return type.toClass().isInterface();
        return false;
    }

    /**
     * Determines whether the <code>Type</code> is a primitive type.
     * 
     * @param type
     *            the type to be tested
     * @return true if <code>Type</code> is a primitive type
     */
    public static boolean isPrimitive(Type type) {
        return type != null && type.toPrimitive() != null;
    }

    /**
     * Convert this <code>Type</code> to a <code>PrimitiveType</code>.
     * @return a <code>PrimitiveType</code> if appropriate; otherwise null
     */
    public PrimitiveType toPrimitive()
    {
        return null;
    }

    /**
     * Determines whether the <code>Type</code> is a class.
     * 
     * @param type
     *            the type to be tested
     * @return true if <code>Type</code> implements a class
     */
    public static boolean isClass(Type type) {
        return type != null && type.toClass() != null;
    }

    /**
     * Convert this <code>Type</code> to a <code>ClassType</code>.
     * @return a <code>ClassType</code> if appropriate; otherwise null
     */
    public ClassType toClass()
    {
        return null;
    }

    /**
     * Determines whether the <code>Type</code> is a <code>GenericType</code>.
     * 
     * @param type the type to be tested
     * @return true if <code>Type</code> is a <code>GenericType</code>
     */
    public static boolean isGeneric(Type type) {
        return type != null && type.toGeneric() != null;
    }

    /**
     * Convert this <code>Type</code> to a <code>GenericType</code>.
     * 
     * A generic type is similar to a class type but with some types parameterized.
     * @return a <code>GenericType</code> if appropriate; otherwise null
     */
    public GenericType toGeneric()
    {
        return null;
    }
    
    /**
     * Determines whether the <code>Type</code> is a <code>GenericType</code>.
     * 
     * @param type the type to be tested
     * @return true if <code>Type</code> is a <code>GenericType</code>
     */
    public static boolean isParameter(Type type) {
        return type != null && type.toParameter() != null;
    }

    /**
     * Convert this <code>Type</code> to a <code>GenericType</code>.
     * 
     * A generic type is similar to a class type but with some types parameterized.
     * @return a <code>GenericType</code> if appropriate; otherwise null
     */
    public ParameterType toParameter()
    {
        return null;
    }
    
    /**
     * Determines whether the <code>Type</code> is an array.
     * 
     * @param type
     *            the type to be tested
     * @return true if <code>Type</code> implements a array
     */
    public static boolean isArray(Type type) {
        return type != null && type.toArray() != null;
    }

    /**
     * Convert this <code>Type</code> to an <code>ArrayType</code>.
     * @return an <code>ArrayType</code> if appropriate; otherwise null
     */
    public ArrayType toArray()
    {
        return null;
    }
    
    /**
     * The name of this type.
     * @return a type name String
     */
    public String getName()
    {
        return name;
    }

    /**
     * The signature for this type.
     * @return a signature String
     */
    public String getSignature()
    {
        return signature;
    }

    /**
     * The java Class that this type is based upon.
     * @return a java Class
     */
    public Class getJavaClass()
    {
        return javaClass;
    }
    
    /**
     * Sets the java class for this type.
     * @param javaClass a java class
     */
    public void setJavaClass(Class javaClass)
    {
        this.javaClass = javaClass;
    }

    public short getSlotSize()
    {
        return (short)(ClassMakerFactory.DOUBLE_TYPE.equals(this) || ClassMakerFactory.LONG_TYPE.equals(this) ? 2 : 1);
    }

    /**
     * A string that describes the type.
     */
    public String toString()
    {
        return getClass().getSimpleName() + '(' + name + ')';
    }

    /**
     * Compares two types and returns <code>true</code> if they are equal.
     * <br/>
     * Compares the names of the types.
     */
    public boolean equals(Object obj)
    {
        if (obj != null && getClass().equals(obj.getClass()))
            return name.equals(((Type) obj).name);
        return false;
    }

    /**
     * A hashcode for the <code>Type</code>.
     * <br/>
     * The hashcode is based on the <code>this.getClass()</code> and the name.
     * @return a hashcode
     */
    public int hashcode()
    {
        return getClass().hashCode() ^ name.hashCode();
    }
}
