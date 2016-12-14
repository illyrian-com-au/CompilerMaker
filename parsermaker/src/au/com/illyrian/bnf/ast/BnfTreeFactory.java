package au.com.illyrian.bnf.ast;

import au.com.illyrian.classmaker.SourceLine;
import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;

public class BnfTreeFactory extends AstExpressionFactory
{
    public BnfTreeFactory () {
    }

    public BnfTreeFactory (SourceLine source) {
        super(source);
    }
    
    public BnfTreeParser Parser(BnfTree list) {
        return new BnfTreeParser(list);
    }

    public BnfTreeList List(BnfTree left, BnfTree right) {
        return new BnfTreeList(left, right);
    }
    
    public BnfTreeRule Rule(BnfTree target, BnfTree body) {
        return new BnfTreeRule(target, body);
    }

    public BnfTreeTarget Target(BnfTree name, BnfTree type) {
        return new BnfTreeTarget(name, type);
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
    
    public BnfTreeMethodCall MethodCall(AstExpression name, AstExpression actuals) {
        return new BnfTreeMethodCall(name, actuals);
    }

    public BnfTreeMacroCall MacroCall(AstExpression name, AstExpression pattern) {
        return new BnfTreeMacroCall(name, pattern);
    }

    public BnfTreeName BnfName(Lexer lexer) throws ParserException
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
        return new BnfTreeName(name);
    }
    
    public BnfTreeReserved BnfReserved(String name) {
        return new BnfTreeReserved(name);
    }
    
    public BnfTreeString BnfString(Lexer lexer) {
        if (lexer.getTokenType() == TokenType.STRING) {
            BnfTreeString result = BnfString(lexer.getTokenString());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("String expected.");
        }
    }
    
    public BnfTreeString BnfString(String string) {
        return new BnfTreeString(string);
    }
 
    public AstExpression Integer(Lexer lexer)
    {
        if (lexer.getTokenType() == TokenType.NUMBER) {
            AstExpression result = Literal(lexer.getTokenInteger());
            lexer.nextToken();
            return result;
        } else {
            throw new IllegalArgumentException("Integer expected.");
        }
    }

    public AstExpression Decimal(Lexer lexer)
    {
        if (lexer.getTokenType() == TokenType.DECIMAL) {
            AstExpression result = Literal(lexer.getTokenFloat());
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
        BnfTreeNonterminal result = Nonterminal(lexer.getTokenValue());
        lexer.nextToken();
        return result;
    }

    public BnfTreeNonterminal Nonterminal(String name)
    {
        BnfTreeNonterminal result = new BnfTreeNonterminal(name);
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
