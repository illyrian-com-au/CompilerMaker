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
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;
import au.com.illyrian.classmaker.util.MakerUtil;

public class MakerResolveMethodTest extends ClassMakerTestCase
{
    ClassMakerFactory factory;
    ClassMaker maker;
    MethodResolver resolver;
    final ClassType DESSERT = new ClassType("test.Dessert", ClassMakerFactory.OBJECT_TYPE);;
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
            addClassType(DESSERT); 
            addClassType(CAKE);
            addClassType(SCONE);
            addClassType(CHOCOLATE_CAKE);
            addClassType(BUTTERED_SCONE);
        }
    }

    // Generate default constructor
    public void defaultConstructor(ClassMaker maker) throws Exception
    {
        maker.Method("<init>", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Begin();
          maker.Init(maker.Super(), null);
        maker.Return();
        maker.End();
    }

    public void forwardPrimitiveMethods(Type type) throws Exception
    {
        maker.Method("eval", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Forward();

        maker.Method("eval", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", type, 0);
        maker.Forward();

        maker.Method("eval", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", type, 0);
        maker.Declare("y", type, 0);
        maker.Forward();
        
        maker.EndClass();
    }

    public void testResolveIntMethods() throws Exception
    {
    	forwardPrimitiveMethods(ClassMakerFactory.INT_TYPE);
        MakerMethod [] methods = maker.getClassType().getDeclaredMethods();
        assertEquals("Wrong number of methods found.", 3, methods.length);
        assertEquals("public void eval()", methods[0].toString());
        assertEquals("public void eval(int)", methods[1].toString());
        assertEquals("public void eval(int, int)", methods[2].toString());

        MakerMethod method;
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push());
        assertEquals("Did not find eval()", 0, method.getFormalTypes().length);

        // Check assignment of primitives
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push(ClassMakerFactory.INT_TYPE.getValue()));
        assertEquals("Did not find eval(int)", 1, method.getFormalTypes().length);
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push(ClassMakerFactory.BYTE_TYPE.getValue()));
        assertEquals("Did not find eval(int)", 1, method.getFormalTypes().length);
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push(ClassMakerFactory.SHORT_TYPE.getValue()));
        assertEquals("Did not find eval(int)", 1, method.getFormalTypes().length);
        method = maker.resolveMethod(maker.getClassType(), "eval", maker.Push(ClassMakerFactory.CHAR_TYPE.getValue()));
        assertEquals("Did not find eval(int)", 1, method.getFormalTypes().length);

        failToResolveMethod("eval", maker.Push(ClassMakerFactory.FLOAT_TYPE.getValue()));
        failToResolveMethod("eval", maker.Push(ClassMakerFactory.DOUBLE_TYPE.getValue()));
        failToResolveMethod("eval", maker.Push(ClassMakerFactory.LONG_TYPE.getValue()));

        method = maker.resolveMethod(maker.getClassType(), "eval", 
                maker.Push(ClassMakerFactory.INT_TYPE.getValue()).Push(ClassMakerFactory.INT_TYPE.getValue()));
        assertEquals("Did not find eval(int,int)", 2, method.getFormalTypes().length);
    }
    
    public void testResolveReferenceMethods() throws Exception
    {
        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", DESSERT, 0);
        maker.Declare("y", SCONE, 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", CAKE, 0);
        maker.Declare("y", DESSERT, 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", CHOCOLATE_CAKE, 0);
        maker.Declare("y", SCONE, 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", CHOCOLATE_CAKE, 0);
        maker.Declare("y", ClassMakerFactory.INT_TYPE, 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", ClassMakerFactory.OBJECT_TYPE, 0);
        maker.Declare("y", ClassMakerFactory.OBJECT_TYPE, 0);
        maker.Forward();
        
        maker.EndClass();

        MakerMethod method;
        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(DESSERT.getValue()).Push(SCONE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Dessert, test.Scone)", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(CHOCOLATE_CAKE.getValue()).Push(DESSERT.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Cake, test.Dessert)", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(CHOCOLATE_CAKE.getValue()).Push(BUTTERED_SCONE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, test.Scone)", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(CHOCOLATE_CAKE.getValue()).Push(ClassMakerFactory.BYTE_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, int)", method.toString());

    	failToResolveMethod("moorge", maker.Push(CAKE.getValue()).Push(SCONE.getValue()));

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(DESSERT.getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Dessert, test.Scone)", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(SCONE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, test.Scone)", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(CHOCOLATE_CAKE.getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, test.Scone)", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake, test.Scone)", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(DESSERT.getValue()).Push(DESSERT.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(java.lang.Object, java.lang.Object)", method.toString());
    }

    public void testResolveArrayReferenceMethods() throws Exception
    {
        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(DESSERT), 0);
        maker.Declare("y", maker.ArrayOf(SCONE), 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CAKE), 0);
        maker.Declare("y", maker.ArrayOf(DESSERT), 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CHOCOLATE_CAKE), 0);
        maker.Declare("y", maker.ArrayOf(SCONE), 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CHOCOLATE_CAKE), 0);
        maker.Declare("y", maker.ArrayOf(ClassMakerFactory.INT_TYPE), 0);
        maker.Forward();
        
        maker.EndClass();;

        MakerMethod method;
        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(DESSERT).getValue()).Push(maker.ArrayOf(SCONE).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Dessert[], test.Scone[])", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(CHOCOLATE_CAKE).getValue()).Push(maker.ArrayOf(DESSERT).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Cake[], test.Dessert[])", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(CHOCOLATE_CAKE).getValue()).Push(maker.ArrayOf(BUTTERED_SCONE).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake[], test.Scone[])", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(CHOCOLATE_CAKE).getValue()).Push(maker.ArrayOf(ClassMakerFactory.INT_TYPE).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake[], int[])", method.toString());

    	failToResolveMethod("moorge", maker.Push(maker.ArrayOf(CHOCOLATE_CAKE).getValue()).Push(maker.ArrayOf(ClassMakerFactory.BYTE_TYPE).getValue()));

    	failToResolveMethod("moorge", maker.Push(maker.ArrayOf(CAKE).getValue()).Push(maker.ArrayOf(SCONE).getValue()));

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(DESSERT).getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.Dessert[], test.Scone[])", method.toString());

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(maker.ArrayOf(SCONE).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(test.ChocolateCake[], test.Scone[])", method.toString());

    	failToResolveMethod("moorge", maker.Push(maker.ArrayOf(CAKE).getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));

    	failToResolveMethod("moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));
    }

    public void testResolveArrayToObjectMethods() throws Exception
    {
        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(DESSERT), 0);
        maker.Declare("y", maker.ArrayOf(SCONE), 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CAKE), 0);
        maker.Declare("y", maker.ArrayOf(DESSERT), 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CHOCOLATE_CAKE), 0);
        maker.Declare("y", maker.ArrayOf(SCONE), 0);
        maker.Forward();

        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", maker.ArrayOf(CHOCOLATE_CAKE), 0);
        maker.Declare("y", maker.ArrayOf(ClassMakerFactory.INT_TYPE), 0);
        maker.Forward();

        MakerMethod method;
        maker.Method("moorge", ClassMakerFactory.VOID_TYPE, ByteCode.ACC_PUBLIC);
        maker.Declare("x", ClassMakerFactory.OBJECT_TYPE, 0);
        maker.Declare("y", ClassMakerFactory.OBJECT_TYPE, 0);
        maker.Forward();
        
        maker.EndClass();

        method = maker.resolveMethod(maker.getClassType(), "moorge", maker.Push(maker.ArrayOf(DESSERT).getValue()).Push(maker.ArrayOf(DESSERT).getValue()));
        assertEquals("Did not resolve method: ", "public void moorge(java.lang.Object, java.lang.Object)", method.toString());
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
    public static final String MOORGE_CHOC_INT      = "public abstract void moorge(au.com.illyrian.classmaker.members.MakerResolveMethodTest$ChocolateCake, int)";
    public static final String MOORGE_DESSERT_SCONE = "public abstract void moorge(au.com.illyrian.classmaker.members.MakerResolveMethodTest$Dessert, au.com.illyrian.classmaker.members.MakerResolveMethodTest$Scone)";
    public static final String MOORGE_CHOC_SCONE    = "public abstract void moorge(au.com.illyrian.classmaker.members.MakerResolveMethodTest$ChocolateCake, au.com.illyrian.classmaker.members.MakerResolveMethodTest$Scone)";
    public static final String MOORGE_CAKE_DESSERT  = "public abstract void moorge(au.com.illyrian.classmaker.members.MakerResolveMethodTest$Cake, au.com.illyrian.classmaker.members.MakerResolveMethodTest$Dessert)";

    public Type type(Class javaClass)
    {
        return  factory.classToType(javaClass);
    }
    
    public Value value(Class javaClass) {
        return type(javaClass).getValue();
    }

    public void testResolveMethod()
    {
        MethodResolver resolver = factory.getMethodResolver();
        ClassType classType = new ClassType("Test", ClassMakerFactory.OBJECT_TYPE);
        classType.setFactory(factory);
        classType.setJavaClass(MoorgeInterface.class);
        //MakerMethod [] methods = classType.findMethods("moorge");
        MakerMethod [] methods = classType.getDeclaredMethods();
        CallStack actualParameters =  maker.Push(value(ChocolateCake.class)).Push(value(ButteredScone.class));
        Vector<MakerMethod> candidates = resolver.findMethods(methods, "moorge");
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
        assertContains("Expected method", MOORGE_CHOC_SCONE, methods);
        assertContains("Expected method", MOORGE_CAKE_DESSERT, methods);
        assertContains("Expected method", MOORGE_DESSERT_SCONE, methods);

        resolver.removeLessSpecificCandidates(candidates);
        assertEquals("removeLessSpecificCandidates", 1, candidates.size());

        resolver.removeLessSpecificReturnType(candidates);
        assertEquals("removeLessSpecificReturnType", 1, candidates.size());

        assertEquals("Should be one candidate", 1, candidates.size());

        int mod = ClassMakerConstants.ACC_PUBLIC | ClassMakerConstants.ACC_ABSTRACT;
        MakerMethod method = candidates.firstElement();
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toString());

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

        int mod = ClassMakerConstants.ACC_PUBLIC | ClassMakerConstants.ACC_ABSTRACT;
        MakerMethod method;
        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert.class)).Push(value(Scone.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", Dessert.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(Dessert.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", Cake.class, Dessert.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(ButteredScone.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(byte.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, int.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert.class)).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", Dessert.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(value(Scone.class)));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString(mod, "moorge", ChocolateCake.class, Scone.class), method.toString());

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
        assertEquals("Did not resolve method: ", toString("moorge", Dessert.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(Dessert.class)));
        assertEquals("Did not resolve method: ", toString("moorge", Cake.class, Dessert.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(ButteredScone.class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(value(byte.class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, int.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert.class)).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", Dessert.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(value(Scone.class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake.class)).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, Scone.class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake.class, Scone.class), method.toString());

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
        assertEquals("Did not resolve method: ", toString("moorge", Dessert[].class, Scone[].class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake[].class)).Push(value(Dessert[].class)));
        assertEquals("Did not resolve method: ", toString("moorge", Cake[].class, Dessert[].class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake[].class)).Push(value(ButteredScone[].class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake[].class, Scone[].class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(Dessert[].class)).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", Dessert[].class, Scone[].class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(value(Scone[].class)));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake[].class, Scone[].class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(value(ChocolateCake[].class)).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake[].class, Scone[].class), method.toString());

        method = maker.resolveMethod(moorge, "moorge", maker.Push(ClassMakerFactory.NULL_TYPE.getValue()).Push(ClassMakerFactory.NULL_TYPE.getValue()));
        assertEquals("Did not resolve method: ", toString("moorge", ChocolateCake[].class, Scone[].class), method.toString());

        try {
            maker.resolveMethod(moorge, "dummy", null);
            fail("Should throw ClassMakerException");
        } catch (ClassMakerException ex) {
            assertEquals("Class contains no method called \'dummy\'", ex.getMessage());
        }
    }

    protected String toString(int modifiers, String name, Class left, Class right)
    {
        return MakerUtil.toModifierString(modifiers) + "void " + name 
                + "(" + MakerUtil.classToName(left) + ", " + MakerUtil.classToName(right) + ")";
    }

    protected String toString(String name, Class left, Class right)
    {
        return toString(ClassMakerConstants.ACC_PUBLIC, name, left, right);
    }

    protected void failToResolveMethod(String name, CallStack params)
    {
    	String signature = MakerMethod.toMethodString(name, (Type[])params.toArray(), null, 0);
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
        assertEquals("Wrong default method", "()V", method.createSignature(ClassMakerFactory.TYPE_ARRAY, ClassMakerFactory.VOID_TYPE));
        assertEquals("Wrong integer method", "()I", method.createSignature(ClassMakerFactory.TYPE_ARRAY, ClassMakerFactory.INT_TYPE));
        Type[] emptyParam = {};
        assertEquals("Wrong default method", "()V", method.createSignature(emptyParam, ClassMakerFactory.VOID_TYPE));
        assertEquals("Wrong integer method", "()I", method.createSignature(emptyParam, ClassMakerFactory.INT_TYPE));
        Type[] intParam = {ClassMakerFactory.INT_TYPE};
        assertEquals("Wrong default method", "(I)V", method.createSignature(intParam, ClassMakerFactory.VOID_TYPE));
        assertEquals("Wrong integer method", "(I)I", method.createSignature(intParam, ClassMakerFactory.INT_TYPE));
        Type[] intIntParam = {ClassMakerFactory.INT_TYPE, ClassMakerFactory.INT_TYPE};
        assertEquals("Wrong default method", "(II)V", method.createSignature(intIntParam, ClassMakerFactory.VOID_TYPE));
        assertEquals("Wrong integer method", "(II)I", method.createSignature(intIntParam, ClassMakerFactory.INT_TYPE));
    }


}
