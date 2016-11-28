package com.github.jasoma.graft.ast

import com.github.jasoma.graft.Unmapped
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.transform.AbstractASTTransformation
/**
 * base class for the entity and node transformations.
 */
abstract class AbstractEntityTransformation extends AbstractASTTransformation {

    /**
     * Class node for the {@link Unmapped} annotation.
     */
    protected static final ClassNode UNMAPPED_CLASS = ClassHelper.makeCached(Unmapped)

    /**
     * Class node for the {@link EntityMethods} delegate.
     */
    protected static final ClassNode METHODS_CLASS = ClassHelper.makeCached(EntityMethods)

    /**
     * Add the graphId and modified properties to an entity class.
     *
     * @param classNode the entity class.
     */
    protected void addEntityProperties(ClassNode classNode) {
        classNode.addProperty("graphId", ACC_PUBLIC, ClassHelper.Long_TYPE, ConstantExpression.NULL, null, null)
        classNode.addProperty("modified", ACC_PUBLIC, ClassHelper.boolean_TYPE, ConstantExpression.NULL, null, null)
    }

    /**
     * Adds a statement to the beginning of all setters for properties in the class that will
     * set the {@code modified} flag of the class to true whenever they are called. Ignores
     * any {@link Unmapped} properties.
     *
     * @param classNode the entity class.
     */
    protected void addModifiedChecks(ClassNode classNode) {
        for (PropertyNode property : classNode.properties) {

            if (unmapped(property)) {
                continue;
            }

            def setter = (BlockStatement) property.setterBlock
            def modifiecCall = new StaticMethodCallExpression(METHODS_CLASS, "modified", new ArgumentListExpression(
                    new VariableExpression("this"),
                    new ConstantExpression(property.name)
            ))

        }
    }

    /**
     * Checks if a property node has the {@link Unmapped} annotation either directly or on the field.
     * making up the property.
     *
     * @param property the property to check.
     * @return true if any part of the property contains the annotation, false otherwise.
     */
    protected boolean unmapped(PropertyNode property) {
        return unmapped(property as AnnotatedNode) || unmapped(property.field)
    }

    /**
     * Checks if any node has the {@link Unmapped} annotation on it and can be ignored.
     *
     * @param node the node to check.
     * @return true if it has the annotation, false otherwise.
     */
    protected boolean unmapped(AnnotatedNode node) {
        return node.annotations.any { it.classNode == UNMAPPED_CLASS }
    }

}
