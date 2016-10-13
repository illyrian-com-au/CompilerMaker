package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class BnfTreeString extends BnfTreeBase
{
    private final String value;
    
    public BnfTreeString(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public boolean matches(BnfTree other)
    {
        if (other instanceof BnfTreeString)
        {
            BnfTreeString otherValue = (BnfTreeString)other;
            return value.equals(otherValue.value);
        }
        return false;
    }
    
    public boolean resolveFirst(BnfFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return visitor.resolveFirst(this, firstSet);
    }

    public String toString() {
        return "\"" + value + "\"";
    }

}
