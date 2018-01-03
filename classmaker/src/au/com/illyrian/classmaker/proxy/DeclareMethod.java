package au.com.illyrian.classmaker.proxy;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;

public class DeclareMethod implements Visitor<MakerMethod> {
    public static final String RETURN_VAR = "$0";
    public static final int ABSTRACT_MASK = ~ClassMakerConstants.ACC_ABSTRACT;

    protected final ClassMaker maker;
    private final Visitor<MakerMethod> visitor;

    public DeclareMethod(ClassMaker maker, Visitor<MakerMethod> visitor) {
        this.maker = maker;
        this.visitor = visitor;
    }
    
    public ClassMaker getClassMaker() {
        return maker;
    }
    
    public void visit(MakerMethod method) {
        methodBegin(method);
        methodBody(method);
        methodEnd(method);
    }

    public String param(int i) {
        return "$" + i;
    }
    
    public void methodBegin(MakerMethod method) {
        int modifiers = method.getModifiers() & ABSTRACT_MASK;
        maker.Method(method.getName(), method.getReturnType(), modifiers);
        // Declare the formal parameters.
        int i = 0;
        for (Type type : method.getFormalTypes()) {
            maker.Declare(param(++i), type, 0);
        }
        maker.Begin();
        // Declare a local variable for the return value.
        if (!ClassMakerFactory.VOID_TYPE.equals(method.getReturnType())) {
            maker.Declare(RETURN_VAR, method.getReturnType(), 0);
        }
    }
    
    public void methodBody(MakerMethod method) {
        visitor.visit(method);
    }
    
    public void methodResult(Value result) {
        if (!ClassMakerFactory.VOID_TYPE.equals(result.getType())) {
            maker.Eval(maker.Assign(RETURN_VAR, result));
        }
    }
    
    public void methodEnd(MakerMethod method) {
        if (ClassMakerFactory.VOID_TYPE.equals(method.getReturnType())) {
            maker.Return();
        } else {
            maker.Return(maker.Get(RETURN_VAR));
        }
        maker.End();
     }
}
