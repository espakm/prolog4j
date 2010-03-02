package org.prolog4j.apt;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.prolog4j.apt.P4JAnnotationVisitor.GoalVisitor;

class GoalAdapter extends ClassAdapter {

	String className, classDesc;
	private AnnotationNode theoryAnn;
	private Set<String> goalMethods;
	LinkedList<GoalVisitor> goalVisitors;
	private int lastProcessedQuery;
	private boolean clinit;
	
	public GoalAdapter(ClassVisitor cv, String internalClassName, P4JAnnotationVisitor ta) {
		super(cv);
		this.classDesc = internalClassName;
		this.className = internalClassName.replace('/', '.');;
		this.theoryAnn = ta.theoryAnn;
		this.goalVisitors = ta.goalVisitors;
		this.goalMethods = ta.goalMethods;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		String sgn = name + desc;
		if (goalMethods.contains(sgn)) {
			generateBody(goalVisitors.get(lastProcessedQuery), access, name, desc, signature, exceptions);
			++lastProcessedQuery;
			return null;
		}
		if (name.equals("<clinit>")) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
			MethodAdapter ma = new MethodAdapter(mv) {
				@Override
				public void visitInsn(int opcode) {
					if (opcode == RETURN) {
						insertStaticInitialization(super.mv);
					}
					super.visitInsn(opcode);
				}
				@Override
				public void visitMaxs(int maxStack, int maxLocals) {
					super.visitMaxs(Math.max(2, maxStack), maxLocals);
				}
			};
			clinit = true;
			return ma;
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	private static final String[] WRAPPER_CLASS = new String[9];
	private static final String[] WRAPPER_DESC = new String[9];
	static {
		WRAPPER_CLASS[Type.BOOLEAN] = "java/lang/Boolean";
		WRAPPER_DESC[Type.BOOLEAN] = "(Z)Ljava/lang/Boolean;";
		WRAPPER_CLASS[Type.CHAR] = "java/lang/Character";
		WRAPPER_DESC[Type.CHAR] = "(C)Ljava/lang/Character;";
		WRAPPER_CLASS[Type.BYTE] = "java/lang/Byte";
		WRAPPER_DESC[Type.BYTE] = "(B)Ljava/lang/Byte;";
		WRAPPER_CLASS[Type.SHORT] = "java/lang/Short";
		WRAPPER_DESC[Type.SHORT] = "(S)Ljava/lang/Short;";
		WRAPPER_CLASS[Type.INT] = "java/lang/Integer";
		WRAPPER_DESC[Type.INT] = "(I)Ljava/lang/Integer;";
		WRAPPER_CLASS[Type.FLOAT] = "java/lang/Float";
		WRAPPER_DESC[Type.FLOAT] = "(F)Ljava/lang/Float;";
		WRAPPER_CLASS[Type.LONG] = "java/lang/Long";
		WRAPPER_DESC[Type.LONG] = "(J)Ljava/lang/Long;";
		WRAPPER_CLASS[Type.DOUBLE] = "java/lang/Double";
		WRAPPER_DESC[Type.DOUBLE] = "(D)Ljava/lang/Double;";
	}
	
	private MethodVisitor generateBody(GoalVisitor gv, int access, String name, String desc,
			String signature, String[] exceptions) {
		
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		mv.visitCode();
		int stack = 6, locals = 1;
		AnnotationNode an = gv.goalAnnotation;
		Boolean cacheB = (Boolean) getValue(an, "cache");
		boolean cache = cacheB == null ? true : cacheB;
		AnnotationNode outBind = gv.bindAnnotations.get(-1);
		String outVar = outBind != null 
			? (String) outBind.values.get(1) : null;
		if (!cache) {
			mv.visitLdcInsn(className);
			mv.visitMethodInsn(INVOKESTATIC, "org/prolog4j/ProverFactory", "getProver", "(Ljava/lang/String;)Lorg/prolog4j/Prover;");
			mv.visitLdcInsn(getValue(an, "value"));
		} else {
			mv.visitFieldInsn(GETSTATIC, classDesc, "$P4J_GOAL_" + lastProcessedQuery, "Lorg/prolog4j/Query;");
		}
		Type[] argumentTypes = Type.getArgumentTypes(desc);
		int argNo = argumentTypes.length;
		if (argNo < 6) {
			mv.visitInsn(ICONST_0 + argNo);
		} else {
			mv.visitIntInsn(BIPUSH, argNo);
		}
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		for (int i = 0; i < argNo; ++i) {
			mv.visitInsn(DUP);
			if (i < 6) {
				mv.visitInsn(ICONST_0 + i);
			} else {
				mv.visitIntInsn(BIPUSH, i);
			}
			Type argType = argumentTypes[i];
			mv.visitVarInsn(argType.getOpcode(ILOAD), locals);
			int sort = argType.getSort();
			if (sort < 9) {
				mv.visitMethodInsn(INVOKESTATIC, WRAPPER_CLASS[sort], 
						"valueOf", WRAPPER_DESC[sort]);
			}
			mv.visitInsn(AASTORE);
			locals += argType.getSize();
			if (argType.getSize() == 2) {
				stack = 7;
			}
		}
		if (!cache) {
			mv.visitMethodInsn(INVOKEINTERFACE, "org/prolog4j/Prover", 
					"solve", "(Ljava/lang/String;[Ljava/lang/Object;)Lorg/prolog4j/Solution;");
		} else {
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/prolog4j/Query", 
					"solve", "([Ljava/lang/Object;)Lorg/prolog4j/Solution;");
		}
		Type returnType = Type.getReturnType(desc);
		String retTypeDesc = returnType.getDescriptor();
		if (retTypeDesc.equals("Lorg/prolog4j/Solution;")) {
			if (outVar != null) {
				mv.visitLdcInsn(outVar);
				mv.visitMethodInsn(INVOKEVIRTUAL, "org/prolog4j/Solution", "on", "(Ljava/lang/String;)Lorg/prolog4j/Solution;");
			}
			mv.visitInsn(ARETURN);
		} else if (returnType == Type.BOOLEAN_TYPE) {
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/prolog4j/Solution", "isSuccess", "()Z");
			mv.visitInsn(IRETURN);
		} else if (retTypeDesc.equals("Ljava/lang/Boolean;")) {
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/prolog4j/Solution", "isSuccess", "()Z");
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
			mv.visitInsn(ARETURN);
		} else {
			if (outVar != null) {
				mv.visitLdcInsn(outVar);
				mv.visitMethodInsn(INVOKEVIRTUAL, "org/prolog4j/Solution", "get", "(Ljava/lang/String;)Ljava/lang/Object;");
			} else {
				mv.visitMethodInsn(INVOKEVIRTUAL, "org/prolog4j/Solution", "get", "()Ljava/lang/Object;");
			}
			Type wrapperType = getWrapperType(returnType);
			String wrapperTypeDesc = wrapperType.getDescriptor();
			mv.visitTypeInsn(CHECKCAST, wrapperTypeDesc);
			if (wrapperType != returnType) {
				mv.visitMethodInsn(INVOKEVIRTUAL, wrapperTypeDesc, 
						returnType.getClassName() + "Value",
						"()" + retTypeDesc);
			}
			mv.visitInsn(returnType.getOpcode(IRETURN));
		}
		mv.visitMaxs(stack, locals);
//		mv.visitMaxs(0, locals);
		mv.visitEnd();
		return mv;
	}

	private Type getWrapperType(Type type) {
		switch (type.getSort()) {
		case Type.BOOLEAN: return Type.getObjectType("java/lang/Boolean");
		case Type.CHAR: return Type.getObjectType("java/lang/Character");
		case Type.BYTE: return Type.getObjectType("java/lang/Byte");
		case Type.SHORT: return Type.getObjectType("java/lang/Short");
		case Type.INT: return Type.getObjectType("java/lang/Integer");
		case Type.FLOAT: return Type.getObjectType("java/lang/Float");
		case Type.LONG: return Type.getObjectType("java/lang/Long");
		case Type.DOUBLE: return Type.getObjectType("java/lang/Double");
		default: return type;
		}
	}

	@Override
	public void visitEnd() {
		visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, 
				"$P4J_PROVER", "Lorg/prolog4j/Prover;", 
				null, null);
		for (int i = 0; i < goalVisitors.size(); ++i) {
			AnnotationNode an = goalVisitors.get(i).goalAnnotation;
			visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, 
					"$P4J_GOAL_" + i, "Lorg/prolog4j/Query;", 
					null, null);
		}
		if (clinit == false) {
			MethodVisitor mv = super.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
			mv.visitCode();
			int stack = insertStaticInitialization(mv);
			mv.visitInsn(RETURN);
			mv.visitMaxs(stack, 0);
			mv.visitEnd();
		}

		super.visitEnd();
	}

	private int insertStaticInitialization(MethodVisitor mv) {
		int stack = 2;
		mv.visitLdcInsn(className);
		mv.visitMethodInsn(INVOKESTATIC, "org/prolog4j/ProverFactory", "getProver", "(Ljava/lang/String;)Lorg/prolog4j/Prover;");
		mv.visitFieldInsn(PUTSTATIC, classDesc, "$P4J_PROVER", "Lorg/prolog4j/Prover;");
		if (theoryAnn != null) {
			List values = theoryAnn.values;
			Object val = values.get(1);
			if (val instanceof String) {
				mv.visitFieldInsn(GETSTATIC, classDesc, "$P4J_PROVER", "Lorg/prolog4j/Prover;");
				mv.visitLdcInsn(val);
				mv.visitMethodInsn(INVOKEINTERFACE, "org/prolog4j/Prover", "addTheory", "(Ljava/lang/String;)V");
			} else if (val instanceof List) {
				List list = (List) val;
				mv.visitFieldInsn(GETSTATIC, classDesc, "$P4J_PROVER", "Lorg/prolog4j/Prover;");
				int argNo = list.size();
				if (argNo < 6) {
					mv.visitInsn(ICONST_0 + argNo);
				} else {
					mv.visitIntInsn(BIPUSH, argNo);
				}
				mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
				for (int i = 0; i < argNo; ++i) {
					mv.visitInsn(DUP);
					if (i < 6) {
						mv.visitInsn(ICONST_0 + i);
					} else {
						mv.visitIntInsn(BIPUSH, i);
					}
					mv.visitLdcInsn(list.get(i));
					mv.visitInsn(AASTORE);
				}
				mv.visitMethodInsn(INVOKEINTERFACE, "org/prolog4j/Prover", "addTheory", "([Ljava/lang/String;)V");
				stack = 5;
			}
		}
		for (int i = 0; i < goalVisitors.size(); ++i) {
			AnnotationNode an = goalVisitors.get(i).goalAnnotation;
			mv.visitFieldInsn(GETSTATIC, classDesc, "$P4J_PROVER", "Lorg/prolog4j/Prover;");
			mv.visitLdcInsn(getValue(an, "value"));
			mv.visitMethodInsn(INVOKEINTERFACE, "org/prolog4j/Prover", "query", "(Ljava/lang/String;)Lorg/prolog4j/Query;");
			mv.visitFieldInsn(PUTSTATIC, classDesc, "$P4J_GOAL_" + i, "Lorg/prolog4j/Query;");
		}
		return stack;
	}

	private Object getValue(AnnotationNode an, String name) {
		Iterator iterator = an.values.iterator();
		while (iterator.hasNext() && !iterator.next().equals(name)) {
			iterator.next();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}
}
