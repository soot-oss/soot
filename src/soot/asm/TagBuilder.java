package soot.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import soot.tagkit.AnnotationConstants;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Host;
import soot.tagkit.VisibilityAnnotationTag;

/**
 * Tag builder.
 */
final class TagBuilder {

	private VisibilityAnnotationTag invisibleTag, visibleTag;
	private final Host host;
	private final SootClassBuilder scb;
	
	TagBuilder(Host host, SootClassBuilder scb) {
		this.host = host;
		this.scb = scb;
	}
	
	/**
	 * @see FieldVisitor#visitAnnotation(String, boolean)
	 * @see MethodVisitor#visitAnnotation(String, boolean)
	 * @see ClassVisitor#visitAnnotation(String, boolean)
	 */
	public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
		VisibilityAnnotationTag tag;
		if (visible) {
			tag = visibleTag;
			if (tag == null) {
				visibleTag = tag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE);
				host.addTag(tag);
			}
		} else {
			tag = invisibleTag;
			if (tag == null) {
				invisibleTag = tag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE);
				host.addTag(tag);
			}
		}
		scb.addDep(AsmUtil.toQualifiedName(desc.substring(1, desc.length() - 1)));
		final VisibilityAnnotationTag _tag = tag;
		return new AnnotationElemBuilder() {
			@Override
			public void visitEnd() {
				AnnotationTag annotTag = new AnnotationTag(desc, elems.size());
				annotTag.setElems(elems);
				_tag.addAnnotation(annotTag);
			}
		};
	}

	/**
	 * @see FieldVisitor#visitAttribute(Attribute)
	 * @see MethodVisitor#visitAttribute(Attribute)
	 * @see ClassVisitor#visitAttribute(Attribute)
	 */
	public void visitAttribute(Attribute attr) {
		throw new UnsupportedOperationException("Unknown attribute: " + attr);
	}
}