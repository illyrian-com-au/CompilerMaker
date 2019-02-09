package au.com.illyrian.classmaker.proxy;

import java.util.Vector;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.Type;
import junit.framework.TestCase;

public class ImplementInterfaceTest extends TestCase {

    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker;
    ImplementInterface impl;
    
    Vector<MakerMethod> methodList = new Vector<>();
    
    @Override
    public void setUp() {
        maker = factory.createClassMaker();
        Visitor<MakerMethod> visitor = createListVisitor();
        impl = new ImplementInterface(maker, visitor);
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
    
    public void testImplementInterface() {
        impl.visit(TestInterface.class);
        
        assertEquals("method count", 3, methodList.size());
        assertEquals("void processFile(java.lang.String)", find("processFile").toShortString());
        assertEquals("void setCharacteristics(java.lang.String, int, float, boolean)", 
                find("setCharacteristics").toShortString());
        assertEquals("void incrementCounter(int)", find("incrementCounter").toShortString());
    }
    
    public void testRunnableInterface() {
        impl.visit(Runnable.class);
        assertEquals("method count", 1, methodList.size());
        assertEquals("void run()", find("run").toShortString());
    }

    private MakerMethod find(String name) {
        for (MakerMethod method : methodList) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        fail("Could not find method: " + name);
        return null;
    }
}
