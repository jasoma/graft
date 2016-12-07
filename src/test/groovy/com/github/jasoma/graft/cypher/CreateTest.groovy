package com.github.jasoma.graft.cypher

import com.github.jasoma.graft.EntityState
import com.github.jasoma.graft.Node
import com.github.jasoma.graft.access.NeoDatabase
import com.github.jasoma.graft.convert.ConcurrentMapRegistry
import com.github.jasoma.graft.test.InMemoryDatabase
import infra.TestClass
import org.junit.ClassRule
import org.junit.Test

class CreateTest implements TestClass {

    @ClassRule public static InMemoryDatabase db = new InMemoryDatabase()
    private NeoDatabase neo = NeoDatabase.wrap(db)

    @Test
    def void testCypherOutput() {
        def node = new CreateTestNode(foo: "Foo", bar: 42)
        def create = new Create(node, "t")
        def cypher = create.parameterizedCypher()

        def clauseHead = cypher.takeWhile { it != "{" } as String
        assert clauseHead.trim() == "CREATE ($create.identifier:Test".toString()
        assertContains(cypher, "`foo`: {t_foo}")
        assertContains(cypher, "`bar`: {t_bar}")
    }

    @Test
    def void testParameterMap() {
        def node = new CreateTestNode(foo: "Foo", bar: 42)
        def create = new Create(node, "t")
        def params = create.parameters()
        assert params["t_foo"] == node.foo
        assert params["t_bar"] == node.bar
    }

    @Test
    def void testCreate() {
        def node = new CreateTestNode(foo: "Foo", bar: 42)
        def create = new Create(node, "t")
        neo.withSession { session ->
            def created = create.create(session, new ConcurrentMapRegistry())
            assert created.state == EntityState.Unmodified
        }
    }

    @Test
    def void testStaticCreate() {
        neo.withSession { session ->
            def created = Create.create(CreateTestNode, session, new ConcurrentMapRegistry(), foo: "Foo", bar: 42)
            assert created.foo == "Foo"
            assert created.bar == 42
            assert created.state == EntityState.Unmodified
        }
    }

    @Node(labels = "Test")
    private static class CreateTestNode {
        String foo
        int bar
    }
}
