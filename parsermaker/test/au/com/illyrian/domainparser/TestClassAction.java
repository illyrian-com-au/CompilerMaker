package au.com.illyrian.domainparser;

public class TestClassAction extends TestModuleAction implements ClassAction
{
    private StringBuffer modifierBuf = new StringBuffer();
    
    public Object addModifier(String modifier)
    {
        modifierBuf.append(modifier);
        modifierBuf.append(" ");
        return modifierBuf.toString();
    }
    
    public Object setClassModifiers(Object modifiers)
    {
        buffer.append("(modifiers ");
        buffer.append(modifiers);
        buffer.append(") ");
        return modifiers;
    }

    public String setClassName(String simpleName)
    {
        String str = "(class " + simpleName + ") ";
        buffer.append(str);
//        if (modifierBuf.length() > 0)
//        {
//            module.append("(modifiers ");
//            module.append(modifierBuf.toString());
//            module.append(") ");
//        }
        return str;
    }

    public Object declareExtends(String className)
    {
        String str = "(extends " + className + ") ";
        buffer.append(str);
        return str;
    }

    public Object declareImplements(String className)
    {
        String str = "(implements " + className + ") ";
        buffer.append(str);
        return str;
    }
}
