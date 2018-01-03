package au.com.illyrian.classmaker.members;

import java.util.HashMap;

import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.types.ClassType;

public class MakerMethodCollector extends HashMap<String, MakerMethod> implements MakerMethodFilter
{
    private static final long serialVersionUID = 1L;
    private final MakerMethodFilter filter;

    public MakerMethodCollector() {
        filter = this;
    }
    
    public MakerMethodCollector(MakerMethodFilter filter) {
        this.filter = filter;
    }
    
    public MakerMethodCollector(String name) {
        this.filter = new MethodNameFilter(name);
    }
    
    public boolean isMatch(MakerMethod method) {
        return true;
    }
    
    /**
     * Adds a <code>MakerMethod</code> to a lookup map.
     * @param method the <code>MakerMethod</code> to be added
     */
    private void addMatchingMethod(MakerMethod method) {
        if (filter.isMatch(method)) {
            String key = method.toShortString();
            if (!containsKey(key)) {
                put(key, method);
            }
        }
    }
    
    /**
     * Adds an array of <code>MakerMethod</code> to a lookup map.
     * @param methods the array of <code>MakerMethod</code>s
     */
    public void addMethods(MakerMethod[] methods) {
        if (methods != null) {
            for (MakerMethod method : methods) {
                addMatchingMethod(method);
            }
        }
    }

    public void includeInterfaceMethods(ClassType classType) {
        addMethods(classType.getDeclaredMethods());
        for (ClassType interfaceType : classType.getInterfaces()) {
            includeInterfaceMethods(interfaceType);
        }
    }

    public void includeClassMethods(ClassType classType) {
        if (classType != null) {
            MakerMethod [] declaredMethods = classType.getDeclaredMethods();
            addMethods(declaredMethods);
            includeClassMethods(classType.getExtendsType());
        }
    }
    
    public MakerMethod [] toArray() {
        return values().toArray(ClassMakerFactory.METHOD_ARRAY);
    }

    public static class MethodNameFilter implements MakerMethodFilter {
        private final String name;
        
        MethodNameFilter(String name) {
            this.name = name;
        }
        
        @Override
        public boolean isMatch(MakerMethod method) {
            return name.equals(method.getName());
        }
    };
}
