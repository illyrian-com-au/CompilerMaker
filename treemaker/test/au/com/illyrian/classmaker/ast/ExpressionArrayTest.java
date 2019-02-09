package au.com.illyrian.classmaker.ast;

import junit.framework.TestCase;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.ClassMakerText;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class ExpressionArrayTest extends TestCase
{
    ClassMakerIfc buf = new ClassMakerText();
    AstExpressionVisitor visitor = new AstExpressionVisitor(buf);
    AstExpressionFactory ast = new AstExpressionFactory();

    public void testGetAt()
    {
        AstExpression expr = ast.ArrayIndex(ast.Name("x"), ast.Name("a"));
        assertEquals("Wrong toString()", "x[a]", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int)", type.toString());
        assertEquals("Wrong output", "[GetAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testGetAt2()
    {
        AstExpression expr = ast.ArrayIndex(ast.ArrayIndex(ast.Name("x"), ast.Name("a")), ast.Literal(2));
        assertEquals("Wrong toString()", "x[a][2]", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int)", type.toString());
        assertEquals("Wrong output", "[GetAt(GetAt(Get(\"x\"), Get(\"a\")), Literal(2))]", buf.toString());
    }

    public void testAssignAt()
    {
        AstExpression expr = ast.Assign(ast.ArrayIndex(ast.Name("x"), ast.Name("a")), ast.Literal(1));
        assertEquals("Wrong toString()", "(x[a] = 1)", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int)", type.toString());
        assertEquals("Wrong output", "[AssignAt(Get(\"x\"), Get(\"a\"), Literal(1))]", buf.toString());
    }

    public void testAssignAt2()
    {
        AstExpression expr = ast.Assign(ast.ArrayIndex(ast.ArrayIndex(ast.Name("x"), ast.Name("a")), ast.Literal(0)), ast.Literal(1));
        assertEquals("Wrong toString()", "(x[a][0] = 1)", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int)", type.toString());
        assertEquals("Wrong output", "[AssignAt(GetAt(Get(\"x\"), Get(\"a\")), Literal(0), Literal(1))]", buf.toString());
    }

    public void testIncAt()
    {
        AstExpression expr = ast.Inc(ast.ArrayIndex(ast.Name("x"), ast.Name("a")));
        assertEquals("Wrong toString()", "++(x[a])", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int)", type.toString());
        assertEquals("Wrong output", "[IncAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testDecAt()
    {
        AstExpression expr = ast.Dec(ast.ArrayIndex(ast.Name("x"), ast.Name("a")));
        assertEquals("Wrong toString()", "--(x[a])", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int)", type.toString());
        assertEquals("Wrong output", "[DecAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testPotIncAt()
    {
        AstExpression expr = ast.PostInc(ast.ArrayIndex(ast.Name("x"), ast.Name("a")));
        assertEquals("Wrong toString()", "(x[a])++", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int)", type.toString());
        assertEquals("Wrong output", "[PostIncAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testPostDecAt()
    {
        AstExpression expr = ast.PostDec(ast.ArrayIndex(ast.Name("x"), ast.Name("a")));
        assertEquals("Wrong toString()", "(x[a])--", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int)", type.toString());
        assertEquals("Wrong output", "[PostDecAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testArrayOf()
    {
        AstExpression expr = ast.ArrayOf(ast.Name("int"));
        assertEquals("Wrong toString()", "int[]", expr.toString());
        Type type = expr.resolveType(visitor);
        assertNotNull("resolveType() returned null", type);
        assertEquals("Wrong type", "ArrayType(int[])", type.toString());
        assertEquals("Wrong output", "[]", buf.toString());
    }

    public void testNewArrayOld()
    {
        AstExpression expr = ast.NewArray(ast.ArrayOf(ast.Name("int")), ast.Name("a"));
        assertEquals("Wrong toString()", "new int[a]", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertNotNull("resolveDecleredType() returned null", type);
        assertEquals("Wrong type", "Value(int[])", type.toString());
        assertEquals("Wrong output", "[NewArray(int[], Get(\"a\"))]", buf.toString());
    }

    public void testNewArray()
    {
        AstExpression expr = ast.New(ast.ArrayIndex(ast.Name("int"), ast.Name("a")));
        assertEquals("Wrong toString()", "new int[a]", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertNotNull("resolveDecleredType() returned null", type);
        assertEquals("Wrong type", "Value(int[])", type.toString());
        assertEquals("Wrong output", "[NewArray(int[], Get(\"a\"))]", buf.toString());
    }

    public void testNewArray2()
    {
        int [][] array = new int[5][];
        AstExpression expr = ast.New(ast.ArrayIndex(ast.ArrayOf(ast.Name("int")), ast.Literal(5)));
        assertEquals("Wrong toString()", "new int[][5]", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertNotNull("resolveDecleredType() returned null", type);
        assertEquals("Wrong type", "Value(int[][])", type.toString());
        assertEquals("Wrong output", "[NewArray(int[][], Literal(5))]", buf.toString());
    }


    public void testNewArray2Alt()
    {
        int [][] array = new int[5][];
        AstExpression expr = ast.New(ast.ArrayIndex(ast.ArrayIndex(ast.Name("int"), ast.Literal(5)), null));
        assertEquals("Wrong toString()", "new int[5][]", expr.toString());
        Value type = expr.resolveValue(visitor);
        assertEquals("Wrong type", "Value(int[][])", type.toString());
        assertEquals("Wrong output", "[NewArray(int[][], Literal(5))]", buf.toString());
    }

    public void testArrayLength()
    {
//        AstExpression expr = ast.NewArray(ast.ArrayOf(ast.Name("int")), ast.Name("a"));
//        assertEquals("Wrong toString()", "int []", expr.toString());
//        DeclaredType type = expr.resolveDeclaredType(visitor);
//        assertNotNull("resolveDecleredType() returned null", type);
//        assertEquals("Wrong type", "DeclaredType(int[])", type.toString());
//        assertEquals("Wrong output", "[ArrayOf(int)]", buf.toString());
    }
}
