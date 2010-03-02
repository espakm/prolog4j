package org.prolog4j.apt;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.ASMifierClassVisitor;

public class Bootstrap {

	public static void premain(String agentArguments, Instrumentation instrumentation) {
		instrumentation.addTransformer(new ClassFileTransformer() {

			Pattern excludePattern = 
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

				P4JAnnotationVisitor p4jVisitor = new P4JAnnotationVisitor();
				cr.accept(p4jVisitor, 0);
				ClassVisitor cv = cw;
				if (!p4jVisitor.goalMethods.isEmpty()
					|| p4jVisitor.theoryAnn != null) {
					cv = new GoalAdapter(cv, className, p4jVisitor);
				}
				cr.accept(cv, 0);
				if (className.equals("org/prolog4j/GoalTest")) {
					ClassReader cr2 = new ClassReader(cw.toByteArray());
//					cr.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);
					cr2.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);
				}
				
				return cw.toByteArray();
			}
		});
	}

}
