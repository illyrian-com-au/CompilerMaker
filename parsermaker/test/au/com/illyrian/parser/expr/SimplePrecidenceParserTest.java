package au.com.illyrian.parser.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class SimplePrecidenceParserTest extends TestCase
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
    
    OperatorPrecidenceParser createSimpleParser()
    {
        AstExpressionPrecidenceParser parser = new AstExpressionPrecidenceParser();
        AstExpressionFactory factory = new AstExpressionFactory();
        parser.setAstExpressionFactory(factory);
        return parser;
    }

    public void testSimpleParser1() throws Exception
    {
        out.println("a * 3 + b / 2");
        
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("((a * 3) + (b / 2))", result.toString());
    }

    public void testSimpleParser2() throws Exception
    {
        out.println("a ^ b * c ^ d + e ^ f / g ^ (h + i)");
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((((a ^ (b * c)) ^ (d + e)) ^ (f / g)) ^ (h + i))", result.toString());
    }

    public void testSimpleParser3() throws Exception
    {
        out.println("a ^ - b ^ c  - - d ^ e ^ - f");
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((((a ^ -(b)) ^ (c - -(d))) ^ e) ^ -(f))", result.toString());
    }

    public void testSimpleParser4() throws Exception
    {
        out.println("a * b - c ^ d - e * f");
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(((a * b) - c) ^ (d - (e * f)))", result.toString());
    }
}
