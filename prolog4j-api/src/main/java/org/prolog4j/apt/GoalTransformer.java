package org.prolog4j.apt;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.ASMifierClassVisitor;

class GoalTransformer implements ClassFileTransformer {

	private static final Pattern excludePattern = 
		Pattern.compile("^(sun/|java/|javax/|alice/|ubc/cs/JLog/).*");

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		if (excludePattern.matcher(className).matches()) {
			return classfileBuffer;
		}

		ClassReader cr = new ClassReader(classfileBuffer);
		ClassWriter cw = new ClassWriter(cr, 0);
		try {
			String cName = className.replace('/', '.');
			TheoryVisitor2 theoryVisitor = new TheoryVisitor2(cName);
			cr.accept(theoryVisitor, 0);
			ClassVisitor cv = cw;
			if (!theoryVisitor.goalMethods.isEmpty()) {
				cv = new GoalAdapter(cv, cName, theoryVisitor);
			}
			cr.accept(cv, 0);
			if (className.equals("org/prolog4j/JLogGoalTest")) {
				ClassReader cr2 = new ClassReader(cw.toByteArray());
//				cr.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);
				cr2.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		
		return cw.toByteArray();
	}

}
