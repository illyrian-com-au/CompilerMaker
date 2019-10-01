package au.com.illyrian.jsub.bnf;

import java.io.File;
import java.io.IOException;

import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.bnf.maker.BnfParserBase;
import au.com.illyrian.bnf.parser.BnfParser;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.jesub.ast.AstClass;
import au.com.illyrian.jesub.ast.AstImport;
import au.com.illyrian.jesub.ast.AstModifiers;
import au.com.illyrian.jesub.ast.AstPackage;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.impl.ModuleContext;

public class JsubCompiler {
    public static final String TEST_DIR = "test";
    public static final String JSUB_BNF = "au/com/illyrian/bnf/Jesub_syntax.bnf";
    private final File sourceDir = new File(TEST_DIR);
    private final ClassMakerFactory factory;
    
    public JsubCompiler(ClassMakerFactory factory) {
        this.factory = factory;
    }
    
    public ClassMaker createClassMaker() {
        ClassMaker maker = factory.createClassMaker();
        maker.setPackageName("au.com.illyrian.bnf");
        maker.setSimpleClassName("Jsub");
        prepare(maker);
        return maker;
    }
    
    public void prepare(ClassMaker maker) {
        maker.Import(AstExpression.class);
        maker.Import(AstStructure.class);
        maker.Import(AstModifiers.class);
        maker.Import(AstPackage.class);
        maker.Import(AstImport.class);
        maker.Import(AstClass.class);
        maker.Import(TerminalName.class);
        maker.Extends(JsubParserBase.class);
    }
    
    public BnfMakerVisitor createBnfVisitor(ClassMaker maker, String source) {
        BnfMakerVisitor visitor = new BnfMakerVisitor(maker);

        visitor.setActionRequired(true);
        visitor.setDefaultTypeName("AstStructure");
        visitor.setFilename(source);
        return visitor;
    }
    
    public void visitBnf(ModuleContext context, ClassMaker maker) {
        BnfParser bnfParser = new BnfParser();

        BnfTreeParser tree = bnfParser.parseModule(context);

        BnfMakerVisitor visitor = createBnfVisitor(maker, context.getSource());
        
        factory.setPass(ClassMakerConstants.FIRST_PASS);
        tree.resolveDeclaration(visitor);
        maker.EndClass();
        
        factory.setPass(ClassMakerConstants.SECOND_PASS);
        tree.resolveDeclaration(visitor);
        maker.EndClass();
    }
    
    @SuppressWarnings("unchecked")
    public <T> ParseModule<T> compile() throws InstantiationException, IllegalAccessException, IOException {
        File file = new File(sourceDir, JSUB_BNF);
        
        ModuleContext context = new ModuleContext();
        context.setInputFile(file, JSUB_BNF);

        ClassMaker maker = createClassMaker();
        
        visitBnf(context, maker);
        
        Class<ParseModule> parserClass = maker.defineClass();
        ParseModule parser = parserClass.newInstance();
        return parser;
    }
}