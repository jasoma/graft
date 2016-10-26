package com.github.jasoma.graft;

import com.github.jasoma.graft.convert.PropertyConverter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for fields or property accessors that specifies a {@link com.github.jasoma.graft.convert.PropertyConverter} to use
 * when storing/reading the property from the database.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Using {

    /**
     * The type of the property converter to use.
     *
     * @return the type of the  property converter to use.
     */
    Class<? extends PropertyConverter> value();

}
