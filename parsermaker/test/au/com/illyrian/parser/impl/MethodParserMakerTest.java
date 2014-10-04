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

public class MethodParserMakerTest extends ClassMakerTestCase
{
    StringWriter writer;
    PrintWriter  out;
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    
    PrecidenceParser createPrecidenceParser()
    {
        PrecidenceParser parser = new JavaOperatorPrecedenceParser();
        parser.addLedOperator("(", ")", ExpressionAction.CALL, 17, Operator.PARAMS, true);
        parser.addLedOperator(".", ExpressionAction.DOT, 16, Operator.BINARY, true);
        parser.addLedOperator("[", "]", ExpressionAction.DOT, 16, Operator.BRACKET, true);
        parser.addNudOperator("-", ExpressionAction.NEG, 15, Operator.PREFIX, true);
        parser.addNudOperator("(", ")", ExpressionAction.CAST, 14, Operator.BRACKET, true);
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
    
    static public class Other {
        public static int id = 0;
        public int a = 0;
        public int b = 0;
        public int c = 0;
        public int getA() {return a;}
        public int getB() {return b;}
        public int getC() {return c;}
        public void setA(int value) {a = value;}
        public void setB(int value) {b = value;}
        public void setC(int value) {c = value;}
        public void set(int a, int b, int c) 
        {
            setA(a);
            setB(b);
            setC(c);
        }
        static public void setId(int value) {id = value;}
        static public int getId() {return id;}
    }

    ClassMaker methodFuncA(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Extends(FuncA.class);
        maker.Declare("id", int.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("const", int.class, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC);
        maker.Declare("self", maker.getClassType(), ClassMaker.ACC_PUBLIC);
        maker.Declare("other", Other.class, ClassMaker.ACC_PUBLIC);
        maker.Method("f", int.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", int.class, 0);
        maker.Begin();
        return maker;
    }
    
    ClassMaker methodFuncABC(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Extends(FuncABC.class);
        maker.Declare("self", maker.getClassType(), ClassMaker.ACC_PUBLIC);
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

    public void testGetZ() throws Exception
    {
        out.println("getZ()");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 0, func.f(0));
        setIntField(parserClass, instance, "z", 3);
        assertEquals("Wrong result", 3, func.f(0));
    }

    public void testSelfGetZ() throws Exception
    {
        out.println("self.getZ()");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        setField(parserClass, instance, "self", instance);
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 0, func.f(0));
        setIntField(parserClass, instance, "z", 3);
        assertEquals("Wrong result", 3, func.f(0));
    }

    public void testSetZ() throws Exception
    {
        out.println("setZ(a)");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 1, func.f(1));
        assertEquals("Wrong z value", 1, getIntField(parserClass, instance, "z"));
        assertEquals("Wrong result", 3, func.f(3));
        assertEquals("Wrong z value", 3, getIntField(parserClass, instance, "z"));
    }

    public void testSetABC() throws Exception
    {
        out.println("set(a, b, c)");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncABC(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncABC func = (FuncABC)instance;
        assertEquals("Wrong result", 6, func.f(1,2,3));
        assertEquals("Wrong valueA", 1, getIntField(parserClass, instance, "valueA"));
        assertEquals("Wrong valueB", 2, getIntField(parserClass, instance, "valueB"));
        assertEquals("Wrong valueC", 3, getIntField(parserClass, instance, "valueC"));
        assertEquals("Wrong result", 52, func.f(100,2,-50));
        assertEquals("Wrong valueA", 100, getIntField(parserClass, instance, "valueA"));
        assertEquals("Wrong valueB", 2, getIntField(parserClass, instance, "valueB"));
        assertEquals("Wrong valueC", -50, getIntField(parserClass, instance, "valueC"));
    }

    public void testSelfSetABC() throws Exception
    {
        out.println("self.set(a, b, c)");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncABC(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        setField(parserClass, instance, "self", instance);
        FuncABC func = (FuncABC)instance;
        assertEquals("Wrong result", 6, func.f(1,2,3));
        assertEquals("Wrong valueA", 1, getIntField(parserClass, instance, "valueA"));
        assertEquals("Wrong valueB", 2, getIntField(parserClass, instance, "valueB"));
        assertEquals("Wrong valueC", 3, getIntField(parserClass, instance, "valueC"));
        assertEquals("Wrong result", 52, func.f(100,2,-50));
        assertEquals("Wrong valueA", 100, getIntField(parserClass, instance, "valueA"));
        assertEquals("Wrong valueB", 2, getIntField(parserClass, instance, "valueB"));
        assertEquals("Wrong valueC", -50, getIntField(parserClass, instance, "valueC"));
    }

    public void testOtherA() throws Exception
    {
        out.println("other.a");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Other other = new Other();
        setField(parserClass, instance, "other", other);
        setField(parserClass, instance, "self", instance);
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 0, func.f(0));
        other.a = 3;
        setIntField(parserClass, instance, "z", 3);
        assertEquals("Wrong result", 3, func.f(0));
    }

    public void testOtherGetA() throws Exception
    {
        out.println("other.getA()");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Other other = new Other();
        setField(parserClass, instance, "other", other);
//        setField(parserClass, instance, "self", instance);
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 0, func.f(0));
        other.a = 3;
        setIntField(parserClass, instance, "z", 3);
        assertEquals("Wrong result", 3, func.f(0));
    }
/*
    public void testMakerOtherGetA() throws Exception
    {
        System.out.println("testMakerOtherGetA()");
        
        PrecidenceParser parser = createPrecidenceParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = maker.Call(maker.Get("other"), "getA", maker.Push());
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Other other = new Other();
        setField(parserClass, instance, "other", other);
        setField(parserClass, instance, "self", instance);
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 0, func.f(0));
        other.a = 3;
        setIntField(parserClass, instance, "z", 3);
        assertEquals("Wrong result", 3, func.f(0));
    }
*/
}
