package au.com.illyrian.jesub.ast;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.ast.ResolvePath;
import au.com.illyrian.classmaker.ast.TerminalName;

public class AstStatementMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();
    AstExpressionFactory expr;

    public void setUp()
    {
        expr = new AstExpressionFactory(maker);
    }

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

    AstDeclareMethod binaryMethod(AstStructureList code) throws Exception
    {
        AstModifiers publicModifier = new AstModifiers("public", null);
        ResolvePath type = new TerminalName("int");
        TerminalName name = new TerminalName("binary");
        TerminalName x = new TerminalName("x");
        TerminalName y = new TerminalName("y");
        AstDeclareVariable paramX = new AstDeclareVariable(null, type, x);
        AstDeclareVariable paramY = new AstDeclareVariable(null, type, y);
        AstStructureList parameters = new AstStructureList();
        parameters.add(paramX);
        parameters.add(paramY);
        
        AstDeclareMethod method = new AstDeclareMethod(publicModifier, type, name, parameters, code);
        return method;
    }
    
    AstDeclareMethod unaryMethod(AstStructureList code) throws Exception
    {
        AstModifiers publicModifier = new AstModifiers("public", null);
        ResolvePath type = new TerminalName("int");
        TerminalName name = new TerminalName("unary");
        TerminalName n = new TerminalName("n");
        AstDeclareVariable paramN = new AstDeclareVariable(null, type, n);
        AstStructureList parameters = new AstStructureList();
        parameters.add(paramN);
        
        AstDeclareMethod method = new AstDeclareMethod(publicModifier, type, name, parameters, code);
        return method;
    }
    
    public void testReturnStatement() throws Exception
    {
 //       createClass("Test", Binary.class);
//        public interface Binary
//        {
//            int binary(int x, int y);
//        }

        AstStructureFactory build = new AstStructureFactory();
        build.Package(build.ClassPath(build.Name("au.com.illyrian.jesub.ast")));
        build.Modifier("public");
        build.ClassName(build.Name("Test"));
        build.Implements(build.ClassPath(build.Name(Binary.class.getName())));
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

//        // Method body
//        expr.name("x");
//        expr.name("y");
//        expr.div();
//        AstStatementReturn ret = new AstStatementReturn(expr.peek());
//        AstStructureList code = new AstStructureList();;
//        code.add(ret);
//        AstDeclareMethod method = binaryMethod(code); 
//                
        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        build.getModule().resolveDeclaration(visitor);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Binary func = (Binary)instance;
        int result = func.binary(50, 5);
        assertEquals("binary(50, 5)", 10, result);
     }

    public void testIfBranch() throws Exception
    {
        maker.Implements(Binary.class);
        maker.Declare("id", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);

        maker.Method("binary", ClassMaker.INT_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", ClassMaker.INT_TYPE, 0);
        maker.Declare("b", ClassMaker.INT_TYPE, 0);
        maker.Begin();
          maker.Eval(maker.Set(maker.This(), "id", maker.Literal(5)));
          maker.If(maker.NE(maker.Get("a"), maker.Get("b")));
            maker.Eval(maker.Set(maker.This(), "id", maker.Literal(2)));
          maker.EndIf();
          maker.Return(maker.Get(maker.This(), "id"));
        maker.End();

        Class myClass = maker.defineClass();
        Binary exec = (Binary)myClass.newInstance();

        assertEquals("Wrong value for exec.eval()", 2, exec.binary(0, 1));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(5, 5));
        assertEquals("Wrong value for exec.eval()", 5, exec.binary(-1, -1));
    }

    public void testIfStatement() throws Exception
    {
        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        createClass("Test", Binary.class);
        
        AstModifiers publicModifier = new AstModifiers("public", null);
        ResolvePath type = new TerminalName("int");
        TerminalName id = new TerminalName("id");
        AstDeclareVariable publicId = new AstDeclareVariable(publicModifier, type, id);

        // Generate code for public member variable "id".
        publicId.resolveDeclaration(visitor);

        AstStructureList code = new AstStructureList();;
        // this.id = 5;
        expr.name("this");
        expr.name("id");
        expr.dot();
        expr.literal(5);
        expr.assign();
        AstStatementEval stmt1 = new AstStatementEval(expr.pop());
        code.add(stmt1);
        
        // (x != y)
        expr.name("x");
        expr.name("y");
        expr.ne();
        AstExpression condition = expr.pop();
        
        //     this.id = 2;
        expr.name("this");
        expr.name("id");
        expr.dot();
        expr.literal(2);
        expr.assign();
        AstStatementEval stmt3 = new AstStatementEval(expr.pop());

        // else
        //     this.id = 3;
        expr.name("this");
        expr.name("id");
        expr.dot();
        expr.literal(3);
        expr.assign();
        AstStatementEval stmt4 = new AstStatementEval(expr.pop());

        // if (condition) then stmt3 else stmt4;
        AstStatementIf stmt2 = new AstStatementIf(condition, stmt3, stmt4);
        code.add(stmt2);

        // return this.id;
        expr.name("x");
        expr.name("y");
        expr.div();
        AstStatementReturn ret = new AstStatementReturn(expr.pop());
        code.add(ret);
        AstDeclareMethod method = binaryMethod(code); 
        
        // Generate code for member method
        method.resolveDeclaration(visitor);

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
//        Method("unary", int.class, ClassMaker.ACC_PUBLIC);
//        Declare("n", int.class, 0);
//        Begin();
//          Declare("x", int.class, 0);
//          Eval(Set("x", Literal(1)));
//          Loop();
//            While(GT(Get("n"), Literal(0)));
//            Eval(Set("x", Mult(Get("x"), Get("n"))));
//            Eval(Dec("n"));
//          EndLoop();
//          Return(Get("x"));
//        End();
        AstStructureVisitor visitor = new AstStructureVisitor(maker);
        createClass("Test", Unary.class);
        
        AstStructureList code = new AstStructureList();;
        ResolvePath typeInt = new TerminalName("int");
        TerminalName nameX = new TerminalName("x");
        AstDeclareVariable declareX = new AstDeclareVariable(null, typeInt, nameX);
        code.add(declareX);

        // x = 5;
        expr.name("x");
        expr.literal(1);
        expr.assign();
        AstStatementEval stmt1 = new AstStatementEval(expr.pop());
        code.add(stmt1);
        
        // (n > 0)
        expr.name("n");
        expr.literal(0);
        expr.gt();
        AstExpression condition = expr.pop();
        
        //     x = x * n;
        expr.name("x");
        expr.name("x");
        expr.name("n");
        expr.mult();
        expr.assign();
        AstStatementEval stmt3 = new AstStatementEval(expr.pop());

        //     --n;
        expr.name("n");
        expr.dec();
        AstStatementEval stmt4 = new AstStatementEval(expr.pop());

        // while (condition) {stmt3; stmt4}
        AstStructureList nest1 = new AstStructureList();
        nest1.add(stmt3);
        nest1.add(stmt4);
        AstStatementWhile stmt2 = new AstStatementWhile(condition, nest1);
        code.add(stmt2);

        // return this.id;
        expr.name("x");
        AstStatementReturn ret = new AstStatementReturn(expr.pop());
        code.add(ret);
        AstDeclareMethod method = unaryMethod(code); 
        
        // Generate code for member method
        method.resolveDeclaration(visitor);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Unary func = (Unary)instance;

        factorialTest(func);
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
}
