/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */






package soot.grimp;
import soot.*;
import soot.jimple.*;

/** Provides static helper methods to indicate if parenthesization is
 * required. 
 *
 * If your sub-expression has strictly higher precedence than you,
 * then no brackets are required: 2 + (4 * 5) = 2 + 4 * 5 is
 * unambiguous, because * has precedence 800 and + has precedence 700.
 *
 * If your subexpression has lower precedence than you, then
 * brackets are required; otherwise you will bind to your 
 * grandchild instead of the subexpression.  2 * (4 + 5) without
 * brackets would mean (2 * 4) + 5.
 *
 * For a binary operation, if your left sub-expression has the same
 * precedence as you, no brackets are needed, since binary operations
 * are all left-associative.  If your right sub-expression has the
 * same precedence than you, then brackets are needed to reproduce the
 * parse tree (otherwise, parsing will give e.g. (2 + 4) + 5 instead
 * of the 2 + (4 + 5) that you had to start with.)  This is OK for
 * integer addition and subtraction, but not OK for floating point
 * multiplication.  To be safe, let's put the brackets on.
 *
 * For the high-precedence operations, I've assigned precedences of
 * 950 to field reads and invoke expressions (.), as well as array reads ([]).
 * I've assigned 850 to cast, newarray and newinvoke.
 *
 * The Dava DCmp?Expr precedences look fishy to me; I've assigned DLengthExpr
 * a precedence of 950, because it looks like it should parse like a field
 * read to me.
 *
 * Basically, the only time I can see that brackets should be required 
 * seems to occur when a cast or a newarray occurs as a subexpression of
 * an invoke or field read; hence 850 and 950. -PL
 */
public class PrecedenceTest
{
    public static boolean needsBrackets( ValueBox subExprBox, Value expr ) {
        Value sub = subExprBox.getValue();
        if( !(sub instanceof Precedence) ) return false;
        Precedence subP = (Precedence) sub;
        Precedence exprP = (Precedence) expr;
        return subP.getPrecedence() < exprP.getPrecedence();
    }
    public static boolean needsBracketsRight( ValueBox subExprBox, Value expr ) {
        Value sub = subExprBox.getValue();
        if( !(sub instanceof Precedence) ) return false;
        Precedence subP = (Precedence) sub;
        Precedence exprP = (Precedence) expr;
        return subP.getPrecedence() <= exprP.getPrecedence();
    }
}
