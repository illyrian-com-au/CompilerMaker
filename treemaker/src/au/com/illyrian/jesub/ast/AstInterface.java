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
import au.com.illyrian.classmaker.ast.ResolvePath;
import au.com.illyrian.classmaker.ast.TerminalName;


public class AstInterface extends AstStructureBase
{
    private AstModifiers modifiers = null;
    private TerminalName className = null;
    private AstExpression baseClass = null;
    private AstStructure  membersList = null;
    
    public AstInterface()
    {
    }
    
    public AstInterface(AstModifiers modifiers, TerminalName className)
    {
        this();
        setModifiers(modifiers);
        setClassName(className);
    }
    
    public AstInterface(AstModifiers modifiers, TerminalName className, AstExpression baseClass, 
    		AstStructure membersList)
    {
        setModifiers(modifiers);
        setClassName(className);
        setExtends(baseClass);
        setMembers(membersList);
    }
    
    public AstModifiers getModifiers()
    {
        return modifiers;
    }

    public void setModifiers(AstModifiers modifiers)
    {
        this.modifiers = modifiers;
    }

    public void setClassName(TerminalName className)
    {
        this.className = className;
    }

    public TerminalName getClassName()
    {
        return className;
    }
    
    public void setExtends(AstExpression className)
    {
        this.baseClass = className;
    }
    
    public ResolvePath getExtends()
    {
        return baseClass;
    }

    public AstStructure getMembers()
    {
        return membersList;
    }

    public void setMembers(AstStructure membersList)
    {
        this.membersList = membersList;
    }

    public AstInterface add(AstStructure member)
    {
    	if (membersList == null)
    		membersList = member;
    	else
    		membersList = new AstStructureLink(membersList, member);
        return this;
    }

    public void resolveDeclaration(AstStructureVisitor visitor)
    {
        visitor.resolveDeclaration(this);
    }
    
    public String toSignature()
    {
    	StringBuffer buf = new StringBuffer();
    	if (modifiers != null)
    		buf.append(modifiers + " ");
    	buf.append("interface " + className + "");
    	if (baseClass != null)
    		buf.append(" extends " + baseClass);
    	return buf.toString();
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(toSignature());
        buf.append(" {\n");
        buf.append(membersList == null ? "" : membersList.toSignature());
        buf.append("\n}\n");
        return buf.toString();
    }
}
