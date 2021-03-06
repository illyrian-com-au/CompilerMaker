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
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.util.MakerUtil;

/**
 * Retains information about methods created in a ClassMaker instance.
 *  
 * @author dstrong
 */
public class MakerMethod {
    private final ClassType classType;
    private final String name;
    private final Type   returnType ;
    private final short  modifiers;
    private Type [] formalParamTypes = null;
    private MakerField [] formalParamFields = null;
    private String signature;
    private boolean hasBody = true;

    /**
     * Creates a MakerMethod that holds information about a generated method.
     * <br/>
     * The information may be added by the ClassMaker instance as the method is
     * generated or the information may be derived from the equivalent reflection class.
     * @param classType the <code>type</code> of the class containing the method
     * @param methodName the name of the method
     * @param returnType the return <code>Type</code>
     * @param methodModifiers a bit-set of modifiers for the method
     */
    public MakerMethod(ClassType classType, String methodName, Type returnType, short methodModifiers)
    {
        this.classType = classType;
        this.name       = methodName;
        this.modifiers  = methodModifiers;
        this.returnType = returnType;
    }
    
    /** The name of the method. */
    public String getName()
    {
    	return name;
    }

    /** The return <code>Type</code> of the method. */
    public Type getReturnType()
    {
    	return returnType;
    }

    /**
     * The modifiers for the method.
     * </br>
     * The modifiers are checked by <code>ClassMaker.checkMethodModifiers</code> prior to being set.
     * The following are valid modifiers for a member method.
     * <ul>
     * <li><code>ClassMaker.ACC_PUBLIC</code></li>
     * <li><code>ClassMaker.ACC_PROTECTED</code></li>
     * <li><code>ClassMaker.ACC_PRIVATE</code></li>
     * <li><code>ClassMaker.ACC_STATIC</code></li>
     * <li><code>ClassMaker.ACC_FINAL</code></li>
     * <li><code>ClassMaker.ACC_ABSTRACT</code></li>
     * <li><code>ClassMaker.ACC_SYNCHRONIZED</code></li>
     * <li><code>ClassMaker.ACC_NATIVE</code></li> 
     * <li><code>ClassMaker.ACC_STRICTFP</code></li>
     * <li>zero</li>
     * </ul>
     * @return A bit-set of modifiers for the method
     */
    public short getModifiers()
    {
    	return modifiers;
    }

    /** The <code>ClassType</code> of the class containing the method. */
    public ClassType getClassType()
    {
        return classType;
    }

    /**
     * Whether the method has a body.
     * </br>
     * Forward declared methods have no body. This may be because the method is declared again 
     * with a body later in the compilation unit or because the method is abstract.
     * @return true if the method has a body; otherwise false  
     */
    public boolean hasBody()
    {
    	return hasBody;
    }
    
    /**
     * Sets whether the method has a body.
     * </br>
     * Forward declared methods have no body. This may be because the method is declared again 
     * with a body later or because the method is abstract.
     * @param hasBody true if the method has a body; otherwise false  
     */
    public void setHasBody(boolean hasBody)
    {
    	this.hasBody = hasBody;
    }

    /**
     * The formal parameters for the method.
     * @return an array of <code>Type</code>s representing the formal parameters
     */
    public Type[] getFormalTypes()
    {
        return formalParamTypes;
    }

    /**
     * Sets the formal parameter types for the method.
     * <br/>
     * The formal parameters must be set before a signature is generated for the method.
     * @param params an array of <code>Type</code>s representing the formal parameters
     * @throws IllegalStateException if a signature has already been generated
     */
    public void setFormalTypes(Type[] params)
    {
        if (signature != null) {
            throw new IllegalStateException("Cannot set formal parameter types for this method as <code>Begin()</code> has been called.");
        }
        formalParamTypes = params;
    }
    
    /**
     * The formal parameter fields for the method.
     * Formal parameter fields include the declared name for each field.
     * @return an array of <code>Type</code>s representing the formal parameters
     */
    public MakerField [] getFormalFields() {
        return formalParamFields;
    }

    /**
     * Sets the formal parameter fields for the method.
     * <br/>
     * The formal parameter fields must be set before a signature is generated for the method.
     * @param params an array of <code>Type</code>s representing the formal parameters
     * @throws IllegalStateException if a signature has already been generated
     */
    public void setFormalFields(MakerField [] fields) {
        if (signature != null) {
            throw new IllegalStateException("Cannot set formal parameter fields for this method as <code>Begin()</code> has been called.");
        }
        formalParamFields = fields;
    }
    
    /**
     * The signature of the method.
     * Derives the signature from the formal parameters and the return type.
     * @return the signature of the method
     */
    public String getSignature()
    {
        if (signature == null) {
            signature = createSignature(formalParamTypes, returnType);
        }
        return signature;
    }
    
    /**
     * Creates a signature from the formal parameter types.
     *
     * @param formalParameters the formal parameters declared by the method
     * @param returnType return type of the method
     * @return the method signature
     */
    String createSignature(Type[] formalParameters, Type returnType)
    {
        StringBuffer buf = new StringBuffer();

        if (formalParameters == null || formalParameters.length == 0)
            buf.append("()");
        else
        {
            buf.append('(');
            for (int i = 0; i < formalParameters.length; i++)
            {
                String param = formalParameters[i].getSignature();
                buf.append(param);
            }
            buf.append(')');
        }
        buf.append(returnType == null ? "V" : returnType.getSignature());

        return buf.toString();
    }

    /** 
     * Checks whether the method modifiers imply this method is static.
     * <br/>
     * A class member is static if the method modifiers include <code>ClassMaker.ACC_STATIC</code>.
     * @return true if the method is static; otherwise false 
     */
    public boolean isStatic()
    {
        return (modifiers & ClassMakerConstants.ACC_STATIC) > 0;
    }

    /** 
     * Checks whether the method modifiers imply this method is abstract.
     * <br/>
     * A class member is static if the method modifiers include <code>ClassMaker.ACC_ABSTRACT</code>.
     * @return true if the method is abstract; otherwise false 
     */
    public boolean isAbstract()
    {
        return (modifiers & ClassMakerConstants.ACC_ABSTRACT) > 0;
    }

    /** 
     * Checks whether the method modifiers imply this method has public access.
     * <br/>
     * A class member has public access if the method modifiers include <code>ClassMaker.ACC_PUBLIC</code>.
     * @return true if the method has public access; otherwise false 
     */
    public boolean isPublic()
    {
        return (modifiers & ClassMakerConstants.ACC_PUBLIC) > 0;
    }

    /** 
     * Checks whether the method modifiers imply this method has protected access.
     * <br/>
     * A class member has protected access if the method modifiers include <code>ClassMaker.ACC_PROTECTED</code>.
     * @return true if the method has protected access; otherwise false 
     */
    public boolean isProtected()
    {
        return (modifiers & ClassMakerConstants.ACC_PROTECTED) > 0;
    }

    /** 
     * Checks whether the method modifiers imply this method has private access.
     * <br/>
     * A class member has package access if the method modifiers include <code>ClassMaker.ACC_PRIVATE</code>.
     * @return true if the method has private access; otherwise false 
     */
    public boolean isPrivate()
    {
        return (modifiers & ClassMakerConstants.ACC_PRIVATE) > 0;
    }

    /** 
     * Checks whether the method modifiers imply this method has package access.
     * <br/>
     * A class member has package access if it does not have any of the following access modifiers:
     * <code>ClassMaker.ACC_PUBLIC</code>, <code>ClassMaker.ACC_PROTECTED</code>
     * or <code>ClassMaker.ACC_PRIVATE</code>.
     * @return true if the method has package access; otherwise false 
     */
    public boolean isPackage()
    {
        return ((ClassMakerConstants.MASK_ACCESS) & modifiers) == 0;
    }

    /**
     * Compares the name and signature of the methods to determine equality.
     * <br/>
     * The other Object must be a MakerMethod, otherwise the method returns false.
     * Two methods are equal if they have the same name and signature.
     * @param other the Object to be compared
     * @return true if the other method is equal; otherwise false
     */
    public boolean equals(Object other)
    {
        if (getSignature() != null && name != null && other != null && other instanceof MakerMethod)
        {
            MakerMethod otherMethod = (MakerMethod)other;
            return name.equals(otherMethod.name) && getSignature().equals(otherMethod.getSignature());
        }
        return super.equals(other);
    }


    /**
     * A hash code value for the method. 
     * <br/>
     * The hash code is derived from the name and signature of the method.
     * @return a hash code for the method
     */
    public int hashCode()
    {
    	if (name != null)
    		return name.hashCode() | (signature != null ? signature.hashCode() : 0);
    	else
    		return super.hashCode();
    }

    /**
     * Creates a descriptor for the method suitable for display. <br/>
     * 
     * @param name the name of the method
     * @param params the formal parameter Types
     * @param returnType the return Type of the method
     * @param modifiers a bit mask of the method modifiers
     * @return a string describing the method
     */
    public static String toMethodString(String name, Type[] params, Type returnType, int modifiers)
    {
        StringBuffer buf = new StringBuffer();
        if (modifiers != 0) {
            buf.append(MakerUtil.toModifierString(modifiers));
        }
        buf.append((returnType != null) ? returnType.getName() : "void");
        buf.append(' ');
        buf.append(name);
        buf.append('(');
        if (params != null)
            for (int i = 0; i < params.length; i++) {
                if (i > 0)
                    buf.append(", ");
                Type type = params[i];
                if (type == null)
                    buf.append("null");
                else
                    buf.append(MakerUtil.toDotName(type.getName()));
            }
        buf.append(')');
        return buf.toString();
    }

    /**
     * Creates a string representing the method without modifiers.
     * This representation is suitable for lookups on the method as the modifiers may change on overridden methods.
     */
    public String toShortString()
    {
        return toMethodString(getName(), getFormalTypes(), getReturnType(), 0);
    }

    /**
     * Creates a string representing the field.
     */
    public String toString()
    {
        return toMethodString(getName(), getFormalTypes(), getReturnType(), getModifiers());
    }
}
