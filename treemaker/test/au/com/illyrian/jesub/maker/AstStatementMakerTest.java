package au.com.illyrian.jesub.maker;

import java.io.IOException;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
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
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Implements(build.Name(Binary.class.getName()));
        TerminalName type = build.Name("int");
        build.Method(build.Modifier("public"), type, build.Name("binary"));
        
        build.Declare(type, build.Name("x"));
        build.Declare(type, build.Name("y"));

        // Method body
        build.Begin();
        {
            build.Return(build.Div(build.Name("x"), build.Name("y")));
        }
        build.End();

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
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Implements(build.Name(Binary.class.getName()));
        
        TerminalName type = build.Name("int");
        build.Modifier("public");
        build.Declare(type, build.Name("id"));

        build.Method(build.Modifier("public"), type, build.Name("binary"));
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
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Implements(build.Name(Binary.class.getName()));
        
        TerminalName type = build.Name("int");
        build.Declare(build.Modifier("public"), type, build.Name("id"));

        build.Method(build.Modifier("public"), type, build.Name("binary"));
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
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Implements(build.Name(Unary.class.getName()));
        
        TerminalName type = build.Name("int");
        build.Modifier("public");
        build.Declare(type, build.Name("id"));

        build.Method(build.Modifier("public"), type, build.Name("unary"));
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

    public void testBreakContinue() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Implements(build.Name(Unary.class.getName()));

        build.Method(build.Modifier("public"), build.Name("int"), build.Name("unary"));
        build.Declare(build.Name("int"), build.Name("n"));
        build.Begin();
        {
	        build.Declare(build.Name("int"), build.Name("x"));
	        build.For(build.Assign(build.Name("x"), build.Literal(0)),null, null);
	        {
		        build.If(build.LE(build.Name("n"), build.Literal(0)));
		        {
		            build.Break();
		        }
		        build.EndIf();
		        // if (n%2 != 0){--n; continue;}
		        build.If(build.NE(build.Rem(build.Name("n"), build.Literal(2)), build.Literal(0)));
		        {
		        	build.Eval(build.Dec(build.Name("n")));
		        	build.Continue();
		        }
		        build.EndIf();
		        build.Eval(build.Assign(build.Name("x"), build.Add(build.Name("x"), build.Name("n"))));
		        build.Eval(build.Dec(build.Name("n")));
	        }
	        build.EndFor();
	        build.Return(build.Name("x"));
        }
        build.End();

        Class myClass = maker.defineClass();
        Unary exec = (Unary)myClass.newInstance();

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 6, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 12, exec.unary(6));
    }

    public void testSwitch() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.ast"));
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Implements(build.Name(Unary.class.getName()));
        
        TerminalName intType = build.Name("int");
        build.Modifier("public");
        build.Declare(intType, build.Name("id"));

        build.Method(build.Modifier("public"), intType, build.Name("unary"));
        build.Declare(intType, build.Name("x"));

        // Method body
        build.Begin();
        {
        	build.Declare(intType, build.Name("y"));
        	build.Switch(build.Name("x"));
        	build.Case(build.Literal(0));
        	    build.Eval(build.Assign(build.Name("y"), build.Literal(1)));
        	    build.Break();
            build.Case(build.Literal(2));
        	    build.Eval(build.Assign(build.Name("y"), build.Literal(2)));
        	    build.Break();
            build.Case(build.Literal(4));
        	    build.Eval(build.Assign(build.Name("y"), build.Literal(3)));
        	    build.Break();
            build.Case(build.Literal(6));
        	    build.Eval(build.Assign(build.Name("y"), build.Literal(4)));
        	    build.Break();
            build.Default();
        	    build.Eval(build.Assign(build.Name("y"), build.Literal(0)));
        	    build.Break();
        	build.EndSwitch();
            build.Return(build.Name("y"));
        }
        build.End();

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Unary exec = (Unary)instance;

        assertEquals("Wrong value for exec.unary()", 0, exec.unary(-1));
        assertEquals("Wrong value for exec.unary()", 1, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 2, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(3));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(4));
        assertEquals("Wrong value for exec.unary()", 0, exec.unary(5));
        assertEquals("Wrong value for exec.unary()", 4, exec.unary(6));
    }
    
    public static class Unreliable {
    	public int f(int a) throws IOException
    	{
    		if (a < 0)
    			throw new IllegalStateException("Exception thrown as part of test");
    		else if (a == 0)
    			throw new IOException("Exception thrown as part of test");
    		else
    			return a;
    	}
    }

    public void testTryCatchFinally() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.ast"));
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Extends(build.Name(Unreliable.class.getName()));
        build.Implements(build.Name(Unary.class.getName()));
        
        build.Modifier("public");
        build.Declare(build.Name("int"), build.Name("id"));

        build.Method(build.Modifier("public"), build.Name("int"), build.Name("unary"));
        build.Declare(build.Name("int"), build.Name("x"));

        // Method body
        build.Begin();
        {
            build.Declare(build.Name("int"), build.Name("y"));
        	build.Try(); 
        	{
        	    build.Eval(build.Call(build.Name("f"), build.Comma(build.Name("x"))));
        	    build.Eval(build.Assign(build.Name("y"), build.Name("x")));
        	} build.Catch(build.Name(IOException.class.getName()), build.Name("ex1")); {
        	    build.Eval(build.Assign(build.Name("y"), build.Literal(-10)));
        	} build.Catch(build.Name("IllegalStateException"), build.Name("ex2")); {
        	    build.Eval(build.Assign(build.Name("y"), build.Literal(-100)));
        	} build.Finally(); {
        		build.Eval(build.Inc(build.Name("y")));
        	}
        	build.EndTry();
            build.Return(build.Name("y"));
        }
        build.End();

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Unary exec = (Unary)instance;

        assertEquals("Wrong value for exec.unary()", 2, exec.unary(1));
        assertEquals("Wrong value for exec.unary()", 3, exec.unary(2));
        assertEquals("Wrong value for exec.unary()", -9, exec.unary(0));
        assertEquals("Wrong value for exec.unary()", -99, exec.unary(-1));
    }

    public interface BreakContinueIface {
    	int breakContinue(int i);
    }

    public void testBreakContinueLoop() throws Exception
    {
        AstStructureFactoryMaker build = new AstStructureFactoryMaker(maker);
        build.Package(build.Name("au.com.illyrian.jesub.ast"));
        build.ClassName(build.Modifier("public"), build.Name("Test"));
        build.Implements(build.Name(BreakContinueIface.class.getName()));
        
        TerminalName intType = build.Name("int");
        build.Method(build.Modifier("public"), intType, build.Name("breakContinue"));
        build.Declare(intType, build.Name("i"));

        // Method body
        build.Begin();
        {
        	build.Declare(intType, build.Name("n"));
        	build.Eval(build.Assign(build.Name("n"), build.Literal(0)));
        	build.Declare(intType, build.Name("j"));
        	build.Begin().setLabel("outer");
        	{
        		build.Try().setLabel("trying");
        		{
        			build.For(build.Assign(build.Name("j"), build.Literal(1)),
        				build.LE(build.Name("j"), build.Literal(3)),
        				build.Inc(build.Name(("j")))).setLabel("loop");
        			{
        				build.If(build.NE(build.Name("i"), build.Literal(0))).setLabel("branch");
        				{
        					build.Switch(build.Name("i")).setLabel("switch");
        					{
        						build.Case(build.Literal(1));
        						build.Break();

        						build.Case(build.Literal(2));
        						build.Continue();

        						build.Case(build.Literal(3));
	        	    			build.Break(build.Name("loop"));

	        	    			build.Case(build.Literal(4));
	        	    			build.Continue(build.Name("loop"));

	        	    			build.Case(build.Literal(5));
	        	    			build.Break(build.Name("switch"));

	        	    			build.Case(build.Literal(6));
	        	    			build.Break(build.Name("trying"));

	        	    			build.Case(build.Literal(7));
	        	    			build.Break(build.Name("outer"));

	        	    			build.Case(build.Literal(8));
	        	    		    build.Break(build.Name("branch"));
        					}
	        	    		build.EndSwitch();
	        	    		build.Eval(build.Inc(build.Name("n")));
        				}
        				build.Else();
        				{
  		        	    	build.Eval(build.Assign(build.Name("n"), build.Literal(100)));
        				}
	        	    	build.EndIf();
        			}
        			build.EndFor();
        			build.Eval(build.Assign(build.Name("n"), build.Add(build.Name("n"), build.Name("i"))));
        		}
        		build.Finally();
        		{
        			build.Eval(build.Inc(build.Name("n")));
        		}
        		build.EndTry();
        	}
        	build.End();
        	build.Return(build.Name("n"));
        }
        build.End(); // Method


        Class testClass = maker.defineClass();
        BreakContinueIface exec = (BreakContinueIface)testClass.newInstance();

        assertEquals("Else", 101, exec.breakContinue(0));
    	assertEquals("Break", 5, exec.breakContinue(1));
    	assertEquals("Continue", 3, exec.breakContinue(2));
    	assertEquals("Break loop", 4, exec.breakContinue(3));
    	assertEquals("Continue loop", 5, exec.breakContinue(4));
    	assertEquals("Break switch", 9, exec.breakContinue(5));
    	assertEquals("Break trying", 1, exec.breakContinue(6));
    	assertEquals("Break outer", 0, exec.breakContinue(7));        
    	assertEquals("Break branch", 9, exec.breakContinue(8));
    	assertEquals("Default", 14, exec.breakContinue(10));        
    }
}
