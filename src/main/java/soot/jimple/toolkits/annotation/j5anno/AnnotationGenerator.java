/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Will Benton
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
package soot.jimple.toolkits.annotation.j5anno;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import soot.G;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Singletons.Global;
import soot.tagkit.AnnotationConstants;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Host;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;

/**
 * AnnotationGenerator is a singleton class that wraps up Soot's support for Java 5
 * annotations in a more convenient interface. It supplies three <tt>annotate()</tt>
 * methods that take an <tt>Host</tt>, an annotation class, and zero or more 
 * <tt>AnnotationElem</tt> objects; these methods find the appropriate <tt>Tag</tt>
 * on the given <tt>Host</tt> for the appropriate annotation visibility and
 * add an annotation of the given type to it.
 * <b>Note</b> that the first two methods expect an annotation class, which the last
 * method expects a class name. If the class is passed, this class has to be on
 * Soot's classpath at compile time. It is not enough to add the class to
 * the soo-classpath!<br> <br>
 *   
 * One caveat:
 * <tt>annotate()</tt> does not add annotation classes to the Scene, so you will 
 * have to manually add any annotation classes that were not already in the Scene to the 
 * output directory or jar.
 * 
 * @author <a href="mailto:willbenton+javadoc@gmail.com">Will Benton</a>
 * @author Eric Bodden
 */
public class AnnotationGenerator {

	public AnnotationGenerator(Global g) {}
		
	/**
	 * Returns the unique instance of AnnotationGenerator. 
	 */
	public static AnnotationGenerator v() {
		return G.v().soot_jimple_toolkits_annotation_j5anno_AnnotationGenerator();
	}
		
	/**
	 * Applies a Java 1.5-style annotation to a given Host. The Host must be of type {@link SootClass}, {@link SootMethod}
	 * or {@link SootField}.
	 * 
	 * @param h a method, field, or class
	 * @param klass the class of the annotation to apply to <code>h</code>
	 * @param elems a (possibly empty) sequence of AnnotationElem objects corresponding to the elements that should be contained in this annotation
	 */
	public void annotate(Host h, Class<? extends Annotation> klass, AnnotationElem... elems) {		
		annotate(h, klass, Arrays.asList(elems));
	}
	
	/**
	 * Applies a Java 1.5-style annotation to a given Host. The Host must be of type {@link SootClass}, {@link SootMethod}
	 * or {@link SootField}.
	 * 
	 * @param h a method, field, or class
	 * @param klass the class of the annotation to apply to <code>h</code>
	 * @param elems a (possibly empty) sequence of AnnotationElem objects corresponding to the elements that should be contained in this annotation
	 */
	public void annotate(Host h, Class<? extends Annotation> klass, List<AnnotationElem> elems) {
		//error-checking -- is this annotation appropriate for the target Host?
		Target t = klass.getAnnotation(Target.class);
		Collection<ElementType> elementTypes = Arrays.asList(t.value());		
		final String ERR = "Annotation class "+klass+" not applicable to host of type "+h.getClass()+".";
		if(h instanceof SootClass) {
			if(!elementTypes.contains(ElementType.TYPE)) {
				throw new RuntimeException(ERR);
			}
		} else if(h instanceof SootMethod) {
			if(!elementTypes.contains(ElementType.METHOD)) {
				throw new RuntimeException(ERR);
			}
		} else if(h instanceof SootField) {
			if(!elementTypes.contains(ElementType.FIELD)) {
				throw new RuntimeException(ERR);
			}
		} else {
			throw new RuntimeException("Tried to attach annotation to host of type "+h.getClass()+".");
		}
		
		//get the retention type of the class
		Retention r = klass.getAnnotation(Retention.class);
		
		// CLASS (runtime invisible) retention is the default
		int retPolicy = AnnotationConstants.RUNTIME_INVISIBLE;
		if(r!=null) {
			//TODO why actually do we have AnnotationConstants at all and don't use
			//     RetentionPolicy directly? (Eric Bodden 20/05/2008)
			switch(r.value()) {
			case CLASS:
				retPolicy = AnnotationConstants.RUNTIME_INVISIBLE;
				break;
			case RUNTIME:
				retPolicy = AnnotationConstants.RUNTIME_VISIBLE;
				break;
			default:
				throw new RuntimeException("Unexpected retention policy: "+retPolicy);
			}
		} 

		annotate(h, klass.getCanonicalName(), retPolicy , elems);	
	}
	
	/**
	 * Applies a Java 1.5-style annotation to a given Host. The Host must be of type {@link SootClass}, {@link SootMethod}
	 * or {@link SootField}.
	 * 
	 * @param h a method, field, or class
	 * @param annotationName the qualified name of the annotation class
	 * @param visibility any of the constants in {@link AnnotationConstants}
	 * @param elems a (possibly empty) sequence of AnnotationElem objects corresponding to the elements that should be contained in this annotation
	 */
	public void annotate(Host h, String annotationName, int visibility, List<AnnotationElem> elems) {
		annotationName = annotationName.replace('.','/');
		if(!annotationName.endsWith(";"))
			annotationName = "L" + annotationName + ';';
		VisibilityAnnotationTag tagToAdd = findOrAdd(h, visibility);
		AnnotationTag at = new AnnotationTag(annotationName, elems);
		tagToAdd.addAnnotation(at);
	}
	
	/**
	 * Finds a VisibilityAnnotationTag attached to a given Host with the appropriate visibility,
	 * or adds one if no such tag is attached.
	 * @param h an Host
	 * @param visibility a visibility level, taken from soot.tagkit.AnnotationConstants
	 * @return
	 */
	private VisibilityAnnotationTag findOrAdd(Host h, int visibility) {
		ArrayList<VisibilityAnnotationTag> va_tags = new ArrayList<VisibilityAnnotationTag>();
		
		for (Tag t : h.getTags()) {
			if(t instanceof VisibilityAnnotationTag) {
				VisibilityAnnotationTag vat = (VisibilityAnnotationTag)t;
				if (vat.getVisibility() == visibility) va_tags.add(vat);					
			}
		}
				
		if (va_tags.isEmpty()) {
			VisibilityAnnotationTag vat = new VisibilityAnnotationTag(visibility);
			h.addTag(vat);
			return vat;
		}
		
		// return the first visibility annotation with the right visibility
		return (va_tags.get(0));
	}
	
}