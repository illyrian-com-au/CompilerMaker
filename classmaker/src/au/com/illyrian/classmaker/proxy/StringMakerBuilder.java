package au.com.illyrian.classmaker.proxy;

import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.types.Value;

public class StringMakerBuilder {
    private final ClassMakerIfc maker;
    private Value stringBuilder;

    public StringMakerBuilder(ClassMakerIfc maker) {
        this.maker = maker;
        stringBuilder = createStringBuilder();
    }
    
    ClassMakerIfc getMaker() {
        return maker;
    }
    
    Value createStringBuilder() {
        return maker.New(StringBuffer.class).Init(null);
    }

    StringMakerBuilder append(Value value) {
        stringBuilder = maker.Call(stringBuilder, "append", maker.Push(value));
        return this;
    }
    
    StringMakerBuilder append(String literal) {
        return append(maker.Literal(literal));
    }
    
    Value build() {
        return maker.Call(stringBuilder, "toString", maker.Push());
    }
}
