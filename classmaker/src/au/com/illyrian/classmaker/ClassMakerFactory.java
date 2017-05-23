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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.DeclaredTypeMaker;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;

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
    private HashMap<String, DeclaredType> declaredMap = new HashMap<String, DeclaredType>();

    /** An empty prototype array of <code>Type</code> that may be provided to <code>Collection.toArray(Object[])</code>. */
    public static final Type[] TYPE_ARRAY = new Type[0];
    /** An empty prototype array of <code>ClassType</code>  that may be provided to <code>Collection.toArray(Object[])</code>. */
    public static final DeclaredType[] DECLARED_TYPE_ARRAY = new DeclaredType[0];
    public static final ClassType[] CLASS_TYPE_ARRAY = new ClassType[0];
    /** An empty prototype array of <code>MakerMethod</code> that may be provided to <code>Collection.toArray(Object[])</code>. */
    public static final MakerMethod[] METHOD_ARRAY = new MakerMethod[0];
    /** An empty prototype array of <code>MakeField</code> that may be provided to <code>Collection.toArray(Object[])</code>. */
    public static final MakerField[] FIELD_ARRAY = new MakerField[0];

    /** Discriminator for anonymous classes */
    private int anonomousClass = 0;

    /** The current phase of a two pass class generation */
    private int generationPass = ClassMaker.ONE_PASS;

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
     * Create a ClassMaker instance using this factory and the given LineNumber interface.
     * The <code>LineNumber</code> interface provides the source file name and current line number for
     * debugging and error messages.
     * @param sourceLine returns the current source file and line number for errors and debugging.
     * @return a ClassMaker instance.
     */
    public ClassMaker createClassMaker(SourceLine sourceLine)
    {
        return new ClassMaker(this, sourceLine);
    }

    /**
     * Creates a <code>ClassMaker</code> instance for generating the named class.
     * @param className the name of the class to be generated
     * @param extendsClass the class that the generated class will extend
     * @param sourceFile an optional source file name
     */
    public ClassMaker createClassMaker(String packageName, String simpleName, String sourceFile)
    {
        return new ClassMaker(this, packageName, simpleName, sourceFile);
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
     * </ul>
     * The default is <code>ClassMaker.ONE_PASS</code>.
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

    protected Type putType(String name, Type type)
    {
        return typeMap.put(name, type);
    }

    private DeclaredType getDeclaredType(String name)
    {
        return declaredMap.get(name);
    }

    private DeclaredType putDeclaredType(String name, DeclaredType declared)
    {
        Type type = getType(declared.getName());
        if (type == null)
            throw new IllegalArgumentException("DeclaredType is unknown as Type: " + declared.getName());
        return declaredMap.put(name, declared);
    }

    public int incAnonomousClass()
    {
        return anonomousClass++;
    }

    /** Adds all the standard PrimitiveTypes */
    protected void addPrimitives()
    {
        addTypeAndDeclaredType(PrimitiveType.VOID_TYPE);
        addTypeAndDeclaredType(PrimitiveType.BYTE_TYPE);
        addTypeAndDeclaredType(PrimitiveType.CHAR_TYPE);
        addTypeAndDeclaredType(PrimitiveType.DOUBLE_TYPE);
        addTypeAndDeclaredType(PrimitiveType.FLOAT_TYPE);
        addTypeAndDeclaredType(PrimitiveType.INT_TYPE);
        addTypeAndDeclaredType(PrimitiveType.LONG_TYPE);
        addTypeAndDeclaredType(PrimitiveType.SHORT_TYPE);
        addTypeAndDeclaredType(PrimitiveType.BOOLEAN_TYPE);
    }

    /** Adds important Types representing standard java classes */
    protected void addStandardClasses()
    {
        // Prime the first objects in classTypeToString
        addTypeAndDeclaredType(ClassType.NULL_TYPE);
        addTypeAndDeclaredType(ClassType.OBJECT_TYPE);
        addTypeAndDeclaredType(ClassType.STRING_TYPE);
        addTypeAndDeclaredType(ClassType.AUTO_STRING_TYPE);
        addTypeAndDeclaredType(ClassType.STRING_BUFFER_TYPE);
        addTypeAndDeclaredType(ClassType.CLONEABLE_TYPE);
        addTypeAndDeclaredType(ClassType.THROWABLE_TYPE);
        addTypeAndDeclaredType(ClassType.CLASS_TYPE);
    }

    /**
     * Adds a primitive type to the type map.
     * @param index an index for the primitive used in case statements
     * @param name the name of the primitive type
     * @param signature the JVM signature of the primitive type
     * @param javaClass the java class for the primitive type
     * @return a PrimitiveType that has been added to the Type map
     */
    protected PrimitiveType addPrimitiveType(int index, String name, String signature, Class javaClass)
    {
        PrimitiveType prim = new PrimitiveType(index, name, signature, javaClass);
        addTypeAndDeclaredType(prim);
        return prim;
    }

    /**
     * Adds the Type to the type map and maps an equivalent DeclaredType.
     * <br/>
     * @param type the Type to be mapped for future lookups 
     */
    protected void addTypeAndDeclaredType(Type type)
    {
        String name = type.getName();
        putType(name, type);
        DeclaredType declared = new DeclaredType(type);
        putDeclaredType(name, declared);
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
        if (javaClass.isPrimitive()) // Should not get here
            throw new IllegalArgumentException(javaClass.getName() + " is not a class");
        ClassType type = new ClassType(javaClass);
        addTypeAndDeclaredType(type);
        return type;
    }
    
    protected DeclaredTypeMaker createDeclaredTypeMaker(ClassMaker maker)
    {
        DeclaredTypeMaker declared = new DeclaredTypeMaker(maker);
        // Bypass check that Type exists.
        declaredMap.put(maker.getFullyQualifiedClassName(), declared);
        return declared;
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
        if (type != null && type.toArray() != null)
            return type.toArray();
        else
            return addArrayOfType(arrayOfType);
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
        putType(name, element);
        DeclaredType declared = new DeclaredType(element);
        putDeclaredType(name, declared);
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
        String name = ClassMaker.classToName(javaClass);
        String signature = ClassMaker.classToSignature(javaClass);
        Type element = classToType(javaClass.getComponentType());
        ArrayType array = new ArrayType(name, signature, element);
        putType(name, array);
        DeclaredType declared = new DeclaredType(array);
        putDeclaredType(name, declared);
        return array;
    }

    /**
     * Fetches the Type associated with the given java class, creating it if necessary.
     * @param javaClass the java class to be looked up
     * @return a Type corresponding to the java class
     */
    public Type classToType(Class javaClass)
    {
        String className = ClassMaker.classToName(javaClass);
        Type type = getType(className);
        if (type != null)
            return type;
        else if (javaClass.isArray())
            return addArrayType(javaClass);
        else
            return addClassType(javaClass);
    }
    
    public DeclaredType classToDeclaredType(Class javaClass)
    {
        Type type = classToType(javaClass);
        DeclaredType declared = getDeclaredType(type.getName());
        return declared;
    }
    
    public DeclaredType typeToDeclaredType(Type type)
    {
        DeclaredType declared = getDeclaredType(type.getName());
        if (type != declared.getType())
            throw new IllegalStateException(declared + " does not contain the same instance of " + type);
        return declared;
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
            String name = ClassMaker.toDotName(className);
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
    	    if (type != null)
    	        // Add another alias using the short name for the class.
    	        putDeclaredType(className, new DeclaredType(type));
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
    
    public DeclaredType stringToDeclaredType(String typeName)
    {
        DeclaredType declared = getDeclaredType(typeName);
        if (declared == null)
        {
            if (stringToType(typeName) != null)
                declared = getDeclaredType(typeName);
        }
        return declared;
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
    public ClassType populateJavaClassMethods(ClassType classType, Class javaClass)
    {
    	if (classType == null && javaClass != null)
    		classType = classToType(javaClass).toClass();

    	if (classType.getMethods() == null)
    	{
    	    HashMap<String, MakerMethod> methods = new HashMap<String, MakerMethod>();
    	    while (javaClass != null) {
	    	java.lang.reflect.Method [] javaMethods = javaClass.getDeclaredMethods();
	        for (int i = 0; i < javaMethods.length; i++)
	        {
	            java.lang.reflect.Method javaMethod = javaMethods[i];
	            int modifiers = javaMethod.getModifiers();
	            if (!Modifier.isPrivate(modifiers)) {
                        MakerMethod method = toMethod(classType, javaMethod);
                        String signature = method.getName() + method.getSignature();
                        // Only add methods that are not overridden
                        if (methods.get(signature) == null) {
                            methods.put(signature, method);
                        }
	            }
	        }
	        javaClass = javaClass.getSuperclass();
    	    }
            classType.setMethods(methods.values().toArray(METHOD_ARRAY));
    	}
        return classType;
    }

    /**
     * Populates a <code>ClassType</code> with the interfaces declared in the given java class.
     * </br>
     * Finds or creates a <code>ClassType</code> for each interface, if one does not already exist,
     * then populates each of the <code>ClassType</code>s.
     * @param classType the ClassType wrapper around the java class
     * @param javaClass the java class from which to derive the methods
     */
    public void populateJavaClassInterfaces(ClassType classType, Class javaClass)
    {
        if (classType.getInterfaces() == null && javaClass != null) {
            Class[] javaInterfaces = javaClass.getInterfaces();
            ClassType[] interfaces = new ClassType[javaInterfaces.length];
            for (int i = 0; i < javaInterfaces.length; i++) {
                ClassType ifaceType = populateJavaClassMethods(null, javaInterfaces[i]);
                interfaces[i] = ifaceType;
            }
            classType.setInterfaces(interfaces);
        }
    }

    /**
     * Fetches the methods in the given ClassType.
     *
     * The methods are lazy loaded for existing java classes or the current list
     * of methods is used for the class being generated.
     * @param classType the ClassType that holds information about the class
     * @return an array of method descriptors
     */
    public MakerMethod [] getMethods(ClassType classType)
    {
        HashMap<String, MakerMethod> candidates = new HashMap<String, MakerMethod>();
        findJavaClassMethods(candidates, classType);
        if (classType.isInterface())
            findJavaInterfaceMethods(candidates, classType);
        return candidates.values().toArray(METHOD_ARRAY);
    }

    /**
     * Creates an array of method descriptors for the given java class.
     * </br>
     * All methods and constructors for the class are returned.
     * Private methods in base classes are filtered out.
     * @param candidates the map of candidates from <code>String</code> to <code>MakerMethod</code>
     * @param classType the ClassType that holds information about the class
     */
    public void findJavaClassMethods(HashMap<String, MakerMethod> candidates, ClassType classType)
    {
        if (classType != null)
        {
            populateJavaClassMethods(classType, classType.getJavaClass());
            ClassMakerFactory.addMethods(candidates, classType.getMethods());

            findJavaClassMethods(candidates, classType.getExtendsType());
        }
    }

    /**
     * Creates an array of method descriptors for the given java interface.
     * </br>
     * All methods for the interface are returned.
     * @param candidates the map of candidates from <code>String</code> to <code>MakerMethod</code>
     * @param classType the ClassType that holds information about the class
     */
    public void findJavaInterfaceMethods(HashMap<String, MakerMethod> candidates, ClassType classType)
    {
        populateJavaClassInterfaces(classType, classType.getJavaClass());
    	if (classType.getInterfaces() != null)
    	{    	
    	    for (ClassType interfaceType : classType.getInterfaces())
    	    {
    	        //ClassType interfaceType = declared.getClassType();
    	        populateJavaClassMethods(interfaceType, interfaceType.getJavaClass());
    	        ClassMakerFactory.addMethods(candidates, interfaceType.getMethods());

    	        findJavaInterfaceMethods(candidates, interfaceType);
    	    }
    	}
    }

    /**
     * Creates an array of constructor descriptors for the given java class.
     * </br>
     * Only constructors in the most explicit class are returned.
     * @param classType the ClassType that holds information about the class
     */
    public void populateJavaClassConstructors(ClassType classType)
    {
        Class javaClass = classType.getJavaClass();
        if (javaClass != null)
        {
            java.lang.reflect.Constructor [] construct = javaClass.getDeclaredConstructors();
        	MakerMethod [] constructors = new MakerMethod[construct.length];
            for (int i = 0; i < construct.length; i++)
            {
                java.lang.reflect.Constructor javaMethod = construct[i];
                constructors[i] = toMethod(classType, javaMethod);
            }
            classType.setConstructors(constructors);
        }
    }

    /**
     * Adds a <code>MakerMethod</code> to a lookup map.
     * @param allMethods the map from <code>String</code> to <code>MakerMethod</code>
     * @param method the <code>MakerMethod</code> to be added
     */
    static void addMethod(Map<String, MakerMethod> allMethods, MakerMethod method)
    {
        String key = method.toString();
        if (!allMethods.containsKey(key))
            allMethods.put(key, method);
    }
    
    /**
     * Adds an array of <code>MakerMethod</code> to a lookup map.
     * @param allMethods the map from <code>String</code> to <code>MakerMethod</code>
     * @param methods the array of <code>MakerMethod</code>s
     */
    static void addMethods(Map<String, MakerMethod> allMethods, MakerMethod [] methods)
    {
        if (methods != null)
        {
            for (MakerMethod method : methods)
            {
                addMethod(allMethods, method);
            }
        }
    }

    static void addInterfaceMethods(Map<String, MakerMethod> allMethods, ClassType [] interfaces)
    {
        // Add interface methods
        for (ClassType ifaceType : interfaces)
        {
            //ClassType ifaceType = declared.getClassType();
            if (ifaceType == null)
                throw new IllegalArgumentException("DeclaredType must refer to an interface");
            addMethods(allMethods, ifaceType.getMethods());
            // Recursively add methods from extended interfaces
            addInterfaceMethods(allMethods, ifaceType.getInterfaces());
        }
    }

    /**
     * Convert a reflection java method into a ClassMaker method descriptor.
     * @param javaMethod the java method to be converted
     * @return the corresponding ClassMaker method descriptor
     */
    protected MakerMethod toMethod(ClassType classType, java.lang.reflect.Method javaMethod)
    {
        String name = javaMethod.getName();
        short modifiers = (short)javaMethod.getModifiers();
        DeclaredType returnType = classToDeclaredType(javaMethod.getReturnType());
        MakerMethod method = new MakerMethod(classType, name, returnType, modifiers);
        Class[] params = javaMethod.getParameterTypes();
        DeclaredType [] formalParams = new DeclaredType[params.length];
        for (int i=0; i<params.length; i++)
        {
            DeclaredType param = classToDeclaredType(params[i]);
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
        DeclaredType declaredVoid = typeToDeclaredType(PrimitiveType.VOID_TYPE);
        MakerMethod method = new MakerMethod(classType, ClassMaker.INIT, declaredVoid, modifiers);
        Class[] params = javaMethod.getParameterTypes();
        DeclaredType [] formalParams = new DeclaredType[params.length];
        for (int i=0; i<params.length; i++)
        {
            DeclaredType param = classToDeclaredType(params[i]);
            formalParams[i] = param;
        }
        method.setFormalParams(formalParams);
        return method;
    }

    /**
     * Finds a member field declared in the class or a base class.
     * @param classType type of the class
     * @param name name of the field
     * @return the <code>MakerField</code> corresponding to the given name
     */
    protected MakerField findMemberField(ClassType classType, String name)
    {
        MakerField field = null;
        if (classType != null) {
            if (classType.getFields() == null && classType.getJavaClass() != null)
                classType.setFields(populateJavaClassFields(classType));
            field = classType.findField(name);
            if (field == null)
                field = findMemberField(classType.getExtendsType(), name);
        }
        return field;
    }

    /**
     * Fetch an array of field descriptors for the given java class.
     * @param classType the class from which to extract field descriptors
     * @return an array of field descriptors
     */
    protected MakerField[] populateJavaClassFields(ClassType classType)
    {
        Class javaClass = classType.getJavaClass();
        //MakerField[] makerFields = new MakerField[javaFields.length];
        ArrayList<MakerField> makerFields = new ArrayList<MakerField>();
        while (javaClass != null) {
            java.lang.reflect.Field[] javaFields = javaClass.getDeclaredFields();
            for (int i = 0; i < javaFields.length; i++)
            {
                String name = javaFields[i].getName();
                Class fieldType = javaFields[i].getType();
                //Type type = classToType(fieldType);
                DeclaredType declared = classToDeclaredType(fieldType);
                int modifiers = javaFields[i].getModifiers();
                makerFields.add(new MakerField(classType, name, declared, modifiers));
            }
            javaClass = javaClass.getSuperclass();
        }
        return makerFields.toArray(FIELD_ARRAY);
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
