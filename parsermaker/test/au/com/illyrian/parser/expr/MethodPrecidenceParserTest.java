package au.com.illyrian.parser.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class MethodPrecidenceParserTest extends TestCase
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
    
    OperatorPrecidenceParser createMethodParser()
    {
        AstExpressionPrecidenceParser parser = new AstExpressionPrecidenceParser();
        AstExpressionFactory factory = new AstExpressionFactory();
        parser.setAstExpressionFactory(factory);
        return parser;
    }

    public void testMethodParser1() throws Exception
    {
        out.println("x()");
        
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("x()", result.toString());
    }

    public void testGetZ() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("getZ()", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("getZ()", result.toString());
    }

    public void testSelfGetZ() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("self.getZ()", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("self.getZ()", result.toString());
    }

    public void testSetZ() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("setZ(a)", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("setZ(a)", result.toString());
    }

    public void testSetxGety() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("setX(getY())", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("setX(getY())", result.toString());
    }

    public void testOtherSetxGety() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("other.setX(getY())", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("other.setX(getY())", result.toString());
    }

    public void testSetABC() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("set(a,b,c)", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("set(a, b, c)", result.toString());
    }

    public void testSelfSetABC() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("self.set(a,b,c)", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("self.set(a, b, c)", result.toString());
    }

    public void testOtherA() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("other.a", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("other.a", result.toString());
    }

    public void testOtherGetA() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("other.getA()", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("other.getA()", result.toString());
    }
    
    public void testOtherSetABC() throws Exception
    {
        OperatorPrecidenceParser parser = createMethodParser();
        createCompileModule("other.set(a,b,c)", parser);
        parser.nextToken();
        Object result = parser.expression();

        assertNotNull("Parser result is null", result);
        assertEquals("other.set(a, b, c)", result.toString());
    }
}
