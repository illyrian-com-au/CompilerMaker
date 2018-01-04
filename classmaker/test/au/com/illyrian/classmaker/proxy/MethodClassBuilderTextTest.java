package au.com.illyrian.classmaker.proxy;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerText;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;

public class MethodClassBuilderTextTest extends TestCase {
    
    ClassMakerFactory factory = new ClassMakerFactory();

    private LineNumberReader getReader(String input)
    {
        return new LineNumberReader(new StringReader(input));
    }

    public void testBeginClass() throws IOException {
        ClassMakerText maker = new ClassMakerText();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        ClassType iface = factory.classToType(TestInterface.class).toClass();
        MakerMethod method = iface.getMethods("processFile")[0];
        builder.withInterface(iface);
        builder.withMethod(method);;
        builder.beginClass();
        LineNumberReader output = getReader(maker.toString());
        assertEquals("setSimpleClassName(\"TestInterface$ProcessFile\");", output.readLine());
        assertEquals("setPackageName(\"au.com.illyrian.classmaker.proxy\");", output.readLine());
        assertEquals("Implements(\"au.com.illyrian.classmaker.proxy.TestInterface\");", output.readLine());
        assertNull("More output to be read", output.readLine());
    }

    public void testDeclareFields() throws IOException {
        ClassMakerText maker = new ClassMakerText();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        ClassType iface = factory.classToType(TestInterface.class).toClass();
        MakerMethod method = iface.getMethods("processFile")[0];
        builder.withInterface(iface);
        builder.withMethod(method);
        String [] fieldNames = builder.createFieldNames("m", 1);
        builder.declareFields(method, fieldNames); // FIXME
        LineNumberReader output = getReader(maker.toString());
        assertEquals("  Declare(\"methodName\", java.lang.String, 0);", output.readLine());
        assertEquals("  Declare(\"m0\", \"java.lang.String\", ACC_PRIVATE);", output.readLine());
        assertNull("More output to be read", output.readLine());
    }

    public void testCreateGetter() throws IOException {
        ClassMakerText maker = new ClassMakerText();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        builder.createGetter("offset", ClassMakerFactory.INT_TYPE);
        LineNumberReader output = getReader(maker.toString());
        assertEquals("Method(\"getOffset\", \"int\", ACC_PUBLIC)", output.readLine());
        assertEquals("  Begin();", output.readLine());
        assertEquals("  Return(Get(This(), \"offset\"));", output.readLine());
        assertEquals("  End();", output.readLine());
        assertNull("More output to be read", output.readLine());
    }

    public void testCreateSetter() throws IOException {
        ClassMakerText maker = new ClassMakerText();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        builder.createSetter("offset", ClassMakerFactory.INT_TYPE);
        LineNumberReader output = getReader(maker.toString());
        assertEquals("Method(\"setOffset\", \"void\", ACC_PUBLIC)", output.readLine());
        assertEquals("  Declare(\"value\", \"int\", 0);", output.readLine());
        assertEquals("  Begin();", output.readLine());
        assertEquals("  Eval(Set(This(), \"offset\", Get(\"value\")));", output.readLine());
        assertEquals("  End();", output.readLine());
        assertNull("More output to be read", output.readLine());
    }

    public void testCreateMethod() throws IOException {
        ClassMakerText maker = new ClassMakerText();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        ClassType iface = factory.classToType(TestInterface.class).toClass();
        MakerMethod method = iface.getMethods("processFile")[0];
        builder.withInterface(iface);
        builder.withMethod(method);
        int len = method.getFormalTypes().length;
        String [] memberNames = builder.createFieldNames("p", len);
        builder.createMethod(method, memberNames);
        LineNumberReader output = getReader(maker.toString());
        assertEquals("Method(\"processFile\", \"void\", ACC_PUBLIC)", output.readLine());
        assertEquals("  Declare(\"$0\", \"java.lang.String\", 0);", output.readLine());
        assertEquals("  Begin();", output.readLine());
        assertEquals("  Return();", output.readLine());
        assertEquals("  End();", output.readLine());
        assertNull("More output to be read", output.readLine());
    }

    public void testCreateApply() throws IOException {
        ClassMakerText maker = new ClassMakerText();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        ClassType iface = factory.classToType(TestInterface.class).toClass();
        MakerMethod method = iface.getMethods("setCharacteristics")[0];
        int len = method.getFormalTypes().length;
        String [] memberNames = builder.createFieldNames("p", len);
        builder.createApply(iface, method, memberNames);
        LineNumberReader output = getReader(maker.toString());
        assertEquals("Method(\"apply\", \"void\", ACC_PUBLIC)", output.readLine());
        assertEquals("  Declare(\"reference\", \"au.com.illyrian.classmaker.proxy.TestInterface\", 0);", 
                output.readLine());
        assertEquals("  Begin();", output.readLine());
        String expr = "  Eval(Call(Get(\"reference\"), \"setCharacteristics\", "
                + "Push(Get(This(), \"p0\")).Push(Get(This(), \"p1\"))."
                + "Push(Get(This(), \"p2\")).Push(Get(This(), \"p3\"))));";
        assertEquals(expr, output.readLine());
        assertEquals("  End();", output.readLine());
        assertNull("More output to be read", output.readLine());
    }

    public void testCreateToString() throws IOException {
        ClassMakerText maker = new ClassMakerText();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        String [] none = {};
        builder.createToString("foobar", none, ClassMakerFactory.VOID_TYPE);
        LineNumberReader output = getReader(maker.toString());
        assertEquals("Method(\"toString\", \"java.lang.String\", ACC_PUBLIC)", output.readLine());
        assertEquals("  Begin();", output.readLine());
        assertEquals("  Return(Call(Call(Call(Call(New(java.lang.StringBuffer).Init(null), "
                + "\"append\", Push(Literal(\"foobar\"))), \"append\", Push(Literal(\"(\"))), "
                + "\"append\", Push(Literal(\")\"))), \"toString\", Push()));", output.readLine());
        assertEquals("  End();", output.readLine());
        assertNull("More output to be read", output.readLine());
    }

    public void testCreateProcessFileToString() throws IOException {
        ClassMakerText maker = new ClassMakerText();
        MethodClassBuilder builder = new MethodClassBuilder(maker);
        String [] fieldNames = {"a", "b"};
        builder.createToString("foobar", fieldNames, ClassMakerFactory.VOID_TYPE);
        LineNumberReader output = getReader(maker.toString());
        assertEquals("Method(\"toString\", \"java.lang.String\", ACC_PUBLIC)", output.readLine());
        assertEquals("  Begin();", output.readLine());
        assertEquals("  Return(Call(Call(Call(Call(Call(Call(Call(New(java.lang.StringBuffer).Init(null), "
                + "\"append\", Push(Literal(\"foobar\"))), \"append\", Push(Literal(\"(\"))), "
                + "\"append\", Push(Get(This(), \"a\"))), \"append\", Push(Literal(\", \"))), \"append\", Push(Get(This(), \"b\"))), "
                + "\"append\", Push(Literal(\")\"))), \"toString\", Push()));", output.readLine());
        assertEquals("  End();", output.readLine());
        assertNull("More output to be read", output.readLine());
    }
}
