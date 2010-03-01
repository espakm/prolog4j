package org.prolog4j.apt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.tree.AnnotationNode;

class TheoryVisitor2 extends LazyClassVisitor {

	public final class GoalVisitor extends EmptyVisitor {
		public AnnotationNode goalAnnotation;
		public Map<Integer, AnnotationNode> bindAnnotations =
			new HashMap<Integer, AnnotationNode>();
		private final String name;
		private final String mDesc;

		private GoalVisitor(String name, String mDesc) {
			this.name = name;
			this.mDesc = mDesc;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String aDesc, boolean visible) {
			if (aDesc.equals("Lorg/prolog4j/annotations/Goal;")) {
				goalAnnotation = new AnnotationNode(aDesc);
				goalMethods.add(name + mDesc);
				goalVisitors.add(this);
				return goalAnnotation;
			}
			else if (aDesc.equals("Lorg/prolog4j/annotations/Bind;")) {
				AnnotationNode an = new AnnotationNode(aDesc);
				bindAnnotations.put(-1, an);
				return an;
			}
			return null;
		}
		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter,
				String aDesc, boolean visible) {
			if (goalAnnotation == null) {
				return null;
			}
			if (aDesc.equals("Lorg/prolog4j/annotations/In;")) {
				AnnotationNode an = new AnnotationNode(aDesc);
				bindAnnotations.put(parameter, an);
				return an;
			}
			return null;
		}
	}

	private String className;
	
	AnnotationNode theoryAnn;
	Set<String> goalMethods = new HashSet<String>();
	LinkedList<GoalVisitor> goalVisitors = new LinkedList<GoalVisitor>();
	
	public TheoryVisitor2(String className) {
		this.className = className;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals("Lorg/prolog4j/annotations/Theory;")) {
//			return new TheoryVisitor(className);
			return theoryAnn = new AnnotationNode(desc);
		}
		return null;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, final String name, final String mDesc,
			String signature, String[] exceptions) {
		return new GoalVisitor(name, mDesc);
	}
	
}
