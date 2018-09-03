package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Value;

public class StatementManager {
    private final ClassMaker maker;
    
    public StatementManager(ClassMaker maker) {
        this.maker = maker;
    }
    //################# Statement Management ###################

    /* The bottom of the Statement stack is always a method body. */
    MethodBodyStatement bottomStatement = null;

    /* Top of the statement stack. Statements are pushed and popped from here. */
    Statement statementStack = null;

    /** Top of the statement stack. */
    public Statement topStatement() {
        return statementStack;
    }

    public Statement pushStatement(Statement statement) {
        statement.setNext(statementStack);
        return statementStack = statement;
    }
    
    public Statement popStatement() {
        Statement statement = statementStack;
        statementStack = statementStack.getNext();
        return statement;
    }
    
    public void setBottomStatement(MethodBodyStatement bottomStatement) {
        this.bottomStatement = bottomStatement;
    }

    public MethodBodyStatement getBottomStatement() {
        return bottomStatement;
    }
 
    /**
     * Creates an <code>MethodBodyStatement</code> and pushes it onto the statement stack.
     * 
     * @return the <code>MethodBodyStatement</code> on top of the stack
     */
    protected MethodBodyStatement createMethodBodyStatement() {
        MethodBodyStatement stmt = new MethodBodyStatement(maker);
        pushStatement(stmt);
        setBottomStatement(stmt);
        return stmt;
    }

    /**
     * Creates an <code>ScopeStatement</code> and pushes it onto the statement stack.
     * 
     * @return the <code>ScopeStatement</code> on top of the stack
     */
    protected ScopeStatement createScopeStatement() {
        ScopeStatement stmt = new ScopeStatement(maker);
        pushStatement(stmt);
        return stmt;
    }

    /**
     * Fetches the <code>ScopeStatement</code> at the top of the statement
     * stack.
     * Casts the statement on the top of the statement stack or throws an
     * <code>IllegalStateException</code> if this is not possible.
     * 
     * @return the <code>ScopeStatement</code> on top of the stack
     */
    protected ScopeStatement topScopeStatement(String msg) {
        if ((topStatement() instanceof ScopeStatement))
            return (ScopeStatement) topStatement();
        throw createException(msg);
    }

    // ##################  If Then Else EndIf  ###################
    /**
     * Creates an <code>IfStatement</code> and pushes it onto the statement stack.
     * 
     * @return the <code>IfStatement</code> on top of the stack
     */
    protected IfStatement createIfStatement() {
        IfStatement stmt = new IfStatement(maker);
        pushStatement(stmt);
        return stmt;
    }
    
    /**
     * Fetches the <code>IfStatement</code> at the top of the statement stack.
     * Casts the statement on the top of the statement stack or throws an
     * <code>IllegalStateException</code> if this is not possible.
     * 
     * @return the <code>IfStatement</code> on top of the stack
     */
    protected IfStatement topIfStatement(String msg) {
        if ((topStatement() instanceof IfStatement))
            return (IfStatement) topStatement();
        throw createException(msg);
    }

    // #######################  Loop Statement ######################
    /**
     * Creates an <code>LoopStatement</code> and pushes it onto the statement stack.
     * 
     * @return the <code>LoopStatement</code> on top of the stack
     */
    protected LoopStatement createLoopStatement() {
        LoopStatement stmt = new LoopStatement(maker);
        pushStatement(stmt);
        return stmt;
    }
    /**
     * Fetches the <code>LoopStatement</code> at the top of the statement stack.
     * Casts the statement on the top of the statement stack or throws an
     * <code>IllegalStateException</code> if this is not possible.
     * 
     * @return the <code>LoopStatement</code> on top of the stack
     */
    protected LoopStatement topLoopStatement(String msg) {
        if ((topStatement() instanceof LoopStatement))
            return (LoopStatement) topStatement();
        throw createException(msg);
    }

    /**
     * Creates an <code>ForStatement</code> and pushes it onto the statement stack.
     * 
     * @return the <code>ForStatement</code> on top of the stack
     */
    protected ForStatement createForStatement() {
        ForStatement stmt = new ForStatement(maker);
        pushStatement(stmt);
        return stmt;
    }

   /**
     * Fetches the <code>ForStatement</code> at the top of the statement stack.
     * Casts the statement on the top of the statement stack or throws an
     * <code>IllegalStateException</code> if this is not possible.
     * 
     * @return the <code>LoopStatement</code> on top of the stack
     */
    protected ForStatement topForStatement(String msg) {
        if ((topStatement() instanceof ForStatement))
            return (ForStatement) topStatement();
        throw createException(msg);
    }
    
    /**
     * Creates an <code>SwitchStatement</code> and pushes it onto the statement stack.
     * 
     * @return the <code>SwitchStatement</code> on top of the stack
     */
    protected SwitchStatement createSwitchStatement() {
        SwitchStatement stmt = new SwitchStatement(maker);
        pushStatement(stmt);
        return stmt;
    }

    /**
     * Fetches the <code>SwitchStatement</code> at the top of the statement
     * stack.
     * </br>
     * Casts the statement on the top of the statement stack or throws an
     * <code>IllegalStateException</code> if this is not possible.
     * 
     * @return the <code>SwitchStatement</code> on top of the stack
     */
    protected SwitchStatement topSwitchStatement(String msg) {
        if ((topStatement() instanceof SwitchStatement)) {
            return (SwitchStatement) topStatement();
        } else {
            throw createException(msg);
        }
    }

    /**
     * Creates an <code>TryCatchFinally</code> and pushes it onto the statement stack.
     * 
     * @return the <code>TryCatchFinally</code> on top of the stack
     */
    protected TryCatchFinally createTryCatchFinally() {
        TryCatchFinally stmt = new TryCatchFinally(maker);
        pushStatement(stmt);
        return stmt;
    }

    /**
     * Fetches the <code>TryCatchFinally</code> statement at the top of the
     * statement stack.
     * </br>
     * Casts the statement on the top of the statement stack or throws an
     * exception if this is not possible.
     * 
     * @return the <code>TryCatchFinally</code> on top of the stack
     */
    protected TryCatchFinally topTryCatchFinally(String msg) {
        if ((topStatement() instanceof TryCatchFinally)) {
            return (TryCatchFinally) topStatement();
        } else {
            throw createException(msg);
        }
    }

    public void dispose(Statement statement) {
        Statement discarded = popStatement();
        if (discarded != statement) {
            // This is caused by a bug in the code generator.
            throw new IllegalStateException("Can only dispose of top most Statement.");
        }
    }
    
    public void dispose(MethodBodyStatement statement) {
        Statement discarded = popStatement();
        if (discarded != statement) {
            throw new IllegalStateException("Can only dispose of top most Statement.");
        }
        if (statementStack != null) {
            // This is caused by a bug in the code generator.
            throw new IllegalStateException("MethodBodyStatement can only be at the bottom of the statement stack.");
        }
        setBottomStatement(null);
    }
    
    ClassMakerException createException(String key, String ... values) {
        return maker.createException(key, values);
    }
}
