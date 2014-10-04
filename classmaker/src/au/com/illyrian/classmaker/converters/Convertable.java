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
import au.com.illyrian.classmaker.types.Type;

/**
 * Defines the interface for a number of strategies that convert from one <code>Type</code> to another.
 * @author dstrong
 */
public interface Convertable
{
    /**
     * Tests whether the source <code>Type</code> can be converted to the target <code>Type</code>.
     * @param source <code>Type</code> of the value to be converted
     * @param target the <code>Type</code> to which to convert
     * @return <code>true</code> if source can be converted to target; otherwise <code>false</code>
     */
    public boolean isConvertable(Type source, Type target);

    /**
     * Converts the value on top of the stack from from source <code>Type</code> to target <code>Type</code>.
     * Generates the appropriate byte-code to perform the conversion.
     * @param maker the <code>ClassMaker</code> instance in which to generate the byte-codes
     * @param source <code>Type</code> of the value to be converted
     * @param target the <code>Type</code> to which to convert
     * @return the target <code>Type</code>
     */
    public Type convertTo(ClassMaker maker, Type source, Type target);
}
