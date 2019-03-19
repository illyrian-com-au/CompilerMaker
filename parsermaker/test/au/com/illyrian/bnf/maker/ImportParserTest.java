package au.com.illyrian.bnf.maker;

import java.io.File;
import java.io.IOException;

import au.com.illyrian.bnf.BnfCompiler;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.SourceNames;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

public class ImportParserTest extends BnfMakerTextBase {
    private static final String TEST_DIR = "test/";

    ClassMakerFactory factory = new ClassMakerFactory();
    ParseMembers<AstStructure> parser;

    public void setUp() throws InstantiationException, IllegalAccessException, IOException {
        ClassMakerFactory factory = new ClassMakerFactory();
        BnfCompiler compiler = new BnfCompiler(new File(TEST_DIR), factory);
        parser = compiler.compile("test/ImportParser.bnf");
    }

    ClassMaker resolve(AstStructure tree) {
        ClassMaker maker = factory.createClassMaker();
        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        tree.resolveDeclaration(visitor);
        return maker;
    }

    public void testSourceNames() {
        SourceNames names = new SourceNames("au/com/illyrian/bnf/ImportParser.bnf");
        assertEquals("au/com/illyrian/bnf/ImportParser.bnf", names.getSource());
        assertEquals("au.com.illyrian.bnf", names.getPackageName());
        assertEquals("ImportParser", names.getClassName());
        assertEquals("bnf", names.getExtention());
    }

    public void testParseImportSimpleName() throws Exception {
        LexerInputString input = new LexerInputString("import java.io.IOException;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        AstStructure tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "import java.io.IOException;\n";
        assertEquals(expected, tree.toString());
        
        ClassMaker maker = resolve(tree);
        assertNotNull("IOException", maker.findImportedType("IOException"));
    }

    public void testParseImportMultiple() throws Exception {
        StringReadWriter out = new StringReadWriter();
        out.println("import java.io.IOException;");
        out.println("import au.com.illyrian.classmaker.ClassMaker;");
        out.println("import java.util.*;");
        out.close();
        Input input = new LexerInputStream(out.getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        AstStructure tree = parser.parseMembers(compile);
        assertNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        String expected = "import java.io.IOException;\nimport au.com.illyrian.classmaker.ClassMaker;\nimport java.util.*;\n";
        assertEquals(expected, tree.toString());

        ClassMaker maker = resolve(tree);
        assertNotNull("IOException", maker.findImportedType("IOException"));
        assertNotNull("ClassMaker", maker.findImportedType("ClassMaker"));
    }

    public void testParseImportStar() throws Exception {
        LexerInputString input = new LexerInputString("import java.io.*;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        AstStructure tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "import java.io.*;\n";
        assertEquals(expected, tree.toString());
        
        ClassMaker maker = resolve(tree);
        assertNotNull("IOException", maker.findImportedType("IOException"));
    }

    public void testParseImportQualifiedName() throws Exception {
        LexerInputString input = new LexerInputString("import au.com.test;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "import au.com.test;\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseErrorIdentifierExpected() throws Exception {
        LexerInputString input = new LexerInputString("import au. ;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
        } catch (ParserException ex) {
            assertEquals("Incomplete import path", ex.getMessage());
        }
    }

    public void testParseImportPathStar() throws Exception {
        LexerInputString input = new LexerInputString("import au.com. *;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "import au.com.*;\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseImportPathDotStar() throws Exception {
        LexerInputString input = new LexerInputString("import au.com.*;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);

        assertNotNull("Should not be null:", tree);
        String expected = "import au.com.*;\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseNoImport() throws Exception {
        LexerInputString input = new LexerInputString("");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);

        assertNotNull("Should not be null:", tree);
        assertEquals("", tree.toString());
    }

    public void testParseErrorQualifiedExpected() throws Exception {
        LexerInputString input = new LexerInputString("import ;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
        } catch (ParserException ex) {
            assertEquals("Incomplete import path", ex.getMessage());
        }
    }

    public void testParseErrorNameExpected() throws Exception {
        LexerInputString input = new LexerInputString("import au*;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
        } catch (ParserException ex) {
            assertEquals(".* expected", ex.getMessage());
        }
    }
}
