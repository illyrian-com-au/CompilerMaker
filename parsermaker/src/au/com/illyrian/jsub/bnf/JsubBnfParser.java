package au.com.illyrian.jsub.bnf;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.bnf.parser.BnfParser;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.SourceNames;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.impl.ModuleContext;

public class JsubBnfParser {
    public static final String TEST_DIR = "test";
    public static final String JSUB_BNF = "au/com/illyrian/bnf/Jesub_syntax.bnf";

    ClassMakerFactory factory = new ClassMakerFactory();
    ParseModule<AstStructure> parser;
    File sourceDir;

    public JsubBnfParser(File sourceDir) throws InstantiationException, IllegalAccessException, IOException {
        this.sourceDir = sourceDir;
        ClassMakerFactory factory = new ClassMakerFactory();
        JsubCompiler compiler = new JsubCompiler(factory);
        parser = compiler.compile();        
    }
    
    public ModuleContext createModuleContext(Reader reader, String source) throws IOException {
        ModuleContext context = new ModuleContext();
        context.setInputReader(reader, source);
        return context;
    }
    
    public ParseModule<AstStructure> getParser() {
        return parser;
    }
    
    public AstStructureVisitor createAstVisitor(ClassMaker maker, String source) {
        AstStructureVisitor visitor = new AstStructureVisitor(maker);

        visitor.setFilename(source);
        return visitor;
    }
/*
    public void visitBnf(ModuleContext context, ClassMaker maker) {
        BnfParser bnfParser = new BnfParser();

        BnfTreeParser tree = bnfParser.parseModule(context);

        AstStructureVisitor visitor = createAstVisitor(maker, context.getSource());
        
        factory.setPass(ClassMakerConstants.FIRST_PASS);
        tree.resolveDeclaration(visitor);
        maker.EndClass();
        
        factory.setPass(ClassMakerConstants.SECOND_PASS);
        tree.resolveDeclaration(visitor);
        maker.EndClass();
    }
 */   

/*    
    public AstStructure parse(ModuleContext context) throws InstantiationException, IllegalAccessException, IOException {
        File file = new File(sourceDir, source);
        
        ModuleContext context = new ModuleContext();
        context.setInputFile(file, source);

        AstStructure tree = parser.parseMembers(context);
        return tree;
    }
    
    @SuppressWarnings("unchecked")
    public <T> Class<T> compile(String source) throws InstantiationException, IllegalAccessException, IOException {
        SourceNames names = new SourceNames(source);
        File file = new File(sourceDir, source);
        
        ModuleContext context = new ModuleContext();
        context.setInputFile(file, source);

        AstStructure tree = parser.parseMembers(context);
        
        ClassMaker maker = factory.createClassMaker();
        // FIXME do something with maker.
        Class<T> javaClass = maker.defineClass();
        return javaClass;
    }
*/
}
