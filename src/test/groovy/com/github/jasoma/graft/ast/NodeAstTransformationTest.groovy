package com.github.jasoma.graft.ast

import org.junit.Test

class NodeAstTransformationTest extends GroovyTestCase {

    @Test
    def void testEntityProperties() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.ast.EntityProxy

            @Node
            class MyNode { }

            def n = new MyNode()
            assert n.graphId == null
            assert n.modified == false
            assert n.modifiedProperties instanceof Set
            assert n.modifiedProperties.empty
        '''
    }

    @Test
    def void testModifiedChecks() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.ast.EntityProxy

            @Node
            class MyNode {
                String prop
                String unmapped
            }

            def n = new MyNode()
            n.prop = "foo"
            assert n.modified == false

            def p = new EntityProxy(n, 1, Collections.singleton("prop"))
            assert p.modified == false
            p.prop = "foo"
            assert p.modified
            assert p.modifiedProperties.contains("prop")
        '''
    }

    @Test
    def void testModifiedCustomAccessors() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.ast.EntityProxy

            @Node
            class MyNode {
                String prop
                String unmapped

                public void setProp(String value) {
                    this.prop = value
                }

                public String getProp() {
                    return this.prop
                }
            }

            def n = new MyNode()
            n.prop = "foo"
            assert n.modified == false

            def p = new EntityProxy(n, 1, Collections.singleton("prop"))
            assert p.modified == false
            p.prop = "foo"
            assert p.modified
            assert p.modifiedProperties.contains("prop")
        '''
    }

    @Test
    def void testModifiedChecksUnmapped() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.ast.EntityProxy

            @Node
            class MyNode {
                String prop
                String unmapped
            }

            def n = new MyNode()
            p = new EntityProxy(n, 1, Collections.emptySet())
            assert p.modified == false
            p.unmapped = "bar"
            assert p.modified == false
            assert p.modifiedProperties.empty
        '''
    }

}
