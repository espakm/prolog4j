package org.prolog4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Denotes an input only argument of a goal. It can be specified for a formal
 * argument of a goal method. The argument of the annotation is the name of a
 * Prolog variable occurring in the goal. The actual argument will be bound to the
 * Prolog variable before searching for the solutions.
 */
@Target(ElementType.PARAMETER)
public @interface In {

    /**
     * The name of a Prolog variable in the goal. The value of the actual
     * argument will be bound to the variable before solving the goal.
     */
    String value();

}
