package au.com.illyrian.bnf.maker;

import java.util.Map;
import java.util.Vector;

import au.com.illyrian.bnf.ast.BnfTree;
import au.com.illyrian.bnf.ast.BnfTreeAction;
import au.com.illyrian.bnf.ast.BnfTreeAlternative;
import au.com.illyrian.bnf.ast.BnfTreeEmpty;
import au.com.illyrian.bnf.ast.BnfTreeList;
import au.com.illyrian.bnf.ast.BnfTreeLookahead;
import au.com.illyrian.bnf.ast.BnfTreeMethodCall;
import au.com.illyrian.bnf.ast.BnfTreeName;
import au.com.illyrian.bnf.ast.BnfTreeNonterminal;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.ast.BnfTreeRecover;
import au.com.illyrian.bnf.ast.BnfTreeReserved;
import au.com.illyrian.bnf.ast.BnfTreeRule;
import au.com.illyrian.bnf.ast.BnfTreeSequence;
import au.com.illyrian.bnf.ast.BnfTreeTarget;
import au.com.illyrian.classmaker.CallStack;
import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMaker.AndOrExpression;
import au.com.illyrian.classmaker.ClassMakerIfc;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionVisitor;
import au.com.illyrian.classmaker.types.ClassType;
import au.com.illyrian.classmaker.types.PrimitiveType;
import au.com.illyrian.classmaker.types.Type;
import au.com.illyrian.classmaker.types.Value;
import au.com.illyrian.parser.ParserException;

public class BnfMakerVisitor extends AstExpressionVisitor
{
    private String defaultTypeName = "java.lang.Object";
    private Type   defaultType = null;
    private boolean actionRequired = false;
    private Map<String, BnfTreeRule> ruleSet = null;
    private final Vector<Type>localVariables = new Vector<Type>();
    private String filename;
    private int lineNumber;
    
    public BnfMakerVisitor() {
    }
    
    public BnfMakerVisitor(ClassMakerIfc classMaker)
    {
        super(classMaker);
    }

    public Type getDefaultType()
    {
        if (defaultType == null) {
            Type type = getMaker().findType(defaultTypeName);
            if (type != null) {
                setDefaultType(type);
            } else {
                throw new IllegalArgumentException("Unknown class type: " + defaultType);
            }
        }
        return defaultType;
    }
    
    public void setDefaultType(Type defaultType) {
        this.defaultType = defaultType;
    }

    public void setDefaultTypeName(String typeName)
    {
        this.defaultTypeName = typeName;
    }

    @Override
    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String sourceFilename)
    {
        this.filename = sourceFilename;
    }
    
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public void setLineNumber(BnfTree tree) {
        setLineNumber(tree.getLineNumber());
    }
    
    public Map<String, BnfTreeRule> getRuleSet()
    {
        return ruleSet;
    }

    public boolean isActionRequired()
    {
        return actionRequired;
    }

    public void setActionRequired(boolean actionRequired)
    {
        this.actionRequired = actionRequired;
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
        setLineNumber(tree);
        tree.getRules().resolveDeclaration(this);
        return PrimitiveType.VOID_TYPE;
    }

    public Type resolveDeclaration(BnfTreeList list)
    {
        BnfTreeRule [] rules = list.toRuleArray();
        for (BnfTree rule : rules) {
            rule.resolveDeclaration(this);
        }
        return PrimitiveType.VOID_TYPE;
    }

    public Type resolveDeclaration(BnfTreeRule rule)
    {
        Type returnType = rule.getTarget().resolveDeclaration(this);
        setLineNumber(rule);
        String methodName = rule.getTarget().getName();
        if (getMaker().getPass() == ClassMaker.FIRST_PASS) {
            returnType = methodForward(methodName, returnType);
        } else {
            returnType = methodBegin(methodName, returnType);
            rule.getBody().resolveDeclaration(this);
            methodEnd();
        }
        return returnType;
    }

    public Type resolveDeclaration(BnfTreeTarget target)
    {
        String typeName = target.getType();
        Type type = getMaker().findType(typeName);
        return type;
    }

    public Type resolveDeclaration(BnfTreeName target)
    {
        return getDefaultType();
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
            setLineNumber(left);
            Value cond = left.getHead().resolveLookahead(this, 0);
            getMaker().If(cond);
            {
                getMaker().Begin();
                left.resolveSequence(this, variable);
                getMaker().End();
            }
            BnfTree right = alternatives[offset];
            // If the right hand side is EMPTY then this is an optional 
            // clause so no need for an else part.
            if (!right.isEmpty() || isOnlyOneOption()) {
                getMaker().Else();
                getMaker().Begin();
                setLineNumber(right);
                resolveAlternatives(alternatives, offset+1, variable);
                getMaker().End();
            }
            getMaker().EndIf();
        } else {
            //getMaker().Begin();
            left.resolveSequence(this, variable);
            //getMaker().End();
        }
        return PrimitiveType.VOID_TYPE;
    }

    public Type resolveDeclaration(BnfTreeSequence seq) {
        return resolveSequence(seq, 1);
    }
    
    public Type resolveSequence(BnfTreeSequence seq, int variable) {
        BnfTree [] sequence = seq.toSeqArray();
        int scopeSize = variable-1;
        int offset = 0;
        while (offset < sequence.length) {
            BnfTree tree = sequence[offset];
            setLineNumber(tree);
            if (!tree.isMacro()) {
                Type type = tree.resolveSequence(this, variable);
                localVariables.add(type);
                variable++;
            }
            offset++;
        }
        // Clear local variables going out of scope.
        localVariables.setSize(scopeSize);
        return PrimitiveType.VOID_TYPE;
    }
    
    public Type resolveSequence(BnfTreeAlternative tree, int variable) {
        return resolveAlternatives(tree.toAltArray(), 1, variable);
    }
    
    public Type resolveSequence(BnfTreeAction tree, int variable) {
        setLineNumber(tree);
        Value value = tree.resolveType(this);
        assign(0, value);
        return value.getType();
    }

    public Type resolveSequence(BnfTree tree, int variable) {
        setLineNumber(tree);
        declare(variable, getDefaultType());
        Value value = tree.resolveType(this);
        assign(variable, value);
        return value.getType();
    }

    public Type resolveSequence(BnfTreeName tree, int variable) {
        setLineNumber(tree);
        declare(variable, ClassType.STRING_TYPE);
        Value value = tree.resolveType(this);
        assign(variable, value);
        return value.getType();
    }

    public Type resolveSequence(BnfTreeEmpty empty, int variable)
    {
        // Automatically return $1 if that is the only option.
        if (isOnlyOneOption()) {
            getMaker().Eval(getMaker().Assign("$0", getMaker().Get("$1")));
        }
        return null; // empty();
    }
    
    boolean isOnlyOneOption() {
        return (isActionRequired() 
                && localVariables.size() == 1
                && localVariables.lastElement() != PrimitiveType.VOID_TYPE);
    }

    public Type resolveSequence(BnfTreeMethodCall call, int variable)
    {
        setLineNumber(call);
        getMaker().Eval(resolveType(call));
        return PrimitiveType.VOID_TYPE;
    }

    public Type resolveSequence(BnfTreeReserved reserved, int variable)
    {
        setLineNumber(reserved);
        declare(variable, ClassType.STRING_TYPE);
        Value value = expect(reserved.getName());
        assign(variable, value);
        return PrimitiveType.VOID_TYPE;
    }

    public Type resolveSequence(BnfTreeNonterminal nonterm, int variable)
    {
        setLineNumber(nonterm);
        String name = nonterm.getName();
        Value value =  getMaker().Call(getMaker().This(), name, getMaker().Push());
        Type type = value.getType();
        declare(variable, type);
        assign(variable, value);
        return type;
    }

    public Value resolveLookahead(BnfTreeAlternative alt, int howFar) {
        return alt.getLeft().resolveLookahead(this, howFar);
    }

    public Value resolveLookahead(BnfTreeSequence seq, int howFar)
    {
        BnfTree [] list = seq.toSeqArray();
        if (list.length < 2) {
            throw new IllegalArgumentException("Array length must be more than 2 but was " + list.length);
        }
        Value result = null;
        AndOrExpression andThen = null;
        for (int i=0; i<list.length; i++) {
            setLineNumber(list[i]);
            Value cond = list[i].resolveLookahead(this, i);
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

    public Value resolveLookahead(BnfTreeReserved reserved, int howFar)
    {
        return match(reserved.getName(), howFar);
    }

    public Value resolveLookahead(BnfTreeRule  rule, int howFar)
    {
        BnfTree [] list = rule.getFirstSet().toArray();
        Value result = null;
        if (list.length == 1) { 
            result = list[0].resolveLookahead(this, howFar);
        } else if (list.length > 1) {
            result = resolveLookahead(list);
        } else {
            result = getMaker().Literal(true);
        }
        return result;
    }

    public Value resolveLookahead(BnfTreeLookahead macro, int howFar)
    {
        BnfTree<Type> pattern = macro.getPattern();
        return pattern.resolveLookahead(this, howFar);
    }
    
    Value resolveLookahead(BnfTree [] list)
    {
        if (list.length < 2) {
            throw new IllegalArgumentException("Array length must be more than 2 but was " + list.length);
        }
        Value result = null;
        AndOrExpression orElse = null;
        for (int i=0; i<list.length; i++) {
            if (list[i] == null) {
                continue;
            }
            Value cond = list[i].resolveLookahead(this, 0);
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

    Value resolveLookahead(String [] list)
    {
        if (list.length < 2) {
            throw new IllegalArgumentException("Array length must be more than 2 but was " + list.length);
        }
        Value result = null;
        AndOrExpression orElse = null;
        for (int i=0; i<list.length; i++) {
            Value cond = match(list[i]);
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

    public Value resolveLookahead(BnfTreeRecover bnfTreeRecover, int howFar)
    {
        return null;
    }

    public Value resolveLookahead(BnfTreeNonterminal ruleName, int howFar)
    {
        BnfTreeRule rule = ruleSet.get(ruleName);
        return resolveLookahead(rule, howFar);
    }

    public Value resolveLookahead(BnfTreeName term, int howFar)
    {
        String name = term.getName();
        // Names in lookahead clauses are not converted to BnfTreeNonterminal
        if (isRule(name)) {
            BnfTreeRule rule = ruleSet.get(name);
            return resolveLookahead(rule, howFar);
        } else {
            return match(name, howFar);
        }
    }

    public Value resolveType(BnfTreeNonterminal tree)
    {
        String ruleName = tree.getName();
        Value reference = getMaker().This();
        setLineNumber(tree);
        return getMaker().Call(reference, ruleName, getMaker().Push());
    }

    public Value resolveType(BnfTreeName tree)
    {
        String name = tree.getName();
        return expect(name);
    }

    public Value resolveType(BnfTreeAction tree)
    {
        return tree.getExpression().resolveValue(this);
    }

    public Value resolveType(AstExpression tree)
    {
        throw new IllegalStateException("No special case for ExpressionTree type: " 
                + tree.getClass().getSimpleName());
    }

    public Value resolveType(BnfTreeMethodCall call)
    {
        Value reference = getMaker().This();
        CallStack callStack = resolveCallStack(call);
        setLineNumber(call);
        return getMaker().Call(reference, callStack.getMethodName(), callStack);
    }
    
    public CallStack resolveCallStack(BnfTreeMethodCall call)
    {
        String methodName = call.getName();
        CallStack actualParameters;
        setLineNumber(call);
        if (call.getActuals() == null)
            actualParameters = getMaker().Push();
        else
            actualParameters = call.getActuals().resolveCallStack(this);
        actualParameters.setMethodName(methodName);
        return actualParameters;
    }

    /************* Convenience Methods **************/
    
    Type methodForward(String methodName, Type returnType) {
        getMaker().Method(methodName, returnType, ClassMaker.ACC_PUBLIC);
        getMaker().Forward();
        return returnType;
    }
    
    Type methodBegin(String methodName, Type returnType) {
        getMaker().Method(methodName, returnType, ClassMaker.ACC_PUBLIC);
        getMaker().Begin();
        getMaker().Declare("$0", returnType, 0);
        return returnType;
    }
    
    void methodEnd() {
        getMaker().Return(getMaker().Get("$0"));
        getMaker().End();
    }
    
    void declare(int index, Type returnType) {
        getMaker().Declare("$" + index, returnType, 0);
    }

    void assign(int index, Value type) {
        getMaker().Eval(getMaker().Assign("$" + index, type));
    }
    
    Value match(String token) {
        return getMaker().Call(getMaker().This(), "match", 
                getMaker().Push(getMaker().Get(getMaker().This(), token)));
    }

    Value match(String token, int howFar) {
        if (howFar==0) {
            return match(token);
        } else {
            return getMaker().Call(getMaker().This(), "match", 
                    getMaker().Push(getMaker().Get(getMaker().This(), token)).Push(getMaker().Literal(howFar)));
        }
    }

    Value expect(String token) {
        return getMaker().Call(getMaker().This(), "expect", 
                getMaker().Push(getMaker().Get(getMaker().This(), token)));
    }
    
    Value empty() {
        return getMaker().Call(getMaker().This(), "empty", getMaker().Push());
    }

}
