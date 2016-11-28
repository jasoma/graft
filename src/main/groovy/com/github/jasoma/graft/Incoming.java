package com.github.jasoma.graft;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or property as the outgoing end of a relationship. The type of the field must be a type annotated
 * with {@link Node}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Incoming {

    /**
     * The type of the relationship that links the two nodes.
     *
     * @return the type of the relationship linking the two nodes.
     */
    Class<?> value();
}
