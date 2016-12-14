package au.com.illyrian.domainparser;


import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.BinaryOperator;
import au.com.illyrian.classmaker.ast.TerminalNumber;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserBase;

public class AstTokenParser extends ParserBase implements ParseClass<AstExpression>
{
    public AstTokenParser()
    {
        setLexer(createLexer());
    }

    protected Latin1Lexer createLexer()
    {
        return new Latin1Lexer();
    }
    
    public AstExpression parseClass(CompilerContext context) throws ParserException
    {
        setCompilerContext(context);
        
        nextToken();
        return perentheses();
    }
    
    public AstExpression perentheses() throws ParserException
    {
    	AstExpression result = null;
        expect(TokenType.DELIMITER, "{", "'{' expected");
    	result = expr();
        if (getTokenType() == TokenType.END)
            throw error("Unexpected end of input");
        if (!match(TokenType.DELIMITER, "}"))
        	throw error("Operator or '}' expected");
        return result;
    }
    
    public AstExpression expr() throws ParserException
    {
    	AstExpression result = value();
    	while (getTokenType() == TokenType.OPERATOR)
    	{
    		String operator = getLexer().getTokenValue();
    		nextToken();
    		AstExpression operand2 = value();
    		result = createOperator(operator, result, operand2);
    	}
    	return result;
    }
    
    public AstExpression value() throws ParserException
    {
    	if (getTokenType() == TokenType.NUMBER)
    	{
    		Integer value = getLexer().getTokenInteger();
    		nextToken();
    		TerminalNumber number = new TerminalNumber(value);
    		return number;
    	} else {
            throw error("A numeric value is expected");
    	}
    }
    
    AstExpression createOperator(String operator, AstExpression operand1, AstExpression operand2) throws ParserException
    {
    	if ("+".equals(operator)) {
    		return new BinaryOperator(BinaryOperator.ADD, operand1, operand2);
    	} else if ("-".equals(operator)) {
    		return new BinaryOperator(BinaryOperator.SUBT, operand1, operand2);
    	} else if ("*".equals(operator)) {
    		return new BinaryOperator(BinaryOperator.MULT, operand1, operand2);
    	} else if ("/".equals(operator)) {
    		return new BinaryOperator(BinaryOperator.DIV, operand1, operand2);
    	} else if ("%".equals(operator)) {
    		return new BinaryOperator(BinaryOperator.REM, operand1, operand2);
    	} else {
    		throw error("Unknown operator: " + operator);
    	}
    }
}
