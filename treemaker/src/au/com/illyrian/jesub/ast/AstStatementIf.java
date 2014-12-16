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

public class AstStatementIf extends AstStatementBase
{
    AstExpression condition = null;
    AstStructure  thenCode = null;
    AstStructure  elseCode = null;
    
    public AstStatementIf()
    {
    }
    
    public AstStatementIf(AstExpression expression, AstStructure  thenCode, AstStructure  elseCode)
    {
        this.condition = expression;
        this.thenCode = thenCode;
        this.elseCode = elseCode;
    }

    public AstExpression getCondition()
    {
        return condition;
    }

    public void setCondition(AstExpression condition)
    {
        this.condition = condition;
    }

    public AstStructure getThenCode()
    {
        return thenCode;
    }

    public void setThenCode(AstStructure thenCode)
    {
        this.thenCode = thenCode;
    }

    public AstStructure getElseCode()
    {
        return elseCode;
    }

    public void setElseCode(AstStructure elseCode)
    {
        this.elseCode = elseCode;
    }

    public void resolveStatement(AstStructureVisitor visitor)
    {
        visitor.resolveStatement(this);
    }

	public AstStatementIf toIf() 
	{
		return this;
	}
	
	public String toString()
	{
		return "if (" + getCondition() + ") ... " + (elseCode == null ? "" : " else ... ");
	}
}
