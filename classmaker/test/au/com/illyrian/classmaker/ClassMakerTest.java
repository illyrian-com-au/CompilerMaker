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

import junit.framework.TestCase;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

public class ClassMakerTest extends TestCase
{
    ClassMakerFactory factory;
    ClassMaker maker;

    public void setUp() throws Exception
    {
        factory = new ClassMakerFactory();
        maker = factory.createClassMaker("test", "MyClass", "MyClass.java");
    }

    public void testConstructorHelloWorld() throws Exception
    {
        // Generate Class
        maker.Import(System.class);
        maker.Declare("id", PrimitiveType.INT_TYPE, ClassMaker.ACC_PUBLIC);

        // Generate constructor
        {
            maker.Method(ClassMaker.INIT, void.class, ClassMaker.ACC_PUBLIC);
            maker.Begin();
            maker.Init(maker.Super(), null);

            // System.out.println("Hello World");
//            maker.Call(maker.Get("System", "out"), "println",
//                       maker.Push(maker.Literal("test.MyClass.<init> : Hello World")));
            // FIXME - remove load
            Type s1 = maker.Get("System", "out");
            CallStack s2 = maker.Push(maker.Literal("test.MyClass.<init> : Hello World"));
            Type s3 = maker.Call(s1, "println", s2);

            // set id to 1
            maker.Set(maker.This(), "id", maker.Literal(1));
            maker.Return();
            maker.End();
        }

        Class myClass = maker.defineClass();
        myClass.newInstance();
    }

    public void testClassToName()
    {
        checkType("void", "V", void.class);
        checkType("byte", "B", byte.class);
        checkType("char", "C", char.class);
        checkType("double", "D", double.class);
        checkType("float", "F", float.class);
        checkType("int", "I", int.class);
        checkType("long", "J", long.class);
        checkType("short", "S", short.class);
        checkType("boolean", "Z", boolean.class);
        checkType("java.lang.Object", "Ljava/lang/Object;", Object.class);
        checkType("java.lang.Runnable", "Ljava/lang/Runnable;", Runnable.class);
        checkType("java.lang.Object[]", "[Ljava/lang/Object;", Object[].class);
        checkType("java.lang.Runnable[]", "[Ljava/lang/Runnable;", Runnable[].class);
        checkType("int[]", "[I", int[].class);
        checkType("long[]", "[J", long[].class);
        checkType("float[]", "[F", float[].class);
        checkType("double[]", "[D", double[].class);
    }

    private void checkType(String name, String signature, Class javaClass)
    {
    	assertEquals(name, ClassMaker.classToName(javaClass));
    	assertEquals(signature, ClassMaker.classToSignature(javaClass));
    }

    public void testArrayToName()
    {
    	assertEquals(ClassMaker.classToName(Object[].class), maker.ArrayOf(Object.class).getName());
    	assertEquals(ClassMaker.classToName(int[].class), maker.ArrayOf(int.class).getName());
    	assertEquals(ClassMaker.classToName(Object[][].class), maker.ArrayOf(Object[].class).getName());
    	assertEquals(ClassMaker.classToName(int[][].class), maker.ArrayOf(int[].class).getName());
    	assertEquals(ClassMaker.classToName(Object[].class), maker.classToClassType(Object[].class).getName());
    	assertEquals(ClassMaker.classToName(int[].class), maker.classToClassType(int[].class).getName());
    	assertEquals(ClassMaker.classToName(Object[][].class), maker.classToClassType(Object[][].class).getName());
    	assertEquals(ClassMaker.classToName(int[][].class), maker.classToClassType(int[][].class).getName());
    }
    
    public void testModifiers()
    {
        assertEquals("public", ClassMaker.ACC_PUBLIC, ClassMaker.fromModifierString("public"));
        assertEquals("public", ClassMaker.ACC_PROTECTED, ClassMaker.fromModifierString("protected"));
        assertEquals("public", ClassMaker.ACC_PRIVATE, ClassMaker.fromModifierString("private"));
        assertEquals("public", ClassMaker.ACC_STATIC, ClassMaker.fromModifierString("static"));
        assertEquals("public", ClassMaker.ACC_FINAL, ClassMaker.fromModifierString("final"));
        assertEquals("public", ClassMaker.ACC_SYNCHRONIZED, ClassMaker.fromModifierString("synchronized"));
        assertEquals("public", ClassMaker.ACC_VOLATILE, ClassMaker.fromModifierString("volatile"));
        assertEquals("public", ClassMaker.ACC_TRANSIENT, ClassMaker.fromModifierString("transient"));
        assertEquals("public", ClassMaker.ACC_NATIVE, ClassMaker.fromModifierString("native"));
        assertEquals("public", ClassMaker.ACC_ABSTRACT, ClassMaker.fromModifierString("abstract"));
        assertEquals("public", ClassMaker.ACC_STRICTFP, ClassMaker.fromModifierString("strictfp"));
        try {
            ClassMaker.fromModifierString("pulbic");
            fail("ClassMakerException expected");
        } catch (IllegalArgumentException ex)
        {
            assertEquals("Invalid modifier", "Invalid modifier: pulbic", ex.getMessage());
        }
    }
    
    public void testAddModifiers()
    {
        try {
            maker.addModifier(ClassMaker.ACC_PUBLIC, "pulbic");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex)
        {
            assertEquals("Invalid modifier", "Invalid modifier: pulbic", ex.getMessage());
        }
        try {
            maker.addModifier(ClassMaker.ACC_PUBLIC, "public");
            fail("ClassMakerException expected");
        } catch (ClassMakerException ex) {
            assertEquals("Duplicate modifiers", "Duplicate modifier: public", ex.getMessage());
        }
        int expected = ClassMaker.ACC_PUBLIC | ClassMaker.ACC_STATIC;
        assertEquals("Wrong modifiers", ClassMaker.toModifierString(expected),
                ClassMaker.toModifierString(maker.addModifier(ClassMaker.ACC_PUBLIC, "static")));

    }

    public void testExceptions()
    {
        ExceptionFactory exFactory = factory.getExceptionFactory();
        assertNotNull("Exception factory should not be null", exFactory);
        String val = exFactory.getResourceBundle().getString("ClassMaker.MethodReturnsTypeSoCannotReturnType_3");
        assertEquals("Method ${0} returns type ${1} so cannot return a value of type ${2}", val);

        String[] values = {"test", "int", "long"};
        ClassMakerException ex = exFactory.createException(maker.getSourceLine(), "ClassMaker.MethodReturnsTypeSoCannotReturnType_3", values);
        String msg = "Method test returns type int so cannot return a value of type long";
        assertEquals("Wrong message", msg, ex.getMessage());

        ex = exFactory.createException(maker.getSourceLine(), "ClassMaker.DoesNotExist", null);
        assertEquals("!ClassMaker.DoesNotExist!", ex.getMessage());
    }

    public interface Unary
    {
        int square(int a);
    }

    public class SquareTestMaker extends ClassMakerBase
    {
        public void code()
        {
            Implements(Unary.class);

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
        Unary exec = (Unary)squareClass.newInstance();
        assertEquals("Square test", 4, exec.square(2));
    }

    void checkClassModifiersException(int modifiers, String msg)
    {
        try {
            maker.checkClassModifiers(modifiers);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", msg, ex.getMessage());
        }
    }

    public void testClassModifiers()
    {
        maker.checkClassModifiers(ClassMaker.ACC_PUBLIC);
        maker.checkClassModifiers(ClassMaker.ACC_PROTECTED);
        maker.checkClassModifiers(ClassMaker.ACC_PACKAGE);
        maker.checkClassModifiers(ClassMaker.ACC_PRIVATE);
        maker.checkClassModifiers(ClassMaker.ACC_ABSTRACT);
        maker.checkClassModifiers(ClassMaker.ACC_FINAL);
        maker.checkClassModifiers(ClassMaker.ACC_STRICTFP);
        checkClassModifiersException(ClassMaker.ACC_STATIC, "Invalid class modifier: static ");
        checkClassModifiersException(ClassMaker.ACC_SYNCHRONIZED, "Invalid class modifier: synchronized ");
        checkClassModifiersException(ClassMaker.ACC_VOLATILE, "Invalid class modifier: volatile ");
        checkClassModifiersException(ClassMaker.ACC_TRANSIENT, "Invalid class modifier: transient ");
        checkClassModifiersException(ClassMaker.ACC_NATIVE, "Invalid class modifier: native ");
        checkClassModifiersException(-1, "Invalid class modifier: static synchronized volatile transient native ");
        // Class modifier combinations
        maker.checkClassModifiers(ClassMaker.ACC_FINAL|ClassMaker.ACC_PUBLIC);
        maker.checkClassModifiers(ClassMaker.ACC_FINAL|ClassMaker.ACC_STRICTFP);
        checkClassModifiersException(ClassMaker.ACC_FINAL|ClassMaker.ACC_ABSTRACT,
                        "Incompatible class modifier combination: abstract with final");
    }

    void checkMethodModifiersException(int modifiers, String msg)
    {
        try {
            maker.checkMethodModifiers(modifiers);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", msg, ex.getMessage());
        }
    }

    public void testMethodModifiers()
    {
        maker.checkMethodModifiers(ClassMaker.ACC_PUBLIC);
        maker.checkMethodModifiers(ClassMaker.ACC_ABSTRACT);
        maker.checkMethodModifiers(ClassMaker.ACC_PROTECTED);
        maker.checkMethodModifiers(ClassMaker.ACC_PRIVATE);
        maker.checkMethodModifiers(ClassMaker.ACC_STATIC);
        maker.checkMethodModifiers(ClassMaker.ACC_FINAL);
        maker.checkMethodModifiers(ClassMaker.ACC_ABSTRACT);
        maker.checkMethodModifiers(ClassMaker.ACC_SYNCHRONIZED);
        maker.checkMethodModifiers(ClassMaker.ACC_NATIVE);
        maker.checkMethodModifiers(ClassMaker.ACC_STRICTFP);
        checkMethodModifiersException(ClassMaker.ACC_VOLATILE, "Invalid method modifier: volatile ");
        checkMethodModifiersException(ClassMaker.ACC_TRANSIENT, "Invalid method modifier: transient ");
        checkMethodModifiersException(-1, "Invalid method modifier: volatile transient ");
        // Method modifier combinations
        maker.checkMethodModifiers(ClassMaker.ACC_ABSTRACT|ClassMaker.ACC_PUBLIC);
        maker.checkMethodModifiers(ClassMaker.ACC_ABSTRACT|ClassMaker.ACC_PROTECTED);
        maker.checkMethodModifiers(ClassMaker.ACC_ABSTRACT|ClassMaker.ACC_PRIVATE);
        checkMethodModifiersException(ClassMaker.ACC_ABSTRACT|ClassMaker.ACC_STATIC,
                        "Incompatible method modifier combination: abstract with static ");
        checkMethodModifiersException(ClassMaker.ACC_ABSTRACT|ClassMaker.ACC_FINAL,
                        "Incompatible method modifier combination: abstract with final ");
        checkMethodModifiersException(ClassMaker.ACC_ABSTRACT|ClassMaker.ACC_SYNCHRONIZED,
                        "Incompatible method modifier combination: abstract with synchronized ");
        checkMethodModifiersException(ClassMaker.ACC_ABSTRACT|ClassMaker.ACC_NATIVE,
                        "Incompatible method modifier combination: abstract with native ");
        checkMethodModifiersException(ClassMaker.ACC_ABSTRACT|ClassMaker.ACC_STRICTFP,
                        "Incompatible method modifier combination: abstract with strictfp ");
    }
    
    void checkMultipleAccessModifiersException(int modifiers, String msg)
    {
        try {
            maker.checkMultipleAccessModifiers(modifiers);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", msg, ex.getMessage());
        }
    }

    public void testMultipleAccessModifiers()
    {
        // Check multiple access modifiers
    	checkMultipleAccessModifiersException(ClassMaker.ACC_PUBLIC|ClassMaker.ACC_PROTECTED,
                "Cannot have more than one access modifier: public protected ");
    	checkMultipleAccessModifiersException(ClassMaker.ACC_PUBLIC|ClassMaker.ACC_PRIVATE,
                "Cannot have more than one access modifier: public private ");
    	checkMultipleAccessModifiersException(ClassMaker.ACC_PRIVATE|ClassMaker.ACC_PROTECTED,
                "Cannot have more than one access modifier: protected private ");
    	checkMultipleAccessModifiersException(ClassMaker.ACC_PUBLIC|ClassMaker.ACC_PROTECTED|ClassMaker.ACC_PRIVATE,
                "Cannot have more than one access modifier: public protected private ");
    }

    void checkFieldModifiersException(int modifiers, String msg)
    {
        try {
            maker.checkFieldModifiers(modifiers);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Wrong message", msg, ex.getMessage());
        }
    }

    public void testFieldModifiers()
    {
        maker.checkFieldModifiers(ClassMaker.ACC_PUBLIC);
        maker.checkFieldModifiers(ClassMaker.ACC_PROTECTED);
        maker.checkFieldModifiers(ClassMaker.ACC_PRIVATE);
        maker.checkFieldModifiers(ClassMaker.ACC_STATIC);
        maker.checkFieldModifiers(ClassMaker.ACC_FINAL);
        maker.checkFieldModifiers(ClassMaker.ACC_TRANSIENT);
        maker.checkFieldModifiers(ClassMaker.ACC_VOLATILE);
        checkFieldModifiersException(ClassMaker.ACC_ABSTRACT, "Invalid field modifier: abstract ");
        checkFieldModifiersException(ClassMaker.ACC_SYNCHRONIZED, "Invalid field modifier: synchronized ");
        checkFieldModifiersException(ClassMaker.ACC_STRICTFP, "Invalid field modifier: strictfp ");
        checkFieldModifiersException(ClassMaker.ACC_NATIVE, "Invalid field modifier: native ");
        checkFieldModifiersException(-1, "Invalid field modifier: synchronized native abstract strictfp ");
        // Class modifier combinations
        maker.checkFieldModifiers(ClassMaker.ACC_FINAL|ClassMaker.ACC_PUBLIC);
        maker.checkFieldModifiers(ClassMaker.ACC_FINAL|ClassMaker.ACC_PROTECTED);
        maker.checkFieldModifiers(ClassMaker.ACC_FINAL|ClassMaker.ACC_PRIVATE);
        maker.checkFieldModifiers(ClassMaker.ACC_FINAL|ClassMaker.ACC_STATIC);
        maker.checkFieldModifiers(ClassMaker.ACC_FINAL|ClassMaker.ACC_TRANSIENT);
        checkFieldModifiersException(ClassMaker.ACC_FINAL|ClassMaker.ACC_VOLATILE,
                        "Incompatible field modifier combination: final with volatile");
    }

    public static class ExtendsThrowable extends Throwable
    {
        // Gets rid of compiler warning. Never used.
        static final long serialVersionUID = 0;
    }

    public void testAccessDenied()
    {
        ClassType string = ClassType.STRING_TYPE;
        ClassType throwable = ClassType.THROWABLE_TYPE;
        ClassType extendsThrow = factory.addClassType(ExtendsThrowable.class);

        assertFalse("Same class: public",    maker.isAccessDenied(string, string, ClassMaker.ACC_PUBLIC));
        assertFalse("Same class: protected", maker.isAccessDenied(string, string, ClassMaker.ACC_PROTECTED));
        assertFalse("Same class: package",   maker.isAccessDenied(string, string, ClassMaker.ACC_PACKAGE));
        assertFalse("Same class: private",   maker.isAccessDenied(string, string, ClassMaker.ACC_PRIVATE));

        assertFalse("Same package: public",    maker.isAccessDenied(string, throwable, ClassMaker.ACC_PUBLIC));
        assertFalse("Same package: protected", maker.isAccessDenied(string, throwable, ClassMaker.ACC_PROTECTED));
        assertFalse("Same package: package",   maker.isAccessDenied(string, throwable, ClassMaker.ACC_PACKAGE));
        assertTrue( "Same package: private",   maker.isAccessDenied(string, throwable, ClassMaker.ACC_PRIVATE));

        assertFalse("Derived class: public",    maker.isAccessDenied(extendsThrow, throwable, ClassMaker.ACC_PUBLIC));
        assertFalse("Derived class: protected", maker.isAccessDenied(extendsThrow, throwable, ClassMaker.ACC_PROTECTED));
        assertTrue( "Derived class: package",   maker.isAccessDenied(extendsThrow, throwable, ClassMaker.ACC_PACKAGE));
        assertTrue( "Derived class: private",   maker.isAccessDenied(extendsThrow, throwable, ClassMaker.ACC_PRIVATE));

        assertFalse("Unrelated class: public",    maker.isAccessDenied(string, extendsThrow, ClassMaker.ACC_PUBLIC));
        assertTrue( "Unrelated class: protected", maker.isAccessDenied(string, extendsThrow, ClassMaker.ACC_PROTECTED));
        assertTrue( "Unrelated class: package",   maker.isAccessDenied(string, extendsThrow, ClassMaker.ACC_PACKAGE));
        assertTrue( "Unrelated class: private",   maker.isAccessDenied(string, extendsThrow, ClassMaker.ACC_PRIVATE));
    }
    
    public void testSaveClassException() throws Exception
    {
        ClassMaker maker = new SquareTestMaker();
        try {
            maker.saveClass(new File("Dummy"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("saveClass()", "Output folder does not exist: Dummy", ex.getMessage());
        }
        try {
            maker.deleteClass(new File("Dummy"));
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("saveClass()", "Output folder does not exist: Dummy", ex.getMessage());
        }
    }
    
    public interface UnaryDouble
    {
    	double unary(double d);
    }
    
    public void testJavaLangClasses() throws Exception
    {
    	maker.Implements(UnaryDouble.class);
    	maker.Declare("ch", "Character", 0);
    	maker.Method("unary", "double", ClassMaker.ACC_PUBLIC);
    	maker.Declare("d", "double", 0);
    	maker.Begin();
    	{
    		maker.Return(maker.Call("Math", "log", maker.Push(maker.Get("d"))));
    	}
    	maker.End();
        Class squareClass = maker.defineClass();
        UnaryDouble exec = (UnaryDouble)squareClass.newInstance();
        assertEquals("Math.log(x)", Math.log(2), exec.unary(2));
    }

    //FIXME - this is broken
    public void untestSaveClass() throws Exception
    {
        ClassMaker maker = new SquareTestMaker();
        String className = maker.getFullyQualifiedClassName();
        File classesDir =new File("build/classes");
        File classFile = new File(classesDir, ClassMaker.toSlashName(className)+".class");
        maker.saveClass(classesDir);
        assertTrue("File does not exist: " + classFile.getAbsolutePath(), classFile.exists());
        Class squareClass = ClassMakerBase.getSharedFactory().getClassLoader().loadClass(className);
        assertEquals("Class Name", className, squareClass.getName());
        Unary exec = (Unary)squareClass.newInstance();
        assertEquals("Square test", 4, exec.square(2));
        maker.deleteClass(classesDir);
        assertFalse("File should be deleted: " + classFile.getAbsolutePath(), classFile.exists());
    }
}
