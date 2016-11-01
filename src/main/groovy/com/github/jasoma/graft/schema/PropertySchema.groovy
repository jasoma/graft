package com.github.jasoma.graft.schema

import com.github.jasoma.graft.Using
import com.github.jasoma.graft.access.NeoEntity
import com.github.jasoma.graft.access.ResultRow
import com.github.jasoma.graft.convert.ConverterRegistry
import com.github.jasoma.graft.convert.DefaultConverter
import com.github.jasoma.graft.convert.PropertyConverter

import java.beans.PropertyDescriptor
/**
 * Contains the details of a class property and the converter for reading/writing that property to the database.
 */
class PropertySchema {

    private Class containingClass
    private Class propertyType
    private String propertyName
    private PropertyConverter converter

    /**
     * Constructor.
     *
     * @param containingClass the class owning the property.
     * @param descriptor the descriptor for the property.
     * @param registry a registry for finding converters.
     */
    PropertySchema(Class containingClass, PropertyDescriptor descriptor, ConverterRegistry registry) {
        this.containingClass = containingClass
        this.propertyName = descriptor.name
        this.propertyType = descriptor.propertyType

        for (def element : [descriptor.readMethod, descriptor.writeMethod, containingClass.declaredFields.find {it.name == descriptor.name}]) {
            def annotation = element?.getAnnotation(Using)
            if (annotation) {
                converter = registry.get(annotation.value())
                break;
            }
        }

        if (converter == null) {
            converter = DefaultConverter.INSTANCE
        }
    }

    /**
     * Read the property value from an entity.
     *
     * @param entity the entity to read the value from.
     * @param row the result row the entity is from.
     * @return the value for the property.
     */
    def read(NeoEntity entity, ResultRow row) {
        return converter.read(propertyType, propertyName, entity)
    }

    @Override
    public String toString() {
        return "PropertySchema(${containingClass.simpleName}.$propertyName), type: ${propertyType.simpleName}, converter: ${converter.class.simpleName})";
    }

}
