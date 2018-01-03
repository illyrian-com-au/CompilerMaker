package au.com.illyrian.classmaker.proxy;

import java.io.IOException;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;

public class MethodClassBuilderTest extends TestCase {
    
    ClassMakerFactory factory = new ClassMakerFactory();

    static public interface Character {
        void setCharacteristics(String name, int age, float height, boolean male);
    }
    
    static public class CharacterImpl implements Character {
        public String name;
        public int age;
        public float height;
        public boolean male;
        
        public void setCharacteristics(String name, int age, float height, boolean male) {
            this.name = name;
            this.age = age;
            this.height = height;
            this.male = male;
        }
        
        public String toString() {
            return "setCharacteristics(" + name + ", " + age + ", " + height + ", " + male + ")";
        }
    }

    static public interface Apply<T> {
        void apply(T ref);
    }
    
    static public class ApplyCharacterImpl extends CharacterImpl implements Apply<Character> {
        public void apply(Character ref) {
            ref.setCharacteristics(name, age, height, male);
        }
    }
    
    public void testCharacterMethod() throws IOException, InstantiationException, IllegalAccessException {
        ClassMaker<Character> maker = factory.createClassMaker();
        maker.Implements(Character.class);
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        ClassType iface = factory.classToType(Character.class).toClass();
        MakerMethod method = iface.getMethods("setCharacteristics")[0];
        builder.setInterface(iface);
        builder.setMethod(method);
        builder.build();
        
        Class<Character> testClass = maker.defineClass();
        Character test = testClass.newInstance();
        test.setCharacteristics("Bruce", 50, 1.8f, true);
        assertEquals("setCharacteristics(Bruce, 50, 1.8, true)", test.toString());
    }

    public void testApplyMethod() throws IOException, InstantiationException, IllegalAccessException {
        ClassMaker<Apply<Character>> maker = factory.createClassMaker();
        maker.Implements(Apply.class);
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        ClassType iface = factory.classToType(Character.class).toClass();
        MakerMethod method = iface.getMethods("setCharacteristics")[0];
        builder.setInterface(iface);
        builder.setMethod(method);
        builder.build();
        
        Class<Apply<Character>> testClass = maker.defineClass();
        Apply<Character> test = testClass.newInstance();
        assertEquals("setCharacteristics(null, 0, 0.0, false)", test.toString());
        CharacterImpl character = new CharacterImpl();
        test.apply(character);
        assertEquals("setCharacteristics(null, 0, 0.0, false)", character.toString());
    }

    public void testApplyCharacterImpl() throws Exception {
        ApplyCharacterImpl ref = new ApplyCharacterImpl();
        ref.setCharacteristics("Bert", 21, 1.5f, true);
        CharacterImpl character = new CharacterImpl();
        ref.apply(character);
        assertEquals("Bert", character.name);
        assertEquals(21, character.age);
        assertEquals(1.5f, character.height);
        assertEquals(true, character.male);
    }

    public void testApplyImpl() throws Exception{
        ClassMaker<Apply<Character>> maker = factory.createClassMaker();
        maker.Implements(Apply.class);
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        ClassType iface = factory.classToType(Character.class).toClass();
        MakerMethod method = iface.getMethods("setCharacteristics")[0];
        builder.setInterface(iface);
        builder.setMethod(method);
        builder.build();
        
        Class<Apply<Character>> testClass = maker.defineClass();
        Apply<Character> test = testClass.newInstance();
        assertEquals("setCharacteristics(null, 0, 0.0, false)", test.toString());
        CharacterImpl character = new CharacterImpl();
        test.apply(character);
        assertEquals("setCharacteristics(null, 0, 0.0, false)", character.toString());
    }
}
