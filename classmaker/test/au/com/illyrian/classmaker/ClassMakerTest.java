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
import au.com.illyrian.classmaker.types.Value;
import au.com.illyrian.classmaker.util.MakerUtil;

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
        maker.Declare("id", ClassMakerFactory.INT_TYPE, ClassMakerConstants.ACC_PUBLIC);

        // Generate constructor
        {
            maker.Method(ClassMaker.INIT, void.class, ClassMakerConstants.ACC_PUBLIC);
            maker.Begin();
            maker.Init(maker.Super(), null);

            Value s1 = maker.Get("System", "out");
            CallStack s2 = maker.Push(maker.Literal("test.MyClass.<init> : Hello World"));
            Value s3 = maker.Call(s1, "println", s2);

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
    	assertEquals(name, MakerUtil.classToName(javaClass));
    	assertEquals(signature, MakerUtil.classToSignature(javaClass));
    }

    public void testArrayToName()
    {
    	assertEquals(MakerUtil.classToName(Object[].class), maker.ArrayOf(Object.class).getName());
    	assertEquals(MakerUtil.classToName(int[].class), maker.ArrayOf(int.class).getName());
    	assertEquals(MakerUtil.classToName(Object[][].class), maker.ArrayOf(Object[].class).getName());
    	assertEquals(MakerUtil.classToName(int[][].class), maker.ArrayOf(int[].class).getName());
    	assertEquals(MakerUtil.classToName(Object[].class), maker.classToClassType(Object[].class).getName());
    	assertEquals(MakerUtil.classToName(int[].class), maker.classToClassType(int[].class).getName());
    	assertEquals(MakerUtil.classToName(Object[][].class), maker.classToClassType(Object[][].class).getName());
    	assertEquals(MakerUtil.classToName(int[][].class), maker.classToClassType(int[][].class).getName());
    }
    
    public void testModifiers()
    {
        assertEquals("public",    ClassMakerConstants.ACC_PUBLIC, MakerUtil.fromModifierString("public"));
        assertEquals("protected", ClassMakerConstants.ACC_PROTECTED, MakerUtil.fromModifierString("protected"));
        assertEquals("private",   ClassMakerConstants.ACC_PRIVATE, MakerUtil.fromModifierString("private"));
        assertEquals("static",    ClassMakerConstants.ACC_STATIC, MakerUtil.fromModifierString("static"));
        assertEquals("final",     ClassMakerConstants.ACC_FINAL, MakerUtil.fromModifierString("final"));
        assertEquals("synchronized", ClassMakerConstants.ACC_SYNCHRONIZED, MakerUtil.fromModifierString("synchronized"));
        assertEquals("volatile",  ClassMakerConstants.ACC_VOLATILE, MakerUtil.fromModifierString("volatile"));
        assertEquals("transient", ClassMakerConstants.ACC_TRANSIENT, MakerUtil.fromModifierString("transient"));
        assertEquals("native",    ClassMakerConstants.ACC_NATIVE, MakerUtil.fromModifierString("native"));
        assertEquals("abstract",  ClassMakerConstants.ACC_ABSTRACT, MakerUtil.fromModifierString("abstract"));
        assertEquals("strictfp",  ClassMakerConstants.ACC_STRICTFP, MakerUtil.fromModifierString("strictfp"));
        try {
            MakerUtil.fromModifierString("pulbic");
            fail("ClassMakerException expected");
        } catch (IllegalArgumentException ex)
        {
            assertEquals("Invalid modifier", "Invalid modifier: pulbic", ex.getMessage());
        }
    }
    
    public void testAddModifiers()
    {
        try {
            maker.addModifier(ClassMakerConstants.ACC_PUBLIC, "pulbic");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid modifier", "Invalid modifier: pulbic", ex.getMessage());
        }
        try {
            maker.addModifier(ClassMakerConstants.ACC_PUBLIC, "public");
            fail("ClassMakerException expected");
        } catch (ClassMakerException ex) {
            assertEquals("Duplicate modifiers", "Duplicate modifier: public", ex.getMessage());
        }
        int expected = ClassMakerConstants.ACC_PUBLIC | ClassMakerConstants.ACC_STATIC;
        assertEquals("Wrong modifiers", MakerUtil.toModifierString(expected),
                MakerUtil.toModifierString(maker.addModifier(ClassMakerConstants.ACC_PUBLIC, "static")));

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

    public class SquareTestMaker extends ClassMakerCode
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
        maker.checkClassModifiers(ClassMakerConstants.ACC_PUBLIC);
        maker.checkClassModifiers(ClassMakerConstants.ACC_PROTECTED);
        maker.checkClassModifiers(ClassMakerConstants.ACC_PACKAGE);
        maker.checkClassModifiers(ClassMakerConstants.ACC_PRIVATE);
        maker.checkClassModifiers(ClassMakerConstants.ACC_ABSTRACT);
        maker.checkClassModifiers(ClassMakerConstants.ACC_FINAL);
        maker.checkClassModifiers(ClassMakerConstants.ACC_STRICTFP);
        checkClassModifiersException(ClassMakerConstants.ACC_STATIC, "Invalid class modifier: static ");
        checkClassModifiersException(ClassMakerConstants.ACC_SYNCHRONIZED, "Invalid class modifier: synchronized ");
        checkClassModifiersException(ClassMakerConstants.ACC_VOLATILE, "Invalid class modifier: volatile ");
        checkClassModifiersException(ClassMakerConstants.ACC_TRANSIENT, "Invalid class modifier: transient ");
        checkClassModifiersException(ClassMakerConstants.ACC_NATIVE, "Invalid class modifier: native ");
        checkClassModifiersException(-1, "Invalid class modifier: static synchronized volatile transient native ");
        // Class modifier combinations
        maker.checkClassModifiers(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_PUBLIC);
        maker.checkClassModifiers(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_STRICTFP);
        checkClassModifiersException(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_ABSTRACT,
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
        maker.checkMethodModifiers(ClassMakerConstants.ACC_PUBLIC);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_ABSTRACT);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_PROTECTED);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_PRIVATE);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_STATIC);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_FINAL);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_ABSTRACT);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_SYNCHRONIZED);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_NATIVE);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_STRICTFP);
        checkMethodModifiersException(ClassMakerConstants.ACC_VOLATILE, "Invalid method modifier: volatile ");
        checkMethodModifiersException(ClassMakerConstants.ACC_TRANSIENT, "Invalid method modifier: transient ");
        checkMethodModifiersException(-1, "Invalid method modifier: volatile transient ");
        // Method modifier combinations
        maker.checkMethodModifiers(ClassMakerConstants.ACC_ABSTRACT|ClassMakerConstants.ACC_PUBLIC);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_ABSTRACT|ClassMakerConstants.ACC_PROTECTED);
        maker.checkMethodModifiers(ClassMakerConstants.ACC_ABSTRACT|ClassMakerConstants.ACC_PRIVATE);
        checkMethodModifiersException(ClassMakerConstants.ACC_ABSTRACT|ClassMakerConstants.ACC_STATIC,
                        "Incompatible method modifier combination: abstract with static ");
        checkMethodModifiersException(ClassMakerConstants.ACC_ABSTRACT|ClassMakerConstants.ACC_FINAL,
                        "Incompatible method modifier combination: abstract with final ");
        checkMethodModifiersException(ClassMakerConstants.ACC_ABSTRACT|ClassMakerConstants.ACC_SYNCHRONIZED,
                        "Incompatible method modifier combination: abstract with synchronized ");
        checkMethodModifiersException(ClassMakerConstants.ACC_ABSTRACT|ClassMakerConstants.ACC_NATIVE,
                        "Incompatible method modifier combination: abstract with native ");
        checkMethodModifiersException(ClassMakerConstants.ACC_ABSTRACT|ClassMakerConstants.ACC_STRICTFP,
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
    	checkMultipleAccessModifiersException(ClassMakerConstants.ACC_PUBLIC|ClassMakerConstants.ACC_PROTECTED,
                "Cannot have more than one access modifier: public protected ");
    	checkMultipleAccessModifiersException(ClassMakerConstants.ACC_PUBLIC|ClassMakerConstants.ACC_PRIVATE,
                "Cannot have more than one access modifier: public private ");
    	checkMultipleAccessModifiersException(ClassMakerConstants.ACC_PRIVATE|ClassMakerConstants.ACC_PROTECTED,
                "Cannot have more than one access modifier: protected private ");
    	checkMultipleAccessModifiersException(ClassMakerConstants.ACC_PUBLIC|ClassMakerConstants.ACC_PROTECTED|ClassMakerConstants.ACC_PRIVATE,
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
        maker.checkFieldModifiers(ClassMakerConstants.ACC_PUBLIC);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_PROTECTED);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_PRIVATE);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_STATIC);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_FINAL);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_TRANSIENT);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_VOLATILE);
        checkFieldModifiersException(ClassMakerConstants.ACC_ABSTRACT, "Invalid field modifier: abstract ");
        checkFieldModifiersException(ClassMakerConstants.ACC_SYNCHRONIZED, "Invalid field modifier: synchronized ");
        checkFieldModifiersException(ClassMakerConstants.ACC_STRICTFP, "Invalid field modifier: strictfp ");
        checkFieldModifiersException(ClassMakerConstants.ACC_NATIVE, "Invalid field modifier: native ");
        checkFieldModifiersException(-1, "Invalid field modifier: synchronized native abstract strictfp ");
        // Class modifier combinations
        maker.checkFieldModifiers(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_PUBLIC);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_PROTECTED);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_PRIVATE);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_STATIC);
        maker.checkFieldModifiers(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_TRANSIENT);
        checkFieldModifiersException(ClassMakerConstants.ACC_FINAL|ClassMakerConstants.ACC_VOLATILE,
                        "Incompatible field modifier combination: final with volatile");
    }

    public static class ExtendsThrowable extends Throwable
    {
        // Gets rid of compiler warning. Never used.
        static final long serialVersionUID = 0;
    }

    public void testAccessDenied()
    {
        ClassType string = ClassMakerFactory.STRING_TYPE;
        ClassType throwable = ClassMakerFactory.THROWABLE_TYPE;
        ClassType extendsThrow = factory.addClassType(ExtendsThrowable.class);

        assertFalse("Same class: public",    maker.isAccessDenied(string, string, ClassMakerConstants.ACC_PUBLIC));
        assertFalse("Same class: protected", maker.isAccessDenied(string, string, ClassMakerConstants.ACC_PROTECTED));
        assertFalse("Same class: package",   maker.isAccessDenied(string, string, ClassMakerConstants.ACC_PACKAGE));
        assertFalse("Same class: private",   maker.isAccessDenied(string, string, ClassMakerConstants.ACC_PRIVATE));

        assertFalse("Same package: public",    maker.isAccessDenied(string, throwable, ClassMakerConstants.ACC_PUBLIC));
        assertFalse("Same package: protected", maker.isAccessDenied(string, throwable, ClassMakerConstants.ACC_PROTECTED));
        assertFalse("Same package: package",   maker.isAccessDenied(string, throwable, ClassMakerConstants.ACC_PACKAGE));
        assertTrue( "Same package: private",   maker.isAccessDenied(string, throwable, ClassMakerConstants.ACC_PRIVATE));

        assertFalse("Derived class: public",    maker.isAccessDenied(extendsThrow, throwable, ClassMakerConstants.ACC_PUBLIC));
        assertFalse("Derived class: protected", maker.isAccessDenied(extendsThrow, throwable, ClassMakerConstants.ACC_PROTECTED));
        assertTrue( "Derived class: package",   maker.isAccessDenied(extendsThrow, throwable, ClassMakerConstants.ACC_PACKAGE));
        assertTrue( "Derived class: private",   maker.isAccessDenied(extendsThrow, throwable, ClassMakerConstants.ACC_PRIVATE));

        assertFalse("Unrelated class: public",    maker.isAccessDenied(string, extendsThrow, ClassMakerConstants.ACC_PUBLIC));
        assertTrue( "Unrelated class: protected", maker.isAccessDenied(string, extendsThrow, ClassMakerConstants.ACC_PROTECTED));
        assertTrue( "Unrelated class: package",   maker.isAccessDenied(string, extendsThrow, ClassMakerConstants.ACC_PACKAGE));
        assertTrue( "Unrelated class: private",   maker.isAccessDenied(string, extendsThrow, ClassMakerConstants.ACC_PRIVATE));
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
    	maker.Method("unary", "double", ClassMakerConstants.ACC_PUBLIC);
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
        File classFile = new File(classesDir, MakerUtil.toSlashName(className)+".class");
        maker.saveClass(classesDir);
        assertTrue("File does not exist: " + classFile.getAbsolutePath(), classFile.exists());
        Class squareClass = ClassMakerCode.getSharedFactory().getClassLoader().loadClass(className);
        assertEquals("Class Name", className, squareClass.getName());
        Unary exec = (Unary)squareClass.newInstance();
        assertEquals("Square test", 4, exec.square(2));
        maker.deleteClass(classesDir);
        assertFalse("File should be deleted: " + classFile.getAbsolutePath(), classFile.exists());
    }
}
