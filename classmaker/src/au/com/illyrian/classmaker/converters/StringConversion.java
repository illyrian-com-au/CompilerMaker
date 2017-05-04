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
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;

/**
 * A conversion strategy that is applied when adding Strings and assigning the result.
 * <br/>
 * String Conversion supports the following conversions:
 * <UL>
 *   <LI>primitive string conversion</LI>
 *   <LI>reference string conversion</LI>
 *   <LI>string assignment conversion</LI>
 * </UL>
 * The details of these conversions are described in the methods that implement them.
 * @author - Donald Strong
 */
public class StringConversion implements Convertable, Assignable
{
    /** The ClassMakerFactory which contains this instance. */
    final protected ClassMakerFactory factory;

    /**
     * Creates an instance of StringConversion with a reference to the
     * containing factory.
     * @param factory the factory which contains this instance
     */
    public StringConversion(ClassMakerFactory factory)
    {
        this.factory = factory;
    }

    /**
     * Tests whether the result of an <code>Add</code> operation should be a string.
     * <br/>
     * Tests whether either operand to an <code>Add</code> is a String or an
     * automatically generated <code>StringBuffer</code>.
     * @param left the type of the left operand
     * @param right the type of the right operand
     * @return <code>true</code> if the result should be a string; otherwise <code>false</code>
     */
    public boolean isConvertable(Type left, Type right)
    {
        return (ClassType.STRING_TYPE.equals(left) || ClassType.STRING_TYPE.equals(right)
             || ClassType.AUTO_STRING_TYPE == left || ClassType.AUTO_STRING_TYPE == right);
    }

    /**
     * Converts the left and right operands of an <code>Add</code> operation to strings and
     * concatenates them in an automatic <code>StringBuffer</code>.
     * <br/>
     * If the left operand is an automatic <code>StringBuffer</code> the right operand is appended
     * and the StringBuffer is returned; otherwise a <code>StringBuffer</code> is automatically
     * created and both operands are appended to it.
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-codes
     * @param left the type of the left operand
     * @param right the type of the right operand
     * @return an automatic <code>StringBuffer</code>
     */
    public Type convertTo(ClassMaker maker, Type left, Type right)
    {
        if (isConvertable(left, right))
        {
            ClassType buf;
            if (ClassType.AUTO_STRING_TYPE == left)
                buf = left.toClass();
            else
            {
                // Stack contents
                // left, right
                maker.swap(left, right);
                // right, left
                buf = newStringBuffer(maker);
                // right, left, buf
                maker.swap(left, buf);
                // right, buf, left
                buf = append(maker, buf, left);
                // right, buf
                maker.swap(right, buf);
            }

            // buf, right
            return append(maker, buf, right);
            // buf
        }
        // Should not get here. This method should be guarded by a call to isConvertable.
        throw new IllegalArgumentException("Cannot apply a String Conversion to type " + left.getName() + " and type " + right.getName());
    }

    /**
     * Tests whether an automatic <code>StringBuffer</code> is being assigned to a <code>String</code>.
     * <br/>
     * The source type must be an automatically generated <code>StringBuffer</code> and the target
     * must be a <code>String</code>.
     * @param source the type of the value to be assigned
     * @param target the type of the variable being assigned the value
     * @return <code>true</code> if the result should be converted to a string; otherwise <code>false</code>
     */
    public boolean isAssignable(Type source, Type target)
    {
        return (ClassType.AUTO_STRING_TYPE == source  && ClassType.STRING_TYPE.equals(target));
    }

    /**
     * Converts an automatic <code>StringBuffer</code> prior to it being assigned to a
     * <code>String</code> variable.
     * <br/>
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-code
     * @param source the type (<code>StringBuffer</code>) to be converted
     * @param target the type (<code>String</code>) to be converted to
     * @return the type (<code>String</code>) of the result
     */
    public Type assignTo(ClassMaker maker, Type source, Type target)
    {
        if (isAssignable(source, target))
            return toString(maker, source.toClass());
        // Should not get here. This method should be guarded by a call to isAssignable.
        throw new IllegalArgumentException("Cannot apply an String Assignment Conversion from type " + source.getName() + " to type " + target.getName());
    }

    /**
     * Generates the byte-code to allocate and initialise an automatic <code>StringBffer</code>.
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-code
     * @return the type of the automatically generated <code>StringBuffer</code>
     */
    protected ClassType newStringBuffer(ClassMaker maker)
    {
        maker.New(StringBuffer.class).Init(null);
        return ClassType.AUTO_STRING_TYPE;
    }

    /**
     * Generates the byte-code to append a value to a <code>StringBuffer</code>.
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-code
     * @param buffer the type (<code>StringBuffer</code>) to which the value will be appended
     * @param value the type of the value to be appended
     * @return the type (<code>StringBuffer</code>) of the result
     */
    protected ClassType append(ClassMaker maker, ClassType buffer, Type value)
    {
        if (ClassType.AUTO_STRING_TYPE.equals(buffer))
        {
            maker.Call(buffer, "append", maker.Push(value));
            return buffer;
        }
        // Should not get here.
        throw new IllegalArgumentException("buffer parameter to append method must be an automatic StringBuffer");
    }

    /**
     * Generates the byte-code to call the <code>toString()</code> method of an automatic
     * <code>StringBuffer</code>
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-code
     * @param buffer the type (<code>StringBuffer</code>) to which the value will be appended
     * @return the type (<code>String</code>) of the result
     */
    public ClassType toString(ClassMaker maker, ClassType buffer)
    {
        if (ClassType.AUTO_STRING_TYPE.equals(buffer))
        {
            maker.Call(buffer, "toString", maker.Push());
            return ClassType.STRING_TYPE;
        }
        // Should not get here.
        throw new IllegalArgumentException("buffer parameter to append method must be an automatic StringBuffer");
    }

} // StringConversion
