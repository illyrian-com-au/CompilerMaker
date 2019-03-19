package au.com.illyrian.expressionparser;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.parser.maker.ModuleContextMaker;

public class ExpressionModuleMakerTest extends TestCase
{
    StringWriter writer;
    PrintWriter  out;
    ClassMakerFactory factory = new ClassMakerFactory();
    
    public void setUp()
    {
        writer = new StringWriter() ;
        out = new PrintWriter(writer);
    }

    public void testDomainParserDefault() throws Exception
    {
        out.println("import au.com.illyrian.expressionparser.ExpressionParser;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest {}");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "ExprTest", maker.getSimpleClassName());
        assertEquals("package name", "", maker.getPackageName());
        assertEquals("full class name", "ExprTest", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "ExprTest", instance.getClass().getCanonicalName());
    }

    public void testDomainParserPackage() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest {}");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "ExprTest", maker.getSimpleClassName());
        assertEquals("package name", "au.com.illyrian.expressionparser", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
    }

    public void testDomainParserOtherPackage() throws Exception
    {
        out.println("package au.com.illyrian.test;");
        out.println("import au.com.illyrian.expressionparser.ExpressionParser;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest {}");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "ExprTest", maker.getSimpleClassName());
        assertEquals("package name", "au.com.illyrian.test", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.test.ExprTest", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.test.ExprTest", instance.getClass().getCanonicalName());
    }

    public void testDomainParserFunction() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncVoid;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncVoid");
        out.println("    {");
        out.println("        f() = 1;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "ExprTest", maker.getSimpleClassName());
        assertEquals("package name", "au.com.illyrian.expressionparser", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncVoid func = new FuncVoid();
        assertEquals("f()", 0, func.f());

        Class<FuncVoid> parserClass = maker.defineClass();
        FuncVoid instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("Generated class does not override f()", 1, instance.f());
    }

    public void testExtendsFuncVoid() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncVoid;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncVoid");
        out.println("    {");
        out.println("        f() = 1;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncVoid func = new FuncVoid();
        assertEquals("f()", 0, func.f());

        Class<FuncVoid> parserClass = maker.defineClass();
        FuncVoid instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());        func = (FuncVoid)instance;
        assertEquals("Generated class does not override f()", 1, instance.f());
    }

    public void testExtendsFuncA() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncA;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncA");
        out.println("    {");
        out.println("        f(a) = a + 1;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncA func = new FuncA();
        assertEquals("f(a)", 0, func.f(1));

        Class<FuncA> parserClass = maker.defineClass();
        FuncA instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("f(a)", 2, instance.f(1));
        assertEquals("f(a)", 6, instance.f(5));
        assertEquals("f(a)", -9, instance.f(-10));
    }

    public void testExtendsFuncABMultiply() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncAB;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncAB");
        out.println("    {");
        out.println("        f(a,b) = a * b;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncAB func = new FuncAB();
        assertEquals("f(a,b)", 0, func.f(1, 1));

        Class<FuncAB> parserClass = maker.defineClass();
        FuncAB instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("f(2,3)", 6, instance.f(2, 3));
        assertEquals("f(4,-5)", -20, instance.f(4, -5));
        assertEquals("f(-2,-4)", 8, instance.f(-2,-4));
    }

    public void testExtendsFuncABAdd() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncAB;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncAB");
        out.println("    {");
        out.println("        f(a,b) = a + b;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncAB func = new FuncAB();
        assertEquals("f(a,b)", 0, func.f(1, 1));

        Class<FuncAB> parserClass = maker.defineClass();
        FuncAB instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("f(2,3)", 5, instance.f(2, 3));
        assertEquals("f(4,-5)", -1, instance.f(4, -5));
        assertEquals("f(-2,-4)", -6, instance.f(-2,-4));
    }

    public void testExtendsFuncABSubtract() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncAB;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncAB");
        out.println("    {");
        out.println("        f(a,b) = a - b;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncAB func = new FuncAB();
        assertEquals("f(a,b)", 0, func.f(1, 1));

        Class<FuncAB> parserClass = maker.defineClass();
        FuncAB instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("f(2,3)", -1, instance.f(2, 3));
        assertEquals("f(4,-5)", 9, instance.f(4, -5));
        assertEquals("f(-2,-4)", 2, instance.f(-2,-4));
    }

    public void testExtendsFuncABDivide() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncAB;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncAB");
        out.println("    {");
        out.println("        f(a,b) = a / b;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncAB func = new FuncAB();
        assertEquals("f(a,b)", 0, func.f(1, 1));

        Class<FuncAB> parserClass = maker.defineClass();
        FuncAB instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("f(6,3)", 2, instance.f(6, 3));
        assertEquals("f(-20,-5)", 4, instance.f(-20, -5));
        assertEquals("f(8,-4)", -2, instance.f(8,-4));
    }

    public void testExtendsFuncABCSubtract() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncABC;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncABC");
        out.println("    {");
        out.println("        f(a,b,c) = a - b - c;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncABC func = new FuncABC();
        assertEquals("f(a,b,c)", 3, func.f(1, 1, 1));

        Class<FuncABC> parserClass = maker.defineClass();
        FuncABC instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("f(2,3, 4)", -5, instance.f(2, 3, 4));
        assertEquals("f(4,-5, -2)", 11, instance.f(4, -5, -2));
        assertEquals("f(-2,-4, -2)", 4, instance.f(-2,-4, -2));
    }

    public void testExtendsFuncABCDivide() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncABC;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncABC");
        out.println("    {");
        out.println("        f(a,b,c) = a / b / c;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncABC func = new FuncABC();
        assertEquals("f(a,b)", 3, func.f(1, 1, 1));

        Class<FuncABC> parserClass = maker.defineClass();
        FuncABC instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("f(18,3,2)", 3, instance.f(18, 3, 2));
        assertEquals("f(-40,-5, 2)", 4, instance.f(-40, -5, 2));
        assertEquals("f(36,-3,4)", -3, instance.f(36,-3, 4));
    }

    public void testExtendsFuncABCComplex() throws Exception
    {
        out.println("package au.com.illyrian.expressionparser;");
        out.println("import au.com.illyrian.expressionparser.FuncABC;");
        out.println("ExpressionParser::{");
        out.println("    class ExprTest extends FuncABC");
        out.println("    {");
        out.println("        f(a,b,c) = a + b * c - a;");
        out.println("    }");
        out.println("}::ExpressionParser");
        
        ClassMaker maker = factory.createClassMaker();
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInputString(writer.toString(), null);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("full class name", "au.com.illyrian.expressionparser.ExprTest", maker.getFullyQualifiedClassName());
        
        FuncABC func = new FuncABC();
        assertEquals("f(a,b)", 3, func.f(1, 1, 1));

        Class<FuncABC> parserClass = maker.defineClass();
        FuncABC instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.expressionparser.ExprTest", instance.getClass().getCanonicalName());
        assertEquals("f(1,1,1)", 2, instance.f(1, 1, 2));
        assertEquals("f(-4,6, 2)", 12, instance.f(-4, 6, 2));
        assertEquals("f(3,4,2)", 8, instance.f(3,4,2));
    }

}
