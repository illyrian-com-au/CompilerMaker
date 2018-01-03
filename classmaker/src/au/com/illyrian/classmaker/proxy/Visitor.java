package au.com.illyrian.classmaker.proxy;

public interface Visitor<T> {
    void visit(T element);
}
