package au.com.illyrian.bnf.ast;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.types.Type;



public class BnfTreeTarget extends BnfTreeBinary
{
    public BnfTreeTarget(BnfTree left, BnfTree right) {
        super(left, right);
    }

    public String getName() {
        return getLeft().getName();
    }
    
    public String getType() {
        return getRight().getName();
    }

    public Type resolveDeclaration(BnfMakerVisitor visitor) {
        return visitor.resolveDeclaration(this);
    }
    
    public String toString() {
        return getLeft() + ":" + getRight();
    }
}
