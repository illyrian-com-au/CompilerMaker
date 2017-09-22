package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.Initialiser;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.types.ArrayType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public interface ClassMakerIfc
{
    public void setSourceLine(SourceLine source);

    /**
     * Sets the name of the class being generated.
     * @param fullyQualifiedClassName the fully qualified name of the class
     * @throws ClassMakerException if it is too late to call this method
     */
    public void setPackageName(String packageName) throws ClassMakerException;

    /**
     * Imports the named class and creates a mapping for the short class name.
     * @param className the fully qualified name for the class
     * @throws ClassMakerException if the class does not exist
     */
    public void Import(String className) throws ClassMakerException;

    /**
     * Sets the name of the class being generated.
     * @param className the fully qualified name of the class
     * @throws ClassMakerException if it is too late to call this method
     */
    public void setSimpleClassName(String className) throws ClassMakerException;

    /**
     * Sets the class modifiers for the generated class.
     * @param modifiers valid class modifiers are:
     * <code>ACC_PUBLIC, ACC_PROTECTED, ACC_PRIVATE, ACC_STATIC, ACC_FINAL</code>
     * and <code>ACC_ABSTRACT</code>
     */
    public void setClassModifiers(int modifiers);

    /**
     * The generated class extends the given class.
     * </br>
     * This version takes a <code>String</code> and can be used to extend other generated classes.
     * @param className the name of the class that the generated class extends
     * @throws ClassMakerException if it is too late to call this method
     */
    public void Extends(String className) throws ClassMakerException;

    /**
     * Indicates that the class implements the named interface.
     * @param className the fully qualified class name
     */
    public void Implements(String className) throws ClassMakerException;

    //
    //################ Class Instantiation #########################
    /**
     * Creates a new instance of the given <code>Class</code>.
     * @param javaClass the <code>Class</code> to be instantiated
     * @return an <code>Initialiser</code> for the instance
     */
    public Initialiser New(Class javaClass) throws ClassMakerException;

    /**
     * Creates a new instance of the named class.
     * @param className the name of the class to intantiate
     * @return an <code>Initialiser</code> for the instance
     */
    public Initialiser New(String className) throws ClassMakerException;

    /**
     * Creates a new instance of the class.
     * @param declared a declared type represents the type of class
     * @return an <code>Initialiser</code> for the instance
     */
    public Initialiser New(Type type) throws ClassMakerException;

    /**
     * Calls a constructor from the base class that is appropriate for the actual parameters.
     * </br>
     * Uses <code>MethodResolver</code> to determine the appropriate constructor for the
     * actual parameters and invokes that constructor using the reference to <code>super</code>
     * on top of the stack. The first parameter to this call must be <code>Super()</code>.
     * @param classType the type of the base class
     * @param actualParameters the types of the actual parameters in the call stack
     */
    public void Init(Value classType, CallStack actualParameters) throws ClassMakerException;

    //################## Method calls ##########################
    /**
     * Calls a static method in the given class that is appropriate for the actual parameters.
     * </br>
     * Uses <code>MethodResolver</code> to determine the appropriate method for the actual parameters and
     * then statically invokes the method.
     * @param javaClass the <code>Class<code> containing the method to be invoked
     * @param methodName the name of the method to call
     * @param actualParameters the types of the actual parameters in the call stack
     * @return the return type of the called method
     */
    //public Value Call(Class javaClass, String methodName, CallStack actualParameters) throws ClassMakerException;

    /**
     * Calls a static method in the named class that is appropriate for the actual parameters.
     * </br>
     * Uses <code>MethodResolver</code> to determine the appropriate method for the actual parameters and
     * then statically invokes the method.
     * @param className a fully qualified classname
     * @param methodName the name of the method to call
     * @param actualParameters the types of the actual parameters in the call stack
     * @return the return type of the called method
     */
    public Value Call(String className, String methodName, CallStack actualParameters) throws ClassMakerException;

    /**
     * Calls a method from the class instance on top of the stack that is appropriate for the actual parameters.
     * </br>
     * Uses <code>MethodResolver</code> to determine the appropriate method for the actual parameters and
     * then determines whether the method is private, static, virtual or an interface method and
     * uses the appropriate invocation.
     * @param type the type of the reference on top of the stack
     * @param methodName the name of the method to call
     * @param actualParameters the types of the actual parameters in the call stack
     * @return the return type of the called method
     */
    public Value Call(Value reference, String methodName, CallStack actualParameters) throws ClassMakerException;

    // Special references
    /**
     * Pushes a reference to <code>this</code> class onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>process();<code></td>
     * <td><code>Call(This(), "process", null);</code></td></tr>
     * </table>
     * @return the type for this class
     */
    public Value This() throws ClassMakerException;

    /**
     * Pushes a reference to this class's <code>super</code> class onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>super();<code></td>
     * <td><code>Init(Super(), null);</code></td></tr>
     * <tr><td><code>super.process();<code></td>
     * <td><code>Call(Super(), "process", null);</code></td></tr>
     * </table>
     * @return the type for the super class
     */
    public Value Super() throws ClassMakerException;

    /**
     * Pushes <code>null</code> onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>null<code></td>
     * <td><code>Null()</code></td></tr>
     * </table>
     * @return the type for <code>null</code>
     */
    public Value Null() throws ClassMakerException;

    // Literals
    /**
     * Pushes a literal <code>double</code> onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>234.56789D<code></td>
     * <td><code>Literal(234.56789D)</code></td></tr>
     * </table>
     * @param value the double to be pushed onto the stack
     * @return the type for <code>double</code>
     */
    public Value Literal(double value) throws ClassMakerException;

    /**
     * Pushes a literal <code>float</code> onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>223.345F<code></td>
     * <td><code>Literal(123.456F)</code></td></tr>
     * </table>
     * @param value the float to be pushed onto the stack
     * @return the type for <code>float</code>
     */
    public Value Literal(float value) throws ClassMakerException;

    /**
     * Pushes a literal <code>long</code> onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>2000000L<code></td>
     * <td><code>Literal(2000000L)</code></td></tr>
     * </table>
     * @param value the long to be pushed onto the stack
     * @return the type for <code>long</code>
     */
    public Value Literal(long value) throws ClassMakerException;

    /**
     * Pushes a literal <code>int</code> onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>200<code></td>
     * <td><code>Literal(200)</code></td></tr>
     * </table>
     * @param value the int to be pushed onto the stack
     * @return the type for <code>int</code>
     */
    public Value Literal(int value) throws ClassMakerException;

    /**
     * Pushes a literal <code>char</code> onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>'Z'<code></td>
     * <td><code>Literal('Z')</code></td></tr>
     * </table>
     * @param value the char to be pushed onto the stack
     * @return the type for <code>char</code>
     */
    public Value Literal(char value) throws ClassMakerException;

    /**
     * Pushes a literal <code>byte</code> value onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>(byte)200<code></td>
     * <td><code>Literal((byte)200)</code></td></tr>
     * </table>
     * @param value the byte to be pushed onto the stack
     * @return the type for <code>byte</code>
     */
    public Value Literal(byte value) throws ClassMakerException;

    /**
     * Pushes a literal <code>short</code> onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>(short)32000<code></td>
     * <td><code>Literal((short)32000)</code></td></tr>
     * </table>
     * @param value the short to be pushed onto the stack
     * @return the type for <code>short</code>
     */
    public Value Literal(short value) throws ClassMakerException;

    /**
     * Pushes a literal <code>boolean</code> value onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>true<code></td>
     * <td><code>Literal(true)</code></td></tr>
     * </table>
     * @param value the boolean value to be pushed onto the stack
     * @return the type for <code>boolean</code>
     */
    public Value Literal(boolean value) throws ClassMakerException;

    /**
     * Pushes a literal <code>String</code> onto the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>"Hello World"<code></td>
     * <td><code>Literal("Hello World")</code></td></tr>
     * </table>
     * @param value the string to be pushed onto the stack
     * @return the type for <code>String</code>
     */
    public Value Literal(String value) throws ClassMakerException;

    //#################### Getters and Setters ######################
    /**
     * Assigns the value on top of the stack to a local variable or formal parameter and
     * leaves the value on the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>x = i = a;<code></td>
     * <td><code>Set("x", Assign("i", Get("a")));</code></td></tr>
     * </table>
     * Performs assignment conversion on the value before
     * assigning it. The unconverted value is left on top of the stack.
     * @param name name of the local variable
     * @param type type of the value being set
     * @return type of the value on the stack
     */
    public Value Assign(String name, Value type) throws ClassMakerException;

    /**
     * Assigns a value to a member variable and leaves the value on the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>x = obj.i = a;<code></td>
     * <td><code>Set("x", Assign(Get("obj"), "i", Get("a")));</code></td></tr>
     * </table>
     * The value is subject to assignment conversion before it is assigned.
     * The unconverted value is left on the stack.
     * @param type the type of the class containing the variable
     * @param fieldName the name of the member variable
     * @param valueType the type of the value to be set
     * @return the type of the unconverted value left on the stack
     */
    public Value Assign(Value type, String fieldName, Value value) throws ClassMakerException;

    /**
     * Assigns a value to a static member field, leaving the value on top of the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>MyClass.i = a;<code></td>
     * <td><code>Set("MyClass", "i", Get("a"));</code></td></tr>
     * </table>
     * The value is subject to assignment conversion before it is assigned.
     * The unconverted value is left on top of the stack.
     * @param className the short or fully qualified name of the class
     * @param fieldName the name of the static member variable
     * @param valueType the type of the value to be set
     * @return type of the value on top of the stack
     */
    public Value Assign(String className, String fieldName, Value value) throws ClassMakerException;

    /**
     * Sets the value on top of the stack to the named local variable or formal parameter.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>i = a;<code></td>
     * <td><code>Set("i", Get("a"));</code></td></tr>
     * </table>
     * Performs assignment conversion on the value on top of the stack before
     * storing it in the named local variable.
     * @param name name of the local variable
     * @param type type of the value being set
     * @return <code>ClassMaker.VOID_TYPE</code>
     */
    public Value Set(String name, Value value) throws ClassMakerException;

    /**
     * Sets a member variable to the value.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i = a;<code></td>
     * <td><code>Eval(Set(Get("obj"), "i", Get("a")));</code></td></tr>
     * </table>
     * The value is subject to assignment conversion before it is set.
     * @param type the type of the class containing the variable
     * @param fieldName the name of the member variable
     * @param valueType the type of the value to be set
     * @return a <code>Value</code> representing <code>void</code>
     */
    public Value Set(Value reference, String fieldName, Value value) throws ClassMakerException;

    /**
     * Sets a static member field to the value.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>MyClass.i = a;<code></td>
     * <td><code>Set("MyClass", "i", Get("a"));</code></td></tr>
     * </table>
     * The value is subject to assignment conversion before it is set.
     * @param className the short or fully qualified name of the class
     * @param fieldName the name of the static member variable
     * @param valueType the type of the value to be set
     * @return a <code>Value</code> representing <code>void</code>
     */
    public Value Set(String className, String fieldName, Value value) throws ClassMakerException;

    /**
     * Gets a value from a member variable.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i<code></td>
     * <td><code>Get(Get("obj"), "i");</code></td></tr>
     * </table>
     * @param reference the type of the class containing the variable
     * @param fieldName the name of the member variable
     * @return the type of the value left on the stack
     */
    public Value Get(Value reference, String fieldName) throws ClassMakerException;

    /**
     * Gets a value from a static member variable.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>MyClass.i<code></td>
     * <td><code>Get("MyClass", "i");</code></td></tr>
     * </table>
     * @param className the short or fully qualified name of the class
     * @param fieldName the name of the static member variable
     * @return the type of the value left on the stack
     */
    public Value Get(String className, String fieldName) throws ClassMakerException;

    /**
     * Gets a local variable or formal parameter by name.
     * @param name name of the local variable
     * @return type of the local variable
     */
    public Value Get(String name) throws ClassMakerException;

    /**
     * Finds a named field in the given class.
     * </br>
     * Delegates to <code>findMemberField</code> and throws an <code>Exception</code> if the field is not found.
     * @param reference the Value of the reference on top of the stack
     * @param name name of the member field
     * @return the <code>MakerField</code> corresponding to the given name
     * @throws ClassMakerException if the field is not found
     */
    public MakerField Find(Value type, String name) throws ClassMakerException;

    /**
     * Finds a named field in the given class.
     * </br>
     * Delegates to <code>findMemberField</code> and throws an <code>Exception</code> if the field is not found.
     * @param classType type of the class
     * @param name name of the member field
     * @return the <code>MakerField</code> corresponding to the given name
     * @throws ClassMakerException if the field is not found
     */
    public MakerField Find(String className, String fieldName) throws ClassMakerException;

    /**
     * Gets a local variable or formal parameter by name.
     * @param name name of the local variable
     * @return type of the local variable
     */
    public MakerField Find(String name) throws ClassMakerException;

    public Type findType(String typeName) throws ClassMakerException;

    public MakerField findField(String name) throws ClassMakerException;
    
    public int addModifier(int modifiers, String modifierName);
    
    public void Method(String methodName, String returnType, int methodModifiers) throws ClassMakerException;

    public void Method(String methodName, Type returnType, int methodModifiers) throws ClassMakerException;

    public Labelled Begin() throws ClassMakerException;

    public void End() throws ClassMakerException;
    
    /**
     * Declares a local variable or member field using a java class descriptor.
     * </br>
     * This method is used to declare all variables. The variable will be a
     * class member, formal parameter or local variable depending upon where
     * the <code>declare</code> method is called.
     * @param name the name of the variable
     * @param javaClass a class that describes the type of the variable
     * @param modifiers bitmask of variable modifiers
     */
    public void Declare(String name, Class javaClass, int modifiers) throws ClassMakerException;

    /**
     * Declares a local variable or member field using a java class descriptor.
     * </br>
     * This method is used to declare all variables. The variable will be a
     * class member, formal parameter or local variable depending upon where
     * the <code>declare</code> method is called.
     * @param name the name of the variable
     * @param typeName the name of the type of the variable
     * @param modifiers bitmask of variable modifiers
     */
    public void Declare(String name, String typeName, int modifiers) throws ClassMakerException;

    /**
     * Declares a local variable or member field using a <code>Value</code>.
     * </br>
     * This version of <code>Declare</code> may be used when there is no
     * class descriptor for the type.
     * The first pass of a two pass compiler will produce a <code>Value</code>,
     * but the <code>Class</code> will not be available until after the second pass.
     * The <code>Value</code> can be used to declare Classes during the first pass.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code><pre>
        public OtherClass x;
     * </pre><code></td>
     * <td><code><pre>
        Value otherClass = findClass("au.com.Illyrian.OtherClass");
        Declare("x", otherClass, ACC_PUBLIC);
       </pre></code></td></tr>
     * </table>
     * @param name the name of the variable
     * @param type the type of the variable
     * @param modifiers bitmask of variable modifiers
     */
    public void Declare(String name, Type type, int modifiers) throws ClassMakerException;

    //################### Casting methods. ##################
    /**
     * Casts a reference from one type to another.
     * </br>
     * Performs a casting conversion between the source and target types.
     * The short or fully qualified name of the target class may be used.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>(Runnable)a<code></td>
     * <td><code>Cast(Get("a"), "java/lang/Runnable")</code></td></tr>
     * <tr><td><code>(String)null<code></td>
     * <td><code>Cast(Null(), "String")</code></td></tr>
     * </table>
     * @param source the type of the reference on top of the stack
     * @param target the type into which to cast
     * @return the target type
     */
    public Value Cast(Value source, String target) throws ClassMakerException;

    /**
     * Casts a reference from one type to another.
     * </br>
     * Performs a casting conversion between the source and target types.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>(Runnable)a<code></td>
     * <td><code>Cast(Get("a"), Runnable.class)</code></td></tr>
     * <tr><td><code>(String)null<code></td>
     * <td><code>Cast(Null(), String.class)</code></td></tr>
     * </table>
     * @param source the type of the reference on top of the stack
     * @param target the type into which to cast
     * @return the target type
     */
    public Value Cast(Value source, Class target) throws ClassMakerException;

    /**
     * Casts a reference from one type to another.
     * </br>
     * Performs a casting conversion between the source and target types.
     * @param source the type of the reference on top of the stack
     * @param target the declared type which the reference shall be cast to
     * @return the target type
     */
    public Value Cast(Value source, Type target) throws ClassMakerException;

    /**
     * Test whether the given reference is of the specified type.
     * @param reference the reference to be tested
     * @param type the Type to be tested against
     * @return true if the instance is of the specified type; otherwise false 
     */
    public Value InstanceOf(Value reference, String target);
    public Value InstanceOf(Value reference, Type type);

    /**
     * <b>Add</b>s the two values on top of the stack.
     * </br>
     * The operands are numerically promoted to the same type and then added.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a + b<code></td>
     * <td><code>Add(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @param op2 the type of the right operand
     * @return the type of the result after promotion
     */
    public Value Add(Value value1, Value value2);

    /**
     * <b>Subt</b>racts the two values on top of the stack.
     * </br>
     * The operands are numerically promoted to the same type and then subtracted.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a - b<code></td>
     * <td><code>Subt(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @param op2 the type of the right operand
     * @return the type of the result after promotion
     */
    public Value Subt(Value value1, Value value2);

    /**
     * <b>Mult</b>iplies the two values on top of the stack.
     * </br>
     * The operands are numerically promoted to the same type and then multiplied.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a * b<code></td>
     * <td><code>Mult(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @param op2 the type of the right operand
     * @return the type of the result after promotion
     */
    public Value Mult(Value value1, Value value2);

    /**
     * <b>Div</b>ides the two values on top of the stack.
     * </br>
     * The operands are numerically promoted to the same type and then divided.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a / b<code></td>
     * <td><code>Div(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @param op2 the type of the right operand
     * @return the type of the result after promotion
     */
    public Value Div(Value value1, Value value2);

    /**
     * Determines the integer <b>Rem</b>ainder after dividing the two values on top of the stack.
     * </br>
     * The operands are numerically promoted to the same type and then integer divided.
     * The remainder is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a % 3<code></td>
     * <td><code>Rem(Get("a"), Literal(3))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @param op2 the type of the right operand
     * @return the type of the result after promotion
     */
    public Value Rem(Value value1, Value value2);

    /**
     * Negates the numeric value on top of the stack.
     * </br>
     * The value is numerically promoted to <code>int</code>, if appropriate, and then negated.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>-a<code></td>
     * <td><code>Neg(Get("a"))</code></td></tr>
     * </table>
     * @param value the type of the value
     * @return the type of the promoted value
     */
    public Value Neg(Value value);

    /**
     * Applies bitwise <b>exclisive or</b> to the two values on top of the stack.
     * </br>
     * The operands are numerically promoted to the same type and then exclusive or'ed.
     * A result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a ^ 3<code></td>
     * <td><code>Xor(Get("a"), Literal(3))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @param op2 the type of the right operand
     * @return the type of the result after promotion
     */
    public Value Xor(Value value1, Value value2);

    /**
     * Applies bitwise <b>and</b> to the two values on top of the stack.
     * </br>
     * The operands are numerically promoted to the same type and then bitwised and'ed.
     * A result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a & 3<code></td>
     * <td><code>And(Get("a"), Literal(3))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @param op2 the type of the right operand
     * @return the type of the result after promotion
     */
    public Value And(Value value1, Value value2);

    /**
     * Applies bitwise <b>or</b> to the two values on top of the stack.
     * </br>
     * The operands are numerically promoted to the same type and then bitwised or'ed.
     * The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a | 3<code></td>
     * <td><code>Or(Get("a"), Literal(3))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @param op2 the type of the right operand
     * @return the type of the result after promotion
     */
    public Value Or(Value value1, Value value2);

    /**
     * Applies bitwise <b>Inv</b>ersion to the value on top of the stack.
     * </br>
     * The operand is numerically promoted to an <code>int</code>, if appropriate,
     * and then bitwised inverted. The result is left on the stack.
     *
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>~ a<code></td>
     * <td><code>Inv(Get("a"))</code></td></tr>
     * </table>
     * @param op1 the type of the left operand
     * @return the type of the result after promotion
     */
    public Value Inv(Value value);

    //################ Bitwise shifting operators ####################
    /**
     * <b>SH</b>ifts <b>L</b>eft the value of the first operand by the number of bits indicated by the second operand.
     * </br>
     * The operands are independantly numerically promoted.
     * A shifted result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a << 3<code></td>
     * <td><code>SHL(Get("a"), Literal(3))</code></td></tr>
     * </table>
     * @param op1 the type of the operand being shifted
     * @param op2 the type of the operand indicating places to shift
     * @return the type of op1 after promotion
     */
    public Value SHL(Value value1, Value value2);

    /**
     * <b>SH</b>ifts <b>R</b>ight the signed value of the first operand by the number of bits indicated by the second operand.
     * </br>
     * The operands are independantly numerically promoted.
     * A shifted result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a >> 3<code></td>
     * <td><code>SHR(Get("a"), Literal(3))</code></td></tr>
     * </table>
     * @param op1 the type of the operand being shifted
     * @param op2 the type of the operand indicating places to shift
     * @return the type of op1 after promotion
     */
    public Value SHR(Value value1, Value value2);

    /**
     * <b>U</b>nsigned <b>SH</b>ifts <b>R</b>ight the value of the first operand by the number of bits indicated by the second operand.
     * </br>
     * The operands are independantly numerically promoted.
     * A shifted result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a >>> 3<code></td>
     * <td><code>USHR(Get("a"), Literal(3))</code></td></tr>
     * </table>
     * @param op1 the type of the operand being shifted
     * @param op2 the type of the operand indicating places to shift
     * @return the type of op1 after promotion
     */
    public Value USHR(Value value1, Value value2);

    //################# Comparison operators ######################
    /**
     * Tests whether the first value is <b>G</b>reater <b>T</b>han the second.
     * </br>
     * The operand are numerically promoted to the same type and then compared.
     * A boolean result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a > b<code></td>
     * <td><code>GT(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the first operand
     * @param op2 the type of the second operand
     * @return the type of the result is always <code>boolean</code>
     */
    public Value GT(Value value1, Value value2);

    /**
     * Tests whether the first value is <b>G</b>reater than or <B>E</B>qual to the second.
     * </br>
     * The operand are numerically promoted to the same type and then compared.
     * A boolean result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a >= b<code></td>
     * <td><code>GE(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the first operand
     * @param op2 the type of the second operand
     * @return the type of the result is always <code>boolean</code>
     */
    public Value GE(Value value1, Value value2);

    /**
     * Tests whether the first value is <b>L</b>ess than or <B>E</B>qual to the second.
     * </br>
     * The operand are numerically promoted to the same type and then compared.
     * A boolean result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a <= b<code></td>
     * <td><code>LE(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the first operand
     * @param op2 the type of the second operand
     * @return the type of the result is always <code>boolean</code>
     */
    public Value LE(Value value1, Value value2);

    /**
     * Tests whether the first value is <b>L</b>ess <b>T</b>han the second.
     * </br>
     * The operand are numerically promoted to the same type and then compared.
     * A boolean result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a < b<code></td>
     * <td><code>LT(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the first operand
     * @param op2 the type of the second operand
     * @return the type of the result is always <code>boolean</code>
     */
    public Value LT(Value value1, Value value2);

    /**
     * Tests whether the two values are <b>Eq</b>ual.
     * </br>
     * The operands are numerically promoted to the same type and then compared.
     * A boolean result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a == b<code></td>
     * <td><code>EQ(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the first operand
     * @param op2 the type of the second operand
     * @return the type of the result is always <code>boolean</code>
     */
    public Value EQ(Value value1, Value value2);

    /**
     * Tests whether the two values are <b>N</b>ot <b>E</b>qual.
     * </br>
     * The operands are numerically promoted to the same type and then compared.
     * A boolean result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a != b<code></td>
     * <td><code>NE(Get("a"), Get("b"))</code></td></tr>
     * </table>
     * @param op1 the type of the first operand
     * @param op2 the type of the second operand
     * @return the type of the result is always <code>boolean</code>
     */
    public Value NE(Value value1, Value value2);

    /**
     * Negates a boolean value.
     * </br>
     * A boolean result is left on the stack.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code> ! a<code></td>
     * <td><code>Not(Get("a"))</code></td></tr>
     * </table>
     * @param op1 the type of the first operand
     * @return the type of the result is always <code>boolean</code>
     */
    public Value Not(Value value);

    /**
     * Creates an array using the dimension on the stack.
     * <pre>
        Set("x" , NewArray(ArrayOf(int.class), Literal(5)));
     * </pre>
     * @param arrayType the type of array to be created
     * @param sizeType the type of the dimension
     * @return the type of the new array instance
     */
    public Value NewArray(Type arrayType, Value size);

    /**
     * Finds a type representing an array of the given java class.
      <pre>
         Declare("intArray", ArrayOf(int.class), 0);
      </pre>
     * @param javaClass the class of element in the array
     * @return an <code>Value</code> whose elements are of the given class
     */
    public ArrayType ArrayOf(Class javaClass);

    /**
     * Finds a type representing an array of the given java class.
      <pre>
         Declare("intArray", ArrayOf("int"), 0);
      </pre>
     * @param typeName the class name of the element in the array
     * @return an <code>Value</code> whose elements are of the given class
     */
    public ArrayType ArrayOf(String typeName);

    /**
     * Finds a type representing an array of the given type.
      <pre>
         Declare("intArray", ArrayOf(ClassMaker.INT_TYPE), 0);
         Declare("processArray", ArrayOf(getClassType()), 0);
      </pre>
     * This method must be used when there is no concrete <code>Class</code>
     * for the array element, for example when declaring an array of
     * the class currently being generated.
     * @param type the type of element in the array
     * @return an <code>Value</code> whose elements are of the given type
     */
     public ArrayType ArrayOf(Type type);

    /**
     * Gets a value from an array element.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a[0]<code></td>
     * <td><code>GetAt(Get("a"), Literal(0));</code></td></tr>
     * </table>
     * The <code>indexType</code> must be int, short, char or byte.
     * @param type the type of the array containing the element
     * @param indexType the type of the index into the array
     * @return the type of the value on the stack
     */
    public Value GetAt(Value reference, Value index);

    /**
     * Assigns an array element to a value and leaves the value on the stack.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>x = a[0] = b;<code></td>
     * <td><code>Set("x", AssignAt(Get("a"), Literal(0), Get("b")));</code></td></tr>
     * </table>
     * The <code>indexType</code> must be int, short, char or byte.
     * The value is subject to assignment conversion before it is set.
     * @param arrayType the type of the array containing the element
     * @param indexType the type of the index into the array
     * @param valueType the type of the value to be set
     * @return the type of the value on the stack
     */
    public Value AssignAt(Value array, Value index, Value value);

    /**
     * Sets an array element to a value.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a[0] = b;<code></td>
     * <td><code>SetAt(Get("a"), Literal(0), Get("b"));</code></td></tr>
     * </table>
     * The <code>indexType</code> must be int, short, char or byte.
     * The value is subject to assignment conversion before it is set.
     * @param arrayType the type of the array containing the element
     * @param indexType the type of the index into the array
     * @param value the type of the value to be set
     * @return a <code>Value</code> representing <code>void</code>
     */
    public Value SetAt(Value arrayRef, Value index, Value value);

    /**
     * Gets the length of the array on top of the stack.
     * <pre>
     *    Eval(Set("len", Length(Get("anArray"))));
     * </pre>
     * @param arrayType the type of the array
     * @return the length of the array
     */
    public Value Length(Value arrayRef);

    /**
     * Gets the value of a local variable after incrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>++i;<code></td>
     * <td><code>Inc("i")</code></td></tr>
     * </table>
     * @param name the name of the member variable
     * @return the value of the variable after it is incremented
     */
    public Value Inc(String name) throws ClassMakerException;

    /**
     * Gets the value of a member variable after incrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>++obj.i;<code></td>
     * <td><code>Inc(Get("obj"), "i")</code></td></tr>
     * </table>
     * @param type the type of the class containing the variable
     * @param name the name of the member variable
     * @return the value of the variable after it is incremented
     */
    public Value Inc(Value reference, String name) throws ClassMakerException;

    /**
     * Gets the value of a static member variable after incrementing it.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>++MyClass.i;<code></td>
     * <td><code>Inc("MyClass", "i")</code></td></tr>
     * </table>
     * @param className the short or fully qualified name of the class
     * @param name the name of the static member variable
     * @return the value of the variable after it is incremented
     */
    public Value Inc(String className, String name) throws ClassMakerException;

    /**
     * Sets a member variable to the value.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i = a;<code></td>
     * <td><code>Eval(Inc(Find("obj"), "i"));</code></td></tr>
     * </table>
     * The value is subject to assignment conversion before it is set.
     * @param type the type of the class containing the variable
     * @param fieldName the name of the member variable
     * @param valueType the type of the value to be set
     * @return a <code>Value</code> representing <code>void</code>
     */
//    public Value Inc(Value lvalue) throws ClassMakerException;

    /**
     * Gets the value of a local variable after decrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>--i;<code></td>
     * <td><code>Dec("i")</code></td></tr>
     * </table>
     * @param name the name of the member variable
     * @return the value of the variable after it is decremented
     */
    public Value Dec(String name) throws ClassMakerException;

    /**
     * Gets the value of a member variable after decrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>--obj.i;<code></td>
     * <td><code>Dec(Get("obj"), "i")</code></td></tr>
     * </table>
     * @param type the type of the class containing the variable
     * @param name the name of the member variable
     * @return the value of the variable after it is decremented
     */
    public Value Dec(Value reference, String name) throws ClassMakerException;

    /**
     * Gets the value of a static member variable after decrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>--obj.i;<code></td>
     * <td><code>Dec("MyClass", "i")</code></td></tr>
     * </table>
     * @param className the short or fully qualified name of the class
     * @param name the name of the static member variable
     * @return the value of the variable after if is decremented
     */
    public Value Dec(String className, String name) throws ClassMakerException;

    /**
     * Sets a member variable to the value.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i = a;<code></td>
     * <td><code>Eval(Dec(Find("obj", "i")));</code></td></tr>
     * </table>
     * The value is subject to assignment conversion before it is set.
     * @param type the type of the class containing the variable
     * @param fieldName the name of the member variable
     * @param valueType the type of the value to be set
     * @return a <code>Value</code> representing <code>void</code>
     */
//    public Value Dec(Value lvalue) throws ClassMakerException;

    /**
     * Gets the value of a local variable before incrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>i++;<code></td>
     * <td><code>PostInc("i")</code></td></tr>
     * </table>
     * @param name the name of the member variable
     * @return the value of the variable before it is incremented
     */
    public Value PostInc(String name) throws ClassMakerException;

    /**
     * Gets the value of a member variable before incrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i++;<code></td>
     * <td><code>PostInc(Get("obj"), "i")</code></td></tr>
     * </table>
     * @param type the type of the class containing the variable
     * @param name the name of the member variable
     * @return the value of the variable before it is incremented
     */
    public Value PostInc(Value reference, String name) throws ClassMakerException;

    /**
     * Gets the value of a static member variable before incrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>MyClass.i++;<code></td>
     * <td><code>PostInc("MyClass", "i")</code></td></tr>
     * </table>
     * @param className the short or fully qualified name of the class
     * @param name the name of the static member variable
     * @return the value of the variable before it is incremented
     */
    public Value PostInc(String className, String name) throws ClassMakerException;

    /**
     * Sets a member variable to the value.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i = a;<code></td>
     * <td><code>Eval(Dec(Find("obj", "i")));</code></td></tr>
     * </table>
     * The value is subject to assignment conversion before it is set.
     * @param type the type of the class containing the variable
     * @param fieldName the name of the member variable
     * @param valueType the type of the value to be set
     * @return a <code>Value</code> representing <code>void</code>
     */
    //public Value PostInc(Value lvalue) throws ClassMakerException;

    /**
     * Gets the value of a local variable before decrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>i--;<code></td>
     * <td><code>PostDec("i")</code></td></tr>
     * </table>
     * @param name the name of the member variable
     * @return the value of the variable before it is decremented
     */
    public Value PostDec(String name) throws ClassMakerException;

    /**
     * Gets the value of a member variable before decrementing it.
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i--;<code></td>
     * <td><code>PostDec(Get("obj"), "i")</code></td></tr>
     * </table>
     * @param type the type of the class containing the variable
     * @param name the name of the member variable
     * @return the value of the variable before if is decremented
     */
    public Value PostDec(Value reference, String name) throws ClassMakerException;

    /**
     * Gets the value of a static member variable before decrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>MyClass.i--;<code></td>
     * <td><code>PostDec("MyClass", "i")</code></td></tr>
     * </table>
     * @param className the short or fully qualified name of the class
     * @param name the name of the static member variable
     * @return the value of the variable before if is decremented
     */
    public Value PostDec(String className, String name) throws ClassMakerException;

    /**
     * Sets a member variable to the value.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i = a;<code></td>
     * <td><code>Eval(Dec(Find("obj", "i")));</code></td></tr>
     * </table>
     * The value is subject to assignment conversion before it is set.
     * @param type the type of the class containing the variable
     * @param fieldName the name of the member variable
     * @param valueType the type of the value to be set
     * @return a <code>Value</code> representing <code>void</code>
     */
//    public Value PostDec(Value lvalue) throws ClassMakerException;

    /**
     * Gets the value of an array element after incrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>a = ++values[2];<code></td>
     * <td><code>Set("a", IncAt(Get("values"), Literal(2)));</code></td></tr>
     * </table>
     * @param array the type of the array
     * @param index the type of the index
     * @return the value of the array element after it is incremented
     */
    public Value IncAt(Value array, Value index) throws ClassMakerException;

    /**
     * Gets the value of an array element after decrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>--obj.i;<code></td>
     * <td><code>Dec(Get("obj"), "i")</code></td></tr>
     * </table>
     * @param array the type of the array
     * @param index the type of the index
     * @return the value of the array element after it is decremented
     */
    public Value DecAt(Value array, Value index) throws ClassMakerException;

    /**
     * Gets the value of an array element before incrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i++;<code></td>
     * <td><code>PostInc(Get("obj"), "i")</code></td></tr>
     * </table>
     * @param array the type of the array
     * @param index the type of the index
     * @return the value of the array element before it is incremented
     */
    public Value PostIncAt(Value array, Value index) throws ClassMakerException;

    /**
     * Gets the value of an array element before decrementing it.
     * </br>
     * The following code is equivalent.
     * <table border="1" width="100%">
     * <tr><td>Java code</td><td>ClassMaker code</td></tr>
     * <tr><td><code>obj.i--;<code></td>
     * <td><code>PostDec(Get("obj"), "i")</code></td></tr>
     * </table>
     * @param array the type of the array
     * @param index the type of the index
     * @return the value of the array element before if is decremented
     */
    public Value PostDecAt(Value array, Value index) throws ClassMakerException;
    
    public CallStack Push(Value reference);

    public CallStack Push();

    /**
     * Appends a <code>cond &&</code> logic expression to a preceeding expression.
     * </br>
     * If the preceeding <code>andOr</code> expression evaluates to true the
     * <code>cond</code> will be evaluated.
     * If the preceeding <code>andOr</code> expression evaluates to false then
     * evaluation of the <code>cond</code> expression will be jumped over.
     * <pr/>
     * The following code generates bytecode for <code>a && b && c</code>. <br/>
     <pre>
     Logic(AndThen(AndThen(Get("a")), Get("b")), Get("c"))
     </pre>
     * @param andOr preceeding logical expression
     * @param cond next conditional expression
     * @return logic expression including shortcut logic
     */
    public AndOrExpression AndThen(AndOrExpression andOr, Value cond);

    /**
     * Creates a <code>cond &&</code> logic expression.
     * </br>
     * If the <code>cond</code> expression evaluates to true the
     * following logic expression will be evaluated.
     * If the <code>cond</code> expression evaluates to false then
     * evaluation of the following expression will be jumped over.
     * <pr/>
     * The following code generates bytecode for <code>a && b</code>. <br/>
     <pre>
     Logic(AndThen(Get("a")), Get("b"))
     </pre>
     * @param cond next conditional expression
     * @return logic expression including shortcut logic
     */
    public AndOrExpression AndThen(Value cond);

    /**
     * Appends a <code>cond ||</code> logic expression to a preceeding expression.
     * </br>
     * If the preceeding <code>andOr</code> expression evaluates to false the
     * <code>cond</code> will be evaluated.
     * If the preceeding <code>andOr</code> expression evaluates to true then
     * evaluation of the <code>cond</code> expression will be jumped over.
     * <pr/>
     * The following code generates bytecode for <code>a || b || c</code>. <br/>
     <pre>
     Logic(OrElse(OrElse(Get("a")), Get("b")), Get("c"))
     </pre>
     * @param andOr preceeding logical expression
     * @param cond next conditional expression
     * @return logic expression including shortcut logic
     */
    public AndOrExpression OrElse(AndOrExpression andOr, Value cond);

    /**
     * Creates a <code>cond ||</code> logic expression.
     * </br>
     * If the <code>cond</code> expression evaluates to false the
     * following logic expression will be evaluated.
     * If the <code>cond</code> expression evaluates to true then
     * evaluation of the following expression will be jumped over.
     * <pr/>
     * The following code generates bytecode for <code>a || b</code>. <br/>
     <pre>
     Logic(OrElse(Get("a")), Get("b"))
     </pre>
     * @param cond next conditional expression
     * @return logic expression including shortcut logic
     */
    public AndOrExpression OrElse(Value cond);

    /**
     * Contains an expression that includes shortcut AND and OR logic.
     * </br>
     * If the preceeding <code>andOr</code> expression is shortcut then
     * evaluation of the <code>cond</code> expression will be jumped over.
     * <pr/>
     * The following code generates bytecode for <code>a && b || c</code>. <br/>
     <pre>
     Logic(OrElse(AndThen(Get("a")), Get("b")), Get("c"))
     </pre>
     * This method creates a label for the shortcut expression to jump to.
     * @param andOr preceeding logical expression
     * @param cond last conditional expression
     * @return return type is always boolean
     */
    public Value Logic(AndOrExpression andOr, Value cond);

    // Program Statements
    /**
     * Evaluate an expression as though it were a statement.
     * </br>
     * An <code>Eval</code> should surround each expression that is used like a statement.
     * Expressions leave their results on the top of the program stack. Calling
     * <code>Eval</code> discards the result of the expression by popping it off the stack.
     * Expressions that have no result, such as method calls that return <code>void</code>,
     * are ignored.
     * <pre>
          Eval(Dec("a"));
     * </pre>
     * @param type the result type of the expression
     */
    public void Eval(Value value);

    /**
     * Ends a method that does not have a body.
     */
    public void Forward() throws ClassMakerException;

    public void Return() throws ClassMakerException;
    
    public void Return(Value type) throws ClassMakerException;

    /**
     * Begins an <code>If</code> statement.
     * @return an interface to set a Label
     */
    public Labelled If(Value condition) throws ClassMakerException;

    /**
     * Begins an <code>Else</code> clause of an <code>If</code> statement.
     */
    public void Else() throws ClassMakerException;

    /**
     * Ends an <ocde>If</code> Statement.
     */
    public void EndIf() throws ClassMakerException;
    
    /**
     * Begins a <code>Loop</code> statement.
     */
    public Labelled Loop() throws ClassMakerException;
    
    /**
     * Ends a <code>Loop</code> statement.
     */
    public void EndLoop() throws ClassMakerException;

    /**
     * Iterates through a <code>Loop</code> while the condition is <code>true</code>
     */
    public void While(Value condition) throws ClassMakerException;

    /**
     * Start of a <code>For</code> loop.
     */
    public ForWhile For(Value declare) throws ClassMakerException;

    /**
     * Ends a <code>Loop</code> statement.
     */
    public void EndFor() throws ClassMakerException;
    
    /** Breaks to the end of the nearest enclosing <code>Loop</code> or <code>Switch</code> statement. */
    public void Break() throws ClassMakerException;

    /** Breaks to the end of the enclosing statement with the given label.     */
    public void Break(String label) throws ClassMakerException;

    /** Continues the nearest enclosing <code>Loop<code> statement. */
    public void Continue() throws ClassMakerException;
    
    /** Continues the enclosing <code>Loop<code> statement with the given label.     */
    public void Continue(String label) throws ClassMakerException;

    /** Begins a <code>Switch</code> statement. */
    public Labelled Switch(Value type);

    /** Adds a <code>Case</code> clause of a <code>Switch</code> statement. */
    public void Case(int key);
    
    /** Adds a <code>Default</code> clause of a <code>Switch</code> statement. */
    public void Default();

    /** Ends a <code>Switch</code> statement. */
    public void EndSwitch();

    /**
     * Begins a <code>Try Catch Finally</code> block.
     */
    public Labelled Try();

    /**
     * Catches the Exception and stores it in a the named local variable.
     */
    public void Catch(String exceptionName, String name);

    /**
     * Catches the Exception represented by the java Class.
     */
    public void Catch(Class javaClass, String name) throws ClassMakerException;

    /**
     * Starts a Finally block.
     */
    public void Finally() throws ClassMakerException;

    /**
     * Ends a <code>Try Catch Finally</code> block.     
     */
    public void EndTry() throws ClassMakerException;

    /**
     * Creates an exception with a variable number of parameters to the error message.
     * </br>
     * The resource bundle is <code>ExceptionMessages.properties</code>.
     * 
     * @param key the key to lookup in the resource bundle
     * @param values a variable list of values to be substituted into the error message
     * @return a formatted ClassMakerException
     */
    public ClassMakerException createException(String key, String ... values);

    /**
     * Get the current pass for the class generator.
     * </br>
     * The default is <code>ClassMaker.ONE_PASS</code>.
     */
    public int getPass();
    
    /**
     * Completes processing of the class.
     * This method is automatically called when the class is defined.
     */
    public void EndClass() throws ClassMakerException;
}