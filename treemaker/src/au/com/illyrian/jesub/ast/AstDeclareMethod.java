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

package au.com.illyrian.jesub.ast;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;

public class AstDeclareMethod extends AstDeclareVariable
{
    private AstStructure parameters = null;
    private AstStructure methodBody = null;

    public AstDeclareMethod(AstModifiers modifiers, AstExpression type, TerminalName name)
    {
        super(modifiers, type, name);
    }

    public AstDeclareMethod(AstModifiers modifiers, AstExpression type, TerminalName name, AstStructure params,
            AstStructure code)
    {
        super(modifiers, type, name);
        this.parameters = params;
        this.methodBody = code;
    }

    public AstStructure getParameters()
    {
        return parameters;
    }

    public void setParameters(AstStructureLink parameters)
    {
        this.parameters = parameters;
    }

    public AstDeclareMethod addParameter(AstDeclareVariable param)
    {
        if (parameters == null)
            parameters = param;
        else
            parameters = new AstStructureLink(parameters, param);
        return this;
    }

    public AstStructure getMethodBody()
    {
        return methodBody;
    }

    public void setMethodBody(AstStructureLink methodBody)
    {
        this.methodBody = methodBody;
    }

    public AstDeclareMethod add(AstStructure statement)
    {
        if (methodBody == null)
            methodBody = statement;
        else
            methodBody = new AstStructureLink(methodBody, statement);
        return this;
    }

    public void resolveDeclaration(AstStructureVisitor visitor)
    {
        visitor.resolveDeclaration(this);
    }

    public String toSignature()
    {
        return super.toSignature() + "(" + parameters + ")";
    }

    public String toString()
    {
        return toSignature() + methodBody;
    }
}
