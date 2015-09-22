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

import au.com.illyrian.classmaker.SourceLine;


public class AstStructureBase /*extends AstExpressionBase*/ implements AstStructure, SourceLine
{
    private String filename = null;
    private int    lineNumber = 0;
    
    public AstStructureBase() {
    }
    
    public void resolveMember(AstStructureVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolve Member: " + getClass().getSimpleName());
    }

    public int resolveModifiers(AstStructureVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolve Modifiers: " + getClass().getSimpleName());
    }
    
    public void resolveDeclaration(AstStructureVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolve Declaration: " + getClass().getSimpleName());
    }

    public void resolveStatement(AstStructureVisitor visitor)
    {
        throw new IllegalStateException("No override for resolveStatement in " + getClass().getSimpleName());
    }

    public void resolveImport(AstStructureVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolve Import: " + getClass().getSimpleName());
    }

    public void resolveImplements(AstStructureVisitor visitor)
    {
        throw new IllegalStateException("Cannot resolve Implements: " + getClass().getSimpleName());
    }

    public String toSignature()
    {
        return toString();
    }

    public int size()
    {
    	return 1;
    }
    
    public void setSourceLine(SourceLine sourceLine)
    {
        if (sourceLine != null)
        {
            filename = sourceLine.getFilename();
            lineNumber = sourceLine.getLineNumber();
        }
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
