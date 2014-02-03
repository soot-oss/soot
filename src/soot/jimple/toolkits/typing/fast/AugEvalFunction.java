/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Ben Bellamy 
 * 
 * All rights reserved.
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
package soot.jimple.toolkits.typing.fast;

import java.util.*;
import soot.*;
import soot.jimple.*;

/**
 * @author Ben Bellamy
 */
public class AugEvalFunction implements IEvalFunction
{
	private JimpleBody jb;
	
	public AugEvalFunction(JimpleBody jb)
	{
		this.jb = jb;
	}
	
	public Collection<Type> eval(Typing tg, Value expr, Stmt stmt)
	{
		return Collections.<Type>singletonList(eval_(tg, expr, stmt, this.jb));
	}

	public static Type eval_(Typing tg, Value expr, Stmt stmt, JimpleBody jb)
	{
		
		if ( expr instanceof ThisRef )
			return ((ThisRef)expr).getType();
		else if ( expr instanceof ParameterRef )
			return ((ParameterRef)expr).getType();
		else if ( expr instanceof Local ) {
			Local ex = (Local) expr;
			//changed to prevent null pointer exception in case of phantom classes where a null typing is encountered
			//syed 
			if (tg == null) return null; 
			else return tg.get(ex);
		}
		else if ( expr instanceof BinopExpr )
		{
			BinopExpr be = (BinopExpr)expr;
			
			Value opl = be.getOp1(), opr = be.getOp2();
			Type tl = eval_(tg, opl, stmt, jb), tr = eval_(tg, opr, stmt, jb);

			if ( expr instanceof CmpExpr
				|| expr instanceof CmpgExpr
				|| expr instanceof CmplExpr )
				return ByteType.v();
			else if ( expr instanceof GeExpr
				|| expr instanceof GtExpr
				|| expr instanceof LeExpr
				|| expr instanceof LtExpr
				|| expr instanceof EqExpr
				|| expr instanceof NeExpr )
				return BooleanType.v();
			else if ( expr instanceof ShlExpr )
			{
				if ( tl instanceof IntegerType )
					return IntType.v();
				else return tl;
			}
			else if ( expr instanceof ShrExpr
				|| expr instanceof UshrExpr )
				return tl;
			else if ( expr instanceof AddExpr
				|| expr instanceof SubExpr
				|| expr instanceof MulExpr
				|| expr instanceof DivExpr
				|| expr instanceof RemExpr )
			{
				if ( tl instanceof IntegerType )
					return IntType.v();
				else return tl;
			}
			else if ( expr instanceof AndExpr
				|| expr instanceof OrExpr
				|| expr instanceof XorExpr )
			{
				if ( tl instanceof IntegerType && tr instanceof IntegerType )
				{
					if ( tl instanceof BooleanType )
					{
						if ( tr instanceof BooleanType )
							return BooleanType.v();
						else return tr;
					}
					else if ( tr instanceof BooleanType )
						return tl;
					else
					{
						Collection<Type> rs = AugHierarchy.lcas_(tl, tr);
						// AugHierarchy.lcas_ is single-valued
						for ( Type r : rs )
							return r;
						throw new RuntimeException();
					}
				}
				else return tl;
			}
			else throw new RuntimeException(
					"Unhandled binary expression: " + expr);
		}
		else if ( expr instanceof NegExpr )
		{
			Type t = eval_(tg, ((NegExpr)expr).getOp(), stmt, jb);
			if ( t instanceof IntegerType )
			{
				/* Here I repeat the behaviour of the original type assigner,
				but is it right? For example, -128 is a byte, but -(-128) is
				not! --BRB */
				if ( t instanceof Integer1Type
					|| t instanceof BooleanType
					|| t instanceof Integer127Type
					|| t instanceof ByteType )
					return ByteType.v();
				else if ( t instanceof ShortType
					|| t instanceof Integer32767Type )
					return ShortType.v();
				else
					return IntType.v();
			}
			else
				return t;
		}
		else if ( expr instanceof CaughtExceptionRef )
		{
			RefType r = null;
			
			for (RefType t : TrapManager.getExceptionTypesOf(stmt, jb))
			{
				if ( r == null )
					r = t;
				else
					/* In theory, we could have multiple exception types 
					pointing here. The JLS requires the exception parameter be a *subclass* of Throwable, so we do not need to worry about multiple inheritance. */
					r = BytecodeHierarchy.lcsc(r, t);
			}
			
			if ( r == null )
				throw new RuntimeException(
					"Exception reference used other than as the first "
					+ "statement of an exception handler.");
				
			return r;
		}
		else if ( expr instanceof ArrayRef )
		{
			Local av = (Local)((ArrayRef)expr).getBase();
			Type at = tg.get(av);
			
			if ( at instanceof ArrayType )
				return ((ArrayType)at).getElementType();
			else
				return BottomType.v();
		}
		else if ( expr instanceof NewArrayExpr )
			return ((NewArrayExpr)expr).getBaseType().makeArrayType();
		else if ( expr instanceof NewMultiArrayExpr )
			return ((NewMultiArrayExpr)expr).getBaseType();
		else if ( expr instanceof CastExpr )
			return ((CastExpr)expr).getCastType();
		else if ( expr instanceof InstanceOfExpr )
			return BooleanType.v();
		else if ( expr instanceof LengthExpr )
			return IntType.v();
		else if ( expr instanceof InvokeExpr )
			return ((InvokeExpr)expr).getMethodRef().returnType();
		else if ( expr instanceof NewExpr )
			return ((NewExpr)expr).getBaseType();
		else if ( expr instanceof FieldRef )
			return ((FieldRef)expr).getType();
		else if ( expr instanceof DoubleConstant )
			return DoubleType.v();
		else if ( expr instanceof FloatConstant )
			return FloatType.v();
		else if ( expr instanceof IntConstant )
		{
			int value = ((IntConstant)expr).value;
		
			if ( value >= 0 && value < 2 )
				return Integer1Type.v();
			else if ( value >= 2 && value < 128 )
				return Integer127Type.v();
			else if ( value >= -128 && value < 0 )
				return ByteType.v();
			else if ( value >= 128 && value < 32768 )
				return Integer32767Type.v();
			else if ( value >= -32768 && value < -128 )
				return ShortType.v();
			else if ( value >= 32768 && value < 65536 )
				return CharType.v();
			else return IntType.v();
		}
		else if ( expr instanceof LongConstant )
			return LongType.v();
		else if ( expr instanceof NullConstant )
			return NullType.v();
		else if ( expr instanceof StringConstant )
			return RefType.v("java.lang.String");
		else if ( expr instanceof ClassConstant )
			return RefType.v("java.lang.Class");
		else throw new RuntimeException("Unhandled expression: " + expr);
	}
}