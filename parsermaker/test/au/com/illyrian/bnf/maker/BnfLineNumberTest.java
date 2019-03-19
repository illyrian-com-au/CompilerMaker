package au.com.illyrian.bnf.maker;

import java.io.File;
import java.util.Vector;

import au.com.illyrian.bnf.ast.BnfTree;
import au.com.illyrian.bnf.ast.BnfTreeBinary;
import au.com.illyrian.bnf.ast.BnfTreeParser;
import au.com.illyrian.bnf.ast.BnfTreeRule;
import au.com.illyrian.bnf.parser.BnfParser;
import au.com.illyrian.parser.impl.ModuleContext;

public class BnfLineNumberTest extends BnfMakerTextBase
{
    private static final String TEST_DIR = "test/";

    ModuleContext compile = new ModuleContext();
    BnfParser parserTree = new BnfParser();
    
    @Override
    public void setUp() throws Exception {
        String source = "test/PackageParser.bnf";
        File file = new File(TEST_DIR, source);
        assertTrue("Cannot find " + file.getAbsolutePath(), file.exists());
        compile.setInputFile(file, source);
    }
    
    public void testParseNameLineNumbers() throws Exception 
    {
        BnfTreeParser root = parserTree.parseMembers(compile);        
        BnfTreeRule rule3 = root.getRuleSet().get("name");
        assertNotNull("Rule name", rule3);
        assertEquals("Line number", 2, rule3.getLineNumber());
        BnfTree [] rules = toArray(rule3.getBody());
        assertEquals("Number of BnfTree items", 3, rules.length);
        assertEquals("2: IDENTIFIER {}", toString(rules[0]));
        assertEquals("2: IDENTIFIER", toString(rules[1]));
        assertEquals("2: {}", toString(rules[2]));
    }
    
    public void testParseQualifiedNameLineNumbers() throws Exception 
    {
        BnfTreeParser root = parserTree.parseMembers(compile);        
        BnfTreeRule rule2 = root.getRuleSet().get("qualified_name");
        assertNotNull("Rule qualified_name", rule2);
        assertEquals("Line number", 4, rule2.getLineNumber());
        BnfTree [] rules = toArray(rule2.getBody());
        assertEquals("Number of BnfTree items", 9, rules.length);
        assertEquals("4: name() ( DOT qualifi", toString(rules[0]));
        assertEquals("4: name()", toString(rules[1]));
        assertEquals("4: ( DOT qualified_name", toString(rules[2]));
        assertEquals("4: DOT qualified_name()", toString(rules[3]));
        assertEquals("4: DOT", toString(rules[4]));
        assertEquals("4: qualified_name() {}", toString(rules[5]));
        assertEquals("4: qualified_name()", toString(rules[6]));
        assertEquals("4: {}", toString(rules[7]));
        assertEquals("5: .", toString(rules[8]));

    }
    
    public void testParsePackageLineNumbers() throws Exception 
    {
        BnfTreeParser root = parserTree.parseMembers(compile);        
        BnfTreeRule rule1 = root.getRuleSet().get("package");
        assertEquals("Line number", 7, rule1.getLineNumber());
        BnfTree [] rules = toArray(rule1.getBody());
        assertEquals("Number of BnfTree items", 13, rules.length);
        assertEquals("7: ( PACKAGE ( qualifie", toString(rules[0]));
        assertEquals("7: PACKAGE ( qualified_", toString(rules[1]));
        assertEquals("7: PACKAGE", toString(rules[2]));
        assertEquals("7: ( qualified_name() S", toString(rules[3]));
        assertEquals("7: qualified_name() SEM", toString(rules[4]));
        assertEquals("7: qualified_name()", toString(rules[5]));
        assertEquals("7: SEMI {}", toString(rules[6]));
        assertEquals("7: SEMI", toString(rules[7]));
        assertEquals("7: {}", toString(rules[8]));
        assertEquals("8: error(\"Package name ", toString(rules[9])); // 9
        assertEquals("8: error(\"Package name ", toString(rules[10]));
        assertEquals("9: .", toString(rules[11]));
        assertEquals("9: .", toString(rules[12]));
        
        
    }
    
    public void testParseClassBodyLineNumbers() throws Exception 
    {
        BnfTreeParser root = parserTree.parseMembers(compile);        
        BnfTreeRule rule0 = root.getRuleSet().get("goal");
        assertNotNull("Rule goal", rule0);
        assertEquals("Line number", 11, rule0.getLineNumber());
        BnfTree [] rules = toArray(rule0.getBody());
        assertEquals("Number of BnfTree items", 3, rules.length);
        assertEquals("11: package() {}", toString(rules[0]));
        assertEquals("11: package()", toString(rules[1]));
        assertEquals("11: {}", toString(rules[2]));
    }
    
    public void testParseTreeLineNumbers() throws Exception 
    {
        BnfTreeParser root = parserTree.parseMembers(compile);        
        assertNotNull("Nothing returned from parser", root);
        assertEquals("BnfTreeParser", 12, root.getLineNumber());
    }
    
    String toString(BnfTree tree) {
        String str = tree.getLineNumber()
                + ": " + truncate(tree.toRuleString(), 20);
        return str;
    }
    
    String truncate(String str, int length) {
        if (str.length() > length) {
            return str.substring(0, length);
        }
        return str;
    }
    
    void addLineNumbers(StringBuffer buf, BnfTree tree) {
        buf.append(tree.getLineNumber()).append(": ");
        buf.append(tree.getClass().getSimpleName());
        buf.append(" ").append(tree.toRuleString());
        buf.append("\n");
        if (tree instanceof BnfTreeBinary) {
            BnfTreeBinary binary = (BnfTreeBinary)tree;
            addLineNumbers(buf, binary.getLeft());
            addLineNumbers(buf, binary.getRight());
        }
    }
    
    BnfTree [] toArray(BnfTree tree) {
        Vector<BnfTree> list = new Vector<BnfTree>();
        addBnfTree(list, tree);
        return list.toArray(new BnfTree[0]);
    }

    void addBnfTree(Vector<BnfTree> list, BnfTree tree) {
        list.add(tree);
        if (tree instanceof BnfTreeBinary) {
            BnfTreeBinary binary = (BnfTreeBinary)tree;
            addBnfTree(list, binary.getLeft());
            addBnfTree(list, binary.getRight());
        }
    }
}
