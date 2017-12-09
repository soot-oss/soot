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

import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import soot.*;
import soot.Type;
import soot.tagkit.AnnotationConstants;
import soot.tagkit.AnnotationDefaultTag;
import soot.tagkit.AnnotationTag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;

/**
 * Soot method builder.
 * 
 * @author Aaloan Miftah
 */
class MethodBuilder extends JSRInlinerAdapter {

	private TagBuilder tb;
	private VisibilityAnnotationTag[] visibleParamAnnotations;
	private VisibilityAnnotationTag[] invisibleParamAnnotations;
	private final SootMethod method;
	private final SootClassBuilder scb;
	
	MethodBuilder(SootMethod method, SootClassBuilder scb,
			String desc, String[] ex) {
		super(Opcodes.ASM5, null, method.getModifiers(),
				method.getName(), desc, null, ex);
		this.method = method;
		this.scb = scb;
	}
	
	private TagBuilder getTagBuilder() {
		TagBuilder t = tb;
		if (t == null)
			t = tb = new TagBuilder(method, scb);
		return t;
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return getTagBuilder().visitAnnotation(desc, visible);
	}
	
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return new AnnotationElemBuilder(1) {
			@Override
			public void visitEnd() {
				method.addTag(new AnnotationDefaultTag(elems.get(0)));
			}
		};
	}
	
	@Override
	public void visitAttribute(Attribute attr) {
		getTagBuilder().visitAttribute(attr);
	}
	
	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter,
			final String desc, boolean visible) {
		VisibilityAnnotationTag vat, vats[];
		if (visible) {
			vats = visibleParamAnnotations;
			if (vats == null) {
				vats = new VisibilityAnnotationTag[method.getParameterCount()];
				visibleParamAnnotations = vats;
			}
			vat = vats[parameter];
			if (vat == null) {
				vat = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE);
				vats[parameter] = vat;
			}
		} else {
			vats = invisibleParamAnnotations;
			if (vats == null) {
				vats = new VisibilityAnnotationTag[method.getParameterCount()];
				invisibleParamAnnotations = vats;
			}
			vat = vats[parameter];
			if (vat == null) {
				vat = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE);
				vats[parameter] = vat;
			}
		}
		final VisibilityAnnotationTag _vat = vat;
		return new AnnotationElemBuilder() {
			@Override
			public void visitEnd() {
				AnnotationTag annotTag = new AnnotationTag(desc, elems);
				_vat.addAnnotation(annotTag);
			}
		};
	}
	
	@Override
	public void visitTypeInsn(int op, String t) {
		super.visitTypeInsn(op, t);
		Type rt = AsmUtil.toJimpleRefType(t);
		if (rt instanceof ArrayType)
			scb.addDep(((ArrayType) rt).baseType);
		else
			scb.addDep(rt);
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		super.visitFieldInsn(opcode, owner, name, desc);
		for (Type t : AsmUtil.toJimpleDesc(desc)) {
			if (t instanceof RefType)
				scb.addDep(t);
		}

		scb.addDep(AsmUtil.toQualifiedName(owner));
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterf) {
		super.visitMethodInsn(opcode, owner, name, desc, isInterf);
		for (Type t : AsmUtil.toJimpleDesc(desc)) {
			addDeps(t);
		}
		
		scb.addDep(AsmUtil.toBaseType(owner));
	}

	@Override
	public void visitLdcInsn(Object cst) {
		super.visitLdcInsn(cst);
		
		if(cst instanceof Handle) {
			Handle methodHandle = (Handle) cst;
			scb.addDep(AsmUtil.toBaseType(methodHandle.getOwner()));
		}
	}

	private void addDeps(Type t) {
		if (t instanceof RefType)
			scb.addDep(t);
		else if (t instanceof ArrayType) {
			ArrayType at = (ArrayType) t;
			addDeps(at.getElementType());
		}
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end,
			Label handler, String type) {
		super.visitTryCatchBlock(start, end, handler, type);
		if (type != null)
			scb.addDep(AsmUtil.toQualifiedName(type));
	}
	
	@Override
	public void visitEnd() {
		super.visitEnd();
		if (visibleParamAnnotations != null) {
			VisibilityParameterAnnotationTag tag =
					new VisibilityParameterAnnotationTag(visibleParamAnnotations.length,
							AnnotationConstants.RUNTIME_VISIBLE);
			for (VisibilityAnnotationTag vat : visibleParamAnnotations) {
				tag.addVisibilityAnnotation(vat);
			}
			method.addTag(tag);
		}
		if (invisibleParamAnnotations != null) {
			VisibilityParameterAnnotationTag tag =
					new VisibilityParameterAnnotationTag(invisibleParamAnnotations.length,
							AnnotationConstants.RUNTIME_INVISIBLE);
			for (VisibilityAnnotationTag vat : invisibleParamAnnotations){
				tag.addVisibilityAnnotation(vat);
			}
			method.addTag(tag);
		}
		if (method.isConcrete()) {
			method.setSource(new AsmMethodSource(maxLocals, instructions,
					localVariables, tryCatchBlocks));
		}
	}
}