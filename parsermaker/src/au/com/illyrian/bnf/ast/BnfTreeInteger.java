package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfTreeInteger <T> extends BnfTreeBase <T>
{
    private final Integer value;
    
    public BnfTreeInteger(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return value;
    }

    public boolean matches(BnfTree other)
    {
        if (other instanceof BnfTreeInteger)
        {
            BnfTreeInteger otherValue = (BnfTreeInteger)other;
            return value.equals(otherValue.value);
        }
        return false;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return false;
    }

    public String toString() {
        return "" + value;
    }

}
