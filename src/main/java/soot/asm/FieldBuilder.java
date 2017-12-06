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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;

import org.objectweb.asm.Opcodes;

import soot.SootField;

/**
 * Soot field builder.
 * 
 * @author Aaloan Miftah
 */
final class FieldBuilder extends FieldVisitor {

	private TagBuilder tb;
	private final SootField field;
	private final SootClassBuilder scb;
	
	FieldBuilder(SootField field, SootClassBuilder scb) {
		super(Opcodes.ASM5);
		this.field = field;
		this.scb = scb;
	}
	
	private TagBuilder getTagBuilder() {
		TagBuilder t = tb;
		if (t == null)
			t = tb = new TagBuilder(field, scb);
		return t;
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return getTagBuilder().visitAnnotation(desc, visible);
	}
	
	@Override
	public void visitAttribute(Attribute attr) {
		getTagBuilder().visitAttribute(attr);
	}
}