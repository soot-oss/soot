/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 1998-2001  Gerwin Klein <lsf@jflex.de>                    *
 * All rights reserved.                                                    *
 *                                                                         *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License. See the file      *
 * COPYRIGHT for more information.                                         *
 *                                                                         *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with this program; if not, write to the Free Software Foundation, Inc., *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* Java 1.2 language lexer specification */

/* Use together with unicode.flex for Unicode preprocesssing */
/* and java12.cup for a Java 1.2 parser                      */

/* Note that this lexer specification is not tuned for speed.
   It is in fact quite slow on integer and floating point literals, 
   because the input is read twice and the methods used to parse
   the numbers are not very fast. 
   For a production quality application (e.g. a Java compiler) 
   this could be optimized */

package soot.javaToJimple.jj.parse;

import java_cup.runtime.Symbol;
import polyglot.lex.Lexer;
import polyglot.lex.*;
import polyglot.util.Position;
import polyglot.util.ErrorQueue;
import polyglot.util.ErrorInfo;
import soot.javaToJimple.jj.DPosition;

%%

%public
%class Lexer_c
%implements Lexer
%type Token
%function nextToken

%unicode
%pack

%line
%column

%{
    StringBuffer sb = new StringBuffer();
    String file;
    ErrorQueue eq;

    public Lexer_c(java.io.InputStream in, String file, ErrorQueue eq) {
        this(new java.io.BufferedReader(new java.io.InputStreamReader(in)),
             file, eq);
    }
    
    public Lexer_c(java.io.Reader reader, String file, ErrorQueue eq) {
        this(new EscapedUnicodeReader(reader));
        this.file = file;
        this.eq = eq;
    }

    public String file() {
        return file;
    }

    private Position pos() {
        return new DPosition(file, yyline+1, yycolumn, yycolumn+yytext().length());
    }
    private Position pos(int len) {
        return new DPosition(file, yyline+1, yycolumn-len-1, yycolumn+1);
    }

    private Token key(int symbol) {
        return new Keyword(pos(), yytext(), symbol);
    }

    private Token op(int symbol) {
        return new Operator(pos(), yytext(), symbol);
    }

    private Token id() {
        return new Identifier(pos(), yytext(), sym.IDENTIFIER);
    }

    private Token int_token(String s, int radix) {
        long x = parseLong(s, radix);
        return new IntegerLiteral(pos(), (int) x, sym.INTEGER_LITERAL);
    }

    private Token long_token(String s, int radix) {
        long x = parseLong(s, radix);
        return new LongLiteral(pos(), x, sym.LONG_LITERAL);
    }

    private Token float_token(String s) {
        float x = Float.valueOf(s).floatValue();
        return new FloatLiteral(pos(), x, sym.FLOAT_LITERAL);
    }

    private Token double_token(String s) {
        double x = Double.valueOf(s).doubleValue();
        return new DoubleLiteral(pos(), x, sym.DOUBLE_LITERAL);
    }
    
    private Token char_token(char x) {
        return new CharacterLiteral(pos(), x, sym.CHARACTER_LITERAL);
    }

    private Token boolean_token(boolean x) {
        return new BooleanLiteral(pos(), x, sym.BOOLEAN_LITERAL);
    }

    private Token null_token() {
        return new NullLiteral(pos(), sym.NULL_LITERAL);
    }

    private Token string_token() {
        return new StringLiteral(pos(sb.length()), sb.toString(), sym.STRING_LITERAL);
    }

  /* assumes correct representation of a long value for 
     specified radix in String s */
  private long parseLong(String s, int radix) {
    int max = s.length();
    long result = 0;
    long digit;

    for (int i = 0; i < max; i++) {
      digit = Character.digit(s.charAt(i), radix);
      result *= radix;
      result += digit;
    }

    return result;
  }
%}

%eofval{
        return new EOF(pos(), sym.EOF); 
%eofval}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment = "/*" [^*] {CommentContent} \*+ "/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} \*+ "/"

CommentContent = ( [^*] | \*+[^*/] )*

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]
    
/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}|{FLit4}) [fF]
DoubleLiteral = {FLit1}|{FLit2}|{FLit3}|{FLit4}

FLit1 = [0-9]+ \. [0-9]* {Exponent}?
FLit2 = \. [0-9]+ {Exponent}?
FLit3 = [0-9]+ {Exponent}
FLit4 = [0-9]+ {Exponent}?

Exponent = [eE] [+\-]? [0-9]+

/* string and character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

%state STRING, CHARLITERAL

%%

<YYINITIAL> {

  /* keywords */
  "abstract"                     { return key(sym.ABSTRACT); }
  "boolean"                      { return key(sym.BOOLEAN); }
  "break"                        { return key(sym.BREAK); }
  "byte"                         { return key(sym.BYTE); }
  "case"                         { return key(sym.CASE); }
  "catch"                        { return key(sym.CATCH); }
  "char"                         { return key(sym.CHAR); }
  "class"                        { return key(sym.CLASS); }
  "const"                        { return key(sym.CONST); }
  "continue"                     { return key(sym.CONTINUE); }
  "do"                           { return key(sym.DO); }
  "double"                       { return key(sym.DOUBLE); }
  "else"                         { return key(sym.ELSE); }
  "extends"                      { return key(sym.EXTENDS); }
  "final"                        { return key(sym.FINAL); }
  "finally"                      { return key(sym.FINALLY); }
  "float"                        { return key(sym.FLOAT); }
  "for"                          { return key(sym.FOR); }
  "default"                      { return key(sym.DEFAULT); }
  "implements"                   { return key(sym.IMPLEMENTS); }
  "import"                       { return key(sym.IMPORT); }
  "instanceof"                   { return key(sym.INSTANCEOF); }
  "int"                          { return key(sym.INT); }
  "interface"                    { return key(sym.INTERFACE); }
  "long"                         { return key(sym.LONG); }
  "native"                       { return key(sym.NATIVE); }
  "new"                          { return key(sym.NEW); }
  "goto"                         { return key(sym.GOTO); }
  "if"                           { return key(sym.IF); }
  "public"                       { return key(sym.PUBLIC); }
  "short"                        { return key(sym.SHORT); }
  "super"                        { return key(sym.SUPER); }
  "switch"                       { return key(sym.SWITCH); }
  "synchronized"                 { return key(sym.SYNCHRONIZED); }
  "package"                      { return key(sym.PACKAGE); }
  "private"                      { return key(sym.PRIVATE); }
  "protected"                    { return key(sym.PROTECTED); }
  "transient"                    { return key(sym.TRANSIENT); }
  "return"                       { return key(sym.RETURN); }
  "void"                         { return key(sym.VOID); }
  "static"                       { return key(sym.STATIC); }
  "while"                        { return key(sym.WHILE); }
  "this"                         { return key(sym.THIS); }
  "throw"                        { return key(sym.THROW); }
  "throws"                       { return key(sym.THROWS); }
  "try"                          { return key(sym.TRY); }
  "volatile"                     { return key(sym.VOLATILE); }
  "strictfp"                     { return key(sym.STRICTFP); }
  "assert"                       { return key(sym.ASSERT); }

  /* boolean literals */
  "true"                         { return boolean_token(true); }
  "false"                        { return boolean_token(false); }

  /* null literal */
  "null"                         { return null_token(); }

  /* separators */
  "("                            { return op(sym.LPAREN); }
  ")"                            { return op(sym.RPAREN); }
  "{"                            { return op(sym.LBRACE); }
  "}"                            { return op(sym.RBRACE); }
  "["                            { return op(sym.LBRACK); }
  "]"                            { return op(sym.RBRACK); }
  ";"                            { return op(sym.SEMICOLON); }
  ","                            { return op(sym.COMMA); }
  "."                            { return op(sym.DOT); }

  /* operators */
  "="                            { return op(sym.EQ); }
  ">"                            { return op(sym.GT); }
  "<"                            { return op(sym.LT); }
  "!"                            { return op(sym.NOT); }
  "~"                            { return op(sym.COMP); }
  "?"                            { return op(sym.QUESTION); }
  ":"                            { return op(sym.COLON); }
  "=="                           { return op(sym.EQEQ); }
  "<="                           { return op(sym.LTEQ); }
  ">="                           { return op(sym.GTEQ); }
  "!="                           { return op(sym.NOTEQ); }
  "&&"                           { return op(sym.ANDAND); }
  "||"                           { return op(sym.OROR); }
  "++"                           { return op(sym.PLUSPLUS); }
  "--"                           { return op(sym.MINUSMINUS); }
  "+"                            { return op(sym.PLUS); }
  "-"                            { return op(sym.MINUS); }
  "*"                            { return op(sym.MULT); }
  "/"                            { return op(sym.DIV); }
  "&"                            { return op(sym.AND); }
  "|"                            { return op(sym.OR); }
  "^"                            { return op(sym.XOR); }
  "%"                            { return op(sym.MOD); }
  "<<"                           { return op(sym.LSHIFT); }
  ">>"                           { return op(sym.RSHIFT); }
  ">>>"                          { return op(sym.URSHIFT); }
  "+="                           { return op(sym.PLUSEQ); }
  "-="                           { return op(sym.MINUSEQ); }
  "*="                           { return op(sym.MULTEQ); }
  "/="                           { return op(sym.DIVEQ); }
  "&="                           { return op(sym.ANDEQ); }
  "|="                           { return op(sym.OREQ); }
  "^="                           { return op(sym.XOREQ); }
  "%="                           { return op(sym.MODEQ); }
  "<<="                          { return op(sym.LSHIFTEQ); }
  ">>="                          { return op(sym.RSHIFTEQ); }
  ">>>="                         { return op(sym.URSHIFTEQ); }

  /* string literal */
  \"                             { yybegin(STRING); sb.setLength(0); }

  /* character literal */
  \'                             { yybegin(CHARLITERAL); }

  /* numeric literals */

  {DecIntegerLiteral}            { return int_token(yytext(), 10); }
  {DecLongLiteral}               { return long_token(yytext().substring(0,yylength()-1), 10); }
  
  {HexIntegerLiteral}            { return int_token(yytext().substring(2), 16); }
  {HexLongLiteral}               { return long_token(yytext().substring(2,yylength()-1), 16); }
 
  {OctIntegerLiteral}            { return int_token(yytext(), 8); }  
  {OctLongLiteral}               { return long_token(yytext().substring(0,yylength()-1), 8); }
  
  {FloatLiteral}                 { return float_token(yytext().substring(0,yylength()-1)); }
  {DoubleLiteral}                { return double_token(yytext()); }
  {DoubleLiteral}[dD]            { return double_token(yytext().substring(0,yylength()-1)); }
  
  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }

  /* identifiers */ 
  {Identifier}                   { return id(); }  
}

<STRING> {
  \"                             { yybegin(YYINITIAL); return string_token(); }
  
  {StringCharacter}+             { sb.append( yytext() ); }
  
  /* escape sequences */
  "\\b"                          { sb.append( '\b' ); }
  "\\t"                          { sb.append( '\t' ); }
  "\\n"                          { sb.append( '\n' ); }
  "\\f"                          { sb.append( '\f' ); }
  "\\r"                          { sb.append( '\r' ); }
  "\\\""                         { sb.append( '\"' ); }
  "\\'"                          { sb.append( '\'' ); }
  "\\\\"                         { sb.append( '\\' ); }
  \\[0-3]?{OctDigit}?{OctDigit}  { char val = (char) Integer.parseInt(yytext().substring(1),8);
				   sb.append(val); }
  
  /* error cases */
  \\.                            { eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                              "Illegal escape sequence \""+yytext()+"\"",
					      pos()); }
  {LineTerminator}               { eq.enqueue(ErrorInfo.LEXICAL_ERROR,
					      "Unterminated string at end of line",
					      pos()); }
}

<CHARLITERAL> {
  {SingleCharacter}\'            { yybegin(YYINITIAL); return char_token(yytext().charAt(0)); }
  
  /* escape sequences */
  "\\b"\'                        { yybegin(YYINITIAL); return char_token('\b');}
  "\\t"\'                        { yybegin(YYINITIAL); return char_token('\t');}
  "\\n"\'                        { yybegin(YYINITIAL); return char_token('\n');}
  "\\f"\'                        { yybegin(YYINITIAL); return char_token('\f');}
  "\\r"\'                        { yybegin(YYINITIAL); return char_token('\r');}
  "\\\""\'                       { yybegin(YYINITIAL); return char_token('\"');}
  "\\'"\'                        { yybegin(YYINITIAL); return char_token('\'');}
  "\\\\"\'                       { yybegin(YYINITIAL); return char_token('\\'); }
  \\[0-3]?{OctDigit}?{OctDigit}\' { yybegin(YYINITIAL);
				    long val = parseLong(yytext().substring(1,yylength()-1), 8);
			            return char_token((char)val); }
  
  /* error cases */
  \\.                            { eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                              "Illegal escape sequence \""+yytext()+"\"",
					      pos()); }
  {LineTerminator}               { eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                              "Unterminated character literal at end of line",
					      pos()); }
}

/* error fallback */
.|\n                             { eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                              "Illegal character \""+yytext()+"\"",
					      pos()); }
