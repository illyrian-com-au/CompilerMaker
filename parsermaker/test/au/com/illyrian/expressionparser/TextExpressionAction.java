package au.com.illyrian.expressionparser;

import au.com.illyrian.parser.impl.PrecidenceActionString;


public class TextExpressionAction extends PrecidenceActionString implements ExpressionAction <String>
{
    protected StringBuffer module = new StringBuffer();
    
    public Object declareFunctionName(String identifier) 
    {
		return identifier;
	}

	public Object addParameter(Object params, String identifier)
    {
        if (params == null)
            return identifier;
        else if (identifier == null)
            return params;
        else 
            return params + " " + identifier;
    }

    public void beginMethod() 
    {
	}

	public Object endMethod(Object type) 
	{
		return type;
	}

	public Object declareFunction(Object name, Object params,
            Object body)
    {
        if (params == null)
            params = "";
        return "(" + name + " (" + params + ") (" + body + "))";
    }

    public Object addFunction(Object function, Object functionList)
    {
        if (functionList == null)
            return function;
        else 
            return function + " " + functionList;
    }

    public Object declareClassname(String identifier)
    {
        return identifier;
    }

    public Object declareExtends(String extendClass)
    {
        return extendClass;
    }

    public Object declareClass(Object classname, Object extendClass, Object functionList)
    {
        String str = "(class " + classname + " " + extendClass + " (" + functionList + ")";
        module.append(str);
        return str;
    }

    public Object getModule()
    {
        return module.toString();
    }
}
