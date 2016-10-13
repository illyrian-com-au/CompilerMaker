package au.com.illyrian.parser.maker;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerLocation;
import au.com.illyrian.parser.impl.ModuleContext;

public class ModuleContextMaker extends ModuleContext implements ClassMakerLocation
{
    private ClassMaker maker = null;
    
    public ModuleContextMaker()
    {
    }

    public void setClassMaker(ClassMaker classMaker)
    {
        maker = classMaker;
    }

    public ClassMaker getClassMaker()
    {
        if (maker == null)
            throw new NullPointerException("ClassMaker is null.");
        return maker;
    }

    public ClassLoader getClassLoader()
    {
        return getClassMaker().getFactory().getClassLoader();
    }

    public void visitParser(Object object)
    {
        super.visitParser(object);
        if (object instanceof ClassMakerLocation)
        {
            visit((ClassMakerLocation)object);
        }
    }

    public void visit(ClassMakerLocation location)
    {
        location.setClassMaker(getClassMaker());
    }
}
