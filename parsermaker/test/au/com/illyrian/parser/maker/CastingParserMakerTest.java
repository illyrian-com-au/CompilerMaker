package au.com.illyrian.parser.maker;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.types.Value;
import au.com.illyrian.expressionparser.FuncA;
import au.com.illyrian.expressionparser.FuncABC;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.expr.AstExpressionPrecidenceParser;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class CastingParserMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    
    OperatorPrecidenceParser createParser()
    {
        AstExpressionPrecidenceParser parser = new AstExpressionPrecidenceParser();
        AstExpressionFactory factory = new AstExpressionFactory();
        parser.setAstExpressionFactory(factory);
        return parser;
    }

    ClassMakerConstants methodFuncA(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Extends(FuncA.class);
        maker.Declare("id", int.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("const", int.class, ClassMakerConstants.ACC_PUBLIC | ClassMakerConstants.ACC_STATIC);
        maker.Declare("other", maker.getClassType(), ClassMakerConstants.ACC_PUBLIC);
        maker.Method("f", int.class, 0);
        maker.Declare("a", int.class, 0);
        maker.Begin();
        return maker;
    }
    
    ClassMakerConstants methodFuncABC(ClassMaker maker)
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
    
    void endMethod(ClassMaker maker, Value value)
    {
        maker.Return(value);
        maker.End();
    }

    private Value parseExpression(String input)
    {
        Input lexer = new LexerInputString(input);
        OperatorPrecidenceParser parser = createParser();
        parser.setInput(lexer);
        parser.nextToken();
        AstExpression expr = (AstExpression)parser.expression();
    	AstStructureVisitor visitor = new AstStructureVisitor(maker);
        return expr.resolveValue(visitor);
    }

    public void testCastingParser1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("(byte)a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 6, func.f(6));
    }

    public void testCastingParser2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("(byte)(a + (char)2)");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 8, func.f(6));
    }

    public void testCastingParser3() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("(short)id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testCastingParser4() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("(short) other.id");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", instance);
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
    }

    public void testCastingParser5() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("(byte)(short)(int)(long)3");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 3, func.f(0));
    }

}
