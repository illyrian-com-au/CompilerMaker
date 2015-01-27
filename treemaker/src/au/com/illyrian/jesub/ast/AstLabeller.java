package au.com.illyrian.jesub.ast;

import au.com.illyrian.classmaker.ast.TerminalName;

/**
 * Provides methods to set and get Label names.
 * @author dstrong
 */
public interface AstLabeller
{
    /**
     * Labels a <code>Statement</code>.
     * The statement can then be the target of a <code>Break</code> or <code>Continue</code>.
     * @param label the name of the label
     */
	void setLabel(TerminalName label);

    /**
     * The label for a <code>Statement</code>.
     * @return the name of the label
     */
    TerminalName getLabel();
}

