package au.com.illyrian.parser.impl;

import java.util.Properties;

import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.impl.LexerInputString;


import junit.framework.TestCase;

public class InputStringTest extends TestCase
{

    public void testEmpty()
    {
        LexerInputString input = new LexerInputString("");
        assertEquals("NULL expected",  Input.NULL, input.startChar());
        assertEquals("NULL expected",  Input.NULL, input.nextChar());
        assertEquals("Start",  0, input.getTokenStart());
        assertEquals("Finish",  0, input.getTokenFinish());
    }
    
    public void testSpace()
    {
        LexerInputString tok = new LexerInputString(" ");
        assertEquals("Space expected",  ' ', tok.startChar());
        assertEquals("NULL expected",  Input.NULL, (int)tok.nextChar());
        assertEquals("Start",  0, tok.getTokenStart());
        assertEquals("Finish",  1, tok.getTokenFinish());
        assertEquals("space expected",  " ", tok.getTokenString());
    }

    public void testSpaces()
    {
    	String input = " \t\n";
    	LexerInputString tok = new LexerInputString(input);
        assertEquals("Space expected",  ' ', tok.startChar());
        assertEquals("Space expected",  input.charAt(1), (int)tok.nextChar());
        assertEquals("Space expected",  input.charAt(2), (int)tok.nextChar());
        assertEquals("NULL expected",  Input.NULL, (int)tok.nextChar());
    }

    public void testSkipWhiteSpace()
    {
        LexerInputString inp = new LexerInputString(" \t\n");
        Latin1Lexer tok = new Latin1Lexer(inp);
        tok.spanWhiteSpace();
        assertEquals("NULL expected",  Input.NULL, inp.nextChar());
        assertEquals("Start",  0, inp.getTokenStart());
        assertEquals("Finish",  3, inp.getTokenFinish());
        assertEquals("Whitespace expected",  " \t\n", inp.getTokenString());
    }

    public void testCharacter()
    {
        LexerInputString tok = new LexerInputString("a");
        assertEquals("a expected",  'a', tok.startChar());
        assertEquals("NULL expected",  Input.NULL, tok.nextChar());
        assertEquals("Start",  0, tok.getTokenStart());
        assertEquals("Finish",  1, tok.getTokenFinish());
        assertEquals("Identifier 'a' expected",  "a", tok.getTokenString());
    }

    public void testCharacters()
    {
        LexerInputString tok = new LexerInputString("abc");
        assertEquals("a expected",  'a', tok.startChar());
        assertEquals("b expected",  'b', tok.nextChar());
        assertEquals("c expected",  'c', tok.nextChar());
        assertEquals("NULL expected",  Input.NULL, tok.nextChar());
        assertEquals("Start",  0, tok.getTokenStart());
        assertEquals("Finish",  3, tok.getTokenFinish());
        assertEquals("Identifier expected",  "abc", tok.getTokenString());
    }
}
