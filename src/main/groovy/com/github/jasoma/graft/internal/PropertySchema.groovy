package com.github.jasoma.graft.internal

import com.github.jasoma.graft.Using
import com.github.jasoma.graft.convert.ConverterRegistry
import com.github.jasoma.graft.convert.ImplicitConverter
import groovy.transform.ToString
import groovy.transform.Immutable

import java.beans.PropertyDescriptor

/**
 * Describes a property of a {@link com.github.jasoma.graft.Node} or {@link com.github.jasoma.graft.Relation}
 * holding its name, entityType, and whether or not a converter is needed when reading/writing the property to a
 * neo4j database.
 */
@Immutable
@ToString(includePackage = false, includeNames = true)
class PropertySchema {

    /**
     * The name of the property.
     */
    String name

    /**
     * The type of the property.
     */
    Class propertyType

    /**
     * The type of converter the property uses if it has one.
     */
    Class converterType

    /**
     * Create a schema from a property descriptor.
     *
     * @param owningClass the class that owns the property.
     * @param descriptor the descriptor for that property.
     * @return the generated schema.
     */
    public static PropertySchema forDescriptor(Class<?> owningClass, PropertyDescriptor descriptor) {
        def using = Reflection.findAnnotation(owningClass, descriptor, Using)
        if (using) {
            return new PropertySchema(name: descriptor.name, propertyType: descriptor.propertyType, converterType: using.value())
        }
        if (ImplicitConverter.isImplicitlyConvertable(descriptor.propertyType)) {
            return new PropertySchema(name: descriptor.name, propertyType: descriptor.propertyType, converterType: ImplicitConverter)
        }
        return new PropertySchema(name: descriptor.name, propertyType: descriptor.propertyType)
    }

    /**
     * Convert a local value to a db value using the property's converter if it has one.
     *
     * @param value the value to converts.
     * @param converters a registry of converters.
     */
    def toDbValue(Object value, ConverterRegistry converters) {
        if (hasConverter()) {
            def converter = converters.get(converterType)
            return converter.toDbValue(value)
        }
        return value
    }

    /**
     * Convert a db value to a local value using the property's converter if it has one.
     *
     * @param dbValue the value to convert.
     * @param converters a registry of converters.
     */
    def fromDbValue(Object dbValue, ConverterRegistry converters) {
        if (hasConverter()) {
            def converter = converters.get(converterType)
            return converter.fromDbValue(dbValue, propertyType)
        }
        return dbValue
    }

    /**
     * @return whether or not the property needs to use a converter.
     */
    boolean hasConverter() {
        return converterType != null
    }

}
