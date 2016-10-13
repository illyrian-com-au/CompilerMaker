package au.com.illyrian.bnf.ast;


public class BnfTreeLookahead extends BnfTreeBinary
{
    public BnfTreeLookahead(BnfTree head, BnfTree tail)
    {
        super(head, tail);
    }

    public String toString() {
        return "lookahead(" + getHead().getName() + ", " + getTail() + ")";
    }
}
