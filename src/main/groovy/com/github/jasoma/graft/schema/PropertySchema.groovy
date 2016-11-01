package com.github.jasoma.graft.schema

import com.github.jasoma.graft.Using
import com.github.jasoma.graft.access.NeoEntity
import com.github.jasoma.graft.convert.ConverterRegistry
import com.github.jasoma.graft.convert.DefaultConverter
import com.github.jasoma.graft.convert.PropertyConverter

import java.beans.PropertyDescriptor
import java.lang.reflect.Field

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

        for (def element : [descriptor.readMethod, descriptor.writeMethod, findField(containingClass, descriptor.name)]) {
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

    private static Field findField(Class<?> type, String name) {
        Class<?> current = type
        while (current && current != Object) {
            def field = current.declaredFields.find {it.name == name}
            if (field) {
                return field
            }
            current = current.superclass
        }
        return null
    }

    /**
     * Read the property value from an entity.
     *
     * @param entity the entity to read the value from.
     * @param row the result row the entity is from.
     * @return the value for the property.
     */
    def read(NeoEntity entity) {
        return converter.read(propertyType, propertyName, entity)
    }

    /**
     * Write the property value from a local instance onto a database entity.
     *
     * @param localEntity the local object to read the property from.
     * @param dbEntity the database entity to write the property to.
     */
    def write(Object localEntity, NeoEntity dbEntity) {
        converter.write(localEntity[propertyName], propertyName, dbEntity.properties().&put as PropertyConverter.PropertyWriter)
    }

    @Override
    public String toString() {
        return "PropertySchema(${containingClass.simpleName}.$propertyName), type: ${propertyType.simpleName}, converter: ${converter.class.simpleName})";
    }

}
