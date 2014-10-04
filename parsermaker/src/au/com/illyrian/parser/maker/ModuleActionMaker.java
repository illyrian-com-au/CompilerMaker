package au.com.illyrian.parser.maker;

import java.util.Properties;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerLocation;
import au.com.illyrian.domainparser.ModuleAction;
import au.com.illyrian.parser.CompileUnit;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.InvokeParser;

public class ModuleActionMaker implements ModuleAction, ClassMakerLocation
{
    protected ClassMaker  maker = null;
    protected CompileUnit compileUnit = null;
    
    private Properties map = new Properties();

    public ModuleActionMaker()
    {
        super();
    }

    public void setClassMaker(ClassMaker classMaker) 
    {
    	maker = classMaker;
	}

    public ClassMaker getClassMaker()
    {
        if (maker == null)
            throw new NullPointerException("classMaker is null.");
        return maker;
    }

	public CompileUnit getCompileUnit()
    {
        if (compileUnit == null)
            throw new NullPointerException("compileUnit is null.");
        return compileUnit;
    }

    public void setCompileUnit(CompileUnit compileUnit)
    {
        this.compileUnit = compileUnit;
    }

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
        String className = simpleName;
        if (simpleName.indexOf('.') == -1)
        {
            className = map.getProperty(simpleName);
            if (className == null)
                className = getLocalClassName(simpleName);
        }
        return className;
    }
    
    // FIXME - move to ClassMaker
    String getLocalClassName(String simpleName)
    {
        String packageName = maker.getPackageName();
        if (packageName == null || "".equals(packageName))
            return simpleName;
        else
            return packageName + "." + simpleName;
    }

    public String addClassName(String className, String simpleName)
    {
        if (className == null)
            return simpleName;
        else
            return className + "." + simpleName;
    }

    public Object declareImport(String fullyQualifiedClassname,
            String simpleClassName)
    {
        maker.Import(fullyQualifiedClassname.toString());
        return simpleClassName;
    }

    public Object declarePackage(String packageName)
    {
        maker.setPackageName(packageName.toString());
        return packageName;
    }

    public void handleModule(Object module)
    {
        // Do nothing
    }

    public Object getModule()
    {
        return maker;
    }
    
//    public void invokeParseClass(String parseName, Input input) throws ParserException
//    {
//        String qualifiedName = getAlias(parseName);
//        InvokeParser invoker = getCompileUnit().getInvokeParser();
//        invoker.invokeParseClass(qualifiedName, input);
//    }
}
