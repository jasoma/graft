package com.github.jasoma.graft.ast

import org.junit.Test

class RelationAstTransformationTest extends GroovyTestCase {

    @Test
    def void testEntityProperties() {
        assertScript '''
            import com.github.jasoma.graft.Relation
            import com.github.jasoma.graft.EntityState
            import com.github.jasoma.graft.ast.EntityProxy

            @Relation
            class MyRelation { }

            def r = new MyRelation()
            assert r.graphId == null
            assert r.modified == false
            assert r.modifiedProperties instanceof Set
            assert r.modifiedProperties.empty
            assert r.state == EntityState.Unsaved
        '''
    }

    @Test
    def void testModifiedChecks() {
        assertScript '''
            import com.github.jasoma.graft.Relation
            import com.github.jasoma.graft.EntityState
            import com.github.jasoma.graft.ast.EntityProxy

            @Relation
            class MyRelation {
                String prop
                String unmapped
            }

            def r = new MyRelation()
            r.prop = "foo"
            assert r.modified == false

            def p = new EntityProxy(r, 1, Collections.singleton("prop"))
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
            import com.github.jasoma.graft.Relation
            import com.github.jasoma.graft.EntityState
            import com.github.jasoma.graft.ast.EntityProxy

            @Relation
            class MyRelation {
                String prop
                String unmapped

                public void setProp(String value) {
                    this.prop = value
                }

                public String getProp() {
                    return this.prop
                }
            }

            def r = new MyRelation()
            def p = new EntityProxy(r, 1, Collections.singleton("prop"))
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
            import com.github.jasoma.graft.Relation
            import com.github.jasoma.graft.EntityState
            import com.github.jasoma.graft.ast.EntityProxy

            @Relation
            class MyRelation {
                String prop
                String unmapped
            }

            def r = new MyRelation()
            p = new EntityProxy(r, 1, Collections.emptySet())
            assert p.modified == false
            assert p.state == EntityState.Unmodified
            p.unmapped = "bar"
            assert p.modified == false
            assert p.modifiedProperties.empty
            assert p.state == EntityState.Unmodified
        '''
    }

    @Test
    def void testDefaultTypeName() {
        assertScript '''
            import com.github.jasoma.graft.Relation

            @Relation
            class MyRelation { }

            def r = new MyRelation()
            assert r.typeName == "MyRelation"
        '''
    }

    @Test
    def void testCustomTypeName() {
        assertScript '''
            import com.github.jasoma.graft.Relation

            @Relation("Foo")
            class MyRelation { }

            def r = new MyRelation()
            assert r.typeName == "Foo"
        '''
    }
}
