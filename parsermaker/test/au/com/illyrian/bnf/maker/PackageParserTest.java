package au.com.illyrian.bnf.maker;

import java.io.File;

import au.com.illyrian.bnf.BnfParser;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.impl.ModuleContext;

public class PackageParserTest extends BnfMakerTextBase
{
    private static final String TEST_DIR = "test/";
    
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    BnfMakerVisitor visitor = new BnfMakerVisitor(maker);

    public ParseMembers loadParser() throws Exception 
    {
        String packageName = "test"; 
        String className = "PackageParser"; 
        String source = "test/PackageParser.bnf";
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

    public void testParsePackageSimpleName() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("package test;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        String expected = "package test;\n";
        assertEquals(expected, tree.toString());
    }

    public void testParsePackageQualifiedName() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("package au.com.test;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        Object tree = parser.parseMembers(compile);
        assertNotNull("Should not be null:", input.getLine());
        assertNotNull("Should not be null:", tree);
        String expected = "package au.com.test;\n";
        assertEquals(expected, tree.toString());
     }

    public void testParseErrorIdentifierExpected() throws Exception 
    {
        ParseMembers parser = loadParser();
        
        LexerInputString input = new LexerInputString("package au. ;");
        ModuleContext compile = new ModuleContext();
        compile.setInput(input);
        try {
            parser.parseMembers(compile);
        } catch (ParserException ex) {
            assertEquals("Identifier expected", ex.getMessage());
        }
    }

    public void testParseErrorQualifiedExpected() throws Exception 
    {
        ParseMembers parser = loadParser();
        
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
