package com.github.jasoma.graft.deserialize

import com.github.jasoma.graft.Using
import com.github.jasoma.graft.access.ResultRow
import com.github.jasoma.graft.convert.PropertyConverter

import java.util.concurrent.ConcurrentHashMap
/**
 * A deserializer that reads any {@link Using} annotations on properties and uses the specified converter for that
 * type. Falls back to the behavior of {@link BeanPropertyDeserializer} for types without the annotation. Converters
 * can be provided upon construction or the default constructor will be used when first encountered.
 *
 * TODO: Caching of all the converter lookups.
 */
class ConverterAwareDeserializer extends BeanPropertyDeserializer {

    private Map<Class, PropertyConverter> converters = new ConcurrentHashMap<>()

    /**
     * Default constructor, all {@link PropertyConverter converters} will be created on demand.
     */
    ConverterAwareDeserializer() { }

    /**
     * Constructor with pre-instantiated converters.
     *
     * @param converters a set of pre-constructed converters.
     */
    ConverterAwareDeserializer(PropertyConverter... converters) {
        for (PropertyConverter c : converters) {
            this.converters[c.class] = c
        }
    }

    /**
     * Get the converter for a named property of a type if one is specified.
     *
     * @param type the type that owns the property.
     * @param propertyName the name of the property on the type.
     * @return the converter for the property if one exists, null otherwise.
     */
    protected PropertyConverter getConverter(Class<?> type, String propertyName) {

    }

    /**
     * Get the converter referenced by a {@link Using} annotation.
     *
     * @param annotation the annotation on the property being read.
     * @return the converter instance.
     */
    protected PropertyConverter getConverter(Using annotation) {
        return converters.computeIfAbsent(annotation.value(), { type -> type.newInstance()} )
    }

    @Override
    protected void setProperty(Object instance, String name, Object value, ResultRow row) {

    }
}
