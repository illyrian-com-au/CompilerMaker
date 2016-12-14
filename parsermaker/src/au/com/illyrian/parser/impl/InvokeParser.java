package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.ParserException;


public interface InvokeParser<T>
{
    public T invokeParseModule(String parseName) throws ParserException;

    public T invokeParseClass(String parseName) throws ParserException;

    public T invokeParseMember(String parseName) throws ParserException;

    public T invokeParseStatement(String parseName) throws ParserException;

    public T invokeParseExpression(String parseName) throws ParserException;
}