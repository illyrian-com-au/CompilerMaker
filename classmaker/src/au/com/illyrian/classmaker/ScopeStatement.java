package au.com.illyrian.classmaker;

/**
 * Represents the scope of enclosed variables.
 */
class ScopeStatement extends Statement
{
     protected ScopeStatement(ClassMaker maker) {
        super(maker);
    }
    
    /** Break target at the end of labelled block. */
    protected int blockEnd = 0;

    public void Begin()
    {
        if (!isFirstPass()) {
            blockEnd = acquireLabel();
        }
    }

    /**
     * Ends the body of a method.
     * Checks that the method body ends with a <code>Return</code> or <code>Throw</code>
     * statement.
     */
    public void End() throws ClassMakerException
    {
        if (!isFirstPass()) { 
            markLabel(blockEnd);
            // Save local variable descriptors to be used by the debugger.
            maker.getLocalFields().exitScope(getScopeLevel(), maker.getProgramCounter());
        }
    }

    public int getScopeLevel()
    {
        return super.getScopeLevel() +1;
    }

    protected int getStatementEnd()
    {
        return blockEnd;
    }
}