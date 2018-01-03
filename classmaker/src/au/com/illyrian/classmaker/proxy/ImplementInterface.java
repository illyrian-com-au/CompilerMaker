package au.com.illyrian.classmaker.proxy;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.members.MakerMethodCollector;
import au.com.illyrian.classmaker.types.ClassType;

public class ImplementInterface implements Visitor<ClassType> {
    public static final String RETURN_VAR = "$0";

    protected final ClassMaker maker;
    private final Visitor<MakerMethod> visitor;

    public ImplementInterface(ClassMaker maker, Visitor<MakerMethod> visitor) {
        this.maker = maker;
        this.visitor = visitor;
    }
    
    public ClassMaker getClassMaker() {
        return maker;
    }
    
    public void visit(Class<?> javaClass) {
        ClassType classType = maker.classToClassType(javaClass);
        visit(classType);
    }
    
    public void visit(ClassType classType) {
        maker.Implements(classType.getName()); // FIXME Implements(ClassType)
        implementMethods(classType);
    }
    
    public void implementMethods(ClassType classType) {
        MakerMethodCollector methods = new MakerMethodCollector();
        methods.includeInterfaceMethods(classType);
        for (MakerMethod method : methods.toArray()) {
            visitMethod(method);
        }
    }

    public void visitMethod(MakerMethod method) {
        visitor.visit(method);
    }
}
