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

package soot.jimple.spark.pag;
import soot.*;
import soot.Type;
import java.util.*;

/** Represents an array element.
 * @author Ondrej Lhotak
 */
public class ArrayElement implements SparkField {
    public ArrayElement( Singletons.Global g ) {}
    public static ArrayElement v() { return G.v().soot_jimple_spark_pag_ArrayElement(); }

    public ArrayElement() {
        Scene.v().getFieldNumberer().add(this);
    }

    public final int getNumber() {
        return number;
    }
    public final void setNumber(int number) {
        this.number = number;
    }
    private int number = 0;
}
