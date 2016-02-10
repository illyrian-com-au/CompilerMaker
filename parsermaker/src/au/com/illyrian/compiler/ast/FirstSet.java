package au.com.illyrian.compiler.ast;

import java.util.Arrays;
import java.util.HashSet;

public class FirstSet extends HashSet<String>
{
    private static final long serialVersionUID = 1832471208457914351L;
    private static final String[] STRING_ARRAY = new String[0];

    final String name;

    public FirstSet(String name) {
        this.name = name;
    }
    
    public String [] toArray() {
        String [] list = toArray(STRING_ARRAY);
        Arrays.sort(list);
        return list;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("first(").append(name).append(")=[");
        String[] list = toArray();
        for (int i=0; i<list.length; i++) {
            if (i>0) buf.append(", ");
            buf.append(list[i]);
        }
        buf.append("]");
        return buf.toString();
    }
}
