package au.com.illyrian.parser;


public interface ParseMember
{
    /**
     * Parse a code fragment for a Domain Specific Language.
     * @return the result of parsing the input.
     * @throws Exception - if an error occurs.
     */
    public Object parseMember() throws ParserException;
}