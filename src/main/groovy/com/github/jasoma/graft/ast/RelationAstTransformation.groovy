package com.github.jasoma.graft.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * AST Transformation for all classes marked with {@link com.github.jasoma.graft.Relation}. Adds graphId properties and related methods
 * as well persistence methods.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class RelationAstTransformation extends AbstractEntityTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode atRelation = nodes[0] as AnnotationNode
        ClassNode relationClass = nodes[1] as ClassNode
        addEntityProperties(relationClass)
        addTypeProperty(relationClass, atRelation)
    }

    private void addTypeProperty(ClassNode relationClass, AnnotationNode atRelation) {
        def name = typeName(relationClass, atRelation)
        def getter = createMethod("getTypeName", ClassHelper.makeCached(String), new ReturnStatement(constant(name)))
        relationClass.addMethod(getter)
    }

    private String typeName(ClassNode relationClass, AnnotationNode atRelation) {
        if (atRelation.members.isEmpty()) {
            return relationClass.nameWithoutPackage
        }
        def expr = atRelation.members["value"] as ConstantExpression
        return expr.value
    }

}
