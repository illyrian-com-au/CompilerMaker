package au.com.illyrian.jesub.ast;

import java.lang.reflect.Field;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;

public class AstStructureTreeTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    public void testDeclareModule() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression auCom = build.Dot(build.Name("au"), build.Name("com"));
        assertEquals("Wrong resolvePath", "au.com", auCom.toString());
        AstPackage package1 = build.Package(build.Dot(auCom, build.Name("illyrian")));
        assertEquals("Wrong resolvePath", "package au.com.illyrian;", package1.toString());
        AstImport import1 = build.Import(
                build.Dot(
                        build.Dot(
                                build.Dot(package1.getExpression(), build.Name("jesub")), 
                                build.Name("ast")), 
                                build.Name("AstStructure")));
        assertEquals("Wrong resolvePath", "import au.com.illyrian.jesub.ast.AstStructure;", import1.toString());
        AstImport import2 = build.Import(build.Name("java.io.File")); 
        AstStructure importsList = build.Seq(import1, import2);
        
        // Declare Class
        AstModifiers modifiers = build.Modifier("public"); // FIXME
        TerminalName name = build.Name("Test");
        AstClass declaredClass = build.DeclareClass(modifiers, name, null, null, null);
        
        AstModule module = build.Module(package1, importsList, declaredClass);
        
        String expected = "package au.com.illyrian;\n"
      		+ "import au.com.illyrian.jesub.ast.AstStructure;\n"
                + "import java.io.File;\n"
      		+ "public class Test\n";
        assertEquals("Wrong AstModule.toString", expected, module.toString());

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);
        
        assertEquals("Package name:", "au.com.illyrian", maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.Test", maker.getFullyQualifiedClassName());
        Type declared1 = maker.findType("AstStructure");
        Type declared2 = maker.findType("File");
        assertNotNull("findDeclaredType:", declared1);
        assertNotNull("findDeclaredType:", declared2);
        assertEquals("Imported name expected:", "au.com.illyrian.jesub.ast.AstStructure", declared1.getName());
        assertEquals("Imported name expected:", "java.io.File", declared2.getName());
    }

    public void testDeclareClass() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstPackage package1 = build.Package(build.Dot(build.Dot(
        		build.Dot(build.Dot(build.Name("au"), build.Name("com")), build.Name("illyrian")),
        		build.Name("jesub")), build.Name("ast")));
        
        // Imports
        AstImport import1 = build.Import(build.Dot(package1.getExpression(), build.Name("AstStructure")));
        AstImport import2 = build.Import(build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("Runnable")));
        AstStructure imports = build.Seq(import1, import2);
        
        AstExpression baseClass = build.Name("AstStructureBase");

        // Implements
        AstExpression implementsList = build.Link(build.Name("AstStructure"), build.Name("Runnable"));

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        AstClass declareClass = build.DeclareClass(modifiers, name, baseClass, implementsList, null);
        
        AstModule module = build.Module(package1, imports, declareClass);

        String expected = "public class Test extends AstStructureBase"
    		  + " implements AstStructure, Runnable {\n"
    		  + "\n}\n";
        assertEquals("Wrong AstDeclaredClass.toString", expected, declareClass.toString());

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);
        
        assertEquals("Package name:", "au.com.illyrian.jesub.ast", maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.jesub.ast.Test", maker.getFullyQualifiedClassName());
        ClassType superClass = maker.getSuperClass();
        assertEquals("Super class:", "au.com.illyrian.jesub.ast.AstStructureBase", superClass.getName());
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
    
    public static int getIntField(Class myClass, Object myObj, String name) throws IllegalAccessException, NoSuchFieldException
    {
        Field sidField = myClass.getDeclaredField(name);
        return sidField.getInt(myObj);
    }

    public void testDeclareVariable() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Name("au.com.illyrian.jesub.ast");

        AstExpression type = build.Name("int");
        AstModifiers astPublic = build.Modifier("public");
        AstStructure var1 = build.Declare(astPublic, type, build.Name("w"));
        AstModifiers astProtected = build.Modifier("protected");
        AstStructure var2 = build.Declare(astProtected, type, build.Name("x"));
        // package
        AstStructure var3 = build.Declare(null, type, build.Name("y"));
        AstModifiers astPrivate = build.Modifier("private");
        AstStructure var4 = build.Declare(astPrivate, type, build.Name("z"));
        
        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName base = build.Name("au.com.illyrian.jesub.ast.FuncABC");
        
        AstClass declareClass = build.DeclareClass(modifiers, name, base, null, null);
        declareClass.add(var1);
        declareClass.add(var2);
        declareClass.add(var3);
        declareClass.add(var4);
        
        AstModule module = build.Module(packageName, null, declareClass);

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);
        
        declareFuncABC(maker);
        
        assertEquals("Package name:", "au.com.illyrian.jesub.ast", maker.getPackageName());
        assertEquals("Package name:", getClass().getPackage().getName(), maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.jesub.ast.Test", maker.getFullyQualifiedClassName());
        ClassType superClass = maker.getSuperClass();
        assertEquals("Super class:", "au.com.illyrian.jesub.ast.FuncABC", superClass.getName());
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncABC func = (FuncABC)instance;

        setIntField(parserClass, instance, "w", 3);
        assertEquals("FuncABC result", 3, func.f(10, 6, -2));
        assertEquals("field w: ", 3, getIntField(parserClass, instance, "w"));
//        assertEquals("field x: ", 3, getIntField(parserClass, instance, "x"));
//        assertEquals("field y: ", 3, getIntField(parserClass, instance, "y"));
//        assertEquals("field z: ", 3, getIntField(parserClass, instance, "z"));
     }

    public void testDeclareMethod() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Name("au.com.illyrian.jesub.ast");

        AstExpression type = build.Name("int");
        AstModifiers astPublic = build.Modifier("public");
        
        // Declare method
        AstStructure paramA = build.Declare(null, type, build.Name("a")); 
        AstStructure paramB = build.Declare(null, type, build.Name("b")); 
        AstStructure paramC = build.Declare(null, type, build.Name("c")); 
        AstStructure params = build.Seq(build.Seq(paramA, paramB), paramC);
        AstStatementReturn body = build.Return(build.Div(build.Div(build.Name("a"), build.Name("b")), build.Name("c")));
        AstDeclareMethod method = build.Method(astPublic, type, build.Name("f"), params, body);

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName base = build.Name("au.com.illyrian.jesub.ast.FuncABC");
        AstClass declareClass = build.DeclareClass(modifiers, name, base, null, null);
        declareClass.add(method);
        
        // Declare Module
        AstModule module = build.Module(packageName, null, declareClass);

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);
        
        assertEquals("Package name:", "au.com.illyrian.jesub.ast", maker.getPackageName());
        assertEquals("Package name:", getClass().getPackage().getName(), maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.jesub.ast.Test", maker.getFullyQualifiedClassName());
        ClassType superClass = maker.getSuperClass();
        assertEquals("Super class:", "au.com.illyrian.jesub.ast.FuncABC", superClass.getName());
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncABC func = (FuncABC)instance;
        int result = func.f(50, 5, -2);
        assertEquals("f(50, 5, -2)", -5, result);
     }
}
