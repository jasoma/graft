package com.github.jasoma.graft;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type as a relation entity to be persisted in Neo4j. The type of the relation will be the
 * simple name of the class.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@GroovyASTTransformationClass("com.github.jasoma.graft.ast.RelationAstTransformation")
public @interface Relation { }

