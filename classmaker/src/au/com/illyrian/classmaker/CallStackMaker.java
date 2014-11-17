package au.com.illyrian.classmaker;

import java.util.Vector;

import au.com.illyrian.classmaker.types.Type;

/**
 * A call stack for the actual parameters types of a method call.
 */
public class CallStackMaker implements CallStack 
{
    /**
     * 
     */
    private final ClassMaker classMaker;
    private String       methodName = null;

    /**
     * @param classMaker
     */
    public CallStackMaker(ClassMaker classMaker)
    {
        this.classMaker = classMaker;
    }

    private Vector<Type>stack = new Vector<Type>();

    /**
     * Pushes an actual parameter type onto the stack.
     * May promote the parameter type to an int using <code>MethodInvocationConversion</code>.
     * @param param type of the actual parameter
     * @return a call stack containing the actual parameter types for a method call
     */
    public CallStack Push(Type type) throws ClassMakerException
    {
        // Convert automatically created StringBuffers back to Strings as they are pushed onto the stack.
        if (type == ClassMaker.AUTO_STRING_TYPE)
            type = this.classMaker.getFactory().getStringConversion().toString(this.classMaker, type.toClass());
        stack.add(type);
        return this;
    }

    /**
     * Creates an array of the actual parameter types on the call stack.
     */
    public Type[] toArray()
    {
        return getStack().toArray(ClassMakerFactory.TYPE_ARRAY);
    }

    /**
     * The size of the call stack.
     */
    public int size()
    {
        return stack.size();
    }

    /**
     * The actual parameter at the given index on the call stack
     * @param index the index of the parameter
     * @return the <code>Type</code> of the actual parameter
     */
    public Type get(int index)
    {
        return stack.get(index);
    }

    /**
     * The <code>Type</code>s on the call stack.
     * @return a <code>Vector</code> of <code>Type</code>s on the call stack.
     */
    protected Vector<Type> getStack()
    {
        return stack;
    }
    
    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        if (getMethodName() != null)
            buf.append(getMethodName());
        buf.append('(');
        for(int i=0; i<stack.size(); i++)
        {
            if (i>0) buf.append(", ");
            buf.append(stack.elementAt(i));
        }
        buf.append(')');
        return buf.toString();
    }

    public String getName()
    {
        return toString();
    }
}