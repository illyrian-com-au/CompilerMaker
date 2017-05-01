// Copyright (c) 2010, Donald Strong.
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

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMakerText;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.jesub.ast.AstStructureVisitor;

public class ExpresssionMethodCallTest extends TestCase
{
    ClassMakerIfc buf = new ClassMakerText();
    AstExpressionFactory build = new AstExpressionFactory();
    AstStructureVisitor visitor = new AstStructureVisitor(buf);

    public void testLocalCall()
    {
    	AstExpression ast = build.Call(build.Name("f"), null);
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "f()", ast.toString());
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"f\", Push())]", buf.toString());
    }

    public void testDotsCall()
    {
    	AstExpression ast = build.Dot(build.Dot(build.Dot(build.Name("x"), build.Name("y")), build.Name("z")), 
    			build.Call(build.Name("f"), null));
        assertEquals("Wrong toString()", "x.y.z.f()", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(Get(Get(Get(\"x\"), \"y\"), \"z\"), \"f\", Push())]", buf.toString());
    }

    public void testStaticCall()
    {
    	AstExpression ast = build.Dot(build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("Object")), 
    			build.Call(build.Name("f"), null));
        assertEquals("Wrong toString()", "java.lang.Object.f()", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong stack contents", "[Call(\"java.lang.Object\", \"f\", Push())]", buf.toString());
    }

    public void testLocalCallParams()
    {
    	AstExpression ast = build.Call(build.Name("f"), 
    			build.Comma(build.Comma(build.Literal(1), build.Literal(2)), build.Literal(3)));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "f(1, 2, 3)", ast.toString());
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"f\", Push(Literal(1)).Push(Literal(2)).Push(Literal(3)))]", buf.toString());
    }

    public void testOtherCallParams()
    {
    	AstExpression ast = build.Dot(build.Name("x"), build.Call(build.Name("f"), build.Literal(1)));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "x.f(1)", ast.toString());
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(Get(\"x\"), \"f\", Push(Literal(1)))]", buf.toString());
    }

    public void testStaticCallParams()
    {  
    	AstExpression ast = build.Dot(build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("Object")), 
    			build.Call(build.Name("f"), build.Literal(1)));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "java.lang.Object.f(1)", ast.toString());
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(\"java.lang.Object\", \"f\", Push(Literal(1)))]", buf.toString());
    }

    public void testLocalCallSingleParam()
    {
        AstExpression ast = build.Call(build.Name("f"), build.Literal(1));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "f(1)", ast.toString());
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"f\", Push(Literal(1)))]", buf.toString());
    }

    public void testOtherCallSingleParam()
    {
        AstExpression ast = build.Dot(build.Name("x"), build.Call(build.Name("f"), build.Name("a")));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "x.f(a)", ast.toString());
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(Get(\"x\"), \"f\", Push(Get(\"a\")))]", buf.toString());
    }

    public void testStaticCallSingleParam()
    {  
        AstExpression ast = build.Dot(build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("Object")), 
                        build.Call(build.Name("f"), build.Dot(build.Name("a"), build.Name("b"))));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "java.lang.Object.f(a.b)", ast.toString());
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(\"java.lang.Object\", \"f\", Push(Get(Get(\"a\"), \"b\")))]", buf.toString());
    }

    public void testLocalCallExpressions()
    {
    	AstExpression ast = build.Call(build.Name("f"), 
    			build.Comma(build.Comma(build.Mult(build.Literal(2), build.Literal(3)), build.Literal("Hello")), build.Name("a")));
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong toString()", "f((2 * 3), \"Hello\", a)", ast.toString());
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"f\", Push(Mult(Literal(2), Literal(3))).Push(Literal(\"Hello\")).Push(Get(\"a\")))]", buf.toString());
    }

    public void testNewString()
    {
        AstExpression ast = build.New(build.Call(build.Name("String"), null));
        assertEquals("Wrong toString()", "new String()", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.String)", type.toString());
        assertEquals("Wrong output", "[New(java.lang.String).Init(null)]", buf.toString());
    }

    public void testNewJavaLangString()
    {
        AstExpression className = build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("String")); 
        AstExpression ast = build.New(build.Call(className, null));
        assertEquals("Wrong toString()", "new java.lang.String()", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.String)", type.toString());
        assertEquals("Wrong output", "[New(java.lang.String).Init(null)]", buf.toString());
    }

    public void testNewStringBuffer30()
    {
        AstExpression className = build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("StringBuffer")); 
        AstExpression ast = build.New(build.Call(className, build.Literal(30)));
        assertEquals("Wrong toString()", "new java.lang.StringBuffer(30)", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.StringBuffer)", type.toString());
        assertEquals("Wrong output", "[New(java.lang.StringBuffer).Init(Literal(30))]", buf.toString());
    }

    public void testCallDotCall()
    {
        // y().z()
        AstExpression ast = build.Dot(
                build.Call(build.Name("y"), null), build.Call(build.Name("z"), null));
        assertEquals("Wrong toString()", "y().z()", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(Call(This(), \"y\", Push()), \"z\", Push())]", buf.toString());
    }

    public void testDotCallDotCall()
    {
        // x.y().z()
        AstExpression ast = build.Dot(build.Dot(build.Name("x"), 
                build.Call(build.Name("y"), null)), build.Call(build.Name("z"), null));
        assertEquals("Wrong toString()", "x.y().z()", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(Call(Get(\"x\"), \"y\", Push()), \"z\", Push())]", buf.toString());
    }

    public void testCallParamCall()
    {
        // y(z())
        AstExpression ast = build.Call(build.Name("y"), build.Call(build.Name("z"), null));
        assertEquals("Wrong toString()", "y(z())", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(This(), \"y\", Push(Call(This(), \"z\", Push())))]", buf.toString());
    }

    public void testDotCallParamCall()
    {
        // x.y(z())
        AstExpression ast = build.Dot(build.Name("x"), 
                build.Call(build.Name("y"), build.Call(build.Name("z"), null)));
        assertEquals("Wrong toString()", "x.y(z())", ast.toString());
        Type type = ast.resolveType(visitor);
        assertEquals("Wrong type", "ClassType(java.lang.Object)", type.toString());
        assertEquals("Wrong output", "[Call(Get(\"x\"), \"y\", Push(Call(This(), \"z\", Push())))]", buf.toString());
    }
}
