package au.com.illyrian.classmaker.proxy;

import au.com.illyrian.classmaker.types.Type;

public interface Visitor<T> {
    Type visit(T element);
}
