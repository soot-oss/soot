package soot.dotnet.members;

import static soot.dotnet.specifications.DotnetModifier.toSootModifier;

import java.math.BigDecimal;

import soot.BooleanConstant;
import soot.ByteConstant;
import soot.Scene;
import soot.ShortConstant;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import soot.SootField;
import soot.Type;
import soot.UByteConstant;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.UIntConstant;
import soot.jimple.ULongConstant;
import soot.tagkit.DecimalConstantValueTag;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;

/**
 * Represents a .NET Field
 */
public class DotnetField extends AbstractDotnetMember {

  private final ProtoAssemblyAllTypes.FieldDefinition protoField;

  public DotnetField(ProtoAssemblyAllTypes.FieldDefinition protoField) {
    this.protoField = protoField;
  }

  public SootField makeSootField() {
    int modifier = toSootModifier(protoField);
    Type type = DotnetTypeFactory.toSootType(protoField.getType());
    String name = protoField.getName();

    SootField mf = Scene.v().makeSootField(name, type, modifier);
    if (protoField.getInitialValue() != null && !protoField.getInitialValue().isEmpty()) {
      mf.addTag(new InitialFieldTagValue(protoField.getInitialValue().toByteArray()));
    }
    switch (protoField.getConstantType()) {
      case type_unknown:
        break;
      default:
      case UNRECOGNIZED:
        throw new RuntimeException("Unsupported: " + protoField.getConstantType());
      case type_double:
        mf.addTag(new DoubleConstantValueTag(protoField.getValueConstantDouble()));
        break;
      case type_float:
        mf.addTag(new FloatConstantValueTag(protoField.getValueConstantFloat()));
        break;
      case type_int32:
        mf.addTag(new IntegerConstantValueTag((int) protoField.getValueConstantInt64()));
        break;
      case type_int64:
        mf.addTag(new LongConstantValueTag(protoField.getValueConstantInt64()));
        break;
      case type_string:
        mf.addTag(new StringConstantValueTag(protoField.getValueConstantString()));
        break;
      case type_bool:
        mf.addTag(new IntegerConstantValueTag(BooleanConstant.v(protoField.getValueConstantBool() ? 1 : 0)));
        break;
      case type_sbyte:
        mf.addTag(new IntegerConstantValueTag(ByteConstant.v((byte) protoField.getValueConstantInt64())));
        break;
      case type_byte:
        mf.addTag(new IntegerConstantValueTag(UByteConstant.v((int) protoField.getValueConstantInt64())));
        break;
      case type_uint16:
        mf.addTag(new IntegerConstantValueTag(ShortConstant.v((short) protoField.getValueConstantInt64())));
        break;
      case type_int16:
        mf.addTag(new IntegerConstantValueTag(ShortConstant.v((short) protoField.getValueConstantInt64())));
        break;
      case type_char:
        mf.addTag(new IntegerConstantValueTag(ShortConstant.v((char) protoField.getValueConstantInt64())));
        break;
      case type_uint32:
        mf.addTag(new IntegerConstantValueTag(UIntConstant.v((int) protoField.getValueConstantInt64())));
        break;
      case type_uint64:
        mf.addTag(new LongConstantValueTag(ULongConstant.v(protoField.getValueConstantUint64())));
        break;
      case type_decimal:
        mf.addTag(new DecimalConstantValueTag(new BigDecimal(protoField.getValueConstantString())));
        break;

    }
    return mf;
  }
}
