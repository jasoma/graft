package convert

import com.github.jasoma.graft.access.ResultRow
import com.github.jasoma.graft.convert.BeanPropertyDeserializer
import infra.TestNode
import models.CollectionPropertiesNode
import models.SimplePropertiesNode
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static org.junit.Assert.*

@RunWith(MockitoJUnitRunner)
class BeanPropertyDeserializerTest {

    @Mock ResultRow row;

    def deserializer = new BeanPropertyDeserializer()

    @Test
    def void testSimpleProperties() {
        def node = new TestNode(aBoolean: true, aString: "string", aLong: 123L, aDouble: 42.0D)
        def bean = deserializer.convert(SimplePropertiesNode, node, row)

        assertEquals(true, bean.aBoolean)
        assertEquals("string", bean.aString)
        assertEquals(123L, bean.aLong)
        assertEquals(42.0D, bean.aDouble, 0.0)
    }

    @Test
    def void testSimplePropertiesCoercion() {
        def node = new TestNode(aBoolean: "true", aString: 123, aLong: 123L, aDouble: 42.0D)
        def bean = deserializer.convert(SimplePropertiesNode, node, row)

        assertEquals(true, bean.aBoolean)
        assertEquals("123", bean.aString)
        assertEquals(123L, bean.aLong)
        assertEquals(42.0D, bean.aDouble, 0.0)
    }

    @Test
    def void testCollectionProperties() {
        def node = new TestNode(integerList: [1,2,3], intArray: [4,5,6] as int[], booleanMap: [t: true, f: false])
        def bean = deserializer.convert(CollectionPropertiesNode, node, row)

        assertEquals([1,2,3], bean.integerList)
        assertArrayEquals([4,5,6] as int[], bean.intArray)
        assertEquals([t: true, f: false], bean.booleanMap)
    }

    @Test
    def void testCollectionPropertiesCoercion() {
        def node = new TestNode(integerList: [1,2,3] as int[], intArray: [4,5,6], booleanMap: [t: true, f: false])
        def bean = deserializer.convert(CollectionPropertiesNode, node, row)

        assertEquals([1,2,3], bean.integerList)
        assertArrayEquals([4,5,6] as int[], bean.intArray)
        assertEquals([t: true, f: false], bean.booleanMap)
    }

}
