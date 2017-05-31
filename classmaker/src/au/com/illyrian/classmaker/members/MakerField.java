// Copyright (c) 2010, Donald Strong.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.

package au.com.illyrian.classmaker.members;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;

/**
 * Retains information about fields created in a ClassMaker instance.
 *  
 * @author dstrong
 */
public class MakerField
{
    private final ClassType classType;
    private final String name;
    private final Type type;
    private final int    modifiers;
    private int    slot;
    private int    scopeLevel;
    private int    startPC = 0;
    private int    endPC = -1;
    private boolean isInScope = true;

    /**
     * Creates a MakerField that holds information about a local variable or formal parameter.
     * <br/>
     * The information is added by the ClassMaker instance as the variable is declared.
     * <br/>
     * <code>ClassMaker.ACC_FINAL</code> and zero are valid modifiers for variables and parameters.
     * @param name the name of the field
     * @param type the type of the field
     * @param modifiers the modifiers on the variable or parameter
     */
    public MakerField(String name, Type type, int modifiers)
    {
        this.classType = null;
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }
    
    /**
     * Creates a MakerField that holds information about a field in a class.
     * <br/>
     * The information may be added by the ClassMaker instance as the field is
     * generated or the information may be derived from the equivalent reflection class.
     * <br/>
     * The following are valid modifiers for a member field.
     * <ul>
     * <li><code>ClassMaker.ACC_PUBLIC</code></li>
     * <li><code>ClassMaker.ACC_PROTECTED</code></li>
     * <li><code>ClassMaker.ACC_PRIVATE</code></li>
     * <li><code>ClassMaker.ACC_STATIC</code></li>
     * <li><code>ClassMaker.ACC_FINAL</code></li>
     * <li><code>ClassMaker.ACC_TRANSIENT</code></li>
     * <li><code>ClassMaker.ACC_VOLATILE</code></li>
     * <li>zero</li>
     * </ul>
     * @param classType the type of the class containing the field
     * @param name the name of the field
     * @param type the type of the field
     * @param modifiers the modifiers on the field
     */
    public MakerField(ClassType classType, String name, Type type, int modifiers)
    {
        this.classType = classType;
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }
    
    public boolean isLvalue()
    {
        return true;
    }
    
    public boolean isLocal()
    {
        return getScopeLevel() > 0;
    }

    public boolean isStatic()
    {
        return (getModifiers() & ClassMaker.ACC_STATIC) == ClassMaker.ACC_STATIC;
    }

    /** The type of the class containing the field. */
    public ClassType getClassType()
    {
    	return classType;
    }
    
    /** The name of the field or variable. */
    public String getName()
    {
    	return name;
    }

    /** The type of the field or variable. */
    public Type getType()
    {
    	return type;
    }
    
    public MakerField toField()
    {
        return this;
    }

    /** The modifiers for the variable or variable.
     * </br>
     *  See the constructors for valid modifiers.
     * @return A bit-set of modifiers for the variable or field
     */
    public int getModifiers()
    {
    	return modifiers;
    }
    
    /**
     * The slot offset of a formal parameter or local variable.
     * @return the slot offset
     */
    public int getSlot()
    {
    	return slot;
    }

    /**
     * Set the slot offset of a formal parameter or local variable.
     * <br/>
     * The slot is the number of words (4 bytes) the local variable is from the
     * start of the stack frame. This increments by 1 for most variables, but by 2 for
     * wide variables. Wide variables are primitive types long and double.
     * @param slot the slot offset
     */
    public void setSlot(int slot)
    {
    	this.slot = slot;
    }
    
    /** 
     * The scope level of the local variable.
     * <br/>
     * The scope level is the level of nesting of Begin End scope statements.
     * @return the scope level of the local variable 
     */
    public int getScopeLevel()
    {
    	return scopeLevel;
    }
    
    /** 
     * Set the scope level of the local variable.
     * <br/>
     * The scope level is the level of nesting of Begin End scope statements.
     * @param scopeLevel the scope level of the local variable 
     */
    public void setScopeLevel(int scopeLevel)
    {
    	this.scopeLevel = scopeLevel;
    }
    
    /** The start program counter from which this variable is in scope. */
    public int getStartPC()
    {
    	return startPC;
    }
    
    /** Set the start program counter from which this variable is in scope. */
    public void setStartPC(int startPC)
    {
    	this.startPC = startPC;
    }
    
    /** The end program counter to which this variable is in scope. */
    public int getEndPC()
    {
    	return endPC;
    }
    
    /** Set the end program counter to which this variable is in scope. */
    public void setEndPC(int endPC)
    {
    	this.endPC = endPC;
    }
    
    /**
     * Check whether this variable is currently in scope.
     * @return true if the variable is in scope; otherwise false
     */
    public boolean isInScope()
    {
    	return isInScope;
    }
    
    /** Sets whether this variable is currently in scope. */
    public void setInScope(boolean isInScope)
    {
    	this.isInScope = isInScope;
    }
    
    /**
     * Creates a string representing the field.
     * <br/>
     * E.g. MakerField(test, java/lang/String) 
     */
    public String toString()
    {
        return "MakerField(" + name + ", " + type.getName() + ")";
    }
}
