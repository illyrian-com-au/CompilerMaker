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
        return rule.replace(rule.getTarget(), newBody);
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
        AstParser merged  = mergeAlternatives(grouped, 0, 1);
        return merged;
    }

    AstParser [] groupCommonHeads(AstParser [] original)
    {
        // Perform a bubble sort to find matching heads and group them together.
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
    
    AstParser mergeAlternatives(AstParser [] list, int common, int offset)
    {
        if (offset == list.length)
            return list[common].resolveMerge(this);
        AstParser result = null;
        if (list[common].getHead().matches(list[offset].getHead())) {
            AstParser temp = mergeCommonHeads(list[common], list[offset]);
            list[common] = temp;
            list[offset] = null;
            result = mergeAlternatives(list, common, offset+1);
        } else {
            AstParser right = mergeAlternatives(list, offset, offset+1);
            AstParser left  = list[common].resolveMerge(this); 
            result = alternative(left, right);
        }
        return result;
    }
    
    AstParser mergeCommonHeads(AstParser left, AstParser right)
    {
        // ( common alt1 | common alt2 )
        AstParserAlternative sub = new AstParserAlternative(left.getTail(), right.getTail());
        AstParserSequence    seq = new AstParserSequence(left.getHead(), sub);
        // ( common ( alt1 | alt2 ) )
        return seq;
    }
    
    AstParser alternative(AstParser alt1, AstParser alt2) {
        if (alt1 == null) {
            return alt2;
        } else if (alt2 == null) {
            return alt1; 
        } else {
            return new AstParserAlternative(alt1, alt2);
        }
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
