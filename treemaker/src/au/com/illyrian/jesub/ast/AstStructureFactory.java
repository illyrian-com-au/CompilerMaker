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

import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.ast.AstExpressionLink;
import au.com.illyrian.classmaker.ast.AstStatementReserved;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.classmaker.ast.TerminalNumber;

public class AstStructureFactory extends AstExpressionFactory {
    public AstStructureFactory() {
    }

    public AstStructureFactory(SourceLine sourceLine) {
        super(sourceLine);
    }

    AstDeclareModule Module(AstExpression packageName,
            AstExpressionLink importsList, AstDeclareClass declaredClass) {
        return new AstDeclareModule(packageName, importsList, declaredClass);
    }

    public AstStructureLink Seq(AstStructure left, AstStructure right) {
        return new AstStructureLink(left, right);
    }

    public AstModifiers Modifier(String modifier, AstModifiers next) {
        return new AstModifiers(modifier, next);
    }

    public AstModifiers Modifier(String modifier) {
        return new AstModifiers(modifier, null);
    }

    public AstDeclareClass DeclareClass(AstModifiers modifiers,
            TerminalName name, AstExpression baseClass,
            AstExpression implementsList, AstStructure membersList) {
        AstDeclareClass declareClass = new AstDeclareClass(modifiers, name,
                baseClass, implementsList, membersList);
        return declareClass;
    }

    public AstDeclareClass DeclareClass(AstModifiers modifiers,
            TerminalName name, AstExpression baseClass,
            AstExpression implementsList) {
        AstDeclareClass declareClass = new AstDeclareClass(modifiers, name,
                baseClass, implementsList, null);
        return declareClass;
    }

    public AstDeclareVariable Declare(AstModifiers modifiers,
            AstExpression type, TerminalName name) {
        return new AstDeclareVariable(modifiers, type, name);
    }

    public AstDeclareMethod Method(AstModifiers modifiers, AstExpression type,
            TerminalName name, AstStructure params, AstStructure code) {
        AstDeclareMethod declareMethod = new AstDeclareMethod(modifiers, type,
                name, params, code);
        return declareMethod;
    }

    public AstStatementReturn Return(AstExpression value) {
        AstStatementReturn stmt = new AstStatementReturn(value);
        return stmt;
    }

    public AstStatementEval Eval(AstExpression value) {
        AstStatementEval stmt = new AstStatementEval(value);
        return stmt;
    }

    public AstStatementCompound Compound() {
        AstStatementCompound stmt = new AstStatementCompound();
        return stmt;
    }

    public AstStatementIf If(AstExpression condition,
            AstStructure thenStatement, AstStructure elseStatement) {
        AstStatementIf stmt = new AstStatementIf(condition, thenStatement,
                elseStatement);
        return stmt;
    }

    public AstStatementWhile While(AstExpression condition,
            AstStructure bodyStatement) {
        AstStatementWhile stmt = new AstStatementWhile(condition, bodyStatement);
        return stmt;
    }

    public AstStatementFor For(AstExpression init, AstExpression cond,
            AstExpression step, AstStructure code) {
        AstStatementFor stmt = new AstStatementFor(init, cond, step, code);
        return stmt;
    }

    public AstStatementBreak Break() {
        AstStatementBreak stmt = new AstStatementBreak();
        return stmt;
    }

    public AstStructure Break(TerminalName name) {
        AstStatementBreak stmt = new AstStatementBreak(name);
        return stmt;
    }

    public AstStatementContinue Continue() {
        AstStatementContinue stmt = new AstStatementContinue();
        return stmt;
    }

    public AstStructure Continue(TerminalName name) {
        AstStatementContinue stmt = new AstStatementContinue(name);
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
        return stmt;
    }

    public AstStatementCase Case(TerminalNumber value) {
        AstStatementCase stmt = new AstStatementCase(value);
        return stmt;
    }

    public AstStatementDefault Default() {
        AstStatementDefault stmt = new AstStatementDefault();
        return stmt;
    }

    public AstStatementTry Try(AstStructure tryCode, AstStructure catchCode,
            AstStatementFinally finallyCode) {
        AstStatementTry stmt = new AstStatementTry(tryCode, catchCode,
                finallyCode);
        return stmt;
    }

    public AstStatementCatch Catch(AstDeclareVariable exception,
            AstStructure catchCode) {
        AstStatementCatch stmt = new AstStatementCatch(exception, catchCode);
        return stmt;
    }

    public AstStatementFinally Finally(AstStructure code) {
        AstStatementFinally stmt = new AstStatementFinally(code);
        return stmt;
    }

}
