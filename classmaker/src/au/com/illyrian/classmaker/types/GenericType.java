package au.com.illyrian.classmaker.types;

public class GenericType extends ClassType {
    ParameterType [] parameterTypes;
    
    public GenericType(Class javaClass) {
        super(javaClass);
    }
    
    public GenericType(java.lang.reflect.ParameterizedType paraType) {
        super(getRawClass(paraType));
        java.lang.reflect.Type [] types = paraType.getActualTypeArguments();
    }

    public GenericType toGeneric() {
        return this;
    }
    
    static Class getRawClass(java.lang.reflect.ParameterizedType paraType) {
        java.lang.reflect.Type rawType = paraType.getRawType();
        return rawType instanceof Class ? (Class)rawType : null;
    }
    
    /** The list of interfaces implemented by this class. */
    public ParameterType [] getParameterTypes()
    {
        if (parameterTypes == null) {
            return populateParameterTypes();
        }
        return parameterTypes;
    }
    
    ParameterType [] populateParameterTypes() {
        if (javaClass != null) {
            ParameterType [] parameters = getFactory().getParameterTypes(this);
            setParameterTypes(parameters);
        }
        return parameterTypes;
    }

    /** Sets the list of interfaces implemented by this class. */
    public void setParameterTypes(ParameterType [] interfaces)
    {
        this.parameterTypes = interfaces;
    }
    
    public ParameterType findParameter(String name) {
        for (ParameterType type : getParameterTypes()) {
            if (name.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }
    
    public String getGenericName() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append('<');
        ParameterType [] paras = getParameterTypes();
        if (paras != null) {
            for (int i=0; i<paras.length; i++) {
                if (i>0) {
                    builder.append(", ");
                }
                builder.append(paras[i].getActualName());
            }
        } else {
            builder.append("...");
        }
        builder.append('>');
        return builder.toString();
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append('(');
        builder.append(getGenericName());
        builder.append(')');
        return builder.toString();
    }
}
