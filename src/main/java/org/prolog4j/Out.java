package org.prolog4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Out {

	String value();
	
//	boolean returns() default false;
	
}
