package au.com.illyrian.parser.maker;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.types.Value;
import au.com.illyrian.expressionparser.FuncA;
import au.com.illyrian.expressionparser.FuncABC;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.expr.AstExpressionPrecidenceParser;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class AssignParserMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    OperatorPrecidenceParser createIncrementParser()
    {
        AstExpressionPrecidenceParser parser = new AstExpressionPrecidenceParser();
        AstExpressionFactory factory = new AstExpressionFactory();
        parser.setAstExpressionFactory(factory);
        return parser;
    }
    
    ClassMaker methodFuncA(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Extends(FuncA.class);
        maker.Declare("id", int.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("other", maker.getClassType(), ClassMaker.ACC_PUBLIC);
        maker.Method("f", int.class, 0);
        maker.Declare("a", int.class, 0);
        maker.Begin();
        return maker;
    }
    
    ClassMaker methodFuncABC(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Extends(FuncABC.class);
        maker.Method("f", int.class, 0);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Declare("c", int.class, 0);
        maker.Begin();
        return maker;
    }
    
    void endMethod(ClassMaker maker, Value value)
    {
        maker.Return(value);
        maker.End();
    }

    private Value parseExpression(String string)
    {
        Input input = new LexerInputString(string);
        OperatorPrecidenceParser parser = createIncrementParser();
        parser.setInput(input);
        parser.nextToken();
        AstExpression expr = (AstExpression)parser.expression();
    	AstStructureVisitor visitor = new AstStructureVisitor(maker);
        return expr.resolveType(visitor);
    }

    public void testGetLocal1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("a");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 1, func.f(1));
    }

    public void testSimpleMaths1() throws Exception
    {
        methodFuncABC(maker);
        Value result = parseExpression("a * 3 + b / 2");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncABC func = (FuncABC)instance;
        assertEquals("Wrong result", 4, func.f(1, 2,3));
    }

    public void testSimpleMaths2() throws Exception
    {
        methodFuncABC(maker);
        Value result = parseExpression("a - b * c");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncABC func = (FuncABC)instance;
        assertEquals("Wrong result", 3, func.f(3, 0,0));
        assertEquals("Wrong result", -5, func.f(1, 2,3));
    }

    public void testSimpleMaths3() throws Exception
    {
        methodFuncABC(maker);
        Value result = parseExpression("a - -b - c");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncABC func = (FuncABC)instance;
        assertEquals("Wrong result", 3, func.f(3, 0,0));
        assertEquals("Wrong result", 9, func.f(10, 2, 3));
    }

    public void testSimpleMaths4() throws Exception
    {
        methodFuncABC(maker);
        Value result = parseExpression("a / b + a % c");
        endMethod(maker, result);
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncABC func = (FuncABC)instance;
        assertEquals("Wrong result", 4, func.f(5, 2, 3));
        assertEquals("Wrong result", 6, func.f(10, 2, 3));
    }
    
    public void testAssignLocal1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("a = 1");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 1, func.f(1));
        assertEquals("Wrong result", 1, func.f(6));
    }

    public void testAssignLocal2() throws Exception
    {
        methodFuncABC(maker);
        Value result = parseExpression("a = b * c");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncABC func = (FuncABC)instance;
        assertEquals("Wrong result", 6, func.f(0, 3, 2));
        assertEquals("Wrong result", -12, func.f(0, 3, -4));
    }

    public void testAssignField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("id = 2");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", -1);
        assertEquals("Wrong result", 2, func.f(1));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 2, id);
    }

    public void testAssignField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("id = a");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", -1);
        assertEquals("Wrong result", 12, func.f(12));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 12, id);

        assertEquals("Wrong result", 6, func.f(6));
        id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 6, id);

    }

    public void testAssignField3() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("id = a / 3");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", -1);
        assertEquals("Wrong result", 2, func.f(7));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 2, id);

        assertEquals("Wrong result", -4, func.f(-13));
        id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", -4, id);

    }

    public void testAssignField4() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("id = id + a");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 0);
        assertEquals("Wrong result", 7, func.f(7));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 7, id);

        assertEquals("Wrong result", -6, func.f(-13));
        id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", -6, id);
    }

    public void testIncLocal1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("++a");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 2, func.f(1));
    }

    public void testIncLocal2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("++a *2");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 8, func.f(3));
    }

    public void testPostIncLocal1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("a++");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 1, func.f(1));
    }

    public void testPostIncLocal2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("-a++");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", -1, func.f(1));
    }

    public void testDecLocal1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("--a");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 2, func.f(3));
    }

    public void testDecLocal2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("- --a");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", -2, func.f(3));
    }

    public void testPostDecLocal1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("a-- - -1");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 6, func.f(5));
    }

    public void testPostDecLocal2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("a--");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        assertEquals("Wrong result", 1, func.f(1));
    }

    public void testIncField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("++id");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 7, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 7, id);
    }


    public void testIncField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("10 - ++id * 2");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", -4, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Inc Field", 7, id);
    }

    public void testPostIncField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("id++");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 7, id);
    }

    public void testPostIncField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("3 + -id++");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", -3, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 7, id);
    }

    public void testDecField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("--id");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 5, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 5, id);
    }

    public void testDecField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("1 + --id - 1");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 5, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 5, id);
    }

    public void testPostDecField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("id--");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 5, id);
    }

    public void testPostDecField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("1 + id-- - 1");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setIntField(parserClass, instance, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
        int id = getIntField(parserClass, instance, "id");
        assertEquals("Set Field", 5, id);
    }

    // Increment field in other class
    
    public void testIncOtherField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("++other.id");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 7, func.f(0));
        int id = getIntField(parserClass, other, "id");
        assertEquals("Set OtherField", 7, id);
    }


    public void testIncOtherField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("10 - ++other.id * 2");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", -4, func.f(0));
        int id = getIntField(parserClass, other, "id");
        assertEquals("Inc OtherField", 7, id);
    }

    public void testPostIncOtherField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("other.id++");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
        int id = getIntField(parserClass, other, "id");
        assertEquals("Set OtherField", 7, id);
    }

    public void testPostIncOtherField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("3 + -other.id++");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", -3, func.f(0));
        int id = getIntField(parserClass, other, "id");
        assertEquals("Set Field", 7, id);
    }

    public void testDecOtherField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("--other.id");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 5, func.f(0));
        int id = getIntField(parserClass, other, "id");
        assertEquals("Set Field", 5, id);
    }

    public void testDecOtherField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("1 + --other.id - 1");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 5, func.f(0));
        int id = getIntField(parserClass, other, "id");
        assertEquals("Set Field", 5, id);
    }

    public void testPostDecOtherField1() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("other.id--");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
        int id = getIntField(parserClass, other, "id");
        assertEquals("Set Field", 5, id);
    }

    public void testPostDecOtherField2() throws Exception
    {
        methodFuncA(maker);
        Value result = parseExpression("1 + other.id-- - 1");
        endMethod(maker, result);

        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        Object other = parserClass.newInstance();
        FuncA func = (FuncA)instance;
        setField(parserClass, instance, "other", other);
        setIntField(parserClass, other, "id", 6);
        assertEquals("Wrong result", 6, func.f(0));
        int id = getIntField(parserClass, other, "id");
        assertEquals("Set Field", 5, id);
    }

}
