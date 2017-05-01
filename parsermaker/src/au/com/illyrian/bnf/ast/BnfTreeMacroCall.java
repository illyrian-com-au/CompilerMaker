package au.com.illyrian.bnf.ast;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;

@Deprecated
public class BnfTreeMacroCall extends BnfTreeBase <Type>
{
    private final AstExpression name;
    private final AstExpression pattern;
    
    public BnfTreeMacroCall(AstExpression name, AstExpression pattern, int sourceLine) {
        super(sourceLine);
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

//    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet)
//    {
//        return visitor.resolveFirst(this, firstSet);
//    }
//
//    public Type resolveLookahead(BnfMakerVisitor visitor)
//    {
//        return visitor.resolveLookahead(this);
//    }

    public boolean isMacro() {
        return true;
    }

    public String toString() {
        return getName() + "(" + pattern + ")";
    }
}
