package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;


public interface InvokeParser<T>
{
    public T invokeParseModule(String parseName, Input input) throws ParserException;

    public T invokeParseClass(String parseName, Input input) throws ParserException;

    public T invokeParseMember(String parseName, Input input) throws ParserException;

    public T invokeParseStatement(String parseName, Input input) throws ParserException;

    public T invokeParseExpression(String parseName, Input input) throws ParserException;
}