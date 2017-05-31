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

import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;
import au.com.illyrian.jesub.ast.AstStructureVisitor;

public class DotOperator extends AstExpressionBase
{
    private final AstExpression leftOperand;
    private final AstExpression rightOperand;
    
    public DotOperator(AstExpression left, AstExpression right)
    {
        leftOperand = left;
        rightOperand = right;
    }
    
    public DotOperator(AstExpression left, AstExpression right, SourceLine sourceLine)
    {
        super(sourceLine);
        leftOperand = left;
        rightOperand = right;
    }
    
    public Value resolveValue(AstExpressionVisitor visitor)
    {
        return visitor.resolveValue(this);
    }
    
    public Value resolveValueOrNull(AstExpressionVisitor visitor)
    {
        return visitor.resolveValueOrNull(this);
    }
    
    public Type resolveType(AstExpressionVisitor visitor)
    {
        return visitor.resolveType(this);
    }
    
    public MakerField resolveMakerField(AstExpressionVisitor visitor)
    {
        return visitor.resolveMakerField(this);
    }
    
    public String resolvePath(AstExpressionVisitor visitor)
    {
        return visitor.resolvePath(this);
    }
    
    public void resolveImplements(AstStructureVisitor visitor)
    {
         visitor.resolveImplements(this);
    }

    public void resolveImport(AstStructureVisitor visitor)
    {
         visitor.resolveImport(this);
    }

    public AstExpression getLeftOperand()
    {
        return leftOperand;
    }

    public AstExpression getRightOperand()
    {
        return rightOperand;
    }

    public String toString()
    {
        return leftOperand + "." + rightOperand;
    }

}
