package au.com.illyrian.parser.maker;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ClassMakerTestCase;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.jesub.ast.AstStructureVisitor;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.expr.AstExpressionPrecidenceParser;
import au.com.illyrian.parser.impl.LexerInputString;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class RelativeParserMakerTest extends ClassMakerTestCase
{
    ClassMakerFactory factory = new ClassMakerFactory();
    ClassMaker maker = factory.createClassMaker();

    public interface FuncAB {
    	public boolean f(int a, int b);
    }
    
    public interface BooleanAB {
    	public boolean f(boolean a, boolean b);
    }
    
    public interface FuncABCD {
    	public boolean f(int a, int b, int c, int d);
    }
    
    public interface BooleanABCD {
    	public boolean f(boolean a, boolean b, boolean c, boolean d);
    }
    
    OperatorPrecidenceParser createParser()
    {
        AstExpressionPrecidenceParser parser = new AstExpressionPrecidenceParser();
        AstExpressionFactory factory = new AstExpressionFactory();
        parser.setAstExpressionFactory(factory);
        return parser;
    }

    ClassMaker methodFuncAB(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Implements(FuncAB.class);
        maker.Method("f", boolean.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Begin();
        return maker;
    }
    
    ClassMaker methodBooleanAB(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Implements(BooleanAB.class);
        maker.Method("f", boolean.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Begin();
        return maker;
    }
    
    ClassMaker methodFuncABCD(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Implements(FuncABCD.class);
        maker.Method("f", boolean.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", int.class, 0);
        maker.Declare("b", int.class, 0);
        maker.Declare("c", int.class, 0);
        maker.Declare("d", int.class, 0);
        maker.Begin();
        return maker;
    }
    
    ClassMaker methodBooleanABCD(ClassMaker maker)
    {
        maker.setPackageName("au.com.illyrian.parser.impl");
        maker.setSimpleClassName("Test");
        maker.Implements(BooleanABCD.class);
        maker.Method("f", boolean.class, ClassMaker.ACC_PUBLIC);
        maker.Declare("a", boolean.class, 0);
        maker.Declare("b", boolean.class, 0);
        maker.Declare("c", boolean.class, 0);
        maker.Declare("d", boolean.class, 0);
        maker.Begin();
        return maker;
    }
    
    void endMethod(ClassMaker maker, Object value)
    {
        maker.Return((Type)value);
        maker.End();
    }

    private Type parseExpression(String input) throws ParserException
    {
        Input lexer = new LexerInputString(input);
        OperatorPrecidenceParser parser = createParser();
        parser.setInput(lexer);
        parser.nextToken();
        AstExpression expr = (AstExpression)parser.expression();
    	AstStructureVisitor visitor = new AstStructureVisitor(maker);
        return expr.resolveType(visitor);
    }

    public void testEqual() throws Exception
    {
        methodFuncAB(maker);
        Type result = parseExpression("a == b");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncAB func = (FuncAB)instance;
        assertEquals("Wrong result", false, func.f(0, 1));
        assertEquals("Wrong result", true, func.f(1, 1));
        assertEquals("Wrong result", false, func.f(1, 0));
    }

    public void testNotEqual() throws Exception
    {
        methodFuncAB(maker);
        Type result = parseExpression("a != b");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncAB func = (FuncAB)instance;
        assertEquals("Wrong result", true, func.f(0, 1));
        assertEquals("Wrong result", false, func.f(1, 1));
        assertEquals("Wrong result", true, func.f(1, 0));
    }

    public void testLessEqual() throws Exception
    {
        methodFuncAB(maker);
        Type result = parseExpression("a <= b");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncAB func = (FuncAB)instance;
        assertEquals("Wrong result", true, func.f(0, 1));
        assertEquals("Wrong result", true, func.f(1, 1));
        assertEquals("Wrong result", false, func.f(1, 0));
    }

    public void testGreatEqual() throws Exception
    {
        methodFuncAB(maker);
        Type result = parseExpression("a >= b");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncAB func = (FuncAB)instance;
        assertEquals("Wrong result", false, func.f(0, 1));
        assertEquals("Wrong result", true, func.f(1, 1));
        assertEquals("Wrong result", true, func.f(1, 0));
    }

    public void testLessThan() throws Exception
    {
        methodFuncAB(maker);
        Type result = parseExpression("a < b");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncAB func = (FuncAB)instance;
        assertEquals("Wrong result", true, func.f(0, 1));
        assertEquals("Wrong result", false, func.f(1, 1));
        assertEquals("Wrong result", false, func.f(1, 0));
    }

    public void testGreaterThan() throws Exception
    {
        methodFuncAB(maker);
        Type result = parseExpression("a > b");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncAB func = (FuncAB)instance;
        assertEquals("Wrong result", false, func.f(0, 1));
        assertEquals("Wrong result", false, func.f(1, 1));
        assertEquals("Wrong result", true, func.f(1, 0));
    }

    public void testAndThen() throws Exception
    {
        methodBooleanAB(maker);
        Type result = parseExpression("a && b");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanAB func = (BooleanAB)instance;
        assertEquals("Wrong result", false, func.f(false, false));
        assertEquals("Wrong result", true, func.f(true, true));
        assertEquals("Wrong result", false, func.f(false, true));
        assertEquals("Wrong result", false, func.f(true, false));
    }

    public void testOrElse() throws Exception
    {
        methodBooleanAB(maker);
        Type result = parseExpression("a || b");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanAB func = (BooleanAB)instance;
        assertEquals("Wrong result", false, func.f(false, false));
        assertEquals("Wrong result", true, func.f(true, true));
        assertEquals("Wrong result", true, func.f(true, false));
        assertEquals("Wrong result", true, func.f(false, true));
    }

    public void testAndThenInt() throws Exception
    {
        methodFuncABCD(maker);
        Type result = parseExpression("a == b && c == d");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncABCD func = (FuncABCD)instance;
        assertEquals("Wrong result", true, func.f(1, 1, 1, 1));
        assertEquals("Wrong result", false, func.f(0, 1, 2, 3));
        assertEquals("Wrong result", false, func.f(1, 0, 1, 1));
        assertEquals("Wrong result", false, func.f(1, 1, 2, 3));
    }

    public void testOrElseInt() throws Exception
    {
        methodFuncABCD(maker);
        Type result = parseExpression("a == b || c == d");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        FuncABCD func = (FuncABCD)instance;
        assertEquals("Wrong result", false, func.f(0, 1, 2, 3));
        assertEquals("Wrong result", true, func.f(1, 1, 1, 1));
        assertEquals("Wrong result", true, func.f(1, 1, 2, 3));
        assertEquals("Wrong result", true, func.f(1, 0, 1, 1));
    }


    public void testAndThenAndThen() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a && b && c");
        endMethod(maker, result);

        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", false, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", false, func.f(true, true, false, false));
        
        assertEquals("Wrong result", false, func.f(false, false, true, false));
        assertEquals("Wrong result", false, func.f(true, false, true, false));
        assertEquals("Wrong result", false, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
    }

    public void testOrElseOrElse() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a || b || c");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", true, func.f(true, false, false, false));
        assertEquals("Wrong result", true, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", true, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
    }

    public void testOrElseAndThen() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a || b && c");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", true, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", false, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
    }

    public void testAndThenOrElse() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a && b || c");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", false, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", true, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
    }
    
    public void testOrElseOrElseOrElse() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a || b || c || d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", true, func.f(true, false, false, false));
        assertEquals("Wrong result", true, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", true, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", true, func.f(false, false, false, true));
        assertEquals("Wrong result", true, func.f(true, false, false, true));
        assertEquals("Wrong result", true, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", true, func.f(false, false, true, true));
        assertEquals("Wrong result", true, func.f(true, false, true, true));
        assertEquals("Wrong result", true, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }
    
    public void testAndThenAndThenAndThen() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a&&b&&c&&d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", false, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", false, func.f(true, true, false, false));
        
        assertEquals("Wrong result", false, func.f(false, false, true, false));
        assertEquals("Wrong result", false, func.f(true, false, true, false));
        assertEquals("Wrong result", false, func.f(false, true, true, false));
        assertEquals("Wrong result", false, func.f(true, true, true, false));
        
        assertEquals("Wrong result", false, func.f(false, false, false, true));
        assertEquals("Wrong result", false, func.f(true, false, false, true));
        assertEquals("Wrong result", false, func.f(false, true, false, true));
        assertEquals("Wrong result", false, func.f(true, true, false, true));
        
        assertEquals("Wrong result", false, func.f(false, false, true, true));
        assertEquals("Wrong result", false, func.f(true, false, true, true));
        assertEquals("Wrong result", false, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }


    public void testOrElseAndThenAndThen() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a || b && c && d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", true, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", false, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", false, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", false, func.f(false, false, false, true));
        assertEquals("Wrong result", true, func.f(true, false, false, true));
        assertEquals("Wrong result", false, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", false, func.f(false, false, true, true));
        assertEquals("Wrong result", true, func.f(true, false, true, true));
        assertEquals("Wrong result", true, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }

    public void testOrElseOrElseAndThen() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a || b || c && d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", true, func.f(true, false, false, false));
        assertEquals("Wrong result", true, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", false, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", false, func.f(false, false, false, true));
        assertEquals("Wrong result", true, func.f(true, false, false, true));
        assertEquals("Wrong result", true, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", true, func.f(false, false, true, true));
        assertEquals("Wrong result", true, func.f(true, false, true, true));
        assertEquals("Wrong result", true, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }


    public void testOrElseAndThenOrElse() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a || b && c || d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", true, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", false, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", true, func.f(false, false, false, true));
        assertEquals("Wrong result", true, func.f(true, false, false, true));
        assertEquals("Wrong result", true, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", true, func.f(false, false, true, true));
        assertEquals("Wrong result", true, func.f(true, false, true, true));
        assertEquals("Wrong result", true, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }

    public void testAndThenOrElseAndThen() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a && b || c && d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", false, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", false, func.f(false, false, true, false));
        assertEquals("Wrong result", false, func.f(true, false, true, false));
        assertEquals("Wrong result", false, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", false, func.f(false, false, false, true));
        assertEquals("Wrong result", false, func.f(true, false, false, true));
        assertEquals("Wrong result", false, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", true, func.f(false, false, true, true));
        assertEquals("Wrong result", true, func.f(true, false, true, true));
        assertEquals("Wrong result", true, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }

    public void testAndThenOrElseOrElse() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a && b || c || d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", false, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", true, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", true, func.f(false, false, false, true));
        assertEquals("Wrong result", true, func.f(true, false, false, true));
        assertEquals("Wrong result", true, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", true, func.f(false, false, true, true));
        assertEquals("Wrong result", true, func.f(true, false, true, true));
        assertEquals("Wrong result", true, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }
    
    public void testAndThenAndThenOrElse() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a || b && c || d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", true, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", false, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", true, func.f(false, false, false, true));
        assertEquals("Wrong result", true, func.f(true, false, false, true));
        assertEquals("Wrong result", true, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", true, func.f(false, false, true, true));
        assertEquals("Wrong result", true, func.f(true, false, true, true));
        assertEquals("Wrong result", true, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }

    public void testEQAndThenNE() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a == b && c != d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", false, func.f(false, false, false, false));
        assertEquals("Wrong result", false, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", false, func.f(true, true, false, false));
        
        assertEquals("Wrong result", true, func.f(false, false, true, false));
        assertEquals("Wrong result", false, func.f(true, false, true, false));
        assertEquals("Wrong result", false, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", true, func.f(false, false, false, true));
        assertEquals("Wrong result", false, func.f(true, false, false, true));
        assertEquals("Wrong result", false, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", false, func.f(false, false, true, true));
        assertEquals("Wrong result", false, func.f(true, false, true, true));
        assertEquals("Wrong result", false, func.f(false, true, true, true));
        assertEquals("Wrong result", false, func.f(true, true, true, true));
    }

    public void testEQOrElseNE() throws Exception
    {
        methodBooleanABCD(maker);
        Type result = parseExpression("a == b || c != d");
        endMethod(maker, result);
        
        assertEquals("full class name", "au.com.illyrian.parser.impl.Test", maker.getFullyQualifiedClassName());
        
        Class parserClass = maker.defineClass();
        Object instance = parserClass.newInstance();
        assertEquals("Canonical class name", "au.com.illyrian.parser.impl.Test", instance.getClass().getCanonicalName());
        BooleanABCD func = (BooleanABCD)instance;

        assertEquals("Wrong result", true, func.f(false, false, false, false));
        assertEquals("Wrong result", false, func.f(true, false, false, false));
        assertEquals("Wrong result", false, func.f(false, true, false, false));
        assertEquals("Wrong result", true, func.f(true, true, false, false));
        
        assertEquals("Wrong result", true, func.f(false, false, true, false));
        assertEquals("Wrong result", true, func.f(true, false, true, false));
        assertEquals("Wrong result", true, func.f(false, true, true, false));
        assertEquals("Wrong result", true, func.f(true, true, true, false));
        
        assertEquals("Wrong result", true, func.f(false, false, false, true));
        assertEquals("Wrong result", true, func.f(true, false, false, true));
        assertEquals("Wrong result", true, func.f(false, true, false, true));
        assertEquals("Wrong result", true, func.f(true, true, false, true));
        
        assertEquals("Wrong result", true, func.f(false, false, true, true));
        assertEquals("Wrong result", false, func.f(true, false, true, true));
        assertEquals("Wrong result", false, func.f(false, true, true, true));
        assertEquals("Wrong result", true, func.f(true, true, true, true));
    }

    public void testBooleanExceptions() throws Exception
    {
        methodBooleanAB(maker);
    	try {
	        parseExpression("a < b");
    	} catch (ClassMakerException ex) {
    		assertEquals("Wrong Exception", "Cannot LT type boolean with boolean", ex.getMessage());
    	}

    	try {
	        parseExpression("a > b");
    	} catch (ClassMakerException ex) {
    		assertEquals("Wrong Exception", "Cannot GT type boolean with boolean", ex.getMessage());
    	}

    	try {
	        parseExpression("a <= b");
    	} catch (ClassMakerException ex) {
    		assertEquals("Wrong Exception", "Cannot LE type boolean with boolean", ex.getMessage());
    	}

    	try {
	        parseExpression("a >= b");
    	} catch (ClassMakerException ex) {
    		assertEquals("Wrong Exception", "Cannot GE type boolean with boolean", ex.getMessage());
    	}

    }
}
