/*
 * Copyright 2010 by Miklós Espák <espakm@gmail.com>
 * 
 * This file is part of Prolog4J.
 * 
 * Prolog4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Prolog4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Prolog4J.  If not, see <http://www.gnu.org/licenses/>.
 */
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
