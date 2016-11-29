package com.github.jasoma.graft.ast

import com.github.jasoma.graft.Unmapped
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.tools.GenericsUtils
import org.codehaus.groovy.transform.AbstractASTTransformation
/**
 * base class for the relationship and node transformations.
 */
abstract class AbstractEntityTransformation extends AbstractASTTransformation {

    /**
     * Empty array for creating no-args methods.
     */
    protected static final Parameter[] NO_ARGS = new Parameter[0]

    /**
     * Empty array for creating methods with no declared exceptions.
     */
    protected static final ClassNode[] NO_EXCEPTIONS = new ClassNode[0]

    /**
     * Class node for the {@link Unmapped} annotation.
     */
    protected static final ClassNode UNMAPPED_CLASS = ClassHelper.makeCached(Unmapped)

    /**
     * Class node for the {@link EntityMethods} delegate.
     */
    protected static final ClassNode METHODS_CLASS = ClassHelper.makeCached(EntityMethods)

    /**
     * Add the graphId and modified properties to an entity class. The methods added directly always return null/false
     * and will be intercepted by the proxy if the entity was loaded from the database.
     *
     * @param classNode the entity class.
     */
    protected void addEntityProperties(ClassNode classNode) {
        def idGetter = createMethod("getGraphId", ClassHelper.Long_TYPE, new ReturnStatement(constant(null)))
        classNode.addMethod(idGetter)

        def modifiedGetter = createMethod("isModified", ClassHelper.boolean_TYPE, new ReturnStatement(constant(false)))
        classNode.addMethod(modifiedGetter)

        def emptySetCall = new StaticMethodCallExpression(ClassHelper.makeCached(Collections), "emptySet", new ArgumentListExpression())
        def stringSet = GenericsUtils.makeClassSafeWithGenerics(Set, ClassHelper.makeCached(String))
        def modifiedPropertiesGetter = createMethod("getModifiedProperties", stringSet, new ReturnStatement(emptySetCall))
        classNode.addMethod(modifiedPropertiesGetter)
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

    /**
     * Create a method call expression for one of the utility methods in {@linnk EntityMethods}.
     *
     * @param method the name of the method to call.
     * @param args arguments to the method.
     * @return the method call expression.
     */
    protected StaticMethodCallExpression callEntityMethod(String method, Expression... args) {
        return new StaticMethodCallExpression(METHODS_CLASS, method, new ArgumentListExpression(args))
    }

    /**
     * Helper method for creating a {@link MethodNode}, by default generates a public no args method.
     *
     * @param name the name of the method.
     * @param returnType the result type.
     * @param code the method body.
     * @param modifiers modifiers for the method.
     * @param parameters parameters for the method.
     * @param exceptions exceptions the method might through.
     * @return the method node.
     */
    protected MethodNode createMethod(String name, ClassNode returnType, Statement code, int modifiers = ACC_PUBLIC, Parameter[] parameters = NO_ARGS, ClassNode[] exceptions = NO_EXCEPTIONS) {
        return new MethodNode(name, modifiers, returnType, parameters, exceptions, code)
    }

    /**
     * Shorthand for creating a variable expression.
     *
     * @param name the name of the variable to reference.
     * @return the expression referencing the variable.
     */
    protected VariableExpression var(String name) {
        return new VariableExpression(name)
    }

    /**
     * Shorthand for creating a constant expression.
     *
     * @param name the value for the constant to take.
     * @return the expression.
     */
    protected ConstantExpression constant(Object value) {
        return new ConstantExpression(value)
    }
}
