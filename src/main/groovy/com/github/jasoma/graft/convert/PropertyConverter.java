package com.github.jasoma.graft.convert;

import com.github.jasoma.graft.access.NeoEntity;

/**
 * Interface for converting complex properties into values suitable for storage in neo4j and back.
 */
public interface PropertyConverter {

    /**
     * Convert a local property value to neo4j compatible types for storage. A local value can be
     * converted into one or more neo4j properties.
     *
     * @param value the value to convert.
     * @param name the name of the property the value was stored under.
     * @param writer a writer to add new properties to the node being created/modified.
     */
    void write(Object value, String name, PropertyWriter writer);

    /**
     * Convert one or more properties on a neo4j entity into a complex local type.
     *
     * @param type the type of the property being read.
     * @param name the name under which the property is being stored on the local type.
     * @param entity the neo4j entity being converted from.
     * @param <T> the type of the property being read.
     * @return a deserialized instance of the property.
     */
    <T> T read(Class<T> type, String name, NeoEntity entity);

    /**
     * Function for writing new or updated values to a neo4j entity when converting between local types
     * and database types in {@link PropertyConverter#write}.
     */
    @FunctionalInterface
    public interface PropertyWriter {

        /**
         * Add or update a named property on the neo4j entity.
         *
         * @param name the name of the property in the database.
         * @param value the value of the property. Must be a valid neo4j type.
         */
        void addProperty(String name, Object value);

    }

}
