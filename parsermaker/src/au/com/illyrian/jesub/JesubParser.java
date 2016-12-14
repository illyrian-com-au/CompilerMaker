package au.com.illyrian.jesub;


import au.com.illyrian.classmaker.ClassMaker;
import au.com.illyrian.classmaker.ClassMakerLocation;
import au.com.illyrian.parser.CompilerContext;
import au.com.illyrian.parser.ParseClass;
import au.com.illyrian.parser.ParseExpression;
import au.com.illyrian.parser.ParseMembers;
import au.com.illyrian.parser.ParserException;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

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
public class JesubParser extends OperatorPrecidenceParser
    implements ParseClass, ParseMembers, ParseExpression, ClassMakerLocation
{
    JesubAction jesubAction = null;
    ClassMaker classMaker = null;
    
   /**
    * Public constructor for the search query parser. When no actions are provided the parser only performs validation.
    */
   public JesubParser()
   {
       getLexer().getReservedWords().setProperty("class", "class");
       getLexer().getReservedWords().setProperty("extends", "extends");
       getLexer().getReservedWords().setProperty("implements", "implements");
       getLexer().getReservedWords().setProperty("public", "public");
       getLexer().getReservedWords().setProperty("protected", "protected");
       getLexer().getReservedWords().setProperty("abstract", "abstract");
       getLexer().getReservedWords().setProperty("final", "final");
       getLexer().getReservedWords().setProperty("strictfp", "strictfp");
       getLexer().getReservedWords().setProperty("transient", "transient");
       getLexer().getReservedWords().setProperty("volatile", "volatile");
       getLexer().getReservedWords().setProperty("boolean", "boolean");
       getLexer().getReservedWords().setProperty("byte", "byte");
       getLexer().getReservedWords().setProperty("short", "short");
       getLexer().getReservedWords().setProperty("char", "char");
       getLexer().getReservedWords().setProperty("int", "int");
       getLexer().getReservedWords().setProperty("long", "long");
       getLexer().getReservedWords().setProperty("float", "float");
       getLexer().getReservedWords().setProperty("double", "double");
       getLexer().getReservedWords().setProperty("void", "void");
   }

    public JesubAction getJesubAction()
    {
        if (jesubAction == null)
            jesubAction = createJesubAction();
        return jesubAction;
    }

    public void setJesubAction(JesubAction actions)
    {
        jesubAction = actions;
    }

    protected JesubAction createJesubAction()
    {
        // FIXME ClassMaker if possible otherwise string.
        JesubAction action = new JesubActionString();
        getCompilerContext().visitParser(action);
        return action;
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

    public void setClassMaker(ClassMaker classMaker)
    {
        this.classMaker = classMaker;
    }

    public ClassMaker getClassMaker()
    {
        return classMaker;
    }

    public Object parseExpression(CompilerContext context) throws ParserException
    {
        setCompilerContext(context);
        beginFragment();
        Object result = expression();
        endFragment();
        return result;
    }

    public Object parseMembers(CompilerContext context) throws ParserException
    {
        setCompilerContext(context);
        beginFragment();
        Object result = declare_body();
        endFragment();
        return result;
    }

    public Object parseClass(CompilerContext context) throws ParserException
    {
        setCompilerContext(context);
        beginFragment();
        Object result = dec_class();
        endFragment();
        return getJesubAction().getModule();
    }

    protected void beginFragment() throws ParserException
    {
        getJesubAction();
        nextToken();
        expect(TokenType.DELIMITER, "{", "'{' expected at start of code fragment.");
    }

    protected void endFragment() throws ParserException
    {
        if (match(TokenType.DELIMITER, "}"))
            return;
        
        TokenType token = getTokenType();
        // Ensure all tokens have been processed.
        if (token == TokenType.ERROR)
        {
            throw error(getLexer().getErrorMessage());
        }
        else if (token == TokenType.DELIMITER)
        {
            throw error("Unbalanced perentheses - too many \')\'.");
        }
        else if (token == TokenType.IDENTIFIER)
        {
            throw error("Operator expected.");
        }
        else
        {
            throw error("Operator or '}' expected.");
        }
    }
    
    /** dec_class   ::= [ access_modifiers ] "class" classname ["extends" classname] ["implements" implements_list] dec_body
     *                = {
     *                   case 1 : { action.setModifiers($1); }
     *                   case 3 : { action.setClassName($3); }
     *                   case 5 : { action.setExtends($5); }
     *                  }
     */
    public Object dec_class() throws ParserException
    {
        Object $1 = access_modifiers();
        if ($1 != null)
            jesubAction.setClassModifiers($1);
        expect(TokenType.RESERVED, "class", "Access modifier or class expected.");
        String className = classname();
        jesubAction.setClassName(className);
        
        if (accept(TokenType.RESERVED, "extends"))
        {
            String extendsClassName = classname();
            jesubAction.declareExtends(extendsClassName);
        }

        if (accept(TokenType.RESERVED, "implements"))
        {
            implements_list();
        }
        return declare_body();
    }

    /** access_modifiers :== { "public" | "protected" | "private" | "abstract" | "final" 
     *                                  | "static" | "strictfp" | "transient" | "volatile" */
    public Object access_modifiers() throws ParserException
    {
        Object result = null;
        while (true)
        {
            if (accept(TokenType.RESERVED, "public"))
                result = jesubAction.addModifier("public");
            else if (accept(TokenType.RESERVED, "protected"))
                result = jesubAction.addModifier("protected");
            else if (accept(TokenType.RESERVED, "private"))
                result = jesubAction.addModifier("private");
            else if (accept(TokenType.RESERVED, "abstract"))
                result = jesubAction.addModifier("abstract");
            else if (accept(TokenType.RESERVED, "final"))
                result = jesubAction.addModifier("final");
            else if (accept(TokenType.RESERVED, "static"))
                result = jesubAction.addModifier("static");
            else if (accept(TokenType.RESERVED, "strictfp"))
                result = jesubAction.addModifier("strictfp");
            else if (accept(TokenType.RESERVED, "transient"))
                result = jesubAction.addModifier("transient");
            else if (accept(TokenType.RESERVED, "volatile"))
                result = jesubAction.addModifier("volatile");
            else
                break;
        }
        return result;
    }
    
    /** classname ::= IDENTIFIER { '.' IDENTIFIER } 
     *              = {
     *                when $0 : { String className = null; }
     *                when $1 : ( className = lexer.getTokenValue(); }
     *                when $3 : { className = action.addClassName(className, $3); }
     *                when $$ : { return className; }
     *                } 
     */
    public String classname() throws ParserException
    {
        String qualifiedClassName = null;
        if (getTokenType() == TokenType.IDENTIFIER)
        {
            String simpleName = getLexer().getTokenValue();
            nextToken();
            qualifiedClassName = jesubAction.Dot(qualifiedClassName, simpleName);

            while (accept(TokenType.OPERATOR, "."))
            {
                if (getTokenType() == TokenType.IDENTIFIER)
                {
                    simpleName = getLexer().getTokenValue();
                    nextToken();
                    qualifiedClassName = jesubAction.Dot(qualifiedClassName, simpleName);
                }
                else
                    throw error("More package name expected.");
            }
        }
        else
            throw error("Class name expected.");
        return qualifiedClassName;
    }

    /** 
     * implements_list ::= classname { ',' classname }
     */
    public Object implements_list() throws ParserException
    {
        Object result = null;
        String implementsClassName = classname();
        result = jesubAction.declareImplements(implementsClassName);
        
        while (accept(TokenType.DELIMITER, ","))
        {
            implementsClassName = classname();
            result = jesubAction.declareImplements(implementsClassName);
        }
        return result;
    }
   
    /** declare_body ::= '{' { declare_member } '}' */
    public Object declare_body() throws ParserException
    {
        expect(TokenType.DELIMITER, "{");
        Object result = null;
        while (true)
        {
            if (match(TokenType.DELIMITER, "}"))
                break;
            else if (match(TokenType.END, null))
                break;
            result = declare_member();
            // action.addMember(...);
        }
        expect(TokenType.DELIMITER, "}");
        return result;
    }
   
    /** declare_member ::= declare_type [ formal_parameters | semicolon ] */
    public Object declare_member() throws ParserException
    {
        Object result = null;
        result = declare_type();
        if (accept(TokenType.DELIMITER, ";"))
            ; // jesubAction.declareType();
        else if (match(TokenType.DELIMITER, "("))
        {
            Object formal = formal_parameters();
            if (accept(TokenType.DELIMITER, ";"))
                ; // jesubAction.Forward();
            //else
            }
        return result;
    }
   
    /** declare_type ::= [ access_modifiers ] type [ array_bounds ] IDENTIFIER */
    public Object declare_type() throws ParserException
    {
        String simpleName = null;
        Object result = null;
        Object modifiers = access_modifiers();
        Object declared = type();
        // array bounds
        if (getTokenType() == TokenType.IDENTIFIER)
        {
            simpleName = getLexer().getTokenValue();
            nextToken();
        } else {
            throw error("Name expected.");
        }
        //result = action.declareType(modifiers, declared, simpleName);
        return result;
   }

    /** formal_parameters ::= '(' [ declare_type { ',' declare_type ] ')'*/
    public Object formal_parameters() throws ParserException
    {
        Object result = null;
        expect(TokenType.DELIMITER, "(");
        if (match(TokenType.IDENTIFIER, null))
        {
            result = declare_type();
            // action.addFormal(result);
            while (accept(TokenType.DELIMITER, ","))
            {
                result = declare_type();
                // action.addFormal(result);
            }
        }
        expect(TokenType.DELIMITER, ")");
        return result;
    }

    public Object type() throws ParserException
    {
        Object result = null;
        result = primitive_type();
        return result;
   }
    
    /** primitive_type ::= "boolean" | "byte" | "short" | "char" | "int" | "long" | "float" | "double" | "void" */
    public Object primitive_type() throws ParserException
    {
        Object result = null;
        if (accept(TokenType.RESERVED, "boolean"))
            result = jesubAction.primitiveType("boolean");
        else if (accept(TokenType.RESERVED, "byte"))
            result = jesubAction.primitiveType("byte");
        else if (accept(TokenType.RESERVED, "short"))
            result = jesubAction.primitiveType("short");
        else if (accept(TokenType.RESERVED, "char"))
            result = jesubAction.primitiveType("char");
        else if (accept(TokenType.RESERVED, "int"))
            result = jesubAction.primitiveType("int");
        else if (accept(TokenType.RESERVED, "long"))
            result = jesubAction.primitiveType("long");
        else if (accept(TokenType.RESERVED, "float"))
            result = jesubAction.primitiveType("float");
        else if (accept(TokenType.RESERVED, "double"))
            result = jesubAction.primitiveType("double");
        else if (accept(TokenType.RESERVED, "void"))
            result = jesubAction.primitiveType("void");
        return result;
   }
}
