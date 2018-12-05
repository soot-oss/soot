package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2001 Michael Pan (pan@math.tau.ac.il)
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

import soot.G;
import soot.Singletons;

/**
 * Provides sharing for Utf8_info string objects reused in different contexts.
 */

public class CONSTANT_Utf8_collector {
  public CONSTANT_Utf8_collector(Singletons.Global g) {
  }

  public static CONSTANT_Utf8_collector v() {
    return G.v().soot_coffi_CONSTANT_Utf8_collector();
  }

  HashMap<String, CONSTANT_Utf8_info> hash = null;

  synchronized CONSTANT_Utf8_info add(CONSTANT_Utf8_info _Utf8_info) {
    if (hash == null) {
      hash = new HashMap<String, CONSTANT_Utf8_info>();
    }

    String Utf8_str_key = _Utf8_info.convert();
    if (hash.containsKey(Utf8_str_key)) {
      return hash.get(Utf8_str_key);
    }
    hash.put(Utf8_str_key, _Utf8_info);
    _Utf8_info.fixConversion(Utf8_str_key);
    return _Utf8_info;
  }
}
