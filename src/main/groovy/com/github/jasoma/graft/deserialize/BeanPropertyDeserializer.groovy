package com.github.jasoma.graft.deserialize

import com.github.jasoma.graft.access.NeoNode
import com.github.jasoma.graft.access.ResultRow
/**
 * A basic deserializer implementation that directly sets values from the node onto a new instance of the target
 * type using JavaBeans naming conventions. Uses groovy casting via {@code asType} as the only type handling.
 */
class BeanPropertyDeserializer implements NodeDeserializer, DeserializerUtils {

    @Override
    def <T> T convert(Class<T> type, NeoNode node, ResultRow row) throws NodeDeserializationException {
        def obj = type.newInstance()
        node.properties().each { key, value ->
            if (value == null) {
                return;
            }
            if (obj.hasProperty(key)) {
                try {
                    setProperty(obj, key, value, row)
                }
                catch (Exception ex) {
                    throw new NodeDeserializationException("Error trying to set property $key on an instance of $type", ex)
                }
            }
        }
        return obj
    }

    /**
     * Set a property on an instance being deserialized.
     *
     * @param instance the instance to set on.
     * @param name the name of the property to set.
     * @param value the value of the property in the database object.
     * @param row the result row the node came from.
     */
    protected void setProperty(Object instance, String name, Object value, ResultRow row) {
        def propertyType = getPropertyType(instance, name)

        if (propertyType != null) {
            instance[name] = value.asType(propertyType)
        }
        else {
            instance[name] = value
        }
    }

}
