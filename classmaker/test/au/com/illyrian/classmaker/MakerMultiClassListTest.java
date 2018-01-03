package au.com.illyrian.classmaker;

import org.mozilla.classfile.ByteCode;

public class MakerMultiClassListTest extends ClassMakerTestCase implements ByteCode
{
    public interface Value {
        public int getValue();
    }
    public interface List {
        public void insert(int key);
        public Value find(int key);
    }
    
    public static class JavaList implements List
    {
        JavaLink stack;
        
        public JavaList()
        {
            stack = null;
        }
        
        public void insert(int key)
        {
            stack = insert(key, stack);
        }
        
        private JavaLink insert(int key, JavaLink list)
        {
            if (list != null && list.key <= key)
                list.next = insert(key, list.next);
            else
                list = new JavaLink(key, list);
            return list;
        }
        
        public Value find(int key)
        {
            JavaLink list;
            for (list = stack; list != null; list = list.next)
            {
                if (list.key == key)
                    return list;
                else if (list.key > key)
                    break;
            }
            return null;
        }
        
        public String toString()
        {
            return "List: " + stack;
        }
    }
    
    public static class JavaLink implements Value
    {
        int key;
        JavaLink next;
        
        public JavaLink(int key, JavaLink next)
        {
            this.key = key;
            this.next = next;
        }
        
        public int getValue() { return key; }
        
        public String toString()
        {
            if (next == null)
                return "" + key;
            else
                return key + ", " + next;
        }
    }
    
    // Test the java version of the linked list
    public void testJavaLinkList()
    {
        JavaList list = new JavaList();
        assertNull("Should not find 1", list.find(1));
        list.insert(1);
        assertNotNull("Should find 1", list.find(1));
        list.insert(3);
        assertNotNull("Should find 3", list.find(3));
        list.insert(9);
        assertNotNull("Should find 9", list.find(9));
        list.insert(7);
        list.insert(5);
        assertNotNull("Should find 1", list.find(1));
        assertNull("Should not find 2", list.find(2));
        assertNotNull("Should find 3", list.find(3));
        assertNull("Should not find 4", list.find(4));
        assertNotNull("Should find 5", list.find(5));
        assertNull("Should not find 6", list.find(6));
        assertNotNull("Should find 7", list.find(7));
        assertNull("Should not find 8", list.find(8));
        assertNotNull("Should find 9", list.find(9));
        assertNull("Should not find 10", list.find(10));
    }
    
    // Maker version of the linked list class
    private void codeList(ClassMaker maker, String entryName)
    {
        maker.Implements(List.class);
        
        maker.Declare("stack", entryName, 0);
        
        maker.Method(ClassMaker.INIT, ClassMakerFactory.VOID_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
        {
            maker.Init(maker.Super(), null);
            maker.Assign(maker.This(), "stack", maker.Null());
            maker.Return();
        }
        maker.End();

        maker.Method("insert", void.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("key", int.class, 0);
        maker.Begin();
        {
            //  this.stack = insert(key, this.stack);
            maker.Assign(maker.This(), "stack", maker.Call(maker.This(), "insert", 
                    maker.Push(maker.Get("key")).Push(maker.Get(maker.This(), "stack"))));
            maker.Return();
        }
        maker.End();

        maker.Method("insert", entryName, ClassMakerConstants.ACC_PRIVATE);
        maker.Declare("key", int.class, 0);
        maker.Declare("list", entryName, 0);
        maker.Begin();
        {
//            if (list != null && list.key <= key)
//                list.next = insert(key, list.next);
//            else
//                list = new JavaLink(key, list);
//            return list;
            maker.If(maker.Logic(maker.AndThen(maker.NE(maker.Get("list"), maker.Null())), 
                    maker.LE(maker.Get(maker.Get("list"), "key"), maker.Get("key"))));
            {
                maker.Eval(maker.Assign(maker.Get("list"), "next", maker.Call(maker.This(), "insert", 
                        maker.Push(maker.Get("key")).Push(maker.Get(maker.Get("list"), "next")))));
            }
            maker.Else();
            {
                maker.Eval(maker.Assign("list", maker.New(entryName)
                        .Init(maker.Push(maker.Get("key")).Push(maker.Get("list")))));
            }
            maker.EndIf();
            maker.Return(maker.Get("list"));
        }
        maker.End();

        maker.Method("find", Value.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("key", int.class, 0);
        maker.Begin();
        {
//            JavaLink list;
//            for (list = stack; list != null; list = list.next)
//            {
//                if (list.key == key)
//                    return list;
//                else if (list.key > key)
//                    break;
//            }
//            return null;
            maker.Declare("list", entryName, 0);
            maker.For(maker.Assign("list", maker.Get(maker.This(), "stack")))
                .While(maker.NE(maker.Get("list"), maker.Null()))
                .Step(maker.Assign("list", maker.Get(maker.Get("list"), "next"))); 
            {
                maker.If(maker.EQ(maker.Get(maker.Get("list"), "key"), maker.Get("key"))); {
                    maker.Return(maker.Get("list"));
                } maker.Else(); {
                    maker.If(maker.GT(maker.Get(maker.Get("list"), "key"), maker.Get("key"))); {
                        maker.Break();
                    } maker.EndIf();
                } maker.EndIf();
            } maker.EndFor();
            maker.Return(maker.Null());
        }
        maker.End();

        maker.EndClass();
    }

    public void codeEntry(ClassMaker maker, String entryName)
    {
        maker.Implements(Value.class);
        
        maker.Declare("key", int.class, 0);
        maker.Declare("next", entryName, 0);

        maker.Method(ClassMaker.INIT, ClassMakerFactory.VOID_TYPE, ClassMakerConstants.ACC_PUBLIC);
        maker.Declare("key", int.class, 0);
        maker.Declare("next", entryName, 0);
        maker.Begin();
        {
            maker.Init(maker.Super(), null);
            maker.Assign(maker.This(), "key",  maker.Get("key"));
            maker.Assign(maker.This(), "next",  maker.Get("next"));
            maker.Return();
        }
        maker.End();

        maker.Method("getValue", int.class, ClassMakerConstants.ACC_PUBLIC);
        maker.Begin();
        {
            maker.Return(maker.Get(maker.This(), "key"));
        }
        maker.End();
        maker.EndClass();
    }
    
    // Test maker version where the base class is declared first.
    // This makes sure that the code works when compiled in the correct order
    public void testListMakerOrdered() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        // new Fac().ComputeFac(10)
        ClassMaker listMaker = factory.createClassMaker("test", "MakerList", null);
        ClassMaker entryMaker = factory.createClassMaker("test", "MakerEntry", null);

        factory.setPass(ClassMakerConstants.FIRST_PASS);
        codeEntry(entryMaker, "MakerEntry");
        codeList(listMaker, "MakerEntry");
        
        factory.setPass(ClassMakerConstants.SECOND_PASS);
        codeEntry(entryMaker, "MakerEntry");
        codeList(listMaker, "MakerEntry");
        
        entryMaker.defineClass();
        Class myClass = listMaker.defineClass();
        List list =  (List)myClass.newInstance();

        assertNull("Should not find 1", list.find(1));
        list.insert(1);
        assertNotNull("Should find 1", list.find(1));
        list.insert(3);
        assertNotNull("Should find 3", list.find(3));
        list.insert(9);
        assertNotNull("Should find 9", list.find(9));
        list.insert(7);
        list.insert(5);
        assertNotNull("Should find 1", list.find(1));
        assertNull("Should not find 2", list.find(2));
        assertNotNull("Should find 3", list.find(3));
        assertNull("Should not find 4", list.find(4));
        assertNotNull("Should find 5", list.find(5));
        assertNull("Should not find 6", list.find(6));
        assertNotNull("Should find 7", list.find(7));
        assertNull("Should not find 8", list.find(8));
        assertNotNull("Should find 9", list.find(9));
        assertNull("Should not find 10", list.find(10));
   }

    // Test maker version where the base class is declared last.
    // This makes sure that the code works when classes are compiled in the wrong order.
    public void testListMakerReversed() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        // new Fac().ComputeFac(10)
        ClassMaker listMaker = factory.createClassMaker("test", "MakerList", null);
        ClassMaker entryMaker = factory.createClassMaker("test", "MakerEntry", null);

        factory.setPass(ClassMakerConstants.FIRST_PASS);
        codeList(listMaker, "MakerEntry");
        codeEntry(entryMaker, "MakerEntry");
        
        factory.setPass(ClassMakerConstants.SECOND_PASS);
        codeList(listMaker, "MakerEntry");
        codeEntry(entryMaker, "MakerEntry");
        
        entryMaker.defineClass();
        Class myClass = listMaker.defineClass();
        List list =  (List)myClass.newInstance();

        assertNull("Should not find 1", list.find(1));
        list.insert(1);
        assertNotNull("Should find 1", list.find(1));
        list.insert(3);
        assertNotNull("Should find 3", list.find(3));
        list.insert(9);
        assertNotNull("Should find 9", list.find(9));
        list.insert(7);
        list.insert(5);
        assertNotNull("Should find 1", list.find(1));
        assertNull("Should not find 2", list.find(2));
        assertNotNull("Should find 3", list.find(3));
        assertNull("Should not find 4", list.find(4));
        assertNotNull("Should find 5", list.find(5));
        assertNull("Should not find 6", list.find(6));
        assertNotNull("Should find 7", list.find(7));
        assertNull("Should not find 8", list.find(8));
        assertNotNull("Should find 9", list.find(9));
        assertNull("Should not find 10", list.find(10));
   }

}
