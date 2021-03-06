package au.com.illyrian.bnf;

import java.io.File;
import java.io.IOException;

import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.bnf.maker.BnfParserBase;
import au.com.illyrian.bnf.parser.BnfParser;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.SourceNames;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.jesub.ast.AstClass;
import au.com.illyrian.jesub.ast.AstImport;
import au.com.illyrian.jesub.ast.AstModifiers;
import au.com.illyrian.jesub.ast.AstPackage;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.impl.ModuleContext;

public class BnfCompiler {
    private final File sourceDir;
    private final ClassMakerFactory factory;
    private String defaultType = "AstExpression";
    
    public BnfCompiler(File sourceDir, ClassMakerFactory factory) {
        this.sourceDir = sourceDir;
        this.factory = factory;
    }
    
    public ClassMaker createClassMaker(SourceNames names) {
        ClassMaker maker = factory.createClassMaker();
        maker.setPackageName(names.getPackageName());
        maker.setSimpleClassName(names.getClassName());
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
        maker.Extends(BnfParserBase.class);
    }
    
    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(Class defaultType) {
        setDefaultType(defaultType.getName());
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public BnfMakerVisitor createBnfVisitor(ClassMaker maker, String source) {
        BnfMakerVisitor visitor = new BnfMakerVisitor(maker);

        visitor.setActionRequired(true);
        visitor.setDefaultTypeName(defaultType);
        visitor.setFilename(source);
        visitor.prepare(maker);
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
    public <T> ParseMembers<T> compile(String source) throws InstantiationException, IllegalAccessException, IOException {
        SourceNames names = new SourceNames(source);
        File file = new File(sourceDir, source);
        
        ModuleContext context = new ModuleContext();
        context.setInputFile(file, source);

        ClassMaker maker = createClassMaker(names);
        
        visitBnf(context, maker);
        
        Class<ParseMembers> parserClass = maker.defineClass();
        ParseMembers parser = parserClass.newInstance();
        return parser;
    }
}