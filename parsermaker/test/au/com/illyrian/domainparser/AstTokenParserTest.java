package au.com.illyrian.domainparser;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.parser.impl.LexerInputString;

public class AstTokenParserTest extends TestCase
{
    AstTokenParser parser;
    LexerInputString input;
    AstExpression output;
    ModuleContext context;

    public void setUp()
    {
        input = new LexerInputString();
        parser = new AstTokenParser();
        context = new ModuleContext();
    }

    public void testInteger() throws Exception
    {
        input.setLine("{1234}");
        context.setInput(input);
        output = parser.parseClass(context);
        assertEquals("Integer expected",  "1234", output.toString());
    }

    public void testNoInput() throws Exception
    {
        input.setLine("");
        try {
            context.setInput(input);
            output = parser.parseClass(context);
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            assertEquals("Wrong ParserException",  "'{' expected", ex.getMessage());
        }
    }

    public void testNoExpresion() throws Exception
    {
        input.setLine("{");
        try {
            context.setInput(input);
            output = parser.parseClass(context);
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            assertEquals("Wrong ParserException",  "A numeric value is expected", ex.getMessage());
        }
    }

    public void testEnd() throws Exception
    {
        input.setLine("{}");
        try {
            context.setInput(input);
            output = parser.parseClass(context);
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            assertEquals("Wrong ParserException",  "A numeric value is expected", ex.getMessage());
        }
    }


    public void testOpenPException() throws Exception
    {
        input.setLine("{");
        try {
            context.setInput(input);
            output = parser.parseClass(context);
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "A numeric value is expected", ex.getMessage());
        }
    }

    public void testUnexpectedEndException() throws Exception
    {
        input.setLine("{1 ");
        try {
            context.setInput(input);
            output = parser.parseClass(context);
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "Unexpected end of input", ex.getMessage());
        }
    }

    public void testOperatorException() throws Exception
    {
        input.setLine("{1 2)");
        try {
            context.setInput(input);
            output = parser.parseClass(context);
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "Operator or '}' expected", ex.getMessage());
        }
    }

    public void testEndOfInputException() throws Exception
    {
        input.setLine("{1 + }");
        try {
            context.setInput(input);
            output = parser.parseClass(context);
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "A numeric value is expected", ex.getMessage());
        }
    }

}
