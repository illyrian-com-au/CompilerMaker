package au.com.illyrian.domainparser;

public class TestClassAction extends TestModuleAction implements ClassAction
{
    private StringBuffer modifierBuf = new StringBuffer();
    private boolean hasImplements = false;
    
    public Object addModifier(String modifier)
    {
        modifierBuf.append(modifier);
        modifierBuf.append(" ");
        return modifierBuf.toString();
    }
    
    public Object setClassModifiers(Object modifiers)
    {
        buffer.append(modifiers);
        return modifiers;
    }

    public String setClassName(String simpleName)
    {
        String str = "class " + simpleName + " ";
        buffer.append(str);
        return str;
    }

    public Object declareExtends(String className)
    {
        String str = "extends " + className + " ";
        buffer.append(str);
        return str;
    }

    public Object declareImplements(String className)
    {
        String str = "implements " + className + " ";
        if (hasImplements) {
            str = className + " ";
        }
        hasImplements = true;
        buffer.append(str);
        return str;
    }
    
    public Object declareMembers(Object members)
    {
        String str = members.toString();
        buffer.append("{\n");
        buffer.append(str);
        buffer.append("\n}");
        return str;
    }
}
