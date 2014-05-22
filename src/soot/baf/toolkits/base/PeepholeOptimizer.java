/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.baf.toolkits.base;

import java.util.*;
import java.io.*;

import soot.*;

/**
 *   Driver class to run peepholes on the Baf IR. The peepholes applied
 *   must implement the Peephole interface. Peepholes are loaded dynamically
 *   by the soot runtime; the runtime reads the file peephole.dat, in order to
 *   determine which peepholes to apply.
 *  
 *   @see Peephole
 *   @see ExamplePeephole
 */

public class PeepholeOptimizer extends BodyTransformer
{
    public PeepholeOptimizer( Singletons.Global g ) {}
    public static PeepholeOptimizer v() { return G.v().soot_baf_toolkits_base_PeepholeOptimizer(); }

    private InputStream peepholeListingStream = null;
    private final String packageName = "soot.baf.toolkits.base";

    private final Map<String, Class<?>> peepholeMap = new HashMap<String, Class<?>>();

    /** The method that drives the optimizations. */
    /* This is the public interface to PeepholeOptimizer */
  
    protected void internalTransform(Body body, String phaseName, Map<String,String> options) 
    {           
        boolean changed = true;
        BufferedReader reader = null;
        
        peepholeListingStream = PeepholeOptimizer.class.getResourceAsStream("peephole.dat");
	if (peepholeListingStream == null)
	    throw new RuntimeException("could not open file peephole.dat!");
        reader = new BufferedReader(new InputStreamReader(peepholeListingStream));        

        String line = null;
        List<String> peepholes = new LinkedList<String>();
        try {
            line = reader.readLine();
            while(line != null) {
                if(line.length() > 0)
                    if(!(line.charAt(0) == '#'))
                        peepholes.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("IO error occured while reading file:  " +
                                       line + System.getProperty("line.separator") + e);
        }

        
        while(changed) {
            changed = false;           

            Iterator<String>  it = peepholes.iterator();
            while(it.hasNext()) {
                
                boolean peepholeWorked = true;
                String peepholeName = it.next();
                
                while(peepholeWorked) {
                    peepholeWorked = false;

                
                    Class<?> peepholeClass;
                    if((peepholeClass = peepholeMap.get(peepholeName)) == null) {
                        try {
                            peepholeClass =  Class.forName(packageName + "." + peepholeName);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e.toString());
                        }
                        peepholeMap.put(peepholeName, peepholeClass);
                    }
                    
                    Peephole p = null;
                    try {
                        p = (Peephole) peepholeClass.newInstance();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e.toString());
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e.toString());
                    }
                    if(p.apply(body)) {
                        peepholeWorked = true;
                        changed = true;
                    }
                }
            }
        }
        try
        {
            peepholeListingStream.close();
        }
        catch (IOException e)
            {}
    }
}
