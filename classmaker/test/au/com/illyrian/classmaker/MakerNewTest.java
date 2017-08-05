package au.com.illyrian.classmaker;

import org.mozilla.classfile.ByteCode;

public class MakerNewTest extends ClassMakerTestCase implements ByteCode
{

    public void setUp() throws Exception
    {
    }
    
    public interface Construct {
        Object create();
    }

    public interface Unary {
        public int eval(int i);
    }
    
    public static class JavaFactorial implements Unary
    {
        public int eval(int num)
        {
            int num_aux;
            if (num < 1)
                num_aux = 1;
            else
                num_aux = num * eval(num-1);
            return num_aux;
        }
    }
    
    private void mainCode(ClassMaker maker, String factorialName)
    {
        maker.Implements(Unary.class);
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Declare("i",  int.class, 0);
        maker.Begin();
          maker.Return(maker.Call((maker.New(factorialName).Init(null)), "eval", maker.Push(maker.Get("i"))));
        maker.End();

        maker.EndClass();
    }

    /* Factorial class is declared as a variable before it is defined. */
    private void mainCodeForward(ClassMaker maker, String factorialName)
    {
        maker.Implements(Unary.class);
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Declare("i",  int.class, 0);
        maker.Begin();
          maker.Declare("fac", factorialName, 0);
          maker.Assign("fac", maker.New(factorialName).Init(null));
          maker.Return(maker.Call(maker.Get("fac"), "eval", maker.Push(maker.Get("i"))));
        maker.End();

        maker.EndClass();
    }

    public void factorialCode(ClassMaker maker)
    {
       // maker.Implements(Unary.class);
        maker.Method("eval", int.class, ACC_PUBLIC);
        maker.Declare("num", int.class, 0);
        maker.Begin();
            maker.Declare("num_aux", int.class, 0);
            maker.If(maker.LT(maker.Get("num"), maker.Literal(1)));
                maker.Assign("num_aux", maker.Literal(1));
            maker.Else();
                maker.Assign("num_aux", maker.Mult(maker.Get("num"),
                        maker.Call(maker.This(), "eval",
                                maker.Push(maker.Subt(maker.Get("num"), maker.Literal(1))))));
            maker.EndIf();
            maker.Return(maker.Get("num_aux"));
        maker.End();

        maker.EndClass();
    }
    

    public void testNewInstance() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        maker.Implements(Construct.class);

        maker.Method("create", Object.class, ACC_PUBLIC);
        maker.Begin();
          maker.Return(maker.New(String.class).Init(maker.Push(maker.Literal("Hello World"))));
        maker.End();

        Class myClass = maker.defineClass();
        Construct exec =  (Construct)myClass.newInstance();

        Object result = exec.create();
        assertNotNull("Result was null", result);
        assertEquals("Wrong type", "java.lang.String", result.getClass().getName());
        assertEquals("Wrong message", "Hello World", result.toString());
   }

    public void testMainCode() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
        mainCode(maker, "au.com.illyrian.classmaker.MakerNewTest$JavaFactorial");

        Class myClass = maker.defineClass();
        Unary exec =  (Unary)myClass.newInstance();

        assertEquals("Factorial(0)", 1, exec.eval(0));
        assertEquals("Factorial(1)", 1, exec.eval(1));
        assertEquals("Factorial(2)", 2, exec.eval(2));
        assertEquals("Factorial(3)", 6, exec.eval(3));
        assertEquals("Factorial(4)", 24, exec.eval(4));
   }
    
    public void testFactorialCode() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "GenFactorial1", "GenFactorial1.java");
        maker.Implements(Unary.class);
        factorialCode(maker);

        Class myClass = maker.defineClass();
        Unary exec =  (Unary)myClass.newInstance();

        assertEquals("Factorial(0)", 1, exec.eval(0));
        assertEquals("Factorial(1)", 1, exec.eval(1));
        assertEquals("Factorial(2)", 2, exec.eval(2));
        assertEquals("Factorial(3)", 6, exec.eval(3));
        assertEquals("Factorial(4)", 24, exec.eval(4));
   }
    
    public void testTwoClassOnePass() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        // new Fac().ComputeFac(10)
        ClassMaker maker = factory.createClassMaker("test", "MainClass2", null);
        ClassMaker factMaker = factory.createClassMaker("test", "GenFactorial2", null);
        maker.toString();

        factorialCode(factMaker);
        mainCode(maker, "GenFactorial2");
        
        factMaker.defineClass();
        Class myClass = maker.defineClass();
        Unary exec =  (Unary)myClass.newInstance();

        assertEquals("Factorial(0)", 1, exec.eval(0));
        assertEquals("Factorial(1)", 1, exec.eval(1));
        assertEquals("Factorial(2)", 2, exec.eval(2));
        assertEquals("Factorial(3)", 6, exec.eval(3));
        assertEquals("Factorial(4)", 24, exec.eval(4));
   }

    public void testTwoClassTwoPass() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        // new Fac().ComputeFac(10)
        ClassMaker maker = factory.createClassMaker("test", "MainClass3", null);
        ClassMaker factMaker = factory.createClassMaker("test", "GenFactorial3", null);

        factory.setPass(ClassMakerConstants.FIRST_PASS);
        mainCode(maker, "GenFactorial3");
        factorialCode(factMaker);
        
        factory.setPass(ClassMakerConstants.SECOND_PASS);
        mainCode(maker, "GenFactorial3");
        factorialCode(factMaker);
        
        factMaker.defineClass();
        Class myClass = maker.defineClass();
        Unary exec =  (Unary)myClass.newInstance();

        assertEquals("Factorial(0)", 1, exec.eval(0));
        assertEquals("Factorial(1)", 1, exec.eval(1));
        assertEquals("Factorial(2)", 2, exec.eval(2));
        assertEquals("Factorial(3)", 6, exec.eval(3));
        assertEquals("Factorial(4)", 24, exec.eval(4));
   }

    public void testTwoClassTwoPassForward() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        // new Fac().ComputeFac(10)
        ClassMaker maker = factory.createClassMaker("test", "MainClass4", null);
        ClassMaker factMaker = factory.createClassMaker("test", "GenFactorial4", null);

        factory.setPass(ClassMakerConstants.FIRST_PASS);
        mainCodeForward(maker, "GenFactorial4");
        factorialCode(factMaker);
        
        factory.setPass(ClassMakerConstants.SECOND_PASS);
        mainCodeForward(maker, "GenFactorial4");
        factorialCode(factMaker);
        
        factMaker.defineClass();
        Class myClass = maker.defineClass();
        Unary exec =  (Unary)myClass.newInstance();

        assertEquals("Factorial(0)", 1, exec.eval(0));
        assertEquals("Factorial(1)", 1, exec.eval(1));
        assertEquals("Factorial(2)", 2, exec.eval(2));
        assertEquals("Factorial(3)", 6, exec.eval(3));
        assertEquals("Factorial(4)", 24, exec.eval(4));
   }

}
