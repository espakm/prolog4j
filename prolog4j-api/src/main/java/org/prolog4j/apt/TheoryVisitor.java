package org.prolog4j.apt;

import org.objectweb.asm.AnnotationVisitor;
import org.prolog4j.Prover;
import org.prolog4j.ProverFactory;

class TheoryVisitor implements AnnotationVisitor {

	private Prover p;
	
	public TheoryVisitor(String className) {
		p = ProverFactory.getProver(className);
	}

	@Override
	public void visit(String name, Object value) {
		p.addTheory((String) value);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		return null;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		return this;
	}

	@Override
	public void visitEnd() {
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
	}

}
