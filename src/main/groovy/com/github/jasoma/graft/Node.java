package com.github.jasoma.graft;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a node entity to be persisted in Neo4j. The label of the node will be the
 * simple name of the class.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@GroovyASTTransformationClass("com.github.jasoma.graft.ast.NodeAstTransformation")
public @interface Node { }

