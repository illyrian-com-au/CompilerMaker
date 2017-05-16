package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public interface CallStack {
	
    public CallStack Push(Value reference);
	
    public Type[] toArray();

    public String getMethodName();

    public void setMethodName(String methodName);
    
    public int size();
}