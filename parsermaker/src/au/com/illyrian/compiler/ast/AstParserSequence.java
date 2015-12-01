package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserSequence extends AstParserBinary
{
    AstParser [] seqArray;
    
    public AstParserSequence(AstParser left, AstParser right)
    {
        super(left, right);
        seqArray = concat(left.toSeqArray(), right.toSeqArray());
    }

    public AstParser resolveMerge(AstMergeVisitor visitor) {
        return visitor.resolveMerge(this);
    }
    
    public boolean matches(AstParser other) {
        if (other instanceof AstParserSequence) {
            AstParserSequence otherSeq = (AstParserSequence)other;
            return getLeft().matches(otherSeq.getLeft());
        }
        return false;
    }

    public AstParserSequence replace(AstParser head, AstParser tail) {
        if (head == this.getHead() && tail == this.getTail())
            return this;
        return new AstParserSequence(head, tail);
    }

    public AstParser [] toSeqArray() {
        return seqArray;
    }
    
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return getLeft() + " " + getRight();
    }
}
