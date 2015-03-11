package au.com.illyrian.domainparser;


import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionLink;
import au.com.illyrian.classmaker.ast.DotOperator;
import au.com.illyrian.classmaker.ast.TerminalName;
import au.com.illyrian.jesub.ast.AstDeclareClass;
import au.com.illyrian.jesub.ast.AstDeclareModule;
import au.com.illyrian.jesub.ast.AstStructure;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.impl.InvokeParser;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserBase;
import au.com.illyrian.parser.maker.ModuleActionMaker;

/**
*
* Loads a domain specific language parser and delegates to it.
* <p>
* The Domain Parser is a generic parser that can parse any domain specific language.
* It does this by loading a specific parser for the langauge and then delegating
* the parsing of the domain specific language to that parser.
*  
* The Domain Parser only does three things:
* <OL>
* <LI>it imports one or more classes</LI>
* <LI>it instantiates one of the imported classes that implements the GenericParser interface</LI>
* <LI>it delegates the parsing of the rest of the code to the instantiated parser.
* </OL>
* 
* <code>
* import au.com.illyrian.exprparser.Expr
* 
* Expr::{
* 
*   func1(a, b) = a + b;
*   
*   func2(a, b, c) = a * b + c;
* }::Expr
* </code>
* 
* @author Donald Strong
*/
public class AstModuleParser extends ParserBase implements ParseModule<AstStructure>
{
   /** The actions to be applied to the recognised input tokens. */
   private ModuleAction                 moduleAction = null;

   /**
    * Public constructor for the search query parser. When no actions are provided the parser only performs validation.
    */
   public AstModuleParser()
   {
       setLexer(createLexer());
       getLexer().getReservedWords().setProperty("package", "package");
       getLexer().getReservedWords().setProperty("import", "import");
   }

    public ModuleAction getModuleAction()
    {
        if (moduleAction == null)
            moduleAction = createModuleAction();
        return moduleAction;
    }

    public void setModuleAction(ModuleAction actions)
    {
        this.moduleAction = actions;
    }
    
    protected ModuleAction createModuleAction()
    {
        ModuleAction action = new ModuleActionMaker();
        getCompileUnit().visitParser(action);
        return action;
    }

    protected Latin1Lexer createLexer()
    {
        return new Latin1Lexer();
    }
    
/*
     dec_module:AstDeclareModule   ::= dec_package dec_imports dec_class
     dec_package:AstExpression     ::= 'package' class_name ';'
                                   | EMPTY ;
     dec_imports:AstExpression     ::= 'import' class_name ';' dec_imports
                                   | EMPTY ;
     class_name:AstExpression      ::= IDENTIFIER more_classname
                                   | '.' IDENTIFIER
                                   | '.' error("More of the class name expected) 
                                   | error("Class name expected") ;
     dec_class:AstDeclareClass     ::= class_name '::' code '::' class_name ';'
     code:AstDeclareClass          ::= <code for the invoked parser> 
 */
    
   /**
    * Parse the given Domain Specific Language.
    * <p>
    * This is a recursive descent parser. <br>
    * The parser rules are expressed in Extended Bacus Nour Form (EBNF). <br>
    * <code>
    *     dec_module   ::= dec_package dec_imports dec_class
    *     dec_package  ::= [ 'package' classname ';' ]
    *     dec_imports  ::= { 'import' classname ';' }
    *     classname    ::= IDENTIFIER { '.' IDENTIFIER } 
    *     dec_class    ::= IDENTIFIER '::' code '::' IDENTIFIER ';'
    *     code         ::= <code for the invoked parser> 
    * </code>
    *
    * @param input - shared input for the parser
    * @return the result of parsing the input and applying actions from ExpressionAction.
    * @throws ParserException - if an error occurs.
    */
   public AstStructure parseModule() throws ParserException
   {
       // Read the first token from input.
       nextToken();

       // Parse top level expression.
       AstStructure module = dec_module(); 
       // Ensure all tokens have been processed.
       endModule();

       return module;
   }
   
   protected void endModule() throws ParserException
   {
       int token = getToken();
       // Ensure all tokens have been processed.
       if (token == Lexer.ERROR)
       {
           throw error(getInput(), getLexer().getErrorMessage());
       }
       else if (token == Lexer.CLOSE_P)
       {
           throw error(getInput(), "Unbalanced perentheses - too many \')\'.");
       }
       else if (token != Lexer.END)
       {
           throw error(getInput(), "End of input expected");
       }
   }
   
   /** dec_module ::= dec_package more_imports dec_class ;*/
   public AstStructure dec_module() throws ParserException
   {
       AstExpression packageExpr = dec_package();
       AstExpression importsExpr = more_imports();
       AstDeclareClass classExpr = dec_class();
       return new AstDeclareModule(packageExpr, importsExpr, classExpr);
   }
   
   /** packageStatement   ::= 'package' class_name ';'
    *                     |   EMPTY ;
    */
   public AstExpression dec_package() throws ParserException
   {
       if (accept(Lexer.RESERVED, "package"))
       {
           AstExpression packageName = class_name();
           expect(Lexer.DELIMITER, ";", "';' expected at the end of the package name");
           
           return packageName;
       }
       return null;
   }

   /** more_imports ::= 'import' class_name ';' more_imports
    *               |   'import' class_name error("';' expected at end of fully qualified class name")
    *               |   EMPTY ;
    */
   public AstExpression more_imports() throws ParserException
   {
       AstExpression result = null;
       if (accept(Lexer.RESERVED, "import"))
       {
           AstExpression className = class_name();
           expect(Lexer.DELIMITER, ";", "';' expected at end of fully qualified class name");
           AstExpression more = more_imports();
           
           result = (more == null) ? className : new AstExpressionLink(className, more);
       }
       return result;
   }

   /** simple_name ::= IDENTIFIER 
    *              | error("Class name expected") ;
    */
   public AstExpression simple_name() throws ParserException
   {
       AstExpression result = null;
       if (getToken() == Lexer.IDENTIFIER)
       {
           String simpleName = getLexer().getTokenValue();
           nextToken();
           result = new TerminalName(simpleName);
       }
       else
           throw error(getInput(), "Class name expected.");
       return result;
   }

   /** class_name :== simple_name '.' class_name
    *             |   simple_name ;
    */
   public AstExpression class_name() throws ParserException
   {
       AstExpression result = simple_name();
       if (accept(Lexer.OPERATOR, "."))
       {
           AstExpression moreClassName = class_name();
           
           result = new DotOperator(result, moreClassName);
       }
       return result;
   }

   /**
    *     parser    ::= class_name '::' code($1) '::' class_name ';' verifyParserName($1, $5)
    *               |   class_name '::' code($1) '::' error("'$1' expected at end of parser space")
    *               |   error("Parser class name expected") ;
    */
   public AstDeclareClass dec_class() throws ParserException
   {
       AstDeclareClass decClass = null;
       if (getToken() == Lexer.IDENTIFIER)
       {
    	   AstExpression parseName = class_name();
           if (match(Lexer.OPERATOR, "::"))
           {
        	   decClass = code(parseName.toString());
               nextToken();
               if (accept(Lexer.OPERATOR, "::")) 
               {
            	   AstExpression className = class_name();
                   verifyParserName(parseName, className);
                   if (!className.equals(parseName))
                       throw error(getInput(), "'" + parseName + "' expected at end of parser space");
               }
               else
                   throw error(getInput(), ":: expected");
           }
           else
               throw error(getInput(), ":: expected");
       }
       else
           throw error(getInput(), "Parser class name expected");
       return decClass;
   }
   
   public AstDeclareClass code(String parseName) throws ParserException
   {
	   AstDeclareClass decClass = null;
       String qualifiedName = getModuleAction().getParserName(parseName);
       InvokeParser parser = getCompileUnit().getInvokeParser();
       Input input = getInput();
       Object result = (AstDeclareClass)parser.invokeParseClass(qualifiedName, input);
       if (result instanceof AstDeclareClass)
    	   decClass = (AstDeclareClass)result;
       return decClass;
   }
   
   public void verifyParserName(AstExpression name1, AstExpression name2) throws ParserException
   {
	   String firstName = name1.toString();
	   String secondName = name2.toString();
	   if (!firstName.equals(secondName))
	       throw error(getInput(), "'" + firstName + "' expected at end of parser space");
   }
}
