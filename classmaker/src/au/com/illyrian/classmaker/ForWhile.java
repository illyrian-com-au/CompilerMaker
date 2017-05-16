package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Value;

/**
 * Interface for the <code>While</code> part of a <code>For</code> loop.
 */
public interface ForWhile
{
    /**
     * The <code>While</code> part of a <code>For</code> loop.
     * </br>
     * Used in a daisy chain like this.
     * <pre>
     *    For( <expression> ).While( <condition> ).Step( <increment> );
     * </pre>
     * @param condition the <code>Type</code> of the condition expression
     * @return an interface to allow the <code>For</code> statement to be labeled
     * @throws ClassMakerException if the condition type is not boolean
     */
    ForStep While(Value condition) throws ClassMakerException;
}