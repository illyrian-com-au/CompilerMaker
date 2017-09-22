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

import java.util.HashMap;
import java.util.Map;

import org.mozilla.classfile.SimpleClassLoader;

import au.com.illyrian.classmaker.converters.AssignmentConversion;
import au.com.illyrian.classmaker.converters.CastingConversion;
import au.com.illyrian.classmaker.converters.MethodInvocationConversion;
import au.com.illyrian.classmaker.converters.NumericPromotion;
import au.com.illyrian.classmaker.converters.StringConversion;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.members.MethodResolver;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.util.MakerUtil;

/**
 * The ClassMakerFactory contains services that are shared between all ClassMaker instances.
 * <br/>
 * It contains a map of type descriptors,
 * a factory for generating Exceptions and
 * a common class loader.
 * <br/>
 * The ClassMakerFactory contains a set of strategies for the following conversions and promotions.
 * <UL>
 * <LI><b>Assignment Conversion</b> which is applied when the result of an expression is
 * assigned to a storage location</LI>
 * <LI><b>Method Invocation Conversion</b> which is applied to each actual parameter of a method call</LI>
 * <LI><b>Numeric Promotion Conversion</b> which is applied to the operands of unary and binary operators</LI>
 * <LI><b>Casting Conversion</b> which is applied explicitly to a type</LI>
 * <LI><b>String Conversion</b> which is applied when either operand of an Add operator is a String</LI>
 * </UL>
 *
 * @author dstrong
 */
public class ClassMakerFactory
{
    private SimpleClassLoader loader = null;
    private HashMap<String, Type>         typeMap = new HashMap<String, Type>();
    private HashMap<String, ClassMaker> classMakerMap = new HashMap<String, ClassMaker>();

    /** An empty prototype array of <code>Type</code> that may be provided to <code>Collection.toArray(Object[])</code>. */
    public static final Type[] TYPE_ARRAY = new Type[0];
    /** An empty prototype array of <code>ClassType</code>  that may be provided to <code>Collection.toArray(Object[])</code>. */
    public static final ClassType[] CLASS_TYPE_ARRAY = new ClassType[0];
    /** An empty prototype array of <code>MakerMethod</code> that may be provided to <code>Collection.toArray(Object[])</code>. */
    public static final MakerMethod[] METHOD_ARRAY = new MakerMethod[0];
    /** An empty prototype array of <code>MakeField</code> that may be provided to <code>Collection.toArray(Object[])</code>. */
    public static final MakerField[] FIELD_ARRAY = new MakerField[0];

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

    public static final PrimitiveType VOID_TYPE = new PrimitiveType(PrimitiveType.VOID_INDEX, "void", "V", void.class);
    /** Reference to <code>byte</code> type */
    public static final PrimitiveType BYTE_TYPE = new PrimitiveType(PrimitiveType.BYTE_INDEX, "byte", "B", byte.class);
    /** Reference to <code>char</code> type */
    public static final PrimitiveType CHAR_TYPE = new PrimitiveType(PrimitiveType.CHAR_INDEX, "char", "C", char.class);
    /** Reference to <code>double</code> type */
    public static final PrimitiveType DOUBLE_TYPE = new PrimitiveType(PrimitiveType.DOUBLE_INDEX, "double", "D", double.class);
    /** Reference to <code>float</code> type */
    public static final PrimitiveType FLOAT_TYPE = new PrimitiveType(PrimitiveType.FLOAT_INDEX, "float", "F", float.class);
    /** Reference to <code>int</code> type */
    public static final PrimitiveType INT_TYPE = new PrimitiveType(PrimitiveType.INT_INDEX, "int", "I", int.class);
    /** Reference to <code>long</code> type */
    public static final PrimitiveType LONG_TYPE = new PrimitiveType(PrimitiveType.LONG_INDEX, "long", "J", long.class);
    /** Reference to <code>short</code> type */
    public static final PrimitiveType SHORT_TYPE = new PrimitiveType(PrimitiveType.SHORT_INDEX, "short", "S", short.class);
    /** Reference to <code>boolean</code> type */
    public static final PrimitiveType BOOLEAN_TYPE = new PrimitiveType(PrimitiveType.BOOLEAN_INDEX, "boolean", "Z", boolean.class);

    /** Discriminator for anonymous classes */
    private int anonomousClass = 0;

    /** The current phase of a two pass class generation */
    private int generationPass = ClassMakerConstants.ONE_PASS;

    private ExceptionFactory exceptionFactory = null;

    private MethodInvocationConversion methodInvocationConversion;
    private MethodResolver methodResolver;
    private AssignmentConversion assignmentConversion;
    private CastingConversion castingConversion;
    private NumericPromotion numericPromotion;
    private StringConversion stringConversion;

    /**
     * Default Constructor for ClassMakerFactory.
     *
     * Defines primitive types and standard classes.
     */
    public ClassMakerFactory()
    {
        addPrimitives();
        addStandardClasses();
    }

    /**
     * Create a ClassMaker instance using this factory.
     * @return a ClassMaker instance.
     */
    public ClassMaker createClassMaker()
    {
        return new ClassMaker(this);
    }

    /**
     * Creates a <code>ClassMaker</code> instance for generating the named
     * class.
     * The <code>ClassMakerFactory</code> provides shared services for all
     * instances of <code>ClassMaker</code>.
     * 
     * @param globalFactory the <code>ClassMakerFactory</code> instance that is used by
     *            all class generators
     * @param className the name of the class to be generated
     * @param extendsClass the class that the generated class will extend
     * @param sourceFile an optional source file name
     */
    public ClassMaker createClassMaker(String packageName, String simpleName, String sourceFile)
    {
        ClassMaker maker = new ClassMaker(this);
        maker.setPackageName(packageName);
        maker.setSimpleClassName(simpleName);
        maker.setSourceFilename(sourceFile);
        maker.getClassType();
        return maker;
    }
    
    /**
     * Set the current pass for the class generator.
     * </br>
     * The following are valid options.
     * <ul>
     * <li><code>ClassMaker.ONE_PASS</code></li>
     * <li><code>ClassMaker.FIRST_PASS</code></li>
     * <li><code>ClassMaker.SECOND_PASS</code></li>
     * </ul>
     * The default is <code>ClassMaker.ONE_PASS</code>.
     * @param pass the pass for the class generator
     */
    public void setPass(int pass)
    {
        generationPass = pass;
    }

    /**
     * Get the current pass for the class generator.
     * </br>
     * The following are valid options.
     * <ul>
     * <li><code>ClassMaker.ONE_PASS</code></li>
     * <li><code>ClassMaker.FIRST_PASS</code></li>
     * <li><code>ClassMaker.SECOND_PASS</code></li>
     * <li><code>ClassMaker.COMPLETED_PASS</code></li>
     * </ul>
     * <code>COMPLETED_PASS</code> indicates that the <code>ClassMaker.defineClass()</code> method can be called.
     */
    public int getPass()
    {
        return generationPass;
    }

    /**
     * Create a new ClassLoader instance.
     * @return the shared class loader
     */
    public SimpleClassLoader createClassLoader()
    {
        return new SimpleClassLoader();
    }

    /**
     * The ClassLoader used by all ClassMakers that share this factory.
     * @return the shared class loader
     */
    public SimpleClassLoader getClassLoader()
    {
        // FIXME - Do not use mozilla ClassLoader.
        if (loader == null)
            loader = createClassLoader();
        return loader;
    }
    /**
     * Sets the ClassLoader used by all ClassMakers that share this factory.
     * @param classLoader a shared class loader
     */
    public void setClassLoader(SimpleClassLoader classLoader)
    {
    	loader = classLoader;
    }

    /**
     * The ExceptionFactory to be used by all ClassMakers that share this factory.
     */
    public ExceptionFactory getExceptionFactory()
    {
        if (exceptionFactory == null)
            exceptionFactory = new ExceptionFactory();
        return exceptionFactory;
    }

    private Type getType(String name)
    {
        return typeMap.get(name);
    }

    public ClassMaker findClassMaker(String className) {
        return classMakerMap.get(className);
    }
    
    public void addClassMaker(ClassMaker maker) {
        String className = maker.defaultFullyQualifiedClassName();
        classMakerMap.put(className, maker);
    }

    public int incAnonomousClass()
    {
        return anonomousClass++;
    }

    /** Adds all the standard PrimitiveTypes */
    protected void addPrimitives()
    {
        addType(VOID_TYPE);
        addType(BYTE_TYPE);
        addType(CHAR_TYPE);
        addType(DOUBLE_TYPE);
        addType(FLOAT_TYPE);
        addType(INT_TYPE);
        addType(LONG_TYPE);
        addType(SHORT_TYPE);
        addType(BOOLEAN_TYPE);
    }

    /** Adds important Types representing standard java classes */
    protected void addStandardClasses()
    {
        // Prime the first objects in classTypeToString
        addClassType(NULL_TYPE);
        addClassType(OBJECT_TYPE);
        addClassType(STRING_TYPE);
        addClassType(AUTO_STRING_TYPE);
        addClassType(STRING_BUFFER_TYPE);
        addClassType(CLONEABLE_TYPE);
        addClassType(THROWABLE_TYPE);
        addClassType(CLASS_TYPE);
    }

    /**
     * Adds the Type to the type map.
     * <br/>
     * @param type the Type to be mapped for future lookups 
     */
    protected void addType(Type type)
    {
        String name = type.getName();
        typeMap.put(name, type);
   }

    /**
     * Adds the ClassType to the type map.
     * <br/>
     * @param type the ClassType to be mapped for future lookups 
     */
    protected void addClassType(ClassType classType)
    {
        classType.setFactory(this);
        addType(classType);
    }

    /**
     * Adds a ClassType to the type map.
     * <br/>
     * A ClassType wrapper is created around the java class.
     * Information about the java class, such as implemented fields and methods,
     * will be lazy loaded into the ClassType as required.
     * @param javaClass the java class to be added
     * @return a ClassType that has been added to the Type map
     */
    protected ClassType addClassType(Class javaClass)
    {
        if (javaClass.isPrimitive()) {// Should not get here
            throw new IllegalArgumentException(javaClass.getName() + " is not a class");
        }
        ClassType type = new ClassType(javaClass);
        type.setFactory(this);
        addType(type);
        return type;
    }
    
    /**
     * Fetches the ArrayType that holds the given element Type.
     * </br>
     * Creates the ArrayType if it does not exist.
     * @param arrayOfType the Type of the elements in the array
     * @return an ArrayType that is included in the Type map
     */
    public ArrayType typeToArray(Type arrayOfType)
    {
        String arrayName = arrayOfType.getName() + "[]";
        Type type = stringToType(arrayName);
        if (type != null && type.toArray() != null) {
            return type.toArray();
        } else {
            return addArrayOfType(arrayOfType);
        }
    }

    /**
     * Creates a new ArrayType to hold elements of the given Type.
     * @param arrayOfType the Type of the elements in the array
     * @return an ArrayType that has been added to the Type map
     */
    protected ArrayType addArrayOfType(Type arrayOfType)
    {
        String typeName = arrayOfType.getName();
        String name = typeName + "[]";
        String signature = "[" + arrayOfType.getSignature();
        ArrayType element = new ArrayType(name, signature, arrayOfType);
        addType(element);
        return element;
    }

    /**
     * Adds an ArrayType to the type map.
     * </br>
     * An ArrayType wrapper is created around the java class that represents the array.
     * @param javaClass the java class for the array to be added
     * @return an ArrayType that has been added to the Type map
     */
    protected ArrayType addArrayType(Class javaClass)
    {
        String name = MakerUtil.classToName(javaClass);
        String signature = MakerUtil.classToSignature(javaClass);
        Type element = classToType(javaClass.getComponentType());
        ArrayType array = new ArrayType(name, signature, element);
        array.setJavaClass(javaClass);
        addType(array);
        return array;
    }

    /**
     * Fetches the Type associated with the given java class, creating it if necessary.
     * @param javaClass the java class to be looked up
     * @return a Type corresponding to the java class
     */
    public Type classToType(Class javaClass)
    {
        if (javaClass == null) {
            throw new NullPointerException("Expected a java Class instance");
        }
        String className = MakerUtil.classToName(javaClass);
        Type type = getType(className);
        if (type != null) {
            return type;
        } else if (javaClass.isArray()) {
            return addArrayType(javaClass);
        } else {
            return addClassType(javaClass);
        }
    }
    
    /**
     * Loads a <code>Class</code> using the factory <code>ClassLoader</code>.
     * @param className the fully qualified name of the class
     * @return the loaded <code>Class</code>
     */
    private Type loadClass(String className) throws ClassMakerException
    {
        try
        {
            String name = MakerUtil.toDotName(className);
            Class javaClass = getClassLoader().loadClass(name);
            return classToType(javaClass);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    /**
     * Loads a <code>Class</code> using the factory <code>ClassLoader</code>.
     * @param className the fully qualified name of the class
     * @return the loaded <code>Class</code>
     */
    private Type lookupDefaultClassTypes(String className) throws ClassMakerException
    {
    	Type type = null;
    	// is simple class name?
    	if (className.indexOf('.') == -1)
    	{
    	    type = loadClass("java.lang." + className);
    	    if (type != null) {
    	        // Add another alias using the short name for the class.
    	        addType(type);
    	    }
    	}
    	return type;
    }
    
    /**
     * Fetches a Type from the type map using a fully qualified class name
     * @param typeName the fully qualified class name
     * @return the corresponding type
     */
    public Type stringToType(String typeName)
    {
        Type type = getType(typeName);
        if (type == null)
        {   // Load the class from the classpath.
            type = loadClass(typeName);
            if (type == null)
            	type = lookupDefaultClassTypes(typeName);
        }
        return type;
    }
    
    /**
     * Creates a <code>ClassType</code> populated with methods declared in the given java class.
     * </br>
     * Creates and returns a ClassType if one does not already exist for the java class.
     * Creates a <code>MakerMethod</code> for each method declared in the java class.
     * @param classType the ClassType wrapper around the java class, if one exists
     * @param javaClass the java class from which to derive the methods
     * @return the ClassType wrapper around the given java class
     */
//    public ClassType populateJavaClassMethods(ClassType classType, Class javaClass)
//    {
//    	if (classType == null && javaClass != null)
//    		classType = classToType(javaClass).toClass();
//
//    	if (classType.getAllMethods() == null)
//    	{
//    	    HashMap<String, MakerMethod> methods = new HashMap<String, MakerMethod>();
//    	    while (javaClass != null) {
//	    	java.lang.reflect.Method [] javaMethods = javaClass.getDeclaredMethods();
//	        for (int i = 0; i < javaMethods.length; i++)
//	        {
//	            java.lang.reflect.Method javaMethod = javaMethods[i];
//	            int modifiers = javaMethod.getModifiers();
//	            if (!Modifier.isPrivate(modifiers)) {
//                        MakerMethod method = toMethod(classType, javaMethod);
//                        String signature = method.getName() + method.getSignature();
//                        // Only add methods that are not overridden
//                        if (methods.get(signature) == null) {
//                            methods.put(signature, method);
//                        }
//	            }
//	        }
//	        javaClass = javaClass.getSuperclass();
//    	    }
//            classType.setAllMethods(methods.values().toArray(METHOD_ARRAY));
//    	}
//        return classType;
//    }

    public MakerMethod [] getDeclaredMethods(ClassType classType)
    {
        Class javaClass = classType.getJavaClass();
        java.lang.reflect.Method [] javaMethods = javaClass.getDeclaredMethods();
        int len = javaMethods.length;
        MakerMethod [] methods = new MakerMethod[len];
        for (int i = 0; i < len; i++)
        {
            java.lang.reflect.Method javaMethod = javaMethods[i];
            MakerMethod method = toMethod(classType, javaMethod);
            methods[i] = method;
        }
        return methods;
    }

    /**
     * Populates a <code>ClassType</code> with the interfaces declared in the given java class.
     * </br>
     * Finds or creates a <code>ClassType</code> for each interface, if one does not already exist,
     * then populates each of the <code>ClassType</code>s.
     * @param classType the ClassType wrapper around the java class
     * @param javaClass the java class from which to derive the methods
     */
//    public void populateJavaClassInterfaces(ClassType classType, Class javaClass)
//    {
//        if (classType.getDeclaredInterfaces() == null && javaClass != null) {
//            Class[] javaInterfaces = javaClass.getInterfaces();
//            ClassType[] interfaces = new ClassType[javaInterfaces.length];
//            for (int i = 0; i < javaInterfaces.length; i++) {
//                Class ifaceClass = javaInterfaces[i];
//                ClassType ifaceType = classToType(ifaceClass).toClass();
//                interfaces[i] = ifaceType;
//            }
//            classType.setDeclaredInterfaces(interfaces);
//        }
//    }

    public ClassType[] getDeclaredInterfaces(ClassType classType)
    {
        Class javaClass = classType.getJavaClass();
        Class[] javaInterfaces = javaClass.getInterfaces();
        ClassType[] interfaces = new ClassType[javaInterfaces.length];
        for (int i = 0; i < javaInterfaces.length; i++) {
            Class ifaceClass = javaInterfaces[i];
            ClassType ifaceType = classToType(ifaceClass).toClass();
            interfaces[i] = ifaceType;
        }
        return interfaces;
    }
    
    /**
     * Fetches the methods in the given 
     *
     * The methods are lazy loaded for existing java classes or the current list
     * of methods is used for the class being generated.
     * @param classType the ClassType that holds information about the class
     * @return an array of method descriptors
     */
//    public MakerMethod [] getMethods(ClassType classType)
//    {
//        HashMap<String, MakerMethod> candidates = new HashMap<String, MakerMethod>();
//        findJavaClassMethods(candidates, classType);
//        if (classType.isInterface())
//            findJavaInterfaceMethods(candidates, classType);
//        return candidates.values().toArray(METHOD_ARRAY);
//    }

    /**
     * Creates an array of method descriptors for the given java class.
     * </br>
     * All methods and constructors for the class are returned.
     * Private methods in base classes are filtered out.
     * @param candidates the map of candidates from <code>String</code> to <code>MakerMethod</code>
     * @param classType the ClassType that holds information about the class
     */
//    public void findJavaClassMethods(HashMap<String, MakerMethod> candidates, ClassType classType)
//    {
//        if (classType != null)
//        {
//            populateJavaClassMethods(classType, classType.getJavaClass());
//            ClassMakerFactory.addMethods(candidates, classType.getAllMethods());
//
//            findJavaClassMethods(candidates, classType.getExtendsType());
//        }
//    }

    /**
     * Creates an array of method descriptors for the given java interface.
     * </br>
     * All methods for the interface are returned.
     * @param candidates the map of candidates from <code>String</code> to <code>MakerMethod</code>
     * @param classType the ClassType that holds information about the class
     */
//    public void findJavaInterfaceMethods(HashMap<String, MakerMethod> candidates, ClassType classType)
//    {
//        for (ClassType interfaceType : classType.getDeclaredInterfaces()) {
//            ClassMakerFactory.addMethods(candidates, interfaceType.getDeclaredMethods());
//            findJavaInterfaceMethods(candidates, interfaceType);
//        }
//    }

    /**
     * Creates an array of constructor descriptors for the given java class.
     * </br>
     * Only constructors in the most explicit class are returned.
     * @param classType the ClassType that holds information about the class
     */
    public MakerMethod [] getDeclaredConstructors(ClassType classType)
    {
        Class javaClass = classType.getJavaClass();
        if (javaClass == null) {
            throw new NullPointerException("javaClass not set");
        }
        java.lang.reflect.Constructor [] construct = javaClass.getDeclaredConstructors();
        MakerMethod [] constructors = new MakerMethod[construct.length];
        for (int i = 0; i < construct.length; i++)
        {
            java.lang.reflect.Constructor javaMethod = construct[i];
            constructors[i] = toMethod(classType, javaMethod);
        }
        return constructors;
    }
    
    /**
     * Adds a <code>MakerMethod</code> to a lookup map.
     * @param allMethods the map from <code>String</code> to <code>MakerMethod</code>
     * @param method the <code>MakerMethod</code> to be added
     */
    static void addMethod(Map<String, MakerMethod> allMethods, MakerMethod method)
    {
        String key = method.toShortString();
        if (!allMethods.containsKey(key)) {
            allMethods.put(key, method);
        }
    }
    
    /**
     * Adds an array of <code>MakerMethod</code> to a lookup map.
     * @param allMethods the map from <code>String</code> to <code>MakerMethod</code>
     * @param methods the array of <code>MakerMethod</code>s
     */
    static void addMethods(Map<String, MakerMethod> allMethods, MakerMethod[] methods)
    {
        if (methods != null) {
            for (MakerMethod method : methods) {
                addMethod(allMethods, method);
            }
        }
    }

//    static void addInterfaceMethods(Map<String, MakerMethod> allMethods, ClassType [] interfaces)
//    {
//        // Add interface methods
//        for (ClassType ifaceType : interfaces)
//        {
//            if (ifaceType == null)
//                throw new IllegalArgumentException("Type must refer to an interface");
//            addMethods(allMethods, ifaceType.getDeclaredMethods());
//            // Recursively add methods from extended interfaces
//            addInterfaceMethods(allMethods, ifaceType.getDeclaredInterfaces());
//        }
//    }

    /**
     * Convert a reflection java method into a ClassMaker method descriptor.
     * @param javaMethod the java method to be converted
     * @return the corresponding ClassMaker method descriptor
     */
    protected MakerMethod toMethod(ClassType classType, java.lang.reflect.Method javaMethod)
    {
        String name = javaMethod.getName();
        short modifiers = (short)javaMethod.getModifiers();
        Type returnType = classToType(javaMethod.getReturnType());
        MakerMethod method = new MakerMethod(classType, name, returnType, modifiers);
        Class[] params = javaMethod.getParameterTypes();
        Type [] formalParams = new Type[params.length];
        for (int i=0; i<params.length; i++)
        {
            Type param = classToType(params[i]);
            formalParams[i] = param;
        }
        method.setFormalParams(formalParams);
        return method;
    }

    /**
     * Convert a reflection java constructor into a ClassMaker method descriptor.
     * @param javaMethod the java constructor to be converted
     * @return the corresponding ClassMaker method descriptor
     */
    protected MakerMethod toMethod(ClassType classType, java.lang.reflect.Constructor javaMethod)
    {
        short modifiers = (short)javaMethod.getModifiers();
        MakerMethod method = new MakerMethod(classType, ClassMaker.INIT, VOID_TYPE, modifiers);
        Class[] params = javaMethod.getParameterTypes();
        Type [] formalParams = new Type[params.length];
        for (int i=0; i<params.length; i++)
        {
            Type param = classToType(params[i]);
            formalParams[i] = param;
        }
        method.setFormalParams(formalParams);
        return method;
    }

     /**
     * Fetch an array of field descriptors for the given java class.
     * @param classType the class from which to extract field descriptors
     * @return an array of field descriptors
     */
//    protected MakerField[] populateJavaClassFields(ClassType classType)
//    {
//        Class javaClass = classType.getJavaClass();
//        //MakerField[] makerFields = new MakerField[javaFields.length];
//        ArrayList<MakerField> makerFields = new ArrayList<MakerField>();
//        while (javaClass != null) {
//            java.lang.reflect.Field[] javaFields = javaClass.getDeclaredFields();
//            for (int i = 0; i < javaFields.length; i++)
//            {
//                String name = javaFields[i].getName();
//                Class fieldType = javaFields[i].getType();
//                Type type = classToType(fieldType);
//                int modifiers = javaFields[i].getModifiers();
//                makerFields.add(new MakerField(classType, name, type, modifiers));
//            }
//            javaClass = javaClass.getSuperclass();
//        }
//        return makerFields.toArray(FIELD_ARRAY);
//    }

    public MakerField[] getDeclaredFields(ClassType classType)
    {
        Class javaClass = classType.getJavaClass();
        java.lang.reflect.Field[] javaFields = javaClass.getDeclaredFields();
        MakerField[] makerFields = new MakerField[javaFields.length];
        for (int i = 0; i < javaFields.length; i++)
        {
            String name = javaFields[i].getName();
            Class fieldType = javaFields[i].getType();
            Type type = classToType(fieldType);
            int modifiers = javaFields[i].getModifiers();
            MakerField field = new MakerField(classType, name, type, modifiers);
            makerFields[i] = field;
        }
        return makerFields;
    }

    //################## Conversion Strategies #####################

    /**
     * The Method Invocation Conversion strategy is shared between all classes being generated.
     */
    public MethodInvocationConversion getMethodInvocationConversion()
    {
        if (methodInvocationConversion == null)
            methodInvocationConversion = new MethodInvocationConversion(this);

        return methodInvocationConversion;
    }

    /**
     * The Method Resolver strategy is shared between all classes being generated.
     */
    public MethodResolver getMethodResolver()
    {
        if (methodResolver == null)
           methodResolver = new MethodResolver(this);

        return methodResolver;
    }

    /**
     * The Assignment Conversion strategy is shared between all classes being generated.
     */
    public AssignmentConversion getAssignmentConversion()
    {
        if (assignmentConversion == null)
            assignmentConversion = new AssignmentConversion(this);
        return assignmentConversion;
    }

    /**
     * The Casting Conversion strategy is shared between all classes being generated.
     */
    public CastingConversion getCastingConversion()
    {
        if (castingConversion == null)
              castingConversion = new CastingConversion(this);
        return castingConversion;
    }

    /**
     * The Numeric Promotion strategy is shared between all classes being generated.
     */
    public NumericPromotion getNumericPromotion()
    {
        if (numericPromotion == null)
            numericPromotion = new NumericPromotion(this);
        return numericPromotion;
    }

    /**
     * The String Conversion strategy is shared between all classes being generated.
     */
    public StringConversion getStringConversion()
    {
        if (stringConversion == null)
            stringConversion = new StringConversion(this);
        return stringConversion;
    }
}
