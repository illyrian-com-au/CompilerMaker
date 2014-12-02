package au.com.illyrian.parser.maker;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.expressionparser.ExpressionAction;
import au.com.illyrian.expressionparser.FuncA;
import au.com.illyrian.expressionparser.FuncABC;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.Operator;
import au.com.illyrian.parser.impl.PrecidenceParser;
import au.com.illyrian.parser.maker.PrecidenceActionFactory;

public class DereferenceParserMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    PrecidenceParser createParser()
    {
        PrecidenceParser parser = new PrecidenceParser();
        parser.addInfixOperator(".", ExpressionAction.DOT, 16, Operator.BINARY, true);
        parser.addPrefixOperator("-", ExpressionAction.NEG, 14, Operator.PREFIX);
        parser.addInfixOperator("*", ExpressionAction.MULT, 12, Operator.BINARY, true);
        parser.addInfixOperator("/", ExpressionAction.DIV, 12, Operator.BINARY, true);
        parser.addInfixOperator("%", ExpressionAction.REM, 12, Operator.BINARY, true);
        parser.addInfixOperator("+", ExpressionAction.ADD, 11, Operator.BINARY, true);
        parser.addInfixOperator("-", ExpressionAction.SUBT, 11, Operator.BINARY, true);
        parser.addInfixOperator("=", ExpressionAction.ASSIGN, 1, Operator.BINARY, false);
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
        maker.Declare("const", int.class, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
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
        PrecidenceParser parser = createParser();
        parser.setInput(lexer);
        parser.nextToken();
        AstExpression expr = (AstExpression)parser.expression();
    	AstStructureVisitor visitor = new AstStructureVisitor(maker);
        return expr.resolveType(visitor);
    }

    public void testGetValue1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 6, func.f(6));
    }

    public void testGetValue2() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testGetField1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("other.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testGetField2() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("(other).id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testGetField3() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("other.other.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 5);
        assertEquals("Wrong result", 5, func.f(0));
    }

    public void testGetField4() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("10 + -other.id + a");
        endMethod(maker, result);
//        out.println("10 + -other.id + a");
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 5);
        assertEquals("Wrong result", 7, func.f(2));
    }

    public void testGetStatic1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("Test.const");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, null, "const", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testGetStatic2() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("au.com.illyrian.parser.impl.Test.const");
        endMethod(maker, result);
//        out.println("au.com.illyrian.parser.impl.Test.const");
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, null, "const", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testAssignField1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("other.id = 1");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 5);
        assertEquals("Wrong result", 1, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 1, id);
    }

    public void testAssignField2() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("other.id = a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 5);
        assertEquals("Wrong result", 3, func.f(3));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 3, id);
    }

    public void testAssignStatic1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("Test.const = a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, null, "const", 5);
        assertEquals("Wrong result", 3, func.f(3));
        int x = getIntField(parserClass, null, "const");
        assertEquals("Set Static", 3, x);
    }

    public void testAssignStatic2() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("au.com.illyrian.parser.impl.Test.const = a");
        endMethod(maker, result);
//        out.println("au.com.illyrian.parser.impl.Test.const = a");
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, null, "const", 5);
        assertEquals("Wrong result", 3, func.f(3));
        int x = getIntField(parserClass, null, "const");
        assertEquals("Set Static", 3, x);
    }

}
