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
 */
abstract class AnnotationElemBuilder extends AnnotationVisitor {

	protected final ArrayList<AnnotationElem> elems;
	
	AnnotationElemBuilder(int expected) {
		super(Opcodes.ASM4);
		this.elems = new ArrayList<AnnotationElem>(expected);
	}
	
	AnnotationElemBuilder() {
		this(4);
	}
	
	@Override
	public void visit(String name, Object value) {
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
		} else {
			throw new UnsupportedOperationException("Unsupported value type: " + value.getClass());
		}
		elems.add(elem);
	}
	
	@Override
	public void visitEnum(String name, String desc, String value) {
		elems.add(new AnnotationEnumElem(value, desc, 'e', name));
	}
	
	@Override
	public AnnotationVisitor visitArray(final String name) {
		return new AnnotationElemBuilder() {
			@Override
			public void visitEnd() {
				AnnotationElemBuilder.this.elems.add(new AnnotationArrayElem(this.elems, '[', name));
			}
		};
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(final String name, final String desc) {
		return new AnnotationElemBuilder() {
			@Override
			public void visitEnd() {
				AnnotationTag tag = new AnnotationTag(desc, this.elems.size());
				tag.setElems(this.elems);
				AnnotationElemBuilder.this.elems.add(new AnnotationAnnotationElem(tag, '@', name));
			}
		};
	}
	
	@Override
	public abstract void visitEnd();
}