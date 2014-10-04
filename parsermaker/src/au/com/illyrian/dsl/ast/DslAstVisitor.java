// Copyright (c) 2014, Donald Strong.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.

package au.com.illyrian.dsl.ast;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

public class DslAstVisitor
{
    StringWriter writer= new StringWriter() ;
    PrintWriter  out = new PrintWriter(writer);
    
    public DslAstVisitor()
    {}
    
    public void visit(DslLanguage lang)
    {
        out.println();
        out.print("class ");
        out.print((lang.getName() == null) ? "test" : lang.getName());
        out.println("{");
        for (DslAst element : lang.getRules())
        {
            element.sequence(this);
        }
        out.println(")");
    }
    
    public void visit(DslTarget target)
    {
        out.print("public ");
        out.print((target.gettype() == null) ? "void" : target.gettype());
        out.print(" ");
        out.print(target.getname());
        out.println("()");
    }
    
    public void visit(DslRule rule)
    {
        rule.getTarget().sequence(this);
        out.print("{\n");
        rule.getExpr().sequence(this);
        out.println("}");
    }

    public void sequence(DslSequence seq)
    {
        for (DslAst element : seq.getSequence())
        {
            out.print("  ");
            element.sequence(this);
            out.println(";");
        }
    }

    public void sequence(DslAlternative alt)
    {
        alt.alternative(this);
    }

    public void alternative(DslAlternative alt)
    {
        DslAst [] list = alt.getAlternatives();
        int i = list.length;
        for (DslAst element : list)
        {
            out.print("  if ");
            element.alternative(this);
            if (i-- > 1)
                out.print("\n  else");
        }
    }

    public void sequence(DslOptional opt)
    {
        out.print("  if ");
        opt.getExpr().optional(this);
    }
    
    public void alternative(DslOptional opt)
    {
        out.print("  if ");
        opt.getExpr().optional(this);
        out.print("\n  ");
    }
    
    public void optional(DslOptional opt)
    {
        out.print("  if ");
        opt.getExpr().optional(this);
        out.print("\n  ");
    }
    
    public void sequence(DslIdentifier identifier)
    {
        out.print("    " + identifier.getValue() + "()");
    }
    
    public void alternative(DslIdentifier identifier)
    {
        out.print("(match_" + identifier.getValue() + "())");
        out.println(" " + identifier.getValue() + "();");
    }
    
    public void optional(DslIdentifier identifier)
    {
        out.print("(match_" + identifier.getValue() + "())");
        out.print(" " + identifier.getValue() + "()");
    }
    
    public void sequence(DslString string)
    {
        out.print("    expect(Lexer.IDENTIFIER, \"");
        out.print(string.getValue());
        out.print("\")");
    }
    
    public void alternative(DslString string)
    {
        out.print("(match(Lexer.IDENTIFIER, \"");
        out.print(string.getValue());
        out.print("\"))");
        out.print(" process(\"" + string.getValue() + "\")");
    }
    
    public void optional(DslString string)
    {
        out.print("(match(Lexer.IDENTIFIER, \"");
        out.print(string.getValue());
        out.print("\"))");
        out.print(" process(\"" + string.getValue() + "\")");
    }
    
    public void sequence(DslDelimiter delim)
    {
        out.print("    expect(Lexer.OPERATOR, \"");
        out.print(delim.getValue());
        out.print("\")");
    }
    
    public void alternative(DslDelimiter delim)
    {
        out.print("match(Lexer.OPERATOR, \"");
        out.print(delim.getValue());
        out.print("\")");
    }

    public void optional(DslDelimiter delim)
    {
        out.print("match(Lexer.OPERATOR, \"");
        out.print(delim.getValue());
        out.print("\")");
    }

    public String toString()
    {
        return writer.toString();
    }
}
