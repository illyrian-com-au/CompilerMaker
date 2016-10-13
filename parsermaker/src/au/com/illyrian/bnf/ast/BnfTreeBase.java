package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.classmaker.ast.AstExpressionBase;
import au.com.illyrian.parser.ParserException;

public abstract class BnfTreeBase extends AstExpressionBase implements BnfTree
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
     
     public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
     {
         return false;
     }

     public boolean matches(BnfTree other) {
         return false;
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
