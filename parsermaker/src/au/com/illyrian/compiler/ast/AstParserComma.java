package au.com.illyrian.compiler.ast;

import java.util.Set;

import au.com.illyrian.parser.ParserException;

public class AstParserComma extends AstParserBinary
{
    public AstParserComma(AstParser left, AstParser right)
    {
        super(left, right);
    }

    @Override
    public boolean resolveFirst(AstFirstVisitor visitor, Set<String> firstSet) throws ParserException
    {
        return false;
    }

    public String toString() {
        return getLeft() + ", " + getRight();
    }
}
