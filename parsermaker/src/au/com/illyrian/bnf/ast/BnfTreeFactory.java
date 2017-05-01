package au.com.illyrian.bnf.ast;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.classmaker.ast.LineNumber;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.TokenType;

public class BnfTreeFactory extends AstExpressionFactory
{
    LineNumber source;
    
    public BnfTreeFactory (LineNumber lineNumber) {
        this.source = lineNumber;
    }
    
    public BnfTreeParser Parser(BnfTree list) {
        return new BnfTreeParser(list, getLineNumber());
    }
    
    public int getLineNumber() {
        return source == null ? 0 : source.getLineNumber();
    }

    public BnfTreeList List(BnfTree left, BnfTree right) {
        return new BnfTreeList(left, right);
    }
    
    public BnfTreeRule Rule(BnfTree target, BnfTree body) {
        return new BnfTreeRule(target, body);
    }

    public BnfTreeTarget Target(BnfTree name, BnfTree type) {
        BnfTreeTarget result = new BnfTreeTarget(name, type);
        return result;
    }

    public BnfTree Seq(BnfTree left, BnfTree right) {
        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        } else {
            return new BnfTreeSequence(left, right);
        }
    }
    
    public BnfTreeAlternative Alt(BnfTree left, BnfTree right) {
        return new BnfTreeAlternative(left, right);
    }
    
    public BnfTreeMethodCall MethodCall(BnfTree name, AstExpression actuals) {
        BnfTreeMethodCall result = new BnfTreeMethodCall(name, actuals);
        return result;
    }

    public BnfTreeLookahead Lookahead(BnfTree pattern) {
        return new BnfTreeLookahead(pattern, getLineNumber());
    }

    public BnfTreeRecover Recover(BnfTree pattern) {
        BnfTreeRecover result = new BnfTreeRecover(pattern, getLineNumber());
        return result;
    }

    public BnfTreeName BnfName(Lexer lexer)
    {
        if (lexer.getTokenType() == TokenType.IDENTIFIER) {
            BnfTreeName result = BnfName(lexer.getTokenValue());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("Identifier expected.");
        }
    }
    
    public BnfTreeName BnfName(String name) {
        BnfTreeName result = new BnfTreeName(name, getLineNumber());
        return result;
    }
    
    public BnfTreeReserved BnfReserved(String name) {
        BnfTreeReserved result = new BnfTreeReserved(name, getLineNumber());
        return result;
    }
     
    public BnfTreeReserved Reserved(Lexer lexer)
    {
        BnfTreeReserved result = BnfReserved(lexer.getTokenValue());
        lexer.nextToken();
        return result;
    }

    // FIXME don't think this is necessary
    public BnfTreeNonterminal Nonterminal(String name)
    {
        BnfTreeNonterminal result = new BnfTreeNonterminal(name, getLineNumber());
        return result;
    }
    
    public BnfTreeAction Action(AstExpression expr) {
        return new BnfTreeAction(expr, getLineNumber());
    }

    public BnfTree Empty()
    {
        return new BnfTreeEmpty(getLineNumber());
    }

}
