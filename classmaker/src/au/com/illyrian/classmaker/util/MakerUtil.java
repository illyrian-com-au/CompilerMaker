package au.com.illyrian.classmaker.util;

import au.com.illyrian.classmaker.ClassMakerConstants;

public class MakerUtil {

    //////////// Method name methods ////////////
    
    public static int fromModifierString(String value) {
        if ("public".equals((value)))
            return ClassMakerConstants.ACC_PUBLIC;
        else if ("protected".equals((value)))
            return ClassMakerConstants.ACC_PROTECTED;
        else if ("private".equals((value)))
            return ClassMakerConstants.ACC_PRIVATE;
        else if ("static".equals((value)))
            return ClassMakerConstants.ACC_STATIC;
        else if ("final".equals((value)))
            return ClassMakerConstants.ACC_FINAL;
        else if ("synchronized".equals((value)))
            return ClassMakerConstants.ACC_SYNCHRONIZED;
        else if ("volatile".equals((value)))
            return ClassMakerConstants.ACC_VOLATILE;
        else if ("transient".equals((value)))
            return ClassMakerConstants.ACC_TRANSIENT;
        else if ("native".equals((value)))
            return ClassMakerConstants.ACC_NATIVE;
        else if ("abstract".equals((value)))
            return ClassMakerConstants.ACC_ABSTRACT;
        else if ("strictfp".equals((value)))
            return ClassMakerConstants.ACC_STRICTFP;
        else
            throw new IllegalArgumentException("Invalid modifier: " + value);
    }

    /**
     * Creates a white space separated string of modifiers from a bit mask.
     * 
     * @param modifiers
     *            a bit mask of modifiers.
     * @return a white space separated string of modifier names
     */
    public static String toModifierString(int modifiers) {
        StringBuffer buf = new StringBuffer();
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_PUBLIC, "public");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_PROTECTED, "protected");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_PRIVATE, "private");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_STATIC, "static");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_FINAL, "final");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_SYNCHRONIZED, "synchronized");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_VOLATILE, "volatile");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_TRANSIENT, "transient");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_NATIVE, "native");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_ABSTRACT, "abstract");
        appendModifier(buf, modifiers, ClassMakerConstants.ACC_STRICTFP, "strictfp");
        return buf.toString();
    }

    /* Appends a modifier to a StringBuffer. */
    public static void appendModifier(StringBuffer buf, int modifiers, int expected, String name) {
        if ((modifiers & expected) != 0) {
            buf.append(name).append(" ");
        }
    }

    /* Append a string to the StringBuffer placing a separator before subsequent values. */
    public static void appendStrings(StringBuffer buf, String value, String separator) {
        if (buf.length() > 0)
            buf.append(separator);
        buf.append(value);
    }

    /////////// Class name manipulator methods ////////////

    /**
     * Converts a <code>Class</code> into a class name.
     * 
     * @param javaClass
     *            the class from which to derive the name
     * @return a fully qualified class name delimited by dots
     */
    public static String classToName(Class javaClass) {
        if (javaClass.isArray())
            return toDotName(javaClass.getCanonicalName());
        else
            return toDotName(javaClass.getName());
    }

    /**
     * Converts a <code>Class</code> into a class name.
     * 
     * @param javaClass
     *            the class from which to derive the name
     * @return a fully qualified class name delimited by slashes
     */
    public static String classToSlashName(Class javaClass) {
        if (javaClass.isArray())
            return toSlashName(javaClass.getCanonicalName());
        else
            return toSlashName(javaClass.getName());
    }

    /**
     * Converts dots into slashes in a class name.
     * 
     * @param name
     *            the name to be converted
     * @return the name with dots replaced by slashes
     */
    public static String toSlashName(String name) {
        return name.replace('.', '/');
    }

    /**
     * Converts slashes into dots in a class name.
     * 
     * @param name
     *            the name to be converted
     * @return the name with slashes replaced by dots
     */
    public static String toDotName(String name) {
        return name.replace('/', '.');
    }

    /**
     * Converts a type Class into a signature.
     * 
     * @param javaClass
     *            a class representing a type from which to derive the signature
     * @return a JVM signature
     */
    public static String classToSignature(Class javaClass) {
        if (javaClass.isArray())
            return toSlashName(javaClass.getName());
        else if (javaClass.isPrimitive()) {
            if ("int".equals(javaClass.getName()))
                return "I";
            if ("float".equals(javaClass.getName()))
                return "F";
            if ("long".equals(javaClass.getName()))
                return "J";
            if ("void".equals(javaClass.getName()))
                return "V";
            if ("byte".equals(javaClass.getName()))
                return "B";
            if ("char".equals(javaClass.getName()))
                return "C";
            if ("double".equals(javaClass.getName()))
                return "D";
            if ("short".equals(javaClass.getName()))
                return "S";
            if ("boolean".equals(javaClass.getName()))
                return "Z";
            // Should never get here.
            throw new IllegalArgumentException("Could not determine signature for primitive: " + javaClass.getName());
        } else
            return "L" + classToSlashName(javaClass) + ";";
    }
}
