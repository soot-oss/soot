
/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

/* THIS FILE IS AUTO-GENERATED FROM soot_options.xml. DO NOT MODIFY. */

package soot.options;
import java.util.*;

/** Option parser for CFG Output Options. */
public class CFGOutputOptions
{
    private Map options;

    public CFGOutputOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    public static final int graph_type_complete_unit_graph = 1;
    public static final int graph_type_unit_graph = 2;
    public static final int graph_type_complete_block_graph = 3;
    public static final int graph_type_brief_block_graph = 4;
    public static final int graph_type_array_block_graph = 5;
    /** Graph Type --
    
     * Determines which type of graph to output.
    
     * Determines which type of graph to output based on whether nodes 
     * are units or blocks and whether control flow associated with 
     * Exceptions is taken into consideration or not.
     */
    public int graph_type() {
        String s = soot.PhaseOptions.getString( options, "graph-type" );
        
        if( s.equalsIgnoreCase( "complete-unit-graph" ) )
            return graph_type_complete_unit_graph;
        
        if( s.equalsIgnoreCase( "unit-graph" ) )
            return graph_type_unit_graph;
        
        if( s.equalsIgnoreCase( "complete-block-graph" ) )
            return graph_type_complete_block_graph;
        
        if( s.equalsIgnoreCase( "brief-block-graph" ) )
            return graph_type_brief_block_graph;
        
        if( s.equalsIgnoreCase( "array-block-graph" ) )
            return graph_type_array_block_graph;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option graph-type" );
    }
    
    public static final int output_type_dot_files = 1;
    public static final int output_type_eclipse_graphs = 2;
    /** Output Type --
    
     * Determines which type of files to generate.
    
     * Determines which type of files to generate
     */
    public int output_type() {
        String s = soot.PhaseOptions.getString( options, "output-type" );
        
        if( s.equalsIgnoreCase( "dot-files" ) )
            return output_type_dot_files;
        
        if( s.equalsIgnoreCase( "eclipse-graphs" ) )
            return output_type_eclipse_graphs;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option output-type" );
    }
    
}
        