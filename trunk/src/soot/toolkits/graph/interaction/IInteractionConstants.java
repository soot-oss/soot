/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.toolkits.graph.interaction;

public interface IInteractionConstants{

    public static final int NEW_ANALYSIS = 0;
    public static final int WANT_ANALYSIS = 1;
    public static final int NEW_CFG = 2;
    public static final int CONTINUE = 3;
    public static final int NEW_BEFORE_ANALYSIS_INFO = 4;
    public static final int NEW_AFTER_ANALYSIS_INFO = 5;
    public static final int DONE = 6;
    public static final int FORWARDS = 7;
    public static final int BACKWARDS = 8;
    public static final int CLEARTO = 9;
    public static final int REPLACE = 10;
    public static final int NEW_BEFORE_ANALYSIS_INFO_AUTO = 11;
    public static final int NEW_AFTER_ANALYSIS_INFO_AUTO = 12;
    public static final int STOP_AT_NODE = 13;
    
    public static final int CALL_GRAPH_START = 50;
    public static final int CALL_GRAPH_NEXT_METHOD = 51;
    public static final int CALL_GRAPH_PART = 52;
    public static final int CALL_GRAPH_DONE = 53;    
}
