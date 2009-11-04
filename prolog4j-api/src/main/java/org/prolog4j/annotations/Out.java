package org.prolog4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Denotes an output only argument of a goal. It can be specified for the return
 * type of a goal method. The argument of the annotation is the name of a Prolog
 * variable occurring the goal. The value(s) that get(s) bound to the variable
 * in course of solving the goal will be represented by the return value of the
 * goal method.
 */
@Target(ElementType.METHOD)
public @interface Out {

    /**
     * The name of a Prolog variable in the goal. The value(s) that get bound to
     * the variable can be accessed through the return value of the method.
     */
    String value();

}
