package au.com.illyrian.jesub.ast;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.TerminalName;

public class AstStatementTreeTest extends ClassMakerTestCase
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
        
        AstDeclareMethod method = new AstDeclareMethod(publicModifier, type, name, paramN, code);
        return method;
    }
   
    public void testReturnStatement() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Name("au.com.illyrian.jesub.ast");

        AstExpression type = build.Name("int");
        AstModifiers astPublic = build.Modifier("public");

        // Declare method
        AstStructure formal1 = build.Declare(null, type, build.Name("x")); 
        AstStructure formal2 = build.Declare(null, type, build.Name("y")); 
        AstStructureLink params = build.Link(formal1, formal2);
        AstStatementReturn body = build.Return(build.Div(build.Name("x"), build.Name("y")));
        AstDeclareMethod method = build.Method(astPublic, type, build.Name("binary"), params, body);

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName impl = build.Name(Binary.class.getName());
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, null, null, null);
        declareClass.addImplements(impl);
        declareClass.addMember(method);
        
        // Declare Module
        AstDeclareModule module = build.Module(packageName, null, declareClass);

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Binary func = (Binary)instance;
        int result = func.binary(50, 5);
        assertEquals("binary(50, 5)", 10, result);
     }

    public void testIfBranch() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Name("au.com.illyrian.jesub.ast");

        AstExpression type = build.Name("int");
        AstModifiers astPublic = build.Modifier("public");
        
        // Declare variable
        AstStructure var1 = build.Declare(build.Modifier("public"), build.Name("int"), build.Name("id"));

        // Declare method
        AstStructure stmt1 = build.Eval(build.Assign(build.Name("id"), build.Literal(5)));
        AstExpression cond1 = build.NE(build.Name("x"), build.Name("y"));
        AstStructure stmt2 = build.Eval(build.Assign(build.Name("id"), build.Literal(2)));
        AstStructure stmt3 = build.If(cond1, stmt2, null);
        AstStructure stmt4 = build.Return(build.Name("id"));
        AstStructureLink body = build.Link(build.Link(stmt1, stmt3), stmt4);
        
        AstStructure formal1 = build.Declare(null, type, build.Name("x")); 
        AstStructure formal2 = build.Declare(null, type, build.Name("y")); 
        AstStructureLink params = build.Link(formal1, formal2);
        AstDeclareMethod method = build.Method(astPublic, type, build.Name("binary"), params, body);

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName impl = build.Name(Binary.class.getName());
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, null, null, null);
        declareClass.addImplements(impl);
        declareClass.addMember(var1);
        declareClass.addMember(method);
        
        // Declare Module
        AstDeclareModule module = build.Module(packageName, null, declareClass);

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(0, 1));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(5, 5));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(-1, -1));
    }

    public void testIfElseBranch() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Name("au.com.illyrian.jesub.ast");

        AstExpression type = build.Name("int");
        AstModifiers astPublic = build.Modifier("public");
        
        // Declare variable
        AstStructure var1 = build.Declare(build.Modifier("public"), build.Name("int"), build.Name("id"));

        // Declare method
        AstStructure stmt1 = build.Eval(build.Assign(build.Name("id"), build.Literal(5)));
        AstExpression cond1 = build.NE(build.Name("x"), build.Name("y"));
        AstStructure stmt2a = build.Eval(build.Assign(build.Name("id"), build.Literal(2)));
		AstStructure stmt2b = build.Eval(build.Assign(build.Name("id"), build.Literal(3)));
        AstStructure stmt3 = build.If(cond1, stmt2a, stmt2b);
        AstStructure stmt4 = build.Return(build.Div(build.Name("x"), build.Name("y")));
        AstStructureLink body = build.Link(build.Link(stmt1, stmt3), stmt4);
        
        AstStructure formal1 = build.Declare(null, type, build.Name("x")); 
        AstStructure formal2 = build.Declare(null, type, build.Name("y")); 
        AstStructureLink params = build.Link(formal1, formal2);
        AstDeclareMethod method = build.Method(astPublic, type, build.Name("binary"), params, body);

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName impl = build.Name(Binary.class.getName());
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, null, null, null);
        declareClass.addImplements(impl);
        declareClass.addMember(var1);
        declareClass.addMember(method);
        
        // Declare Module
        AstDeclareModule module = build.Module(packageName, null, declareClass);

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);

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
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Name("au.com.illyrian.jesub.ast");

        AstExpression type = build.Name("int");
        AstModifiers astPublic = build.Modifier("public");
        
        // Declare variable
        AstStructure var1 = build.Declare(build.Modifier("public"), build.Name("int"), build.Name("id"));

        // Declare method
        AstStructure stmt1 = build.Declare(null, type, build.Name("x"));
        AstStructure stmt2 = build.Eval(build.Assign(build.Name("x"), build.Literal(1)));
        AstExpression cond1 = build.GT(build.Name("n"), build.Literal(0));
        AstStructure stmt3a = build.Eval(build.Assign(build.Name("x"), build.Mult(build.Name("x"), build.Name("n"))));
        AstStructure stmt3b = build.Eval(build.Dec(build.Name("n")));
        AstStructure stmt3 = build.While(cond1, build.Link(stmt3a,  stmt3b));
        AstStructure stmt4 = build.Return(build.Name("x"));
        AstStructureLink body = build.Link(build.Link(build.Link(stmt1, stmt2), stmt3), stmt4);
        
        AstStructure formal1 = build.Declare(null, type, build.Name("n")); 
        AstDeclareMethod method = build.Method(astPublic, type, build.Name("unary"), formal1, body);

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName base = build.Name("Object");
        TerminalName impl = build.Name(Unary.class.getName());
        AstStructureLink members = build.Link(var1, method);
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, base, impl, members);
        
        // Declare Module
        AstDeclareModule module = build.Module(packageName, null, declareClass);

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Unary exec = (Unary)instance;

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));

        factorialTest(exec);
    }

    public void testForFactorial() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Name("au.com.illyrian.jesub.ast");

        AstExpression type = build.Name("int");
        AstModifiers astPublic = build.Modifier("public");
        
        // Declare variable
        AstStructure var1 = build.Declare(build.Modifier("public"), build.Name("int"), build.Name("id"));

        // Declare method
        AstStructure stmt1 = build.Declare(null, type, build.Name("x"));
        AstExpression expr2a = build.Assign(build.Name("x"), build.Literal(1));
        AstExpression cond2b = build.GT(build.Name("n"), build.Literal(0));
        AstExpression expr2c = build.Dec(build.Name("n"));
        AstStructure stmt3 = build.Eval(build.Assign(build.Name("x"), build.Mult(build.Name("x"), build.Name("n"))));
        AstStructure stmt2 = build.For(expr2a, cond2b, expr2c, stmt3);
        AstStructure stmt4 = build.Return(build.Name("x"));
        AstStructureLink body = build.Link(build.Link(stmt1, stmt2), stmt4);
        
        AstStructure formal1 = build.Declare(null, type, build.Name("n")); 
        AstDeclareMethod method = build.Method(astPublic, type, build.Name("unary"), formal1, body);

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName base = build.Name("Object");
        TerminalName impl = build.Name(Unary.class.getName());
        AstStructureLink members = build.Link(var1, method);
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, base, impl, members);
        
        // Declare Module
        AstDeclareModule module = build.Module(packageName, null, declareClass);

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);

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

    public void testSwitch() throws Exception
    {
        AstStructureFactory build = new AstStructureFactory();
        AstExpression packageName = build.Name("au.com.illyrian.jesub.ast");

        AstExpression intType = build.Name("int");
        AstModifiers astPublic = build.Modifier("public");
        
        // Declare method
        AstDeclareMethod method = build.Method(astPublic, intType, build.Name("unary"), null, null);
        method.addParameter(build.Declare(null, build.Name("int"), build.Name("x")));

      	method.addStatement(build.Declare(null, intType, build.Name("y")));
      	AstStatementSwitch stmt = build.Switch(build.Name("x"), null);
      	stmt.add(build.Case(build.Literal(0)));
      	stmt.add(build.Eval(build.Assign(build.Name("y"), build.Literal(1))));
      	stmt.add(build.Break());
      	stmt.add(build.Case(build.Literal(2)));
      	stmt.add(build.Eval(build.Assign(build.Name("y"), build.Literal(2))));
      	stmt.add(build.Break());
      	stmt.add(build.Case(build.Literal(4)));
      	stmt.add(build.Eval(build.Assign(build.Name("y"), build.Literal(3))));
      	stmt.add(build.Break());
      	stmt.add(build.Case(build.Literal(6)));
      	stmt.add(build.Eval(build.Assign(build.Name("y"), build.Literal(4))));
      	stmt.add(build.Break());
      	stmt.add(build.Default());
      	stmt.add(build.Eval(build.Assign(build.Name("y"), build.Literal(0))));
      	stmt.add(build.Break());
      	method.addStatement(stmt);;
      	method.addStatement(build.Return(build.Name("y")));

        // Declare Class
        AstModifiers modifiers = build.Modifier("public");
        TerminalName name = build.Name("Test");
        TerminalName impl = build.Name(Unary.class.getName());
        AstDeclareClass declareClass = build.DeclareClass(modifiers, name, null, null, null);
        declareClass.addImplements(impl);
        declareClass.addMember(method);
        
        // Declare Module
        AstDeclareModule module = build.Module(packageName, null, declareClass);

        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        module.resolveDeclaration(visitor);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Unary exec = (Unary)instance;

        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(-1));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 4, exec.unary(6));
    }

    /*  
    public void unknownVariableTest()
    {
    	// FIXME test local and member variables
    }
*/
    }
