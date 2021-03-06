package au.com.illyrian.classmaker.types;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;

public class MakerClassType extends ClassType
{
    private final ClassMaker classMaker;
    /**
     * Constructor for a <code>ClassType</code> generated by a ClassMaker instance.
     * @param className the name of the class
     */
    public MakerClassType(ClassMaker classMaker)
    {
        super(classMaker.getFullyQualifiedClassName());
        this.setFactory(classMaker.getFactory());
        this.classMaker = classMaker;
    }

    public ClassMaker getClassMaker() {
        return classMaker;
    }

    @Override
    public ClassType populateExtendsType() {
        return classMaker.getExtendsType();
    }
    
    @Override
    protected MakerMethod [] populateConstructors() {
        return classMaker.getDeclaredConstructors();
    }

    @Override
    protected MakerMethod [] populateDeclaredMethods() {
        return classMaker.getDeclaredMethods();
    }

    @Override
    protected ClassType[] populateDeclaredInterfaces() {
        return classMaker.getDeclaredInterfaces();
    }

    @Override
    protected MakerField [] populateDeclaredFields() {
        return classMaker.getDeclaredFields();
    }

    public String getSimpleName() {
        return classMaker.getSimpleClassName();
    }
}
