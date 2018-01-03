package au.com.illyrian.classmaker.util;

import java.util.List;
import java.util.Vector;

import au.com.illyrian.classmaker.ClassGenerator;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.Type;

public class LocalFieldList {

    private final ClassGenerator gen;

    /** A list of local variables in the class being generated. */
    private Vector<MakerField> localTable = new Vector<MakerField>();
    
    private short maxLocalSlots = 0; 
    
    public LocalFieldList(ClassGenerator generator) {
        gen = generator;
    }
    
    public ClassGenerator getGen() {
        return gen;
    }
    
    public MakerField [] getMakerFields() {
        return localTable.toArray(ClassMakerFactory.FIELD_ARRAY);
    }

    public int incLocalSlots(int incLocalSlots) {
        int localSlot = maxLocalSlots;
        maxLocalSlots += incLocalSlots;
        return localSlot;
    }

    public short getMaxLocalSlots() {
        return maxLocalSlots;
    }

    /**
     * Adds a formal parameter or local variable to the method.
     * 
     * @param name name of the local variable
     * @param type type of the local variable
     * @param modifiers access modifiers for the variable
     * @param scopeLevel the level of nesting within the method
     * @return index into <code>localTable</code>
     */
    public int addLocal(String name, Type type, int modifiers, int scopeLevel) {
        MakerField field = new MakerField(name, type, modifiers);
        field.setSlot(maxLocalSlots);
        field.setScopeLevel(scopeLevel);
        // Adjust the number of slots used.
        maxLocalSlots += type.getSlotSize();
        if (getGen() != null) {
            field.setStartPC(getGen().getCurrentCodeOffset());
        }
        int index = localTable.size();
        localTable.add(field);
        return index;
    }

    /**
     * Find a local variable by index.
     * 
     * @param index
     *            an index into <code>localTable</code>
     * @return the indexed local field
     */
    public MakerField findLocalField(int index) {
        return localTable.get(index);
    }

    /**
     * Finds a local variable in the method.
     * 
     * @param name
     *            the name of the variable
     * @return a <code>Field</code> that describes the variable
     */
    public MakerField findLocalField(String name) {
        for (int i = localTable.size() - 1; i >= 0; i--) {
            MakerField local = localTable.get(i);
            if (!local.isInScope())
                continue; // Skip locals that are out of scope
            if (name.equals(local.getName())) {
                return local;
            }
        }
        return null;
    }
    
    /**
     * Limits the visibility of all local variables that were added at the given
     * scope level. <br/>
     * Records the program counter as the variable goes out of scope and marks
     * the variable as out of scope.
     * Scope entries are added to the method as it is completed.
     * 
     * @param scope
     *            the level of nesting of the current scoped code block
     */
    public void exitScope(int scope) {
        // Local variable descriptors are used by the debugger.
        for (int i = localTable.size() - 1; i >= 0; i--) {
            MakerField local = localTable.elementAt(i);
            if (!local.isInScope()) {
                continue; // Skip out of scope variables
            }
            if (local.getScopeLevel() < scope) {
                break; // Stop when field is in wider scope
            }
            if (getGen() != null) { // FIXME remove when gen is set.
                local.setEndPC(gen.getCurrentCodeOffset());
            }
            local.setInScope(false);
        }
    }

    /**
     * Creates a list of formal parameters for the method currently being
     * generated.
     * 
     * @return an array of formal parameter <code>Type</code>s
     */
    public Type[] createFormalParameters() {
        int size = localTable.size();
        Type[] params = new Type[size];
        for (int index = 0; index < size; index++) {
            params[index] = findLocalField(index).getType();
        }
        return params;
    }

    public void clear() {
        localTable.clear();
        maxLocalSlots = 0;
    }
}
