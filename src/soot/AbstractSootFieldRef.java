/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot;
import java.util.LinkedList;

import soot.options.Options;

/** Representation of a reference to a field as it appears in a class file.
 * Note that the field directly referred to may not actually exist; the
 * actual target of the reference is determined according to the resolution
 * procedure in the Java Virtual Machine Specification, 2nd ed, section 5.4.3.2.
 */

class AbstractSootFieldRef implements SootFieldRef {
    public AbstractSootFieldRef( 
            SootClass declaringClass,
            String name,
            Type type,
            boolean isStatic) {
        this.declaringClass = declaringClass;
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
        if( declaringClass == null ) throw new RuntimeException( "Attempt to create SootFieldRef with null class" );
        if( name == null ) throw new RuntimeException( "Attempt to create SootFieldRef with null name" );
        if( type == null ) throw new RuntimeException( "Attempt to create SootFieldRef with null type" );
    }
    
    private final SootClass declaringClass;
    private final String name;
    private final Type type;
    private final boolean isStatic;
    
    public SootClass declaringClass() { return declaringClass; }
    public String name() { return name; }
    public Type type() { return type; }
    public boolean isStatic() { return isStatic; }

    public String getSignature() {
        return SootField.getSignature(declaringClass, name, type);
    }

    public class FieldResolutionFailedException extends ResolutionFailedException {
        /**
		 * 
		 */
		private static final long serialVersionUID = -4657113720516199499L;
		
		public FieldResolutionFailedException() {
            super("Class "+declaringClass+" doesn't have field "+name+
                    " : "+type+
                    "; failed to resolve in superclasses and interfaces" );
        }
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(super.toString());
            resolve(ret);
            return ret.toString();
        }
    }

    public SootField resolve() {
    	return resolve(null);
    }
    
    private SootField checkStatic(SootField ret) {
    	if(Options.v().wrong_staticness() == Options.wrong_staticness_fail) {
    		if( ret.isStatic() != isStatic() && !ret.isPhantom()) {
        		throw new ResolutionFailedException( "Resolved "+this+" to "+ret+" which has wrong static-ness" );
        	}
        }
        return ret;
    }
    
    private SootField resolve(StringBuffer trace) {
        SootClass cl = declaringClass;
        while(true) {
            if(trace != null) trace.append(
                    "Looking in "+cl+" which has fields "+cl.getFields()+"\n" );
            
            // Check whether we have the field in the current class
            SootField clField = cl.getFieldUnsafe(name, type);
            if (clField != null) {
                return checkStatic(clField);
            }
            // If we have a phantom class, we directly construct a phantom field
            // in it and don't care about superclasses.
            else if (Scene.v().allowsPhantomRefs() && cl.isPhantom()) {
                synchronized (cl) {
                	// Check that no other thread has created the field in the meantime
    	            clField = cl.getFieldUnsafe(name, type);
    	            if (clField != null) {
    	                return checkStatic(clField);
    	            }
    	            
    	            // Create the phantom field
	                SootField f = new SootField(name, type, isStatic()?Modifier.STATIC:0);
	                f.setPhantom(true);
	                cl.addField(f);
	                return f;
                }
            }
            else {
            	// Since this class is not phantom, we look at its interfaces
                LinkedList<SootClass> queue = new LinkedList<SootClass>();
                queue.addAll( cl.getInterfaces() );
                while( !queue.isEmpty() ) {
                    SootClass iface = queue.removeFirst();
                    if(trace != null) trace.append(
                            "Looking in "+iface+" which has fields "+iface.getFields()+"\n" );
                    SootField ifaceField = iface.getFieldUnsafe(name, type);
                    if (ifaceField != null) {
                        return checkStatic(ifaceField);
                    }
                    queue.addAll( iface.getInterfaces() );
                }
                
                // If we have not found a suitable field in the current class,
                // try the superclass
                if( cl.hasSuperclass() ) cl = cl.getSuperclass();
                else break;
            }
        }

        // If we allow phantom refs, we construct phantom fields
        if(Options.v().allow_phantom_refs()) {
        	SootField sf = new SootField(name, type, isStatic ? Modifier.STATIC : 0);
        	sf.setPhantom(true);
        	synchronized (declaringClass) {
        		// Be careful: Another thread may have already created this
        		// field in the meantime, so better check twice.
        		SootField clField = declaringClass.getFieldUnsafe(name, type);
                if (clField != null)
                    return checkStatic(clField);
                else {
                	declaringClass.addField(sf);
                	return sf;
                }
        	}
        }

        if( trace == null ) {
        	FieldResolutionFailedException e = new FieldResolutionFailedException();
        	if(Options.v().ignore_resolution_errors())
        		G.v().out.println(e.getMessage());
        	else
        		throw e;
        }
        return null;
    }
    
    public String toString() {
        return getSignature();
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((declaringClass == null) ? 0 : declaringClass.hashCode());
		result = prime * result + (isStatic ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractSootFieldRef other = (AbstractSootFieldRef) obj;
		if (declaringClass == null) {
			if (other.declaringClass != null)
				return false;
		} else if (!declaringClass.equals(other.declaringClass))
			return false;
		if (isStatic != other.isStatic)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
    
}
