package au.com.illyrian.classmaker;

import org.mozilla.classfile.ByteCode;

import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

/**
 * Represents a <code>For</code> statement.
 * Manages the jump labels and generates the bytecode for the <code>Loop</code> statement.
 */
class ForStatement extends LoopStatement implements ForWhile, ForStep
{
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
    protected boolean calledStep  = false;

    /** Top of the For loop statement. */
    public void Loop() throws ClassMakerException
    {
        if (getClassFileWriter() == null) return;
        maker.markLineNumber(); // possibly add a new line number entry.
        beginLoop = cfw.acquireLabel();
        endLoop = cfw.acquireLabel();
        if (cfw.isDebugCode()) cfw.setDebugComment("For loop");
        cfw.markLabel(beginLoop);
        cfw.add(ByteCode.NOP);
    }

    /**
     * Implements a While clause of a For loop.
     *
     * Jumps to the end of the loop if the condition eveluates to true; otherwise
     * jumps to the body of the loop.
     *
     * @param condition the type of the condition must be <code>boolean</code>
     * @return an interface for the <code>Step</code> clause
     */
    public ForStep While(Type condition) throws ClassMakerException
    {
        if (getClassFileWriter() == null) return this;
        maker.markLineNumber(); // possibly add a new line number entry.
        if (condition != null)
        {
                if (!PrimitiveType.BOOLEAN_TYPE.equals(condition))
                {
                    throw maker.createException("ClassMaker.WhileConditionMustBeTypeBooleanNot_1", condition.getName());
                }
                if (cfw.isDebugCode()) cfw.setDebugComment("For while");
                // Boolean value on stack will be 1 (true) to continute Loop or 0 (false) to exit Loop.
                cfw.add(ByteCode.IFEQ, endLoop);   // Break out of the loop if equal to zero.
    	
        }
        endStep = cfw.acquireLabel();
        beginStep = cfw.acquireLabel();
        cfw.add(ByteCode.GOTO, endStep);
        cfw.markLabel(beginStep);
        breakCount++;
        calledWhile = true;
        return this;
    }

    /**
     * Implements a Step clause of a For loop.
     *
     * Jumps to the begining of the loop after evaluating the Step clause.
     *
     * @param step the type of the Step expression
     * @return an interface to set a Label
     */
    public Labelled Step(Type step) throws ClassMakerException
    {
        if (getClassFileWriter() == null) return this;
        if (cfw.isDebugCode()) cfw.setDebugComment("For step");
        if (step != null)
            maker.Eval(step);
        maker.markLineNumber(); // possibly add a new line number entry.

        cfw.add(ByteCode.GOTO, beginLoop);
        cfw.markLabel(endStep);
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
    public void EndLoop() throws ClassMakerException
    {
        if (getClassFileWriter() != null)
        {
            if (breakCount == 0)
                throw maker.createException("ClassMaker.ForDoesNotContainBreak");
            maker.markLineNumber(); // possibly add a new line number entry.

            if (cfw.isDebugCode()) cfw.setDebugComment("For end");
            // Handles a For() statement with or without While() and Step() clauses.
            if (!calledWhile)
            {
                cfw.add(ByteCode.GOTO, beginLoop);
            }
            else if (!calledStep)
            {
                cfw.add(ByteCode.GOTO, beginLoop);
                cfw.markLabel(endStep);
                cfw.add(ByteCode.GOTO, beginStep);
            }
            else
                cfw.add(ByteCode.GOTO, beginStep);
            cfw.markLabel(endLoop);
        }
        // Pop ForStatement off the stack.
        dispose();
    }

    /**
     * Iterates through the loop once more, calling the Step code if appropriate.
     */
    protected void continueLoop()
    {
        if (!calledWhile)
            // Jumps to the begining of the loop
            cfw.add(ByteCode.GOTO, beginLoop);
        else
            // Jumps to the Step code and then to the begining of the loop
            cfw.add(ByteCode.GOTO, beginStep);
    }
}