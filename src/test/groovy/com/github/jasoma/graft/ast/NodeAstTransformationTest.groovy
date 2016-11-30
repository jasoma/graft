package com.github.jasoma.graft.ast

import org.junit.Test

class NodeAstTransformationTest extends GroovyTestCase {

    @Test
    def void testEntityProperties() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.EntityState
            import com.github.jasoma.graft.ast.EntityProxy

            @Node
            class MyNode { }

            def n = new MyNode()
            assert n.graphId == null
            assert n.modified == false
            assert n.modifiedProperties instanceof Set
            assert n.modifiedProperties.empty
            assert n.state == EntityState.Unsaved
        '''
    }

    @Test
    def void testModifiedChecks() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.EntityState
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
            assert p.state == EntityState.Unmodified
            p.prop = "foo"
            assert p.modified
            assert p.modifiedProperties.contains("prop")
            assert p.state == EntityState.Modified
        '''
    }

    @Test
    def void testModifiedCustomAccessors() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.EntityState
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
            def p = new EntityProxy(n, 1, Collections.singleton("prop"))
            assert p.modified == false
            assert p.state == EntityState.Unmodified
            p.prop = "foo"
            assert p.modified
            assert p.modifiedProperties.contains("prop")
            assert p.state == EntityState.Modified
        '''
    }

    @Test
    def void testModifiedChecksUnmapped() {
        assertScript '''
            import com.github.jasoma.graft.Node
            import com.github.jasoma.graft.EntityState
            import com.github.jasoma.graft.ast.EntityProxy

            @Node
            class MyNode {
                String prop
                String unmapped
            }

            def n = new MyNode()
            p = new EntityProxy(n, 1, Collections.emptySet())
            assert p.modified == false
            assert p.state == EntityState.Unmodified
            p.unmapped = "bar"
            assert p.modified == false
            assert p.modifiedProperties.empty
            assert p.state == EntityState.Unmodified
        '''
    }

}
