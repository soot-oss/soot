/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 * 
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 * 
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

package soot.dexpler;


import org.jf.dexlib.TypeIdItem;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.Type;
import soot.UnknownType;
import soot.VoidType;


/**
 * Wrapper for a dexlib TypeIdItem.
 *
 */
public class DexType {

    protected String name;

    protected TypeIdItem type;

    public DexType(TypeIdItem type) {
        this.type = type;
        this.name = type.getConciseIdentity();
    }

    public String getName() {
        return name;
    }

    public boolean overwriteEquivalent(DexType field) {
        return name.equals(field.getName());
    }

    public TypeIdItem getType() {
        return type;
    }

    /**
     * Return the appropriate Soot Type for this DexType.
     *
     * @return the Soot Type
     */
    public Type toSoot() {
        return toSoot(type.getTypeDescriptor(), 0);
    }

    /**
     * Return the appropriate Soot Type for the given TypeIdItem.
     *
     * @param type the TypeIdItem to convert
     * @return the Soot Type
     */
    public static Type toSoot(TypeIdItem type) {
        return toSoot(type.getTypeDescriptor(), 0);
    }

    /**
     * Return if the given TypeIdItem is wide (i.e. occupies 2 registers).
     *
     * @param type the TypeIdItem to analyze
     * @return if type is wide
     */
    public static boolean isWide(TypeIdItem type) {
        String t = type.getTypeDescriptor();
        return t.startsWith("J") || t.startsWith("D");
    }

    /**
     * Determine the soot type from a byte code type descriptor.
     *
     */
    private static Type toSoot(String typeDescriptor, int pos) {
        Type type;
        char typeDesignator = typeDescriptor.charAt(pos);
        // see https://code.google.com/p/smali/wiki/TypesMethodsAndFields
        switch (typeDesignator) {
        case 'Z':               // boolean
            type = BooleanType.v();
            break;
        case 'B':               // byte
            type = ByteType.v();
            break;
        case 'S':               // short
            type = ShortType.v();
            break;
        case 'C':               // char
            type = CharType.v();
            break;
        case 'I':               // int
            type = IntType.v();
            break;
        case 'J':               // long
            type = LongType.v();
            break;
        case 'F':               // float
            type = FloatType.v();
            break;
        case 'D':               // double
            type = DoubleType.v();
            break;
        case 'L':               // object
            type = RefType.v(Util.dottedClassName(typeDescriptor));
            break;
        case 'V':               // void
            type = VoidType.v();
            break;
        case '[':               // array
            type = toSoot(typeDescriptor, pos + 1).makeArrayType();
            break;
        default:
            type = UnknownType.v();
        }

        return type;
    }
    
    @Override
    public String toString() {
    	return name;
    }
}
