/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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



package soot;

/** Describes a switch on internal types. */
interface ITypeSwitch extends soot.util.Switch
{
    void caseArrayType(ArrayType t);
    void caseBooleanType(BooleanType t);
    void caseByteType(ByteType t);
    void caseCharType(CharType t);
    void caseDoubleType(DoubleType t);
    void caseFloatType(FloatType t);
    void caseIntType(IntType t);
    void caseLongType(LongType t);
    void caseRefType(RefType t);
    void caseShortType(ShortType t);
    void caseStmtAddressType(StmtAddressType t);
    void caseUnknownType(UnknownType t);
    void caseVoidType(VoidType t);
    void caseAnySubType(AnySubType t);
    void caseNullType(NullType t);
    void caseErroneousType(ErroneousType t);
    void caseDefault(Type t);
}
