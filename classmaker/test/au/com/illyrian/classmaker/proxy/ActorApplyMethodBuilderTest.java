package au.com.illyrian.classmaker.proxy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mockito;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;

public class ActorApplyMethodBuilderTest {
    ClassMakerFactory factory = new ClassMakerFactory();
    
    public static interface SetCharacteristics {
        void setCharacteristics(String name, int age, float height, boolean male);
    }

    @Test
    public void testSetCharacteristics() throws InstantiationException, IllegalAccessException {
        ClassMaker maker = factory.createClassMaker();
        ClassType iface = factory.classToType(TestInterface.class).toClass();
        MakerMethod [] methods = iface.getMethods("setCharacteristics");
        assertEquals("setCharacteristics(...) methods", 1, methods.length);
        ActorApplyMethodBuilder builder = new ActorApplyMethodBuilder(maker);
        builder.withInterface(TestInterface.class);
        builder.withMethod(methods[0]);
        builder.build();
        maker.Implements(SetCharacteristics.class);
        Class<ActorApply<TestInterface>> testClass = maker.defineClass();
        assertEquals("au.com.illyrian.classmaker.proxy.TestInterface$SetCharacteristics", testClass.getName());
        ActorApply<TestInterface> testApply = testClass.newInstance();
        SetCharacteristics testInit = (SetCharacteristics)testApply;
        testInit.setCharacteristics("Fred", 32, 1.75f, true);
        assertEquals("setCharacteristics(\"Fred\", 32, 1.75, true)", testApply.toString());
        TestInterface reference = Mockito.mock(TestInterface.class);
        testApply.apply(reference);
        Mockito.verify(reference).setCharacteristics("Fred", 32, 1.75f, true);
    }

    public static interface ProcessFile {
        void processFile(String filename);
    }

    @Test
    public void testProcessFile() throws InstantiationException, IllegalAccessException {
        ClassMaker maker = factory.createClassMaker();
        ClassType iface = factory.classToType(TestInterface.class).toClass();
        MakerMethod [] methods = iface.getMethods("processFile");
        assertEquals("processFile(...) methods", 1, methods.length);
        ActorApplyMethodBuilder builder = new ActorApplyMethodBuilder(maker);
        builder.withInterface(TestInterface.class);
        builder.withMethod(methods[0]);
        builder.build();
        maker.Implements(ProcessFile.class);
        Class<ActorApply<TestInterface>> testClass = maker.defineClass();
        assertEquals("au.com.illyrian.classmaker.proxy.TestInterface$ProcessFile", testClass.getName());
        ActorApply<TestInterface> testApply = testClass.newInstance();
        ProcessFile testInit = (ProcessFile)testApply;
        testInit.processFile("Fred.txt");
        assertEquals("processFile(\"Fred.txt\")", testApply.toString());
        TestInterface reference = Mockito.mock(TestInterface.class);
        testApply.apply(reference);
        Mockito.verify(reference).processFile("Fred.txt");
    }

    public static interface IncrementCounter {
        void incrementCounter(int offset);
    }

    @Test
    public void testIncrementCounter() throws InstantiationException, IllegalAccessException {
        ClassMaker maker = factory.createClassMaker();
        ClassType iface = factory.classToType(TestInterface.class).toClass();
        MakerMethod [] methods = iface.getMethods("incrementCounter");
        assertEquals("incrementCounter(...) methods", 1, methods.length);
        ActorApplyMethodBuilder builder = new ActorApplyMethodBuilder(maker);
        builder.withInterface(TestInterface.class);
        builder.withMethod(methods[0]);
        builder.build();
        maker.Implements(IncrementCounter.class);
        Class<ActorApply<TestInterface>> testClass = maker.defineClass();
        assertEquals("au.com.illyrian.classmaker.proxy.TestInterface$IncrementCounter", testClass.getName());
        ActorApply<TestInterface> testApply = testClass.newInstance();
        IncrementCounter testInit = (IncrementCounter)testApply;
        testInit.incrementCounter(3);
        assertEquals("incrementCounter(3)", testApply.toString());
        TestInterface reference = Mockito.mock(TestInterface.class);
        testApply.apply(reference);
        Mockito.verify(reference).incrementCounter(3);
    }
}
