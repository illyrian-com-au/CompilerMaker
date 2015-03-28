package au.com.illyrian.classmaker.test;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerBase;

public class SyncCounterTest extends TestCase 
{
    public static class CounterBase {
        protected long count = 0;
        
        public void add(long value)
        {
            long i = count + value;
            // Outputting a dot provides maximum opportunity for a synchronization problem.
            // If another thread enters at this point the count will be wrong.
            System.out.print(".");
            count = i;
        }
    }
    
    public static class CounterSync extends CounterBase 
    {
        // Synchronize the method.
        public synchronized void add(long value) { super.add(value); }
    }
    
    public static class CounterThread extends Thread
    {
        final CounterBase counter;
        final int max;
        CounterThread(CounterBase counter, int max)
        {
            this.counter = counter;
            this.max = max;
        }
        
        public void run()
        {
            for (int i=0; i<max; i++)
                counter.add(i);
        }
    }
    
    public void testSum()
    {
        long count = 0;
        for (int i=0; i<5; i++)
            count += i;
        assertEquals("Wrong count", 10, count);
    }
    
    public void testCounter() throws Exception
    {
        System.out.print("SingleCounter ");
        CounterBase counter = new CounterBase();
        CounterThread thread = new CounterThread(counter, 5);
        thread.start();
        thread.join();
        System.out.println(counter.count);
        assertEquals("Wrong count", 10, counter.count);
    }
    
    public void testTwoCounterFail() throws Exception
    {
        // No synchronization - sometimes works, mostly fails.
        System.out.print("TwoCounterFail ");
        CounterBase counter = new CounterBase();
        CounterThread thread1 = new CounterThread(counter, 5);
        CounterThread thread2 = new CounterThread(counter, 5);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(counter.count);
    }
    
    public void testTwoCounterSync() throws Exception
    {
        // Two threads each add 10 to counter if synchronized.
        System.out.print("TwoCounterSync ");
        CounterSync counter = new CounterSync();
        CounterThread thread1 = new CounterThread(counter, 5);
        CounterThread thread2 = new CounterThread(counter, 5);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(counter.count);
        assertEquals("Wrong count", 20, counter.count);
    }
    
    public static class CounterMaker extends ClassMakerBase
    {
        public void code()
        {
            Extends(CounterBase.class);
            Method("add", void.class, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_SYNCHRONIZED);
            Declare("value", long.class, 0);
            Begin();
            {
                Declare("i", long.class, 0);
                Eval(Assign("i", Add(Get(This(), "count"), Get("value"))));
                Call(Get("System", "out"), "print", Push(Literal(".")));
                Eval(Assign(This(), "count", Get("i")));
                Return();
            }
            End();
        }
    }
   
    public void testSyncAddMaker() throws Exception
    {
        CounterMaker maker = new CounterMaker();
        Class myClass = maker.defineClass();
        CounterBase counter = (CounterBase)myClass.newInstance();

        // Two threads each add 10 to counter if synchronized.
        System.out.print("TwoCounterMaker ");
        CounterThread thread1 = new CounterThread(counter, 5);
        CounterThread thread2 = new CounterThread(counter, 5);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(counter.count);
        assertEquals("Wrong count", 20, counter.count);
    }
}
