/* Soot - a J*va Optimization Framework
 * Copyright (C) 2010 Hela Oueslati, Eric Bodden
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
package soot.sootify;

import soot.AnySubType;
import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.ErroneousType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.NullType;
import soot.RefType;
import soot.ShortType;
import soot.StmtAddressType;
import soot.Type;
import soot.TypeSwitch;
import soot.UnknownType;
import soot.VoidType;

public class TypeTemplatePrinter extends TypeSwitch {

	private String varName;
	private final TemplatePrinter p;
	
	public void printAssign(String v, Type t) {
		String oldName = varName;
		varName = v;
		t.apply(this);
		varName = oldName;
	}
	
	public TypeTemplatePrinter(TemplatePrinter p) {
		this.p = p;
	}

	public void setVariableName(String name) {
		this.varName = name;		
	}
	
	private void emit(String rhs) {
		p.println("Type "+ varName+" = "+rhs+";");
	}

	private void emitSpecial(String type, String rhs) {
		p.println(type+" "+ varName+" = "+rhs+";");
	}

	public void caseAnySubType(AnySubType t) {
		throw new IllegalArgumentException("cannot print this type");
	}

	public void caseArrayType(ArrayType t) {
		printAssign("baseType", t.getElementType());

		p.println("int numDimensions=" + t.numDimensions+ ";");
		
		emit("ArrayType.v(baseType, numDimensions)");
	}


	public void caseBooleanType(BooleanType t) {
		emit("BooleanType.v()");
	}

	public void caseByteType(ByteType t) {
		emit("ByteType.v()");
	}

	public void caseCharType(CharType t) {
		emit("CharType.v()");
	}

	public void caseDefault(Type t) {
		throw new IllegalArgumentException("cannot print this type");		
	}

	public void caseDoubleType(DoubleType t) {
		emit("DoubleType.v()");
	}

	public void caseErroneousType(ErroneousType t) {
		throw new IllegalArgumentException("cannot print this type");		
	}

	public void caseFloatType(FloatType t) {
		emit("FloatType.v()");
	}

	public void caseIntType(IntType t) {
		emit("IntType.v()");
	}

	public void caseLongType(LongType t) {
		emit("LongType.v()");
	}

	public void caseNullType(NullType t) {
		emit("NullType.v()");
	}

	public void caseRefType(RefType t) {
		emitSpecial("RefType","RefType.v(\""+t.getClassName()+"\")");
	}

	public void caseShortType(ShortType t) {
		emit("ShortType.v()");
	}

	public void caseStmtAddressType(StmtAddressType t) {
		throw new IllegalArgumentException("cannot print this type");		
	}

	public void caseUnknownType(UnknownType t) {
		throw new IllegalArgumentException("cannot print this type");		
	}

	public void caseVoidType(VoidType t) {
		emit("VoidType.v()");
	}

}
