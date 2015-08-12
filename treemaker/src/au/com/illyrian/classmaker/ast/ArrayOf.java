package au.com.illyrian.classmaker.ast;

import au.com.illyrian.classmaker.types.DeclaredType;


public class ArrayOf extends AstExpressionBase 
{
    private final AstExpression type;
    private final AstExpression dimension;

    public ArrayOf(AstExpression type, AstExpression dimension)
    {
        this.type = type;
        this.dimension = dimension;
    }
    
    public DeclaredType resolveDeclaredType(AstExpressionVisitor visitor) 
    {
        return visitor.resolveDeclaredType(this);
    }
    
    public AstExpression getType() {
        return type;
    }

    public AstExpression getDimension() {
        return dimension;
    }

    public String toString()
    {
        return type + " [" + (dimension == null ? "" : dimension) + "]";
    }

}
