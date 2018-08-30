package au.com.illyrian.classmaker;


/**
 * <code>Statement</code> instances form a stack of nested statements in a method.
 * This is a base class for specific statement types like <code>LoopStatement</code>.
 * @author dstrong
 */
abstract class Statement implements Labelled
{
    protected final ClassMaker maker;
    
    /* Next Statement down the statement stack. */
    private Statement next = null;

    /* Label for this statement. */
    private String label = null;

    /* The jump address for this statement. This is typically the end of the statement. */
    protected int labelTarget;

    /** The default constructor pushes this instance onto the statement stack. */
    public Statement(ClassMaker maker) {
        this.maker = maker;
        next = maker.statementStack;
        maker.statementStack = this;
    }

    public ClassMakerConstants getMaker() {
        return maker;
    }
    
    public boolean isFirstPass() {
        return maker.isFirstPass();
    }
    
    public ClassGenerator getGen() {
        return maker.getGen();
    }

    /** Is there another <code>Statement</code> nested around this one? */
    boolean hasNext() {
        return next != null;
    }

    /** Gets the <code>Statement</code> nested around this one. */
    Statement getNext() {
        return next;
    }
    
    void setNext(Statement next) {
        this.next = next;
    }

    public int getScopeLevel() {
        if (next == null) {
            throw new IllegalStateException("Statement Stack should start with class MethodBodyStatement");
        }
        return next.getScopeLevel();
    }
    
    protected abstract int getStatementEnd();

    /**
     * Jumps to a <code>Statement</code>.
     * This default implementation passes the jump request down the statement stack.
     * The label identifies which <code>Statement</code> to jump to. If the label is <code>null</code>
     * the first appropriate <code>Statement</code> will be the target.
     * The <code>jumpType</code> determines where in the statement, execution will jump to.
     * This will typically be the start of the statement for continue, the end of the
     * statement for break or the end of the method for return.
     * @param jumpType <code>ClassMaker.BREAK</code>, <code>ClassMaker.CONTINUE</code>, 
     * <code>ClassMaker.RETURN</code> or <code>null</code>.
     * @param label the name of the statement to jump to
     * @return the target <code>Statement</code> or <code>null</code> if not found.
     */
    protected Statement jumpToTarget(String jumpType, String label)
    {
        if (ClassMaker.BREAK.equals(jumpType) && getLabel() != null && getLabel().equals(label)) {
            // Break jumps to the end of the loop
            jumpTo(getStatementEnd());
            return this;
        } else if (hasNext()) {
            return getNext().jumpToTarget(jumpType, label);
        } else {
            return null;
        }
    }
    
    /**
     * Create a location holder where a jump label can be stored.
     * This allows forward jumps as the jump label can be used before the target location 
     * is reached in the byte code stream.
     * @return an offset to the jump label
     */
    protected int acquireLabel() {
        return getGen().acquireLabel();
    }
    
    /**
     * Mark the location of a jump label.
     * This method is called at the target point in the byte code stream.
     * The PC counter is stored in the location holder.
     * @param jumpLabel an offset to the jump label
     */
    protected void markLabel(int jumpLabel) {
        getGen().markLabel(jumpLabel);
    }
    
    /** 
     * Jump to the given label if the value on top of the stack is zero.
     * @param jumpLabel an offset to the jump label
     */
    protected void jumpIfEqualZero(int jumpLabel) {
        getGen().jumpIfEqualZero(jumpLabel);
    }
    
    /**
     * Unconditional jump to the given label.
     * @param jumpLabel
     */
    protected void jumpTo(int jumpLabel) {
        getGen().jumpTo(jumpLabel);
    }

    /* Implements Labelled. */
    public void setLabel(String label) {
        this.label = label;
    }

    /* Implements Labelled. */
    public String getLabel() {
        return label;
    }

    /** Pops the Statement off the stack. */
    protected void dispose() {
        if (maker.statementStack != this) { // Should not get here.
            throw new IllegalStateException("Can only dispose of top most Statement.");
        }
        maker.statementStack = getNext();
    }
}