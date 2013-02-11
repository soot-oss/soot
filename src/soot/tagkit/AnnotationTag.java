/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Jennifer Lhotak
 * Copyright (C) 2013 Tata Consultancy Services & Ecole Polytechnique de Montreal
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
 * 
 * Modified by Marc-Andre Laverdiere-Papineau in 2013
 * 
 */
package soot.tagkit;
import java.util.*;


/** 
 * Represents the annotation attribute attached to a class, method, field,
 * method param - they could have many annotations each
 * for Java 1.5.
 */

public class AnnotationTag implements Tag
{
    
    // type - the question here is the class of the type is potentially
    // not loaded -- Does it need to be ??? - If it does then this may
    // be something difficult (if just passing the attributes through
    // then it maybe doesn't need to - but if they are runtime visible 
    // attributes then won't the annotation class need to be created
    // in the set of output classes for use by tools when using 
    // reflection ??? 

    // number of elem value pairs
    // a bunch of element value pairs
        // a type B C D F I J S Z s e c @ [
        // for B C D F I J S Z s elem is a constant value (entry to cp)
            // in Soot rep as 
        // for e elem is a type and the simple name of the enum class
            // rep in Soot as a type and SootClass or as two strings
        // for c elem is a descriptor of the class represented 
            // rep in Soot as a SootClass ?? or a string ??
        // for @ (nested annotation) 
        // for [ elem is num values and array of values 
    // should probably make a bunch of subclasses for all the 
    // different kinds - with second level for the constant kinds
 
    /**
     * The type
     */
    private String type;
    
    /**
     * The annotations
     */
    private List<AnnotationElem> elems;
    
    public AnnotationTag(String type){
      this(type, new ArrayList<AnnotationElem>());
    }
    public AnnotationTag(String type, Collection<AnnotationElem> elements){
      this.type = type;
      if (elements instanceof List<?>)
        this.elems = (List<AnnotationElem>)elements;
      else
        this.elems = new ArrayList<AnnotationElem>(elements);
    }
    
    @Deprecated
    public AnnotationTag(String type, int numElem){
      this.type = type;
      this.elems = new ArrayList<AnnotationElem>(numElem);
    }
    
    // should also print here number of annotations and perhaps the annotations themselves
    public String toString() {
        StringBuffer sb = new StringBuffer("Annotation: type: "+type+" num elems: "+elems.size()+" elems: ");
        if (elems != null){
            Iterator<AnnotationElem> it = elems.iterator();
            while (it.hasNext()){
                sb.append("\n");
                sb.append(it.next());
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    /** Returns the tag name. */
    public String getName() {
        return "AnnotationTag";
    }

    public String getInfo(){
        return "Annotation";
    }
   
    public String getType(){
        return type;
    }
    
    /**
     * @return the number of elements
     */
    @Deprecated
    public int getNumElems(){
        return elems.size();
    }
    
    /** Returns the tag raw data. */
    public byte[] getValue() {
        throw new RuntimeException( "AnnotationTag has no value for bytecode" );
    }

    /**
     * Adds one element to the list
     * @param elem the element
     */
    public void addElem(AnnotationElem elem){
        elems.add(elem);
    }
    
    /**
     * Overwrites the elements stored previously
     * @param list the new list of elements
     */
    public void setElems(List<AnnotationElem> list){
        this.elems = list;
    }

    /**
     * Gets the ith element
     * @param i the element to retrieve
     * @return whichever element is at this index
     */
    @Deprecated
    public AnnotationElem getElemAt(int i){
        return elems.get(i);
    }
    
    /**
     * @return an immutable collection of the elements
     */
    public Collection<AnnotationElem> getElems(){
      return Collections.unmodifiableCollection(elems);
    }
}

