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

package au.com.illyrian.classmaker.members;

import java.util.Vector;

import org.mozilla.classfile.ByteCode;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class MakerResolveMethodTest extends ClassMakerTestCase
{
    ClassMakerFactory factory;
    ClassMaker maker;
    MethodResolver resolver;
    final ClassType DESSERT = new ClassType("test.Dessert", ClassType.OBJECT_TYPE);;
    final ClassType CAKE = new ClassType("test.Cake", DESSERT);
    final ClassType SCONE = new ClassType("test.Scone", DESSERT);
    final ClassType CHOCOLATE_CAKE = new ClassType("test.ChocolateCake", CAKE);
    final ClassType BUTTERED_SCONE = new ClassType("test.ButteredScone", SCONE);

    public void setUp() throws Exception
    {
        factory = new MyClassMakerFactory();
        maker = factory.createClassMaker();
        maker.setFullyQualifiedClassName("MyClass");
        maker.Extends(Object.class);
        maker.setSourceFilename("MyClass.java");
        defaultConstructor(maker);
        resolver = factory.getMethodResolver();
    }

    class MyClassMakerFactory extends ClassMakerFactory
    {
        MyClassMakerFactory()
        {
            super();
            addLocalClasses();
        }
        protected void addLocalClasses()
        {
            addTypeAndDeclaredType(DESSERT); 
            addTypeAndDeclaredType(CAKE);
            addTypeAndDeclaredType(SCONE);
            addTypeAndDeclaredType(CHOCOLATE_CAKE);
            addTypeAndDeclaredType(BUTTERED_SCONE);
        }
    }

    // Generate default constructor
    public void defaultConstructor(ClassMaker maker) throws Exception
    {
        maker.Method("<init>", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public void forwardPrimitiveMethods(Type type) throws Exception
    {
        maker.Method("eval", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Forward();

        maker.Method("eval", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", type, 0);
        maker.Forward();

        maker.Method("eval", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", type, 0);
        maker.Declare("y", type, 0);
        maker.Forward();
    }

	public void testResolveIntMethods() throws Exception
    {
    	forwardPrimitiveMethods(PrimitiveType.INT_TYPE);
        Vector<MakerMethod> methods = resolver.findMethods(maker.getDeclaredMethods(), "eval");
        assertEquals("Wrong number of methods found.", 3, methods.size());
        assertEquals("public void eval()", methods.get(0).toFullString());
        assertEquals("public void eval(int)", methods.get(1).toFullString());
        assertEquals("public void eval(int, int)", methods.get(2).toFullString());

        MakerMethod method;
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push());
        assertEquals("Did not find eval()", 0, method.getFormalTypes().length);

        // Check assignment of primitives
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push(PrimitiveType.INT_TYPE.getValue()));
        assertEquals("Did not find eval(int)", 1, method.getFormalTypes().length);
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push(PrimitiveType.BYTE_TYPE.getValue()));
        assertEquals("Did not find eval(int)", 1, method.getFormalTypes().length);
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push(PrimitiveType.SHORT_TYPE.getValue()));
        assertEquals("Did not find eval(int)", 1, method.getFormalTypes().length);
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push(PrimitiveType.CHAR_TYPE.getValue()));
        assertEquals("Did not find eval(int)", 1, method.getFormalTypes().length);

        failToResolveMethod("eval", maker.Push(PrimitiveType.FLOAT_TYPE.getValue()));
        failToResolveMethod("eval", maker.Push(PrimitiveType.DOUBLE_TYPE.getValue()));
        failToResolveMethod("eval", maker.Push(PrimitiveType.LONG_TYPE.getValue()));

        method = maker.resolveMethod(maker.getClassType(), "eval", 
                maker.Push(PrimitiveType.INT_TYPE.getValue()).Push(PrimitiveType.INT_TYPE.getValue()));
        assertEquals("Did not find eval(int,int)", 2, method.getFormalTypes().length);
    }

    public void testResolveReferenceMethods() throws Exception
    {
        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", DESSERT, 0);
        maker.Declare("y", SCONE, 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", CAKE, 0);
        maker.Declare("y", DESSERT, 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", CHOCOLATE_CAKE, 0);
        maker.Declare("y", SCONE, 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", CHOCOLATE_CAKE, 0);
        maker.Declare("y", PrimitiveType.INT_TYPE, 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", ClassType.OBJECT_TYPE, 0);
        maker.Declare("y", ClassType.OBJECT_TYPE, 0);
        maker.Forward();

        MakerMethod method;
        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(DESSERT.getValue()).Push(SCONE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Dessert, test.Scone)", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(CHOCOLATE_CAKE.getValue()).Push(DESSERT.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Cake, test.Dessert)", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(CHOCOLATE_CAKE.getValue()).Push(BUTTERED_SCONE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, test.Scone)", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(CHOCOLATE_CAKE.getValue()).Push(PrimitiveType.BYTE_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, int)", method.toFullString());

    	failToResolveMethod("moorge", maker.Push(CAKE.getValue()).Push(SCONE.getValue()));

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(DESSERT.getValue()).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Dessert, test.Scone)", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(SCONE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, test.Scone)", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(CHOCOLATE_CAKE.getValue()).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, test.Scone)", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, test.Scone)", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(DESSERT.getValue()).Push(DESSERT.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(java.lang.Object, java.lang.Object)", method.toFullString());
    }

    public void testResolveArrayReferenceMethods() throws Exception
    {
        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(DESSERT), 0);
        maker.Declare("y", maker.ArrayOf(SCONE), 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CAKE), 0);
        maker.Declare("y", maker.ArrayOf(DESSERT), 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CHOCOLATE_CAKE), 0);
        maker.Declare("y", maker.ArrayOf(SCONE), 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CHOCOLATE_CAKE), 0);
        maker.Declare("y", maker.ArrayOf(PrimitiveType.INT_TYPE), 0);
        maker.Forward();

        MakerMethod method;
        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(DESSERT).getValue()).Push(maker.ArrayOf(SCONE).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Dessert[], test.Scone[])", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(CHOCOLATE_CAKE).getValue()).Push(maker.ArrayOf(DESSERT).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Cake[], test.Dessert[])", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(CHOCOLATE_CAKE).getValue()).Push(maker.ArrayOf(BUTTERED_SCONE).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake[], test.Scone[])", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(CHOCOLATE_CAKE).getValue()).Push(maker.ArrayOf(PrimitiveType.INT_TYPE).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake[], int[])", method.toFullString());

    	failToResolveMethod("moorge", maker.Push(maker.ArrayOf(CHOCOLATE_CAKE).getValue()).Push(maker.ArrayOf(PrimitiveType.BYTE_TYPE).getValue()));

    	failToResolveMethod("moorge", maker.Push(maker.ArrayOf(CAKE).getValue()).Push(maker.ArrayOf(SCONE).getValue()));

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(DESSERT).getValue()).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Dessert[], test.Scone[])", method.toFullString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(maker.ArrayOf(SCONE).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake[], test.Scone[])", method.toFullString());

    	failToResolveMethod("moorge", maker.Push(maker.ArrayOf(CAKE).getValue()).Push(ClassType.NULL_TYPE.getValue()));

    	failToResolveMethod("moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(ClassType.NULL_TYPE.getValue()));
    }

    public void testResolveArrayToObjectMethods() throws Exception
    {
        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(DESSERT), 0);
        maker.Declare("y", maker.ArrayOf(SCONE), 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CAKE), 0);
        maker.Declare("y", maker.ArrayOf(DESSERT), 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CHOCOLATE_CAKE), 0);
        maker.Declare("y", maker.ArrayOf(SCONE), 0);
        maker.Forward();

        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CHOCOLATE_CAKE), 0);
        maker.Declare("y", maker.ArrayOf(PrimitiveType.INT_TYPE), 0);
        maker.Forward();

        MakerMethod method;
        maker.Method("moorge", PrimitiveType.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", ClassType.OBJECT_TYPE, 0);
        maker.Declare("y", ClassType.OBJECT_TYPE, 0);
        maker.Forward();

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(DESSERT).getValue()).Push(maker.ArrayOf(DESSERT).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(java.lang.Object, java.lang.Object)", method.toFullString());
    }

    class Dessert {}
    class Cake extends Dessert {}
    class Scone extends Dessert {}
    class ChocolateCake extends Cake {}
    class ButteredScone extends Scone {}
    public interface MoorgeInterface
    {
        public void moorge(Dessert d, Scone s);
        public void moorge(Cake c, Dessert d);
        public void moorge(ChocolateCake cc, Scone s);
        public void moorge(ChocolateCake cc, int x);
    }
    public static final String MOORGE_CHOC_INT      = "void moorge(au.com.illyrian.classmaker.members.MakerResolveMethodTest$ChocolateCake, int)";
    public static final String MOORGE_DESSERT_SCONE = "void moorge(au.com.illyrian.classmaker.members.MakerResolveMethodTest$Dessert, au.com.illyrian.classmaker.members.MakerResolveMethodTest$Scone)";
    public static final String MOORGE_CHOC_SCONE    = "void moorge(au.com.illyrian.classmaker.members.MakerResolveMethodTest$ChocolateCake, au.com.illyrian.classmaker.members.MakerResolveMethodTest$Scone)";
    public static final String MOORGE_CAKE_DESSERT  = "void moorge(au.com.illyrian.classmaker.members.MakerResolveMethodTest$Cake, au.com.illyrian.classmaker.members.MakerResolveMethodTest$Dessert)";

    public Type type(Class javaClass)
    {
        return  factory.classToType(javaClass);
    }
    
    public Value value(Class javaClass) {
        return type(javaClass).getValue();
    }

    public void testResolveMethod()
    {
        int i = 0;
        MethodResolver resolver = factory.getMethodResolver();
        ClassType classType = new ClassType("Test", ClassType.OBJECT_TYPE);
        classType.setJavaClass(MoorgeInterface.class);
        MakerMethod [] methods = factory.getMethods(classType);
        String name = "moorge";
        CallStack actualParameters =
            maker.Push(value(ChocolateCake.class)).Push(value(ButteredScone.class));
        Vector<MakerMethod> candidates = resolver.findMethods(methods, name);
//        public void moorge(Dessert d, Scone s);
//        public void moorge(Cake c, Dessert d);
//        public void moorge(ChocolateCake cc, Scone s);
//        public void moorge(ChocolateCake cc, int x);
        assertEquals("findMethods", 4, candidates.size());
        methods = candidates.toArray(ClassMakerFactory.METHOD_ARRAY);
        assertContains("Expected method", MOORGE_CHOC_INT, methods);
        assertContains("Expected method", MOORGE_CHOC_SCONE, methods);
        assertContains("Expected method", MOORGE_CAKE_DESSERT, methods);
        assertContains("Expected method", MOORGE_DESSERT_SCONE, methods);

        //      Remove :   public void moorge(ChocolateCake cc, int x);
        resolver.removeIncompatableCandidates(candidates, actualParameters);
        assertEquals("removeIncompatableCandidates", 3, candidates.size());
        methods = candidates.toArray(ClassMakerFactory.METHOD_ARRAY);
        i=0;
        assertContains("Expected method", MOORGE_CHOC_SCONE, methods);
        assertContains("Expected method", MOORGE_CAKE_DESSERT, methods);
        assertContains("Expected method", MOORGE_DESSERT_SCONE, methods);

        resolver.removeLessSpecificCandidates(candidates);
        assertEquals("removeLessSpecificCandidates", 1, candidates.size());

        resolver.removeLessSpecificReturnType(candidates);
        assertEquals("removeLessSpecificReturnType", 1, candidates.size());

        assertEquals("Should be one candidate", 1, candidates.size());

        int mod = ClassMaker.ACC_PUBLIC | ClassMaker.ACC_ABSTRACT;
        MakerMethod method = candidates.firstElement();
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toFullString());

    }
    
    private void assertContains(String msg, String target, Object [] values)
    {
        StringBuffer buff = new StringBuffer();
        for (int i=0; i<values.length; i++)
            if (target.equals(values[i].toString()))
                return;
            else
                buff.append("\n").append(values[i]);
        fail(msg + ": " + target + buff.toString());
    }

    public void testResolveJavaInterfaceMethods() throws Exception
    {
        ClassType moorge = maker.classToClassType(MoorgeInterface.class);

        int mod = ClassMaker.ACC_PUBLIC | ClassMaker.ACC_ABSTRACT;
        MakerMethod method;
        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert.class)).Push(value(Scone.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", Dessert.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(Dessert.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", Cake.class, Dessert.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(ButteredScone.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(byte.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, int.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert.class)).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", Dessert.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(value(Scone.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toFullString());

        try {
            maker.resolveMethod(moorge, "dummy", null);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Class contains no method called \'dummy\'", ex.getMessage());
        }
    }

    public class MoorgeClass
    {
        public void moorge(Dessert d, Scone s) {}
        public void moorge(Cake c, Dessert d){}
        public void moorge(ChocolateCake cc, Scone s){}
        public void moorge(ChocolateCake cc, int x){}
    }

    public void testResolveJavaClassMethods() throws Exception
    {
        ClassType moorge = maker.classToClassType(MoorgeClass.class);

        MakerMethod method;
        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert.class)).Push(value(Scone.class)));
        assertEquals("Did not resolve method: ", toString("moorge", Dessert.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(Dessert.class)));
        assertEquals("Did not resolve method: ", toString("moorge", Cake.class, Dessert.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(ButteredScone.class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(byte.class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, int.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert.class)).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", Dessert.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(value(Scone.class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, Scone.class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, Scone.class), method.toFullString());

        try {
            maker.resolveMethod(moorge, "dummy", null);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Class contains no method called \'dummy\'", ex.getMessage());
        }
    }

    public class MoorgeArray
    {
        public void moorge(Dessert[] d, Scone[] s) {}
        public void moorge(Cake[] c, Dessert[] d){}
        public void moorge(ChocolateCake[] cc, Scone[] s){}
    }

    public void testResolveJavaArrayMethods() throws Exception
    {
        ClassType moorge = maker.classToClassType(MoorgeArray.class);

        MakerMethod method;
        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert[].class)).Push(value(Scone[].class)));
        assertEquals("Did not resolve method: ", toString("moorge", Dessert[].class, Scone[].class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake[].class)).Push(value(Dessert[].class)));
        assertEquals("Did not resolve method: ", toString("moorge", Cake[].class, Dessert[].class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake[].class)).Push(value(ButteredScone[].class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake[].class, Scone[].class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert[].class)).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", Dessert[].class, Scone[].class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(value(Scone[].class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake[].class, Scone[].class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake[].class)).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake[].class, Scone[].class), method.toFullString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassType.NULL_TYPE.getValue()).Push(ClassType.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake[].class, Scone[].class), method.toFullString());

        try {
            maker.resolveMethod(moorge, "dummy", null);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Class contains no method called \'dummy\'", ex.getMessage());
        }
    }

    protected String toString(int modifiers, String name, Class left, Class right)
    {
        return ClassMaker.toModifierString(modifiers) + "void " + name 
                + "(" + ClassMaker.classToName(left) + ", " + ClassMaker.classToName(right) + ")";
    }

    protected String toString(String name, Class left, Class right)
    {
        return toString(ClassMaker.ACC_PUBLIC, name, left, right);
    }

    protected void failToResolveMethod(String name, CallStack params)
    {
    	String signature = ClassMaker.toMethodString(name, (Type[])params.toArray(), null);
        try {
            maker.resolveMethod(maker.getClassType(), name, params);
            fail("Should not resolve method for " + signature);
        } catch (ClassMakerException ex) {
            assertEquals("Cannot resolve method call : " + signature, ex.getMessage());
        }
    }
    
    public void testFormalParameters()
    {
        MakerMethod method = new MakerMethod(null, null, (Type)null, (short)0);
        assertEquals("Wrong default method", "()V", method.createSignature(ClassMakerFactory.TYPE_ARRAY, PrimitiveType.VOID_TYPE));
        assertEquals("Wrong integer method", "()I", method.createSignature(ClassMakerFactory.TYPE_ARRAY, PrimitiveType.INT_TYPE));
        Type[] emptyParam = {};
        assertEquals("Wrong default method", "()V", method.createSignature(emptyParam, PrimitiveType.VOID_TYPE));
        assertEquals("Wrong integer method", "()I", method.createSignature(emptyParam, PrimitiveType.INT_TYPE));
        Type[] intParam = {PrimitiveType.INT_TYPE};
        assertEquals("Wrong default method", "(I)V", method.createSignature(intParam, PrimitiveType.VOID_TYPE));
        assertEquals("Wrong integer method", "(I)I", method.createSignature(intParam, PrimitiveType.INT_TYPE));
        Type[] intIntParam = {PrimitiveType.INT_TYPE, PrimitiveType.INT_TYPE};
        assertEquals("Wrong default method", "(II)V", method.createSignature(intIntParam, PrimitiveType.VOID_TYPE));
        assertEquals("Wrong integer method", "(II)I", method.createSignature(intIntParam, PrimitiveType.INT_TYPE));
    }


}
