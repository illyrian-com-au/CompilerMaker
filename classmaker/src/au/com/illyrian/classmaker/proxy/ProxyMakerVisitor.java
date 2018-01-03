package au.com.illyrian.classmaker.proxy;

import java.lang.reflect.Method;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;

public class ProxyMakerVisitor {
    public static final String STASH_VAR = "values";

    protected final ClassMaker maker;

    public ProxyMakerVisitor(ClassMaker maker) {
        this.maker = maker;
    }
    
    protected void createVariables(Class iface) {
        maker.Declare(STASH_VAR, ClassMakerFactory.OBJECT_TYPE, ClassMakerConstants.ACC_PRIVATE);
    }
    
    public void methodBody(Method method) {
        Class<?> [] params = method.getParameterTypes();
        maker.Eval(maker.Assign(maker.This(), STASH_VAR,
                maker.NewArray(maker.ArrayOf(ClassMakerFactory.OBJECT_TYPE), maker.Literal(params.length))));
        int i=0;
        for (Class<?>javaClass : method.getParameterTypes()) {
            pushParameter(i++, javaClass);
        }
    }
    
    void pushParameter(int offset, Class<?> param) {        
    }
}
