package au.com.illyrian.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import au.com.illyrian.compiler.ast.AstParser;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.impl.CompileModule;
import au.com.illyrian.parser.impl.LexerInputStream;
import junit.framework.TestCase;

public class ParseJesubSyntaxTest extends TestCase
{
    public static final String JESUB_SYNTAX_BNF = "test/au/com/illyrian/compiler/Jesub_syntax.bnf";
    
    private BufferedReader createReader(String filename) throws Exception {
        File file = new File(JESUB_SYNTAX_BNF);
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        return reader;
    }
    
    public void testFileExists() throws Exception {
        File file = new File(JESUB_SYNTAX_BNF);
        assertTrue("Could not find " + JESUB_SYNTAX_BNF, file.exists());
    }
    
    public void testFileLineCount() throws Exception {
        int count = 0;
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        while (reader.readLine()!= null) {
            count++;
        }
        assertEquals("Wrong line count", 221, count);
    }
/*
    public void testJesubSyntax() throws Exception {
        BufferedReader reader = createReader(JESUB_SYNTAX_BNF);
        Input input = new LexerInputStream(reader, JESUB_SYNTAX_BNF);
        CompileModule compile = new CompileModule();
        compile.setInput(input);
        
        RecursiveParser parser = new RecursiveParser();
        compile.visit(parser);
        AstParser tree = parser.parseClass();
        assertEquals("token", Lexer.END, parser.getLexer().nextToken());
    }
    */
}
