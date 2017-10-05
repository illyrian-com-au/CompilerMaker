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

package au.com.illyrian.classmaker.types;

import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.util.MakerUtil;

/**
 * The class for representing all java class types.
 * <br/>
 * Contains information about the constructors, methods and fields in the class
 * and the interfaces that the class implements.
 */
public class ClassType extends Type {
    private ClassMakerFactory factory = null;
    private String            packageName = null;
    protected ClassType       extendsType = null;
    protected ClassType   []  declaredInterfaces = null;
    protected MakerMethod []  declaredConstructors = null;
    protected MakerMethod []  declaredMethods = null;
    protected MakerField  []  declaredFields = null;
    private int               modifiers = 0;

    public ClassType(Class javaClass)
    {
        this(MakerUtil.classToName(javaClass), null);
        setJavaClass(javaClass);
        setModifiers(javaClass.getModifiers());
    }
    
    /**
     * Constructor for a <code>ClassType</code>.
     * @param className the name of the class
     * @param extendsType the <code>ClassType</code> of the base class
     */
    public ClassType(String className)
    {
        this(className, toSignature(className), null);
    }
    
    /**
     * Constructor for a <code>ClassType</code>.
     * @param className the name of the class
     * @param extendsType the <code>ClassType</code> of the base class
     */
    public ClassType(String className, ClassType extendsType)
    {
        this(className, toSignature(className), extendsType);
    }
    
    /**
     * Constructor for a <code>ClassType</code>.
     * @param className the name of the class
     * @param signature the signature of the class
     * @param extendsType the <code>ClassType</code> of the base class
     */
    protected ClassType(String className, String signature, ClassType extendsType)
    {
        super(className, signature);
        this.extendsType  = extendsType;
        extractPackageName(className);
    }

    public ClassMakerFactory getFactory() {
        if (factory == null) {
            throw new NullPointerException("factory not set");
        }
        return factory;
    }
    
    public void setFactory(ClassMakerFactory factory) {
        if (this.factory == null) {
            this.factory = factory;
        }
    }
    
    protected static String toSignature(String className)
    {
        return "L" + MakerUtil.toSlashName(className) + ";";
    }

    /**
     * Convert this <code>Type</code> to a <code>ClassType</code>.
     * @return a <code>ClassType</code>
     */
    public ClassType toClass()
    {
    	return this;
    }

    /**
     * Finds a field declared within this class.
     * @param name the name of the field
     * @return a representation of the field
     */
    public MakerField findDeclaredField(String name)
    {
        MakerField [] fields = getDeclaredFields();
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                if (name.equals(fields[i].getName())) {
                    return fields[i];
                }
            }
        }
        return null;
    }

    /**
     * Finds a field within this class or a parent class.
     * @param name the name of the field
     * @return a representation of the field
     */
    public MakerField findField(String name)
    {
        MakerField field = findDeclaredField(name);
        if (field == null && getExtendsType() != null) {
            field = getExtendsType().findField(name);
        }
        return field;
    }

    /**
     * A bitset of access modifiers for the class. 
     * @return a bitset of access modifiers
     */
    public int getModifiers()
    {
        return modifiers;
    }

    /**
     * Sets the access modifiers for the class.
     * @param modifiers a bitset of access modifiers
     */
    public void setModifiers(int modifiers)
    {
        this.modifiers = modifiers;
    }

    /** The list of interfaces implemented by this class. */
    public ClassType [] getDeclaredInterfaces()
    {
        if (declaredInterfaces == null) {
            return populateDeclaredInterfaces();
        }
        return declaredInterfaces;
    }
    
    ClassType [] populateDeclaredInterfaces() {
        if (javaClass != null) {
            ClassType [] interfaces = getFactory().getDeclaredInterfaces(this);
            setDeclaredInterfaces(interfaces);
        }
        return declaredInterfaces;
    }
    
    /** Sets the list of interfaces implemented by this class. */
    public void setDeclaredInterfaces(ClassType [] interfaces)
    {
        this.declaredInterfaces = interfaces;
    }
    
    /**
     * An array of constructor descriptors for this class type.
     * Only constructors in the most specific class are returned.
     * 
     * @param classType the ClassType that holds information about the class
     */
    public MakerMethod [] getDeclaredConstructors()
    {
        if (declaredConstructors == null) {
            return populateDeclaredConstructors();
        }
    	return declaredConstructors;
    }
    
    /** Sets the list of constructors for this class. */
    public void setDeclaredConstructors(MakerMethod [] constructors)
    {
    	this.declaredConstructors = constructors;
    }
    
    protected MakerMethod [] populateDeclaredConstructors() {
        MakerMethod [] methods = getFactory().getDeclaredConstructors(this);
        setDeclaredConstructors(methods);
        return methods;
    }
    
    /** The list of methods implemented by this class. */
    public MakerMethod [] getDeclaredMethods()
    {
        if (declaredMethods == null) {
            return populateDeclaredMethods();
        }
    	return declaredMethods;
    }
    
    protected MakerMethod [] populateDeclaredMethods() {
        MakerMethod [] methods = getFactory().getDeclaredMethods(this);
        setDeclaredMethods(methods);
        return methods;
    }

    /** Sets the list of methods implemented by this class. */
    public void setDeclaredMethods(MakerMethod [] methods)
    {
    	this.declaredMethods = methods;
    }
    
    /** The list of member fields in this class. */
    public MakerField [] getDeclaredFields()
    {
        if (declaredFields == null) {
            return populateDeclaredFields();
        }
    	return declaredFields;
    }
    
    MakerField [] populateDeclaredFields() {
        MakerField [] fields = getFactory().getDeclaredFields(this);
        setDeclaredFields(fields);
        return fields;
    }

    /** Sets the list of member fields in this class. */
    public void setDeclaredFields(MakerField [] fields)
    {
    	this.declaredFields = fields;
    }
    
    /**
     * Determines whether the class that this <code>ClassType</code> represents is an interface.
     * @return true if the class is an interface; otherwise false
     */
    public boolean isInterface()
    {
        return (getModifiers() & ClassMakerConstants.ACC_INTERFACE) == ClassMakerConstants.ACC_INTERFACE;
    }

     /**
     * The base class for this class.
     * @return the <code>ClassType</code> for the base class
     */
    public ClassType getExtendsType()
    {
        if (extendsType == null) {
            return populateExtendsType();
        }
        return extendsType;
    }
    
    public void setExtendsType(ClassType baseType) {
        extendsType = baseType;
    }
    
    public ClassType populateExtendsType() {
        if (javaClass.getSuperclass() == null) {
            return null;
        } else {
            setExtendsType(getFactory().classToType(javaClass.getSuperclass()).toClass());
        }
        return extendsType;
    }

    /**
     * The package name of the class.
     * @return the package name
     */
    public String getPackageName()
    {
        return packageName;
    }

    /**
     * Extracts a package name from the class name.
     * @param className the fully qualified name of the class
     */
    void extractPackageName(String className)
    {
        int index = className.replace('/', '.').lastIndexOf('.');
        if (index != -1) {
            packageName = className.substring(0, index);
        } else {
            packageName = "";
        }
    }
    
    public String getSimpleName() {
        return javaClass.getSimpleName();
    }

    /**
     * Tests whether the given <code>ClassType</code> is from the same package as this <code>ClassType</code>.
     * @param other the other <code>ClassType</code>
     * @return true if the other class is from the same package
     */
    public boolean isSamePackage(ClassType other)
    {
        return getPackageName().equals(other.getPackageName());
    }
}
