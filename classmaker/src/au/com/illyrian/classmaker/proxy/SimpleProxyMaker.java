package au.com.illyrian.classmaker.proxy;

import java.lang.reflect.Method;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.types.Value;

public class SimpleProxyMaker implements ClassMakerConstants {
    public static final String REFERENCE = "reference";
    ClassMaker maker;

    public SimpleProxyMaker(ClassMaker maker) {
        this.maker = maker;
    }

    protected void createVariables(Class<?> javaClass) {
        maker.Declare("reference", javaClass, ACC_PRIVATE);
    }

    protected void createOtherMethods(Class<?> iface) {
    }
    
    protected void methodBody(Method method) {
        Value reference = pushReference();
        CallStack callStack = pushParameters(method);
       // methodResult(maker.Call(reference, method.getName(), callStack));
    }
    
    private Value pushReference() {
        return maker.Get(maker.This(), REFERENCE);
    }

    private CallStack pushParameters(Method method) {
        CallStack callStack = maker.Push();
        int len = method.getParameterTypes().length;
        for (int i=1; i<=len; i++) {
 //           callStack =  callStack.Push(pushParam(i));
        }
        return callStack;
    }
    
//    protected Value pushParam(int i) {
//       return maker.Get(param(i+1));
//    }
}
