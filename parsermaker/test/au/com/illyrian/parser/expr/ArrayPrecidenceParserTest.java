package au.com.illyrian.parser.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class ArrayPrecidenceParserTest extends TestCase
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

    public void testArrayIndex1() throws Exception
    {
        out.println("a[1]");
        
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("a[1]", result.toString());
    }

    public void testArrayIndex2() throws Exception
    {
        out.println("a[1][2]");
        
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("a[1][2]", result.toString());
    }

    public void testArrayNew() throws Exception
    {
        out.println("new int[1]");
        
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("new int[](1)", result.toString());
    }

    public void testArray2New() throws Exception
    {
        out.println("new int[1][]");
        
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("new int[][](1)", result.toString());
    }

    public void testArray2x2New() throws Exception
    {
        out.println("new int[3][2][][]");
        
        OperatorPrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("new int[][][](2)[](3)", result.toString());
    }
}
