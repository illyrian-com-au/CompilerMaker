package au.com.illyrian.compiler.ast;

public class AstParserString extends AstParserBase
{
    private final String value;
    
    public AstParserString(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }
    
    public boolean matches(AstParser other)
    {
        if (other instanceof AstParserString)
        {
            AstParserString otherString = (AstParserString)other;
            return value.equals(otherString.value);
        }
        return false;
    }
    
    public String toString() {
        return "\"" + value + "\"";
    }

}
