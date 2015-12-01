package au.com.illyrian.compiler.ast;

import java.util.Set;

public class AstParserEmpty extends AstParserBase
{
    public AstParserEmpty() {
    }
    
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return ".";
    }
}
