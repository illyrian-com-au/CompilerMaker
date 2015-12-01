package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserName extends AstParserBase
{
    private final String name;
    
    public AstParserName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public boolean matches(AstParser other)
    {
        if (other instanceof AstParserName)
        {
            AstParserName otherName = (AstParserName)other;
            return name.equals(otherName.name);
        }
        return false;
    }
    
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return name;
    }
}
