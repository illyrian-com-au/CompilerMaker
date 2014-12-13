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
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.jesub.ast.AstStructureVisitor;

public abstract class AstExpressionBase implements AstExpression, SourceLine
{
    String filename = null;
    int    lineNumber = 0;
    
    public Type resolveType(AstExpressionVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolve Type: " + getClass().getSimpleName());
    }
    
    public String resolvePath(AstExpressionVisitor visitor)
    {
        //return null;
        throw new IllegalStateException("Cannot resolve Path: " + getClass().getSimpleName());
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
        throw new IllegalStateException("Cannot resolve MakerField: " + getClass().getSimpleName());
    }
    
    public CallStack resolveCallStack(AstExpressionVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolve CallStack: " + getClass().getSimpleName());
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
        throw new IllegalStateException("Cannot resolveImports: " + getClass().getSimpleName());
    }

    public void resolveImplements(AstStructureVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolveImplements: " + getClass().getSimpleName());
    }


    public void setSourceLine(SourceLine sourceLine)
    {
        filename = sourceLine.getFilename();
        lineNumber = sourceLine.getLineNumber();
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
