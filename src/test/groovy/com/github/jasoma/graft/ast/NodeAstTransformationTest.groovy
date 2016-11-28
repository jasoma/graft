package com.github.jasoma.graft.ast

import org.junit.Test

class NodeAstTransformationTest extends GroovyTestCase {

    @Test
    def void testEntityProperties() {
        assertScript '''
            import com.github.jasoma.graft.Node

            @Node
            class MyNode { }

            def n = new MyNode()
            assert n.graphId == null
            assert n.modified == false
        '''
    }

    @Test
    def void testModifiedChecks() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.Unmapped

            @Node
            class MyNode {
                String prop
                @Unmapped String unmapped
            }

            def n = new MyNode()
            assert n.graphId == null
            assert n.modified == false
        '''
    }

}
