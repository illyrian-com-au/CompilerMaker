package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;

public class BnfTreeList <T> extends BnfTreeBinary <T>
{
    
    public BnfTreeList(BnfTree left, BnfTree right) {
        super(left, right);
    }

    public BnfTreeList replace(BnfTree left, BnfTree right) {
        if (left == this.getLeft() && right == this.getRight())
            return this;
        return new BnfTreeList(left, right);
    }

    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public BnfTreeRule [] toRuleArray() {
        BnfTreeRule [] head = (BnfTreeRule [])getHead().toRuleArray();
        BnfTreeRule [] tail = (BnfTreeRule [])getTail().toRuleArray();
        return (BnfTreeRule [])concat(head, tail);
    }
    
    protected BnfTreeRule [] concat(BnfTreeRule [] left, BnfTreeRule [] right)
    {
        BnfTreeRule [] list = new BnfTreeRule [left.length + right.length];
        System.arraycopy(left, 0, list, 0, left.length);
        System.arraycopy(right, 0, list, left.length, right.length);
        return list;
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor)
    {
        return visitor.resolveDeclaration(this);
    }

    public String toString() {
        return getLeft() + "\n" + getRight();
    }
}
