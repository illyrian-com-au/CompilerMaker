// Copyright (c) 2010, Donald Strong.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.

package au.com.illyrian.classmaker.members;

import java.util.Iterator;
import java.util.Vector;

import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerException;
import au.com.illyrian.classmaker.ClassMakerFactory;
import au.com.illyrian.classmaker.converters.AssignmentConversion;
import au.com.illyrian.classmaker.converters.MethodInvocationConversion;
import au.com.illyrian.classmaker.types.Type;

/**
 * A method resolver will determine the most appropriate method to be called on a
 * target class given a call stack of actual parameters.
 * 
 * @author Donald Strong
 */
public class MethodResolver
{
    private final MethodInvocationConversion invokeConverter;
    private final AssignmentConversion assignConverter;
    private static final MakerMethod[] METHOD_ARRAY = new MakerMethod[0];

    /**
     * Constructs a method resolver.
     * @param factory the shared <code>ClassMakerFactory</code>
     */
    public MethodResolver(ClassMakerFactory factory)
    {
        invokeConverter = factory.getMethodInvocationConversion();
        assignConverter = factory.getAssignmentConversion();
    }

    /**
     * Resolves which method to call from the list of method descriptors.
     * <br/>
     * The list of methods should include all methods from the target class including
     * those inherited from base classes.
     * A unique candidate is selected from the given list by applying 4 filters.
     * <ol>
     * <li>Select those methods with the required name.</li>
     * <li>Remove methods with incompatible parameters.</li>
     * <li>Remove methods with less specific parameters.</li>
     * <li>Remove methods with less specific return types.</li>
     * </ol> 
     * This progression stops as soon as there is one candidate left. An exception is thrown 
     * if all candidates are removed or if there is more than one candidate after all filtering.
     * @param maker the <code>ClassMaker</code> instance which is calling the method
     * @param methods an array of all methods in the target class
     * @param name the name of the method
     * @param actualParameters the actual parameters provided to the method
     * @return the most suitable method to call
     * @throws ClassMakerException if their are no methods of the given name or 
     *         if a unique candidate cannot be chosen from the overloaded methods
     */
    public MakerMethod resolveMethod(ClassMaker maker, MakerMethod[] methods, String name, CallStack actualParameters)
    {
        if (actualParameters == null)
        {   // Should not get here
            throw new IllegalArgumentException("CallStack cannot be null.");
        }

        Vector<MakerMethod> candidates = findMethods(methods, name);
        if (candidates.size() == 0)
            throw maker.createException("ClassMaker.NoMethodCalled_1", name);
        removeIncompatableCandidates(candidates, actualParameters);
        if (candidates.size() > 1)
            removeLessSpecificCandidates(candidates);
        if (candidates.size() > 1)
            removeLessSpecificReturnType(candidates);

        if (candidates.size() != 1)
        {
            throw maker.createException("ClassMaker.CannotResolveMethodCall_1",
                 ClassMaker.toMethodString(name, (Type[]) actualParameters.toArray(), null));
        }
        return candidates.firstElement();
    }

    /**
     * Finds all methods in the list of methods with the given name.
     * @param methods the list of method descriptors
     * @param name the name of the method to be found
     * @return a list containing all method with the given name
     */
    Vector<MakerMethod> findMethods(MakerMethod[] methods, String name)
    {
        Vector<MakerMethod> candidates = new Vector<MakerMethod>();
        candidates.clear();

        for (int i = 0; i < methods.length; i++)
        {
            MakerMethod method = methods[i];
            if (name.equals(method.getName()))
                candidates.add(method);
        }
        return candidates;
    }

    /**
     * Removes methods with incompatible parameters from the list of candidates.
     * <br/>
     * A parameter is incompatible if the actual parameter cannot be converted to the
     * formal parameter using method invocation conversion.
     * @see MethodInvocationConversion
     * @param candidates the list of candidates
     */
    void removeIncompatableCandidates(Vector<MakerMethod> candidates, CallStack parameters)
    {
        // Compare the actual and formal parameters.
        Iterator<MakerMethod> iter = candidates.iterator();
        while (iter.hasNext())
        {
            MakerMethod method = iter.next();
            Type[] formalParameters = method.getFormalTypes();
            Type [] actualParameters = parameters.toArray();

            // Remove candidates with the wrong number of parameters
            int formalSize = formalParameters.length;
            int actualSize = actualParameters.length;
            if (formalSize != actualSize)
            {
//                System.out.println("Remove " + method.toString() + ", wrong number of parameters.");
                iter.remove();
                continue;
            }

            // Remove candidates with incompatable parameter types
            for (int i = 0; i < formalSize; i++)
            {
                Type formal = formalParameters[i];
                Type actual = actualParameters[i];
                if (!invokeConverter.isConvertable(actual, formal))
                {
//                    System.out.println("Remove " + method.toString() + ", incompatable parameter:"
//                                    + actual + "!>" + formal);
                    iter.remove();
                    break;
                }
            }
        }
    }

    /**
     * Removes methods with less specific parameters from the list of candidates.
     * <br/>
     * A formal parameter is less specific if the parameter in one method can be converted 
     * to the same parameter in another method using method invocation conversion.
     * @see MethodInvocationConversion
     * @param candidates the list of candidates
     */
    void removeLessSpecificCandidates(Vector<MakerMethod> candidates)
    {
        // FIXME - include logging.
//        System.out.println("####################################################");
        // Remove candidates with less specific parameters
        MakerMethod[] method = candidates.toArray(METHOD_ARRAY);
        int len = method.length;
        int paramSize = method[0].getFormalDeclaredTypes().length;
        for (int i = 0; i < len; i++)
        {
//            System.out.println("Compare i=" + method[i].toString());
            for (int j = i + 1; j < len; j++)
            {
//                System.out.println(" verses j=" + method[j].toString());
                // Compare each pair of candidates
                for (int k = 0; k < paramSize; k++)
                {
                    Type param_i = method[i].getFormalTypes()[k];
                    Type param_j = method[j].getFormalTypes()[k];
                    if (!param_i.equals(param_j))
                    {
                        // Remove the candidate with a less specific parameter
                        if (invokeConverter.isConvertable(param_i, param_j))
                        {
//                            System.out.println("Remove j, it is less specific than i:"
//                                            + param_i + " |> " + param_j);
                            candidates.remove(method[j]);
                        }
                        if (invokeConverter.isConvertable(param_j, param_i))
                        {
//                            System.out.println("Remove i, it is less specific than j:"
//                                            + param_j + " |> " + param_i);
                            candidates.remove(method[i]);
                        }
                    }
                }
            }
        }
//        System.out.println("----------------------------------------------------");
    }

    /**
     * Removes methods with less specific return type from the list of candidates.
     * <br/>
     * A return type is less specific if the return type for one method can be converted 
     * to the return type for another method using assignment conversion.
     * @see AssignmentConversion
     * @param candidates the list of candidates
     */
    void removeLessSpecificReturnType(Vector<MakerMethod> candidates)
    {
        // FIXME - include logging.
//        System.out.println("### removeLessSpecificReturnType, size=" + candidates.size());
        // Remove candidates with less specific parameters
        MakerMethod[] method = candidates.toArray(METHOD_ARRAY);
        int len = method.length;
        for (int i = 0; i < len; i++)
        {
//            System.out.println("Compare i=" + method[i].toString());
            for (int j = i + 1; j < len; j++)
            {
//                System.out.println("against j=" + method[j].toString());
                Type param_i = method[i].getReturnType();
                Type param_j = method[j].getReturnType();
                if (!param_i.equals(param_j))
                {
                    // Remove the candidate with the less specific return type
                    if (assignConverter.isConvertable(param_i, param_j))
                    {
//                        System.out.println("Remove j, it is less specific than i:"
//                                        + param_j + " <| " + param_i);
                        candidates.remove(method[j]);
                    }
                    if (assignConverter.isConvertable(param_j, param_i))
                    {
//                        System.out.println("Remove i, it is less specific than j:"
//                                        + param_i + " <| " + param_j);
                        candidates.remove(method[i]);
                    }
                }
            }
        }
//        System.out.println("----------------------------------------------------");
    }
}
