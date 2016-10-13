package au.com.illyrian.bnf.ast;

import java.util.Set;

public class BnfTreeEmpty extends BnfTreeBase
{
    public BnfTreeEmpty() {
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return ".";
    }
}
