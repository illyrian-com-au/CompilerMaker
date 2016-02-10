package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserCall extends AstParserBase
{
    private final AstParser name;
    private final AstParser actuals;
    
    public AstParserCall(AstParser name, AstParser actuals)
    {
        this.name = name;
        this.actuals = actuals;
    }

    public String getName()
    {
        return name.getName();
    }

    public AstParser getActuals()
    {
        return actuals;
    }

    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return getName() + "(" + (actuals == null ? "" : actuals) + ")";
    }
}
