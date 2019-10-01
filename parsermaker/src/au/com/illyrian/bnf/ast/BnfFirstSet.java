package au.com.illyrian.bnf.ast;

import java.util.Arrays;
import java.util.HashMap;

import au.com.illyrian.parser.ParserException;

public class BnfFirstSet
{
    private static final String[] STRING_ARRAY = new String[0];

    final HashMap<String, BnfTree> hashMap;
    final String name;

    public BnfFirstSet(String name) {
        this.name = name;
        hashMap = new HashMap<String, BnfTree>();
    }
    
    public void add(String name, BnfTree tree) {
        hashMap.put(name, tree);
    }
    
    public void addOnce(BnfTree tree) {
        String name = tree.getName();
        addOnce(name, tree);
    }
    
    public void addOnce(String name, BnfTree tree) {
        if (contains(name)) {
            throw new ParserException("Ambiguous grammer on terminal: " + name);
        } else if (name != BnfFirstVisitor.EMPTY) {
            add(name, tree);
        }
    }
    
    public BnfTree get(String name) {
        return hashMap.get(name);
    }
    
    public boolean contains(String key) {
        return hashMap.containsKey(key);
    }
    
    void merge(BnfFirstSet set) {
        for (String name: set.toKeyArray()) {
            addOnce(name, set.get(name));
        }
    }

    public String [] toKeyArray() {
        String [] list = hashMap.keySet().toArray(STRING_ARRAY);
        Arrays.sort(list);
        return list;
    }
    
    public BnfTree [] toArray() {
        String [] keys = toKeyArray();
        int len = keys.length;
        BnfTree [] list = new BnfTree [len];
        for (int i=0; i<len; i++) {
            list[i] = get(keys[i]);
        }
        return list;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("first(").append(name).append(")=[");
        String[] list = toKeyArray();
        for (int i=0; i<list.length; i++) {
            if (i>0) buf.append(", ");
            buf.append(list[i]);
        }
        buf.append("]");
        return buf.toString();
    }
}
