package au.com.illyrian.classmaker.proxy;

public interface InterfaceReference<T> {
    void setReference(T ref);
    T getReference();
    void apply();
}
