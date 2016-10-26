package com.github.jasoma.graft.convert

import com.github.jasoma.graft.access.NeoEntity
import org.apache.commons.beanutils.PropertyUtils

/**
 * Stores complex types in neo4j by storing each of their bean properties as a separate property on the
 * root entity. For example given the classes:
 *
 * <pre>{@code
 *     class MyBeanType {
 *
 *         String name
 *         int id
 *
 *     }
 *
 *     @Node
 *     class MyNode {
 *
 *         @Using(BeanConverter) MyBeanType bean
 *         String basicProperty
 *
 *     }
 * }</pre>
 *
 * Saving a new instance of {@code MyNode} would translate into the following Cypher query:
 *
 * <pre>{@code
 *    def node = new MyNode()
 *    node.basicProperty = "hello"
 *    node.bean = new MyBeanType(name: "world", id: 42)
 *    node.save() // => CREATE (n: MyNode {basicProperty: "hello", `bean.name`: "hello", `bean.id`: 42})
 * }</pre>
 *
 * BeanConverter does not handle further nesting of complex types and as such is only suitable when the
 * type to be converted follows JavaBeans naming conventions and consists only of types that can be natively
 * stored in neo4j.
 */
class BeanConverter implements PropertyConverter {

    @Override
    void write(Object value, String name, PropertyConverter.PropertyWriter writer) {
        PropertyUtils.getPropertyDescriptors(value).each { property ->

            if (property.getReadMethod().getDeclaringClass() == Object) {
                return;
            }

            if (property.name == "metaClass") {
                return;
            }

            def pvalue = value[property.name]
            if (pvalue == null) {
                return;
            }

            String neoName = "$name.${property.name}"
            writer.addProperty(neoName, pvalue);
        }
    }

    @Override
    def <T> T read(Class<T> type, String name, NeoEntity entity) {
        def instance = type.newInstance();
        PropertyUtils.getPropertyDescriptors(type).each { property ->
            def neoName = "$name.${property.name}".toString()
            if (entity.properties().containsKey(neoName)) {
                instance[property.name] = entity[neoName].asType(property.propertyType)
            }
        }
        return instance
    }
}
