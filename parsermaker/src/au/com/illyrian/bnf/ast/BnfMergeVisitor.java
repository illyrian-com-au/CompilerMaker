package au.com.illyrian.bnf.ast;


public class BnfMergeVisitor
{
    public BnfMergeVisitor()
    {
    }
    
    public BnfTreeParser resolveMerge(BnfTreeParser goal)
    {
        BnfTree list = goal.getRules().resolveMerge(this);
        return goal.replace(list);
    }
    
    public BnfTreeRule resolveMerge(BnfTreeRule rule)
    {
        BnfTree newBody = rule.getBody().resolveMerge(this);
        return rule.replace(rule.getTarget(), newBody);
    }

    public BnfTreeBinary resolveMerge(BnfTreeBinary binary)
    {
        BnfTree head = null;
        if (binary.getLeft() != null) {
            head = binary.getLeft().resolveMerge(this);
        }
        BnfTree tail = null;
        if (binary.getRight() != null) {
            tail = binary.getRight().resolveMerge(this);
        }
        return binary.replace(head, tail);
    }

    public BnfTree resolveMerge(BnfTreeAlternative alt)
    {
        BnfTree [] grouped = groupCommonHeads(alt.toAltArray());
        BnfTree merged  = mergeAlternatives(grouped, 0, 1);
        return merged;
    }

    BnfTree [] groupCommonHeads(BnfTree [] original)
    {
        // Perform a bubble sort (filter) to find matching heads and group them together.
        // This is actually a filter because nothing will change if all heads are unique.
        // The bubble sort algorithm retains secondary order and while not particularly efficient
        // it is a simple algorithm and we are typically filtering less than half a dozen options.
        BnfTree [] list = clone(original);
        for (int i=0; i<list.length; i++) {
            BnfTree target = list[i];
            // Starting at the end of the list bubble up any items that match the target.
            for (int j=list.length-1; j>i; j--) {
                // Switch to another matching option if it is further up the list.
                if (target.matches(list[j]) && !target.matches(list[j-1])) {
                    swap(list, j, j-1);
//                    BnfTree temp = list[j];
//                    list[j] = list[j-1];
//                    list[j-1] = temp;
                } else if (list[j-1].isEmpty() && !list[j].isMacro()) {
                    swap(list, j, j-1);
                }
            }
        }
        return list;
    }
    
    void swap(BnfTree [] list, int i, int j) {
        BnfTree temp = list[i];
        list[i] = list[j];
        list[j] = temp;
    }
    
    BnfTree mergeAlternatives(BnfTree [] list, int common, int offset)
    {
        if (offset == list.length)
            return list[common].resolveMerge(this);
        BnfTree result = null;
        if (list[common].getHead().matches(list[offset].getHead())) {
            BnfTree temp = mergeCommonHeads(list[common], list[offset]);
            list[common] = temp;
            list[offset] = null;
            result = mergeAlternatives(list, common, offset+1);
        } else {
            BnfTree right = mergeAlternatives(list, offset, offset+1);
            BnfTree left  = list[common].resolveMerge(this); 
            result = alternative(left, right);
        }
        return result;
    }
    
    BnfTree mergeCommonHeads(BnfTree left, BnfTree right)
    {
        // ( common alt1 | common alt2 )
        BnfTreeAlternative sub = new BnfTreeAlternative(left.getTail(), right.getTail());
        BnfTreeSequence    seq = new BnfTreeSequence(left.getHead(), sub);
        // ( common ( alt1 | alt2 ) )
        return seq;
    }
    
    BnfTree alternative(BnfTree alt1, BnfTree alt2) {
        if (alt1 == null) {
            return alt2;
        } else if (alt2 == null) {
            return alt1; 
        } else {
            return new BnfTreeAlternative(alt1, alt2);
        }
    }

    BnfTree [] clone(BnfTree [] list) {
        BnfTree [] temp = new BnfTree [list.length];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;

    }
    
    public BnfTreeBase resolveMerge(BnfTreeBase item)
    {
        return item;
    }
}
