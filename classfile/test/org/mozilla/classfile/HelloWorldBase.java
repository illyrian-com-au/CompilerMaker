package org.mozilla.classfile;

public class HelloWorldBase implements Runnable
{
    public int id = 0;

    public HelloWorldBase()
    {
        System.out.println("HelloWorldBase.<init>: Hello World");
    }

    public void run()
    {
        id = 2;
    }
}
