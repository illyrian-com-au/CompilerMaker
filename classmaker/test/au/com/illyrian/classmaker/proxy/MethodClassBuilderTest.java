package au.com.illyrian.classmaker.proxy;

import java.io.IOException;

import junit.framework.TestCase;

import org.mockito.Mockito;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.proxy.MethodClassBuilder.Apply;
import au.com.illyrian.classmaker.types.ClassType;

public class MethodClassBuilderTest extends TestCase {
    
    ClassMakerFactory factory = new ClassMakerFactory();

    static public interface CharacterIfc {
        void setCharacteristics(String name, int age, float height, boolean male);
    }
    
    static public class CharacterImpl implements CharacterIfc {
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
        
        private String quote(String value) {
            if (value != null) {
                return '\"' + value + '\"';
            } else {
                return "null";
            }
        }
        
        public String toString() {
            return "setCharacteristics(" + quote(name) + ", " + age + ", " + height + ", " + male + ")";
        }
    }

    static public class ApplyCharacterImpl extends CharacterImpl implements Apply<CharacterIfc> {
        public void apply(CharacterIfc ref) {
            ref.setCharacteristics(name, age, height, male);
        }
    }
    
    public void testCharacterMethod() throws IOException, InstantiationException, IllegalAccessException {
        ClassMaker maker = factory.createClassMaker();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        builder.withInterface(CharacterIfc.class, "setCharacteristics");
        builder.build();
        
        Class<CharacterIfc> testClass = maker.defineClass();
        CharacterIfc test = testClass.newInstance();
        test.setCharacteristics("Bruce", 50, 1.8f, true);
        assertEquals("setCharacteristics(\"Bruce\", 50, 1.8, true)", test.toString());
    }

    public void testApplyMethod() throws IOException, InstantiationException, IllegalAccessException {
        ClassMaker maker = factory.createClassMaker();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        builder.withInterface(CharacterIfc.class, "setCharacteristics");
        builder.build();
        
        Class<Apply<CharacterIfc>> testClass = maker.defineClass();
        Apply<CharacterIfc> test = testClass.newInstance();
        assertEquals("setCharacteristics(null, 0, 0.0, false)", test.toString());
        ((CharacterIfc)test).setCharacteristics("Bruce", 42, 1.78f, true);
        CharacterImpl character = new CharacterImpl();
        test.apply(character);
        assertEquals("setCharacteristics(\"Bruce\", 42, 1.78, true)", character.toString());
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
        ClassMaker maker = factory.createClassMaker();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        ClassType iface = factory.classToType(CharacterIfc.class).toClass();
        builder.withInterface(iface, "setCharacteristics");
        builder.build();
        
        Class<Apply<CharacterIfc>> testClass = maker.defineClass();
        Apply<CharacterIfc> test = testClass.newInstance();
        ((CharacterIfc)test).setCharacteristics("Fred", 23, 1.75f, true);
        assertEquals("setCharacteristics(\"Fred\", 23, 1.75, true)", test.toString());
        CharacterIfc mockCharacter = Mockito.mock(CharacterIfc.class);
        test.apply(mockCharacter);
        Mockito.verify(mockCharacter).setCharacteristics("Fred", 23, 1.75f, true);
    }
}
