package au.com.illyrian.classmaker.ast;

import au.com.illyrian.classmaker.types.Type;


public class ArrayOf extends AstExpressionBase 
{
    private final AstExpression type;
    private final AstExpression dimension;

    public ArrayOf(AstExpression type, AstExpression dimension)
    {
        this.type = type;
        this.dimension = dimension;
    }
    
    public ArrayOf(AstExpression type)
    {
        this.type = type;
        this.dimension = null;
    }
    
    public Type resolveType(AstExpressionVisitor visitor) 
    {
        return visitor.resolveType(this);
    }
    
    public AstExpression getType() {
        return type;
    }

    public AstExpression getDimension() {
        return dimension;
    }

    public String toString()
    {
        String arrayType = type + "[]";
        if (dimension == null) {
            return arrayType;
        } else {
            return arrayType + "(" + dimension + ")";
        }
    }

}
