/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2014 Raja Vallee-Rai and others
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
package soot.asm;

import java.util.ArrayList;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElem;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;

/**
 * Annotation element builder.
 * 
 * @author Aaloan Miftah
 */
abstract class AnnotationElemBuilder extends AnnotationVisitor {

	protected final ArrayList<AnnotationElem> elems;
	
	AnnotationElemBuilder(int expected) {
		super(Opcodes.ASM5);
		this.elems = new ArrayList<AnnotationElem>(expected);
	}
	
	AnnotationElemBuilder() {
		this(4);
	}
	
	public AnnotationElem getAnnotationElement(String name, Object value){
		AnnotationElem elem;
		if (value instanceof Byte) {
			elem = new AnnotationIntElem((Byte) value, 'B', name);
		} else if (value instanceof Boolean) {
			elem = new AnnotationIntElem(((Boolean) value) ? 1 : 0, 'Z', name);
		} else if (value instanceof Character) {
			elem = new AnnotationIntElem((Character) value, 'C', name);
		} else if (value instanceof Short) {
			elem = new AnnotationIntElem((Short) value, 'S', name);
		} else if (value instanceof Integer) {
			elem = new AnnotationIntElem((Integer) value, 'I', name);
		} else if (value instanceof Long) {
			elem = new AnnotationLongElem((Long) value, 'J', name);
		} else if (value instanceof Float) {
			elem = new AnnotationFloatElem((Float) value, 'F', name);
		} else if (value instanceof Double) {
			elem = new AnnotationDoubleElem((Double) value, 'D', name);
		} else if (value instanceof String) {
			elem = new AnnotationStringElem(value.toString(), 's', name);
		} else if (value instanceof Type) {
			Type t = (Type) value;
			elem = new AnnotationClassElem(t.getDescriptor(), 'c', name);
		} else if (value.getClass().isArray()){
			ArrayList<AnnotationElem> annotationArray = new ArrayList<AnnotationElem>();
			if (value instanceof byte[]) {
				for(Object element:(byte[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof boolean[]) {
				for(Object element:(boolean[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof char[]) {
				for(Object element:(char[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof short[]) {
				for(Object element:(short[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof int[]) {
				for(Object element:(int[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof long[]) {
				for(Object element:(long[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof float[]) {
				for(Object element:(float[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof double[]) {
				for(Object element:(double[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof String[]) {
				for(Object element:(String[])value) annotationArray.add(getAnnotationElement(name,element));
			} else if (value instanceof Type[]) {
				for(Object element:(Type[])value) annotationArray.add(getAnnotationElement(name,element));
			}
			else
				throw new UnsupportedOperationException("Unsupported array value type: " + value.getClass());
			elem = new AnnotationArrayElem(annotationArray, '[', name);
		} else
			throw new UnsupportedOperationException("Unsupported value type: " + value.getClass());
		return(elem);
	}
	
	@Override
	public void visit(String name, Object value) {
		AnnotationElem elem = getAnnotationElement(name,value);
		this.elems.add(elem);
	}
	
	@Override
	public void visitEnum(String name, String desc, String value) {
		elems.add(new AnnotationEnumElem(desc, value, 'e', name));
	}
	
	@Override
	public AnnotationVisitor visitArray(final String name) {
		return new AnnotationElemBuilder() {
			@Override
			public void visitEnd() {
				String ename = name;
				if (ename == null)
					ename = "default";
				AnnotationElemBuilder.this.elems.add(new AnnotationArrayElem(
						this.elems, '[', ename));
			}
		};
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(final String name, final String desc) {
		return new AnnotationElemBuilder() {
			@Override
			public void visitEnd() {
				AnnotationTag tag = new AnnotationTag(desc, elems);
				AnnotationElemBuilder.this.elems.add(new AnnotationAnnotationElem(tag, '@', name));
			}
		};
	}
	
	@Override
	public abstract void visitEnd();
}