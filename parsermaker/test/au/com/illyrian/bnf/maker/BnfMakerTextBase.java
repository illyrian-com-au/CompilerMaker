package au.com.illyrian.bnf.maker;

import junit.framework.TestCase;

public class BnfMakerTextBase extends TestCase
{
    public void testBase() {
    }
    
    // String functions
    String match(String token) {
        return "Call(This(), \"match\", Push(Get(This(), \"" + token + "\")))";
    }
    String match(String token, int ahead) {
        return "Call(This(), \"match\", Push(Get(This(), \"" + token + "\")).Push(Literal(" + ahead + ")))";
    }
    String expect(String token) {
        return "Call(This(), \"expect\", Push(Get(This(), \"" + token + "\")))";
    }
    String error(String message) {
        return "  Eval(Call(This(), \"error\", Push(Literal(\"" + message + "\"))));\n";
    }
    String assign(String name, String value) {
        return "  Eval(Assign(\"" + name + "\", " + value + "));\n";
    }
    String assign$0(String value) {
        return assign("$0", value);
    }
    String assign$0$1() {
        return assign$0("Get(\"$1\")");
    }
     String set(String name, String value) {
        return set(name, "java.lang.String", value);
    }
    String set(String name, String type, String value) {
        return declare(name, type) + assign(name, value);
    }
    String call(String name) {
        return call(name, "Push()");
    }
    String call(String name, String parameters) {
        return call("This()", name, parameters);
    }
    String call(String reference, String name, String parameters) {
        return "Call(" + reference + ", \"" + name + "\", " + parameters + ")";
    }
    String declare(String name, String type) {
        return "  Declare(\"" + name + "\", \"" + type + "\", 0);\n";
    }
    String ifMatchThen(String token, String thenCode) {
        return ifThen(match(token), thenCode);
    }
    String ifThen(String cond, String thenCode) {
        return "  If(" + cond + ");\n  Begin();\n" + thenCode + "  End();\n  EndIf();\n";
    }
    String ifMatchThenElse(String token, String thenCode, String elseCode) {
        return ifThenElse(match(token), thenCode, elseCode);
    }
    String ifThenElse(String cond, String thenCode, String elseCode) {
        return "  If(" + cond + ");\n  Begin();\n"  + thenCode 
                + "  End();\n  Else();\n  Begin();\n" + elseCode 
                + "  End();\n  EndIf();\n";
    }
    String orElse(String cond) {
        return "OrElse(" + cond + ")";
    }
    String orElse(String prev, String cond) {
        return "OrElse(" + prev + ", " + cond + ")";
    }
    String andThen(String cond) {
        return "AndThen(" + cond + ")";
    }
    String andThen(String prev, String cond) {
        return "AndThen(" + prev + ", " + cond + ")";
    }
    String logic(String prev, String cond) {
        return "Logic(" + prev + ", " + cond + ")";
    }
    String beginMethod(String methodName, String returnType) {
        return "Method(\"" + methodName + "\", \"" + returnType + "\", ACC_PUBLIC)\n"
                + "  Begin();\n"
                + "  Declare(\"$0\", \"" + returnType + "\", 0);\n";
    }
    String endMethod() {
        return "  Return(Get(\"$0\"));\n"
                + "  End();\n";        
    }
    
}
