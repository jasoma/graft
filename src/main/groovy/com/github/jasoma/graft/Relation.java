package com.github.jasoma.graft;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type as a relation entity to be persisted in Neo4j.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@GroovyASTTransformationClass("com.github.jasoma.graft.ast.RelationAstTransformation")
public @interface Relation {

    /**
     * The type name of the relation to use in neo4j. If not specified then the simple name of the class will
     * be used as the type.
     *
     * @return the type name of the relation.
     */
    String value() default "";
}

