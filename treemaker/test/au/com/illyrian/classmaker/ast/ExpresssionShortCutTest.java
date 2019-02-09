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
import au.com.illyrian.classmaker.types.Value;

public class ExpresssionShortCutTest extends TestCase
{
    ClassMakerIfc buf = new ClassMakerText();
    AstExpressionVisitor visitor = new AstExpressionVisitor(buf);
    AstExpressionFactory ast = new AstExpressionFactory();
   
    public void testAndThen()
    {
        AstExpression expr = ast.AndThen(ast.AndThen(ast.Name("a"), ast.Name("b")), ast.Name("c"));
        assertEquals("Wrong toString()", "((a && b) && c)", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(boolean)", type.toString());
        assertEquals("Wrong output", "[Logic(AndThen(AndThen(Get(\"a\")), Get(\"b\")), Get(\"c\"))]", buf.toString());        
    }

    public void testAndThen4()
    {
        AstExpression expr = ast.AndThen(ast.AndThen(ast.AndThen(ast.Name("a"), ast.Name("b")), ast.Name("c")), ast.Name("d"));
        assertEquals("Wrong toString()", "(((a && b) && c) && d)", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(boolean)", type.toString());
        assertEquals("Wrong output", "[Logic(AndThen(AndThen(AndThen(Get(\"a\")), Get(\"b\")), Get(\"c\")), Get(\"d\"))]", buf.toString());        
    }

    public void testOrElse()
    {
        AstExpression expr = ast.OrElse(ast.OrElse(ast.Name("a"), ast.Name("b")), ast.Name("c"));
        assertEquals("Wrong toString()", "((a || b) || c)", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(boolean)", type.toString());
        assertEquals("Wrong output", "[Logic(OrElse(OrElse(Get(\"a\")), Get(\"b\")), Get(\"c\"))]", buf.toString());        
    }
    
    public void testAndThenOrElse()
    {
        AstExpression expr = ast.OrElse(ast.AndThen(ast.Name("a"), ast.Name("b")), ast.Name("c"));
        assertEquals("Wrong toString()", "((a && b) || c)", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(boolean)", type.toString());
        assertEquals("Wrong output", "[Logic(OrElse(Logic(AndThen(Get(\"a\")), Get(\"b\"))), Get(\"c\"))]", buf.toString());        
    }

    public void testOrElseAndThen()
    {
        AstExpression expr = ast.AndThen(ast.OrElse(ast.Name("a"), ast.Name("b")), ast.Name("c"));
        assertEquals("Wrong toString()", "((a || b) && c)", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(boolean)", type.toString());
        assertEquals("Wrong output", "[Logic(AndThen(Logic(OrElse(Get(\"a\")), Get(\"b\"))), Get(\"c\"))]", buf.toString());        
    }

    public void testOrElseAndThenOrElse()
    {
        AstExpression expr = ast.AndThen(ast.OrElse(ast.Name("a"), ast.Name("b")), ast.OrElse(ast.Name("c"), ast.Name("d")));
        assertEquals("Wrong toString()", "((a || b) && (c || d))", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(boolean)", type.toString());
        String expected = 
        		"[Logic(AndThen(Logic(OrElse(Get(\"a\")), Get(\"b\"))), Logic(OrElse(Get(\"c\")), Get(\"d\")))]";
        assertEquals("Wrong output", expected, buf.toString());        
    }

    public void testAndThenOrElseAndThen()
    {
        AstExpression expr = ast.OrElse(ast.AndThen(ast.Name("a"), ast.Name("b")), ast.AndThen(ast.Name("c"), ast.Name("d")));
        assertEquals("Wrong toString()", "((a && b) || (c && d))", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(boolean)", type.toString());
        String expected = 
        		"[Logic(OrElse(Logic(AndThen(Get(\"a\")), Get(\"b\"))), Logic(AndThen(Get(\"c\")), Get(\"d\")))]";
        assertEquals("Wrong output", expected, buf.toString());        
    }
}
