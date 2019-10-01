package au.com.illyrian.jesub.ast;

import java.util.Vector;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;

public class AstVisitorClassMakerFactory extends AstStructureVisitor
{
    static final ClassMaker [] CLASSES_PROTO = new ClassMaker[0]; 
    final ClassMakerFactory factory;
    AstStructure  packageDec;
    AstStructure  importsList;
    Vector <ClassMaker>      classMakers = new Vector<ClassMaker>(); 
    Vector <AstClass> declareClasses = new Vector<AstClass>(); 

    public AstVisitorClassMakerFactory(ClassMakerFactory factory)
    {
        this.factory = factory;
    }
    
    public ClassMakerFactory getFactory()
    {
        return factory;
    }

    public AstStructure getPackageDec()
    {
        return packageDec;
    }

    public AstStructure getImportsList()
    {
        return importsList;
    }
    
    void addClassMaker(ClassMaker maker)
    {
        classMakers.add(maker);
    }
    
    public ClassMakerConstants[] getClassMakers()
    {
        return classMakers.toArray(CLASSES_PROTO);
    }

    public Class[] defineClasses()
    {
//        if (hasErrors())
//            throw new IllegalStateException("Compilation errors prevent production of classes.");
        Class [] classes = new Class[classMakers.size()];
        for (int i=0; i<classMakers.size(); i++)
            classes[i] = classMakers.get(i).defineClass();
        return classes;
    }

    public Class defineClass()
    {
        Class [] classes = defineClasses();
        return classes[0];
    }

    public void resolveDeclaration(AstModule unit)
    {
        // Store the package and imports to be added to each generated class.
        packageDec = unit.getPackage();
        importsList = unit.getImportsList();
        
        if (unit.getClassList() != null)
        {
            unit.getClassList().resolveDeclaration(this);
            
            factory.setPass(ClassMakerConstants.FIRST_PASS);
            for (int i=0; i<declareClasses.size(); i++)
                firstPass(declareClasses.get(i), classMakers.get(i));

            factory.setPass(ClassMakerConstants.SECOND_PASS);
            for (int i=0; i<declareClasses.size(); i++)
                secondPass(declareClasses.get(i), classMakers.get(i));
        }
    }

    public void resolveDeclaration(AstClass unit)
    {
        // Add to list of AstClass instances
        declareClasses.add(unit);
        String className = unit.getClassName().getName();
        
        // Add to list of ClassMaker instances
        ClassMaker maker = factory.createClassMaker();
        maker.setSourceLine(this);
        addClassMaker(maker);
        maker.setSimpleClassName(className);
        maker.getFullyQualifiedClassName();
    }
    
    public void resolveDeclaration(AstPackage unit)
    {
        String packageName = unit.getExpression().resolvePath(this);
        getMaker().setPackageName(packageName);
    }

    public void firstPass(AstClass unit, ClassMaker maker)
    {
        setMaker(maker);

        if (getPackageDec() != null) {
            getPackageDec().resolveDeclaration(this);
        }
        if (getImportsList() != null) {
            getImportsList().resolveImport(this);
        }
        // FIXME - modifiers should be added incrementally
        int modifiers = resolveModifiers(unit.getModifiers());
        getMaker().setClassModifiers(modifiers);
        String className = unit.getClassName().resolvePath(this);
        getMaker().setSimpleClassName(className);
        resolveExtends(unit.getExtends());
        if (unit.getImplementsList() != null)
            unit.getImplementsList().resolveImplements(this);
        if (unit.getMembers() != null)
            unit.getMembers().resolveDeclaration(this);
        getMaker().EndClass();
    }
    
    public void secondPass(AstClass unit, ClassMaker maker)
    {
        setMaker(maker);

        int modifiers = resolveModifiers(unit.getModifiers());
        getMaker().setClassModifiers(modifiers);
        String className = unit.getClassName().resolvePath(this);
        getMaker().setSimpleClassName(className);
        resolveExtends(unit.getExtends());
        if (unit.getImplementsList() != null)
            unit.getImplementsList().resolveImplements(this);
        if (unit.getMembers() != null)
            unit.getMembers().resolveDeclaration(this);
        getMaker().EndClass();
    }
}
