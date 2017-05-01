package au.com.illyrian.bnf.maker;

import au.com.illyrian.bnf.BnfParserToken;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.jesub.ast.AstStructureFactory;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.Token;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.ParserBase;

public abstract class BnfParserBase extends ParserBase implements ParseMembers
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
    public Token SEMI = new BnfParserToken(TokenType.DELIMITER, ";");
    public Token COMMA = new BnfParserToken(TokenType.DELIMITER, ",");
    public Token MULT = new BnfParserToken(TokenType.OPERATOR, "*");
    public Token DOTSTAR = new BnfParserToken(TokenType.OPERATOR, ".*");
    public Token BEGIN = new BnfParserToken(TokenType.DELIMITER, "{");
    public Token END = new BnfParserToken(TokenType.DELIMITER, "}");
    
    public BnfParserBase () {
        ast = new AstStructureFactory();
    }
    
    @Override
    public AstStructure parseMembers(CompilerContext context)
    {
        setCompilerContext(context);

        nextToken();
        AstStructure tree = goal();

        return tree;
    }
    
    public abstract AstStructure goal();
}
