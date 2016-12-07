package com.github.jasoma.graft.ast

import com.github.jasoma.graft.internal.NodeSchema
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * AST Transformation for all classes marked with {@link com.github.jasoma.graft.Node}. Adds graphId properties and related methods
 * as well persistence methods.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class NodeAstTransformation extends AbstractEntityTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode atNode = nodes[0] as AnnotationNode
        ClassNode nodeClass = nodes[1] as ClassNode
        addEntityProperties(nodeClass)
        addSchemaProperty(nodeClass)
        assertNoArgsCtor(nodeClass, source)
    }

    private void addSchemaProperty(ClassNode nodeClass) {
        def schemaType = ClassHelper.makeCached(NodeSchema)
        def schemaValue = new ConstructorCallExpression(schemaType, new ArgumentListExpression(new ClassExpression(nodeClass)))
        nodeClass.addField("schema", ACC_PRIVATE | ACC_STATIC | ACC_FINAL, schemaType, schemaValue)
        def getter = createMethod("getSchema", schemaType, new ReturnStatement(var("schema")), ACC_PUBLIC | ACC_STATIC)
        nodeClass.addMethod(getter)
    }

}
