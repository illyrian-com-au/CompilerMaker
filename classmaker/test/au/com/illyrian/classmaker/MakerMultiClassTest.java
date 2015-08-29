package au.com.illyrian.classmaker;

import org.mozilla.classfile.ByteCode;

public class MakerMultiClassTest extends ClassMakerTestCase implements ByteCode
{
    public interface Getter {
        public int getValue();
    }
    public interface Setter {
        public void setValue(int i);
    }
    
    int[] TWO_PASS = {ClassMaker.FIRST_PASS, ClassMaker.SECOND_PASS};

    /*
     * The base class is the same for all test routines.
     * class Base {
     *     int val;
     *     public Base()
     *     {
     *         super();
     *         val = 9;
     *     }
     * }
     */
    private void codeBase(ClassMaker maker)
    {
        maker.Declare("val", int.class, ClassMaker.ACC_PUBLIC);
        
        maker.Method(ClassMaker.INIT, ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC);
        maker.Begin();
            maker.Init(maker.Super(), null);
            maker.Eval(maker.Assign(maker.This(), "val", maker.Literal(9)));
            maker.Return();
        maker.End();
        
        maker.EndClass();
    }

    public void testInitClass() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker initMaker = factory.createClassMaker("test", "Init", null);

        for (int pass : TWO_PASS)
        {
            factory.setPass(pass);
            codeBase(initMaker);
        }
        
        Class initClass = initMaker.defineClass();
        Object initObj = initClass.newInstance();
        
        assertEquals("initClass.val", 9, getIntField(initClass, initObj, "val"));
    }
    
    public void testForwardDeclaredLocal() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MakerValue", null);
        ClassMaker initMaker = factory.createClassMaker("test", "Init", null);

        for (int pass : TWO_PASS)
        {
            factory.setPass(pass);
            maker.Implements(Getter.class);
            maker.Method("getValue", int.class, ClassMaker.ACC_PUBLIC);
            maker.Begin();
            {
                maker.Declare("init", "Init", 0);
                maker.Eval(maker.Assign("init", maker.New("Init").Init(null)));
                maker.Return(maker.Get(maker.Get("init"), "val"));
            }
            maker.End();
            maker.EndClass();
            codeBase(initMaker);
        }
        
        Class myClass = maker.defineClass();
        initMaker.defineClass();
        Getter value =  (Getter)myClass.newInstance();
        
        assertEquals("value.getValue()", 9, value.getValue());
    }
    
    public void testForwardDeclaredParameter() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MakerValue", null);
        ClassMaker initMaker = factory.createClassMaker("test", "Init", null);

        for (int pass : TWO_PASS)
        {
            factory.setPass(pass);
            maker.Implements(Getter.class);
            maker.Method("getValue", int.class, ClassMaker.ACC_PUBLIC);
            maker.Begin();
            {
                maker.Declare("obj", "Init", 0);
                maker.Eval(maker.Assign("obj", maker.New("Init").Init(null)));
                maker.Return(maker.Call(maker.This(), "getValue", maker.Push(maker.Get("obj"))));
            }
            maker.End();
            maker.Method("getValue", int.class, ClassMaker.ACC_PUBLIC);
            maker.Declare("init", "Init", 0);
            maker.Begin();
            {
                maker.Return(maker.Get(maker.Get("init"), "val"));
            }
            maker.End();
            maker.EndClass();
            codeBase(initMaker);
        }
        
        Class myClass = maker.defineClass();
        initMaker.defineClass();
        Getter value =  (Getter)myClass.newInstance();
        
        assertEquals("value.getValue()", 9, value.getValue());
    }
    
    public void testForwardDeclaredResult() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MakerValue", null);
        ClassMaker initMaker = factory.createClassMaker("test", "Init", null);

        for (int pass : TWO_PASS)
        {
            factory.setPass(pass);
            maker.Implements(Getter.class);
            // Method to create an instance of class Init
            maker.Method("getInit", "Init", ClassMaker.ACC_PUBLIC);
            maker.Begin();
                maker.Return(maker.New("Init").Init(null));
            maker.End();
            // Method to get the value from an instance of class Init
            maker.Method("getValue", int.class, ClassMaker.ACC_PUBLIC);
            maker.Begin();
                maker.Return(maker.Get(maker.Call(maker.This(), "getInit", null), "val"));
            maker.End();
            maker.EndClass();
            // Declare class Init
            codeBase(initMaker);
        }
        
        Class myClass = maker.defineClass();
        initMaker.defineClass();

        Getter value =  (Getter)myClass.newInstance();
        assertEquals("value.getValue()", 9, value.getValue());
    }
    
    public void testForwardDeclaredField() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MakerValue", null);
        ClassMaker initMaker = factory.createClassMaker("test", "Init", null);

        for (int pass : TWO_PASS)
        {
            factory.setPass(pass);
            maker.Implements(Getter.class);
            // Constructor initialises the init field with an instance of class Init.
            maker.Method(ClassMaker.INIT, ClassMaker.VOID_TYPE, ClassMaker.ACC_PUBLIC);
            maker.Begin(); 
            {
                maker.Init(maker.Super(), null);
                maker.Eval(maker.Assign(maker.This(), "init", maker.New("Init").Init(null)));
                maker.Return();
            } 
            maker.End();
            // Gets the value from the init instance.
            maker.Method("getValue", int.class, ClassMaker.ACC_PUBLIC);
            maker.Begin();
            {
                maker.Return(maker.Get(maker.Get(maker.This(), "init"), "val"));
            }
            maker.End();
            maker.Declare("init", "Init", ACC_PUBLIC);
            maker.EndClass();
            // Declare class Init
            codeBase(initMaker);
        }
        
        Class myClass = maker.defineClass();
        initMaker.defineClass();

        Getter value =  (Getter)myClass.newInstance();
        assertEquals("value.getValue()", 9, value.getValue());
    }
    
    public void testForwardDeclaredExtends() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "MakerValue", null);
        ClassMaker initMaker = factory.createClassMaker("test", "Init", null);

        for (int pass : TWO_PASS)
        {
            factory.setPass(pass);
            maker.Extends("Init");
            maker.Implements(Getter.class);
            maker.Method("getValue", int.class, ClassMaker.ACC_PUBLIC);
            maker.Begin();
                maker.Return(maker.Get(maker.This(), "val"));
            maker.End();
            maker.EndClass();
            codeBase(initMaker);
        }

        // Test implicit loading of base class
        // initMaker.defineClass();
        Class myClass = maker.defineClass();
        
        Getter value =  (Getter)myClass.newInstance();
        assertEquals("value.getValue()", 9, value.getValue());
    }
    
    public void testForwardDeclaredImplements() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("", "MakerValue", null);
        ClassMaker initMaker = factory.createClassMaker("", "Init", null);
        ClassMaker ifaceMaker = factory.createClassMaker("", "Initialiser", null);

        for (int pass : TWO_PASS)
        {
            factory.setPass(pass);
            // class MakerValue
            maker.Implements(Getter.class);
            maker.Method("getValue", int.class, ClassMaker.ACC_PUBLIC);
            maker.Begin();
            {
                maker.Return(maker.Call(maker.New("Init").Init(null), "setValue", maker.Push(maker.Literal(5))));
            }
            maker.End();
            maker.EndClass();
            
            // class Init
            initMaker.Implements("Initialiser");
            initMaker.Method("setValue", int.class, ACC_PUBLIC);
            initMaker.Declare("value", int.class, 0);
            initMaker.Begin();
            {
                initMaker.Return(initMaker.Assign(initMaker.This(), "val", initMaker.Get("value")));
            }
            initMaker.End();
            codeBase(initMaker);
            
            // interface Initialiser
            ifaceMaker.setClassModifiers(ClassMaker.ACC_INTERFACE);
            ifaceMaker.Method("setValue", int.class, ACC_PUBLIC | ACC_ABSTRACT);
            ifaceMaker.Declare("value", int.class, 0);
            ifaceMaker.Forward();
            ifaceMaker.EndClass();
        }

        // Test implicit loading of interface
        //ifaceMaker.defineClass();
        initMaker.defineClass();
        Class myClass = maker.defineClass();
        
        Getter value =  (Getter)myClass.newInstance();
        assertEquals("value.getValue()", 5, value.getValue());
    }
    
}
