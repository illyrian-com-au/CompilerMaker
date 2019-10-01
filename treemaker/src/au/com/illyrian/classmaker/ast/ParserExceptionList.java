package au.com.illyrian.classmaker.ast;

import java.util.Vector;

import au.com.illyrian.classmaker.ClassMakerException;

public class ParserExceptionList {
    private static final ClassMakerException [] ERRORS_PROTO = new ClassMakerException[0];
    public Vector<ClassMakerException> errorList;

    public ParserExceptionList() {
        this.errorList = new Vector <ClassMakerException>();
    }
    

    public boolean hasErrors()
    {
        return !errorList.isEmpty();
    }

    public ClassMakerException [] getErrors()
    {
        ClassMakerException [] list = errorList.toArray(ERRORS_PROTO);
        return list;
    }
    
    public void addError(ClassMakerException ex)
    {
        errorList.add(ex);
    }

}