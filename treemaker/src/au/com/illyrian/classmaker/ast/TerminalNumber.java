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

public final class TerminalNumber extends AstExpressionBase
{
    public static final int LONG   = 8;
    public static final int INT    = 4;
    public static final int CHAR   = 3;
    public static final int SHORT  = 2;
    public static final int BYTE   = 1;

    private final long value;
    private final int size;
    
    public TerminalNumber(long longValue)
    {
        value = longValue;
        size = LONG;
    }
    
    public TerminalNumber(Long longValue)
    {
        value = longValue.longValue();
        size = INT;
    }
    
    public TerminalNumber(int intValue)
    {
        value = intValue;
        size = INT;
    }
    
    public TerminalNumber(Integer intValue)
    {
        value = intValue.intValue();
        size = INT;
    }
    
    public TerminalNumber(short shortValue)
    {
        value = shortValue;
        size = SHORT;
    }
    
    public TerminalNumber(Short shortValue)
    {
        value = shortValue.shortValue();
        size = SHORT;
    }
    
    public TerminalNumber(byte byteValue)
    {
        value = byteValue;
        size = BYTE;
    }
    
    public TerminalNumber(Byte byteValue)
    {
        value = byteValue.byteValue();
        size = BYTE;
    }
    
    public TerminalNumber(char charValue)
    {
        value = charValue;
        size = CHAR;
    }
    
    public TerminalNumber(Character charValue)
    {
        value = charValue.charValue();
        size = CHAR;
    }
    
    public long longValue()
    {
        return value;
    }
    
    public int intValue()
    {
        return (int)value;
    }
    
    public short shortValue()
    {
        return (short)value;
    }
    
    public byte byteValue()
    {
        return (byte)value;
    }
    
    public char charValue()
    {
        return (char)value;
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
        switch (size)
        {
        case LONG:
            return Long.toString(value) + "l";
        case SHORT:
            return Short.toString(shortValue()) + "s";
        case CHAR: // FIXME convert chars outside Latin printable range to octal.
            if (' ' <= value && value <= '~')
                return "'" + Character.toString(charValue()) + "'";
            else
                return toOctalString((int)value);
        case BYTE:
            return Byte.toString(byteValue()) + "b";
        default:
            return Integer.toString(intValue());
        }
    }
    
    private String toOctalString(int ch)
    {
        String octal = Integer.toOctalString(ch);
        int    len = octal.length();
        String pad = (len < 3) ? "000".substring(len) : "";
        return "'\\" + pad + octal + "'";
    }
}
