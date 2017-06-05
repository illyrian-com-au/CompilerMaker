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

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;

/**
 * The class for representing all java class types.
 * <br/>
 * Contains information about the constructors, methods and fields in the class
 * and the interfaces that the class implements.
 */
public class ClassType extends Type {
    /** Reference to <code>null</code> type */
    public static final ClassType NULL_TYPE = new ClassType("null", (ClassType)null);
    /** Reference to <code>Object</code> type */
    public static final ClassType OBJECT_TYPE = new ClassType(Object.class);
    /** Reference to a special automatically created StringBuffer.
     *  An automatically created StringBuffer results from concatenating
     *  a String with any value or object.
     */
    public static final ClassType AUTO_STRING_TYPE = new ClassType(StringBuffer.class);
    /** Reference to <code>String</code> type */
    public static final ClassType STRING_TYPE = new ClassType(String.class);
    /** Reference to <code>StringBuffer</code> type */
    public static final ClassType STRING_BUFFER_TYPE = new ClassType(StringBuffer.class);
    /** Reference to <code>Cloneable</code> type */
    public static final ClassType CLONEABLE_TYPE = new ClassType(Cloneable.class);
    /** Reference to <code>Throwable</code> type */
    public static final ClassType THROWABLE_TYPE = new ClassType(Throwable.class);
    /** Reference to <code>Class</code> type */
    public static final ClassType CLASS_TYPE = new ClassType(Class.class);

    private ClassType       extendsType = null;
    private String          packageName = null;
    private ClassType    [] interfaces = null;
    private MakerMethod  [] constructors = null;
    private MakerMethod  [] methods = null;
    private MakerMethod  [] allMethods = null;
    private MakerField   [] fields = null;
    private int             modifiers = 0;

    public ClassType(Class javaClass)
    {
        this(ClassMaker.classToName(javaClass), OBJECT_TYPE);
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

    
    protected static String toSignature(String className)
    {
        return "L" + ClassMaker.toSlashName(className) + ";";
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
     * Finds a named field in the class.
     * @param name the name of the field
     * @return a representation of the field
     */
    public MakerField findField(String name)
    {
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
    public ClassType   [] getInterfaces()
    {
    	return interfaces;
    }
    
    /** Sets the list of interfaces implemented by this class. */
    public void setInterfaces(ClassType [] interfaces)
    {
        this.interfaces = interfaces;
    }
    
    /** The list of constructors for this class. */
    public MakerMethod [] getConstructors()
    {
    	return constructors;
    }
    
    /** Sets the list of constructors for this class. */
    public void setConstructors(MakerMethod [] constructors)
    {
    	this.constructors = constructors;
    }
    
    /**
     * Creates an array of constructor descriptors for the given java class.
     * </br>
     * Only constructors in the most explicit class are returned.
     * @param classType the ClassType that holds information about the class
     */
//    public void populateJavaClassConstructors(ClassType classType)
//    {
//        Class javaClass = classType.getJavaClass();
//        if (javaClass != null)
//        {
//            java.lang.reflect.Constructor [] construct = javaClass.getDeclaredConstructors();
//                MakerMethod [] constructors = new MakerMethod[construct.length];
//            for (int i = 0; i < construct.length; i++)
//            {
//                java.lang.reflect.Constructor javaMethod = construct[i];
//                constructors[i] = toMethod(classType, javaMethod);
//            }
//            classType.setConstructors(constructors);
//        }
//    }

    /** The list of methods implemented by this class. */
    public MakerMethod [] getMethods()
    {
    	return methods;
    }
    
    /** Sets the list of methods implemented by this class. */
    public void setMethods(MakerMethod [] methods)
    {
    	this.methods = methods;
    }
    
    /** The consolidated list of methods implemented by this class and all base classes. */
    public MakerMethod [] getAllMethods()
    {
    	return allMethods;
    }
    
    /** Sets the consolidated list of methods implemented by this class and all base classes. */
    public void setAllMethods(MakerMethod [] allMethods)
    {
    	this.allMethods = allMethods;
    }
    
    /** The list of member fields in this class. */
    public MakerField  [] getFields()
    {
    	return fields;
    }

    /** Sets the list of member fields in this class. */
    public void setFields(MakerField [] fields)
    {
    	this.fields = fields;
    }
    
    /**
     * Determines whether the class that this <code>ClassType</code> represents is an interface.
     * @return true if the class is an interface; otherwise false
     */
    public boolean isInterface()
    {
        return (getModifiers() & ClassMaker.ACC_INTERFACE) == ClassMaker.ACC_INTERFACE;
    }

     /**
     * The base class for this class.
     * @return the <code>ClassType</code> for the base class
     */
    public ClassType getExtendsType()
    {
        return extendsType;
    }
    
    public void setExtendsType(ClassType baseType) {
        extendsType = baseType;
    }
    
    public ClassType defaultExtendsType() {
        if (extendsType == null) {
            extendsType = ClassType.OBJECT_TYPE;
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
        if (index != -1)
            packageName = className.substring(0, index);
        else packageName = "";
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
