package com.github.jasoma.graft.ast

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
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
