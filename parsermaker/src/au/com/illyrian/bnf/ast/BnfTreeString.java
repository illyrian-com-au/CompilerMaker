package au.com.illyrian.bnf.ast;

import java.util.Set;

import au.com.illyrian.bnf.maker.BnfMakerVisitor;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.ParserException;

public class BnfTreeString <T> extends BnfTreeBase <T>
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

    public Type resolveType(BnfMakerVisitor visitor) throws ParserException
    {
        return visitor.resolveType(this);
    }

    public String toString() {
        return "\"" + value + "\"";
    }

}
