package au.com.illyrian.bnf.ast;

import java.util.Set;

public class BnfTreeComma <T> extends BnfTreeBinary <T>
{
    public BnfTreeComma(BnfTree left, BnfTree right)
    {
        super(left, right);
    }

    @Override
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet)
    {
        return false;
    }

    public String toString() {
        return getLeft() + ", " + getRight();
    }
}
