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
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

/**
 * A conversion strategy that is applied to a value before it is assigned to a storage location.
 * <br/>
 * Assignment Conversion supports the following conversions:
 * <UL>
 *   <LI>identity conversion</LI>
 *   <LI>widening reference conversion</LI>
 *   <LI>widening primitive conversion</LI>
 *   <LI>string assignment conversion</LI>
 * </UL>
 * The details of these conversions are described in the methods that implement them.
 * @author - Donald Strong
 */
public class AssignmentConversion extends MethodInvocationConversion implements Convertable
{
    /** A reference to the StringConversion strategy. */
    StringConversion stringConversion;

    /**
     * Creates an instance of AssignmentConversion with a reference to the
     * containing factory.
     * @param factory the factory which contains this instance
     */
    public AssignmentConversion(ClassMakerFactory factory)
    {
        super(factory);
        stringConversion = factory.getStringConversion();
    }

    /**
     * Tests whether the source <code>Type</code> can be converted to the target <code>Type</code>.
     * <br/>
     * Assignment conversion supports the following conversions:
     * <UL>
     *   <LI>identity conversion</LI>
     *   <LI>widening reference conversion</LI>
     *   <LI>widening primitive conversion</LI>
     *   <LI>string assignment conversion</LI>
     * </UL>
     * @param source <code>Type</code> of the rvalue
     * @param target the <code>Type</code> of the lvalue
     * @return <code>true</code> if source can be assigned to target; otherwise <code>false</code>
     */
    public boolean isConvertable(Type source, Type target)
    {
        // Identity conversion.
        if (source.equals(target))
            return true;
        // Widening Primitive Conversion
        if (ClassMaker.isPrimitive(source) && ClassMaker.isPrimitive(target))
            if (isWideningPrimitiveConvertable(source.toPrimitive(), target.toPrimitive()))
                return true;
        // Widening Reference Conversion
        if (ClassMaker.isClass(source) && ClassMaker.isClass(target))
            if (isWideningReferenceConvertable(source.toClass(), target.toClass()))
                return true;
        // String Assignment Conversion
        if (stringConversion.isAssignable(source, target))
            return true;
        return false;
    }

    /**
     * Tests whether a primitive can be widened from source type to target type.
     * <br/>
     * The following are the widening primitive conversions supported by ClassMaker:
     * <UL>
     *   <LI>from byte to short, int, long, float or double </LI>
     *   <LI>from short to int, long, float or double </LI>
     *   <LI>from char to int, long, float or double </LI>
     *   <LI>from int to long, float or double </LI>
     *   <LI>from long to float or double </LI>
     *   <LI>from float to double </LI>
     * </UL>
     * @param source type of the value to be converted
     * @param target the type to convert to
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    protected boolean isWideningPrimitiveConvertable(PrimitiveType source, PrimitiveType target)
    {
        switch (source.index)
        {
        case PrimitiveType.BYTE_INDEX: // byte can be promoted to short or int ...
            if (ClassMakerFactory.SHORT_TYPE.equals(target))
                return true;
        case PrimitiveType.SHORT_INDEX: // short can be promoted to int ...
        case PrimitiveType.CHAR_INDEX: // char can be promoted to int ...
            if (ClassMakerFactory.INT_TYPE.equals(target))
                return true;
        case PrimitiveType.INT_INDEX: // int can be promoted to long, float or
                                    // double
            if (ClassMakerFactory.LONG_TYPE.equals(target))
                return true;
        case PrimitiveType.LONG_INDEX: // long can be promoted to double
            if (ClassMakerFactory.FLOAT_TYPE.equals(target))
                return true;
        case PrimitiveType.FLOAT_INDEX: // float can be promoted to double
            if (ClassMakerFactory.DOUBLE_TYPE.equals(target))
                return true;
            break;
        }
        return false;
    }

    /**
     * Converts the primitive on top of the stack from from source <code>Type</code> to target
     * <code>Type</code> using a widening conversion.
     * <br/>
     * A widening primitive conversion from byte to char to short to int requires no byte-code to be
     * generated and is the equivalent of a widening integer conversion.
     * <br/>
     * A widening primitive conversion from int to long to float to double requires byte code
     * to be generated to either increase the storage size or to perform the conversion from
     * a whole number to a real number.
     *
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-codes
     * @param source <code>Type</code> of the value to be converted
     * @param target the <code>Type</code> to which to convert
     * @return the target <code>Type</code>
     */
    protected Type wideningPrimitiveConversion(ClassMaker maker, PrimitiveType source, PrimitiveType target)
    {
        switch (source.index)
        {
        case PrimitiveType.BYTE_INDEX: // byte can be promoted to short or int
            if (ClassMakerFactory.SHORT_TYPE.equals(target))
                return maker.toShort(source);
        case PrimitiveType.SHORT_INDEX: // short can be promoted to int
        case PrimitiveType.CHAR_INDEX:  // char can be promoted to int
            if (ClassMakerFactory.INT_TYPE.equals(target))
                return maker.toInt(source);
        case PrimitiveType.INT_INDEX: // int can be promoted to long, float or double
            if (ClassMakerFactory.LONG_TYPE.equals(target))
                return maker.toLong(source);
        case PrimitiveType.LONG_INDEX: // long can be promoted to float or double
            if (ClassMakerFactory.FLOAT_TYPE.equals(target))
                return maker.toFloat(source);
        case PrimitiveType.FLOAT_INDEX: // float can be promoted to double
            if (ClassMakerFactory.DOUBLE_TYPE.equals(target))
                return maker.toDouble(source);
        }
        // Should not get here. All calls to this method should be guarded by a call to isConvertable.
        throw new IllegalArgumentException("Cannot apply a Widening Primitive Conversion from type " + source.getName() + " to type " + target.getName());
    }

    /**
     * Converts the value on top of the stack from from source <code>Type</code> to target <code>Type</code>.
     * <br/>
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-codes
     * @param source <code>Type</code> of the value to be converted
     * @param target the <code>Type</code> to which to convert
     * @return the target <code>Type</code>
     */
    public Type convertTo(ClassMaker maker, Type source, Type target)
    {
        // Identity conversion.
        if (source.equals(target))
            return target;
        // Widening Primitive Conversion
        if (ClassMaker.isPrimitive(source) && ClassMaker.isPrimitive(target))
            if (isWideningPrimitiveConvertable(source.toPrimitive(), target.toPrimitive()))
                return wideningPrimitiveConversion(maker, source.toPrimitive(), target.toPrimitive());
        // Widening Reference Conversion
        if (ClassMaker.isClass(source) && ClassMaker.isClass(target))
            if (isWideningReferenceConvertable(source.toClass(), target.toClass()))
                return wideningReferenceConversion(maker, source.toClass(), target.toClass());
        // String Assignment Conversion
        if (stringConversion.isAssignable(source, target))
            return stringConversion.assignTo(maker, source, target);
        // Should not get here. All calls to this method should be guarded by a call to isConvertable.
        throw new IllegalArgumentException("Cannot apply an Assignment Conversion from type " + source.getName() + " to type " + target.getName());
    }
}
