// Copyright (c) 2010, Donald Strong.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.

package au.com.illyrian.classmaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mozilla.classfile.SimpleClassLoader;

import junit.framework.TestCase;

public class ExampleClassesTest extends TestCase
{
    protected void setUp() throws Exception {
        // Use separate factories to avoid duplicates in class loaded.
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMakerBase.setSharedFactory(factory);
    }
    
    // BEGIN Import Example 1
    public interface Openable
    {
        FileOutputStream open(File file) throws IOException;
    }

    public static class OpenClass implements Openable
    {
        public FileOutputStream open(File file) throws IOException
        {
            FileOutputStream output;
            if (file.exists())
                output = new FileOutputStream(file);
            else
                throw new IOException("File not found");
            return output;
        }
    }

    // END Import Example 1

    public void testJavaImport() throws IOException
    {
        Openable exec = new OpenClass();
        File file = new File("Test.txt");
        try {
            exec.open(file);
            fail("Should throw IOException");
        } catch (IOException ex) {
            // As expected.
        }
        file = File.createTempFile("Test", ".txt");
        FileOutputStream output = exec.open(file);
        output.close();
        file.delete();
    }

    public void testImport() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker();

        // BEGIN Import Example 2
        maker.Import(File.class);
        maker.Import("java.io.FileOutputStream");
        maker.Import("java.io.IOException");
        maker.Implements(Openable.class);

        maker.Method("open", "FileOutputStream", ClassMaker.ACC_PUBLIC);
        maker.Declare("file", "File", 0);
        maker.Begin();
        {
            maker.Declare("output", "FileOutputStream", 0);
            maker.If(maker.Call(maker.Get("file"), "exists", null));
            maker.Set("output", maker.New("FileOutputStream").Init(maker.Push(maker.Get("file"))));
            maker.Else();
            maker.Throw(maker.New("IOException").Init(maker.Push(maker.Literal("File not found."))));
            maker.EndIf();
            maker.Return(maker.Get("output"));
        }
        maker.End();
        // END Import Example 2

        Class openClass = maker.defineClass();
        Openable exec = (Openable) openClass.newInstance();

        File file = new File("Test.txt");
        try {
            exec.open(file);
            fail("Should throw IOException");
        } catch (IOException ex) {
            // As expected.
        }
        file = File.createTempFile("Test", ".txt");
        FileOutputStream output = exec.open(file);
        output.close();
        file.delete();
    }

    // Classes Example - Implementing an Interface 
    public interface Square
    {
        int square(int a);
    }

    public class SquareTestMaker extends ClassMakerBase
    {
        public void code()
        {
            Implements(Square.class);

            Method("square", int.class, ACC_PUBLIC);
            Declare("a", int.class, 0);
            Begin();
            Return(Mult(Get("a"), Get("a")));
            End();
        }
    }
   
    public void testSquareTest() throws Exception
    {
        ClassMaker maker = new SquareTestMaker();
        Class squareClass = maker.defineClass();
        Square exec = (Square) squareClass.newInstance();
        assertEquals("Square test", 4, exec.square(2));
    }

    public interface Unary
    {
        int eval(int a, int b);
    }
    
    public void testSimpleMath() throws Exception
    {
        // Use separate factories to avoid duplicates in class loaded.
        ClassMakerFactory factory = new ClassMakerFactory();
        ClassMaker maker = factory.createClassMaker("test", "SimpleMath", null);

        maker.Implements(Unary.class);

        maker.Method("eval", int.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Begin();
        maker.Return(maker.Add(maker.Get("a"), maker.Get("b")));
        maker.End();

        Class squareClass = maker.defineClass();
        Unary exec = (Unary) squareClass.newInstance();
        assertEquals("Unary test", 4, exec.eval(2, 2));
    }

    // Classes Example - Implementing a generated Interface 
    public void testGeneratedInterface() throws Exception
    {
        ClassMakerFactory factory = new ClassMakerFactory();

        // Create interface test.Unary
        ClassMaker maker1 = factory.createClassMaker("test", "Unary", null);
        maker1.setIsInterface();
        maker1.Method("square", int.class, ClassMaker.ACC_PUBLIC | ClassMaker.ACC_ABSTRACT);
        maker1.Declare("a", int.class, 0);
        maker1.Forward();
        maker1.EndClass();

        // Create a class that implements test.Unary
        ClassMaker maker2 = factory.createClassMaker("test", "SquareTest", null);
        maker2.Implements("test.Unary");

        maker2.Method("square", int.class, ClassMaker.ACC_PUBLIC);
        maker2.Declare("a", int.class, 0);
        maker2.Begin();
        maker2.Return(maker2.Mult(maker2.Get("a"), maker2.Get("a")));
        maker2.End();
        maker2.EndClass();

        // Create a class that calls test.Unary
        ClassMaker maker3 = factory.createClassMaker("test", "UnaryTest", null);
        maker3.Implements(Square.class);
        maker3.Declare("test", "test.Unary", ClassMaker.ACC_PUBLIC);

        maker3.Method("square", int.class, ClassMaker.ACC_PUBLIC);
        maker3.Declare("a", int.class, 0);
        maker3.Begin();
        maker3.Return(maker3.Call(maker3.Get(maker3.This(), "test"), "square", maker3.Push(maker3.Get("a"))));
        maker3.End();
        maker3.EndClass();

        maker1.defineClass();
        Class squareClass = maker2.defineClass();
        Object squareTest = squareClass.newInstance();

        Class testClass = maker3.defineClass();
        Square exec = (Square) testClass.newInstance();

        // Assign a reference to the interface to the member field exec.test using reflection.
        testClass.getField("test").set(exec, squareTest);

        assertEquals("Square test", 4, exec.square(2));
    }

    // Classes Example - Implementing an Interface 
    public void testSaveClass() throws Exception
    {
        ClassMaker maker = new SquareTestMaker();
        String className = maker.getFullyQualifiedClassName();

        // Save the generated class into the build folder.
        File classesDir = new File("build/temp/classes");
        classesDir.mkdirs();
        try {
            File classFile = maker.saveClass(classesDir);
            assertTrue("File does not exist: " + classFile.getAbsolutePath(), classFile.exists());
    
            // Load the Class from the classpath and create an instance.
            SimpleClassLoader loader = maker.getFactory().getClassLoader();
            loader.setClassesDir(classesDir);
            Class squareClass = loader.loadClass(className);
            assertEquals("Class Name", className, squareClass.getName());
            Square exec = (Square) squareClass.newInstance();
            assertEquals("Square test", 4, exec.square(2));
            maker.deleteClass(classesDir);
            assertFalse("File should be deleted: " + classFile.getAbsolutePath(), classFile.exists());
        } finally {
            // Delete the SquareTest.class file.
            maker.deleteClass(classesDir);
        }
    }
}
