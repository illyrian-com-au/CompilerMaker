package au.com.illyrian.parser.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.maker.PrecidenceActionFactory;

public class MethodPrecidenceParserTest extends TestCase
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
    
    PrecidenceParser createMethodParser()
    {
        PrecidenceParser<AstExpression> parser = new PrecidenceParser<AstExpression>();
        parser.addPostfixOperator("(", ")", ParserConstants.CALL, 17, Operator.PARAMS);
        parser.addInfixOperator(".", ParserConstants.DOT, 16, Operator.BINARY);
        parser.addPostfixOperator("[", "]", ParserConstants.INDEX, 16, Operator.BRACKET);
        parser.addPrefixOperator("-", ParserConstants.NEG, 4, Operator.PREFIX);
        parser.addInfixOperator("*", ParserConstants.MULT, 2, Operator.BINARY);
        parser.addInfixOperator("/", ParserConstants.DIV, 2, Operator.BINARY);
        parser.addInfixOperator("%", ParserConstants.REM, 2, Operator.BINARY);
        parser.addInfixOperator("+", ParserConstants.ADD, 1, Operator.BINARY);
        parser.addInfixOperator("-", ParserConstants.SUBT, 1, Operator.BINARY);
        parser.addInfixOperator(",", ParserConstants.COMMA, -1, Operator.BINARY);
        PrecidenceAction<AstExpression> actions = new PrecidenceActionFactory();
        parser.setPrecidenceActions(actions);
        return parser;
    }

    public void testMethodParser1() throws Exception
    {
        out.println("x()");
        
        PrecidenceParser parser = createMethodParser();
        createCompileModule(writer.toString(), parser);
        parser.nextToken();
        Object result = parser.expression();
        assertNotNull("Parser result is null", result);
        assertEquals("x()", result.toString());
    }

}
