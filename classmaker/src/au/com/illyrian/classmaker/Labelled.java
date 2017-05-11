package au.com.illyrian.classmaker;

/**
 * Provides methods to set and get Label names.
 * @author dstrong
 */
public interface Labelled
{
    /**
     * Labels a <code>Statement</code>.
     * The statement can then be the target of a <code>Break</code> or <code>Continue</code>.
     * @param label the name of the label
     */
    void setLabel(String label);

    /**
     * The label for a <code>Statement</code>.
     * @return the name of the label
     */
    String getLabel();
}
