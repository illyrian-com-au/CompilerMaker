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

public class RelativeParserMakerTest extends ClassMakerTestCase
{
    StringWriter writer;
    PrintWriter  out;
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    PrecidenceParser createIncrementParser()
    {
        PrecidenceParser parser = new PrecidenceParser();
        parser.addInfixOperator(".", ExpressionAction.DOT, 16, Operator.BINARY, true);
        parser.addPostfixOperator("--", ExpressionAction.POSTDEC, 15, Operator.POSTFIX);
        parser.addPostfixOperator("++", ExpressionAction.POSTINC, 15, Operator.POSTFIX);
        parser.addPrefixOperator("-", ExpressionAction.NEG, 14, Operator.PREFIX);
        parser.addPrefixOperator("--", ExpressionAction.DEC, 14, Operator.PREFIX);
        parser.addPrefixOperator("++", ExpressionAction.INC, 14, Operator.PREFIX);
        parser.addInfixOperator("*", ExpressionAction.MULT, 12, Operator.BINARY, true);
        parser.addInfixOperator("/", ExpressionAction.DIV, 12, Operator.BINARY, true);
        parser.addInfixOperator("%", ExpressionAction.REM, 12, Operator.BINARY, true);
        parser.addInfixOperator("+", ExpressionAction.ADD, 11, Operator.BINARY, true);
        parser.addInfixOperator("-", ExpressionAction.SUBT, 11, Operator.BINARY, true);
        parser.addInfixOperator("=", ExpressionAction.ASSIGN, 1, Operator.BINARY, false);
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

    public void testGetLocal1() throws Exception
    {
        out.println("a");
        
        PrecidenceParser parser = createIncrementParser();
        createCompileModule(parser);
        parser.nextToken();
        methodFuncA(maker);
        Object result = parser.expression();
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 1, func.f(1));
    }

}
