package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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

import soot.*;
import soot.dotnet.types.DotnetBasicTypes;
import soot.options.Options;

@SuppressWarnings("serial")
public abstract class AbstractFloatBinopExpr extends AbstractBinopExpr {

  protected AbstractFloatBinopExpr(ValueBox op1Box, ValueBox op2Box) {
    super(op1Box, op2Box);
  }

  @Override
  public Type getType() {
    final Type t1 = op1Box.getValue().getType();
    final Type t2 = op2Box.getValue().getType();

    final IntType tyInt = IntType.v();
    final ByteType tyByte = ByteType.v();
    final ShortType tyShort = ShortType.v();
    final CharType tyChar = CharType.v();
    final BooleanType tyBool = BooleanType.v();
    if ((tyInt.equals(t1) || tyByte.equals(t1) || tyShort.equals(t1) || tyChar.equals(t1) || tyBool.equals(t1))
        && (tyInt.equals(t2) || tyByte.equals(t2) || tyShort.equals(t2) || tyChar.equals(t2) || tyBool.equals(t2))) {
      return tyInt;
    }
    final LongType tyLong = LongType.v();
    if (tyLong.equals(t1) || tyLong.equals(t2)) {
      return tyLong;
    }
    final DoubleType tyDouble = DoubleType.v();
    if (tyDouble.equals(t1) || tyDouble.equals(t2)) {
      return tyDouble;
    }
    final FloatType tyFloat = FloatType.v();
    if (tyFloat.equals(t1) || tyFloat.equals(t2)) {
      return tyFloat;
    }

    // in dotnet enums are value types, such as myBool = 1 is allowed in CIL
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
     if(isSuperclassSystemEnum(t1) || isSuperclassSystemEnum(t2))
    	 return tyInt;
    }
    return UnknownType.v();
}
  
  private boolean isSuperclassSystemEnum(Type t){
	  if (t instanceof RefType) {
	        SootClass sootClass = ((RefType) t).getSootClass();
	        if (sootClass != null) {
	          SootClass superclass = sootClass.getSuperclass();
	          if(superclass == null)
	        	return false;
	          if (superclass.getName().equals(DotnetBasicTypes.SYSTEM_ENUM))
	            return true;
	        }
	      }
	return false;
  }
}
