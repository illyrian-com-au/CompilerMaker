package au.com.illyrian.domainparser;


import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.BinaryOperator;
import au.com.illyrian.classmaker.ast.TerminalNumber;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParserException;
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
    
    public AstExpression perentheses() throws ParserException
    {
    	AstExpression result = null;
        expect(Lexer.OPEN_P, "{", "'{' expected");
    	result = expr();
        if (getToken() == Lexer.END)
            throw error(getInput(), "Unexpected end of input");
        if (!match(Lexer.CLOSE_P, "}"))
        	throw error(getInput(), "Operator or '}' expected");
        return result;
    }
    
    public AstExpression expr() throws ParserException
    {
    	AstExpression result = value();
    	while (getToken() == Lexer.OPERATOR)
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
    	if (getToken() == Lexer.INTEGER)
    	{
    		Integer value = getLexer().getTokenInteger();
    		nextToken();
    		TerminalNumber number = new TerminalNumber(value);
    		return number;
    	} else {
            throw error(getInput(), "A numeric value is expected");
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
    		throw error(getInput(), "Unknown operator: " + operator);
    	}
    }
    
    public AstExpression parseClass() throws ParserException
    {
        nextToken();
        return perentheses();
    }
}
