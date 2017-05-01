package au.com.illyrian.bnf.maker;

import java.io.File;

import junit.framework.TestCase;
import au.com.illyrian.bnf.BnfParser;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.jesub.ast.AstClass;
import au.com.illyrian.jesub.ast.AstImport;
import au.com.illyrian.jesub.ast.AstModifiers;
import au.com.illyrian.jesub.ast.AstPackage;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

public class ModuleParserTest extends TestCase
{
    private static final String TEST_DIR = "test/";
    
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    BnfMakerVisitor visitor = new BnfMakerVisitor(maker);

    public ParseMembers loadParser() throws Exception 
    {
        String packageName = "test"; 
        String className = "ModuleParser"; 
        String source = "test/ModuleParser.bnf";
        File file = new File(TEST_DIR, source);
        assertTrue("Cannot find " + file.getAbsolutePath(), file.exists());
        
        ModuleContext compile = new ModuleContext();
        compile.setInputFile(file, source);
        
        BnfParser parserTree = new BnfParser();
        visitor.setActionRequired(true);
        visitor.setDefaultTypeName("AstExpression");
        visitor.setFilename(source);
        maker.setSimpleClassName(className);
        maker.setPackageName(packageName);
        importClasses();
        maker.Extends(BnfParserBase.class);
        BnfTreeParser tree = parserTree.parseMembers(compile);
        assertEquals("token", TokenType.END, parserTree.getLexer().nextToken());
        assertNotNull("Nothing returned from parser", tree);
        
        factory.setPass(ClassMaker.FIRST_PASS);
        tree.resolveDeclaration(visitor);
        maker.EndClass();
        
        factory.setPass(ClassMaker.SECOND_PASS);
        tree.resolveDeclaration(visitor);
        maker.EndClass();
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertTrue("Generated class does not implement ParseMembers interface",
                instance instanceof ParseMembers);
        ParseMembers parser = (ParseMembers)instance;
        return parser;
    }
    
    public void importClasses() {
        maker.Import(AstExpression.class);
        maker.Import(AstStructure.class);
        maker.Import(AstModifiers.class);
        maker.Import(AstPackage.class);
        maker.Import(AstImport.class);
        maker.Import(AstClass.class);
        maker.Import(TerminalName.class);
    }

    public void testParsePackage() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("class test { }");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "class test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseImports() throws Exception 
    {
        ParseMembers parser = loadParser();

        StringReadWriter out = new StringReadWriter();
        out.println("import test.MyClass;");
        out.println("import au.com.test.OtherClass;");
        out.println("class test {}");
        out.close();
        Input input = new LexerInputStream(out.getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        String expected = "import test.MyClass;\nimport au.com.test.OtherClass;\nclass test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseSimpleClass() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("class test {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "class test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassModifiers() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString(
                "public protected private static abstract final class test {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "public protected private static abstract final class test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassImplements() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("static class test implements Runnable {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "static class test implements Runnable\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassImplementsMultiple() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString(
                "static class test implements Runnable, Throwable, Serializable {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "static class test implements Runnable, Throwable, Serializable\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassExtends() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("protected class test extends Base {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "protected class test extends Base\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseSimpleInterface() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("public interface test {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "public interface test\n";
        assertEquals(expected, tree.toString());
     }

    public void testParseClassException() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("test {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
            fail("ParserException expected");
        } catch(ParserException ex) {
            assertEquals("Expected modifiers followed by class or interface", ex.getMessage());
        }
    }

    public void testParseClassOptionsException() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("class test depends {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
            fail("ParserException expected");
        } catch(ParserException ex) {
            assertEquals("A class name should be followed by extends or implements and a class body", ex.getMessage());
        }
    }
    
    public void testParseClassExtendsException() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("class test extends {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
            fail("ParserException expected");
        } catch(ParserException ex) {
            assertEquals("A class name is expected after extends", ex.getMessage());
        }
    }
    
    public void testParseClassImplementsException() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("class test implements {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
            fail("ParserException expected");
        } catch(ParserException ex) {
            assertEquals("One or more class names is expected after implements", ex.getMessage());
        }
    }
    
    public void testParseInterfaceOptionsException() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("interface test depends {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
            fail("ParserException expected");
        } catch(ParserException ex) {
            assertEquals("An interface name should be followed by extends and an interface body", ex.getMessage());
        }
    }
    
    public void testParseInterfaceExtendsException() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("interface test extends {}");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
            fail("ParserException expected");
        } catch(ParserException ex) {
            assertEquals("A class name is expected after extends", ex.getMessage());
        }
    }
}
