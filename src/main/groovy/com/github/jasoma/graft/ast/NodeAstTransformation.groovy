package com.github.jasoma.graft.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation
/**
 * AST Transformation for all clases marked with {@link Node}. Adds graphId properties and related methods
 * as well persistence methods.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class NodeAstTransformation extends AbstractEntityTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        ClassNode nodeClass = nodes[1] as ClassNode
        addEntityProperties(nodeClass)
    }

}
