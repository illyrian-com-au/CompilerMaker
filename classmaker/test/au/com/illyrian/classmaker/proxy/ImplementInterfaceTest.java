package au.com.illyrian.classmaker.proxy;

import java.util.Vector;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.members.MakerMethod;
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
            public void visit(MakerMethod element) {
                methodList.add(element);
            }
        };
    }
    
    public void testImplementInterface() {
        impl.visit(TestInterface.class);
        
        assertEquals("method count", 3, methodList.size());
        assertEquals("void processFile(java.lang.String)", methodList.get(0).toShortString());
        assertEquals("void setCharacteristics(java.lang.String, int, float, boolean)", 
                methodList.get(1).toShortString());
        assertEquals("void incrementCounter(int)", methodList.get(2).toShortString());
    }

}
