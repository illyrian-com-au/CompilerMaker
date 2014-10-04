package au.com.illyrian.parser.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import au.com.illyrian.parser.Lexer;
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
        assertEquals("token", Lexer.IDENTIFIER, tokeniser.nextToken());
        assertEquals("String", "package", tokeniser.getTokenValue());
        assertEquals("token", Lexer.DELIMITER, tokeniser.nextToken());
        assertEquals("String", ";", tokeniser.getTokenValue());
        assertEquals("token", Lexer.IDENTIFIER, tokeniser.nextToken());
        assertEquals("String", "import", tokeniser.getTokenValue());
        assertEquals("token", Lexer.DELIMITER, tokeniser.nextToken());
        assertEquals("String", ";", tokeniser.getTokenValue());
        assertEquals("token", Lexer.END, tokeniser.nextToken());
    }
    
    public void testSimpleTokeniser() throws Exception
    {
        out.println("package;");
        out.println("import;");
        Latin1Lexer tokeniser = new Latin1Lexer(new LexerInputStream(getReader(), null));
        expect(tokeniser, Lexer.IDENTIFIER, "package");
        expect(tokeniser, Lexer.DELIMITER, ";");
        expect(tokeniser, Lexer.IDENTIFIER, "import");
        expect(tokeniser, Lexer.DELIMITER, ";");
        expect(tokeniser, Lexer.END, null);
    }
    
    public void testSpacedTokeniser() throws Exception
    {
        out.println();
        out.println("package;");
        out.println();
        out.println("import;");
        out.println();
        Latin1Lexer tokeniser = new Latin1Lexer(new LexerInputStream(getReader(), null));
        expect(tokeniser, Lexer.IDENTIFIER, "package");
        expect(tokeniser, Lexer.DELIMITER, ";");
        expect(tokeniser, Lexer.IDENTIFIER, "import");
        expect(tokeniser, Lexer.DELIMITER, ";");
        expect(tokeniser, Lexer.END, null);
    }
    
    public void testFileLoad() throws Exception
    {
        File file = new File(srcDir, "au/com/illyrian/parser/impl/InputStreamTest.java");
        System.out.println(file.getAbsolutePath());
        LexerInputStream input = new LexerInputStream(file);
        Latin1Lexer tokeniser = new Latin1Lexer(input);
        assertEquals("token", Lexer.IDENTIFIER, tokeniser.nextToken());
        assertEquals("String", "package", tokeniser.getTokenValue());
        expect(tokeniser, Lexer.IDENTIFIER, "au");
        expect(tokeniser, Lexer.OPERATOR, ".");
        expect(tokeniser, Lexer.IDENTIFIER, "com");
        expect(tokeniser, Lexer.OPERATOR, ".");
        expect(tokeniser, Lexer.IDENTIFIER, "illyrian");
        expect(tokeniser, Lexer.OPERATOR, ".");
        expect(tokeniser, Lexer.IDENTIFIER, "parser");
        expect(tokeniser, Lexer.OPERATOR, ".");
        expect(tokeniser, Lexer.IDENTIFIER, "impl");
        expect(tokeniser, Lexer.DELIMITER, ";");
        expect(tokeniser, Lexer.IDENTIFIER, "import");
        expect(tokeniser, Lexer.IDENTIFIER, "java");
        expect(tokeniser, Lexer.OPERATOR, ".");
        expect(tokeniser, Lexer.IDENTIFIER, "io");
        expect(tokeniser, Lexer.OPERATOR, ".");
        expect(tokeniser, Lexer.IDENTIFIER, "File");
        expect(tokeniser, Lexer.DELIMITER, ";");
        input.close();
    }
    
    void expect(Lexer tok, int token, String value)
    {
        assertEquals("Token", token, tok.nextToken());
        assertEquals("String", value, tok.getTokenValue());
    }
}
