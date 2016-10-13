package au.com.illyrian.bnf.ast;

import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;

public class BnfTreeFactory extends AstExpressionFactory
{
    public BnfTreeFactory () {
    }

    public BnfTreeFactory (SourceLine source) {
        super(source);
    }

    public BnfTreeList List(BnfTree left, BnfTree right) {
        return new BnfTreeList(left, right);
    }
    
    public BnfTreeRule Rule(BnfTree target, BnfTree body) {
        return new BnfTreeRule(target, body);
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
    
    public BnfTreeMethodCall Call(BnfTree name, BnfTree actuals) {
        return new BnfTreeMethodCall(name, actuals);
    }

    public BnfTreeName Name(Lexer lexer) throws ParserException
    {
        if (lexer.getToken() == Lexer.IDENTIFIER) {
            BnfTreeName result = new BnfTreeName(lexer.getTokenValue());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("Identifier expected.");
        }
    }
    
    public BnfTreeString String(Lexer lexer) {
        if (lexer.getToken() == Lexer.STRING) {
            BnfTreeString result = new BnfTreeString(lexer.getTokenString());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("String expected.");
        }
    }

    public BnfTree Integer(Lexer lexer)
    {
        if (lexer.getToken() == Lexer.INTEGER) {
            BnfTreeInteger result = new BnfTreeInteger(lexer.getTokenInteger());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("Integer expected.");
        }
    }

    public BnfTree Decimal(Lexer lexer)
    {
        if (lexer.getToken() == Lexer.DECIMAL) {
            BnfTreeDecimal result = new BnfTreeDecimal(lexer.getTokenFloat());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("Integer expected.");
        }
    }

    public BnfTreeReserved Reserved(Lexer lexer)
    {
        BnfTreeReserved result = new BnfTreeReserved(lexer.getTokenValue());
        lexer.nextToken();
        return result;
    }

    public BnfTreeNonterminal Nonterminal(Lexer lexer)
    {
        BnfTreeNonterminal result = new BnfTreeNonterminal(lexer.getTokenValue());
        lexer.nextToken();
        return result;
    }

    public BnfTree Empty()
    {
        return new BnfTreeEmpty();
    }

    public BnfTree Comma(BnfTree left, BnfTree right)
    {
        return new BnfTreeComma(left, right);
    }

}
