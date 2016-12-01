package com.github.jasoma.graft.ast

import com.github.jasoma.graft.EntityState
import groovy.transform.PackageScope

/**
 * Proxy wrapper returned by {@link com.github.jasoma.graft.Graft} for all entity instances loaded from the database.
 * The proxy will track modifications to the entity as well as store the graphId.
 */
class EntityProxy {

    private final def entity
    private final Set<String> mappedProperties
    private final Set<String> modifiedProperties = new HashSet<>()

    /**
     * The neo4j identifier assigned to the entity.
     */
    long graphId

    /**
     * Whether or not the entity was deleted from the database.
     */
    boolean deleted

    /**
     * Construct a proxy for an entity loaded from the database.
     *
     * @param entity the entity that was loaded.
     * @param graphId the identifier of the entity in neo4j.
     * @param mappedProperties the set of properties for the entity that are mapped to the database.
     */
    EntityProxy(entity, long graphId, Set<String> mappedProperties) {
        this.entity = entity
        this.graphId = graphId
        this.mappedProperties = mappedProperties
    }

    def propertyMissing(String name) {
        return entity[name]
    }

    def propertyMissing(String name, def arg) {
        if (mappedProperties.contains(name)) {
            modifiedProperties.add(name)
        }
        entity[name] = arg
    }

    /**
     * Add a property to the set of modified properties.
     *
     * @param name the name of the modified property.
     */
    @PackageScope
    def void modified(String name) {
        modifiedProperties.add(name)
    }

    /**
     * @return true if any property on the entity has been modified.
     */
    boolean isModified() {
        return !modifiedProperties.empty
    }

    /**
     * @return the set of properties that have been modified since the object was loaded.
     */
    Set<String> getModifiedProperties() {
        return Collections.unmodifiableSet(modifiedProperties)
    }

    /**
     * @return the state of the entity.
     */
    EntityState getState() {
        if (deleted) {
            return EntityState.Deleted
        }
        return (modified) ? EntityState.Modified : EntityState.Unmodified
    }

    /**
     * Provides access to the entity being proxied.
     *
     * @return the entity being proxied.
     */
    def getEntity() {
        return entity
    }

    /**
     * @return the class of the entity being proxied.
     */
    def Class getEntityClass() {
        return entity.class
    }

    @Override
    String toString() {
        return entity.toString()
    }
}
