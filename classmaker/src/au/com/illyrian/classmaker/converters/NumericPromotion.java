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
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

/**
 * A conversion strategy that is applied to a numeric value when it is used in an expression.
 * <br/>
 * Numeric promotion supports the following conversions:
 * <UL>
 *   <LI>identity conversion</LI>
 *   <LI>widening integer conversion (for a unary operand)</LI>
 *   <LI>widening primitive conversion (for binary operands)</LI>
 * </UL>
 * The details of these conversions are described in the methods that implement them.
 * @author - Donald Strong
 */
public class NumericPromotion extends AssignmentConversion implements Convertable
{
    /**
     * Creates an instance of NumericPromotion with a reference to the
     * containing factory.
     * @param factory the factory which contains this instance
     */
    public NumericPromotion(ClassMakerFactory factory)
    {
        super(factory);
    }

    /**
     * Tests whether the operand can be promoted to type <code>int</code>.
     * <br/>
     * Numeric promotion supports the following unary conversions:
	 * <UL>
	 *   <LI>identity conversion</LI>
	 *   <LI>widening integer conversion</LI>
	 * </UL>
     * @param op the type of the operand
     * @return <code>true</code> if the operand can be promoted to type <code>int</code>; otherwise <code>false</code>
     */
    public boolean isConvertable(Type op)
    {
        return (Type.isPrimitive(op)
             && isWideningIntegerConvertable(op.toPrimitive(), ClassMakerFactory.INT_TYPE));
    }

    /**
     * Converts the operand on top of the stack to type <code>int</code>.
     * <br/>
     * Performs integer promotion of the operand.
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-codes
     * @param op the <code>Type</code> of the operand
     * @return int <code>Type</code>
     */
    public Type convertTo(ClassMakerConstants maker, Type op)
    {
        if (Type.isPrimitive(op))
        {
            // Assume a conversion from byte, short or char -> int.
            if (isWideningIntegerConvertable(op.toPrimitive(), ClassMakerFactory.INT_TYPE))
                return ClassMakerFactory.INT_TYPE;
        }
        throw new IllegalArgumentException("Cannot convert " + op.getName());
    }

    /**
     * Tests whether the left and right operands can be converted to the same <code>Type</code>.
     * <br/>
     * Numeric promotion supports the following binary conversions:
	 * <UL>
	 *   <LI>identity conversion</LI>
	 *   <LI>widening integer conversion (for a unary operand)</LI>
	 *   <LI>widening primitive conversion (for binary operands)</LI>
	 * </UL>
     * @param left the type of the left operand
     * @param right the type of the right operand
     * @return <code>true</code> if both operands can be promoted to the same type; otherwise <code>false</code>
     */
    public boolean isConvertable(Type left, Type right)
    {
        if (Type.isPrimitive(left) && Type.isPrimitive(right))
        {
            if (isWideningIntegerConvertable(left.toPrimitive(), ClassMakerFactory.INT_TYPE))
                left = ClassMakerFactory.INT_TYPE;
            if (isWideningIntegerConvertable(right.toPrimitive(), ClassMakerFactory.INT_TYPE))
                right = ClassMakerFactory.INT_TYPE;
            if (left.equals(right))
                return true;
            if (isWideningPrimitiveConvertable(left.toPrimitive(), right.toPrimitive()))
                return true;
            if (isWideningPrimitiveConvertable(right.toPrimitive(), left.toPrimitive()))
                return true;
        }
        return false;
    }

    /**
     * Converts the two operands on top of the stack so that they are the same <code>Type</code>.
     * <br/>
     * Performs integer promotion of both operands before promoting one operand to the type of the other.
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-codes
     * @param left the <code>Type</code> of the left operand
     * @param right the <code>Type</code> of the right operand
     * @return the <code>Type</code> that both operands were promoted to
     */
    public Type convertTo(ClassMaker maker, Type left, Type right)
    {
        Type promotedType = left;
        if (Type.isPrimitive(left) && Type.isPrimitive(right))
        {
            // Assume a conversion from byte, short or char -> int.
            if (isWideningIntegerConvertable(left.toPrimitive(), ClassMakerFactory.INT_TYPE))
                left = ClassMakerFactory.INT_TYPE;
            if (isWideningIntegerConvertable(right.toPrimitive(), ClassMakerFactory.INT_TYPE))
                right = ClassMakerFactory.INT_TYPE;

            // Identity conversion
            if (left.equals(right))
                return left;

            // Now convert to wider types int -> long -> float -> double.
            if (isWideningPrimitiveConvertable(left.toPrimitive(), right.toPrimitive()))
            { // Promoting Left operand to the type of the right operand.
                maker.getGen().swap(left, right);
                promotedType = this.wideningPrimitiveConversion(maker, left.toPrimitive(), right.toPrimitive());
                // Swap stack back again.
                maker.getGen().swap(right, promotedType);
            } else if (isWideningPrimitiveConvertable(right.toPrimitive(), left.toPrimitive()))
            { // Promoting Right operand to the type of the left operand.
                promotedType = this.wideningPrimitiveConversion(maker, right.toPrimitive(), left.toPrimitive());
            } else
            { // No extra code needed to affect promotion.
                promotedType = left;
            }
        }
        return promotedType;
    }
} // NumericPromotion
