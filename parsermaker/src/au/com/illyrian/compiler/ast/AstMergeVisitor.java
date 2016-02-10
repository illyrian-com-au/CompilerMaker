package au.com.illyrian.compiler.ast;


public class AstMergeVisitor
{
    public AstMergeVisitor()
    {
    }
    
    public AstParserBase resolveMerge(AstParserList binary)
    {
        AstParser head = null;
        if (binary.getLeft() != null) {
            head = binary.getLeft().resolveMerge(this);
        }
        AstParser tail = null;
        if (binary.getRight() != null) {
            tail = binary.getRight().resolveMerge(this);
        }
        return binary.replace(head, tail);
    }

    public AstParser resolveMerge(AstParserRule rule)
    {
        AstParser newBody = rule.getBody().resolveMerge(this);
        return rule.replicate(rule.getTarget(), newBody);
    }

    public AstParserBase resolveMerge(AstParserBinary binary)
    {
        AstParser head = null;
        if (binary.getLeft() != null) {
            head = binary.getLeft().resolveMerge(this);
        }
        AstParser tail = null;
        if (binary.getRight() != null) {
            tail = binary.getRight().resolveMerge(this);
        }
        return binary.replace(head, tail);
    }

    public AstParser resolveMerge(AstParserAlternative alt)
    {
        AstParser [] grouped = groupCommonHeads(alt.toAltArray());
        AstParser merged  = mergeAlternatives(grouped, 0);
        return merged;
    }

    public AstParser [] groupCommonHeads(AstParser [] original)
    {
        AstParser [] list = clone(original);
        for (int i=0; i<list.length; i++) {
            AstParser target = list[i];
            for (int j=list.length-1; j>i+1; j--) {
                if (target.matches(list[j]) && !target.matches(list[j-1])) {
                    AstParser temp = list[j];
                    list[j] = list[j-1];
                    list[j-1] = temp;
                }
            }
        }
        return list;
    }
    
    AstParser alternative(AstParser alt1, AstParser alt2) {
        if (alt1 == null && alt2 == null) {
            return null; 
        } else {
            return new AstParserAlternative(alt1, alt2);
        }
    }

    AstParser sequence(AstParser seq1, AstParser seq2) {
        if (seq1 == null) {
            return seq2;
        } else if (seq2 == null) {
            return seq1; 
        } else {
            return new AstParserSequence(seq1, seq2);
        }
    }
    
    public AstParser mergeAlternatives(AstParser [] list, int offset)
    {
        return mergeAlternatives(list, 0, 1);
    }

    public AstParser mergeAlternatives(AstParser [] list, int common, int offset)
    {
        if (offset == list.length)
            return list[common].resolveMerge(this);
        AstParser result = null;
        if (list[common].getHead().matches(list[offset].getHead())) {
            AstParser temp = merge(list[common], list[offset]);
            list[common] = temp;
            list[offset] = null;
            result = mergeAlternatives(list, common, offset+1);
        } else {
            AstParser right = mergeAlternatives(list, offset, offset+1);
            AstParser left  = list[common].resolveMerge(this); 
            result = (right == null) ? left : new AstParserAlternative(left, right);
        }
        return result;
    }
    
    public AstParser merge(AstParser left, AstParser right)
    {
        // ( common alt1 | common alt2 )
        AstParserAlternative sub = new AstParserAlternative(left.getTail(), right.getTail());
        AstParserSequence    seq = new AstParserSequence(left.getHead(), sub);
        return seq;
    }
    
    AstParser [] clone(AstParser [] list) {
        AstParser [] temp = new AstParser [list.length];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;

    }
    
    public AstParserBase resolveMerge(AstParserBase item)
    {
        return item;
    }
}
