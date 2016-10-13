package au.com.illyrian.parser.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class CastingPrecidenceParserTest extends TestCase
{
    StringWriter writer;
    PrintWriter  out;
    
    public void setUp()
    {
        writer = new StringWriter() ;
        out = new PrintWriter(writer);
    }

    ModuleContext createCompileModule(String input, OperatorPrecidenceParser parser) throws IOException
    {
        ModuleContext compile = new ModuleContext();
        compile.setInputString(input, null);
        compile.visitParser(parser);
        return compile;
    }

    OperatorPrecidenceParser createReferenceParser()
    {
        AstExpressionPrecidenceParser parser = new AstExpressionPrecidenceParser();
        AstExpressionFactory factory = new AstExpressionFactory();
        parser.setAstExpressionFactory(factory);
        return parser;
    }

    // StrawMan demonstrates some interesting problems with parsing the java cast operator.
    private static class StrawMan {
        public static int z;
        public int a(int b) { return b;}
        public void dummy()
        {
            int x;
            int bite = 1;
            int a = 0;
            // Parentheses are a prefix operator as well as a surrounding expressions
            x = (byte)(short)(int)(long)(3);
            x = (byte)(short)(int)(long)3;
            x = (byte)(short)(int)(long)a;
            x = (byte)(short)(int)(long)(a);
            x = (byte)(short)(int)(long)a(3);
            // A parser cannot generate the correct AST for the following without a semantic check.
            x = (byte)-x; // negative x cast to a byte
            x = (bite)-x; // bite minus x
            x = (StrawMan.z)-x;
            Object o = new StrawMan();
            StrawMan s = (StrawMan)o;
            x = ((StrawMan)o).a(2);
            (this).a(2);
            (s).a(3);
            s = (CastingPrecidenceParserTest.StrawMan)o;
        }
    }
    
    public void testStrawMan() throws Exception
    {
        StrawMan strawman = new StrawMan();
        strawman.dummy();
    }

    public void testCastingParser1() throws Exception
    {
        out.println("(byte)b");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(byte, b)", result.toString());
    }

    public void testCastingParser2() throws Exception
    {
        out.println("(int)(b)");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(int, b)", result.toString());
    }

    public void testCastingParser3() throws Exception
    {
        out.println("(short)b()");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(short, b())", result.toString());
    }

    public void testCastingParser4() throws Exception
    {
        out.println("(byte)-2+b");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(cast(byte, -(2)) + b)", result.toString());
    }

    public void testCastingParser5() throws Exception
    {
        out.println("(byte)(-2+b)");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(byte, (-(2) + b))", result.toString());
    }

    public void testCastingParser6() throws Exception
    {
        out.println("(byte)-(2+b)");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(byte, -((2 + b)))", result.toString());
    }

    public void testCastingParser7() throws Exception
    {
        out.println("(byte)-2+b()");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(cast(byte, -(2)) + b())", result.toString());
    }

    public void testMultipleCasting() throws Exception
    {
        out.println("(byte)(short)(int)b()");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(byte, cast(short, cast(int, b())))", result.toString());
    }

    public void testCastThis() throws Exception
    {
        out.println("(au.com.MyClass)this");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(au.com.MyClass, this)", result.toString());
    }

    public void testCastSuper() throws Exception
    {
        out.println("(au.com.MyClass)super");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(au.com.MyClass, super)", result.toString());
    }

    public void testInstanceof() throws Exception
    {
        out.println("this instanceof au.com.MyIface");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "instanceof(this, au.com.MyIface)", result.toString());
    }

    public void testIndex1() throws Exception
    {
        out.println("b[1]");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "b[1]", result.toString());
    }

    public void testCastingIndex1() throws Exception
    {
        out.println("(int)b()[1][2]");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(int, b()[1][2])", result.toString());
    }

    public void testSimpleDotPath() throws Exception
    {
        out.println("a.b.c");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a.b.c", result.toString());
    }

    public void testSimpleMethodCall1() throws Exception
    {
        out.println("a()");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a()", result.toString());
    }

    public void testSimpleMethodCall2() throws Exception
    {
        out.println("z.a()");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z.a()", result.toString());
    }

    public void testSimpleMethodCall3() throws Exception
    {
        out.println("z().a");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z().a", result.toString());
    }

    public void testSimpleMethodCall4() throws Exception
    {
        out.println("(z().a)");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z().a", result.toString());
    }

    public void testSimpleMethodCall5() throws Exception
    {
        out.println("(z()).a");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z().a", result.toString());
    }

    public void testSimpleMethodCall6() throws Exception
    {
        out.println("z().(a())");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z().a()", result.toString());
    }

    public void testSimpleMethodCall7() throws Exception
    {
        out.println("((z()).(a()))");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z().a()", result.toString());
    }

    public void testSimpleMethodCall8() throws Exception
    {
        out.println("(z).a()");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z.a()", result.toString());
    }

    public void testIndex2() throws Exception
    {
        out.println("z[1].a[b+2][c- -7]");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z[1].a[(b + 2)][(c - -(7))]", result.toString());
    }

    public void testPerentheses1() throws Exception
    {
        out.println("(3)-2");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(3 - 2)", result.toString());
    }

    public void testPerentheses2() throws Exception
    {
        out.println("(3)+2");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(3 + 2)", result.toString());
    }

    public void testPerentheses3() throws Exception
    {
        out.println("(byte)+2");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(byte, 2)", result.toString());
    }

    public void testPerentheses4() throws Exception
    {
        out.println("(int)-(2)");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(int, -(2))", result.toString());
    }

    public void testNew1() throws Exception
    {
        out.println("new String()");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "new String()", result.toString());
    }

    public void testNew2() throws Exception
    {
        out.println("new Thread(3)");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "new Thread(3)", result.toString());
    }

    public void testNew3() throws Exception
    {
        out.println("(Runnable)new Thread(3)");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(Runnable, new Thread(3))", result.toString());
    }

    public void testString1() throws Exception
    {
        out.println("(String)a");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(String, a)", result.toString());
    }

    public void testString2() throws Exception
    {
        out.println("(String)\"Hello world\"");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(String, \"Hello world\")", result.toString());
    }

    public void testString3() throws Exception
    {
        out.println("(String)(\"Hello\") + \"world\"");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(cast(String, \"Hello\") + \"world\")", result.toString());
    }

    public void testBoolean1() throws Exception
    {
        out.println("(boolean)a");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(boolean, a)", result.toString());
    }

    public void testBoolean2() throws Exception
    {
        out.println("!a");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "!(a)", result.toString());
    }

    public void testBoolean3() throws Exception
    {
        out.println("(boolean)!a");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "cast(boolean, !(a))", result.toString());
    }

    public void testBoolean4() throws Exception
    {
        out.println("(boolean)!a||b");
        OperatorPrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(cast(boolean, !(a)) || b)", result.toString());
    }
    
// Semantic check that cannot be detected by the parser.
//    public void testCastException1() throws Exception
//    {
//        out.println("(1)a");
//        PrecidenceParser parser = createReferenceParser();
//        createCompileModule(writer.toString(), parser);
//        parser.nextToken();
//        try {
//            Object result = parser.expression();
//            fail("Expected ParserException, got: " + result);
//        } catch (ParserException ex) 
//        {
//            assertEquals("Wrong exception", "z", ex.getMessage());
//        }
//    }

}
