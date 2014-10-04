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

package au.com.illyrian.dsl;

public class DslActionString implements DslAction
{

    public Object newDslLanguage(Object list)
    {
        return "\nlang(\n" + list + ")";
    }

    public Object addDslRule(Object language, Object rule)
    {
        return (language == null) ? rule : language + "\n" + rule;
    }

    public Object newDslRule(Object target, Object expr)
    {
        return target + " ::= " + expr;
    }

    public Object addDslSequence(Object rule, Object expr)
    {
        return rule + ", " + expr;
    }

    public Object addDslAlternative(Object rule, Object expr)
    {
        return rule + " | " + expr;
    }

    public Object newDslOptional(Object expr)
    {
        return "[" + expr + "] ";
    }

    public Object newDslTarget(String name, String type)
    {
        return (type==null ? name : name + ":" + type);
    }

    public Object newDslIdentifier(String identifier)
    {
        return identifier;
    }

    public Object newDslString(String string)
    {
        return "\"" + string + "\"";
    }

    public Object newDslDelimiter(String delimiter)
    {
        return "'" + delimiter + "'";
    }

}
