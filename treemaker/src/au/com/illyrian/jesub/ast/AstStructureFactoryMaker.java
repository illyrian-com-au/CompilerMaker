// Copyright (c) 2014, Donald Strong.
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

package au.com.illyrian.jesub.ast;

import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerConstants;
import au.com.illyrian.classmaker.ClassMakerLocation;
import au.com.illyrian.classmaker.ForStep;
import au.com.illyrian.classmaker.ForWhile;
import au.com.illyrian.classmaker.Labelled;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.ast.TerminalNumber;
import au.com.illyrian.classmaker.types.Value;

public class AstStructureFactoryMaker extends AstExpressionFactory implements ClassMakerLocation, AstClassAction
{
    protected ClassMaker maker = null;
    protected AstExpressionVisitor visitor = null;

    public AstStructureFactoryMaker(ClassMaker maker)
    {
        setClassMaker(maker);
    }

    public void setClassMaker(ClassMaker classMaker)
    {
        maker = classMaker;
        visitor = new AstExpressionVisitor(maker);
    }

    public ClassMakerConstants getClassMaker()
    {
        if (maker == null)
            throw new NullPointerException("classMaker is null.");
        return maker;
    }

    public void Package(AstExpression packageName)
    {
        String path = packageName.resolvePath(visitor);
        maker.setPackageName(path);
    }

    public void Import(AstExpression className)
    {
        String path = className.resolvePath(visitor);
        maker.Import(path);
    }

    public int Modifier(String modifierName)
    {
        int modifiers = maker.addModifier(0, modifierName);
        return modifiers;
    }

    public int Modifier(int modifiers, String modifierName)
    {
        modifiers = maker.addModifier(modifiers, modifierName);
        return modifiers;
    }

    public void ClassName(int modifiers, TerminalName className)
    {
        maker.setClassModifiers(modifiers);
        maker.setSimpleClassName(className.getName());
    }

    public void Extends(AstExpression className)
    {
        String path = className.resolvePath(visitor);
        maker.Extends(path);
    }

    public void Implements(AstExpression className)
    {
        String path = className.resolvePath(visitor);
        maker.Implements(path);
    }

    public void Declare(AstExpression type, TerminalName name)
    {
        Declare(0, type, name);
    }

    public void Declare(int modifiers, AstExpression type, TerminalName name)
    {
        String typeName = type.resolvePath(visitor);
        String varName = name.getName();
        maker.Declare(varName, typeName, modifiers);
    }

    //    public void Method(AstExpression type, TerminalName name)
    //    {
    //        Method(popModifiers(), type, name);
    //    }

    public void Method(int modifiers, AstExpression type, TerminalName name)
    {
        String typeName = type.resolvePath(visitor);
        String varName = name.getName();
        maker.Method(varName, typeName, modifiers);
    }

    public Labelled Begin()
    {
        Labelled labeller = maker.Begin();
        return labeller;
    }

    public void End()
    {
        maker.End();
    }

    public void Return(AstExpression expr)
    {
        Value type = expr.resolveValue(visitor);
        maker.Return(type);
    }

    public void Return()
    {
        maker.Return();
    }

    public void Eval(AstExpression expr)
    {
        Value type = expr.resolveValue(visitor);
        maker.Eval(type);
    }

    public Labelled If(AstExpression condition)
    {
        Value type = condition.resolveValue(visitor);
        Labelled labeller = maker.If(type);
        return labeller;
    }

    public void Else()
    {
        maker.Else();
    }

    public void EndIf()
    {
        maker.EndIf();
    }

    public Labelled While(AstExpression condition)
    {
        Labelled label = maker.Loop();
        Value type = condition.resolveValue(visitor);
        maker.While(type);
        return label;
    }

    public void EndWhile()
    {
        maker.EndLoop();
    }

    public Labelled For(AstExpression initialise, AstExpression condition, AstExpression increment)
    {
        Value type1 = (initialise == null) ? null : initialise.resolveValue(visitor);
        ForWhile part1 = maker.For(type1);
        Value type2 = (condition == null) ? null : condition.resolveValue(visitor);
        ForStep part2 = part1.While(type2);
        Value type3 = (increment == null) ? null : increment.resolveValue(visitor);
        Labelled part3 = part2.Step(type3);
        return part3;
    }

    public void EndFor()
    {
        maker.EndFor();
    }

    public void Break()
    {
        maker.Break();
    }

    public void Break(TerminalName label)
    {
        maker.Break(label.getName());
    }

    public void Continue()
    {
        maker.Continue();
    }

    public void Continue(TerminalName label)
    {
        maker.Continue(label.getName());
    }

    public Labelled Switch(AstExpression expr)
    {
        Value type = expr.resolveValue(visitor);
        Labelled labeller = maker.Switch(type);
        return labeller;
    }

    public void Case(TerminalNumber value)
    {
        maker.Case(value.intValue());
    }

    public void Default()
    {
        maker.Default();
    }

    public void EndSwitch()
    {
        maker.EndSwitch();
    }

    public Labelled Try()
    {
        Labelled labeller = maker.Try();
        return labeller;
    }

    public void Catch(AstExpression type, TerminalName name)
    {
        String typeString = type.resolvePath(visitor);
        String nameString = name.getName();
        maker.Catch(typeString, nameString);
    }

    public void Finally()
    {
        maker.Finally();
    }

    public void EndTry()
    {
        maker.EndTry();
    }

}
