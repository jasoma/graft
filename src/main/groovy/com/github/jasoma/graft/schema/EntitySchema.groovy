package com.github.jasoma.graft.schema

import com.github.jasoma.graft.Unmapped
import com.github.jasoma.graft.access.NeoEntity
import com.github.jasoma.graft.access.ResultRow
import com.github.jasoma.graft.convert.ConverterRegistry
import com.github.jasoma.graft.internal.Reflection
import org.apache.commons.beanutils.PropertyUtils

import java.beans.PropertyDescriptor

/**
 * Created by jason on 10/27/16.
 */
class EntitySchema<EntityType> {

    private static final String GROOVY_META_CLASS_PROPERTY = "metaClass"

    private Class<EntityType> entityType
    private Map<String, PropertySchema> entityProperties

    EntitySchema(Class<EntityType> entityType, ConverterRegistry converters, SchemaRegistry schemas) {
        this.entityType = entityType
        this.entityProperties = new HashMap<>()

        PropertyUtils.getPropertyDescriptors(entityType).each { property ->
            if (ignorable(property, entityType)) {
                return;
            }
            if (Reflection.isEntity(property.propertyType)) {
                // TODO
            }
            else {
                entityProperties[property.name] = new PropertySchema(entityType, property, converters)
            }

        }
    }

    private static boolean ignorable(PropertyDescriptor property, Class<EntityType> type) {
        if (!(property.readMethod && property.writeMethod)) {
            return true
        }
        if (property.readMethod.declaringClass == Object || property.name == GROOVY_META_CLASS_PROPERTY) {
            return true
        }
        return Reflection.annotationSources(type, property).any { it?.getAnnotation(Unmapped) != null }
    }

    def EntityType read(ResultRow row, String key) {
        def instance = entityType.newInstance()
        def entity = row.get(key) as NeoEntity
        entityProperties.each { name, schema ->
            instance[name] = schema.read(entity)
        }
        return instance
    }

    /**
     * @return the type of entity the schema describes.
     */
    def Class<EntityType> entityType() {
        return entityType
    }

}
