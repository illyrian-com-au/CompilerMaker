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

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.ast.LineNumber;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.ast.TerminalNumber;

public class AstStructureFactory extends AstExpressionFactory {
    public AstStructureFactory() {
    }

    public AstStructureFactory(LineNumber source) {
        super(source);
    }

    public AstModule Module(AstExpression packageName,
            AstExpression [] importsList, AstClass declaredClass) {
        AstModule module = new AstModule();
        module.setPackage(Package(packageName));
        if (importsList != null) {
            for (AstExpression expr : importsList) {
                module.addImportsList(Import(expr));
            }
        }
        module.add(declaredClass);
        return module;
    }
    
    public AstModule Module(AstStructure packageDec,
            AstStructure importsList, AstStructure declaredClass) {
        return new AstModule(packageDec, importsList, declaredClass);
    }
    
    public AstPackage Package(AstExpression tree) {
        AstPackage result = new AstPackage(tree);
        result.setLineNumber(getLineNumber());
        return result;
    }

    public AstImport Import(AstExpression tree) {
        AstImport result = new AstImport(tree);
        result.setLineNumber(getLineNumber());
        return result;
    }

    public AstStructure Seq(AstStructure left, AstStructure right) {
        if (right == null) {
            return left;
        } else if (left == null) {
            return right;
        } else {
            return new AstStructureLink(left, right);
        }
    }

    public AstModifiers Modifier(String modifier, AstModifiers next) {
        return new AstModifiers(modifier, next);
    }

    public AstModifiers Modifier(String modifier) {
        return new AstModifiers(modifier, null);
    }

    public AstInterface DeclareInterface(AstModifiers modifiers,
            TerminalName name, AstExpression baseClass, AstStructure membersList) {
        AstInterface declared = new AstInterface(modifiers, name, baseClass, membersList);
        declared.setLineNumber(getLineNumber());
        return declared;
    }

    public AstClass DeclareClass(AstModifiers modifiers,
            TerminalName name, AstExpression baseClass,
            AstExpression implementsList, AstStructure membersList) {
        AstClass declareClass = new AstClass(modifiers, name,
                baseClass, implementsList, membersList);
        declareClass.setLineNumber(getLineNumber());
        return declareClass;
    }

    public AstDeclareVariable Declare(AstExpression type, TerminalName name) {
        return Declare(null, type, name);
    }

    public AstDeclareVariable Declare(AstModifiers modifiers,
            AstExpression type, TerminalName name) {
        return new AstDeclareVariable(modifiers, type, name);
    }

    public AstDeclareMethod Method(AstModifiers modifiers, AstExpression type,
            TerminalName name, AstStructure params, AstStructure code) {
        AstDeclareMethod declareMethod = new AstDeclareMethod(modifiers, type,
                name, params, code);
        declareMethod.setLineNumber(getLineNumber());
        return declareMethod;
    }

    public AstStatementReturn Return(AstExpression value) {
        AstStatementReturn stmt = new AstStatementReturn(value);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementEval Eval(AstExpression value) {
        AstStatementEval stmt = new AstStatementEval(value);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementCompound Compound() {
        AstStatementCompound stmt = new AstStatementCompound();
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementCompound Compound(AstStructure body) {
        AstStatementCompound stmt = new AstStatementCompound(body);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementIf If(AstExpression condition,
            AstStructure thenStatement, AstStructure elseStatement) {
        AstStatementIf stmt = new AstStatementIf(condition, thenStatement,
                elseStatement);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementWhile While(AstExpression condition,
            AstStructure bodyStatement) {
        AstStatementWhile stmt = new AstStatementWhile(condition, bodyStatement);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementFor For(AstExpression init, AstExpression cond,
            AstExpression step, AstStructure code) {
        AstStatementFor stmt = new AstStatementFor(init, cond, step, code);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementBreak Break() {
        AstStatementBreak stmt = new AstStatementBreak();
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStructure Break(TerminalName name) {
        AstStatementBreak stmt = new AstStatementBreak(name);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementContinue Continue() {
        AstStatementContinue stmt = new AstStatementContinue();
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStructure Continue(TerminalName name) {
        AstStatementContinue stmt = new AstStatementContinue(name);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstCompoundBase setLabel(TerminalName label, AstCompoundBase compound) {
        compound.setLabel(label);
        return compound;
    }

    public AstStructure setLabel(TerminalName label, AstStatementIf statement) {
        statement.setLabel(label);
        return statement;
    }

    public AstStatementSwitch Switch(AstExpression expression, AstStructure code) {
        AstStatementSwitch stmt = new AstStatementSwitch(expression, code);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementCase Case(TerminalNumber value) {
        AstStatementCase stmt = new AstStatementCase(value);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementDefault Default() {
        AstStatementDefault stmt = new AstStatementDefault();
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementTry Try(AstStructure tryCode, AstStructure catchCode,
            AstStructure finallyCode) {
        AstStatementTry stmt = new AstStatementTry(tryCode, catchCode,
                finallyCode);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementCatch Catch(AstDeclareVariable exception,
            AstStructure catchCode) {
        AstStatementCatch stmt = new AstStatementCatch(exception, catchCode);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

    public AstStatementFinally Finally(AstStructure code) {
        AstStatementFinally stmt = new AstStatementFinally(code);
        stmt.setLineNumber(getLineNumber());
        return stmt;
    }

}
