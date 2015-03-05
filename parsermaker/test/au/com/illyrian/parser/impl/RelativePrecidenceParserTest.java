package au.com.illyrian.parser.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.expressionparser.ExpressionAction;
import au.com.illyrian.parser.maker.PrecidenceActionFactory;

public class RelativePrecidenceParserTest extends TestCase
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
    
    PrecidenceParser createParser()
    {
        PrecidenceParser<AstExpression> parser = new JavaOperatorPrecedenceParser<AstExpression>();
        parser.addInfixOperator("==", ParserConstants.EQ, 8, Operator.BINARY, true);
        parser.addInfixOperator("!=", ParserConstants.NE, 8, Operator.BINARY, true);
        parser.addInfixOperator("&&", ParserConstants.ANDTHEN, 4, Operator.BINARY, true);
        parser.addInfixOperator("||", ParserConstants.ORELSE, 3, Operator.BINARY, true);
        PrecidenceAction<AstExpression> actions = new PrecidenceActionFactory();
        parser.setPrecidenceActions(actions);
       return parser;
    }

    public void testShortcutParser1() throws Exception
    {
        out.println("a == b != c");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a == b) != c)", result.toString());
    }

    public void testShortcutParser2() throws Exception
    {
        out.println("a && b");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(a && b)", result.toString());
    }

    public void testShortcutParser3() throws Exception
    {
        out.println("a && b && c && d && e");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((((a && b) && c) && d) && e)", result.toString());
    }

    public void testShortcutParser4() throws Exception
    {
        out.println("a || b || c || d || e");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((((a || b) || c) || d) || e)", result.toString());
    }

    public void testShortcutParser5() throws Exception
    {
        out.println("a || b && c || d && e");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a || (b && c)) || (d && e))", result.toString());
    }

    public void testShortcutParser6() throws Exception
    {
        out.println("a && b && c || d && e && f");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(((a && b) && c) || ((d && e) && f))", result.toString());
    }

    public void testShortcutParser7() throws Exception
    {
        out.println("a || b || c && d || e || f");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((((a || b) || (c && d)) || e) || f)", result.toString());
    }

    public void testShortcutParser8() throws Exception
    {
        out.println("a == b || c != d && e == f");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "((a == b) || ((c != d) && (e == f)))", result.toString());
    }

    public void testShortcutParser9() throws Exception
    {
        out.println("a == b && c != d || e == f");
        PrecidenceParser parser = createParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("Wrong expression", "(((a == b) && (c != d)) || (e == f))", result.toString());
    }

}
