package au.com.illyrian.parser;


public interface ParseModule
{
    /**
     * Parse a code fragment for a Domain Specific Language.
     * @return the result of parsing the input.
     * @throws Exception - if an error occurs.
     */
    public Object parseModule() throws ParserException;
}