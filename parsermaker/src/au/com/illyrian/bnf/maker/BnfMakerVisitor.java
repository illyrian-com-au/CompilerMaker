package au.com.illyrian.bnf.maker;

import java.util.Map;

import au.com.illyrian.bnf.ast.BnfTree;
import au.com.illyrian.bnf.ast.BnfTreeAlternative;
import au.com.illyrian.bnf.ast.BnfTreeEmpty;
import au.com.illyrian.bnf.ast.BnfTreeList;
import au.com.illyrian.bnf.ast.BnfTreeLookahead;
import au.com.illyrian.bnf.ast.BnfTreeMacroCall;
import au.com.illyrian.bnf.ast.BnfTreeMethodCall;
import au.com.illyrian.bnf.ast.BnfTreeName;
import au.com.illyrian.bnf.ast.BnfTreeNonterminal;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.ast.BnfTreeRecover;
import au.com.illyrian.bnf.ast.BnfTreeReserved;
import au.com.illyrian.bnf.ast.BnfTreeRule;
import au.com.illyrian.bnf.ast.BnfTreeSequence;
import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.types.DeclaredType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.parser.ParserException;

public class BnfMakerVisitor extends AstExpressionVisitor
{
//    Type ruleType = null;
//    Type defaultRuleType = null;
    private Map<String, BnfTreeRule> ruleSet = null;
    
    public BnfMakerVisitor() {
    }
    
    public BnfMakerVisitor(ClassMakerIfc classMaker)
    {
        setMaker(classMaker);
    }

    public Map<String, BnfTreeRule> getRuleSet()
    {
        return ruleSet;
    }

    public void setRuleSet(Map<String, BnfTreeRule> ruleSet)
    {
        this.ruleSet = ruleSet;
    }
    
    public boolean isRule(String name) {
        return (ruleSet != null && ruleSet.containsKey(name));
    }
    
    public BnfTreeRule findRule(String name) {
        return (isRule(name)) ? ruleSet.get(name) : null;
    }

    public Type resolveDeclaration(BnfTreeParser tree)
    {
        setRuleSet(tree.getRuleSet());
        tree.getRules().resolveDeclaration(this);
        return ClassMaker.VOID_TYPE;
    }

    public Type resolveDeclaration(BnfTreeList list)
    {
        BnfTreeRule [] rules = list.toRuleArray();
        for (BnfTree rule : rules) {
            rule.resolveDeclaration(this);
        }
        return ClassMaker.VOID_TYPE;
    }

    public Type resolveDeclaration(BnfTreeRule rule)
    {
        String methodName = rule.getTarget().getName();
        Type returnType = methodBegin(methodName, "Object");
        rule.getBody().resolveDeclaration(this);
        methodEnd();
        return returnType;
    }
    
    public Type resolveDeclaration(BnfTreeAlternative alt) {
        return resolveAlternatives(alt.toAltArray(), 1, 1);
    }
 
    Type resolveAlternatives(BnfTree [] alternatives, int offset, int variable) {
        BnfTree left = alternatives[offset-1];
        if (offset < alternatives.length) {
            if (left.isEmpty()) {
                throw new ParserException("Empty alternative must appear last");
            }
            Type cond = (Type)left.getHead().resolveLookahead(this, 0);
            getMaker().If(cond);
            {
                resolveSequence(left.toSeqArray(), 0, variable);
            }
            BnfTree right = alternatives[offset];
            // If the right hand side is EMPTY then this is an optional 
            // clause so no need for an else part.
            if (!right.isEmpty()) {
                getMaker().Else();
                resolveAlternatives(alternatives, offset+1, variable);
            }
            getMaker().EndIf();
        } else {
            resolveSequence(left.toSeqArray(), 0, variable);
        }
        return ClassMaker.VOID_TYPE;
    }

    public Type resolveLookahead(BnfTreeAlternative alt, int howFar) {
        Type cond = (Type)alt.getLeft().resolveLookahead(this, howFar);
        return cond;
    }

    public Type resolveDeclaration(BnfTreeSequence seq)
    {
        return resolveSequence(seq.toSeqArray(), 0, 1);
    }
    
    Type resolveSequence(BnfTree [] sequence, int offset, int variable) {
        while (offset < sequence.length) {
            BnfTree tree = sequence[offset];
            if (tree.isEmpty() || tree.isVoidType()) {
                tree.resolveDeclaration(this);
            } else if (tree.toAlternative() != null) {
                resolveAlternatives(tree.toAltArray(), 1, variable);
            } else if (tree.isMacro()) {
                offset++;
                continue;
            } else {
                declare(variable, ClassMaker.OBJECT_TYPE);
                Type type = (Type)tree.resolveType(this);
                assign(variable, type);
            }
            offset++;
            variable++;
        }
        return ClassMaker.VOID_TYPE;
    }

    public Type resolveDeclaration(BnfTreeEmpty empty)
    {
        return null; // empty();
    }

    public Type resolveDeclaration(BnfTreeMethodCall call)
    {
        getMaker().Eval(resolveType(call));
        return ClassMaker.VOID_TYPE;
    }

    public Type resolveDeclaration(BnfTreeReserved reserved)
    {
        expect(reserved.getName());
        return ClassMaker.VOID_TYPE;
    }

    public Type resolveDeclaration(BnfTreeNonterminal nonterm)
    {
        String name = nonterm.getName();
        return getMaker().Call(getMaker().This(), name, getMaker().Push());
    }

    public Type resolveLookahead(BnfTreeSequence seq, int howFar)
    {
        BnfTree<Type> [] list = seq.toSeqArray();
        if (list.length < 2) {
            throw new IllegalArgumentException("Array length must be more than 2 but was " + list.length);
        }
        Type result = null;
        AndOrExpression andThen = null;
        for (int i=0; i<list.length; i++) {
            Type cond = list[i].resolveLookahead(this, i);
            if (i == 0) { 
                andThen = getMaker().AndThen(cond);
            } else if (i < list.length-1) {
                andThen = getMaker().AndThen(andThen, cond);
            } else {
                result = getMaker().Logic(andThen, cond);
            }
        }
        return result;
    }

    public Type resolveLookahead(BnfTreeReserved reserved, int howFar)
    {
        return match(reserved.getName(), howFar);
    }

    public Type resolveLookahead(BnfTreeRule  rule, int howFar)
    {
        String [] list = rule.getFirstSet().toArray();
        Type result = null;
        if (list.length == 1) { 
            result = match(list[0]);
        } else if (list.length > 1) {
            result = resolveLookahead(list);
        } else {
            result = getMaker().Literal(true);
        }
        return result;
    }

    public Type resolveLookahead(BnfTreeLookahead macro, int howFar)
    {
        BnfTree<Type> pattern = macro.getPattern();
        return pattern.resolveLookahead(this, howFar);
    }
    
    Type resolveLookahead(String [] list)
    {
        if (list.length < 2) {
            throw new IllegalArgumentException("Array length must be more than 2 but was " + list.length);
        }
        Type result = null;
        AndOrExpression orElse = null;
        for (int i=0; i<list.length; i++) {
            Type cond = match(list[i]);
            if (i == 0) { 
                orElse = getMaker().OrElse(cond);
            } else if (i < list.length-1) {
                orElse = getMaker().OrElse(orElse, cond);
            } else {
                result = getMaker().Logic(orElse, cond);
            }
        }
        return result;
    }

    public Type resolveLookahead(BnfTreeRecover bnfTreeRecover, int howFar)
    {
        return null;
    }

    public Type resolveLookahead(BnfTreeNonterminal reserved, int howFar)
    {
        return getMaker().Literal(true);
    }

    public Type resolveLookahead(BnfTreeName term, int howFar)
    {
        String name = term.getName();
        if (isRule(name)) {
            BnfTreeRule rule = ruleSet.get(name);
            return resolveLookahead(rule, howFar);
        } else {
            return match(name, howFar);
        }
    }

    public Type resolveType(BnfTreeNonterminal tree)
    {
        String ruleName = tree.getName();
        Type reference = getMaker().This();
        sourceLine = tree;
        return getMaker().Call(reference, ruleName, getMaker().Push());
    }

    public Type resolveType(BnfTreeName tree)
    {
        String name = tree.getName();
        if (isRule(name)) {
            BnfTreeRule rule = ruleSet.get(name);
            return callMethod(rule);
        } else {
            return expect(name);
        }
    }

    Type callMethod(BnfTreeRule rule)
    {
        String methodName = rule.getName();
        Type reference = getMaker().This();
        CallStack callStack = getMaker().Push();
        sourceLine = rule;
        return getMaker().Call(reference, methodName, callStack);
    }

    public Type resolveType(BnfTreeMethodCall call)
    {
        Type reference = getMaker().This();
        CallStack callStack = resolveCallStack(call);
        sourceLine = call;
        return getMaker().Call(reference, callStack.getMethodName(), callStack);
    }
    
    public Type resolveType(AstExpression tree)
    {
        throw new IllegalStateException("No special case for ExpressionTree type: " 
                + tree.getClass().getSimpleName());
    }

    public CallStack resolveCallStack(BnfTreeMethodCall call)
    {
        String methodName = call.getName();
        CallStack actualParameters;
        sourceLine = call;
        if (call.getActuals() == null)
            actualParameters = getMaker().Push();
        else
            actualParameters = call.getActuals().resolveCallStack(this);
        actualParameters.setMethodName(methodName);
        return actualParameters;
    }

    /************* Convenience Methods **************/
    
    Type methodBegin(String methodName, String returnType) {
        DeclaredType declared = getMaker().findDeclaredType(returnType);
        getMaker().Method(methodName, declared.getName(), ClassMaker.ACC_PUBLIC);
        getMaker().Begin();
        getMaker().Declare("$$", declared.getType(), 0);
        return declared.getType();
    }
    
    void methodEnd() {
        getMaker().Return(getMaker().Get("$$"));
        getMaker().End();
    }
    
    void declare(int index, Type returnType) {
        getMaker().Declare("$" + index, returnType, 0);
    }

    void assign(int index, Type type) {
        getMaker().Eval(getMaker().Assign("$" + index, type));
    }
    
    Type match(String token) {
        return getMaker().Call(getMaker().This(), "match", 
                getMaker().Push(getMaker().Get(token)));
    }

    Type match(String token, int howFar) {
        if (howFar==0) {
            return match(token);
        } else {
            return getMaker().Call(getMaker().This(), "match", 
                    getMaker().Push(getMaker().Get(token)).Push(getMaker().Literal(howFar)));
        }
    }

    Type expect(String token) {
        return getMaker().Call(getMaker().This(), "expect", 
                getMaker().Push(getMaker().Get(token)));
    }
    Type empty() {
        return getMaker().Call(getMaker().This(), "empty", getMaker().Push());
    }

}
