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

/**
 * The class for representing all primitive types.
 *
 * Primitive types are based upon the the corresponding java classes,
 * i.e.
 * <UL>
 *   <LI><code>void.class</code></LI>
 *   <LI><code>byte.class</code></LI>
 *   <LI><code>char.class</code></LI>
 *   <LI><code>int.class</code></LI>
 *   <LI><code>long.class</code></LI>
 *   <LI><code>float.class</code></LI>
 *   <LI><code>double.class</code></LI>
 *   <LI><code>boolean.class</code></LI>
 * </UL>
 *
 * @author dstrong
 */
public class PrimitiveType extends Type
{
    //############# Constants ############
    /** Case index for <code>void</code> type */
    public static final int VOID_INDEX = 0;
    /** Case index for <code>byte</code> type */
    public static final int BYTE_INDEX = 1;
    /** Case index for <code>char</code> type */
    public static final int CHAR_INDEX = 2;
    /** Case index for <code>double</code> type */
    public static final int DOUBLE_INDEX = 3;
    /** Case index for <code>float</code> type */
    public static final int FLOAT_INDEX = 4;
    /** Case index for <code>int</code> type */
    public static final int INT_INDEX = 5;
    /** Case index for <code>long</code> type */
    public static final int LONG_INDEX = 6;
    /** Case index for <code>short</code> type */
    public static final int SHORT_INDEX = 7;
    /** Case index for <code>boolean</code> type */
    public static final int BOOLEAN_INDEX = 8;

    public static final PrimitiveType VOID_TYPE = new PrimitiveType(VOID_INDEX, "void", "V", void.class);
    /** Reference to <code>byte</code> type */
    public static final PrimitiveType BYTE_TYPE = new PrimitiveType(BYTE_INDEX, "byte", "B", byte.class);
    /** Reference to <code>char</code> type */
    public static final PrimitiveType CHAR_TYPE = new PrimitiveType(CHAR_INDEX, "char", "C", char.class);
    /** Reference to <code>double</code> type */
    public static final PrimitiveType DOUBLE_TYPE = new PrimitiveType(DOUBLE_INDEX, "double", "D", double.class);
    /** Reference to <code>float</code> type */
    public static final PrimitiveType FLOAT_TYPE = new PrimitiveType(FLOAT_INDEX, "float", "F", float.class);
    /** Reference to <code>int</code> type */
    public static final PrimitiveType INT_TYPE = new PrimitiveType(INT_INDEX, "int", "I", int.class);
    /** Reference to <code>long</code> type */
    public static final PrimitiveType LONG_TYPE = new PrimitiveType(LONG_INDEX, "long", "J", long.class);
    /** Reference to <code>short</code> type */
    public static final PrimitiveType SHORT_TYPE = new PrimitiveType(SHORT_INDEX, "short", "S", short.class);
    /** Reference to <code>boolean</code> type */
    public static final PrimitiveType BOOLEAN_TYPE = new PrimitiveType(BOOLEAN_INDEX, "boolean", "Z", boolean.class);

    /** An index associated with the primitive type that can be used in case statements. */
    public final int    index;

    /**
     * Constructor for a primitive type.
     * @param index the index associated with the primitive type
     * @param name the name of the type
     * @param signature the JVM signature of the type
     * @param javaClass the java Class that the primitive type is based upon
     */
    public PrimitiveType(int index, String name, String signature, Class javaClass)
    {
        super(name, signature);
    	this.index = index;
        setJavaClass(javaClass);
    }

    /**
     * Convert this <code>Type</code> to a <code>PrimitiveType</code>.
     * @return a <code>PrimitiveType</code>
     */
    public PrimitiveType toPrimitive()
    {
    	return this;
    }
}
