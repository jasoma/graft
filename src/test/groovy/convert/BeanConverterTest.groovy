package convert

import com.github.jasoma.graft.convert.BeanConverter
import groovy.transform.Canonical
import infra.TestNode
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse

class BeanConverterTest {

    def converter = new BeanConverter()

    @Test
    def void testRead() {
        def properties = new HashMap()
        properties["root.name"] = "test"
        properties["root.id"] = 1

        def bean = converter.read(TestBean, "root", new TestNode(properties))
        assertEquals("test", bean.name)
        assertEquals(1, bean.id)
    }

    @Test
    def void testReadWithMissingProperties() {
        def properties = new HashMap()
        properties["root.id"] = 2

        def bean = converter.read(TestBean, "root", new TestNode(properties))
        assertEquals(null, bean.name)
        assertEquals(2, bean.id)
    }

    @Test
    def void testWrite() {
        def bean = new TestBean("test", 3)
        def added = new HashMap()

        converter.write(bean , "root", added.&put)
        assertEquals("test", added["root.name"])
        assertEquals(3, added["root.id"])
    }

    @Test
    def void testWriteWithNullValues() {
        def bean = new TestBean(null, 4)
        def added = new HashMap()

        converter.write(bean , "root", added.&put)
        assertFalse(added.containsKey("root.name"))
        assertEquals(4, added["root.id"])
    }

    @Canonical
    static class TestBean {
        String name
        int id
    }

}
