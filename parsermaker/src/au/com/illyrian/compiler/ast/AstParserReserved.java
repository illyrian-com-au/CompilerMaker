package au.com.illyrian.compiler.ast;

public class AstParserReserved extends AstParserName
{
    public AstParserReserved(String name)
    {
        super(name);
    }

    public void resolveRule(AstParserVisitor visitor)
    {
        visitor.resolveRule(this);
    }
    
    public boolean matches(AstParser other)
    {
        if (other instanceof AstParserReserved)
        {
            AstParserReserved otherReserved = (AstParserReserved)other;
            return getName().matches(otherReserved.getName());
        }
        return false;
    }
    
    public String toString() {
        return "<" + getName() + ">";
    }
}
