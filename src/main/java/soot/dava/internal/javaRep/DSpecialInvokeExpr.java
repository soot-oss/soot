/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
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

package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.grimp.*;
import soot.grimp.internal.*;

public class DSpecialInvokeExpr extends GSpecialInvokeExpr {
	public DSpecialInvokeExpr(Value base, SootMethodRef methodRef, java.util.List args) {
		super(base, methodRef, args);
	}

	public void toString(UnitPrinter up) {
		if (getBase().getType() instanceof NullType) {
			// OL: I don't know what this is for; I'm just refactoring the
			// original code. An explanation here would be welcome.
			up.literal("((");
			up.type(methodRef.declaringClass().getType());
			up.literal(") ");

			if (PrecedenceTest.needsBrackets(baseBox, this))
				up.literal("(");
			baseBox.toString(up);
			if (PrecedenceTest.needsBrackets(baseBox, this))
				up.literal(")");

			up.literal(")");
			up.literal(".");

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
		} else {
			super.toString(up);
		}
	}

	public String toString() {
		if (getBase().getType() instanceof NullType) {
			StringBuffer b = new StringBuffer();

			b.append("((");
			b.append(methodRef.declaringClass().getJavaStyleName());
			b.append(") ");

			String baseStr = (getBase()).toString();
			if ((getBase() instanceof Precedence) && (((Precedence) getBase()).getPrecedence() < getPrecedence()))
				baseStr = "(" + baseStr + ")";

			b.append(baseStr);
			b.append(").");

			b.append(methodRef.name());
			b.append("(");

			if (argBoxes != null) {
				for (int i = 0; i < argBoxes.length; i++) {
					if (i != 0)
						b.append(", ");
	
					b.append((argBoxes[i].getValue()).toString());
				}
			}

			b.append(")");

			return b.toString();
		}

		return super.toString();
	}

	public Object clone() {
		ArrayList clonedArgs = new ArrayList(getArgCount());

		for (int i = 0; i < getArgCount(); i++)
			clonedArgs.add(i, Grimp.cloneIfNecessary(getArg(i)));

		return new DSpecialInvokeExpr(getBase(), methodRef, clonedArgs);
	}
}
