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

package au.com.illyrian.classmaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.classfile.ByteCode;
import org.mozilla.classfile.ClassFileWriter;

import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

/**
 *
 * @author Donald Strong
 */
public class ClassGenerator implements ClassMakerConstants
{
    private static final Logger log = Logger.getLogger(ClassGenerator.class.getName());

    /* Bitmask for class modifier to indicate that the class follows java 1.3+ (?) semantics for method invocation. */
    private static final short MASK_SUPER = 0x0002;

    // Fields - internal references
    /** A list of local variables in the class being generated. */
    Vector<MakerField> localTable = new Vector<MakerField>();
    /** Get the maximum local slots used by this method. */
    private short maxLocalSlots = 0;

    /* The ClassFileWriter instance that is used to generate byte-code for the class. */
    private final ClassFileWriter cfw;

    /** The name of the source file relative to the source path */
    private int previousLineNumber = 0;
    private SourceLine sourceLine;

    //#################### Constructors #################
    
    /**
     * Creates an instance of the ClassFileWriter if one has not been set.
     * 
     * @return a ClassFileWriter instance
     */
    public ClassGenerator(String className, String extendsClassName, String sourceFile)
    {
        cfw = new ClassFileWriter(toSlashName(className), toSlashName(extendsClassName), sourceFile);
    }
    
    public void setClassModifiers(int modifiers) {
        // Setting the Super bit is required for class files after Java 1.2.
        cfw.setFlags((short) (modifiers | MASK_SUPER));
    }

    /**
     * Each <code>ClassMaker</code> instance has its own
     * <code>ClassFileWriter</code>.
     * </br>
     * Generates a default ClassFileWriter if one has not been set explicitly.
     * 
     * @see #defaultClassFileWriter()
     * @return the
     *         <code>ClassFileWriter<code> instance for this <code>ClassMaker<code>
     */
    protected ClassFileWriter getClassFileWriter()
    {
        return cfw;
    }

    protected boolean isDebugCode()
    {
        if (getClassFileWriter() != null)
            return cfw.isDebugCode();
        return false;
    }

    protected void setDebugComment(String comment)
    {
        cfw.setDebugComment(comment);
    }

    // Convenience methods

    /**
     * Find a local variable by index.
     * 
     * @param index
     *            an index into <code>localTable</code>
     * @return the indexed local field
     */
    public MakerField lookupLocal(int index)
    {
        return localTable.get(index);
    }

    //############# Helper methods for derived classes #########

    /**
     * Determines whether the <code>Type</code> is a class.
     * 
     * @param type
     *            the type to be tested
     * @return true if <code>Type</code> implements a class
     */
    public static boolean isClass(Type type)
    {
        return type != null && type.toClass() != null;
    }

    /**
     * Determines whether the <code>Type</code> is an array.
     * 
     * @param type
     *            the type to be tested
     * @return true if <code>Type</code> implements a array
     */
    public static boolean isArray(Type type)
    {
        return type != null && type.toArray() != null;
    }

    /**
     * Determines whether the <code>Type</code> is an interface.
     * 
     * @param type
     *            the type to be tested
     * @return true if <code>Type</code> implements a class
     */
    public static boolean isInterface(Type type)
    {
        if (type != null && type.toClass() != null)
            return type.toClass().isInterface();
        return false;
    }

    /**
     * Determines whether the <code>Type</code> is a primitive type.
     * 
     * @param type
     *            the type to be tested
     * @return true if <code>Type</code> is a primitive type
     */
    public static boolean isPrimitive(Type type)
    {
        return type != null && type.toPrimitive() != null;
    }

    //###################  ################

    /**
     * Converts a <code>Class</code> into a class name.
     * 
     * @param javaClass
     *            the class from which to derive the name
     * @return a fully qualified class name delimited by dots
     */
    public static String classToName(Class javaClass)
    {
        if (javaClass.isArray())
            return toDotName(javaClass.getCanonicalName());
        else
            return toDotName(javaClass.getName());
    }

    /**
     * Converts a <code>Class</code> into a class name.
     * 
     * @param javaClass
     *            the class from which to derive the name
     * @return a fully qualified class name delimited by slashes
     */
    public static String classToSlashName(Class javaClass)
    {
        if (javaClass.isArray())
            return toSlashName(javaClass.getCanonicalName());
        else
            return toSlashName(javaClass.getName());
    }

    /**
     * Converts dots into slashes in a class name.
     * 
     * @param name
     *            the name to be converted
     * @return the name with dots replaced by slashes
     */
    public static String toSlashName(String name)
    {
        return name.replace('.', '/');
    }

    /**
     * Converts slashes into dots in a class name.
     * 
     * @param name
     *            the name to be converted
     * @return the name with slashes replaced by dots
     */
    public static String toDotName(String name)
    {
        return name.replace('/', '.');
    }

    /**
     * Converts a type Class into a signature.
     * 
     * @param javaClass
     *            a class representing a type from which to derive the signature
     * @return a JVM signature
     */
    public static String classToSignature(Class javaClass)
    {
        if (javaClass.isArray())
            return toSlashName(javaClass.getName());
        else if (javaClass.isPrimitive()) {
            if ("int".equals(javaClass.getName()))
                return "I";
            if ("float".equals(javaClass.getName()))
                return "F";
            if ("long".equals(javaClass.getName()))
                return "J";
            if ("void".equals(javaClass.getName()))
                return "V";
            if ("byte".equals(javaClass.getName()))
                return "B";
            if ("char".equals(javaClass.getName()))
                return "C";
            if ("double".equals(javaClass.getName()))
                return "D";
            if ("short".equals(javaClass.getName()))
                return "S";
            if ("boolean".equals(javaClass.getName()))
                return "Z";
            // Should never get here.
            throw new IllegalArgumentException("Could not determine signature for primitive: " + javaClass.getName());
        } else
            return "L" + classToSlashName(javaClass) + ";";
    }

    //##################### Method resolving ###################

    /**
     * Adds a local variable or formal parameter to the list of scoped
     * variables. <br/>
     * Sets the start program counter and scope level in the given field.
     * 
     * @param local the variable or parameter being added
     * @param scope the current nesting level of scoped code blocks
     */
    public void addToScope(MakerField local, int scope)
    {
        local.setStartPC(cfw.getCurrentCodeOffset());
        local.setEndPC(-1);
        local.setScopeLevel(scope);
    }

    /**
     * Limits the visibility of all local variables that were added at the given
     * scope level. <br/>
     * Records the program counter as the variable goes out of scope and marks
     * the variable as out of scope.
     * Scope entries are added to the method as it is completed.
     * 
     * @param scope the level of nesting of the current scoped code block
     */
    void exitScope(int scope)
    {
        if (getClassFileWriter() != null) {
            // Local variable descriptors are used by the debugger.
            for (int i = localTable.size() - 1; i >= 0; i--) {
                MakerField local = localTable.elementAt(i);
                if (local.getName() == null)
                    continue; // Skip anonymous local variables
                if (!local.isInScope())
                    continue; // Skip out of scope variables
                if (local.getScopeLevel() < scope)
                    break; // Stop when field is in wider scope
                local.setEndPC(cfw.getCurrentCodeOffset());
                local.setInScope(false);
            }
        }
    }

     /**
     * Adds an abstract method to the generated class.
     * </br>
     * An abstract method cannot have a body so must be forward declared.
     *
     */
    public void addAbstractMethod(MakerMethod method)
    {
        markLineNumber(); // possibly add a new line number entry.
        cfw.startMethod(method.getName(), method.getSignature(), method.getModifiers());
    }

    /**
     * Creates a new instance of the class.
     * 
     * @param declared
     *            a declared type represents the type of class
     * @return an <code>Initialiser</code> for the instance
     */
    public Value New(Type classType) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        cfw.add(ByteCode.NEW, toSlashName(classType.getName()));
        return classType.getValue();
    }

     //################## Method calls ##########################
 
    /**
     * Calls a virtual method in the class instance on top of the stack.
     * 
     * @param classType
     *            type of the class containing the method
     * @param method
     *            a descriptor of the resolved method
     */
    void invokeVirtual(ClassType classType, MakerMethod method)
    {
        String signature = method.getSignature();
        cfw.addInvoke(ByteCode.INVOKEVIRTUAL, toSlashName(classType.getName()), method.getName(), signature);
    }

    /**
     * Calls a method in the interface instance on top of the stack.
     * 
     * @param classType
     *            type of the interface containing the method
     * @param method
     *            a descriptor of the resolved method
     */
    void invokeInterface(ClassType classType, MakerMethod method)
    {
        String signature = method.getSignature();
        cfw.addInvoke(ByteCode.INVOKEINTERFACE, toSlashName(classType.getName()), method.getName(), signature);
    }

    /**
     * Calls a static method of the named class.
     * 
     * @param className
     *            a fully qualified classname
     * @param method
     *            a descriptor of the resolved method
     */
    void invokeStatic(String className, MakerMethod method)
    {
        String signature = method.getSignature();
        cfw.addInvoke(ByteCode.INVOKESTATIC, toSlashName(className), method.getName(), signature);
    }

    /**
     * Calls a private method or a method from the super class, in the class on
     * top of the stack.
     * 
     * @param classType
     *            type of the class containing the method
     * @param method
     *            a descriptor of the resolved method
     */
    void invokeSpecial(ClassType classType, MakerMethod method)
    {
        String signature = method.getSignature();
        cfw.addInvoke(ByteCode.INVOKESPECIAL, toSlashName(classType.getName()), method.getName(), signature);
    }

    /**
     * Throws an <code>Exception</code>.
     * </br>
     * The last statement of a method must be either <code>Return</code> or
     * <code>Throw</code>.
     * 
     * @param exception
     *            type exception being thrown
     */
    public void Throw(Type exception) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (cfw.isDebugCode())
            setDebugComment("Throw(" + exception.getName() + ");");
        cfw.add(ByteCode.ATHROW);
    }

    /**
     * Returns from the current method.
     * </br>
     * The last statement of a method must be either <code>Return</code> or
     * <code>Throw</code>.
     */
    public void Return() throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode())
            setDebugComment("Return();");
        cfw.add(ByteCode.RETURN);
    }

    /**
     * Returns with the value on top of the stack.
     * </br>
     * Promotes the value to the return type using
     * <code>AssignmentConversion</code>.
     * The last statement of a method must be either <code>Return</code> or
     * <code>Throw</code>.
     */
    public void Return(Type type) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode())
            setDebugComment("Return(" + type + ");");

        if (isClass(type)) {
            cfw.add(ByteCode.ARETURN);
            return;
        } else if (isPrimitive(type)) {
            switch (type.toPrimitive().index) {
            case PrimitiveType.BOOLEAN_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IRETURN);
                return;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LRETURN);
                return;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FRETURN);
                return;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DRETURN);
                return;
            }
        }
        // Should not get here.
        throw new IllegalArgumentException("Cannot return type" + type.getName());
    }

    // Special references
    /**
     * Pushes a reference to <code>this</code> class onto the stack.
     */
    public void loadThis() throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("This();");
        }
        cfw.addLoadThis();
    }

    /**
     * Pushes a reference to this class's <code>super</code> class onto the
     * stack.
     */
    public void loadSuper() throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Super();");
        }
        cfw.addLoadThis();
    }

    /**
     * Pushes <code>null</code> onto the stack.
     */
    public void Null() throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Null();");
        }
        cfw.add(ByteCode.ACONST_NULL);
    }

    // Literals
    /**
     * Pushes a literal <code>double</code> onto the stack.
     * @param value the double to be pushed onto the stack
     * @return the type for <code>double</code>
     */
    public Value Literal(double value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(" + value + ");");
        }
        cfw.addPush(value);
        return ClassMakerFactory.DOUBLE_TYPE.getValue();
    }

    /**
     * Pushes a literal <code>float</code> onto the stack.
     * @param value the float to be pushed onto the stack
     * @return the type for <code>float</code>
     */
    public Value Literal(float value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(" + value + ");");
        }
        cfw.addPush(value);
        return ClassMakerFactory.FLOAT_TYPE.getValue();
    }

    /**
     * Pushes a literal <code>long</code> onto the stack.
     * @param value the long to be pushed onto the stack
     * @return the type for <code>long</code>
     */
    public Value Literal(long value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(" + value + ");");
        }
        cfw.addPush(value);
        return ClassMakerFactory.LONG_TYPE.getValue();
    }

    /**
     * Pushes a literal <code>byte</code>, <code>short</code> or <code>int</code> onto the stack.
     * The value is pushed onto the stack and a Type is returned.
     * @param value the int to be pushed onto the stack
     * @return the Type representing a <code>byte</code>, <code>short</code> or <code>int</code>
     */
    public Value Literal(int value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(" + value + ");");
        }
        cfw.addPush(value);
        // Return the most specific type.
        // This will be promoted to an INT by numeric promotion.
        if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE)
            return ClassMakerFactory.BYTE_TYPE.getValue();
        else if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE)
            return ClassMakerFactory.SHORT_TYPE.getValue();
        else
            return ClassMakerFactory.INT_TYPE.getValue();
    }

    /**
     * Pushes a literal <code>char</code> onto the stack.
     * @param value the char to be pushed onto the stack
     * @return the type for <code>char</code>
     */
    public Value Literal(char value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(\'" + value + "\');");
        }
        cfw.addPush(value);
        return ClassMakerFactory.CHAR_TYPE.getValue();
    }

    /**
     * Pushes a literal <code>byte</code> value onto the stack.
     * @param value the byte to be pushed onto the stack
     * @return the type for <code>byte</code>
     */
    public Value Literal(byte value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(" + value + ");");
        }
        cfw.add(ByteCode.BIPUSH, value); // constant byte operand
        return ClassMakerFactory.BYTE_TYPE.getValue();
    }

    /**
     * Pushes a literal <code>short</code> onto the stack.
     * @param value the short to be pushed onto the stack
     * @return the type for <code>short</code>
     */
    public Value Literal(short value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(" + value + ");");
        }
        cfw.add(ByteCode.SIPUSH, value); // constant short operand
        return ClassMakerFactory.SHORT_TYPE.getValue();
    }

    /**
     * Pushes a literal <code>boolean</code> value onto the stack.
     * @param value the boolean value to be pushed onto the stack
     * @return the type for <code>boolean</code>
     */
    public Value Literal(boolean value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(" + value + ");");
        }
        cfw.addPush(value); // constant boolean operand
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    /**
     * Pushes a literal <code>String</code> onto the stack.
     * @param value the string to be pushed onto the stack
     * @return the type for <code>String</code>
     */
    public Value Literal(String value) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        if (isDebugCode()) {
            setDebugComment("Literal(" + value + ");");
        }
        cfw.addLoadConstant(value);
        return ClassMakerFactory.STRING_TYPE.getValue();
    }

    //#################### Getters and Setters ######################

    public Value setField(Value reference, MakerField field, Type valueType) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.

        String className = field.getClassType().getName();
        Type fieldType = field.getType();
        String signature = fieldType.getSignature();
        cfw.add(ByteCode.PUTFIELD, className, field.getName(), signature);
        return ClassMakerFactory.VOID_TYPE.getValue();
    }

    public Value setFieldStatic(MakerField field, Type valueType) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.

        Type type = field.getType();
        String signature = type.getSignature();
        cfw.add(ByteCode.PUTSTATIC, field.getClassType().getName(), field.getName(), signature);
        return ClassMakerFactory.VOID_TYPE.getValue();
    }

    public Type loadField(MakerField field)
    {
        assertNotNull(field, "field");
        markLineNumber(); // possibly add a new line number entry.
        ClassType classType = field.getClassType();
        if (log.isLoggable(Level.FINE))
            log.finest("load " + classType.getName() + ", " + field.getName() + ", " + field.getType().getSignature());
        cfw.add(ByteCode.GETFIELD, classType.getName(), field.getName(), field.getType().getSignature());
        return field.getType();
    }

    public Value loadStatic(MakerField field)
    {
        assertNotNull(field, "field");
        markLineNumber(); // possibly add a new line number entry.
        ClassType classType = field.getClassType();
        if (log.isLoggable(Level.FINE))
            log.finest("static load " + classType.getName() + ", " + field.getName() + ", "
                    + field.getType().getSignature());
        cfw.add(ByteCode.GETSTATIC, classType.getName(), field.getName(), field.getType().getSignature());
        return field.getValue();
    }

    //############### Local variable methods ##################

    /**
     * Loads a value onto the stack from a local variable.
     * 
     * @param local
     *            the <code>Field</code> that describes the member variable
     * @return the type of the variable on top of the stack
     */
    Value loadLocal(MakerField local) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        int slot = local.getSlot();
        Type type = local.getType();

        if (isClass(type))
            cfw.addALoad(slot);
        else if (isPrimitive(type)) {
            switch (type.toPrimitive().index) {
            case PrimitiveType.BOOLEAN_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                cfw.addILoad(slot);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.addLLoad(slot);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.addDLoad(slot);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.addFLoad(slot);
                break;
            default:
                // Should never get here.
                throw new IllegalArgumentException("Do not know how to load local " + type);
            }
        } else {
            throw new IllegalArgumentException("Do not know how to load local " + type);
        }
        return type.getValue();
    }

    /**
     * Sets the value on top of the stack to a local variable.
     * 
     * @param slot
     *            the slot in the stack frame that contains the variable
     * @param type
     *            type of the value on top of the stack
     */
    void storeLocal(MakerField field, Type type) throws ClassMakerException
    {
        int slot = field.getSlot();
        if (isClass(type)) {
            cfw.addAStore(slot);
            return;
        } else if (isPrimitive(type)) {
            switch (type.toPrimitive().index) {
            case PrimitiveType.BOOLEAN_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                cfw.addIStore(slot);
                return;
            case PrimitiveType.LONG_INDEX:
                cfw.addLStore(slot);
                return;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.addDStore(slot);
                return;
            case PrimitiveType.FLOAT_INDEX:
                cfw.addFStore(slot);
                return;
            }
        }
        // Should never get here.
        throw new IllegalArgumentException("Do not know how to store " + type);
    }

    public void initLocal(MakerField field)
    {
        Type type = field.getType();
        int slot = field.getSlot();
        if (isClass(type)) {
            cfw.add(ByteCode.ACONST_NULL);
            cfw.addAStore(slot);
            return;
        } else if (isPrimitive(type)) {
            switch (type.toPrimitive().index) {
            case PrimitiveType.BOOLEAN_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.ICONST_0);
                cfw.addIStore(slot);
                return;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LCONST_0);
                cfw.addLStore(slot);
                return;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DCONST_0);
                cfw.addDStore(slot);
                return;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FCONST_0);
                cfw.addFStore(slot);
                return;
            }
        }
        // Should never get here.
        throw new IllegalArgumentException("Do not know how to initialize " + type);
    }

    //############# Variable declarations ################

    public void declareVariable(String name, Type type, int modifiers) throws ClassMakerException
    {
        cfw.addField(name, type.getSignature(), (short) modifiers);
    }
    //################### Casting methods. ##################

    /**
     * Converts a reference from one type to another.
     * </br>
     * Generates code to check the cast of the reference on top of the stack.
     * This method is called by CastingConversion.wideningReferenceConversion()
     * to perform casts.
     *
     * @param source
     *            the type of the reference on top of the stack
     * @param target
     *            the type into which to cast
     * @return the target type
     */
    public ClassType toReference(ClassType source, ClassType target) throws ClassMakerException
    {
        markLineNumber(); // possibly add a new line number entry.
        String className = target.getName();
        cfw.add(ByteCode.CHECKCAST, className);
        return target;
    }

    Type checkInstanceOf(Type source, ClassType target) throws ClassMakerException
    {
        if (cfw.isDebugCode())
            setDebugComment("InstanceOf(" + source.getName() + ", " + target.getName() + ");");
        markLineNumber(); // possibly add a new line number entry.
        String className = toSlashName(target.getName());
        cfw.add(ByteCode.INSTANCEOF, className);
        return ClassMakerFactory.BOOLEAN_TYPE;
    }

    /**
     * Converts a value to a byte.
     * </br>
     * The value on top of the stack is converted from any primitive type to a
     * byte.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr>
     * <td>Java code</td>
     * <td>ClassMaker code</td>
     * </tr>
     * <tr>
     * <td><code>(byte)a<code></td>
     * <td><code>toByte(Get("a"))</code></td>
     * </tr>
     * </table>
     * 
     * @param op
     *            the type of the operand
     * @return the type of the result is always byte
     */
    public Type toByte(Type op) throws ClassMakerException {
        if (!ClassMakerFactory.BYTE_TYPE.equals(op)) {
            toInt(op);
            cfw.add(ByteCode.I2B);
        }
        return ClassMakerFactory.BYTE_TYPE;
    }

    /**
     * Converts a value to a short.
     * </br>
     * The value on top of the stack is converted from any primitive type to a
     * short.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr>
     * <td>Java code</td>
     * <td>ClassMaker code</td>
     * </tr>
     * <tr>
     * <td><code>(short)a<code></td>
     * <td><code>toShort(Get("a"))</code></td>
     * </tr>
     * </table>
     * 
     * @param op
     *            the type of the operand
     * @return the type of the result is always short
     */
    public Type toShort(Type op) throws ClassMakerException
    {
        if (!ClassMakerFactory.SHORT_TYPE.equals(op)) {
            toInt(op);
            cfw.add(ByteCode.I2S);
        }
        return ClassMakerFactory.SHORT_TYPE;
    }

    /**
     * Converts a value to a char.
     * </br>
     * The value on top of the stack is converted from any primitive type to a
     * char.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr>
     * <td>Java code</td>
     * <td>ClassMaker code</td>
     * </tr>
     * <tr>
     * <td><code>(char)a<code></td>
     * <td><code>toChar(Get("a"))</code></td>
     * </tr>
     * </table>
     * 
     * @param op
     *            the type of the operand
     * @return the type of the result is always char
     */
    public Type toChar(Type op) throws ClassMakerException
    {
        if (!ClassMakerFactory.CHAR_TYPE.equals(op)) {
            toInt(op);
            cfw.add(ByteCode.I2C);
        }
        return ClassMakerFactory.CHAR_TYPE;
    }

    /**
     * Converts a value to an int.
     * </br>
     * The value on top of the stack is converted from any primitive type to an
     * int.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr>
     * <td>Java code</td>
     * <td>ClassMaker code</td>
     * </tr>
     * <tr>
     * <td><code>(int)a<code></td>
     * <td><code>toInt(Get("a"))</code></td>
     * </tr>
     * </table>
     * 
     * @param op
     *            the type of the operand
     * @return the type of the result is always int
     */
    public Type toInt(Type op) throws ClassMakerException
    {
        if (isPrimitive(op)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                return ClassMakerFactory.INT_TYPE;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.L2I);
                return ClassMakerFactory.INT_TYPE;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.D2I);
                return ClassMakerFactory.INT_TYPE;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.F2I);
                return ClassMakerFactory.INT_TYPE;
            }
        }
        throw new IllegalArgumentException("Do not know how to convert to int " + op);
    }

    /**
     * Converts a value to a long.
     * </br>
     * The value on top of the stack is converted from any primitive type to a
     * long.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr>
     * <td>Java code</td>
     * <td>ClassMaker code</td>
     * </tr>
     * <tr>
     * <td><code>(long)a<code></td>
     * <td><code>toLong(Get("a"))</code></td>
     * </tr>
     * </table>
     * 
     * @param op
     *            the type of the operand
     * @return the type of the result is always long
     */
    public Type toLong(Type op) throws ClassMakerException
    {
        if (isPrimitive(op)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.I2L);
                return ClassMakerFactory.LONG_TYPE;
            case PrimitiveType.LONG_INDEX:
                return ClassMakerFactory.LONG_TYPE;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.D2L);
                return ClassMakerFactory.LONG_TYPE;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.F2L);
                return ClassMakerFactory.LONG_TYPE;
            }
        }
        throw new IllegalArgumentException("Do not know how to convert to long " + op);
    }

    /**
     * Converts a value to a float.
     * </br>
     * The value on top of the stack is converted from any primitive type to a
     * float.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr>
     * <td>Java code</td>
     * <td>ClassMaker code</td>
     * </tr>
     * <tr>
     * <td><code>(float)a<code></td>
     * <td><code>toFloat(Get("a"))</code></td>
     * </tr>
     * </table>
     * 
     * @param op
     *            the type of the operand
     * @return the type of the result is always float
     */
    public Type toFloat(Type op) throws ClassMakerException
    {
        if (isPrimitive(op)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.I2F);
                return ClassMakerFactory.FLOAT_TYPE;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.L2F);
                return ClassMakerFactory.FLOAT_TYPE;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.D2F);
                return ClassMakerFactory.FLOAT_TYPE;
            case PrimitiveType.FLOAT_INDEX:
                return ClassMakerFactory.FLOAT_TYPE;
            }
        }
        throw new IllegalArgumentException("Do not know how to convert to float " + op);
    }

    /**
     * Converts a value to a double.
     * </br>
     * The value on top of the stack is converted from any primitive type to a
     * double.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr>
     * <td>Java code</td>
     * <td>ClassMaker code</td>
     * </tr>
     * <tr>
     * <td><code>(double)a<code></td>
     * <td><code>toDouble(Get("a"))</code></td>
     * </tr>
     * </table>
     * 
     * @param op
     *            the type of the operand
     * @return the type of the result is always double
     */
    public Type toDouble(Type op) throws ClassMakerException
    {
        if (isPrimitive(op)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.I2D);
                return ClassMakerFactory.DOUBLE_TYPE;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.L2D);
                return ClassMakerFactory.DOUBLE_TYPE;
            case PrimitiveType.DOUBLE_INDEX:
                return ClassMakerFactory.DOUBLE_TYPE;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.F2D);
                return ClassMakerFactory.DOUBLE_TYPE;
            }
        }
        throw new IllegalArgumentException("Do not know how to convert to double " + op);
    }

    //################ Arithmetic operators ######################

    /**
     * Integer addition on primitive operands.
     * </br>
     * This method is provided so that <code>Add</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * perform
     * integer addition for <code>byte, short, char, int and long</code> operand
     * types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @param op2
     *            the type of the right operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveAdd(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.

        if (op1.equals(op2)) {
            switch (op1.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2B);
                return op1;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2S);
                return op1;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2C);
                return op1;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IADD);
                return op1;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LADD);
                return op1;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DADD);
                return op1;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FADD);
                return op1;
            }
        }
        throw new IllegalArgumentException("Do not know how to add operands of type " + op1 + " and " + op2);
    }

    /**
     * Integer subtraction on primitive operands.
     * </br>
     * This method is provided so that <code>Subt</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * perform
     * integer subtraction for <code>byte, short, char, int and long</code>
     * operand types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @param op2
     *            the type of the right operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveSubt(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.
        if (op1.equals(op2)) {
            switch (op1.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.ISUB);
                cfw.add(ByteCode.I2B);
                return op1;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.ISUB);
                cfw.add(ByteCode.I2S);
                return op1;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.ISUB);
                cfw.add(ByteCode.I2C);
                return op1;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.ISUB);
                return op1;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LSUB);
                return op1;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DSUB);
                return op1;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FSUB);
                return op1;
            }
        }
        throw new IllegalArgumentException("Do not know how to subtract operands of type " + op1 + " and " + op2);
    }

     /**
     * Integer multiply on primitive operands.
     * </br>
     * This method is provided so that <code>Mult</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * perform
     * integer multiplication for <code>byte, short, char, int and long</code>
     * operand types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @param op2
     *            the type of the right operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveMult(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.
        if (op1.equals(op2)) {
            switch (op1.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.IMUL);
                cfw.add(ByteCode.I2B);
                return op1;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.IMUL);
                cfw.add(ByteCode.I2S);
                return op1;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.IMUL);
                cfw.add(ByteCode.I2C);
                return op1;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IMUL);
                return op1;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LMUL);
                return op1;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DMUL);
                return op1;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FMUL);
                return op1;
            }
        }
        throw new IllegalArgumentException("Do not know how to multiply operands of type " + op1 + " and " + op2);
    }


    /**
     * Integer divide on primitive operands.
     * </br>
     * This method is provided so that <code>Rem</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * perform
     * integer division for <code>byte, short, char, int and long</code> operand
     * types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @param op2
     *            the type of the right operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveDiv(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.
        if (op1.equals(op2)) {
            switch (op1.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.IDIV);
                cfw.add(ByteCode.I2B);
                return op1;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.IDIV);
                cfw.add(ByteCode.I2S);
                return op1;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.IDIV);
                cfw.add(ByteCode.I2C);
                return op1;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IDIV);
                return op1;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LDIV);
                return op1;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DDIV);
                return op1;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FDIV);
                return op1;
            }
        }
        throw new IllegalArgumentException("Do not know how to divide operands of type " + op1 + " and " + op2);
    }

    /**
     * Integer remainder on primitive operands.
     * </br>
     * This method is provided so that <code>Rem</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * calculate
     * the integer remainder for <code>byte, short, char, int and long</code>
     * operand types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @param op2
     *            the type of the right operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveRem(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.

        if (op1.equals(op2)) {
            switch (op1.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.IREM);
                cfw.add(ByteCode.I2B);
                return op1;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.IREM);
                cfw.add(ByteCode.I2S);
                return op1;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.IREM);
                cfw.add(ByteCode.I2C);
                return op1;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IREM);
                return op1;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LREM);
                return op1;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DREM);
                return op1;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FREM);
                return op1;
            }
        }
        throw new IllegalArgumentException("Do not know how to remainder operands of type " + op1 + " and " + op2);
    }

    Type primitiveNeg(Type type)
    {
        if (isPrimitive(type)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (type.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.INEG);
                return type;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LNEG);
                return type;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DNEG);
                return type;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FNEG);
                return type;
            }
        }
        throw new IllegalArgumentException("Do not know how to negate type " + type);
    }

    /**
     * Bitwise XOR on primitive operands.
     * </br>
     * This method is provided so that <code>Xor</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * exclusive or <code>byte, short, char, int and long</code> operand types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @param op2
     *            the type of the right operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveXor(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.

        if (op1.equals(op2)) {
            switch (op1.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.IXOR);
                cfw.add(ByteCode.I2B);
                return op1;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.IXOR);
                cfw.add(ByteCode.I2S);
                return op1;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.IXOR);
                cfw.add(ByteCode.I2C);
                return op1;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IXOR);
                return op1;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LXOR);
                return op1;
            }
        }
        throw new IllegalArgumentException("Do not know how to XOR operands of type " + op1 + " and " + op2);
    }

    /**
     * Bitwise AND on primitive operands.
     * </br>
     * This method is provided so that <code>And</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * bitwise and <code>byte, short, char, int and long</code> operand types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @param op2
     *            the type of the right operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveAnd(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.

        if (op1.equals(op2)) {
            switch (op1.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.IAND);
                cfw.add(ByteCode.I2B);
                return op1;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.IAND);
                cfw.add(ByteCode.I2S);
                return op1;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.IAND);
                cfw.add(ByteCode.I2C);
                return op1;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IAND);
                return op1;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LAND);
                return op1;
            }
        }
        throw new IllegalArgumentException("Do not know how to bitwise AND operands of type " + op1 + " and " + op2);
    }

    /**
     * Bitwise OR on primitive operands.
     * </br>
     * This method is provided so that <code>Or</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * bitwise or <code>byte, short, char, int and long</code> operand types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @param op2
     *            the type of the right operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveOr(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.

        if (op1.equals(op2)) {
            switch (op1.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.IOR);
                cfw.add(ByteCode.I2B);
                return op1;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.IOR);
                cfw.add(ByteCode.I2S);
                return op1;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.IOR);
                cfw.add(ByteCode.I2C);
                return op1;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IOR);
                return op1;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LOR);
                return op1;
            }
        }
        throw new IllegalArgumentException("Do not know how to bitwise OR operands of type " + op1 + " and " + op2);
    }

    /**
     * Bitwise <b>Inversion</b> on primitive operands.
     * </br>
     * This method is provided so that <code>Inv</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * invert the bits on <code>byte, short, char, int and long</code> operand
     * types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the left operand
     * @return the type of the result
     */
    protected PrimitiveType primitiveInv(PrimitiveType op1)
    {
        markLineNumber(); // possibly add a new line number entry.

        switch (op1.index) {
        case PrimitiveType.BYTE_INDEX:
            cfw.addPush(0xFFFFFFFF);
            cfw.add(ByteCode.IXOR);
            cfw.add(ByteCode.I2B);
            break;
        case PrimitiveType.SHORT_INDEX:
            cfw.addPush(0xFFFFFFFF);
            cfw.add(ByteCode.IXOR);
            cfw.add(ByteCode.I2S);
            break;
        case PrimitiveType.CHAR_INDEX:
            cfw.addPush(0xFFFFFFFF);
            cfw.add(ByteCode.IXOR);
            cfw.add(ByteCode.I2C);
            break;
        case PrimitiveType.INT_INDEX:
            cfw.addPush(0xFFFFFFFF);
            cfw.add(ByteCode.IXOR);
            break;
        case PrimitiveType.LONG_INDEX:
            cfw.addPush(0xFFFFFFFFFFFFFFFFL);
            cfw.add(ByteCode.LXOR);
            break;
        default:
            throw new IllegalArgumentException("Do not know how to invert type " + op1);
        }
        return op1;
    }

    //################ Bitwise shifting operators ####################
    /**
     * Raw Shift left on primitive operands.
     * </br>
     * This method is provided so that <code>SHL</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * shift left <code>byte, short, char, int and long</code> operand types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the operand being shifted
     * @param op2
     *            the type of the operand indicating places to shift
     * @return the type of op1 after promotion
     */
    protected PrimitiveType primitiveShiftLeft(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.
        switch (op1.index) {
        case PrimitiveType.BYTE_INDEX:
            cfw.add(ByteCode.ISHL);
            cfw.add(ByteCode.I2B);
            break;
        case PrimitiveType.SHORT_INDEX:
            cfw.add(ByteCode.ISHL);
            cfw.add(ByteCode.I2S);
            break;
        case PrimitiveType.CHAR_INDEX:
            cfw.add(ByteCode.ISHL);
            cfw.add(ByteCode.I2C);
            break;
        case PrimitiveType.INT_INDEX:
            cfw.add(ByteCode.ISHL);
            break;
        case PrimitiveType.LONG_INDEX:
            cfw.add(ByteCode.LSHL);
            break;
        default:
            throw new IllegalArgumentException("Do not know how to shift left operands of type " + op1 + " and " + op2);
        }
        return op1;
    }

    /**
     * Raw Shift Right on primitive operands.
     * </br>
     * This method is provided so that <code>SHR</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * shift right <code>byte, short, char, int and long</code> operand types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the operand being shifted
     * @param op2
     *            the type of the operand indicating places to shift
     * @return the type of op1 after promotion
     */
    protected PrimitiveType primitiveShiftRight(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.
        switch (op1.index) {
        case PrimitiveType.BYTE_INDEX:
            cfw.add(ByteCode.ISHR);
            cfw.add(ByteCode.I2B);
            break;
        case PrimitiveType.SHORT_INDEX:
            cfw.add(ByteCode.ISHR);
            cfw.add(ByteCode.I2S);
            break;
        case PrimitiveType.CHAR_INDEX:
            cfw.add(ByteCode.ISHR);
            cfw.add(ByteCode.I2C);
            break;
        case PrimitiveType.INT_INDEX:
            cfw.add(ByteCode.ISHR);
            break;
        case PrimitiveType.LONG_INDEX:
            cfw.add(ByteCode.LSHR);
            break;
        default:
            throw new IllegalArgumentException("Do not know how to shift right operands of type " + op1 + " and " + op2);
       }
        return op1;
    }

    /**
     * Raw Unsigned Shift Right on primitive operands.
     * </br>
     * This method is provided so that <code>USR</code> can be overridden to
     * handle
     * new types of operands and promotions. The method generates bytecode to
     * unsigned shift right <code>byte, short, char, int and long</code> operand
     * types.
     * The operands are not numerically promoted.
     * 
     * @param op1
     *            the type of the operand being shifted
     * @param op2
     *            the type of the operand indicating places to shift
     * @return the type of op1 after promotion
     */
    protected PrimitiveType primitiveUnsignedShiftRight(PrimitiveType op1, PrimitiveType op2)
    {
        markLineNumber(); // possibly add a new line number entry.

        switch (op1.index) {
        case PrimitiveType.BYTE_INDEX:
            cfw.add(ByteCode.IUSHR);
            cfw.add(ByteCode.I2B);
            break;
        case PrimitiveType.SHORT_INDEX:
            cfw.add(ByteCode.IUSHR);
            cfw.add(ByteCode.I2S);
            break;
        case PrimitiveType.CHAR_INDEX:
            cfw.add(ByteCode.IUSHR);
            cfw.add(ByteCode.I2C);
            break;
        case PrimitiveType.INT_INDEX:
            cfw.add(ByteCode.IUSHR);
            break;
        case PrimitiveType.LONG_INDEX:
            cfw.add(ByteCode.LUSHR);
            break;
        default:
            throw new IllegalArgumentException("Do not know how to unsigned shift right operands of type " + op1 + " and " + op2);
        }
        return op1;
    }

    //################# Comparison operators ######################

    Value primitiveGreaterThan(Type op1, Type op2)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("GT(" + op1 + ", " + op2 + ");");
        }
        if (isPrimitive(op1) && op1.equals(op2)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op1.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                addCompare(ByteCode.IF_ICMPGT);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LCMP);
                addCompare(ByteCode.IFGT);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DCMPL);
                addCompare(ByteCode.IFGT);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FCMPL);
                addCompare(ByteCode.IFGT);
                break;
            default:
                throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
            }
        } else {
            throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
        }
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    Value primitiveGreaterEqual(Type op1, Type op2)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("GE(" + op1 + ", " + op2 + ");");
        }
        if (isPrimitive(op1) && op1.equals(op2)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op1.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                addCompare(ByteCode.IF_ICMPGE);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LCMP);
                addCompare(ByteCode.IFGE);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DCMPL);
                addCompare(ByteCode.IFGE);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FCMPL);
                addCompare(ByteCode.IFGE);
                break;
            default:
                throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
            }
        } else {
            throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
        }
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    Value primitiveLessEqual(Type op1, Type op2)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("LE(" + op1 + ", " + op2 + ");");
        }
        if (isPrimitive(op1) && op1.equals(op2)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op1.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                addCompare(ByteCode.IF_ICMPLE);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LCMP);
                addCompare(ByteCode.IFLE);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DCMPG);
                addCompare(ByteCode.IFLE);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FCMPG);
                addCompare(ByteCode.IFLE);
                break;
            default:
                throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
            }
        } else {
            throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
        }
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    Value primitiveLessThan(Type op1, Type op2)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("LT(" + op1 + ", " + op2 + ");");
        }
        if (isPrimitive(op1) && op1.equals(op2)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op1.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                addCompare(ByteCode.IF_ICMPLT);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LCMP);
                addCompare(ByteCode.IFLT);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DCMPG);
                addCompare(ByteCode.IFLT);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FCMPG);
                addCompare(ByteCode.IFLT);
                break;
            default:
                throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
            }
        } else {
            throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
        }
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    Value primitiveIsEqual(Type op1, Type op2)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("EQ(" + op1 + ", " + op2 + ");");
        }
        if (isClass(op1) && isClass(op2)) {
            addCompare(ByteCode.IF_ACMPEQ);
        } else if (isPrimitive(op1) && op1.equals(op2)) {
            markLineNumber(); // possibly add a new line number entry.
            switch (op1.toPrimitive().index) {
            case PrimitiveType.BOOLEAN_INDEX: // fall thru
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                addCompare(ByteCode.IF_ICMPEQ);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LCMP);
                addCompare(ByteCode.IFEQ);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DCMPL);
                addCompare(ByteCode.IFEQ);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FCMPL);
                addCompare(ByteCode.IFEQ);
                break;
            default:
                throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
            }
        } else {
            throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
        }
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    Value primitiveNotEqual(Type op1, Type op2)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("NE(" + op1 + ", " + op2 + ");");
        }
        if (isClass(op1) && isClass(op2)) {
            addCompare(ByteCode.IF_ACMPNE);
        } else if (isPrimitive(op1) && op1.equals(op2)) {
            switch (op1.toPrimitive().index) {
            case PrimitiveType.BOOLEAN_INDEX: // fall thru
            case PrimitiveType.BYTE_INDEX: // fall thru
            case PrimitiveType.SHORT_INDEX: // fall thru
            case PrimitiveType.CHAR_INDEX: // fall thru
            case PrimitiveType.INT_INDEX:
                addCompare(ByteCode.IF_ICMPNE);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LCMP);
                addCompare(ByteCode.IFNE);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DCMPL);
                addCompare(ByteCode.IFNE);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FCMPL);
                addCompare(ByteCode.IFNE);
                break;
            default:
                throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
            }
        } else {
            throw new IllegalArgumentException("Do not know how to compare operands of type " + op1 + " and " + op2);
        }
        return ClassMakerFactory.BOOLEAN_TYPE.getValue();
    }

    /**
     * Compares the two values on top of the stack using the given bytecode
     * operator.
     * </br>
     * There is no convenient way using JVM bytecode to compare two values and
     * leave a boolean
     * result on the stack. It must be done by pushing 1 or 0 onto the stack, as
     * appropriate.
     * 
     * @param ifOperator
     *            the bytcode comparison operator to use.
     */
    private void addCompare(int ifOperator)
    {
        int jumpTrue = cfw.acquireLabel();
        int jumpFalse = cfw.acquireLabel();

        cfw.add(ifOperator, jumpTrue);
        if (isDebugCode())
            setDebugComment("false");
        cfw.add(ByteCode.ICONST_0);
        cfw.add(ByteCode.GOTO, jumpFalse);
        cfw.markLabel(jumpTrue);
        if (isDebugCode())
            setDebugComment("true");
        cfw.add(ByteCode.ICONST_1);
        cfw.markLabel(jumpFalse);
    }

    protected Value primitiveNot(Type op1)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("Not(" + op1 + ");");
        }
        if (ClassMakerFactory.BOOLEAN_TYPE.equals(op1)) {
            cfw.add(ByteCode.ICONST_1);
            cfw.add(ByteCode.IXOR);
            return ClassMakerFactory.BOOLEAN_TYPE.getValue();
        } else {
            throw new IllegalArgumentException("Do not know how to NOT operand of type " + op1);
        }
    }

    //##################### Arrays ##########################
    
    protected Value primitiveNewArray(ArrayType array, Type sizeType) {
        // FIXME promote sizeType.
        Type arrayOfType = array.getComponentType();
        if (isPrimitive(arrayOfType)) {
            byte elementType = typeToArrayElement(arrayOfType.toPrimitive());
            cfw.add(ByteCode.NEWARRAY, elementType);
        } else if (isClass(arrayOfType)) {
            String className = arrayOfType.getName();
            cfw.add(ByteCode.ANEWARRAY, className);
        } else {
            throw new IllegalArgumentException("Do not know how create array of type " + array);
        }
        return array.getValue();
    }
    
    protected Value primitiveNewArray(ArrayType arrayType, Type[] dims) {
        if (cfw.isDebugCode())
            setDebugComment("NewArray(" + arrayType + ", " + dims + ");");
        if (isClass(arrayType.getComponentType())) {
            cfw.add(ByteCode.MULTIANEWARRAY, arrayType.getSignature(), (byte) dims.length);
        } else {
            throw new IllegalArgumentException("Do not know how create multi dimensional array of type " + arrayType);
        }
        return arrayType.getValue();
    }

    Value getAtIndex(ArrayType arrayType, Type index)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("GetAt(" + arrayType + ", " + index + ");");
        }
        Type elementType = arrayType.getComponentType();
        if (isClass(elementType) || isArray(elementType)) {
            cfw.add(ByteCode.AALOAD);
        } else {
            switch (elementType.toPrimitive().index) {
            case PrimitiveType.BOOLEAN_INDEX: // Fall thru.
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.BALOAD);
                break;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.SALOAD);
                break;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.CALOAD);
                break;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IALOAD);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LALOAD);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DALOAD);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FALOAD);
                break;
            default:
                throw new IllegalArgumentException("Cannot load type " + elementType.getName() + " from array type: "
                        + arrayType.getName());
            }
        }
        return arrayType.getComponentType().getValue();
    }

    Value setAtIndex(ArrayType arrayType, Type indexType, Type valueType)
    {
        if (cfw.isDebugCode()) {
            setDebugComment("SetAt(" + arrayType + ", " + indexType + ", " + valueType + ");");
        }
        if (isClass(valueType)) {
            cfw.add(ByteCode.AASTORE);
        } else if (isPrimitive(valueType)) {
            switch (valueType.toPrimitive().index) {
            case PrimitiveType.BOOLEAN_INDEX: // Fall thru.
            case PrimitiveType.BYTE_INDEX:
                cfw.add(ByteCode.BASTORE);
                break;
            case PrimitiveType.SHORT_INDEX:
                cfw.add(ByteCode.SASTORE);
                break;
            case PrimitiveType.CHAR_INDEX:
                cfw.add(ByteCode.CASTORE);
                break;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IASTORE);
                break;
            case PrimitiveType.LONG_INDEX:
                cfw.add(ByteCode.LASTORE);
                break;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.add(ByteCode.DASTORE);
                break;
            case PrimitiveType.FLOAT_INDEX:
                cfw.add(ByteCode.FASTORE);
                break;
            default:
                throw new IllegalArgumentException("Cannot assign type " + valueType.getName() + " to an array.");
            }
        }
        return ClassMakerFactory.VOID_TYPE.getValue();
    }

    Value arrayLength(ArrayType array)
    {
        cfw.add(ByteCode.ARRAYLENGTH);
        return ClassMakerFactory.INT_TYPE.getValue();
    }

    /**
     * Converts a Primitive type into an appropriate element code
     * 
     * @param type
     *            the type of element in the array
     * @return a element code suitable for the JVM
     */
    public byte typeToArrayElement(PrimitiveType type)
    {
        switch (type.index) {
        case PrimitiveType.BOOLEAN_INDEX:
            return ByteCode.T_BOOLEAN;
        case PrimitiveType.BYTE_INDEX:
            return ByteCode.T_BYTE;
        case PrimitiveType.SHORT_INDEX:
            return ByteCode.T_SHORT;
        case PrimitiveType.CHAR_INDEX:
            return ByteCode.T_CHAR;
        case PrimitiveType.INT_INDEX:
            return ByteCode.T_INT;
        case PrimitiveType.LONG_INDEX:
            return ByteCode.T_LONG;
        case PrimitiveType.DOUBLE_INDEX:
            return ByteCode.T_DOUBLE;
        case PrimitiveType.FLOAT_INDEX:
            return ByteCode.T_FLOAT;
        }
        throw new IllegalArgumentException("Cannot create an array of type: " + type.getName());
    }

    /**
     * Pops a value off the program stack.
     * </br>
     * If the <code>type</code> is <code>long</code> or <code>double</code> two
     * words (64 bits) are popped; otherwise one word (32 bits) are popped.
     * 
     * @param type
     *            the type of the value on top of the stack
     */
    protected void pop(Type type)
    {
        markLineNumber(); // possibly add a new line number entry.
        if (ClassMakerFactory.LONG_TYPE.equals(type) || ClassMakerFactory.DOUBLE_TYPE.equals(type))
            cfw.add(ByteCode.POP2);
        else
            cfw.add(ByteCode.POP);
    }

    /**
     * Duplicate the value on top of the stack.
     * </br>
     * If the <code>type</code> is <code>long</code> or <code>double</code> two
     * words (64 bits) are duplicated; otherwise one word (32 bits) is
     * duplicated.
     * 
     * @param type
     *            the type of the value on top of the stack
     */
    protected void dup(Type type)
    {
        if (ClassMakerFactory.LONG_TYPE.equals(type) || ClassMakerFactory.DOUBLE_TYPE.equals(type))
            cfw.add(ByteCode.DUP2);
        else
            cfw.add(ByteCode.DUP);
    }

    /**
     * Duplicates the value on top of the stack and place it underneath the next
     * value.
     * </br>
     * If the <code>type</code> is <code>long</code> or <code>double</code> two
     * words (64 bits) are duplicated; otherwise one word (32 bits) are
     * duplicated.
     * The duplicated value us placed underneath the next value down from the
     * top
     * of the stack.
     * 
     * @param underType
     *            the type of the second value on the stack
     * @param type
     *            the type of the top value on the stack
     */
    protected void dupunder(Type underType, Type type)
    {
        if (ClassMakerFactory.LONG_TYPE.equals(type) || ClassMakerFactory.DOUBLE_TYPE.equals(type)) {
            if (ClassMakerFactory.LONG_TYPE.equals(underType) || ClassMakerFactory.DOUBLE_TYPE.equals(underType)) {
                cfw.add(ByteCode.DUP2_X2);
            } else {
                cfw.add(ByteCode.DUP2_X1);
            }
        } else {
            if (ClassMakerFactory.LONG_TYPE.equals(underType) || ClassMakerFactory.DOUBLE_TYPE.equals(underType)) {
                cfw.add(ByteCode.DUP_X2);
            } else {
                cfw.add(ByteCode.DUP_X1);
            }
        }
    }

    /**
     * Swaps the two values on top of the stack.
     * </br>
     * If the type is <code>long</code> or <code>double</code> two words (64
     * bits) are moved; otherwise one word (32 bits) are moved.
     * 
     * @param underType
     *            the type of the second value on the stack
     * @param type
     *            the type of the top value on the stack
     */
    public void swap(Type underType, Type type)
    {
        if (ClassMakerFactory.LONG_TYPE.equals(type) || ClassMakerFactory.DOUBLE_TYPE.equals(type)) {
            if (ClassMakerFactory.LONG_TYPE.equals(underType) || ClassMakerFactory.DOUBLE_TYPE.equals(underType)) {
                cfw.add(ByteCode.DUP2_X2);
                cfw.add(ByteCode.POP2);
            } else {
                cfw.add(ByteCode.DUP2_X1);
                cfw.add(ByteCode.POP2);
            }
        } else {
            if (ClassMakerFactory.LONG_TYPE.equals(underType) || ClassMakerFactory.DOUBLE_TYPE.equals(underType)) {
                cfw.add(ByteCode.DUP_X2);
                cfw.add(ByteCode.POP);
            } else {
                cfw.add(ByteCode.SWAP);
            }
        }
    }

    //############# Increment & Decrement operators ###############

    /**** Inc ****/

    Value incLocal(MakerField local)
    {
        markLineNumber(); // possibly add a new line number entry.
        if (!incrementLocal(local, 1)) {
            throw new IllegalArgumentException("Cannot increment local field " + local.getName()
                    + " of type " + local.getType());
        }
        return loadLocal(local);
    }

    Value incField(MakerField field)
    {
        markLineNumber(); // possibly add a new line number entry.
        Type fieldType = field.getType();
        ClassType classType = field.getClassType();
        String className = classType.getName();
        String name = field.getName();

        // GET myclass.id
        dup(classType);
        cfw.add(ByteCode.GETFIELD, toSlashName(className), name, fieldType.getSignature());

        if (!increment(fieldType, 1)) {
            throw new IllegalArgumentException("Cannot increment member field " + field.getName()
                    + " of type " + field.getType());
        }
        dupunder(classType, fieldType);

        cfw.add(ByteCode.PUTFIELD, toSlashName(className), name, fieldType.getSignature());

        return fieldType.getValue();
    }

    Value incStatic(MakerField field)
    {
        markLineNumber(); // possibly add a new line number entry.
        Type fieldType = field.getType();
        String fieldName = field.getName();
        ClassType classType = field.getClassType();
        String className = classType.getName();

        markLineNumber(); // possibly add a new line number entry.
        // GET myclass.id
        cfw.add(ByteCode.GETSTATIC, toSlashName(className), fieldName, fieldType.getSignature());

        if (!increment(fieldType, 1)) {
            throw new IllegalArgumentException("Cannot increment static field " + field.getName()
                    + " of type " + field.getType());
        }
        dup(fieldType);

        cfw.add(ByteCode.PUTSTATIC, toSlashName(className), fieldName, fieldType.getSignature());

        return fieldType.getValue();
    }

    /**** Dec ****/

    Value decLocal(MakerField local)
    {
        markLineNumber(); // possibly add a new line number entry.
        if (!incrementLocal(local, -1)) {
            throw new IllegalArgumentException("Cannot decrement local field " + local.getName()
                    + " of type " + local.getType());
        }
        return loadLocal(local);
    }

    Value decField(MakerField field)
    {
        markLineNumber(); // possibly add a new line number entry.
        Type fieldType = field.getType();
        String fieldName = field.getName();
        ClassType classType = field.getClassType();
        String className = classType.getName();

        // GET myclass.id
        dup(classType);
        cfw.add(ByteCode.GETFIELD, toSlashName(className), fieldName, fieldType.getSignature());

        if (!increment(fieldType, -1)) {
            throw new IllegalArgumentException("Cannot decrement member field " 
                    + classType.getName() + "." + field.getName() + " of type " + field.getType());
        }
        dupunder(classType, fieldType);

        // PUT myclass.id
        cfw.add(ByteCode.PUTFIELD, toSlashName(className), fieldName, fieldType.getSignature());

        return fieldType.getValue();
    }

    Value decStatic(MakerField field)
    {
        markLineNumber(); // possibly add a new line number entry.
        Type fieldType = field.getType();
        String fieldName = field.getName();
        ClassType classType = field.getClassType();
        String className = classType.getName();

        markLineNumber(); // possibly add a new line number entry.
        // GET myclass.id
        cfw.add(ByteCode.GETSTATIC, toSlashName(className), fieldName, fieldType.getSignature());

        if (!increment(fieldType, -1)) {
            throw new IllegalArgumentException("Cannot decrement static field " 
                    + classType.getName() + "." + field.getName() + " of type " + field.getType());
        }
        dup(fieldType);

        cfw.add(ByteCode.PUTSTATIC, toSlashName(className), fieldName, fieldType.getSignature());

        return fieldType.getValue();
    }

    /**** PostInc ****/

    Value postIncLocal(MakerField local)
    {
        markLineNumber(); // possibly add a new line number entry.
        Value value = loadLocal(local);
        if (!incrementLocal(local, 1)) {
            throw new IllegalArgumentException("Cannot increment local field " + local.getName()
                    + " of type " + local.getType());
        }
        return value;
    }

    Value postIncField(MakerField field)
    {
        markLineNumber(); // possibly add a new line number entry.
        Type fieldType = field.getType();
        String fieldName = field.getName();
        ClassType classType = field.getClassType();
        String className = classType.getName();

        markLineNumber(); // possibly add a new line number entry.

        dup(classType);
        cfw.add(ByteCode.GETFIELD, toSlashName(className), fieldName, fieldType.getSignature());

        dupunder(classType, fieldType);
        if (!increment(fieldType, 1)) {
            throw new IllegalArgumentException("Cannot increment member field " 
                    + classType.getName() + "." + field.getName() + " of type " + field.getType());
        }
        // PUT myclass.id
        cfw.add(ByteCode.PUTFIELD, toSlashName(className), fieldName, fieldType.getSignature());

        return fieldType.getValue();
    }

    Value postIncStatic(MakerField field)
    {
        markLineNumber(); // possibly add a new line number entry.
        Type fieldType = field.getType();
        String fieldName = field.getName();
        ClassType classType = field.getClassType();
        String className = classType.getName();

        markLineNumber(); // possibly add a new line number entry.
        // GET myclass.id
        cfw.add(ByteCode.GETSTATIC, toSlashName(className), fieldName, fieldType.getSignature());

        dup(fieldType);
        if (!increment(fieldType, 1)) {
            throw new IllegalArgumentException("Cannot increment static field " 
                    + classType.getName() + "." + field.getName() + " of type " + field.getType());
        }
        cfw.add(ByteCode.PUTSTATIC, toSlashName(className), fieldName, fieldType.getSignature());

        return fieldType.getValue();
    }

    /**** PostDec ****/

    Value postDecLocal(MakerField local)
    {
        markLineNumber(); // possibly add a new line number entry.
        Value value = loadLocal(local);
        if (!incrementLocal(local, -1)) {
            throw new IllegalArgumentException("Cannot decrement local field " + local.getName()
                    + " of type " + local.getType());
        }
        return value;
    }

    Value postDecField(MakerField field)
    {
        Type fieldType = field.getType();
        String fieldName = field.getName();
        ClassType classType = field.getClassType();
        String className = classType.getName();

        markLineNumber(); // possibly add a new line number entry.
        // GET myclass.id
        dup(classType);
        cfw.add(ByteCode.GETFIELD, toSlashName(className), fieldName, fieldType.getSignature());

        dupunder(classType, fieldType);
        if (!increment(fieldType, -1)) {
            throw new IllegalArgumentException("Cannot decrement member field " 
                    + classType.getName() + "." + field.getName() + " of type " + field.getType());
        }
        // PUT myclass.id
        cfw.add(ByteCode.PUTFIELD, toSlashName(className), fieldName, fieldType.getSignature());

        return fieldType.getValue();
    }

    Value postDecStatic(MakerField field)
    {
        markLineNumber(); // possibly add a new line number entry.
        Type fieldType = field.getType();
        String fieldName = field.getName();
        ClassType classType = field.getClassType();
        String className = classType.getName();

        markLineNumber(); // possibly add a new line number entry.
        // GET myclass.id
        cfw.add(ByteCode.GETSTATIC, toSlashName(className), fieldName, fieldType.getSignature());

        dup(fieldType);
        if (!increment(fieldType, -1)) {
            throw new IllegalArgumentException("Cannot decrement static field " 
                    + classType.getName() + "." + field.getName() + " of type " + field.getType());
        }
        cfw.add(ByteCode.PUTSTATIC, toSlashName(className), fieldName, fieldType.getSignature());

        return fieldType.getValue();
    }

    /**** IncAt ****/

    protected boolean incrementAtIndex(ArrayType arrayType, Type indexType, Type elementType, int amount) {
        markLineNumber(); // possibly add a new line number entry.
        //# Stack contents
        //# array index
        swap(arrayType, indexType);
        //# index array
        dupunder(indexType, arrayType);
        //# array index array
        swap(indexType, arrayType);
        //# array array index
        dupunder(arrayType, indexType);
        //# array index array index
        getAtIndex(arrayType, indexType);
        //# array index value
        if (!increment(elementType, amount)) {
            return false;
        }
        //# array index value+1
        dup(elementType);
        //# array index value+1 value+1
        int slot = storeAnonymousValue(elementType);
        //# array index value+1
        setAtIndex(arrayType, indexType, elementType);
        //# -
        this.loadAnonymousValue(slot);
        //# value+1

        return true;
    }

    boolean postIncrementAtIndex(ArrayType arrayType, Type indexType, Type elementType, int amount) {
        markLineNumber(); // possibly add a new line number entry.
        //# Stack contents
        //# array index
        swap(arrayType, indexType);
        //# index array
        dupunder(indexType, arrayType);
        //# array index array
        swap(indexType, arrayType);
        //# array array index
        dupunder(arrayType, indexType);
        //# array index array index
        getAtIndex(arrayType, indexType);
        //# array index value
        dup(elementType);
        //# array index value value
        int slot = storeAnonymousValue(elementType);
        //# array index value
        if (!increment(elementType, amount)) {
            return false;
        }
        //# array index value+1
        setAtIndex(arrayType, indexType, elementType);
        //# -
        this.loadAnonymousValue(slot);
        //# value

        return true;
    }

    /**
     * Increments a local variable by the given amount.
     * 
     * @param local
     *            the variable to be incremented
     * @param amount
     *            the amount to increment the variable
     * @return <code>false</code> if value cannot be incremented
     */
    protected boolean incrementLocal(MakerField local, int amount)
    {
        if (isPrimitive(local.getType())) {
            PrimitiveType prim = local.getType().toPrimitive();
            switch (prim.index) {
            case PrimitiveType.BYTE_INDEX:
                cfw.addILoad(local.getSlot());
                cfw.addLoadConstant(amount);
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2B);
                cfw.addIStore(local.getSlot());
                return true;
            case PrimitiveType.SHORT_INDEX:
                cfw.addILoad(local.getSlot());
                cfw.addLoadConstant(amount);
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2S);
                cfw.addIStore(local.getSlot());
                return true;
            case PrimitiveType.CHAR_INDEX:
                cfw.addILoad(local.getSlot());
                cfw.addLoadConstant(amount);
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2C);
                cfw.addIStore(local.getSlot());
                return true;
            case PrimitiveType.INT_INDEX:
                cfw.add(ByteCode.IINC, local.getSlot(), amount);
                return true;
            case PrimitiveType.LONG_INDEX:
                cfw.addLLoad(local.getSlot());
                cfw.addLoadConstant((long) amount);
                cfw.add(ByteCode.LADD);
                cfw.addLStore(local.getSlot());
                return true;
            case PrimitiveType.FLOAT_INDEX:
                cfw.addFLoad(local.getSlot());
                cfw.addLoadConstant((float) amount);
                cfw.add(ByteCode.FADD);
                cfw.addFStore(local.getSlot());
                return true;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.addDLoad(local.getSlot());
                cfw.addLoadConstant((double) amount);
                cfw.add(ByteCode.DADD);
                cfw.addDStore(local.getSlot());
                return true;
            }
        }
        return false;
    }

    /**
     * Increments the value on top of the stack by the given amount
     * 
     * @param type
     *            the type of the value on top of the stack
     * @param amount
     *            the amount to increment the value
     * @return <code>false</code> if value cannot be incremented
     */
    protected boolean increment(Type type, int amount)
    {
        if (isPrimitive(type)) {
            markLineNumber(); // possibly add a new line number entry.

            switch (type.toPrimitive().index) {
            case PrimitiveType.BYTE_INDEX: // fall thru
                cfw.addLoadConstant(amount);
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2B);
                return true;
            case PrimitiveType.SHORT_INDEX: // fall thru
                cfw.addLoadConstant(amount);
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2S);
                return true;
            case PrimitiveType.CHAR_INDEX: // fall thru
                cfw.addLoadConstant(amount);
                cfw.add(ByteCode.IADD);
                cfw.add(ByteCode.I2C);
                return true;
            case PrimitiveType.INT_INDEX:
                cfw.addLoadConstant(amount);
                cfw.add(ByteCode.IADD);
                return true;
            case PrimitiveType.LONG_INDEX:
                cfw.addLoadConstant((long) amount);
                cfw.add(ByteCode.LADD);
                return true;
            case PrimitiveType.FLOAT_INDEX:
                cfw.addLoadConstant((float) amount);
                cfw.add(ByteCode.FADD);
                return true;
            case PrimitiveType.DOUBLE_INDEX:
                cfw.addLoadConstant((double) amount);
                cfw.add(ByteCode.DADD);
                return true;
            }
        }
        return false;
    }

    // Nameless local variables for storing intermediate values.

    /**
     * Adds a formal parameter or local variable to the method.
     * 
     * @param name name of the local variable
     * @param type type of the local variable
     * @param modifiers access modifiers for the variable
     * @param scopeLevel the level of nesting that determines when the variable is out of scope
     * @return index into <code>localTable</code>
     */
    int addLocal(String name, Type type, int modifiers, int scopeLevel)
    {
        MakerField field = new MakerField(name, type, modifiers);
        field.setSlot(maxLocalSlots);
        field.setScopeLevel(scopeLevel);
        // Adjust the number of slots used.
        maxLocalSlots += type.getSlotSize();
        if (getClassFileWriter() != null)
            field.setStartPC(cfw.getCurrentCodeOffset());
        int index = localTable.size();
        localTable.add(field);
        return index;
    }

    /**
     * Finds a local variable in the method.
     * 
     * @param name
     *            the name of the variable
     * @return a <code>Field</code> that describes the variable
     */
    MakerField findLocalField(String name)
    {
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
     * Stores a value in a nameless local variable.
     * </br>
     * The value may be retrieved using the slot offset.
     * The type determines how may slots are reserved.
     * 
     * @param type
     *            type of value being stored.
     */
    protected int storeAnonymousValue(Type type) throws ClassMakerException
    {
        int index = addLocal(null, type, 0, 0);
        MakerField local = lookupLocal(index);
        storeLocal(local, type);
        return index;
    }

    /**
     * Loads a value from a nameless local variable.
     * 
     * @param index
     *            the index of the anonymous field holding the value
     * @return type of the value being loaded
     */
    protected Value loadAnonymousValue(int index) throws ClassMakerException
    {
        MakerField local = lookupLocal(index);
        return loadLocal(local);
    }

    private void assertNotNull(Object obj, String name)
    {
        if (obj == null)
            throw new IllegalArgumentException(name + " cannot be null");
    }

    /**
     * Adds a line number entry to the generated class, if appropriate. <br/>
     * Called by methods that generate byte codes. A line number entry will
     * be added to the method being generated if the line number has changed
     * since the last time this method was called.
     */
    protected void markLineNumber()
    {
        int lineNumber = sourceLine.getLineNumber();
        if (lineNumber != previousLineNumber && lineNumber > 0) {
            cfw.addLineNumberEntry((short) lineNumber);
            previousLineNumber = lineNumber;
        }
        //checkInMethod();
        // For convenience we reset the followsReturn flag
        // because almost every method calls this method.
        //followsReturn = false;
    }

    /**
     * Saves the class to the given output folder. <br/>
     * The class will be placed in the appropriate path corresponding to the
     * package name.
     * 
     * @param classesDir
     *            the base folder for output classes
     * @throws IOException
     *             if the class file cannot be created
     * @return a File referring to the saved class file
     */
    public File saveClass(File classesDir) throws IOException
    {
        ClassFileWriter cfw = getClassFileWriter();
        String className = cfw.getClassName() + ".class";
        File classFile = new File(classesDir, className);
        File packageFile = classFile.getParentFile();
        if (!packageFile.exists()) {
            packageFile.mkdirs();
        }
        FileOutputStream output = new FileOutputStream(classFile);
        cfw.write(output);
        output.close();

        return classFile;
    }

    /**
     * Deletes the class from the given output folder. <br/>
     * The class will be deleted from the appropriate path corresponding to the
     * package name.
     * 
     * @param classesDir
     *            the base folder for output classes
     * @throws IOException
     *             if the class file cannot be created
     */
    public void deleteClass(File classesDir, String className) throws IOException
    {
        String fileName = toSlashName(className) + ".class";
        File classFile = new File(classesDir, fileName);
        if (classFile.exists()) {
            if (!classFile.delete())
                throw new IOException("Could not delete: " + classFile.getAbsolutePath());
        }
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("ClassGenerator(");
        buf.append(cfw.getClassName());
        if (sourceLine != null)
            buf.append(", ").append(sourceLine.getFilename()).append(":").append(sourceLine.getLineNumber());
        buf.append(')');
        return buf.toString();
    }
}
