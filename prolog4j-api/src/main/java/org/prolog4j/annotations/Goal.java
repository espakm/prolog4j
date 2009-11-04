package org.prolog4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the Java interface of a Prolog goal (a <i>goal method</i>). It can
 * be applied for a method. The return type of the method can be one of the
 * followings:
 * <ul>
 * <li> <tt>boolean</tt> or <tt>Boolean</tt>: Only the existence of a solution is
 * of interest.</li>
 * <li> <tt>Solution<E></tt>: All the solutions are of interest. E should be the
 * common type of the values of a given variable of the goal. The values of
 * other variables can also be accessed through the solution.</li>
 * <li>Other: Only the first solution is of interest that should be of the
 * specified type.</li>
 * </ul>
 * The argument of the advice is the Prolog goal itself.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Goal {

    /**
     * The Prolog goal. Should be ended by a dot.
     */
    String value();

    /**
     * If there are more output variables in the goal, their names can be
     * specified by this annotations. If the return type is denoted by
     * {@link org.prolog4j.annotations.Out @Out}, its name is <i>not</i>
     * required to be specified here.
     */
    String[] out() default { };

}
