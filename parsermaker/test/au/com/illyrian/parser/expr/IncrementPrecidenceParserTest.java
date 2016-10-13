package au.com.illyrian.parser.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class IncrementPrecidenceParserTest extends TestCase
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
    
    OperatorPrecidenceParser createIncrementParser()
    {
        AstExpressionPrecidenceParser parser = new AstExpressionPrecidenceParser();
        AstExpressionFactory factory = new AstExpressionFactory();
        parser.setAstExpressionFactory(factory);
        return parser;
//        OperatorPrecidenceParser<AstExpression> parser = new OperatorPrecidenceParser<AstExpression>();
//        AstExpressionFactory factory = new AstExpressionFactory();
//        AstExpressionPrecidenceAction actions = new AstExpressionPrecidenceAction(factory);
//        parser.setPrecidenceActions(actions);
//        parser.addLedOperator(".", ParserConstants.DOT, 16, Operator.BINARY);
//        parser.addLedOperator("--", ParserConstants.POSTDEC, 15, Operator.POSTFIX);
//        parser.addLedOperator("++", ParserConstants.POSTINC, 15, Operator.POSTFIX);
//        parser.addNudOperator("-", ParserConstants.NEG, 14, Operator.PREFIX);
//        parser.addNudOperator("--", ParserConstants.DEC, 14, Operator.PREFIX);
//        parser.addNudOperator("++", ParserConstants.INC, 14, Operator.PREFIX);
//        parser.addLedOperator("*", ParserConstants.MULT, 12, Operator.BINARY);
//        parser.addLedOperator("/", ParserConstants.DIV, 12, Operator.BINARY);
//        parser.addLedOperator("%", ParserConstants.REM, 12, Operator.BINARY);
//        parser.addLedOperator("+", ParserConstants.ADD, 11, Operator.BINARY);
//        parser.addLedOperator("-", ParserConstants.SUBT, 11, Operator.BINARY);
//        parser.addLedOperator("=", ParserConstants.ASSIGN, 1, Operator.BINARYRIGHT);
//        return parser;
    }

    public void testPostfixOperator() throws Exception
    {
        out.println("a--");
        OperatorPrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(a)--", result.toString());
    }

    public void testPostincAddPostdec() throws Exception
    {
        out.println("a++ +b--");
        OperatorPrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a)++ + (b)--)", result.toString());
    }

    public void testPostdecMultPostinc() throws Exception
    {
        out.println("a-- *b++");
        OperatorPrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a)-- * (b)++)", result.toString());
    }

    public void testIncrementParser1() throws Exception
    {
        out.println("b = - ++a--");
        OperatorPrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(b = -(++((a)--)))", result.toString());
    }

    public void testAssignNegParser2() throws Exception
    {
        out.println("b = - z.a");
        OperatorPrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(b = -(z.a))", result.toString());
    }

    public void testPostDecParser3() throws Exception
    {
        out.println("z.a--");
        OperatorPrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(z.a)--", result.toString());
    }

    public void testPreDecParser3() throws Exception
    {
        out.println("--z.a");
        OperatorPrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "--(z.a)", result.toString());
    }

    public void testPrePostDecParser3() throws Exception
    {
        out.println("--z.a--");
        OperatorPrecidenceParser parser = createIncrementParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "--((z.a)--)", result.toString());
    }
}
