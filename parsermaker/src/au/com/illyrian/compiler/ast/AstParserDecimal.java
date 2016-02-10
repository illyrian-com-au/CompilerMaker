package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserDecimal extends AstParserBase
{
    private final Float value;
    
    public AstParserDecimal(Float value)
    {
        this.value = value;
    }

    public Float getValue()
    {
        return value;
    }

    public boolean matches(AstParser other)
    {
        if (other instanceof AstParserDecimal)
        {
            AstParserDecimal otherValue = (AstParserDecimal)other;
            return value.equals(otherValue.value);
        }
        return false;
    }
    
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return false;
    }

    public String toString() {
        return "" + value;
    }

}
