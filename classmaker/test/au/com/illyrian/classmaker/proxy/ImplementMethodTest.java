package au.com.illyrian.classmaker.proxy;

import java.util.Vector;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;

public class ImplementMethodTest extends TestCase {

    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker;
    ImplementMethod impl;
    
    Vector<MakerMethod> methodList = new Vector<>();
    
    @Override
    public void setUp() {
        maker = factory.createClassMaker();
        maker.setClassModifiers(ClassMakerConstants.ACC_PUBLIC);
        maker.setPackageName(getClass().getPackage().getName());
        maker.setSimpleClassName("TestProxy");
        
        Visitor<MakerMethod> visitor = createListVisitor();
        impl = new ImplementMethod(maker, visitor);
    }
    
    Visitor<MakerMethod> createListVisitor() {
        return new Visitor<MakerMethod>() {
            @Override
            public Type visit(MakerMethod element) {
                methodList.add(element);
                return null;
            }
        };
    }
    
    public void testImplementProcessFileMethod() {
        ClassType iface = maker.classToClassType(TestInterface.class);
        MakerMethod [] methods = iface.getMethods("processFile");        
        assertEquals("method count", 1, methods.length);
        MakerMethod method = methods[0];
        assertEquals("public abstract void processFile(java.lang.String)", method.toString());
        
        impl.visit(method);
        Class<?> genClass = maker.defineClass();
        java.lang.reflect.Method [] genMethods = genClass.getDeclaredMethods();
        // FIXME get methods from maker.
        assertEquals("method count", 1, genMethods.length);
        assertEquals("public void au.com.illyrian.classmaker.proxy.TestProxy.processFile(java.lang.String)", genMethods[0].toString());
    }

    public void testImplementIncrementCounterMethod() {
        ClassType iface = maker.classToClassType(TestInterface.class);
        MakerMethod [] methods = iface.getMethods("incrementCounter");        
        assertEquals("method count", 1, methods.length);
        MakerMethod method = methods[0];
        assertEquals("public abstract void incrementCounter(int)", method.toString());
        
        impl.visit(method);
        Class<?> genClass = maker.defineClass();
        java.lang.reflect.Method [] genMethods = genClass.getDeclaredMethods();
        // FIXME get methods from maker.
        assertEquals("method count", 1, genMethods.length);
        assertEquals("public void au.com.illyrian.classmaker.proxy.TestProxy.incrementCounter(int)", genMethods[0].toString());
    }

    public void testImplementSetCharacteristicsMethod() {
        ClassType iface = maker.classToClassType(TestInterface.class);
        MakerMethod [] methods = iface.getMethods("setCharacteristics");        
        assertEquals("method count", 1, methods.length);
        MakerMethod method = methods[0];
        assertEquals("public abstract void setCharacteristics(java.lang.String, int, float, boolean)", method.toString());
        
        impl.visit(method);
        Class<?> genClass = maker.defineClass();
        java.lang.reflect.Method [] genMethods = genClass.getDeclaredMethods();
        assertEquals("method count", 1, genMethods.length);
        assertEquals("public void au.com.illyrian.classmaker.proxy.TestProxy.setCharacteristics(java.lang.String,int,float,boolean)", 
                genMethods[0].toString());
    }

    public void testImplementMethods() throws Exception{
        ClassType iface = maker.classToClassType(TestInterface.class);
        ImplementInterface visitor = new ImplementInterface(maker, impl);
        visitor.visit(iface);

        Class<?> genClass = maker.defineClass();
        assertEquals("Number of methods", 3, methodList.size());
        assertEquals("public abstract void processFile(java.lang.String)", find("processFile").toString());
        assertEquals("public abstract void setCharacteristics(java.lang.String, int, float, boolean)", find("setCharacteristics").toString());
        assertEquals("public abstract void incrementCounter(int)", find("incrementCounter").toString());
        TestInterface obj = (TestInterface)genClass.newInstance();
        obj.incrementCounter(1);
        obj.processFile("fred.txt");
        obj.setCharacteristics("Wilber", 45, 1.85f, true);
    }

    private MakerMethod find(String name) {
        for (MakerMethod method : methodList) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }
}
