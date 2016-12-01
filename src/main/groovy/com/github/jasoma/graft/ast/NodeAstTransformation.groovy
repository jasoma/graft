package com.github.jasoma.graft.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.tools.GenericsUtils
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
        addLabelsProperty(nodeClass, atNode)
    }

    private void addLabelsProperty(ClassNode nodeClass, AnnotationNode atNode) {
        def names = labelNames(nodeClass, atNode)
        def listString = GenericsUtils.makeClassSafeWithGenerics(List, ClassHelper.makeCached(String))
        // TODO: this is generating a new list each time the method is called,
        //       convert to a class static field and return that
        def getter = createMethod("getLabels", listString, new ReturnStatement(names))
        nodeClass.addMethod(getter)
    }

    private ListExpression labelNames(ClassNode nodeClass, AnnotationNode atNode) {
        def members = atNode.members
        List<String> names

        if (members.isEmpty()) {
            names = [nodeClass.nameWithoutPackage]
        }

        def expr = members["labels"]
        if (names == null && expr instanceof ConstantExpression) {
            names = [expr.value]
        }
        if (names == null) {
            def list = expr as ListExpression
            names = list.expressions.collect { it.value }
        }
        return new ListExpression(names.collect { new ConstantExpression(it) })
    }

}
