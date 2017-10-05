package au.com.illyrian.classmaker;

/**
 * Represents the body of a method.
 * An instance of this class is always created when the body of a method
 * is entered. Consequently, there will always be an instance of this class
 * at the bottom of the stack.
 */
class MethodBodyStatement extends ScopeStatement
{
    protected MethodBodyStatement(ClassMaker maker) {
        super(maker);
    }
    
    /**
     * Jumps to a <code>Statement</code>.
     * Returns this instance if the jumpTarget is <code>ClassMaker.RETURN</code>;
     * all other options should have been handleded further up the statement stack.
     * Otherwise, calls <code>super.jumpToTarget</code> which will probably return null.
     * @param jumpTarget must be <code>ClassMaker.RETURN</code>.
     * @param label the name of the statement to jump to
     * @return the target <code>Statement</code> or <code>null</code> if not found.
     */
    protected Statement jumpToTarget(String jumpTarget, String label)
    {
        if (ClassMaker.RETURN.equals(jumpTarget) && getNext() == null)
        {
            return this;
        }
        return null;
    }

    /* Implements Labelled. */
    public void setLabel(String label)
    {
        throw maker.createException("ClassMaker.CannotSetLabelOnMethodBlock");
    }

    protected int getStatementEnd()
    {
        return 0;
    }

    public void Begin()
    {
        maker.bottomStatement = this;
        maker.BeginMethod();
    }

    /**
     * Ends the body of a method.
     * Checks that the method body ends with a <code>Return</code> or <code>Throw</code>
     * statement.
     * Delegates to <code>ScopeStatement.End</code>.
     */
    public void End() throws ClassMakerException
    {
        if (!isFirstPass()) {
            // Save local variable descriptors to be used by the debugger.
            maker.getLocalFields().exitScope(getScopeLevel());
        }
        maker.EndMethod();
        // Pop ScopeStatement off statement stack.
        dispose();
        // Pop MethodBodyStatement off statement stack.
        maker.bottomStatement = null;
    }

    public int getScopeLevel()
    {
        return 1;
    }

    /** Pops the Statement off the stack. */
    protected void dispose()
    {
        maker.bottomStatement = null;
        super.dispose();
    }
}