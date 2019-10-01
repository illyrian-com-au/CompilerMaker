package au.com.illyrian.bnf.maker;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import test.IntValue;
import au.com.illyrian.bnf.BnfCompiler;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.parser.maker.ModuleContextMaker;
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

    ClassMaker resolve(AstStructure tree) {
        ClassMaker maker = factory.createClassMaker();
        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        tree.resolveDeclaration(visitor);
        return maker;
    }

    public void testParsePackage() throws Exception {
        LexerInputString input = new LexerInputString("package test; public class Test { }");
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "package test;\npublic class Test\n";
        assertEquals(expected, tree.toString());

        ClassMaker maker = resolve(tree);
        Class clazz = maker.defineClass();
        Object instance = clazz.newInstance();

        assertEquals("test", maker.getPackageName());
        assertEquals("Test", maker.getSimpleClassName());
        assertEquals("Extends", ClassMakerFactory.OBJECT_TYPE, maker.getExtendsType());
        assertEquals("Class modifiers", ClassMakerConstants.ACC_PUBLIC, maker.getModifiers());
        assertEquals("Number of interfaces", 0, maker.getDeclaredInterfaces().length);
        assertEquals("Number of constructors", 1, maker.getDeclaredConstructors().length);
        assertEquals("Number of fields", 0, maker.getDeclaredFields().length);
        assertEquals("Number of methods", 0, maker.getDeclaredMethods().length);
        assertNotNull("test.Test", clazz);
        assertEquals("Test", clazz.getSimpleName());
        assertEquals("test.Test", clazz.getName());
        assertNotNull("Package name", clazz.getPackage());
        assertEquals("test", clazz.getPackage().getName());
        assertEquals("Super class", Object.class, clazz.getSuperclass());
        assertEquals("Class modifiers", ClassMakerConstants.ACC_PUBLIC, clazz.getModifiers());
        assertEquals("Number of interfaces", 0, clazz.getInterfaces().length);
        assertEquals("Number of constructors", 1, clazz.getDeclaredConstructors().length);
        assertEquals("Number of fields", 0, clazz.getDeclaredFields().length);
        assertEquals("Number of methods", 0, clazz.getDeclaredMethods().length);
        assertNotNull("test.Test", instance);
        assertEquals("test.Test", instance.toString().substring(0, 9));
    }

    public void testParseImports() throws Exception {
        StringReadWriter out = new StringReadWriter();
        out.println("import java.io.File;");
        out.println("import au.com.illyrian.bnf.maker.ModuleParserTest;");
        out.println("public class Test {}");
        out.close();
        Input input = new LexerInputStream(out.getReader(), null);
        ModuleContext context = new ModuleContext();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        String expected = "import java.io.File;\nimport au.com.illyrian.bnf.maker.ModuleParserTest;\npublic class Test\n";
        assertEquals(expected, tree.toString());

        ClassMaker maker = resolve(tree);
        Class clazz = maker.defineClass();
        Object instance = clazz.newInstance();
        assertEquals("Test", instance.toString().substring(0, 4));
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
        final String fullName = "au.com.illyrian.bnf.maker.IntTest";
        StringReadWriter out = new StringReadWriter();
        out.println("package au.com.illyrian.bnf.maker;");
        out.println("import test.IntValue;");
        out.println("public class IntTest extends IntValue {}");
        out.close();
        Input input = new LexerInputStream(out.getReader(), null);
        ModuleContext context = new ModuleContextMaker();
        context.setInput(input);
        AstStructure tree = parser.parseMembers(context);
        assertNotNull("Should not be null:", tree);
        String expected = "package au.com.illyrian.bnf.maker;\n"
                + "import test.IntValue;\n"
                + "public class IntTest extends IntValue\n";
        assertEquals(expected, tree.toString());

        ClassMaker maker = resolve(tree);
        maker.EndClass();
        assertEquals("au.com.illyrian.bnf.maker", maker.getPackageName());
        assertEquals("IntTest", maker.getSimpleClassName());
        assertEquals("Extends", factory.stringToType("test.IntValue"), maker.getExtendsType());
        assertEquals("Class modifiers", ClassMakerConstants.ACC_PUBLIC, maker.getModifiers());
        assertEquals("Number of interfaces", 0, maker.getDeclaredInterfaces().length);
        assertEquals("Number of constructors", 1, maker.getDeclaredConstructors().length);
        assertEquals("Number of fields", 0, maker.getDeclaredFields().length);
        assertEquals("Number of methods", 0, maker.getDeclaredMethods().length);

        Class<IntValue> clazz = maker.defineClass();
        IntValue instance = clazz.newInstance();
        assertNotNull("test.IntTest", clazz);
        assertEquals("IntTest", clazz.getSimpleName());
        assertEquals(fullName, clazz.getName());
        assertNotNull("Package name", clazz.getPackage());
        assertEquals("au.com.illyrian.bnf.maker", clazz.getPackage().getName());
        assertEquals("Super class", IntValue.class, clazz.getSuperclass());
        assertEquals("Class modifiers", ClassMakerConstants.ACC_PUBLIC, clazz.getModifiers());
        assertEquals("Number of interfaces", 0, clazz.getInterfaces().length);
        assertEquals("Number of constructors", 1, clazz.getDeclaredConstructors().length);
        assertEquals("Number of fields", 0, clazz.getDeclaredFields().length);
        assertEquals("Number of methods", 0, clazz.getDeclaredMethods().length);
        assertNotNull("test.IntTest", instance);
        assertNotNull("SimpleClassLoader", factory.getClassLoader().loadClass(fullName));
        assertEquals("IntValue.getInt()", 10, instance.getInt());
        // Synthetic class should not be visible except through factory class loader.
        try {
            getClass().getClassLoader().loadClass(fullName);
            fail("Should throw ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            assertEquals(fullName, e.getMessage());
        }
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
