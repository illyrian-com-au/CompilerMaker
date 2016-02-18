package au.com.illyrian.classmaker.ast;

import au.com.illyrian.classmaker.ClassMakerText;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.Type;
import junit.framework.TestCase;

public class ExpressionArrayTest extends TestCase
{
    ClassMakerIfc buf = new ClassMakerText();
    AstExpressionVisitor visitor = new AstExpressionVisitor(buf);
    AstExpressionFactory ast = new AstExpressionFactory();

    public void testGetAt()
    {
        AstExpression expr = ast.ArrayIndex(ast.Name("x"), ast.Name("a"));
        assertEquals("Wrong toString()", "x[a]", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[GetAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testAssignAt()
    {
        AstExpression expr = ast.Assign(ast.ArrayIndex(ast.Name("x"), ast.Name("a")), ast.Literal(1));
        assertEquals("Wrong toString()", "(x[a] = 1)", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[AssignAt(Get(\"x\"), Get(\"a\"), Literal(1))]", buf.toString());
    }

    public void testIncAt()
    {
        AstExpression expr = ast.Inc(ast.ArrayIndex(ast.Name("x"), ast.Name("a")));
        assertEquals("Wrong toString()", "++(x[a])", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[IncAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testDecAt()
    {
        AstExpression expr = ast.Dec(ast.ArrayIndex(ast.Name("x"), ast.Name("a")));
        assertEquals("Wrong toString()", "--(x[a])", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[DecAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testPotIncAt()
    {
        AstExpression expr = ast.PostInc(ast.ArrayIndex(ast.Name("x"), ast.Name("a")));
        assertEquals("Wrong toString()", "(x[a])++", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[PostIncAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testPostDecAt()
    {
        AstExpression expr = ast.PostDec(ast.ArrayIndex(ast.Name("x"), ast.Name("a")));
        assertEquals("Wrong toString()", "(x[a])--", expr.toString());
        Type type = expr.resolveType(visitor);
        assertEquals("Wrong type", "PrimitiveType(int)", type.toString());
        assertEquals("Wrong output", "[PostDecAt(Get(\"x\"), Get(\"a\"))]", buf.toString());
    }

    public void testArrayOf()
    {
        AstExpression expr = ast.ArrayOf(ast.Name("int"));
        assertEquals("Wrong toString()", "int []", expr.toString());
        DeclaredType type = expr.resolveDeclaredType(visitor);
        assertNotNull("resolveDecleredType() returned null", type);
        assertEquals("Wrong type", "DeclaredType(int[])", type.toString());
        assertEquals("Wrong output", "[]", buf.toString());
    }

    public void testNewArray()
    {
        AstExpression expr = ast.NewArray(ast.ArrayOf(ast.Name("int")), ast.Name("a"));
        assertEquals("Wrong toString()", "new int [a]", expr.toString());
        Type type = expr.resolveType(visitor);
        assertNotNull("resolveDecleredType() returned null", type);
        assertEquals("Wrong type", "ArrayType(int[])", type.toString());
        assertEquals("Wrong output", "[NewArray(int[], Get(\"a\"))]", buf.toString());
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
