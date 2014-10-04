// Copyright (c) 2014, Donald Strong.
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

package au.com.illyrian.classmaker.ast;

import au.com.illyrian.classmaker.types.Type;

public class TerminalDecimal extends AstExpressionBase
{
    public static final int DOUBLE   = 8;
    public static final int FLOAT    = 4;

    private final double value;
    private final int size;
    
    public TerminalDecimal(double doubleValue)
    {
        value = doubleValue;
        size = DOUBLE;
    }
    
    public TerminalDecimal(Double doubleValue)
    {
        value = doubleValue.doubleValue();
        size = DOUBLE;
    }
    
    public TerminalDecimal(float floatValue)
    {
        value = floatValue;
        size = FLOAT;
    }
    
    public TerminalDecimal(Float floatValue)
    {
        value = floatValue.floatValue();
        size = FLOAT;
    }
    
    public double doubleValue()
    {
        return value;
    }
    
    public float floatValue()
    {
        return (float)value;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public Type resolveType(AstExpressionVisitor visitor)
    {
        return visitor.resolveType(this);
    }
    
    public String toString()
    {
        if (size == FLOAT)
            return Float.toString(floatValue()) + "f";
        else
            return Double.toString(value);
    }
}
