package au.com.illyrian.expressionparser;

import junit.framework.TestCase;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputString;

public class ExpressionParserTest extends TestCase
{
    ExpressionParser parser;
    LexerInputString input;
    Object output;

    ExpressionAction action = new TextExpressionAction();
    
    public void setUp()
    {
        input = new LexerInputString();
        parser = new ExpressionParser();
        parser.setExpressionAction(action);
        CompileModule unit = new CompileModule();
        unit.setInput(input);
        parser.setCompileUnit(unit);
    }

    public void testIdentifier() throws Exception
    {
        input.setLine("{fred}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Identifier expected",  "fred", output);
    }

    public void testInteger() throws Exception
    {
        input.setLine("{1234}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Integer expected",  "1234", output);
    }

    public void testAndVarVar() throws Exception
    {
        input.setLine("{fred+bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred bert +", output);
    }

    public void testAndVarVarConst() throws Exception
    {
        input.setLine("{fred+bert-3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred bert + 3 -", output);
    }

    public void testTimesDiv() throws Exception
    {
        input.setLine("{fred*bert/3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred bert * 3 /", output);
    }

    public void testTimesPlusDiv() throws Exception
    {
        input.setLine("{fred*3+bert/2}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred 3 * bert 2 / +", output);
    }

    public void testPlusTimesMinus() throws Exception
    {
        input.setLine("{fred+3*bert-2}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred 3 bert * + 2 -", output);
    }

    public void testAndVarInt() throws Exception
    {
        input.setLine("{fred+8765}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred 8765 +", output);
    }

    public void testAndIntVar() throws Exception
    {
        input.setLine("{4567 + fred}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "4567 fred +", output);
    }

    public void testAndIntInt() throws Exception
    {
        input.setLine("{1234+8765}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "1234 8765 +", output);
    }

    public void testMinus() throws Exception
    {
        input.setLine("{fred-bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred bert -", output);
    }

    public void testMultVarVar() throws Exception
    {
        input.setLine("{fred*bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred bert *", output);

    }

    public void testMultIntVar() throws Exception
    {
        input.setLine("{1234*bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "1234 bert *", output);

    }

    public void testMultVarInt() throws Exception
    {
        input.setLine("{fred*5678}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred 5678 *", output);

    }

    public void testMultIntInt() throws Exception
    {
        input.setLine("{1234*5678}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "1234 5678 *", output);

    }

    public void testDiv() throws Exception
    {
        input.setLine("{fred/bert}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "fred bert /", output);
    }

    public void testAddPrentheses() throws Exception
    {
        input.setLine("{(a/b)+(c*d)}");
        parser.setInput(input);
        output = parser.parseExpression();
        System.out.println(output);
        assertEquals("Wrong expression", "(a b /) (c d *) +", output);
    }

    public void testMultPrentheses() throws Exception
    {
        input.setLine("{a*(b+c)*d/(e-f)}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a (b c +) * d * (e f -) /", output);
    }

    public void testMultPlus() throws Exception
    {
        input.setLine("{a*b+c*d/e-f}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b * c d * e / + f -", output);
    }

    public void testMultPlusMult() throws Exception
    {
        input.setLine("{a*b+c*d}");
        parser.setInput(input);
        output = parser.parseExpression();
        System.out.println(output);
        assertEquals("Wrong expression", "a b * c d * +", output);
    }

    public void testFunction0() throws Exception
    {
        input.setLine("{f() = a/2;}");
        parser.setInput(input);
        output = parser.parseMember();
        System.out.println(output);
        assertEquals("Wrong expression", "(f () (a 2 /))", output);
    }

    public void testFunction1() throws Exception
    {
        input.setLine("{f(a) = a*1234;}");
        parser.setInput(input);
        output = parser.parseMember();
        System.out.println(output);
        assertEquals("Wrong expression", "(f (a) (a 1234 *))", output);
    }

    public void testFunction2() throws Exception
    {
        input.setLine("{f(a,b) = a - b;}");
        parser.setInput(input);
        output = parser.parseMember();
        System.out.println(output);
        assertEquals("Wrong expression", "(f (a b) (a b -))", output);
    }

    public void testDeclareClass() throws Exception
    {
        input.setLine("{class Test{ f(a,b) = a - b; }}");
        parser.setInput(input);
        output = parser.parseClass();
        System.out.println(output);
        assertEquals("Wrong expression", "(class Test null ((f (a b) (a b -)))", output);
    }

    public void testDeclareClassExtends() throws Exception
    {
        input.setLine("{class Test extends Fred{ f(a,b) = a - b; }}");
        parser.setInput(input);
        output = parser.parseClass();
        System.out.println(output);
        assertEquals("Wrong expression", "(class Test Fred ((f (a b) (a b -)))", output);
    }

    public void testAssign() throws Exception
    {
        input.setLine("{a = b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b =", output);
    }

    public void testAssignChain2() throws Exception
    {
        input.setLine("{a = b = 2}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b 2 = =", output);
    }

    public void testAssignChainMult() throws Exception
    {
        input.setLine("{a = b = 2 * 3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b 2 3 * = =", output);
    }

    public void testPower() throws Exception
    {
        input.setLine("{a ** b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b **", output);
    }

    public void testPowerChain2() throws Exception
    {
        input.setLine("{a ** b ** 2}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b 2 ** **", output);
    }

    public void testPowerChainMult() throws Exception
    {
        input.setLine("{a ** 2 + b ** 2 * c ** 3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a 2 ** b 2 ** c 3 ** * +", output);
    }

    public void testPowerChainMult2() throws Exception
    {
        input.setLine("{a ** 2 / b ** 2 - c ** 3}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a 2 ** b 2 ** / c 3 ** -", output);
    }

    public void testNot() throws Exception
    {
        input.setLine("{!a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a !$", output);
    }

    public void testInverse() throws Exception
    {
        input.setLine("{~a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a ~$", output);
    }

    public void testNegate() throws Exception
    {
        input.setLine("{-a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a -$", output);
    }

    public void testNegateMult() throws Exception
    {
        input.setLine("{-a*b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a -$ b *", output);
    }

    public void testMultNegate() throws Exception
    {
        input.setLine("{a* -b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b -$ *", output);
    }

    public void testNegateSubt() throws Exception
    {
        input.setLine("{-a-b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a -$ b -", output);
    }

    public void testSubtNegate() throws Exception
    {
        input.setLine("{a- -b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b -$ -", output);
    }

    public void testNegateAssign() throws Exception
    {
        input.setLine("{-a=b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a -$ b =", output);
    }

    public void testPrefix() throws Exception
    {
        input.setLine("{- ~ a + ! ~ b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a ~$ -$ b ~$ !$ +", output);
    }

    public void testAssignNegate() throws Exception
    {
        input.setLine("{a= -b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b -$ =", output);
    }

    public void testIncrement() throws Exception
    {
        input.setLine("{++a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a ++$", output);
    }

    public void testDecrement() throws Exception
    {
        input.setLine("{--a}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a --$", output);
    }

    public void testIncAddDec() throws Exception
    {
        input.setLine("{++a+ --b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a ++$ b --$ +", output);
    }

    public void testDecMultInc() throws Exception
    {
        input.setLine("{--a* ++b}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a --$ b ++$ *", output);
    }

    public void testPostIncrement() throws Exception
    {
        input.setLine("{a++}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a $++", output);
    }

    public void testPostDecrement() throws Exception
    {
        input.setLine("{a--}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a $--", output);
    }

    public void testPostincAddPostdec() throws Exception
    {
        input.setLine("{a++ +b--}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a $++ b $-- +", output);
    }

    public void testPostdecMultPostinc() throws Exception
    {
        input.setLine("{a-- *b++}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a $-- b $++ *", output);
    }

    public void testNegPostinc() throws Exception
    {
        input.setLine("{-a++}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a $++ -$", output);
    }

    public void testNegPostdec() throws Exception
    {
        input.setLine("{-a--}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a $-- -$", output);
    }

    public void testNew() throws Exception
    {
        input.setLine("{new Abc}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "Abc new$", output);
    }

//    public void testInstanceOf() throws Exception
//    {
//        input.setLine("{a instanceof Abc}");
//        parser.setInput(input);
//        output = parser.parseExpression();
//        assertEquals("Wrong expression", "a Abc instanceof", output);
//    }
//
    public void testBitwise1() throws Exception
    {
        input.setLine("{a << b >> c >>> d}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b << c >> d >>>", output);
    }
    
    public void testBitwise2() throws Exception
    {
        input.setLine("{a >>> b >> c << d}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b >>> c >> d <<", output);
    }
    
    public void testBitwiseAdd() throws Exception
    {
        input.setLine("{a+1 >>> b-2 != c & d}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a 1 + b 2 - >>> c != d &", output);
    }
    
    public void testRelations1() throws Exception
    {
        input.setLine("{a < b > c <= d >= e}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b < c > d <= e >=", output);
    }
    
    public void testRelations2() throws Exception
    {
        input.setLine("{a >= b <= c < d > e}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a b >= c <= d < e >", output);
    }
        
    public void testRelation3() throws Exception
    {
        input.setLine("{a+1 >= b*2 <= c<<2 < d>>>3 > e>>1}");
        parser.setInput(input);
        output = parser.parseExpression();
        assertEquals("Wrong expression", "a 1 + b 2 * >= c 2 << <= d 3 >>> < e 1 >> >", output);
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
