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
 * The class for representing all array types in ClassMaker.
 * <br/>
 * Contains information about the dimensions and element types in the array as well as
 * the methods, fields and interfaces implemented by all arrays.
 */
public class ArrayType extends ClassType
{
    private Type    componentType;
    
    /**
     * Constructor for an <code>ArrayType</code>.
     * @param name the name of the array
     * @param signature the signature of the class
     * @param componentType the <code>Type</code> the array elements
     */

    public ArrayType(String name, String signature, Type componentType)
    {
        super(name, signature, ClassMakerFactory.OBJECT_TYPE);
        this.componentType = componentType;
    }

    /**
     * Get the <code>Type</code> of the array element.
     * @return the type of the array element
     */
    public Type getComponentType()
    {
    	return componentType;
    }
    
    /**
     * Set the <code>Type</code> of the array element. 
     * @param type the type of the array element
     */
    public void setComponentType(Type type)
    {
    	componentType = type;
    }
    
    /**
     * Convert this <code>Type</code> to an <code>ArrayType</code>.
     * @return  an <code>ArrayType</code>
     */
    public ArrayType toArray()
    {
    	return this;
    }
}
