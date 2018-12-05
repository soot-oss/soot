package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Eric Bodden
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

import java.util.HashMap;
import java.util.Map;

public class TryCatchTag implements soot.tagkit.Tag {

  public static final String NAME = "TryCatchTag";

  protected Map<soot.Unit, soot.Unit> handlerUnitToFallThroughUnit = new HashMap<soot.Unit, soot.Unit>();

  public void register(soot.Unit handler, soot.Unit fallThrough) {
    handlerUnitToFallThroughUnit.put(handler, fallThrough);
  }

  public soot.Unit getFallThroughUnitOf(soot.Unit handlerUnit) {
    return handlerUnitToFallThroughUnit.get(handlerUnit);
  }

  public String getName() {
    return NAME;
  }

  public byte[] getValue() throws AttributeValueException {
    throw new UnsupportedOperationException();
  }

}