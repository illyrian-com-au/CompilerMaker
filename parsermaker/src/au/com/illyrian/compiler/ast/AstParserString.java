package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserString extends AstParserBase
{
    private final String value;
    
    public AstParserString(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public boolean matches(AstParser other)
    {
        if (other instanceof AstParserString)
        {
            AstParserString otherString = (AstParserString)other;
            return value.equals(otherString.value);
        }
        return false;
    }
    
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return "\"" + value + "\"";
    }

}
