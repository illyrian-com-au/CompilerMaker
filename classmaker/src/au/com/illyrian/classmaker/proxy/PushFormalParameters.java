package au.com.illyrian.classmaker.proxy;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;

public class PushFormalParameters implements Visitor<MakerMethod> {
    public static final String RETURN_VAR = "$0";
    public static final int ABSTRACT_MASK = ~ClassMakerConstants.ACC_ABSTRACT;

    protected final ClassMaker maker;

    public PushFormalParameters(ClassMaker maker, Visitor<MakerMethod> visitor) {
        this.maker = maker;
    }
    
    public ClassMaker getClassMaker() {
        return maker;
    }
    
    public void visit(MakerMethod method) {
        pushParams(method.getFormalFields());
    }
    
    public CallStack pushParams(MakerField [] params) {
        CallStack stack = maker.Push();
        for (MakerField field : params) {
            stack.Push(maker.Get(field.getName()));
        }
        return stack;
    }
}
