package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Value;

/**
 * Represents a <code>For</code> statement.
 * Manages the jump labels and generates the bytecode for the <code>Loop</code>
 * statement.
 */
class ForStatement extends LoopStatement implements ForWhile, ForStep {
    protected ForStatement(ClassMaker maker) {
        super(maker);
    }

    /** Jump label for the start of the step. */
    protected int beginStep = 0;

    /** Jump label for the end of the step. */
    protected int endStep = 0;

    /** Set <code>true</code> when <code>While</code> is called. */
    protected boolean calledWhile = false;

    /** Set <code>true</code> when <code>Step</code> is called. */
    protected boolean calledStep = false;

    /** Top of the For loop statement. */
    public void For(Value declare) throws ClassMakerException {
        if (isFirstPass()) {
            return;
        }
        if (maker.isDebugCode()) {
            maker.setDebugComment("For loop");
        }
        if (declare != null) {
            maker.Eval(declare);
        }
        Loop();
    }

    /**
     * Implements a While clause of a For loop.
     *
     * Jumps to the end of the loop if the condition evaluates to true;
     * otherwise
     * jumps to the body of the loop.
     *
     * @param condition the type of the condition must be <code>boolean</code>
     * @return an interface for the <code>Step</code> clause
     */
    public ForStep While(Value condition) throws ClassMakerException {
        if (isFirstPass()) {
            return this;
        }
        maker.markLineNumber(); // possibly add a new line number entry.
        if (condition != null) {
            if (!ClassMakerFactory.BOOLEAN_TYPE.equals(condition.getType())) {
                throw maker.createException("ClassMaker.WhileConditionMustBeTypeBooleanNot_1", condition.getName());
            }
            if (maker.isDebugCode()) {
                maker.setDebugComment("For while");
            }
            // Boolean value on stack will be 1 (true) to continue Loop or 0 (false) to exit Loop.
            jumpIfEqualZero(endLoop);
        }
        endStep = acquireLabel();
        beginStep = acquireLabel();
        jumpTo(endStep);
        markLabel(beginStep);
        breakCount++;
        calledWhile = true;
        return this;
    }

    /**
     * Implements a Step clause of a For loop.
     *
     * Jumps to the beginning of the loop after evaluating the Step clause.
     *
     * @param step the type of the Step expression
     * @return an interface to set a Label
     */
    public Labelled Step(Value step) throws ClassMakerException {
        if (isFirstPass()) {
            return this;
        }
        if (maker.isDebugCode()) {
            maker.setDebugComment("For step");
        }
        if (step != null) {
            maker.Eval(step);
        }
        maker.markLineNumber(); // possibly add a new line number entry.

        jumpTo(beginLoop);
        markLabel(endStep);
        calledStep = true;
        return this;
    }

    /**
     * Ends a <code>For</code> statement.
     *
     * Jumps to the <code>Step</code> clause of the <code>For</code> statement.
     * A <code>Break</code> will jump to the end of this clause;
     * thus terminating the loop.
     */
    public void EndLoop() throws ClassMakerException {
        if (!isFirstPass()) {
            if (breakCount == 0) {
                throw maker.createException("ClassMaker.ForDoesNotContainBreak");
            }
            maker.markLineNumber(); // possibly add a new line number entry.

            if (maker.isDebugCode()) {
                maker.setDebugComment("For end");
            }
            // Handles a For() statement with or without While() and Step() clauses.
            if (!calledWhile) {
                jumpTo(beginLoop);
            } else if (!calledStep) {
                jumpTo(beginLoop);
                markLabel(endStep);
                jumpTo(beginStep);
            } else {
                jumpTo(beginStep);
            }
            markLabel(endLoop);
        }
        // Pop ForStatement off the stack.
        //dispose();
    }

    /**
     * Iterates through the loop once more, calling the Step code if
     * appropriate.
     */
    protected void continueLoop() {
        if (!calledWhile) {
            // Jumps to the beginning of the loop
            jumpTo(beginLoop);
        } else {
            // Jumps to the Step code and then to the beginning of the loop
            jumpTo(beginStep);
        }
    }
}