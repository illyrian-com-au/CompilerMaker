package au.com.illyrian.domainparser;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.expressionparser.FuncA;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.maker.ModuleContextMaker;

import junit.framework.TestCase;

public class ClassParserMakerTest extends TestCase
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

    public void testClassDefault() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("class test");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::TestTokenParser");
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "test", maker.getSimpleClassName());
        assertEquals("simple class name", "", maker.getPackageName());
        assertEquals("full class name", "test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "test", instance.getClass().getCanonicalName());
    }

    public void testClassPackage() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("class Test");
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "Test", maker.getSimpleClassName());
        assertEquals("package name", "au.com.illyrian.domainparser", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.domainparser.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.domainparser.Test", instance.getClass().getCanonicalName());
    }

    public void testClassExtends() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("import au.com.illyrian.expressionparser.FuncA;");
        out.println("class Test extends FuncA");
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "Test", maker.getSimpleClassName());
        assertEquals("package name", "au.com.illyrian.domainparser", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.domainparser.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.domainparser.Test", instance.getClass().getCanonicalName());
        assertTrue("Does not extend class FuncA", instance instanceof FuncA);
    }

    public void testClassImplements() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("import au.com.illyrian.expressionparser.IfaceA;");
        out.println("import au.com.illyrian.expressionparser.FuncA;");
        out.println("class Test extends FuncA implements IfaceA");
        out.println("au.com.illyrian.domainparser.TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::au.com.illyrian.domainparser.TestTokenParser");
        Input input = new LexerInputStream(getReader(), null);
        ClassMaker maker = factory.createClassMaker();
        ClassParser parser = new ClassParser();
        
        ModuleContextMaker compile = new ModuleContextMaker();
        compile.setInput(input);
        compile.setModuleParser(parser);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "Test", maker.getSimpleClassName());
        assertEquals("package name", "au.com.illyrian.domainparser", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.domainparser.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.domainparser.Test", instance.getClass().getCanonicalName());
        assertTrue("Does not extend class FuncA", instance instanceof FuncA);
    }

}
