package au.com.illyrian.parser.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.expressionparser.ExpressionAction;
import au.com.illyrian.expressionparser.FuncA;
import au.com.illyrian.expressionparser.FuncABC;
import au.com.illyrian.parser.CompileUnit;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Operator;
import au.com.illyrian.parser.maker.CompileModuleMaker;
import au.com.illyrian.parser.maker.ExpressionActionMaker;

public class CastingParserMakerTest extends ClassMakerTestCase
{
    StringWriter writer;
    PrintWriter  out;
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    
    PrecidenceParser createPrecidenceParser()
    {
        PrecidenceParser parser = new JavaOperatorPrecedenceParser();
        parser.addLedOperator(".", ExpressionAction.DOT, 16, Operator.BINARY, true);
        parser.addLedOperator("(", ")", ExpressionAction.DOT, 16, Operator.BRACKET, true);
        parser.addLedOperator("[", "]", ExpressionAction.DOT, 16, Operator.BRACKET, true);
        parser.addNudOperator("-", ExpressionAction.NEG, 15, Operator.PREFIX, true);
        parser.addNudOperator("(", ")", ExpressionAction.DOT, 14, Operator.BRACKET, true);
        parser.addLedOperator("+", ExpressionAction.ADD, 12, Operator.BINARY, true);
        parser.addLedOperator("-", ExpressionAction.SUBT, 12, Operator.BINARY, true);

        ExpressionActionMaker actions = new ExpressionActionMaker();
        parser.setPrecidenceActions(actions);
        return parser;
    }

    public void setUp()
    {
        writer = new StringWriter() ;
        out = new PrintWriter(writer);
    }

    public StringReader getReader()
    {
        return new StringReader(writer.toString());
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

    CompileUnit createCompileModule(PrecidenceParser parser) throws IOException
    {
        Input input = new LexerInputStream(getReader(), null);
        
        CompileModuleMaker compile = new CompileModuleMaker();
        compile.setInput(input);
        compile.setClassMaker(maker);
        compile.visitParser(parser);
        compile.visitParser(parser.getPrecidenceActions());
        return compile;
    }

    public void testCastingParser1() throws Exception
    {
        out.println("(byte)a");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 6, func.f(6));
    }

    public void testCastingParser2() throws Exception
    {
        out.println("(byte)(a + (char)2)");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 8, func.f(6));
    }

    public void testCastingParser3() throws Exception
    {
        out.println("(short)id");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
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
        out.println("(short)other.id");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
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
        out.println("(byte)(short)(int)(long)3");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 3, func.f(0));
    }

}
