package au.com.illyrian.parser.maker;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.expressionparser.FuncA;
import au.com.illyrian.expressionparser.FuncABC;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.Operator;
import au.com.illyrian.parser.impl.ParserConstants;
import au.com.illyrian.parser.impl.PrecidenceParser;

public class OperatorParserMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    PrecidenceParser createIncrementParser()
    {
        PrecidenceParser<AstExpression> parser = new PrecidenceParser<AstExpression>();
        parser.addInfixOperator(".", ParserConstants.DOT, 16, Operator.BINARY);
        parser.addPostfixOperator("--", ParserConstants.POSTDEC, 15, Operator.POSTFIX);
        parser.addPostfixOperator("++", ParserConstants.POSTINC, 15, Operator.POSTFIX);
        parser.addPrefixOperator("-", ParserConstants.NEG, 14, Operator.PREFIX);
        parser.addPrefixOperator("--", ParserConstants.DEC, 14, Operator.PREFIX);
        parser.addPrefixOperator("++", ParserConstants.INC, 14, Operator.PREFIX);
        parser.addInfixOperator("*", ParserConstants.MULT, 12, Operator.BINARY);
        parser.addInfixOperator("/", ParserConstants.DIV, 12, Operator.BINARY);
        parser.addInfixOperator("%", ParserConstants.REM, 12, Operator.BINARY);
        parser.addInfixOperator("+", ParserConstants.ADD, 11, Operator.BINARY);
        parser.addInfixOperator("-", ParserConstants.SUBT, 11, Operator.BINARY);
        parser.addInfixOperator("=", ParserConstants.ASSIGN, 1, Operator.BINARYRIGHT);
        PrecidenceActionFactory actions = new PrecidenceActionFactory();
        parser.setPrecidenceActions(actions);
        return parser;
    }

    ClassMaker methodFuncA(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Extends(FuncA.class);
        maker.Declare("id", int.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("other", maker.getClassType(), ClassMaker.ACC_PUBLIC);
        maker.Method("f", int.class, 0);
        maker.Declare("a", int.class, 0);
        maker.Begin();
        return maker;
    }
    
    ClassMaker methodFuncABC(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Extends(FuncABC.class);
        maker.Method("f", int.class, 0);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Declare("c", int.class, 0);
        maker.Begin();
        return maker;
    }
    
    void endMethod(ClassMaker maker, Object value)
    {
        maker.Return((Type)value);
        maker.End();
    }

    private Type parseExpression(String input) throws ParserException
    {
        Input lexer = new LexerInputString(input);
        PrecidenceParser parser = createIncrementParser();
        parser.setInput(lexer);
        parser.nextToken();
        AstExpression expr = (AstExpression)parser.expression();
    	AstStructureVisitor visitor = new AstStructureVisitor(maker);
        return expr.resolveType(visitor);
    }

    public void testGetLocal() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 1, func.f(1));
    }

    public void testGetMember() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testGetOther() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("other.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, instance, "id", 3);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testGetThis() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("this.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testAdd1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a + 1");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 2, func.f(1));
    }

    public void testSubt1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a-1");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 4, func.f(5));
    }
    
    public void testMult1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a * 3");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 15, func.f(5));
    }

    public void testDiv1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a / 2");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 2, func.f(5));
    }

    public void testRem1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a % 5");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 0, func.f(10));
        assertEquals("Wrong result", 1, func.f(11));
        assertEquals("Wrong result", 2, func.f(12));
        assertEquals("Wrong result", 3, func.f(13));
        assertEquals("Wrong result", 4, func.f(14));
        assertEquals("Wrong result", 0, func.f(15));
    }

    public void testMultDiv3() throws Exception
    {
        methodFuncABC(maker);
        Type result = parseExpression("a * b / c");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncABC func = (FuncABC)instance;
        assertEquals("Wrong result", 5, func.f(2, 5, 2));
        assertEquals("Wrong result", 7, func.f(3, 5, 2));
        assertEquals("Wrong result", -7, func.f(-3, 5, 2));
        assertEquals("Wrong result", -7, func.f(3, -5, 2));
        assertEquals("Wrong result", -7, func.f(3, 5, -2));
        assertEquals("Wrong result", 7, func.f(-3, -5, 2));
        assertEquals("Wrong result", 7, func.f(3, -5, -2));
        assertEquals("Wrong result", 7, func.f(-3, 5, -2));
        assertEquals("Wrong result", -7, func.f(-3, -5, -2));
    }

    
    public void testNeg() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("-a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 0, func.f(0));
        assertEquals("Wrong result", -1, func.f(1));
        assertEquals("Wrong result", 2, func.f(-2));
    }

    public void testDecNeg() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a - - 5");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 5, func.f(0));
        assertEquals("Wrong result", 6, func.f(1));
        assertEquals("Wrong result", 3, func.f(-2));
    }

    public void testNegDec() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("-a - 5");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", -5, func.f(0));
        assertEquals("Wrong result", -6, func.f(1));
        assertEquals("Wrong result", -3, func.f(-2));
    }

    public void testAssignLocal() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a = 5");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 5, func.f(10));
    }

    public void testAssignMemberLocal() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("id = a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 10, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 10, id);
    }

    public void testAssignThisLocal() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("this.id = a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 10, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 10, id);
    }

    public void testAssignOther() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("other.id = a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, instance, "id", 3);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 10, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Wrong instance value", 3, id);
        id = getIntField(parserClass, other, "id");
        assertEquals("Wrong other value", 10, id);
    }

    public void testAssignLocalMember() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a = id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 7, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 7, id);
    }

    public void testAssignLocalThis() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a = this.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 7, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 7, id);
    }

    public void testIncLocal() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("++a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 11, func.f(10));
    }
    
    public void testIncMember() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("++id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 8, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 8, id);
    }
    
    public void testIncThis() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("++this.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 8, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 8, id);
    }
    
    public void testIncOther() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("++other . id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, instance, "id", 3);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 7, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Wrong instance value", 3, id);
        id = getIntField(parserClass, other, "id");
        assertEquals("Wrong other value", 7, id);
    }

    public void testDecLocal() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("--a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 9, func.f(10));
    }

    public void testDecMember() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("-- id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 6, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 6, id);
    }
    
    public void testDecThis() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("--this.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 6, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 6, id);
    }
    
    public void testDecOther() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("--other.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, instance, "id", 3);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 5, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Wrong instance value", 3, id);
        id = getIntField(parserClass, other, "id");
        assertEquals("Wrong other value", 5, id);
    }

    public void testPostIncLocal() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a++");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 10, func.f(10));
    }
    
    public void testPostIncMember() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("id++");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 7, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 8, id);
    }
    
    public void testPostIncThis() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("this.id ++");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 7, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 8, id);
    }
    
    public void testPostIncOther() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("other.id++");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, instance, "id", 3);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 6, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Wrong instance value", 3, id);
        id = getIntField(parserClass, other, "id");
        assertEquals("Wrong other value", 7, id);
    }

    public void testPostDecLocal() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a--");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 10, func.f(10));
    }

    public void testPostDecMember() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("id--");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 7, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 6, id);
    }
    
    public void testPostDecThis() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("this.id--");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 7);
        assertEquals("Wrong result", 7, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 6, id);
    }
    
    public void testPostDecOther() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("other.id--");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, instance, "id", 3);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 6, func.f(10));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Wrong instance value", 3, id);
        id = getIntField(parserClass, other, "id");
        assertEquals("Wrong other value", 5, id);
    }

}
