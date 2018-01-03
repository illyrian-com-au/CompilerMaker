package au.com.illyrian.classmaker.reflect;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.illyrian.classmaker.reflect.ReflectionUtils;
import junit.framework.TestCase;

public class GenericReflectionTest extends TestCase {

    static public interface Character {
        void setCharacteristics(String name, int age, float height, boolean male);
    }

    static interface Apply<T> {
        void apply(T obj);
    }
    
    static interface Setter<T> {
        void set(T obj);
        T get();
    }
    
    static class ApplyCharacter implements Apply<Character> {
        public void apply(Character character) {
            character.setCharacteristics("Betty", 21, 1.5f, false);
        }
    }
    
    static class SetterCharacter implements Setter<Character> {
        Character value;
        
        public void set(Character character) {
            value = character;
        }
        
        public Character get() {
            return value;
        }
    }
    
    public void testCharacterInformation() {
        Class<Character> testClass = Character.class;
        Type[] ifaces = testClass.getGenericInterfaces();
        assertEquals(0, ifaces.length);

        Type base = testClass.getGenericSuperclass();
        assertNull(base);
    }

    public void testApplyInterface() {
        Class<Apply> javaClass = Apply.class;
        assertEquals(0, javaClass.getGenericInterfaces().length);
        assertNull(javaClass.getGenericSuperclass());
        assertNull(javaClass.getSuperclass());
        
        // Determine class generic parameters
        TypeVariable [] javaClassParam = javaClass.getTypeParameters();
        assertEquals(1, javaClassParam.length);
        assertEquals("T", javaClassParam[0].getName());
        GenericDeclaration javaClassDecl = javaClassParam[0].getGenericDeclaration();
        assertEquals("interface au.com.illyrian.classmaker.reflect.GenericReflectionTest$Apply", javaClassDecl.toString());
        assertEquals(1, javaClassDecl.getTypeParameters().length);
        assertEquals("T", javaClassDecl.getTypeParameters()[0].getName());
        assertEquals(1, javaClassParam[0].getBounds().length);
        assertEquals("class java.lang.Object", javaClassParam[0].getBounds()[0].toString());
        
        // Determine method generic parameters
        java.lang.reflect.Method [] javaMethods = javaClass.getDeclaredMethods();
        assertEquals(1, javaMethods.length);
        java.lang.reflect.Method javaMethod = javaMethods[0];
        assertEquals(1, javaMethod.getParameterTypes().length);
        Class param = javaMethod.getParameterTypes()[0];
        assertEquals("java.lang.Object", param.getName());
        assertEquals(1, javaMethod.getGenericParameterTypes().length);
        java.lang.reflect.Type generic = javaMethod.getGenericParameterTypes()[0];
        assertEquals("T", generic.toString());
    }
    
    Map <String, Class<?>> getActualTypes(Type[] gfaces) {
        Map <String, Class<?>> genericMap = null;
        for (Type type : gfaces) {
            Map <String, Class<?>> nextMap = getActualTypes(type);
            if (genericMap == null) {
                genericMap = nextMap;
            } else {
                genericMap.putAll(nextMap);
            }
        }
        return genericMap;
    }
    
    Map <String, Class<?>> getActualTypes(Type type) {
        Map <String, Class<?>> genericMap = new HashMap<>();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] actualTypes = pType.getActualTypeArguments();
            TypeVariable[] paramsTypes = ((Class) pType.getRawType()).getTypeParameters();
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] instanceof Class) {
                    Class<?> actualClass = (Class) actualTypes[i];
                    String genericName = paramsTypes[i].getName();
                    genericMap.put(genericName, actualClass);
                } else if (actualTypes[i] instanceof TypeVariable) {
                    TypeVariable aType = (TypeVariable)actualTypes[i];
                    TypeVariable bType = paramsTypes[i];
                    Type bounds = aType.getBounds()[0];
                    if (bounds instanceof Class) {
                        genericMap.put(aType.getName(), (Class)bounds);
                    }
                }
            }
        }
        return genericMap;
    }

    public void testApplyCharacter() {
        Class<ApplyCharacter> testClass = ApplyCharacter.class;
        Type[] gfaces = testClass.getGenericInterfaces();
        assertEquals(1, gfaces.length);
        assertTrue(gfaces[0] instanceof ParameterizedType);
        ParameterizedType pType0 = (ParameterizedType)gfaces[0]; 
        Type [] args = pType0.getActualTypeArguments(); 
        assertEquals(1, args.length);
        assertTrue(args[0] instanceof Class);
        Class paraClass = (Class)args[0];
        assertEquals("au.com.illyrian.classmaker.reflect.GenericReflectionTest$Character", paraClass.getName());
        TypeVariable [] rawClassParam = ((Class)pType0.getRawType()).getTypeParameters();
        assertEquals("T", rawClassParam[0].getName());
        
        Class [] ifaces = testClass.getInterfaces();
        assertEquals(1, ifaces.length);
        Class javaIface = ifaces[0];
        assertEquals("au.com.illyrian.classmaker.reflect.GenericReflectionTest$Apply", javaIface.getName());
        assertEquals(Apply.class, testClass.getInterfaces()[0]);
        //assertEquals("", testClass.getGenericInterfaces()[0]);
        
        // Determine class generic parameters
        TypeVariable [] javaIfaceParam = javaIface.getTypeParameters();
        assertEquals(1, javaIfaceParam.length);
        assertEquals("T", javaIfaceParam[0].getName());

        Type baseType = testClass.getGenericSuperclass();
        assertNotNull(baseType);
        assertEquals("class java.lang.Object", baseType.toString());
        assertEquals(testClass.getGenericSuperclass(), testClass.getSuperclass());
        
        Map <String, Class<?>> paramMap = getActualTypes(testClass.getGenericInterfaces());
        
        // Determine class generic parameters
        TypeVariable [] javaClassParam = testClass.getTypeParameters();
        assertEquals(0, javaClassParam.length);

        java.lang.reflect.Method methods [] = testClass.getDeclaredMethods();
        assertEquals(2, methods.length);
        assertEquals("public void au.com.illyrian.classmaker.reflect.GenericReflectionTest$ApplyCharacter.apply(au.com.illyrian.classmaker.reflect.GenericReflectionTest$Character)", methods[0].toString());
        assertEquals("public void au.com.illyrian.classmaker.reflect.GenericReflectionTest$ApplyCharacter.apply(java.lang.Object)", methods[1].toString());
    }

    public void testHashMap() {
        Map <String, Character> instance = new HashMap<>();
        Class<?> testClass = instance.getClass();
        Map <String, Class<?>>paramMap = getActualTypes(testClass.getGenericInterfaces());
        assertNotNull(paramMap);
        assertEquals(Object.class, paramMap.get("K"));
        assertEquals(Object.class, paramMap.get("V"));

        Map <String, Class<?>>superMap = getActualTypes(testClass.getGenericSuperclass());
        assertNotNull(superMap);
        assertEquals(Object.class, superMap.get("K"));
        assertEquals(Object.class, superMap.get("V"));
        
        // Determine class generic parameters
        TypeVariable [] javaClassParam = testClass.getTypeParameters();
        assertEquals(2, javaClassParam.length);
        assertEquals("K", javaClassParam[0].getName());
        assertEquals("V", javaClassParam[1].getName());
        
        java.lang.reflect.Method [] methods = testClass.getDeclaredMethods();
        assertTrue(methods.length > 0);
    }

    public void testSetterInterface() {
        Class<Setter> testClass = Setter.class;
        assertEquals(0, testClass.getGenericInterfaces().length);
        assertNull(testClass.getGenericSuperclass());
        assertNull(testClass.getSuperclass());
    }

    public void testSetterCharacter() {
        Class<SetterCharacter> testClass = SetterCharacter.class;
        Type[] ifaces = testClass.getGenericInterfaces();
        assertEquals(1, testClass.getGenericInterfaces().length);
        //assertNotEquals(testClass.getGenericInterfaces()[0], testClass.getInterfaces()[0]);
        assertTrue(ifaces[0] instanceof ParameterizedType);
        ParameterizedType pType0 = (ParameterizedType)ifaces[0];
        Type [] args = pType0.getActualTypeArguments();
        assertEquals(1, args.length);
        assertTrue(args[0] instanceof Class);
        Class paraClass = (Class)args[0];
        assertEquals("au.com.illyrian.classmaker.reflect.GenericReflectionTest$Character", paraClass.getName());

        Type baseType = testClass.getGenericSuperclass();
        assertNotNull(baseType);
        assertTrue(baseType instanceof Object);
        assertEquals(testClass.getGenericSuperclass(), testClass.getSuperclass());
        
        assertEquals(Setter.class, ReflectionUtils.getClass(ifaces[0]));
        assertEquals(Object.class, ReflectionUtils.getClass(testClass.getGenericSuperclass()));
        List<Class<?>> list = ReflectionUtils.getTypeArguments(Setter.class, testClass);
        assertEquals(0, list.size());
        
    }
}
