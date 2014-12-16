package au.com.illyrian.jesub.maker;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.ResolvePath;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.jesub.ast.AstDeclareMethod;
import au.com.illyrian.jesub.ast.AstDeclareVariable;
import au.com.illyrian.jesub.ast.AstModifiers;
import au.com.illyrian.jesub.ast.AstStructureFactoryMaker;
import au.com.illyrian.jesub.ast.AstStructureLink;

public class AstStatementMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    public interface Eval
    {
        int eval();
    }

    public interface Unary
    {
        int unary(int a);
    }

    public interface UnaryObject
    {
        int unary(Object a);
    }

    public interface Binary
    {
        int binary(int x, int y);
    }

    void createClass(String name, Class impl) throws Exception
    {
        maker.setPackageName("au.com.illyrian.jesub.ast");
        maker.setClassModifiers(ClassMaker.ACC_PUBLIC);
        maker.setSimpleClassName(name);
        maker.Implements(impl);
    }

    AstDeclareMethod binaryMethod(AstStructureLink code) throws Exception
    {
        AstModifiers publicModifier = new AstModifiers("public", null);
        AstExpression type = new TerminalName("int");
        TerminalName name = new TerminalName("binary");
        TerminalName x = new TerminalName("x");
        TerminalName y = new TerminalName("y");
        AstDeclareVariable paramX = new AstDeclareVariable(null, type, x);
        AstDeclareVariable paramY = new AstDeclareVariable(null, type, y);
        AstStructureLink parameters = new AstStructureLink(paramX, paramY);
        
        AstDeclareMethod method = new AstDeclareMethod(publicModifier, type, name, parameters, code);
        return method;
    }
    
    AstDeclareMethod unaryMethod(AstStructureLink code) throws Exception
    {
        AstModifiers publicModifier = new AstModifiers("public", null);
        AstExpression type = new TerminalName("int");
        TerminalName name = new TerminalName("unary");
        TerminalName n = new TerminalName("n");
        AstDeclareVariable paramN = new AstDeclareVariable(null, type, n);
        AstStructureLink parameters = new AstStructureLink(null, paramN);
        
        AstDeclareMethod method = new AstDeclareMethod(publicModifier, type, name, parameters, code);
        return method;
    }
   
    public void testReturnStatement() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.ast"));
        build.Modifier("public");
        build.ClassName(build.Name("Test"));
        build.Implements(build.Name(Binary.class.getName()));
        TerminalName type = build.Name("int");
        build.Modifier("public");
        build.Method(type, build.Name("binary"));
        
        build.Declare(type, build.Name("x"));
        build.Declare(type, build.Name("y"));

        // Method body
        build.Begin();
        {
            build.Return(build.Div(build.Name("x"), build.Name("y")));
        }
        build.End();

//        AstStructureVisitor visitor = new AstStructureVisitor(maker);
//        build.getModule().resolveDeclaration(visitor);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Binary func = (Binary)instance;
        int result = func.binary(50, 5);
        assertEquals("binary(50, 5)", 10, result);
     }

    public void testIfBranch() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.ast"));
        build.Modifier("public");
        build.ClassName(build.Name("Test"));
        build.Implements(build.Name(Binary.class.getName()));
        
        TerminalName type = build.Name("int");
        build.Modifier("public");
        build.Declare(type, build.Name("id"));

        build.Modifier("public");
        build.Method(type, build.Name("binary"));
        build.Declare(type, build.Name("x"));
        build.Declare(type, build.Name("y"));

        // Method body
        build.Begin();
        {
        	build.Eval(build.Assign(build.Name("id"), build.Literal(5)));
        	build.If(build.NE(build.Name("x"), build.Name("y")));
        	    build.Eval(build.Assign(build.Name("id"), build.Literal(2)));
        	build.EndIf();
            build.Return(build.Name("id"));
        }
        build.End();

//        AstStructureVisitor visitor = new AstStructureVisitor(maker);
//        build.getModule().resolveDeclaration(visitor);

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(0, 1));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(5, 5));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(-1, -1));
    }

    public void testIfElseBranch() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.ast"));
        build.Modifier("public");
        build.ClassName(build.Name("Test"));
        build.Implements(build.Name(Binary.class.getName()));
        
        TerminalName type = build.Name("int");
        build.Modifier("public");
        build.Declare(type, build.Name("id"));

        build.Modifier("public");
        build.Method(type, build.Name("binary"));
        build.Declare(type, build.Name("x"));
        build.Declare(type, build.Name("y"));

        // Method body
        build.Begin();
        {
        	build.Eval(build.Assign(build.Name("id"), build.Literal(5)));
        	build.If(build.NE(build.Name("x"), build.Name("y")));
        	    build.Eval(build.Assign(build.Name("id"), build.Literal(2)));
        	build.Else();
        		build.Eval(build.Assign(build.Name("id"), build.Literal(3)));
        	build.EndIf();
            build.Return(build.Div(build.Name("x"), build.Name("y")));
        }
        build.End();

//        AstStructureVisitor visitor = new AstStructureVisitor(maker);
//        build.getModule().resolveDeclaration(visitor);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Binary func = (Binary)instance;

        // Test then branch
        int result = func.binary(50, 5);
        assertEquals("binary(50, 5)", 10, result);
        int valueId = getIntField(parserClass, instance, "id");
        assertEquals("field id: ", 2, valueId);

        // Test else branch
        result = func.binary(5, 5);
        assertEquals("binary(5, 5)", 1, result);
        valueId = getIntField(parserClass, instance, "id");
        assertEquals("field id: ", 3, valueId);
    }
    
    public void testWhileFactorial() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.ast"));
        build.Modifier("public");
        build.ClassName(build.Name("Test"));
        build.Implements(build.Name(Unary.class.getName()));
        
        TerminalName type = build.Name("int");
        build.Modifier("public");
        build.Declare(type, build.Name("id"));

        build.Modifier("public");
        build.Method(type, build.Name("unary"));
        build.Declare(type, build.Name("n"));

        // Method body
        build.Begin();
        {
            build.Declare(type, build.Name("x"));
        	build.Eval(build.Assign(build.Name("x"), build.Literal(1)));
            build.While(build.GT(build.Name("n"), build.Literal(0))); 
            {
            	build.Eval(build.Assign(build.Name("x"), build.Mult(build.Name("x"), build.Name("n"))));
            	build.Eval(build.Dec(build.Name("n")));
            }
            build.EndWhile();
            build.Return(build.Name("x"));
        }
        build.End();

//        AstStructureVisitor visitor = new AstStructureVisitor(maker);
//        build.getModule().resolveDeclaration(visitor);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Unary exec = (Unary)instance;

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));

        factorialTest(exec);
    }

    public void factorialTest(Unary exec) throws Exception
    {
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 24, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 120, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 720, exec.unary(6));
        assertEquals("Wrong value for exec.unary()", 5040, exec.unary(7));
        assertEquals("Wrong value for exec.unary()", 40320, exec.unary(8));
    }
  /*  
    public void unknownVariableTest()
    {
    	// FIXME test local and member variables
    }
*/
    }
