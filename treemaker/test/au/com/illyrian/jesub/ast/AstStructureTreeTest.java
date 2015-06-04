package au.com.illyrian.jesub.ast;

import java.lang.reflect.Field;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionLink;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.DeclaredType;

public class AstStructureTreeTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    public void testDeclareModule() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression auCom = build.Dot(build.Name("au"), build.Name("com"));
        assertEquals("Wrong resolvePath", "au.com", auCom.toString());
        AstExpression packageName = build.Dot(auCom, build.Name("illyrian"));
        assertEquals("Wrong resolvePath", "au.com.illyrian", packageName.toString());
        AstExpression AstStructure = 
                build.Dot(
                        build.Dot(
                                build.Dot(packageName, build.Name("jesub")), 
                                build.Name("ast")), 
                                build.Name("AstStructure"));
        assertEquals("Wrong resolvePath", "au.com.illyrian.jesub.ast.AstStructure", AstStructure.toString());
        AstExpression fileImport = build.Name("java.io.File"); 
        AstExpressionLink importsList = build.Link(AstStructure, fileImport);
        
        // Declare Class
        AstModifiers modifiers = build.Modifier("public"); // FIXME
        TerminalName name = build.Name("Test");
        AstDeclareClass declaredClass = build.DeclareClass(modifiers, name, null, null, null);
        
        AstDeclareModule module = build.Module(packageName, importsList, declaredClass);
        
        String expected = "package au.com.illyrian;\n"
      		+ "import au.com.illyrian.jesub.ast.AstStructure, java.io.File;\n"
      		+ "public class Test";
        assertEquals("Wrong AstModule.toString", expected, module.toString());

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);
        
        assertEquals("Package name:", "au.com.illyrian", maker.getPackageName());
        assertEquals("Class name:", "Test", maker.getSimpleClassName());
        assertEquals("Fully Qualified Class name:", "au.com.illyrian.Test", maker.getFullyQualifiedClassName());
        DeclaredType declared1 = maker.findDeclaredType("AstStructure");
        DeclaredType declared2 = maker.findDeclaredType("File");
        assertNotNull("findDeclaredType:", declared1);
        assertNotNull("findDeclaredType:", declared2);
        assertEquals("Imported name expected:", "au.com.illyrian.jesub.ast.AstStructure", declared1.getName());
        assertEquals("Imported name expected:", "java.io.File", declared2.getName());
    }

    public void testDeclareClass() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Dot(build.Dot(
        		build.Dot(build.Dot(build.Name("au"), build.Name("com")), build.Name("illyrian")),
        		build.Name("jesub")), build.Name("ast"));
        
        // Imports
        AstExpression import1 = build.Dot(packageName, build.Name("AstStructure"));
        AstExpression import2 = build.Dot(build.Dot(build.Name("java"), build.Name("lang")), build.Name("Runnable"));
        AstExpressionLink imports = build.Link(import1, import2);
        
        AstExpression baseClass = build.Name("AstStructureBase");

        // Implements
        AstExpressionLink implementsList = build.Link(build.Name("AstStructure"), build.Name("Runnable"));

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, baseClass, implementsList, null);
        
        AstDeclareModule module = build.Module(packageName, imports, declareClass);

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
        
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, base, null, null);
        declareClass.add(var1);
        declareClass.add(var2);
        declareClass.add(var3);
        declareClass.add(var4);
        
        AstDeclareModule module = build.Module(packageName, null, declareClass);

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
        AstStructureLink params = build.Seq(build.Seq(paramA, paramB), paramC);
        AstStatementReturn body = build.Return(build.Div(build.Div(build.Name("a"), build.Name("b")), build.Name("c")));
        AstDeclareMethod method = build.Method(astPublic, type, build.Name("f"), params, body);

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName base = build.Name("au.com.illyrian.jesub.ast.FuncABC");
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, base, null, null);
        declareClass.add(method);
        
        // Declare Module
        AstDeclareModule module = build.Module(packageName, null, declareClass);

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
