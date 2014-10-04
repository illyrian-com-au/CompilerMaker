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

package au.com.illyrian.dsl.ast;

import java.util.Vector;

public class DslAlternative extends DslAstBase implements DslAst
{
    final Vector<DslAst> alternative = new Vector<DslAst>();
    private static final DslAst[] AST_PROTO = new DslAst[0];
    
    public DslAlternative()
    {
    }
    
    public void addElement(DslAst element)
    {
        alternative.add(element);
    }
    
    DslAst[] getAlternatives()
    {
        return alternative.toArray(AST_PROTO);
    }

    public void sequence(DslAstVisitor visitor)
    {
        visitor.sequence(this);
    }

    public void alternative(DslAstVisitor visitor)
    {
        visitor.alternative(this);
    }

//    public void optional(DslAstVisitor visitor)
//    {
//        visitor.optional(this);
//    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (DslAst expr : alternative)
        {
            if (buf.length() > 1)
                buf.append(" | ");
            buf.append(expr);
        }
        buf.append(")");
        return buf.toString();
    }

}
