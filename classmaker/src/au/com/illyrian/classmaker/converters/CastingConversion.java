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
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

/**
 * A conversion strategy that is applied to force a change from one type to another.
 * <br/>
 * Casting Conversion supports the following conversions:
 * <UL>
 *   <LI>identity conversion</LI>
 *   <LI>widening primitive conversion</LI>
 *   <LI>narrowing primitive conversion</LI>
 *   <LI>widening reference conversion</LI>
 *   <LI>narrowing reference conversion</LI>
 * </UL>
 * The details of these conversions are described in the methods that implement them.
 * @author - Donald Strong
 */
public class CastingConversion extends AssignmentConversion implements Convertable
{
    /**
     * Creates an instance of CastingConversion with a reference to the
     * containing factory.
     * @param factory the factory which contains this instance
     */
    public CastingConversion(ClassMakerFactory factory)
    {
        super(factory);
    }

    /**
     * Tests whether a primitive can be narrowed from source type to target type.
     * <br/>
     * The following are the narrowing primitive conversions supported by ClassMaker:
     * <UL>
     *   <LI>from char to byte </LI>
     *   <LI>from short to byte </LI>
     *   <LI>from int to short, char or byte </LI>
     *   <LI>from long to int, short, char or byte </LI>
     *   <LI>from float to long, int, short, char or byte </LI>
     *   <LI>from double to float, long, int, short, char or byte </LI>
     * </UL>
     * @param source type of the value to be converted
     * @param target the type to convert to
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    protected boolean isNarrowingPrimitiveConvertable(PrimitiveType source, Type target)
    {
        switch (source.index)
        {
        case PrimitiveType.DOUBLE_INDEX: // -> float, long, int, short, char, byte
            if (PrimitiveType.FLOAT_TYPE.equals(target))
                return true;
        case PrimitiveType.FLOAT_INDEX: // -> long, int, short, char, byte
            if (PrimitiveType.LONG_TYPE.equals(target))
                return true;
        case PrimitiveType.LONG_INDEX: // -> int, short, char, byte
            if (PrimitiveType.INT_TYPE.equals(target))
                return true;
        case PrimitiveType.INT_INDEX: // -> short, char, byte
            if (PrimitiveType.SHORT_TYPE.equals(target))
                return true;
        case PrimitiveType.SHORT_INDEX: // -> char, byte
            if (PrimitiveType.BYTE_TYPE.equals(target))
                return true;
        case PrimitiveType.BYTE_INDEX: // -> char
            if (PrimitiveType.CHAR_TYPE.equals(target))
                return true;
            break;
        case PrimitiveType.CHAR_INDEX: // -> short, byte
            if (PrimitiveType.SHORT_TYPE.equals(target))
                return true;
            if (PrimitiveType.BYTE_TYPE.equals(target))
                return true;
            break;
        }
        return false;
    }

    /**
     * Converts the primitive on top of the stack from from source <code>Type</code> to target
     * <code>Type</code> using a narrowing conversion.
     * <br/>
     * A narrowing primitive conversion requires byte code
     * to be generated to either decrease the storage size of a value or to perform the conversion from
     * a real number to a whole number.
     *
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-codes
     * @param source <code>Type</code> of the value to be converted
     * @param target the <code>Type</code> to which to convert
     * @return the target <code>Type</code>
     */
    protected Type narrowingPrimitiveConversion(ClassMaker maker, PrimitiveType source, PrimitiveType target)
    {
        if (isNarrowingPrimitiveConvertable(source, target))
        {
            switch (target.index)
            {
            case PrimitiveType.BYTE_INDEX:
                return maker.toByte(source);
            case PrimitiveType.SHORT_INDEX:
                return maker.toShort(source);
            case PrimitiveType.CHAR_INDEX:
                return maker.toChar(source);
            case PrimitiveType.INT_INDEX:
                return maker.toInt(source);
            case PrimitiveType.LONG_INDEX:
                return maker.toLong(source);
            case PrimitiveType.FLOAT_INDEX:
                return maker.toFloat(source);
            case PrimitiveType.DOUBLE_INDEX:
                return maker.toDouble(source);
            }
        }
        // Should not get here. All calls to this method should be guarded by a call to isConvertable.
        throw new IllegalArgumentException("Cannot apply a Narrowing Primitive Conversion from type " + source.getName() + " to type " + target.getName());
    }

    /**
     * Tests whether a reference can be narrowed from source type to target type.
     * <br/>
     * The following are the narrowing reference conversions supported by ClassMaker:
     * <UL>
     *   <LI>from a class to a sub class </LI>
     *   <LI>from an interface to a class that implements it </LI>
     *   <LI>from {{{Object}}} to any array type or interface </LI>
     *   <LI>from a class to an interface that it does not implement </LI>
     *   <LI>FIXME - incomplete ... </LI>
     * </UL>
     * @param source type of the reference to be converted
     * @param target the type of reference to convert to
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    protected boolean isNarrowingReferenceConvertable(ClassType source, ClassType target)
    {
        // From Object to any class type.
        // From Object to any interface.
        // From Object to any array type.
        if (ClassType.OBJECT_TYPE.equals(source))
            return true;

        if (ClassMaker.isArray(target))
        {
            // A narrowing array conversion is the opposite of the widening
            // array conversion.
            return isWideningArrayConvertable(target.toArray(), source);
        }

        // A narrowing class conversion is the opposite of a widening class
        // conversion.
        // This predicate applies if target is a class or interface.
        return isWideningClassConvertable(target, source);
    }

    /**
     * Converts the reference on top of the stack from from source <code>Type</code> to target
     * <code>Type</code> using a narrowing conversion.
     * <br/>
     * A narrowing reference conversion requires byte code
     * to be generated to check at runtime whether the conversion is allowed.
     *
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-codes
     * @param source <code>Type</code> of the value to be converted
     * @param target the <code>Type</code> to which to convert
     * @return the target <code>Type</code>
     */
    protected Type narrowingReferenceConversion(ClassMaker maker, ClassType source, ClassType target)
    {
        if (isNarrowingReferenceConvertable(source, target))
            return maker.toReference(source, target);
        // Should not get here. All calls to this method should be guarded by a call to isConvertable.
        throw new IllegalArgumentException("Cannot apply a Narrowing Reference Conversion from type " + source.getName() + " to type " + target.getName());
    }

    /**
     * Tests whether the source <code>Type</code> can be converted to the target <code>Type</code>.
     * <br/>
     * Casting conversion supports the following conversions:
     * <UL>
     *   <LI>identity conversion</LI>
     *   <LI>widening primitive conversion</LI>
     *   <LI>narrowing primitive conversion</LI>
     *   <LI>widening reference conversion</LI>
     *   <LI>narrowing reference conversion</LI>
     * </UL>
     * @param source type of the array to be converted
     * @param target the type of array to convert to
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    public boolean isConvertable(Type source, Type target)
    {
        // Identity conversion.
        if (source.equals(target))
            return true;
        else if (ClassMaker.isPrimitive(source) && ClassMaker.isPrimitive(target))
        {
            if (isWideningPrimitiveConvertable(source.toPrimitive(), target.toPrimitive()))
                return true;

            if (isNarrowingPrimitiveConvertable(source.toPrimitive(), target.toPrimitive()))
                return true;
        }
        else if (ClassMaker.isClass(source) && ClassMaker.isClass(target))
        {
            if (isWideningReferenceConvertable(source.toClass(), target.toClass()))
                return true;

            if (isNarrowingReferenceConvertable(source.toClass(), target.toClass()))
                return true;
        }
        return false;
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
        else if (ClassMaker.isPrimitive(source) && ClassMaker.isPrimitive(target))
        {
            if (isWideningPrimitiveConvertable(source.toPrimitive(), target.toPrimitive()))
                return wideningPrimitiveConversion(maker, source.toPrimitive(), target.toPrimitive());

            if (isNarrowingPrimitiveConvertable(source.toPrimitive(), target.toPrimitive()))
                return narrowingPrimitiveConversion(maker, source.toPrimitive(), target.toPrimitive());
        }
        else if (ClassMaker.isClass(source) && ClassMaker.isClass(target))
        {
            if (isWideningReferenceConvertable(source.toClass(), target.toClass()))
                return wideningReferenceConversion(maker, source.toClass(), target.toClass());

            if (isNarrowingReferenceConvertable(source.toClass(), target.toClass()))
                return narrowingReferenceConversion(maker, source.toClass(), target.toClass());
        }
        // Should not get here. All calls to this method should be guarded by a call to isConvertable.
        throw new IllegalArgumentException("Cannot apply an Casting Conversion from type " + source.getName() + " to type " + target.getName());
    }

} // CastingConversion
