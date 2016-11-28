package com.github.jasoma.graft.schema

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.function.Supplier

/**
 * Simple registry based on a current hash map.
 */
class ConcurrentMapSchemaRegistry implements SchemaRegistry {

    private ConcurrentMap<Class<?>, EntitySchema<?>> registry = new ConcurrentHashMap<>();

    /**
     * Default constructor, creates an empty registry.
     */
    ConcurrentMapSchemaRegistry() { }

    /**
     * Creates a registry with several schema instance pre-populated.
     *
     * @param schemas a set of schemas to add to the register.
     */
    ConcurrentMapSchemaRegistry(EntitySchema... schemas) {
        schemas.each { put(it) }
    }

    @Override
    def <T> EntitySchema<T> get(Class<T> entityType) {
        return (EntitySchema<T>) registry.get(entityType)
    }

    @Override
    def <T> EntitySchema<T> getOrCreate(Class<T> entityType, Supplier<EntitySchema<T>> creator) {
        return (EntitySchema<T>) registry.computeIfAbsent(entityType, { type -> creator.get() })
    }

    @Override
    void put(EntitySchema<?> schema) {
        registry[schema.entityType()] = schema
    }
}
