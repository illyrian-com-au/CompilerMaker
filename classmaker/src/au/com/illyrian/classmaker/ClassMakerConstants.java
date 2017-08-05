package au.com.illyrian.classmaker;

public interface ClassMakerConstants
{

    /** Bitmask for <code>public</code> access modifier */
    public static final short ACC_PUBLIC = 0x0001;
    /** Bitmask for <code>private</code> access modifier */
    public static final short ACC_PRIVATE = 0x0002;
    /** Bitmask for <code>protected</code> access modifier */
    public static final short ACC_PROTECTED = 0x0004;
    /** Bitmask for <code>static</code> access modifier */
    public static final short ACC_STATIC = 0x0008;
    /** Bitmask for <code>final</code> class and field modifier */
    public static final short ACC_FINAL = 0x0010;
    /** Bitmask for <code>synchronized</code> method modifier */
    public static final short ACC_SYNCHRONIZED = 0x0020;
    /** Bitmask for <code>volatile</code> field modifier */
    public static final short ACC_VOLATILE = 0x0040;
    /** Bitmask for <code>transient</code> field modifier */
    public static final short ACC_TRANSIENT = 0x0080;
    /** Bitmask for <code>native</code> method modifier */
    public static final short ACC_NATIVE = 0x0100;
    /** Bitmask for <code>interface</code> class modifier */
    public static final short ACC_INTERFACE = 0x0200;
    /** Bitmask for <code>abstract</code> class and method modifier */
    public static final short ACC_ABSTRACT = 0x0400;
    /** Bitmask for strict floating point class modifier */
    public static final short ACC_STRICTFP = 0x0800;
    /** Bitmask to test for package visibility. Alias for zero (0). */
    public static final short ACC_PACKAGE = 0;

    /** Bitmask of valid access modifiers */
    public static final int MASK_ACCESS = ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE;
    /** Bitmask of valid class modifiers */
    public static final int MASK_CLASS = MASK_ACCESS | ACC_ABSTRACT | ACC_FINAL | ACC_STRICTFP;
    /** Bitmask of valid interface modifiers */
    public static final int MASK_INTERFACE = ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE;
    /** Bitmask of valid field modifiers */
    public static final int MASK_FIELD = MASK_ACCESS | ACC_STATIC | ACC_FINAL | ACC_TRANSIENT | ACC_VOLATILE;
    /** Bitmask of valid method modifiers */
    public static final int MASK_METHOD = MASK_ACCESS | ACC_STATIC | ACC_FINAL | ACC_ABSTRACT | ACC_SYNCHRONIZED
            | ACC_NATIVE | ACC_STRICTFP;

    /**
     * Enumeration to indicate that byte-code will be generated in one pass
     * (default).
     */
    public static final int ONE_PASS = 0;
    /**
     * Enumeration to indicate that this is the first pass of two pass byte-code
     * generation.
     */
    public static final int FIRST_PASS = 1;
    /**
     * Enumeration to indicate that this is the second pass of two pass
     * byte-code generation.
     */
    public static final int SECOND_PASS = 2;
    /** Enumeration to indicate that byte-code generation is complete. */
    public static final int COMPLETED_PASS = -1;

}