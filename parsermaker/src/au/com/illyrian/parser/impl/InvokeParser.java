package au.com.illyrian.parser.impl;

import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;


public interface InvokeParser
{
    public Object invokeParseModule(String parseName, Input input) throws ParserException;

    public Object invokeParseClass(String parseName, Input input) throws ParserException;

    public Object invokeParseMember(String parseName, Input input) throws ParserException;

    public Object invokeParseStatement(String parseName, Input input) throws ParserException;

    public Object invokeParseExpression(String parseName, Input input) throws ParserException;
}