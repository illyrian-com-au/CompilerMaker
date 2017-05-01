package au.com.illyrian.parser.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.LexerInputStream;
import junit.framework.TestCase;

public class InputStreamTest extends TestCase
{
    File srcDir = new File("test");
    
    StringReader reader;
    StringWriter writer;
    PrintWriter  out;
    
    public void setUp()
    {
        writer = new StringWriter() ;
        out = new PrintWriter(writer);
    }
    
    public StringReader getReader()
    {
        return new StringReader(writer.toString());
    }
    
    public void testSimpleReader() throws Exception
    {
        out.println("package;");
        out.println("import;");
        Latin1Lexer tokeniser = new Latin1Lexer(new LexerInputStream(getReader(), null));
        assertEquals("token", TokenType.IDENTIFIER, tokeniser.nextToken());
        assertEquals("String", "package", tokeniser.getTokenValue());
        assertEquals("token", TokenType.DELIMITER, tokeniser.nextToken());
        assertEquals("String", ";", tokeniser.getTokenValue());
        assertEquals("token", TokenType.IDENTIFIER, tokeniser.nextToken());
        assertEquals("String", "import", tokeniser.getTokenValue());
        assertEquals("token", TokenType.DELIMITER, tokeniser.nextToken());
        assertEquals("String", ";", tokeniser.getTokenValue());
        assertEquals("token", TokenType.END, tokeniser.nextToken());
    }
    
    public void testSimpleTokeniser() throws Exception
    {
        out.println("package;");
        out.println("import;");
        Latin1Lexer tokeniser = new Latin1Lexer(new LexerInputStream(getReader(), null));
        expect(tokeniser, TokenType.IDENTIFIER, "package");
        expect(tokeniser, TokenType.DELIMITER, ";");
        expect(tokeniser, TokenType.IDENTIFIER, "import");
        expect(tokeniser, TokenType.DELIMITER, ";");
        expect(tokeniser, TokenType.END, null);
    }
    
    public void testSpacedTokeniser() throws Exception
    {
        out.println();
        out.println("package ;");
        out.println();
        out.println();
        out.println("import; ");
        out.println();
        Latin1Lexer tokeniser = new Latin1Lexer(new LexerInputStream(getReader(), null));
        expect(tokeniser, TokenType.IDENTIFIER, "package");
        expect(tokeniser, TokenType.DELIMITER, ";");
        expect(tokeniser, TokenType.IDENTIFIER, "import");
        expect(tokeniser, TokenType.DELIMITER, ";");
        expect(tokeniser, TokenType.END, null);
    }
    
    public void testFileLoad() throws Exception
    {
        File file = new File(srcDir, "au/com/illyrian/parser/impl/InputStreamTest.java");
        System.out.println(file.getAbsolutePath());
        LexerInputStream input = new LexerInputStream(file, 
                "au/com/illyrian/parser/impl/InputStreamTest.java");
        Latin1Lexer tokeniser = new Latin1Lexer(input);
        assertEquals("token", TokenType.IDENTIFIER, tokeniser.nextToken());
        assertEquals("String", "package", tokeniser.getTokenValue());
        expect(tokeniser, TokenType.IDENTIFIER, "au");
        expect(tokeniser, TokenType.OPERATOR, ".");
        expect(tokeniser, TokenType.IDENTIFIER, "com");
        expect(tokeniser, TokenType.OPERATOR, ".");
        expect(tokeniser, TokenType.IDENTIFIER, "illyrian");
        expect(tokeniser, TokenType.OPERATOR, ".");
        expect(tokeniser, TokenType.IDENTIFIER, "parser");
        expect(tokeniser, TokenType.OPERATOR, ".");
        expect(tokeniser, TokenType.IDENTIFIER, "impl");
        expect(tokeniser, TokenType.DELIMITER, ";");
        expect(tokeniser, TokenType.IDENTIFIER, "import");
        expect(tokeniser, TokenType.IDENTIFIER, "java");
        expect(tokeniser, TokenType.OPERATOR, ".");
        expect(tokeniser, TokenType.IDENTIFIER, "io");
        expect(tokeniser, TokenType.OPERATOR, ".");
        expect(tokeniser, TokenType.IDENTIFIER, "File");
        expect(tokeniser, TokenType.DELIMITER, ";");
        input.close();
    }
    
    void expect(Lexer tok, TokenType token, String value)
    {
        assertEquals("Token", token, tok.nextToken());
        assertEquals("String", value, tok.getTokenValue());
    }
}
