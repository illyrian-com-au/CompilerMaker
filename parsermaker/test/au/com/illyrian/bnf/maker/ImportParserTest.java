package au.com.illyrian.bnf.maker;

import java.io.File;

import au.com.illyrian.bnf.BnfParser;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.jesub.ast.AstClass;
import au.com.illyrian.jesub.ast.AstImport;
import au.com.illyrian.jesub.ast.AstPackage;
import au.com.illyrian.jesub.ast.AstModifiers;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputStream;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

public class ImportParserTest extends BnfMakerTextBase
{
    private static final String TEST_DIR = "test/";
    
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    BnfMakerVisitor visitor = new BnfMakerVisitor(maker);

    public void importClasses() {
        maker.Import(AstExpression.class);
        maker.Import(AstStructure.class);
        maker.Import(AstModifiers.class);
        maker.Import(AstPackage.class);
        maker.Import(AstImport.class);
        maker.Import(AstClass.class);
        maker.Import(TerminalName.class);
    }

    public ParseMembers loadParser() throws Exception 
    {
        String packageName = "test"; 
        String className = "ImportParser"; 
        String source = "test/ImportParser.bnf";
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
        maker.Import(AstImport.class);
        maker.Import(AstExpression.class);
        maker.Import(AstStructure.class);
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

    public void testParseImportSimpleName() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("import test;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "import test;\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseImportMultiple() throws Exception 
    {
        ParseMembers parser = loadParser();

        StringReadWriter out = new StringReadWriter();
        out.println("import test.MyClass;");
        out.println("import au.com.test.OtherClass;");
        out.println("import au.com.other.*;");
        out.close();
        Input input = new LexerInputStream(out.getReader(), null);
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        String expected = "import test.MyClass;\nimport au.com.test.OtherClass;\nimport au.com.other.*;\n";
        assertEquals(expected, tree.toString());
    }

    public void testParseImportQualifiedName() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("import au.com.test;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "import au.com.test;\n";
        assertEquals(expected, tree.toString());
     }

    public void testParseErrorIdentifierExpected() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("import au. ;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
        } catch (ParserException ex) {
            assertEquals("Incomplete import path", ex.getMessage());
        }
    }

    public void testParseImportPathStar() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("import au.com. *;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", tree);
        String expected = "import au.com.*;\n";
        assertEquals(expected, tree.toString());
     }

    public void testParseImportPathDotStar() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("import au.com.*;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
 
       assertNotNull("Should not be null:", tree);
        String expected = "import au.com.*;\n";
        assertEquals(expected, tree.toString());
     }

    public void testParseNoImport() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
 
       assertNotNull("Should not be null:", tree);
        assertEquals("", tree.toString());
     }

    public void testParseErrorQualifiedExpected() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("import ;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
        } catch (ParserException ex) {
            assertEquals("Incomplete import path", ex.getMessage());
        }
    }

    public void testParseErrorNameExpected() throws Exception 
    {
        ParseMembers parser = loadParser();
        
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
