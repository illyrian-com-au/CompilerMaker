package au.com.illyrian.jsub.bnf;

import au.com.illyrian.bnf.parser.BnfParserToken;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.jesub.ast.AstStructureFactory;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.Token;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.expr.AstExpressionPrecidenceAction;
import au.com.illyrian.parser.expr.AstExpressionPrecidenceParser;

public abstract class JsubParserBase extends AstExpressionPrecidenceParser 
    implements ParseModule<AstStructure>
{
    public AstStructureFactory ast;
    
    public Token IDENTIFIER = new BnfParserToken(TokenType.IDENTIFIER);
    public Token DOT = new BnfParserToken(TokenType.OPERATOR, ".");
    public Token PACKAGE = new BnfParserToken(TokenType.IDENTIFIER, "package");
    public Token IMPORT = new BnfParserToken(TokenType.IDENTIFIER, "import");
    public Token PRIVATE = new BnfParserToken(TokenType.IDENTIFIER, "private");
    public Token PROTECTED = new BnfParserToken(TokenType.IDENTIFIER, "protected");
    public Token PUBLIC = new BnfParserToken(TokenType.IDENTIFIER, "public");
    public Token STATIC = new BnfParserToken(TokenType.IDENTIFIER, "static");
    public Token ABSTRACT = new BnfParserToken(TokenType.IDENTIFIER, "abstract");
    public Token FINAL = new BnfParserToken(TokenType.IDENTIFIER, "final");
    public Token STRICTFP = new BnfParserToken(TokenType.IDENTIFIER, "strictfp");
    public Token TRANSIENT = new BnfParserToken(TokenType.IDENTIFIER, "transient");
    public Token EXTENDS = new BnfParserToken(TokenType.IDENTIFIER, "extends");
    public Token IMPLEMENTS = new BnfParserToken(TokenType.IDENTIFIER, "implements");
    public Token CLASS = new BnfParserToken(TokenType.IDENTIFIER, "class");
    public Token INTERFACE = new BnfParserToken(TokenType.IDENTIFIER, "interface");
    public Token BOOLEAN = new BnfParserToken(TokenType.IDENTIFIER, "boolean");
    public Token BYTE = new BnfParserToken(TokenType.IDENTIFIER, "byte");
    public Token CHAR = new BnfParserToken(TokenType.IDENTIFIER, "char");
    public Token SHORT = new BnfParserToken(TokenType.IDENTIFIER, "short");
    public Token INT = new BnfParserToken(TokenType.IDENTIFIER, "int");
    public Token LONG = new BnfParserToken(TokenType.IDENTIFIER, "long");
    public Token FLOAT = new BnfParserToken(TokenType.IDENTIFIER, "float");
    public Token DOUBLE = new BnfParserToken(TokenType.IDENTIFIER, "double");
    public Token VOID = new BnfParserToken(TokenType.IDENTIFIER, "void");
    public Token BREAK = new BnfParserToken(TokenType.IDENTIFIER, "break");
    public Token CONTINUE = new BnfParserToken(TokenType.IDENTIFIER, "continue");
    public Token RETURN = new BnfParserToken(TokenType.IDENTIFIER, "return");
    public Token FOR = new BnfParserToken(TokenType.IDENTIFIER, "for");
    public Token WHILE = new BnfParserToken(TokenType.IDENTIFIER, "while");
    public Token IF = new BnfParserToken(TokenType.IDENTIFIER, "if");
    public Token ELSE = new BnfParserToken(TokenType.IDENTIFIER, "else");
    public Token TRY = new BnfParserToken(TokenType.IDENTIFIER, "try");
    public Token CATCH = new BnfParserToken(TokenType.IDENTIFIER, "catch");
    public Token FINALLY = new BnfParserToken(TokenType.IDENTIFIER, "finally");
    public Token SEMI = new BnfParserToken(TokenType.DELIMITER, ";");
    public Token COLON = new BnfParserToken(TokenType.DELIMITER, ":");
    public Token COMMA = new BnfParserToken(TokenType.DELIMITER, ",");
    public Token MULT = new BnfParserToken(TokenType.OPERATOR, "*");
    public Token DOTSTAR = new BnfParserToken(TokenType.OPERATOR, ".*");
    public Token BEGIN = new BnfParserToken(TokenType.DELIMITER, "{");
    public Token END = new BnfParserToken(TokenType.DELIMITER, "}");
    public Token LPAR = new BnfParserToken(TokenType.DELIMITER, "(");
    public Token RPAR = new BnfParserToken(TokenType.DELIMITER, ")");
    public Token LBRAC = new BnfParserToken(TokenType.DELIMITER, "[");
    public Token RBRAC = new BnfParserToken(TokenType.DELIMITER, "]");
    
    public JsubParserBase () {
        ast = new AstStructureFactory();
        AstExpressionPrecidenceAction precidenceActions = new AstExpressionPrecidenceAction(ast);
        setPrecidenceActions(precidenceActions);
    }
    
    public AstStructure parseModule(CompilerContext context)
    {
        setCompilerContext(context);

        nextToken();
        AstStructure tree = goal();

        return tree;
    }
    
    public abstract AstStructure goal();
    
//    public AstExpression precedence(int level) {
//        return null;
//    }
}
