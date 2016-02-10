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

public class CastingParserMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    
    PrecidenceParser createParser()
    {
        PrecidenceParser<AstExpression> parser = new PrecidenceParser<AstExpression>();
        parser.addInfixOperator(".", ParserConstants.DOT, 16, Operator.BINARY);
        parser.addPostfixOperator("(", ")", ParserConstants.DOT, 16, Operator.BRACKET);
        parser.addPostfixOperator("[", "]", ParserConstants.DOT, 16, Operator.BRACKET);
        parser.addPrefixOperator("-", ParserConstants.NEG, 15, Operator.PREFIX);
        parser.addPrefixOperator("(", ")", ParserConstants.DOT, 14, Operator.BRACKET);
        parser.addInfixOperator("+", ParserConstants.ADD, 12, Operator.BINARY);
        parser.addInfixOperator("-", ParserConstants.SUBT, 12, Operator.BINARY);

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

    public void testCastingParser1() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("(byte)a");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 6, func.f(6));
    }

    public void testCastingParser2() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("(byte)(a + (char)2)");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 8, func.f(6));
    }

    public void testCastingParser3() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("(short)id");
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
        Type result = parseExpression("(short) other.id");
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
        Type result = parseExpression("(byte)(short)(int)(long)3");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 3, func.f(0));
    }

}
