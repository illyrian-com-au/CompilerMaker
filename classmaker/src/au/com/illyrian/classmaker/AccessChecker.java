package au.com.illyrian.classmaker;

import au.com.illyrian.classmaker.members.MakerField;
import au.com.illyrian.classmaker.members.MakerMethod;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.MakerClassType;
import au.com.illyrian.classmaker.util.MakerUtil;

public class AccessChecker implements ClassMakerConstants {
    /**
     * Bit-mask of method modifiers that are incompatible with the <code>abstract</code> modifier.
     */
    private static final int MASK_INCOMPATABLE_WITH_ABSTRACT_METHOD = ACC_STATIC | ACC_FINAL | ACC_SYNCHRONIZED
            | ACC_NATIVE | ACC_STRICTFP;

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
    
    final ClassMaker maker;
    
    AccessChecker(ClassMaker maker) {
        this.maker = maker;
    }
    
    public MakerClassType getClassType() {
        return maker.getClassType();
    }
    
    public ClassMakerException createException(String key, String ... values) {
        return maker.createException(key, values);
    }
    
    public ClassMakerFactory getFactory() {
        return maker.getFactory();
    }
    
    /**
     * Determine whether the generated class can access the field in the given class.
     * 
     * @param classType the type of the class being accessed
     * @param field the field being accessed
     */
    void checkAccessDenied(ClassType classType, MakerField field) {
        // Determine whether the class is accessible
        if (isAccessDenied(getClassType(), classType, classType.getModifiers()))
            throw createException("ClassMaker.AccessDeniedToClass_1", classType.getName());
        // Determine whether the field is accessible
        if (isAccessDenied(getClassType(), field.getClassType(), field.getModifiers()))
            throw createException("ClassMaker.AccessDeniedToField_2", classType.getName(), field.getName());
        // Determine whether a protected field is accessible
        if (isAccessDeniedToProtected(getClassType(), classType, field.getModifiers()))
            throw createException("ClassMaker.AccessDeniedToProtectedField_2", classType.getName(), field.getName());
    }

    /**
     * Determine whether the generated class can access the method in the given class.
     * 
     * @param classType the type of the class being accessed
     * @param method the method being accessed
     */
    void checkAccessDenied(ClassType classType, MakerMethod method) {
        // Determine whether the class is accessible
        if (isAccessDenied(getClassType(), classType, classType.getModifiers()))
            throw createException("ClassMaker.AccessDeniedToClass_1", classType.getName());
        // Determine whether the method is accessible
        if (isAccessDenied(getClassType(), method.getClassType(), method.getModifiers()))
            throw createException("ClassMaker.AccessDeniedToMethod_2", classType.getName(), method.toString());
        // Determine whether a protected method is accessible
        if (isAccessDeniedToProtected(getClassType(), classType, method.getModifiers()))
            throw createException("ClassMaker.AccessDeniedToProtectedMethod_2", classType.getName(), method.toString());
    }

    /**
     * Determines whether access is denied to a protected method or field.
     * </br>
     * If the method or field is protected and not static this method ensures
     * that the called class must be of the same type as the caller class or a
     * sub-type of it.
     * 
     * @param caller the caller is the class doing the access
     * @param called the class being accessed
     * @param modifiers the access modifiers of the method or field being accessed
     * @return true if the caller class is denied access to the method or field
     */
    boolean isAccessDeniedToProtected(ClassType caller, ClassType called, int modifiers) {
        // Test does not apply to static fields.
        if ((modifiers & ClassMakerConstants.ACC_STATIC) == ClassMakerConstants.ACC_STATIC)
            return false;
        modifiers &= MASK_ACCESS;
        // FIXME - include explanation and reference 
        return ((modifiers == ClassMakerConstants.ACC_PROTECTED) && 
                !caller.getPackageName().equals(called.getPackageName()) && 
                !getFactory().getAssignmentConversion().isWideningClassConvertable(called, caller));
    }

    /**
     * Determines whether access is denied to a class, member method or member
     * field.
     * </br>
     * This method is used to determine whether:
     * <ul>
     * <li>a class is accessible by providing the class modifiers</li>
     * <li>a method within a class is accessible by providing the method
     * modifiers</li>
     * <li>a field within a class is accessible by providing the field
     * modifiers.</li>
     * </ul>
     * This method ensures the following access restrictions.
     * <UL>
     * <LI>A <code>public</code> class or member is accessible from anywhere.</LI>
     * <LI>A <code>protected</code> class or member is accessible from the same
     * package or a derived class.</LI>
     * <LI>A <code>package</code> class or member has no access modifier and is
     * accessible from the same package.</LI>
     * <LI>A <code>private</code> class or member is accessible from within its
     * own class.</LI>
     * </UL>
     * 
     * @param caller the caller is the class doing the access
     * @param called the class being accessed
     * @param modifiers the access modifiers of the class, method or field being
     *            accessed
     * @return true if the caller class is denied access to the class, method or
     *         field
     */
    boolean isAccessDenied(ClassType caller, ClassType called, int modifiers) {
        modifiers &= MASK_ACCESS;
        if (modifiers == ClassMakerConstants.ACC_PUBLIC)
            return false;
        else if (((modifiers == ClassMakerConstants.ACC_PACKAGE) || (modifiers == ClassMakerConstants.ACC_PROTECTED))
                && caller.getPackageName().equals(called.getPackageName()))
            return false;
        else if ((modifiers == ClassMakerConstants.ACC_PROTECTED)
                && getFactory().getAssignmentConversion().isConvertable(caller, called))
            return false;
        else if ((modifiers == ClassMakerConstants.ACC_PRIVATE) && caller.equals(called))
            return false;
        return true;
    }
    

    /**
     * Check that the bit mask only contains valid class modifiers. <br/>
     * The following are valid modifiers for a class.
     * <ul>
     * <li><code>ClassMaker.ACC_PUBLIC</code></li>
     * <li><code>ClassMaker.ACC_PROTECTED</code></li>
     * <li><code>ClassMaker.ACC_PRIVATE</code></li>
     * <li><code>ClassMaker.ACC_ABSTRACT</code></li>
     * <li><code>ClassMaker.ACC_FINAL</code></li>
     * <li><code>ClassMaker.ACC_STRICTFP</code></li>
     * <li>zero</li>
     * </ul>
     * 
     * @param modifiers
     *            the bit mask of modifiers
     * @throws ClassMakerException
     *             if the bit mask includes an invalid modifier
     */
    protected void checkClassModifiers(int modifiers) throws ClassMakerException {
        int wrongModifiers = modifiers & (~MASK_CLASS);
        if (wrongModifiers != 0)
            throw createException("ClassMaker.InvalidClassModifier_1", MakerUtil.toModifierString(wrongModifiers));
        checkMultipleAccessModifiers(modifiers);
        if (((modifiers & ClassMakerConstants.ACC_FINAL) == ClassMakerConstants.ACC_FINAL)
                && ((modifiers & ClassMakerConstants.ACC_ABSTRACT) == ClassMakerConstants.ACC_ABSTRACT))
            throw createException("ClassMaker.InvalidClassModifierCombination");
    }

    /**
     * Check that the bit mask only contains valid interface modifiers. <br/>
     * The following are valid modifiers for an interface.
     * <ul>
     * <li><code>ClassMaker.ACC_PUBLIC</code></li>
     * <li><code>ClassMaker.ACC_ABSTRACT</code></li>
     * <li><code>ClassMaker.ACC_INTERFACE</code></li>
     * <li>zero</li>
     * </ul>
     * 
     * @param modifiers
     *            the bit mask of modifiers
     * @throws ClassMakerException
     *             if the bit mask includes an invalid modifier
     */
    protected void checkInterfaceModifiers(int modifiers) throws ClassMakerException {
        int wrongModifiers = modifiers & (~MASK_INTERFACE);
        if (wrongModifiers != 0)
            throw createException("ClassMaker.InvalidInterfaceModifier_1", MakerUtil.toModifierString(wrongModifiers));
        checkMultipleAccessModifiers(modifiers);
    }

    /**
     * Check that the bit mask only contains valid method modifiers.
     * </br>
     * The following are valid modifiers for a member method.
     * <ul>
     * <li><code>ClassMaker.ACC_PUBLIC</code></li>
     * <li><code>ClassMaker.ACC_PROTECTED</code></li>
     * <li><code>ClassMaker.ACC_PRIVATE</code></li>
     * <li><code>ClassMaker.ACC_STATIC</code></li>
     * <li><code>ClassMaker.ACC_FINAL</code></li>
     * <li><code>ClassMaker.ACC_ABSTRACT</code></li>
     * <li><code>ClassMaker.ACC_SYNCHRONIZED</code></li>
     * <li><code>ClassMaker.ACC_NATIVE</code></li>
     * <li><code>ClassMaker.ACC_STRICTFP</code></li>
     * <li>zero</li>
     * </ul>
     * 
     * @param modifiers
     *            the bit mask of modifiers
     * @throws ClassMakerException
     *             if the bit mask includes an invalid modifier
     */
    protected void checkMethodModifiers(int modifiers) throws ClassMakerException {
        // Check that the list contains valid modifiers
        int wrongModifiers = modifiers & (~MASK_METHOD);
        if (wrongModifiers != 0)
            throw createException("ClassMaker.InvalidMethodModifier_1", MakerUtil.toModifierString(wrongModifiers));
        checkMultipleAccessModifiers(modifiers);
        // Check that abstract is not used with an incompatible modifier
        if (((modifiers & ClassMakerConstants.ACC_ABSTRACT) == ClassMakerConstants.ACC_ABSTRACT)) {
            if ((modifiers & (MASK_INCOMPATABLE_WITH_ABSTRACT_METHOD)) > 0)
                throw createException("ClassMaker.InvalidMethodModifierCombination_1", MakerUtil.toModifierString(modifiers
                        & MASK_INCOMPATABLE_WITH_ABSTRACT_METHOD));
        }
    }

    /**
     * Check that the bit mask only contains valid field modifiers.
     * </br>
     * The following are valid modifiers for a member field.
     * <ul>
     * <li><code>ClassMaker.ACC_PUBLIC</code></li>
     * <li><code>ClassMaker.ACC_PROTECTED</code></li>
     * <li><code>ClassMaker.ACC_PRIVATE</code></li>
     * <li><code>ClassMaker.ACC_STATIC</code></li>
     * <li><code>ClassMaker.ACC_FINAL</code></li>
     * <li><code>ClassMaker.ACC_TRANSIENT</code></li>
     * <li><code>ClassMaker.ACC_VOLATILE</code></li>
     * <li>zero</li>
     * </ul>
     * 
     * @param modifiers
     *            the bit mask of modifiers
     * @throws ClassMakerException
     *             if the bit mask includes an invalid modifier
     */
    protected void checkFieldModifiers(int modifiers) throws ClassMakerException {
        int wrongModifiers = modifiers & (~MASK_FIELD);
        if (wrongModifiers != 0)
            throw createException("ClassMaker.InvalidFieldModifier_1", MakerUtil.toModifierString(wrongModifiers));
        checkMultipleAccessModifiers(modifiers);
        if (((modifiers & ClassMakerConstants.ACC_FINAL) == ClassMakerConstants.ACC_FINAL)
                && ((modifiers & ClassMakerConstants.ACC_VOLATILE) == ClassMakerConstants.ACC_VOLATILE)) {
            throw createException("ClassMaker.InvalidFieldModifierCombination");
        }
    }

    /**
     * Check that the bit mask does not contain more than one access modifier.
     * </br>
     * The following are access modifiers.
     * <ul>
     * <li><code>ClassMaker.ACC_PUBLIC</code></li>
     * <li><code>ClassMaker.ACC_PROTECTED</code></li>
     * <li><code>ClassMaker.ACC_PRIVATE</code></li>
     * <li>zero</li>
     * </ul>
     * 
     * @param modifiers
     *            the bit mask of modifiers
     * @throws ClassMakerException
     *             if the bit mask includes more than one access modifier
     */
    protected void checkMultipleAccessModifiers(int modifiers) throws ClassMakerException {
        // Check there is no more than one access modifier
        int accessModifiers = modifiers & MASK_ACCESS;
        if (accessModifiers != ClassMakerConstants.ACC_PUBLIC && accessModifiers != ClassMakerConstants.ACC_PROTECTED
                && accessModifiers != ClassMakerConstants.ACC_PRIVATE && accessModifiers != 0) {
            throw createException("ClassMaker.MultipleAccessModifiers_1", MakerUtil.toModifierString(accessModifiers));
        }
    }


}
