package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Value;

/**
 * Interface for the <code>Step</code> part of a <code>For</code> loop.
 */
public interface ForStep
{
    /**
     * The <code>Step</code> part of a <code>For</code> loop.
     * </br>
     * Used in a daisy chain like this.
     * <pre>
     *    For( <expression> ).While( <condition> ).Step( <increment> );
     * </pre>
     * @param step the <code>Type</code> of the increment expression
     * @return an interface to allow the <code>For</code> statement to be labeled
     * @throws ClassMakerException
     */
    Labelled Step(Value step) throws ClassMakerException;
}