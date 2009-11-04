package org.prolog4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a Prolog theory. The theory will be automatically loaded into the
 * {@link org.prolog4j.Prover Prover} that has the name as the annotated package
 * or type, respectively.
 */
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Theory {

    /**
     * The Prolog theory.
     */
    String[] value();

}
