package au.com.illyrian.parser.impl;

public interface InvokeParser<T>
{
    public T invokeParseModule(String parseName);

    public T invokeParseClass(String parseName);

    public T invokeParseMember(String parseName);

    public T invokeParseStatement(String parseName);

    public T invokeParseExpression(String parseName);
}