package au.com.illyrian.classmaker.proxy;

import static org.junit.Assert.*;

import org.junit.Test;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.ClassMakerText;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.MakerClassType;

public class ActorInterfaceBuilderTest {
    ClassMakerFactory factory = new ClassMakerFactory();

    @Test
    public void testSimpleInterface() throws InstantiationException, IllegalAccessException {
        ClassMaker maker = factory.createClassMaker();
        ActorInterfaceBuilder builder = new ActorInterfaceBuilder(maker);
        builder.withInterface(Runnable.class);
        builder.build();
        maker.EndClass(); // Create default constructor
        MakerClassType classType = maker.getClassType();
        assertEquals("RunnableActor", classType.getSimpleName());
        assertEquals("au.com.illyrian.classmaker.proxy", classType.getPackageName());
        MakerMethod [] methods = classType.getDeclaredMethods();
        assertEquals("Number of Methods", 4, methods.length);
        MakerMethod [] constructors = classType.getConstructors();
        assertEquals("Number of constructors", 1, constructors.length);
        Class<Runnable> testClass = maker.defineClass();
//        Runnable test = testClass.newInstance();
//        test.run();
    }

    @Test
    public void testRunnableActor() throws InstantiationException, IllegalAccessException {
        ClassMaker maker = factory.createClassMaker();
        ActorInterfaceBuilder builder = new ActorInterfaceBuilder(maker);
        builder.withInterface(Runnable.class);
        builder.build();
        maker.EndClass(); // Create default constructor
    }
    
    @Test
    public void testTestInterface() throws InstantiationException, IllegalAccessException {
        ClassMaker maker = factory.createClassMaker();
        ActorInterfaceBuilder builder = new ActorInterfaceBuilder(maker);
        builder.withInterface(TestInterface.class);
        builder.build();
//        Class<TestInterface> testClass = maker.defineClass();
//        TestInterface test = testClass.newInstance();
//        test.setCharacteristics("Fred", 32, 1.75f, true);
    }
}
