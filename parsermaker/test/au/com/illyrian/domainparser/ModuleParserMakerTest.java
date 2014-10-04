package au.com.illyrian.domainparser;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.maker.CompileModuleMaker;

import junit.framework.TestCase;

public class ModuleParserMakerTest extends TestCase
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

    public void testDomainParserDefault() throws Exception
    {
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("import au.com.illyrian.domainparser.ModuleAction;");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::TestTokenParser");
        Input input = new LexerInputStream(getReader(), null);
        
        ClassMaker maker = factory.createClassMaker();
        CompileModuleMaker compile = new CompileModuleMaker();
        compile.setInput(input);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "ClassMaker_$0", maker.getSimpleClassName());
        assertEquals("simple class name", "", maker.getPackageName());
        assertEquals("full class name", "ClassMaker_$0", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "ClassMaker_$0", instance.getClass().getCanonicalName());
    }

    public void testDomainParserPackage() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("import au.com.illyrian.domainparser.TestTokenParser;");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::TestTokenParser");
        Input input = new LexerInputStream(getReader(), null);
        
        ClassMaker maker = factory.createClassMaker();
        CompileModuleMaker compile = new CompileModuleMaker();
        compile.setInput(input);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "ClassMaker_$0", maker.getSimpleClassName());
        assertEquals("simple class name", "au.com.illyrian.domainparser", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.domainparser.ClassMaker_$0", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.domainparser.ClassMaker_$0", instance.getClass().getCanonicalName());
    }

    public void testDomainParserPackageSimple() throws Exception
    {
        out.println("package au.com.illyrian.domainparser;");
        out.println("TestTokenParser::{");
        out.println("   a * b + c;");
        out.println("}::TestTokenParser");
        Input input = new LexerInputStream(getReader(), null);
        
        ClassMaker maker = factory.createClassMaker();
        CompileModuleMaker compile = new CompileModuleMaker();
        compile.setInput(input);
        compile.setClassMaker(maker);
        compile.parseModule();
        
        assertEquals("simple class name", "ClassMaker_$0", maker.getSimpleClassName());
        assertEquals("simple class name", "au.com.illyrian.domainparser", maker.getPackageName());
        assertEquals("full class name", "au.com.illyrian.domainparser.ClassMaker_$0", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.domainparser.ClassMaker_$0", instance.getClass().getCanonicalName());
    }
}
