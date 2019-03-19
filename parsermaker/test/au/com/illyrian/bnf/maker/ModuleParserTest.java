package au.com.illyrian.bnf.maker;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import au.com.illyrian.bnf.BnfCompiler;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

public class ModuleParserTest extends TestCase {
    private static final String TEST_DIR = "test/";

    ClassMakerFactory factory = new ClassMakerFactory();
    ParseMembers<AstStructure> parser;

    public void setUp() throws InstantiationException, IllegalAccessException, IOException {
        ClassMakerFactory factory = new ClassMakerFactory();
        BnfCompiler compiler = new BnfCompiler(new File(TEST_DIR), factory);
        parser = compiler.compile("test/ModuleParser.bnf");
    }

    public void testParsePackage() throws Exception {
        LexerInputString input = new LexerInputString("class test { }");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "class test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseImports() throws Exception {
        StringReadWriter out = new StringReadWriter();
        out.println("import test.MyClass;");
        out.println("import au.com.test.OtherClass;");
        out.println("class test {}");
        out.close();
        Input input = new LexerInputStream(out.getReader(), null);
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        String expected = "import test.MyClass;\nimport au.com.test.OtherClass;\nclass test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseSimpleClass() throws Exception {
        LexerInputString input = new LexerInputString("class test {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "class test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassModifiers() throws Exception {
        LexerInputString input = new LexerInputString("public protected private static abstract final class test {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "public protected private static abstract final class test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassImplements() throws Exception {
        LexerInputString input = new LexerInputString("static class test implements Runnable {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "static class test implements Runnable\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassImplementsMultiple() throws Exception {
        LexerInputString input = new LexerInputString(
                "static class test implements Runnable, Throwable, Serializable {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "static class test implements Runnable, Throwable, Serializable\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassExtends() throws Exception {
        LexerInputString input = new LexerInputString("protected class test extends Base {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "protected class test extends Base\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseSimpleInterface() throws Exception {
        LexerInputString input = new LexerInputString("public interface test {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "public interface test\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseClassException() throws Exception {
        LexerInputString input = new LexerInputString("test {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        try {
            parser.parseMembers(context);
            fail("ParserException expected");
        } catch (ParserException ex) {
            assertEquals("Expected modifiers followed by class or interface", ex.getMessage());
        }
    }

    public void testParseClassOptionsException() throws Exception {
        LexerInputString input = new LexerInputString("class test depends {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        try {
            parser.parseMembers(context);
            fail("ParserException expected");
        } catch (ParserException ex) {
            assertEquals("A class name should be followed by extends or implements and a class body", ex.getMessage());
        }
    }

    public void testParseClassExtendsException() throws Exception {
        LexerInputString input = new LexerInputString("class test extends {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        try {
            parser.parseMembers(context);
            fail("ParserException expected");
        } catch (ParserException ex) {
            assertEquals("A class name is expected after extends", ex.getMessage());
        }
    }

    public void testParseClassImplementsException() throws Exception {
        LexerInputString input = new LexerInputString("class test implements {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        try {
            parser.parseMembers(context);
            fail("ParserException expected");
        } catch (ParserException ex) {
            assertEquals("One or more class names is expected after implements", ex.getMessage());
        }
    }

    public void testParseInterfaceOptionsException() throws Exception {
        LexerInputString input = new LexerInputString("interface test depends {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        try {
            parser.parseMembers(context);
            fail("ParserException expected");
        } catch (ParserException ex) {
            assertEquals("An interface name should be followed by extends and an interface body", ex.getMessage());
        }
    }

    public void testParseInterfaceExtendsException() throws Exception {
        LexerInputString input = new LexerInputString("interface test extends {}");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        try {
            parser.parseMembers(context);
            fail("ParserException expected");
        } catch (ParserException ex) {
            assertEquals("A class name is expected after extends", ex.getMessage());
        }
    }
}
