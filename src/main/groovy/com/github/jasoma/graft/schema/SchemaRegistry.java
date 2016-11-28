package com.github.jasoma.graft.schema;

import java.util.function.Supplier;

/**
 * Container for {@link EntitySchema} instances.
 */
public interface SchemaRegistry {

    /**
     * Lookup a schema in the registery.
     *
     * @param entityType the type of the entity to find a schema for.
     * @param <T> the type of the local class the schema is associated with.
     * @return the schema if one exists or null.
     */
    <T> EntitySchema<T> get(Class<T> entityType);

    /**
     * Return an existing schema or create a new one and add it to the registry.
     *
     * @param entityType the type of the entity to find a schema for.
     * @param creator a function for creating a schema if none exists in the registry.
     * @param <T> the type of the entity.
     * @return the schema for the entity.
     */
    <T> EntitySchema<T> getOrCreate(Class<T> entityType, Supplier<EntitySchema<T>> creator);

    /**
     * Add a schema to the registry.
     *
     * @param schema the schema to add.
     */
    void put(EntitySchema<?> schema);



}
