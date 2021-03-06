package au.com.illyrian.expressionparser;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.domainparser.ClassParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.maker.ModuleContextMaker;

public class ExpressionClassMakerTest extends TestCase
{
    StringReader reader;
    StringWriter writer;
    PrintWriter  out;
    ClassMakerFactory factory = new ClassMakerFactory();
    
    public void setUp()
    {
        writer = new StringWriter() ;
        out = new PrintWriter(writer);
    }
    
    public StringReader getReader()
    {
        return new StringReader(writer.toString());
    }

    public void testDomainParserFunction() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncVoid;");
        out.println("class ExprTest extends FuncVoid");
        out.println("ExpressionParser::{");
        out.println("        f() = 1;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "ExprTest", maker.getSimpleClassName());
        assertEquals("package name", "au.com.illyrian.expressionparser", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncVoid func = new FuncVoid();
        assertEquals("f()", 0, func.f());

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncVoid)instance;
        assertEquals("Generated class does not override f()", 1, func.f());
    }

    public void testExtendsFuncVoid() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncVoid;");
        out.println("    class ExprTest extends FuncVoid");
        out.println("ExpressionParser::{");
        out.println("        f() = 1;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncVoid func = new FuncVoid();
        assertEquals("f()", 0, func.f());

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncVoid)instance;
        assertEquals("Generated class does not override f()", 1, func.f());
    }

    public void testExtendsFuncA() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncA;");
        out.println("    class ExprTest extends FuncA");
        out.println("ExpressionParser::{");
        out.println("        f(a) = a + 1;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncA func = new FuncA();
        assertEquals("f(a)", 0, func.f(1));

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncA)instance;
        assertEquals("f(a)", 2, func.f(1));
        assertEquals("f(a)", 6, func.f(5));
        assertEquals("f(a)", -9, func.f(-10));
    }

    public void testExtendsFuncABMultiply() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncAB;");
        out.println("    class ExprTest extends FuncAB");
        out.println("ExpressionParser::{");
        out.println("        f(a,b) = a * b;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncAB func = new FuncAB();
        assertEquals("f(a,b)", 0, func.f(1, 1));

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncAB)instance;
        assertEquals("f(2,3)", 6, func.f(2, 3));
        assertEquals("f(4,-5)", -20, func.f(4, -5));
        assertEquals("f(-2,-4)", 8, func.f(-2,-4));
    }

    public void testExtendsFuncABAdd() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncAB;");
        out.println("    class ExprTest extends FuncAB");
        out.println("ExpressionParser::{");
        out.println("        f(a,b) = a + b;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncAB func = new FuncAB();
        assertEquals("f(a,b)", 0, func.f(1, 1));

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncAB)instance;
        assertEquals("f(2,3)", 5, func.f(2, 3));
        assertEquals("f(4,-5)", -1, func.f(4, -5));
        assertEquals("f(-2,-4)", -6, func.f(-2,-4));
    }

    public void testExtendsFuncABSubtract() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncAB;");
        out.println("    class ExprTest extends FuncAB");
        out.println("ExpressionParser::{");
        out.println("        f(a,b) = a - b;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncAB func = new FuncAB();
        assertEquals("f(a,b)", 0, func.f(1, 1));

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncAB)instance;
        assertEquals("f(2,3)", -1, func.f(2, 3));
        assertEquals("f(4,-5)", 9, func.f(4, -5));
        assertEquals("f(-2,-4)", 2, func.f(-2,-4));
    }

    public void testExtendsFuncABDivide() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncAB;");
        out.println("    class ExprTest extends FuncAB");
        out.println("ExpressionParser::{");
        out.println("        f(a,b) = a / b;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncAB func = new FuncAB();
        assertEquals("f(a,b)", 0, func.f(1, 1));

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncAB)instance;
        assertEquals("f(6,3)", 2, func.f(6, 3));
        assertEquals("f(-20,-5)", 4, func.f(-20, -5));
        assertEquals("f(8,-4)", -2, func.f(8,-4));
    }

    public void testExtendsFuncABCSubtract() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncABC;");
        out.println("    class ExprTest extends FuncABC");
        out.println("ExpressionParser::{");
        out.println("        f(a,b,c) = a - b - c;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncABC func = new FuncABC();
        assertEquals("f(a,b,c)", 3, func.f(1, 1, 1));

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncABC)instance;
        assertEquals("f(2,3, 4)", -5, func.f(2, 3, 4));
        assertEquals("f(4,-5, -2)", 11, func.f(4, -5, -2));
        assertEquals("f(-2,-4, -2)", 4, func.f(-2,-4, -2));
    }

    public void testExtendsFuncABCDivide() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncABC;");
        out.println("    class ExprTest extends FuncABC");
        out.println("ExpressionParser::{");
        out.println("        f(a,b,c) = a / b / c;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncABC func = new FuncABC();
        assertEquals("f(a,b)", 3, func.f(1, 1, 1));

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncABC)instance;
        assertEquals("f(18,3,2)", 3, func.f(18, 3, 2));
        assertEquals("f(-40,-5, 2)", 4, func.f(-40, -5, 2));
        assertEquals("f(36,-3,4)", -3, func.f(36,-3, 4));
    }

    public void testExtendsFuncABCComplex() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncABC;");
        out.println("    class ExprTest extends FuncABC");
        out.println("ExpressionParser::{");
        out.println("        f(a,b,c) = a + b * c - a;");
        out.println("}::ExpressionParser");
        
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncABC func = new FuncABC();
        assertEquals("f(a,b)", 3, func.f(1, 1, 1));

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        func = (FuncABC)instance;
        assertEquals("f(1,1,1)", 2, func.f(1, 1, 2));
        assertEquals("f(-4,6, 2)", 12, func.f(-4, 6, 2));
        assertEquals("f(3,4,2)", 8, func.f(3,4,2));
    }
}
