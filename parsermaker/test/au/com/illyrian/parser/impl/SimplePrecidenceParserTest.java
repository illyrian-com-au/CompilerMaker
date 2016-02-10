package au.com.illyrian.parser.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.maker.PrecidenceActionFactory;

public class SimplePrecidenceParserTest extends TestCase
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
    
    PrecidenceParser createSimpleParser()
    {
        PrecidenceParser<AstExpression> parser = new PrecidenceParser<AstExpression>();
        parser.addInfixOperator("^", ParserConstants.XOR, 5, Operator.BINARYRIGHT);
        parser.addPrefixOperator("-", ParserConstants.NEG, 4, Operator.PREFIX);
        parser.addInfixOperator("*", ParserConstants.MULT, 2, Operator.BINARY);
        parser.addInfixOperator("/", ParserConstants.DIV, 2, Operator.BINARY);
        parser.addInfixOperator("%", ParserConstants.REM, 2, Operator.BINARY);
        parser.addInfixOperator("+", ParserConstants.ADD, 1, Operator.BINARY);
        parser.addInfixOperator("-", ParserConstants.SUBT, 1, Operator.BINARY);
        PrecidenceAction<AstExpression> actions = new PrecidenceActionFactory();
        parser.setPrecidenceActions(actions);
        return parser;
    }

    public void testSimpleParser1() throws Exception
    {
        out.println("a * 3 + b / 2");
        
        PrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("((a * 3) + (b / 2))", result.toString());
    }

    public void testSimpleParser2() throws Exception
    {
        out.println("a ^ b * c ^ d + e ^ f / g ^ (h + i)");
        PrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(((a ^ b) * (c ^ d)) + ((e ^ f) / (g ^ (h + i))))", result.toString());
    }

    public void testSimpleParser3() throws Exception
    {
        out.println("a ^ - b ^ c  - - d ^ e ^ - f");
        PrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a ^ -((b ^ c))) - -((d ^ (e ^ -(f)))))", result.toString());
    }

    public void testSimpleParser4() throws Exception
    {
        out.println("a * b - c ^ d - e * f");
        PrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(((a * b) - (c ^ d)) - (e * f))", result.toString());
    }
}
