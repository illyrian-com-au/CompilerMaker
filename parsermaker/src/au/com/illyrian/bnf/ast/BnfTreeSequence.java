package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.ParserException;

public class BnfTreeSequence extends BnfTreeBinary <Type>
{
    BnfTree [] seqArray;
    
    public BnfTreeSequence(BnfTree left, BnfTree right)
    {
        super(left, right);
        seqArray = concat(left.toSeqArray(), right.toSeqArray());
    }

    public BnfTree resolveMerge(BnfMergeVisitor visitor) {
        return visitor.resolveMerge(this);
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

    public BnfTree [] toSeqArray() {
        return seqArray;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public Type  resolveDeclaration(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveDeclaration(this);
    }

    public Type resolveLookahead(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveLookahead(this);
    }

    public String toString() {
        return getLeft() + " " + getRight();
    }
}
