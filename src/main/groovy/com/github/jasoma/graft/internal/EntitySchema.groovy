package com.github.jasoma.graft.internal

import com.github.jasoma.graft.convert.ConverterRegistry

/**
 * Details the properties of a {@link com.github.jasoma.graft.Node} or {@link com.github.jasoma.graft.Relation} entityType
 * and can create new instances.
 */
abstract class EntitySchema {

    // TODO: need all mapped properties as well as entity properties

    private Class entityType
    private Map<String, PropertySchema> scalarProperties = new HashMap<>()

    /**
     * Create schemas for each of the entities properties.
     *
     * @param entityType the entityType of the entity.
     */
    EntitySchema(Class<?> entityType) {
        this.entityType = entityType
        for (PropertySchema propertySchema : Reflection.mappedProperties(entityType)) {
            scalarProperties[propertySchema.name] = propertySchema
        }
    }

    /**
     * @return the names of all non-entity properties of the {@link #entityType}.
     */
    public Set<String> scalarProperties() {
        return scalarProperties.keySet()
    }

    /**
     * @return the schemas of all non-entity properties of the {@link #entityType}.
     */
    public Collection<PropertySchema> scalarSchema() {
        return scalarProperties.values()
    }

    public PropertySchema propertySchema(String property) {
        def schema = scalarProperties[property]
        if (schema == null) {
            throw new IllegalArgumentException("No mapped property '$property' exists on type $entityType")
        }
        return schema
    }

    /**
     * Creates a new instance of the type the schema represents and assigns all non-entity values.
     *
     * @param values the values from the database to use when creating the instance.
     * @param converters a registry to find property converters in.
     * @return the created instance.
     */
    def createInstance(Map<String, Object> values, ConverterRegistry converters) {
        def instance = entityType.newInstance()

        scalarProperties.each {name, schema ->
            if (!values.containsKey(name)) {
                return
            }
            if (schema.hasConverter()) {
                def converter = converters.get(schema.converterType)
                if (!converter) {
                    throw new IllegalStateException("Could not load a converter of type $schema.converterType to converter property $name of $entityType")
                }
                instance[name] = converter.fromDbValue(values[name], schema.propertyType)
            }
            else {
                instance[name] = values[name]
            }
        }

        return instance
    }

}
