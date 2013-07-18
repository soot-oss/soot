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


import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.value.BooleanEncodedValue;
import org.jf.dexlib2.iface.value.ByteEncodedValue;
import org.jf.dexlib2.iface.value.CharEncodedValue;
import org.jf.dexlib2.iface.value.DoubleEncodedValue;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.iface.value.FloatEncodedValue;
import org.jf.dexlib2.iface.value.IntEncodedValue;
import org.jf.dexlib2.iface.value.LongEncodedValue;
import org.jf.dexlib2.iface.value.ShortEncodedValue;
import org.jf.dexlib2.iface.value.StringEncodedValue;

import soot.Modifier;
import soot.SootField;
import soot.Type;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;

/**
 * This class represents all instance and static fields of a dex class.
 * It holds its name, its modifier, and the type
 */
public class DexField {
    private DexField() {}

    /**
     * Add constant tag. Should only be called if field is final.
     * @param df
     * @param sf
     */
    private static void addConstantTag(SootField df, Field sf) {
        Tag tag = null;

        EncodedValue ev = sf.getInitialValue();

        if (ev instanceof BooleanEncodedValue) {
          tag = new IntegerConstantValueTag(((BooleanEncodedValue) ev).getValue() ==true?1:0);
        } else if (ev instanceof ByteEncodedValue) {
          tag = new IntegerConstantValueTag(((ByteEncodedValue) ev).getValue());
        } else if (ev instanceof CharEncodedValue) {
          tag = new IntegerConstantValueTag(((CharEncodedValue) ev).getValue());
        } else if (ev instanceof DoubleEncodedValue) {
          tag = new DoubleConstantValueTag(((DoubleEncodedValue) ev).getValue());
        } else if (ev instanceof FloatEncodedValue) {
          tag = new FloatConstantValueTag(((FloatEncodedValue) ev).getValue());
        } else if (ev instanceof IntEncodedValue) {
          tag = new IntegerConstantValueTag(((IntEncodedValue) ev).getValue());
        } else if (ev instanceof LongEncodedValue) {
          tag = new LongConstantValueTag(((LongEncodedValue) ev).getValue());
        } else if (ev instanceof ShortEncodedValue) {
          tag = new IntegerConstantValueTag(((ShortEncodedValue) ev).getValue());
        } else if (ev instanceof StringEncodedValue) {
          tag = new StringConstantValueTag(((StringEncodedValue) ev).getValue());
        }

        if (tag != null)
          df.addTag(tag);
    }

    /**
     *
     * @return the Soot equivalent of a field
     */
    public static SootField makeSootField(Field f) {
        String name = f.getName();
        Type type = DexType.toSoot(f.getType());
        int flags = f.getAccessFlags();
        SootField sf = new SootField(name, type, flags);
        if (Modifier.isFinal(flags))
            DexField.addConstantTag(sf, f);
        return sf;
    }
}
