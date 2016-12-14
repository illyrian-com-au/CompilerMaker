package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.parser.ParserException;

public class BnfTreeMacroCall <T> extends BnfTreeBase <T>
{
    private final AstExpression name;
    private final AstExpression pattern;
    
    public BnfTreeMacroCall(AstExpression name, AstExpression pattern)
    {
        this.name = name;
        this.pattern = pattern;
    }

    public String getName()
    {
        return name.toString();
    }

    public AstExpression getPattern()
    {
        return pattern;
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return getName() + "(" + pattern + ")";
    }
}
