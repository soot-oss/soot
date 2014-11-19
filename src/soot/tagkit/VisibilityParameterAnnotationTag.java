/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Jennifer Lhotak
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

package soot.tagkit;
import java.util.*;

/** Represents the visibility of an annotation attribute attatched 
 * to a class, field, method or method param (only one of these each)
 * has one or more annotations
 * for Java 1.5.
 */

public class VisibilityParameterAnnotationTag implements  Tag
{
    
    private int num_params;
    private int kind;
    private ArrayList<VisibilityAnnotationTag> visibilityAnnotations;
    
    public VisibilityParameterAnnotationTag(int num, int kind){
        this.num_params = num;
        this.kind = kind;
    }
    
    // should also print here number of annotations and perhaps the annotations themselves
    public String toString() {
        StringBuffer sb = new StringBuffer("Visibility Param Annotation: num params: "+num_params+" kind: "+kind);
        if (visibilityAnnotations != null){
            for (VisibilityAnnotationTag tag : visibilityAnnotations) {
            	sb.append("\n");
            	if (tag != null)
	                sb.append(tag.toString());
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    /** Returns the tag name. */
    public String getName() {
        return "VisibilityParameterAnnotationTag";
    }

    public String getInfo(){
        return "VisibilityParameterAnnotation";
    }
    
    /** Returns the tag raw data. */
    public byte[] getValue() {
        throw new RuntimeException( "VisibilityParameterAnnotationTag has no value for bytecode" );
    }

    public void addVisibilityAnnotation(VisibilityAnnotationTag a){
        if (visibilityAnnotations == null){
            visibilityAnnotations = new ArrayList<VisibilityAnnotationTag>();
        }
        visibilityAnnotations.add(a);
    }

    public ArrayList<VisibilityAnnotationTag> getVisibilityAnnotations(){
        return visibilityAnnotations;
    }

    public int getKind(){
        return kind;
    }
}

