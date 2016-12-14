package au.com.illyrian.bnf.ast;

public abstract class BnfTreeBinary <T> extends BnfTreeBase <T>
{
    private final BnfTree left;
    private final BnfTree right;
    
    public BnfTreeBinary(BnfTree left, BnfTree right)
    {
        this.left = left;
        this.right = right;
    }

    public BnfTree resolveAlternatives(BnfMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }

    public BnfTree getLeft()
    {
        return left;
    }

    public BnfTree getRight()
    {
        return right;
    }
    
    public BnfTree getHead() {
        return left;
    }
    
    public BnfTree getTail() {
        return right;
    }

    public BnfTreeBinary replace(BnfTree head, BnfTree tail) {
        return this;
    }

    public String toRuleString() {
        return (left==null? "null" : left.toRuleString()) 
                + " " 
                + (right==null ? "null" : right.toRuleString());
    }

    public String toString() {
        return left + " " + right;
    }
}
