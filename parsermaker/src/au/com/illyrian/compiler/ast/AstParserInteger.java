package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserInteger extends AstParserBase
{
    private final Integer value;
    
    public AstParserInteger(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return value;
    }

    public boolean matches(AstParser other)
    {
        if (other instanceof AstParserInteger)
        {
            AstParserInteger otherValue = (AstParserInteger)other;
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
