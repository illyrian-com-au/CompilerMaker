package au.com.illyrian.parser.impl;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.impl.LexerInputString;
import junit.framework.TestCase;

public class Latin1LexerTest extends TestCase
{
    /**
     * A map from reserved word to an object.
     */
    public static Properties reservedWords = new Properties();
    {
        reservedWords.put("and", "");
        reservedWords.put("or", "");
        reservedWords.put("if", "");
        reservedWords.put("else", "");
        reservedWords.put("while", "");
    }

    /**
     * A map from operator to an object.
     */
    public static Properties operators = new Properties();
    {
        operators.put("+", "");
        operators.put("-", "");
        operators.put("/", "");
        operators.put("*", "");
        operators.put("%", "");
        operators.put("=", "");
    }

    public void testSpanIdentifier()
    {
        Input inp = new LexerInputString("a big\tname");
        Latin1Lexer tok = new Latin1Lexer(inp);
        // Tokenise "a "
        assertTrue("isStartIdentifier", tok.isIdentifierStartChar(inp.startChar()));
        tok.spanIdentifier();
        assertEquals("Identifier expected", "a", tok.getTokenValue());
        assertEquals("Start", 0, tok.getStart());
        assertEquals("Finish", 1, tok.getFinish());
        assertEquals("isWhitespace", ' ', inp.startChar());
        assertTrue("isWhitespace", tok.isWhitespace(inp.startChar()));
        tok.spanWhiteSpace();
        assertEquals("Start", 1, tok.getStart());
        assertEquals("Finish", 2, tok.getFinish());
        assertEquals("Whitespace expected", " ", tok.getTokenValue());

        // Tokenise "big\t"
        assertTrue("isStartIdentifier", tok.isIdentifierStartChar(inp.startChar()));
        tok.spanIdentifier();
        assertEquals("Identifier expected", "big", tok.getTokenValue());
        assertEquals("Start", 2, tok.getStart());
        assertEquals("Finish", 5, tok.getFinish());
        assertEquals("isWhitespace", '\t', (int) inp.startChar());
        assertTrue("isWhitespace", tok.isWhitespace(inp.startChar()));
        tok.spanWhiteSpace();
        assertEquals("Start", 5, tok.getStart());
        assertEquals("Finish", 6, tok.getFinish());
        assertEquals("Whitespace expected", "\t", tok.getTokenValue());

        // Tokenise "name"
        assertTrue("isStartIdentifier", tok.isIdentifierStartChar(inp.startChar()));
        tok.spanIdentifier();
        assertEquals("Identifier expected", "name", tok.getTokenValue());
        assertEquals("Start", 6, tok.getStart());
        assertEquals("Finish", 10, tok.getFinish());
        assertEquals("NULL expected", Input.NULL, (int) inp.startChar());
    }

    public void testIdentifiers()
    {
        LexerInputString inp = new LexerInputString("a\n b\t c");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Identifier expected", Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("Whitespace '' expected", "", tok.getWhitespace());
        assertEquals("Identifier 'a' expected", "a", tok.getTokenValue());
        assertEquals("Identifier expected", Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("Whitespace '' expected", "\n ", tok.getWhitespace());
        assertEquals("Identifier 'b' expected", "b", tok.getTokenValue());
        assertEquals("Whitespace '' expected", "\n ", tok.getWhitespace());
        assertEquals("Identifier expected", Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("Whitespace '' expected", "\t ", tok.getWhitespace());
        assertEquals("Identifier 'c' expected", "c", tok.getTokenValue());
        assertEquals("END expected", Lexer.END, tok.nextToken());
        assertEquals("Whitespace '' expected", "", tok.getWhitespace());
    }

    public void testPerentheses()
    {
        LexerInputString inp = new LexerInputString("()");
        Latin1Lexer tok = new Latin1Lexer(inp);

        assertEquals("Open perenthesis expected", Lexer.OPEN_P, tok.nextToken());
        assertEquals("Perenthesis '(' expected", "(", tok.getTokenValue());
        assertEquals("Close perenthesis expected", Lexer.CLOSE_P, tok.nextToken());
        assertEquals("Perenthesis ')' expected", ")", tok.getTokenValue());
        assertEquals("END expected", Lexer.END, tok.nextToken());
    }

    public void testPerenthesesIdentifier()
    {
        LexerInputString inp = new LexerInputString("(a)");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Open perenthesis expected", Lexer.OPEN_P, tok.nextToken());
        assertEquals("Perenthesis '(' expected", "(", tok.getTokenValue());
        assertEquals("Identifier expected", Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("Identifier 'a' expected", "a", tok.getTokenValue());
        assertEquals("Close perenthesis expected", Lexer.CLOSE_P, tok.nextToken());
        assertEquals("Perenthesis ')' expected", ")", tok.getTokenValue());
        assertEquals("END expected", Lexer.END, tok.nextToken());
    }

    public void testStringLiteral()
    {
        LexerInputString inp = new LexerInputString("\"fred\"");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Lexer.STRING", Lexer.STRING, tok.nextToken());
        assertEquals("String literal", "\"fred\"", tok.getTokenValue());
        assertEquals("String value", "fred", tok.getTokenString());
        assertEquals("String delimter", '"', tok.getTokenDelimiter());
        assertEquals("END expected", Lexer.END, tok.nextToken());
    }

    public void testEmptyStringLiteral()
    {
        LexerInputString inp = new LexerInputString("\"\"");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Lexer.STRING", Lexer.STRING, tok.nextToken());
        assertEquals("String literal", "\"\"", tok.getTokenValue());
        assertEquals("String value", "", tok.getTokenString());
        assertEquals("String delimter", '"', tok.getTokenDelimiter());
        assertEquals("END expected", Lexer.END, tok.nextToken());
    }

    public void testStringLiteralError1()
    {
        LexerInputString inp = new LexerInputString("\"");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("ERROR expected", Lexer.ERROR, tok.nextToken());
        assertEquals("Error message", "Missing quote at end of String: \"", tok.getErrorMessage());
    }

    public void testStringLiteralError2()
    {
        LexerInputString inp = new LexerInputString("\"fred");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("ERROR expected", Latin1Lexer.ERROR, tok.nextToken());
        assertEquals("Error message", "Missing quote at end of String: \"", tok.getErrorMessage());
    }

    public void testCharLiteral()
    {
        LexerInputString inp = new LexerInputString("'a'");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Latin1Lexer.CHARACTER", Latin1Lexer.CHARACTER, tok.nextToken());
        assertEquals("Character literal", "'a'", tok.getTokenValue());
        assertEquals("Character value", "a", tok.getTokenString());
        assertEquals("Character delimter", '\'', tok.getTokenDelimiter());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testCharLiteralError1()
    {
        LexerInputString inp = new LexerInputString("'");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("ERROR expected", Latin1Lexer.ERROR, tok.nextToken());
        assertEquals("Error message", "Missing quote at end of character: '", tok.getErrorMessage());
    }

    public void testCharLiteralError2()
    {
        LexerInputString inp = new LexerInputString("'a");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("ERROR expected", Latin1Lexer.ERROR, tok.nextToken());
        assertEquals("Error message", "Missing quote at end of character: '", tok.getErrorMessage());
    }

    public void testCharLiteralError3()
    {
        LexerInputString inp = new LexerInputString("''");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("ERROR expected", Latin1Lexer.ERROR, tok.nextToken());
        assertEquals("Error message", "Missing character within quotes.", tok.getErrorMessage());
    }

    public void testPeekLineComment1()
    {
        LexerInputString inp = new LexerInputString("brown//\nfox");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("tok.getTokenValue()", "brown", tok.getTokenValue());
        assertEquals("peek", "//", inp.peek(2));
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("tok.getTokenValue()", "fox", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testPeekLineComment2()
    {
        LexerInputString inp = new LexerInputString("brown+//\nfox");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("tok.getTokenValue()", "brown", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("tok.getTokenValue()", "+", tok.getTokenValue());
        assertEquals("peek", "//", inp.peek(2));
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("tok.getTokenValue()", "fox", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testPeekMultiComment1()
    {
        LexerInputString inp = new LexerInputString("brown/*Hello*/fox");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("tok.getTokenValue()", "brown", tok.getTokenValue());
        assertEquals("peek", "/*", inp.peek(2));
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("tok.getTokenValue()", "fox", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testPeekMultiComment2()
    {
        LexerInputString inp = new LexerInputString("brown*/*Hello*/*fox");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("tok.getTokenValue()", "brown", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("tok.getTokenValue()", "*", tok.getTokenValue());
        assertEquals("peek", "/*", inp.peek(2));
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("tok.getTokenValue()", "*", tok.getTokenValue());
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("tok.getTokenValue()", "fox", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testComment1()
    {
        LexerInputString inp = new LexerInputString("c //h*(\n+a");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("'c' expected", "c", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "+", tok.getTokenValue());
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("'a' expected", "a", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());

    }

    public void testComment2()
    {
        LexerInputString inp = new LexerInputString("c //comment\n+ //(another)\na");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("'c' expected", "c", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("'+' expected", "+", tok.getTokenValue());
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("'a' expected", "a", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testComment3() throws Exception
    {
        StringWriter writer = new StringWriter() ;
        PrintWriter  out = new PrintWriter(writer);
        out.println("c /* this is a comment */ ");
        out.println(" +  /* followed");
        out.println("  by another */ a  ");
        StringReader reader = new StringReader(writer.toString());
        Latin1Lexer tok = new Latin1Lexer(new LexerInputStream(reader, null));
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("'c' expected", "c", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("'+' expected", "+", tok.getTokenValue());
        assertEquals("Identifier expected", Latin1Lexer.IDENTIFIER, tok.nextToken());
        assertEquals("'a' expected", "a", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testReservedWordIf()
    {
        LexerInputString inp = new LexerInputString(" if \t");
        Latin1Lexer tok = new Latin1Lexer(inp);
        tok.setReservedWords(reservedWords);
        assertEquals("Reserved Word expected", Latin1Lexer.RESERVED, tok.nextToken());
        assertEquals("Reserved Word 'if' expected", "if", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());

    }

    public void testReservedWordElse()
    {
        LexerInputString inp = new LexerInputString("else");
        Latin1Lexer tok = new Latin1Lexer(inp);
        tok.setReservedWords(reservedWords);
        assertEquals("Reserved Word expected", Latin1Lexer.RESERVED, tok.nextToken());
        assertEquals("Reserved Word 'else' expected", "else", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());

    }

    public void testReservedWordWhile()
    {
        LexerInputString inp = new LexerInputString(" while");
        Latin1Lexer tok = new Latin1Lexer(inp);
        tok.setReservedWords(reservedWords);
        assertEquals("Reserved Word expected", Latin1Lexer.RESERVED, tok.nextToken());
        assertEquals("Reserved Word 'while' expected", "while", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testDelimiters()
    {
        LexerInputString inp = new LexerInputString(",;");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Delimiter expected", Latin1Lexer.DELIMITER, tok.nextToken());
        assertEquals("Delimiter expected", ",", tok.getTokenValue());
        assertEquals("Delimiter expected", Latin1Lexer.DELIMITER, tok.nextToken());
        assertEquals("Delimiter expected", ";", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testOperators()
    {
        LexerInputString inp = new LexerInputString("* - + = / % | &");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "*", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "-", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "+", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "=", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "/", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "%", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "|", tok.getTokenValue());
        assertEquals("Operator expected", Latin1Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "&", tok.getTokenValue());
        assertEquals("END expected", Latin1Lexer.END, tok.nextToken());
    }

    public void testBigOperators()
    {
        LexerInputString inp = new LexerInputString("== === != -= += *= /= %= |= &= && || << >> >>>");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "==", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "===", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "!=", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "-=", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "+=", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "*=", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "/=", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "%=", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "|=", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "&=", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "&&", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "||", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", "<<", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", ">>", tok.getTokenValue());
        assertEquals("Operator expected", Lexer.OPERATOR, tok.nextToken());
        assertEquals("Operator expected", ">>>", tok.getTokenValue());
        assertEquals("END expected", Lexer.END, tok.nextToken());
    }

    public void testOne()
    {
        LexerInputString inp = new LexerInputString("1");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Integer expected", Lexer.INTEGER, tok.nextToken());
    }

    public void testInteger()
    {
        LexerInputString inp = new LexerInputString(" 1234567890");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Integer expected", Lexer.INTEGER, tok.nextToken());
        assertEquals("Whitespace ' ' expected", " ", tok.getWhitespace());
    }

    public void testDecimal()
    {
        LexerInputString inp = new LexerInputString("12345.67890");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Decimal expected", Lexer.DECIMAL, tok.nextToken());
    }

    public void testDelimiter()
    {
        LexerInputString inp = new LexerInputString("1;");
        Latin1Lexer tok = new Latin1Lexer(inp);
        assertEquals("Integer expected", Lexer.INTEGER, tok.nextToken());
        assertEquals("1 expected", "1", tok.getTokenValue());
        assertEquals("Delimiter expected", Lexer.DELIMITER, tok.nextToken());
        assertEquals("';' expected", ";", tok.getTokenValue());
    }
}
