package au.com.illyrian.jsub;

import java.io.File;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.jsub.bnf.JsubBnfParser;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.impl.ModuleContext;
import au.com.illyrian.test.StringReadWriter;

public class JsubParserTest extends TestCase {
    ClassMakerFactory factory = new ClassMakerFactory();    
    StringReadWriter out = new StringReadWriter();
    
    public AstStructureVisitor createAstVisitor(ClassMaker maker, String source) {
        AstStructureVisitor visitor = new AstStructureVisitor(maker);

        visitor.setFilename(source);
        return visitor;
    }
    
    public void testCreateParser() throws Exception {
        out.println("package au.com.illyrian.jsub;");
        out.println("public class MyClass implements One {");
        out.println("  int one;");
        out.println("  public int one() {return one;}");
        out.println("  public void none() {}");
        out.println("  public void one(int i) {this.one = i;}");
        out.println("}");
        JsubBnfParser bnfParser = new JsubBnfParser(new File("test"));
        ModuleContext context = bnfParser.createModuleContext(out.getReader(), null);
        ParseModule<AstStructure> parser = bnfParser.getParser();
        AstStructure tree = parser.parseModule(context);
        assertNotNull("AstStructure", tree);
        String expected = "package au.com.illyrian.jsub;\npublic class MyClass implements One\n";
        assertEquals(expected, tree.toString());
        
        ClassMaker maker = factory.createClassMaker();
        AstStructureVisitor visitor = createAstVisitor(maker, context.getSource());
        
        factory.setPass(ClassMakerConstants.FIRST_PASS);
        tree.resolveDeclaration(visitor);
        maker.EndClass();
        
        factory.setPass(ClassMakerConstants.SECOND_PASS);
        tree.resolveDeclaration(visitor);
        maker.EndClass();
        
        Class<One> clazz = maker.defineClass();
        One instance = clazz.newInstance();
        assertEquals(0, instance.one());
        instance.none();
        instance.one(5);
        assertEquals(5, instance.one());
    }
}
