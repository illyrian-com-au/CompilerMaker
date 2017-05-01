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

import au.com.illyrian.classmaker.types.ClassType;

public class MakerExtendsJavaClassTest extends ClassMakerTestCase
{
    public void setUp() throws Exception
    {
        super.setUp();
        ClassMakerBase.setSharedFactory(null);
    }
    
    public static interface FuncABC {
        int funcA(int a);
        int funcB(int b);
        int funcC(int c);
    }

    public static class BaseClass
    {
        public int baseId = 1000;

        public int getBaseId() {return baseId;}

        public void setBaseId(int value) {baseId = value;}
        
        public int funcB(int b) { return Integer.MAX_VALUE;}

        public int funcC(int c) { return Integer.MAX_VALUE;}
    }

    public static class DerivedBaseClassMaker extends ClassMakerBase
    {
        public void code()
        {
            Extends(BaseClass.class);
            Implements(FuncABC.class);

            Method("funcA", int.class, ACC_PUBLIC);
            Declare("a", int.class, 0);
            Begin();
            {
                Eval(Call(This(), "setBaseId", Push(Get("a"))));
                Return(Add(Call(This(), "getBaseId", null), Literal(2)));
            }
            End();
        }
    }

    public void testDerivedBaseClass() throws Exception
    {
        ClassMaker maker = new DerivedBaseClassMaker();
        Class myClass = maker.defineClass();
        FuncABC exec =  (FuncABC)myClass.newInstance();

        assertEquals("Wrong value baseId", 1000, getIntField(myClass, exec, "baseId"));
        assertEquals("Wrong value", 3, exec.funcA(1));
        assertEquals("Wrong value baseId", 1, getIntField(myClass, exec, "baseId"));
   }

    
    public static class OneClass extends BaseClass
    {
        public int oneId = 100;

        public int getOneId() {return oneId;}

        public void setOneId(int value) {oneId = value;}
    }

   public static class DerivedOneClassMaker extends ClassMakerBase
    {
        public void code()
        {
            Extends(OneClass.class);
            Implements(FuncABC.class);

            Method("funcA", int.class, ACC_PUBLIC);
            Declare("a", int.class, 0);
            Begin();
            {
                Eval(Call(This(), "setBaseId", Push(Get("a"))));
                Return(Add(Call(This(), "getBaseId", null), Literal(2)));
            }
            End();

            Method("funcB", int.class, ACC_PUBLIC);
            Declare("b", int.class, 0);
            Begin();
            {
                Eval(Call(This(), "setOneId", Push(Get("b"))));
                Return(Add(Call(This(), "getOneId", null), Call(This(), "getBaseId", null)));
            }
            End();

            Method("funcC", int.class, ACC_PUBLIC);
            Declare("c", int.class, 0);
            Begin();
            {
                Eval(Set(This(), "baseId", Get("c")));
                Return(Add(Get(This(), "oneId"), Get(This(), "baseId")));
            }
            End();
        }
    }

    public void testDerivedOneClass() throws Exception
    {
        ClassMaker maker = new DerivedOneClassMaker();
        Class myClass = maker.defineClass();
        FuncABC exec =  (FuncABC)myClass.newInstance();

        assertEquals("Wrong value oneId", 100, getIntField(myClass, exec, "oneId"));
        assertEquals("Wrong value", 1001, exec.funcB(1));
        assertEquals("Wrong value oneId", 1, getIntField(myClass, exec, "oneId"));

        assertEquals("Wrong value baseId", 1000, getIntField(myClass, exec, "baseId"));
        assertEquals("Wrong value", 3, exec.funcA(1));
        assertEquals("Wrong value baseId", 1, getIntField(myClass, exec, "baseId"));

        assertEquals("Wrong value", 201, exec.funcC(200));
        assertEquals("Wrong value baseId", 200, getIntField(myClass, exec, "baseId"));
   }

    public static class DerivedTwoClassMaker extends ClassMakerBase
    {
        private final Class extendsType;
        
        public DerivedTwoClassMaker(Class extendsType) {
            this.extendsType = extendsType;
        }
        
        public void code()
        {
            Extends(extendsType);
            Implements(FuncABC.class);

            Method("funcA", int.class, ACC_PUBLIC);
            Declare("a", int.class, 0);
            Begin();
            {
                Return(Add(Call(This(), "getBaseId", null), Literal(10)));
            }
            End();

            Method("funcB", int.class, ACC_PUBLIC);
            Declare("b", int.class, 0);
            Begin();
            {
                Return(Add(Call(This(), "getOneId", null), Get("b")));
            }
            End();

            Method("funcC", int.class, ACC_PUBLIC);
            Declare("c", int.class, 0);
            Begin();
            {
                Return(Add(Get("c"), Literal(30)));
            }
            End();
        }
    }

    public void testDerivedTwoClass() throws Exception
    {
        ClassMaker oneMaker = new DerivedOneClassMaker();
        Class oneClass = oneMaker.defineClass();
        ClassMaker twoMaker = new DerivedTwoClassMaker(oneClass);
        Class twoClass = twoMaker.defineClass();
        
        FuncABC exec =  (FuncABC)twoClass.newInstance();

        assertEquals("Wrong value oneId", 100, getIntField(twoClass, exec, "oneId"));
        assertEquals("Wrong value", 105, exec.funcB(5));
        assertEquals("Wrong value oneId", 100, getIntField(twoClass, exec, "oneId"));

        assertEquals("Wrong value baseId", 1000, getIntField(twoClass, exec, "baseId"));
        assertEquals("Wrong value", 1010, exec.funcA(222));
        assertEquals("Wrong value baseId", 1000, getIntField(twoClass, exec, "baseId"));

        assertEquals("Wrong value", 230, exec.funcC(200));
        assertEquals("Wrong value baseId", 1000, getIntField(twoClass, exec, "baseId"));
   }

}
