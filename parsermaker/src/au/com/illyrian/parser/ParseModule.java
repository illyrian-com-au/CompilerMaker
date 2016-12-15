package au.com.illyrian.parser;


public interface ParseModule<T>
{
    /**
     * Parse a code fragment for a Domain Specific Language.
     * @param context information about the compilation
     * @return the result of parsing the input.
     */
    public T parseModule(CompilerContext context);
}