package au.com.illyrian.jesub;

import java.util.Properties;

public class JesubActionString implements JesubAction
{
    private StringBuffer modifierBuf = new StringBuffer();

    private Properties map = new Properties();

    PrettyPrintWriter  out = new PrettyPrintWriter();
    
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
        String str = "Import(\"" + fullyQualifiedClassname + "\");";
        out.println(str);
        return str;
    }

    public Object Package(String packageName)
    {
        String str = "setPackage(\"" + packageName + "\");";
        out.println(str);
        return str;
    }
    
    public Object getModule()
    {
        return out.toString();
    }

    public Object handleModule(Object module)
    {
        out.print(module);
        return toString();
    }

    public Object addModifier(String modifier)
    {
        modifierBuf.append(modifier);
        modifierBuf.append(" ");
        return modifierBuf.toString();
    }
    
    public Object setClassModifiers(Object modifiers)
    {
        String str = "setModifiers(\"" + modifiers + "\");";
        out.println(str);
        return str;
    }

    public String setClassName(String simpleName)
    {
        String str = "setSimpleClassName(\"" + simpleName + "\");";
        out.println(str);
        return str;
    }

    public Object declareExtends(String className)
    {
        String str = "Extends(\"" + className + "\");";
        out.println(str);
        return str;
    }

    public Object declareImplements(String className)
    {
        String str ="Implements(\"" + className + "\");";
        out.println(str);
        return str;
    }

    public Object primitiveType(String name)
    {
        return name;
    }

}
