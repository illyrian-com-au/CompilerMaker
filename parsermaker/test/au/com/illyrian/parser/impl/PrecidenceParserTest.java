package au.com.illyrian.parser.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.maker.PrecidenceActionFactory;

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
        PrecidenceParser<AstExpression> parser = new PrecidenceParser<AstExpression>();
        parser.addInfixOperator("^", ParserConstants.XOR, 5, Operator.BINARY, false);
        parser.addPrefixOperator("-", ParserConstants.NEG, 4, Operator.PREFIX);
        parser.addInfixOperator("*", ParserConstants.MULT, 2, Operator.BINARY, true);
        parser.addInfixOperator("/", ParserConstants.DIV, 2, Operator.BINARY, true);
        parser.addInfixOperator("%", ParserConstants.REM, 2, Operator.BINARY, true);
        parser.addInfixOperator("+", ParserConstants.ADD, 1, Operator.BINARY, true);
        parser.addInfixOperator("-", ParserConstants.SUBT, 1, Operator.BINARY, true);
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

    PrecidenceParser createIncrementParser()
    {
        PrecidenceParser<AstExpression> parser = new PrecidenceParser<AstExpression>();
        parser.addInfixOperator(".", ParserConstants.DOT, 16, Operator.BINARY, true);
        parser.addPostfixOperator("--", ParserConstants.POSTDEC, 15, Operator.POSTFIX);
        parser.addPostfixOperator("++", ParserConstants.POSTINC, 15, Operator.POSTFIX);
        parser.addPrefixOperator("-", ParserConstants.NEG, 14, Operator.PREFIX);
        parser.addPrefixOperator("--", ParserConstants.DEC, 14, Operator.PREFIX);
        parser.addPrefixOperator("++", ParserConstants.INC, 14, Operator.PREFIX);
        parser.addInfixOperator("*", ParserConstants.MULT, 12, Operator.BINARY, true);
        parser.addInfixOperator("/", ParserConstants.DIV, 12, Operator.BINARY, true);
        parser.addInfixOperator("%", ParserConstants.REM, 12, Operator.BINARY, true);
        parser.addInfixOperator("+", ParserConstants.ADD, 11, Operator.BINARY, true);
        parser.addInfixOperator("-", ParserConstants.SUBT, 11, Operator.BINARY, true);
        parser.addInfixOperator("=", ParserConstants.ASSIGN, 1, Operator.BINARY, false);
        PrecidenceAction<AstExpression> actions = new PrecidenceActionFactory();
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
        assertEquals("Wrong expression", "(a)--", result.toString());
    }

    public void testPostincAddPostdec() throws Exception
    {
        out.println("a++ +b--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a)++ + (b)--)", result.toString());
    }

    public void testPostdecMultPostinc() throws Exception
    {
        out.println("a-- *b++");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a)-- * (b)++)", result.toString());
    }

    public void testIncrementParser1() throws Exception
    {
        out.println("b = - ++a--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(b = -(++((a)--)))", result.toString());
    }

    public void testAssignNegParser2() throws Exception
    {
        out.println("b = - z.a");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(b = -(z.a))", result.toString());
    }

    public void testPostDecParser3() throws Exception
    {
        out.println("z.a--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(z.a)--", result.toString());
    }

    public void testPreDecParser3() throws Exception
    {
        out.println("--z.a");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "--(z.a)", result.toString());
    }

    public void testPrePostDecParser3() throws Exception
    {
        out.println("--z.a--");
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "--((z.a)--)", result.toString());
    }
}
