package au.com.illyrian.bnf.maker;

import java.io.File;
import java.io.IOException;

import au.com.illyrian.bnf.BnfCompiler;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.ModuleContext;

public class PackageParserTest extends BnfMakerTextBase {
    private static final String TEST_DIR = "test/";

    ClassMakerFactory factory = new ClassMakerFactory();
    ParseMembers<AstStructure> parser;

    public void setUp() throws InstantiationException, IllegalAccessException, IOException {
        ClassMakerFactory factory = new ClassMakerFactory();
        BnfCompiler compiler = new BnfCompiler(new File(TEST_DIR), factory);
        parser = compiler.compile("test/PackageParser.bnf");
    }
    
    ClassMaker resolve(AstStructure tree) {
        ClassMaker maker = factory.createClassMaker();
        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        tree.resolveDeclaration(visitor);
        return maker;
    }

    public void testParsePackageSimpleName() throws Exception {
        LexerInputString input = new LexerInputString("package test;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        AstStructure tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        assertEquals("package test;\n", tree.toString());
        
        ClassMaker maker = resolve(tree);
        assertEquals("test", maker.getPackageName());
    }

    public void testParsePackageQualifiedName() throws Exception {
        LexerInputString input = new LexerInputString("package au.com.test;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        AstStructure tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        String expected = "package au.com.test;\n";
        assertEquals(expected, tree.toString());
        
        ClassMaker maker = resolve(tree);
        assertEquals("au.com.test", maker.getPackageName());
    }

    public void testParseErrorIdentifierExpected() throws Exception {
        LexerInputString input = new LexerInputString("package au. ;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
        } catch (ParserException ex) {
            assertEquals("Identifier expected", ex.getMessage());
        }
    }

    public void testParseErrorQualifiedExpected() throws Exception {
        LexerInputString input = new LexerInputString("package ;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
        } catch (ParserException ex) {
            assertEquals("Package name expected", ex.getMessage());
        }
    }
}
