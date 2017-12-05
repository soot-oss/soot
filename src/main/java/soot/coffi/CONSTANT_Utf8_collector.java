/* Soot - a J*va Optimization Framework
 * Copyright (C) 2001 Michael Pan (pan@math.tau.ac.il)
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
 * Modified by the Sable Research Group and others 2001.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.coffi;
import soot.*;
import java.util.*;

/** Provides sharing for Utf8_info string objects 
 * reused in different contexts. */

public class CONSTANT_Utf8_collector 
{
    public CONSTANT_Utf8_collector( Singletons.Global g ) {}
    public static CONSTANT_Utf8_collector v() { return G.v().soot_coffi_CONSTANT_Utf8_collector(); }
    HashMap<String, CONSTANT_Utf8_info> hash = null;

    synchronized CONSTANT_Utf8_info add(CONSTANT_Utf8_info _Utf8_info) 
    {
        if (hash == null) 
        {
            hash = new HashMap<String, CONSTANT_Utf8_info>();
        }

        String Utf8_str_key = _Utf8_info.convert();
        if (hash.containsKey(Utf8_str_key)) 
        {
            return hash.get(Utf8_str_key);
        }
        hash.put(Utf8_str_key, _Utf8_info);
        _Utf8_info.fixConversion(Utf8_str_key);
        return _Utf8_info;
    }
}
