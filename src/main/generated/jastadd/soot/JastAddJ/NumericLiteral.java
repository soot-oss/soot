/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;
/**
 * A NumericLiteral is a raw literal, produced by the parser.
 * NumericLiterals are rewritten to the best matching concrete
 * numeric literal kind, or IllegalLiteral.
 * @production NumericLiteral : {@link Literal};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.ast:18
 */
public class NumericLiteral extends Literal implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public NumericLiteral clone() throws CloneNotSupportedException {
    NumericLiteral node = (NumericLiteral)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public NumericLiteral copy() {
    try {
      NumericLiteral node = (NumericLiteral) clone();
      node.parent = null;
      if(children != null)
        node.children = (ASTNode[]) children.clone();
      return node;
    } catch (CloneNotSupportedException e) {
      throw new Error("Error: clone not supported for " +
        getClass().getName());
    }
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public NumericLiteral fullCopy() {
    NumericLiteral tree = (NumericLiteral) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) children[i];
        if(child != null) {
          child = child.fullCopy();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
  }
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:321
   */
  

	public static final int DECIMAL = 0;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:322
   */
  
	public static final int HEXADECIMAL = 1;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:323
   */
  
	public static final int OCTAL = 2;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:324
   */
  
	public static final int BINARY = 3;
  /**
	 * The trimmed digits.
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:382
   */
  

	/**
	 * The trimmed digits.
	 */
	protected String digits = "";
  /**
	 * Sets the trimmed digits of this literal.
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:387
   */
  public void setDigits(String digits) {
		this.digits = digits;
	}
  /**
	 * The literal kind tells which kind of literal it is;
	 * it's either a DECIMAL, HEXADECIMAL, OCTAL or BINARY literal.
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:401
   */
  

	/**
	 * The literal kind tells which kind of literal it is;
	 * it's either a DECIMAL, HEXADECIMAL, OCTAL or BINARY literal.
	 */
	protected int kind = NumericLiteral.DECIMAL;
  /**
	 * Sets the literal kind.
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:406
   */
  public void setKind(int kind) {
		this.kind = kind;
	}
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:489
   */
  
		private StringBuffer buf = new StringBuffer();
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:490
   */
  
		private int idx = 0;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:491
   */
  
		private boolean whole;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:492
   */
  // have whole part?
		private boolean fraction;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:493
   */
  // have fraction part?
		private boolean exponent;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:494
   */
  // have exponent part?
		private boolean floating;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:495
   */
  // is floating point?
		private boolean isFloat;
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:496
   */
  
		private boolean isLong;
  /**
 		 * @return a readable name to describe this literal.
 		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:501
   */
  

		/**
 		 * @return a readable name to describe this literal.
 		 */
		private String name() {
			String name;
			switch (kind) {
				case DECIMAL:
					name = "decimal";
					break;
				case HEXADECIMAL:
					name = "hexadecimal";
					break;
				case OCTAL:
					name = "octal";
					break;
				case BINARY:
				default:
					name = "binary";
					break;
			}
			if (floating)
				return name+" floating point";
			else
				return name;
		}
  /**
		 * The next character in the literal is a significant character;
		 * push it onto the buffer.
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:528
   */
  

		/**
		 * The next character in the literal is a significant character;
		 * push it onto the buffer.
		 */
		private void pushChar() {
			buf.append(getLITERAL().charAt(idx++));
		}
  /**
		 * Skip ahead n chracters in the literal.
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:535
   */
  

		/**
		 * Skip ahead n chracters in the literal.
		 */
		private void skip(int n) {
			idx += n;
		}
  /**
		 * @return true if there exists at least n more characters
		 * in the literal
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:543
   */
  

		/**
		 * @return true if there exists at least n more characters
		 * in the literal
		 */
		private boolean have(int n) {
			return getLITERAL().length() >= idx+n;
		}
  /**
		 * Look at the n'th next character.
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:550
   */
  

		/**
		 * Look at the n'th next character.
		 */
		private char peek(int n) {
			return getLITERAL().charAt(idx+n);
		}
  /**
		 * @return true if the character c is a decimal digit
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:557
   */
  

		/**
		 * @return true if the character c is a decimal digit
		 */
		private static final boolean isDecimalDigit(char c) {
			return c == '_' || c >= '0' && c <= '9';
		}
  /**
		 * @return true if the character c is a hexadecimal digit
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:564
   */
  

		/**
		 * @return true if the character c is a hexadecimal digit
		 */
		private static final boolean isHexadecimalDigit(char c) {
			return c == '_' || c >= '0' && c <= '9' ||
				c >= 'a' && c <= 'f' ||
				c >= 'A' && c <= 'F';
		}
  /**
		 * @return true if the character c is a binary digit
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:573
   */
  

		/**
		 * @return true if the character c is a binary digit
		 */
		private static final boolean isBinaryDigit(char c) {
			return c == '_' || c == '0' || c == '1';
		}
  /**
		 * @return true if the character c is an underscore
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:580
   */
  

		/**
		 * @return true if the character c is an underscore
		 */
		private static final boolean isUnderscore(char c) {
			return c == '_';
		}
  /**
		 * Parse a literal. If there is a syntax error in the literal,
		 * an IllegalLiteral will be returned.
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:588
   */
  

		/**
		 * Parse a literal. If there is a syntax error in the literal,
		 * an IllegalLiteral will be returned.
		 */
		public Literal parse() {
			if (getLITERAL().length() == 0)
				throw new IllegalStateException("Empty NumericLiteral");

			kind = classifyLiteral();

			Literal literal;
			if (!floating)
				literal = parseDigits();
			else
				literal = parseFractionPart();
			literal.setStart(LITERALstart);
			literal.setEnd(LITERALend);
			return literal;
		}
  /**
		 * Classify the literal.
		 *
		 * @return either DECIMAL, HEXADECIMAL or BINARY
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:609
   */
  

		/**
		 * Classify the literal.
		 *
		 * @return either DECIMAL, HEXADECIMAL or BINARY
		 */
		private int classifyLiteral() {
			if (peek(0) == '.') {
				floating = true;
				return DECIMAL;
			} else if (peek(0) == '0') {
				if (!have(2)) {
					// the only 1-length string that starts with 0 (obvious!)
					return DECIMAL;
				} else if (peek(1) == 'x' || peek(1) == 'X') {
					skip(2);
					return HEXADECIMAL;
				} else if (peek(1) == 'b' || peek(1) == 'B') {
					skip(2);
					return BINARY;
				} else {
					return DECIMAL;
				}
			} else {
				return DECIMAL;
			}
		}
  /**
		 * If the current character is an underscore, the previous and next
		 * characters need to be valid digits or underscores.
		 *
		 * @return true if the underscore is misplaced
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:637
   */
  

		/**
		 * If the current character is an underscore, the previous and next
		 * characters need to be valid digits or underscores.
		 *
		 * @return true if the underscore is misplaced
		 */
		private boolean misplacedUnderscore() {
			// first and last characters are never allowed to be an underscore
			if (idx == 0 || idx+1 == getLITERAL().length())
				return true;

			switch (kind) {
				case DECIMAL:
					return !(isDecimalDigit(peek(-1)) && isDecimalDigit(peek(1)));
				case HEXADECIMAL:
					return !(isHexadecimalDigit(peek(-1)) && isHexadecimalDigit(peek(1)));
				case BINARY:
					return !(isBinaryDigit(peek(-1)) && isBinaryDigit(peek(1)));
			}
			throw new IllegalStateException("Unexpected literal kind");
		}
  /**
		 * Report an illegal digit.
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:656
   */
  

		/**
		 * Report an illegal digit.
		 */
		private Literal syntaxError(String msg) {
			String err = "in "+name()+" literal "+
				"\""+getLITERAL()+"\""+": "+msg;
			return new IllegalLiteral(err);
		}
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:662
   */
  

		private Literal unexpectedCharacter(char c) {
			return syntaxError("unexpected character '"+c+"'; not a valid digit");
		}
  /**
		 * Returns a string of only the lower case digits of the
		 * parsed numeric literal.
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:670
   */
  

		/**
		 * Returns a string of only the lower case digits of the
		 * parsed numeric literal.
		 */
		private String getLiteralString() {
			return buf.toString().toLowerCase();
		}
  /**
		 * Parse and build an IntegerLiteral, LongLiteral,
		 * FloatingPointLiteral or DoubleLiteral. Returns an
		 * IllegalLiteral if the numeric literal can not be
		 * parsed.
		 *
		 * Note: does not perform bounds checks.
		 *
		 * @return a concrete literal on success, or an IllegalLiteral if there is a syntax error
		 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:684
   */
  

		/**
		 * Parse and build an IntegerLiteral, LongLiteral,
		 * FloatingPointLiteral or DoubleLiteral. Returns an
		 * IllegalLiteral if the numeric literal can not be
		 * parsed.
		 *
		 * Note: does not perform bounds checks.
		 *
		 * @return a concrete literal on success, or an IllegalLiteral if there is a syntax error
		 */
		private Literal buildLiteral() {
			NumericLiteral literal;
			setDigits(buf.toString().toLowerCase());

			if (!floating) {
				if (!whole)
					return syntaxError("at least one digit is required");

				// check if the literal is octal, and if so report illegal digits
				if (kind == DECIMAL) {
					if (digits.charAt(0) == '0') {
						kind = OCTAL;
						for (int idx = 1; idx < digits.length(); ++idx) {
							char c = digits.charAt(idx);
							if (c < '0' || c > '7')
								return unexpectedCharacter(c);
						}
					}
				}
				
				if (isLong)
					literal = new LongLiteral(getLITERAL());
				else
					literal = new IntegerLiteral(getLITERAL());
			} else {
				if (kind == HEXADECIMAL && !exponent)
					return syntaxError("exponent is required");

				if (!(whole || fraction))
					return syntaxError("at least one digit is required in "+
							"either the whole or fraction part");

				if (kind == HEXADECIMAL)
					digits = "0x"+digits;// digits parsed with Float or Double

				if (isFloat)
					literal = new FloatingPointLiteral(getLITERAL());
				else
					literal = new DoubleLiteral(getLITERAL());
			}

			literal.setDigits(getDigits());
			literal.setKind(getKind());
			return literal;
		}
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:730
   */
  

		private Literal parseDigits() {
			// while we have at least one more character/digit
			while (have(1)) {
				char c = peek(0);
				switch (c) {
					case '_':
						if (misplacedUnderscore())
							return syntaxError("misplaced underscore - underscores may only "+
									"be used within sequences of digits");
						skip(1);
						continue;
					case '.':
						if (kind != DECIMAL && kind != HEXADECIMAL)
							return unexpectedCharacter(c);
						return parseFractionPart();
					case 'l':
					case 'L':
						if (have(2))
							return syntaxError("extra digits/characters "+
								"after suffix "+c);
						isLong = true;
						skip(1);
						continue;
					case 'f':
					case 'F':
						if (kind == BINARY)
							return unexpectedCharacter(c);
						isFloat = true;
					case 'd':
					case 'D':
						if (kind == BINARY)
							return unexpectedCharacter(c);
						if (kind != HEXADECIMAL) {
							if (have(2))
								return syntaxError("extra digits/characters "+
										"after type suffix "+c);
							floating = true;
							skip(1);
						} else {
							whole = true;
							pushChar();
						}
						continue;
				}

				switch (kind) {
					case DECIMAL:
						if (c == 'e' || c == 'E') {
							return parseExponentPart();

						} else if (c == 'f' || c == 'F') {
							if (have(2))
								return syntaxError("extra digits/characters "+
										"after type suffix "+c);
							floating = true;
							isFloat = true;
							skip(1);
						} else if (c == 'd' || c == 'D') {
							if (have(2))
								return syntaxError("extra digits/characters "+
										"after type suffix "+c);
							floating = true;
							skip(1);
						} else {
							if (!isDecimalDigit(c))
								return unexpectedCharacter(c);
							whole = true;
							pushChar();
						}
						continue;
					case HEXADECIMAL:
						if (c == 'p' || c == 'P')
							return parseExponentPart();

						if (!isHexadecimalDigit(c))
							return unexpectedCharacter(c);
						whole = true;
						pushChar();
						continue;
					case BINARY:
						if (!isBinaryDigit(c))
							return unexpectedCharacter(c);
						whole = true;
						pushChar();
						continue;
				}
			}

			return buildLiteral();
		}
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:821
   */
  

		private Literal parseFractionPart() {
			floating = true;

			// current char is the decimal period
			pushChar();

			// while we have at least one more character/digit
			while (have(1)) {
				char c = peek(0);
				switch (c) {
					case '_':
						if (misplacedUnderscore())
							return syntaxError("misplaced underscore - underscores may only "+
									"be used as separators within sequences of valid digits");
						skip(1);
						continue;
					case '.':
						return syntaxError("multiple decimal periods are not allowed");
				}

				if (kind == DECIMAL) {
					if (c == 'e' || c == 'E') {
						return parseExponentPart();

					} else if (c == 'f' || c == 'F') {
						if (have(2))
							return syntaxError("extra digits/characters "+
									"after type suffix "+c);
						floating = true;
						isFloat = true;
						skip(1);
					} else if (c == 'd' || c == 'D') {
						if (have(2))
							return syntaxError("extra digits/characters "+
									"after type suffix "+c);
						floating = true;
						skip(1);
					} else {
						if (!isDecimalDigit(c))
							return unexpectedCharacter(c);
						pushChar();
						fraction = true;
					}
				} else { // kind == HEXADECIMAL
					if (c == 'p' || c == 'P')
						return parseExponentPart();

					if (!isHexadecimalDigit(c))
						return unexpectedCharacter(c);
					fraction = true;
					pushChar();
				}
			}

			return buildLiteral();
		}
  /**
   * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:878
   */
  

		private Literal parseExponentPart() {
			floating = true;

			// current char is the exponent specifier char
			pushChar();

			// exponent sign
			if (have(1) && (peek(0) == '+' || peek(0) == '-'))
				pushChar();

			// while we have at least one more character/digit
			while (have(1)) {
				char c = peek(0);
				switch (c) {
					case '_':
						if (misplacedUnderscore())
							return syntaxError("misplaced underscore - underscores may only "+
									"be used as separators within sequences of valid digits");
						skip(1);
						continue;
					case '-':
					case '+':
						return syntaxError("exponent sign character is only allowed as "+
								"the first character of the exponent part of a "+
								"floating point literal");
					case '.':
						return syntaxError("multiple decimal periods are not allowed");
					case 'p':
					case 'P':
						return syntaxError("multiple exponent specifiers are not allowed");
					case 'f':
					case 'F':
						isFloat = true;
					case 'd':
					case 'D':
						if (have(2))
							return syntaxError("extra digits/characters "+
									"after type suffix "+c);
						skip(1);
						continue;
				}

				// exponent is a signed integer
				if (!isDecimalDigit(c))
					return unexpectedCharacter(c);
				pushChar();
				exponent = true;
			}

			return buildLiteral();
		}
  /**
   * @ast method 
   * 
   */
  public NumericLiteral() {
    super();


  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @ast method 
   * 
   */
  public void init$Children() {
  }
  /**
   * @ast method 
   * 
   */
  public NumericLiteral(String p0) {
    setLITERAL(p0);
  }
  /**
   * @ast method 
   * 
   */
  public NumericLiteral(beaver.Symbol p0) {
    setLITERAL(p0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 0;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * Replaces the lexeme LITERAL.
   * @param value The new value for the lexeme LITERAL.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setLITERAL(String value) {
    tokenString_LITERAL = value;
  }
  /**
   * JastAdd-internal setter for lexeme LITERAL using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setLITERAL(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setLITERAL is only valid for String lexemes");
    tokenString_LITERAL = (String)symbol.value;
    LITERALstart = symbol.getStart();
    LITERALend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme LITERAL.
   * @return The value for the lexeme LITERAL.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public String getLITERAL() {
    return tokenString_LITERAL != null ? tokenString_LITERAL : "";
  }
  /**
	 * This is a refactored version of Literal.parseLong which supports
	 * binary literals. This version of parseLong is implemented as an
	 * attribute rather than a static method. Perhaps some slight
	 * performance boost could be gained from keeping it static, but with
	 * the loss of declarative- and ReRAGness.
	 *
	 * There exists only a parseLong, and not a parseInteger. Parsing
	 * of regular integer literals works the same, but with stricter
	 * bounds requirements on the resulting parsed value.
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:212
   */
  public long parseLong() {
    ASTNode$State state = state();
    try {
		switch (getKind()) {
			case HEXADECIMAL:
				return parseLongHexadecimal();
			case OCTAL:
				return parseLongOctal();
			case BINARY:
				return parseLongBinary();
			case DECIMAL:
			default:
				return parseLongDecimal();
		}
	}
    finally {
    }
  }
  /**
	 * Parse a hexadecimal long literal.
	 *
	 * @throws NumberFormatException if the literal is too large.
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:231
   */
  public long parseLongHexadecimal() {
    ASTNode$State state = state();
    try {
		long val = 0;
		if (digits.length() > 16) {
			for (int i = 0; i < digits.length()-16; i++)
				if (digits.charAt(i) != '0')
					throw new NumberFormatException("");
		}
		for (int i = 0; i < digits.length(); i++) {
			int c = digits.charAt(i);
			if (c >= 'a' && c <= 'f')
				c = c - 'a' + 10;
			else
				c = c - '0';
			val = val * 16 + c;
		}
		return val;
	}
    finally {
    }
  }
  /**
	 * Parse an octal long literal.
	 *
	 * @throws NumberFormatException if the literal is too large.
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:254
   */
  public long parseLongOctal() {
    ASTNode$State state = state();
    try {
		long val = 0;
		if (digits.length() > 21) {
			for (int i = 0; i < digits.length() - 21; i++)
				if (i == digits.length() - 21 - 1) {
					if(digits.charAt(i) != '0' && digits.charAt(i) != '1')
						throw new NumberFormatException("");
				} else {
					if(digits.charAt(i) != '0')
						throw new NumberFormatException("");
				}
		}
		for (int i = 0; i < digits.length(); i++) {
			int c = digits.charAt(i) - '0';
			val = val * 8 + c;
		}
		return val;
	}
    finally {
    }
  }
  /**
	 * Parse a binary long literal.
	 *
	 * @throws NumberFormatException if the literal is too large.
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:278
   */
  public long parseLongBinary() {
    ASTNode$State state = state();
    try {
		long val = 0;
		if (digits.length() > 64) {
			for (int i = 0; i < digits.length()-64; i++)
				if (digits.charAt(i) != '0')
					throw new NumberFormatException("");
		}
		for (int i = 0; i < digits.length(); ++i) {
			if (digits.charAt(i) == '1')
				val |= 1L << (digits.length()-i-1);
		}
		return val;
	}
    finally {
    }
  }
  /**
	 * Parse an octal long literal.
	 * @throws NumberFormatException if the literal is too large.
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:296
   */
  public long parseLongDecimal() {
    ASTNode$State state = state();
    try {
		long val = 0;
		long prev = 0;
		for (int i = 0; i < digits.length(); i++) {
			prev = val;
			int c = digits.charAt(i);
			if(c >= '0' && c <= '9')
				c = c - '0';
			else
				throw new NumberFormatException("");
			val = val * 10 + c;
			if (val < prev) {
				boolean negMinValue = i == (digits.length()-1) &&
					isNegative() && val == Long.MIN_VALUE;
				if (!negMinValue)
					throw new NumberFormatException("");
			}
		}
		if (val == Long.MIN_VALUE)
			return val;
		if (val < 0)
			throw new NumberFormatException("");
		return isNegative() ? -val : val;
	}
    finally {
    }
  }
  /**
	 * Utility attribute for literal rewriting.
	 * Any of the NumericLiteral subclasses have already
	 * been rewritten and/or parsed, and should not be
	 * rewritten again.
	 *
	 * @return true if this literal is a "raw", not-yet-parsed NumericLiteral
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:334
   */
  public boolean needsRewrite() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:371
   */
  public boolean isNegative() {
    ASTNode$State state = state();
    try {  return getLITERAL().charAt(0) == '-';  }
    finally {
    }
  }
  /**
	 * Get the trimmed digits of this literal, excluding
	 * underscore, prefix and suffix.
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:377
   */
  public String getDigits() {
    ASTNode$State state = state();
    try {  return digits;  }
    finally {
    }
  }
  /**
	 * The literal kind tells which kind of literal it is;
	 * it's either a DECIMAL, HEXADECIMAL, OCTAL or BINARY literal.
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:395
   */
  public int getKind() {
    ASTNode$State state = state();
    try {  return kind;  }
    finally {
    }
  }
  /**
	 * Get the radix of this literal.
	 * @return 16 (hex), 10 (decimal), 8 (octal) or 2 (binary)
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:414
   */
  public int getRadix() {
    ASTNode$State state = state();
    try {
		switch (kind) {
			case HEXADECIMAL:
				return 16;
			case OCTAL:
				return 8;
			case BINARY:
				return 2;
			case DECIMAL:
			default:
				return 10;
		}
	}
    finally {
    }
  }
  /**
	 * @return true if the literal is a decimal literal
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:431
   */
  public boolean isDecimal() {
    ASTNode$State state = state();
    try {  return kind == DECIMAL;  }
    finally {
    }
  }
  /**
	 * @return true if the literal is a hexadecimal literal
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:436
   */
  public boolean isHex() {
    ASTNode$State state = state();
    try {  return kind == HEXADECIMAL;  }
    finally {
    }
  }
  /**
	 * @return true if the literal is an octal literal
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:441
   */
  public boolean isOctal() {
    ASTNode$State state = state();
    try {  return kind == OCTAL;  }
    finally {
    }
  }
  /**
	 * @return true if the literal is a binary literal
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:446
   */
  public boolean isBinary() {
    ASTNode$State state = state();
    try {  return kind == BINARY;  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean type_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl type_value;
  /**
	 * The type of a NumericLiteral is undefined.
	 * The literal must be parsed before it can have a type.
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:463
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
    if(type_computed) {
      return type_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    type_value = type_compute();
      if(isFinal && num == state().boundariesCrossed) type_computed = true;
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return unknownType();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag at line 365
    if(needsRewrite()) {
      state().duringLiterals++;
      ASTNode result = rewriteRule0();
      state().duringLiterals--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:365
   * @apilevel internal
   */  private Literal rewriteRule0() {
{
			return parse();
		}  }
}
