package au.com.illyrian.expressionparser;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.BinaryOperator;
import au.com.illyrian.classmaker.ast.UnaryOperator;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.Operator;
import au.com.illyrian.parser.impl.Operator;
import au.com.illyrian.parser.impl.PrecidenceAction;
import au.com.illyrian.parser.maker.PrecidenceActionFactory;

public class ExpressionParserTest extends TestCase
{
    ExpressionParser parser;
    LexerInputString input;
    Object output;

    PrecidenceAction action = new PrecidenceActionFactory() {
    	
        public AstExpression infixAction(Operator operator, Object leftOperand, Object rightOperand)
                throws ParserException
        {
        	if (operator.getIndex() == ExpressionAction.POW)
        	{
	        	AstExpression left = (AstExpression)leftOperand;
	        	AstExpression right = (AstExpression)rightOperand;
	        	AstExpression result = new BinaryOperator(BinaryOperator.POW, left, right);
	        	return result;
	        } else if (operator.getIndex() == ExpressionAction.INSTANCEOF)
        	{
	        	AstExpression left = (AstExpression)leftOperand;
	        	AstExpression right = (AstExpression)rightOperand;
	        	AstExpression result = new BinaryOperator(ExpressionAction.INSTANCEOF, left, right);
	        	return result;
	        }
        	return super.infixAction(operator, leftOperand, rightOperand);
        }

        public AstExpression prefixAction(Operator operator, Object operand) throws ParserException
        {
        	if (operator.getIndex() == ExpressionAction.NEW)
        	{
	        	AstExpression expr = (AstExpression)operand;
	        	AstExpression result = new UnaryOperator(operator.getIndex(), expr);
	        	return result;
	        }
        	return super.prefixAction(operator, operand);
        }
    };
    
    public void setUp()
    {
        input = new LexerInputString();
        parser = new ExpressionParser();
        parser.setPrecidenceActions(action);
        CompileModule unit = new CompileModule();
        unit.setInput(input);
        parser.setCompileUnit(unit);
    }

    public void testIdentifier() throws Exception
    {
        input.setLine("{fred}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Identifier expected",  "fred", output.toString());
    }

    public void testInteger() throws Exception
    {
        input.setLine("{1234}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Integer expected",  "1234", output.toString());
    }

    public void testAndVarVar() throws Exception
    {
        input.setLine("{fred+bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(fred + bert)", output.toString());
    }

    public void testAndVarVarConst() throws Exception
    {
        input.setLine("{fred+bert-3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((fred + bert) - 3)", output.toString());
    }

    public void testTimesDiv() throws Exception
    {
        input.setLine("{fred*bert/3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((fred * bert) / 3)", output.toString());
    }

    public void testTimesPlusDiv() throws Exception
    {
        input.setLine("{fred*3+bert/2}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((fred * 3) + (bert / 2))", output.toString());
    }

    public void testPlusTimesMinus() throws Exception
    {
        input.setLine("{fred+3*bert-2}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((fred + (3 * bert)) - 2)", output.toString());
    }

    public void testAndVarInt() throws Exception
    {
        input.setLine("{fred+8765}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(fred + 8765)", output.toString());
    }

    public void testAndIntVar() throws Exception
    {
        input.setLine("{4567 + fred}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(4567 + fred)", output.toString());
    }

    public void testAndIntInt() throws Exception
    {
        input.setLine("{1234+8765}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(1234 + 8765)", output.toString());
    }

    public void testMinus() throws Exception
    {
        input.setLine("{fred-bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(fred - bert)", output.toString());
    }

    public void testMultVarVar() throws Exception
    {
        input.setLine("{fred*bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(fred * bert)", output.toString());

    }

    public void testMultIntVar() throws Exception
    {
        input.setLine("{1234*bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(1234 * bert)", output.toString());

    }

    public void testMultVarInt() throws Exception
    {
        input.setLine("{fred*5678}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(fred * 5678)", output.toString());

    }

    public void testMultIntInt() throws Exception
    {
        input.setLine("{1234*5678}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(1234 * 5678)", output.toString());

    }

    public void testDiv() throws Exception
    {
        input.setLine("{fred/bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(fred / bert)", output.toString());
    }

    public void testAddPrentheses() throws Exception
    {
        input.setLine("{(a/b)+(c*d)}");
        parser.setInput(input);
        output = parser.parseExpression();
        System.out.println(output.toString());
        assertEquals("Wrong expression", "((a / b) + (c * d))", output.toString());
    }

    public void testMultPrentheses() throws Exception
    {
        input.setLine("{a*(b+c)*d/(e-f)}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(((a * (b + c)) * d) / (e - f))", output.toString());
    }

    public void testMultPlus() throws Exception
    {
        input.setLine("{a*b+c*d/e-f}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(((a * b) + ((c * d) / e)) - f)", output.toString());
    }

    public void testMultPlusMult() throws Exception
    {
        input.setLine("{a*b+c*d}");
        parser.setInput(input);
        output = parser.parseExpression();
        System.out.println(output.toString());
        assertEquals("Wrong expression", "((a * b) + (c * d))", output.toString());
    }
/*
    public void testFunction0() throws Exception
    {
        input.setLine("{f() = a/2;}");
        parser.setInput(input);
        output = parser.parseMember();
        System.out.println(output.toString());
        assertEquals("Wrong expression", "(f () (a 2 /))", output.toString());
    }

    public void testFunction1() throws Exception
    {
        input.setLine("{f(a) = a*1234;}");
        parser.setInput(input);
        output = parser.parseMember();
        System.out.println(output.toString());
        assertEquals("Wrong expression", "(f (a) (a 1234 *))", output.toString());
    }

    public void testFunction2() throws Exception
    {
        input.setLine("{f(a,b) = a - b;}");
        parser.setInput(input);
        output = parser.parseMember();
        System.out.println(output.toString());
        assertEquals("Wrong expression", "(f (a b) (a b -))", output.toString());
    }

    public void testDeclareClass() throws Exception
    {
        input.setLine("{class Test{ f(a,b) = a - b; }}");
        parser.setInput(input);
        output = parser.parseClass();
        System.out.println(output.toString());
        assertEquals("Wrong expression", "(class Test null ((f (a b) (a b -)))", output.toString());
    }

    public void testDeclareClassExtends() throws Exception
    {
        input.setLine("{class Test extends Fred{ f(a,b) = a - b; }}");
        parser.setInput(input);
        output = parser.parseClass();
        System.out.println(output.toString());
        assertEquals("Wrong expression", "(class Test Fred ((f (a b) (a b -)))", output.toString());
    }
*/
    public void testAssign() throws Exception
    {
        input.setLine("{a = b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a = b)", output.toString());
    }

    public void testAssignChain2() throws Exception
    {
        input.setLine("{a = b = 2}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a = (b = 2))", output.toString());
    }

    public void testAssignChainMult() throws Exception
    {
        input.setLine("{a = b = 2 * 3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a = (b = (2 * 3)))", output.toString());
    }

    public void testPower() throws Exception
    {
        input.setLine("{a ** b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a #24# b)", output.toString());
    }

    public void testPowerChain2() throws Exception
    {
        input.setLine("{a ** b ** 2}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a #24# (b #24# 2))", output.toString());
    }

    public void testPowerChainMult() throws Exception
    {
        input.setLine("{a ** 2 + b ** 2 * c ** 3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((a #24# 2) + ((b #24# 2) * (c #24# 3)))", output.toString());
    }

    public void testPowerChainMult2() throws Exception
    {
        input.setLine("{a ** 2 / b ** 2 - c ** 3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(((a #24# 2) / (b #24# 2)) - (c #24# 3))", output.toString());
    }

    public void testPowerChainMult3() throws Exception
    {
        input.setLine("{a ** (2 / b) ** (2 - c) ** 3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a #24# ((2 / b) #24# ((2 - c) #24# 3)))", output.toString());
    }

    public void testNot() throws Exception
    {
        input.setLine("{!a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "!(a)", output.toString());
    }

    public void testInverse() throws Exception
    {
        input.setLine("{~a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "~(a)", output.toString());
    }

    public void testNegate() throws Exception
    {
        input.setLine("{-a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "-(a)", output.toString());
    }

    public void testNegateMult() throws Exception
    {
        input.setLine("{-a*b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(-(a) * b)", output.toString());
    }

    public void testMultNegate() throws Exception
    {
        input.setLine("{a* -b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a * -(b))", output.toString());
    }

    public void testNegateSubt() throws Exception
    {
        input.setLine("{-a-b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(-(a) - b)", output.toString());
    }

    public void testSubtNegate() throws Exception
    {
        input.setLine("{a- -b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a - -(b))", output.toString());
    }

    public void testNegateAssign() throws Exception
    {
        input.setLine("{-a=b}");
        parser.setInput(input);
        output = parser.parseExpression();
        // Note: this is semantically incorrect.
        assertEquals("Wrong expression", "(-(a) = b)", output.toString());
    }

    public void testPrefix() throws Exception
    {
        input.setLine("{- ~ a + ! ~ b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(-(~(a)) + !(~(b)))", output.toString());
    }

    public void testAssignNegate() throws Exception
    {
        input.setLine("{a= -b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a = -(b))", output.toString());
    }

    public void testIncrement() throws Exception
    {
        input.setLine("{++a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "++(a)", output.toString());
    }

    public void testDecrement() throws Exception
    {
        input.setLine("{--a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "--(a)", output.toString());
    }

    public void testIncAddDec() throws Exception
    {
        input.setLine("{++a+ --b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(++(a) + --(b))", output.toString());
    }

    public void testDecMultInc() throws Exception
    {
        input.setLine("{--a* ++b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(--(a) * ++(b))", output.toString());
    }

    public void testPostIncrement() throws Exception
    {
        input.setLine("{a++}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a)++", output.toString());
    }

    public void testPostDecrement() throws Exception
    {
        input.setLine("{a--}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a)--", output.toString());
    }

    public void testPostincAddPostdec() throws Exception
    {
        input.setLine("{a++ +b--}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((a)++ + (b)--)", output.toString());
    }

    public void testPostdecMultPostinc() throws Exception
    {
        input.setLine("{a-- *b++}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((a)-- * (b)++)", output.toString());
    }

    public void testNegPostinc() throws Exception
    {
        input.setLine("{-a++}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "-((a)++)", output.toString());
    }

    public void testNegPostdec() throws Exception
    {
        input.setLine("{-a--}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "-((a)--)", output.toString());
    }

    public void testNew() throws Exception
    {
        input.setLine("{new Abc}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "#18(Abc)", output.toString());
    }

   
    public void testInstanceOf() throws Exception
    {
        input.setLine("{a instanceof Abc}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(a #55# Abc)", output.toString());
    }

    public void testBitwise1() throws Exception
    {
        input.setLine("{a << b >> c >>> d}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(((a << b) >> c) >>> d)", output.toString());
    }
    
    public void testBitwise2() throws Exception
    {
        input.setLine("{a >>> b >> c << d}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(((a >>> b) >> c) << d)", output.toString());
    }
    
    public void testBitwiseAdd() throws Exception
    {
        input.setLine("{a+1 >>> b-2 != c & d}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((((a + 1) >>> (b - 2)) != c) & d)", output.toString());
    }
    
    public void testRelations1() throws Exception
    {
        input.setLine("{a < b > c <= d >= e}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((((a < b) > c) <= d) >= e)", output.toString());
    }
    
    public void testRelations2() throws Exception
    {
        input.setLine("{a >= b <= c < d > e}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "((((a >= b) <= c) < d) > e)", output.toString());
    }
        
    public void testRelation3() throws Exception
    {
        input.setLine("{a+1 >= b*2 <= c<<2 < d>>>3 > e>>1}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "(((((a + 1) >= (b * 2)) <= (c << 2)) < (d >>> 3)) > (e >> 1))", output.toString());
    }
    
    public void testNoInput() throws Exception
    {
        input.setLine("");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            assertEquals("Wrong ParserException",  "'{' expected at start of code fragment.", ex.getMessage());
        }
    }

    public void testNoExpresion() throws Exception
    {
        input.setLine("{");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            assertEquals("Wrong ParserException",  "Expression expected.", ex.getMessage());
        }
    }

    public void testEnd() throws Exception
    {
        input.setLine("{}");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            assertEquals("Wrong ParserException",  "Expression expected.", ex.getMessage());
        }
    }

    public void testPerenthesesException() throws Exception
    {
        input.setLine("{(a+b]");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "\')\' expected.", ex.getMessage());
        }
    }

    public void testOperatorNotImplementedException() throws Exception
    {
        input.setLine("{(a+b<>");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "Operator not implemented: <>", ex.getMessage());
        }
    }

    public void testOpenPException() throws Exception
    {
        input.setLine("{");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "Expression expected.", ex.getMessage());
        }
    }

    public void testUnexpectedEndException() throws Exception
    {
        input.setLine("{a ");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "Operator or '}' expected.", ex.getMessage());
        }
    }

    public void testOperatorException() throws Exception
    {
        input.setLine("{a b)");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "Operator expected.", ex.getMessage());
        }
    }

    public void testEndOfInputException() throws Exception
    {
        input.setLine("{a + }");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "Expression expected.", ex.getMessage());
        }
    }

    public void testUnbalancedException() throws Exception
    {
        input.setLine("{(a+b))");
        try {
            parser.setInput(input);
            output = parser.parseExpression();
            fail("Expected Exception but got \"" + output + "\"");
        } catch (ParserException ex) {
            System.out.println(ex);
            assertEquals("Wrong ParserException",  "Unbalanced perentheses - too many \')\'.", ex.getMessage());
        }
    }
}
