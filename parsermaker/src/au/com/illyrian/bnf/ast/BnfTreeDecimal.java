package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfTreeDecimal <T> extends BnfTreeBase <T>
{
    private final Float value;
    
    public BnfTreeDecimal(Float value)
    {
        this.value = value;
    }

    public Float getValue()
    {
        return value;
    }

    public boolean matches(BnfTree other)
    {
        if (other instanceof BnfTreeDecimal)
        {
            BnfTreeDecimal otherValue = (BnfTreeDecimal)other;
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
