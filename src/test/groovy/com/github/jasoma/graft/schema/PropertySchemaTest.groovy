package com.github.jasoma.graft.schema

import com.github.jasoma.graft.Using
import com.github.jasoma.graft.access.NeoEntity
import com.github.jasoma.graft.access.ResultRow
import com.github.jasoma.graft.convert.ConcurrentMapRegistry
import com.github.jasoma.graft.convert.ConverterRegistry
import com.github.jasoma.graft.convert.PropertyConverter
import infra.TestNode
import org.apache.commons.beanutils.PropertyUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner)
class PropertySchemaTest {

    @Mock ResultRow row
    ConverterRegistry registry = new ConcurrentMapRegistry(new StringToFloatConverter())

    @Test
    def void testReadUsesDefaultConverter() {
        def nameSchema = new PropertySchema(DefaultConverterBean, PropertyUtils.getPropertyDescriptor(new DefaultConverterBean(), "name"), registry)
        def idSchema = new PropertySchema(DefaultConverterBean, PropertyUtils.getPropertyDescriptor(new DefaultConverterBean(), "id"), registry)

        def entity = new TestNode(name: "test", id: 1)

        def name = nameSchema.read(entity, row)
        assert name == "test"

        def id = idSchema.read(entity, row)
        assert id == 1
    }

    @Test
    def void testReadUsesCustomConverter() {
        def fieldSchema = new PropertySchema(CustomConverterBean, PropertyUtils.getPropertyDescriptor(new CustomConverterBean(), "usingOnField"), registry)
        def getterSchema = new PropertySchema(CustomConverterBean, PropertyUtils.getPropertyDescriptor(new CustomConverterBean(), "usingOnGetter"), registry)
        def setterSchema = new PropertySchema(CustomConverterBean, PropertyUtils.getPropertyDescriptor(new CustomConverterBean(), "usingOnSetter"), registry)

        def entity = new TestNode(usingOnField: "field", usingOnGetter: "getter", usingOnSetter: "setter")

        def field = fieldSchema.read(entity, row)
        assert field == 42f

        def getter = getterSchema.read(entity, row)
        assert getter == 42f

        def setter = setterSchema.read(entity, row)
        assert setter == 42f

        def converter = registry.get(StringToFloatConverter)
        assert converter.propertiesRead.removeFirst() == "usingOnField+field"
        assert converter.propertiesRead.removeFirst() == "usingOnGetter+getter"
        assert converter.propertiesRead.removeFirst() == "usingOnSetter+setter"
    }

    static class DefaultConverterBean {
        String name
        int id
    }

    static class CustomConverterBean {

        @Using(StringToFloatConverter) float usingOnField
        float usingOnGetter
        float usingOnSetter

        @Using(StringToFloatConverter)
        float getUsingOnGetter() {
            return usingOnGetter
        }

        void setUsingOnGetter(float usingOnGetter) {
            this.usingOnGetter = usingOnGetter
        }

        float getUsingOnSetter() {
            return usingOnSetter
        }

        @Using(StringToFloatConverter)
        void setUsingOnSetter(float usingOnSetter) {
            this.usingOnSetter = usingOnSetter
        }
    }

    static class StringToFloatConverter implements PropertyConverter {

        Deque<String> propertiesRead = new ArrayDeque<>();

        @Override
        void write(Object value, String name, PropertyConverter.PropertyWriter writer) {
            writer.addProperty(name, "TEST")
        }

        @Override
        def <T> T read(Class<T> type, String name, NeoEntity entity) {
            propertiesRead.addLast("$name+${entity.properties()[name]}")
            return (T) 42
        }
    }

}
