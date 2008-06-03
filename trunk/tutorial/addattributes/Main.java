/* Soot - a J*va Optimization and Annotation Framework
 * Copyright (C) 1997-2000 Raja Vallee-Rai
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

/* This example is writen by Feng Qian.
 */

package ashes.examples.addattributes;

import soot.*;
import soot.tagkit.*;
import java.util.*;


/** Annotation example, adds a "Hello World!" string
 *  as method attribute to each method.
 */ 

public class Main
{
    public static void main(String[] args)
    {
        /* adds the transformer. */
        PackManager.v().getPack("jtp").add(new
                        Transform("annotexample",
                        AnnExampleWrapper.v()));

        /* invokes Soot */
        soot.Main.main(args);
    }
}


class AnnExampleWrapper extends BodyTransformer
{
    private static AnnExampleWrapper instance =
        new AnnExampleWrapper();

    private AnnExampleWrapper() {};

    public static AnnExampleWrapper v()
    {
        return instance;
    }

    public void internalTransform(Body body, String phaseName, Map options)
    {
        SootMethod method = body.getMethod();
        String attr = new String("Hello world!");
        
        Tag example = new GenericAttribute("Example", attr.getBytes());
        method.addTag(example);
    }
}
