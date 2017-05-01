package au.com.illyrian.domainparser;


import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.maker.ClassActionMaker;

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
public class ClassParser extends ModuleParser
{
    private ClassAction classAction = null;
    
   /**
    * Public constructor for the search query parser. When no actions are provided the parser only performs validation.
    */
   public ClassParser()
   {
       getLexer().getReservedWords().setProperty("class", "class");
       getLexer().getReservedWords().setProperty("extends", "extends");
       getLexer().getReservedWords().setProperty("implements", "implements");
       getLexer().getReservedWords().setProperty("public", "public");
       getLexer().getReservedWords().setProperty("protected", "protected");
       getLexer().getReservedWords().setProperty("abstract", "abstract");
       getLexer().getReservedWords().setProperty("final", "final");
       getLexer().getReservedWords().setProperty("strictfp", "strictfp");
   }

    public ClassAction getClassAction()
    {
        if (classAction == null)
            createAction();
        return classAction;
    }

//    public void setModuleAction(ModuleAction actions)
//    {
//        super.setModuleAction(actions);
//        if (actions instanceof ClassAction) {
//            setClassAction((ClassAction)actions);
//        }
//    }
//
    public void setAction(ClassAction actions)
    {
        classAction = actions;
        super.setAction(actions);
    }

    protected void createAction()
    {
        ClassAction action = new ClassActionMaker();
        getCompilerContext().visitParser(action);
        setAction(action);
    }

    /**
    *     dec_class       ::= class_modifiers 'class' IDENTIFIER opt_extends more_implements class_parser
    *     class_modifiers ::= { 'public' | 'abstract' | 'static' | 'final' | 'strictfp' | EMPTY }
    *     opt_extends     ::= 'extends' classname 
    *     more_implements ::= 'implements' implement_list 
    *     class_parser    ::= IDENTIFIER '::' code '::' IDENTIFIER ';'
    *
    * @return the result of ExpressionAction.actionIdentifier(IDENTIFIER) or
    *         ExpressionAction.actionPerentheses(and_expr).
    * @throws Exception -
    *             if an error occurs.
    */

    /** dec_class   ::= class_modifiers "class" classname ["extends" classname] ["implements" implements_list] dec_body */
    public Object dec_class()
    {
        class_modifiers();
        expect(TokenType.RESERVED, "class", "import or class expected.");
        String className = classname();
        getClassAction().setClassName(className);
        
        if (accept(TokenType.RESERVED, "extends"))
        {
            String extendsClassName = classname();
            getClassAction().declareExtends(extendsClassName);
        }

        if (accept(TokenType.RESERVED, "implements"))
        {
            implements_list();
        }
        return dec_body();
    }
    
    public Object class_modifiers()
    {
        Object result = null;
        if (!match(TokenType.RESERVED, "class"))
        {
            while (true)
            {
                if (accept(TokenType.RESERVED, "public"))
                    result = getClassAction().addModifier("public");
                else if (accept(TokenType.RESERVED, "protected"))
                    result = getClassAction().addModifier("protected");
                else if (accept(TokenType.RESERVED, "private"))
                    result = getClassAction().addModifier("private");
                else if (accept(TokenType.RESERVED, "abstract"))
                    result = getClassAction().addModifier("abstract");
                else if (accept(TokenType.RESERVED, "final"))
                    result = getClassAction().addModifier("final");
                else if (accept(TokenType.RESERVED, "strictfp"))
                    result = getClassAction().addModifier("strictfp");
                else
                    break;
            }
            getClassAction().setClassModifiers(result);
        }
        return result;
    }
    
    /** 
     * implements_list ::= classname { ',' classname }
     */
    public Object implements_list()
    {
        Object result = null;
        String implementsClassName = classname();
        result = getClassAction().declareImplements(implementsClassName);
        
        while (accept(TokenType.DELIMITER, ","))
        {
            implementsClassName = classname();
            result = getClassAction().declareImplements(implementsClassName);
        }
        return result;
    }
   
    public Object dec_body()
    {
    	Object result = null;
       if (getTokenType() == TokenType.IDENTIFIER)
       {
           Object parseName = classname();
           if (match(TokenType.OPERATOR, "::"))
           {
               String qualifiedName = getClassAction().getParserName(parseName.toString());
               result = getCompilerContext().getInvokeParser().invokeParseMember(qualifiedName);
               getClassAction().declareMembers(result);

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
