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
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.util.MakerUtil;

/**
 * A conversion strategy that may be applied to each parameter of a method invocation.
 * <br/>
 * Method Invocation conversion supports the following conversions:
 * <UL>
 *   <LI>identity conversion</LI>
 *   <LI>widening integer conversion</LI>
 *   <LI>widening reference conversion</LI>
 * </UL>
 * The details of these conversions are described in the methods that implement them.
 * @author - Donald Strong
 */
public class MethodInvocationConversion implements Convertable
{
    /** The ClassMakerFactory which contains this instance. */
    final protected ClassMakerFactory factory;

    /**
     * Creates an instance of MethodInvocationConversion with a reference to the
     * containing factory.
     * @param factory the factory which contains this instance
     */
    public MethodInvocationConversion(ClassMakerFactory factory)
    {
        this.factory = factory;
    }

     /**
      * Tests whether a number can be widened up to the size of an <code>int</code>.
      * <br/>
      * The following are widening integer conversions:
      * <UL>
      *   <LI>from byte to short, int</LI>
      *   <LI>from short to int</LI>
      *   <LI>from char to int</LI>
      * </UL>
      * Widening conversion from <code>byte</code> to <code>short</code>
      * to <code>int</code> is always possible because they all use the same
      * amount of storage, i.e. 4 bytes.
      * @param source type of the value to be converted
      * @param target the type to convert to
      * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
      */
    protected boolean isWideningIntegerConvertable(PrimitiveType source, PrimitiveType target)
    {
        switch (source.index)
        {
        case PrimitiveType.BYTE_INDEX: // byte can be promoted to short or int
            if (ClassMakerFactory.SHORT_TYPE.equals(target))
                return true;
        case PrimitiveType.SHORT_INDEX: // short can be promoted to int
        case PrimitiveType.CHAR_INDEX:  // char can be promoted to int
            if (ClassMakerFactory.INT_TYPE.equals(target))
                return true;
        }
        return false;
    }

    /**
     * Tests whether a reference can be widened from source type to target type.
     * <br/>
     * The following are widening reference conversions:
     * <UL>
     *   <LI>From any class type to class type Object. </LI>
     *   <LI>From any class type S to any class type T, provided that S is a subclass of T. </LI>
     *   <LI>From any class type S to any interface type K, provided that S implements K. </LI>
     *   <LI>From the null type to any class type, interface type, or array type. </LI>
     *   <LI>From any interface type J to any interface type K, provided that J is a sub-interface of K. </LI>
     *   <LI>From any interface type to type Object. </LI>
     *   <LI>From any array type to type Object.</LI>
     *   <LI>From any array type to type Cloneable. </LI>
     *   <LI>From any array type SC[] to any array type TC[], provided that SC and TC are
     *   reference types and there is a widening conversion from SC to TC.</LI>
     * </UL>
      * @param source type of the value to be converted
      * @param target the type to convert to
      * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    protected boolean isWideningReferenceConvertable(ClassType source, ClassType target)
    {
        // From any class type to class type Object.
        // From any interface type to type Object.
        // From any array type to type Object.
        if (ClassMakerFactory.OBJECT_TYPE.equals(target))
            return true;

        // From the null type to any class type, interface type, or array type.
        if (ClassMakerFactory.NULL_TYPE.equals(source))
            return true;

        // From any array type SC[] to any array type TC[], provided that SC and TC are
        // reference types and there is a widening conversion from SC to TC.
        if (Type.isArray(source))
            return isWideningArrayConvertable(source.toArray(), target);

        // This predicate applies if source is a class or interface.
        return isWideningClassConvertable(source, target);
    }

    /**
     * Tests whether the target class is a base class or is an interface that is
     * implemented by the source class.
     * The following are widening reference conversions that apply to classes:
     * <UL>
     *   <LI>From any class type S to any class type T, provided that S is a subclass of T. </LI>
     *   <LI>From any interface type J to any interface type K, provided that J is a sub-interface of K. </LI>
     *   <LI>From any class type S to any interface type K, provided that S implements K. </LI>
     * </UL>
     * @param source type of the value to be converted
     * @param target the type to convert to
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    public boolean isWideningClassConvertable(ClassType source, ClassType target)
    {
        ClassType baseClass = source;
        while (baseClass != null)
        {
            // From any class type S to any class type T, provided that S is a
            // subclass of T.
            if (baseClass.equals(target))
                return true;

            // FIXME - not sure this will work for super interfaces, need to climb up implements chain.
            // From any class type S to any interface type K, provided that S
            // implements K.
            // From any interface type J to any interface type K, provided that
            // J is a sub-interface of K.
            if (baseClass.getInterfaces() != null)
                for (int i = 0; i < baseClass.getInterfaces().length; i++)
                    if (target.equals(baseClass.getInterfaces()[i]))
                        return true;

            // Handle the switch from ClassType to Class
            if (baseClass.getJavaClass() != null)
                return isWideningClassConvertable(baseClass.getJavaClass(), target);

            // Examine the base class
            baseClass = baseClass.getExtendsType();
        }
        return false;
    }

    /**
     * Tests whether the target class is a base class or is an interface that is
     * implemented by the source <b>java</b> Class.
     * <br/>
     * Implements that same comparisons as the other isWideningClassConvertable method,
     * but the source type is a java Class.
     * @param source java Class to be converted
     * @param target the type to convert to
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    private boolean isWideningClassConvertable(Class source, ClassType target)
    {
        Class baseClass = source;
        while (baseClass != null)
        {
            // From any class type S to any class type T, provided that S is a
            // subclass of T.
            if (equals(baseClass, target))
                return true;

            // FIXME - not sure this will work for super interfaces, need to climb up implements chain.
            // From any interface type J to any interface type K, provided that
            // J is a sub-interface of K.
            // From any class type S to any interface type K, provided that S
            // implements K.
            Class[] interfaces = baseClass.getInterfaces();
            if (interfaces != null)
                for (int i = 0; i < interfaces.length; i++)
                    if (equals(interfaces[i], target))
                        return true;

            // Examine the base class
            baseClass = baseClass.getSuperclass();
        }
        return false;
    }

    /*
     * Tests whether the Class and ClassType represent the same java type
     * by doing a simple name comparison.
     */
    private boolean equals(Class source, ClassType target)
    {
        return (target.getName().equals(MakerUtil.classToName(source)));
    }

    /**
     * Tests whether an Array reference can be widened from source type to target type.
     * <br/>
     * @param source type of the array to be converted
     * @param target the type of array to convert to
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    protected boolean isWideningArrayConvertable(ArrayType source, ClassType target)
    {
        // From any array type to type Object
        if (ClassMakerFactory.OBJECT_TYPE.equals(target))
            return true;

        // From any array type to type Cloneable
        if (ClassMakerFactory.CLONEABLE_TYPE.equals(target))
            return true;

        // From any array type SC[] to any array type TC[], provided that SC and TC are
        // reference types and there is a widening conversion from SC to TC.
        if (Type.isArray(target))
        {
            Type sourceOfType = source.getComponentType();
            Type targetOfType = target.toArray().getComponentType();
            if (!Type.isPrimitive(sourceOfType) && !Type.isPrimitive(targetOfType))
                return isConvertable(sourceOfType, targetOfType);
        }
        return false;
    }

    /**
     * Tests whether the source <code>Type</code> can be converted to the target <code>Type</code>.
     * <br/>
     * Method Invocation conversion supports the following conversions:
     * <UL>
     *   <LI>identity conversion</LI>
     *   <LI>widening integer conversion</LI>
     *   <LI>widening reference conversion</LI>
     * </UL>
     * @param source type of the array to be converted
     * @param target the type of array to convert to
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    public boolean isConvertable(Type source, Type target)
    {
        // identity conversion.
        if (source.equals(target))
            return true;

        // Widening Integer Conversion
        if (Type.isPrimitive(source) && Type.isPrimitive(target))
            return isWideningIntegerConvertable(source.toPrimitive(), target.toPrimitive());

        // Widening Reference conversion
        if (Type.isClass(source) && Type.isClass(target))
            return isWideningReferenceConvertable(source.toClass(), target.toClass());

        return false;
    }

    /**
     * Converts the value on top of the stack from from source <code>ClassType</code> to target <code>ClassType</code>.
     * <br/>
     * This conversion doesn't require any byte-code to be generated; the reference
     * can just be considered to be of the target type.
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-code
     * @param source <code>Type</code> of the value to be converted
     * @param target the <code>Type</code> to which to convert
     * @return the target <code>Type</code>
     */
    public Type wideningReferenceConversion(ClassMakerConstants maker, ClassType source, ClassType target)
    {
        if (isWideningReferenceConvertable(source, target))
            return target;
        // Should not get here. All calls to this method should be guarded by a call to isConvertable.
        throw new IllegalArgumentException("Cannot apply a Method Invocation Conversion from type " + source.getName() + " to type " + target.getName());
    }

    /**
     * Converts the value on top of the stack from from source <code>Type</code> to target <code>Type</code>.
     * <br/>
     * This conversion doesn't require any byte-code to be generated; the reference
     * can just be considered to be of the target type.
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-code
     * @param source <code>Type</code> of the value to be converted
     * @param target the <code>Type</code> to which to convert
     * @return the target <code>Type</code>
     */
    public Type convertTo(ClassMaker maker, Type source, Type target)
    {
        if (isConvertable(source, target))
            return target;
        // Should not get here. All calls to this method should be guarded by a call to isConvertable.
        throw new IllegalArgumentException("Cannot apply a Method Invocation Conversion from type " + source.getName() + " to type " + target.getName());
    }
}
