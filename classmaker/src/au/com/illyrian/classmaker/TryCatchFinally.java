package au.com.illyrian.classmaker;

import org.mozilla.classfile.ByteCode;

import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ClassType;
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
    int startFinallyBlock = 0;

    /* An anonomous local variable holds the return PC for the finally subroutine. */
    int finallyReturnSlot = 0;
    int endFinallyBlock = 0;
    
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
        if (cfw == null) return;
        if (cfw.isDebugCode())
        	cfw.setDebugComment("Try();");
        startTryBlock = cfw.acquireLabel();
        endCatchBlock = cfw.acquireLabel();
        cfw.markLabel(startTryBlock);
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
        if (cfw == null) return;
        if (cfw.isDebugCode())
        	cfw.setDebugComment("Catch(" + exceptionType + ", " + name  + ");");
        endTryCatchBlock();

        // catch (Exception ex)
        int catchBlock = cfw.acquireLabel();
        String exceptionName = exceptionType.getName();
        cfw.addExceptionHandler(startTryBlock, endTryBlock, catchBlock, exceptionName);
        cfw.markLabel(catchBlock);
        cfw.adjustStackTop(1); // exception pointer pushed onto stack.
        maker.markLineNumber(); // possibly add a new line number entry.

        maker.Declare(name, exceptionType, 0);
        maker.Eval(maker.Set(name, exceptionType.getValue()));
    }

    void endTryCatchBlock2()
    {
        if (endTryBlock == 0)
        {
            endTryBlock = cfw.acquireLabel();
            cfw.markLabel(endTryBlock);
        }
        finalyExceptionSlot = maker.storeAnonymousValue(ClassMakerFactory.OBJECT_TYPE);
        // initialise slot
        // Jump over remaining catch and finally blocks.
        cfw.add(ByteCode.GOTO, endCatchBlock);
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
        if (cfw == null) return;
        if (cfw.isDebugCode())
        	cfw.setDebugComment("Finally();");
        endTryCatchBlock();

        // Start finaly block
        int catchBlockAll = cfw.acquireLabel();
        cfw.addExceptionHandler(startTryBlock, catchBlockAll, catchBlockAll, null);
        cfw.markLabel(catchBlockAll);
        // An exception pointer has been pushed onto the stack.
        cfw.adjustStackTop(1);
        finallySubroutine = cfw.acquireLabel();
        maker.markLineNumber(); // possibly add a new line number entry.

        // Store the exception pointer in an anonomous local variable.
        if (cfw.isDebugCode())
        	cfw.setDebugComment("Store reference to exception");
        int finalyExceptionAddress = maker.storeAnonymousValue(ClassMakerFactory.OBJECT_TYPE);

        // Jump to the finally subroutine
        callFinallySubroutine();

        // Rethrow the exception.
        if (cfw.isDebugCode())
        	cfw.setDebugComment("Load reference to exception");
        maker.loadAnonymousValue(finalyExceptionAddress);
        if (cfw.isDebugCode())
        	cfw.setDebugComment("Rethrow exception");
        cfw.add(ByteCode.ATHROW);

        // Finaly subroutine
        if (cfw.isDebugCode())
        	cfw.setDebugComment("finally subroutine");
        cfw.markLabel(finallySubroutine);

        // Store return address in an annonomous local variable.
        finallyReturnSlot = maker.storeAnonymousValue(ClassMakerFactory.OBJECT_TYPE);
    }

    /**
     * Generates the bytecode to end a <code>Try Catch Finally</code> block.
     * The <code>Try</code> block and all preceeding <code>Catch</code> blocks jump to here.
     * Completes from the finally subroutine and then calls it.
     */
    public void EndTry()
    {
        if (cfw != null)
        {
            if (cfw.isDebugCode()) 
            	cfw.setDebugComment("EndTry();");
            if (finallyReturnSlot != 0)
            {
                MakerField local = maker.localTable.get(finallyReturnSlot);
                cfw.add(ByteCode.RET, local.getSlot());
            }
            cfw.markLabel(endCatchBlock);
            callFinallySubroutine();
        }
        dispose();
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
        if (endTryBlock == 0)
        {
            endTryBlock = cfw.acquireLabel();
            cfw.markLabel(endTryBlock);
        }
        // Jump over remaining catch and finally blocks.
        cfw.add(ByteCode.GOTO, endCatchBlock);
    }

    /**
     * Jump to the finally subroutine.
     */
    void callFinallySubroutine()
    {
        if (finallySubroutine != 0)
        {
        	if (cfw.isDebugCode())
        	    cfw.setDebugComment("jump to finally subroutine");
            cfw.add(ByteCode.JSR, finallySubroutine);
        }
    }
    
    protected int getStatementEnd()
    {
    	return endCatchBlock;
    }

}