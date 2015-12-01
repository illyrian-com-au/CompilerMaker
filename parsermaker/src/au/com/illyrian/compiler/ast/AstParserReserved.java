package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserReserved extends AstParserName
{
    public AstParserReserved(String name)
    {
        super(name);
    }

    public boolean matches(AstParser other)
    {
        if (other instanceof AstParserReserved)
        {
            AstParserReserved otherReserved = (AstParserReserved)other;
            return getName().matches(otherReserved.getName());
        }
        return false;
    }
    
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return "<" + getName() + ">";
    }
}
