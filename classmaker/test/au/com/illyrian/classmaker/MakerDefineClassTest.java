package au.com.illyrian.classmaker;

import junit.framework.TestCase;

public class MakerDefineClassTest extends TestCase {
    ClassMakerFactory factory = new ClassMakerFactory();

    public void testParsePackage() throws Exception {

        ClassMaker maker = factory.createClassMaker();
        maker.setPackageName("test");
        maker.setSimpleClassName("Test");
        Class clazz = maker.defineClass();
        Object instance = clazz.newInstance();

        assertEquals("test", maker.getPackageName());
        assertEquals("Test", maker.getSimpleClassName());
        assertEquals("Extends", ClassMakerFactory.OBJECT_TYPE, maker.getExtendsType());
        assertEquals("Class modifiers", ClassMakerConstants.ACC_PUBLIC, maker.getModifiers());
        assertEquals("Number of interfaces", 0, maker.getDeclaredInterfaces().length);
        assertEquals("Number of constructors", 1, maker.getDeclaredConstructors().length);
        assertEquals("Number of fields", 0, maker.getDeclaredFields().length);
        assertEquals("Number of methods", 0, maker.getDeclaredMethods().length);
        assertNotNull("test.Test", clazz);
        assertEquals("Test", clazz.getSimpleName());
        assertEquals("test.Test", clazz.getName());
        assertNotNull("Package name", clazz.getPackage());
        assertEquals("test", clazz.getPackage().getName());
        assertEquals("Super class", Object.class, clazz.getSuperclass());
        assertEquals("Class modifiers", ClassMakerConstants.ACC_PUBLIC, clazz.getModifiers());
        assertEquals("Number of interfaces", 0, clazz.getInterfaces().length);
        assertEquals("Number of constructors", 1, clazz.getDeclaredConstructors().length);
        assertEquals("Number of fields", 0, clazz.getDeclaredFields().length);
        assertEquals("Number of methods", 0, clazz.getDeclaredMethods().length);
        assertNotNull("test.Test", instance);
    }
}
