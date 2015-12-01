package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserRule extends AstParserBase
{
    private final AstParser target;
    private final AstParser body;
    private final AstParser action;
    
    public AstParserRule(AstParser target, AstParser body, AstParser action)
    {
        this.target = target;
        this.body = body;
        this.action = action;
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

    public AstParser getAction()
    {
        return action;
    }
    
    public AstParserRule replicate(AstParser target, AstParser body, AstParser action) {
        if (target == this.target && body == this.body && action == this.action)
            return this;
        return new AstParserRule(target, body, action);
    }

    public String toString() {
        String ext = (action == null) ? "" : " = " + action;
        return target + " ::= " + body + ext + " ;";
    }
}
