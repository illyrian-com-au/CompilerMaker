package au.com.illyrian.bnf.ast;


public class BnfTreeTarget extends BnfTreeBinary
{
    public BnfTreeTarget(BnfTree left, BnfTree right)
    {
        super(left, right);
    }

    public String toString() {
        return getLeft() + ":" + getRight();
    }
}
