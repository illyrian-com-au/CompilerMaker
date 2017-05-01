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

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.jesub.ast.AstStructureVisitor;

public abstract class AstExpressionBase implements AstExpression, LineNumber
{
    private String filename = null;
    private int    lineNumber = 0;
    
    public AstExpressionBase()
    {
    }
    
    public AstExpressionBase(SourceLine sourceLine)
    {
        setSourceLine(sourceLine);
    }
    
    public Type resolveType(AstExpressionVisitor visitor)
    {
        throw new IllegalStateException(getClass().getSimpleName() + " does not define resolveType(visitor)");
    }
    
    public String resolvePath(AstExpressionVisitor visitor)
    {
        throw new IllegalStateException(getClass().getSimpleName() + " does not define resolvePath(visitor)");
    }

    public Type resolveNew(AstExpressionVisitor visitor)
    {
        // FIXME Cannot apply new operator to expression: toString()
        throw new IllegalStateException("Cannot apply new operator to expression: " + toString());
    }

    /**
     * Resolve AST to a Type or null if a Type cannot be determined.
     * This method is overridden by DotOperator where a path may resolve to a
     * declared type; otherwise just use resolveType().
     */
    public Type resolveTypeOrNull(AstExpressionVisitor visitor)
    {
        return resolveType(visitor);
    }
    
    public MakerField resolveMakerField(AstExpressionVisitor visitor)
    {
        throw new IllegalStateException(getClass().getSimpleName() + " does not define resolveMakerField(visitor)");
    }
    
    public CallStack resolveCallStack(AstExpressionVisitor visitor)
    {
        return visitor.resolveCallStack(this);
    }

    public AndOrExpression resolveOrElse(AstExpressionVisitor visitor)
    {
    	return visitor.resolveOrElse(this);
    }

    public AndOrExpression resolveAndThen(AstExpressionVisitor visitor)
    {
    	return visitor.resolveAndThen(this);
    }
    
    public void resolveImport(AstStructureVisitor visitor)
    {
        throw new IllegalStateException(getClass().getSimpleName() + " does not define resolveImport(visitor)");
    }

    public void resolveImplements(AstStructureVisitor visitor)
    {
        throw new IllegalStateException(getClass().getSimpleName() + " does not define resolveImplements(visitor)");
    }

    public DeclaredType resolveDeclaredType(AstExpressionVisitor visitor) 
    {
        throw new IllegalStateException(getClass().getSimpleName() + " does not define resolveDeclaredType(visitor)");
    }

    public Type resolveArraySize(AstExpressionVisitor visitor) {
        throw new IllegalStateException(getClass().getSimpleName() + " does not define resolveArraySize(visitor)");
    }
    
    public MethodCall toMethodCall() {
        return null;
    }

    public void setSourceLine(SourceLine sourceLine)
    {
        if (sourceLine != null) {
            filename = sourceLine.getFilename();
            lineNumber = sourceLine.getLineNumber();
        }
    }
    
    public void setLineNumber(int number) {
        lineNumber = number;
    }
    
    public String getFilename()
    {
        return filename;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }
}
