package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.types.Type;

public class BnfTreeSequence extends BnfTreeBinary <Type>
{
    BnfTree [] seqArray;
    
    public BnfTreeSequence(BnfTree left, BnfTree right) {
        super(left, right);
        seqArray = concat(left.toSeqArray(), right.toSeqArray());
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }
    
    public Type resolveSequence(BnfMakerVisitor visitor, int variable) {
        return visitor.resolveSequence(this, variable);
    }

    public boolean matches(BnfTree other) {
        if (other instanceof BnfTreeSequence) {
            BnfTreeSequence otherSeq = (BnfTreeSequence)other;
            return getLeft().matches(otherSeq.getLeft());
        }
        return false;
    }

    public BnfTreeSequence replace(BnfTree head, BnfTree tail) {
        if (head == this.getHead() && tail == this.getTail())
            return this;
        return new BnfTreeSequence(head, tail);
    }

    public BnfTreeSequence toSequence() {
        return this;
    }
    
    public BnfTree [] toSeqArray() {
        return seqArray;
    }
    
    public boolean isMacro() {
        return getHead().isMacro();
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, BnfFirstSet firstSet)
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public Type  resolveDeclaration(BnfMakerVisitor visitor)
    {
        return visitor.resolveDeclaration(this);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor, int howFar)
    {
        return visitor.resolveLookahead(this, howFar);
    }

    public String toString() {
        return getLeft() + " " + getRight();
    }
}
