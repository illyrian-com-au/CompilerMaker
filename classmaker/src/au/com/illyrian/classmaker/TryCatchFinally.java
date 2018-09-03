package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.Type;

/**
 * Represents a <code>Try Catch Finally</code> statement.
 * Assists the generation of code for the statement by managing the
 * jump addresses and subroutines.
 */
public class TryCatchFinally extends Statement
{
    /* Start of the try block is a parameter to the exception handlers. */
    int startTryBlock = 0;

    /* End of the try block is a parameter to the exception handlers. */
    int endTryBlock = 0;

    /* Jump to the end of the catch block from the try block and each exception handler. */
    int endCatchBlock = 0;

    /* Reference to the finally subroutine which is called from many places. */
    int finallySubroutine = 0;

    /* An anonymous local variable holds the return PC for the finally subroutine. */
    int finallyReturnSlot = 0;
    
    int finalyExceptionSlot = -1;
    
    public TryCatchFinally(ClassMaker classMaker) {
        super(classMaker);
    }
    
    /**
     * Begins a <code>Try Catch Finally</code> block.
     * </br>
     * Marks the start of the try block.
     */
    public void Try()
    {
        if (isFirstPass()) {
            return;
        }
        if (maker.isDebugCode()) {
            maker.setDebugComment("Try();");
        }
        startTryBlock = acquireLabel();
        endCatchBlock = acquireLabel();
        markLabel(startTryBlock);
    }

    /**
     * Catch an Exception type.
     * </br>
     * Catches the given Exception type and stores it in a local variable with the given name.
     * Also marks the begining of a block of code to handle the exception.
     * @param exceptionType the type of exception handled by this block of code
     * @param name the local variable name for the exception
     */
    public void Catch(Type exceptionType, String name) throws ClassMakerException
    {
        if (isFirstPass()) {
            return;
        }
        if (maker.isDebugCode()) {
            maker.setDebugComment("Catch(" + exceptionType + ", " + name  + ");");
        }
        endTryCatchBlock();

        // catch (Exception ex)
        int catchBlock = acquireLabel();
        String exceptionName = exceptionType.getName();
        getGen().catchException(startTryBlock, endTryBlock, catchBlock, exceptionName);

        markLabel(catchBlock);
        maker.markLineNumber(); // possibly add a new line number entry.

        // Store the exception reference in a local variable
        maker.Declare(name, exceptionType, 0);
        maker.Eval(maker.Set(name, exceptionType.getValue()));
    }

    void endTryCatchBlock2()
    {
        if (endTryBlock == 0)
        {
            endTryBlock = acquireLabel();
            markLabel(endTryBlock);
        }
        finalyExceptionSlot = maker.storeAnonymousValue(ClassMakerFactory.OBJECT_TYPE);
        // Jump over remaining catch and finally blocks.
        jumpTo(endCatchBlock);
    }

   /**
     * Starts a Finaly block.
     * </br>
     * Begins a subroutine that will always be executed regardless of the execution path.
     * The finally sunroutine is called:
     * <UL>
     *   <LI>after execution of the Try block completes normally</LI>
     *   <LI>after a Catch clause processes an exception</LI>
     *   <LI>as appropriate when Break, Continue or Return methods are called</LI>
     *   <LI>whenever an exception passes through the method without being caught.</LI>
     * </UL>
     */
    public void Finally()
    {
        if (isFirstPass()) {
            return;
        }
        if (maker.isDebugCode()) {
            maker.setDebugComment("Finally();");
        }
        endTryCatchBlock();

        // Start finally block
        int catchBlockAll = acquireLabel();
        getGen().catchException(startTryBlock, catchBlockAll, catchBlockAll, null);

        markLabel(catchBlockAll);
        finallySubroutine = acquireLabel();

        maker.markLineNumber(); // possibly add a new line number entry.
        // Store the exception pointer in an anonymous local variable.
        if (maker.isDebugCode()) {
            maker.setDebugComment("Store reference to exception");
        }
        int finalyExceptionAddress = maker.storeAnonymousValue(ClassMakerFactory.OBJECT_TYPE);

        // Jump to the finally subroutine
        if (finallySubroutine != 0) {
            getGen().callFinallySubroutine(finallySubroutine);
        }
 
        // Re-throw the exception.
        if (maker.isDebugCode()) {
            maker.setDebugComment("Load reference to exception");
        }
        maker.markLineNumber(); // possibly add a new line number entry.
        maker.loadAnonymousValue(finalyExceptionAddress);
        if (maker.isDebugCode()) {
            maker.setDebugComment("Rethrow exception");
        }
        getGen().Throw(ClassMakerFactory.OBJECT_TYPE);

        // Finally subroutine
        if (maker.isDebugCode()) {
            maker.setDebugComment("finally subroutine");
        }
        markLabel(finallySubroutine);

        // Store return address in an anonymous local variable.
        finallyReturnSlot = maker.storeAnonymousValue(ClassMakerFactory.OBJECT_TYPE);
    }

    /**
     * Generates the bytecode to end a <code>Try Catch Finally</code> block.
     * The <code>Try</code> block and all preceeding <code>Catch</code> blocks jump to here.
     * Completes from the finally subroutine and then calls it.
     */
    public void EndTry()
    {
        if (!isFirstPass()) {
            if (maker.isDebugCode())  {
            	maker.setDebugComment("EndTry();");
            }
            if (finallyReturnSlot != 0) {
                MakerField local = maker.getLocalFields().findLocalField(finallyReturnSlot);
                getGen().returnFinallySubroutine(local.getSlot());
            }
            markLabel(endCatchBlock);
            if (finallySubroutine != 0) {
                getGen().callFinallySubroutine(finallySubroutine);
            }
        }
        //dispose();
    }

    /**
     * Generates bytecode at the start of a <code>Catch</code> block.
     * </br>
     * Marks the bottom of the <code>Try</code> block so it can be used in
     * exception handlers.
     * Jumps over the following <code>Catch</code> blocks.
     */
    void endTryCatchBlock()
    {
        if (endTryBlock == 0) {
            endTryBlock = acquireLabel();
            markLabel(endTryBlock);
        }
        // Jump over remaining catch and finally blocks.
        jumpTo(endCatchBlock);
    }

    protected int getStatementEnd()
    {
    	return endCatchBlock;
    }

}