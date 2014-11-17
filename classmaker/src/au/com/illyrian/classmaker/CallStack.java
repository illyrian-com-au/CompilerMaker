package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Type;

public interface CallStack {
	
	public CallStack Push(Type reference);
	
    public Type[] toArray();

    public String getMethodName();

    public void setMethodName(String methodName);
    
    public int size();
}