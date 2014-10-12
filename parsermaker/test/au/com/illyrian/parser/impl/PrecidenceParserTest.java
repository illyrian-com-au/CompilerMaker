package au.com.illyrian.parser.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import au.com.illyrian.expressionparser.ExpressionAction;
import au.com.illyrian.parser.Operator;
import au.com.illyrian.parser.impl.PrecidenceParser;
import junit.framework.TestCase;

public class PrecidenceParserTest extends TestCase
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
        PrecidenceParser parser = new PrecidenceParser();
        parser.addInfixOperator("^", ExpressionAction.NOP, 5, Operator.BINARY, false);
        parser.addPrefixOperator("-", ExpressionAction.NEG, 4, Operator.PREFIX);
        parser.addInfixOperator("*", ExpressionAction.MULT, 2, Operator.BINARY, true);
        parser.addInfixOperator("/", ExpressionAction.DIV, 2, Operator.BINARY, true);
        parser.addInfixOperator("%", ExpressionAction.REM, 2, Operator.BINARY, true);
        parser.addInfixOperator("+", ExpressionAction.ADD, 1, Operator.BINARY, true);
        parser.addInfixOperator("-", ExpressionAction.SUBT, 1, Operator.BINARY, true);
        PrecidenceActionString actions = new PrecidenceActionString();
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
        assertEquals("a 3 * b 2 / +", result.toString());
    }

    public void testSimpleParser2() throws Exception
    {
        out.println("a ^ b * c ^ d + e ^ f / g ^ (h + i)");
        PrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a b ^ c d ^ * e f ^ g (h i +) ^ / +", result.toString());
    }

    public void testSimpleParser3() throws Exception
    {
        out.println("a ^ - b ^ c  - - d ^ e ^ - f");
        PrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a b c ^ -$ ^ d e f -$ ^ ^ -$ -", result.toString());
    }

    public void testSimpleParser4() throws Exception
    {
        out.println("a * b - c ^ d - e * f");
        PrecidenceParser parser = createSimpleParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a b * c d ^ - e f * -", result.toString());
    }

    PrecidenceParser createIncrementParser()
    {
        PrecidenceParser parser = new PrecidenceParser();
        parser.addInfixOperator(".", ExpressionAction.DOT, 16, Operator.BINARY, true);
        parser.addPostfixOperator("--", ExpressionAction.POSTDEC, 15, Operator.POSTFIX);
        parser.addPostfixOperator("++", ExpressionAction.POSTINC, 15, Operator.POSTFIX);
        parser.addPrefixOperator("-", ExpressionAction.NEG, 14, Operator.PREFIX);
        parser.addPrefixOperator("--", ExpressionAction.DEC, 14, Operator.PREFIX);
        parser.addPrefixOperator("++", ExpressionAction.INC, 14, Operator.PREFIX);
        parser.addInfixOperator("*", ExpressionAction.MULT, 12, Operator.BINARY, true);
        parser.addInfixOperator("/", ExpressionAction.DIV, 12, Operator.BINARY, true);
        parser.addInfixOperator("%", ExpressionAction.REM, 12, Operator.BINARY, true);
        parser.addInfixOperator("+", ExpressionAction.ADD, 11, Operator.BINARY, true);
        parser.addInfixOperator("-", ExpressionAction.SUBT, 11, Operator.BINARY, true);
        parser.addInfixOperator("=", ExpressionAction.ASSIGN, 1, Operator.BINARY, false);
        PrecidenceActionString actions = new PrecidenceActionString();
        parser.setPrecidenceActions(actions);
        return parser;
    }

    public void testPostfixOperator() throws Exception
    {
        out.println("a--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a $--", result.toString());
    }

    public void testPostincAddPostdec() throws Exception
    {
        out.println("a++ +b--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a $++ b $-- +", result.toString());
    }

    public void testPostdecMultPostinc() throws Exception
    {
        out.println("a-- *b++");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "a $-- b $++ *", result.toString());
    }

    public void testIncrementParser1() throws Exception
    {
        out.println("b = - ++a--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "b a $-- ++$ -$ =", result.toString());
    }

    public void testIncrementParser2() throws Exception
    {
        out.println("b = - z.a");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "b z a . -$ =", result.toString());
    }

    public void testPostIncParser3() throws Exception
    {
        out.println("z.a--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z a . $--", result.toString());
    }

    public void testPreIncParser3() throws Exception
    {
        out.println("--z.a");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z a . --$", result.toString());
    }

    public void testPrePostIncParser3() throws Exception
    {
        out.println("--z.a--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "z a . $-- --$", result.toString());
    }
}
