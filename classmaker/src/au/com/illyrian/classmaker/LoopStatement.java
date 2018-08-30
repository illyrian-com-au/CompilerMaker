package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Type;

/**
 * Represents a <code>Loop</code> statement.
 * Manages the jump labels and generates the bytecode for the <code>Loop</code>
 * statement.
 */
class LoopStatement extends Statement {
    protected LoopStatement(ClassMaker maker) {
        super(maker);
    }

    /** Jump label for the start of the loop. */
    protected int beginLoop = 0;

    /** Jump label for the end of the loop. */
    protected int endLoop = 0;

    /**
     * There must be at least one <code>Break</code> or equivalent within the
     * <code>Loop</code>.
     */
    protected int breakCount = 0;

    /**
     * Begins a <code>Loop</code> statement.
     * Control will jump here from the <code>EndLoop</code> clause or from
     * an enclosed <code>Continue</code> statement.
     * The loop will not terminate unless there is an enclosed statement that
     * breaks out of the loop, for example, <code>While</code> or
     * <code>Break</code>.
     */
    public void Loop() throws ClassMakerException {
        if (isFirstPass()) {
            return;
        }
        if (maker.isDebugCode()) {
            maker.setDebugComment("Loop()");
        }
        maker.markLineNumber(); // possibly add a new line number entry.

        beginLoop = acquireLabel();
        endLoop = acquireLabel();
        markLabel(beginLoop);
    }

    /**
     * Ends a <code>Loop</code> statement.
     * Jumps to the <code>Loop</code> clause.
     *
     * A <code>Break</code> or <code>While</code> will jump to the end of this
     * clause;
     * thus terminating the loop.
     */
    public void EndLoop() throws ClassMakerException {
        if (!isFirstPass()) {
            if (breakCount == 0) {
                throw maker.createException("ClassMaker.LoopDoesNotContainBreak");
            }
            maker.markLineNumber(); // possibly add a new line number entry.

            if (maker.isDebugCode()) {
                maker.setDebugComment("   Jump to begining of Loop");
            }
            jumpTo(beginLoop);

            if (maker.isDebugCode()) {
                maker.setDebugComment("End of Loop");
            }
            markLabel(endLoop);
        }
        // Pop LoopStatement off statement stack.
        dispose();
    }

    /**
     * Iterates through a <code>Loop</code> while the condition is
     * <code>true</code> Breaks out of the enclosing <code>Loop</code> when the
     * condition is <code>false</code>.
     * The <code>While</code> clause should be the first or last in a loop;
     * however, this is not enforced.
     * 
     * @param condition the type of the condition expression must be boolean
     */
    public ForStep While(Type condition) throws ClassMakerException {
        if (isFirstPass()) {
            return null;
        }
        if (!ClassMakerFactory.BOOLEAN_TYPE.equals(condition)) {
            throw maker.createException("ClassMaker.WhileConditionMustBeTypeBooleanNot_1", condition.getName());
        }
        maker.markLineNumber(); // possibly add a new line number entry.
        if (maker.isDebugCode()) {
            maker.setDebugComment("    jump conditional to end of loop");
        }
        // Boolean value on stack will be 1 (true) to continue Loop or 0 (false) to exit Loop.
        this.jumpIfEqualZero(endLoop);
        breakCount++;
        return null;
    }

    /**
     * Jumps to <code>EndLoop</code> when <code>Break</code> is called.
     * The <code>label</code> must also match this statement if it is provided;
     * otherwise, passes the jump request down the statement stack.
     * 
     * @param jumpTarget <code>ClassMaker.BREAK</code>,
     *            <code>ClassMaker.CONTINUE</code> or
     *            <code>ClassMaker.RETURN</code>.
     * @param label the name of the statement to jump to or <code>null</code>
     * @return the target <code>Statement</code> or <code>null</code> if not
     *         found.
     */
    protected Statement jumpToTarget(String jumpTarget, String label) {
        if (ClassMaker.BREAK.equals(jumpTarget) && (label == null || label.equals(getLabel()))) { 
            // Break jumps to the end of the loop
            if (maker.isDebugCode()) {
                maker.setDebugComment("    Break jumps to end of loop " + (label == null ? "" : label));
            }
            jumpTo(endLoop);
            breakCount++;
        } else if (ClassMaker.CONTINUE.equals(jumpTarget) && (label == null || label.equals(getLabel()))) {
            // Continue jumps to the start of the loop
            if (maker.isDebugCode()) {
                maker.setDebugComment("    Continue jumps to start of loop " + (label == null ? "" : label));
            }
            continueLoop();
        } else {
            // We have not found the appropriate break.
            if (ClassMaker.BREAK.equals(jumpTarget)) {
                breakCount++; // indicate that this statement has at least one break.
            }
            // Pass the request down the statement stack
            return super.jumpToTarget(jumpTarget, label);
        }
        return this;
    }

    /** Jumps to the beginning of the loop. */
    protected void continueLoop() {
        jumpTo(beginLoop);
    }

    protected int getStatementEnd() {
        return endLoop;
    }
}