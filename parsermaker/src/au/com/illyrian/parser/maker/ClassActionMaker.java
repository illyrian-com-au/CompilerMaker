package au.com.illyrian.parser.maker;

import au.com.illyrian.domainparser.ClassAction;

public class ClassActionMaker extends ModuleActionMaker implements ClassAction
{
    private int modifiers = 0;
    
    public Object addModifier(String modifierName)
    {
        modifiers = maker.addModifier(modifiers, modifierName);
        return Integer.valueOf(modifiers);
    }

    public Object setClassModifiers(Object modifiers)
    {
        maker.setClassModifiers((Integer)modifiers);
        return modifiers;
    }

    public String setClassName(String simpleName)
    {
        maker.setSimpleClassName(simpleName);
        return simpleName;
    }

    public Object declareExtends(String className)
    {
        maker.Extends(className);
        return className;
    }

    public Object declareImplements(String className)
    {
        maker.Implements(className);
        return className;
    }
    
    @Override
    public Object declareMembers(Object members)
    {
        return members;
    }
}
