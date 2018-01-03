package au.com.illyrian.classmaker.types;

public class ParameterType extends Type {
    private final ClassType parentClassType;
    private final ClassType boundType;
    private ClassType actualType;

    public ParameterType(ClassType classType, String name, ClassType boundType) {
        super(name, boundType.getSignature());
        this.parentClassType = classType;
        this.boundType = boundType;
    }

    public ParameterType toParameter() {
        return this;
    }
    
    public ClassType getParentClassType() {
        return parentClassType;
    }
    
    public ClassType getBoundType() {
        return boundType;
    }
    
    public ClassType getActualType() {
        return actualType;
    }

    public void setActualType(ClassType actualType) {
        this.actualType = actualType;
    }
    
    public String getActualName() {
        return (actualType != null) ? actualType.getName() : getName();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('<');
        buf.append(getName());
        if (actualType != null) {
            buf.append('=');
            buf.append(actualType.getName());
        }
        buf.append('>');
        return buf.toString();
    }
}
