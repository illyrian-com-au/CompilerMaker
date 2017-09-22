package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Value;


/**
 * Initialiser for a class that has just been instantiated.
 * </br>
 * An instance of this class is returned when <code>New</code> is called.
 * It can be used to call a constructor on the new instance.
 */
public class InitialiserImpl implements Initialiser
{
    private final ClassMaker maker;

    private final Value reference;

    /**
     * Constructor that takes the type of class being initialised.
     * 
     * @param classType
     */
    InitialiserImpl(ClassMaker maker, Value reference)
    {
        this.maker = maker;
        this.reference = reference;
        if (maker.getGen() != null) {
            maker.getGen().dup(reference.getType());
        }
    }

    /**
     * Calls a constructor from the base class that is appropriate for the
     * actual parameters.
     * </br>
     * Uses <code>MethodResolver</code> to determine the appropriate
     * constructor for the
     * actual parameters and invokes that constructor using the reference to
     * <code>super</code> on top of the stack. The first parameter to this
     * call must be <code>Super()</code>.
     * 
     * @param actualParameters
     *            the types of the actual parameters in the call stack
     * @return the return type of the called method
     */
    public Value Init(CallStack actualParameters)
    {
        if (maker.getGen() != null) {
            maker.Init(reference, actualParameters);
        }
        return reference;
    }
}


