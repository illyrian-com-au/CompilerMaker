package au.com.illyrian.jesub.ast;

import java.util.Vector;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.ResolvePath;

public class AstVisitorClassMakerFactory extends AstStructureVisitor
{
    static final ClassMaker [] CLASSES_PROTO = new ClassMaker[0]; 
    final ClassMakerFactory factory;
    ResolvePath packageName;
    AstExpression importsList;
    Vector <ClassMaker>      classMakers = new Vector<ClassMaker>(); 
    Vector <AstDeclareClass> declareClasses = new Vector<AstDeclareClass>(); 

    public AstVisitorClassMakerFactory(ClassMakerFactory factory)
    {
        this.factory = factory;
    }
    
    public ClassMakerFactory getFactory()
    {
        return factory;
    }

    public ResolvePath getPackageName()
    {
        return packageName;
    }

    public AstExpression getImportsList()
    {
        return importsList;
    }
    
    void addClassMaker(ClassMaker maker)
    {
        classMakers.add(maker);
    }
    
    public ClassMaker[] getClassMakers()
    {
        return classMakers.toArray(CLASSES_PROTO);
    }

    public Class[] defineClasses()
    {
        if (hasErrors())
            throw new IllegalStateException("Compilation errors prevent production of classes.");
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

    public void resolveDeclaration(AstDeclareModule unit)
    {
        // Store the package and imports to be added to each generated class.
        packageName = unit.getPackageName();
        importsList = unit.getImportsList();
        
        if (unit.getClassList() != null)
        {
            unit.getClassList().resolveDeclaration(this);
            
            factory.setPass(ClassMaker.FIRST_PASS);
            for (int i=0; i<declareClasses.size(); i++)
                firstPass(declareClasses.get(i), classMakers.get(i));

            factory.setPass(ClassMaker.SECOND_PASS);
            for (int i=0; i<declareClasses.size(); i++)
                secondPass(declareClasses.get(i), classMakers.get(i));
        }
    }

    public void resolveDeclaration(AstDeclareClass unit)
    {
        // Add to list of AstDeclareClass instances
        declareClasses.add(unit);
        
        // Add to list of ClassMaker instances
        ClassMaker maker = factory.createClassMaker(this);
        addClassMaker(maker);
    }

    public void firstPass(AstDeclareClass unit, ClassMaker maker)
    {
        setMaker(maker);

        if (getPackageName() != null)
        {
            String name = getPackageName().resolvePath(this);
            getMaker().setPackageName(name);
        }
        if (getImportsList() != null)
            getImportsList().resolveImport(this);

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
    
    public void secondPass(AstDeclareClass unit, ClassMaker maker)
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
