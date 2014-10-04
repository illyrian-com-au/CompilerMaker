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

import java.util.Vector;

import au.com.illyrian.classmaker.ast.ResolvePath;


public class AstDeclareModule extends AstStructureBase
{
    String fileName;
    AstClassPath packageName;
    Vector<ResolvePath> importsList = new Vector<ResolvePath>();
    AstDeclareClass declareClass;
    
    public AstDeclareModule()
    {
    }
    
    public AstDeclareModule(AstDeclareClass declareClass)
    {
        this.declareClass = declareClass;
    }
    
    public AstDeclareModule(String fileName)
    {
        setFileName(fileName);
    }
   
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public AstClassPath getPackageName()
    {
        return packageName;
    }

    public void setPackageName(AstClassPath packageName)
    {
        this.packageName = packageName;
    }

    public Vector<ResolvePath> getImportsList()
    {
        return importsList;
    }

    public void setImportsList(Vector<ResolvePath> importsList)
    {
        this.importsList = importsList;
    }

    public void addImportsList(AstClassPath className)
    {
        this.importsList.add(className);
    }

    public AstDeclareClass getDeclareClass()
    {
        return declareClass;
    }

    public void setDeclareClass(AstDeclareClass declareClass)
    {
        this.declareClass = declareClass;
    }

    public void resolveDeclaration(AstStructureVisitor visitor)
    {
        visitor.resolveDeclaration(this);
    }
}
