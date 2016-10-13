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
    public String getParserName(String simpleName)
    {
        String className = map.getProperty(simpleName);
        if (className == null)
            className = simpleName;
        return className;
    }

    public String Dot(String className, String simpleName)
    {
        if (className == null)
            return simpleName;
        else if (simpleName == null)
            return className;
        else
            return className + "." + simpleName;
    }

    public Object Import(String fullyQualifiedClassname)
    {
    	int offset = fullyQualifiedClassname.lastIndexOf('.');
    	String simpleClassName = (offset == -1) ? fullyQualifiedClassname : fullyQualifiedClassname.substring(offset+1);
    	this.setAlias(simpleClassName, fullyQualifiedClassname);
        String str = "import " + fullyQualifiedClassname + ";\n";
        buffer.append(str);
        return str;
    }

    public Object Package(String packageName)
    {
        String str = "package " + packageName + ";\n";
        buffer.append(str);
        return str;
    }
    
    public Object getModule()
    {
        return buffer.toString();
    }

    public Object handleModule(Object module)
    {
        buffer.append(module);
        return buffer.toString();
    }
}
