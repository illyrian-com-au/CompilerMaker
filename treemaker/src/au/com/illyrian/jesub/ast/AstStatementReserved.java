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


public class AstStatementReserved extends AstStructureBase
{
	public static final AstStatementReserved THIS = new AstStatementReserved("this");
	public static final AstStatementReserved SUPER = new AstStatementReserved("super");
	public static final AstStatementReserved NULL = new AstStatementReserved("null");
	public final String reservedWord;
	
	public static AstStatementReserved lookup(String name)
	{
    	if (THIS.reservedWord.equals(name))
    		return THIS;
    	else if (SUPER.reservedWord.equals(name))
    		return SUPER;
    	else if (NULL.reservedWord.equals(name))
    		return NULL;
    	else
    		throw new IllegalStateException("Unknown reserved word: " + name);
	}
	
    private AstStatementReserved(String reservedWord)
    {
    	this.reservedWord = reservedWord;
    }
    
    public void resolveStatement(AstStructureVisitor visitor)
    {
        visitor.resolveStatement(this);
    }
    
    public String toString()
    {
    	return reservedWord;
    }
}
