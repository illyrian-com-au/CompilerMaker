package au.com.illyrian.parser.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.expressionparser.ExpressionAction;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.Operator;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.OperatorImpl;
import au.com.illyrian.parser.impl.PrecidenceParser;
import au.com.illyrian.parser.maker.ExpressionActionMaker;

public class CastingPrecidenceParserTest extends TestCase
{
    StringWriter writer;
    PrintWriter  out;
    
    public void setUp()
    {
        writer = new StringWriter() ;
        out = new PrintWriter(writer);
    }

    CompileModule createCompileModule(String input, PrecidenceParser parser) throws IOException
    {
        CompileModule compile = new CompileModule();
        compile.setInputString(input, null);
        compile.visitParser(parser);
        return compile;
    }

    PrecidenceParser createReferenceParser()
    {
        PrecidenceParser parser = new JavaOperatorPrecedenceParser();
        parser.addLedOperator("(", ")", ExpressionAction.DOT, 17, Operator.PARAMS, false);
        parser.addLedOperator(".", ExpressionAction.DOT, 16, Operator.BINARY, true);
        parser.addLedOperator("[", "]", ExpressionAction.DOT, 16, Operator.BRACKET, true);
        parser.addNudOperator("-", ExpressionAction.NEG, 15, Operator.PREFIX, true);
        parser.addNudOperator("(", ")", ExpressionAction.DOT, 14, Operator.BRACKET, true);
        parser.addLedOperator("+", ExpressionAction.ADD, 12, Operator.BINARY, true);
        parser.addLedOperator("-", ExpressionAction.SUBT, 12, Operator.BINARY, true);
        parser.addInfixOperator("&&", ExpressionAction.ORELSE, 4, Operator.BINARY, true);
        parser.addInfixOperator("||", ExpressionAction.ORELSE, 3, Operator.BINARY, true);
        parser.addInfixOperator("?", ExpressionAction.ORELSE, 2, Operator.BINARY, true);
        parser.addInfixOperator(":", ExpressionAction.ORELSE, 2, Operator.BINARY, true);
        PrecidenceActionString actions = new PrecidenceActionString();
        parser.setPrecidenceActions(actions);
        return parser;
    }

    public int a(int b) { return b;}
    public void dummy()
    {
        int x;
        int a = 0;
        x = (byte)(short)(int)(long)(3);
        x = (byte)(short)(int)(long)3;
        x = (byte)(short)(int)(long)a;
        x = (byte)(short)(int)(long)(a);
        x = (byte)(short)(int)(long)a(3);
    }

    public void testCastingParser1() throws Exception
    {
        out.println("(byte)b");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "b cast(byte)", result.toString());
    }

    public void testCastingParser2() throws Exception
    {
        out.println("(int)(b)");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(b) cast(int)", result.toString());
    }

    public void testCastingParser3() throws Exception
    {
        out.println("(short)b()");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "b() cast(short)", result.toString());
    }

    public void testCastingParser4() throws Exception
    {
        out.println("(byte)-2+b");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "2 -$ cast(byte) b +", result.toString());
    }

    public void testCastingParser5() throws Exception
    {
        out.println("(byte)(-2+b)");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(2 -$ b +) cast(byte)", result.toString());
    }

    public void testCastingParser6() throws Exception
    {
        out.println("(byte)-(2+b)");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(2 b +) -$ cast(byte)", result.toString());
    }

    public void testCastingParser7() throws Exception
    {
        out.println("(byte)-2+b()");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "2 -$ cast(byte) b() +", result.toString());
    }

    public void testCastingParser8() throws Exception
    {
        out.println("(byte)(short)(int)b()");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "b() cast(int) cast(short) cast(byte)", result.toString());
    }

    public void testCastingParser9() throws Exception
    {
        out.println("b[1]");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "b [1]", result.toString());
    }

    public void testCastingParser10() throws Exception
    {
        out.println("(int)b()[1][2]");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "b() [1] [2] cast(int)", result.toString());
    }

    public void testSimpleCastParser1() throws Exception
    {
        out.println("a.b.c");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a b . c .", result.toString());
    }

    public void testSimpleMethodCall1() throws Exception
    {
        out.println("a()");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a()", result.toString());
    }

    public void testSimpleMethodCall2() throws Exception
    {
        out.println("z.a()");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z a() .", result.toString());
    }

    public void testSimpleCastParser4() throws Exception
    {
        out.println("z().a");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z() a .", result.toString());
    }

    public void testSimpleCastParser5() throws Exception
    {
        out.println("(z().a)");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(z() a .)", result.toString());
    }

    public void testSimpleCastParser6() throws Exception
    {
        out.println("(z()).a");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(z()) a .", result.toString());
    }

    public void testSimpleCastParser7() throws Exception
    {
        out.println("z().(a())");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z() (a()) .", result.toString());
    }

    public void testSimpleCastParser8() throws Exception
    {
        out.println("((z()).(a()))");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((z()) (a()) .)", result.toString());
    }

    public void testSimpleCastParser9() throws Exception
    {
        out.println("(z).a()");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(z) a() .", result.toString());
    }

    public void testSimpleCastParser10() throws Exception
    {
        out.println("z[1].a[b+2][c- -7]");
        PrecidenceParser parser = createReferenceParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z [1] a . [b 2 +] [c 7 -$ -]", result.toString());
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
