/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.jimple.internal;

import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.util.*;

import java.util.*;

@SuppressWarnings("serial")
public abstract class AbstractStaticInvokeExpr extends AbstractInvokeExpr implements StaticInvokeExpr, ConvertToBaf {
	AbstractStaticInvokeExpr(SootMethodRef methodRef, List<Value> args) {
		this(methodRef, new ValueBox[args.size()]);

		for (int i = 0; i < args.size(); i++)
			this.argBoxes[i] = Jimple.v().newImmediateBox(args.get(i));
	}

	public boolean equivTo(Object o) {
		if (o instanceof AbstractStaticInvokeExpr) {
			AbstractStaticInvokeExpr ie = (AbstractStaticInvokeExpr) o;
			if (!(getMethod().equals(ie.getMethod())
					&& (argBoxes == null ? 0 : argBoxes.length) == (ie.argBoxes == null ? 0 : ie.argBoxes.length)))
				return false;
			if (argBoxes != null) {
				for (int i = 0; i < argBoxes.length; i++) {
					if (!(argBoxes[i]).getValue().equivTo(ie.argBoxes[i].getValue()))
						return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns a hash code for this object, consistent with structural equality.
	 */
	public int equivHashCode() {
		return getMethod().equivHashCode();
	}

	public abstract Object clone();

	protected AbstractStaticInvokeExpr(SootMethodRef methodRef, ValueBox[] argBoxes) {
		super(methodRef, argBoxes);
		if (!methodRef.isStatic())
			throw new RuntimeException("wrong static-ness");
		this.methodRef = methodRef;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(Jimple.STATICINVOKE + " " + methodRef.getSignature() + "(");

		if (argBoxes != null) {
			for (int i = 0; i < argBoxes.length; i++) {
				if (i != 0)
					buffer.append(", ");
	
				buffer.append(argBoxes[i].getValue().toString());
			}
		}

		buffer.append(")");

		return buffer.toString();
	}

	public void toString(UnitPrinter up) {
		up.literal(Jimple.STATICINVOKE);
		up.literal(" ");
		up.methodRef(methodRef);
		up.literal("(");

		if (argBoxes != null) {
			for (int i = 0; i < argBoxes.length; i++) {
				if (i != 0)
					up.literal(", ");
	
				argBoxes[i].toString(up);
			}
		}

		up.literal(")");
	}

	public void apply(Switch sw) {
		((ExprSwitch) sw).caseStaticInvokeExpr(this);
	}

	public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
		if (argBoxes != null) {
			for (ValueBox element : argBoxes) {
				((ConvertToBaf) (element.getValue())).convertToBaf(context, out);
			}
		}

		Unit u = Baf.v().newStaticInvokeInst(methodRef);
		out.add(u);
		u.addAllTagsOf(context.getCurrentUnit());
	}
}
