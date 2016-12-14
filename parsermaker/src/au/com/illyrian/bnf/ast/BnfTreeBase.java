package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpressionBase;

public abstract class BnfTreeBase <T> extends AstExpressionBase implements BnfTree <T>
{
     public BnfTree resolveMerge(BnfMergeVisitor visitor) {
         return visitor.resolveMerge(this);
     }

     public BnfTreeAlternative resolveAlternatives(BnfMergeVisitor visitor, BnfTreeAlternative alt) {
         return alt;
     }
     
     public BnfTree resolveAlternatives(BnfMergeVisitor visitor) {
         return visitor.resolveMerge(this);
     }
     
     public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet)
     {
         return false;
     }
     
     public T resolveDeclaration(BnfMakerVisitor visitor)
     {
         throw new IllegalStateException("Cannot resolve Declaration: " + getClass().getSimpleName());
     }

     public T resolveLookahead(BnfMakerVisitor visitor)
     {
         throw new IllegalStateException("Cannot resolve Lookahead: " + getClass().getSimpleName());
     }

     public boolean matches(BnfTree other) {
         return false;
     }
     
     public boolean isEmpty() {
         return false;
     }
     
     public boolean isVoidType()
     {
         return true;
     }
     
     public BnfTree [] toAltArray() {
         return new BnfTree [] { this };
     }
     
     public BnfTree [] toSeqArray() {
         return new BnfTree [] { this };
     }
     
     public BnfTreeRule [] toRuleArray() {
         return null;
     }
     
     protected BnfTree [] concat(BnfTree [] left, BnfTree [] right)
     {
         BnfTree [] list = new BnfTree [left.length + right.length];
         System.arraycopy(left, 0, list, 0, left.length);
         System.arraycopy(right, 0, list, left.length, right.length);
         return list;
     }

     public BnfTree getHead() {
         return this;
     }
     
     public BnfTree getTail() {
         return null;
     }
     
     public String getName() {
         return null;
     }
     
     public String toRuleString() {
         return toString();
     }
}
