package au.com.illyrian.parser.expr;

import au.com.illyrian.classmaker.ast.AstExpression;
import au.com.illyrian.classmaker.ast.AstExpressionFactory;
import au.com.illyrian.parser.Lexer;
import au.com.illyrian.parser.TokenType;
import au.com.illyrian.parser.impl.Latin1Lexer;
import au.com.illyrian.parser.impl.ParserConstants;
import au.com.illyrian.parser.opp.Operator;
import au.com.illyrian.parser.opp.OperatorPrecidenceParser;

public class AstExpressionPrecidenceParser 
     extends OperatorPrecidenceParser<AstExpression> implements ParserConstants
{
    AstExpressionPrecidenceAction actions = null;
    
    public AstExpressionPrecidenceParser()
    {
        addOperators();
        addReservedOperators();
        addReservedOperands();
    }

    public AstExpressionPrecidenceParser(AstExpressionFactory factory)
    {
        addOperators();
        addReservedOperators();
        addReservedOperands();
        setAstExpressionFactory(factory);
    }
    
    public void setAstExpressionFactory(AstExpressionFactory factory)
    {
        actions = new AstExpressionPrecidenceAction(factory);
        setPrecidenceActions(actions);
    }

    protected Lexer createLexer()
    {
        return new Latin1Lexer();
    }
    
    protected void addReservedOperands() {
        addReservedOperand("this");
        addReservedOperand("super");
        addReservedOperand("null");
        addReservedOperand("new");
    }
    
    protected void addReservedOperators() {
        addReservedOperator("new");
        addReservedOperator("instanceof");
    }
    
    protected void addOperators() {
        //addLedOperator("(", ")", ParserConstants.CALL, 16, Operator.PARAMS);
        addLedOperator("[", "]", ParserConstants.INDEX, 16, Operator.BRACKET);
        addLedOperator(".", ParserConstants.DOT, 15, Operator.BINARY);
        addLedOperator("--", ParserConstants.POSTDEC, 14, Operator.POSTFIX);
        addLedOperator("++", ParserConstants.POSTINC, 14, Operator.POSTFIX);
        addNudOperator("--", ParserConstants.DEC, 13, Operator.PREFIX);
        addNudOperator("++", ParserConstants.INC, 13, Operator.PREFIX);
        addNudOperator("+", ParserConstants.NOP, 13, Operator.PREFIX);
        addNudOperator("-", ParserConstants.NEG, 13, Operator.PREFIX);
        addNudOperator("!", ParserConstants.NOT, 13, Operator.PREFIX);
        addNudOperator("~", ParserConstants.INV, 13, Operator.PREFIX);
        //addNudOperator("new", ParserConstants.NEW, 13, Operator.PREFIX);
        addNudOperator("(", ")", ParserConstants.CAST, 13, Operator.BRACKET);
        addLedOperator("*", ParserConstants.MULT, 12, Operator.BINARY);
        addLedOperator("/", ParserConstants.DIV, 12, Operator.BINARY);
        addLedOperator("%", ParserConstants.REM, 12, Operator.BINARY);
        addLedOperator("+", ParserConstants.ADD, 11, Operator.BINARY);
        addLedOperator("-", ParserConstants.SUBT, 11, Operator.BINARY);
        addLedOperator("<<", ParserConstants.SHL, 10, Operator.BINARY);
        addLedOperator(">>", ParserConstants.SHR, 10, Operator.BINARY);
        addLedOperator(">>>", ParserConstants.USHR, 10, Operator.BINARY);
        addLedOperator("<", ParserConstants.LT, 9, Operator.BINARY);
        addLedOperator(">", ParserConstants.GT, 9, Operator.BINARY);
        addLedOperator("<=", ParserConstants.LE, 9, Operator.BINARY);
        addLedOperator(">=", ParserConstants.GE, 9, Operator.BINARY);
        addLedOperator("instanceof", ParserConstants.INSTANCEOF, 9, Operator.BINARY);
        addLedOperator("==", ParserConstants.EQ, 8, Operator.BINARY);
        addLedOperator("!=", ParserConstants.NE, 8, Operator.BINARY);
        addLedOperator("&", ParserConstants.AND, 7, Operator.BINARY);
        addLedOperator("^", ParserConstants.XOR, 6, Operator.BINARY);
        addLedOperator("|", ParserConstants.OR, 5, Operator.BINARY);
        addLedOperator("&&", ParserConstants.ANDTHEN, 4, Operator.BINARY);
        addLedOperator("||", ParserConstants.ORELSE, 3, Operator.BINARY);
//        addLedOperator("?", ":", ParserConstants.COND, 2, Operator.TERNARY);
        addLedOperator("=", ParserConstants.ASSIGN, 1, Operator.BINARYRIGHT);
//        addLedOperator("+=", ParserConstants.ADDASSIGN, 1, Operator.BINARYRIGHT);
//        addLedOperator("-=", ParserConstants.MINUSASSIGN, 1, Operator.BINARYRIGHT);
//        addLedOperator("*=", ParserConstants.MULTASSIGN, 1, Operator.BINARYRIGHT);
//        addLedOperator("/=", ParserConstants.DIVASSIGN, 1, Operator.BINARYRIGHT);
//        addLedOperator("%=", ParserConstants.REMASSIGN, 1, Operator.BINARYRIGHT);
        addLedOperator(",", ParserConstants.COMMA, 0, Operator.BINARY);
    }
    
    protected AstExpression parentheses(Operator nudOperator) {
        AstExpression result = null;
        if (accept(TokenType.DELIMITER, "(")) {
            AstExpression firstOperand = expression();
            expect(TokenType.DELIMITER, ")", "\')\' expected");
            // If an expression immediately follows a parenthesized expression
            // then the previous expression may have been a cast.
            // Eg. (java.lang.Serializable)b (long)(int)(short)d
            // However there are special cases.
            // Eg. (value)-2 (a.b.c)+5
            if (nudOperator != null && isCastExpression(firstOperand)) {
                AstExpression secondOperand = expression(nudOperator.precidence);
                result = getPrecidenceActions().binaryAction(nudOperator.getIndex(), firstOperand, secondOperand);
            } else {
                result = getPrecidenceActions().unaryAction(ParserConstants.NOP, firstOperand);
            }
        } else {
            throw exception("Unexpected Perenthesis");
        }

        return result;
    }
       
   /**
     * name_method      ::= IDENTIFIER 
     *                    | IDENTIFIER ( )
     *                    | IDENTIFIER ( expression )
    * @return
    */
   protected AstExpression nameOperand()
   {
       return nameMethod();
   }

   /**
     * reserveSequence  ::= THIS 
     *                    | THIS ( ) 
     *                    | THIS ( expression ) 
     *                    | SUPER 
     *                    | SUPER ( )  ;
     *                    | SUPER ( expression ) 
     *                    | NEW newInitialiser
    */
   protected AstExpression reservedOperand()
   {
       AstExpression result = null;
       if (match(TokenType.RESERVED, "this")) {
           result = nameMethod();
       } else if (match(TokenType.RESERVED, "super")) {
           result = nameMethod();
       } else if (match(TokenType.RESERVED, "new")) {
           result = newInitialiser();
       } else {
           result = super.reservedOperand();
       }
       return result;
   }
   
   protected AstExpression nameMethod()
   {
       AstExpression result = getPrecidenceActions().tokenAction(getLexer());
       nextToken();
       if (accept(TokenType.DELIMITER, "(")) {
           AstExpression parameters = null;
           if (!match(TokenType.DELIMITER, ")")) {
               parameters = expression();
           }
           expect(TokenType.DELIMITER, ")", "\')\' expected");
           result = actions.call(result, parameters);
       }
       return result;
   }
  
   /**
    * newInitiliser   ::= NEW qualifiedType ( )
    *                   | NEW qualifiedType ( parameters )
    *                   | NEW qualifiedType [ expression ]
    *                   | NEW qualifiedType [ expression ] arrayTypeExtend
    * @return
    */
   protected AstExpression newInitialiser()
   {
       //Object a = new int[5][6][][];
       AstExpression result = null;
       expect(TokenType.RESERVED, "new");
       AstExpression qualifiedType = qualifiedType();
       if (accept(TokenType.DELIMITER, "(")) {
           AstExpression parameters = null;
           if (!match(TokenType.DELIMITER, ")")) {
               parameters = expression(0);
           }
           expect(TokenType.DELIMITER, ")", "\')\' expected");
           AstExpression ctor = getPrecidenceActions().binaryAction(CALL, qualifiedType, parameters);
           result = getPrecidenceActions().unaryAction(NEW, ctor);
       } else if (accept(TokenType.DELIMITER, "[")) {
           AstExpression parameters = expression();
           expect(TokenType.DELIMITER, "]", "\']\' expected");
           AstExpression arrayType = arrayTypeDimension(qualifiedType);
           AstExpression ctor = getPrecidenceActions().binaryAction(ARRAYOF, arrayType, parameters);
           result = getPrecidenceActions().unaryAction(NEW, ctor);
       } else {
           throw exception("Array dimension or constructor parameters expected");
       }
       return result;
   }
   
   /**
    * arrayTypeDimension ::= [ expression ]
    *                      | [ expression ] arrayTypeDimension
    *                      | [ ] arrayTypeExtend
    *                      | EMPTY
    * 
    * @param qualifiedType
    * @return
    */
   protected AstExpression arrayTypeDimension(AstExpression qualifiedType) {
       AstExpression result = qualifiedType;
       if (accept(TokenType.DELIMITER, "[")) {
           if (accept(TokenType.DELIMITER, "]")) {
               AstExpression arrayType = arrayTypeExtend(qualifiedType);
               result = getPrecidenceActions().unaryAction(ARRAYOF, arrayType);
           } else {
               AstExpression parameters = expression();
               expect(TokenType.DELIMITER, "]", "\']\' expected");
               AstExpression arrayType = arrayTypeDimension(qualifiedType);
               result = getPrecidenceActions().binaryAction(ARRAYOF, arrayType, parameters);
           }
       }
       return result;
   }
   
   /**
    * arrayTypeExtend ::= [ ] arrayTypeExtend
    *                   | EMPTY
    * 
    * @param qualifiedType
    * @return
    */
   protected AstExpression arrayTypeExtend(AstExpression qualifiedType) {
       AstExpression result = qualifiedType;
       if (accept(TokenType.DELIMITER, "[")) {
           expect(TokenType.DELIMITER, "]");
           AstExpression arrayType = arrayTypeExtend(qualifiedType);
           result = getPrecidenceActions().unaryAction(ARRAYOF, arrayType);
       }
       return result;
   }
   
   /**
    * qualifiedType   ::= IDENTIFIER
    *                   | IDENTIFIER . qualifiedType
    * @return
    */
   protected AstExpression qualifiedType()
   {
       if (match(TokenType.IDENTIFIER)) {
           AstExpression result = getPrecidenceActions().tokenAction(getLexer());
           nextToken();
           if (accept(TokenType.DELIMITER, ".")) {
               AstExpression rightOperand = qualifiedType();
               result = actions.binaryAction(DOT, result, rightOperand);
           }
           return result;
       } else {
           throw exception("Identifier expected");
       }
   }

   /**
     * Test whether the previous parenthesized expression was a cast.
     * If another expression immediately follows a parenthesized expression
     * then the previous expression may have been a cast. <br/>
     * Eg. (java.lang.Serializable)b (long)(int)(short)d <p/>
     * However there are special cases. <br/>
     * Eg. (value)-2 (a.b.c)+5 <br/>
     * @param firstOperand the expression that may be a type
     * @return true if the parenthesized expression represents a cast
     */
    public boolean isCastExpression(AstExpression firstOperand)
    {
        if (isTokenAnOperand()) {
            // if the next token is an operand then the parenthesized expression MUST be a cast
            return true;
        } else {
            // Otherwise test whether the next token is a prefix operator
            Operator nud = getNudOperator(); 
            if (nud != null) {
                if ("-".equals(nud.getName()) || "+".equals(nud.getName())) {
                    // The + and - operators are unary if the previous expression is a cast,
                    // otherwise they are binary.
                    // Eg. (type)-expr vs. (expr)-expr and (type)+expr vs (expr)+expr
                    String operand = firstOperand.toString();
                    // type can only be a primitive numeric type if firstOperand is a cast
                    if ("byte".equals(operand) ||
                            "char".equals(operand) ||
                            "short".equals(operand) ||
                            "int".equals(operand) ||
                            "long".equals(operand) ||
                            "float".equals(operand) ||
                            "double".equals(operand)) {
                        return true;
                    }
                    return false;
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Check whether the next token is an operand.
     * Operands include literals and identifiers, including some reserved words
     * like <code>this</code> and <code>super</code>.
     * @return true if the next token is an operand
     */
    public boolean isTokenAnOperand()
    {
        TokenType token = getTokenType();
        switch (token) {
        case IDENTIFIER:
        case NUMBER:
        case DECIMAL:
        case STRING:
        case CHARACTER:
            return true;
        case RESERVED:
            String operator = getLexer().getTokenValue();
            return reservedOperands.contains(operator);
        default:
        }
        return false;
    }
}
