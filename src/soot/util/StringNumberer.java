/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.util;
import java.util.*;

/** A class that numbers strings, so they can be placed in bitsets.
 *
 * @author Ondrej Lhotak
 */

public class StringNumberer extends ArrayNumberer<NumberedString> {
    HashMap<String, NumberedString> stringToNumbered = new HashMap<String, NumberedString>(1024);

    public NumberedString find( String s ) {
        NumberedString ret = stringToNumbered.get( s );
        if( ret == null ) {
            stringToNumbered.put( s, ret = new NumberedString(s) );
            add( ret );
        }
        return ret;
    }
    public NumberedString findOrAdd( String s ) {
        NumberedString ret = stringToNumbered.get( s );
        if( ret == null ) {
            stringToNumbered.put( s, ret = new NumberedString(s) );
            add( ret );
        }
        return ret;
    }
}
