package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserRule extends AstParserBase
{
    private final AstParser target;
    private final AstParser body;
    
    public AstParserRule(AstParser target, AstParser body)
    {
        this.target = target;
        this.body = body;
    }

    public AstParser resolveMerge(AstMergeVisitor visitor) 
    {
        return visitor.resolveMerge(this);
    }

    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }
    
    public AstParserRule [] toRuleArray() {
        return new AstParserRule [] { this };
    }

    public AstParser getTarget()
    {
        return target;
    }

    public AstParser getBody()
    {
        return body;
    }
    
    public String getName() {
        return getTarget().getName();
    }

    public AstParserRule replicate(AstParser target, AstParser body) {
        if (target == this.target && body == this.body)
            return this;
        return new AstParserRule(target, body);
    }

    public String toString() {
        return target + " ::= " + body + " ;";
    }
}
