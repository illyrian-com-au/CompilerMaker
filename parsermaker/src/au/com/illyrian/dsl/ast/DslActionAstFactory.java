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

import au.com.illyrian.dsl.DslAction;

public class DslActionAstFactory implements DslAction
{

    public Object newDslLanguage(Object name)
    {
        return new DslLanguage((DslIdentifier)name);
    }

    public Object addDslRule(Object language, Object rule)
    {
        DslLanguage lang = (DslLanguage)language;
        DslRule expr = (DslRule)rule;
        return lang.addDslRule(expr);
    }

    public Object newDslRule(Object target, Object expr)
    {
        return new DslRule((DslTarget)target, (DslAst)expr);
    }

    public Object addDslSequence(Object expr, Object element)
    {
        DslSequence sequence;
        if (expr instanceof DslSequence)
            sequence = (DslSequence)expr;
        else
        {
            sequence = new DslSequence();
            sequence.addElement((DslAst)expr);
        }
        sequence.addElement((DslAst)element);
        return sequence;
    }

    public Object addDslAlternative(Object expr, Object element)
    {
        DslAlternative alternative;
        if (expr instanceof DslAlternative)
            alternative = (DslAlternative)expr;
        else
        {
            alternative = new DslAlternative();
            alternative.addElement((DslAst)expr);
        }
        alternative.addElement((DslAst)element);
        return alternative;
    }

    public Object newDslOptional(Object expr)
    {
        return new DslOptional((DslAst)expr);
    }

    public Object newDslTarget(String name, String type)
    {
        return new DslTarget(name, type);
    }

    public Object newDslIdentifier(String identifier)
    {
        return new DslIdentifier(identifier);
    }

    public Object newDslString(String string)
    {
        return new DslString(string);
    }

    public Object newDslDelimiter(String delimiter)
    {
        return new DslDelimiter(delimiter);
    }
}
