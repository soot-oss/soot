package soot.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;

import org.objectweb.asm.Opcodes;

import soot.SootField;

/**
 * Soot field builder.
 */
final class FieldBuilder extends FieldVisitor {

	private TagBuilder tb;
	private final SootField field;
	private final SootClassBuilder scb;
	
	FieldBuilder(SootField field, SootClassBuilder scb) {
		super(Opcodes.ASM4);
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