package au.com.illyrian.jesub.maker;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.jesub.ast.AstStructureFactoryMaker;

public class AstStructureMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    public void testDeclareModule() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        AstExpression auCom = build.Dot(build.Name("au"), build.Name("com"));
        assertEquals("Wrong resolvePath", "au.com", auCom.toString());
        AstExpression auComIllyrian = build.Dot(auCom, build.Name("illyrian"));
        assertEquals("Wrong resolvePath", "au.com.illyrian", auComIllyrian.toString());
        AstExpression AstStructure = 
                build.Dot(
                        build.Dot(
                                build.Dot(auComIllyrian, build.Name("jesub")), 
                                build.Name("ast")), 
                                build.Name("AstStructure"));
        assertEquals("Wrong resolvePath", "au.com.illyrian.jesub.ast.AstStructure", AstStructure.toString());
        build.Package(auComIllyrian);
        build.Import(AstStructure);
        build.Import(build.Name("java.io.File"));
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        
//        AstStructureVisitor visitor = new AstStructureVisitor(maker);
//        build.getModule().resolveDeclaration(visitor);
        
        assertEquals("Package name:", "au.com.illyrian", maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.Test", maker.getFullyQualifiedClassName());
        DeclaredType declared1 = maker.findDeclaredType("AstStructure");
        DeclaredType declared2 = maker.findDeclaredType("File");
        assertNotNull("findDeclaredType:", declared1);
        assertNotNull("findDeclaredType:", declared2);
        assertEquals("Imported name expected:", "au.com.illyrian.jesub.ast.AstStructure", declared1.getName());
        assertEquals("Imported name expected:", "java.io.File", declared2.getName());
        
//        String expected = "package au.com.illyrian;\n"
//        		+ "import au.com.illyrian.jesub.ast.AstStructure, java.io.File;\n"
//        		+ "class Test ...";
//        assertEquals("Wrong AstModule.toString", expected, maker.toString());
    }

    public void testDeclareClass() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.ast"));
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Extends(build.Name("AstStructureBase"));
        build.Implements(build.Name("AstStructure"));
        build.Implements(build.Name("Runnable"));

//        AstStructureVisitor visitor = new AstStructureVisitor(maker);
//        build.getModule().resolveDeclaration(visitor);
        
//        String expected = "class Test extends AstStructureBase\n"
//        		+ "    implements AstStructure, Runnable\n"
//        		+ "{...}";
//        assertEquals("Wrong AstDeclaredClass.toString", expected, build.getModule().getDeclareClass().toString());
        
        assertEquals("Package name:", "au.com.illyrian.jesub.ast", maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.jesub.ast.Test", maker.getFullyQualifiedClassName());
        DeclaredType superClass = maker.getSuperClass();
        assertEquals("Super class:", "au.com.illyrian.jesub.ast.AstStructureBase", superClass.getName());
    }

    public static class FuncABC 
    {
        public int valueA; 
        public int valueB; 
        public int valueC; 
        public int set(int a, int b, int c) {valueA = a; valueB = b; valueC = c; return a+b+c;} 
    	public int f(int a, int b, int c) {return a+b+c;}
    }

    private void declareFuncABC(ClassMaker maker)
    {
        maker.Method("f", int.class, 0);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Declare("c", int.class, 0);
        maker.Begin();
        {
            maker.Set(maker.This(), "x", maker.Get("a"));
            maker.Set(maker.This(), "y", maker.Get("b"));
            maker.Set(maker.This(), "z", maker.Get("c"));
            maker.Return(maker.Get(maker.This(), "w"));
        }
        maker.End();
    }
    
    public void testDeclareVariable() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.maker"));
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Extends(build.Name(FuncABC.class.getName()));

        AstExpression type = build.Name("int");
        build.Declare(build.Modifier("public"), type, build.Name("w"));
        build.Declare(build.Modifier("protected"), type, build.Name("x"));
        // package
        build.Declare(0, type, build.Name("y"));
        build.Declare(build.Modifier("private"), type, build.Name("z"));
        
        //AstStructureVisitor visitor = new AstStructureVisitor(maker);
        //build.getModule().resolveDeclaration(visitor);
        
        declareFuncABC(maker);
        
        assertEquals("Not the same package as Test case", getClass().getPackage().getName(), maker.getPackageName());
        assertEquals("Package name:", "au.com.illyrian.jesub.maker", maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.jesub.maker.Test", maker.getFullyQualifiedClassName());
        DeclaredType superClass = maker.getSuperClass();
        assertEquals("Super class:", "au.com.illyrian.jesub.maker.AstStructureMakerTest$FuncABC", superClass.getName());
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncABC func = (FuncABC)instance;
        func.f(10, 6, -2);

        setIntField(parserClass, instance, "w", 3);
        assertEquals("field w: ", 3, getIntField(parserClass, instance, "w"));
     }

    public void testDeclareMethod() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.maker"));
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Extends(build.Name(FuncABC.class.getName()));

        TerminalName type = build.Name("int");
        build.Method(build.Modifier("public"), type, build.Name("f"));
        
        build.Declare(type, build.Name("a"));
        build.Declare(type, build.Name("b"));
        build.Declare(type, build.Name("c"));

        // Method body
        build.Begin();
        {
            build.Return(build.Div(build.Div(build.Name("a"), build.Name("b")), build.Name("c")));
        }
        build.End();
        
        assertEquals("Not the same package as Test case", getClass().getPackage().getName(), maker.getPackageName());
        assertEquals("Package name:", "au.com.illyrian.jesub.maker", maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.jesub.maker.Test", maker.getFullyQualifiedClassName());
        DeclaredType superClass = maker.getSuperClass();
        assertEquals("Super class:", "au.com.illyrian.jesub.maker.AstStructureMakerTest$FuncABC", superClass.getName());
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncABC func = (FuncABC)instance;
        int result = func.f(50, 5, -2);
        assertEquals("f(50, 5, -2)", -5, result);
     }
}
