package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Type;

//################# Switch Statement ########################
/**
 * Represents a <code>Switch</code> statement.
 * Manages the case keys and generates the bytecode for the switch statement.
 */
class SwitchStatement extends Statement
{
    protected SwitchStatement(ClassMaker maker) {
        super(maker);
    }

    int beginSwitch = 0;
    int endSwitch = 0;
    int defaultSwitch = 0;
    int[] cases = new int[16];
    int caseSize = 0;
    String label = null;
    short startLineNumber;

    /**
     * Begins a <code>Switch</code> statement.
     * </br>
     * The type of the selector must be int, char, short or byte.
     * The bytecode for the switch is added in <code>EndSwitch</code>,
     * after the bytecode for each case clause has been added,
     * so we jump to <code>EndSwitch</code>.
     * @param switchType the type of the selector for the switch
     */
    public void Switch(Type switchType)
    {
        if (isFirstPass()) {
            return;
        }
        checkSwitchType(switchType);
        maker.markLineNumber(); // possibly add a new line number entry.
        startLineNumber = (short)maker.getSourceLine().getLineNumber();
        beginSwitch = acquireLabel();
        endSwitch = acquireLabel();
        if (maker.isDebugCode()) {
            maker.setDebugComment("Switch("+switchType+");");
        }
        jumpTo(beginSwitch);
    }

    /**
     * Adds a <code>Case</code> clause of a <code>Switch</code> statement.
     * </br>
     * The switch statement will jump here if the switch value matches the given key.
     * Bytecode is generated to mark the jump offset and insert it and the key
     * into the <code>cases</code> table.
     * @param caseKey the selector which will cause the switch statement to jump to this case
     */
    public void Case(int caseKey)
    {
        if (isFirstPass()) {
            return;
        }
        if (maker.isDebugCode()) {
            maker.setDebugComment("Case("+caseKey+");");
        }
        maker.markLineNumber(); // possibly add a new line number entry.
        int caseLabel = acquireLabel();
        markLabel(caseLabel);

        insertCaseKey(caseKey, caseLabel);
    }

    /**
     * Adds a <code>Default</code> clause of a <code>Switch</code> statement.
     * </br>
     * The switch statement will jump here if none of the other cases apply.
     *
     * Bytecode is generated to mark the jump offset.
     */
    public void Default()
    {
        if (isFirstPass()) {
            return;
        }
        if (maker.isDebugCode()) {
            maker.setDebugComment("Default();");
        }
        if (defaultSwitch != 0) {
            throw maker.createException("ClassMaker.MoreThanOneDefaultInSwitch");
        }
        maker.markLineNumber(); // possibly add a new line number entry.

        defaultSwitch = acquireLabel();
        markLabel(defaultSwitch);
    }

    /**
     * Ends a <code>Switch</code> statement.
     * </br>
     * The switch statement jumps here before being redirected to the appropriate
     * code block.
     * </br>
     * Byte code is generated for either a Table Switch or a Lookup Switch
     * depending upon whether the case keys are contiguous. Control
     * will jump to the end of this code if <code>Break</code> is called.
     */
    public void EndSwitch()
    {
        if (isFirstPass()) {
            return;
        }
        if (caseSize == 0) {
            throw maker.createException("ClassMaker.NoCaseClauseInSwitch");
        }
        if (maker.isDebugCode()) { 
            maker.setDebugComment("EndSwitch();");
        }
        if (defaultSwitch == 0)
        {   // Ensure there is a default option that does nothing.
            maker.Default();
            maker.Break();
        }

        getGen().markLineNumber(startLineNumber);
        markLabel(beginSwitch);
        // Use a Table Switch if the keys are contiguous; otherwise use a Lookup Switch.
        if (isContiguous()) {
            createTableSwitch();
        } else {
            createLookupSwitch();
        }
        markLabel(endSwitch);
        //dispose();
    }
    
    protected int getStatementEnd()
    {
    	return endSwitch;
    }

    /** Doubles the size of the cases array. */
    protected void expand()
    {
        int[] tmp = new int[cases.length * 2];
        System.arraycopy(cases, 0, tmp, 0, cases.length);
        cases = tmp;
    }

    /**
     * Checks that the switch type is int, char, short or byte.
     * @param switchType the type of the selector for the switch
     */
    protected void checkSwitchType(Type switchType)
    {
        if (!ClassMaker.isIntegerType(switchType)) {
            throw maker.createException("ClassMaker.SwitchTypeMustBeNumberNot_1", switchType.getName());
        }
    }

    /** Returns <code>true</code> if the case keys form a contigous sequence. */
    protected boolean isContiguous()
    {
        for (int i = 2; i < caseSize; i += 2) {
            if (cases[i - 2] + 1 != cases[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Inserts a case key into the sequence for this <code>Switch</code>.
     * @param key the case key to be inserted
     * @param label the jump offset corresponding to the given key
     */
    protected void insertCaseKey(int key, int label)
    {
        // Expand array if required.
        if (caseSize + 2 > cases.length) {
            expand();
        }
        // insert key & label into correct place in array.
        int i;
        for (i = caseSize; i > 0; i -= 2) {
            if (cases[i - 2] > key) {
                cases[i] = cases[i - 2];
                cases[i + 1] = cases[i - 1];
                continue;
            } else if (cases[i - 2] == key) {
                throw maker.createException("ClassMaker.DuplicateCaseKey_1", Integer.toString(key));
            }
            break;
        }
        cases[i] = key;
        cases[i + 1] = label;
        caseSize += 2;
    }

    /** Generates the bytecode for a <code>Switch<code> statement with contiguous keys. */
    protected void createTableSwitch()
    {
       getGen().createTableSwitch(cases, caseSize, defaultSwitch);
    }

    /** Generates the bytecode for a <code>Switch<code> statement with <b>non-</b>contiguous keys. */
    protected void createLookupSwitch()
    {
        getGen().createLookupSwitch(cases, caseSize, defaultSwitch);
    }

    /**
     * Jumps to <code>EndSwitch</code> when <code>Break</code> is called.
     * </br>
     * The <code>label</code> must also match this statement if it is provided;
     * otherwise, passess the jump request down the statement stack.
     * @param jumpTarget <code>ClassMaker.BREAK</code>, <code>ClassMaker.CONTINUE</code>, <code>ClassMaker.RETURN</code> or <code>null</code>.
     * @param label the name of the statement to jump to or <code>null</code>
     * @return the target <code>Statement</code> or <code>null</code> if not found.
     */
    protected Statement jumpToTarget(String jumpTarget, String label)
    {
        if (ClassMaker.BREAK.equals(jumpTarget) && (label == null || label.equals(this.label)))
        {
            jumpTo(endSwitch);
            return this;
        }
        return super.jumpToTarget(jumpTarget, label);
    }
}