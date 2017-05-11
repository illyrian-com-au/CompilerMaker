package au.com.illyrian.classmaker;

import org.mozilla.classfile.ByteCode;
import org.mozilla.classfile.ClassFileWriter;

/**
 * <code>Statement</code> instances form a stack of nested statements in a method.
 * This is a base class for specific statement types like <code>LoopStatement</code>.
 * @author dstrong
 */
abstract class Statement implements Labelled
{
    protected final ClassMaker maker;
    
    protected final ClassFileWriter cfw;

    /* Next Statement down the statement stack. */
    private Statement next = null;

    /* Label for this statement. */
    private String label = null;

    /* The jump address for this statement. This is typically the end of the statement. */
    protected int labelTarget;

    /** The default constructor pushes this instance onto the statement stack. */
    public Statement(ClassMaker maker)
    {
        this.maker = maker;
        this.cfw = maker.getClassFileWriter();
        next = maker.statementStack;
        maker.statementStack = this;
    }

    public ClassMaker getMaker()
    {
        return maker;
    }
    
    public ClassFileWriter getClassFileWriter() {
        return maker.getClassFileWriter();
    }

    /** Is there another <code>Statement</code> nested around this one? */
    boolean hasNext()
    {
        return next != null;
    }

    /** Gets the <code>Statement</code> nested around this one. */
    Statement getNext()
    {
        return next;
    }

    public int getScopeLevel()
    {
        if (next == null)
            throw new IllegalStateException("Statement Stack should start with class MethodBodyStatement");
        return next.getScopeLevel();
    }
    
    protected abstract int getStatementEnd();

    /**
     * Jumps to a <code>Statement</code>.
     * This default implementation passess the jump request down the statement stack.
     * The label identifies which <code>Statement<code> to jump to. If the label is <code>null<code>
     * the first appropriate <code>Statement<code> will be the target.
     * The <code>jumpType</code> determines where in the statement execution will jump to.
     * This will typically be the start of the statement for continue, the end of the
     * statement for break or the end of the method for return.
     * @param jumpType <code>ClassMaker.BREAK</code>, <code>ClassMaker.CONTINUE</code>, <code>ClassMaker.RETURN</code> or <code>null</code>.
     * @param label the name of the statement to jump to
     * @return the target <code>Statement</code> or <code>null</code> if not found.
     */
    protected Statement jumpToTarget(String jumpType, String label)
    {
        if (ClassMaker.BREAK.equals(jumpType) && getLabel() != null && getLabel().equals(label))
        {   // Break jumps to the end of the loop
            cfw.add(ByteCode.GOTO, getStatementEnd());
            return this;
        }
        else if (hasNext())
            return getNext().jumpToTarget(jumpType, label);
        else
            return null;
    }

    /* Implements Labelled. */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /* Implements Labelled. */
    public String getLabel()
    {
        return label;
    }

    /** Pops the Statement off the stack. */
    protected void dispose()
    {
        if (maker.statementStack != this) // Should not get here.
            throw new IllegalStateException("Can only dispose of top most Statement.");
        maker.statementStack = getNext();
    }
}