package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Value;

/**
 * Represents an <code>If Then Else </code> statement.
 * Manages the jump labels and generates the bytecode for the <code>If</code>
 * statement.
 */
class IfStatement extends Statement
{
    protected IfStatement(ClassMaker maker)
    {
        super(maker);
    }

    /** Jump label at the end of the Then block. */
    protected int jumpThen = 0;

    /** Jump label at the end of the Else block. */
    protected int jumpElse = 0;

    /** Jump label at the end of the If-Then-Else block. */
    protected int endStatement = 0;

    /*
     * Tried implementing Elsif(condition) but Else must be called before
     * condition is evaluated so cannot be solved without daisy chaining.
     * Not worth the effort. Donald Strong 25/08/2011.
     */

    /**
     * Begins an <code>If</code> statement.
     * The subsequent code block is executed if the <code>condition</code>
     * evaluates to <code>true</code>.
     */
    public void If(Value condition) throws ClassMakerException
    {
        if (isFirstPass()) {
            return;
        }
        if (!ClassMakerFactory.BOOLEAN_TYPE.equals(condition.getType())) {
            dispose();
            throw maker.createException("ClassMaker.IfConditionMustBeBoolean_1", condition.getName());
        }
        maker.markLineNumber(); // possibly add a new line number entry.
        if (maker.isDebugCode()) {
            maker.setDebugComment("If");
        }
        endStatement = acquireLabel();
        // Boolean value on stack will be 1 to execute Then block or 0 to
        // execute Else block.
        jumpThen = acquireLabel();
        jumpIfEqualZero(jumpThen); // Jump over Then block if equal to zero.
    }

    /**
     * Begins an <code>Else</code> clause of an <code>If</code> statement.
     * The subsequent code block is executed if the <code>condition</code> in
     * the <code>If</code> clause evaluated to <code>false</code>.
     */
    public void Else() throws ClassMakerException
    {
        if (isFirstPass()) {
            return;
        }
        if (jumpElse != 0) {
            throw maker.createException("ClassMaker.ElseCalledTwice");
        }
        maker.markLineNumber(); // possibly add a new line number entry.
        if (maker.isDebugCode()) {
            maker.setDebugComment("Else");
        }
        jumpElse = acquireLabel();
        jumpTo(jumpElse);
        markLabel(jumpThen);
    }

    /**
     * Ends an <code>If</code> Statement.
     * Control jumps to here if the preceeding code block is not executed.
     */
    public void EndIf() throws ClassMakerException
    {
        if (!isFirstPass()) {
            maker.markLineNumber(); // possibly add a new line number entry.
            if (maker.isDebugCode()) {
                maker.setDebugComment("End If");
            }
            if (jumpElse != 0) {
                markLabel(jumpElse);
            } else {
                markLabel(jumpThen);
            }
            markLabel(endStatement);
        }
        // Pop IfStatement off statement stack.
        dispose();
    }

    protected int getStatementEnd()
    {
        return endStatement;
    }
}