package au.com.illyrian.domainparser;


import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.Input;
import au.com.illyrian.parser.ParseModule;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.InvokeParser;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserBase;
import au.com.illyrian.parser.maker.ModuleActionMaker;

/**
*
* Loads a domain specific language parser and delegates to it.
* <p>
* The Domain Parser is a generic parser that can parse any domain specific language.
* It does this by loading a specific parser for the language and then delegating
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
public class ModuleParser extends ParserBase implements ParseModule
{
   /** The actions to be applied to the recognised input tokens. */
   private ModuleAction                 moduleAction = null;

   /**
    * Public constructor for the search query parser. When no actions are provided the parser only performs validation.
    */
   public ModuleParser()
   {
       setLexer(createLexer());
       getLexer().getReservedWords().setProperty("package", "package");
       getLexer().getReservedWords().setProperty("import", "import");
   }

    public ModuleAction getModuleAction()
    {
        if (moduleAction == null)
            createAction();
        return moduleAction;
    }

    public void setAction(ModuleAction actions)
    {
        this.moduleAction = actions;
    }
    
    protected void createAction()
    {
        ModuleAction action = new ModuleActionMaker();
        getCompilerContext().visitParser(action);
        setAction(action);
    }

    protected Latin1Lexer createLexer()
    {
        return new Latin1Lexer();
    }
    
/*
     dec_module:AstModule   ::= dec_package dec_imports dec_class
     dec_package:AstExpression     ::= 'package' class_name ';'
                                   | EMPTY ;
     dec_imports:AstExpression     ::= 'import' class_name ';' dec_imports
                                   | EMPTY ;
     class_name:AstExpression      ::= IDENTIFIER more_classname
                                   | '.' IDENTIFIER
                                   | '.' error("More of the class name expected) 
                                   | error("Class name expected") ;
     dec_class:AstClass     ::= class_name '::' code '::' class_name ';'
     code:AstClass          ::= <code for the invoked parser> 
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
    */
   public Object parseModule(CompilerContext context)
   {
       setCompilerContext(context);
       // Read the first token from input.
       nextToken();

       // Parse top level expression.
       Object module = dec_module(); 
       // Ensure all tokens have been processed.
       endModule();

       return module;
   }
   
   protected void endModule()
   {
       TokenType token = getTokenType();
       // Ensure all tokens have been processed.
       if (token == TokenType.ERROR)
       {
           throw exception(getLexer().getErrorMessage());
       }
       else if (token == TokenType.DELIMITER)
       {
           throw exception("Unbalanced perentheses - too many \')\'.");
       }
       else if (token != TokenType.END)
       {
           throw exception("End of input expected");
       }
   }
   
   /** dec_module ::= dec_package more_imports dec_class */
   public Object dec_module()
   {
       Object decModule = packageStatement();
       decModule = more_imports();
       decModule = dec_class();
       return getModuleAction().getModule();
   }
   
   /** packageStatement   ::= [ 'package' classname ';' ] */
   public Object packageStatement()
   {
       if (accept(TokenType.RESERVED, "package"))
       {
           String packageName = classname();
           expect(TokenType.DELIMITER, ";", "';' expected at the end of the package name");
           
           return getModuleAction().Package(packageName);
       }
       return null;
   }

   /** more_imports ::= { 'import' classname ';' } */
   public Object more_imports()
   {
	   Object result = null;
       if (accept(TokenType.RESERVED, "import"))
       {
           String className = classname();
           expect(TokenType.DELIMITER, ";", "';' expected at end of fully qualified class name");
           more_imports();

           this.getCompilerContext().addFullyQualifiedClassName(className);
           result = getModuleAction().Import(className);
       }
       return result;
   }

   /** classname ::= IDENTIFIER { '.' IDENTIFIER } */
   public String classname()
   {
       String qualifiedClassName = null;
       if (getTokenType() == TokenType.IDENTIFIER)
       {
           String simpleName = getLexer().getTokenValue();
           nextToken();
           qualifiedClassName = getModuleAction().Dot(qualifiedClassName, simpleName);

           while (accept(TokenType.OPERATOR, "."))
           {
               if (getTokenType() == TokenType.IDENTIFIER)
               {
                   simpleName = getLexer().getTokenValue();
                   nextToken();
                   qualifiedClassName = getModuleAction().Dot(qualifiedClassName, simpleName);
               }
               else
                   throw exception("More package name expected.");
           }
       }
       else
           throw exception("Class name expected.");
       return qualifiedClassName;
   }

   /** more_classname :== { '.' IDENTIFIER } */
//   public String more_classname()
//   {
//       String qualifiedClassName = null;
//       if (accept(TokenType.OPERATOR, "."))
//       {
//           if (getToken() == TokenType.IDENTIFIER)
//           {
//               String simpleName = getLexer().getTokenValue();
//               lastIdentifier = simpleName;
//               nextToken();
//               qualifiedClassName = more_classname();
//
//               qualifiedClassName = getModuleAction().addClassName(simpleName, qualifiedClassName);
//           }
//           else
//               throw error("More package name expected.");
//       }
//       return qualifiedClassName;
//   }

   /**
    *     classname ::= IDENTIFIER { '.' IDENTIFIER } ';'
    *     parser    ::= IDENTIFIER '::' code '::' IDENTIFIER ';'
    *
    * @return the result of ExpressionAction.actionIdentifier(IDENTIFIER) or
    *         ExpressionAction.actionPerentheses(and_expr).
    * @throws Exception -
    *             if an error occurs.
    */
   public Object dec_class()
   {
       Object result = null;
       if (getTokenType() == TokenType.IDENTIFIER)
       {
           Object parseName = classname();
           if (match(TokenType.OPERATOR, "::"))
           {
               String qualifiedName = getModuleAction().getParserName(parseName.toString());
               InvokeParser parser = getCompilerContext().getInvokeParser();
               Input input = getInput();
               result = parser.invokeParseClass(qualifiedName);
               getModuleAction().handleModule(result);

               nextToken();
               if (accept(TokenType.OPERATOR, "::")) 
               {
                   Object className = classname();
                   if (!className.equals(parseName))
                       throw exception("'" + parseName + "' expected at end of parser space");
               }
               else
                   throw exception(":: expected");
           }
           else
               throw exception(":: expected");
       }
       else
           throw exception("Parser name expected");
       return result;
   }
}
