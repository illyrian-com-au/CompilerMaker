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

package au.com.illyrian.classmaker;

import java.io.File;
import java.io.IOException;

/**
 * The simplest way to use ClassMaker is to derive a class from ClassMakerBase 
 * and implement the <code>code()</code> method.
 * Byte code is generated as a side effect of calling methods in the ClassMakerBase instance.
  
  The following example compares an implementation of the factorial algorithm in java 
  and using the ClassMaker API.
  <table width="100%" border="1">
  <tr>
    <td width="50%" >
  Java code
    </td>
    <td>
  ClassMaker code
    </td>
  </tr>
  <tr>
    <td>
      <pre>
    public interface Unary
    {
        int unary(int a);
    }

    public class Factorial
        implements Unary
    {
        public int unary(int n)
        {
            int x;
            x= 1;
            while (n>0)
            {
                x = x * n;
                n--;
            }
            return x;
        }
    }
      </pre>
    </td>
    <td>
      <pre>
    public class FactorialMaker extends ClassMakerBase
    {
        public void code()
        {
            Implements(Unary.class);

            Method("unary", int.class, ACC_PUBLIC);
            Declare("n", int.class, 0);
            Begin();
              Declare("x", int.class, 0);
              Set("x", Literal(1));
              Loop();
                While(GT(Get("n"), Literal(0)));
                Set("x", Mult(Get("x"), Get("n")));
                Eval(Dec("n"));
              EndLoop();
              Return(Get("x"));
            End();
        }
    }
      </pre>
    </td>
  </tr>
</table>
 *
 * @author Donald Strong
 */
public abstract class ClassMakerCode extends ClassMaker
{
    /* A ClassMakerFactory instance that is shared by all instances of ClassMakerBase. */
    private static ClassMakerFactory sharedFactory = null;

    /* A flag to prevent code from being generated twice. */
    private boolean codeGenerated = false;

    /**
     * The default constructor may be easily extended to implement classes at runtime.
     * </br>
     * By convention, the extending class should be called <code>ClassNameMaker</code>
     * then the generated class will be <code>ClassName</code>.
     * </br>
     * A default constructor will be provided for the generated class and the
     * <code>code()</code> method will be called to generate member fields and methods.
     */
    protected ClassMakerCode()
    {
        super(ClassMakerCode.getSharedFactory());
    }

    /**
     * Creates a <code>ClassMakerBase</code> instance given a <code>ClassMakerFactory</code>.
     * @param globalFactory the <code>ClassMakerFactory</code> instance that is used by all class generators
     */
    protected ClassMakerCode(ClassMakerFactory globalFactory)
    {
        super(globalFactory);
    }

    /**
     * A <code>ClassMakerFactory</code> instance that is shared by all instances of ClassMakerBase
     */
    public static void setSharedFactory(ClassMakerFactory factory)
    {
        ClassMakerCode.sharedFactory = factory;
    }

    /**
     * A <code>ClassMakerFactory</code> instance that is shared by all instances of ClassMakerBase
     * @return the shared <code>ClassMakerFactory</code>
     */
    public static ClassMakerFactory getSharedFactory()
    {
        if (ClassMakerCode.sharedFactory == null)
            ClassMakerCode.sharedFactory = new ClassMakerFactory();
        return ClassMakerCode.sharedFactory;
    }

    //############# Helper methods for derived classes #########

    /**
     * Derives a name for the generated class from the name of the generating class.
     * </br>
     * By convention, the extending class should be called <code><i>ClassName</i>Maker</code>
     * then the generated class will be <code><i>ClassName</i></code>.
     * @return a default name for the generated class
     */
    protected String defaultFullyQualifiedClassName()
    {
        String makerName = getClass().getName();
        if (makerName.endsWith("Maker"))
        {
            int offset = makerName.length() - "Maker".length();
            return toDotName(makerName.substring(0, offset));
        }
        return toDotName(makerName) + "_$";
    }

    /**
     * Defines this class in the factory <code>ClassLoader</code>.
     * @return the generated class
     */
    public Class defineClass()
    {
        generateCode();
        return super.defineClass();
    }

    public File saveClass(File classesDir) throws IOException
    {
        generateCode();
        return super.saveClass(classesDir);
    }
    
    /**
     * Completes processing of the class.
     * </br>
     * This method is automatically called when the class is defined.
     */
    public void generateCode()
    {
        if (!codeGenerated)
        {
            if (isTwoPass())
            {
                getFactory().setPass(ClassMakerConstants.FIRST_PASS);
                code();
                super.EndClass();
                getFactory().setPass(ClassMakerConstants.SECOND_PASS);
            }
            code();
            super.EndClass();
        }
        codeGenerated = true;
    }

    /**
     * This method may be overridden to generate member fields and methods for the class.
     */
    abstract public void code();

}
