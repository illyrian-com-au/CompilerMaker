package au.com.illyrian.parser.maker;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.expressionparser.FuncA;
import au.com.illyrian.expressionparser.FuncABC;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.CompileUnit;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.JavaOperatorPrecedenceParser;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.Operator;
import au.com.illyrian.parser.impl.ParserConstants;
import au.com.illyrian.parser.impl.PrecidenceParser;

public class MethodParserMakerTest extends ClassMakerTestCase
{
    StringWriter writer;
    PrintWriter  out;
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    
    PrecidenceParser createParser()
    {
        PrecidenceParser<AstExpression> parser = new JavaOperatorPrecedenceParser<AstExpression>();
        parser.addPostfixOperator("(", ")", ParserConstants.CALL, 17, Operator.PARAMS);
        parser.addInfixOperator(".", ParserConstants.DOT, 16, Operator.BINARY);
        parser.addPostfixOperator("[", "]", ParserConstants.DOT, 16, Operator.BRACKET);
        parser.addPrefixOperator("-", ParserConstants.NEG, 15, Operator.PREFIX);
        parser.addPrefixOperator("(", ")", ParserConstants.CAST, 14, Operator.BRACKET);
        parser.addInfixOperator("+", ParserConstants.ADD, 12, Operator.BINARY);
        parser.addInfixOperator("-", ParserConstants.SUBT, 12, Operator.BINARY);
//        parser.addLedOperator("(", ")", ParserConstants.CALL, 17, Operator.PARAMS, true);
//        parser.addLedOperator(".", ParserConstants.DOT, 16, Operator.BINARY);
//        parser.addLedOperator("[", "]", ParserConstants.DOT, 16, Operator.BRACKET, true);
//        parser.addNudOperator("-", ParserConstants.NEG, 15, Operator.PREFIX, true);
//        parser.addNudOperator("(", ")", ParserConstants.CAST, 14, Operator.BRACKET, true);
//        parser.addLedOperator("+", ParserConstants.ADD, 12, Operator.BINARY);
//        parser.addLedOperator("-", ParserConstants.SUBT, 12, Operator.BINARY);

        PrecidenceActionFactory actions1 = new PrecidenceActionFactory();
        parser.setPrecidenceActions(actions1);
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

    public void testGetZ() throws Exception
    {
        methodFuncA(maker);
        Type result = parseExpression("getZ()");
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
        methodFuncA(maker);
        Type result = parseExpression("self.getZ()");
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
        methodFuncA(maker);
        Type result = parseExpression("setZ(a)");
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
        methodFuncABC(maker);
        Type result = parseExpression("set(a, b, c)");
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
        methodFuncABC(maker);
        Type result = parseExpression("self.set(a, b, c)");
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
        methodFuncA(maker);
        Type result = parseExpression("other.a");
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
        methodFuncA(maker);
        Type result = parseExpression("other.getA()");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Other other = new Other();
        setField(parserClass, instance, "other", other);
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 0, func.f(0));
        other.a = 3;
        setIntField(parserClass, instance, "z", 3);
        assertEquals("Wrong result", 3, func.f(0));
    }
}
