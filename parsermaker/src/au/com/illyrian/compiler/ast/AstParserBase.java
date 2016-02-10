package au.com.illyrian.compiler.ast;

public abstract class AstParserBase implements AstParser
{
     public AstParser resolveMerge(AstMergeVisitor visitor) {
         return visitor.resolveMerge(this);
     }

     public AstParserAlternative resolveAlternatives(AstMergeVisitor visitor, AstParserAlternative alt) {
         return alt;
     }
     
     public AstParser resolveAlternatives(AstMergeVisitor visitor) {
         return visitor.resolveMerge(this);
     }
     
     public boolean matches(AstParser other) {
         return false;
     }
     
     public AstParser [] toAltArray() {
         return new AstParser [] { this };
     }
     
     public AstParser [] toSeqArray() {
         return new AstParser [] { this };
     }
     
     public AstParserRule [] toRuleArray() {
         return null;
     }
     
     protected AstParser [] concat(AstParser [] left, AstParser [] right)
     {
         AstParser [] list = new AstParser [left.length + right.length];
         System.arraycopy(left, 0, list, 0, left.length);
         System.arraycopy(right, 0, list, left.length, right.length);
         return list;
     }

     public AstParser getHead() {
         return this;
     }
     
     public AstParser getTail() {
         return null;
     }
     
     public String getName() {
         return null;
     }
}
