package au.com.illyrian.parser;


public interface ParseMembers<T>
{
    /**
     * Parse a code fragment for a Domain Specific Language.
     * @param context information about the compilation
     * @return the result of parsing the input.
     * @throws Exception - if an error occurs.
     */
    public T parseMembers(CompilerContext context) throws ParserException;
}