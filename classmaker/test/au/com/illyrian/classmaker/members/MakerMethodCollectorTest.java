package au.com.illyrian.classmaker.members;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.types.ClassType;

public class MakerMethodCollectorTest extends TestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();

    public void testCollectAllMethodsObjectType() {
        MakerMethodCollector collector = new MakerMethodCollector();
        collector.includeClassMethods(ClassMakerFactory.OBJECT_TYPE);
        MakerMethod [] methods = collector.toArray();
        assertNotNull("MakerMethodCollector.toArray", methods);
        assertEquals("MakerMethodCollector.size", 12, methods.length);
        assertEquals("OBJECT_TYPE.getDeclaredMethods", 12, ClassMakerFactory.OBJECT_TYPE.getDeclaredMethods().length);
    }

    public void testCollectAllMethodsStringType() {
        MakerMethodCollector collector = new MakerMethodCollector();
        collector.includeClassMethods(ClassMakerFactory.STRING_TYPE);
        MakerMethod [] methods = collector.toArray();
        assertNotNull("MakerMethodCollector.toArray", methods);
        assertEquals("MakerMethodCollector.size", 82, methods.length);
    }

    public void testCollectAllMethodsClonableType() {
        MakerMethodCollector collector = new MakerMethodCollector();
        collector.includeClassMethods(ClassMakerFactory.CLONEABLE_TYPE);
        collector.includeInterfaceMethods(ClassMakerFactory.CLONEABLE_TYPE);
        MakerMethod [] methods = collector.toArray();
        assertNotNull("MakerMethodCollector.toArray", methods);
        assertEquals("MakerMethodCollector.size", 0, methods.length);
        assertEquals("CLONEABLE_TYPE.getDeclaredMethods", 0, ClassMakerFactory.CLONEABLE_TYPE.getDeclaredMethods().length);
    }

    public void testCollectAllMethodsRunnableType() {
        ClassType runnableType = factory.classToType(Runnable.class).toClass();

        assertEquals("Runnable.class.size", 1, Runnable.class.getDeclaredMethods().length);        
        assertEquals("ClassType(Runnable).getDeclaredMethods", 1, runnableType.getDeclaredMethods().length);

        MakerMethodCollector allMathods = new MakerMethodCollector();
        allMathods.includeClassMethods(runnableType);
        assertEquals("MakerMethodCollector.size", 1, allMathods.toArray().length);
        
        MakerMethodCollector ifaceMathods = new MakerMethodCollector();
        ifaceMathods.includeInterfaceMethods(runnableType);
        assertEquals("MakerMethodCollector.size", 1, ifaceMathods.toArray().length);
    }

}
