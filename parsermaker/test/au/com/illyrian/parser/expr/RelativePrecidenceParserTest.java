package au.com.illyrian.parser.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class RelativePrecidenceParserTest extends TestCase
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
    
    OperatorPrecidenceParser createParser()
    {
        AstExpressionPrecidenceParser parser = new AstExpressionPrecidenceParser();
        AstExpressionFactory factory = new AstExpressionFactory();
        parser.setAstExpressionFactory(factory);
        return parser;
//        OperatorPrecidenceParser<AstExpression> parser = new OperatorPrecidenceParser<AstExpression>();
//        AstExpressionFactory factory = new AstExpressionFactory();
//        AstExpressionPrecidenceAction actions = new AstExpressionPrecidenceAction(factory);
//        parser.setPrecidenceActions(actions);
//        parser.addLedOperator("==", ParserConstants.EQ, 8, Operator.BINARY);
//        parser.addLedOperator("!=", ParserConstants.NE, 8, Operator.BINARY);
//        parser.addLedOperator("&&", ParserConstants.ANDTHEN, 4, Operator.BINARY);
//        parser.addLedOperator("||", ParserConstants.ORELSE, 3, Operator.BINARY);
//       return parser;
    }

    public void testShortcutParser1() throws Exception
    {
        out.println("a == b != c");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a == b) != c)", result.toString());
    }

    public void testShortcutParser2() throws Exception
    {
        out.println("a && b");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(a && b)", result.toString());
    }

    public void testShortcutParser3() throws Exception
    {
        out.println("a && b && c && d && e");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((((a && b) && c) && d) && e)", result.toString());
    }

    public void testShortcutParser4() throws Exception
    {
        out.println("a || b || c || d || e");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((((a || b) || c) || d) || e)", result.toString());
    }

    public void testShortcutParser5() throws Exception
    {
        out.println("a || b && c || d && e");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a || (b && c)) || (d && e))", result.toString());
    }

    public void testShortcutParser6() throws Exception
    {
        out.println("a && b && c || d && e && f");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(((a && b) && c) || ((d && e) && f))", result.toString());
    }

    public void testShortcutParser7() throws Exception
    {
        out.println("a || b || c && d || e || f");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((((a || b) || (c && d)) || e) || f)", result.toString());
    }

    public void testShortcutParser8() throws Exception
    {
        out.println("a == b || c != d && e == f");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a == b) || ((c != d) && (e == f)))", result.toString());
    }

    public void testShortcutParser9() throws Exception
    {
        out.println("a == b && c != d || e == f");
        OperatorPrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(((a == b) && (c != d)) || (e == f))", result.toString());
    }

}
