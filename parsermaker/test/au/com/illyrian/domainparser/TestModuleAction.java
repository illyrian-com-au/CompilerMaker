package au.com.illyrian.domainparser;

import java.util.Properties;

public class TestModuleAction implements ModuleAction
{
    protected StringBuffer buffer = new StringBuffer();
    
    private Properties map = new Properties();

    /**
     * Creates a mapping from a simple class name to a fully qualified class name.
     * @param simpleName the simple class name
     * @param className the fully qualified class name
     */
    public void setAlias(String simpleName, String className)
    {
        map.put(simpleName, className);
    }
    
    /**
     * Resolves a simple class name to a fully qualified class name.
     * @param simpleName the simple class name
     * @return the fully qualified class name if available; otherwise the simple class name
     */
    public String getAlias(String simpleName)
    {
        String className = map.getProperty(simpleName);
        if (className == null)
            className = simpleName;
        return className;
    }

    public String addClassName(String className, String simpleName)
    {
        if (className == null)
            return simpleName;
        else if (simpleName == null)
            return className;
        else
            return className + "." + simpleName;
    }

    public Object declareImport(String fullyQualifiedClassname, String simpleClassname)
    {
        String str = "(import " + fullyQualifiedClassname + " " + simpleClassname + ") ";
        buffer.append(str);
        return str;
    }

    public Object declarePackage(String packageName)
    {
        String str = "(package " + packageName + ") ";
        buffer.append(str);
        return str;
    }
    
    public Object getModule()
    {
        return buffer.toString();
    }

    public void handleModule(Object module)
    {
        buffer.append(module);
    }
}
