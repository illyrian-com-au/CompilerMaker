package au.com.illyrian.parser;


public interface ParseStatement<T>
{
    /**
     * Parse a code fragment for a Domain Specific Language.
     * @return the result of parsing the input.
     * @throws Exception - if an error occurs.
     */
    public T parseStatement() throws ParserException;
}